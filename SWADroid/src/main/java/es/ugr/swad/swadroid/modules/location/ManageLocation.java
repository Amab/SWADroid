package es.ugr.swad.swadroid.modules.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.gui.MenuActivity;
import es.ugr.swad.swadroid.model.LocationDistance;
import es.ugr.swad.swadroid.model.UserFilter;
import es.ugr.swad.swadroid.modules.messages.SearchUsers;

public class ManageLocation extends MenuActivity {
    /**
     * Messages tag name for Logcat
     */
    private static final String TAG = Constants.APP_TAG + " Manage location";

    /**
     * Code width
     */
    private static final int CODE_WIDTH = 250;

    /**
     * Code height
     */
    private static final int CODE_HEIGHT = 250;

    /**
     * Barcode Encoder
     */
    private static BarcodeEncoder barcodeEncoder;
    /**
     * Location switch
     */
    private static Switch shareLocation;
    /**
     * Find user location text&icon
     */
    private static TextView findUser;
    /**
     * Location history
     */
    private static ListView history;
    /**
     * Allow to share location
     */
    private static Boolean allowSharingLocation=false;
    /**
     * Update location
     */
    private static Button updateLocation;
    /**
     * Manage wifi properties
     */
    private WifiManager wifiManager;
    /**
     * Arraylisto to store SSID of available networks
     */
    private ArrayList<String> availableNetworks = new ArrayList<>();
    /**
     * List to store ScanResults of available networks
     */
    private List<ScanResult> results;
    /**
     * URL of MacStore
     */
    private String url = "https://mac-store.herokuapp.com/";
    /**
     * Adapter of LocationDistance array
     */
    private ArrayAdapter adapter;
    /**
     * Array of receivers
     */
    private ArrayList<UserFilter> arrayReceivers;
    /**
     * Message's receivers (nicknames)
     */
    private String receivers;
    /**
     * Save if the list is expanded
     */
    private boolean showAll;
    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.modules.Module#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.indoor_location);

        setTitle(R.string.manageLocation);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        barcodeEncoder = new BarcodeEncoder();

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

        //Share location listener
        shareLocation = findViewById(R.id.share_location);
        shareLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId()==R.id.share_location) {
                    if (shareLocation.isChecked()) {
                        Toast.makeText(getApplicationContext(),"Ahora estás compartiendo tu ubicación", Toast.LENGTH_LONG).show();
                        allowSharingLocation = true;
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Has dejado de compartir tu ubicación", Toast.LENGTH_LONG).show();
                        allowSharingLocation = false;
                    }
                }
            }
        });

        //Find user location listener
        findUser = findViewById(R.id.find_user);
        findUser.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Log.d(TAG, "onClick: Find user location");
               arrayReceivers = new ArrayList<UserFilter>();
               Intent intent = new Intent(getApplicationContext(), SearchUsers.class);
               intent.putExtra("receivers", arrayReceivers);
               startActivityForResult(intent, Constants.SEARCH_USERS_REQUEST_CODE);
           }
        });

        //Get history
        history = findViewById(R.id.location_history_data);

        updateLocation = findViewById(R.id.updateLocation);
        updateLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanWifi();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            arrayReceivers = (ArrayList) data.getSerializableExtra("receivers");
            receivers = "";
            for(int i=0; i<arrayReceivers.size(); i++){
                receivers += arrayReceivers.get(i) + ",";
            }

            if(arrayReceivers.size() <= 3)
                showAll = false;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void scanWifi() {
        availableNetworks.clear();
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
    }

    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            results = wifiManager.getScanResults();
            unregisterReceiver(this);
            if (results.size() > 0) {
                for (ScanResult scanResult : results) {
                    String bssid = scanResult.BSSID.replace(":", "");
                    double distance = (int) calculateDistance(scanResult.level, scanResult.frequency);
                    addLocations(bssid, distance);
                }
            }
        }
    };

    private double calculateDistance(double levelInDb, double freqInMHz)    {
        double exp = (27.55 - (20 * Math.log10(freqInMHz)) + Math.abs(levelInDb)) / 20.0;
        return Math.pow(10.0, exp);
    }

    private void addLocations(String bssid, double distance) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String uri = url + "ap/" + bssid;
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
    }

}
