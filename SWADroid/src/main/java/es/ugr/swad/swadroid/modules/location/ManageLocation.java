package es.ugr.swad.swadroid.modules.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.gui.MenuActivity;
import es.ugr.swad.swadroid.model.Location;
import es.ugr.swad.swadroid.modules.messages.SearchUsers;
import es.ugr.swad.swadroid.preferences.Preferences;

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
    private ArrayList arrayReceivers;
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.modules.Module#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.indoor_location);

        setTitle(R.string.manageLocation);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        history = findViewById(R.id.location_history_data);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        assert wifiManager != null;
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
        FloatingActionButton findUser = findViewById(R.id.find_user);

        findUser.setOnClickListener(view -> {
            Log.d(TAG, "onClick: Find user location");
            arrayReceivers = new ArrayList<>();
            Intent intent = new Intent(getApplicationContext(), SearchUsers.class);
            intent.putExtra("receivers", arrayReceivers);
            startActivityForResult(intent, Constants.SEARCH_USERS_REQUEST_CODE);
        });

        //Get history
        history = findViewById(R.id.location_history_data);

        FloatingActionButton updateLocation = findViewById(R.id.update_location);
        updateLocation.setOnClickListener(v -> {
            if(Preferences.getShareLocation()) {
                int syncTime = Integer.parseInt(Preferences.getSyncLocationTime());

                Runnable wifiScanner = this::scanWifi;
                try {
                    ScheduledFuture<?> scheduledWifiScanner = scheduler.scheduleAtFixedRate(wifiScanner, 0, syncTime, TimeUnit.MINUTES);
                }catch (IllegalArgumentException e){
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(getApplicationContext(), "You need to activate sharing location", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            arrayReceivers = (ArrayList) data.getSerializableExtra("receivers");

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
                        SendCurrentLocation sendCurrentLocation = new SendCurrentLocation(location.getRoomCode());
                        sendCurrentLocation.execute();
                    } catch (Exception e) {
                        Log.d(TAG, "EXCEPCION AL OBTENER LA LOCALIZACIÓN");
                        e.printStackTrace();
                    }

                }
            }
        }
    };

    public static double calculateDistance(double levelInDb, double freqInMHz)    {
        double exp = (27.55 - (20 * Math.log10(freqInMHz)) + Math.abs(levelInDb)) / 20.0;
        return Math.pow(10.0, exp);
    }

}
