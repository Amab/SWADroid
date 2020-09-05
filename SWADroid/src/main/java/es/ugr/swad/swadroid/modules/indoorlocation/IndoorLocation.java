package es.ugr.swad.swadroid.modules.indoorlocation;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.util.Pair;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.gui.MenuActivity;
import es.ugr.swad.swadroid.model.LocationTimeStamp;
import es.ugr.swad.swadroid.model.UserFilter;
import es.ugr.swad.swadroid.modules.messages.SearchUsers;
import es.ugr.swad.swadroid.preferences.Preferences;

public class IndoorLocation extends MenuActivity {

    private static final String TAG = Constants.APP_TAG + " Location";

    private ListView history;

    private ArrayList<String> locationHistory = new ArrayList<>();

    private ArrayAdapter<String> locationHistoryAdapter;

    private WifiManager wifiManager;

    public ArrayList<Parcelable> arrayReceivers = new ArrayList<>();

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    TextView textView;

    List<Pair<ScanResult, Integer>> availableNetworks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.indoor_location);

        setTitle(R.string.manageLocation);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        textView = findViewById(R.id.location_history);

        history = findViewById(R.id.location_history_data);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        assert wifiManager != null;
        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(this, "Wifi is disabled ... You need to enable it", Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
        }

        locationHistoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, locationHistory);
        history.setAdapter(locationHistoryAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FloatingActionButton findUser = findViewById(R.id.find_user);
        findUser.setOnClickListener(view -> {
            scheduler.shutdownNow();
            scheduler = Executors.newScheduledThreadPool(1);
            Intent intent = new Intent(getApplicationContext(), SearchUsers.class);
            intent.putExtra("receivers", arrayReceivers);
            startActivityForResult(intent, Constants.SEARCH_USERS_REQUEST_CODE);
        });

        history = findViewById(R.id.location_history_data);

        FloatingActionButton updateLocation = findViewById(R.id.user_location);
        updateLocation.setOnClickListener(v -> {
            if(Preferences.getShareLocation()) {
                textView.setText(getString(R.string.locationHistory));

                scheduler.shutdownNow();
                int syncTime = Integer.parseInt(Preferences.getSyncLocationTime());
                Runnable wifiScanner = this::scanWifi;
                try {
                    scheduler = Executors.newScheduledThreadPool(1);
                    scheduler.scheduleAtFixedRate(wifiScanner,0, syncTime, TimeUnit.MINUTES);
                }catch (IllegalArgumentException e){
                    e.printStackTrace();
                }
            }else{
                scheduler.shutdownNow();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.locationDisabled), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            switch (requestCode) {
                case Constants.SEARCH_USERS_REQUEST_CODE:
                    if (data != null) {
                        arrayReceivers = data.getParcelableArrayListExtra("receivers");
                        if(arrayReceivers != null && arrayReceivers.size() > 0){
                            locationHistory.clear();
                            locationHistoryAdapter.notifyDataSetChanged();
                            UserFilter user = ((UserFilter)arrayReceivers.get(0));
                            String userText = getResources().getString(R.string.userLocation) + " " + user.getUserFirstname() + " "
                                    + user.getUserSurname1();
                            textView.setText(userText);
                            Intent getLastLocation = new Intent(getApplicationContext(), GetLastLocation.class);
                            getLastLocation.putExtra("userCode", user.getUserCode());
                            startActivityForResult(getLastLocation, Constants.GET_LAST_LOCATION);
                        }
                    }
                    break;
                case Constants.GET_LOCATION:
                    if (data != null) {
                        es.ugr.swad.swadroid.model.Location location = (es.ugr.swad.swadroid.model.Location) data.getSerializableExtra("location");
                        if (location != null) {
                            double distance = (double) data.getSerializableExtra("distance");
                            locationHistory.add(
                                    getResources().getString(R.string.institution) + ": "  + location.getInstitutionShortName() + "\n" +
                                    getResources().getString(R.string.center) + ": " + location.getCenterFullName() + "\n" +
                                    getResources().getString(R.string.building) + ": " + location.getBuildingFullName() + "\n" +
                                    getResources().getString(R.string.floor) + ": " + location.getFloor() + "\n" +
                                    getResources().getString(R.string.room) + ": " + location.getFloor() + "\n" +
                                    getResources().getString(R.string.distance) + ": " + distance + " m");
                            locationHistoryAdapter.notifyDataSetChanged();
                            Intent sendCurrentLocation = new Intent(getApplicationContext(), SendCurrentLocation.class);
                            sendCurrentLocation.putExtra("roomCode", location.getRoomCode());
                            startActivityForResult(sendCurrentLocation, Constants.SEND_CURRENT_LOCATION);
                        }else {
                            try {
                                availableNetworks.remove(0);
                                Intent getLocation = new Intent(this.getApplicationContext(), GetLocation.class);
                                getLocation.putExtra("mac", availableNetworks.get(0).first.BSSID.replace(":",""));
                                startActivityForResult(getLocation, Constants.GET_LOCATION);
                            }catch (IndexOutOfBoundsException e){
                                Log.d(TAG, "No more available networks");
                            }
                        }
                    }
                    break;
                case Constants.GET_LAST_LOCATION:
                    if (data != null){
                        LocationTimeStamp locationTimeStamp = (LocationTimeStamp) data.getSerializableExtra("locationTimeStamp");
                        if ( locationTimeStamp != null) {
                            Date checkIn = new Date((long)locationTimeStamp.getCheckInTime()*1000);
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat ft = new SimpleDateFormat ("hh:mm a");
                            locationHistory.clear();
                            locationHistory.add(
                                    getResources().getString(R.string.institution) + ": " + locationTimeStamp.getInstitutionShortName() + "\n" +
                                    getResources().getString(R.string.center) + ": " + locationTimeStamp.getCenterShortName() + "\n" +
                                    getResources().getString(R.string.building) + ": " + locationTimeStamp.getBuildingFullName() + "\n" +
                                    getResources().getString(R.string.floor) + ": " + locationTimeStamp.getFloor() + "\n" +
                                    getResources().getString(R.string.room) + ": "+ locationTimeStamp.getRoomFullName() + "\n" +
                                    getResources().getString(R.string.checkIn) + ": "+ ft.format(checkIn) );
                            locationHistoryAdapter.notifyDataSetChanged();
                        }
                    }
                    break;
                case Constants.SEND_CURRENT_LOCATION:
                    if (data != null){
                        if ((boolean) data.getSerializableExtra("success"))
                            Log.d(TAG, "Current location sent");
                    }
                    break;
            }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void scanWifi() {
        Log.d(TAG, "SCANNING WIFI");
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
    }

    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            List<ScanResult> results = wifiManager.getScanResults();
            unregisterReceiver(this);
            if (results.size() > 0) {
                for (ScanResult scanResult : results) {
                    int distance = (int) calculateDistance(scanResult.level, scanResult.frequency);
                    availableNetworks.add(new Pair<>(scanResult, distance));
                }
                Collections.sort(availableNetworks, (n1,n2) -> n1.second - n2.second);
                Intent getLocation = new Intent(context, GetLocation.class);
                getLocation.putExtra("mac", "F07F0667D5FF"); //availableNetworks.get(0).first.BSSID.replace(":",""));
                getLocation.putExtra("distance", availableNetworks.get(0).second.doubleValue());
                startActivityForResult(getLocation, Constants.GET_LOCATION);
            }
        }
    };

    public double calculateDistance(double levelInDb, double freqInMHz) {
        double FREE_SPACE_PATH_LOSS_CONSTANT = 27.55;
        double exp = (FREE_SPACE_PATH_LOSS_CONSTANT - (20 * Math.log10(freqInMHz)) + Math.abs(levelInDb)) / 20.0;
        return Math.pow(10.0, exp);
    }

}
