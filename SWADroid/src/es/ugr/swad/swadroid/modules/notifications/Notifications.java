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
import android.view.View.OnClickListener;
import android.widget.Button;

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
	 * Downloads new notifications when pushed
	 */
	private Button updateButton;
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
	
	/* (non-Javadoc)
	 * @see es.ugr.swad.swadroid.modules.Module#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
        setContentView(R.layout.notifications);
        
        updateButton = (Button)this.findViewById(R.id.notificationsUpdateButton);
        updateButton.setOnClickListener(new OnClickListener() {
          public void onClick(View v) {
        	  runConnection();
          }          
        });
        
        //dbHelper.emptyTable(Global.DB_TABLE_NOTIFICATIONS);
        dbCursor = dbHelper.getDb().getCursor(Global.DB_TABLE_NOTIFICATIONS, selection, orderby);
        adapter = new NotificationsCursorAdapter(this, dbCursor);
        setListAdapter(adapter);
        
        setMETHOD_NAME("getNotifications");
	}

	/* (non-Javadoc)
	 * @see es.ugr.swad.swadroid.modules.Module#onResume()
	 */
	@Override
	protected void onResume() {		
		super.onResume();
		
		//Refresh data on screen 
        dbCursor = dbHelper.getDb().getCursor(Global.DB_TABLE_NOTIFICATIONS, selection, orderby);
        adapter.changeCursor(dbCursor);
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
	    	Vector res = (Vector) result;
	    	SoapObject soap = (SoapObject) res.get(1);
	    	int csSize = soap.getPropertyCount();
	    	Integer lastId = new Integer(dbHelper.getFieldOfLastNotification("id"));
	        for (int i = 0; i < csSize; i++) {
	            SoapObject pii = (SoapObject)soap.getProperty(i);
	            String eventType = pii.getProperty("eventType").toString();
	            Long eventTime = new Long(pii.getProperty("eventTime").toString());
	            String userSurname1 = pii.getProperty("userSurname1").toString();
	            String userSurname2 = pii.getProperty("userSurname2").toString();
	            String userFirstName = pii.getProperty("userFirstname").toString();
	            String location = pii.getProperty("location").toString();
	            String summary = pii.getProperty("summary").toString();
	            Integer status = new Integer(pii.getProperty("status").toString());
	            Notification n = new Notification(lastId+i, eventType, eventTime, userSurname1, userSurname2, userFirstName, location, summary, status);
	            dbHelper.insertNotification(n);
	            Log.d(Global.NOTIFICATIONS_TAG, n.toString());
	        }
	        
	        //Request finalized without errors
	        Log.i(Global.NOTIFICATIONS_TAG, "Retrieved " + csSize + " notifications");
			
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
		//Refresh data on screen 
        dbCursor = dbHelper.getDb().getCursor(Global.DB_TABLE_NOTIFICATIONS, selection, orderby);
        adapter.changeCursor(dbCursor);		
	}
	
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
