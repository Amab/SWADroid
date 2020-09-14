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
            SoapObject properties = (SoapObject)soap.getProperty(0);

            int institutionCode = Integer.parseInt(properties.getProperty("institutionCode").toString());
            String institutionShortName = properties.getProperty("institutionShortName").toString();
            String institutionFullName = properties.getProperty("institutionFullName").toString();
            int centerCode = Integer.parseInt(properties.getProperty("centerCode").toString());
            String centerShortName = properties.getProperty("centerShortName").toString();
            String centerFullName = properties.getProperty("centerFullName").toString();
            int buildingCode = Integer.parseInt(properties.getProperty("buildingCode").toString());
            String buildingShortName = properties.getProperty("buildingShortName").toString();
            String buildingFullName = properties.getProperty("buildingFullName").toString();
            int floor = Integer.parseInt(properties.getProperty("floor").toString());
            int roomCode = Integer.parseInt(properties.getProperty("roomCode").toString());
            String roomShortName = properties.getProperty("roomShortName").toString();
            String roomFullName = properties.getProperty("roomFullName").toString();
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
