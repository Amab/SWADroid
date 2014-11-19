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

import android.util.Log;

import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

/**
 * User attendance data.
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class UserAttendance extends Model implements Comparable<UserAttendance> {
    /**
     * User identifier.
     */
    private String userID;
    /**
     * User nickname.
     */
    private String userNickname;
    /**
     * User first surname.
     */
    private String userSurname1;
    /**
     * User last surname.
     */
    private String userSurname2;
    /**
     * User name.
     */
    private String userFirstname;
    /**
     * Full path where user's picture is stored.
     */
    private String userPhoto;
    /**
     * Flag for indicate if the user is present in the attendance.
     */
    private boolean userPresent;

    private static final PropertyInfo PI_userID = new PropertyInfo();
    private static final PropertyInfo PI_userNickname = new PropertyInfo();
    private static final PropertyInfo PI_userSurname1 = new PropertyInfo();
    private static final PropertyInfo PI_userSurname2 = new PropertyInfo();
    private static final PropertyInfo PI_userFirstname = new PropertyInfo();
    private static final PropertyInfo PI_userPhoto = new PropertyInfo();
    private static final PropertyInfo PI_userPresent = new PropertyInfo();

    @SuppressWarnings("unused")
    private static PropertyInfo[] PI_PROP_ARRAY = {
            PI_userID,
            PI_userNickname,
            PI_userSurname1,
            PI_userSurname2,
            PI_userFirstname,
            PI_userPhoto,
            PI_userPresent
    };

    public UserAttendance(long id) {
        super(id);
    }

    /**
     * Constructor.
     *
     * @param id            User code.
     * @param userID        User identifier.
     * @param userNickname  User nickname.
     * @param userSurname1  User first surname.
     * @param userSurname2  User last surname.
     * @param userFirstname User name.
     * @param userPhoto     Full path where user's picture is stored.
     * @param userPresent   Flag for indicate if the user is present in the attendance.
     */
    public UserAttendance(long id, String userID, String userNickname, String userSurname1,
                          String userSurname2, String userFirstname, String userPhoto, boolean userPresent) {
        super(id);
        this.userID = userID;
        this.userNickname = userNickname;
        this.userSurname1 = userSurname1;
        this.userSurname2 = userSurname2;
        this.userFirstname = userFirstname;
        this.userPhoto = userPhoto;
        this.userPresent = userPresent;
    }



    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
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

    public String getUserFirstname() {
        return userFirstname;
    }

    public void setUserFirstname(String userFirstname) {
        this.userFirstname = userFirstname;
    }

    public String getFullName() {
        return this.userSurname1 + " " + this.userSurname2 + ", "
                + this.userFirstname;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public boolean isUserPresent() {
        return userPresent;
    }

    public void setUserPresent(boolean userPresent) {
        this.userPresent = userPresent;
    }

    public Object getProperty(int param) {
        Object object = null;
        switch (param) {
            case 0:
                object = userID;
                break;
            case 1:
                object = userNickname;
                break;
            case 2:
                object = userSurname1;
                break;
            case 3:
                object = userSurname2;
                break;
            case 4:
                object = userFirstname;
                break;
            case 5:
                object = userPhoto;
                break;
            case 6:
                object = userPresent;
                break;
        }

        return object;
    }

    public int getPropertyCount() { return PI_PROP_ARRAY.length; }

    public void getPropertyInfo(int param, @SuppressWarnings("rawtypes") Hashtable arg1, PropertyInfo propertyInfo) {
        switch (param) {
            case 0:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "userID";
                break;
            case 1:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "userNickname";
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
                propertyInfo.name = "userFirstname";
                break;
            case 5:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "userPhoto";
                break;
            case 6:
                propertyInfo.type = PropertyInfo.BOOLEAN_CLASS;
                propertyInfo.name = "userPresent";
                break;
        }
    }

    public void setProperty(int param, Object obj) {
        switch (param) {
            case 0:
                userID = (String) obj;
                break;
            case 1:
                userNickname = (String) obj;
                break;
            case 2:
                userSurname1 = (String) obj;
                break;
            case 3:
                userSurname2 = (String) obj;
                break;
            case 4:
                userFirstname = (String) obj;
                break;
            case 5:
                userPhoto = (String) obj;
                break;
            case 6:
                userPresent = (Boolean) obj;
                break;
        }
    }

    @Override
    public int compareTo(UserAttendance another) {
        int cmpFullName = this.getFullName().compareToIgnoreCase(another.getFullName());

        if (cmpFullName == 0) {
            return cmpFullName;
        } else {
            return this.getUserID().compareToIgnoreCase(another.getUserID());
        }
    }

    @Override
    public boolean equals(Object o) {
        UserAttendance another = (UserAttendance) o;
        String thisNickName = this.getUserNickname();
        String anotherNickName = "@" + another.getUserNickname();

        return (thisNickName.equals(anotherNickName))
                || (getUserID().equals(another.getUserID()));
    }

    @Override
    public String toString() {
        return "UserAttendance{" +
                "userCode='" + super.getId() + '\'' +
                "userID='" + userID + '\'' +
                ", userNickname='" + userNickname + '\'' +
                ", userSurname1='" + userSurname1 + '\'' +
                ", userSurname2='" + userSurname2 + '\'' +
                ", userFirstname='" + userFirstname + '\'' +
                ", userPhoto='" + userPhoto + '\'' +
                ", userPresent=" + userPresent +
                '}';
    }
}
