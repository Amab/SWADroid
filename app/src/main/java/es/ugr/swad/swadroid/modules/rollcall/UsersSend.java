/*
 *
 *  *  This file is part of SWADroid.
 *  *
 *  *  Copyright (C) 2010 Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 *  *
 *  *  SWADroid is free software: you can redistribute it and/or modify
 *  *  it under the terms of the GNU General Public License as published by
 *  *  the Free Software Foundation, either version 3 of the License, or
 *  *  (at your option) any later version.
 *  *
 *  *  SWADroid is distributed in the hope that it will be useful,
 *  *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  *  GNU General Public License for more details.
 *  *
 *  *  You should have received a copy of the GNU General Public License
 *  *  along with SWADroid.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package es.ugr.swad.swadroid.modules.rollcall;

import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.model.UserAttendance;
import es.ugr.swad.swadroid.modules.Login;
import es.ugr.swad.swadroid.modules.Module;
import es.ugr.swad.swadroid.utils.Utils;
import es.ugr.swad.swadroid.webservices.SOAPClient;

/**
 * Rollcall users download module.
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class UsersSend extends Module {
    /**
     * Number of users marked as present in the event
     */
    private int numUsers;
    /**
     * Result of webservice call.
     */
    private int success;
    /**
     * List of user codes separated with commas
     */
    String usersCodes;
    /**
     * Code of event associated to the users list
     */
    private int eventCode;
    /**
     * Rollcall Users Download tag name for Logcat
     */
    private static final String TAG = Constants.APP_TAG + " UsersSend";

    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.modules.Module#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setMETHOD_NAME("sendAttendanceUsers");
        getSupportActionBar().hide();
    }

    @Override
    protected void onStart() {
        super.onStart();
        
        try {
            eventCode = this.getIntent().getIntExtra("attendanceEventCode", 0);
            usersCodes = this.getIntent().getStringExtra("usersCodes");
            runConnection();
        } catch (Exception e) {
            String errorMsg = getString(R.string.errorServerResponseMsg);
            error(TAG, errorMsg, e, true);
        }
    }

    @Override
    protected void requestService() throws Exception {
        // Creates webservice request, adds required params and sends request to webservice
        createRequest(SOAPClient.CLIENT_TYPE);
        addParam("wsKey", Login.getLoggedUser().getWsKey());
        addParam("attendanceEventCode", eventCode);
        addParam("usersCodes", usersCodes);
        sendRequest(Integer.class, true);

        if (result != null) {
            SoapObject soap = (SoapObject) result;

            //Stores data returned by webservice response
            success = Integer.parseInt(soap.getProperty("success").toString());
            numUsers = Integer.parseInt(soap.getProperty("numUsers").toString());
        }    // end if (result != null)

        Log.i(TAG, "Sended " + numUsers + " users");

        // Request finalized without errors
        setResult(RESULT_OK);
    }

    @Override
    protected void connect() {
        String progressDescription = getString(R.string.usersDownloadProgressDescription);
        int progressTitle = R.string.usersDownloadProgressTitle;

        //TODO Add progress screen like in LoginActivity
        startConnection(false, progressDescription, progressTitle);
    }

    @Override
    protected void postConnect() {
        if (!Utils.parseIntBool(success)) {
            //TODO Add error message

            setResult(RESULT_CANCELED);
        } else {
            String msg = String.valueOf(numUsers) + " " + getResources().getString(R.string.usersUpdated);
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();

            setResult(RESULT_OK);
        }

        finish();
    }

    @Override
    protected void onError() {

    }

}
