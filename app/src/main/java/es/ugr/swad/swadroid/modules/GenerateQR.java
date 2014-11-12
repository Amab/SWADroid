package es.ugr.swad.swadroid.modules;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

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
        ImageView qr_image = (ImageView) findViewById(R.id.qr_code_image);

        SWADroidTracker.sendScreenView(getApplicationContext(), TAG);

        try {
            Bitmap qrCode = QR.encode(this, "@" + Login.getLoggedUser().getUserNickname());
            qr_image.setImageBitmap(qrCode);
        } catch (WriterException e) {
            error(TAG, e.getMessage(), e, true);
        }
    }
}
