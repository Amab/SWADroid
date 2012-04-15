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
package es.ugr.swad.swadroid.modules.attendance;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Vector;

import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import es.ugr.swad.swadroid.Global;
import es.ugr.swad.swadroid.Preferences;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.model.Course;
import es.ugr.swad.swadroid.model.Model;
import es.ugr.swad.swadroid.model.User;
import es.ugr.swad.swadroid.modules.Courses;
import es.ugr.swad.swadroid.modules.Module;

/**
 * Attendance module for get user's courses
 * @author Antonio Aguilera Malagon <aguilerin@gmail.com>
 */
public class AttendanceConfigDownload extends Module {
	/**
	 * Cursor for database access
	 */
	private Cursor dbCursor;
	/**
	 * User courses list
	 */
	private List<Model>listCourses;
	/**
	 * Selected course code
	 */
	private long selectedCourseCode = 0;
	/**
	 * Users counter
	 */
	private int usersCount;
	/**
	 * Inserted users counter
	 */
	private int insertedUsersCount = 0;
	/**
	 * Attendance tag name for Logcat
	 */
	public static final String TAG = Global.APP_TAG + " AttendanceConfigDownload";
	/**
	 * Application preferences.
	 */
	protected static Preferences prefs = new Preferences(); 

	/* (non-Javadoc)
	 * @see es.ugr.swad.swadroid.modules.Module#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);

		prefs.getPreferences(getBaseContext());
		Intent activity = new Intent(getBaseContext(), Courses.class);
		Toast.makeText(getBaseContext(), R.string.coursesProgressDescription, Toast.LENGTH_LONG).show();
		startActivityForResult(activity, Global.COURSES_REQUEST_CODE);
		setMETHOD_NAME("getUsers");
	}

	/* (non-Javadoc)
	 * @see es.ugr.swad.swadroid.modules.Module#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		int lastCourseSelected;

		OnClickListener singleChoiceItemsClickListener = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				Course c = (Course) listCourses.get(whichButton);
				selectedCourseCode = c.getId();
				prefs.setLastCourseSelected(whichButton);

				if(isDebuggable) {
					Integer s = whichButton;
					Log.d(TAG, "singleChoice = " + s.toString());
				}
			}
		};

		OnClickListener positiveClickListener = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				try {					
					if(selectedCourseCode == 0) {
						//Toast.makeText(getBaseContext(), R.string.noCourseSelectedMsg, Toast.LENGTH_LONG).show();
						Course c = (Course) listCourses.get(0);
						selectedCourseCode = c.getId();
					}

					if(isDebuggable) {
						Log.d(TAG, "selectedCourseCode = " + Long.toString(selectedCourseCode));
					}
					runConnection();

				} catch (Exception ex) {
					String errorMsg = getString(R.string.errorServerResponseMsg);
					error(errorMsg);

					if(isDebuggable) {
						Log.e(ex.getClass().getSimpleName(), errorMsg);        		
						ex.printStackTrace();
					}
				}
			}
		};

		OnClickListener negativeClickListener = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
				finish();
			}
		};

		super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode == Activity.RESULT_OK) {
			switch(requestCode) {
			case Global.COURSES_REQUEST_CODE:
				final AlertDialog.Builder alert = new AlertDialog.Builder(this);
				dbCursor = dbHelper.getDb().getCursor(Global.DB_TABLE_COURSES);
				listCourses = dbHelper.getAllRows(Global.DB_TABLE_COURSES);
				lastCourseSelected = prefs.getLastCourseSelected();

				alert.setSingleChoiceItems(dbCursor, lastCourseSelected, "name", singleChoiceItemsClickListener)
				.setTitle(R.string.selectCourseTitle)
				.setPositiveButton(R.string.acceptMsg, positiveClickListener)
				.setNegativeButton(R.string.cancelMsg, negativeClickListener)
				.show();
				break;
			case Global.SCAN_QR_REQUEST_CODE:
				// Propagate the scan result to the previous activity
				setResult(resultCode, intent);
				finish();
				break;
			}
		} else {
			setResult(RESULT_CANCELED);
			finish();
		}	
	}

	/* (non-Javadoc)
	 * @see es.ugr.swad.swadroid.modules.Module#requestService()
	 */
	@Override
	protected void requestService() throws NoSuchAlgorithmException, IOException, XmlPullParserException, SoapFault, IllegalAccessException, InstantiationException {
		int userTypeCode = Global.STUDENT_TYPE_CODE;
		String empty = "anyType{}";

		// Check if user is logged as teacher
		if (Global.getLoggedUser().getUserRole() == Global.TEACHER_TYPE_CODE) {
			//Creates webservice request, adds required params and sends request to webservice
			createRequest();
			addParam("wsKey", Global.getLoggedUser().getWsKey());
			addParam("courseCode", (int) selectedCourseCode);
			addParam("groupCode", 0);		// All groups
			addParam("userRole", userTypeCode);
			sendRequest(User.class, false);

			if (result != null) {
				dbHelper.beginTransaction();

				//Stores users data returned by webservice response
				Vector<?> res = (Vector<?>) result;
				SoapObject soap = (SoapObject) res.get(1);
				usersCount = soap.getPropertyCount();
				for (int i = 0; i < usersCount; i++) {
					SoapObject pii = (SoapObject) soap.getProperty(i);
					Long userCode = new Long(pii.getProperty("userCode").toString());
					String userID = pii.getProperty("userID").toString();
					String userNickname = pii.getProperty("userNickname").toString();
					String userSurname1 = pii.getProperty("userSurname1").toString();
					String userSurname2 = pii.getProperty("userSurname2").toString();
					String userFirstName = pii.getProperty("userFirstname").toString();
					String userTypeName = userTypeCode == Global.TEACHER_TYPE_CODE ? "teacher" : "student";

					/*if(isDebuggable) {
						Log.d(TAG, "userCode=" + userCode);
						Log.d(TAG, "userID=" + userID);
						Log.d(TAG, "userNickname=" + userNickname);
						Log.d(TAG, "userSurname1=" + userSurname1);
						Log.d(TAG, "userSurname2=" + userSurname2);
						Log.d(TAG, "userFirstName=" + userFirstName);
						Log.d(TAG, "userTypeName=" + userTypeName);
					}*/

					if (userNickname.equals(empty)) userNickname = null;
					if (userSurname1.equals(empty)) userSurname1 = null;
					if (userSurname2.equals(empty)) userSurname2 = null;
					if (userFirstName.equals(empty)) userFirstName = null;

					User u = new User(
							userCode,			// id
							userTypeCode,
							null,				// wsKey
							userID,
							userNickname,
							userSurname1,
							userSurname2,
							userFirstName,
							userTypeName,
							null,				// photoPath
							userTypeCode		// userRole
							);
					if (dbHelper.insertUser(u))
						insertedUsersCount++;
					
					dbHelper.insertUserCourse(u, selectedCourseCode);
				}	// end for (int i=0; i < usersCount; i++)

				//Request finalized without errors
				Log.i(TAG, "Retrieved " + usersCount + " users");

				dbHelper.endTransaction();
			}	// end if (result != null)

			//Request finalized without errors
			setResult(RESULT_OK);
		}
	}

	/* (non-Javadoc)
	 * @see es.ugr.swad.swadroid.modules.Module#connect()
	 */
	@Override
	protected void connect() {
		String progressDescription = getString(R.string.usersDownloadProgressDescription);
		int progressTitle = R.string.usersDownloadProgressTitle;

		new Connect(true, progressDescription, progressTitle).execute();
	}

	/* (non-Javadoc)
	 * @see es.ugr.swad.swadroid.modules.Module#postConnect()
	 */
	@Override
	protected void postConnect() {
		Log.i(TAG, "Added " + insertedUsersCount + " new users");
		Toast.makeText(this, "La asignatura seleccionada tiene " + usersCount + " estudiantes", Toast.LENGTH_LONG).show();
		//finish();

		Intent activity = new Intent("es.ugr.swad.swadroid.android.SCAN");
		activity.putExtra("SCAN_MODE", "QR_CODE_MODE");
		activity.putExtra("SCAN_FORMATS", "QR_CODE");
		activity.putExtra("selectedCourseCode", selectedCourseCode);
		// pasar a CaptureActivity selectedCourseCode + cualquier otra informacion que necesite
		// recibir esta informacion adecuadamente en CaptureActivity y trabajar a partir de ella (ej: lista 
		//de alumnos -> comprobar al escanear un QR si pertenece a la lista)

		//activity.putExtra("selectedCourseCode", selectedCourseCode);
		startActivityForResult(activity, Global.SCAN_QR_REQUEST_CODE);
	}

	@Override
	protected void onError() {
	}
}
