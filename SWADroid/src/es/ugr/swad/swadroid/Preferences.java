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
     * Last application version
     */
    private int lastVersion; 
    /**
     * Last application version preference name.
     */
    private static final String LASTVERSIONPREF = "lastVersionPref";
    /**
     * User ID preference
     */
    private Preference userIDPref;
    /**
     * User password preference
     */
    private Preference userPasswordPref;
    /**
     * Preferences editor
     */
 	Editor editor;

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
	 * Gets last application version
	 *
	 * @return returns true, if this is the first run
	 */
	 public int getLastVersion() {
		return lastVersion;
	 }
	 
	 /**
	 * Sets last application version
	 */
	 public void setLastVersion(int lv) {
		lastVersion = lv;
		editor = prefs.edit();
		editor.putInt(LASTVERSIONPREF, lv);
		editor.commit();
	 }
	 
	 private String getStarsSequence(int size)
	 {
		 String stars = "";
		 
		 for(int i=0; i<size; i++)
		 {
			 stars += "*";
		 }
		 
		 return stars;
	 }
	
    /**
     * Initializes preferences of activity.
     * @param ctx Context of activity.
     */
    public void getPreferences(Context ctx) {
        // Get the preferences
        prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        userID = prefs.getString(USERIDPREF, "");
        userPassword = prefs.getString(USERPASSWORDPREF, "");
        lastVersion = prefs.getInt(LASTVERSIONPREF, 0);
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

        userID = prefs.getString(USERIDPREF, "");
        userPassword = prefs.getString(USERPASSWORDPREF, "");
        lastVersion = prefs.getInt(LASTVERSIONPREF, 0);
        editor = prefs.edit();
        
        userIDPref = findPreference(USERIDPREF);
        userPasswordPref = findPreference(USERPASSWORDPREF);
        
        userIDPref.setOnPreferenceChangeListener(this);
        userPasswordPref.setOnPreferenceChangeListener(this);
        
        userIDPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

            /**
             * Called when a preference is selected.
             * @param preference Preference selected.
             */
            public boolean onPreferenceClick(Preference preference) {
                userID = prefs.getString(USERIDPREF, "");
            	editor.putString(USERIDPREF, userID);
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
            	editor.putString(USERPASSWORDPREF, userPassword);
                return true;
            }
        });
    }

	public boolean onPreferenceChange(Preference preference, Object newValue) {
		 	String key = preference.getKey();
		 	Notifications n = new Notifications();
		 
		 	//If preferences have changed, logout and save new preferences
	        if (USERIDPREF.equals(key) || USERPASSWORDPREF.equals(key)) {
	        	Global.setLogged(false);
	        	n.clearNotifications(this);
            	editor.commit();
	        }
	        
	        if(USERPASSWORDPREF.equals(key))
	        {
        		String stars = getStarsSequence(((String) newValue).length());
        		preference.setSummary(stars);	        	
	        } else {
	        	preference.setSummary((CharSequence) newValue);
	        }
	        
	        return true;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		String stars = getStarsSequence(prefs.getString(USERPASSWORDPREF, "").length());
        userIDPref.setSummary(prefs.getString(USERIDPREF, ""));
        userPasswordPref.setSummary(stars);
	}
	
	@Override
    protected void onPause() {
        super.onPause();
	    editor.putInt(LASTVERSIONPREF, lastVersion);
	    editor.commit();
    }
}
