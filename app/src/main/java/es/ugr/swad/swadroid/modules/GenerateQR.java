package es.ugr.swad.swadroid.modules;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.WriterException;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.SWADroidTracker;
import es.ugr.swad.swadroid.gui.MenuActivity;
import es.ugr.swad.swadroid.utils.QR;

public class GenerateQR extends MenuActivity {
    /**
     * Messages tag name for Logcat
     */
    private static final String TAG = Constants.APP_TAG + " GenerateQR";

    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.modules.Module#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generate_qr_layout);

        setTitle(R.string.generateQRModuleLabel);
    	getSupportActionBar().setIcon(R.drawable.qr);
    }

    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.modules.Module#onStart()
     */
    @Override
    protected void onStart() {
        super.onStart();

        SWADroidTracker.sendScreenView(getApplicationContext(), TAG);

        if (!Login.isLogged() || (Login.getLoggedUser() == null)) {
            Intent activity = new Intent(getApplicationContext(), Login.class);
            startActivityForResult(activity, Constants.LOGIN_REQUEST_CODE);
        } else {
            generateQR();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Constants.LOGIN_REQUEST_CODE:
                    if ((Login.getLoggedUser() != null) && !Login.getLoggedUser().getUserNickname().equals(Constants.NULL_VALUE)) {
                        generateQR();
                    } else {
                        Login.setLogged(false);
                        Toast.makeText(getApplicationContext(), R.string.errorNoUserNickname, Toast.LENGTH_LONG).show();

                        finish();
                    }
                    break;
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            switch (requestCode) {
                case Constants.LOGIN_REQUEST_CODE:
                    finish();
                    break;
            }
        }
    }

    private void generateQR() {
        try {
            ImageView qr_image = (ImageView) findViewById(R.id.qr_code_image);
            Bitmap qrCode = QR.encode(this, "@" + Login.getLoggedUser().getUserNickname());
            qr_image.setImageBitmap(qrCode);
        } catch (WriterException e) {
            error(TAG, e.getMessage(), e, true);
        }
    }
}
