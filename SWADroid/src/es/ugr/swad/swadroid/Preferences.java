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
package es.ugr.swad.swadroid;

import java.security.NoSuchAlgorithmException;

import es.ugr.swad.swadroid.utils.Crypto;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;

/**
 * Class for store the application preferences
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class Preferences {
    /**
     * Login tag name for Logcat
     */
    public static final String TAG = Constants.APP_TAG + " Preferences";
    /**
     * Preferences name
     */
    public static final String PREFS_NAME = "es.ugr.swad.swadroid_preferences";
    /**
     * Application preferences
     */
    private static SharedPreferences prefs = null;
    /**
     * Preferences editor
     */
    private static Editor editor;
    /**
     * User identifier preference name
     */
    public static final String USERIDPREF = "userIDPref";
    /**
     * User password preference name
     */
    public static final String USERPASSWORDPREF = "userPasswordPref";
    /**
     * Last application version preference name
     */
    public static final String LASTVERSIONPREF = "lastVersionPref";
    /**
     * Current application version preference name
     */
    public static final String CURRENTVERSIONPREF = "currentVersionPref";
    /**
     * Last course selected preference name
     */
    public static final String LASTCOURSESELECTEDPREF = "lastCourseSelectedPref";
    /**
     * Rate preference name
     */
    public static final String RATEPREF = "ratePref";
    /**
     * Twitter preference name
     */
    public static final String TWITTERPREF = "twitterPref";
    /**
     * Facebook preference name
     */
    public static final String FACEBOOKPREF = "facebookPref";
    /**
     * Google Plus preference name
     */
    public static final String GOOGLEPLUSPREF = "googlePlusPref";
    /**
     * Mailing list preference name
     */
    public static final String MAILINGLISTPREF = "mailingListPref";
    /**
     * Blog preference name
     */
    public static final String BLOGPREF = "blogPref";
    /**
     * Share preference name
     */
    public static final String SHAREPREF = "sharePref";
    /**
     * Server preference name
     */
    public static final String SERVERPREF = "serverPref";
    /**
     * Database passphrase preference name
     */
    public static final String DBKEYPREF = "DBKeyPref";
    /**
     * Synchronization time preference name
     */
    public static final String SYNCTIMEPREF = "prefSyncTime";
    /**
     * Synchronization enable preference name
     */
    public static final String SYNCENABLEPREF = "prefSyncEnable";
    /**
     * Notifications limit preference name
     */
    public static final String NOTIFLIMITPREF = "prefNotifLimit";
    /**
     * Last synchronization time preference name
     */
    public static final String LASTSYNCTIMEPREF = "lastSyncTimeLimit";
    /**
     * Notifications sound enable preference name
     */
    public static final String NOTIFSOUNDENABLEPREF = "prefNotifSoundEnable";
    /**
     * Notifications vibrate enable preference name
     */
    public static final String NOTIFVIBRATEENABLEPREF = "prefNotifVibrateEnable";
    /**
     * Notifications lights enable preference name
     */
    public static final String NOTIFLIGHTSENABLEPREF = "prefNotifLightsEnable";
    /**
     * Changelog preference name
     */
    public static final String CHANGELOGPREF = "changelogPref";
    /**
     * Authors preference name
     */
    public static final String AUTHORSPREF = "authorsPref";

    /**
     * Constructor
     */
    public Preferences(Context ctx) { 
    	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
    		// If Android API >= 11 (HONEYCOMB) enable access to SharedPreferences from all application processes 
    		prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_MULTI_PROCESS);
		} else {
			/* If Android API < 11 (HONEYCOMB) access is enabled by default
			 * MODE_MULTI_PROCESS is not defined
			 */
			prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		}

		editor = prefs.edit();
	}

	/**
     * Gets user identifier
     *
     * @return User identifier
     */
    public static String getUserID() {
        return prefs.getString(USERIDPREF, "");
    }

    /**
     * Sets user id
     */
    public static void setUserID(String userID) {
        editor = editor.putString(USERIDPREF, userID);
        editor.commit();
    }

    /**
     * Gets User password
     *
     * @return User password
     */
    public static String getUserPassword() {
        return prefs.getString(USERPASSWORDPREF, "");
    }

    /**
     * Sets user password
     */
    public static void setUserPassword(String userPassword) {
        editor = editor.putString(USERPASSWORDPREF, userPassword);
        editor.commit();
    }

    /**
     * Gets server URL
     *
     * @return Server URL
     */
    public static String getServer() {
    	String server = prefs.getString(SERVERPREF, Constants.DEFAULT_SERVER);
    	
    	if(server.equals("")) {
    		server = Constants.DEFAULT_SERVER;
    	}
    	
        return server;
    }

    /**
     * Sets server URL
     *
     * @param Server URL
     */
    public static void setServer(String server) {
    	editor = editor.putString(SERVERPREF, server);
        editor.commit();
    }

    /**
     * Gets last application version
     *
     * @return returns last application version
     */
    public static int getLastVersion() {
        return prefs.getInt(LASTVERSIONPREF, 0);
    }

    /**
     * Sets last application version
     */
    public static void setLastVersion(int lv) {
        editor = editor.putInt(LASTVERSIONPREF, lv);
        editor.commit();
    }

    /**
     * Gets last course selected
     *
     * @return Last course selected
     */
    public static int getLastCourseSelected() {
        return prefs.getInt(LASTCOURSESELECTEDPREF, 0);
    }

    /**
     * Sets last course selected
     */
    public static void setLastCourseSelected(int lcs) {
        editor = editor.putInt(LASTCOURSESELECTEDPREF, lcs);
        editor.commit();
    }

    /**
     * Gets the database passphrase
     *
     * @return The database passphrase
     */
    public static String getDBKey() {
        return prefs.getString(DBKEYPREF, "");
    }

    /**
     * Gets the max number of notifications to be stored
     *
     * @return The max number of notifications to be stored
     */
    public static int getNotifLimit() {
        return prefs.getInt(NOTIFLIMITPREF, 25);
    }

    /**
     * Sets the max number of notifications to be stored
     *
     * @param notifLimit The max number of notifications to be stored
     */
    public static void setNotifLimit(int notifLimit) {
        editor = editor.putInt(NOTIFLIMITPREF, notifLimit);
        editor.commit();
    }

    /**
     * Gets the synchronization time
     *
     * @return The synchronization time
     */
    public static String getSyncTime() {
        return prefs.getString(SYNCTIMEPREF, "0");
    }

    /**
     * Sets the synchronization time
     *
     * @param syncTime The synchronization time
     */
    public static void setSyncTime(String syncTime) {
        editor = editor.putString(SYNCTIMEPREF, syncTime);
        editor.commit();
    }

    /**
     * Gets the last synchronization time
     *
     * @return The last synchronization time
     */
    public static long getLastSyncTime() {
        return prefs.getLong(LASTSYNCTIMEPREF, 0);
    }

    /**
     * Sets the last synchronization time
     *
     * @param time The last synchronization time
     */
    public static void setLastSyncTime(long time) {
        editor = editor.putLong(LASTSYNCTIMEPREF, time);
        editor.commit();
    }

    /**
	 *Checks if automatic synchronization is enabled
     * 
	 * @return true if automatic synchronization is enabled
	 * 		   false otherwise
	 */
	public static boolean isSyncEnabled() {
		return prefs.getBoolean(SYNCENABLEPREF, true);
	}

	/**
     * Sets the sync enabled flag
     *
     * @param syncEnabled true if automatic synchronization is enabled
	 * 		              false otherwise
     */
    public static void setSyncEnabled(boolean syncEnabled) {
        editor = editor.putBoolean(SYNCENABLEPREF, syncEnabled);
        editor.commit();
    }

	/**
     * Sets the database passphrase
     *
     * @param key The database passphrase
     */
    public static void setDBKey(String key) {
        editor = editor.putString(DBKEYPREF, key);
        editor.commit();
    }

    /**
     * Checks if the sound is enabled for notification alerts
     * 
	 * @return true if the sound is enabled for notification alerts
	 * 		   false otherwise
	 */
	public static boolean isNotifSoundEnabled() {
		return prefs.getBoolean(NOTIFSOUNDENABLEPREF, true);
	}

	/**
	 * Enables or disables the sound for notification alerts
	 * 
	 * @param notifSoundEnabled true if the sound is enabled for notification alerts
	 * 		   				    false otherwise
	 */
	public static void setNotifSoundEnabled(boolean notifSoundEnabled) {
		editor = editor.putBoolean(NOTIFSOUNDENABLEPREF, notifSoundEnabled);
	    editor.commit();
	}

	/**
	 * Checks if the vibration is enabled for notification alerts
	 * 
	 * @return true if the vibration is enabled for notification alerts
	 * 		   false otherwise
	 */
	public static boolean isNotifVibrateEnabled() {
		return prefs.getBoolean(NOTIFVIBRATEENABLEPREF, true);
	}

	/**
	 * Enables or disables the vibration for notification alerts
	 * 
	 * @param notifVibrateEnabled the notifVibrateEnabled to set
	 */
	public static void setNotifVibrateEnabled(boolean notifVibrateEnabled) {
		editor = editor.putBoolean(NOTIFVIBRATEENABLEPREF, notifVibrateEnabled);
	    editor.commit();
	}

	/**
	 * Checks if the lights are enabled for notification alerts
	 * 
	 * @return true if the lights are enabled for notification alerts
	 * 		   false otherwise
	 */
	public static boolean isNotifLightsEnabled() {
		return prefs.getBoolean(NOTIFLIGHTSENABLEPREF, true);
	}

	/**
	 * Enables or disables the lights for notification alerts
	 * 
	 * @param notifLightsEnabled true if the lights are enabled for notification alerts
	 * 		   				     false otherwise
	 */
	public static void setNotifLightsEnabled(boolean notifLightsEnabled) {
		editor = editor.putBoolean(NOTIFLIGHTSENABLEPREF, notifLightsEnabled);
	    editor.commit();
	}

    /**
     * Upgrade password encryption
     *
     * @throws NoSuchAlgorithmException
     */
    public static void upgradeCredentials() throws NoSuchAlgorithmException {
        String userPassword = getUserPassword();
        setUserPassword(Crypto.encryptPassword(userPassword));
    }

}