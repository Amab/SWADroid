package es.ugr.swad.swadroid.modules.qr;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.EnumMap;
import java.util.Map;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.analytics.SWADroidTracker;
import es.ugr.swad.swadroid.gui.MenuActivity;
import es.ugr.swad.swadroid.modules.login.Login;

public class GenerateQR extends MenuActivity {
    /**
     * Messages tag name for Logcat
     */
    private static final String TAG = Constants.APP_TAG + " GenerateQR";

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

    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.modules.Module#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generate_qr_layout);

        setTitle(R.string.generateQRModuleLabel);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        barcodeEncoder = new BarcodeEncoder();
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
            String qrContents = "@" + Login.getLoggedUser().getUserNickname();
            ImageView qr_image = (ImageView) findViewById(R.id.qr_code_image);
            Bitmap qrCode;
            Map<EncodeHintType, Object> hintMap = new EnumMap<>(EncodeHintType.class);

            hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");

            // Now with zxing version 3.2.1 you could change border size (white border size to just 1)
            hintMap.put(EncodeHintType.MARGIN, 1); /* default = 4 */
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

            qrCode = barcodeEncoder.encodeBitmap(qrContents, BarcodeFormat.QR_CODE, CODE_WIDTH, CODE_HEIGHT, hintMap);
            qr_image.setImageBitmap(qrCode);
        } catch (WriterException e) {
            error(e.getMessage(), e, true);
        }
    }
}
