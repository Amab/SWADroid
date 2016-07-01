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
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import es.ugr.swad.swadroid.Constants;

/**
 * Class for manage a periodic sync request
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class SyncUtils {
    /**
     * Login tag name for Logcat
     */
    private static final String TAG = Constants.APP_TAG + " SyncUtils";

    public static void addPeriodicSync(String authority, Bundle extras, long frequency, Context context) {
        long pollFrequencyMsec = frequency * 60000;

        if (android.os.Build.VERSION.SDK_INT < 8) {
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            int type = AlarmManager.ELAPSED_REALTIME_WAKEUP;
            long triggerAtTime = SystemClock.elapsedRealtime() + pollFrequencyMsec;
            PendingIntent operation = PeriodicSyncReceiver.createPendingIntent(context, authority, extras);

            manager.setInexactRepeating(type, triggerAtTime, pollFrequencyMsec, operation);
            
            Log.i(TAG, "Added periodic alarm with pollFrequency=" + pollFrequencyMsec);
        } else {
            AccountManager am = AccountManager.get(context);
            Account[] accounts = am.getAccountsByType(Constants.ACCOUNT_TYPE);

            Log.d(TAG, "[addPeriodicSync] Number of accounts with type " + Constants.ACCOUNT_TYPE + " = " + accounts.length);
            for (Account a : accounts) {
            	ContentResolver.setSyncAutomatically(a, Constants.AUTHORITY, true);
                ContentResolver.addPeriodicSync(a, authority, extras, frequency * 60);
                
                Log.i(TAG, "Added periodic synchronization with pollFrequency=" + (frequency * 60)
                		+ " for account " + a.toString());
            }
        }
    }

    public static void removePeriodicSync(String authority, Bundle extras, Context context) {
        if (android.os.Build.VERSION.SDK_INT < 8) {
	        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	        PendingIntent operation = PeriodicSyncReceiver.createPendingIntent(context, authority, extras);
	        manager.cancel(operation);
	        
            Log.i(TAG, "Removed periodic alarm");
        } else {
        	 AccountManager am = AccountManager.get(context);
             Account[] accounts = am.getAccountsByType(Constants.ACCOUNT_TYPE);

             Log.d(TAG, "[removePeriodicSync] Number of accounts with type " + Constants.ACCOUNT_TYPE + " = " + accounts.length);
             for (Account a : accounts) {
            	 ContentResolver.setSyncAutomatically(a, Constants.AUTHORITY, false);
                 ContentResolver.removePeriodicSync(a, authority, extras);
                 
                 Log.i(TAG, "Removed periodic synchronization for account " + a.toString());
             }
        }
    }
    
    public static boolean isSyncAutomatically(Context context) {
		boolean isSyncAutomatically = true;
		
    	if (android.os.Build.VERSION.SDK_INT >= 8) {
	    	AccountManager am = AccountManager.get(context);
	        Account[] accounts = am.getAccountsByType(Constants.ACCOUNT_TYPE);
	
	        Log.d(TAG, "[isSyncAutomatically] Number of accounts with type " + Constants.ACCOUNT_TYPE + " = " + accounts.length);
	        for (Account a : accounts) {
                if (!ContentResolver.getMasterSyncAutomatically()
                        || !ContentResolver.getSyncAutomatically(a, Constants.AUTHORITY)) {
                    isSyncAutomatically = false;
                }
	        }
    	} else {
    		isSyncAutomatically = false;
    		Log.e(TAG, "Operation isSyncAutomatically is not supported by build version " + android.os.Build.VERSION.SDK_INT);
    	}
    	
    	return isSyncAutomatically;
    }
    
    public static boolean isPeriodicSynced(Context context) {
    	boolean isPeriodicSynced = false;
    	
    	if (android.os.Build.VERSION.SDK_INT >= 8) {
        	 AccountManager am = AccountManager.get(context);
             Account[] accounts = am.getAccountsByType(Constants.ACCOUNT_TYPE);
             
             isPeriodicSynced = (accounts.length > 0);

             Log.d(TAG, "[isPeriodicSynced] Number of accounts with type " + Constants.ACCOUNT_TYPE + " = " + accounts.length);
        }
    	
    	return isPeriodicSynced;
    }
}
