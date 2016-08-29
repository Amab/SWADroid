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

import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

/**
 * Class for store a notification
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com> *
 */
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
    private static final PropertyInfo PI_id = new PropertyInfo();
    private static final PropertyInfo PI_eventCode = new PropertyInfo();
    private static final PropertyInfo PI_eventType = new PropertyInfo();
    private static final PropertyInfo PI_eventTime = new PropertyInfo();
    private static final PropertyInfo PI_userNickname = new PropertyInfo();
    private static final PropertyInfo PI_userSurname1 = new PropertyInfo();
    private static final PropertyInfo PI_userSurname2 = new PropertyInfo();
    private static final PropertyInfo PI_userFirstName = new PropertyInfo();
    private static final PropertyInfo PI_userPhoto = new PropertyInfo();
    private static final PropertyInfo PI_location = new PropertyInfo();
    private static final PropertyInfo PI_summary = new PropertyInfo();
    private static final PropertyInfo PI_status = new PropertyInfo();
    private static final PropertyInfo PI_content = new PropertyInfo();
    @SuppressWarnings("unused")
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

    /**
     * @param id            Notification id (unique)
     * @param eventCode     Event id (not unique)
     * @param eventType     Notification type
     * @param eventTime     Notification timestamp
     * @param userNickname  Sender nickname
     * @param userSurname1  Sender first surname
     * @param userSurname2  Sender second surname
     * @param userFirstName Sender first name
     * @param userPhoto     Full URL path of the sender photo
     * @param location      Notification location
     * @param summary       Notification summary
     * @param status        Notification status
     * @param seenLocal     Notification has been seen locally
     * @param seenRemote    Notification has been seen in SWAD
     */
    public SWADNotification(long id, long eventCode, String eventType, long eventTime,
                            String userNickname, String userSurname1, String userSurname2,
                            String userFirstName, String userPhoto, String location, String summary,
                            int status, String content, boolean seenLocal, boolean seenRemote) {

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

    /**
     * Gets event id
     *
     * @return Event id
     */
    public long getEventCode() {
        return eventCode;
    }

    /**
     * Sets event id
     *
     * @param eventCode Event id
     */
    public void setEventCode(long eventCode) {
        this.eventCode = eventCode;
    }

    /**
     * Gets notification type
     *
     * @return Notification type
     */
    public String getEventType() {
        return eventType;
    }

    /**
     * Sets notification type
     *
     * @param eventType Notification type
     */
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    /**
     * Gets notification timestamp
     *
     * @return Notification timestamp
     */
    public long getEventTime() {
        return eventTime;
    }

    /**
     * Sets notification timestamp
     *
     * @param eventTime Notification timestamp
     */
    public void setEventTime(int eventTime) {
        this.eventTime = eventTime;
    }

    /**
     * Gets sender nickname
     *
     * @return Sender nickname
     */
    public String getUserNickname() {
        return userNickname;
    }

    /**
     * Sets sender nickname
     *
     * @param userNickname Sender nickname
     */
    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    /**
     * Gets sender first surname
     *
     * @return Sender first surname
     */
    public String getUserSurname1() {
        return userSurname1;
    }

    /**
     * Sets sender first surname
     *
     * @param userSurname1 Sender first surname
     */
    public void setUserSurname1(String userSurname1) {
        this.userSurname1 = userSurname1;
    }

    /**
     * Gets sender second surname
     *
     * @return Sender second surname
     */
    public String getUserSurname2() {
        return userSurname2;
    }

    /**
     * Sets sender second surname
     *
     * @param userSurname2 Sender second surname
     */
    public void setUserSurname2(String userSurname2) {
        this.userSurname2 = userSurname2;
    }

    /**
     * Gets sender first name
     *
     * @return Sender first name
     */
    public String getUserFirstName() {
        return userFirstName;
    }

    /**
     * Sets sender first name
     *
     * @param userFirstName Sender first name
     */
    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    /**
     * Gets full URL path of the sender photo
     *
     * @return Full URL path of the sender photo
     */
    public String getUserPhoto() {
        return userPhoto;
    }

    /**
     * Sets full URL path of the sender photo
     *
     * @param userPhoto Full URL path of the sender photo
     */
    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    /**
     * Gets notification location
     *
     * @return Notification location
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets notification location
     *
     * @param location Notification location
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Gets notification summary
     *
     * @return Notification summary
     */
    public String getSummary() {
        return summary;
    }

    /**
     * Sets notification summary
     *
     * @param summary Notification summary
     */
    public void setSummary(String summary) {
        this.summary = summary;
    }

    /**
     * Gets notification status
     *
     * @return Notification status
     */
    public int getStatus() {
        return status;
    }

    /**
     * Sets notification status
     *
     * @param status Notification status
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * Gets notification content
     *
     * @return Notification content
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets notification content
     *
     * @param content notification content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Gets notification seenLocal attribute
     * 
	 * @return true if notification has been seen locally
	 * 		   false otherwise
	 */
	public boolean isSeenLocal() {
		return seenLocal;
	}

	/**
	 * Sets notification seenLocal attribute
	 * 
	 * @param seenLocal true if notification has been seen locally
	 * 		   			false otherwise
	 */
	public void setSeenLocal(boolean seenLocal) {
		this.seenLocal = seenLocal;
	}

	/**
	 * Gets notification seenRemote attribute
     * 
	 * @return true if notification has been seen in SWAD
	 * 		   false otherwise
	 */
	public boolean isSeenRemote() {
		return seenRemote;
	}

	/**
	 * Sets notification seenRemote attribute
	 * 
	 * @param seenRemote true if notification has been seen in SWAD
	 * 		   			 false otherwise
	 */
	public void setSeenRemote(boolean seenRemote) {
		this.seenRemote = seenRemote;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SWADNotification)) return false;
        if (!super.equals(o)) return false;

        SWADNotification that = (SWADNotification) o;

        if (eventCode != that.eventCode) return false;
        if (eventTime != that.eventTime) return false;
        if (status != that.status) return false;
        if (seenLocal != that.seenLocal) return false;
        if (seenRemote != that.seenRemote) return false;
        if (eventType != null ? !eventType.equals(that.eventType) : that.eventType != null)
            return false;
        if (userNickname != null ? !userNickname.equals(that.userNickname) : that.userNickname != null)
            return false;
        if (userSurname1 != null ? !userSurname1.equals(that.userSurname1) : that.userSurname1 != null)
            return false;
        if (userSurname2 != null ? !userSurname2.equals(that.userSurname2) : that.userSurname2 != null)
            return false;
        if (userFirstName != null ? !userFirstName.equals(that.userFirstName) : that.userFirstName != null)
            return false;
        if (userPhoto != null ? !userPhoto.equals(that.userPhoto) : that.userPhoto != null)
            return false;
        if (location != null ? !location.equals(that.location) : that.location != null)
            return false;
        if (summary != null ? !summary.equals(that.summary) : that.summary != null) return false;
        return content != null ? content.equals(that.content) : that.content == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (int) (eventCode ^ (eventCode >>> 32));
        result = 31 * result + (eventType != null ? eventType.hashCode() : 0);
        result = 31 * result + (int) (eventTime ^ (eventTime >>> 32));
        result = 31 * result + (userNickname != null ? userNickname.hashCode() : 0);
        result = 31 * result + (userSurname1 != null ? userSurname1.hashCode() : 0);
        result = 31 * result + (userSurname2 != null ? userSurname2.hashCode() : 0);
        result = 31 * result + (userFirstName != null ? userFirstName.hashCode() : 0);
        result = 31 * result + (userPhoto != null ? userPhoto.hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + (summary != null ? summary.hashCode() : 0);
        result = 31 * result + status;
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + (seenLocal ? 1 : 0);
        result = 31 * result + (seenRemote ? 1 : 0);
        return result;
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

    @Override
    public String toString() {
        return "SWADNotification{" +
                "eventCode=" + eventCode +
                ", eventType='" + eventType + '\'' +
                ", eventTime=" + eventTime +
                ", userNickname='" + userNickname + '\'' +
                ", userSurname1='" + userSurname1 + '\'' +
                ", userSurname2='" + userSurname2 + '\'' +
                ", userFirstName='" + userFirstName + '\'' +
                ", userPhoto='" + userPhoto + '\'' +
                ", location='" + location + '\'' +
                ", summary='" + summary + '\'' +
                ", status=" + status +
                ", content='" + content + '\'' +
                ", seenLocal=" + seenLocal +
                ", seenRemote=" + seenRemote +
                "} " + super.toString();
    }
}
