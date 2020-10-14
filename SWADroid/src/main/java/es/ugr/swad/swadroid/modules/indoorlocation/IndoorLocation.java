package es.ugr.swad.swadroid.modules.indoorlocation;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.gui.MenuActivity;
import es.ugr.swad.swadroid.model.Location;
import es.ugr.swad.swadroid.model.LocationTimeStamp;
import es.ugr.swad.swadroid.model.Roles;
import es.ugr.swad.swadroid.model.UserFilter;
import es.ugr.swad.swadroid.modules.messages.SearchUsers;
import es.ugr.swad.swadroid.preferences.Preferences;

public class IndoorLocation extends MenuActivity {

    private static final String TAG = Constants.APP_TAG + " IndoorLocation";

    private final List<String> locationHistory = new ArrayList<>();

    private final List<Pair<ScanResult, Integer>> availableNetworks = new ArrayList<>();

    private final double FREE_SPACE_PATH_LOSS_CONSTANT = 27.55;

    private ListView history;

    private ArrayAdapter<String> locationHistoryAdapter;

    private WifiManager wifiManager;

    private ArrayList<Parcelable> arrayReceivers = new ArrayList<>();

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private TextView textView;

    private boolean showMac = false;

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

        locationHistory.add(getResources().getString(R.string.lostLocation));
        locationHistoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, locationHistory);
        history.setAdapter(locationHistoryAdapter);

        history.setOnItemClickListener((adapterView, view, i, l) -> {
            ClipboardManager clipboard = (ClipboardManager)
                    getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("macAddress", availableNetworks.get(0).first.BSSID);
            clipboard.setPrimaryClip(clip);
        });
    }

    private void init() {
        Intent getAvailableRoles = new Intent(getApplicationContext(), GetAvailableRoles.class);
        startActivityForResult(getAvailableRoles, Constants.GET_AVAILABLE_ROLES);

        FloatingActionButton findUser = findViewById(R.id.find_user);
        findUser.setOnClickListener(view -> {
            stopScheduler();
            Intent intent = new Intent(getApplicationContext(), SearchUsers.class);
            intent.putExtra("receivers", arrayReceivers);
            startActivityForResult(intent, Constants.SEARCH_USERS_REQUEST_CODE);
        });

        history = findViewById(R.id.location_history_data);

        FloatingActionButton updateLocation = findViewById(R.id.user_location);
        updateLocation.setOnClickListener(v -> {
            stopScheduler();
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

        FloatingActionButton findMac = findViewById(R.id.find_mac);
        findMac.setOnClickListener(v -> {
            showMac = true;
            scanWifi();
        });
    }

    private void stopScheduler() {
        scheduler.shutdownNow();
        scheduler = Executors.newScheduledThreadPool(1);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            requestMarshmallowPermissions();
        } else {
            requestSimplePermissions();
        }

    }

    private void requestSimplePermissions() {
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

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void requestMarshmallowPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE)
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE)
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                        != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_WIFI_STATE,
                            Manifest.permission.CHANGE_WIFI_STATE,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION}, Constants.PERMISSION_MULTIPLE);
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
                    if (arrayReceivers != null && !arrayReceivers.isEmpty()) {
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
                            stopScheduler();
                            Log.d(TAG, "No more available networks");
                        } else {
                            Intent getLocation = new Intent(this.getApplicationContext(), GetLocation.class);
                            getLocation.putExtra("mac", availableNetworks.get(0).first.BSSID.replace(":",""));
                            availableNetworks.remove(0);
                            startActivityForResult(getLocation, Constants.GET_LOCATION);
                        }
                    }
                    break;
                case Constants.GET_LAST_LOCATION:
                    LocationTimeStamp locationTimeStamp = (LocationTimeStamp) data.getSerializableExtra("locationTimeStamp");
                    if (locationTimeStamp != null && locationTimeStamp.getRoomCode() != -1) {
                        Date checkIn = new Date((long)locationTimeStamp.getCheckInTime()*1000);
                        SimpleDateFormat ft = new SimpleDateFormat ("hh:mm a", Locale.getDefault());
                        locationHistory.clear();
                        locationHistory.add(
                                getBasicInformation(locationTimeStamp) +
                                getResources().getString(R.string.checkIn) + ": "+ ft.format(checkIn));
                        locationHistoryAdapter.notifyDataSetChanged();
                    } else {
                        if (locationHistory.isEmpty()) {
                            locationHistory.add(getResources().getString(R.string.lostLocation));
                            locationHistoryAdapter.notifyDataSetChanged();
                        }
                    }
                    break;
                case Constants.SEND_CURRENT_LOCATION:
                    if ((boolean) data.getSerializableExtra("success"))
                        Log.d(TAG, "Current location sent");
                    break;
                case Constants.GET_AVAILABLE_ROLES:
                    int rol = ((Roles) Objects.requireNonNull(data.getSerializableExtra("roles"))).getRol();
                    int bitEnabled = Integer.toBinaryString(rol).length();

                    FloatingActionButton findMac = findViewById(R.id.find_mac);
                    if (bitEnabled >= 7 ){
                        findMac.setVisibility(View.VISIBLE);
                    }else{
                        findMac.setVisibility(View.INVISIBLE);
                    }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void scanWifi() {
        Log.i(TAG, "SCANNING WIFI");
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
    }

    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            List<ScanResult> results = wifiManager.getScanResults();
            unregisterReceiver(this);
            if (!results.isEmpty()) {
                for (ScanResult scanResult : results) {
                    int distance = (int) calculateDistance(scanResult.level, scanResult.frequency);
                    availableNetworks.add(new Pair<>(scanResult, distance));
                }
                availableNetworks.sort((n1, n2) -> n1.second - n2.second);
                scanFinished();
            }
        }

        private void scanFinished() {
            if (!showMac){
                Intent getLocation = new Intent(getApplicationContext(), GetLocation.class);
                getLocation.putExtra("mac", availableNetworks.get(0).first.BSSID.replace(":",""));
                getLocation.putExtra("distance", availableNetworks.get(0).second.doubleValue());
                startActivityForResult(getLocation, Constants.GET_LOCATION);
            } else {
                showMac = false;
                locationHistory.clear();
                locationHistory.add(availableNetworks.get(0).first.SSID + " - " + availableNetworks.get(0).first.BSSID + " - " + availableNetworks.get(0).second + "m" );
                locationHistoryAdapter.notifyDataSetChanged();
                textView.setText(getString(R.string.nearestLocation));
            }

        }

        private double calculateDistance(double levelInDb, double freqInMHz) {
            double exp = (FREE_SPACE_PATH_LOSS_CONSTANT - (20 * Math.log10(freqInMHz)) + Math.abs(levelInDb)) / 20.0;
            return Math.pow(10.0, exp);
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.PERMISSION_MULTIPLE: {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                            && grantResults[1] == PackageManager.PERMISSION_GRANTED
                            && grantResults[2] == PackageManager.PERMISSION_GRANTED
                            && grantResults[3] == PackageManager.PERMISSION_GRANTED) {
                        init();
                    } else {
                        setResult(Activity.RESULT_CANCELED);
                        finish();
                    }
                } else {
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
    }

    private String getBasicInformation(Location location) {
        return  getResources().getString(R.string.institution) + ": "  + location.getInstitutionShortName() + "\n" +
                getResources().getString(R.string.center) + ": " + location.getCenterFullName() + "\n" +
                getResources().getString(R.string.building) + ": " + location.getBuildingFullName() + "\n" +
                getResources().getString(R.string.floor) + ": " + location.getFloor() + "\n" +
                getResources().getString(R.string.room) + ": " + location.getFloor() + "\n";
    }
}
