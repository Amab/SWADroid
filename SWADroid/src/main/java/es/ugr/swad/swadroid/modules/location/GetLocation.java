package es.ugr.swad.swadroid.modules.location;

import android.os.AsyncTask;
import android.util.Log;

import org.ksoap2.serialization.SoapObject;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.model.Location;
import es.ugr.swad.swadroid.modules.login.Login;
import es.ugr.swad.swadroid.webservices.IWebserviceClient;
import es.ugr.swad.swadroid.webservices.SOAPClient;

// This class must be AsyncTasked
public class GetLocation extends AsyncTask<Void, Void, Void> {
    /**
     * Physical address of AP
     */
    private String mac;
    /**
     * Location related to mac
     */
    private Location location;
    /**
     * Messages tag name for Logcat
     */
    private static final String TAG = Constants.APP_TAG + " Get location";

    private static String METHOD_NAME = "";
    private static IWebserviceClient webserviceClient;
    private static Object result;

    GetLocation(String mac){
        super();
        this.mac = mac;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            location = getLocation(mac);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        getValue();
    }

    private Location getLocation(String mac) throws Exception {
        Log.d(TAG, "Get location from MAC address");

        METHOD_NAME= "getLocation";

        createRequest(SOAPClient.CLIENT_TYPE);
        addParam("wsKey", Login.getLoggedUser().getWsKey());
        addParam("MAC", mac);
        return sendRequest(Location.class, true);
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
    protected Location sendRequest(Class<?> cl, boolean simple) throws Exception {
        ((SOAPClient) webserviceClient).sendRequest(cl, simple);
        result = webserviceClient.getResult();

        if (result!=null) {
            SoapObject soap = (SoapObject) result;
            // Location Output structure
            soap = (SoapObject)soap.getProperty(0);
            // Rest of properties
            Integer institutionCode = Integer.valueOf(soap.getProperty("institutionCode").toString());
            String institutionShortName = soap.getProperty("institutionShortName").toString();
            String institutionFullName = soap.getProperty("institutionShortName").toString();
            Integer centerCode = Integer.valueOf(soap.getProperty("centerCode").toString());
            String centerShortName = soap.getProperty("centerShortName").toString();
            String centerFullName = soap.getProperty("centerFullName").toString();
            Integer buildingCode = Integer.valueOf(soap.getProperty("buildingCode").toString());
            String buildingShortName = soap.getProperty("buildingShortName").toString();
            String buildingFullName = soap.getProperty("buildingFullName").toString();
            Integer floor = Integer.valueOf(soap.getProperty("floor").toString());
            Integer roomCode = Integer.valueOf(soap.getProperty("roomCode").toString());
            String roomShortName = soap.getProperty("roomShortName").toString();
            String roomFullName = soap.getProperty("roomFullName").toString();

            location = new Location(institutionCode, institutionShortName, institutionFullName,
                    centerCode, centerShortName, centerFullName, buildingCode, buildingShortName, buildingFullName,
                    floor, roomCode, roomShortName, roomFullName);
        }
        return location;
    }

    /**
     * Get location
     */
    public Location getValue() {
        return location;
    }
}
