package es.ugr.swad.swadroid.modules.messages;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Vector;

import org.apache.http.util.ByteArrayBuffer;
import org.ksoap2.serialization.SoapObject;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.bugsense.trace.BugSenseHandler;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.model.User;
import es.ugr.swad.swadroid.modules.Module;
import es.ugr.swad.swadroid.webservices.SOAPClient;

/**
 * Download users list.
 *
 * @author José Antonio Guerrero Avilés <cany20@gmail.com>
 */

public class DownloadUsers extends Module{
	
	 /**
     * Number of available users
     */
    private int numUsers;
    /**
     * Maximum size of a student photo (in bytes)
     */
    private static final int PHOTO_FILE_MAX_SIZE = 30 * 1024;    // 30 KB
    /**
     * Download Users tag name for Logcat
     */
    private static final String TAG = Constants.APP_TAG + " Downloadusers";

    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.modules.Module#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setMETHOD_NAME("getUsers");
        getSupportActionBar().hide();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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
    protected void requestService() throws Exception {
        int userRole = Constants.TEACHER_TYPE_CODE;
        long courseCode = Constants.getSelectedCourseCode();
        long groupCode = getIntent().getLongExtra("groupCode", 0);

        // Creates webservice request, adds required params and sends request to webservice
        createRequest(SOAPClient.CLIENT_TYPE);
        addParam("wsKey", Constants.getLoggedUser().getWsKey());
        addParam("courseCode", (int) courseCode);
        addParam("groupCode", (int) groupCode);
        addParam("userRole", userRole);
        sendRequest(User.class, false);

        if (result != null) {
            // Stores users data returned by webservice response
            ArrayList<?> res = new ArrayList<Object>((Vector<?>) result);
            SoapObject soap = (SoapObject) res.get(1);
            numUsers = soap.getPropertyCount();
            for (int i = 0; i < numUsers; i++) {
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
                
            }    // end for (int i=0; i < usersCount; i++)

            if (isDebuggable) {
                Log.d(TAG, "Retrieved " + numUsers + " users");
            }
        }    // end if (result != null)

        // Request finalized without errors
        setResult(RESULT_OK);
    }

    @Override
    protected void connect() {
        String progressDescription = getString(R.string.studentsDownloadProgressDescription);
        int progressTitle = R.string.usersDownloadProgressTitle;

        startConnection(true, progressDescription, progressTitle);
    }

    @Override
    protected void postConnect() {
        String status = Environment.getExternalStorageState();
        if (!status.equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, R.string.scan_no_photos, Toast.LENGTH_LONG).show();
        }

        if (numUsers == 0) {
            Toast.makeText(this, R.string.noStudentsAvailableMsg, Toast.LENGTH_LONG).show();
        } else {
            String msg = String.valueOf(numUsers) + " " + getResources().getString(R.string.studentsUpdated);
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        }
        finish();
    }

    @Override
    protected void onError() {

    }
}
