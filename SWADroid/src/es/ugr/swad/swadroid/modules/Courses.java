/*
 *  This file is part of SWADroid.
 *
 *  Copyright (C) 2010 Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 *
 *  SWADroid is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  SWADroid is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with SWADroid.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.ugr.swad.swadroid.modules;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParserException;

import com.bugsense.trace.BugSenseHandler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import es.ugr.swad.swadroid.Global;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.model.Course;
import es.ugr.swad.swadroid.model.Model;

/**
 * Courses module for get user's courses
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 * @author Antonio Aguilera Malagon <aguilerin@gmail.com> 
 * @author Helena Rodriguez Gijon <hrgijon@gmail.com>
 */
public class Courses extends Module {
	/**
	 * Courses tag name for Logcat
	 */
	public static final String TAG = Global.APP_TAG + " Courses";

	@Override
	protected void runConnection() {
		super.runConnection();
		if (!isConnected) {
			setResult(RESULT_CANCELED);
			finish();
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate()
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setMETHOD_NAME("getCourses");
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		super.onStart();      
		runConnection();
	}

	/* (non-Javadoc)
	 * @see es.ugr.swad.swadroid.modules.Module#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_CANCELED) {
			setResult(RESULT_CANCELED);
			finish();
		}
	}

	/* (non-Javadoc)
	 * @see es.ugr.swad.swadroid.modules.Module#connect()
	 */
	@Override
	protected void connect() {
		String progressDescription = getString(R.string.coursesProgressDescription);
		int progressTitle = R.string.coursesProgressTitle;

		new Connect(false, progressDescription, progressTitle).execute();
	}

	/* (non-Javadoc)
	 * @see es.ugr.swad.swadroid.modules.Module#requestService()
	 */
	@Override
	protected void requestService()
			throws NoSuchAlgorithmException, IOException, XmlPullParserException, SoapFault, IllegalAccessException, InstantiationException {

		//Creates webservice request, adds required params and sends request to webservice
		createRequest();
		addParam("wsKey", Global.getLoggedUser().getWsKey());
		sendRequest(Course.class, false);

		if (result != null) {
			//Stores courses data returned by webservice response
			List<Model> coursesDB = dbHelper.getAllRows(Global.DB_TABLE_COURSES);
			List<Model> coursesSWAD = new ArrayList<Model>();
			List<Model> newCourses = new ArrayList<Model>();
			List<Model> obsoleteCourses = new ArrayList<Model>();
			//List<Model> modifiedCourses = new ArrayList<Model>();
			Vector<?> res = (Vector<?>) result;
			SoapObject soap = (SoapObject) res.get(1);	
			int csSize = soap.getPropertyCount();
			for (int i = 0; i < csSize; i++) {
				SoapObject pii = (SoapObject)soap.getProperty(i);
				long id = Long.parseLong(pii.getProperty("courseCode").toString());
				//String name = pii.getProperty("courseName").toString(); this field is obsolete, and will be erased in the future
				int userRole = Integer.parseInt(pii.getProperty("userRole").toString());
				String shortName = pii.getProperty("courseShortName").toString();
				String fullName = pii.getProperty("courseFullName").toString();
				Course c = new Course(id, userRole,shortName, fullName);
				coursesSWAD.add(c);

				/*if(isDebuggable)
        			Log.d(TAG, c.toString());*/
			}

			Log.i(TAG, "Retrieved " + csSize + " courses"); 

			//Obtain old unregistered courses and modified courses
			obsoleteCourses.addAll(coursesDB);
			obsoleteCourses.removeAll(coursesSWAD);

			//Obtain new registered courses
			newCourses.addAll(coursesSWAD);
			newCourses.removeAll(coursesDB);
			//modifiedCourses.addAll(newCourses);
			newCourses.removeAll(obsoleteCourses);

			//modified courses
			// modifiedCourses.removeAll(newCourses);

			//Only old unregistered courses
			//obsoleteCourses.removeAll(modifiedCourses);

			//Delete old unregistered courses stuff
			csSize = obsoleteCourses.size();
			for (int i = 0; i < csSize; i++) {
				Course c = (Course) obsoleteCourses.get(i);
				dbHelper.removeRow(Global.DB_TABLE_COURSES, c.getId());
			}

			Log.i(TAG, "Deleted " + csSize + " old courses");

			//Insert new registered courses
			csSize = newCourses.size();
			for (int i = 0; i < csSize; i++) {
				Course c = (Course) newCourses.get(i);
				dbHelper.insertCourse(c);
			}

			Log.i(TAG, "Added " + csSize + " new courses");

			//update modified courses
			/*   csSize = modifiedCourses.size();
            for(int i=0; i < csSize; ++i){
            	Course c = (Course) newCourses.get(i);
            	dbHelper.updateCourse(c.getId(), c);
            }
            Log.i(TAG, "Updated " + csSize + " courses");*/

			//Request finalized without errors
			setResult(RESULT_OK);
		}
	}

	/* (non-Javadoc)
	 * @see es.ugr.swad.swadroid.modules.Module#postConnect()
	 */
	@Override
	protected void postConnect() {
		finish();
	}

	/* (non-Javadoc)
	 * @see es.ugr.swad.swadroid.modules.Module#onError()
	 */
	@Override
	protected void onError() {

	}

	/**
	 * Removes all courses from database
	 * @param context Database context
	 */
	public void clearCourses(Context context) {
		try {
			dbHelper.emptyTable(Global.DB_TABLE_COURSES);
		} catch (Exception e) {
			e.printStackTrace();
			
			//Send exception details to Bugsense
			BugSenseHandler.sendException(e);
		}
	}
}
