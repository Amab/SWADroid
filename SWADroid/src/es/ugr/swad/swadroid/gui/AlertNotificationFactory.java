/*
 *  This file is part of SWADroid.
 *
 *  Copyright (C) 2010 Juan Miguel Boyero Corral <juanmi1982@gmail.com>
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

package es.ugr.swad.swadroid.gui;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import es.ugr.swad.swadroid.Preferences;

/**
 * Class for create notification alerts.
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class AlertNotificationFactory {
	public static NotificationCompat.Builder createAlertNotificationBuilder(Context context, String contentTitle, String contentText,
			String ticker, PendingIntent pendingIntent, int smallIcon, int largeIcon, boolean alertSignals,
			boolean autocancel, boolean ongoing, boolean onlyAlertOnce) {
		
		int flags = 0;

    	NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(context)
            .setAutoCancel(autocancel)
            .setSmallIcon(smallIcon)
            .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), largeIcon))
            .setContentTitle(contentTitle)
            .setContentText(contentText)
            .setTicker(ticker)
            .setOngoing(ongoing)
            .setOnlyAlertOnce(onlyAlertOnce)
        	.setWhen(System.currentTimeMillis());
            //.setLights(Color.GREEN, 500, 500);

        //Launch activity on alert click
    	if(pendingIntent != null) {
            notifBuilder.setContentIntent(pendingIntent);
    	}
        
        //Add sound, vibration and lights
    	if(alertSignals) {
	        if(Preferences.isNotifSoundEnabled()) {
	        	flags |= Notification.DEFAULT_SOUND;
	        } else {
	        	notifBuilder.setSound(null);
	        }
	        
	        if(Preferences.isNotifVibrateEnabled()) {
	        	flags |= Notification.DEFAULT_VIBRATE;
	        }
	        
	        if(Preferences.isNotifLightsEnabled()) {
	        	flags |= Notification.DEFAULT_LIGHTS;
	        }
    	}
        
    	notifBuilder.setDefaults(flags);
    	
		return notifBuilder;
	}
	
	public static NotificationCompat.Builder createProgressNotificationBuilder(Context context, String contentTitle, String contentText,
			String ticker, PendingIntent pendingIntent, int smallIcon, int largeIcon, boolean alertSignals,
			boolean autocancel, boolean ongoing, boolean onlyAlertOnce, int maxProgress, int progress, boolean indeterminate) {
		
		NotificationCompat.Builder notifBuilder = createAlertNotificationBuilder(context, 
    			contentTitle, 
    			contentText,
    			ticker, 
    			pendingIntent, 
    			smallIcon,
    			largeIcon, 
    			alertSignals,
    			autocancel, 
    			ongoing, 
    			onlyAlertOnce);
    	
    	notifBuilder.setProgress(maxProgress, progress, indeterminate);
		
		return notifBuilder;
	}
	
	public static Notification createAlertNotification(Context context, String contentTitle, String contentText,
			String ticker, PendingIntent pendingIntent, int smallIcon, int largeIcon, boolean alertSignals,
			boolean autocancel, boolean ongoing, boolean onlyAlertOnce) {

    	NotificationCompat.Builder notifBuilder = createAlertNotificationBuilder(context, 
    			contentTitle, 
    			contentText,
    			ticker, 
    			pendingIntent, 
    			smallIcon,
    			largeIcon, 
    			alertSignals,
    			autocancel,
    			ongoing, 
    			onlyAlertOnce);
       
        //Create alert
        return notifBuilder.build();
    }
	
	public static Notification createProgressNotification(Context context, String contentTitle, String contentText,
			String ticker, PendingIntent pendingIntent, int smallIcon, int largeIcon, boolean alertSignals,
			boolean autocancel, boolean ongoing, boolean onlyAlertOnce, int maxProgress, int progress, boolean indeterminate) {

    	NotificationCompat.Builder notifBuilder = createProgressNotificationBuilder(context, 
    			contentTitle, 
    			contentText,
    			ticker, 
    			pendingIntent, 
    			smallIcon,
    			largeIcon, 
    			alertSignals,
    			autocancel,
    			ongoing, 
    			onlyAlertOnce,
    			maxProgress, 
    			progress, 
    			indeterminate);
       
        //Create alert
        return notifBuilder.build();
    }
	
	public static void showAlertNotification(Context context, Notification notif, int notifId) {		
    	//Obtain a reference to the notification service
        NotificationManager notifManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        //Send alert
        notifManager.notify(notifId, notif);
	}
}
