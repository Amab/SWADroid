package es.ugr.swad.swadroid.modules.indoorlocation;

import android.os.AsyncTask;
import android.util.Log;

import org.ksoap2.serialization.SoapObject;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.model.LocationTimeStamp;
import es.ugr.swad.swadroid.modules.login.Login;
import es.ugr.swad.swadroid.webservices.IWebserviceClient;
import es.ugr.swad.swadroid.webservices.SOAPClient;


public class GetLastLocation extends AsyncTask<Void, Void, Void> {

    private int userCode;
    private LocationTimeStamp locationTimeStamp;
    /**
     * Messages tag name for Logcat
     */
    private static final String TAG = Constants.APP_TAG + " Get last location";
    private static String METHOD_NAME = "";
    private static IWebserviceClient webserviceClient;
    private static Object result;

    GetLastLocation(int userCode) {
        super();
        this.userCode = userCode;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try{
            locationTimeStamp = getLastLocation(userCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private LocationTimeStamp getLastLocation(int userCode) throws Exception {
        Log.d(TAG, "Get location from user code");

        METHOD_NAME= "getLastLocation";

        createRequest(SOAPClient.CLIENT_TYPE);
        addParam("wsKey", Login.getLoggedUser().getWsKey());
        addParam("userCode", userCode);
        return sendRequest(LocationTimeStamp.class, true);
    }

    /**
     * Creates webservice request.
     */
    private static void createRequest(String clientType) {
        if(webserviceClient == null) {
            if(clientType.equals(SOAPClient.CLIENT_TYPE)) {
                webserviceClient = new SOAPClient();
            }

        }

        webserviceClient.setMETHOD_NAME(METHOD_NAME);
        webserviceClient.createRequest();
    }

    /**
     * Adds a parameter to webservice request.
     *
     * @param param Parameter name.
     * @param value Parameter value.
     */
    private static void addParam(String param, Object value) {
        webserviceClient.addParam(param, value);
    }

    /**
     * Sends a SOAP request to the specified webservice in METHOD_NAME class
     * constant of the webservice client.
     *
     * @param cl     Class to be mapped
     * @param simple Flag for select simple or complex response
     * @throws Exception
     */
    protected LocationTimeStamp sendRequest(Class<?> cl, boolean simple) throws Exception {
        ((SOAPClient) webserviceClient).sendRequest(cl, simple);
        result = webserviceClient.getResult();

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
        return locationTimeStamp;
    }

    /**
     * Get last location
     */
    public LocationTimeStamp getValue() {
        return this.locationTimeStamp;
    }
}
