package es.ugr.swad.swadroid.utils;

/**
 * Class for manage Android Oreo notifications
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.O)
public class NotificationUtils extends ContextWrapper {

    private NotificationManager mManager;
    public static final String SWADROID_CHANNEL_ID = "es.ugr.swad.swadroid.SWADROID";
    public static final String SWADROID_CHANNEL_NAME = "SWADROID CHANNEL";

    public NotificationUtils(Context base) {
        super(base);
        createChannels();
    }

    public void createChannels() {

        // create SWADroid channel
        NotificationChannel androidChannel = new NotificationChannel(SWADROID_CHANNEL_ID,
                SWADROID_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        // Sets whether notifications posted to this channel should display notification lights
        androidChannel.enableLights(true);
        // Sets whether notification posted to this channel should vibrate.
        androidChannel.enableVibration(true);
        // Sets the notification light color for notifications posted to this channel
        androidChannel.setLightColor(Color.GREEN);
        // Sets whether notifications posted to this channel appear on the lockscreen or not
        androidChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

        getManager().createNotificationChannel(androidChannel);
    }

    public NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }
}
