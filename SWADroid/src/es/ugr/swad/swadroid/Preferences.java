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
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package es.ugr.swad.swadroid;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;

import com.bugsense.trace.BugSenseHandler;

import es.ugr.swad.swadroid.gui.DialogFactory;
import es.ugr.swad.swadroid.gui.widget.SeekBarDialogPreference;
import es.ugr.swad.swadroid.model.DataBaseHelper;
import es.ugr.swad.swadroid.sync.SyncUtils;
import es.ugr.swad.swadroid.utils.Crypto;
import es.ugr.swad.swadroid.utils.Utils;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Preferences window of application.
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class Preferences extends PreferenceActivity implements OnPreferenceChangeListener {
    /**
     * Login tag name for Logcat
     */
    public static final String TAG = Constants.APP_TAG + " Preferences";
    /**
     * Application context
     */
    public Context ctx;
    /**
     * Application preferences
     */
    private static SharedPreferences prefs = null;
    /**
     * User identifier.
     */
    private static String userID;
    /**
     * Database Helper.
     */
    private static DataBaseHelper dbHelper;
    /**
     * User identifier preference name.
     */
    private static final String USERIDPREF = "userIDPref";
    /**
     * User password.
     */
    private static String userPassword;
    /**
     * Server.
     */
    private static String server;
    /**
     * Stars length
     */
    private final int STARS_LENGTH = 8;
    /**
     * User password preference name.
     */
    private static final String USERPASSWORDPREF = "userPasswordPref";
    /**
     * Last application version
     */
    private static int lastVersion;
    /**
     * Last course selected
     */
    private static int lastCourseSelected = -1;
    /**
     * Database passphrase
     */
    private static String DBKey;
    /**
     * Synchronization time
     */
    private static String syncTime;
    /**
     * Synchronization enabled flag
     */
    private static boolean syncEnabled;
    /**
     * Last synchronization time
     */
    private static long lastSyncTime;
    /**
     * Notifications limit
     */
    private static int notifLimit;
    /**
     * Notifications sound enabled flag
     */
    private static boolean notifSoundEnabled;
    /**
     * Notifications vibration enabled flag
     */
    private static boolean notifVibrateEnabled;
    /**
     * Notifications lights enabled flag
     */
    private static boolean notifLightsEnabled;
    /**
     * Last application version preference name.
     */
    private static final String LASTVERSIONPREF = "lastVersionPref";
    /**
     * Current application version preference name.
     */
    private static final String CURRENTVERSIONPREF = "currentVersionPref";
    /**
     * Last course selected preference name.
     */
    private static final String LASTCOURSESELECTEDPREF = "lastCourseSelectedPref";
    /**
     * Rate preference name.
     */
    private static final String RATEPREF = "ratePref";
    /**
     * Twitter preference name.
     */
    private static final String TWITTERPREF = "twitterPref";
    /**
     * Facebook preference name.
     */
    private static final String FACEBOOKPREF = "facebookPref";
    /**
     * Google Plus preference name.
     */
    private static final String GOOGLEPLUSPREF = "googlePlusPref";
    /**
     * Mailing list preference name.
     */
    private static final String MAILINGLISTPREF = "mailingListPref";
    /**
     * Blog preference name.
     */
    private static final String BLOGPREF = "blogPref";
    /**
     * Share preference name.
     */
    private static final String SHAREPREF = "sharePref";
    /**
     * Server preference name.
     */
    private static final String SERVERPREF = "serverPref";
    /**
     * Database passphrase preference name.
     */
    private static final String DBKEYPREF = "DBKeyPref";
    /**
     * Synchronization time preference name.
     */
    private static final String SYNCTIMEPREF = "prefSyncTime";
    /**
     * Synchronization enable preference name.
     */
    private static final String SYNCENABLEPREF = "prefSyncEnable";
    /**
     * Notifications limit preference name.
     */
    private static final String NOTIFLIMITPREF = "prefNotifLimit";
    /**
     * Last synchronization time preference name.
     */
    private static final String LASTSYNCTIMEPREF = "lastSyncTimeLimit";
    /**
     * Notifications sound enable preference name.
     */
    private static final String NOTIFSOUNDENABLEPREF = "prefNotifSoundEnable";
    /**
     * Notifications vibrate enable preference name.
     */
    private static final String NOTIFVIBRATEENABLEPREF = "prefNotifVibrateEnable";
    /**
     * Notifications lights enable preference name.
     */
    private static final String NOTIFLIGHTSENABLEPREF = "prefNotifLightsEnable";
    /**
     * Changelog preference name.
     */
    private static final String CHANGELOGPREF = "changelogPref";
    /**
     * Authors preference name.
     */
    private static final String AUTHORSPREF = "authorsPref";
    /**
     * User ID preference
     */
    private static Preference userIDPref;
    /**
     * User password preference
     */
    private static Preference userPasswordPref;
    /**
     * Current application version preference
     */
    private static Preference currentVersionPref;
    /**
     * Rate preference
     */
    private static Preference ratePref;
    /**
     * Twitter preference
     */
    private static Preference twitterPref;
    /**
     * Facebook preference
     */
    private static Preference facebookPref;
    /**
     * Google Plus preference
     */
    private static Preference googlePlusPref;
    /**
     * Mailing list preference
     */
    private static Preference mailingListPref;
    /**
     * Blog preference
     */
    private static Preference blogPref;
    /**
     * Share preference
     */
    private static Preference sharePref;
    /**
     * Server preference
     */
    private static Preference serverPref;
    /**
     * Synchronization time preference
     */
    private static Preference syncTimePref;
    /**
     * Synchronization enable preference
     */
    private static CheckBoxPreference syncEnablePref;
    /**
     * Notifications limit preference
     */
    private static SeekBarDialogPreference notifLimitPref;
    /**
     * Notifications sound enable preference
     */
    private static CheckBoxPreference notifSoundEnablePref;
    /**
     * Notifications vibrate enable preference
     */
    private static CheckBoxPreference notifVibrateEnablePref;
    /**
     * Notifications lights enable preference
     */
    private static CheckBoxPreference notifLightsEnablePref;
    /**
     * Preferences editor
     */
    private static Editor editor;
    /**
     * Application debuggable flag
     */
    protected static boolean isDebuggable;

    /**
     * Gets user identifier.
     *
     * @return User identifier.
     */
    public String getUserID() {
        return userID;
    }

    /**
     * Sets user id
     */
    public void setUserID(String userID) {
    	Preferences.userID = userID;
        editor = editor.putString(USERIDPREF, userID);
        editor.commit();
    }

    /**
     * Gets User password.
     *
     * @return User password.
     */
    public String getUserPassword() {
        return userPassword;
    }

    /**
     * Sets user password
     */
    public void setUserPassword(String userPassword) {
    	Preferences.userPassword = userPassword;
        editor = editor.putString(USERPASSWORDPREF, userPassword);
        editor.commit();
    }

    /**
     * Gets server URL.
     *
     * @return Server URL.
     */
    public String getServer() {
        if (server.equals("")) {
            server = Constants.DEFAULT_SERVER;
        } else {
        	server = prefs.getString(SERVERPREF, Constants.DEFAULT_SERVER);
        }

        return server;
    }

    /**
     * Gets last application version
     *
     * @return returns last application version
     */
    public int getLastVersion() {
        return lastVersion;
    }

    /**
     * Sets last application version
     */
    public void setLastVersion(int lv) {
        lastVersion = lv;
        editor = editor.putInt(LASTVERSIONPREF, lv);
        editor.commit();
    }

    /**
     * Gets last course selected
     *
     * @return Last course selected
     */
    public int getLastCourseSelected() {
        return lastCourseSelected;
    }

    /**
     * Sets last course selected
     */
    public void setLastCourseSelected(int lcs) {
        lastCourseSelected = lcs;
        editor = editor.putInt(LASTCOURSESELECTEDPREF, lcs);
        editor.commit();
    }

    /**
     * Gets the database passphrase
     *
     * @return The database passphrase
     */
    public String getDBKey() {
        return DBKey;
    }

    /**
     * Gets the max number of notifications to be stored
     *
     * @return The max number of notifications to be stored
     */
    public int getNotifLimit() {
        return notifLimit;
    }

    /**
     * Gets the synchronization time
     *
     * @return The synchronization time
     */
    public String getSyncTime() {
        return syncTime;
    }

    /**
     * Sets the synchronization time
     *
     * @param defaultSyncTime The synchronization time
     */
    public void setSyncTime(String defaultSyncTime) {
    	syncTime = defaultSyncTime;
        editor = editor.putString(SYNCTIMEPREF, syncTime);
        editor.commit();
    }

    /**
     * Gets the last synchronization time
     *
     * @return The last synchronization time
     */
    public long getLastSyncTime() {
        return lastSyncTime;
    }

    /**
     * Sets the last synchronization time
     *
     * @param time The last synchronization time
     */
    public void setLastSyncTime(long time) {
    	lastSyncTime = time;
        editor = editor.putLong(LASTSYNCTIMEPREF, lastSyncTime);
        editor.commit();
    }

    /**
	 *Checks if automatic synchronization is enabled
     * 
	 * @return true if automatic synchronization is enabled
	 * 		   false otherwise
	 */
	public static boolean isSyncEnabled() {
		return syncEnabled;
	}

	/**
     * Sets the database passphrase
     *
     * @param key The database passphrase
     */
    public void setDBKey(String key) {
        DBKey = key;
        editor = editor.putString(DBKEYPREF, DBKey);
        editor.commit();
    }

    /**
     * Checks if the sound is enabled for notification alerts
     * 
	 * @return true if the sound is enabled for notification alerts
	 * 		   false otherwise
	 */
	public static boolean isNotifSoundEnabled() {
		return notifSoundEnabled;
	}

	/**
	 * Enables or disables the sound for notification alerts
	 * 
	 * @param notifSoundEnabled true if the sound is enabled for notification alerts
	 * 		   				    false otherwise
	 */
	public static void setNotifSoundEnabled(boolean notifSoundEnabled) {
		Preferences.notifSoundEnabled = notifSoundEnabled;
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
		return notifVibrateEnabled;
	}

	/**
	 * Enables or disables the vibration for notification alerts
	 * 
	 * @param notifVibrateEnabled the notifVibrateEnabled to set
	 */
	public static void setNotifVibrateEnabled(boolean notifVibrateEnabled) {
		Preferences.notifVibrateEnabled = notifVibrateEnabled;
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
		return notifLightsEnabled;
	}

	/**
	 * Enables or disables the lights for notification alerts
	 * 
	 * @param notifLightsEnabled true if the lights are enabled for notification alerts
	 * 		   				     false otherwise
	 */
	public static void setNotifLightsEnabled(boolean notifLightsEnabled) {
		Preferences.notifLightsEnabled = notifLightsEnabled;
		editor = editor.putBoolean(NOTIFLIGHTSENABLEPREF, notifLightsEnabled);
	    editor.commit();
	}

	/**
     * Generates the stars sequence to be showed on password field
     *
     * @param size Length of the stars sequence
     * @return
     */
    private String getStarsSequence(int size) {
        String stars = "";

        for (int i = 0; i < size; i++) {
            stars += "*";
        }

        return stars;
    }

    /**
     * Shows an error message.
     *
     * @param message Error message to show.
     */
    protected void error(String tag, String message, Exception ex, boolean sendException) {
    	DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };
        
    	AlertDialog errorDialog = DialogFactory.createErrorDialog(this, TAG, message, ex, sendException,
    			isDebuggable, onClickListener); 
    	
    	errorDialog.show();
    }

    /**
     * Upgrade password encryption
     *
     * @throws NoSuchAlgorithmException
     */
    public void upgradeCredentials() throws NoSuchAlgorithmException {
        userPassword = Crypto.encryptPassword(userPassword);
        editor = editor.putString(USERPASSWORDPREF, userPassword);
        editor.commit();
    }

    /**
     * Clean data of all tables from database. Removes users photos from external storage
     */
    private void cleanDatabase() {
        //dbHelper.emptyTable(Constants.DB_TABLE_NOTIFICATIONS);
        //dbHelper.emptyTable(Constants.DB_TABLE_COURSES);
    	dbHelper.cleanTables();
        
        setLastCourseSelected(0);
        Utils.setDbCleaned(true);
        
        Log.i(TAG, "Database has been cleaned");
    }
    
    private void logoutClean(String key) {
    	Constants.setLogged(false);
        Log.i(TAG, "Forced logout due to " + key + " change in preferences");
        
        cleanDatabase();
        Constants.setPreferencesChanged();
    }

    /**
     * Initializes preferences of activity.
     *
     * @param ctx Context of activity.
     */
    public void getPreferences(Context ctx) {
        // Get the preferences
    	this.ctx = ctx;
    	
    	if(prefs == null) {
    		prefs = PreferenceManager.getDefaultSharedPreferences(ctx);    	
    		editor = prefs.edit();
    	}
    	
        userID = prefs.getString(USERIDPREF, "");
        userPassword = prefs.getString(USERPASSWORDPREF, "");
        server = prefs.getString(SERVERPREF, Constants.DEFAULT_SERVER);
        lastVersion = prefs.getInt(LASTVERSIONPREF, 0);
        lastCourseSelected = prefs.getInt(LASTCOURSESELECTEDPREF, 0);
        syncEnabled = prefs.getBoolean(SYNCENABLEPREF, true);
        syncTime = prefs.getString(SYNCTIMEPREF, "0");
        lastSyncTime = prefs.getLong(LASTSYNCTIMEPREF, 0);
        notifLimit = prefs.getInt(NOTIFLIMITPREF, 25);
        DBKey = prefs.getString(DBKEYPREF, "");
        notifSoundEnabled = prefs.getBoolean(NOTIFSOUNDENABLEPREF, true);
        notifVibrateEnabled = prefs.getBoolean(NOTIFVIBRATEENABLEPREF, true);
        notifLightsEnabled = prefs.getBoolean(NOTIFLIGHTSENABLEPREF, true);
    }

    /**
     * Saves preferences of activity.
     *
     * @param ctx Context of activity.
     */
    public void writePreferences() {    	
    	if(prefs == null) {
    		prefs = PreferenceManager.getDefaultSharedPreferences(ctx);    	
    		editor = prefs.edit();
    	}
    	
        editor = editor.putString(USERIDPREF, userID);
        editor = editor.putString(USERPASSWORDPREF, userPassword);
        editor = editor.putString(SERVERPREF, server);
        editor = editor.putInt(LASTVERSIONPREF, lastVersion);
        editor = editor.putInt(LASTCOURSESELECTEDPREF, lastCourseSelected);
        editor = editor.putBoolean(SYNCENABLEPREF, syncEnabled);
        editor = editor.putString(SYNCTIMEPREF, syncTime);
        editor = editor.putLong(LASTSYNCTIMEPREF, lastSyncTime);
        editor = editor.putInt(NOTIFLIMITPREF, notifLimit);
        editor = editor.putString(DBKEYPREF, DBKey);
        editor = editor.putBoolean(NOTIFSOUNDENABLEPREF, notifSoundEnabled);
        editor = editor.putBoolean(NOTIFVIBRATEENABLEPREF, notifVibrateEnabled);
        editor = editor.putBoolean(NOTIFLIGHTSENABLEPREF, notifLightsEnabled);
        
        editor.commit();
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onCreate()
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Restore preferences
        addPreferencesFromResource(R.xml.preferences);
        ctx = getBaseContext();        
        getPreferences(ctx);

        //Initialize database
        try {    		
            dbHelper = new DataBaseHelper(this); 
            getPackageManager().getApplicationInfo(
                    getPackageName(), 0);
    		isDebuggable = (ApplicationInfo.FLAG_DEBUGGABLE != 0);
        } catch (Exception ex) {
        	error(TAG, ex.getMessage(), ex, true);
        }

        userIDPref = findPreference(USERIDPREF);
        userPasswordPref = findPreference(USERPASSWORDPREF);
        currentVersionPref = findPreference(CURRENTVERSIONPREF);
        ratePref = findPreference(RATEPREF);
        twitterPref = findPreference(TWITTERPREF);
        facebookPref = findPreference(FACEBOOKPREF);
        googlePlusPref = findPreference(GOOGLEPLUSPREF);
        mailingListPref = findPreference(MAILINGLISTPREF);
        blogPref = findPreference(BLOGPREF);
        sharePref = findPreference(SHAREPREF);
        serverPref = findPreference(SERVERPREF);
        syncTimePref = findPreference(SYNCTIMEPREF);
        syncEnablePref = (CheckBoxPreference) findPreference(SYNCENABLEPREF);
        notifLimitPref = (SeekBarDialogPreference) findPreference(NOTIFLIMITPREF);
        notifSoundEnablePref = (CheckBoxPreference) findPreference(NOTIFSOUNDENABLEPREF);
        notifVibrateEnablePref = (CheckBoxPreference) findPreference(NOTIFVIBRATEENABLEPREF);
        notifLightsEnablePref = (CheckBoxPreference) findPreference(NOTIFLIGHTSENABLEPREF);

        userIDPref.setOnPreferenceChangeListener(this);
        userPasswordPref.setOnPreferenceChangeListener(this);
        ratePref.setOnPreferenceChangeListener(this);
        twitterPref.setOnPreferenceChangeListener(this);
        facebookPref.setOnPreferenceChangeListener(this);
        googlePlusPref.setOnPreferenceChangeListener(this);
        mailingListPref.setOnPreferenceChangeListener(this);
        blogPref.setOnPreferenceChangeListener(this);
        sharePref.setOnPreferenceChangeListener(this);
        serverPref.setOnPreferenceChangeListener(this);
        notifLimitPref.setOnPreferenceChangeListener(this);
        syncEnablePref.setOnPreferenceChangeListener(this);
        syncTimePref.setOnPreferenceChangeListener(this);
        notifSoundEnablePref.setOnPreferenceChangeListener(this);
        notifVibrateEnablePref.setOnPreferenceChangeListener(this);
        notifLightsEnablePref.setOnPreferenceChangeListener(this);

        notifLimitPref.setProgress(notifLimit);

        userIDPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            /**
             * Called when a preference is selected.
             * @param preference Preference selected.
             */
            public boolean onPreferenceClick(Preference preference) {
                //userID = prefs.getString(USERIDPREF, "");
                return true;
            }
        });
        userPasswordPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            /**
             * Called when a preference is selected.
             * @param preference Preference selected.
             */
            public boolean onPreferenceClick(Preference preference) {
                /*try {
					userPassword = Crypto.encryptPassword(prefs.getString(USERPASSWORDPREF, ""));
				} catch (NoSuchAlgorithmException e) {
					error(TAG, e.getMessage(), e, true);
				}*/
                return true;
            }
        });
        ratePref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            /**
             * Called when a preference is selected.
             * @param preference Preference selected.
             */
            public boolean onPreferenceClick(Preference preference) {
                Intent urlIntent = new Intent(Intent.ACTION_VIEW);
                urlIntent.setData(Uri.parse(getString(R.string.marketURL)));
                startActivity(urlIntent);
                return true;
            }
        });
        twitterPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            /**
             * Called when a preference is selected.
             * @param preference Preference selected.
             */
            public boolean onPreferenceClick(Preference preference) {
                Intent urlIntent = new Intent(Intent.ACTION_VIEW);
                urlIntent.setData(Uri.parse(getString(R.string.twitterURL)));
                startActivity(urlIntent);
                return true;
            }
        });
        facebookPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            /**
             * Called when a preference is selected.
             * @param preference Preference selected.
             */
            public boolean onPreferenceClick(Preference preference) {
                Intent urlIntent = new Intent(Intent.ACTION_VIEW);
                urlIntent.setData(Uri.parse(getString(R.string.facebookURL)));
                startActivity(urlIntent);
                return true;
            }
        });
        googlePlusPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            /**
             * Called when a preference is selected.
             * @param preference Preference selected.
             */
            public boolean onPreferenceClick(Preference preference) {
                Intent urlIntent = new Intent(Intent.ACTION_VIEW);
                urlIntent.setData(Uri.parse(getString(R.string.googlePlusURL)));
                startActivity(urlIntent);
                return true;
            }
        });
        mailingListPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            /**
             * Called when a preference is selected.
             * @param preference Preference selected.
             */
            public boolean onPreferenceClick(Preference preference) {
                Intent urlIntent = new Intent(Intent.ACTION_VIEW);
                urlIntent.setData(Uri.parse(getString(R.string.mailingListURL)));
                startActivity(urlIntent);
                return true;
            }
        });
        blogPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            /**
             * Called when a preference is selected.
             * @param preference Preference selected.
             */
            public boolean onPreferenceClick(Preference preference) {
                Intent urlIntent = new Intent(Intent.ACTION_VIEW);
                urlIntent.setData(Uri.parse(getString(R.string.blogURL)));
                startActivity(urlIntent);
                return true;
            }
        });
        sharePref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            /**
             * Called when a preference is selected.
             * @param preference Preference selected.
             */
            public boolean onPreferenceClick(Preference preference) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.shareBodyMsg));
                startActivity(Intent.createChooser(sharingIntent, getString(R.string.shareTitle_menu)));
                return true;
            }
        });
        serverPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            /**
             * Called when a preference is selected.
             * @param preference Preference selected.
             */
            public boolean onPreferenceClick(Preference preference) {
                server = prefs.getString(SERVERPREF, Constants.DEFAULT_SERVER);
                return true;
            }
        });

        try {
            currentVersionPref.setSummary(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            BugSenseHandler.sendException(e);
        }
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onPreferenceChange()
     */
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String key = preference.getKey();
        
        if (USERIDPREF.equals(key)) {
        	userID = (String) newValue;
        	preference.setSummary((CharSequence) newValue);
        	
        	//Reset user password on userid change
        	setUserPassword("");
        	userPasswordPref.setSummary("");
        	Log.i(TAG, "Resetted user password due to userid change"); 
        	
        	//If preferences have changed, logout
        	logoutClean(key);
        } else if (USERPASSWORDPREF.equals(key)) {            
            try {
				userPassword = Crypto.encryptPassword((String) newValue);
	            preference.setSummary(getStarsSequence(STARS_LENGTH));
	            
	            //If preferences have changed, logout
	            Constants.setLogged(false);
	            Log.i(TAG, "Forced logout due to " + key + " change in preferences");
			} catch (NoSuchAlgorithmException ex) {
				error(TAG, ex.getMessage(), ex, true);
			}
        } else if (SERVERPREF.equals(key)) {
            server = (String) newValue; 
            
            //If preferences have changed, logout
        	logoutClean(key);
        } else if(SYNCENABLEPREF.equals(key)) {
        	//boolean masterSyncEnabled = ContentResolver.getMasterSyncAutomatically();
            syncEnabled = (Boolean) newValue;
            //Account account = new Account(getString(R.string.app_name), Constants.ACCOUNT_TYPE);

            //Configure automatic synchronization
            /*if(syncEnabled && !masterSyncEnabled) {
                ContentResolver.setMasterSyncAutomatically(syncEnabled);
			}

            ContentResolver.setSyncAutomatically(account, Constants.AUTHORITY, syncEnabled);*/
            if(syncEnabled) {
            	SyncUtils.addPeriodicSync(Constants.AUTHORITY, Bundle.EMPTY, Long.valueOf(syncTime), ctx);
            } else {
            	SyncUtils.removePeriodicSync(Constants.AUTHORITY, Bundle.EMPTY, ctx);
            }

            syncEnablePref.setChecked(syncEnabled);
        } else if(SYNCTIMEPREF.equals(key)) {            
        	setSyncTime((String) newValue);
        	
            List<String> prefSyncTimeValues = Arrays.asList(getResources().getStringArray(R.array.prefSyncTimeValues));
            List<String> prefSyncTimeEntries = Arrays.asList(getResources().getStringArray(R.array.prefSyncTimeEntries));
            int prefSyncTimeIndex = prefSyncTimeValues.indexOf(syncTime);
            String prefSyncTimeEntry = prefSyncTimeEntries.get(prefSyncTimeIndex);

            SyncUtils.removePeriodicSync(Constants.AUTHORITY, Bundle.EMPTY, ctx);

            if (!syncTime.equals("0") && syncEnabled) {
                SyncUtils.addPeriodicSync(Constants.AUTHORITY, Bundle.EMPTY, Long.parseLong(syncTime), ctx);
            }
            
            if(lastSyncTime == 0) {
            	syncEnablePref.setSummary(getString(R.string.lastSyncTimeLabel) + ": " 
            			+ getString(R.string.neverLabel));
            } else {
                java.text.DateFormat dateShortFormat = android.text.format.DateFormat.getDateFormat(this);
                java.text.DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(this);
            	Date lastSyncDate = new Date(lastSyncTime);
            	
            	syncEnablePref.setSummary(getString(R.string.lastSyncTimeLabel) + ": "
            			+ dateShortFormat.format(lastSyncDate) + " " 
            			+ timeFormat.format(lastSyncDate));
            }
            
            syncTimePref.setSummary(prefSyncTimeEntry);
        } else if(NOTIFLIMITPREF.equals(key)) {
        	 notifLimit = (Integer) newValue;
             dbHelper.clearOldNotifications(notifLimit);
        } else if(NOTIFSOUNDENABLEPREF.equals(key)) {
            notifSoundEnabled = (Boolean) newValue;
            notifSoundEnablePref.setChecked(notifSoundEnabled);
        } else if(NOTIFVIBRATEENABLEPREF.equals(key)) {
            notifVibrateEnabled = (Boolean) newValue;
            notifVibrateEnablePref.setChecked(notifVibrateEnabled);
        } else if(NOTIFLIGHTSENABLEPREF.equals(key)) {
            notifLightsEnabled = (Boolean) newValue;
            notifLightsEnablePref.setChecked(notifLightsEnabled);
        }
        
        //Refresh preferences screen
        //((BaseAdapter)getPreferenceScreen().getRootAdapter()).notifyDataSetChanged();
        //getListView().invalidate();
        
        return true;
    }
    
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
            final Preference preference) {

    	String key = preference.getKey();
    	
		if(key.equals(CHANGELOGPREF)) {
			 AlertDialog alertDialog = DialogFactory.createWebViewDialog(this,
		     		R.string.changelogTitle,
		     		R.raw.changes);
		
		     alertDialog.show();
		} else if(key.equals(AUTHORSPREF)) {
			 AlertDialog alertDialog = DialogFactory.createWebViewDialog(this,
			     		R.string.author_title_preferences,
			     		R.raw.authors);
			
			     alertDialog.show();
			}
		
		return true;
  }

    /* (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();
        java.text.DateFormat dateShortFormat = android.text.format.DateFormat.getDateFormat(this);
        java.text.DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(this);
    	Date lastSyncDate = new Date(lastSyncTime);
        List<String> prefSyncTimeValues = Arrays.asList(getResources().getStringArray(R.array.prefSyncTimeValues));
        List<String> prefSyncTimeEntries = Arrays.asList(getResources().getStringArray(R.array.prefSyncTimeEntries));
        int prefSyncTimeIndex = prefSyncTimeValues.indexOf(syncTime);
        String prefSyncTimeEntry = prefSyncTimeEntries.get(prefSyncTimeIndex);
        String stars = getStarsSequence(STARS_LENGTH);
        
        userIDPref.setSummary(userID);
        
        if (!userPassword.equals(""))
            userPasswordPref.setSummary(stars);

        if (!server.equals("")) {
            serverPref.setSummary(server);
        } else {
            serverPref.setSummary(Constants.DEFAULT_SERVER);
        }
        
        if(lastSyncTime == 0) {
        	syncEnablePref.setSummary(getString(R.string.lastSyncTimeLabel) + ": " 
        			+ getString(R.string.neverLabel));
        } else {        	
        	syncEnablePref.setSummary(getString(R.string.lastSyncTimeLabel) + ": "
        			+ dateShortFormat.format(lastSyncDate) + " " 
        			+ timeFormat.format(lastSyncDate));
        }
        
        syncTimePref.setSummary(prefSyncTimeEntry);
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onPause()
     */
    @Override
    protected void onPause() {
        super.onPause();
        writePreferences();
    }
}
