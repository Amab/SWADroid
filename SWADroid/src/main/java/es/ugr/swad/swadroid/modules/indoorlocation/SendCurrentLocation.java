package es.ugr.swad.swadroid.modules.indoorlocation;

import android.os.AsyncTask;
import android.util.Log;

import org.ksoap2.serialization.SoapObject;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.modules.login.Login;
import es.ugr.swad.swadroid.webservices.IWebserviceClient;
import es.ugr.swad.swadroid.webservices.SOAPClient;

public class SendCurrentLocation  extends AsyncTask<Void, Void, Void> {
    /**
     * Messages tag name for Logcat
     */
    private static final String TAG = Constants.APP_TAG + " Send Current Location";
    /**
     * Room code
     */
    private Integer roomCode;
    private static String METHOD_NAME = "";
    private static IWebserviceClient webserviceClient;
    private Boolean success;
    private static Object result;

    SendCurrentLocation(Integer roomCode) {
        super();
        this.roomCode = roomCode;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
           Object success = sendLocation(roomCode);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private Object sendLocation(Integer roomCode) throws Exception {
        Log.d(TAG, "Send current location");

        METHOD_NAME = "sendCurrentLocation";

        createRequest(SOAPClient.CLIENT_TYPE);
        addParam("wsKey", Login.getLoggedUser().getWsKey());
        addParam("roomCode", roomCode);
        return sendRequest(Boolean.class, true);
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
    protected Boolean sendRequest(Class<?> cl, boolean simple) throws Exception {
        Boolean success = false;
        ((SOAPClient) webserviceClient).sendRequest(cl, simple);
        result = webserviceClient.getResult();

        if (result!=null) {

            SoapObject soap = (SoapObject) result;
            success = Boolean.valueOf(soap.getProperty("success").toString());

        }
        return success;
    }

    /**
     * Get success
     */
    public Boolean getValue() {
        return success;
    }
}
