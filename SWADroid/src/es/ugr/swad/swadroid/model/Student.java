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
 * Class for store a student
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class Student extends Model {
	/**
	 * Student's dni
	 */
	private String dni;
	/**
	 * Student's first name
	 */
	private String firstName;
	/**
	 * Student's first surname
	 */
	private String surname1;
	/**
	 * Student's second surname
	 */
	private String surname2;
	
	/**
	 * Constructor
	 * @param dni Student's dni
	 * @param firstName Student's first name
	 * @param surname1 Student's first surname
	 * @param surname2 Student's second surname
	 */
	public Student(int id, String dni, String firstName, String surname1,
			String surname2) {
		super(id);
		this.firstName = firstName;
		this.surname1 = surname1;
		this.surname2 = surname2;
	}

	/**
	 * Gets student's dni
	 * @return Student's dni
	 */
	public String getDni() {
		return dni;
	}

	/**
	 * Sets student's dni
	 * @param dni Student's dni
	 */
	public void setDni(String dni) {
		this.dni = dni;
	}

	/**
	 * Gets student's first name
	 * @return Student's first name
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * Sets student's first name
	 * @param Student's first name
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * Gets student's first surname
	 * @return Student's first surname
	 */
	public String getSurname1() {
		return surname1;
	}
	
	/**
	 * Sets student's first surname
	 * @param Student's first surname
	 */
	public void setSurname1(String surname1) {
		this.surname1 = surname1;
	}

	/**
	 * Gets student's second surname
	 * @return Student's second surname
	 */
	public String getSurname2() {
		return surname2;
	}
	
	/**
	 * Sets student's second surname
	 * @param Student's second surname
	 */
	public void setSurname2(String surname2) {
		this.surname2 = surname2;
	}

	public Object getProperty(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public int getPropertyCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void getPropertyInfo(int arg0, Hashtable arg1, PropertyInfo arg2) {
		// TODO Auto-generated method stub
		
	}

	public void setProperty(int arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}	
}
