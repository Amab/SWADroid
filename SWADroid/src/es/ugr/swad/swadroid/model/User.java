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

/**
 * User data.
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class User {
    /**
     * User code.
     */
    private static String userCode;
    /**
     * Code of the user type.
     */
    private static String userTypeCode;
    /**
     * Webservices session key.
     */
    private static String wsKey;
    /**
     * User identifier.
     */
    private static String userID;
    /**
     * User first surname.
     */
    private static String userSurname1;
    /**
     * User last surname.
     */
    private static String userSurname2;
    /**
     * User name.
     */
    private static String userFirstName;
    /**
     * Name of the user type.
     */
    private static String userTypeName;

    /**
     * Empty constructor.
     */
    public User() {
    }

    /**
     * Constructor.
     * @param userCode User code.
     * @param userTypeCode Code of the user type.
     * @param wsKey Webservices session key.
     * @param userID User identifier.
     * @param userSurname1 User first surname.
     * @param userSurname2 User last surname.
     * @param userFirstName User name.
     * @param userTypeName Name of the user type.
     */
    public User(String userCode, String userTypeCode, String wsKey, String userID, String userSurname1, String userSurname2, String userFirstName, String userTypeName) {
        User.userCode = userCode;
        User.userTypeCode = userTypeCode;
        User.wsKey = wsKey;
        User.userID = userID;
        User.userSurname1 = userSurname1;
        User.userSurname2 = userSurname2;
        User.userFirstName = userFirstName;
        User.userTypeName = userTypeName;
    }

    /**
     * Gets user code.
     * @return User code.
     */
    public static String getUserCode() {
        return userCode;
    }

    /**
     * Sets user code.
     * @param userCode user code.
     */
    public static void setUserCode(String userCode) {
        User.userCode = userCode;
    }

    /**
     * Gets user name.
     * @return User name.
     */
    public static String getUserFirstName() {
        return userFirstName;
    }

    /**
     * Sets user name.
     * @param userFirstName User name.
     */
    public static void setUserFirstName(String userFirstName) {
        User.userFirstName = userFirstName;
    }

    /**
     * Gets user identifier.
     * @return Uer identifier.
     */
    public static String getUserID() {
        return userID;
    }

    /**
     * Sets user identifier.
     * @param userID User identifier.
     */
    public static void setUserID(String userID) {
        User.userID = userID;
    }

    /**
     * Gets user first surname.
     * @return User first surname.
     */
    public static String getUserSurname1() {
        return userSurname1;
    }

    /**
     * Sets user first surname.
     * @param userSurname1 User first surname.
     */
    public static void setUserSurname1(String userSurname1) {
        User.userSurname1 = userSurname1;
    }

    /**
     * Gets user last surname.
     * @return User last surname.
     */
    public static String getUserSurname2() {
        return userSurname2;
    }

    /**
     * Sets user last surname.
     * @param userSurname2 User last surname.
     */
    public static void setUserSurname2(String userSurname2) {
        User.userSurname2 = userSurname2;
    }

    /**
     * Gets Code of user type.
     * @return Code of user type.
     */
    public static String getUserTypeCode() {
        return userTypeCode;
    }

    /**
     * Sets Code of user type.
     * @param userTypeCode Code of user type.
     */
    public static void setUserTypeCode(String userTypeCode) {
        User.userTypeCode = userTypeCode;
    }

    /**
     * Gets Name of user type.
     * @return Name of user type.
     */
    public static String getUserTypeName() {
        return userTypeName;
    }

    /**
     * Sets Name of user type.
     * @param userTypeName Name of user type.
     */
    public static void setUserTypeName(String userTypeName) {
        User.userTypeName = userTypeName;
    }

    /**
     * Gets Webservices session key.
     * @return Webservices session key.
     */
    public static String getWsKey() {
        return wsKey;
    }

    /**
     * Sets Webservices session key.
     * @param wsKey Webservices session key.
     */
    public static void setWsKey(String wsKey) {
        User.wsKey = wsKey;
    }
}
