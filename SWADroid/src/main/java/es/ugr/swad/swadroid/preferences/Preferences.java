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
package es.ugr.swad.swadroid.preferences;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;

import java.security.NoSuchAlgorithmException;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.database.AppDatabase;
import es.ugr.swad.swadroid.model.LoginInfo;
import es.ugr.swad.swadroid.modules.courses.Courses;
import es.ugr.swad.swadroid.modules.login.Login;
import es.ugr.swad.swadroid.sync.SyncUtils;
import es.ugr.swad.swadroid.utils.CryptoUtils;

/**
 * Class for store the application preferences
 *
 * @author Juan Miguel Boyero Corral <swadroid@gmail.com>
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
     * Telegram preference name
     */
    public static final String TELEGRAMPREF = "telegramPref";
    /**
     * Blog preference name
     */
    public static final String BLOGPREF = "blogPref";
    /**
     * Share preference name
     */
    public static final String SHAREPREF = "sharePref";
    /**
     * Privacy policy preference name
     */
    public static final String PRIVACYPOLICYPREF = "privacyPolicyPref";
    /**
     * Server preference name
     */
    public static final String SERVERPREF = "serverPref";
    /**
     * Logout prefecence name
     */
    public static final String LOGOUTPREF = "logOutPref";
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
     * Last synchronization time preference name
     */
    public static final String LASTSYNCTIMEPREF = "lastSyncTimeLimit";
    /**
     * Changelog preference name
     */
    public static final String CHANGELOGPREF = "changelogPref";
    /**
     * Authors preference name
     */
    public static final String AUTHORSPREF = "authorsPref";
    /**
     * Authors preference name
     */
    public static final String LOGININFOPREF = "loginInfoPref";
    /**
     * Database Helper.
     */
    private static AppDatabase db;
    /**
     * Indicates if there are changes on preferences
     */
    private static boolean preferencesChanged = false;
    /**
     * Synchronization time preference for location
     */
    public static final String SYNCLOCATIONTIMEPREF = "prefSyncLocationTime";
    /*
     * Sharing location preference
     */
    public static final String SHARELOCATION = "prefShareLocation";
    /**
     * Gets application preferences
     * @param ctx Application context
     */
    private static void getPreferences(Context ctx) {
    		/*
    		 *  If Android API >= 11 (HONEYCOMB) enable access to SharedPreferences from all processes
    		 *  of the application
    		 */
            prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_MULTI_PROCESS);
            Log.i(TAG, "Android API >= 11 (HONEYCOMB). Enabling MODE_MULTI_PROCESS explicitly");
    }
    
    /**
     * Constructor
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public Preferences(Context ctx) {
        getPreferences(ctx);

    	editor = prefs.edit();

        try {
            db = AppDatabase.getAppDatabase(ctx);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
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
        return prefs.getString(SERVERPREF, null);
    }

    /**
     * Sets server URL
     *
     * @param server Server URL
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
     * Gets the login data
     *
     * @return The login data
     */
    public static LoginInfo getLoginInfo() {
        Gson gson = new Gson();
        String json = prefs.getString(LOGININFOPREF, null);
        return gson.fromJson(json, LoginInfo.class);
    }

    /**
     * Sets the login data
     *
     * @param loginInfo The login data
     */
    public static void setLoginInfo(LoginInfo loginInfo) {
        Gson gson = new Gson();
        String json = gson.toJson(loginInfo);
        editor = editor.putString(LOGININFOPREF, json);
        editor.commit();
    }

    /**
     * Removes the login data
     */
    public static void removeLoginInfo() {
        editor = editor.remove(LOGININFOPREF);
        editor.commit();
    }

    /**
     * Upgrade password encryption
     *
     * @throws NoSuchAlgorithmException
     */
    public static void upgradeCredentials() throws NoSuchAlgorithmException {
        String userPassword = getUserPassword();
        setUserPassword(CryptoUtils.encryptPassword(userPassword));
    }
    
    /**
     * Clean data of all tables from database. Removes users photos from external storage
     */
    private static void cleanDatabase() {
        db.clearAllTables();
        
        Preferences.setLastCourseSelected(0);
        AppDatabase.setDbCleaned(true);
        
        Log.i(TAG, "Database has been cleaned");
    }
    
    public static void logoutClean(Context context, String key) {
        Login.getLoginInfo().setLogged(false);
        removeLoginInfo();
        initializeSelectedCourse();
        
        cleanDatabase();
        setPreferencesChanged();
        
        if(isSyncEnabled()) {
        	SyncUtils.removePeriodicSync(Constants.AUTHORITY, Bundle.EMPTY, context);
        }

        Log.i(TAG, "Forced logout due to " + key + " change in preferences");
    }

    public static void initializeSelectedCourse() {
        Courses.setSelectedCourseCode(-1);
        Courses.setSelectedCourseShortName("");
        Courses.setSelectedCourseFullName("");

        Login.setCurrentUserRole(-1);

        Preferences.setLastCourseSelected(-1);

        Log.i(TAG, "Initialized selected course to -1");
    }
    
    public static boolean isPreferencesChanged() {
        return preferencesChanged;
    }

    /**
     * Set the fact that the preferences has changed
     */
    public static void setPreferencesChanged() {
        preferencesChanged = true;
    }

    /**
     * Indicates if the preferences has changed
     *
     * @param newState - true when the preferences has changed  and it was not handled it
     *                 - false if the preferences has not changed
     */
    public static void setPreferencesChanged(boolean newState) {
        preferencesChanged = newState;
    }

    /**
     * Gets the synchronization time
     *
     * @return The synchronization time
     */
    public static String getSyncLocationTime() {
        return prefs.getString(SYNCLOCATIONTIMEPREF, "1");
    }

    /**
     * Sets the synchronization time for location
     *
     * @param syncLocationTime The synchronization time for location
     */
    public static void setSyncLocationTime(String syncLocationTime) {
        editor = editor.putString(SYNCLOCATIONTIMEPREF, syncLocationTime);
        editor.commit();
    }

    /**
     * Gets sharing location preference
     *
     * @return share location preference
     */
    public static boolean getShareLocation() {
        return prefs.getBoolean(SHARELOCATION, false);
    }

    /**
     * Sets the SHARELOCATION preference
     *
     * @param shareLocation The preference for sharing location
     */
    public static void setShareLocation(boolean shareLocation) {
        editor = editor.putBoolean(SHARELOCATION, shareLocation);
        editor.commit();
    }

}
