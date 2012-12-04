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

import java.io.File;

import es.ugr.swad.swadroid.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.webkit.MimeTypeMap;

public class DownloadNotification {
    private Context mContext;
    private int NOTIFICATION_ID = 1982;
    private Notification mNotification;
    private NotificationManager mNotificationManager;
    private PendingIntent mContentIntent;
    private CharSequence mContentTitle;
    private String fileName = "";
    private String directoryPath = "";
    private boolean downloadFinished = false;
    
    public DownloadNotification(Context context)
    {
        mContext = context;
    }

    
    /**
     * Put the notification into the status bar
     */
    public void createNotification(String directoryPath,String fileName) {
    	this.fileName = fileName;
    	this.directoryPath = directoryPath;
        //get the notification manager
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        //create the notification
        int icon = android.R.drawable.stat_sys_download;
        CharSequence tickerText =mContext.getString(R.string.app_name)+" " + mContext.getString(R.string.notificationDownloadTitle); //Initial text that appears in the status bar
        long when = System.currentTimeMillis();
        mNotification = new Notification(icon, tickerText, when);

        //create the content which is shown in the notification pulldown
       // mContentTitle = "Your download is in progress";
        mContentTitle = fileName; //Full title of the notification in the pull down
        CharSequence contentText = "O Bytes";//"0%" +" "+ mContext.getString(R.string.complete);  //Text of the notification in the pull down

        //you have to set a PendingIntent on a notification to tell the system what you want it to do when the notification is selected
        //I don't want to use this here so I'm just creating a blank one
        Intent notificationIntent = openFileDefaultApp(this.directoryPath + File.separator+this.fileName);
        mContentIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, 0);

        //add the additional content and intent to the notification
        mNotification.setLatestEventInfo(mContext, mContentTitle, contentText, mContentIntent);

        //make this notification appear in the 'Ongoing events' section
        mNotification.flags = Notification.FLAG_ONGOING_EVENT;

        //show the notification
        mNotificationManager.notify(NOTIFICATION_ID, mNotification);
    }

    /**
     * Receives progress updates from the background task and updates the status bar notification appropriately
     * @param percentageComplete
     */
    public void progressUpdate(int percentageComplete) {
        //build up the new status message
        CharSequence contentText = percentageComplete + "% "+ mContext.getString(R.string.complete);
        //publish it to the status bar
        mNotification.setLatestEventInfo(mContext, mContentTitle, contentText, mContentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mNotification);
    }

    /**
     * called when the background task is complete, this removes the notification from the status bar.
     * We could also use this to add a new ‘task complete’ notification
     */
    public void completedDownload()    {
    	
    	mNotificationManager.cancel(NOTIFICATION_ID);
    	
       //create the notification
        int icon = android.R.drawable.stat_sys_download_done;
        CharSequence tickerText =mContext.getString(R.string.app_name)+" " + mContext.getString(R.string.notificationDownloadTitle); //Initial text that appears in the status bar
        long when = System.currentTimeMillis();
        mNotification = new Notification(icon, tickerText, when);
    	
    	
        //build up the new status message
        CharSequence contentText = "100% "+ mContext.getString(R.string.complete)+"click hier to open";
        //publish it to the status bar
        mNotification.setLatestEventInfo(mContext, mContentTitle, contentText, mContentIntent);
        //create the content which is shown in the notification pulldown
       // mContentTitle = "Your download is in progress";
        mContentTitle = fileName; //Full title of the notification in the pull down
        //you have to set a PendingIntent on a notification to tell the system what you want it to do when the notification is selected
        //I don't want to use this here so I'm just creating a blank one
        Intent notificationIntent = openFileDefaultApp(this.directoryPath + File.separator+this.fileName);
        mContentIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, 0);

        //add the additional content and intent to the notification
        mNotification.setLatestEventInfo(mContext, mContentTitle, contentText, mContentIntent);
        mNotification.flags = Notification.FLAG_AUTO_CANCEL;
        mNotificationManager.notify(NOTIFICATION_ID, mNotification);

    }
    
    /**
     * Notification update when a problem during downloading occurs  
     * */
    public void eraseNotification(){
        //remove the notification from the status bar
        mNotificationManager.cancel(NOTIFICATION_ID);
    }
    
	private Intent openFileDefaultApp(String absolutePath){
		File file = new File(absolutePath);
		Intent intent = null;

		intent = new Intent();
		intent.setAction(android.content.Intent.ACTION_VIEW);
		int lastDotIndex = absolutePath.lastIndexOf(".");
		String extension = absolutePath.substring(lastDotIndex+1);
		String MIME = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
		intent.setDataAndType(Uri.fromFile(file), MIME);

		return intent;
	}
	
	
	
}
