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

package es.ugr.swad.swadroid.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import es.ugr.swad.swadroid.Preferences;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.modules.notifications.Notifications;

/**
 * Class for create notification alerts.
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class AlertNotification {
	public static void alertNotif(Context context, int notifAlertId, String contentTitle, String contentText,
			String ticker) {
		
    	//Obtain a reference to the notification service
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager notifManager =
                (NotificationManager) context.getSystemService(ns);

        //Configure the alert
        int defaults = 0;

    	NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(context)
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.ic_launcher_swadroid)
            .setContentTitle(contentTitle)
            .setContentText(contentText)
            .setTicker(ticker);
            //.setLights(Color.GREEN, 500, 500);
        
        //Add sound, vibration and lights
        if(Preferences.isNotifSoundEnabled()) {
        	defaults |= Notification.DEFAULT_SOUND;
        } else {
        	notifBuilder.setSound(null);
        }
        
        if(Preferences.isNotifVibrateEnabled()) {
        	defaults |= Notification.DEFAULT_VIBRATE;
        }
        
        if(Preferences.isNotifLightsEnabled()) {
        	defaults |= Notification.DEFAULT_LIGHTS;
        }
            
    	notifBuilder.setDefaults(defaults);

        //Configure the Intent
        Intent notIntent = new Intent(context,
                Notifications.class);

        PendingIntent notifPendingIntent = PendingIntent.getActivity(
                context, 0, notIntent, 0);
        
        notifBuilder.setContentIntent(notifPendingIntent);
       
        //Create alert
        Notification notif = notifBuilder.build();

        //Send alert
        notifManager.notify(notifAlertId, notif);
    }
}
