package es.ugr.swad.swadroid.modules.indoorlocation;

import android.content.Intent;
import android.os.Bundle;

import org.ksoap2.serialization.SoapObject;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.model.Roles;
import es.ugr.swad.swadroid.modules.Module;
import es.ugr.swad.swadroid.modules.courses.Courses;
import es.ugr.swad.swadroid.modules.login.Login;
import es.ugr.swad.swadroid.webservices.SOAPClient;

public class GetAvailableRoles extends Module {

    private Roles roles;

    /**
     * Messages tag name for Logcat
     */
    private static final String TAG = Constants.APP_TAG + " GetAvailableRoles";

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
        setMETHOD_NAME("getAvailableRoles");
        getSupportActionBar().hide();
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onStart()
     */
    @Override
    protected void onStart() {
        super.onStart();
        connect();
    }

    @Override
    protected void requestService() throws Exception {
        createRequest(SOAPClient.CLIENT_TYPE);
        addParam("wsKey", Login.getLoggedUser().getWsKey());
        addParam("courseCode", Courses.getSelectedCourseCode());
        sendRequest(Roles.class, true);

        if (result!=null) {
            int rol = Integer.parseInt(((SoapObject) result).getProperty("roles").toString());
            roles = new Roles(rol);
        }
        Intent intent = new Intent();
        intent.putExtra("roles", roles);
        setResult(RESULT_OK, intent);
    }

    @Override
    protected void connect() {
        startConnection();
    }

    @Override
    protected void postConnect() {
        finish();
    }

    @Override
    protected void onError() {
        // No-op
    }
}
