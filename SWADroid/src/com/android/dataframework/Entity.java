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

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import com.android.dataframework.core.Field;
import com.android.dataframework.core.Table;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class Entity {
	
	private String mTable;
	private long mId = -1;
	private long mForceId = -1;
	protected HashMap<String, Object> mAttributes = new HashMap<String, Object>();
	protected HashMap<String, Object> mMultilanguagesAttributes = new HashMap<String, Object>();

    /**
     * Devuelve el siguiente _id
     * 
     * @return siguiente _id
     */	
	public long nextId() {
		if (mForceId<0) 
		{
			Cursor c = DataFramework.getInstance().getCursor(mTable, new String[]{DataFramework.KEY_ID}, 
					null, null, null, null, DataFramework.KEY_ID + " desc", "1");
			if (!c.moveToFirst()) {
				c.close();
				return 1;
			} else {
				long Id = c.getLong(0) + 1;
				c.close();
				return Id;
			}
		} else {
			return mForceId;
		}
	}
	
    /**
     * Devuelve el _id del registro
     * 
     * @return id del registro
     */	
	public long getId() {
		return mId;
	}
	
    /**
     * Devuelve la tabla de la entidad
     * 
     * @return tabla de la entidad
     */	
	public String getTable() {
		return mTable;
	}
	
    /**
     * Devuelve si es una actualizacion del registro
     * 
     * @return true si es una actualizacion
     */	
	public boolean isUpdate() {
		return (mId < 0)?false:true;
	}
	
	/**
     * Devuelve si es un nuevo de registro
     * 
     * @return true si es una nuevo registro
     */	
	public boolean isInsert() {
		return (mId < 0)?true:false;
	}
	
	/**
     * Devuelve el valor a un atributo en tipo entero
     * 
     * @param name nombre del campo
     * @return valor del campo (Tipo int)
     */	
	public Integer getInt(String name) 
	{
		Object obj = getValue(name);
		if (obj == null){
			return 0;
		}else{
			return Integer.parseInt(obj.toString());
		}
		
	}
	
	/**
     * Devuelve el valor a un atributo en tipo String
     * 
     * @param name nombre del campo
     * @return valor del campo (Tipo String)
     */	
	public String getString(String name) 
	{
		Field f = getTableObject().getField(name);
		if (f.getType().equals("string-identifier")) {
			return DataFramework.getInstance().getStringFromIdentifier(getValue(name).toString());
		} else {
			Object obj = getValue(name);
			if (obj == null){
				return "";
			}else{
				return obj.toString();
			}
		}
	}
		
	/**
     * Devuelve el idenfificador del recurso en la aplicacion
     * 
     * @param name nombre del campo
     * @return valor (Tipo int)
     */	
	public int getDrawableIdentifier(String name) 
	{
		int id = DataFramework.getInstance().getContext().getResources().getIdentifier(DataFramework.getInstance().getPackage() + ":drawable/"+getValue(name).toString(), null, null);
		return id;
	}
	
	/**
     * Devuelve un bitmap del recurso en la aplicacion
     * 
     * @param name nombre del campo
     * @return valor (Tipo bitmap)
     */	
	public Bitmap getBitmap(String name) 
	{
		int id = DataFramework.getInstance().getContext().getResources().getIdentifier(DataFramework.getInstance().getPackage() + ":drawable/"+getValue(name).toString(), null, null);
		java.io.InputStream is = DataFramework.getInstance().getContext().getResources().openRawResource(id);
    	BitmapDrawable bmd = new BitmapDrawable(BitmapFactory.decodeStream(is));
		return bmd.getBitmap();
	}
	
	/**
     * Devuelve un BitmapDrawable del recurso en la aplicacion
     * 
     * @param name nombre del campo
     * @return valor (Tipo BitmapDrawable)
     */	
	public BitmapDrawable getBitmapDrawable(String name) 
	{
		int id = DataFramework.getInstance().getContext().getResources().getIdentifier(DataFramework.getInstance().getPackage() + ":drawable/"+getValue(name).toString(), null, null);
		java.io.InputStream is = DataFramework.getInstance().getContext().getResources().openRawResource(id);
    	BitmapDrawable bmd = new BitmapDrawable(BitmapFactory.decodeStream(is));
		return bmd;
	}
	
	/**
     * Devuelve un objeto Drawable del recurso en la aplicaci�n
     * 
     * @param name nombre del campo
     * @return valor (Tipo Drawable)
     */	
	public Drawable getDrawable(String name) 
	{
		int id = DataFramework.getInstance().getContext().getResources().getIdentifier(DataFramework.getInstance().getPackage() + ":drawable/"+getValue(name).toString(), null, null);
		return DataFramework.getInstance().getContext().getResources().getDrawable(id);
	}	
	
    /**
     * Devuelve el Cursor de la entidad con un solo campo pasado como par�metro. 
     * Si la entidad es un nuevo registro devuelve null
     * 
     * @param field campo (Objeto Field)
     * @return cursor
     */    
    public Cursor getCursor(String field) {
    	try {
	    	if (isInsert()){
				return null;
			}
	    	
	    	String fieldName = field;
	    	
	    	Field f = DataFramework.getInstance().getTable(mTable).getField(fieldName);
	    	
	    	if (f.getType().equals("multilanguage")){
				fieldName += "_" +  DataFramework.getInstance().getCurrentLanguage();
			}
	    	
	        Cursor mCursor = DataFramework.getInstance().getDB().query(true, mTable, new String[] {fieldName}, DataFramework.KEY_ID + "=" + mId, null,
	                        null, null, null, null);
	        if (mCursor != null) {
	            mCursor.moveToFirst();
	        }
	
	        return mCursor;
    	} catch (SQLException e) {
            Log.e("Exception on query", e.toString());
        }
    	return null;
    }
    
    /**
     * Devuelve el Cursor de la entidad con todos los campos de esta. 
     * Si la entidad es un nuevo registro devuelve null
     * 
     * @return cursor
     */    
    public Cursor getCursor() {

    	try {
	    	if (isInsert()){
				return null;
			}
	    	
	        Cursor mCursor = DataFramework.getInstance().getDB().query(true, mTable, DataFramework.getInstance().getTable(mTable).getFieldsToArray(), DataFramework.KEY_ID + "=" + mId, null,
	                        null, null, null, null);
	        if (mCursor != null) {
	            mCursor.moveToFirst();
	        }
	
	        return mCursor;
    	} catch (SQLException e) {
            Log.e("Exception on query", e.toString());
        }
    	return null;
    }
    
    /**
     * Devuelve un string de la entidad. Formato obtenido del archivo XML
     * 
     * @return string
     */
	@Override
	public String toString() 
	{
		String[] arString = DataFramework.getInstance().getTable(mTable).getToString().split("%");
		String out = "";
		for (int i=0; i<arString.length; i++) {
			if (isAttribute(arString[i])) 
			{
				Field f = getTableObject().getField(arString[i]);
				if (f.getType().equals("foreign-key")){
					out += getEntity(arString[i]).toString();
				}else{
					out += getString(arString[i]);
				}
			} else if (arString[i].equals(DataFramework.KEY_ID)) {
				out += getId();
			} else {
				out += arString[i];
			}
		}
		return out;
	}

    /**
     * Devuelve el _id que queremos forzar al guardar la entidad
     * 
     * @return id a forzar
     */
	
	public long getForceId() {
		return mForceId;
	}
	
    /**
     * Establece el _id que queremos forzar al guardar la entidad
     * 
     * @param forceId identificador
     */

	public void setForceId(long forceId) {
		this.mForceId = forceId;
	}

	public Table getTableObject()
	{
		return DataFramework.getInstance().getTable(mTable);
	}
	
	/**
     * Devuelve el valor a un atributo en tipo entero largo
     * 
     * @param name nombre del campo
     * @return valor del campo (Tipo int)
     */	
	public long getLong(String name) 
	{
		Object obj = getValue(name);
		if (obj == null){
			return 0;
		}else{
			return Long.parseLong(obj.toString());
		}
	}
	
	public double getDouble(String name)
	{
		Object obj = getValue(name);
		return obj == null? 0.0 : Double.parseDouble(obj.toString());
	}
	
	public float getFloat(String name)
	{
		Object obj = getValue(name);
		return obj == null? 0.0f : Float.parseFloat(obj.toString());
	}

	/**
	 * A�ade los atributos desde el objeto table.
	 * Se llama desde el constructor. 
	 */
	private void addAllAttributesFromTable()
	{
		Table t = getTableObject();
		for (int i=0; i<t.getFields().size(); i++) 
		{
			Field f = t.getFields().get(i);
			mAttributes.put(f.getName(), null);
		}
	}

	/**
	 * Carga los valores creando un cursor.l  
	 */
	protected void loadData()
	{
		Cursor c = getCursor();
		if (c!=null) {
			loadData(c);
			c.close();
		}
	}
	
	/**
	 * Carga los valores de cualquier cursor.
	 * 
	 * @param c Cursor al registro con los datos.
	 */
	protected void loadData(Cursor c)
	{		
		if (c!=null) {
			HashMap<String, Object> attribs = mAttributes; // Para reducir el acceso al heap.
			Object[] attributeNames = attribs.keySet().toArray();
			int attributeCount = attributeNames.length;
			
			for (int i = 0; i < attributeCount; i++)			
			{				
				String attributeName = attributeNames[i].toString();				

				Field f = getTableObject().getField(attributeName);
				
		        int indexField;
		        if (f.getType().equals("multilanguage")) {
		        	indexField = c.getColumnIndexOrThrow(attributeName + "_" + DataFramework.getInstance().getCurrentLanguage());
		        } else {
		        	indexField = c.getColumnIndexOrThrow(attributeName);
		        }
		        
		        if (f.getType().equals("text") || f.getType().equals("multilanguage") || f.getType().equals("string-identifier") || f.getType().equals("drawable-identifier")){
					attribs.put(attributeName, c.getString(indexField));
				}else if (f.getType().equals("int")){
					attribs.put(attributeName, c.getLong(indexField));
				}else if (f.getType().equals("foreign-key")){
					attribs.put(attributeName, c.getLong(indexField));
				}else if (f.getType().equals("real")){
					attribs.put(attributeName, c.getDouble(indexField));
				}else{
					attribs.put(attributeName, c.getString(indexField));
				}
			}
		}
	}

	/**
	 * Constructor a partir de un cursor, de esta forma nos ahorramos el acceso a la base de datos.
	 * 
	 * @param table Nombre de la tabla
	 * @param c Cursor al registro a cargar. 
	 */
	public Entity(String table, Cursor c)
	{
		mId = c.getLong(c.getColumnIndexOrThrow(DataFramework.KEY_ID));
		this.mTable = table;
		mForceId = -1;
		
		addAllAttributesFromTable();
		loadData(c);
	}
	
    /**
    * Constructor - toma como valor el nombre de tabla de la entidad. Solo para nuevos registros 
    * 
    * @param table la tabla de la entidad
    */	
	public Entity (String table) {		
		this.mTable = table;
		mForceId = -1;
		addAllAttributesFromTable();
	}
	
    /**
    * Constructor - toma como valor el nombre de tabla de la entidad y un identificador. 
    * Solo para actualizaciones de registros
    * 
    * @param table la tabla de la entidad
    * @param id el identificador de la entidad
    */	
	public Entity (String table, Long id) {
		this.mTable = table;
		mForceId = -1;
		if (id > 0){
			this.mId = id;
		}
		addAllAttributesFromTable();
		loadData();
	}
	
    /**
    * Constructor - toma como valor el nombre de tabla de la entidad y un XML con la serializaci�n de la entidad. 
    * 
    * @param table la tabla de la entidad
    * @param xml Xml devuelto por getSerialization o getXml
    */	
	public Entity (String table, String xml) {
		this.mTable = table;
		mForceId = -1;

		addAllAttributesFromTable();
		loadFromXml(xml);
	}
	

	/**
     * Estable el valor a un atributo
     * 
     * @param name nombre del campo
     * @param value valor del campo
     */	
	public void setValue(String name, Object value) 
	{
		if (Entity.class.isInstance(value)){
			mAttributes.put(name, ((Entity)value).getId());
		}else{
			mAttributes.put(name, value);
		}
	}
	
	/**
     * Estable el valor a un atributo multilenguaje
     * 
     * @param name nombre del campo
     * @param value valor del campo
     */	
	public void setMultilanguageValue(String name, String lang, Object value) 
	{
		mMultilanguagesAttributes.put(name + "_" + lang, value);
	}
	
	/**
     * Devuelve el valor a un atributo
     * 
     * @param name nombre del campo
     * @return valor del campo (Tipo Object)
     */	
	public Object getValue(String name) 
	{
		return mAttributes.get(name);
	}

	/**
     * Devuelve el valor a un atributo en tipo Entity. Para claves foraneas.
     * Creamos el objeto a partir del ID almacenado en el campo.
     * 
     * @param name nombre del campo
     * @return valor del campo (Tipo Entity)
     */	
	public Entity getEntity(String name) 
	{
		Field f = getTableObject().getField(name);
		if (f != null && f.getType().equals("foreign-key")){
			return new Entity(f.getForeignTable(), getLong(name));
		}else{
			return null;
		}
	}
	
	/**
     * Comprueba si el campo pasado por par�metro es un atributo de la entidad
     * 
     * @param name nombre del campo
     * @return true si es un campo
     */	
	public boolean isAttribute(String name) {
        try {
		    return getTableObject().getField(name) != null;
        } catch (NullPointerException e) {
            return false;
        }
	}
	
	/**
	 * Indica si un atributo es nulo 
	 * @param name
	 * @return si o no.
	 */
	public boolean isNull(String name)
	{
		return mAttributes.get(name) == null;
	}
 
    /**
     * Guarda una fila en la base de datos. Si _id es -1 es un nuevo registro, otra cosa una actualizacion
     * 
     * @return id or -1 if failed
     */
    public boolean save() {
    	try {
	        ContentValues args = new ContentValues();
	        if (isInsert()) {
	        	args.put(DataFramework.KEY_ID, "" + nextId());
	        } else {
	        	if (mForceId>0) {
	        		args.put(DataFramework.KEY_ID, "" + mForceId);	        		
	        	}
	        }
			for (int i=0; i < getTableObject().getFields().size(); i++) 
			{
				Field f = getTableObject().getFields().get(i);
				String attr = f.getName();
				Object value = getValue(attr);
				if (value != null)  
				{
					// TODO: Verificar si no hay que usar las distintas llamdas 
					// al metodo ContentValues.put() dependiendo del tipo del
					// campo.
					//
					// Lo que est� muy claro es que nunca se guarda null para ning�n
					// campo.
					if (f.getType().equals("multilanguage")) {
						args.put(f.getName() + "_" +  DataFramework.getInstance().getCurrentLanguage(), value.toString());
					} else {
						args.put(f.getName(), value.toString());
					}
				}
			}
			
			Iterator<Entry<String, Object>> it = mMultilanguagesAttributes.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, Object> e = (Entry<String, Object>)it.next();
				args.put(e.getKey().toString(), e.getValue().toString());
			}
			
			if (isInsert()) {
				mId = DataFramework.getInstance().getDB().insert(mTable, null, args);
				return (mId > 0);
			}else{
				return DataFramework.getInstance().getDB().update(mTable, args, DataFramework.KEY_ID + "=" + mId, null) > 0;
			}
    	} catch (SQLException e) {
            Log.e("Exception on query", e.toString());
        }
    	return false;
    }

    /**
     * Borra una fila
     * 
     * @return "true" si es borrada, "false" otra cosa
     */
    public boolean delete() {
        boolean res = DataFramework.getInstance().getDB().delete(mTable, DataFramework.KEY_ID + "=" + mId, null) > 0;
        mId = -1;
        return res;
    }

    /**
     * Devuelve la serializaci�n de la entidad en formato de cadena. (XML)
     * @return XML
     */
    public String getSerialization()
    {
    	return getXml();
    }
    
    /**
     * Devuelve un XML con los valores de la entidad.
     * @return
     */
    private String getXml()
    {    	
		HashMap<String, Object> attribs = mAttributes; // Para reducir el acceso al heap.
		Object[] attributeNames = attribs.keySet().toArray();
		int attributeCount = attributeNames.length;
		
    	String result = "<entity>\n";
		result += "<attribute name=\"_id\" value=\"" + mId + "\"/>\n";
    	
    	for (int i = 0; i < attributeCount; i++)
    	{
    		if (!isNull(attributeNames[i].toString()))
    		{
	    		result += "<attribute name=\"" + attributeNames[i].toString() + "\"" +
	    			" value=\"" + getValue(attributeNames[i].toString()).toString() + "\"/>\n";
    		}
    	}
    	
    	result += "</entity>\n";
    	return result;
    }
    
    /**
     * Carga los valores de la entidad desde un XML creado desde getXML.
     * @param xml
     */
    public void loadFromXml(String xml)
    {
    	try {    		
    		
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser x = (XmlPullParser)factory.newPullParser();			
			x.setInput(new StringReader(xml));
			
	    	int eventType = x.getEventType();
	    	
	    	while (eventType != XmlPullParser.END_DOCUMENT) 
	    	{
	    		if (eventType == XmlPullParser.START_TAG) {
	    			if (x.getName().equals("attribute")) {
	    				String name = x.getAttributeValue(null, "name"); 
	    				if (name != null) {
	    					String value = x.getAttributeValue(null, "value");
	    					if (name.equals("_id")){
								mId = Long.parseLong(value);
							}else{
								setValue(name, value);
							}
	    				}
	    			}
	    		}
	    		
	    		eventType = x.next();
	    	}
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
 
}



