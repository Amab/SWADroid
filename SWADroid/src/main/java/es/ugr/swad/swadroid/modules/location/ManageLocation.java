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
    private ArrayList<String> availableNetworks = new ArrayList<>();
    /**
     * Adapter of LocationDistance array
     */
    private ArrayAdapter adapter;
    /**
     * Array of receivers
     */
    private ArrayList<UserFilter> arrayReceivers;
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
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
                    try {
                        String bssid = scanResult.BSSID.replace(":", "");
                        double distance = (int) calculateDistance(scanResult.level, scanResult.frequency);
                        bssid = "F07F0667D5FF";
                        GetLocation getLocation = new GetLocation(bssid);
                        getLocation.execute().get();
                        Location location = getLocation.getValue();
                        availableNetworks.add("Estás a " + distance + "m de " +
                                location.getRoomFullName() + " ( " + location.getCenterShortName() + " " + location.getInstitutionShortName() + " )");
                        adapter.notifyDataSetChanged();
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

}
