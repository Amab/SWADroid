/*
 * Copyright (C) 2008  Javier Perez Pacheco y Javier Ros Moreno
 *
 * Android Data Framework: Trabajo con BD SQLite en Android
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Javier Perez Pacheco
 * Cadiz (Spain)
 * javi.pacheco@gmail.com
 * 
 * Javier Ros Moreno
 * jros@jros.org
 * 
 *
 */

package com.android.dataframework.core;


public class Field {
	
	private String mName;
	private String mType;
	private boolean mObligatory;
	private int mSize;
	private int mNewInVersion;
	private String mForeignTable;
	private String mTextDefault;
	
    /**
     * Constructor - Crea un campo mediante un nombre
     * 
     * @param name nombre del campo 
     */
	
	public Field (String name) {
		this.mName = name;
		this.mType = "string";
		this.mObligatory = false;
		this.mSize = -1;
		this.mForeignTable = "";
		this.mTextDefault = null;
		mNewInVersion = 1;
	}
	
    /**
     * Devuelve el tipo SQL
     * 
     * @return tipo sql
     */
	
	public String getSQLType() 
	{
		String type = mType;
		int size = mSize;
		String out = "";
		if ( (type.equals("text") || type.equals("multilanguage") || type.equals("string-identifier")) && (size>0) ) {
			out += " varchar(" + mSize + ")";
		} else if ( (type.equals("text") || type.equals("multilanguage") || type.equals("string-identifier")) && (size <= 0) ) {
			out += " text";
		} else if (type.equals("drawable-identifier")) {
			out += " varchar(128)";
		} else if (type.equals("foreign-key")) {
			out += " integer";
		} else {
			out += " " + type;
		}
		return out;
	}
	
    /**
     * Devuelve el campo ALTER TABLE
     * 
     * @return tipo sql
     */	
	public String getSQLAddField() 
	{	
		String sql = "";
		sql += " " + getSQLType();
		if (isObligatory())
			sql += " not null";
		if (getTextDefault()!=null) {
			sql += " DEFAULT '" + getTextDefault() + "'";
		}

		return sql;
	}

    /**
     * Devuelve el nombre
     * 
     * @return nombre
     */
	
	public String getName() {
		return mName;
	}
	
    /**
     * Establece el nombre
     * 
     * @param name nombre
     */

	public void setName(String name) {
		this.mName = name;
	}

    /**
     * Devuelve el tipo
     * 
     * @return tipo
     */
	
	public String getType() {
		return mType;
	}

    /**
     * Establece el tipo
     * 
     * @param type tipo 
     */
	
	public void setType(String type) {
		this.mType = type;
	}

    /**
     * Devuelve si el campo es obligatorio
     * 
     * @return true o false
     */
	
	public boolean isObligatory() {
		return mObligatory;
	}

    /**
     * Establece el campo obligatorio
     * 
     * @param true o false
     */
	
	public void setObligatory(boolean obligatory) {
		this.mObligatory = obligatory;
	}

    /**
     * Devuelve el tamano
     * 
     * @return tama�o
     */
	
	public int getSize() {
		return mSize;
	}

    /**
     * Establece el tamano
     * 
     * @param tamano
     */
	
	public void setSize(int size) {
		this.mSize = size;
	}

    /**
     * Devuelve la tabla a la que pertenece la clave foranea
     * 
     * @return tabla
     */
	
	public String getForeignTable() {
		return mForeignTable;
	}

    /**
     * Establece la tabla a la que pertenece la clave foranea
     * 
     * @param tabla
     */
	
	public void setForeignTable(String foreignTable) {
		this.mForeignTable = foreignTable;
	}

    /**
     * Devuelve la versi�n en la que ha sido creado
     * 
     * @return version
     */
	
	public int getNewInVersion() {
		return mNewInVersion;
	}
	
    /**
     * Establece la versi�n en la que ha sido creado
     * 
     * @return version
     */

	public void setNewInVersion(int newInVersion) {
		this.mNewInVersion = newInVersion;
	}

    /**
     * Devuelve el texto por defecto
     * 
     * @return texto por defecto
     */
	
	public String getTextDefault() {
		return mTextDefault;
	}

    /**
     * Establece el texto por defecto
     * 
     * @param texto por defecto
     */
	
	public void setTextDefault(String textDefault) {
		this.mTextDefault = textDefault;
	}

}