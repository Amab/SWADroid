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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.dao.SWADNotificationDao;
import es.ugr.swad.swadroid.gui.ImageFactory;
import es.ugr.swad.swadroid.gui.MenuActivity;
import es.ugr.swad.swadroid.gui.WebViewFactory;
import es.ugr.swad.swadroid.model.SWADNotification;
import es.ugr.swad.swadroid.modules.messages.Messages;
import es.ugr.swad.swadroid.utils.Utils;

/**
 * Webview activity for showing marks
 *
 * @author Juan Miguel Boyero Corral <swadroid@gmail.com>
 */
public class NotificationItem extends MenuActivity {
    /**
     * NotificationsItem tag name for Logcat
     */
    private static final String TAG = Constants.APP_TAG + " NotificationsItem";
    private Long notifCode;
    private Long eventCode;
    private String notificationType;
    private String summary;
    private String sender;
    private String userPhoto;
    /**
     * Data Access Object for Notifications
     */
    private SWADNotificationDao swadNotificationDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TextView senderTextView;
        TextView courseTextView;
        TextView summaryTextView;
        TextView dateTextView;
        TextView timeTextView;
        ImageView userPhotoView;
        WebView webview;
        Intent activity;
        SWADNotification notification;

        super.onCreate(savedInstanceState);

        //Initialize DAOs
        swadNotificationDao = db.getSwadNotificationDao();

        setContentView(R.layout.single_notification_view);

        senderTextView = this.findViewById(R.id.senderNameText);
        courseTextView = this.findViewById(R.id.courseNameText);
        summaryTextView = this.findViewById(R.id.summaryText);
        userPhotoView = this.findViewById(R.id.notifUserPhoto);
        webview = this.findViewById(R.id.contentWebView);
        dateTextView = this.findViewById(R.id.notifDate);
        timeTextView = this.findViewById(R.id.notifTime);

        notifCode = Long.valueOf(this.getIntent().getStringExtra("notifCode")); 
        eventCode = Long.valueOf(this.getIntent().getStringExtra("eventCode"));
        notificationType = this.getIntent().getStringExtra("notificationType");
        sender = this.getIntent().getStringExtra("sender");
        userPhoto = this.getIntent().getStringExtra("userPhoto");
        String course = this.getIntent().getStringExtra("course");
        summary = this.getIntent().getStringExtra("summary");
        String content = this.getIntent().getStringExtra("content");
        String date = this.getIntent().getStringExtra("date");
        String time = this.getIntent().getStringExtra("time");
        boolean seenLocal = Utils.parseStringBool(this.getIntent().getStringExtra("seenLocal"));

        senderTextView.setText(sender);
        courseTextView.setText(course);
        summaryTextView.setText(summary);
        dateTextView.setText(date);
        timeTextView.setText(time);

        //If the user photo exists and is public, download and show it
        if (Utils.connectionAvailable(this)
                && (userPhoto != null) && !userPhoto.equals("")
                && !userPhoto.equals(Constants.NULL_VALUE)) {
			ImageFactory.displayImage(getApplicationContext(), userPhoto, userPhotoView, true, true,
                    R.drawable.usr_bl, R.drawable.usr_bl, R.drawable.usr_bl);
        } else {
            Log.d(TAG, "No connection or no photo " + userPhoto);
        }

        content = Utils.fixLinks(content);
        if (content.startsWith("<![CDATA[")) {
            content = content.substring(9, content.length() - 3);
        }

        webview = WebViewFactory.getMathJaxWebView(webview);
        webview.setWebViewClient(WebViewFactory.getMathJaxExpression(content));
        
        //Set notification as seen locally
        notification = swadNotificationDao.findById(notifCode);
        notification.setSeenLocal(true);
        swadNotificationDao.updateSWADNotifications(Collections.singletonList(notification));
        
        //Sends "seen notifications" info to the server if there is a connection available
        if(!seenLocal) {
        	if(Utils.connectionAvailable(this)) {
		        activity = new Intent(this, NotificationsMarkAllAsRead.class);
		        activity.putExtra("seenNotifCodes", String.valueOf(notifCode));
		        activity.putExtra("numMarkedNotificationsList", 1);
		        startActivityForResult(activity, Constants.NOTIFMARKALLASREAD_REQUEST_CODE);
        	} else {
        		Log.w(TAG, "Not connected: Marking the notification " + notifCode + " as read in SWAD was deferred");
        	}
        }
    }

	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode == Constants.NOTIFMARKALLASREAD_REQUEST_CODE) {
			if (resultCode == Activity.RESULT_OK) {
				Log.i(TAG, "Notification " + notifCode + " marked as readed in SWAD");
			} else {
				Log.e(TAG, "Error marking notification " + notifCode + " as read in SWAD");
			}
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    getMenuInflater().inflate(R.menu.notification_single_activity_actions, menu);	 
	    
	    if (!notificationType.equals(getString(R.string.message))) {
	    	menu.removeItem(R.id.action_reply);
	    }
	    
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
            case R.id.action_reply:
                Intent activity = new Intent(this, Messages.class);
                activity.putExtra("eventCode", eventCode);
                activity.putExtra("summary", summary);
                activity.putExtra("sender", sender);
                activity.putExtra("photo", userPhoto);
                startActivity(activity);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
	    
	}
}
