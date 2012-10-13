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

import org.ksoap2.serialization.PropertyInfo;

/**
 * User data.
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 * @author Antonio Aguilera Malagon <aguilerin@gmail.com>
 */
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
	 * User role. 1:guest 2: student 3: teacher
	 */
	private int userRole;

	private static PropertyInfo PI_wsKey = new PropertyInfo();
	private static PropertyInfo PI_userID = new PropertyInfo();
	private static PropertyInfo PI_userNickname = new PropertyInfo();
	private static PropertyInfo PI_userSurname1 = new PropertyInfo();
	private static PropertyInfo PI_userSurname2 = new PropertyInfo();
	private static PropertyInfo PI_userFirstname = new PropertyInfo();
	private static PropertyInfo PI_userPhoto = new PropertyInfo();
	private static PropertyInfo PI_userRole = new PropertyInfo();

	@SuppressWarnings("unused")
	private static PropertyInfo[] PI_PROP_ARRAY = {
		PI_wsKey,
		PI_userID,
		PI_userNickname,
		PI_userSurname1,
		PI_userSurname2,
		PI_userFirstname,
		PI_userPhoto,
		PI_userRole
	};

	/**
	 * Constructor.
	 * @param id User code.
	 * @param wsKey Webservices session key.
	 * @param userID User identifier.
	 * @param userNickname User nickname.
	 * @param userSurname1 User first surname.
	 * @param userSurname2 User last surname.
	 * @param userFirstname User name.
	 * @param userPhoto Full path where user's picture is stored.
	 * @param userRole User role.
	 */
	public User(long id, String wsKey, String userID, String userNickname, String userSurname1, 
			String userSurname2, String userFirstname, String userPhoto, int userRole) {
		super(id);
		this.wsKey			= wsKey;
		this.userID			= userID;
		this.userNickname	= userNickname;
		this.userSurname1	= userSurname1;
		this.userSurname2	= userSurname2;
		this.userFirstname	= userFirstname;
		this.userPhoto		= userPhoto;
		this.userRole		= userRole;
	}

	/**
	 * Gets user name.
	 * @return User name.
	 */
	public String getUserFirstname() {
		return userFirstname;
	}

	/**
	 * Sets user name.
	 * @param userFirstname User name.
	 */
	public void setUserFirstname(String userFirstname) {
		this.userFirstname = userFirstname;
	}

	/**
	 * Gets user identifier.
	 * @return User identifier.
	 */
	public String getUserID() {
		return userID;
	}

	/**
	 * Sets user identifier.
	 * @param userID User identifier.
	 */
	public void setUserID(String userID) {
		this.userID = userID;
	}

	/**
	 * Gets user nickname.
	 * @return User nickname.
	 */
	public String getUserNickname() {
		return userNickname;
	}

	/**
	 * Sets user nickname.
	 * @param userNickname User nickname.
	 */
	public void setUserNickname(String userNickname) {
		this.userID = userNickname;
	}

	/**
	 * Gets user first surname.
	 * @return User first surname.
	 */
	public String getUserSurname1() {
		return userSurname1;
	}

	/**
	 * Sets user first surname.
	 * @param userSurname1 User first surname.
	 */
	public void setUserSurname1(String userSurname1) {
		this.userSurname1 = userSurname1;
	}

	/**
	 * Gets user last surname.
	 * @return User last surname.
	 */
	public String getUserSurname2() {
		return userSurname2;
	}

	/**
	 * Sets user last surname.
	 * @param userSurname2 User last surname.
	 */
	public void setUserSurname2(String userSurname2) {
		this.userSurname2 = userSurname2;
	}

	/**
	 * Gets Full path where user's picture is stored.
	 * @return the userPhoto
	 */
	public String getUserPhoto() {
		return userPhoto;
	}

	/**
	 * Gets File name where user's picture is stored.
	 * @return the userPhoto
	 */
	public String getPhotoFileName() {
		if (userPhoto == null || userPhoto == "")
			return null;
		else
			return userPhoto.substring(userPhoto.lastIndexOf('/')+1);
	}

	/**
	 * Sets Full path where user's picture is stored.
	 * @param userPhoto the userPhoto to set
	 */
	public void setuserPhoto(String userPhoto) {
		this.userPhoto = userPhoto;
	}

	/**
	 * Gets User role.
	 * @return User role.
	 */
	public int getUserRole() {
		return userRole;
	}

	/**
	 * Sets User role.
	 * @param userRole User role.
	 */
	//TODO check userRole is 1,2,3 
	public void setUserRole(int userRole) {
		this.userRole = userRole;
	}

	/**
	 * Gets Webservices session key.
	 * @return Webservices session key.
	 */
	public String getWsKey() {
		return wsKey;
	}

	/**
	 * Sets Webservices session key.
	 * @param wsKey Webservices session key.
	 */
	public void setWsKey(String wsKey) {
		this.wsKey = wsKey;
	}

	public Object getProperty(int param) {
		Object object = null;
		switch(param)
		{
		case 0 : object = wsKey;			break;
		case 1 : object = userID;			break;
		case 2 : object = userNickname;		break;
		case 3 : object = userSurname1;		break;
		case 4 : object = userSurname2;		break;
		case 5 : object = userFirstname;	break;
		case 6 : object = userPhoto;		break;
		case 7 : object = userRole;			break;
		}

		return object;
	}

	public int getPropertyCount() {		
		return 8;
	}

	public void getPropertyInfo(int param, @SuppressWarnings("rawtypes") Hashtable arg1, PropertyInfo propertyInfo) {
		switch(param){
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
			propertyInfo.type = PropertyInfo.INTEGER_CLASS;
			propertyInfo.name = "userRole";
			break;
		}
	}

	public void setProperty(int param, Object obj) {
		switch(param) {
		case 0 : wsKey			= (String) obj; break;
		case 1 : userID			= (String) obj; break;
		case 2 : userNickname	= (String) obj; break;
		case 3 : userSurname1	= (String) obj; break;
		case 4 : userSurname2	= (String) obj; break;
		case 5 : userFirstname	= (String) obj; break;
		case 6 : userPhoto		= (String) obj; break;
		case 7 : userRole		= (Integer) obj; break;
		}    
	}

}
