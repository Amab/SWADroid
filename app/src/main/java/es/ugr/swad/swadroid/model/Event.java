/*
 *
 *  *  This file is part of SWADroid.
 *  *
 *  *  Copyright (C) 2010 Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 *  *
 *  *  SWADroid is free software: you can redistribute it and/or modify
 *  *  it under the terms of the GNU General Public License as published by
 *  *  the Free Software Foundation, either version 3 of the License, or
 *  *  (at your option) any later version.
 *  *
 *  *  SWADroid is distributed in the hope that it will be useful,
 *  *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  *  GNU General Public License for more details.
 *  *
 *  *  You should have received a copy of the GNU General Public License
 *  *  along with SWADroid.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package es.ugr.swad.swadroid.model;

import android.text.TextUtils;

import org.ksoap2.serialization.PropertyInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

/**
 * Class for store a rollcall event
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class Event extends Model {
    private boolean hidden;
    private String userSurname1;
    private String userSurname2;
    private String userFirstName;
    private String userPhoto;
    private int startTime;
    private int endTime;
    private boolean commentsTeachersVisible;
    private String title;
    private String text;
    private String groups;
    private String status;
    private static final PropertyInfo PI_id = new PropertyInfo();
    private static final PropertyInfo PI_hidden = new PropertyInfo();
    private static final PropertyInfo PI_userSurname1 = new PropertyInfo();
    private static final PropertyInfo PI_userSurname2 = new PropertyInfo();
    private static final PropertyInfo PI_userFirstName = new PropertyInfo();
    private static final PropertyInfo PI_userPhoto = new PropertyInfo();
    private static final PropertyInfo PI_startTime = new PropertyInfo();
    private static final PropertyInfo PI_endTime = new PropertyInfo();
    private static final PropertyInfo PI_commentsTeachersVisible = new PropertyInfo();
    private static final PropertyInfo PI_title = new PropertyInfo();
    private static final PropertyInfo PI_groups = new PropertyInfo();
    private static final PropertyInfo PI_status = new PropertyInfo();
    private static final PropertyInfo[] PI_PROP_ARRAY =
            {
                    PI_id,
                    PI_hidden,
                    PI_userSurname1,
                    PI_userSurname2,
                    PI_userFirstName,
                    PI_userPhoto,
                    PI_startTime,
                    PI_endTime,
                    PI_commentsTeachersVisible,
                    PI_title,
                    PI_groups,
                    PI_status
            };

    /**
     * Constructor with status
     */
    public Event(long attendanceEventCode, boolean hidden, String userSurname1, String userSurname2,
                 String userFirstName, String userPhoto, int startTime, int endTime,
                 boolean commentsTeachersVisible, String title, String text, String groups,
                 String status) {
        super(attendanceEventCode);
        this.hidden = hidden;
        this.userSurname1 = userSurname1;
        this.userSurname2 = userSurname2;
        this.userFirstName = userFirstName;
        this.userPhoto = userPhoto;
        this.startTime = startTime;
        this.endTime = endTime;
        this.commentsTeachersVisible = commentsTeachersVisible;
        this.title = title;
        this.text = text;
        this.groups = groups;
        this.status = status;
    }

    /**
     * Constructor without status
     */
    public Event(long attendanceEventCode, boolean hidden, String userSurname1, String userSurname2,
                 String userFirstName, String userPhoto, int startTime, int endTime,
                 boolean commentsTeachersVisible, String title, String text, String groups) {
        super(attendanceEventCode);
        this.hidden = hidden;
        this.userSurname1 = userSurname1;
        this.userSurname2 = userSurname2;
        this.userFirstName = userFirstName;
        this.userPhoto = userPhoto;
        this.startTime = startTime;
        this.endTime = endTime;
        this.commentsTeachersVisible = commentsTeachersVisible;
        this.title = title;
        this.text = text;
        this.groups = groups;
        this.status = "OK";
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public String getUserSurname1() {
        return userSurname1;
    }

    public void setUserSurname1(String userSurname1) {
        this.userSurname1 = userSurname1;
    }

    public String getUserSurname2() {
        return userSurname2;
    }

    public void setUserSurname2(String userSurname2) {
        this.userSurname2 = userSurname2;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public int getStartTime() {
        return startTime;
    }

    public Calendar getStartTimeCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startTime * 1000L);
        return calendar;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public Calendar getEndTimeCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(endTime * 1000L);
        return calendar;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public boolean isCommentsTeachersVisible() {
        return commentsTeachersVisible;
    }

    public void setCommentsTeachersVisible(boolean commentsTeachersVisible) {
        this.commentsTeachersVisible = commentsTeachersVisible;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getGroups() {
        return groups;
    }

    public void setGroups(String groups) {
        this.groups = groups;
    }

    public List<String> getGroupsList() {
        return new ArrayList(Arrays.asList(groups));
    }

    public void setGroupsList(List<String> groups) {
        this.groups = TextUtils.join(",", groups);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /* (non-Javadoc)
         * @see org.ksoap2.serialization.KvmSerializable#getProperty(int)
         */
    public Object getProperty(int param) {
        Object object = null;
        switch (param) {
            case 0:
                object = this.getId();
                break;
            case 1:
                object = hidden;
                break;
            case 2:
                object = userSurname1;
                break;
            case 3:
                object = userSurname2;
                break;
            case 4:
                object = userFirstName;
                break;
            case 5:
                object = userPhoto;
                break;
            case 6:
                object = startTime;
                break;
            case 7:
                object = endTime;
                break;
            case 8:
                object = commentsTeachersVisible;
                break;
            case 9:
                object = title;
                break;
            case 10:
                object = text;
                break;
            case 11:
                object = groups;
                break;
            case 12:
                object = status;
                break;
        }

        return object;
    }

    /* (non-Javadoc)
     * @see org.ksoap2.serialization.KvmSerializable#getPropertyCount()
     */
    public int getPropertyCount() {
        return PI_PROP_ARRAY.length;
    }

    /* (non-Javadoc)
     * @see org.ksoap2.serialization.KvmSerializable#getPropertyInfo(int, java.util.Hashtable, org.ksoap2.serialization.PropertyInfo)
     */
    public void getPropertyInfo(int param, @SuppressWarnings("rawtypes") Hashtable arg1, PropertyInfo propertyInfo) {
        switch (param) {
            case 0:
                propertyInfo.type = PropertyInfo.LONG_CLASS;
                propertyInfo.name = "attendanceEventCode";
                break;
            case 1:
                propertyInfo.type = PropertyInfo.BOOLEAN_CLASS;
                propertyInfo.name = "hidden";
                break;
            case 2:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "userSurname1";
                break;
            case 3:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "userSurname2";
                break;
            case 4:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "userFirstName";
                break;
            case 5:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "userPhoto";
                break;
            case 6:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "startTime";
                break;
            case 7:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "endTime";
                break;
            case 8:
                propertyInfo.type = PropertyInfo.BOOLEAN_CLASS;
                propertyInfo.name = "commentsTeachersVisible";
                break;
            case 9:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "title";
                break;
            case 10:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "text";
                break;
            case 11:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "groups";
                break;
            case 12:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "status";
                break;
        }
    }

    /* (non-Javadoc)
     * @see org.ksoap2.serialization.KvmSerializable#setProperty(int, java.lang.Object)
     */
    public void setProperty(int param, Object obj) {
        switch (param) {
            case 0:
                this.setId((Long) obj);
                break;
            case 1:
                hidden = (Boolean) obj;
                break;
            case 2:
                userSurname1 = (String) obj;
                break;
            case 3:
                userSurname2 = (String) obj;
                break;
            case 4:
                userFirstName = (String) obj;
                break;
            case 5:
                userPhoto = (String) obj;
                break;
            case 6:
                startTime = (Integer) obj;
                break;
            case 7:
                endTime = (Integer) obj;
                break;
            case 8:
                commentsTeachersVisible = (Boolean) obj;
                break;
            case 9:
                title = (String) obj;
                break;
            case 10:
                text = (String) obj;
                break;
            case 11:
                groups = (String) obj;
                break;
            case 12:
                status = (String) obj;
                break;
        }
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + super.getId() +
                ", hidden=" + hidden +
                ", userSurname1='" + userSurname1 + '\'' +
                ", userSurname2='" + userSurname2 + '\'' +
                ", userFirstName='" + userFirstName + '\'' +
                ", userPhoto='" + userPhoto + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", commentsTeachersVisible=" + commentsTeachersVisible +
                ", title='" + title + '\'' +
                ", text='" + text + '\'' +
                ", groups='" + groups + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}