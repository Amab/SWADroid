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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.android.dataframework.DataFramework;

import android.app.AlertDialog;
import android.app.ExpandableListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import es.ugr.swad.swadroid.model.DataBaseHelper;
import es.ugr.swad.swadroid.modules.notifications.Notifications;
import es.ugr.swad.swadroid.ssl.SecureConnection;

/**
 * Main class of the application.
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class SWADMain extends ExpandableListActivity {
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
    private static DataFramework db;
    /**
     * Array of strings for main ListView
     */
    protected String[] functions;
    /**
     * Function name field
     */
    final String NAME = "functionText";
    /**
     * Function text field
     */
    final String IMAGE = "functionIcon";
    
    /**
     * Gets the database helper
	 * @return the database helper
	 */
	public static DataBaseHelper getDbHelper() {
		return dbHelper;
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
     * Shows an error message.
     * @param message Error message to show.
     */
    public void error(String message) {
        new AlertDialog.Builder(this).setTitle(R.string.title_error_dialog)
                .setMessage(message)
                .setNeutralButton(R.string.close_dialog, null)
                .setIcon(R.drawable.erroricon).show();
                /*.setNeutralButton(R.string.close_dialog,
                new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                SWADMain.this.finish();
                }
                })*/
    }

    /**
     * Shows configuration dialog on first run.
     */
    public void showConfigurationDialog() {
    	new AlertDialog.Builder(this)
    		   .setTitle(R.string.initialDialogTitle)
    	       .setMessage(R.string.firstRunMsg)
    	       .setCancelable(false)
    	       .setPositiveButton(R.string.yesMsg, new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	        	   viewPreferences();
    	           }
    	       })
    	       .setNegativeButton(R.string.noMsg, new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	                dialog.cancel();
    	           }
    	       }).show();
    }
    
    /**
     * Shows initial dialog after application upgrade.
     */
    public void showUpgradeDialog() {
    	new AlertDialog.Builder(this)
    		   .setTitle(R.string.initialDialogTitle)
    	       .setMessage(R.string.upgradeMsg)
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
            case R.id.preferences_menu:
            	viewPreferences();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult()
	 */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == ExpandableListActivity.RESULT_OK) {
            //Bundle extras = data.getExtras();

            switch(requestCode) {
		        case Global.NOTIFICATIONS_REQUEST_CODE:
		            Toast.makeText(getBaseContext(),
		                    R.string.notificationsSuccessfulMsg,
		                    Toast.LENGTH_LONG).show();
		            Log.d(Global.NOTIFICATIONS_TAG, getString(R.string.notificationsSuccessfulMsg));
            }
        }
    }

	/* (non-Javadoc)
	 * @see android.app.ExpandableListActivity#onChildClick(android.widget.ExpandableListView, android.view.View, int, int, long)
	 */
	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		// Get the item that was clicked
		Object o = this.getExpandableListAdapter().getChild(groupPosition, childPosition);
		String keyword = (String) ((Map<String,Object>)o).get(NAME);
		
		Intent activity;
		if(keyword.equals(getString(R.string.notificationsModuleLabel)))
		{
				activity = new Intent(getBaseContext(),
	                Notifications.class);
				startActivityForResult(activity, Global.NOTIFICATIONS_REQUEST_CODE);
				
		} else if(keyword.equals(getString(R.string.testsModuleLabel))) {
				/*activity = new Intent(getBaseContext(),
		                Tests.class);
					startActivityForResult(activity, Global.TESTS_REQUEST_CODE);*/
				Toast.makeText(this, keyword + " aún no implementado", Toast.LENGTH_LONG)
					.show();
		}
		
		return true;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStart()
	 */
    @Override
    protected void onStart() {
        super.onStart();
        prefs.getPreferences(getBaseContext());
    }
    
    private void createMainMenu()
    {
    	// Construct Expandable List
        final ArrayList<HashMap<String, Object>> headerData = new ArrayList<HashMap<String, Object>>();

        final HashMap<String, Object> messages = new HashMap<String, Object>();
        messages.put(NAME, getString(R.string.messages));
        messages.put(IMAGE, getResources().getDrawable(R.drawable.msg));
        headerData.add( messages );

        final HashMap<String, Object> evaluation = new HashMap<String, Object>();
        evaluation.put(NAME, getString(R.string.evaluation));
        evaluation.put(IMAGE, getResources().getDrawable(R.drawable.grades));
        headerData.add( evaluation);

        final ArrayList<ArrayList<HashMap<String, Object>>> childData = new ArrayList<ArrayList<HashMap<String, Object>>>();

        final ArrayList<HashMap<String, Object>> messagesData = new ArrayList<HashMap<String, Object>>();
        childData.add(messagesData);

        final ArrayList<HashMap<String, Object>> evaluationData = new ArrayList<HashMap<String, Object>>();
        childData.add(evaluationData);

        HashMap<String, Object> map = new HashMap<String,Object>();
        map.put(NAME, getString(R.string.notificationsModuleLabel) );
        map.put(IMAGE, getResources().getDrawable(R.drawable.notif));
        messagesData.add(map);
        
        map = new HashMap<String,Object>();
        map.put(NAME, getString(R.string.testsModuleLabel) );
        map.put(IMAGE, getResources().getDrawable(R.drawable.test));
        evaluationData.add(map);

        setListAdapter( new ImageExpandableListAdapter(
                this,
                headerData,
                R.layout.functions_list_item,
                new String[] { NAME },            // the name of the field data
                new int[] { R.id.functionText }, // the text field to populate with the field data
                childData,
                0,
                null,
                new int[] {}
            ));
        
        getExpandableListView().setOnChildClickListener(this);
    }

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate()
	 */
    @Override
    public void onCreate(Bundle icicle) {
        int lastVersion, currentVersion;
		ImageView image;
		TextView text;
		
    	//Initialize screen
        super.onCreate(icicle);
        setContentView(R.layout.main);
        
        image = (ImageView)this.findViewById(R.id.moduleIcon);
        image.setBackgroundResource(R.drawable.ic_launcher_swadroid);
        
        text = (TextView)this.findViewById(R.id.moduleName);
        text.setText(R.string.app_name);
        
        createMainMenu();
        
        try {            
            //Initialize database
            db = DataFramework.getInstance();
            db.open(this, this.getPackageName());
            dbHelper = new DataBaseHelper(db);
            
            //Initialize preferences
            prefs.getPreferences(getBaseContext()); 
            
            //Initialize HTTPS connections 
            SecureConnection.initSecureConnection(); 
            
            //Check if this is the first run after an install or upgrade
            //If this is the first run, show configuration dialog
            //If this is an upgrade, show upgrade dialog
            lastVersion = prefs.getLastVersion();
            currentVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
            if(lastVersion == 0) {
            	showConfigurationDialog();
            	prefs.setLastVersion(currentVersion);
            } else if(lastVersion < 11) {
            	dbHelper.emptyTable(Global.DB_TABLE_NOTIFICATIONS);
            	prefs.setLastVersion(currentVersion);
            	showUpgradeDialog();
            }
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
}
