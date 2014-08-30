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

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.model.Test;
import es.ugr.swad.swadroid.modules.Module;
import es.ugr.swad.swadroid.utils.Utils;
import es.ugr.swad.swadroid.webservices.SOAPClient;

import org.ksoap2.serialization.SoapObject;

/**
 * Tests module for download and update questions
 *
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
    private static final String TAG = Constants.APP_TAG + " TestsConfigDownload";

    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.modules.Module#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setMETHOD_NAME("getTestConfig");
        getSupportActionBar().hide();
    }

    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.modules.Module#onStart()
     */
    @Override
    protected void onStart() {
        super.onStart();
        try {

            if (isDebuggable) {
                Log.d(TAG, "selectedCourseCode = " + Long.toString(Constants.getSelectedCourseCode()));
            }

            runConnection();
        } catch (Exception e) {
            String errorMsg = getString(R.string.errorServerResponseMsg);
            error(TAG, errorMsg, e, true);
        }
    }


    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.modules.Module#requestService()
     */
    @Override
    protected void requestService() throws Exception {

        //Calculates next timestamp to be requested
        Long timestamp = Long.valueOf(dbHelper.getTimeOfLastTestUpdate(Constants.getSelectedCourseCode()));
        timestamp++;

        //Creates webservice request, adds required params and sends request to webservice
        createRequest(SOAPClient.CLIENT_TYPE);
        addParam("wsKey", Constants.getLoggedUser().getWsKey());
        addParam("courseCode", (int) Constants.getSelectedCourseCode());
        sendRequest(Test.class, true);

        if (result != null) {
            //Stores tests data returned by webservice response
			SoapObject soap = (SoapObject) result;

            Integer pluggable = Integer.valueOf(soap.getProperty("pluggable").toString());
            isPluggable = Utils.parseIntBool(pluggable);
            numQuestions = Integer.valueOf(soap.getProperty("numQuestions").toString());

            //If the teacher doesn't allows questions download, notify to user
            if (!isPluggable) {
                Log.i(TAG, getString(R.string.noQuestionsPluggableTestsDownloadMsg));

                //If there are no available questions, notify to user
            } else if (numQuestions == 0) {
                Log.i(TAG, getString(R.string.noQuestionsAvailableTestsDownloadMsg));

                //If there are questions and the teacher allows their download, process the questions data
            } else {
                Integer minQuestions = Integer.valueOf(soap.getProperty("minQuestions").toString());
                Integer defQuestions = Integer.valueOf(soap.getProperty("defQuestions").toString());
                Integer maxQuestions = Integer.valueOf(soap.getProperty("maxQuestions").toString());
                String feedback = soap.getProperty("feedback").toString();
                Test tDB = (Test) dbHelper.getRow(Constants.DB_TABLE_TEST_CONFIG, "id",
                        Long.toString(Constants.getSelectedCourseCode()));

                //If exists a test configuration for this course, remove from database
                if (tDB != null) {
                	dbHelper.removeRow(Constants.DB_TABLE_TEST_CONFIG, Constants.getSelectedCourseCode());
                	Log.i(TAG, "Removed old test configuration for course " + Constants.getSelectedCourseFullName());
                }
                
                Test t = new Test(Constants.getSelectedCourseCode(), minQuestions, defQuestions, maxQuestions, feedback, System.currentTimeMillis() / 1000L);
                dbHelper.insertTestConfig(t);

                if (isDebuggable) {
                    Log.d(TAG, "minQuestions=" + minQuestions);
                    Log.d(TAG, "defQuestions=" + defQuestions);
                    Log.d(TAG, "maxQuestions=" + maxQuestions);
                    Log.d(TAG, "feedback=" + feedback);
                }

                Intent activity = new Intent(this, TestsQuestionsDownload.class);
                activity.putExtra("timestamp", timestamp);
                startActivityForResult(activity, Constants.TESTS_QUESTIONS_DOWNLOAD_REQUEST_CODE);
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

        startConnection(false, progressDescription, progressTitle);
    }

    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.modules.Module#postConnect()
     */
    @Override
    protected void postConnect() {
        if (isPluggable && (numQuestions == 0)) {
            Toast.makeText(this, R.string.noQuestionsAvailableTestsDownloadMsg, Toast.LENGTH_LONG).show();
        } else if (!isPluggable) {
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
