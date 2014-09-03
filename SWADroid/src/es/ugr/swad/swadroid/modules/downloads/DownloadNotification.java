/*
 *  This file is part of SWADroid.
 *
 *  Copyright (C) 2012 Helena Rodriguez Gijon <hrgijon@gmail.com>
 *
 *  SWADroid is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  SWADroid is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with SWADroid.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.ugr.swad.swadroid.modules.downloads;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.webkit.MimeTypeMap;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.gui.AlertNotificationFactory;

import java.io.File;
import java.util.List;

/**
 * This class manages the notifications associated to downloading files.
 * It will create an ongoing notification while downloading and it will be updated to show the progress in percentage.
 * When the download is completed, it will create a static notification with the hability of opening the downloaded file.
 * When the download is failed, it will create a static notification with an informative message
 *
 * @author Helena Rodriguez Gijon <hrgijon@gmail.com>
 */


public class DownloadNotification {
    private final Context mContext;
    private final int NOTIFICATION_ID = 19982;
    private NotificationCompat.Builder mNotifBuilder;
    private final NotificationManager mNotificationManager;
    private PendingIntent mContentIntent;
    private String mContentTitle;

    public DownloadNotification(Context context) {
        mContext = context;

        //get the notification manager
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    }


    /**
     * Creates an ongoing notification into the status bar
     * This notification will be updated while downloading with the percent downloaded
     *
     * @param fileName name of file that is downloading. It is show on the notification.
     */
    public void createNotification(String fileName) {
        mNotificationManager.cancel(NOTIFICATION_ID);
        //create the notification
        String tickerText = mContext.getString(R.string.notificationDownloadTitle); //Initial text that appears in the status bar

        //create the content which is shown in the notification pulldown
        mContentTitle = fileName; //Full title of the notification in the pull down
        String contentText = "0%" + " " + mContext.getString(R.string.complete);  //Text of the notification in the pull down

        //you have to set a PendingIntent on a notification to tell the system what you want it to do when the notification is selected
        //I don't want to use this here so I'm just creating a blank one
        mContentIntent = PendingIntent.getActivity(mContext, 0, new Intent(), 0);
        
        mNotifBuilder = AlertNotificationFactory.createProgressNotificationBuilder(mContext,
        		mContentTitle,
        		contentText,
        		tickerText,
				mContentIntent,
				android.R.drawable.stat_sys_download,
				android.R.drawable.stat_sys_download,
				true,
				false,
				true, //make this notification appear in the 'Ongoing events' section
				false,
				100,
				0,
				false);

        //show the notification
        AlertNotificationFactory.showAlertNotification(mContext, mNotifBuilder.build(), NOTIFICATION_ID);
    }

    /**
     * Receives progress updates from the background task and updates the status bar notification appropriately
     *
     * @param percentageComplete
     */
    public void progressUpdate(int percentageComplete) {
        mNotifBuilder.setProgress(100, percentageComplete, false);
        
        AlertNotificationFactory.showAlertNotification(mContext, mNotifBuilder.build(), NOTIFICATION_ID);
    }

    /**
     * Called when the downloading task is complete.
     * It removes the ongoing notification and creates a new static notification. The new notification will open the downloaded file and erase itself when it is clicked
     *
     * @param directoryPath directory where the downloaded file is stored
     * @param fileName      name of the just downloaded file
     */
    public void completedDownload(String directoryPath, String fileName) {
        mNotificationManager.cancel(NOTIFICATION_ID);

        //create the notification
        String tickerText = mContext.getString(R.string.downloadCompletedTitle); //Initial text that appears in the status bar


        //activity that will be launched when the notification is clicked.
        //this activity will open the downloaded file with the default app. 
        Intent notificationIntent = openFileDefaultApp(directoryPath + File.separator + fileName);
        if (notificationIntent != null)
            mContentIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, 0);


        //build up the new status message
        String contentText;
        if (notificationIntent != null) {
            contentText = mContext.getString(R.string.clickToOpenFile);
            mContentTitle = fileName; //Full title of the notification in the pull down
        } else {
            contentText = mContext.getString(R.string.noApp);
            mContentTitle = directoryPath + File.separator + fileName;
        }
        
        mNotifBuilder = AlertNotificationFactory.createProgressNotificationBuilder(mContext,
        		mContentTitle,
        		contentText,
        		tickerText,
        		mContentIntent,
				android.R.drawable.stat_sys_download_done,
				android.R.drawable.stat_sys_download_done,
				true,
				true,
				true, //make this notification appear in the 'Ongoing events' section
				false,
				100,
				0,
				false);
        
        AlertNotificationFactory.showAlertNotification(mContext, mNotifBuilder.build(), NOTIFICATION_ID);
    }

    /**
     * Called when the downloading task could not be completed
     * It removes the ongoing notification and creates a new static notification. The new notification will inform about download problem and erase itself when it is clicked
     */
    public void eraseNotification(String fileName) {
        //remove the notification from the status bar
        mNotificationManager.cancel(NOTIFICATION_ID);
        
        String tickerText = mContext.getString(R.string.downloadProblemTitle); //Initial text that appears in the status bar

        mContentTitle = fileName;
        //build up the new status message
        String contentText = mContext.getString(R.string.downloadProblemMsg);

        Intent notificationIntent = new Intent();
        mContentIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, 0);
        
        mNotifBuilder = AlertNotificationFactory.createProgressNotificationBuilder(mContext,
        		mContentTitle,
        		contentText,
        		tickerText,
        		mContentIntent,
				android.R.drawable.stat_sys_warning,
				android.R.drawable.stat_sys_warning,
				true,
				false,
				true, //make this notification appear in the 'Ongoing events' section
				false,
				100,
				0,
				false);
        
        AlertNotificationFactory.showAlertNotification(mContext, mNotifBuilder.build(), NOTIFICATION_ID);
    }

    /**
     * It is defined an Intent to open the file located in @a absolutePath with the default app associated with its extension
     *
     * @return null    It does not exist an app associated with the file type located in @a absolutePath
     *         otherwise an intent that will launch the right app to open the file located in @a absolutePath
     */
    private Intent openFileDefaultApp(String absolutePath) {
        File file = new File(absolutePath);
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        int lastDotIndex = absolutePath.lastIndexOf(".");
        String extension = absolutePath.substring(lastDotIndex + 1);
        String MIME = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        intent.setDataAndType(Uri.fromFile(file), MIME);

        PackageManager packageManager = this.mContext.getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
        boolean isIntentSafe = activities.size() > 0;
        if (isIntentSafe)
            return intent;
        else
            return null;
    }


}
