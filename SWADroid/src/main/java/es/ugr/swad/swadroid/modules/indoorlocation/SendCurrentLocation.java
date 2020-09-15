package es.ugr.swad.swadroid.modules.indoorlocation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import org.ksoap2.serialization.SoapObject;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.modules.Module;
import es.ugr.swad.swadroid.modules.login.Login;
import es.ugr.swad.swadroid.webservices.SOAPClient;

public class SendCurrentLocation  extends Module {
    /**
     * Messages tag name for Logcat
     */
    private static final String TAG = Constants.APP_TAG + " SendCurrentLocation";
    /**
     * Room code
     */
    private Integer roomCode;

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
        setMETHOD_NAME("sendMyLocation");
        getSupportActionBar().hide();
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onStart()
     */
    @Override
    protected void onStart() {
        super.onStart();
        roomCode = getIntent().getIntExtra("roomCode", -1);
        connect();
    }

    @Override
    protected void requestService() throws Exception {
        boolean success = false;
        createRequest(SOAPClient.CLIENT_TYPE);
        addParam("wsKey", Login.getLoggedUser().getWsKey());
        addParam("roomCode", roomCode);
        sendRequest(Boolean.class, true);

        if (result!=null) {
            SoapObject soap = (SoapObject) result;
            if (Integer.parseInt(soap.getProperty("success").toString()) != 0) {
                success = true;
            }
        }

        Intent intent = new Intent();
        intent.putExtra("success", success);
        setResult(Activity.RESULT_OK, intent);
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

    }

}
