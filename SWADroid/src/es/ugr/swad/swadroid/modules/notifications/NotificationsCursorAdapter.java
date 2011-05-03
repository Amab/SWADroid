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

import java.util.Date;

import es.ugr.swad.swadroid.R;
import android.content.Context;
import android.database.Cursor;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Custom adapter for display notifications
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 *
 */
public class NotificationsCursorAdapter extends CursorAdapter {	
	public NotificationsCursorAdapter(Context context, Cursor c) {
		super(context, c);
	}

	public NotificationsCursorAdapter(Context context, Cursor c,
			boolean autoRequery) {
		
		super(context, c, autoRequery);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {		
		long unixTime;
		String type, sender, senderFirstname, senderSurname1, senderSurname2, summaryText, contentText;
		String[] dateContent;
    	Date d;
    	
        TextView eventType = (TextView) view.findViewById(R.id.eventType);
        TextView eventDate = (TextView) view.findViewById(R.id.eventDate);
        TextView eventTime = (TextView) view.findViewById(R.id.eventTime);
        TextView eventSender = (TextView) view.findViewById(R.id.eventSender);
        TextView location = (TextView) view.findViewById(R.id.eventLocation);
        TextView summary = (TextView) view.findViewById(R.id.eventSummary);
        TextView content = (TextView) view.findViewById(R.id.eventText);
        ImageView notificationIcon = (ImageView) view.findViewById(R.id.notificationIcon);
        
        if(eventType != null) {
        	type = cursor.getString(cursor.getColumnIndex("eventType"));
        	
        	if(type.equals("examAnnouncement"))
        	{
        		type = context.getString(R.string.examAnnouncement);
        		notificationIcon.setImageResource(R.drawable.announce);
        	} else if(type.equals("marksFile"))
        	{
        		type = context.getString(R.string.marksFile);
        		notificationIcon.setImageResource(R.drawable.grades);
        	} else if(type.equals("notice"))
        	{
        		type = context.getString(R.string.notice);
        		notificationIcon.setImageResource(R.drawable.note);
        	} else if(type.equals("message"))
        	{
        		type = context.getString(R.string.message);
        		notificationIcon.setImageResource(R.drawable.recmsg);
        	} else if(type.equals("forumReply"))
        	{
        		type = context.getString(R.string.forumReply);
        		notificationIcon.setImageResource(R.drawable.forum);
        	}
        	
        	eventType.setText(type);
        }
        if((eventDate != null) && (eventTime != null)){
        	unixTime = Long.parseLong(cursor.getString(cursor.getColumnIndex("eventTime")));
        	d = new Date(unixTime * 1000);
        	dateContent = d.toLocaleString().split(" ");
        	eventDate.setText(dateContent[0]);
        	eventTime.setText(dateContent[1]);
        }
        if(eventSender != null){
        	sender = "";
        	senderFirstname = cursor.getString(cursor.getColumnIndex("userFirstname"));
        	senderSurname1 = cursor.getString(cursor.getColumnIndex("userSurname1"));
        	senderSurname2 = cursor.getString(cursor.getColumnIndex("userSurname2"));
        	
        	//Empty fields checking
        	if(!senderFirstname.equals("anyType{}"))
        		sender += senderFirstname + " ";
        	if(!senderSurname1.equals("anyType{}"))
        		sender += senderSurname1 + " ";
        	if(!senderSurname2.equals("anyType{}"))
        		sender += senderSurname2;
        	
        	eventSender.setText(sender);
        }
        if(location != null) {
        	location.setText(cursor.getString(cursor.getColumnIndex("location")));
        }
        if(summary != null){   
        	summaryText = cursor.getString(cursor.getColumnIndex("summary"));
        	
        	//Empty field checking
        	if(summaryText.equals("anyType{}"))
        		summaryText = context.getString(R.string.noSubjectMsg);
        	
        	summary.setText(Html.fromHtml(summaryText));
        }
        if((content != null)){
        	contentText = cursor.getString(cursor.getColumnIndex("content"));
        	
        	//Empty field checking
        	if(contentText.equals("anyType{}"))
        		contentText = context.getString(R.string.noContentMsg);
        		
        	content.setText(Html.fromHtml(contentText));
        }
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {	
		LayoutInflater vi = LayoutInflater.from(context);
		View v = vi.inflate(R.layout.notifications_list_item, parent, false);
		
		return v;
	}

}
