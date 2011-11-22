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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import es.ugr.swad.swadroid.model.DataBaseHelper;
import es.ugr.swad.swadroid.modules.Messages;
import es.ugr.swad.swadroid.modules.notifications.Notifications;
import es.ugr.swad.swadroid.modules.tests.Tests;
import es.ugr.swad.swadroid.ssl.SecureConnection;

/**
 * Main class of the application.
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class SWADMain extends MenuExpandableListActivity {
    /**
     * Array of strings for main ListView
     */
    protected String[] functions;
    /**
     * Function name field
     */
    final String NAME = "listText";
    /**
     * Function text field
     */
    final String IMAGE = "listIcon";
    
    /**
     * Gets the database helper
	 * @return the database helper
	 */
	public static DataBaseHelper getDbHelper() {
		return dbHelper;
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
	 * @see android.app.ExpandableListActivity#onChildClick(android.widget.ExpandableListView, android.view.View, int, int, long)
	 */
	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		// Get the item that was clicked
		Object o = this.getExpandableListAdapter().getChild(groupPosition, childPosition);
		@SuppressWarnings("unchecked")
		String keyword = (String) ((Map<String,Object>)o).get(NAME);
		
		Intent activity;
		if(keyword.equals(getString(R.string.notificationsModuleLabel)))
		{
			activity = new Intent(getBaseContext(), Notifications.class);
			startActivityForResult(activity, Global.NOTIFICATIONS_REQUEST_CODE);				
		} else if(keyword.equals(getString(R.string.testsModuleLabel))) {
			activity = new Intent(getBaseContext(), Tests.class);
			startActivityForResult(activity, Global.TESTS_REQUEST_CODE);
		} else if(keyword.equals(getString(R.string.messagesModuleLabel))) {
			activity = new Intent(getBaseContext(), Messages.class);
			activity.putExtra("notificationCode", new Long(0));
			startActivityForResult(activity, Global.MESSAGES_REQUEST_CODE);
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
    
    /**
     * Create main menu with an expandable list
     */
    private void createMainMenu()
    {
    	//Construct Expandable List
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
        
        //Messages category
        HashMap<String, Object> map = new HashMap<String,Object>();
        map.put(NAME, getString(R.string.notificationsModuleLabel) );
        map.put(IMAGE, getResources().getDrawable(R.drawable.notif));
        messagesData.add(map); 
        
        map = new HashMap<String,Object>();        
        map.put(NAME, getString(R.string.messagesModuleLabel) );
        map.put(IMAGE, getResources().getDrawable(R.drawable.msg));
        messagesData.add(map);
        
        //Evaluation category
        map = new HashMap<String,Object>();
        map.put(NAME, getString(R.string.testsModuleLabel) );
        map.put(IMAGE, getResources().getDrawable(R.drawable.test));
        evaluationData.add(map);

        setListAdapter( new ImageExpandableListAdapter(
                this,
                headerData,
                R.layout.image_list_item,
                new String[] { NAME },            // the name of the field data
                new int[] { R.id.listText }, // the text field to populate with the field data
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
            /*db = DataFramework.getInstance();
            db.open(this, this.getPackageName());
            dbHelper = new DataBaseHelper(db);*/
            
            //Initialize preferences
            prefs.getPreferences(getBaseContext()); 
            
            //Initialize HTTPS connections 
            SecureConnection.initSecureConnection(); 
            
            //Check if this is the first run after an install or upgrade
            lastVersion = prefs.getLastVersion();
            currentVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;

            //If this is the first run, show configuration dialog
            if(lastVersion == 0) {
            	showConfigurationDialog();
            	dbHelper.initializeDB();
            	prefs.setLastVersion(currentVersion);

            //If this is an upgrade, show upgrade dialog
            } else if(lastVersion < currentVersion) {
            	//showUpgradeDialog();
            	dbHelper.upgradeDB(this);
            	prefs.setLastVersion(currentVersion);
            }
        } catch (Exception ex) {
            Log.e(ex.getClass().getSimpleName(), ex.getMessage());
            error(ex.getMessage());
            ex.printStackTrace();
        }
    }
}
