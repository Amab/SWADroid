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
package es.ugr.swad.swadroid.modules.marks;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.analytics.SWADroidTracker;
import es.ugr.swad.swadroid.gui.DialogFactory;
import es.ugr.swad.swadroid.model.User;
import es.ugr.swad.swadroid.modules.login.Login;
import es.ugr.swad.swadroid.modules.Module;
import es.ugr.swad.swadroid.webservices.SOAPClient;

/**
 * Marks module for get user's marks
 * @see <a href="https://openswad.org/ws/#getMarks">getMarks</a>
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class GetMarks extends Module {
    /**
     * Marks tag name for Logcat
     */
    private static final String TAG = Constants.APP_TAG + " Marks";

    private static String marks;
    private long fileCode;
    private boolean hasError;

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
        setMETHOD_NAME("getMarks");
        getSupportActionBar().hide();
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onStart()
     */
    @Override
    protected void onStart() {
        super.onStart();

        SWADroidTracker.sendScreenView(getApplicationContext(), TAG);

        //fileCode = this.getIntent().getLongExtra("fileCode", 0);
        fileCode = 0;
        hasError = false;

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
        String progressDescription = getString(R.string.marksProgressDescription);

        startConnection();
    }

    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.modules.Module#requestService()
     */
    @Override
    protected void requestService()
            throws Exception {

        try {
            //Creates webservice request, adds required params and sends request to webservice
            createRequest(SOAPClient.CLIENT_TYPE);
            addParam("wsKey", Login.getLoggedUser().getWsKey());
            addParam("fileCode", fileCode);
            sendRequest(User.class, true);

            if (result != null) {
                //Stores courses data returned by webservice response
                SoapObject soap = (SoapObject) result;
                marks = soap.getProperty("content").toString();

                Log.i(TAG, "Retrieved marks [user=" + Login.getLoggedUser().getUserNickname()
                        + ", fileCode=" + fileCode + "]");

                //Request finalized without errors
                setResult(RESULT_OK);
            } else {
                setResult(RESULT_CANCELED);
            }
        } catch(SoapFault e) {
            if (e.faultstring.equals("Bad file code")) {
                hasError = true;
            }
        }
    }

    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.modules.Module#postConnect()
     */
    @Override
    protected void postConnect() {
        AlertDialog errorDialog;
        String errorMsg = getString(R.string.errorBadFileCodeMsg);
        if(hasError) {
            errorDialog = DialogFactory.createErrorDialog(this, TAG,
                    errorMsg, null, false, false, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
            errorDialog.show();
        } else {
            finish();
        }
    }

    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.modules.Module#onError()
     */
    @Override
    protected void onError() {

    }

    /**
     * Get user marks
     * @return User marks
     */
    public static String getMarks() {
        return marks;
    }
}
