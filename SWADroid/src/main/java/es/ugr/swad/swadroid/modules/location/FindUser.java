package es.ugr.swad.swadroid.modules.location;

import com.journeyapps.barcodescanner.BarcodeEncoder;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.gui.MenuActivity;

public class FindUser extends MenuActivity {
    /**
     * Messages tag name for Logcat
     */
    private static final String TAG = Constants.APP_TAG + " Find user location";

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
    @Override
    protected void onStart() {
        super.onStart();
    }
}
