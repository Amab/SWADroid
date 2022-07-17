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
import androidx.room.TypeConverters;

import org.ksoap2.serialization.PropertyInfo;

import java.util.Calendar;
import java.util.Hashtable;

import es.ugr.swad.swadroid.converters.CalendarConverter;
import lombok.Data;

/**
 * User data.
 *
 * @author Juan Miguel Boyero Corral <swadroid@gmail.com>
 * @author Antonio Aguilera Malagon <aguilerin@gmail.com>
 */
@Data
@TypeConverters({CalendarConverter.class})
@Entity(indices = {@Index("userNickname")})
public class User extends Model {
    /**
     * Webservices session key.
     */
    private String wsKey;
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
     * User birthday.
     */
    private Calendar userBirthday;
    /**
     * User role. 1:guest 2: student 3: teacher
     */
    private int userRole;

    @Ignore
    private static final PropertyInfo PI_wsKey = new PropertyInfo();
    @Ignore
    private static final PropertyInfo PI_userID = new PropertyInfo();
    @Ignore
    private static final PropertyInfo PI_userNickname = new PropertyInfo();
    @Ignore
    private static final PropertyInfo PI_userSurname1 = new PropertyInfo();
    @Ignore
    private static final PropertyInfo PI_userSurname2 = new PropertyInfo();
    @Ignore
    private static final PropertyInfo PI_userFirstname = new PropertyInfo();
    @Ignore
    private static final PropertyInfo PI_userPhoto = new PropertyInfo();
    @Ignore
    private static final PropertyInfo PI_userBirthday = new PropertyInfo();
    @Ignore
    private static final PropertyInfo PI_userRole = new PropertyInfo();
    @Ignore
    private static PropertyInfo[] PI_PROP_ARRAY = {
            PI_wsKey,
            PI_userID,
            PI_userNickname,
            PI_userSurname1,
            PI_userSurname2,
            PI_userFirstname,
            PI_userPhoto,
            PI_userBirthday,
            PI_userRole
    };

    @Ignore
    public User() {
        super(-1);
    }

    /**
     * Constructor.
     *
     * @param id            User code.
     * @param wsKey         Webservices session key.
     * @param userID        User identifier.
     * @param userNickname  User nickname.
     * @param userSurname1  User first surname.
     * @param userSurname2  User last surname.
     * @param userFirstname User name.
     * @param userPhoto     Full path where user's picture is stored.
     * @param userBirthday  User birthday.
     * @param userRole      User role.
     */
    public User(long id, String wsKey, String userID, String userNickname, String userSurname1,
                String userSurname2, String userFirstname, String userPhoto, String userBirthday,
                int userRole) {
        super(id);
        this.wsKey = wsKey;
        this.userID = userID;
        this.userNickname = userNickname;
        this.userSurname1 = userSurname1;
        this.userSurname2 = userSurname2;
        this.userFirstname = userFirstname;
        this.userPhoto = userPhoto;
        setUserBirthday(userBirthday);
        this.userRole = userRole;
    }

    public User(long id, String wsKey, String userID, String userNickname, String userSurname1,
                String userSurname2, String userFirstname, String userPhoto, Calendar userBirthday,
                int userRole) {
        super(id);
        this.wsKey = wsKey;
        this.userID = userID;
        this.userNickname = userNickname;
        this.userSurname1 = userSurname1;
        this.userSurname2 = userSurname2;
        this.userFirstname = userFirstname;
        this.userPhoto = userPhoto;
        this.userBirthday = userBirthday;
        this.userRole = userRole;
    }

    public void setUserBirthday(Calendar userBirthday) {
        this.userBirthday = userBirthday;
    }

    /**
     * Sets user birthday.
     *
     * @param userBirthday User birthday.
     */
    public void setUserBirthday(String userBirthday) {
        this.userBirthday = CalendarConverter.toCalendar(userBirthday);
    }

    public Object getProperty(int param) {
        Object object = null;
        switch (param) {
            case 0:
                object = wsKey;
                break;
            case 1:
                object = userID;
                break;
            case 2:
                object = userNickname;
                break;
            case 3:
                object = userSurname1;
                break;
            case 4:
                object = userSurname2;
                break;
            case 5:
                object = userFirstname;
                break;
            case 6:
                object = userPhoto;
                break;
            case 7:
                object = userBirthday;
                break;
            case 8:
                object = userRole;
                break;
        }

        return object;
    }

    public int getPropertyCount() {
        return PI_PROP_ARRAY.length;
    }

    public void getPropertyInfo(int param, @SuppressWarnings("rawtypes") Hashtable arg1, PropertyInfo propertyInfo) {
        switch (param) {
            case 0:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "wsKey";
                break;
            case 1:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "userID";
                break;
            case 2:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "userNickname";
                break;
            case 3:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "userSurname1";
                break;
            case 4:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "userSurname2";
                break;
            case 5:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "userFirstname";
                break;
            case 6:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "userPhoto";
                break;
            case 7:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "userBirthday";
                break;
            case 8:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "userRole";
                break;
        }
    }

    public void setProperty(int param, Object obj) {
        switch (param) {
            case 0:
                wsKey = (String) obj;
                break;
            case 1:
                userID = (String) obj;
                break;
            case 2:
                userNickname = (String) obj;
                break;
            case 3:
                userSurname1 = (String) obj;
                break;
            case 4:
                userSurname2 = (String) obj;
                break;
            case 5:
                userFirstname = (String) obj;
                break;
            case 6:
                userPhoto = (String) obj;
                break;
            case 7:
                userBirthday = (Calendar) obj;
                break;
            case 8:
                userRole = (Integer) obj;
                break;
        }
    }

}
