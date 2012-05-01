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

import es.ugr.swad.swadroid.MenuActivity;
import es.ugr.swad.swadroid.R;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Webview activity for showing marks
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class NotificationItem extends MenuActivity {
	private String fixLinks(String body) {
	    String regex = "(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
	    body = body.replaceAll(regex, "<a href=\"$0\">$0</a>");
	    return body;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		TextView text, senderTextView, courseTextView, summaryTextView;
		ImageView image;
		WebView webview;
		String sender = this.getIntent().getStringExtra("sender");
		String course = this.getIntent().getStringExtra("course");
		String summary = this.getIntent().getStringExtra("summary");
		String content = this.getIntent().getStringExtra("content");
		
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.single_notification_view);
		
		senderTextView = (TextView)this.findViewById(R.id.senderNameText);
		courseTextView = (TextView)this.findViewById(R.id.courseNameText);
		summaryTextView = (TextView)this.findViewById(R.id.summaryText);
		webview = (WebView)this.findViewById(R.id.contentWebView);
        
		image = (ImageView)this.findViewById(R.id.moduleIcon);
        image.setBackgroundResource(R.drawable.notif);
        
        text = (TextView)this.findViewById(R.id.moduleName);
        text.setText(R.string.notificationsModuleLabel);   
        
	    senderTextView.setText(sender);         
	    courseTextView.setText(course); 
	    summaryTextView.setText(summary); 
				
		content = fixLinks(content);		
		webview.getSettings().setRenderPriority(RenderPriority.HIGH);
		webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);		
		webview.loadDataWithBaseURL("", content, "text/html", "utf-8", "");
	}	
}
