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

package com.android.dataframework;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.dataframework.core.DataFrameworkCore;
import com.android.dataframework.core.Table;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

public class DataFramework {
	
	public final static String VERSION = "1.02";
	public final static String KEY_ID = "_id";
	public static DataFramework mDataFramework = null;
	
	private int mOpenInstances = 0;

    private ArrayList<Table> mTables = new ArrayList<Table>();
    
    private DataFrameworkCore mCore;
    
    public void open(Context context, String namePackage) throws XmlPullParserException, IOException 
    {
    	if (mOpenInstances == 0){
			mCore.open(context, namePackage, mTables);
		}
    	mOpenInstances++;
    }
    
    public void close()
    {
    	if(mOpenInstances>0){
			mOpenInstances--;
		}
        if (mOpenInstances == 0){
			mCore.close();
		}
    }
    
    /**
     * Constructor privado, usamos el modelo Singletone.
     */
    private DataFramework() 
    {
    	mCore = new DataFrameworkCore();
    }

    /**
     * Devuelve la instancia de la clase, si no existe la crea.
     * 
     * @return DataFramework
     */
    public static DataFramework getInstance()
    {
    	if (mDataFramework == null)
    	{
    		mDataFramework = new DataFramework();
    	}
    	return mDataFramework;
    }
    
    
    /**
     * Devuelve un objeto Table del nombre de la tabla pasado como par�metro
     * 
     * @param table el nombre de la tabla
     * 
     * @return objeto Table
     */
	
	public Table getTable (String table) throws NullPointerException {
		Table res = null;
		ArrayList<Table> tables = mTables;
		int tableCount = tables.size();
		
		for (int i = 0; i < tableCount; i++)
		{
			Table t = tables.get(i); 
			if (t.getName().equals(table)){
				res = t;
			}
		}
		if(res==null){
			throw new NullPointerException("No existe la tabla '"+table+"'");
		}
		return res;
	}
	
    /**
     * Devuelve el numero de tablas de la base de datos
     * 
     * @return objeto Numero de tablas
     */
	
	public int getTableCount () {
		return mTables.size();
	}
	
    public Context getContext()
    {
    	return mCore.getContext();
    }


	public String getPackage() {
		return mCore.getPackage();
	}
	
    /**
     * Devuelve un valor de un identificador
     * 
     * @param name Nombre del identificador
     * 
     * @return valor
     */	
	public String getStringFromIdentifier (String name) {
		int id = mCore.getContext().getResources().getIdentifier(mCore.getPackage() + ":string/"+name, null, null);
		if (id!=0){
			return mCore.getContext().getResources().getString(id);
		}else{
			return name;
		}
	}
	
	
    
    /**
     * Devuelve una lista con todos los objetos Entity de la tabla
     * 
     * @param table tabla a usar
     * 
     * @return lista de objetos
     */
    
    public ArrayList<Entity> getEntityList(String table) {
    	return getEntityList(table, null, null, null);    	
    }
    
    /**
     * Devuelve una lista los objetos Entity de la tabla de una consulta
     * 
     * @param table tabla a usar
     * @param where parte WHERE de la consulta SQL
     * 
     * @return lista de objetos
     */
    
    public ArrayList<Entity> getEntityList(String table, String where) {
    	return getEntityList(table, where, null, null);    	
    }
    
    /**
     * Devuelve una lista los objetos Entity de la tabla de una consulta
     * 
     * @param table tabla a usar
     * @param where parte WHERE de la consulta SQL
     * @param orderby parte ORDER BY de la consulta SQL
     * 
     * @return lista de objetos
     */
    
    public ArrayList<Entity> getEntityList(String table, String where, String orderby) {
    	return getEntityList(table, where, orderby, null);    	
    }
    
    /**
     * Devuelve una lista los objetos Entity de la tabla de una consulta ordenados
     * 
     * @param table tabla a usar
     * @param where parte WHERE de la consulta SQL
     * @param orderby parte ORDER BY de la consulta SQL
     * @param limit parte LIMIT de la consulta SQL
     * 
     * @return lista de objetos
     */    
    
    public ArrayList<Entity> getEntityList(String table, String where, String orderby, String limit) {
    	String[] fields = getTable(table).getFieldsToArray();
    	Cursor c = getCursor(table, fields, where, null, null, null, orderby, limit);
    	
    	ArrayList<Entity> aux = new ArrayList<Entity>();

    	c.moveToFirst();
    	while (!c.isAfterLast()) {
    		// Creamos la entidad a partir del cursor y nos ahorramos el acceso a la BD
    		Entity ent = new Entity(table, c);

    		aux.add(ent);
    		c.moveToNext();
    	}
    	c.close();
        return aux;
    }
    
    /**
     * Devuelve el primer resultado de la consulta
     * 
     * @param table tabla a usar
     * @param where parte WHERE de la consulta SQL
     * @param orderby parte ORDER BY de la consulta SQL
     * @param limit parte LIMIT de la consulta SQL
     * 
     * @return lista de objetos
     */    
    
    public Entity getTopEntity(String table, String where, String orderby) {
    	ArrayList<Entity> ar = getEntityList(table, where, orderby, "1");
    	if (ar.size()>0) {
    		return ar.get(0);
    	} else {
    		return null;
    	}
    }
    
    /**
     * Devuelve el numero de resultados de una consulta
     * 
     * @param table tabla a usar
     * @param where parte WHERE de la consulta SQL
     * 
     * @return numero de objetos
     */
    
    public int getEntityListCount(String table, String where) {
    	String[] fields = getTable(table).getFieldsToArray();
    	Cursor c = getCursor(table, fields, where, null, null, null, null, null);
    	int count = c.getCount();
    	c.close();
    	c = null;
    	return count;
    }
    
    
    /**
     * Devuelve la posicion de un identificador en una lista de Entidades
     * 
     * @param ar lista de entidades
     * @param id identificador a buscar
     * 
     * @return posicion
     */
	
    public int getPosition(ArrayList<Entity> ar, long id) 
    {
    	int size = ar.size();
    	for (int i=0; i<size; i++) {
    		if ( ar.get(i).getId() == id ) {
    			return i;
    		}
    	}
    	return -1;
    }
    
    /**
     * Devuelve un cursor con todos los registros de la tabla
     * 
     * @param table tabla a usar
     * 
     * @return cursor
     */
    
    public Cursor getCursor(String table) {
    	String[] aux = getTable(table).getFieldsToArray();
        return mCore.getDB().query(table, aux, null, null, null, null, null);
    }
    
    /**
     * Devuelve un cursor con los registros de una consulta en la tabla 
     * 
     * @param table tabla a usar
     * @param selection parte WHERE de la consulta SQL
     * @param orderby parte ORDER BY de la consulta SQL
     * 
     * @return cursor
     */
    
    public Cursor getCursor(String table, String selection, String orderby) {
    	String[] aux = getTable(table).getFieldsToArray();
    	return mCore.getDB().query(table, aux, selection, null, null, null, orderby, null);
    }
    
    /**
     * Devuelve un cursor con los registros de una consulta en la tabla 
     * 
     * @param table tabla a usar
     * @param fields array con los nombres de los campos de la tabla
     * @param selection parte WHERE de la consulta SQL
     * @param selectionArgs array
     * @param groupby parte GROUP BY de la consulta SQL
     * @param having parte HAVING de la consulta SQL
     * @param orderby parte ORDER BY de la consulta SQL
     * @param limit parte LIMIT de la consulta SQL
     * 
     * @return cursor
     */
    
    public Cursor getCursor(String table, String[] fields, String selection, 
    			String[] selectionArgs, String groupby, String having, String orderby, String limit) {
        return mCore.getDB().query(table, fields, selection, selectionArgs, groupby, having, orderby, limit);
    }
	
    /**
     * Devuelve un cursor con los registros de una consulta en la tabla 
     * 
     * @param sql consulta SQL
     * @param selectionArgs array
     * 
     * @return lista de objetos
     */
    
    public Cursor rawQuery(String sql, String[] selectionArgs) {
    	Cursor c = mCore.getDB().rawQuery(sql, selectionArgs);
    	if (c.getCount() > 0) {
    		c.moveToFirst();
    	}
        return c;        
    }
    	
    /**
     * Crea las tablas de la base de datos 
     * 
     */    
    public void createTables() {
		ArrayList<Table> tables = mTables;
		int tableCount = tables.size();
    	for (int i = 0; i < tableCount; i++){
			mCore.getDB().execSQL(tables.get(i).getSQLCreateTable());
		}
	}
    
    /**
     * Borra todas las tablas de la base de datos 
     * 
     */
    
    public void deleteTables() {
		ArrayList<Table> tables = mTables;
		int tableCount = tables.size();
		for (int i = 0; i < tableCount; i++){
			mCore.getDB().execSQL(tables.get(i).getSQLDeleteTable());
		}
	}
    
    /**
     * Borra la tabla pasada como parametro de la base de datos 
     * 
     * @param table tabla
     * 
     */    
    public void deleteTable(String table) {
    	Table t = getTable(table);
    	if (t != null){
			mCore.getDB().execSQL(t.getSQLDeleteTable());
		}
	}
    
    /**
     * Vacia todas las tablas de la base de datos 
     * 
     */    
    public void emptyTables() {
		ArrayList<Table> tables = mTables;
		int tableCount = tables.size();
    	for (int i = 0; i < tableCount; i++){
			mCore.getDB().delete(tables.get(i).getName(), null, null);
		}
	}
    
    /**
     * Vacia todas las tablas que formen parte del backup 
     * 
     */
    
    public void emptyTablesBackup() {
		ArrayList<Table> tables = mTables;
		int tableCount = tables.size();
    	for (int i = 0; i < tableCount; i++)
    	{
    		Table t = tables.get(i);
    		if (t.isBackup()){
				mCore.getDB().delete(t.getName(), null, null);
			}
    	}
	}
    
    /**
     * Vacia la tabla pasada como parametro de la base de datos 
     * 
     * @param table tabla
     * 
     */
    
    public void emptyTable(String table) {
    	mCore.getDB().delete(table, null, null);
	}
    
    /**
     * Crea una copia de seguridad de la base de datos
     * 
     * @param file Archivo XML donde hace la copia
     * 
     */
    public void backup(String file) throws XmlPullParserException, IOException {
    	mCore.backup(file, false);
    }
    
    /**
     * Crea una copia de seguridad de la base de datos
     * 
     * @param file Archivo XML donde hace la copia
     * @param forceBackup Forzar a hacer backup aunque la table no lo permita
     * 
     */	
    public void backup(String file, boolean forceBackup) throws XmlPullParserException, IOException {	
    	mCore.backup(file, forceBackup);
    }
    
    /**
     * Reestablece una copia de seguridad de la base de datos
     * 
     * @param file Archivo XML donde esta la copia
     * 
     */
    public void restore(String file) throws XmlPullParserException, IOException {	
    	mCore.restore(file);
    }
    
	/**
	 * Devuelve un cursor del tipo EntityCursor
	 * 
	 * @param table Tabla
	 * @param where Condicion de busqueda
	 * @param orderby Ordenacion
	 * @return Cursor de entidades.
	 */
    public EntityCursor getEntityCursor(String table, String where, String orderby) {
    	String[] fields = getTable(table).getFieldsToArray();
    	Cursor c = getCursor(table, fields, where, null, null, null, orderby, null);
    	
    	return new EntityCursor(table, c);
    }
    
    /**
     * Devuelve conexion a SQLiteDatabase
     * 
     * @return objeto SQLiteDatabase
     */  
    
    public SQLiteDatabase getDB() {
    	return mCore.getDB();
    }
    
    public void setForceLanguage(String lang) {
    	mCore.setForceLanguage(lang);
    }
    
    
	public ArrayList<String> getLanguages() {
		return mCore.getLanguages();
	}


	public String getCurrentLanguage() {
		return mCore.getCurrentLanguage();
	}
	
	public String getFieldNameLanguage(String name) {
		return name + "_" + mCore.getCurrentLanguage();
	}


	/**
	 * Inicia una transaccion 
	 * @param void
	 * @return void.
	 */
	public void startTransaction()
	{
		mCore.getDB().beginTransaction();
	}
	
	/**
	 * Finaliza una transaccion 
	 * @param void
	 * @return void.
	 */
	public void endTransaction()
	{
		mCore.getDB().endTransaction();
	}

	/**
	 * Indica si estamos en una transaccion 
	 * @param void
	 * @return Boolean True si hay una transaccion en curso, false en caso contrario.
	 */
	public boolean inTransaction()
	{
		return mCore.getDB().inTransaction();
	}

	/**
	 * Confirma la ejecucion correcta de una transaccion 
	 * @param void
	 * @return void.
	 */
	public void successfulTransaction()
	{
		mCore.getDB().setTransactionSuccessful();
	}	
	
}

