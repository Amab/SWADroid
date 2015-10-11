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

import com.android.dataframework.DataFramework;

import java.util.ArrayList;


public class Table {
	
	private String mName;
	private String mToString;
	private ArrayList<Field> mFields = new ArrayList<Field>();
	private int mNewInVersion;
	private boolean mBackup;

    /**
     * Constructor - Contiene la informacion de una tabla de la base de datos
     * 
     * @param name nombre de la tabla 
     */
	
	public Table (String name) {
		mName = name;
		mBackup = true;
		mToString = "";
		mNewInVersion = 1;
	}

    /**
     * Devuelve la sentencia SQL para crear la tabla
     * 
     * @return sql
     */
	
	public String getSQLCreateTable () 
	{
		String out = "create table " + mName + " (_id integer primary key";
		
		ArrayList<Field> fields = mFields;
		int fieldCount = fields.size();
		
		for (int i=0; i < fieldCount; i++) 
		{
			Field f = fields.get(i);
			
			if (f.getType().equals("multilanguage")) {
				 ArrayList<String> langs = DataFramework.getInstance().getLanguages();
				 for (int j=0; j<langs.size(); j++) {
					out += ", " + f.getName() + "_" + langs.get(j);
					out += " " + f.getSQLType();
					if (f.isObligatory()){
						out += " not null";
					}
					if (f.getTextDefault()!=null) {
						out += " DEFAULT '" + f.getTextDefault() + "'";
					}
				 }
			} else {
				out += ", " + f.getName();
				out += " " + f.getSQLType();
				if (f.isObligatory()){
					out += " not null";
				}
				if (f.getTextDefault()!=null) {
					out += " DEFAULT '" + f.getTextDefault() + "'";
				}
			}
		}
		out += ");";
		return out;
	}
	
	/**
     * Devuelve la sentencia SQL para modificar el campo de la tabla
     * 
     * @param field Campo a tratar
     * @return sql
     */
	
	public String getSQLAddField (Field field) {
		if (field!=null) {
			String out = "";
			if (field.getType().equals("multilanguage")) {
				ArrayList<String> langs = DataFramework.getInstance().getLanguages();
				for (int j=0; j<langs.size(); j++) {
					out += "ALTER TABLE " + mName + " ADD " + field.getName() + "_" + langs.get(j) + field.getSQLAddField() + ";";
				}
			} else {
				out = "ALTER TABLE " + mName + " ADD " + field.getName() +  " " + field.getSQLAddField();
			}
			return out;
		}
		return null;
	}
	
    /**
     * Devuelve la sentecia SQL para borrar la tabla
     * 
     * @return sql
     */
	
	
	public String getSQLDeleteTable () {
		String out = "DROP TABLE IF EXISTS " + mName;
		return out;
	}
	
    /**
     * Devuelve una lista con los campos de la tabla
     * 
     * @return lista de objetos Field
     */
	
	
	public ArrayList<Field> getFields() {
		return mFields;
	}
	
    /**
     * Devuelve un array con los nombres de los campos de la tabla
     * 
     * @return array con los nombres
     */
		
	public String[] getFieldsToArray() 
	{
		ArrayList<Field> fields = mFields;
		int fieldCount = fields.size();

		ArrayList<String> aux = new ArrayList<String>();
		
		aux.add(DataFramework.KEY_ID);
		for (int i = 0; i < fieldCount; i++) {
			if (fields.get(i).getType().equals("multilanguage")) {
				ArrayList<String> langs = DataFramework.getInstance().getLanguages();
				for (int j=0; j<langs.size(); j++) {
					aux.add(fields.get(i).getName() + "_" + langs.get(j));
				}
			} else {
				aux.add(fields.get(i).getName());
			}
		}
		
		String[] out = new String[aux.size()];
		
		for (int i = 0; i < aux.size(); i++) {
			out[i] = aux.get(i);
		}
		
		return out;
	}
	
    /**
     * Devuelve el objeto Field del nombre de un campo
     * 
     * @param name nombre del campo
     * @return objeto Field
     */
	
	public Field getField(String name) {
		Field res = null;
		ArrayList<Field> fields = mFields;
		int fieldCount = fields.size();
		
		for (int i=0; i<fieldCount; i++) 
		{
			Field f = fields.get(i);
			if (f.getName().equals(name)){
				res = f;
			}
		}
		if(res==null){
			throw new NullPointerException("No exist field '"+name+"' in table '"+mName+"'");
		}
		return res;
	}
	
    /**
     * Devuelve el nombre de la tabla
     * 
     * @return nombre
     */
	
	public String getName() {
		return mName;
	}
	
	/**
     * Establece el nombre de la tabla
     * 
     * @param name nombre
     */

	public void setName(String name) {
		this.mName = name;
	}
	
	/**
     * Agrega un campo a la tabla
     * 
     * @param field Objeto Field
     */
	
	public void addField(Field field) {
		mFields.add(field);
	}
	
	/**
     * Devuelve el formato del toString()
     * 
     * @return formato
     */

	public String getToString() {
		return mToString;
	}

	/**
     * Establece el formato del toString()
     * 
     * @param toString formato toString()
     */
	
	public void setToString(String toString) {
		this.mToString = toString;
	}
	
    /**
     * Devuelve la version en la que ha sido creado
     * 
     * @return version
     */
	
	public int getNewInVersion() {
		return mNewInVersion;
	}
	
    /**
     * Establece la version en la que ha sido creado
     * 
     * @return version
     */

	public void setNewInVersion(int newInVersion) {
		this.mNewInVersion = newInVersion;
	}
	
    /**
     * Devuelve si se tiene que hacer backups de esta table
     * 
     * @return boolean
     */

	public boolean isBackup() {
		return mBackup;
	}
	
    /**
     * Establece si se tiene que hacer backups de esta table
     * 
     * @param backup
     */

	public void setBackup(boolean backup) {
		this.mBackup = backup;
	}
	
}