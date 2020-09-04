package es.ugr.swad.swadroid.modules.indoorlocation;

import android.content.Intent;
import android.os.Bundle;

import org.ksoap2.serialization.SoapObject;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.model.Location;
import es.ugr.swad.swadroid.modules.Module;
import es.ugr.swad.swadroid.modules.login.Login;
import es.ugr.swad.swadroid.webservices.SOAPClient;

public class GetLocation extends Module {
    /**
     * Physical address of AP
     */
    private String mac;
    /**
     * Distance to AP
     */
    private double distance;
    /**
     * Location related to mac
     */
    private Location location;
    /**
     * Messages tag name for Logcat
     */
    private static final String TAG = Constants.APP_TAG + " Get location";

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
        setMETHOD_NAME("getLocation");
        getSupportActionBar().hide();
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onStart()
     */
    @Override
    protected void onStart() {
        super.onStart();
        mac = getIntent().getStringExtra("mac");
        distance = getIntent().getDoubleExtra("distance", 500.0);
        connect();
    }

    @Override
    protected void requestService() throws Exception {
        createRequest(SOAPClient.CLIENT_TYPE);
        addParam("wsKey", Login.getLoggedUser().getWsKey());
        addParam("MAC", mac);
        sendRequest(Location.class, true);

        if (result!=null) {
            SoapObject soap = (SoapObject) result;
            soap = (SoapObject)soap.getProperty(0);

            int roomCode = Integer.parseInt(soap.getProperty("roomCode").toString());

            if (roomCode != -1) {
                int institutionCode = Integer.parseInt(soap.getProperty("institutionCode").toString());
                String institutionShortName = soap.getProperty("institutionShortName").toString();
                String institutionFullName = soap.getProperty("institutionShortName").toString();
                int centerCode = Integer.parseInt(soap.getProperty("centerCode").toString());
                String centerShortName = soap.getProperty("centerShortName").toString();
                String centerFullName = soap.getProperty("centerFullName").toString();
                int buildingCode = Integer.parseInt(soap.getProperty("buildingCode").toString());
                String buildingShortName = soap.getProperty("buildingShortName").toString();
                String buildingFullName = soap.getProperty("buildingFullName").toString();
                int floor = Integer.parseInt(soap.getProperty("floor").toString());
                String roomShortName = soap.getProperty("roomShortName").toString();
                String roomFullName = soap.getProperty("roomFullName").toString();

                location = new Location(institutionCode, institutionShortName, institutionFullName,
                        centerCode, centerShortName, centerFullName, buildingCode, buildingShortName, buildingFullName,
                        floor, roomCode, roomShortName, roomFullName);
            }else{
                location = null;
            }
        }
        Intent intent = new Intent();
        intent.putExtra("location", location);
        intent.putExtra("distance", distance);
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
