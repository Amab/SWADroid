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
package es.ugr.swad.swadroid.gui;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.SWADMain;
import es.ugr.swad.swadroid.database.AppDatabase;
import es.ugr.swad.swadroid.preferences.Preferences;
import es.ugr.swad.swadroid.preferences.PreferencesActivity;

/**
 * Superclass for add the options menu to all children classes of ExpandableListActivity
 *
 * @author Juan Miguel Boyero Corral <swadroid@gmail.com>
 * @author Antonio Aguilera Malagon <aguilerin@gmail.com>
 * @author Helena Rodriguez Gijon <hrgijon@gmail.com>
 */
public class MenuExpandableListActivity extends AppCompatActivity {
	/**
	 * Application preferences
	 */
    private Preferences prefs;
    /**
     * Database Helper.
     */
    protected AppDatabase db;
    /**
     * Application debuggable flag
     */
    private static boolean isDebuggable;
    /**
     * Class Module's tag name for Logcat
     */
    private static final String TAG = Constants.APP_TAG + " MenuExpandableListActivity";
    /**
     * Flag for indicate if this activity is SWADMain
     */
    private boolean isSWADMain;
    /**
     * Listener for dialog cancellation
     */
    private final OnClickListener cancelClickListener = (dialog, id) -> dialog.cancel();
    /**
     * Listener for clean database dialog
     */
    private final OnClickListener positiveClickListener = (dialog, id) -> {
        db.clearAllTables();
        AppDatabase.vacuumDb();
        Preferences.setLastCourseSelected(0);
        AppDatabase.setDbCleaned(true);
        Toast.makeText(getApplicationContext(), R.string.cleanDatabaseMsg, Toast.LENGTH_LONG).show();
        if (isSWADMain) {
            setMenuDbClean();
        }
        Log.i(Constants.APP_TAG, getString(R.string.cleanDatabaseMsg));
    };

    /**
     * Shows Preferences screen
     */
    private void viewPreferences() {
        Intent settingsActivity = new Intent(this, PreferencesActivity.class);
        startActivity(settingsActivity);
    }

    /**
     * Shares the application through the Android sharing options
     */
    private void shareApplication() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "SWADroid");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.shareBodyMsg));
        startActivity(Intent.createChooser(sharingIntent, getString(R.string.shareTitle_menu)));
        Log.d(Constants.APP_TAG, "shareApplication()");
    }

    /**
     * Rates the application in Android Market
     */
    private void rateApplication() {
        Intent rateIntent = new Intent(Intent.ACTION_VIEW);
        rateIntent.setData(Uri.parse(getString(R.string.marketURL)));
        startActivity(rateIntent);
        Log.d(Constants.APP_TAG, "rateApplication()");
    }

    /**
     * Deletes all data from database
     */
    private void cleanDatabase() {
    	AlertDialog cleanDBDialog = DialogFactory.createWarningDialog(this,
    			-1,
    			R.string.areYouSure,
    			R.string.cleanDatabaseDialogMsg,
    			R.string.yesMsg,
    			R.string.noMsg,
    			true,
    			positiveClickListener,
    			cancelClickListener,
    			null); 
    	
    	cleanDBDialog.show();
    }

    /**
     * Shows an error message.
     *
     * @param message Error message to show.
     */
    protected void error(String message, Exception ex) {
    	DialogInterface.OnClickListener onClickListener = (dialog, id) -> finish();
        
    	AlertDialog errorDialog = DialogFactory.createErrorDialog(this, TAG, message, ex,
    			isDebuggable, onClickListener);    	
    	
    	errorDialog.show();
    }

    /**
     * Shows a dialog.
     */
    public void showDialog(int title, int message) {
       AlertDialog dialog = DialogFactory.createNeutralDialog(this, -1, title, message, R.string.close_dialog, null);
       dialog.show();
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onCreateOptionsMenu()
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

	    if (isSWADMain) {
            menu.findItem(R.id.clean_database_menu).setVisible(true);
            menu.findItem(R.id.share_menu).setVisible(true);
            menu.findItem(R.id.rate_menu).setVisible(true);
            menu.findItem(R.id.preferences_menu).setVisible(true);
	    }

        return super.onCreateOptionsMenu(menu);
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onOptionsItemSelected()
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share_menu:
                shareApplication();
                break;
            case R.id.rate_menu:
                rateApplication();
                break;
            case R.id.clean_database_menu:
                cleanDatabase();
                break;
            case R.id.preferences_menu:
                viewPreferences();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onCreate()
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
        	//Initialize preferences
        	prefs = new Preferences(this);
        	
            //Initialize database
            db = AppDatabase.getAppDatabase(this);
            getPackageManager().getApplicationInfo(
                    getPackageName(), 0);
			isDebuggable = (ApplicationInfo.FLAG_DEBUGGABLE != 0);			
			isSWADMain = this instanceof SWADMain;
        } catch (Exception ex) {
            error(ex.getMessage(), ex);
        }
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onPause()
     */
    @Override
    protected void onPause() {
        AppDatabase.destroyInstance();
        super.onPause();
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();

        //Initialize database
        try {
            db = AppDatabase.getAppDatabase(this);
        } catch (Exception ex) {
            error(ex.getMessage(), ex);
        }
    }

    protected void setMenuDbClean() {
        // No-op
    }
}
