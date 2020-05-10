package es.ugr.swad.swadroid.modules.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.gui.MenuActivity;
import es.ugr.swad.swadroid.model.Location;
import es.ugr.swad.swadroid.model.Pair;
import es.ugr.swad.swadroid.model.UserFilter;
import es.ugr.swad.swadroid.modules.login.Login;
import es.ugr.swad.swadroid.modules.messages.SearchUsers;
import es.ugr.swad.swadroid.preferences.Preferences;
import es.ugr.swad.swadroid.webservices.IWebserviceClient;
import es.ugr.swad.swadroid.webservices.SOAPClient;

public class ManageLocation extends MenuActivity {
    /**
     * Messages tag name for Logcat
     */
    private static final String TAG = Constants.APP_TAG + " Manage location";
    /**
     * Location history
     */
    private ListView history;
    /**
     * Manage wifi properties
     */
    private WifiManager wifiManager;
    /**
     * Arraylist to store SSID of available networks
     */
    private List<Pair> availableNetworks = new ArrayList<>();
    /**
     * Adapter of LocationDistance array
     */
    private ArrayAdapter adapter;
    /**
     * Array of receivers
     */
    private ArrayList<UserFilter> arrayReceivers;
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static String METHOD_NAME = "";
    private static IWebserviceClient webserviceClient;
    private static Object result;
    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.modules.Module#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.indoor_location);

        setTitle(R.string.manageLocation);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        history = findViewById(R.id.location_history_data);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(this, "Wifi is disabled ... You need to enable it", Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, availableNetworks);
        history.setAdapter(adapter);
    }

    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.modules.Module#onStart()
     */
    @Override
    protected void onStart() {
        super.onStart();

        //Find user location listener
        /**
         * Find user location text&icon
         */
        FloatingActionButton findUser = findViewById(R.id.find_user);
        findUser.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Log.d(TAG, "onClick: Find user location");
               arrayReceivers = new ArrayList<>();
               Intent intent = new Intent(getApplicationContext(), SearchUsers.class);
               intent.putExtra("receivers", arrayReceivers);
               startActivityForResult(intent, Constants.SEARCH_USERS_REQUEST_CODE);
           }
        });

        //Get history
        history = findViewById(R.id.location_history_data);

        /**
         * Update location
         */
        FloatingActionButton updateLocation = findViewById(R.id.update_location);
        updateLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Preferences.getShareLocation()) {
                    int syncTime = Integer.parseInt(Preferences.getSyncLocationTime());

                    Runnable wifiScanner = new Runnable() {
                        @Override
                        public void run() {
                            scanWifi();
                        }
                    };
                    try {
                        ScheduledFuture<?> scheduledWifiScanner = scheduler.scheduleAtFixedRate(wifiScanner, 0, syncTime, TimeUnit.MINUTES);
                    }catch (IllegalArgumentException e){
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "You need to activate sharing location", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            arrayReceivers = (ArrayList) data.getSerializableExtra("receivers");
            /**
             * Message's receivers (nicknames)
             */
            String receivers = "";
            for(int i=0; i<arrayReceivers.size(); i++){
                receivers += arrayReceivers.get(i) + ",";
            }

            /**
             * Save if the list is expanded
             */
            boolean showAll;
            if(arrayReceivers.size() <= 3)
                showAll = false;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void scanWifi() {
        availableNetworks.clear();
        Log.d(TAG, "SCANNING WIFI");
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
    }

    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive se está ejecuntado");
            /**
             * List to store ScanResults of available networks
             */
            List<ScanResult> results = wifiManager.getScanResults();
            unregisterReceiver(this);
            if (results.size() > 0) {
                for (ScanResult scanResult : results) {
                    String bssid = scanResult.BSSID.replace(":", "");
                    double distance = (int) calculateDistance(scanResult.level, scanResult.frequency);
                    try {
                        AsyncLocation asyncLocation = new AsyncLocation(bssid, distance);
                        asyncLocation.execute();
                    } catch (Exception e) {
                        Log.d(TAG, "EXCEPCION AL OBTENER LA LOCALIZACIÓN");
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    private double calculateDistance(double levelInDb, double freqInMHz)    {
        double exp = (27.55 - (20 * Math.log10(freqInMHz)) + Math.abs(levelInDb)) / 20.0;
        return Math.pow(10.0, exp);
    }

    /*
    private void addLocations(String bssid, double distance) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String uri = url + "ap/" + bssid;
        Log.d(TAG, "ADD LOCATIONS");
        JsonObjectRequest jsObjectRequest = new JsonObjectRequest(Request.Method.GET, uri, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                     availableNetworks.add("Estás a " + (int)distance + " m de " + response.getString(("es")));
                     adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("TAG", "Error JSON: " + error.getMessage());
                }
            });
        requestQueue.add(jsObjectRequest);
    }*/
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
    protected List<Location> sendRequest(Class<?> cl, boolean simple) throws Exception {
        ((SOAPClient) webserviceClient).sendRequest(cl, simple);
        result = webserviceClient.getResult();

        List<Location> locationList = new ArrayList<>();
        if (result!=null) {

            ArrayList<?> res = new ArrayList<>((Vector<?>) result);
            SoapObject soap = (SoapObject) res.get(1);
            int numLocations = soap.getPropertyCount();

            for (int i = 0; i < numLocations; i++) {
                SoapObject pii = (SoapObject) soap.getProperty(i);
                Integer institutionCode = Integer.valueOf(pii.getProperty("institutionCode").toString());
                String institutionShortName = pii.getProperty("institutionShortName").toString();
                String institutionFullName = pii.getProperty("institutionShortName").toString();
                Integer centerCode = Integer.valueOf(pii.getProperty("centerCode").toString());
                String centerShortName = pii.getProperty("centerShortName").toString();
                String centerFullName = pii.getProperty("centerFullName").toString();
                Integer buildingCode = Integer.valueOf(pii.getProperty("buildingCode").toString());
                String buildingShortName = pii.getProperty("buildingShortName").toString();
                String buildingFullName = pii.getProperty("buildingFullName").toString();
                Integer floor = Integer.valueOf(pii.getProperty("floor").toString());
                Integer roomCode = Integer.valueOf(pii.getProperty("roomCode").toString());
                String roomShortName = pii.getProperty("roomShortName").toString();
                String roomFullName = pii.getProperty("roomFullName").toString();

                locationList.add(new Location(institutionCode, institutionShortName, institutionFullName,
                        centerCode, centerShortName, centerFullName, buildingCode, buildingShortName, buildingFullName,
                        floor, roomCode, roomShortName, roomFullName));
            }
        }
        return locationList;
    }

    private class AsyncLocation extends AsyncTask<String, Void, String> {

        private String mac;
        private Double distance;

        AsyncLocation(String mac, Double distance){
            super();
            this.mac = mac;
            this.distance = distance;
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                for (Location l : getLocation(mac)) {
                    Pair<Location, Double> locationInfo = new Pair<>(l, distance);
                    availableNetworks.add(locationInfo);
                    adapter.notifyDataSetChanged();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private List<Location> getLocation(String mac) throws Exception {
        Log.d(TAG, "Get location from MAC address");

        METHOD_NAME= "getLocations";

        createRequest(SOAPClient.CLIENT_TYPE);
        addParam("wsKey", Login.getLoggedUser().getWsKey());
        addParam("MAC", mac);
        return sendRequest(Object.class, false);
    }
}
