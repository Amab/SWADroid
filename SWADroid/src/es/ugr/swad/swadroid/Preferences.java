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

import es.ugr.swad.swadroid.modules.Notifications;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 * Preferences window of application.
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class Preferences extends PreferenceActivity implements OnPreferenceChangeListener {
	/**
	 * Application preferences
	 */
	private SharedPreferences prefs;
    /**
     * User identifier.
     */
    private String userID;
    /**
     * User identifier preference name.
     */
    private static final String USERIDPREF = "userIDPref";
    /**
     * User password.
     */
    private String userPassword;
    /**
     * User password preference name.
     */
    private static final String USERPASSWORDPREF = "userPasswordPref";

    /**
     * Gets user identifier.
     * @return User identifier.
     */
    public String getUserID() {
        return userID;
    }

    /**
     * Gets User password.
     * @return User password.
     */
    public String getUserPassword() {
        return userPassword;
    }

	/**
	 * Get if this is the first run
	 *
	 * @return returns true, if this is the first run
	 */
	 public boolean getFirstRun() {
	    return prefs.getBoolean("firstRun", true);
	 }
	 
	 /**
	 * Store the first run
	 */
	 public void setRunned() {
	    SharedPreferences.Editor edit = prefs.edit();
	    edit.putBoolean("firstRun", false);
	    edit.commit();
	 }
	
    /**
     * Initializes preferences of activity.
     * @param ctx Context of activity.
     */
    public void getPreferences(Context ctx) {
        // Get the xml/preferences.xml preferences
        prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        userID = prefs.getString(USERIDPREF, "");
        userPassword = prefs.getString(USERPASSWORDPREF, "");
    }

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate()
	 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //Restore preferences
        addPreferencesFromResource(R.xml.preferences);
        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        final Preference userIDPref = findPreference(USERIDPREF);
        final Preference userPasswordPref = findPreference(USERPASSWORDPREF);
        
        userIDPref.setOnPreferenceChangeListener(this);
        userPasswordPref.setOnPreferenceChangeListener(this);
        
        userIDPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

            /**
             * Called when a preference is selected.
             * @param preference Preference selected.
             */
            public boolean onPreferenceClick(Preference preference) {
                userID = prefs.getString(USERIDPREF, "");
                return true;
            }
        });
        userPasswordPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

            /**
             * Called when a preference is selected.
             * @param preference Preference selected.
             */
            public boolean onPreferenceClick(Preference preference) {
                userPassword = prefs.getString(USERPASSWORDPREF, "");
                return true;
            }
        });
    }

	public boolean onPreferenceChange(Preference preference, Object newValue) {
		 	String key = preference.getKey();
		 	Editor editor = prefs.edit();
		 	Notifications n = new Notifications();
		 
		 	//If preferences have changed, logout and save new preferences
	        if (USERIDPREF.equals(key) || USERPASSWORDPREF.equals(key)) {
	        	Global.setLogged(false);
	        	n.clearNotifications(this);	
	        	
	        	editor.putString(USERIDPREF, userID);
	        	editor.putString(USERPASSWORDPREF, userPassword);
	        	editor.commit();
	        }
	        
	        return true;
	}
}
