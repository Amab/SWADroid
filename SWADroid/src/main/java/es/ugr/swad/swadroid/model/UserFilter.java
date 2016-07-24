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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Locale;
import java.util.UUID;

/**
 * User data.
 *
 * @author Rubén Martín Hidalgo
 */
public class UserFilter {
    /**
     * User identifier.
     */
    private String userID;
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
    private int userPhoto;

    /**
     * Constructor.
     *
     * @param userID        User identifier.
     * @param userSurname1  User first surname.
     * @param userSurname2  User last surname.
     * @param userFirstname User name.
     * @param userPhoto     Full path where user's picture is stored.
     */
    public UserFilter(String userID, String userSurname1, String userSurname2, String userFirstname, int userPhoto) throws ParseException {
        this.userID = userID;
        this.userSurname1 = userSurname1;
        this.userSurname2 = userSurname2;
        this.userFirstname = userFirstname;
        this.userPhoto = userPhoto;
    }

    /**
     * Gets user name.
     *
     * @return User name.
     */
    public String getUserFirstname() {
        return userFirstname;
    }

    /**
     * Sets user name.
     *
     * @param userFirstname User name.
     */
    public void setUserFirstname(String userFirstname) {
        this.userFirstname = userFirstname;
    }

    /**
     * Gets user identifier.
     *
     * @return User identifier.
     */
    public String getUserID() {
        return userID;
    }

    /**
     * Sets user identifier.
     *
     * @param userID User identifier.
     */
    public void setUserID(String userID) {
        this.userID = userID;
    }

    /**
     * Gets user first surname.
     *
     * @return User first surname.
     */
    public String getUserSurname1() {
        return userSurname1;
    }

    /**
     * Sets user first surname.
     *
     * @param userSurname1 User first surname.
     */
    public void setUserSurname1(String userSurname1) {
        this.userSurname1 = userSurname1;
    }

    /**
     * Gets user last surname.
     *
     * @return User last surname.
     */
    public String getUserSurname2() {
        return userSurname2;
    }

    /**
     * Sets user last surname.
     *
     * @param userSurname2 User last surname.
     */
    public void setUserSurname2(String userSurname2) {
        this.userSurname2 = userSurname2;
    }

    /**
     * Gets Full path where user's picture is stored.
     *
     * @return the userPhoto
     */
    public int getUserPhoto() {
        return userPhoto;
    }

    /**
     * Sets Full path where user's picture is stored.
     *
     * @param userPhoto the userPhoto to set
     */
    public void setUserPhoto(int userPhoto) {
        this.userPhoto = userPhoto;
    }

}
