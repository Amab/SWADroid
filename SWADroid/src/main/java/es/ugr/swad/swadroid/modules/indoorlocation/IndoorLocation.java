package es.ugr.swad.swadroid.modules.indoorlocation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
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

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.gui.MenuActivity;
import es.ugr.swad.swadroid.model.Location;
import es.ugr.swad.swadroid.model.LocationTimeStamp;
import es.ugr.swad.swadroid.model.UserFilter;
import es.ugr.swad.swadroid.modules.messages.SearchUsers;
import es.ugr.swad.swadroid.preferences.Preferences;

public class IndoorLocation extends MenuActivity {

    private static final String TAG = Constants.APP_TAG + " IndoorLocation";

    private ListView history;

    private ArrayList<String> locationHistory = new ArrayList<>();

    private ArrayAdapter<String> locationHistoryAdapter;

    private WifiManager wifiManager;

    public ArrayList<Parcelable> arrayReceivers = new ArrayList<>();

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    TextView textView;

    List<Pair<ScanResult, Integer>> availableNetworks = new ArrayList<>();

    double FREE_SPACE_PATH_LOSS_CONSTANT = 27.55;

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
        }

        locationHistoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, locationHistory);
        history.setAdapter(locationHistoryAdapter);
    }

    private void init() {
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
            scheduler.shutdownNow();
            scheduler = Executors.newScheduledThreadPool(1);
            if(Preferences.getShareLocation()) {
                textView.setText(getString(R.string.lastLocation));

                int syncTime = Integer.parseInt(Preferences.getSyncLocationTime());
                Runnable wifiScanner = this::scanWifi;
                try {
                    scheduler.scheduleAtFixedRate(wifiScanner,0, syncTime, TimeUnit.MINUTES);
                } catch (IllegalArgumentException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            } else {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.locationDisabled), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // check Android 6 permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE)
                        != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                 Manifest.permission.ACCESS_WIFI_STATE,
                                 Manifest.permission.CHANGE_WIFI_STATE}, Constants.PERMISSION_MULTIPLE);
        } else {
            init();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (data != null) {
            switch (requestCode) {
                case Constants.SEARCH_USERS_REQUEST_CODE:
                    arrayReceivers = data.getParcelableArrayListExtra("receivers");
                    if (arrayReceivers != null && arrayReceivers.size() > 0) {
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
                    break;
                case Constants.GET_LOCATION:
                    Location location = (es.ugr.swad.swadroid.model.Location) data.getSerializableExtra("location");
                    if (location != null) {
                        double distance = (double) data.getSerializableExtra("distance");
                        locationHistory.add(
                                getBasicInformation(location) +
                                getResources().getString(R.string.distance) + ": " + distance + " m");
                        locationHistoryAdapter.notifyDataSetChanged();
                        Intent sendCurrentLocation = new Intent(getApplicationContext(), SendCurrentLocation.class);
                        sendCurrentLocation.putExtra("roomCode", location.getRoomCode());
                        startActivityForResult(sendCurrentLocation, Constants.SEND_CURRENT_LOCATION);
                    } else {
                        if (availableNetworks.isEmpty()) {
                            Log.d(TAG, "No more available networks");
                        } else {
                            availableNetworks.remove(0);
                            Intent getLocation = new Intent(this.getApplicationContext(), GetLocation.class);
                            getLocation.putExtra("mac", availableNetworks.get(0).first.BSSID.replace(":",""));
                            startActivityForResult(getLocation, Constants.GET_LOCATION);
                        }
                    }
                    break;
                case Constants.GET_LAST_LOCATION:
                    LocationTimeStamp locationTimeStamp = (LocationTimeStamp) data.getSerializableExtra("locationTimeStamp");
                    if ( locationTimeStamp != null) {
                        Date checkIn = new Date((long)locationTimeStamp.getCheckInTime()*1000);
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat ft = new SimpleDateFormat ("hh:mm a");
                        locationHistory.clear();
                        locationHistory.add(
                                getBasicInformation(locationTimeStamp) +
                                getResources().getString(R.string.checkIn) + ": "+ ft.format(checkIn));
                        locationHistoryAdapter.notifyDataSetChanged();
                    }
                    break;
                case Constants.SEND_CURRENT_LOCATION:
                    if ((boolean) data.getSerializableExtra("success"))
                        Log.d(TAG, "Current location sent");
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void scanWifi() {
        Log.i(TAG, "SCANNING WIFI");
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
                getLocation.putExtra("mac", availableNetworks.get(0).first.BSSID.replace(":",""));
                getLocation.putExtra("distance", availableNetworks.get(0).second.doubleValue());
                startActivityForResult(getLocation, Constants.GET_LOCATION);
            }
        }
    };

    public double calculateDistance(double levelInDb, double freqInMHz) {
        double exp = (FREE_SPACE_PATH_LOSS_CONSTANT - (20 * Math.log10(freqInMHz)) + Math.abs(levelInDb)) / 20.0;
        return Math.pow(10.0, exp);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.PERMISSION_MULTIPLE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    init();
                } else {
                    setResult(Activity.RESULT_CANCELED);
                    finish();
                }
            }
        }
    }

    private String getBasicInformation(Location location) {
        return  getResources().getString(R.string.institution) + ": "  + location.getInstitutionShortName() + "\n" +
                getResources().getString(R.string.center) + ": " + location.getCenterFullName() + "\n" +
                getResources().getString(R.string.building) + ": " + location.getBuildingFullName() + "\n" +
                getResources().getString(R.string.floor) + ": " + location.getFloor() + "\n" +
                getResources().getString(R.string.room) + ": " + location.getFloor() + "\n";
    }
}
