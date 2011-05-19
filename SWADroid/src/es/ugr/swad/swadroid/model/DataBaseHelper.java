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

import android.database.Cursor;
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
	 * Gets DB object
	 * @return DataFramework DB object
	 */
	public DataFramework getDb() {
		return db;
	}

	/**
	 * Sets DB object
	 * @param db DataFramework DB object
	 */
	public void setDb(DataFramework db) {
		this.db = db;
	}
	
	private Pair<String, String> selectParamsPairTable(String table) {  
		String firstParam = null;
		String secondParam = null;
		
    	if(table.equals(Global.DB_TABLE_TEST_QUESTIONS_COURSE)) {
    		firstParam = "qstCod";
    		secondParam = "crsCod";
    	} else if(table.equals(Global.DB_TABLE_TEST_QUESTION_ANSWERS)) {
    		firstParam = "qstCod";
    		secondParam = "ansCod";
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
		Integer id;
		
		if(table.equals(Global.DB_TABLE_COURSES)) {
			o = new Course(ent.getInt("id"),
							ent.getString("name"));
		} else if(table.equals(Global.DB_TABLE_TEST_QUESTIONS_COURSE) ||
				table.equals(Global.DB_TABLE_TEST_QUESTION_ANSWERS)) {
			
			params = selectParamsPairTable(table);
			
			o = new PairTable<Integer, Integer>(table,
					ent.getInt(params.getFirst()),
					ent.getInt(params.getSecond()));
		} else if(table.equals(Global.DB_TABLE_NOTIFICATIONS)) {			
			o = new Notification(ent.getInt("id"),
					ent.getString("eventType"), 
					ent.getLong("eventTime"), 
					ent.getString("userSurname1"), 
					ent.getString("userSurname2"), 
					ent.getString("userFirstname"), 
					ent.getString("location"), 
					ent.getString("summary"), 
					ent.getInt("status"), 
					ent.getString("content"));
		} else if(table.equals(Global.DB_TABLE_TEST_QUESTIONS)) {
			id = ent.getInt("id");
			PairTable q = (PairTable)getRow(Global.DB_TABLE_TEST_QUESTIONS_COURSE, "qstCod", id.toString());

			if(q != null) {
				o = new TestQuestion(id,
					(Integer) q.getFirst(),
					ent.getString("stem"), 
					ent.getString("ansType"), 
					Global.parseStringBool(ent.getString("shuffle")));
			} else {
				o = null;
			}
		} else if(table.equals(Global.DB_TABLE_TEST_ANSWERS)) {	
			id = ent.getInt("id");
			PairTable a = (PairTable)getRow(Global.DB_TABLE_TEST_QUESTION_ANSWERS, "ansCod", id.toString());

			if(a != null) {
				o = new TestAnswer(id,
					(Integer) a.getFirst(),
					Global.parseStringBool(ent.getString("correct")), 
					ent.getString("answer"));
			} else {
				o = null;
			}
		} else if(table.equals(Global.DB_TABLE_TEST_TAGS)) {	
			id = ent.getInt("tagCod");
			TestTag t = (TestTag)getRow(Global.DB_TABLE_TEST_QUESTION_TAGS, "tagCod", id.toString());
					
			if(t != null) {
				o = new TestTag(id,
					t.getQstCod(),
					ent.getString("tagTxt"),
					ent.getInt("tagInd"));
			} else {
				o = null;
			}
		} else if(table.equals(Global.DB_TABLE_TEST_CONFIG)) {			
			o = new Test(null, 
					ent.getInt("id"),  
					ent.getInt("min"),  
					ent.getInt("def"),  
					ent.getInt("max"),
					ent.getString("feedback"),
					ent.getLong("editTime"));
		} else if(table.equals(Global.DB_TABLE_TEST_QUESTION_TAGS)) {			
			o = new TestTag(ent.getInt("tagCod"),  
					ent.getInt("qstCod"),
					null,  
					ent.getInt("tagInd"));
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
	 * Gets the rows of specified table that matches "where" condition. The rows are ordered as says the "orderby"
	 * parameter
	 * @param table Table containing the rows
	 * @param where Where condition of SQL sentence
	 * @param orderby Orderby part of SQL sentence
	 * @return A list of Model's subclass objects
	 */
	public List<Model> getAllRows(String table, String where, String orderby)
    {
		List<Model> result = new ArrayList<Model>();		
		List<Entity> rows = db.getEntityList(table, where, orderby);
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
	 * Gets a row of specified table
	 * @param table Table containing the rows
	 * @param fieldName Field's name
	 * @param fieldValue Field's value
	 * @return A Model's subclass object
	 */
	public Model getRow(String table, String fieldName, String fieldValue)
    {
		List<Entity> rows = db.getEntityList(table, fieldName + " = " + fieldValue);
		Entity ent;
		Model row = null;
		
		if(rows.size() > 0) {
			ent = rows.get(0);
			row = createObjectByTable(table, ent);
		}
        
        return row;
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
		
		String eventTime = String.valueOf(n.getEventTime());
		String status = String.valueOf(n.getStatus());
		
		ent.setValue("id", n.getId());
		ent.setValue("eventType", n.getEventType());
		ent.setValue("eventTime", eventTime);
		ent.setValue("userSurname1", n.getUserSurname1());
		ent.setValue("userSurname2", n.getUserSurname2());
		ent.setValue("userFirstname", n.getUserFirstName());
		ent.setValue("location", n.getLocation());
		ent.setValue("summary", n.getSummary());
		ent.setValue("status", status);
		ent.setValue("content", n.getContent());
		ent.save();
    }
	
	/**
	 * Inserts a test question in database
	 * @param q Test question to be inserted
	 * @param crsCod Course code to be referenced
	 */
	public void insertTestQuestion(TestQuestion q, int crsCod)
    {
		Entity ent = new Entity(Global.DB_TABLE_TEST_QUESTIONS);
		
		ent.setValue("id", q.getId());
		ent.setValue("ansType", q.getAnstype());
		ent.setValue("stem", q.getStem());
		ent.setValue("shuffle", Global.parseBoolString(q.getShuffle()));
		ent.save();
		
		ent = new Entity(Global.DB_TABLE_TEST_QUESTIONS_COURSE);
		ent.setValue("qstCod", q.getId());
		ent.setValue("crsCod", crsCod);
		ent.save();
    }
	
	/**
	 * Inserts a test answer in database
	 * @param a Test answer to be inserted
	 * @param qstCod Test question code to be referenced
	 */
	public void insertTestAnswer(TestAnswer a, int qstCod)
    {
		Entity ent = new Entity(Global.DB_TABLE_TEST_ANSWERS);
		
		ent.setValue("id", a.getId());
		ent.setValue("answer", a.getAnswer());
		ent.setValue("correct", a.getCorrect());
		ent.save();
		
		ent = new Entity(Global.DB_TABLE_TEST_QUESTION_ANSWERS);
		ent.setValue("qstCod", qstCod);
		ent.setValue("ansCod", a.getId());
		ent.save();
    }
	
	/**
	 * Inserts a test tag in database
	 * @param t Test tag to be inserted
	 */
	public void insertTestTag(TestTag t)
    {
		Entity ent = new Entity(Global.DB_TABLE_TEST_TAGS);
		
		ent.setValue("id", t.getId());
		ent.setValue("tagTxt", t.getTagTxt());
		ent.save();
		
		ent = new Entity(Global.DB_TABLE_TEST_QUESTION_TAGS);
		ent.setValue("qstCod", t.getQstCod());
		ent.setValue("tagCod", t.getId());
		ent.setValue("tagInd", t.getTagInd());
		ent.save();
    }
	
	/**
	 * Inserts a test config in database
	 * @param t Test config to be inserted
	 */
	public void insertTestConfig(Test t)
    {
		Entity ent = new Entity(Global.DB_TABLE_TEST_CONFIG);
		
		ent.setValue("id", t.getId());
		ent.setValue("min", t.getMin());
		ent.setValue("def", t.getDef());
		ent.setValue("max", t.getMax());
		ent.setValue("feedback", t.getFeedback());
		ent.setValue("editTime", t.getEditTime());
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
		
		String eventTime = String.valueOf(actual.getEventTime());
		String status = String.valueOf(actual.getStatus());
		
		ent.setValue("id", actual.getId());
		ent.setValue("eventType", actual.getEventType());
		ent.setValue("eventTime", eventTime);
		ent.setValue("userSurname1", actual.getUserSurname1());
		ent.setValue("userSurname2", actual.getUserSurname2());
		ent.setValue("userFirstName", actual.getUserFirstName());
		ent.setValue("location", actual.getLocation());
		ent.setValue("summary", actual.getSummary());
		ent.setValue("status", status);
		ent.setValue("content", actual.getContent());
		ent.save();
    }
	
	/**
	 * Updates a test question in database
	 * @param prev Test question to be updated
	 * @param actual Updated test question
	 * @param crsCod Course code to be referenced
	 */
	public void updateTestQuestion(TestQuestion prev, TestQuestion actual, int crsCod)
    {
		List<Entity> rows = db.getEntityList(Global.DB_TABLE_TEST_QUESTIONS, "id = " + prev.getId());
		Entity ent = rows.get(0);
		
		ent.setValue("id", actual.getId());
		ent.setValue("ansType", actual.getAnstype());
		ent.setValue("stem", actual.getStem());
		ent.setValue("shuffle", Global.parseBoolString(actual.getShuffle()));
		ent.save();
		
		rows = db.getEntityList(Global.DB_TABLE_TEST_QUESTIONS_COURSE, "qstCod = " + actual.getId());
		Iterator<Entity> iter = rows.iterator();
		while (iter.hasNext()) {
		  ent = iter.next();
		  ent.setValue("crsCod", crsCod);
		  ent.save();
		}
    }
	
	/**
	 * Updates a test answer in database
	 * @param prev Test answer to be updated
	 * @param actual Updated test answer
	 * @param qstCod Test question code to be referenced
	 */
	public void updateTestAnswer(TestAnswer prev, TestAnswer actual, int qstCod)
    {
		List<Entity> rows = db.getEntityList(Global.DB_TABLE_TEST_ANSWERS, "id = " + prev.getId());
		Entity ent = rows.get(0);
		
		ent.setValue("id", actual.getId());
		ent.setValue("answer", actual.getAnswer());
		ent.setValue("correct", actual.getCorrect());
		ent.save();
		
		rows = db.getEntityList(Global.DB_TABLE_TEST_QUESTION_ANSWERS, "ansCod = " + actual.getId());
		Iterator<Entity> iter = rows.iterator();
		while (iter.hasNext()) {
		  ent = iter.next();
		  ent.setValue("qstCod", qstCod);
		  ent.save();
		}
    }
	
	/**
	 * Updates a test tag in database
	 * @param prev Test tag to be updated
	 * @param actual Updated test tag
	 */
	public void updateTestTag(TestTag prev, TestTag actual)
    {
		List<Entity> rows = db.getEntityList(Global.DB_TABLE_TEST_TAGS, "id = " + prev.getId());
		Entity ent = rows.get(0);
		
		ent.setValue("id", actual.getId());
		ent.setValue("tagTxt", actual.getTagTxt());
		ent.save();

		ent = new Entity(Global.DB_TABLE_TEST_QUESTION_TAGS);
		ent.setValue("tagCod", actual.getId());
		ent.setValue("qstCod", actual.getQstCod());
		ent.setValue("tagInd", actual.getTagInd());
		ent.save();
    }
	
	/**
	 * Updates a test config in database
	 * @param prev Test to be updated
	 * @param actual Updated test
	 */
	public void updateTestConfig(Test prev, Test actual)
    {
		List<Entity> rows = db.getEntityList(Global.DB_TABLE_TEST_CONFIG, "id = " + prev.getId());
		Entity ent = rows.get(0);
		ent.setValue("id", actual.getId());
		ent.setValue("min", actual.getMin());
		ent.setValue("def", actual.getDef());
		ent.setValue("max", actual.getMax());
		ent.setValue("feedback", actual.getFeedback());
		ent.setValue("editTime", actual.getEditTime());
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
	 * Removes a row from a database table
	 * @param id Identifier of row to be removed
	 */
	public void removeRow(String table, int id)
    {
		List<Entity> rows = db.getEntityList(table, "id = " + id);
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
	 * Gets a field of last notification
	 * @param field A field of last notification
	 * @return The field of last notification
	 */
	public String getFieldOfLastNotification(String field)
    {
		String where = null;
		String orderby = "eventTime DESC";
		List<Entity> rows = db.getEntityList(Global.DB_TABLE_NOTIFICATIONS, where, orderby);
		String f = "0";
		
		if(rows.size() > 0)
		{
			Entity ent = rows.get(0);
			f = (String) ent.getValue(field);
		}
		
		return f;
    }
	
	/**
	 * Gets last time the test was updated
	 * @param crsCode Test's course
	 * @return Last time the test was updated
	 */
	public String getTimeOfLastTestUpdate(int crsCode)
    {
		String where = "id=" + crsCode;
		String orderby = null;
		List<Entity> rows = db.getEntityList(Global.DB_TABLE_TEST_CONFIG, where, orderby);
		String f = "0";
		
		if(rows.size() > 0)
		{
			Entity ent = rows.get(0);
			f = (String) ent.getValue("editTime");
		}

		return f;
    }
	

	
	/**
	 * Gets the tags of specified course ordered by tagInd field
	 * @param crsCode Test's course
	 * @return A list of the tags of specified course ordered by tagInd field
	 */
	public List<TestTag> getOrderedCourseTags(int crsCode)
    {
		String select = "SELECT DISTINCT T.id, T.tagTxt, Q.qstCod, Q.tagInd";
		String tables = " FROM " + Global.DB_TABLE_TEST_TAGS + " AS T, " + Global.DB_TABLE_TEST_QUESTION_TAGS
			+ " AS Q, "	+ Global.DB_TABLE_TEST_QUESTIONS_COURSE + " AS C";
		String where = " WHERE T.id=Q.tagCod AND Q.qstCod=C.qstCod AND C.crsCod=" + crsCode;
		String orderby = " GROUP BY T.id ORDER BY Q.tagInd ASC";
		Cursor dbCursor = db.getDB().rawQuery(select + tables + where + orderby, null);
		List<TestTag> result = new ArrayList<TestTag>();
		
		while(dbCursor.moveToNext()) {			
			int id = dbCursor.getInt(0);
			String tagTxt = dbCursor.getString(1);
			int qstCod = dbCursor.getInt(2);
			int tagInd = dbCursor.getInt(3);
			
			result.add(new TestTag(id, qstCod, tagTxt, tagInd));
		}
        
        return result;
    }
	
	/**
	 * Clear old notifications
	 * @param size Max table size 
	 */
	public void clearOldNotifications(int size)
	{
		String where = null;
		String orderby = "eventTime ASC";
		List<Entity> rows = db.getEntityList(Global.DB_TABLE_NOTIFICATIONS, where, orderby);
		int numRows = rows.size();
		int numDeletions = numRows - size;
		
		if(numRows > size)
		{
			for(int i=0; i<numDeletions; i++)
				rows.get(i).delete();
		}
	}
}
