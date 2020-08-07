package es.ugr.swad.swadroid.modules.indoorlocation;

import android.content.Intent;
import android.os.Bundle;

import org.ksoap2.serialization.SoapObject;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.model.LocationTimeStamp;
import es.ugr.swad.swadroid.modules.Module;
import es.ugr.swad.swadroid.modules.login.Login;
import es.ugr.swad.swadroid.webservices.SOAPClient;


public class GetLastLocation extends Module {

    private int userCode;
    private LocationTimeStamp locationTimeStamp;
    /**
     * Messages tag name for Logcat
     */
    private static final String TAG = Constants.APP_TAG + " Get last location";

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
        setMETHOD_NAME("getLastLocation");
        getSupportActionBar().hide();
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onStart()
     */
    @Override
    protected void onStart() {
        super.onStart();
        userCode = getIntent().getIntExtra("userCode", -1);
        connect();
    }

    @Override
    protected void requestService() throws Exception {
        createRequest(SOAPClient.CLIENT_TYPE);
        addParam("wsKey", Login.getLoggedUser().getWsKey());
        addParam("userCode", userCode);
        sendRequest(LocationTimeStamp.class, true);

        if (result!=null) {
            SoapObject soap = (SoapObject) result;
            // Location Output structure
            soap = (SoapObject)soap.getProperty(0);
            // Rest of properties
            int institutionCode = Integer.parseInt(soap.getProperty("institutionCode").toString());
            String institutionShortName = soap.getProperty("institutionShortName").toString();
            String institutionFullName = soap.getProperty("institutionFullName").toString();
            int centerCode = Integer.parseInt(soap.getProperty("centerCode").toString());
            String centerShortName = soap.getProperty("centerShortName").toString();
            String centerFullName = soap.getProperty("centerFullName").toString();
            int buildingCode = Integer.parseInt(soap.getProperty("buildingCode").toString());
            String buildingShortName = soap.getProperty("buildingShortName").toString();
            String buildingFullName = soap.getProperty("buildingFullName").toString();
            int floor = Integer.parseInt(soap.getProperty("floor").toString());
            int roomCode = Integer.parseInt(soap.getProperty("roomCode").toString());
            String roomShortName = soap.getProperty("roomShortName").toString();
            String roomFullName = soap.getProperty("roomFullName").toString();

            soap = (SoapObject) result;
            int checkInTime = Integer.parseInt(soap.getProperty(1).toString());

            locationTimeStamp = new LocationTimeStamp(institutionCode, institutionShortName, institutionFullName,
                    centerCode, centerShortName, centerFullName, buildingCode, buildingShortName, buildingFullName,
                    floor, roomCode, roomShortName, roomFullName, checkInTime);
        }
        Intent intent = new Intent();
        intent.putExtra("locationTimeStamp", locationTimeStamp);
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

    }

}
