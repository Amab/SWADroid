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

package es.ugr.swad.swadroid.modules.rollcall;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.Vector;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.model.User;
import es.ugr.swad.swadroid.modules.Courses;
import es.ugr.swad.swadroid.modules.Login;
import es.ugr.swad.swadroid.modules.Module;
import es.ugr.swad.swadroid.webservices.SOAPClient;

/**
 * Rollcall config download module.
 *
 * @author Antonio Aguilera Malagon <aguilerin@gmail.com>
 */
public class RollcallConfigDownload extends Module {
    /**
     * Number of available students
     */
    private int numStudents;
    /**
     * Rollcall Config Download tag name for Logcat
     */
    private static final String TAG = Constants.APP_TAG + " RollcallConfigDownload";

    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.modules.Module#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setMETHOD_NAME("getUsers");
        getSupportActionBar().hide();
    }

    @Override
    protected void onStart() {
        super.onStart();
        
        try {
            if (isDebuggable) {
                Log.d(TAG, "selectedCourseCode = " + Long.toString(Courses.getSelectedCourseCode()));
            }
            runConnection();
        } catch (Exception e) {
            String errorMsg = getString(R.string.errorServerResponseMsg);
            error(TAG, errorMsg, e, true);
        }
    }

    @Override
    protected void requestService() throws Exception {
        int userRole = Constants.STUDENT_TYPE_CODE;
        long courseCode = Courses.getSelectedCourseCode();
        long groupCode = getIntent().getLongExtra("groupCode", 0);

        // Creates webservice request, adds required params and sends request to webservice
        createRequest(SOAPClient.CLIENT_TYPE);
        addParam("wsKey", Login.getLoggedUser().getWsKey());
        addParam("courseCode", (int) courseCode);
        addParam("groupCode", (int) groupCode);
        addParam("userRole", userRole);
        sendRequest(User.class, false);

        if (result != null) {
            // Stores users data returned by webservice response
            ArrayList<?> res = new ArrayList<Object>((Vector<?>) result);
            SoapObject soap = (SoapObject) res.get(1);
            numStudents = soap.getPropertyCount();
            for (int i = 0; i < numStudents; i++) {
                SoapObject pii = (SoapObject) soap.getProperty(i);

                long userCode = Long.valueOf(pii.getProperty("userCode").toString());
                String userID = pii.getProperty("userID").toString();
                String userNickname = pii.getProperty("userNickname").toString();
                String userSurname1 = pii.getProperty("userSurname1").toString();
                String userSurname2 = pii.getProperty("userSurname2").toString();
                String userFirstName = pii.getProperty("userFirstname").toString();
                String userPhoto = pii.getProperty("userPhoto").toString();

                if (userNickname.equalsIgnoreCase(Constants.NULL_VALUE)) userNickname = "";
                if (userSurname1.equalsIgnoreCase(Constants.NULL_VALUE)) userSurname1 = "";
                if (userSurname2.equalsIgnoreCase(Constants.NULL_VALUE)) userSurname2 = "";
                if (userFirstName.equalsIgnoreCase(Constants.NULL_VALUE)) userFirstName = "";
                if (userPhoto.equalsIgnoreCase(Constants.NULL_VALUE)) userPhoto = "";

                User u = new User(
                        userCode,                    // id
                        null,                        // wsKey
                        userID,
                        userNickname,
                        userSurname1,
                        userSurname2,
                        userFirstName,
                        userPhoto,                   // photoPath
                        userRole);

                // Inserts user in database or updates it if already exists
                dbHelper.insertUser(u);
                dbHelper.insertUserCourse(userCode, courseCode, groupCode);
            }    // end for (int i=0; i < usersCount; i++)

             Log.i(TAG, "Retrieved " + numStudents + " users");
        }    // end if (result != null)

        // Request finalized without errors
        setResult(RESULT_OK);
    }

    @Override
    protected void connect() {
        String progressDescription = getString(R.string.studentsDownloadProgressDescription);
        int progressTitle = R.string.studentsDownloadProgressTitle;

        startConnection(true, progressDescription, progressTitle);
    }

    @Override
    protected void postConnect() {
        String status = Environment.getExternalStorageState();
        if (!status.equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, R.string.scan_no_photos, Toast.LENGTH_LONG).show();
        }

        if (numStudents == 0) {
            Toast.makeText(this, R.string.noStudentsAvailableMsg, Toast.LENGTH_LONG).show();
        } else {
            String msg = String.valueOf(numStudents) + " " + getResources().getString(R.string.studentsUpdated);
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        }
        finish();
    }

    @Override
    protected void onError() {

    }

}
