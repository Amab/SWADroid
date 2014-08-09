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
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.widget.Toast;

import com.bugsense.trace.BugSenseHandler;

import es.ugr.swad.swadroid.gui.DialogFactory;
import es.ugr.swad.swadroid.gui.widget.SeekBarDialogPreference;
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
public class PreferencesActivity extends PreferenceActivity implements OnPreferenceChangeListener {
    /**
     * Login tag name for Logcat
     */
    public static final String TAG = Constants.APP_TAG + " PreferencesActivity";
    /**
     * Application context
     */
    public Context ctx;
    /**
     * Stars length
     */
    private final int STARS_LENGTH = 8;
    /**
     * User ID preference
     */
    private static Preference userIDPref;
    /**
     * User password preference
     */
    private static Preference userPasswordPref;
    /**
     * Log out Preference
     */
    private static Preference logOutPref;
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
     * Application debuggable flag
     */
    protected static boolean isDebuggable;
    /**
     * User password
     */
    private String userPassword;

    /**
     * Synchronization preferences changed flag
     */
    private boolean syncPrefsChanged = false;

    /**
     * SWAD server to use
     */
    private String mServer;
    
    /**
     * User password preference changed flag
     */
    private boolean userPasswordPrefChanged = false;
    
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

    /* (non-Javadoc)
     * @see android.app.Activity#onCreate()
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //Restore preferences        
        getPreferenceManager().setSharedPreferencesName(Preferences.PREFS_NAME);
        addPreferencesFromResource(R.xml.preferences);
        
        ctx = getApplicationContext();         

        //Initialize database
        try {    		
            getPackageManager().getApplicationInfo(
                    getPackageName(), 0);
    		isDebuggable = (ApplicationInfo.FLAG_DEBUGGABLE != 0);
        } catch (Exception ex) {
        	error(TAG, ex.getMessage(), ex, true);
        }

        userIDPref = findPreference(Preferences.USERIDPREF);
        userPasswordPref = findPreference(Preferences.USERPASSWORDPREF);
        logOutPref = findPreference(Preferences.LOGOUTPREF);
        currentVersionPref = findPreference(Preferences.CURRENTVERSIONPREF);
        ratePref = findPreference(Preferences.RATEPREF);
        twitterPref = findPreference(Preferences.TWITTERPREF);
        facebookPref = findPreference(Preferences.FACEBOOKPREF);
        googlePlusPref = findPreference(Preferences.GOOGLEPLUSPREF);
        blogPref = findPreference(Preferences.BLOGPREF);
        sharePref = findPreference(Preferences.SHAREPREF);
        serverPref = findPreference(Preferences.SERVERPREF);
        syncTimePref = findPreference(Preferences.SYNCTIMEPREF);
        syncEnablePref = (CheckBoxPreference) findPreference(Preferences.SYNCENABLEPREF);
        notifLimitPref = (SeekBarDialogPreference) findPreference(Preferences.NOTIFLIMITPREF);
        notifSoundEnablePref = (CheckBoxPreference) findPreference(Preferences.NOTIFSOUNDENABLEPREF);
        notifVibrateEnablePref = (CheckBoxPreference) findPreference(Preferences.NOTIFVIBRATEENABLEPREF);
        notifLightsEnablePref = (CheckBoxPreference) findPreference(Preferences.NOTIFLIGHTSENABLEPREF);

        ratePref.setOnPreferenceChangeListener(this);
        twitterPref.setOnPreferenceChangeListener(this);
        facebookPref.setOnPreferenceChangeListener(this);
        googlePlusPref.setOnPreferenceChangeListener(this);
        blogPref.setOnPreferenceChangeListener(this);
        sharePref.setOnPreferenceChangeListener(this);
        serverPref.setOnPreferenceChangeListener(this);
        notifLimitPref.setOnPreferenceChangeListener(this);
        syncEnablePref.setOnPreferenceChangeListener(this);
        syncTimePref.setOnPreferenceChangeListener(this);
        notifSoundEnablePref.setOnPreferenceChangeListener(this);
        notifVibrateEnablePref.setOnPreferenceChangeListener(this);
        notifLightsEnablePref.setOnPreferenceChangeListener(this);
        
        notifLimitPref.setProgress(Preferences.getNotifLimit());
        
        logOutPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Preferences.logoutClean(ctx, Preferences.LOGOUTPREF);
                Preferences.setUserID("");
                Preferences.setUserPassword("");
                Constants.setLogged(false);
                
                startActivity(new Intent(getBaseContext(), LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP).putExtra("fromPreference", true));

                finish();
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

        mServer = Preferences.getServer();
        
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
        // By default we store the value in the preferences
        boolean returnValue = true;
        
        String key = preference.getKey();
        
        if (Preferences.USERIDPREF.equals(key)) {
        	Preferences.setUserID((String) newValue);
        	preference.setSummary((CharSequence) newValue);
        	
        	//Reset user password on userid change
        	Preferences.setUserPassword("");
        	userPasswordPref.setSummary("");
        	Log.i(TAG, "Resetted user password due to userid change"); 
        	
        	//If preferences have changed, logout
        	Preferences.logoutClean(ctx, key);
        	syncPrefsChanged = true;
        } else if (Preferences.USERPASSWORDPREF.equals(key)) {
            try {
                String password = (String) newValue;

                // Try to guest if user is using PRADO password
                if ((password.length() >= 8) && !Utils.isLong(password)) {
                    userPassword = Crypto.encryptPassword(password);
                    // Restore default style if it was changed due to an error in the input
                    userPasswordPref.setLayoutResource(R.layout.preference_child);
                    preference.setSummary(Utils.getStarsSequence(STARS_LENGTH));
                    
                    // If preferences have changed, logout
                    Log.i(TAG, "Forced logout due to " + key + " change in preferences");
                    userPasswordPrefChanged = true;
                    syncPrefsChanged = true;
                    Preferences.setPreferencesChanged();
                    Constants.setLogged(false);
                } else {
                    Toast.makeText(getApplicationContext(), R.string.pradoLoginToast,
                            Toast.LENGTH_LONG).show();
                    // Do not save the password to the preferences.
                    returnValue = false; 
                }
                
			} catch (NoSuchAlgorithmException ex) {
				error(TAG, ex.getMessage(), ex, true);
			}
        } else if (Preferences.SERVERPREF.equals(key)) {
            mServer = newValue.toString();
            Preferences.setServer(mServer);
            serverPref.setSummary(mServer);
            //If preferences have changed, logout
        	Preferences.logoutClean(ctx, key);
        	syncPrefsChanged = true;
        } else if(Preferences.SYNCENABLEPREF.equals(key)) {
        	boolean syncEnabled = (Boolean) newValue;
            Preferences.setSyncEnabled(syncEnabled);
            syncEnablePref.setChecked(syncEnabled);
            syncPrefsChanged = true;
        } else if(Preferences.SYNCTIMEPREF.equals(key)) { 
        	String syncTime = (String) newValue;
        	long lastSyncTime = Preferences.getLastSyncTime();
        	
        	Preferences.setSyncTime(syncTime);
        	
            List<String> prefSyncTimeValues = Arrays.asList(getResources().getStringArray(R.array.prefSyncTimeValues));
            List<String> prefSyncTimeEntries = Arrays.asList(getResources().getStringArray(R.array.prefSyncTimeEntries));
            int prefSyncTimeIndex = prefSyncTimeValues.indexOf(syncTime);
            String prefSyncTimeEntry = prefSyncTimeEntries.get(prefSyncTimeIndex);
            
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
            syncPrefsChanged = true;
        } else if(Preferences.NOTIFLIMITPREF.equals(key)) {
        	 int notifLimit = (Integer) newValue;
        	 Preferences.setNotifLimit(notifLimit);
        	 Preferences.clearOldNotifications(notifLimit);
        } else if(Preferences.NOTIFSOUNDENABLEPREF.equals(key)) {
            boolean notifSoundEnabled = (Boolean) newValue;
            Preferences.setNotifSoundEnabled(notifSoundEnabled);
            notifSoundEnablePref.setChecked(notifSoundEnabled);
        } else if(Preferences.NOTIFVIBRATEENABLEPREF.equals(key)) {
        	boolean notifVibrateEnabled = (Boolean) newValue;
        	Preferences.setNotifVibrateEnabled(notifVibrateEnabled);
            notifVibrateEnablePref.setChecked(notifVibrateEnabled);
        } else if(Preferences.NOTIFLIGHTSENABLEPREF.equals(key)) {
        	boolean notifLightsEnabled = (Boolean) newValue;
        	Preferences.setNotifLightsEnabled(notifLightsEnabled);
            notifLightsEnablePref.setChecked(notifLightsEnabled);
        }
        
        return returnValue;
    }
    
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
            final Preference preference) {

    	String key = preference.getKey();
    	
		if(key.equals(Preferences.CHANGELOGPREF)) {
			 AlertDialog alertDialog = DialogFactory.createWebViewDialog(this,
		     		R.string.changelogTitle,
		     		R.raw.changes);
		
		     alertDialog.show();
		} else if(key.equals(Preferences.AUTHORSPREF)) {
			 AlertDialog alertDialog = DialogFactory.createWebViewDialog(this,
			     		R.string.author_title_preferences,
			     		R.raw.authors);
			
			     alertDialog.show();
			}
		
		return true;
  }

    /* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
		
		//Set final password
		if(userPasswordPrefChanged) {
	    	Preferences.setUserPassword(userPassword);
		}
			
        //Reconfigure automatic synchronization
        if(syncPrefsChanged) {
	        SyncUtils.removePeriodicSync(Constants.AUTHORITY, Bundle.EMPTY, ctx);
	        if (!Preferences.getSyncTime().equals("0") && Preferences.isSyncEnabled()) {
	            SyncUtils.addPeriodicSync(Constants.AUTHORITY, Bundle.EMPTY,
	            		Long.parseLong(Preferences.getSyncTime()), ctx);
	        }
        }
	}

	/* (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();
        java.text.DateFormat dateShortFormat = android.text.format.DateFormat.getDateFormat(this);
        java.text.DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(this);
        long lastSyncTime = Preferences.getLastSyncTime();
    	Date lastSyncDate = new Date(lastSyncTime);
        List<String> prefSyncTimeValues = Arrays.asList(getResources().getStringArray(R.array.prefSyncTimeValues));
        List<String> prefSyncTimeEntries = Arrays.asList(getResources().getStringArray(R.array.prefSyncTimeEntries));
        int prefSyncTimeIndex = prefSyncTimeValues.indexOf(Preferences.getSyncTime());
        String prefSyncTimeEntry = prefSyncTimeEntries.get(prefSyncTimeIndex);
        String stars = Utils.getStarsSequence(STARS_LENGTH);
        mServer = Preferences.getServer();
        
        /*userIDPref.setSummary(Preferences.getUserID());
        
        if (!Preferences.getUserPassword().equals("") && !mIncorrectPassword)
            userPasswordPref.setSummary(stars);
        
        if (mIncorrectPassword)
        	highlightPasswordSummary();
        */
        
        if (!mServer.equals("")) {
            serverPref.setSummary(mServer);
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
}
