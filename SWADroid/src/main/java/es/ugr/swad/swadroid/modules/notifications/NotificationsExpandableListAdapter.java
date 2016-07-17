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
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.model.Model;
import es.ugr.swad.swadroid.model.SWADNotification;
import es.ugr.swad.swadroid.utils.Utils;

/**
 * Custom ExpandableListAdapter for display notifications
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class NotificationsExpandableListAdapter extends
		BaseExpandableListAdapter {

	private ArrayList<String> groupItem;
	private ArrayList<List<Model>> childItem;
	private LayoutInflater minflater;
	private Activity activity;

	public NotificationsExpandableListAdapter(ArrayList<String> grList,
			ArrayList<List<Model>> childItem2) {
		groupItem = grList;
        this.childItem = childItem2;
	}

	public NotificationsExpandableListAdapter(Activity act, ArrayList<String> grList,
			ArrayList<List<Model>> childItem2) {
		groupItem = grList;
		activity = act;
		minflater = LayoutInflater.from(activity);
        this.childItem = childItem2;
	}

	public void setInflater(LayoutInflater mInflater, Activity act) {
		this.minflater = mInflater;
		activity = act;
	}

	public void setActivity(Activity act) {
		activity = act;
		minflater = LayoutInflater.from(activity);
	}

	@Override
	public View getChildView(int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		
		SWADNotification notif = (SWADNotification) childItem.get(groupPosition).get(childPosition);
		final Long notifCode = notif.getId();
        final Long eventCode = notif.getEventCode();
        final String userPhoto = notif.getUserPhoto();
        long unixTime;
        String type = "";
        String sender, senderFirstname, senderSurname1, senderSurname2, summaryText;
        String contentText, contentMsgText;
        Date d;
        java.text.DateFormat dateShortFormat = android.text.format.DateFormat.getDateFormat(activity);
        java.text.DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(activity);
        boolean seenLocal = notif.isSeenLocal();
        String seenLocalString = Utils.parseBoolString(seenLocal);
		
		if(convertView == null) {
			convertView = minflater.inflate(R.layout.list_item_notifications, parent, false);
		}
        
        if(!seenLocal) {
        	convertView.setBackgroundColor(ContextCompat.getColor(activity, R.color.notifications_background_highlighted));
        } else {
        	convertView.setBackgroundColor(ContextCompat.getColor(activity, R.color.background));
        }

        convertView.setScrollContainer(false);
        TextView notifCodeHided = (TextView) convertView.findViewById(R.id.notifCode);
        TextView eventCodeHided = (TextView) convertView.findViewById(R.id.eventCode);
        TextView seenLocalHided = (TextView) convertView.findViewById(R.id.seenLocal);
        TextView eventUserPhoto = (TextView) convertView.findViewById(R.id.eventUserPhoto);
        TextView eventType = (TextView) convertView.findViewById(R.id.eventType);
        TextView eventDate = (TextView) convertView.findViewById(R.id.eventDate);
        TextView eventTime = (TextView) convertView.findViewById(R.id.eventTime);
        TextView eventSender = (TextView) convertView.findViewById(R.id.eventSender);
        TextView location = (TextView) convertView.findViewById(R.id.eventLocation);
        final TextView summary = (TextView) convertView.findViewById(R.id.eventSummary);
        TextView content = (TextView) convertView.findViewById(R.id.eventText);
        TextView contentMsg = (TextView) convertView.findViewById(R.id.eventMsg);
        ImageView notificationIcon = (ImageView) convertView.findViewById(R.id.notificationIcon);

        if (eventType != null) {
            notifCodeHided.setText(notifCode.toString());
            eventCodeHided.setText(eventCode.toString());
            seenLocalHided.setText(seenLocalString);
            eventUserPhoto.setText(userPhoto);
            type = notif.getEventType();

            if (type.equals("examAnnouncement")) {
                type = activity.getString(R.string.examAnnouncement);
                notificationIcon.setImageResource(R.drawable.announce);
            } else if (type.equals("marksFile")) {
                type = activity.getString(R.string.marksFile);
                notificationIcon.setImageResource(R.drawable.grades);
            } else if (type.equals("notice")) {
                type = activity.getString(R.string.notice);
                notificationIcon.setImageResource(R.drawable.note);
            } else if (type.equals("message")) {
                type = activity.getString(R.string.message);
                notificationIcon.setImageResource(R.drawable.msg_received);
            } else if (type.equals("forumPostCourse")) {
                type = activity.getString(R.string.forumPostCourse);
                notificationIcon.setImageResource(R.drawable.forum);
            } else if (type.equals("forumReply")) {
                type = activity.getString(R.string.forumReply);
                notificationIcon.setImageResource(R.drawable.forum);
            } else if (type.equals("assignment")) {
                type = activity.getString(R.string.assignment);
                notificationIcon.setImageResource(R.drawable.desk);
            } else if (type.equals("documentFile")) {
                type = activity.getString(R.string.documentFile);
                notificationIcon.setImageResource(R.drawable.file);
            } else if (type.equals("sharedFile")) {
                type = activity.getString(R.string.sharedFile);
                notificationIcon.setImageResource(R.drawable.file);
            } else if (type.equals("enrollmentRequest")) {
                type = activity.getString(R.string.enrollmentRequest);
                notificationIcon.setImageResource(R.drawable.enrollment_request);
            } else if (type.startsWith("enrollment")) {
                type = activity.getString(R.string.enrollment);
                notificationIcon.setImageResource(R.drawable.enrollment);
            } else if (type.equals("documentFile")) {
                type = activity.getString(R.string.survey);
                notificationIcon.setImageResource(R.drawable.survey);
            } else if (type.equals("follower")) {
                type = activity.getString(R.string.follower);
                notificationIcon.setImageResource(R.drawable.follow);
            } else if (type.equals("timelineComment")) {
                type = activity.getString(R.string.timelineComment);
                notificationIcon.setImageResource(R.drawable.social_comment);
            } else if (type.equals("timelineFav")) {
                type = activity.getString(R.string.timelineFav);
                notificationIcon.setImageResource(R.drawable.social_fav);
            } else if (type.equals("timelineShare")) {
                type = activity.getString(R.string.timelineShare);
                notificationIcon.setImageResource(R.drawable.social_share);
            } else if (type.equals("timelineMention")) {
                type = activity.getString(R.string.timelineMention);
                notificationIcon.setImageResource(R.drawable.social_mention);
            } else {
                type = activity.getString(R.string.unknownNotification);
                notificationIcon.setImageResource(R.drawable.ic_launcher_swadroid);
            }

            eventType.setText(type);
        }
        if ((eventDate != null) && (eventTime != null)) {
            unixTime = notif.getEventTime();
            d = new Date(unixTime * 1000);
            eventDate.setText(dateShortFormat.format(d));
            eventTime.setText(timeFormat.format(d));
        }
        if (eventSender != null) {
            sender = "";
            senderFirstname = notif.getUserFirstName();
            senderSurname1 = notif.getUserSurname1();
            senderSurname2 = notif.getUserSurname2();

            //Empty fields checking
            if (!senderFirstname.equals(Constants.NULL_VALUE))
                sender += senderFirstname + " ";
            if (!senderSurname1.equals(Constants.NULL_VALUE))
                sender += senderSurname1 + " ";
            if (!senderSurname2.equals(Constants.NULL_VALUE))
                sender += senderSurname2;

            eventSender.setText(sender);
        }
        if (location != null) {
            location.setText(Utils.fromHtml(notif.getLocation()));
        }
        if (summary != null) {
            summaryText = notif.getSummary();

            //Empty field checking
            if (summaryText.equals(Constants.NULL_VALUE))
                summaryText = activity.getString(R.string.noSubjectMsg);

            summary.setText(Utils.fromHtml(summaryText));
        }
        if ((content != null)) {
        	contentText = notif.getContent();

            //Empty field checking
            if (contentText.equals(Constants.NULL_VALUE))
                contentText = activity.getString(R.string.noContentMsg);

            content.setText(contentText);

            if (type.equals(activity.getString(R.string.marksFile))) {
                contentMsgText = activity.getString(R.string.marksMsg);
                contentMsg.setText(contentMsgText);
            } else {
                contentMsgText = "";
                contentMsg.setText(contentMsgText);
            }
        }
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return childItem.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return groupItem.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return groupItem.size();
	}

	@Override
	public void onGroupCollapsed(int groupPosition) {
		super.onGroupCollapsed(groupPosition);
	}

	@Override
	public void onGroupExpanded(int groupPosition) {
		super.onGroupExpanded(groupPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		
		TextView groupTitle;
		
		if (convertView == null) {
			convertView = minflater.inflate(R.layout.group_type_list_item, parent, false);
		}
		
		groupTitle = (TextView) convertView.findViewById(R.id.groupTypeText);		
		groupTitle.setText(groupItem.get(groupPosition));
		
		return convertView;
	}

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childItem.get(groupPosition).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childItem.get(groupPosition).get(childPosition).getId();
    }

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
}
