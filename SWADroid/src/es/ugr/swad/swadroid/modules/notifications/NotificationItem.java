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

import es.ugr.swad.swadroid.DownloadImageTask;
import es.ugr.swad.swadroid.Global;
import es.ugr.swad.swadroid.MenuActivity;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.modules.Messages;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Webview activity for showing marks
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class NotificationItem extends MenuActivity {
	Long notificationCode;
	String sender;
	String userPhoto;
	String course;
	String summary;
	String content;
	
	private String fixLinks(String body) {
	    String regex = "(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
	    body = body.replaceAll(regex, "<a href=\"$0\">$0</a>");
	    return body;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		TextView text, senderTextView, courseTextView, summaryTextView;
		ImageView image, imageSep, userPhotoView;
		ImageButton replyButton;
		WebView webview;
		
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.single_notification_view);
		
		senderTextView = (TextView)this.findViewById(R.id.senderNameText);
		courseTextView = (TextView)this.findViewById(R.id.courseNameText);
		summaryTextView = (TextView)this.findViewById(R.id.summaryText);
		userPhotoView = (ImageView) this.findViewById(R.id.notifUserPhoto);
		webview = (WebView)this.findViewById(R.id.contentWebView);
        
		image = (ImageView)this.findViewById(R.id.moduleIcon);
        image.setBackgroundResource(R.drawable.notif);
        
        text = (TextView)this.findViewById(R.id.moduleName);
        text.setText(R.string.notificationsModuleLabel);
        
        imageSep = (ImageView)this.findViewById(R.id.title_sep_4);
        imageSep.setVisibility(View.VISIBLE);

		replyButton = (ImageButton)this.findViewById(R.id.messageReplyButton);
		replyButton.setVisibility(View.VISIBLE);
		
		sender = this.getIntent().getStringExtra("sender");
		userPhoto = this.getIntent().getStringExtra("userPhoto");
		course = this.getIntent().getStringExtra("course");
		summary = this.getIntent().getStringExtra("summary");
		content = this.getIntent().getStringExtra("content");
        
	    senderTextView.setText(sender);         
	    courseTextView.setText(course); 
	    summaryTextView.setText(summary);
        
	    //If the user photo exists and is public, download and show it
	    if(userPhoto != null)
		    if(Global.connectionAvailable(this) && !userPhoto.equalsIgnoreCase("")) {
		    	//userPhotoView.setImageURI(Uri.parse(userPhoto));
		    	new DownloadImageTask(userPhotoView).execute(userPhoto);
		    } else {
		    	Log.d("NotificationItem", "No connection or no photo " + userPhoto);
		    }
				
		content = fixLinks(content);		
		webview.getSettings().setRenderPriority(RenderPriority.HIGH);
		webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);		
		webview.loadDataWithBaseURL("", content, "text/html", "utf-8", "");
	}
	
	/**
	 * Launches an action when reply message button is pushed
	 * @param v Actual view
	 */
	public void onReplyMessageClick(View v)
	{
		notificationCode = Long.valueOf(this.getIntent().getStringExtra("notificationCode"));
		Intent activity = new Intent(this, Messages.class);
		activity.putExtra("notificationCode", notificationCode);
		activity.putExtra("summary", summary);
		startActivity(activity);
	}
}
