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
package es.ugr.swad.swadroid.modules.notifications;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Vector;

import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParserException;

import com.android.dataframework.DataFramework;

import es.ugr.swad.swadroid.Global;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.model.DataBaseHelper;
import es.ugr.swad.swadroid.model.User;
import es.ugr.swad.swadroid.model.Notification;
import es.ugr.swad.swadroid.modules.Module;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Notifications module for get user's notifications
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 *
 */
public class Notifications extends Module {
    /**
     * Max size to store notifications 
     */
	private static final int SIZE_LIMIT = 25;
	/**
	 * Notifications adapter for showing the data
	 */
	private NotificationsCursorAdapter adapter;
	/**
	 * Cursor for database access
	 */
	private Cursor dbCursor;
	/**
	 * Cursor selection parameter
	 */
	private String selection = null;
	/**
	 * Cursor orderby parameter
	 */
    private String orderby = "eventTime DESC";
    /**
     * Notifications tag name for Logcat
     */
    public static final String TAG = Global.APP_TAG + " Notifications";
	
    /**
     * Refreshes data on screen
     */
    private void refreshScreen() {
    	//Refresh data on screen 
        dbCursor = dbHelper.getDb().getCursor(Global.DB_TABLE_NOTIFICATIONS, selection, orderby);
        adapter.changeCursor(dbCursor);
        
        TextView text = (TextView) this.findViewById(R.id.listText);
        ListView list = (ListView)this.findViewById(R.id.listItems);
        
        //If there are notifications to show, hide the empty notifications message and show the notifications list
        if(dbCursor.getCount() > 0) {
        	text.setVisibility(View.GONE);
        	list.setVisibility(View.VISIBLE);
        }
    }
    
	/* (non-Javadoc)
	 * @see es.ugr.swad.swadroid.modules.Module#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		ImageButton updateButton;
		ImageView image;
		TextView text;
		ListView list;
		OnItemClickListener clickListener = new OnItemClickListener() {
			public void onItemClick(AdapterView<?> av, View v, int arg2,
					long arg3) {
				
				TextView content = (TextView) v.findViewById(R.id.eventText);
				if(content.getVisibility() == View.VISIBLE)
				{
					content.setVisibility(View.GONE);
				} else {
					content.setVisibility(View.VISIBLE);
				}
			}    	
        };
		
		super.onCreate(savedInstanceState);
        setContentView(R.layout.list_items);
        
        image = (ImageView)this.findViewById(R.id.moduleIcon);
        image.setBackgroundResource(R.drawable.notif);
        
        text = (TextView)this.findViewById(R.id.moduleName);
        text.setText(R.string.notificationsModuleLabel);        

        image = (ImageView)this.findViewById(R.id.title_sep_1);
        image.setVisibility(View.VISIBLE);
        
        updateButton = (ImageButton)this.findViewById(R.id.refresh);
        updateButton.setVisibility(View.VISIBLE);
        
        dbCursor = dbHelper.getDb().getCursor(Global.DB_TABLE_NOTIFICATIONS, selection, orderby);
        adapter = new NotificationsCursorAdapter(this, dbCursor);
        
        list = (ListView)this.findViewById(R.id.listItems);
        list.setAdapter(adapter);
        list.setOnItemClickListener(clickListener);
        
    	text = (TextView) this.findViewById(R.id.listText);

    	/*
    	 * If there aren't notifications to show, hide the notifications list and show the empty notifications
    	 * message
    	 */
        if(dbCursor.getCount() == 0) {
        	list.setVisibility(View.GONE);
        	text.setVisibility(View.VISIBLE);
        	text.setText(R.string.notificationsEmptyListMsg);
        }
        
        setMETHOD_NAME("getNotifications");
	}
	
	/**
	 * Launches an action when refresh button is pushed
	 * @param v Actual view
	 */
	public void onRefreshClick(View v)
	{
		runConnection();
	}

	/* (non-Javadoc)
	 * @see es.ugr.swad.swadroid.modules.Module#onResume()
	 */
	@Override
	protected void onResume() {		
		super.onResume();		
		refreshScreen();
	}

	/* (non-Javadoc)
	 * @see es.ugr.swad.swadroid.modules.Module#requestService()
	 */
	@Override
	protected void requestService() throws NoSuchAlgorithmException,
			IOException, XmlPullParserException, SoapFault,
			IllegalAccessException, InstantiationException {
        
		//Calculates next timestamp to be requested
		Long timestamp = new Long(dbHelper.getFieldOfLastNotification("eventTime"));
		timestamp++;
		
		//Creates webservice request, adds required params and sends request to webservice
	    createRequest();
	    addParam("wsKey", User.getWsKey());
	    addParam("beginTime", timestamp);
	    sendRequest(Notification.class, false);
	
	    if (result != null) {
	        //Stores notifications data returned by webservice response
	    	@SuppressWarnings("rawtypes")
			Vector res = (Vector) result;
	    	SoapObject soap = (SoapObject) res.get(1);
	    	int csSize = soap.getPropertyCount();
	        for (int i = 0; i < csSize; i++) {
	            SoapObject pii = (SoapObject)soap.getProperty(i);
		    	Long notificationCode = new Long(pii.getProperty("notificationCode").toString());
	            String eventType = pii.getProperty("eventType").toString();
	            Long eventTime = new Long(pii.getProperty("eventTime").toString());
	            String userSurname1 = pii.getProperty("userSurname1").toString();
	            String userSurname2 = pii.getProperty("userSurname2").toString();
	            String userFirstName = pii.getProperty("userFirstname").toString();
	            String location = pii.getProperty("location").toString();
	            String summary = pii.getProperty("summary").toString();
	            Integer status = new Integer(pii.getProperty("status").toString());
	            String content = pii.getProperty("content").toString();
	            Notification n = new Notification(notificationCode, eventType, eventTime, userSurname1, userSurname2, userFirstName, location, summary, status, content);
	            dbHelper.insertNotification(n);
	            
	    		if(isDebuggable)
	    			Log.d(TAG, n.toString());
	        }
	        
	        //Request finalized without errors
	        Log.i(TAG, "Retrieved " + csSize + " notifications");
			
			//Clear old notifications to control database size
			dbHelper.clearOldNotifications(SIZE_LIMIT);
	    }
	}

	/* (non-Javadoc)
	 * @see es.ugr.swad.swadroid.modules.Module#connect()
	 */
	@Override
	protected void connect() {
		String progressDescription = getString(R.string.notificationsProgressDescription);
    	int progressTitle = R.string.notificationsProgressTitle;
  	    
        new Connect(true, progressDescription, progressTitle).execute();
	}

	/* (non-Javadoc)
	 * @see es.ugr.swad.swadroid.modules.Module#postConnect()
	 */
	@Override
	protected void postConnect() {		
		refreshScreen();	
	}
	
	/**
	 * Removes all notifications from database
	 * @param context Database context
	 */
	public void clearNotifications(Context context) {
	    try {
	       	DataFramework db = DataFramework.getInstance();
			db.open(context, context.getPackageName());
		    dbHelper = new DataBaseHelper(db);
	        
			dbHelper.emptyTable(Global.DB_TABLE_NOTIFICATIONS);
		} catch (Exception e) {
				e.printStackTrace();
		}
	}
}
