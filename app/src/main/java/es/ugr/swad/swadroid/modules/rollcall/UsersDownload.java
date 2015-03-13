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
import android.widget.Toast;
import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.database.DataBaseHelper;
import es.ugr.swad.swadroid.model.User;
import es.ugr.swad.swadroid.model.UserAttendance;
import es.ugr.swad.swadroid.modules.Login;
import es.ugr.swad.swadroid.modules.Module;
import es.ugr.swad.swadroid.webservices.SOAPClient;
import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Rollcall users download module.
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class UsersDownload extends Module {
    /**
     * Number of users associated to the selected event
     */
    private int numUsers;
    /**
     * Code of event associated to the users list
     */
    private int eventCode;
    /**
     * Rollcall Users Download tag name for Logcat
     */
    private static final String TAG = Constants.APP_TAG + " UsersDownload";

    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.modules.Module#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setMETHOD_NAME("getAttendanceUsers");
        getActionBar().hide();
    }

    @Override
    protected void onStart() {
        super.onStart();
        
        try {
            eventCode = this.getIntent().getIntExtra("attendanceEventCode", 0);
            if (isDebuggable) {
                Log.d(TAG, "attendanceEventCode = " + eventCode);
            }
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
        sendRequest(UserAttendance.class, false);

        if (result != null) {
            // Stores users data returned by webservice response
            ArrayList<?> res = new ArrayList<Object>((Vector<?>) result);
            SoapObject soap = (SoapObject) res.get(1);
            numUsers = soap.getPropertyCount();

            if(numUsers > 0) {
                dbHelper.beginTransaction();

                //Removes old attendances from database
                dbHelper.removeAllRows(DataBaseHelper.DB_TABLE_USERS_ATTENDANCES, "eventCode", eventCode);
            }
            for (int i = 0; i < numUsers; i++) {
                SoapObject pii = (SoapObject) soap.getProperty(i);

                int userCode = Integer.parseInt(pii.getProperty("userCode").toString());
                String userNickname = pii.getProperty("userNickname").toString();
                String userID = pii.getProperty("userID").toString();
                String userSurname1 = pii.getProperty("userSurname1").toString();
                String userSurname2 = pii.getProperty("userSurname2").toString();
                String userFirstname = pii.getProperty("userFirstname").toString();
                String userPhoto = pii.getProperty("userPhoto").toString();
                //If not 0 ⇒ this user has attended the event.
                boolean userPresent = !pii.getProperty("present").toString().equals("0");

                if (userNickname.equalsIgnoreCase(Constants.NULL_VALUE)) userNickname = "";
                if (userSurname1.equalsIgnoreCase(Constants.NULL_VALUE)) userSurname1 = "";
                if (userSurname2.equalsIgnoreCase(Constants.NULL_VALUE)) userSurname2 = "";
                if (userFirstname.equalsIgnoreCase(Constants.NULL_VALUE)) userFirstname = "";
                if (userPhoto.equalsIgnoreCase(Constants.NULL_VALUE)) userPhoto = null;

                //Inserts user data into database
                dbHelper.insertUser(new User(userCode, null, userID, userNickname, userSurname1, userSurname2,
                        userFirstname, userPhoto, null, 0));

                //Inserts attendance data into database
                dbHelper.insertAttendance(userCode, eventCode, userPresent);
            }

            dbHelper.endTransaction(true);

            Log.i(TAG, "Retrieved " + numUsers + " users");
        }    // end if (result != null)

        // Request finalized without errors
        setResult(RESULT_OK);
    }

    @Override
    protected void connect() {
        String progressDescription = getString(R.string.usersDownloadProgressDescription);
        int progressTitle = R.string.usersDownloadProgressTitle;

        startConnection(false, progressDescription, progressTitle);
    }

    @Override
    protected void postConnect() {
        if (numUsers == 0) {
            Toast.makeText(this, R.string.noUsersAvailableMsg, Toast.LENGTH_LONG).show();
        } else {
            String msg = String.valueOf(numUsers) + " " + getResources().getString(R.string.usersUpdated);
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        }

        dbHelper.updateEventStatus(eventCode, "OK");

        finish();
    }

    @Override
    protected void onError() {
    }

}
