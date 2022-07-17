package es.ugr.swad.swadroid.modules.rollcall;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

import java.util.Collections;
import java.util.List;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.dao.EventDao;
import es.ugr.swad.swadroid.dao.UserAttendanceDao;
import es.ugr.swad.swadroid.dao.UserDao;
import es.ugr.swad.swadroid.database.AppDatabase;
import es.ugr.swad.swadroid.gui.ImageFactory;
import es.ugr.swad.swadroid.model.Event;
import es.ugr.swad.swadroid.model.User;
import es.ugr.swad.swadroid.model.UserAttendance;
import es.ugr.swad.swadroid.utils.Utils;

/**
 * This Activity performs continuous scanning, processing rollcalls whenever
 * a barcode is scanned.
 *
 * @author Juan Miguel Boyero Corral <swadroid@gmail.com>
 */
public class ContinuousCaptureActivity extends AppCompatActivity {

    private static final String TAG = Constants.APP_TAG + " " + ContinuousCaptureActivity.class.getSimpleName();

    /*
    * Text size in scan window
    */
    private static final int SCAN_TEXT_SIZE = 18;

    /**
     * Scan delay
     */
    private static final long BULK_MODE_SCAN_DELAY_MS = 1000L;

    /**
     * Database Access Object for User
     */
    private UserDao userDao;
    /**
     * Database Access Object for UserAttendance
     */
    private UserAttendanceDao userAttendanceDao;
    /**
     * Database Access Object for Event
     */
    private EventDao eventDao;

    /**
     * Code view
     */
    private CompoundBarcodeView barcodeView;

    /**
     * Callback function called when a code is scanned
     */
    private final BarcodeCallback callback = new BarcodeCallback() {
        private long lastTimestamp = 0;

        @Override
        public void barcodeResult(BarcodeResult result) {
            long currentTime = System.currentTimeMillis();
            long diffLastTimestamp = currentTime - lastTimestamp;

            if (result.getText() != null) {
                if(diffLastTimestamp < BULK_MODE_SCAN_DELAY_MS) {
                    Log.d(TAG, "Too soon after the last barcode - ignore: " + diffLastTimestamp + " ms");
                    return;
                }

                handleDecode(result);
                lastTimestamp = currentTime;
            }
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
            // No-op
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppDatabase db;

        super.onCreate(savedInstanceState);

        setContentView(R.layout.continuous_scan);

        getSupportActionBar().hide();

        try {
            //Initialize database
            db = AppDatabase.getAppDatabase(this);
            userDao = db.getUserDao();
            userAttendanceDao = db.getUserAttendanceDao();
            eventDao = db.getEventDao();
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage(), ex);
        }

        barcodeView = findViewById(R.id.barcode_scanner);
        barcodeView.decodeContinuous(callback);
    }

    @Override
    protected void onResume() {
        super.onResume();

        barcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        barcodeView.pause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    /**
     * A valid barcode has been found, so give an indication of success and show the results.
     *
     * @param result The contents of the barcode.
     */
    private void handleDecode(BarcodeResult result) {
        String messageResult;
        int iconResult;
        String qrContent = result.getText();
        boolean validContent = Utils.isValidNickname(qrContent);
        boolean isUserEnrolledEvent;
        User u = null;
        Event event;

        MediaPlayer mediaPlayer;
        if (validContent) {
            u = userDao.findUserByUserNickname(qrContent.substring(1));
            isUserEnrolledEvent = userAttendanceDao.findByUserCodeAndEventCode(u.getId(), UsersActivity.getEventCode()) != null;

            if ((u != null) && isUserEnrolledEvent) {
                Log.d(TAG, "isUserEnrolledEvent=" + isUserEnrolledEvent);
                //Mark student as present in the event
                userAttendanceDao.insertAttendances(Collections.singletonList(new UserAttendance(u.getId(), UsersActivity.getEventCode(), true)));

                //Mark event status as "pending"
                event = eventDao.findById(UsersActivity.getEventCode());
                event.setStatus("pending");
                eventDao.updateEvents(Collections.singletonList(event));

                messageResult = getString(R.string.scan_valid_student);
                iconResult = R.drawable.ok;
                mediaPlayer = MediaPlayer.create(this, R.raw.beep); // Positive sound

                messageResult += "\n\n"
                        + getString(R.string.scan_id) + ": " + u.getUserID() + "\n"
                        + getString(R.string.scan_name) + ": " + u.getUserFirstname() + " "
                        + u.getUserSurname1() + " " + u.getUserSurname2();
            } else {
                // There is no user with that nickname
                messageResult = getString(R.string.scan_data_not_found);
                iconResult = R.drawable.not_ok;
                mediaPlayer = MediaPlayer.create(this, R.raw.klaxon); // Negative sound
            }
        } else {
            // Not detected any valid ID or nickname
            messageResult = getString(R.string.scan_not_valid_code);
            iconResult = R.drawable.not_ok;
            mediaPlayer = MediaPlayer.create(this, R.raw.klaxon); // Negative sound
        }

        //Release media player when playback ends
        mediaPlayer.setOnCompletionListener(MediaPlayer::release);

        //Play selected sound
        mediaPlayer.start();

        // Show photo of student
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_layout, findViewById(R.id.toast_layout_root));
        ImageView image = layout.findViewById(R.id.image);

        if (u != null) {
            ImageFactory.displayImage(getApplicationContext(), u.getUserPhoto(), image, true, true,
                    R.drawable.usr_bl, R.drawable.usr_bl, R.drawable.usr_bl);
        }

        // Show appropriate icon
        ImageView icon = layout.findViewById(R.id.icon);
        icon.setImageResource(iconResult);

        // Show appropriate message
        TextView toastText = layout.findViewById(R.id.text);
        toastText.setText(messageResult);
        toastText.setGravity(Gravity.CENTER_VERTICAL);
        toastText.setTextSize(SCAN_TEXT_SIZE);

        final Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();

        // Hide message after delay
        if(BULK_MODE_SCAN_DELAY_MS < 2000L) {
            new Handler().postDelayed(toast::cancel, BULK_MODE_SCAN_DELAY_MS);
        }
    }
}
