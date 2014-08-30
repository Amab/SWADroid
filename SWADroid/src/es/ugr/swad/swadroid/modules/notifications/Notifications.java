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
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;
import android.widget.Toast;

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
public class Notifications extends Module implements
		SwipeRefreshLayout.OnRefreshListener {
	/**
	 * Max size to store notifications
	 */
	private int SIZE_LIMIT;
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
	 * Layout with "Pull to refresh" function
	 */
	private SwipeRefreshLayout refreshLayout;
	/**
	 * ListView container for notifications
	 */
	ExpandableListView list;
	/**
	 * Adapter container for notifications
	 */
	NotificationsExpandableListAdapter adapter;
	/**
	 * TextView for the empty notifications message
	 */
	TextView emptyNotifTextView;
	/**
	 * List of groups for ExpandableListView
	 */
	ArrayList<String> groupItem;
	/**
	 * List of childs for ExpandableListView
	 */
	ArrayList<List<Model>> childItem;
	/**
	 * Id for the not seen notifications group
	 */
	int NOT_SEEN_GROUP_ID = 0;
	/**
	 * Id for the seen notifications group
	 */
	int SEEN_GROUP_ID = 1;
	/**
	 * ListView click listener
	 */
	private OnChildClickListener clickListener = new OnChildClickListener() {
		@Override
		public boolean onChildClick(ExpandableListView parent, View v,
				int groupPosition, int childPosition, long id) {
			
			TextView notifCode = (TextView) v.findViewById(R.id.notifCode);
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

			Intent activity = new Intent(getApplicationContext(),
					NotificationItem.class);
			activity.putExtra("notifCode", notifCode.getText().toString());
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
			
			return true;
		}
	};

	/**
	 * Refreshes data on screen
	 */
	private void refreshScreen() {
		// Refresh data on screen
		setChildGroupData();
		hideSwipeProgress();
	}

	/**
	 * Sends to SWAD the "seen notifications" info
	 */
	private void sendReadedNotifications() {
		List<Model> markedNotificationsList;
		String seenNotifCodes;
		Intent activity;
		int numMarkedNotificationsList;

		if (isConnected) {
			// Construct a list of seen notifications in state
			// "pending to mark as read in SWAD"
			markedNotificationsList = dbHelper.getAllRows(
					Constants.DB_TABLE_NOTIFICATIONS,
					"seenLocal='" + Utils.parseBoolString(true)
							+ "' AND seenRemote='"
							+ Utils.parseBoolString(false) + "'", null);

			numMarkedNotificationsList = markedNotificationsList.size();
			if (isDebuggable)
				Log.d(TAG, "numMarkedNotificationsList="
						+ numMarkedNotificationsList);

			if (numMarkedNotificationsList > 0) {
				// Creates a string of notification codes separated by commas
				// from the previous list
				seenNotifCodes = Utils
						.getSeenNotificationCodes(markedNotificationsList);
				if (isDebuggable)
					Log.d(TAG, "seenNotifCodes=" + seenNotifCodes);

				// Sends "seen notifications" info to the server
				activity = new Intent(this, NotificationsMarkAllAsRead.class);
				activity.putExtra("seenNotifCodes", seenNotifCodes);
				activity.putExtra("numMarkedNotificationsList",
						numMarkedNotificationsList);
				startActivityForResult(activity,
						Constants.NOTIFMARKALLASREAD_REQUEST_CODE);
			}
		} else {
			Log.w(TAG,
					"Not connected: Sending of notifications read info to SWAD was deferred");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == Constants.NOTIFMARKALLASREAD_REQUEST_CODE) {
			if (resultCode == Activity.RESULT_OK) {
				Log.i(TAG, "Notifications marked as read in SWAD");
			} else {
				Log.e(TAG, "Error marking notifications as read in SWAD");
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.ugr.swad.swadroid.modules.Module#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_items_pulltorefresh);

		getSupportActionBar().setIcon(R.drawable.notif);

		this.findViewById(R.id.groupSpinner).setVisibility(View.GONE);

		refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
		list = (ExpandableListView) findViewById(R.id.list_pulltorefresh);
		emptyNotifTextView = (TextView) findViewById(R.id.list_item_title);
		
		groupItem = new ArrayList<String>();
		childItem = new ArrayList<List<Model>>();

		//Init ExpandableListView
		initSwipeOptions();

		//Set ExpandableListView data
		setGroupData();
		setChildGroupData();

		/*
		 * If there aren't notifications to show, hide the notifications list
		 * and show the empty notifications message
		 */
		if (dbHelper.getAllRowsCount(Constants.DB_TABLE_NOTIFICATIONS) == 0) {
			Log.d(TAG, "[onCreate] Notifications table is empty");
			
			emptyNotifTextView.setText(R.string.notificationsEmptyListMsg);
			emptyNotifTextView.setVisibility(View.VISIBLE);

			list.setOnChildClickListener(null);
			list.setVisibility(View.GONE);
		} else {
			Log.d(TAG, "[onCreate] Notifications table is not empty");
			
			emptyNotifTextView.setVisibility(View.GONE);
			list.setVisibility(View.VISIBLE);
		}

		setMETHOD_NAME("getNotifications");
		receiver = new SyncReceiver(this);
		account = new Account(getString(R.string.app_name), accountType);
		SIZE_LIMIT = Preferences.getNotifLimit();
	}

	/**
	 * Launches an action when markAllRead button is pushed
	 *
	 * @param v
	 *            Actual view
	 */
	public void onMarkAllReadClick(View v) {
		dbHelper.updateAllNotifications("seenLocal",
				Utils.parseBoolString(true));

		// Sends to SWAD the "seen notifications" info
		sendReadedNotifications();

		refreshScreen();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see es.ugr.swad.swadroid.modules.Module#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(NotificationsSyncAdapterService.START_SYNC);
		intentFilter.addAction(NotificationsSyncAdapterService.STOP_SYNC);
		intentFilter.addAction(Intent.CATEGORY_DEFAULT);
		registerReceiver(receiver, intentFilter);
		
		Log.i(TAG, "Registered receiver for automatic synchronization");

		refreshScreen();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.ugr.swad.swadroid.modules.Module#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(receiver);
		Log.i(TAG, "Unregistered receiver for automatic synchronization");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.ugr.swad.swadroid.modules.Module#requestService()
	 */
	@Override
	protected void requestService() throws Exception {
		// Download new notifications from the server
		SIZE_LIMIT = Preferences.getNotifLimit();

		if (SyncUtils.isSyncAutomatically(getApplicationContext())) {
			Log.i(TAG,
					"Automatic synchronization is enabled. Requesting asynchronous sync operation");

			// Call synchronization service
			ContentResolver.requestSync(account, authority, new Bundle());
		} else {
			Log.i(TAG,
					"Automatic synchronization is disabled. Requesting manual sync operation");

			// Calculates next timestamp to be requested
			Long timestamp = Long.valueOf(dbHelper
					.getFieldOfLastNotification("eventTime"));
			timestamp++;

			// Creates webservice request, adds required params and sends
			// request to webservice
			createRequest(SOAPClient.CLIENT_TYPE);
			addParam("wsKey", Constants.getLoggedUser().getWsKey());
			addParam("beginTime", timestamp);
			sendRequest(SWADNotification.class, false);

			if (result != null) {
				dbHelper.beginTransaction();

				// Stores notifications data returned by webservice response
				ArrayList<?> res = new ArrayList<Object>((Vector<?>) result);
				SoapObject soap = (SoapObject) res.get(1);
				int numNotif = soap.getPropertyCount();

				notifCount = 0;
				for (int i = 0; i < numNotif; i++) {
					SoapObject pii = (SoapObject) soap.getProperty(i);
					Long notifCode = Long.valueOf(pii.getProperty("notifCode")
							.toString());
					Long eventCode = Long.valueOf(pii.getProperty(
							"notificationCode").toString());
					String eventType = pii.getProperty("eventType").toString();
					Long eventTime = Long.valueOf(pii.getProperty("eventTime")
							.toString());
					String userSurname1 = pii.getProperty("userSurname1")
							.toString();
					String userSurname2 = pii.getProperty("userSurname2")
							.toString();
					String userFirstName = pii.getProperty("userFirstname")
							.toString();
					String userPhoto = pii.getProperty("userPhoto").toString();
					String location = pii.getProperty("location").toString();
					String summary = pii.getProperty("summary").toString();
					Integer status = Integer.valueOf(pii.getProperty("status")
							.toString());
					String content = pii.getProperty("content").toString();
					boolean notifReadSWAD = (status >= 4);

					SWADNotification n = new SWADNotification(notifCode,
							eventCode, eventType, eventTime, userSurname1,
							userSurname2, userFirstName, userPhoto, location,
							summary, status, content, notifReadSWAD,
							notifReadSWAD);
					dbHelper.insertNotification(n);

					// Count unread notifications only
					if (!notifReadSWAD) {
						notifCount++;
					}

					if (isDebuggable)
						Log.d(TAG, n.toString());
				}

				// Request finalized without errors
				setResult(RESULT_OK);
				Log.i(TAG, "Retrieved " + numNotif + " notifications ("
						+ notifCount + " unread)");

				// Clear old notifications to control database size
				dbHelper.clearOldNotifications(SIZE_LIMIT);

				dbHelper.endTransaction(true);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.ugr.swad.swadroid.modules.Module#connect()
	 */
	@Override
	protected void connect() {
		String progressDescription = getString(R.string.notificationsProgressDescription);
		int progressTitle = R.string.notificationsProgressTitle;

		startConnection(false, progressDescription, progressTitle);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.ugr.swad.swadroid.modules.Module#postConnect()
	 */
	@Override
	protected void postConnect() {
		Intent notIntent = new Intent(this, Notifications.class);

		if (!SyncUtils.isSyncAutomatically(getApplicationContext())) {
			if (notifCount > 0) {
				// If the notifications counter exceeds the limit, set it to the
				// max allowed
				if (notifCount > SIZE_LIMIT) {
					notifCount = SIZE_LIMIT;
				}

				AlertNotification.alertNotif(getApplicationContext(),
						NOTIF_ALERT_ID, getString(R.string.app_name),
						notifCount + " "
								+ getString(R.string.notificationsAlertMsg),
						getString(R.string.app_name), notIntent);
			} else {
				Toast.makeText(this, R.string.NoNotificationsMsg,
						Toast.LENGTH_LONG).show();
			}

			// Sends to SWAD the "seen notifications" info
			sendReadedNotifications();

			refreshScreen();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.ugr.swad.swadroid.modules.Module#onError()
	 */
	@Override
	protected void onError() {

	}

	/**
	 * Removes all notifications from database
	 *
	 * @param context
	 *            Database context
	 */
	public void clearNotifications(Context context) {
		try {
			dbHelper.emptyTable(Constants.DB_TABLE_NOTIFICATIONS);
		} catch (Exception e) {
			error(TAG, e.getMessage(), e, true);
		}
	}

	/**
	 * Synchronization callback. Is called when synchronization starts and stops
	 *
	 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
	 */
	private class SyncReceiver extends BroadcastReceiver {

		public SyncReceiver(Notifications activity) {

		}

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(
					NotificationsSyncAdapterService.START_SYNC)) {
				Log.i(TAG, "Started sync");
			} else if (intent.getAction().equals(
					NotificationsSyncAdapterService.STOP_SYNC)) {
				Log.i(TAG, "Stopped sync");

				notifCount = intent.getIntExtra("notifCount", 0);
				errorMessage = intent.getStringExtra("errorMessage");
				if ((errorMessage != null) && !errorMessage.equals("")) {
					error(TAG, errorMessage, null, true);
				} else if (notifCount == 0) {
					Toast.makeText(context, R.string.NoNotificationsMsg,
							Toast.LENGTH_LONG).show();
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
			dbHelper.updateAllNotifications("seenLocal",
					Utils.parseBoolString(true));
			sendReadedNotifications();
			refreshScreen();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/*
	 * @Override public void setContentView(int layoutResID) { View v =
	 * getLayoutInflater().inflate(layoutResID, refreshLayout, false);
	 * setContentView(v); }
	 * 
	 * @Override public void setContentView(View view) { setContentView(view,
	 * view.getLayoutParams()); }
	 * 
	 * @Override public void setContentView(View view, ViewGroup.LayoutParams
	 * params) { refreshLayout.addView(view, params); initSwipeOptions(); }
	 */

	private void initSwipeOptions() {
		refreshLayout.setOnRefreshListener(this);
		setAppearance();
		// disableSwipe();
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private void setAppearance() {
		refreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
	}

	/**
	 * It shows the SwipeRefreshLayout progress
	 */
	public void showSwipeProgress() {
		refreshLayout.setRefreshing(true);
	}

	/**
	 * It shows the SwipeRefreshLayout progress
	 */
	public void hideSwipeProgress() {
		refreshLayout.setRefreshing(false);
	}

	/**
	 * Enables swipe gesture
	 */
	public void enableSwipe() {
		refreshLayout.setEnabled(true);
	}

	/**
	 * Disables swipe gesture. It prevents manual gestures but keeps the option
	 * tu show refreshing programatically.
	 */
	public void disableSwipe() {
		refreshLayout.setEnabled(false);
	}

	/**
	 * It must be overriden by parent classes if manual swipe is enabled.
	 */
	@Override
	public void onRefresh() {
		showSwipeProgress();
		runConnection();
	}

	public void setGroupData() {
		groupItem.add(getString(R.string.notSeenLabel));
		groupItem.add(getString(R.string.seenLabel));
	}

	public void setChildGroupData() {	
		List<Model> child;
		
		//Clear data
		childItem.clear();
		
		//Add data for not seen notifications		 
		child = dbHelper.getAllRows(Constants.DB_TABLE_NOTIFICATIONS,
				"seenLocal='" + Utils.parseBoolString(false) + "'", orderby);
		childItem.add(child);
		
		//Add data for seen notifications	
		child = dbHelper.getAllRows(Constants.DB_TABLE_NOTIFICATIONS,
				"seenLocal='" + Utils.parseBoolString(true) + "'", orderby);
		childItem.add(child);
		
		Log.d(TAG, "groups size=" + childItem.size());
		Log.d(TAG, "not seen children size=" + childItem.get(NOT_SEEN_GROUP_ID).size());
		Log.d(TAG, "seen children size=" + childItem.get(SEEN_GROUP_ID).size());
		
		adapter = new NotificationsExpandableListAdapter(this, groupItem, childItem);

		if(dbHelper.getAllRowsCount(Constants.DB_TABLE_NOTIFICATIONS) > 0) {
			Log.d(TAG, "[setChildGroupData] Notifications table is not empty");
			
			emptyNotifTextView.setVisibility(View.GONE);
			list.setVisibility(View.VISIBLE);
			
			list.setAdapter(adapter);
			list.setOnChildClickListener(clickListener);
			
			//Expand the not seen notifications group
			list.expandGroup(NOT_SEEN_GROUP_ID);
		} else {
			Log.d(TAG, "[setChildGroupData] Notifications table is empty");
			
			list.setVisibility(View.GONE);
			emptyNotifTextView.setVisibility(View.VISIBLE);
		}
	}
}
