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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.util.Log;

import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;

import es.ugr.swad.swadroid.Global;

/**
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 *
 */
public class DataBaseHelper {
	private DataFramework db;
	
    public DataBaseHelper(DataFramework database) {
    	db = database;
	}
	
	public synchronized void close() {
		db.close();
	}
	
	/**
	 * Function to parse from Integer to Boolean
	 * @param n Integer to be parsed
	 * @return true if n==0, false in other case
	 */
	private boolean parseIntBool(int n) {
		return n==0 ? true : false;
	}
	
	/**
	 * Function to parse from String to Boolean
	 * @param s String to be parsed
	 * @return true if s equals "Y", false in other case
	 */
	private boolean parseStringBool(String s) {
		return s.equals("Y") ? true : false;
	}
	
	/**
	 * Function to parse from Boolean to Integer
	 * @param b Boolean to be parsed
	 * @return 1 if b==true, 0 in other case
	 */
	private int parseBoolInt(boolean b) {
		return b ? 1 : 0;
	}
	
	/**
	 * Function to parse from Boolean to String
	 * @param b Boolean to be parsed
	 * @return "Y" if b==true, "N" in other case
	 */
	private String parseBoolString(boolean b) {
		return b ? "Y" : "N";
	}
	
	private Pair<String, String> selectParamsPairTable(String table) {  
		String firstParam = null;
		String secondParam = null;
		
    	if(table.equals(Global.DB_TABLE_TEST_QUESTIONS_COURSES)) {
    		firstParam = "crscod";
    		secondParam = "qstcod";
    	} else {
    		Log.e("selectParamsPairTable", "Table " + table + " not exists");
    	}
    	
    	return new Pair<String, String>(firstParam, secondParam);
	}
	
	/**
	 * Creates a Model's subclass object looking at the table selected
	 * @param table Table selected
	 * @param ent Cursor to the table rows
	 * @return A Model's subclass object
	 */
	private Model createObjectByTable(String table, Entity ent) {
		Model o = null;
		Pair<String, String> params;
		
		if(table.equals(Global.DB_TABLE_COURSES)) {
			o = new Course(ent.getInt("id"),
							ent.getString("name"));
		} else if(table.equals(Global.DB_TABLE_TEST_QUESTIONS_COURSES)) {
			
			params = selectParamsPairTable(table);
			
			o = new PairTable<Integer, Integer>(table,
					ent.getInt(params.getFirst()),
					ent.getInt(params.getSecond()));
		}
		
		return o;
	}
	
	/**
	 * Gets all rows of specified table
	 * @param table Table containing the rows
	 * @return A list of Model's subclass objects
	 */
	public List<Model> getAllRows(String table)
    {
		List<Model> result = new ArrayList<Model>();		
		List<Entity> rows = db.getEntityList(table);
		Model row;
		
		Iterator<Entity> iter = rows.iterator();
		while (iter.hasNext()) {
		  Entity ent = iter.next();
		  row = createObjectByTable(table, ent);
  		  result.add(row);
		}
        
        return result;
    }
	
	/**
	 * Inserts a course in database
	 * @param c Course to be inserted
	 */
	public void insertCourse(Course c)
    {
		Entity ent = new Entity(Global.DB_TABLE_COURSES);
		ent.setValue("id", c.getId());
		ent.setValue("name", c.getName());
		ent.save();
    }
	
	/**
	 * Inserts a notification in database
	 * @param n Notification to be inserted
	 */
	public void insertNotification(Notification n)
    {
		Entity ent = new Entity(Global.DB_TABLE_NOTIFICATIONS);
		ent.setValue("id", n.getId());
		ent.setValue("eventType", n.getEventType());
		ent.setValue("eventTime", n.getEventTime());
		ent.setValue("userSurname1", n.getUserSurname1());
		ent.setValue("userSurname2", n.getUserSurname2());
		ent.setValue("userFirstName", n.getUserFirstName());
		ent.setValue("location", n.getLocation());
		ent.setValue("summary", n.getSummary());
		ent.save();
    }
	
	/**
	 * Inserts a relation in database
	 * @param p Relation to be inserted
	 */
	public void insertPairTable(PairTable<?, ?> p)
    {
		String table = p.getTable();
		Pair<String, String> params = selectParamsPairTable(table);

		Entity ent = new Entity(table);
		ent.setValue(params.getFirst(), p.getFirst());
		ent.setValue(params.getSecond(), p.getSecond());
		ent.save();
    }
	
	/**
	 * Updates a course in database
	 * @param prev Course to be updated
	 * @param actual Updated course
	 */
	public void updateCourse(Course prev, Course actual)
    {
		List<Entity> rows = db.getEntityList(Global.DB_TABLE_COURSES, "id = " + prev.getId());
		Entity ent = rows.get(0);
		ent.setValue("id", actual.getId());
		ent.setValue("name", actual.getName());
		ent.save();
    }
	
	/**
	 * Updates a notification in database
	 * @param prev Notification to be updated
	 * @param actual Updated notification
	 */
	public void updateNotification(Notification prev, Notification actual)
    {
		List<Entity> rows = db.getEntityList(Global.DB_TABLE_NOTIFICATIONS, "id = " + prev.getId());
		Entity ent = rows.get(0);
		ent.setValue("id", actual.getId());
		ent.setValue("eventType", actual.getEventType());
		ent.setValue("eventTime", actual.getEventTime());
		ent.setValue("userSurname1", actual.getUserSurname1());
		ent.setValue("userSurname2", actual.getUserSurname2());
		ent.setValue("userFirstName", actual.getUserFirstName());
		ent.setValue("location", actual.getSummary());
		ent.save();
    }
	
	/**
	 * Updates a relation in database
	 * @param prev Relation to be updated
	 * @param actual Updated relation
	 */
	public void updatePairTable(PairTable<?, ?> prev, PairTable<?, ?> actual)
    {
		String table = prev.getTable();
		String where;
		Integer first = (Integer) prev.getFirst();
		Integer second = (Integer) prev.getSecond();
		Pair<String, String> params = selectParamsPairTable(table);
    	
		where = params.getFirst() + " = " + first + " AND " + params.getSecond() + " = " + second;

		List<Entity> rows = db.getEntityList(table, where);
		Entity ent = rows.get(0);
		ent.setValue(params.getFirst(), actual.getFirst());
		ent.setValue(params.getSecond(), actual.getSecond());
		ent.save();
    }
	
	/**
	 * Removes a course from database
	 * @param id Identifier of Course to be removed
	 */
	public void removeCourse(int id)
    {
		List<Entity> rows = db.getEntityList(Global.DB_TABLE_COURSES, "id = " + id);
		Entity ent = rows.get(0);		
		ent.delete();
		
		/*rows = db.getEntityList(Global.DB_TABLE_NOTICES_COURSES, "idcourse = " + id);
		Iterator<Entity> iter = rows.iterator();
		while (iter.hasNext()) {
		  ent = iter.next();
		  ent.delete();
		}*/
    }
	
	/**
	 * Removes a notification from database
	 * @param id Identifier of Notification to be removed
	 */
	public void removeNotification(int id)
    {
		List<Entity> rows = db.getEntityList(Global.DB_TABLE_NOTIFICATIONS, "id = " + id);
		Entity ent = rows.get(0);		
		ent.delete();
    }
	
	/**
	 * Removes a PairTable from database
	 * @param p PairTable to be removed
	 */
	public void removePairTable(PairTable p)
    {
		String table = p.getTable();
		Integer first = (Integer) p.getFirst();
		Integer second = (Integer) p.getSecond();
		String where;
		List<Entity> rows;
		Entity ent;
		Pair<String, String> params = selectParamsPairTable(table);
		
		where = params.getFirst() + " = " + first + " AND " + params.getSecond() + " = " + second;

		rows = db.getEntityList(table, where);
		ent = rows.get(0);
		ent.delete();
    }
	
	/**
	 * Empty table from database
	 * @param table Table to be emptied
	 */
	public void emptyTable(String table)
    {
		db.emptyTable(table);
    }
	
	/**
	 * Gets timestamp of last notification
	 * @return Timestamp of last notification
	 */
	public int getLastNotificationTimestamp()
    {
		List<Entity> rows = db.getEntityList(Global.DB_TABLE_NOTIFICATIONS);
		int timestamp;
		
		if(rows.size() > 0)
		{
			Entity ent = rows.get(rows.size()-1);
			timestamp = (Integer) ent.getValue("eventTime");
		} else {
			timestamp = 0;
		}
		
		return timestamp;
    }
	
	/**
	 * Gets id of last notification
	 * @return Id of last notification
	 */
	public int getLastNotificationId()
    {
		List<Entity> rows = db.getEntityList(Global.DB_TABLE_NOTIFICATIONS);
		int id;
		
		if(rows.size() > 0)
		{
			Entity ent = rows.get(rows.size()-1);
			id = (Integer) ent.getValue("id");
		} else {
			id = 0;
		}
		
		return id;
    }
}
