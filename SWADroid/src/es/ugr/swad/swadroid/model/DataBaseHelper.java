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
		
    	if(table.equals(Global.DB_TABLE_NOTICES_COURSES)) {
    		firstParam = "idcourse";
    		secondParam = "idnotice";
    	} else if(table.equals(Global.DB_TABLE_STUDENTS_COURSES)) {
    		firstParam = "idcourse";
    		secondParam = "idstudent";
    	} else if(table.equals(Global.DB_TABLE_TEST_QUESTIONS_COURSES)) {
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
		} else if(table.equals(Global.DB_TABLE_NOTICES)) {
			o = new Notice(ent.getInt("id"),
							ent.getInt("timestamp"),
							ent.getString("description"));
		/*} else if(table.equals(Global.DB_TABLE_STUDENTS)) {
			o = new Student((Integer) ent.getInt(0),
							(String) ent.getString(1),
							(String) ent.getString(2),
							(String) ent.getString(3),
							(String) ent.getString(4));
		} else if(table.equals(Global.DB_TABLE_TEST_ANSWERS)) {
			o = new TestAnswer((Integer) ent.getInt(0),
					(Boolean) parseIntBool(ent.getInt(1)),
					(String) ent.getString(2));
		} else if(table.equals(Global.DB_TABLE_TEST_QUESTIONS)) {
			o = new TestQuestion((Integer) ent.getInt(0),
					(String) ent.getString(1),
					(String) ent.getString(2),
					(Integer) ent.getInt(3),
					(Boolean) parseStringBool(ent.getString(4)),
					(Float) ent.getFloat(5));
		} else if(table.equals(Global.DB_TABLE_MSG_CONTENT)) {
			o = new MessageContent((Integer) ent.getInt(0),
					(String) ent.getString(1),
					(String) ent.getString(2),
					(Boolean) parseStringBool(ent.getString(3)),
					(Integer) ent.getInt(4));
		} else if(table.equals(Global.DB_TABLE_MSG_RCV)) {
			o = new MessageReceived((Integer) ent.getInt(0),
					(String) ent.getString(1),
					(String) ent.getString(2),
					(Integer) ent.getInt(3),
					(Boolean) parseStringBool(ent.getString(4)),
					(Boolean) parseStringBool(ent.getString(5)),
					(Boolean) parseStringBool(ent.getString(6)),
					(Boolean) parseStringBool(ent.getString(7)));
		} else if(table.equals(Global.DB_TABLE_MSG_SNT)) {
			o = new MessageSent((Integer) ent.getInt(0),
					(String) ent.getString(1),
					(String) ent.getString(2),
					(Boolean) parseStringBool(ent.getString(3)),
					(Integer) ent.getInt(4),
					(Integer) ent.getInt(5),
					(String) ent.getString(6));
		} else if(table.equals(Global.DB_TABLE_MARKS)) {
			o = new Mark((Integer) ent.getInt(0),
					(Integer) ent.getInt(1),
					(Integer) ent.getInt(2),
					(String) ent.getString(3),
					(Integer) ent.getInt(4),
					(Integer) ent.getInt(5));*/
		} else if(table.equals(Global.DB_TABLE_NOTICES_COURSES) ||
				table.equals(Global.DB_TABLE_STUDENTS_COURSES) ||
				table.equals(Global.DB_TABLE_TEST_QUESTIONS_COURSES)) {
			
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
	 * Inserts a notice in database
	 * @param n Notice to be inserted
	 */
	public void insertNotice(Notice n)
    {
		Entity ent = new Entity(Global.DB_TABLE_NOTICES);
		ent.setValue("id", n.getId());
		ent.setValue("timestamp", n.getTimestamp());
		ent.setValue("description", n.getDescription());
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
	 * Updates a notice in database
	 * @param prev Notice to be updated
	 * @param actual Updated notice
	 */
	public void updateNotice(Notice prev, Notice actual)
    {
		List<Entity> rows = db.getEntityList(Global.DB_TABLE_NOTICES, "id = " + prev.getId());
		Entity ent = rows.get(0);
		ent.setValue("id", actual.getId());
		ent.setValue("timestamp", actual.getTimestamp());
		ent.setValue("description", actual.getDescription());
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
		
		rows = db.getEntityList(Global.DB_TABLE_NOTICES_COURSES, "idcourse = " + id);
		Iterator<Entity> iter = rows.iterator();
		while (iter.hasNext()) {
		  ent = iter.next();
		  ent.delete();
		}
    }
	
	/**
	 * Removes a notice from database
	 * @param id Identifier of Notice to be removed
	 */
	public void removeNotice(int id)
    {
		List<Entity> rows = db.getEntityList(Global.DB_TABLE_NOTICES, "id = " + id);
		Entity ent = rows.get(0);		
		ent.delete();
		
		rows = db.getEntityList(Global.DB_TABLE_NOTICES_COURSES, "idnotice = " + id);
		Iterator<Entity> iter = rows.iterator();
		while (iter.hasNext()) {
		  ent = iter.next();
		  ent.delete();
		}
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
}
