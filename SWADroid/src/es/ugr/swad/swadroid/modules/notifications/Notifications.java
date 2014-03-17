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

import android.accounts.Account;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bugsense.trace.BugSenseHandler;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.Preferences;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.gui.AlertNotification;
import es.ugr.swad.swadroid.model.Model;
import es.ugr.swad.swadroid.model.SWADNotification;
import es.ugr.swad.swadroid.modules.Module;
import es.ugr.swad.swadroid.sync.SyncUtils;
import es.ugr.swad.swadroid.utils.Utils;
import es.ugr.swad.swadroid.webservices.SOAPClient;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Notifications module for get user's notifications
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 * @author Antonio Aguilera Malagon <aguilerin@gmail.com>
 * @author Helena Rodriguez Gijon <hrgijon@gmail.com>
 */
public class Notifications extends Module {
    /**
     * Max size to store notifications
     */
    private int SIZE_LIMIT;
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
    private final String selection = null;
    /**
     * Cursor orderby parameter
     */
    private final String orderby = "eventTime DESC";
    /**
     * Notifications counter
     */
    private int notifCount;
    /**
     * Error message returned by the synchronization service
     */
    private String errorMessage;
    /**
     * Unique identifier for notification alerts
     */
    private final int NOTIF_ALERT_ID = 1982;
    /**
     * Notifications tag name for Logcat
     */
    private static final String TAG = Constants.APP_TAG + " Notifications";
    /**
     * Account type
     */
    private static final String accountType = "es.ugr.swad.swadroid";
    /**
     * Synchronization authority
     */
    private static final String authority = "es.ugr.swad.swadroid.content";
    /**
     * Synchronization receiver
     */
    private static SyncReceiver receiver;
    /**
     * Synchronization account
     */
    private static Account account;
    /**
     * ListView click listener
     */
    private OnItemClickListener clickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int position, long rowId) {
            //adapter.toggleContentVisibility(position);
            TextView id = (TextView) v.findViewById(R.id.notifCode);
            TextView code = (TextView) v.findViewById(R.id.eventCode);
            TextView type = (TextView) v.findViewById(R.id.eventType);
            TextView userPhoto = (TextView) v.findViewById(R.id.eventUserPhoto);
            TextView sender = (TextView) v.findViewById(R.id.eventSender);
            TextView course = (TextView) v.findViewById(R.id.eventLocation);
            TextView summary = (TextView) v.findViewById(R.id.eventSummary);
            TextView content = (TextView) v.findViewById(R.id.eventText);
            TextView date = (TextView) v.findViewById(R.id.eventDate);
            TextView time = (TextView) v.findViewById(R.id.eventTime);
            TextView seenLocalText = (TextView) v.findViewById(R.id.seenLocal);

            Intent activity = new Intent(getApplicationContext(), NotificationItem.class);
            activity.putExtra("notifCode", id.getText().toString());
            activity.putExtra("eventCode", code.getText().toString());
            activity.putExtra("notificationType", type.getText().toString());
            activity.putExtra("userPhoto", userPhoto.getText().toString());
            activity.putExtra("sender", sender.getText().toString());
            activity.putExtra("course", course.getText().toString());
            activity.putExtra("summary", summary.getText().toString());
            activity.putExtra("content", content.getText().toString());
            activity.putExtra("date", date.getText().toString());
            activity.putExtra("time", time.getText().toString());
            activity.putExtra("seenLocal", seenLocalText.getText().toString());
            startActivity(activity);
        }
    };

    /**
     * Refreshes data on screen
     */
    private void refreshScreen() {
        //Refresh data on screen
        stopManagingCursor(dbCursor);
        dbCursor = dbHelper.getDb().getCursor(Constants.DB_TABLE_NOTIFICATIONS, selection, orderby);
        startManagingCursor(dbCursor);
        adapter.changeCursor(dbCursor);

        //TextView text = (TextView) this.findViewById(R.id.listText);
        //ListView list = (ListView) this.findViewById(R.id.listItems);
        PullToRefreshListView list = (PullToRefreshListView) this.findViewById(R.id.listItemsPullToRefresh);

        //If there are notifications to show, hide the empty notifications message and show the notifications list
        if (dbCursor.getCount() > 0) {
            //text.setVisibility(View.GONE);
            //list.setVisibility(View.VISIBLE);
            list.setAdapter(adapter);
            list.setOnItemClickListener(clickListener);
        	//list.setDividerHeight(1);
        }        

    	list.onRefreshComplete();
    }
    
    /**
     * Sends to SWAD the "seen notifications" info
     */
    private void sendReadedNotifications() {
        List<Model> markedNotificationsList;
        String seenNotifCodes;
        Intent activity;
        int numMarkedNotificationsList;
        
        if(isConnected) {
	    	//Construct a list of seen notifications in state "pending to mark as read in SWAD" 
	        markedNotificationsList = dbHelper.getAllRows(Constants.DB_TABLE_NOTIFICATIONS,
	        		"seenLocal='" + Utils.parseBoolString(true)
	        		+ "' AND seenRemote='" + Utils.parseBoolString(false) + "'", null);
	        
	        numMarkedNotificationsList = markedNotificationsList.size();
	        if(isDebuggable)
	        	Log.d(TAG, "numMarkedNotificationsList=" + numMarkedNotificationsList);
	        
	        if(numMarkedNotificationsList > 0) {            
	            //Creates a string of notification codes separated by commas from the previous list
	            seenNotifCodes = Utils.getSeenNotificationCodes(markedNotificationsList);
		        if(isDebuggable)
		        	Log.d(TAG, "seenNotifCodes=" + seenNotifCodes);
	
	            //Sends "seen notifications" info to the server
		        activity = new Intent(this, NotificationsMarkAllAsRead.class);
		        activity.putExtra("seenNotifCodes", seenNotifCodes);
		        activity.putExtra("numMarkedNotificationsList", numMarkedNotificationsList);
		        startActivityForResult(activity, Constants.NOTIFMARKALLASREAD_REQUEST_CODE);
	        }
        } else {
        	Log.w(TAG, "Not connected: Sending of notifications read info to SWAD was deferred");
        }
    }

	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode == Constants.NOTIFMARKALLASREAD_REQUEST_CODE) {
			if (resultCode == Activity.RESULT_OK) {
				Log.i(TAG, "Notifications marked as read in SWAD");
			} else {
				Log.e(TAG, "Error marking notifications as read in SWAD");
			}
		}
	}

    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.modules.Module#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //ListView list;
        final PullToRefreshListView list;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_items_pulltorefresh);

    	getSupportActionBar().setIcon(R.drawable.notif);

        this.findViewById(R.id.groupSpinner).setVisibility(View.GONE);

        dbCursor = dbHelper.getDb().getCursor(Constants.DB_TABLE_NOTIFICATIONS, selection, orderby);
        startManagingCursor(dbCursor);
        adapter = new NotificationsCursorAdapter(this, dbCursor, Preferences.getDBKey());

        //list = (ListView) this.findViewById(R.id.listItems);
        list = (PullToRefreshListView) this.findViewById(R.id.listItemsPullToRefresh);
        list.setAdapter(adapter);
        list.setOnItemClickListener(clickListener);
        list.setOnRefreshListener(new OnRefreshListener<ListView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                runConnection();
            }
        });

		/*
         * If there aren't notifications to show, hide the notifications list and show the empty notifications
		 * message
		 */
        if (dbCursor.getCount() == 0) {
            //list.setVisibility(View.GONE);
            //text.setVisibility(View.VISIBLE);
        	String emptyMsgArray[]={getString(R.string.notificationsEmptyListMsg)};
        	ArrayAdapter<String> adapter =new ArrayAdapter<String>(getApplicationContext(), R.layout.list_item, emptyMsgArray);
        	list.setAdapter(adapter);
        	list.setOnItemClickListener(null);
        	//list.setDividerHeight(0);
        }

        setMETHOD_NAME("getNotifications");
        receiver = new SyncReceiver(this);
        account = new Account(getString(R.string.app_name), accountType);
        SIZE_LIMIT = Preferences.getNotifLimit();
    }

    /**
     * Launches an action when markAllRead button is pushed
     *
     * @param v Actual view
     */
    public void onMarkAllReadClick(View v) {
        dbHelper.updateAllNotifications("seenLocal", Utils.parseBoolString(true));
        
        //Sends to SWAD the "seen notifications" info
        sendReadedNotifications();  
        
        refreshScreen();
    }

    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.modules.Module#onStart()
     */
    @Override
    protected void onStart() {
        super.onStart();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(NotificationsSyncAdapterService.START_SYNC);
        intentFilter.addAction(NotificationsSyncAdapterService.STOP_SYNC);
        intentFilter.addAction(Intent.CATEGORY_DEFAULT);
        registerReceiver(receiver, intentFilter);
        Log.i(TAG, "Registered receiver for automatic synchronization");

        refreshScreen();
    }
    
    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.modules.Module#onStop()
     */
    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
        Log.i(TAG, "Unregistered receiver for automatic synchronization");
    }

    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.modules.Module#requestService()
     */
    @Override
    protected void requestService() throws Exception {    	
    	//Download new notifications from the server
        SIZE_LIMIT = Preferences.getNotifLimit();

        if (SyncUtils.isSyncAutomatically(getApplicationContext())) {
        	Log.i(TAG, "Automatic synchronization is enabled. Requesting asynchronous sync operation");
        	
            //Call synchronization service
            ContentResolver.requestSync(account, authority, new Bundle());
        } else {
        	Log.i(TAG, "Automatic synchronization is disabled. Requesting manual sync operation");
        	
            //Calculates next timestamp to be requested
            Long timestamp = Long.valueOf(dbHelper.getFieldOfLastNotification("eventTime"));
            timestamp++;

            //Creates webservice request, adds required params and sends request to webservice
            createRequest(SOAPClient.CLIENT_TYPE);
            addParam("wsKey", Constants.getLoggedUser().getWsKey());
            addParam("beginTime", timestamp);
            sendRequest(SWADNotification.class, false);

            if (result != null) {
                dbHelper.beginTransaction();

                //Stores notifications data returned by webservice response
                ArrayList<?> res = new ArrayList<Object>((Vector<?>) result);
                SoapObject soap = (SoapObject) res.get(1);
                int numNotif = soap.getPropertyCount();
                
                notifCount = 0;
                for (int i = 0; i < numNotif; i++) {
                    SoapObject pii = (SoapObject) soap.getProperty(i);
                    Long notifCode = Long.valueOf(pii.getProperty("notifCode").toString());
                    Long eventCode = Long.valueOf(pii.getProperty("notificationCode").toString());
                    String eventType = pii.getProperty("eventType").toString();
                    Long eventTime = Long.valueOf(pii.getProperty("eventTime").toString());
                    String userSurname1 = pii.getProperty("userSurname1").toString();
                    String userSurname2 = pii.getProperty("userSurname2").toString();
                    String userFirstName = pii.getProperty("userFirstname").toString();
                    String userPhoto = pii.getProperty("userPhoto").toString();
                    String location = pii.getProperty("location").toString();
                    String summary = pii.getProperty("summary").toString();
                    Integer status = Integer.valueOf(pii.getProperty("status").toString());
                    String content = pii.getProperty("content").toString();
                    boolean notifReadSWAD = (status >= 4);
                    
                    SWADNotification n = new SWADNotification(notifCode, eventCode, eventType, eventTime, userSurname1, userSurname2, userFirstName, userPhoto, location, summary, status, content, notifReadSWAD, notifReadSWAD);
                    dbHelper.insertNotification(n);
                    
                    //Count unread notifications only
                    if(!notifReadSWAD) {
                    	notifCount++;
                    }

                    if(isDebuggable)
                    	Log.d(TAG, n.toString());
                }

                //Request finalized without errors
                setResult(RESULT_OK);
                Log.i(TAG, "Retrieved " + numNotif + " notifications (" + notifCount + " unread)");

                //Clear old notifications to control database size
                dbHelper.clearOldNotifications(SIZE_LIMIT);

                dbHelper.endTransaction(true);
            }
        }
    }
    
    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.modules.Module#connect()
     */
    @Override
    protected void connect() {
        String progressDescription = getString(R.string.notificationsProgressDescription);
        int progressTitle = R.string.notificationsProgressTitle;

        startConnection(false, progressDescription, progressTitle);
    }

    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.modules.Module#postConnect()
     */
    @Override
    protected void postConnect() {
        Intent notIntent = new Intent(this, Notifications.class);
        
        if (!SyncUtils.isSyncAutomatically(getApplicationContext())) {
        	if (notifCount > 0) {
	            //If the notifications counter exceeds the limit, set it to the max allowed
	            if (notifCount > SIZE_LIMIT) {
	                notifCount = SIZE_LIMIT;
	            }
	            
	            AlertNotification.alertNotif(getApplicationContext(),
	            		NOTIF_ALERT_ID,
	            		getString(R.string.app_name),
	            		notifCount + " " + getString(R.string.notificationsAlertMsg),
	            		getString(R.string.app_name),
	            		notIntent);
        	} else {
        		Toast.makeText(this, R.string.NoNotificationsMsg, Toast.LENGTH_LONG).show();
        	}

            /*ProgressBar pb = (ProgressBar) this.findViewById(R.id.progress_refresh);
            ImageButton updateButton = (ImageButton) this.findViewById(R.id.refresh);

            pb.setVisibility(View.GONE);
            updateButton.setVisibility(View.VISIBLE);*/
            
            //Sends to SWAD the "seen notifications" info
            sendReadedNotifications();  
            
            refreshScreen();
            //Toast.makeText(this, R.string.notificationsDownloadedMsg, Toast.LENGTH_SHORT).show();
        }
    }

    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.modules.Module#onError()
     */
    @Override
    protected void onError() {
        /*ProgressBar pb = (ProgressBar) this.findViewById(R.id.progress_refresh);
        ImageButton updateButton = (ImageButton) this.findViewById(R.id.refresh);

        pb.setVisibility(View.GONE);
        updateButton.setVisibility(View.VISIBLE);*/
    }

    /**
     * Removes all notifications from database
     *
     * @param context Database context
     */
    public void clearNotifications(Context context) {
        try {
            dbHelper.emptyTable(Constants.DB_TABLE_NOTIFICATIONS);
        } catch (Exception e) {
            e.printStackTrace();

            //Send exception details to Bugsense
            if (!isDebuggable) {
                BugSenseHandler.sendException(e);
            }
        }
    }

    /**
     * Synchronization callback. Is called when synchronization starts and stops
     *
     * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
     */
    private class SyncReceiver extends BroadcastReceiver {
        //private final Notifications mActivity;

        public SyncReceiver(Notifications activity) {
            //mActivity = activity;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(NotificationsSyncAdapterService.START_SYNC)) {
                Log.i(TAG, "Started sync");
            } else if (intent.getAction().equals(NotificationsSyncAdapterService.STOP_SYNC)) {
                Log.i(TAG, "Stopped sync");

                notifCount = intent.getIntExtra("notifCount", 0);
                errorMessage = intent.getStringExtra("errorMessage");
                if((errorMessage != null) && !errorMessage.equals("")) {
                	error(TAG, errorMessage, null, true);
                } else if (notifCount == 0) {
                    Toast.makeText(context, R.string.NoNotificationsMsg, Toast.LENGTH_LONG).show();
                }  
                
                refreshScreen();
            }
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notification_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_markAllRead:
                dbHelper.updateAllNotifications("seenLocal", Utils.parseBoolString(true));
                sendReadedNotifications();
                refreshScreen();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
