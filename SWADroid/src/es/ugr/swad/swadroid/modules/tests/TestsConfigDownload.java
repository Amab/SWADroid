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
package es.ugr.swad.swadroid.modules.tests;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Vector;

import org.ksoap2.SoapFault;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import es.ugr.swad.swadroid.Global;
import es.ugr.swad.swadroid.Preferences;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.model.Test;
import es.ugr.swad.swadroid.modules.Module;

/**
 * Tests module for download and update questions
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 * @author Antonio Aguilera Malagon <aguilerin@gmail.com>
 */
public class TestsConfigDownload extends Module {
	/**
	 * Flag for detect if the teacher allows questions download
	 */
	private boolean isPluggable;
	/**
	 * Number of available questions
	 */
	private int numQuestions;
	/**
	 * Tests tag name for Logcat
	 */
	public static final String TAG = Global.APP_TAG + " TestsConfigDownload";
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
		setMETHOD_NAME("getTestConfig");
	}

	/* (non-Javadoc)
	 * @see es.ugr.swad.swadroid.modules.Module#onStart()
	 */
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
	 * @see es.ugr.swad.swadroid.modules.Module#requestService()
	 */
	@Override
	protected void requestService() throws NoSuchAlgorithmException,
	IOException, XmlPullParserException, SoapFault,
	IllegalAccessException, InstantiationException {

		//Calculates next timestamp to be requested		
		Long timestamp = new Long(dbHelper.getTimeOfLastTestUpdate(Global.getSelectedCourseCode()));
		timestamp++;

		//Creates webservice request, adds required params and sends request to webservice
		createRequest();
		addParam("wsKey", Global.getLoggedUser().getWsKey());
		addParam("courseCode", (int)Global.getSelectedCourseCode());
		sendRequest(Test.class, false);

		if (result != null) {
			//Stores tests data returned by webservice response
			Vector<?> res = (Vector<?>) result;

			Integer pluggable = new Integer(res.get(0).toString());
			isPluggable = Global.parseIntBool(pluggable);
			numQuestions = new Integer(res.get(1).toString());

			//If there are no available questions, notify to user
			if(numQuestions == 0) {
				Log.i(TAG, getString(R.string.noQuestionsAvailableTestsDownloadMsg));

				//If the teacher doesn't allows questions download, notify to user
			} else if(!isPluggable) {
				Log.i(TAG, getString(R.string.noQuestionsPluggableTestsDownloadMsg));

				//If there are questions and the teacher allows their download, process the questions data
			} else {
				Integer minQuestions = new Integer(res.get(2).toString());
				Integer defQuestions = new Integer(res.get(3).toString());
				Integer maxQuestions = new Integer(res.get(4).toString());
				String feedback = res.get(5).toString();
				Test tDB = (Test) dbHelper.getRow(Global.DB_TABLE_TEST_CONFIG, "id",
						Long.toString(Global.getSelectedCourseCode()));

				//If not exists a test configuration for this course, insert to database
				if(tDB == null) {
					Test t = new Test(Global.getSelectedCourseCode(), minQuestions, defQuestions, maxQuestions, feedback);
					dbHelper.insertTestConfig(t);
				}

				if(isDebuggable) {
					Log.d(TAG, "minQuestions=" + minQuestions);
					Log.d(TAG, "defQuestions=" + defQuestions);
					Log.d(TAG, "maxQuestions=" + maxQuestions);
					Log.d(TAG, "feedback=" + feedback);
				}

				Intent activity = new Intent(getBaseContext(), TestsQuestionsDownload.class);
				activity.putExtra("timestamp", timestamp);
				startActivityForResult(activity, Global.TESTS_QUESTIONS_DOWNLOAD_REQUEST_CODE);
			}
		}

		//Request finalized without errors
		setResult(RESULT_OK);
	}

	/* (non-Javadoc)
	 * @see es.ugr.swad.swadroid.modules.Module#connect()
	 */
	@Override
	protected void connect() {
		String progressDescription = getString(R.string.testsDownloadProgressDescription);
		int progressTitle = R.string.testsDownloadProgressTitle;

		new Connect(false, progressDescription, progressTitle).execute();
	}

	/* (non-Javadoc)
	 * @see es.ugr.swad.swadroid.modules.Module#postConnect()
	 */
	@Override
	protected void postConnect() {
		if(numQuestions == 0) {
			Toast.makeText(this, R.string.noQuestionsAvailableTestsDownloadMsg, Toast.LENGTH_LONG).show();		
		} else if(!isPluggable) {
			Toast.makeText(this, R.string.noQuestionsPluggableTestsDownloadMsg, Toast.LENGTH_LONG).show();
		}

		finish();
	}

	/* (non-Javadoc)
	 * @see es.ugr.swad.swadroid.modules.Module#onError()
	 */
	@Override
	protected void onError() {

	}
}
