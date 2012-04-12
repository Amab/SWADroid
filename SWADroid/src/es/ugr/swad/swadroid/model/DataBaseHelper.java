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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;

import es.ugr.swad.swadroid.Global;

/**
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 * @author Antonio Aguilera Malagon <aguilerin@gmail.com>
 */
public class DataBaseHelper {
	/**
	 * Field for access to the database backend
	 */
	private DataFramework db;

	/**
	 * Constructor
	 * @param database Previously instantiated DataFramework object
	 */
	public DataBaseHelper(DataFramework database) {
		db = database;
	}

	/**
	 * Closes the database
	 */
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

	/**
	 * Selects the appropriated parameters for access a table
	 * @param table Table to be accessed
	 * @return A pair of strings containing the selected parameters
	 */
	private Pair<String, String> selectParamsPairTable(String table) {  
		String firstParam = null;
		String secondParam = null;

		if(table.equals(Global.DB_TABLE_TEST_QUESTIONS_COURSE)) {
			firstParam = "qstCod";
			secondParam = "crsCod";
		} else if(table.equals(Global.DB_TABLE_TEST_QUESTION_ANSWERS)) {
			firstParam = "qstCod";
			secondParam = "ansCod";
		} else if(table.equals(Global.DB_TABLE_USERS_COURSES)) {
			firstParam = "userCode";
			secondParam = "crsCod";
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
	@SuppressWarnings("rawtypes")
	private Model createObjectByTable(String table, Entity ent) {
		Model o = null;
		Pair<String, String> params;
		long id;

		if(table.equals(Global.DB_TABLE_COURSES)) {
			o = new Course(ent.getInt("id"),
					ent.getString("name"),
					ent.getInt("userRole"));
		} else if(table.equals(Global.DB_TABLE_TEST_QUESTIONS_COURSE) ||
				table.equals(Global.DB_TABLE_TEST_QUESTION_ANSWERS) ||
				table.equals(Global.DB_TABLE_USERS_COURSES)) {

			params = selectParamsPairTable(table);

			o = new PairTable<Integer, Integer>(table,
					ent.getInt(params.getFirst()),
					ent.getInt(params.getSecond()));
		} else if(table.equals(Global.DB_TABLE_NOTIFICATIONS)) {			
			o = new SWADNotification(ent.getInt("id"),
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
			PairTable q = (PairTable)getRow(Global.DB_TABLE_TEST_QUESTIONS_COURSE, "qstCod", Long.toString(id));

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
			id = ent.getId();
			int ansInd = ent.getInt("ansInd");
			PairTable a = (PairTable)getRow(Global.DB_TABLE_TEST_QUESTION_ANSWERS, "ansCod", Long.toString(id));

			if(a != null) {
				o = new TestAnswer(id,
						ansInd,
						(Integer) a.getFirst(),
						Global.parseStringBool(ent.getString("correct")), 
						ent.getString("answer"));
			} else {
				o = null;
			}
		} else if(table.equals(Global.DB_TABLE_TEST_TAGS)) {	
			id = ent.getInt("tagCod");
			TestTag t = (TestTag)getRow(Global.DB_TABLE_TEST_QUESTION_TAGS, "tagCod", Long.toString(id));

			if(t != null) {
				o = new TestTag(id,
						t.getQstCodList(),
						ent.getString("tagTxt"),
						ent.getInt("tagInd"));
			} else {
				o = null;
			}
		} else if(table.equals(Global.DB_TABLE_TEST_CONFIG)) {			
			o = new Test(ent.getInt("id"),  
					ent.getInt("min"),  
					ent.getInt("def"),  
					ent.getInt("max"),
					ent.getString("feedback"),
					ent.getLong("editTime"));
		} else if(table.equals(Global.DB_TABLE_TEST_QUESTION_TAGS)) {
			ArrayList<Integer> l = new ArrayList<Integer>();
			l.add(ent.getInt("qstCod"));
			o = new TestTag(ent.getInt("tagCod"),  
					l,
					null,  
					ent.getInt("tagInd"));
		} else if(table.equals(Global.DB_TABLE_USERS)) {
			o = new User(ent.getInt("userCode"),
					ent.getInt("userTypeCode"),
					null,								// wsKey
					ent.getString("userID"),
					ent.getString("userNickname"),
					ent.getString("userSurname1"),
					ent.getString("userSurname2"),
					ent.getString("userFirstname"),
					null,								// userTypeName
					ent.getString("photoPath"),
					ent.getInt("userRole"));			
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
		List<Entity> rows = db.getEntityList(table, fieldName + " = '" + fieldValue + "'");
		Entity ent;
		Model row = null;

		if(rows.size() > 0) {
			ent = rows.get(0);
			row = createObjectByTable(table, ent);
		}

		return row;
	}

	/**
	 * Gets the user that matches userId and selectedCourseCode
	 * @param userId User's DNI (national identity)
	 * @param selectedCourseCode Course code to be referenced
	 * @return The requested user, or null if the user does not exist or is not enrolled in the selected course
	 */
	public User getUser(String userID, long selectedCourseCode) {
		User u = (User) getRow(Global.DB_TABLE_USERS, "userID", userID);

		/*if (u != null) {
			String sentencia = "SELECT userCode AS _id, crsCod" +
					" FROM " + Global.DB_TABLE_USERS_COURSES + 
					" WHERE userCode = ? AND crsCod = ?" +
					" ORDER BY 1";

			Cursor c = db.getDB().rawQuery(sentencia, new String [] {
					String.valueOf(u.getId()), 
					String.valueOf(selectedCourseCode)
			});

			// Return null if the user is not enrolled in the selected course
			if (!c.moveToFirst()) {
				u = null;
			} 
			c.close();
		}*/
		return u;		
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
		ent.setValue("userRole", c.getUserRole());
		ent.save();
	}

	/**
	 * Inserts a notification in database
	 * @param n Notification to be inserted
	 */
	public void insertNotification(SWADNotification n)
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
	 * @param selectedCourseCode Course code to be referenced
	 */
	public void insertTestQuestion(TestQuestion q, long selectedCourseCode)
	{
		Entity ent = new Entity(Global.DB_TABLE_TEST_QUESTIONS);

		ent.setValue("id", q.getId());
		ent.setValue("ansType", q.getAnswerType());
		ent.setValue("stem", q.getStem());
		ent.setValue("shuffle", Global.parseBoolString(q.getShuffle()));
		ent.save();

		ent = new Entity(Global.DB_TABLE_TEST_QUESTIONS_COURSE);
		ent.setValue("qstCod", q.getId());
		ent.setValue("crsCod", selectedCourseCode);
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
		long id;

		ent.setValue("ansInd", a.getAnsInd());
		ent.setValue("answer", a.getAnswer());
		ent.setValue("correct", a.getCorrect());
		ent.save();
		id = ent.getId();

		ent = new Entity(Global.DB_TABLE_TEST_QUESTION_ANSWERS);
		ent.setValue("qstCod", qstCod);
		ent.setValue("ansCod", id);
		ent.save();
	}

	/**
	 * Inserts a test tag in database
	 * @param t Test tag to be inserted
	 */
	public void insertTestTag(TestTag t)
	{
		List<Entity> rows = db.getEntityList(Global.DB_TABLE_TEST_TAGS, "id = " + t.getId());

		if(rows.isEmpty()) {
			Entity ent = new Entity(Global.DB_TABLE_TEST_TAGS);

			ent.setValue("id", t.getId());
			ent.setValue("tagTxt", t.getTagTxt());
			ent.save();

			for(Integer i : t.getQstCodList()) {			
				ent = new Entity(Global.DB_TABLE_TEST_QUESTION_TAGS);
				ent.setValue("qstCod", i);
				ent.setValue("tagCod", t.getId());
				ent.setValue("tagInd", t.getTagInd());
				ent.save();
			}
		} else {
			throw new SQLException();
		}
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
	 * Inserts a user in database
	 * @param u User to be inserted
	 * @return True if user doesn't exist in database and is inserted. False otherwise.
	 */
	public boolean insertUser(User u) {
		List<Entity> rows = db.getEntityList(Global.DB_TABLE_USERS, "userCode = " + u.getId());

		if(rows.isEmpty()) {
			Entity ent = new Entity(Global.DB_TABLE_USERS);
			ent.setValue("userCode", u.getId());
			ent.setValue("userID", u.getUserID());
			ent.setValue("userNickname", u.getUserNickname());
			ent.setValue("userSurname1", u.getUserSurname1());
			ent.setValue("userSurname2", u.getUserSurname2());
			ent.setValue("userFirstname", u.getUserFirstname());
			ent.setValue("photoPath", u.getPhotoPath());
			ent.setValue("userTypeCode", u.getUserTypeCode());
			ent.setValue("userRole", u.getUserRole());
			ent.save();
			return true;
		} else
			return false;
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
		ent.setValue("userRole", actual.getUserRole());
		ent.save();
	}
	/**
	 * Updates a course in database
	 * @param id Course code of course to be updated
	 * @param actual Updated course
	 */
	public void updateCourse(long id, Course actual)
	{
		List<Entity> rows = db.getEntityList(Global.DB_TABLE_COURSES, "id = " + id);
		if(!rows.isEmpty()){
			Entity ent = rows.get(0);
			ent.setValue("id", actual.getId());
			ent.setValue("name", actual.getName());
			ent.setValue("userRole", actual.getUserRole());
			ent.save();
		}
	}

	/**
	 * Updates a notification in database
	 * @param prev Notification to be updated
	 * @param actual Updated notification
	 */
	public void updateNotification(SWADNotification prev, SWADNotification actual)
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
	 * @param selectedCourseCode Course code to be referenced
	 */
	public void updateTestQuestion(TestQuestion prev, TestQuestion actual, long selectedCourseCode)
	{
		List<Entity> rows = db.getEntityList(Global.DB_TABLE_TEST_QUESTIONS, "id = " + prev.getId());
		Entity ent = rows.get(0);

		ent.setValue("id", actual.getId());
		ent.setValue("ansType", actual.getAnswerType());
		ent.setValue("stem", actual.getStem());
		ent.setValue("shuffle", Global.parseBoolString(actual.getShuffle()));
		ent.save();

		rows = db.getEntityList(Global.DB_TABLE_TEST_QUESTIONS_COURSE, "qstCod = " + actual.getId());
		Iterator<Entity> iter = rows.iterator();
		while (iter.hasNext()) {
			ent = iter.next();
			ent.setValue("crsCod", selectedCourseCode);
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
		List<Entity> rows = db.getEntityList(Global.DB_TABLE_TEST_ANSWERS, "_id = " + prev.getId());
		Entity ent = rows.get(0);

		ent.setValue("ansInd", actual.getAnsInd());
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
		List<Integer> qstCodList = actual.getQstCodList();
		SQLiteStatement st = db.getDB().compileStatement("INSERT OR IGNORE INTO " +
				Global.DB_TABLE_TEST_QUESTION_TAGS + " VALUES (NULL, ?, ?, ?);");

		ent.setValue("id", actual.getId());
		ent.setValue("tagTxt", actual.getTagTxt());
		ent.save();

		for(Integer i : qstCodList) {
			st.bindLong(1, i);
			st.bindLong(2, actual.getId());
			st.bindLong(3, actual.getTagInd());
			st.executeInsert();	
		}		
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
	 * Updates a user in database
	 * @param prev User to be updated
	 * @param actual Updated user
	 */
	public void updateUser(User prev, User actual) {
		List<Entity> rows = db.getEntityList(Global.DB_TABLE_USERS, "id = " + prev.getId());
		Entity ent = rows.get(0);
		ent.setValue("userCode", actual.getId());
		ent.setValue("userID", actual.getUserID());
		ent.setValue("userNickname", actual.getUserNickname());
		ent.setValue("userSurname1", actual.getUserSurname1());
		ent.setValue("userSurname2", actual.getUserSurname2());
		ent.setValue("userFirstname", actual.getUserFirstname());
		ent.setValue("photoPath", actual.getPhotoPath());
		ent.setValue("userTypeCode", actual.getUserTypeCode());
		ent.setValue("userRole", actual.getUserRole());
		ent.save();
	}

	/**
	 * Removes a User from database
	 * @param u User to be removed
	 */
	public void removeUser(User u) {
		removeRow(Global.DB_TABLE_USERS, u.getId());
	}

	/**
	 * Removes a row from a database table
	 * @param id Identifier of row to be removed
	 */
	public void removeRow(String table, long id)
	{
		List<Entity> rows = db.getEntityList(table, "id = " + id);
		Entity ent = rows.get(0);		
		ent.delete();
	}

	/**
	 * Removes a PairTable from database
	 * @param p PairTable to be removed
	 */
	public void removePairTable(@SuppressWarnings("rawtypes") PairTable p)
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
	 * @param selectedCourseCode Test's course
	 * @return Last time the test was updated
	 */
	public String getTimeOfLastTestUpdate(long selectedCourseCode)
	{
		String where = "id=" + selectedCourseCode;
		String orderby = null;
		List<Entity> rows = db.getEntityList(Global.DB_TABLE_TEST_CONFIG, where, orderby);
		String f = "0";

		if(rows.size() > 0)
		{
			Entity ent = rows.get(0);
			f = (String) ent.getValue("editTime");
		}

		if(f == null) {
			f = "0";
		}

		return f;
	}

	/**
	 * Gets the tags of specified course ordered by tagInd field
	 * @param selectedCourseCode Test's course
	 * @return A list of the tags of specified course ordered by tagInd field
	 */
	public List<TestTag> getOrderedCourseTags(long selectedCourseCode)
	{
		String[] columns = {"T.id", "T.tagTxt", "Q.qstCod", "Q.tagInd"};
		String tables = Global.DB_TABLE_TEST_TAGS + " AS T, " + Global.DB_TABLE_TEST_QUESTION_TAGS
				+ " AS Q, "	+ Global.DB_TABLE_TEST_QUESTIONS_COURSE + " AS C";
		String where = "T.id=Q.tagCod AND Q.qstCod=C.qstCod AND C.crsCod=" + selectedCourseCode;
		String orderBy = "Q.tagInd ASC";
		String groupBy = "T.id";
		Cursor dbCursor = db.getDB().query(tables, columns, where, null, groupBy, null, orderBy);
		List<TestTag> result = new ArrayList<TestTag>();
		List<Integer> qstCodList;
		int idOld = -1;
		TestTag t = null;

		while(dbCursor.moveToNext()) {
			int id = dbCursor.getInt(0);

			if(id != idOld) {
				qstCodList = new ArrayList<Integer>();

				String tagTxt = dbCursor.getString(1);
				qstCodList.add(dbCursor.getInt(2));
				int tagInd = dbCursor.getInt(3);

				t = new TestTag(id, qstCodList, tagTxt, tagInd);

				result.add(t);
				idOld = id;
			} else {
				t.addQstCod(dbCursor.getInt(2));
			}
		}

		return result;
	}

	/**
	 * Gets the questions of specified course and tags
	 * @param selectedCourseCode Test's course
	 * @param tagsList Tag's list of the questions to be extracted
	 * @return A list of the questions of specified course and tags
	 */
	public List<TestQuestion> getRandomCourseQuestionsByTagAndAnswerType(long selectedCourseCode, List<TestTag> tagsList,
			List<String> answerTypesList, int maxQuestions)
			{
		String select = "SELECT DISTINCT Q.id, Q.ansType, Q.shuffle, Q.stem";
		String tables = " FROM " + Global.DB_TABLE_TEST_QUESTIONS + " AS Q, "
				+ Global.DB_TABLE_TEST_QUESTIONS_COURSE + " AS C, "
				+ Global.DB_TABLE_TEST_QUESTION_TAGS + " AS T";
		String where = " WHERE Q.id=C.qstCod AND Q.id=T.qstCod AND C.crsCod=" + selectedCourseCode;
		String orderby = " ORDER BY RANDOM()";
		String limit = " LIMIT " + maxQuestions;
		Cursor dbCursorQuestions, dbCursorAnswers;
		List<TestQuestion> result = new ArrayList<TestQuestion>();
		List<TestAnswer> answers = new ArrayList<TestAnswer>();
		int tagsListSize = tagsList.size();
		int answerTypesListSize = answerTypesList.size();

		if(!tagsList.get(0).getTagTxt().equals("all")) {
			where += " AND (";
			for(int i=0; i<tagsListSize; i++) {
				where += "T.tagCod=" + tagsList.get(i).getId();
				if(i < tagsListSize-1) {
					where +=  " OR ";
				}
			}
			where += ")";

			if(!answerTypesList.get(0).equals("all")) {
				where +=  " AND ";
			}
		}

		if(!answerTypesList.get(0).equals("all")) {
			if(tagsList.get(0).getTagTxt().equals("all")) {
				where +=  " AND ";
			}

			where += "(";
			for(int i=0; i<answerTypesListSize; i++) {
				where += "Q.ansType='" + answerTypesList.get(i) + "'";

				if(i < answerTypesListSize-1) {
					where += " OR ";
				}
			}		
			where += ")";
		}

		dbCursorQuestions = db.getDB().rawQuery(select + tables + where + orderby + limit, null);

		select = "SELECT DISTINCT A._id, A.ansInd, A.answer, A.correct";
		tables = " FROM " + Global.DB_TABLE_TEST_ANSWERS + " AS A, "
				+ Global.DB_TABLE_TEST_QUESTION_ANSWERS + " AS Q";
		orderby = " ORDER BY A.ansInd";

		while(dbCursorQuestions.moveToNext()) {
			int qstCod = dbCursorQuestions.getInt(0);
			String ansType = dbCursorQuestions.getString(1);
			boolean shuffle = Global.parseStringBool(dbCursorQuestions.getString(2));
			String stem = dbCursorQuestions.getString(3);
			TestQuestion q = new TestQuestion(qstCod, selectedCourseCode, stem, ansType, shuffle);			

			where = " WHERE Q.qstCod=" + qstCod + " AND Q.ansCod=A._id";
			dbCursorAnswers = db.getDB().rawQuery(select + tables + where + orderby, null);
			answers = new ArrayList<TestAnswer>();
			while(dbCursorAnswers.moveToNext()) {
				long ansCod = dbCursorAnswers.getLong(0);
				int ansInd = dbCursorAnswers.getInt(1);
				String answer = dbCursorAnswers.getString(2);
				boolean correct = dbCursorAnswers.getString(3).equals("true") ? true: false;

				answers.add(new TestAnswer(ansCod, ansInd, qstCod, correct, answer));
			}

			q.setAnswers(answers);
			result.add(q);
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

	/**
	 * Empty table from database
	 * @param table Table to be emptied
	 */
	public void emptyTable(String table)
	{
		db.emptyTable(table);
	}

	/**
	 * Delete table from database
	 * @param table Table to be deleted
	 */
	public void deleteTable(String table)
	{
		db.deleteTable(table);
	}

	/**
	 * Delete all tables from database
	 */
	public void clearDB()
	{
		db.deleteTables();
	}

	/**
	 * Clean data of all tables from database
	 */
	public void cleanTables()
	{
		emptyTable(Global.DB_TABLE_NOTIFICATIONS);
		emptyTable(Global.DB_TABLE_COURSES);
		emptyTable(Global.DB_TABLE_TEST_QUESTION_ANSWERS);
		emptyTable(Global.DB_TABLE_TEST_QUESTION_TAGS);
		emptyTable(Global.DB_TABLE_TEST_QUESTIONS_COURSE);
		emptyTable(Global.DB_TABLE_TEST_ANSWERS);
		emptyTable(Global.DB_TABLE_TEST_CONFIG);
		emptyTable(Global.DB_TABLE_TEST_QUESTIONS);
		emptyTable(Global.DB_TABLE_TEST_TAGS);
		emptyTable(Global.DB_TABLE_USERS_COURSES);
		emptyTable(Global.DB_TABLE_USERS);
		compactDB();
	}

	/**
	 * Begin a database transaction
	 */
	public void beginTransaction() {
		db.getDB().execSQL("BEGIN;");
	}
	/**
	 * End a database transaction
	 */
	public void endTransaction() {
		db.getDB().execSQL("END;");
	}

	/**
	 * Compact the database
	 */
	public void compactDB() {
		db.getDB().execSQL("VACUUM;");
	}

	/**
	 * Initializes the database structure for the first use
	 */
	public void initializeDB() {
		db.getDB().execSQL("CREATE UNIQUE INDEX " + Global.DB_TABLE_TEST_QUESTION_TAGS + "_unique on " 
				+ Global.DB_TABLE_TEST_QUESTION_TAGS + "(qstCod, tagCod);");
	}

	/**
	 * Upgrades the database structure
	 * @throws IOException 
	 * @throws XmlPullParserException 
	 */
	public void upgradeDB(Context context) throws XmlPullParserException, IOException {    	
		//cleanTables();    	
		//initializeDB();
		compactDB();

		/*db.getDB().execSQL("CREATE TEMPORARY TABLE __"
                + Global.DB_TABLE_NOTIFICATIONS
                + " (_id INTEGER PRIMARY KEY AUTOINanCREMENT, id INTEGER, eventType TEXT, eventTime TEXT,"
                + " userSurname1 TEXT, userSurname2 TEXT, userFirstname TEXT, location TEXT, summary TEXT," 
                + "status TEXT, content TEXT); "
                + "INSERT INTO __" + Global.DB_TABLE_NOTIFICATIONS + " SELECT _id, id, eventType, eventTime, "
                + "userSurname1, userSurname2, userFirstname, location, summary, status, content FROM "
                + Global.DB_TABLE_NOTIFICATIONS + ";");

    	deleteTable(Global.DB_TABLE_TEST_QUESTION_ANSWERS);
    	deleteTable(Global.DB_TABLE_TEST_QUESTION_TAGS);
    	deleteTable(Global.DB_TABLE_TEST_QUESTIONS_COURSE);
    	deleteTable(Global.DB_TABLE_COURSES);
    	deleteTable(Global.DB_TABLE_TEST_ANSWERS);
    	deleteTable(Global.DB_TABLE_TEST_CONFIG);
    	deleteTable(Global.DB_TABLE_TEST_QUESTIONS);
    	deleteTable(Global.DB_TABLE_TEST_TAGS);
    	deleteTable(Global.DB_TABLE_NOTIFICATIONS);
		deleteTable(Global.DB_TABLE_USERS_COURSES);
		deleteTable(Global.DB_TABLE_USERS);

        db.createTables();

    	db.getDB().execSQL("INSERT INTO " + Global.DB_TABLE_NOTIFICATIONS + " SELECT _id, id, eventType, eventTime, "
                + "userSurname1, userSurname2, userFirstname, location, summary, status, content FROM __"
                + Global.DB_TABLE_NOTIFICATIONS + ";"
                + "DROP TABLE __" + Global.DB_TABLE_NOTIFICATIONS + ";");*/
	}
}
