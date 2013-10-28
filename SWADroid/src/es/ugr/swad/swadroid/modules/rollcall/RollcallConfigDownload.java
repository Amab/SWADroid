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
import com.bugsense.trace.BugSenseHandler;
import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.model.User;
import es.ugr.swad.swadroid.modules.Module;
import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Vector;

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
     * Maximum size of a student photo (in bytes)
     */
    private static final int PHOTO_FILE_MAX_SIZE = 30 * 1024;    // 30 KB
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
    }

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

    @Override
    protected void requestService() throws NoSuchAlgorithmException, IOException, XmlPullParserException {
        int userRole = Constants.STUDENT_TYPE_CODE;
        long courseCode = Constants.getSelectedCourseCode();
        long groupCode = getIntent().getLongExtra("groupCode", 0);

        // Creates webservice request, adds required params and sends request to webservice
        createRequest();
        addParam("wsKey", Constants.getLoggedUser().getWsKey());
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
                        userPhoto,                    // photoPath
                        userRole);

                // Inserts user in database or updates it if already exists
                dbHelper.insertUser(u);
                dbHelper.insertUserCourse(userCode, courseCode, groupCode);
                
                // If user's picture URL is not empty, download and save it in phone memory
                if(!userPhoto.equals("")) {
                	downloadFile(userPhoto);
                }
            }    // end for (int i=0; i < usersCount; i++)

            if (isDebuggable) {
                Log.d(TAG, "Retrieved " + numStudents + " users");
            }
        }    // end if (result != null)

        // Request finalized without errors
        setResult(RESULT_OK);
    }

    private void downloadFile(String url) {
        if (url != null) {
            // Check the status of the external memory
            String status = Environment.getExternalStorageState();
            if (status.equals(Environment.MEDIA_MOUNTED)) {
                // Create a path where we will place our private file on external storage.
                String fileName = url.substring(url.lastIndexOf('/') + 1);
                File file = new File(getExternalFilesDir(null), fileName);
                URI imageUrl;

                try {
                    imageUrl = new URI(url);
                    HttpURLConnection conn = (HttpURLConnection) imageUrl.toURL().openConnection();
                    conn.connect();

                    InputStream is = conn.getInputStream();
                    OutputStream os = new FileOutputStream(file);

                    byte[] buff = new byte[PHOTO_FILE_MAX_SIZE];

                    int bytesRead;
                    ByteArrayOutputStream bao = new ByteArrayOutputStream();

                    while ((bytesRead = is.read(buff)) != -1) {
                        bao.write(buff, 0, bytesRead);
                    }

                    os.write(bao.toByteArray());

                    is.close();
                    os.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error IO: " + e.getMessage());
                    e.printStackTrace();

                    //Send exception details to Bugsense
                    if (!isDebuggable) {
                        BugSenseHandler.sendException(e);
                    }
                } catch (URISyntaxException e) {
                    e.printStackTrace();

                    //Send exception details to Bugsense
                    if (!isDebuggable) {
                        BugSenseHandler.sendException(e);
                    }
                }
            }
        }
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
