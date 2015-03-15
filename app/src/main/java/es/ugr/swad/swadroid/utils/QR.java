package es.ugr.swad.swadroid.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.android.Contents;
import com.google.zxing.client.android.Intents;
import com.google.zxing.client.android.encode.QRCodeEncoder;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;

public class QR {

    public static Bitmap encode(Activity activity, String content)
            throws WriterException {

        //Returns the size of the entire window, including status bar and title.
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);

        // Find screen size
        /*WindowManager manager = (WindowManager) activity
                .getSystemService(activity.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		Point point = new Point();
		display.getSize(point);
		int width = point.x;
		int height = point.y;*/
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int smallerDimension = width < height ? width : height;
        //smallerDimension = smallerDimension * 3 / 4;

        Intent intent = new Intent();
        intent.setAction(Intents.Encode.ACTION);
        intent.putExtra(Intents.Encode.FORMAT, BarcodeFormat.QR_CODE.toString());
        intent.putExtra(Intents.Encode.TYPE, Contents.Type.TEXT);
        intent.putExtra(Intents.Encode.DATA, content);
        QRCodeEncoder qrcode = new QRCodeEncoder(activity, intent,
                smallerDimension, false);

        return qrcode.encodeAsBitmap();
    }

}
