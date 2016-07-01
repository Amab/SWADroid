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

import org.ksoap2.serialization.SoapObject;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.database.DataBaseHelper;
import es.ugr.swad.swadroid.modules.login.Login;
import es.ugr.swad.swadroid.modules.Module;
import es.ugr.swad.swadroid.utils.Utils;
import es.ugr.swad.swadroid.webservices.SOAPClient;

/**
 * Rollcall users send module.
 * @see <a href="https://openswad.org/ws/#sendAttendanceUsers">sendAttendanceUsers</a>
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class UsersSend extends Module {
    /**
     * Number of users marked as present in the event
     */
    private int numUsers = 0;
    /**
     * Result of webservice call. 1 on success, 0 on error (for example, if the event does not exist)
     */
    private int success = 0;
    /**
     * Code of event associated to the users list
     */
    private int eventCode;
    /**
     * 0 ⇒ users from list users will be added to list of presents and other users formerly marked
     *     as present will not be affected
     * 1 ⇒ users from list users will be marked as present and other users formerly marked as
     *     present will be marked as absent
     */
    private int setOthersAsAbsent;
    /**
     * List of user codes separated with commas
     */
    private String usersCodes;
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
            setOthersAsAbsent = this.getIntent().getIntExtra("setOthersAsAbsent", 0);
            usersCodes = this.getIntent().getStringExtra("usersCodes");
            runConnection();
        } catch (Exception e) {
            String errorMsg = getString(R.string.errorServerResponseMsg);
            error(errorMsg, e, true);
        }
    }

    @Override
    protected void requestService() throws Exception {
        // Creates webservice request, adds required params and sends request to webservice
        createRequest(SOAPClient.CLIENT_TYPE);
        addParam("wsKey", Login.getLoggedUser().getWsKey());
        addParam("attendanceEventCode", String.valueOf(eventCode));
        addParam("users", usersCodes);
        addParam("setOthersAsAbsent", String.valueOf(setOthersAsAbsent));
        sendRequest(Integer.class, true);

        if (result != null) {
            SoapObject soap = (SoapObject) result;

            //Stores data returned by webservice response
            success = Integer.parseInt(soap.getProperty("success").toString());
            numUsers = Integer.parseInt(soap.getProperty("numUsers").toString());
        }    // end if (result != null)

        Log.i(TAG, "Sent " + numUsers + " users");
    }

    @Override
    protected void connect() {
        String progressDescription = getString(R.string.usersDownloadProgressDescription);
        int progressTitle = R.string.usersDownloadProgressTitle;

        startConnection(false, progressDescription, progressTitle);
    }

    @Override
    protected void postConnect() {
        String msg;

        if (!Utils.parseIntBool(success)) {
            Toast.makeText(this, R.string.errorSendingUsersMsg, Toast.LENGTH_LONG).show();

            setResult(RESULT_CANCELED);
        } else {
            if(numUsers > 0) {
                msg = String.valueOf(numUsers) + " " + getResources().getString(R.string.usersUpdated);
            } else {
                msg = getResources().getString(R.string.usersAbsent);
            }

            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();

            dbHelper.beginTransaction();
            //Remove all event attendances from database after a successful sending
            dbHelper.removeAllRows(DataBaseHelper.DB_TABLE_USERS_ATTENDANCES, "eventCode", eventCode);

            //Mark the event as sent to SWAD
            dbHelper.updateEventStatus(eventCode, "OK");
            dbHelper.endTransaction(true);

            setResult(RESULT_OK);
        }

        finish();
    }

    @Override
    protected void onError() {

    }

}
