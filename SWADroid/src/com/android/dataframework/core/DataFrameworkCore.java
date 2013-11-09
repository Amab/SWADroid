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

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

public class DataFrameworkCore {
	
	private final static int TYPESQL_INSERT = 0;
	private final static int TYPESQL_UPDATE = 1;
	private final static int TYPESQL_DELETE = 2;
	
	private String mPackage = "";
	
	private ArrayList<String> mLanguages = new ArrayList<String>();
	private String mCurrentLanguage;

	private SQLiteDatabase mDb;
    private DatabaseHelper mDbHelper;
	private ArrayList<Table> mTables;
    private String mDataBaseName = "";
    private int mDataBaseOldVersion = 0;
    private int mDataBaseVersion = 0;
    private Context mCtx;
	
	private boolean mSaveInitialValues = false;
	
	/**
	 * Constructor
	 */
	public DataFrameworkCore() 
	{
		
	}
	
    private class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper() {
        	super(mCtx, mDataBaseName, null, mDataBaseVersion);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        	try {
	       		System.out.println("(onCreate) CREATE TABLES");
	       		mDataBaseOldVersion = 0;
	       		
	    		ArrayList<Table> tables = mTables;
	    		int tableCount = tables.size();
	    		for (int i = 0; i < tableCount; i++)
	    			db.execSQL(tables.get(i).getSQLCreateTable());
	    		
	        	mSaveInitialValues = true;
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
        {
            Log.w("TableDB", "Upgrading database from version " + oldVersion + " to " + newVersion);
            mDataBaseOldVersion = oldVersion;
            
            ArrayList<Table> tables = mTables;
    		int tableCount = tables.size();            
        	for (int i = 0; i < tableCount; i++) 
        	{
        		Table t = tables.get(i);
        		if (add(t.getNewInVersion(), oldVersion, newVersion)) 
        		{
        			System.out.println("(onUpgrade) CREATE TABLE: " + t.getName());
        			db.execSQL(t.getSQLCreateTable());
        		} else 
        		{
        			ArrayList<Field> fields = t.getFields(); 
        			int fieldCount = fields.size();
        			for (int j=0; j < fieldCount; j++) {
        				Field f = fields.get(j);
        				if (add(f.getNewInVersion(), oldVersion, newVersion)) {
        					String sql = t.getSQLAddField(f);
        					if (sql!=null) {
        						System.out.println("(onUpgrade) ADD FIELD: " + t.getName() + "(" + f.getName() + ")");
        						db.execSQL(sql);
        					}
        				}
        			}
        			
        		}
        	}
        	mSaveInitialValues = true;
        }
        
        private boolean add(int version, int oldVersion, int newVersion) {
        	if (version>oldVersion && version<=newVersion)
        		return true;
        	else 
        		return false;
        }
    }
    
    /**
     * Abre una conexion a ADF generando todos los objetos a partir de los archivos XML 
     * pasados como parametros
     * 
     * @param context Actividad principal
     * @param idTables recurso XML con las tablas
     * @param idInitialValues recurso XML con los registros a generar en la primera carga
     * 
     */	
	public void open(Context context, String namePackage, ArrayList<Table> tables) throws XmlPullParserException, IOException {
		try {
			// Solo en el caso de que no est�n cargadas las tablas, leemos el XML
			if (tables.size() == 0)
			{
				mPackage = namePackage;
				mTables = tables;
				mCtx = context;			
				int idTables = context.getResources().getIdentifier(namePackage+":xml/tables", null, null);
				
				if (idTables!=0) {
					
					XmlResourceParser x = mCtx.getResources().getXml(idTables);
					int eventType = x.getEventType();
					Table currentTable = new Table( "" );
					Field currentField = new Field( "" );
					while (eventType != XmlPullParser.END_DOCUMENT) {
						
						if ( eventType == XmlPullParser.START_TAG ) {
							if (x.getName().equals("database")) {
								mDataBaseName = x.getAttributeValue(null, "name");
								mDataBaseVersion = Integer.parseInt(x.getAttributeValue(null, "version"));
								if (x.getAttributeValue(null, "languages")!=null) {
									boolean isFirst = true;
									StringTokenizer tokens = new StringTokenizer(x.getAttributeValue(null, "languages"), "|");
									while(tokens.hasMoreTokens()){
										String token = tokens.nextToken();
										mLanguages.add(token);
										if (isFirst) { mCurrentLanguage = token; isFirst = false; }
										if (Locale.getDefault().getLanguage().equals(token)) mCurrentLanguage = token;
									}
									System.out.println("Lenguaje por defecto usado en la base de datos: " + mCurrentLanguage);
								}
							} else if (x.getName().equals("table")) {
								currentTable = new Table( x.getAttributeValue(null, "name") );
								if (x.getAttributeValue(null, "to-string")!=null) currentTable.setToString(x.getAttributeValue(null, "to-string"));
								if (x.getAttributeValue(null, "backup")!=null) {
									if (x.getAttributeValue(null, "backup").equals("no")) currentTable.setBackup(false);
								}
								if ( x.getAttributeValue(null, "new-in-version") != null ) {
									currentTable.setNewInVersion(Integer.parseInt( x.getAttributeValue(null, "new-in-version") ));
								}
							} else if (x.getName().equals("field")) {
								currentField = new Field ( x.getAttributeValue(null, "name") );
								if ( x.getAttributeValue(null, "type") != null ) currentField.setType( x.getAttributeValue(null, "type") );
								if ( x.getAttributeValue(null, "foreign-table") != null ) currentField.setForeignTable( x.getAttributeValue(null, "foreign-table") );
								if ( x.getAttributeValue(null, "obligatory") != null ) {
									if (x.getAttributeValue(null, "obligatory")=="true")	currentField.setObligatory( true );
								}
								if ( x.getAttributeValue(null, "size") != null ) currentField.setSize( Integer.parseInt( x.getAttributeValue(null, "size") ) );
								if ( x.getAttributeValue(null, "default") != null ) currentField.setTextDefault( x.getAttributeValue(null, "default") );
								if ( x.getAttributeValue(null, "new-in-version") != null ) {
									currentField.setNewInVersion(Integer.parseInt( x.getAttributeValue(null, "new-in-version") ));
								}
								currentTable.addField(currentField);
							}
						}
						
						
						if ( eventType == XmlPullParser.END_TAG ) {
							if (x.getName().equals("table")) {
								tables.add(currentTable);
							}
						}
						
						eventType = x.next();
					}
					x.close();
					
				} else {
					
					System.out.println("=========================================");
					System.out.println("No se ha encontrado el archivo tables.xml");
					System.out.println("=========================================");
					
				}
				
			}
			// Presuponemos que no se guardan los valores iniciales.
			mSaveInitialValues = false;
			
	        mDbHelper = new DatabaseHelper();
	        mDb = mDbHelper.getWritableDatabase();
			
	        if (mSaveInitialValues)	        
	        	saveInitialValues();
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	

    /**
     * Cierra la conexion a la base de datos
     * 
     */    
    public void close() {
        mDbHelper.close();
    }
	
	
    /**
     * Genera los valores iniciales de la primera carga en el caso que se haya especificado archivo
     * 
     */	
    public void saveInitialValues() throws XmlPullParserException, IOException {
    	try {
    		for (int i=mDataBaseOldVersion+1; i<=mDataBaseVersion; i++) {
        		int idInitialValues = mCtx.getResources().getIdentifier(mPackage+":xml/initialvalues_v"+i, null, null);
        		if (idInitialValues!=0) {
    		    	XmlResourceParser x = mCtx.getResources().getXml(idInitialValues);
    		    	insertXML(x);		    	
        		}
    		}
	    	
		} catch (Exception e) {
			e.printStackTrace();
		}
    }    

    /**
     * Crea una copia de seguridad de la base de datos
     * 
     * @param file Archivo XML donde hace la copia
     * 
     */
    public void backup(String file) throws XmlPullParserException, IOException {
    	backup(file, false);
    }
    
    /**
     * Crea una copia de seguridad de la base de datos
     * 
     * @param file Archivo XML donde hace la copia
     * @param forceBackup Forzar a hacer backup aunque la table no lo permita
     * 
     */	
    public void backup(String file, boolean forceBackup) throws XmlPullParserException, IOException {			
		File f = new File(file);
		if (f.exists()) f.delete();
		FileOutputStream fOut = new FileOutputStream(file); 
		OutputStreamWriter osw = new OutputStreamWriter(fOut); 
		
		osw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
    			+ "<values>\n");
		
		List<Table> tables = mTables;
		int tableCount = tables.size();
		System.out.println("tablas: "+ tables.size());
		
		for (int i = 0; i < tableCount; i++) 
		{
			Table t = tables.get(i);
			
			if (t.isBackup() || forceBackup) {
			
				String tableName = t.getName();
				
				System.out.println("tabla: "+ tableName);
				
		    	String[] aux = t.getFieldsToArray();
		        Cursor c = getDB().query(tableName, aux, null, null, null, null, null);

	    		c.moveToFirst();
	        	
	    		ArrayList<Field> fields = t.getFields();
	    		int fieldCount = fields.size();
	    		
	    		while (!c.isAfterLast()) {
	        		osw.append("<row table=\"" + tableName + "\" id=\"" + c.getString(c.getColumnIndex(DataFramework.KEY_ID)) + "\">\n");
	        		
	        		for (int j = 0; j < fieldCount; j++) 
	        		{
	        			Field field = fields.get(j);
	        			if (c.getString(c.getColumnIndex(field.getName()))!=null)
	        				osw.append("<field name=\"" + field.getName() + "\" value=\"" + c.getString(c.getColumnIndex(field.getName())).replace("\"", "&quot;") + "\" />\n");
	        		}
	        		
	        		osw.append("</row>\n");
	        		c.moveToNext();
	        		
	        	}
	    		c.close();
	    		
			}
			
		}
		
		osw.append("</values>\n");
        
        osw.flush();
        
        osw.close();

    } 
    
    /**
     * Reestablece una copia de seguridad de la base de datos
     * 
     * @param file Archivo XML donde esta la copia
     * 
     */
    public void restore(String file) throws XmlPullParserException, IOException {
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser x = (XmlPullParser)factory.newPullParser();
			x.setInput(new FileReader(file));
			insertXML(x);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    /**
     * Inserta los registros de un XmlResourceParser en la Base de datos
     * 
     * @param x Archivo XmlResourceParser
     * 
     */    
    
    private void insertXML(XmlPullParser x) throws XmlPullParserException, IOException {		
    	try {
    			    	
	    	int eventType = x.getEventType();
	    	
	    	Entity currenEntity = null;
	    	int typesql = TYPESQL_INSERT;
	    	
	    	while (eventType != XmlPullParser.END_DOCUMENT) {
	    		
	    		if ( eventType == XmlPullParser.START_TAG ) {
	    			if (x.getName().equals("row")) {
	    				
	    				currenEntity = null;
	    				
	    				if ( x.getAttributeValue(null, "table") != null ) {
	    					long id = -1;
		    				if ( x.getAttributeValue(null, "action") != null ) {
		    					if (x.getAttributeValue(null, "action").equals("update")) {
		    						typesql = TYPESQL_UPDATE;
		    					} else if (x.getAttributeValue(null, "action").equals("delete")) {
		    						typesql = TYPESQL_DELETE;
		    					} else {
		    						typesql = TYPESQL_INSERT;
		    					}
		    				} else {
		    					typesql = TYPESQL_INSERT;
		    				}
		    				
		    				if ( x.getAttributeValue(null, "id") != null ) {
		    					id = Long.parseLong(x.getAttributeValue(null, "id"));
		    				}
		    				
		    				if (typesql == TYPESQL_INSERT) {
		    					currenEntity = new Entity(x.getAttributeValue(null, "table"));
		    					if (id>0) currenEntity.setForceId(id);
		    				} else if (typesql == TYPESQL_UPDATE) {
		    					if (id>0) currenEntity = new Entity(x.getAttributeValue(null, "table"), id);
		    				} else {
		    					if (id>0) currenEntity = new Entity(x.getAttributeValue(null, "table"), id);
		    				}
	    				}
	    				
	    				

	    			}
	    			
	    			if (x.getName().equals("field")) {
	    				if ( x.getAttributeValue(null, "lang") != null ) {
	    					if ( ( x.getAttributeValue(null, "name") != null ) && ( x.getAttributeValue(null, "value") != null ) ) {
		    					if (currenEntity!=null) currenEntity.setMultilanguageValue(x.getAttributeValue(null, "name"), 
		    																	x.getAttributeValue(null, "lang"),
		    																	x.getAttributeValue(null, "value"));
		    				}
	    				} else {
	    					if ( ( x.getAttributeValue(null, "name") != null ) && ( x.getAttributeValue(null, "value") != null ) ) {
		    					if (currenEntity!=null) {
		    						if (x.getAttributeValue(null, "value")!=null) {
		    							currenEntity.setValue(x.getAttributeValue(null, "name"), 
		    									x.getAttributeValue(null, "value").replace("&quot;", "\""));
		    						}
		    					}
		    				}
	    				}
	    			}
	    			
	    		}
	    		
				if ( eventType == XmlPullParser.END_TAG ) {
					if (x.getName().equals("row")) {
						if (currenEntity!=null) {
							if (typesql == TYPESQL_DELETE) {
								currenEntity.delete();
							} else {
								currenEntity.save();
							}
						}
					}
				}

				eventType = x.next();
			}

	    	
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    /**
     * Devuelve conexion a SQLiteDatabase
     * 
     * @return objeto SQLiteDatabase
     */       
    public SQLiteDatabase getDB() {
    	return mDb;
    }

    public Context getContext()
    {
    	return mCtx;
    }


	public String getPackage() {
		return mPackage;
	}
	
    /**
     * Establece el lenguaje a usar
     * 
     * @param lang Lenguaje
     */     
	
    public void setForceLanguage(String lang) {
    	if (mLanguages.contains(lang)) mCurrentLanguage = lang;
    }
    
    
	public ArrayList<String> getLanguages() {
		return mLanguages;
	}


	public String getCurrentLanguage() {
		return mCurrentLanguage;
	}
    
}
