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
package es.ugr.swad.swadroid.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import es.ugr.swad.swadroid.Constants;

/**
 * Class for manage a periodic sync request
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
@SuppressLint("NewApi")
public class SyncUtils {

    public static void addPeriodicSync(String authority, Bundle extras, long frequency, Context context) {
        long pollFrequencyMsec = frequency * 60000;

        if (android.os.Build.VERSION.SDK_INT < 8) {
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            int type = AlarmManager.ELAPSED_REALTIME_WAKEUP;
            long triggerAtTime = SystemClock.elapsedRealtime() + pollFrequencyMsec;
            PendingIntent operation = PeriodicSyncReceiver.createPendingIntent(context, authority, extras);

            manager.setInexactRepeating(type, triggerAtTime, pollFrequencyMsec, operation);
        } else {
            AccountManager am = AccountManager.get(context);
            Account[] accounts = am.getAccountsByType(Constants.ACCOUNT_TYPE);

            for (Account a : accounts) {
                ContentResolver.addPeriodicSync(a, authority, extras, frequency * 60);
            }
        }
    }

    public static void removePeriodicSync(String authority, Bundle extras, Context context) {
        if (android.os.Build.VERSION.SDK_INT < 8) {
	        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	        PendingIntent operation = PeriodicSyncReceiver.createPendingIntent(context, authority, extras);
	        manager.cancel(operation);
        } else {
        	 AccountManager am = AccountManager.get(context);
             Account[] accounts = am.getAccountsByType(Constants.ACCOUNT_TYPE);

             for (Account a : accounts) {
                 ContentResolver.removePeriodicSync(a, authority, extras);
             }
        }
    }
}
