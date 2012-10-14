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

import android.app.AlertDialog;
import android.app.ExpandableListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.dataframework.DataFramework;

import es.ugr.swad.swadroid.model.DataBaseHelper;

/**
 * Superclass for add the options menu to all children classes of ExpandableListActivity
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 * @author Antonio Aguilera Malagon <aguilerin@gmail.com>
 * @author Helena Rodriguez Gijon <hrgijon@gmail.com>
 */
public class MenuExpandableListActivity extends ExpandableListActivity {
	/**
	 * Application preferences.
	 */
	protected static Preferences prefs = new Preferences();
	/**
	 * Database Helper.
	 */
	protected static DataBaseHelper dbHelper;   
	/**
	 * Database Framework.
	 */
	protected static DataFramework db;  

	/* (non-Javadoc)
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		super.onStart();
		prefs.getPreferences(getBaseContext());
	}

	/**
	 * Shows Preferences screen
	 */
	protected void viewPreferences() {
		Intent settingsActivity = new Intent(getBaseContext(),
				Preferences.class);
		startActivity(settingsActivity);
	}

	/**
	 * Shares the application through the Android sharing options
	 */
	protected void shareApplication() {
		Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
		sharingIntent.setType("text/plain");
		sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "SWADroid");
		sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.shareBodyMsg));
		startActivity(Intent.createChooser(sharingIntent, getString(R.string.shareTitle_menu)));		
		Log.d(Global.APP_TAG, "shareApplication()");
	}

	/**
	 * Rates the application in Android Market
	 */
	protected void rateApplication() {
		Intent rateIntent = new Intent(Intent.ACTION_VIEW);
		rateIntent.setData(Uri.parse(getString(R.string.marketURL)));
		startActivity(rateIntent);
		Log.d(Global.APP_TAG, "rateApplication()");
	}

	/**
	 * Deletes notifications and tests data from database
	 */
	protected void cleanDatabase() {
		dbHelper.cleanTables();
		prefs.setLastCourseSelected(0);
		prefs.setRollcallCourseSelected(-1);
		Global.setSelectedRollcallCourseCode(-1);
		Global.setDbCleaned(true);
		Toast.makeText(this, R.string.cleanDatabaseMsg, Toast.LENGTH_LONG).show();
		if(this instanceof SWADMain){
			setMenuDbClean();
		}
		Log.i(Global.APP_TAG, getString(R.string.cleanDatabaseMsg));
	}

	/**
	 * Shows an error message.
	 * @param message Error message to show.
	 */
	protected void error(String message) {
		new AlertDialog.Builder(this).setTitle(R.string.title_error_dialog)
		.setMessage(message)
		.setNeutralButton(R.string.close_dialog, null)
		.setIcon(R.drawable.erroricon).show();
	}

	/**
	 * Shows a dialog.
	 */
	public void showDialog(int title, int message) {
		new AlertDialog.Builder(this)
		.setTitle(title)
		.setMessage(message)
		.setCancelable(false)
		.setNeutralButton(R.string.close_dialog, null)
		.show();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu()
	 */
	@Override 
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_main, menu);
		return true;
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

		//Initialize database
		try {
			db = DataFramework.getInstance();
			db.open(this, this.getPackageName());
			dbHelper = new DataBaseHelper(db);

		} catch (Exception ex) {
			Log.e(ex.getClass().getSimpleName(), ex.getMessage());
			error(ex.getMessage());
			ex.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		dbHelper.close();
		super.onDestroy();
	}
	
	protected void setMenuDbClean(){
		
	}
}
