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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;
import java.util.List;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.gui.MenuActivity;

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
    private static TextView history;
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
    private ArrayList<String> availableNetworks = new ArrayList<String>();
    /**
     * List to store ScanResults of available networks
     */
    private List<ScanResult> results;
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
               Intent intent = new Intent(getApplicationContext(), FindUser.class);
               startActivity(intent);
           }
        });

        //Show history by default
        history = findViewById(R.id.location_history_data);
        if(history.getText().toString().matches(""))
        {
            history.setText("No data found");
        }

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

            for (ScanResult scanResult : results){
                String bssid = scanResult.BSSID.replace(":","");
                double distance = (int)calculateDistance(scanResult.level,scanResult.frequency);
            }
        }
    };

    private double calculateDistance(double levelInDb, double freqInMHz)    {
        double exp = (27.55 - (20 * Math.log10(freqInMHz)) + Math.abs(levelInDb)) / 20.0;
        return Math.pow(10.0, exp);
    }



}
