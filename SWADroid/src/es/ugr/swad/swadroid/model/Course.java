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
 * Class for store a course
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 * @author Helena Rodriguez Gijon <hrgijon@gmail.com>
 */
public class Course extends Model {
	/**
	 * Course name
	 */
	private int userRole;
	private String shortName;
	private String fullName;
	private static PropertyInfo PI_id = new PropertyInfo();
	private static PropertyInfo PI_userRole = new PropertyInfo();
	private static PropertyInfo PI_shortName = new PropertyInfo();
	private static PropertyInfo PI_fullName = new PropertyInfo();

	private static PropertyInfo[] PI_PROP_ARRAY =
		{
		PI_id,
		PI_userRole,
		PI_shortName,
		PI_fullName
		};

	/**
	 * Constructor
	 * @param id Course identifier
	 * @param name Course name
	 */
	public Course(long id, int userRole, String shortName, String fullName) {
		super(id);
		this.userRole = userRole;
		this.shortName = shortName;
		this.fullName = fullName;
	}


	/**
	 * Gets user role inside the course
	 * @return user role (2 = student, 3 = teacher)
	 */
	public int getUserRole(){
		return userRole;
	}
	/**
	 * Gets short course name
	 * @return Short course name
	 */
	public String getShortName() {
		return shortName;
	}

	/**
	 * Sets short course name
	 * @param shortName short course name
	 */
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	/**
	 * Gets short course name
	 * @return Full course name
	 */
	public String getFullName() {
		return fullName;
	}

	/**
	 * Sets short course name
	 * @param shortName short course name
	 */
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}




	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + userRole;
		result = prime * result + ((shortName == null) ? 0 : shortName.hashCode());
		result = prime * result + ((fullName == null) ? 0 : fullName.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Course [getId()=" + getId() + " getUserRole()="+ getUserRole()+ "getShortName="+ shortName + "getFullName="+ fullName +"]";
	}

	/* (non-Javadoc)
	 * @see org.ksoap2.serialization.KvmSerializable#getProperty(int)
	 */
	public Object getProperty(int param) {
		Object object = null;
		switch(param)
		{
		case 0 : object = this.getId();break;
		case 1 : object = userRole;break;
		case 2 : object = shortName;break;
		case 3 : object = fullName;break;
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
		switch(param){
		case 0:
			propertyInfo.type = PropertyInfo.LONG_CLASS;
			propertyInfo.name = "id";
			break;   
		case 1:
			propertyInfo.type = PropertyInfo.INTEGER_CLASS;
			propertyInfo.name = "userRole";
			break;
		case 2:
			propertyInfo.type = PropertyInfo.STRING_CLASS;
			propertyInfo.name = "shortName";
			break;
		case 3:
			propertyInfo.type = PropertyInfo.STRING_CLASS;
			propertyInfo.name = "fullName";
			break;    

		}
	}

	/* (non-Javadoc)
	 * @see org.ksoap2.serialization.KvmSerializable#setProperty(int, java.lang.Object)
	 */
	public void setProperty(int param, Object obj) {
		switch(param)
		{
		case 0  : this.setId((Long)obj); break;
		case 1  : userRole = (Integer)obj; break;
		case 2  : shortName = (String)obj; break;
		case 3  : fullName = (String)obj; break;

		}    
	}

	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj)) return false;
		Course other = (Course) obj;
		if(userRole != other.getUserRole())	return false;
		if(shortName.compareTo(other.getShortName()) != 0) return false;
		if(fullName.compareTo(other.getFullName()) != 0) return false;
		return true; 
	}

}