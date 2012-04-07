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

import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

/**
 * User data.
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class User implements KvmSerializable {
    /**
     * User code.
     */
    private static String userCode;
    
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
     * User role. 1:guest 2: student 3: teacher
     */
    private static int userRole;
    
    private static PropertyInfo PI_userCode = new PropertyInfo();
    private static PropertyInfo PI_wsKey = new PropertyInfo();
    private static PropertyInfo PI_userID = new PropertyInfo();
    private static PropertyInfo PI_userSurname1 = new PropertyInfo();
    private static PropertyInfo PI_userSurname2 = new PropertyInfo();
    private static PropertyInfo PI_userFirstName = new PropertyInfo();
    private static PropertyInfo PI_userRole = new PropertyInfo();
    @SuppressWarnings("unused")
	private static PropertyInfo[] PI_PROP_ARRAY =
    {
    	PI_userCode,
    	PI_wsKey,
    	PI_userID,
    	PI_userSurname1,
    	PI_userSurname2,
    	PI_userFirstName,
    	PI_userRole
    };

    /**
     * Empty constructor.
     */
    public User() {
    }

    /**
     * Constructor.
     * @param userCode User code.
     * @param userTypeCode Code of user type.
     * @param wsKey Webservices session key.
     * @param userID User identifier.
     * @param userSurname1 User first surname.
     * @param userSurname2 User last surname.
     * @param userFirstName User name.
     * @param userTypeName Name of user type.
     */
    public User(String userCode, String wsKey, String userID, String userSurname1, String userSurname2, String userFirstName, int userRole) {
        User.userCode = userCode;
        User.wsKey = wsKey;
        User.userID = userID;
        User.userSurname1 = userSurname1;
        User.userSurname2 = userSurname2;
        User.userFirstName = userFirstName;
        User.userRole = userRole;
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
     * @return User identifier.
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
    
    /**
     * Gets user role
     * @return user role 1:guest 2:student 3:teacher
     * */
    public static int getUserRole(){
    	return userRole;
    }
    /**
     * Sets user role
     * @param userRole 
     * */
    //TODO check userRole is 1,2,3 
    public static void setUserRole(int userRole){
    	User.userRole = userRole;
    }
    

	public Object getProperty(int param) {
		Object object = null;
        switch(param)
        {
            case 0 : object = userCode;break;
            case 1 : object = wsKey;break;
            case 2 : object = userID;break;
            case 3 : object = userSurname1;break;
            case 4 : object = userSurname2;break;
            case 5 : object = userFirstName;break;
            case 6 : object = userRole; break;
        }
        
        return object;
	}

	public int getPropertyCount() {		
		return 7;
	}

	public void getPropertyInfo(int param, @SuppressWarnings("rawtypes") Hashtable arg1, PropertyInfo propertyInfo) {
		switch(param){
        case 0:
            propertyInfo.type = PropertyInfo.STRING_CLASS;
            propertyInfo.name = "userCode";
            break;            
        case 1:
            propertyInfo.type = PropertyInfo.STRING_CLASS;
            propertyInfo.name = "wsKey";
            break;            
        case 2:
            propertyInfo.type = PropertyInfo.STRING_CLASS;
            propertyInfo.name = "userID";
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
            propertyInfo.name = "userFirstName";
            break;
        case 6:
            propertyInfo.type = PropertyInfo.INTEGER_CLASS;
            propertyInfo.name = "userRole";
            break;   
		}
	}

	public void setProperty(int param, Object obj) {
		switch(param)
		{
			case 0  : userCode     	= (String)obj; break;
			case 2  : wsKey     	= (String)obj; break;
			case 3  : userID     	= (String)obj; break;
			case 4  : userSurname1  = (String)obj; break;
			case 5  : userSurname2  = (String)obj; break;
			case 6  : userFirstName = (String)obj; break;
			case 8  : userRole		= (Integer)obj; break;
		}    
	}
	
}
