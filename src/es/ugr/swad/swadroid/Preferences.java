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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import es.ugr.swad.swadroid.Base64;
import es.ugr.swad.swadroid.modules.Courses;
import es.ugr.swad.swadroid.modules.notifications.Notifications;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
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
     * Server.
     */
    private String server;
    /**
     * Stars length
     */
    private int STARS_LENGTH = 8;
    /**
     * User password preference name.
     */
    private static final String USERPASSWORDPREF = "userPasswordPref";
    /**
     * Last application version
     */
    private int lastVersion; 
    /**
     * Last course selected
     */
    private int lastCourseSelected = -1; 
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
     * User ID preference
     */
    private Preference userIDPref;
    /**
     * User password preference
     */
    private Preference userPasswordPref;
    /**
     * Current application version preference
     */
    private Preference currentVersionPref;
    /**
     * Rate preference
     */
    private Preference ratePref;
    /**
     * Twitter preference
     */
    private Preference twitterPref;
    /**
     * Facebook preference
     */
    private Preference facebookPref;
    /**
     * Google Plus preference
     */
    private Preference googlePlusPref;
    /**
     * Mailing list preference
     */
    private Preference mailingListPref;
    /**
     * Blog preference
     */
    private Preference blogPref;
    /**
     * Share preference
     */
    private Preference sharePref;
    /**
     * Server preference
     */
    private Preference serverPref;
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
     * Gets server URL.
     * @return Server URL.
     */
    public String getServer() {
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
		editor = prefs.edit();
		editor.putInt(LASTVERSIONPREF, lv);
		editor.commit();
	 }
	 
	/**
	 * Gets last course selected
	 * @return Last course selected
	 */
	public int getLastCourseSelected() {
		return lastCourseSelected;
	}

	/**
	 * Sets last course selected
	 * @param lastCourseSelected Last course selected
	 */
	public void setLastCourseSelected(int lcs) {
		lastCourseSelected = lcs;
		editor = prefs.edit();
		editor.putInt(LASTCOURSESELECTEDPREF, lcs);
		editor.commit();
	}

	/**
	 * Generates the stars sequence to be showed on password field
	 * @param size Length of the stars sequence
	 * @return
	 */
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
	 * Encrypts user password with SHA-512 and encodes it to Base64UrlSafe
	 * @param password Password to be encrypted
	 * @return Encrypted password
	 * @throws NoSuchAlgorithmException 
	 */
	private String encryptPassword(String password) throws NoSuchAlgorithmException {
		String p;
		MessageDigest md = MessageDigest.getInstance("SHA-512");
	    md.update(password.getBytes());
	    p = new String(Base64.encodeBytes(md.digest()));
	    p = p.replace('+','-').replace('/','_').replace('=', ' ').replaceAll("\\s+", "").trim();
	    
	    return p;
	}
	
	/**
	 * Upgrade password encryption
	 * @throws NoSuchAlgorithmException
	 */
	public void upgradeCredentials() throws NoSuchAlgorithmException {
		String stars = getStarsSequence(STARS_LENGTH);
		
		editor = prefs.edit();
		userPassword = prefs.getString(USERPASSWORDPREF, "");
        userPassword = encryptPassword(userPassword);
     	editor.putString(USERPASSWORDPREF, userPassword);
     	editor.commit();
		
        userIDPref.setSummary(prefs.getString(USERIDPREF, ""));
        userPasswordPref.setSummary(stars);
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
        server = prefs.getString(SERVERPREF, Global.getDefaultServer());
        lastVersion = prefs.getInt(LASTVERSIONPREF, 0);
        lastCourseSelected = prefs.getInt(LASTCOURSESELECTEDPREF, 0);
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
        server = prefs.getString(SERVERPREF, Global.getDefaultServer());
        lastVersion = prefs.getInt(LASTVERSIONPREF, 0);
        lastCourseSelected = prefs.getInt(LASTCOURSESELECTEDPREF, 0);
        editor = prefs.edit();

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
                //userPassword = encryptPassword(userPassword);
            	editor.putString(USERPASSWORDPREF, userPassword);
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
            	server = prefs.getString(SERVERPREF, Global.getDefaultServer());
            	editor.putString(SERVERPREF, server);
                return true;
            }
        });        
        serverPref.setSummary(prefs.getString(SERVERPREF, Global.getDefaultServer()));
        
        try {
			currentVersionPref.setSummary(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
    }
    
    /* (non-Javadoc)
	 * @see android.app.Activity#onPreferenceChange()
	 */
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		 	String key = preference.getKey();
		 	Notifications n = new Notifications();
		 	Courses c = new Courses();
		 
		 	//If preferences have changed, logout and save new preferences
	        if (USERIDPREF.equals(key) || USERPASSWORDPREF.equals(key)) {
	        	Global.setLogged(false);
	        	n.clearNotifications(this);
	        	c.clearCourses(this);
	        	Global.setPreferencesChanged();
            	editor.commit();
	        }
	        
	        if(USERPASSWORDPREF.equals(key))
	        {
        		String stars = getStarsSequence(STARS_LENGTH);
        		preference.setSummary(stars);	        	
	        } else {
	        	preference.setSummary((CharSequence) newValue);
	        }
	        
	        if (SERVERPREF.equals(key) || SERVERPREF.equals(key)) {
	        	Global.setLogged(false);
	        	n.clearNotifications(this);
	        	c.clearCourses(this);
	        	Global.setPreferencesChanged();
            	editor.commit();
	        }
	        
	        return true;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		String stars = getStarsSequence(STARS_LENGTH);
        userIDPref.setSummary(prefs.getString(USERIDPREF, ""));
        
        if(!prefs.getString(USERPASSWORDPREF, "").equals(""))
        	userPasswordPref.setSummary(stars);
        
        serverPref.setSummary(prefs.getString(SERVERPREF, Global.getDefaultServer()));
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
    protected void onPause() {
        super.onPause();
	    editor.putInt(LASTVERSIONPREF, lastVersion);
	    editor.putInt(LASTCOURSESELECTEDPREF, lastCourseSelected);
	    editor.commit();
    }
}
