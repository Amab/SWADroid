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
package es.ugr.swad.swadroid.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;

import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

import lombok.Data;

/**
 * Class for store a notification
 *
 * @author Juan Miguel Boyero Corral <swadroid@gmail.com> *
 */
@Data
@Entity(indices = {@Index("seenLocal"), @Index("seenRemote"), @Index("eventTime")})
public class SWADNotification extends Model {
    /**
     * Event id (not unique)
     */
    private long eventCode;
    /**
     * Notification type
     */
    private String eventType;
    /**
     * Notification timestamp
     */
    private long eventTime;
    /**
     * Sender nickname
     */
    private String userNickname;
    /**
     * Sender first surname
     */
    private String userSurname1;
    /**
     * Sender second surname
     */
    private String userSurname2;
    /**
     * Sender first name
     */
    private String userFirstName;
    /**
     * Full URL path of the sender photo
     */
    private String userPhoto;
    /**
     * Notification location
     */
    private String location;
    /**
     * Notification summary
     */
    private String summary;
    /**
     * Notification status
     */
    private int status;
    /**
     * Notification content
     */
    private String content;
    /**
     * Notification has been seen locally
     */
    private boolean seenLocal;
    /**
     * Notification has been marked as seen in SWAD
     */
    private boolean seenRemote;

    @Ignore
    private static final PropertyInfo PI_id = new PropertyInfo();
    @Ignore
    private static final PropertyInfo PI_eventCode = new PropertyInfo();
    @Ignore
    private static final PropertyInfo PI_eventType = new PropertyInfo();
    @Ignore
    private static final PropertyInfo PI_eventTime = new PropertyInfo();
    @Ignore
    private static final PropertyInfo PI_userNickname = new PropertyInfo();
    @Ignore
    private static final PropertyInfo PI_userSurname1 = new PropertyInfo();
    @Ignore
    private static final PropertyInfo PI_userSurname2 = new PropertyInfo();
    @Ignore
    private static final PropertyInfo PI_userFirstName = new PropertyInfo();
    @Ignore
    private static final PropertyInfo PI_userPhoto = new PropertyInfo();
    @Ignore
    private static final PropertyInfo PI_location = new PropertyInfo();
    @Ignore
    private static final PropertyInfo PI_summary = new PropertyInfo();
    @Ignore
    private static final PropertyInfo PI_status = new PropertyInfo();
    @Ignore
    private static final PropertyInfo PI_content = new PropertyInfo();
    @Ignore
    private static PropertyInfo[] PI_PROP_ARRAY =
            {
                    PI_id,
                    PI_eventCode,
                    PI_eventType,
                    PI_eventTime,
                    PI_userNickname,
                    PI_userSurname1,
                    PI_userSurname2,
                    PI_userFirstName,
                    PI_userPhoto,
                    PI_location,
                    PI_summary,
                    PI_status,
                    PI_content
            };

    public SWADNotification(long id, long eventCode, String eventType, long eventTime, String userNickname, String userSurname1, String userSurname2, String userFirstName, String userPhoto, String location, String summary, int status, String content, boolean seenLocal, boolean seenRemote) {
        super(id);
        this.eventCode = eventCode;
        this.eventType = eventType;
        this.eventTime = eventTime;
        this.userNickname = userNickname;
        this.userSurname1 = userSurname1;
        this.userSurname2 = userSurname2;
        this.userFirstName = userFirstName;
        this.userPhoto = userPhoto;
        this.location = location;
        this.summary = summary;
        this.status = status;
        this.content = content;
        this.seenLocal = seenLocal;
        this.seenRemote = seenRemote;
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
                object = eventCode;
                break;
            case 2:
                object = eventType;
                break;
            case 3:
                object = eventTime;
                break;
            case 4:
                object = userNickname;
                break;
            case 5:
                object = userSurname1;
                break;
            case 6:
                object = userSurname2;
                break;
            case 7:
                object = userFirstName;
                break;
            case 8:
                object = userPhoto;
                break;
            case 9:
                object = location;
                break;
            case 10:
                object = summary;
                break;
            case 11:
                object = status;
                break;
            case 12:
                object = content;
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
                propertyInfo.name = "id";
                break;
            case 1:
                propertyInfo.type = PropertyInfo.LONG_CLASS;
                propertyInfo.name = "eventCode";
                break;
            case 2:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "eventType";
                break;
            case 3:
                propertyInfo.type = PropertyInfo.LONG_CLASS;
                propertyInfo.name = "eventTime";
                break;
            case 4:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "userNickname";
                break;
            case 5:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "userSurname1";
                break;
            case 6:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "userSurname2";
                break;
            case 7:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "userFirstName";
                break;
            case 8:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "userPhoto";
                break;
            case 9:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "location";
                break;
            case 10:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "summary";
                break;
            case 11:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "status";
                break;
            case 12:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "content";
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
                eventCode = (Long) obj;
                break;
            case 2:
                eventType = (String) obj;
                break;
            case 3:
                eventTime = (Long) obj;
                break;
            case 4:
                userNickname = (String) obj;
                break;
            case 5:
                userSurname1 = (String) obj;
                break;
            case 6:
                userSurname2 = (String) obj;
                break;
            case 7:
                userFirstName = (String) obj;
                break;
            case 8:
                userPhoto = (String) obj;
                break;
            case 9:
                location = (String) obj;
                break;
            case 10:
                summary = (String) obj;
                break;
            case 11:
                status = (Integer) obj;
                break;
            case 12:
                content = (String) obj;
                break;
        }
    }

}
