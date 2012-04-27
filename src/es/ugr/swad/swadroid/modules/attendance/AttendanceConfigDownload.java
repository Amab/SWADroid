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
import java.util.Vector;

import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import es.ugr.swad.swadroid.Global;
import es.ugr.swad.swadroid.Preferences;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.model.User;
import es.ugr.swad.swadroid.modules.Module;

/**
 * Attendance module for get user's courses
 * @author Antonio Aguilera Malagon <aguilerin@gmail.com>
 */
public class AttendanceConfigDownload extends Module {
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
		setMETHOD_NAME("getUsers");
	}

	@Override
	protected void onStart() {
		super.onStart();
		prefs.getPreferences(getBaseContext());
		try {					

			if(isDebuggable) {
				Log.d(TAG, "selectedCourseCode = " + Long.toString(Global.getSelectedCourseCode()));
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

	/* (non-Javadoc)
	 * @see es.ugr.swad.swadroid.modules.Module#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode == Activity.RESULT_OK) {
			switch(requestCode) {
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
		int userRole = Global.STUDENT_TYPE_CODE;
		String empty = "anyType{}";

		// Creates webservice request, adds required params and sends request to webservice
		createRequest();
		addParam("wsKey", Global.getLoggedUser().getWsKey());
		addParam("courseCode", (int) Global.getSelectedCourseCode());
		addParam("groupCode", 0);		// All groups
		addParam("userRole", userRole);
		sendRequest(User.class, false);

		if (result != null) {
			dbHelper.beginTransaction();

			// Stores users data returned by webservice response
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

				if (userNickname.equals(empty)) userNickname = null;
				if (userSurname1.equals(empty)) userSurname1 = null;
				if (userSurname2.equals(empty)) userSurname2 = null;
				if (userFirstName.equals(empty)) userFirstName = null;

				User u = new User(
						userCode,			// id
						null,				// wsKey
						userID,
						userNickname,
						userSurname1,
						userSurname2,
						userFirstName,
						null,				// photoPath
						userRole
						);
				if (dbHelper.insertUser(u))
					insertedUsersCount++;

				dbHelper.insertUserCourse(u, Global.getSelectedCourseCode());
			}	// end for (int i=0; i < usersCount; i++)

			Log.i(TAG, "Retrieved " + usersCount + " users");

			dbHelper.endTransaction();
		}	// end if (result != null)

		// Request finalized without errors
		setResult(RESULT_OK);
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

		Intent activity = new Intent("es.ugr.swad.swadroid.android.SCAN");
		activity.putExtra("SCAN_MODE", "QR_CODE_MODE");
		activity.putExtra("SCAN_FORMATS", "QR_CODE");

		startActivityForResult(activity, Global.SCAN_QR_REQUEST_CODE);
	}

	@Override
	protected void onError() {
	}
}
