package es.ugr.swad.swadroid.modules;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.model.GroupType;
import es.ugr.swad.swadroid.model.Model;
import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Group type module to get the group types of a given course
 * and stores them in the database
 *
 * @author Helena Rodriguez Gijon <hrgijon@gmail.com>
 */
public class GroupTypes extends Module {
    /**
     * Group types name for Logcat
     */
    private static final String TAG = Constants.APP_TAG + " Group Types";
    /**
     * Course code to which the request is related
     */
    private long courseCode = -1;

    @Override
    protected void runConnection() {
        super.runConnection();
        if (!isConnected) {
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        courseCode = getIntent().getLongExtra("courseCode", -1);
        setMETHOD_NAME("getGroupTypes");
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            runConnection();
        } catch (Exception e) {
            String errorMsg = getString(R.string.errorServerResponseMsg);
            error(TAG, errorMsg, e, true);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED) {
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    @Override
    protected void requestService() throws NoSuchAlgorithmException,
            IOException, XmlPullParserException {
        createRequest();
        addParam("wsKey", Constants.getLoggedUser().getWsKey());
        addParam("courseCode", (int) Constants.getSelectedCourseCode());
        sendRequest(GroupTypes.class, false);

        if (result != null) {
            //Stores groups data returned by webservice response
            List<Model> groupsSWAD = new ArrayList<Model>();

            ArrayList<?> res = new ArrayList<Object>((Vector)result);
            SoapObject soap = (SoapObject) res.get(1);
            int csSize = soap.getPropertyCount();

            for (int i = 0; i < csSize; i++) {
                SoapObject pii = (SoapObject) soap.getProperty(i);
                long id = Long.parseLong(pii.getProperty("groupTypeCode").toString());
                String groupTypeName = pii.getProperty("groupTypeName").toString();
                int mandatory = Integer.parseInt(pii.getProperty("mandatory").toString());
                int multiple = Integer.parseInt(pii.getProperty("multiple").toString());
                long openTime = Long.parseLong(pii.getProperty("openTime").toString());
                GroupType g = new GroupType(id, groupTypeName, courseCode, mandatory, multiple, openTime);

                groupsSWAD.add(g);

                if (isDebuggable) {
                    Log.i(TAG, g.toString());
                }
            }

            dbHelper.insertCollection(Constants.DB_TABLE_GROUP_TYPES, groupsSWAD);

            setResult(RESULT_OK);
        }
    }


    @Override
    protected void connect() {
        String progressDescription = getString(R.string.groupTypesProgressDescription);
        int progressTitle = R.string.groupTypesProgressTitle;

        startConnection(true, progressDescription, progressTitle);
    }

    @Override
    protected void postConnect() {
        finish();
    }

    @Override
    protected void onError() {

    }

}
