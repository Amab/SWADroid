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

import android.os.Bundle;
import android.util.Log;
import es.ugr.swad.swadroid.Global;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.model.Course;
import es.ugr.swad.swadroid.model.Model;
import es.ugr.swad.swadroid.model.User;

/**
 * Courses module for get user's courses
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 *
 */
public class Courses extends Module {	
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
        addParam("wsKey", User.getWsKey());
        sendRequest(Course.class, false);

        if (result != null) {
	        //Stores courses data returned by webservice response
            List<Model> coursesDB = dbHelper.getAllRows(Global.DB_TABLE_COURSES);
            List<Model> coursesSWAD = new ArrayList<Model>();
            List<Model> newCourses = new ArrayList<Model>();
            List<Model> obsoleteCourses = new ArrayList<Model>();
        	Vector res = (Vector) result;
        	SoapObject soap = (SoapObject) res.get(1);	
        	int csSize = soap.getPropertyCount();
            for (int i = 0; i < csSize; i++) {
                SoapObject pii = (SoapObject)soap.getProperty(i);
                int id = Integer.parseInt(pii.getProperty(0).toString());
                String name = pii.getProperty(1).toString();
                Course c = new Course(id, name);
               	coursesSWAD.add(c);
                
        		if(isDebuggable)
        			Log.d(Global.COURSES_TAG, c.toString());
            }
            
            Log.i(Global.COURSES_TAG, "Retrieved " + csSize + " courses");

            //Obtain old unregistered courses
            obsoleteCourses.addAll(coursesDB);
            obsoleteCourses.removeAll(coursesSWAD);
            
            //Obtain new registered courses
            newCourses.addAll(coursesSWAD);
            newCourses.removeAll(coursesDB);
            
            //Delete old unregistered courses stuff
            csSize = obsoleteCourses.size();
            for (int i = 0; i < csSize; i++) {
            	Course c = (Course) obsoleteCourses.get(i);
            	dbHelper.removeRow(Global.DB_TABLE_COURSES, c.getId());
            }
            
            Log.i(Global.COURSES_TAG, "Deleted " + csSize + " old courses");
            
            //Insert new registered courses
            csSize = newCourses.size();
            for (int i = 0; i < csSize; i++) {
            	Course c = (Course) newCourses.get(i);
            	dbHelper.insertCourse(c);
            }

            Log.i(Global.COURSES_TAG, "Added " + csSize + " new courses");
            
	        //Request finalized without errors
	        setResult(RESULT_OK);
        }
    	
        finish();
    }

	/* (non-Javadoc)
	 * @see es.ugr.swad.swadroid.modules.Module#postConnect()
	 */
	@Override
	protected void postConnect() {
		
	}
}
