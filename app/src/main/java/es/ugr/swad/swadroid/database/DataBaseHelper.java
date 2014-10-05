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
package es.ugr.swad.swadroid.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Environment;
import android.util.Log;

import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.Preferences;
import es.ugr.swad.swadroid.SWADroidTracker;
import es.ugr.swad.swadroid.model.Course;
import es.ugr.swad.swadroid.model.Group;
import es.ugr.swad.swadroid.model.GroupType;
import es.ugr.swad.swadroid.model.Model;
import es.ugr.swad.swadroid.model.Pair;
import es.ugr.swad.swadroid.model.PairTable;
import es.ugr.swad.swadroid.model.PracticeSession;
import es.ugr.swad.swadroid.model.SWADNotification;
import es.ugr.swad.swadroid.model.Test;
import es.ugr.swad.swadroid.model.TestAnswer;
import es.ugr.swad.swadroid.model.TestQuestion;
import es.ugr.swad.swadroid.model.TestTag;
import es.ugr.swad.swadroid.model.User;
import es.ugr.swad.swadroid.utils.Crypto;
import es.ugr.swad.swadroid.utils.OldCrypto;
import es.ugr.swad.swadroid.utils.Utils;

/**
 * Helper for database operations
 * 
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 * @author Antonio Aguilera Malagon <aguilerin@gmail.com>
 * @author Helena Rodriguez Gijon <hrgijon@gmail.com>
 */
public class DataBaseHelper {
    /**
     * DataBaseHelper tag name for Logcat
     */
    private static final String TAG = Constants.APP_TAG + " DataBaseHelper";
	/**
     * Field for access to the database backend
     */
    private DataFramework db;
    /**
     * Application context
     */
    private final Context mCtx;
    /**
     * Database passphrase
     */
    private String DBKey;
    /**
     * Database passphrase length
     */
    private final int DB_KEY_LENGTH = 128;
    /**
     * Cryptographic object
     */
    private final Crypto crypto;
	/**
	 * Indicates if there are changes on db
	 */
	public static boolean dbCleaned = false;
	/**
	 * Table name for courses
	 */
	public static final String DB_TABLE_COURSES = "courses";
	/**
	 * Table name for notifications
	 */
	public static final String DB_TABLE_NOTIFICATIONS = "notifications";
	/**
	 * Table name for test's answers
	 */
	public static final String DB_TABLE_TEST_ANSWERS = "tst_answers";
	/**
	 * Table name for test's questions
	 */
	public static final String DB_TABLE_TEST_QUESTIONS = "tst_questions";
	/**
	 * Table name for test's tags
	 */
	public static final String DB_TABLE_TEST_TAGS = "tst_tags";
	/**
	 * Table name for test's configuration
	 */
	public static final String DB_TABLE_TEST_CONFIG = "tst_config";
	/**
	 * Table name for relationship between test's questions and tags
	 */
	public static final String DB_TABLE_TEST_QUESTION_TAGS = "tst_question_tags";
	/**
	 * Table name for relationship between test's questions and courses
	 */
	public static final String DB_TABLE_TEST_QUESTIONS_COURSE = "tst_questions_course";
	/**
	 * Table name for relationship between test's questions and answers
	 */
	public static final String DB_TABLE_TEST_QUESTION_ANSWERS = "tst_question_answers";
	/**
	 * Table name for users
	 */
	public static final String DB_TABLE_USERS = "users";
	/**
	 * Table name for relationship between users and courses
	 */
	public static final String DB_TABLE_USERS_COURSES = "users_courses";
	/**
	 * Table name for groups
	 */
	public static final String DB_TABLE_GROUPS = "groups";
	/**
	 * Table name for relationship between groups and courses
	 */
	public static final String DB_TABLE_GROUPS_COURSES = "group_course";
	/**
	 * Table name for group types
	 */
	public static final String DB_TABLE_GROUP_TYPES = "group_types";
	/**
	 * Table name for relationship between groups and group types
	 */
	public static final String DB_TABLE_GROUPS_GROUPTYPES = "group_grouptypes";
	/**
	 * Table name for practice sessions
	 */
	public static final String DB_TABLE_PRACTICE_SESSIONS = "practice_sessions";
	/**
	 * Table name for rollcall
	 */
	public static final String DB_TABLE_ROLLCALL = "rollcall";

    /**
     * Constructor
     * @throws IOException 
     * @throws XmlPullParserException 
     */
    public DataBaseHelper(Context ctx) throws XmlPullParserException, IOException {
        mCtx = ctx;
        DBKey = Preferences.getDBKey();
        db = DataFramework.getInstance();

        db.open(mCtx, mCtx.getPackageName());

        //If the passphrase is empty, generate a random passphrase and recreate database
        if (DBKey.equals("")) {
            DBKey = Utils.randomString(DB_KEY_LENGTH);
            Preferences.setDBKey(DBKey);
        }

        crypto = new Crypto(ctx, DBKey);
        //Log.d("DataBaseHelper", "DBKey=" + DBKey);
    }

    /**
     * Closes the database
     */
    public synchronized void close() {
        db.close();
    }

    /**
     * Gets DB object
     *
     * @return DataFramework DB object
     */
    public DataFramework getDb() {
        return db;
    }

    /**
     * Sets DB object
     *
     * @param db DataFramework DB object
     */
    public void setDb(DataFramework db) {
        this.db = db;
    }

    /**
     * Selects the appropriated parameters for access a table
     *
     * @param table Table to be accessed
     * @return A pair of strings containing the selected parameters
     */
    private Pair<String, String> selectParamsPairTable(String table) {
        String firstParam = null;
        String secondParam = null;

        if (table.equals(DataBaseHelper.DB_TABLE_TEST_QUESTIONS_COURSE)) {
            firstParam = "qstCod";
            secondParam = "crsCod";
        } else if (table.equals(DataBaseHelper.DB_TABLE_TEST_QUESTION_ANSWERS)) {
            firstParam = "qstCod";
            secondParam = "ansCod";
        } else if (table.equals(DataBaseHelper.DB_TABLE_USERS_COURSES)) {
            firstParam = "userCode";
            secondParam = "crsCod";
        } else if (table.equals(DataBaseHelper.DB_TABLE_GROUPS_COURSES)) {
            firstParam = "grpCod";
            secondParam = "crsCod";
        } else if (table.equals(DataBaseHelper.DB_TABLE_GROUPS_GROUPTYPES)) {
            firstParam = "grpTypCod";
            secondParam = "grpCod";
        } else {
            Log.e("selectParamsPairTable", "Table " + table + " not exists");
        }

        return new Pair<String, String>(firstParam, secondParam);
    }
    
    /**
     * Gets ParTable class from table
     * @param <T>
     * */
    /*	private <T> PairTable<T, T> getPairTable(String table, T firstValue, T secondValue){
		PairTable<T,T> par;
		if(table.equals(Global.DB_TABLE_GROUPS_GROUPTYPES)){
			par = new PairTable<T,T>(table,firstValue,secondValue);
		}
		return new Pair<Class,Class>(firstClass,secondClass);
	}*/

    /**
     * Creates a Model's subclass object looking at the table selected
     *
     * @param table Table selected
     * @param ent   Cursor to the table rows
     * @return A Model's subclass object
     */
    private <T extends Model> T createObjectByTable(String table, Entity ent) {
    	Model o = null;
        Pair<String, String> params;
        long id;

        if (table.equals(DataBaseHelper.DB_TABLE_COURSES)) {
            o = new Course(ent.getInt("id"),
                    ent.getInt("userRole"),
                    ent.getString("shortName"),
                    ent.getString("fullName"));
        } else if (table.equals(DataBaseHelper.DB_TABLE_TEST_QUESTIONS_COURSE) ||
                table.equals(DataBaseHelper.DB_TABLE_TEST_QUESTION_ANSWERS) ||
                table.equals(DataBaseHelper.DB_TABLE_USERS_COURSES) ||
                table.equals(DataBaseHelper.DB_TABLE_GROUPS_COURSES) ||
                table.equals(DataBaseHelper.DB_TABLE_GROUPS_GROUPTYPES)) {

            params = selectParamsPairTable(table);

            o = new PairTable<Integer, Integer>(table,
                    ent.getInt(params.getFirst()),
                    ent.getInt(params.getSecond()));
        } else if (table.equals(DataBaseHelper.DB_TABLE_NOTIFICATIONS)) {
            o = new SWADNotification(ent.getInt("notifCode"),
            		ent.getInt("eventCode"),
                    crypto.decrypt(ent.getString("eventType")),
                    ent.getLong("eventTime"),
                    crypto.decrypt(ent.getString("userSurname1")),
                    crypto.decrypt(ent.getString("userSurname2")),
                    crypto.decrypt(ent.getString("userFirstname")),
                    crypto.decrypt(ent.getString("userPhoto")),
                    crypto.decrypt(ent.getString("location")),
                    crypto.decrypt(ent.getString("summary")),
                    ent.getInt("status"),
                    crypto.decrypt(ent.getString("content")),
                    Utils.parseStringBool(ent.getString("seenLocal")),
                    Utils.parseStringBool(ent.getString("seenRemote")));
        } else if (table.equals(DataBaseHelper.DB_TABLE_TEST_QUESTIONS)) {
            id = ent.getInt("id");
            PairTable<?, ?> q = (PairTable<?, ?>) getRow(DataBaseHelper.DB_TABLE_TEST_QUESTIONS_COURSE, "qstCod", Long.toString(id));

            if (q != null) {
                o = new TestQuestion(id,
                        (Integer) q.getFirst(),
                        ent.getString("stem"),
                        ent.getString("ansType"),
                        Utils.parseStringBool(ent.getString("shuffle")),
                        ent.getString("feedback"));
            } else {
                o = null;
            }
        } else if (table.equals(DataBaseHelper.DB_TABLE_TEST_ANSWERS)) {
            id = ent.getId();
            int ansInd = ent.getInt("ansInd");
            PairTable<?, ?> a = (PairTable<?, ?>) getRow(DataBaseHelper.DB_TABLE_TEST_QUESTION_ANSWERS, "ansCod", Long.toString(id));

            if (a != null) {
                o = new TestAnswer(id,
                        ansInd,
                        (Integer) a.getFirst(),
                        Utils.parseStringBool(ent.getString("correct")),
                        ent.getString("answer"),
                        ent.getString("answerFeedback"));
            } else {
                o = null;
            }
        } else if (table.equals(DataBaseHelper.DB_TABLE_TEST_TAGS)) {
            id = ent.getInt("tagCod");
            TestTag t = (TestTag) getRow(DataBaseHelper.DB_TABLE_TEST_QUESTION_TAGS, "tagCod", Long.toString(id));

            if (t != null) {
                o = new TestTag(id,
                        t.getQstCodList(),
                        ent.getString("tagTxt"),
                        ent.getInt("tagInd"));
            } else {
                o = null;
            }
        } else if (table.equals(DataBaseHelper.DB_TABLE_TEST_CONFIG)) {
            o = new Test(ent.getInt("id"),
                    ent.getInt("min"),
                    ent.getInt("def"),
                    ent.getInt("max"),
                    ent.getString("feedback"),
                    ent.getLong("editTime"));
        } else if (table.equals(DataBaseHelper.DB_TABLE_TEST_QUESTION_TAGS)) {
            ArrayList<Integer> l = new ArrayList<Integer>();
            l.add(ent.getInt("qstCod"));
            o = new TestTag(ent.getInt("tagCod"),
                    l,
                    null,
                    ent.getInt("tagInd"));
        } else if (table.equals(DataBaseHelper.DB_TABLE_USERS)) {
            o = new User(ent.getInt("userCode"),
                    null,                                // wsKey
                    ent.getString("userID"),
                    ent.getString("userNickname"),
                    ent.getString("userSurname1"),
                    ent.getString("userSurname2"),
                    ent.getString("userFirstname"),
                    ent.getString("photoPath"),
                    ent.getInt("userRole"));
        } else if (table.equals(DataBaseHelper.DB_TABLE_GROUPS)) {
            long groupTypeCode = getGroupTypeCodeFromGroup(ent.getLong("id"));
            o = new Group(ent.getLong("id"),
                    ent.getString("groupName"),
                    groupTypeCode,
                    ent.getInt("maxStudents"),
                    ent.getInt("open"),
                    ent.getInt("students"),
                    ent.getInt("fileZones"),
                    ent.getInt("member"));
        } else if (table.equals(DataBaseHelper.DB_TABLE_GROUP_TYPES)) {
            o = new GroupType(ent.getLong("id"),
                    ent.getString("groupTypeName"),
                    ent.getLong("courseCode"),
                    ent.getInt("mandatory"),
                    ent.getInt("multiple"),
                    ent.getLong("openTime"));
        } else if (table.equals(DataBaseHelper.DB_TABLE_PRACTICE_SESSIONS)) {
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

            try {
                o = new PracticeSession(ent.getId(),
                        ent.getInt("crsCod"),
                        ent.getInt("grpCod"),
                        format.parse(ent.getString("startDate")),
                        format.parse(ent.getString("endDate")),
                        ent.getString("site"),
                        ent.getString("description"));
            } catch (ParseException e) {
                //Send exception details to Google Analytics
                SWADroidTracker.sendException(mCtx, e, false);
            }
        }

        return (T) o;
    }

    /**
     * Gets all rows of specified table
     *
     * @param table Table containing the rows
     * @return A list of Model's subclass objects
     */
    public <T extends Model> List<T>  getAllRows(String table) {
        List<T> result = new ArrayList<T>();
        List<Entity> rows = db.getEntityList(table);
        T row;

        for (Entity ent : rows) {
            row = createObjectByTable(table, ent);
            result.add(row);
        }

        return result;
    }

    /**
     * Gets the rows of specified table that matches "where" condition. The rows are ordered as says the "orderby"
     * parameter
     *
     * @param table   Table containing the rows
     * @param where   Where condition of SQL sentence
     * @param orderby Orderby part of SQL sentence
     * @return A list of Model's subclass objects
     */
    public <T extends Model> List<T> getAllRows(String table, String where, String orderby) {
        List<T> result = new ArrayList<T>();
        List<Entity> rows = db.getEntityList(table, where, orderby);
        T row;

        if (rows != null) {
            for (Entity ent : rows) {
                row = createObjectByTable(table, ent);
                result.add(row);
            }
        }

        return result;
    }
    
    /**
     * Gets count of all rows of specified table
     *
     * @param table Table containing the rows
     * @return Count of all rows of specified table
     */
    public int getAllRowsCount(String table) {
        return db.getEntityListCount(table, null);
    }
    
    /**
     * Gets count of all rows of specified table
    *
    * @param table   Table containing the rows
    * @param where   Where condition of SQL sentence
    * @return Count of all rows of specified table
    */
   public int getAllRowsCount(String table, String where) {
       return db.getEntityListCount(table, where);
   }

    /**
     * Gets a row of specified table
     *
     * @param table      Table containing the rows
     * @param fieldName  Field's name
     * @param fieldValue Field's value
     * @return A Model's subclass object
     *         or null if the row does not exist in the specified table
     */
    public <T extends Model> T getRow(String table, String fieldName, Object fieldValue) {
        List<Entity> rows;
        Entity ent;
        T row = null;
        
        if(fieldValue instanceof String) {
        	rows = db.getEntityList(table, fieldName + " = '" + fieldValue + "'");
        } else {
        	rows = db.getEntityList(table, fieldName + " = " + fieldValue + "");
        }

        if (rows.size() > 0) {
            ent = rows.get(0);
            row = createObjectByTable(table, ent);
        }

        return row;
    }

    /**
     * Gets an user
     *
     * @param fieldName  Field's name
     * @param fieldValue Field's value
     * @return The user found 
     *         or null if the user does not exist
     */
    public User getUser(String fieldName, Object fieldValue) {
        List<Entity> rows;
        Entity ent;
        User user = null;
        
        if(fieldValue instanceof String) {
        	rows = db.getEntityList(DataBaseHelper.DB_TABLE_USERS, fieldName + " = '" + fieldValue + "'");
        } else {
        	rows = db.getEntityList(DataBaseHelper.DB_TABLE_USERS, fieldName + " = " + fieldValue + "");
        }

        if (rows.size() > 0) {
            ent = rows.get(0);
            
            user = new User(
            		ent.getLong("userCode"),
            		null,
		            crypto.decrypt(ent.getString("userID")),
		            crypto.decrypt(ent.getString("userNickname")),
		            crypto.decrypt(ent.getString("userSurname1")),
		            crypto.decrypt(ent.getString("userSurname2")),
		            crypto.decrypt(ent.getString("userFirstname")),
		            crypto.decrypt(ent.getString("photoPath")),
		            ent.getInt("userRole"));
        }

        return user;
    }

    /**
     * Gets the id of users enrolled in the selected course
     *
     * @param courseCode Course code to be referenced
     * @return A list of User's id
     */
    public List<Long> getUsersCourse(long courseCode) {
        List<Long> result = new ArrayList<Long>();

        List<Entity> rows = db.getEntityList(DataBaseHelper.DB_TABLE_USERS_COURSES, "crsCod = '" + courseCode + "'");
        if (rows != null) {
            for (Entity ent : rows) {
                result.add(ent.getLong("userCode"));
            }
        }
        return result;
    }

    /**
     * Gets the actual practice session in progress for the selected course and group
     *
     * @param courseCode Course code to be referenced
     * @param groupId    Group code to be referenced
     * @return The practice session in progress if any, or null otherwise
     */
    public PracticeSession getPracticeSessionInProgress(long courseCode, long groupId) {
        String table = DataBaseHelper.DB_TABLE_PRACTICE_SESSIONS;
        Calendar cal = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();

        List<Entity> rows = db.getEntityList(table, "crsCod = " + courseCode + " AND grpCod = " + groupId);
        PracticeSession ps;

        if (rows != null) {
            for (Entity ent : rows) {
                ps = (PracticeSession) createObjectByTable(table, ent);
                startDate.setTime(ps.getSessionStartDate());
                endDate.setTime(ps.getSessionEndDate());

                if (cal.after(startDate) && cal.before(endDate)) {
                    return ps;
                }
            }
        }
        return null;
    }

    /**
     * Gets practice sessions for the selected course and group
     *
     * @param courseCode Course code to be referenced
     * @param groupId    Group code to be referenced
     * @return The list of practice sessions
     */
    public List<PracticeSession> getPracticeSessions(long courseCode, long groupId) {
        List<PracticeSession> sessions = new ArrayList<PracticeSession>();
        String table = DataBaseHelper.DB_TABLE_PRACTICE_SESSIONS;
        String where = "crsCod = " + courseCode + " AND grpCod = " + groupId;

        List<Entity> rows = db.getEntityList(table, where, "startDate asc");
        PracticeSession ps;

        if (rows != null) {
            for (Entity ent : rows) {
                ps = (PracticeSession) createObjectByTable(table, ent);
                sessions.add(ps);
            }
        }
        return sessions;
    }

    /**
     * Gets the group which code is given
     *
     * @param groupId long that identifies uniquely the searched group
     * @return group with the referenced code in case it exits
     *         null otherwise
     */
    public Group getGroup(long groupId) {
        String table = DataBaseHelper.DB_TABLE_GROUPS;
        List<Entity> rows = db.getEntityList(table, "id = " + groupId);
        Group g = null;
        if (rows != null)
            g = (Group) createObjectByTable(table, rows.get(0));
        return g;
    }

    /**
     * Gets the group codes in the selected course
     *
     * @param courseCode Course code to be referenced
     * @return A list of group codes belonging to the selected course
     */
    public List<Long> getGroupCodesCourse(long courseCode) {
        List<Long> result = new ArrayList<Long>();

        List<Entity> rows = db.getEntityList(DataBaseHelper.DB_TABLE_GROUPS_COURSES, "crsCod = '" + courseCode + "'");
        if (rows != null) {
            for (Entity ent : rows) {
                result.add(ent.getLong("grpCod"));
            }
        }

        return result;
    }

    /**
     * Gets the groups belonging to the selected type of group
     *
     * @param groupTypeCode group type code to be referenced
     * @return List of Groups
     */
    public List<Group> getGroupsOfType(long groupTypeCode) {
        List<Group> groups = new ArrayList<Group>();
        List<Long> groupCodes = getGroupsCodesOfType(groupTypeCode);
        if (!groupCodes.isEmpty()) {
            for (Long groupCode : groupCodes) {
                groups.add((Group) getRow(DataBaseHelper.DB_TABLE_GROUPS, "id", String.valueOf(groupCode)));
            }
        }

        return groups;
    }

    public Cursor getCursor(String table, String where, String orderby) {
        return db.getCursor(table, where, orderby);
    }

    public Cursor getCursorGroupType(long courseCode) {
        return db.getCursor(DataBaseHelper.DB_TABLE_GROUP_TYPES, "courseCode =" + courseCode, "groupTypeName");
    }

    public GroupType getGroupTypeFromGroup(long groupCode) {
        long groupTypeCode = getGroupTypeCodeFromGroup(groupCode);
        return (GroupType) getRow(DataBaseHelper.DB_TABLE_GROUP_TYPES, "id", String.valueOf(groupTypeCode));
    }

    /**
     * Gets the code of the group type belonging the group with the given group code
     *
     * @param groupCode long that specifies the code of the group
     * @return group type code in case the given group belongs to a group type
     *         -1 	 otherwise
     */
    long getGroupTypeCodeFromGroup(long groupCode) {
        List<Entity> rows = db.getEntityList(DataBaseHelper.DB_TABLE_GROUPS_GROUPTYPES, "grpCod = '" + groupCode + "'");
        long groupTypeCode = -1;
        if (!rows.isEmpty()) {
            groupTypeCode = rows.get(0).getLong("grpTypCod");
        }
        return groupTypeCode;

    }

    /**
     * Gets the codes of groups belonging the selected type of group
     *
     * @param groupTypeCode group type code to be referenced
     * @return List of group codes
     */
    private List<Long> getGroupsCodesOfType(long groupTypeCode) {
        List<Long> groupCodes = new ArrayList<Long>();
        List<Entity> rows = db.getEntityList(DataBaseHelper.DB_TABLE_GROUPS_GROUPTYPES, "grpTypCod = '" + groupTypeCode + "'");
        if (rows != null) {
            for (Entity row : rows) {
                groupCodes.add(row.getLong("grpCod"));
            }
        }
        return groupCodes;
    }

    /**
     * Get groups belonging to the referred course to which the logged user is enrolled
     *
     * @param courseCode course code to which the groups belong
     * @return List of the group
     */
    public List<Group> getUserLoggedGroups(long courseCode) {
        List<Long> groupCodes = getGroupCodesCourse(courseCode);
        List<Group> groups = new ArrayList<Group>();
        if (!groupCodes.isEmpty()) {
            for (Long groupCode : groupCodes) {
                Group g = (Group) getRow(DataBaseHelper.DB_TABLE_GROUPS, "id", String.valueOf(groupCode));
                if (g.isMember()) groups.add(g);
            }
        }
        return groups;
    }

    /**
     * Gets the practice groups in the selected course
     *
     * @param courseCode Course code to be referenced
     * @return Cursor access to the practice groups
     */
    public Cursor getPracticeGroups(long courseCode) {
        String select = "SELECT " +
                "g._id, g.id, gt.groupTypeName, g.groupName" +
                " FROM " +
                DataBaseHelper.DB_TABLE_GROUPS + " g, " + DataBaseHelper.DB_TABLE_GROUPS_COURSES + " gc, " +
                DataBaseHelper.DB_TABLE_GROUP_TYPES + " gt, " + DataBaseHelper.DB_TABLE_GROUPS_GROUPTYPES + " ggt" +
                " WHERE " +
                "gc.crsCod = ? AND gc.grpCod = g.id AND gc.grpCod = ggt.grpCod AND ggt.grpTypCod = gt.id";

        SQLiteDatabase db = DataFramework.getInstance().getDB();
        return db.rawQuery(select, new String[]{String.valueOf(courseCode)});
    }

    /**
     * Gets the user codes of students in the selected session
     *
     * @param sessionId Session code to be referenced
     * @return A list of User's id
     */
    public List<Long> getStudentsAtSession(long sessionId) {
        List<Long> result = new ArrayList<Long>();

        List<Entity> rows = db.getEntityList(DataBaseHelper.DB_TABLE_ROLLCALL, "sessCod = " + sessionId);
        if (rows != null) {
            for (Entity ent : rows) {
                result.add(ent.getLong("usrCod"));
            }
        }
        return result;
    }

    public boolean hasAttendedSession(long userCode, long sessionId) {
        List<Entity> rows = db.getEntityList(DataBaseHelper.DB_TABLE_ROLLCALL, "usrCod = " + userCode + " AND sessCod = " + sessionId);

        return (rows.size() > 0);
    }

    /**
     * Checks if the specified user is enrolled in the selected course
     *
     * @param selectedCourseCode Course code to be referenced
     * @return True if user is enrolled in the selected course. False otherwise
     */
    public boolean isUserEnrolledCourse(String userID, long selectedCourseCode) {
        boolean enrolled = false;
        User u = (User) getRow(DataBaseHelper.DB_TABLE_USERS, "userID", userID);

        if (u != null) {
            String sentencia = "SELECT userCode AS _id, crsCod" +
                    " FROM " + DataBaseHelper.DB_TABLE_USERS_COURSES +
                    " WHERE userCode = ? AND crsCod = ?" +
                    " ORDER BY 1";

            Cursor c = db.getDB().rawQuery(sentencia, new String[]{
                    String.valueOf(u.getId()),
                    String.valueOf(selectedCourseCode)
            });

            if (c.moveToFirst()) {
                enrolled = true;
            }
            c.close();
        } else
            enrolled = false;

        return enrolled;
    }

    /**
     * Gets the groups that owns the selected course
     *
     * @param courseCode Course code to be referenced
     * @return Cursor access to the groups
     */
    public List<Group> getGroups(long courseCode, String... filter) {
        String select = "SELECT grpCod FROM " + DataBaseHelper.DB_TABLE_GROUPS_COURSES + " WHERE crsCod = " + courseCode + ";";
        Cursor groupCodes = db.getDB().rawQuery(select, null);

        List<Group> groups = new ArrayList<Group>(groupCodes.getCount());

        while (groupCodes.moveToNext()) {
            Group group = (Group) this.getRow(DataBaseHelper.DB_TABLE_GROUPS, "id", String.valueOf(groupCodes.getInt(0)));
            groups.add(group);
        }
        return groups;
    }

    /**
     * Inserts a course in database
     *
     * @param c Course to be inserted
     */
    public void insertCourse(Course c) {
        Entity ent = new Entity(DataBaseHelper.DB_TABLE_COURSES);
        ent.setValue("id", c.getId());
        ent.setValue("userRole", c.getUserRole());
        ent.setValue("shortName", c.getShortName());
        ent.setValue("fullName", c.getFullName());
        ent.save();
    }

    /**
     * Inserts a notification in database
     *
     * @param n Notification to be inserted
     */
    public void insertNotification(SWADNotification n) {
        Entity ent = new Entity(DataBaseHelper.DB_TABLE_NOTIFICATIONS);

        String eventTime = String.valueOf(n.getEventTime());
        String status = String.valueOf(n.getStatus());

        ent.setValue("notifCode", n.getId());
        ent.setValue("eventCode", n.getEventCode());
        ent.setValue("eventType", crypto.encrypt(n.getEventType()));
        ent.setValue("eventTime", eventTime);
        ent.setValue("userSurname1", crypto.encrypt(n.getUserSurname1()));
        ent.setValue("userSurname2", crypto.encrypt(n.getUserSurname2()));
        ent.setValue("userFirstname", crypto.encrypt(n.getUserFirstName()));
        ent.setValue("userPhoto", crypto.encrypt(n.getUserPhoto()));
        ent.setValue("location", crypto.encrypt(n.getLocation()));
        ent.setValue("summary", crypto.encrypt(n.getSummary()));
        ent.setValue("status", status);
        ent.setValue("content", crypto.encrypt(n.getContent()));
        ent.setValue("seenLocal", Utils.parseBoolString(n.isSeenLocal()));
        ent.setValue("seenRemote", Utils.parseBoolString(n.isSeenRemote()));
        ent.save();
    }

    /**
     * Inserts a test question in database
     *
     * @param q                  Test question to be inserted
     * @param selectedCourseCode Course code to be referenced
     */
    public void insertTestQuestion(TestQuestion q, long selectedCourseCode) {
        Entity ent = new Entity(DataBaseHelper.DB_TABLE_TEST_QUESTIONS);

        ent.setValue("id", q.getId());
        ent.setValue("ansType", q.getAnswerType());
        ent.setValue("stem", q.getStem());
        ent.setValue("shuffle", Utils.parseBoolString(q.getShuffle()));
        ent.setValue("feedback", q.getFeedback());
        ent.save();

        ent = new Entity(DataBaseHelper.DB_TABLE_TEST_QUESTIONS_COURSE);
        ent.setValue("qstCod", q.getId());
        ent.setValue("crsCod", selectedCourseCode);
        ent.save();
    }

    /**
     * Inserts a test answer in database
     *
     * @param a      Test answer to be inserted
     * @param qstCod Test question code to be referenced
     */
    public void insertTestAnswer(TestAnswer a, int qstCod) {
        Entity ent = new Entity(DataBaseHelper.DB_TABLE_TEST_ANSWERS);
        long id;

        ent.setValue("ansInd", a.getAnsInd());
        ent.setValue("answer", a.getAnswer());
        ent.setValue("correct", a.getCorrect());
        ent.setValue("answerFeedback", a.getFeedback());
        ent.save();
        id = ent.getId();

        ent = new Entity(DataBaseHelper.DB_TABLE_TEST_QUESTION_ANSWERS);
        ent.setValue("qstCod", qstCod);
        ent.setValue("ansCod", id);
        ent.save();
    }

    /**
     * Inserts a test tag in database
     *
     * @param t Test tag to be inserted
     */
    public void insertTestTag(TestTag t) {
        List<Entity> rows = db.getEntityList(DataBaseHelper.DB_TABLE_TEST_TAGS, "id = " + t.getId());

        if (rows.isEmpty()) {
            Entity ent = new Entity(DataBaseHelper.DB_TABLE_TEST_TAGS);

            ent.setValue("id", t.getId());
            ent.setValue("tagTxt", t.getTagTxt());
            ent.save();

            for (Integer i : t.getQstCodList()) {
                ent = new Entity(DataBaseHelper.DB_TABLE_TEST_QUESTION_TAGS);
                ent.setValue("qstCod", i);
                ent.setValue("tagCod", t.getId());
                ent.setValue("tagInd", t.getTagInd());
                ent.save();
            }
        } else {
            throw new SQLException("Duplicated tag");
        }
    }

    /**
     * Inserts a test config in database
     *
     * @param t Test config to be inserted
     */
    public void insertTestConfig(Test t) {
        Entity ent = new Entity(DataBaseHelper.DB_TABLE_TEST_CONFIG);

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
     *
     * @param p Relation to be inserted
     */
    void insertPairTable(PairTable<?, ?> p) {
        String table = p.getTable();
        Pair<String, String> params = selectParamsPairTable(table);

        Entity ent = new Entity(table);
        ent.setValue(params.getFirst(), p.getFirst());
        ent.setValue(params.getSecond(), p.getSecond());
        ent.save();
    }

    /**
     * Inserts a new row or updates an existing one in the table named @a tableName with the data that contains @a currentModel
     *
     * @param tableName    string with the table name
     * @param currentModel model with the new data
     * @param ents         vector of entities. In case this param is given, the entities given will be modified
     */

    boolean insertEntity(String tableName, Model currentModel, Entity... ents) {
        boolean returnValue = true;
        Entity ent;

        if (ents.length >= 1) {
            ent = ents[0];
        } else {
            ent = new Entity(tableName);
        }

        if (tableName.equals(DataBaseHelper.DB_TABLE_GROUPS)) {
            Group g = (Group) currentModel;
            ent.setValue("id", g.getId());
            ent.setValue("groupName", g.getGroupName());
            ent.setValue("maxStudents", g.getMaxStudents());
            ent.setValue("students", g.getCurrentStudents());
            ent.setValue("open", g.getOpen());
            ent.setValue("fileZones", g.getDocumentsArea());
            ent.setValue("member", g.getMember());
            ent.save();
        }

        if (tableName.equals(DataBaseHelper.DB_TABLE_GROUP_TYPES)) {
            GroupType gt = (GroupType) currentModel;
            ent.setValue("id", gt.getId());
            ent.setValue("groupTypeName", gt.getGroupTypeName());
            ent.setValue("courseCode", gt.getCourseCode());
            ent.setValue("mandatory", gt.getMandatory());
            ent.setValue("multiple", gt.getMultiple());
            ent.setValue("openTime", gt.getOpenTime());
            ent.save();
        }

        return returnValue;
    }
    /**
     * Inserts a relation in database
     * @param p Relation to be inserted
     */
	/*	public void insertPairTable(String table)
	{
		Pair<Class, Class> pairClass = selectParamsClassPairTable(table);
		class T1 = pairClass.getFirst();

		Pair<String,String> pairParams = selectParamsPairTable(table);
		PairTable<pairClass.getFirst(), pairClass.getSecond()> 
		String table = p.getTable();
		Pair<String, String> params = selectParamsPairTable(table);

		Entity ent = new Entity(table);
		ent.setValue(params.getFirst(), p.getFirst());
		ent.setValue(params.getSecond(), p.getSecond());
		ent.save();
	}
	 */

    /**
     * Inserts a user in database or updates it if already exists
     *
     * @param u User to be inserted
     */
    public void insertUser(User u) {
        Entity ent;
        List<Entity> rows = db.getEntityList(DataBaseHelper.DB_TABLE_USERS, "userCode = " + u.getId());

        if (rows.isEmpty()) {
            ent = new Entity(DataBaseHelper.DB_TABLE_USERS);
        } else {
            // If user exists and has photo, delete the old photo file
            if (u.getPhotoFileName() != null) {
                File externalPath = Environment.getExternalStorageDirectory();
                String packageName = db.getContext().getPackageName();
                File file = new File(externalPath.getAbsolutePath() + "/Android/data/" + packageName + "/", u.getPhotoFileName());
                //File file = new File(db.getContext().getExternalFilesDir(null), u.getPhotoFileName());
                file.delete();
            }
            ent = rows.get(0);
        }
        
        ent.setValue("userCode", u.getId());
        ent.setValue("userID", crypto.encrypt(u.getUserID()));
        ent.setValue("userNickname", crypto.encrypt(u.getUserNickname()));
        ent.setValue("userSurname1", crypto.encrypt(u.getUserSurname1()));
        ent.setValue("userSurname2", crypto.encrypt(u.getUserSurname2()));
        ent.setValue("userFirstname", crypto.encrypt(u.getUserFirstname()));
        ent.setValue("photoPath", crypto.encrypt(u.getUserPhoto()));
        ent.setValue("userRole", u.getUserRole());
        ent.save();
    }

    /**
     * Inserts a group in database
     *
     * @param g          Group to be inserted
     * @param courseCode Course code to be referenced
     */
    public boolean insertGroup(Group g, long courseCode) {
        List<Entity> rows = db.getEntityList(DataBaseHelper.DB_TABLE_GROUPS, "id = " + g.getId());
        boolean returnValue = true;

        if (rows.isEmpty()) {
            insertEntity(DataBaseHelper.DB_TABLE_GROUPS, g);
        } else { //already exits a group with the given code. just update
            insertEntity(DataBaseHelper.DB_TABLE_GROUPS, g, rows.get(0));

        }

        //update all the relationship
        long groupCode = g.getId();
        rows = db.getEntityList(DataBaseHelper.DB_TABLE_GROUPS_COURSES, "grpCod =" + groupCode);
        Course course = (Course) getRow(DataBaseHelper.DB_TABLE_COURSES, "id", String.valueOf(courseCode));

        //course code is a foreign key. Therefore, to avoid a database error,
        //it should not insert/modify rows in the relationship table if the course does not exists
        if (course != null) {
            if (rows.isEmpty()) {
                PairTable<Long, Long> pair = new PairTable<Long, Long>(DataBaseHelper.DB_TABLE_GROUPS_COURSES, g.getId(), courseCode);
                insertPairTable(pair);
            } else {
                rows.get(0).setValue("crsCod", courseCode);
                rows.get(0).save();
            }
        } else returnValue = false;

        long groupTypeCode = g.getGroupTypeCode();

        //WHILE THE WEB SERVICE TO GET GROUP TYPES STILL UNAVAILABLE, this condition is not evaluated
        //GroupType groupType = (GroupType) getRow(Global.DB_TABLE_GROUP_TYPES,"id",String.valueOf(groupTypeCode));
        //group type code is a foreign key. Therefore, to avoid a database error,
        //it should not insert/modify rows in the relationship table if the group type does not exists
        //if(groupType != null){
        rows = db.getEntityList(DataBaseHelper.DB_TABLE_GROUPS_GROUPTYPES, "grpCod=" + groupCode);
        if (rows.isEmpty()) {
            insertPairTable(new PairTable<Long, Long>(DataBaseHelper.DB_TABLE_GROUPS_GROUPTYPES, groupTypeCode, groupCode));
        } else {
			PairTable<Object, Object> prev = new PairTable<Object, Object>(DataBaseHelper.DB_TABLE_GROUPS_GROUPTYPES, rows.get(0).getValue("grpTypCod"), rows.get(0).getValue("grpCod"));
			PairTable<Object, Object> current = new PairTable<Object, Object>(DataBaseHelper.DB_TABLE_GROUPS_GROUPTYPES, groupTypeCode, groupCode);
            updatePairTable(prev, current);
        }
		/*}else returnValue = false;*/

        return returnValue;
    }

    /**
     * Insert a new group type in the database or updated if the group type exits already in the data base
     *
     * @param gt Group Type to be inserted
     */
    public boolean insertGroupType(GroupType gt) {
        boolean returnValue = true;
        GroupType row = (GroupType) getRow(DataBaseHelper.DB_TABLE_GROUP_TYPES, "id", String.valueOf(gt.getId()));
        if (row == null) {
            insertEntity(DataBaseHelper.DB_TABLE_GROUP_TYPES, gt);
        } else {
            returnValue = false;
        }
        return returnValue;
    }

    public boolean insertCollection(String table, List<Model> currentModels, long... courseCode) {
        boolean result = true;
        Collection<? extends Model> modelsDB = getAllRows(table);
        List<Model> newModels = new ArrayList<Model>();
        List<Model> obsoleteModel = new ArrayList<Model>();
        List<Model> modifiedModel = new ArrayList<Model>();
        
        beginTransaction();

        newModels.addAll(currentModels);
        newModels.removeAll(modelsDB);

        obsoleteModel.addAll(modelsDB);
        obsoleteModel.removeAll(currentModels);

        modifiedModel.addAll(currentModels);
        modifiedModel.removeAll(newModels);
        modifiedModel.removeAll(obsoleteModel);

        if (table.compareTo(DataBaseHelper.DB_TABLE_GROUP_TYPES) == 0) {
            for (Model anObsoleteModel : obsoleteModel) {
                long code = anObsoleteModel.getId();
                removeAllRows(table, "id", code);
                removeAllRows(DataBaseHelper.DB_TABLE_GROUPS_GROUPTYPES, "grpTypCod", code);
            }
            for (Model model : newModels) {
                insertEntity(table, model);
            }
            List<Entity> rows;
            for (Model m : modifiedModel) {
                rows = db.getEntityList(table, "id=" + m.getId());
                insertEntity(table, m, rows.get(0));
            }
        }

        if (table.compareTo(DataBaseHelper.DB_TABLE_GROUPS) == 0) {
            for (Model anObsoleteModel : obsoleteModel) {
                long code = anObsoleteModel.getId();
                removeAllRows(table, "id", code);
                removeAllRows(DataBaseHelper.DB_TABLE_GROUPS_GROUPTYPES, "grpCod", code);
                removeAllRows(DataBaseHelper.DB_TABLE_GROUPS_COURSES, "grpCod", code);
            }
            for (Model model : newModels) {
                insertGroup((Group) model, courseCode[0]);
            }
            for (Model model : modifiedModel) {
                insertGroup((Group) model, courseCode[0]);
            }
        }
        
        //Finish the pending transaction with successful status
        endTransaction(true);

        return result;
    }

    /**
     * Inserts the rollcall data of a student to a practice session in database
     *
     * @param l        User code of student
     * @param sessCode Practice session code
     */
    public void insertRollcallData(long l, long sessCode) {
        List<Entity> rows = db.getEntityList(DataBaseHelper.DB_TABLE_ROLLCALL, "sessCod = " + sessCode + " AND usrCod = " + l);

        if (rows.isEmpty()) {
            Entity ent = new Entity(DataBaseHelper.DB_TABLE_ROLLCALL);

            ent.setValue("sessCod", sessCode);
            ent.setValue("usrCod", l);
            ent.save();
        }
    }

    /**
     * Inserts a practice session in database
     *
     * @param courseCode  Course code to be referenced
     * @param groupCode   Group code to be referenced
     * @param startDate   Start date-time of session
     * @param endDate     End date-time of session
     * @param site        Site where session takes place
     * @param description Optional description of practice session
     * @return True if practice session does not exist in database and is inserted. False otherwise.
     */
    public boolean insertPracticeSession(long courseCode, long groupCode, String startDate, String endDate, String site, String description) {
        String where = "crsCod = '" + courseCode + "' AND " +
                "grpCod = '" + groupCode + "' AND " +
                "startDate = '" + startDate + "' AND " +
                "endDate = '" + endDate + "'";
        List<Entity> rows = db.getEntityList(DataBaseHelper.DB_TABLE_PRACTICE_SESSIONS, where);

        if (rows.isEmpty()) {
            Entity ent = new Entity(DataBaseHelper.DB_TABLE_PRACTICE_SESSIONS);

            ent.setValue("crsCod", courseCode);
            ent.setValue("grpCod", groupCode);
            ent.setValue("startDate", startDate);
            ent.setValue("endDate", endDate);
            if (site != null && !site.equals(""))
                ent.setValue("site", site);
            if (description != null && !description.equals(""))
                ent.setValue("description", description);
            ent.save();
            return true;
        } else {
            return false;
        }
    }


    /**
     * Inserts a new record in database indicating that the user belongs
     * to the course and group specified, or updates it if already exists
     *
     * @param userID     User to be inserted
     * @param courseCode Course code to be referenced
     * @param groupCode  Group code to be referenced
     */
    public void insertUserCourse(long userID, long courseCode, long groupCode) {
        Entity ent;
        String where = "userCode = " + userID + " AND crsCod = " + courseCode;
        List<Entity> rows = db.getEntityList(DataBaseHelper.DB_TABLE_USERS_COURSES, where);

        if (rows.isEmpty()) {
            ent = new Entity(DataBaseHelper.DB_TABLE_USERS_COURSES);
        } else {
            ent = rows.get(0);
        }
        ent.setValue("userCode", userID);
        ent.setValue("crsCod", courseCode);
        ent.setValue("grpCod", groupCode);
        ent.save();
    }

    /**
     * Updates a course in database
     *
     * @param prev   Course to be updated
     * @param actual Updated course
     */
    public void updateCourse(Course prev, Course actual) {
        List<Entity> rows = db.getEntityList(DataBaseHelper.DB_TABLE_COURSES, "id = " + prev.getId());
        Entity ent = rows.get(0);
        ent.setValue("id", actual.getId());
        ent.setValue("userRole", actual.getUserRole());
        ent.setValue("shortName", actual.getShortName());
        ent.setValue("fullName", actual.getFullName());
        ent.save();
    }

    /**
     * Updates a course in database
     *
     * @param id     Course code of course to be updated
     * @param actual Updated course
     */
    public void updateCourse(long id, Course actual) {
        List<Entity> rows = db.getEntityList(DataBaseHelper.DB_TABLE_COURSES, "id = " + id);
        if (!rows.isEmpty()) {
            Entity ent = rows.get(0);
            ent.setValue("id", actual.getId());
            ent.setValue("userRole", actual.getUserRole());
            ent.setValue("shortName", actual.getShortName());
            ent.setValue("fullName", actual.getFullName());
            ent.save();
        }
    }

    /**
     * Updates all notifications in database
     *
     * @param field  Field to be updated
     * @param value  New field value
     */
    public void updateAllNotifications(String field, String value) {
        List<Entity> rows = db.getEntityList(DataBaseHelper.DB_TABLE_NOTIFICATIONS);
        for(Entity ent : rows) {
        	ent.setValue(field, value);
        	ent.save();
        }
    }

    /**
     * Updates a notification in database
     *
     * @param id     Notification code of notification to be updated
     * @param field  Field to be updated
     * @param value  New field value
     */
    public void updateNotification(long id, String field, String value) {
        List<Entity> rows = db.getEntityList(DataBaseHelper.DB_TABLE_NOTIFICATIONS, "notifCode = " + id);
        for(Entity ent : rows) {
        	ent.setValue(field, value);
        	ent.save();
        }
    }

    /**
     * Updates a notification in database
     *
     * @param id     Notification code of notification to be updated
     * @param actual Updated notification
     */
    public void updateNotification(long id, SWADNotification actual) {
        List<Entity> rows = db.getEntityList(DataBaseHelper.DB_TABLE_NOTIFICATIONS, "notifCode = " + id);
        long notifCode = actual.getId();
        long eventCode = actual.getEventCode();
        String eventType = crypto.encrypt(actual.getEventType());
        String eventTime = String.valueOf(actual.getEventTime());
        String userSurname1 = crypto.encrypt(actual.getUserSurname1());
        String userSurname2 = crypto.encrypt(actual.getUserSurname2());
        String userFirstname = crypto.encrypt(actual.getUserFirstName());
        String userPhoto = crypto.encrypt(actual.getUserPhoto());
        String location = crypto.encrypt(actual.getLocation());
        String summary = crypto.encrypt(actual.getSummary());
        String status = String.valueOf(actual.getStatus());
        String content = crypto.encrypt(actual.getContent());
        String seenLocal = Utils.parseBoolString(actual.isSeenLocal());
        String seenRemote = Utils.parseBoolString(actual.isSeenRemote());
        
        for(Entity ent : rows) {
	        ent.setValue("notifCode", notifCode);
	        ent.setValue("eventCode", eventCode);
	        ent.setValue("eventType", eventType);
	        ent.setValue("eventTime", eventTime);
	        ent.setValue("userSurname1", userSurname1);
	        ent.setValue("userSurname2", userSurname2);
	        ent.setValue("userFirstname", userFirstname);
	        ent.setValue("userPhoto", userPhoto);
	        ent.setValue("location", location);
	        ent.setValue("summary", summary);
	        ent.setValue("status", status);
	        ent.setValue("content", content);
	        ent.setValue("seenLocal", seenLocal);
	        ent.setValue("seenRemote", seenRemote);
	        ent.save();
        }
    }

    /**
     * Updates a notification in database
     *
     * @param prev   Notification to be updated
     * @param actual Updated notification
     */
    public void updateNotification(SWADNotification prev, SWADNotification actual) {
        List<Entity> rows = db.getEntityList(DataBaseHelper.DB_TABLE_NOTIFICATIONS, "notifCode = " + prev.getId());
        long notifCode = actual.getId();
        long eventCode = actual.getEventCode();
        String eventType = crypto.encrypt(actual.getEventType());
        String eventTime = String.valueOf(actual.getEventTime());
        String userSurname1 = crypto.encrypt(actual.getUserSurname1());
        String userSurname2 = crypto.encrypt(actual.getUserSurname2());
        String userFirstname = crypto.encrypt(actual.getUserFirstName());
        String userPhoto = crypto.encrypt(actual.getUserPhoto());
        String location = crypto.encrypt(actual.getLocation());
        String summary = crypto.encrypt(actual.getSummary());
        String status = String.valueOf(actual.getStatus());
        String content = crypto.encrypt(actual.getContent());
        String seenLocal = Utils.parseBoolString(actual.isSeenLocal());
        String seenRemote = Utils.parseBoolString(actual.isSeenRemote());
        
        for(Entity ent : rows) {
	        ent.setValue("notifCode", notifCode);
	        ent.setValue("eventCode", eventCode);
	        ent.setValue("eventType", eventType);
	        ent.setValue("eventTime", eventTime);
	        ent.setValue("userSurname1", userSurname1);
	        ent.setValue("userSurname2", userSurname2);
	        ent.setValue("userFirstname", userFirstname);
	        ent.setValue("userPhoto", userPhoto);
	        ent.setValue("location", location);
	        ent.setValue("summary", summary);
	        ent.setValue("status", status);
	        ent.setValue("content", content);
	        ent.setValue("seenLocal", seenLocal);
	        ent.setValue("seenRemote", seenRemote);
	        ent.save();
        }
    }

    /**
     * Updates a test question in database
     *
     * @param prev               Test question to be updated
     * @param actual             Updated test question
     * @param selectedCourseCode Course code to be referenced
     */
    public void updateTestQuestion(TestQuestion prev, TestQuestion actual, long selectedCourseCode) {
        List<Entity> rows = db.getEntityList(DataBaseHelper.DB_TABLE_TEST_QUESTIONS, "id = " + prev.getId());
        Entity ent = rows.get(0);

        ent.setValue("id", actual.getId());
        ent.setValue("ansType", actual.getAnswerType());
        ent.setValue("stem", actual.getStem());
        ent.setValue("shuffle", Utils.parseBoolString(actual.getShuffle()));
        ent.setValue("feedback", actual.getFeedback());
        ent.save();

        rows = db.getEntityList(DataBaseHelper.DB_TABLE_TEST_QUESTIONS_COURSE, "qstCod = " + actual.getId());
        for (Entity row : rows) {
            ent = row;
            ent.setValue("crsCod", selectedCourseCode);
            ent.save();
        }
    }

    /**
     * Updates a test answer in database
     *
     * @param prev   Test answer to be updated
     * @param actual Updated test answer
     * @param qstCod Test question code to be referenced
     */
    public void updateTestAnswer(TestAnswer prev, TestAnswer actual, int qstCod) {
        List<Entity> rows = db.getEntityList(DataBaseHelper.DB_TABLE_TEST_ANSWERS, "_id = " + prev.getId());
        Entity ent = rows.get(0);

        ent.setValue("ansInd", actual.getAnsInd());
        ent.setValue("answer", actual.getAnswer());
        ent.setValue("correct", actual.getCorrect());
        ent.setValue("answerFeedback", actual.getFeedback());
        ent.save();

        rows = db.getEntityList(DataBaseHelper.DB_TABLE_TEST_QUESTION_ANSWERS, "ansCod = " + actual.getId());
        for (Entity row : rows) {
            ent = row;
            ent.setValue("qstCod", qstCod);
            ent.save();
        }
    }

    /**
     * Updates a test tag in database
     *
     * @param prev   Test tag to be updated
     * @param actual Updated test tag
     */
    public void updateTestTag(TestTag prev, TestTag actual) {
        List<Entity> rows = db.getEntityList(DataBaseHelper.DB_TABLE_TEST_TAGS, "id = " + prev.getId());
        Entity ent = rows.get(0);
        List<Integer> qstCodList = actual.getQstCodList();
        SQLiteStatement st = db.getDB().compileStatement("INSERT OR REPLACE INTO " +
                DataBaseHelper.DB_TABLE_TEST_QUESTION_TAGS + " VALUES (NULL, ?, ?, ?);");

        ent.setValue("id", actual.getId());
        ent.setValue("tagTxt", actual.getTagTxt());
        ent.save();

        for (Integer i : qstCodList) {
            st.bindLong(1, i);
            st.bindLong(2, actual.getId());
            st.bindLong(3, actual.getTagInd());
            st.executeInsert();
        }
    }

    /**
     * Updates a test config in database
     *
     * @param id     ID of the test prior to update
     * @param actual Updated test
     */
    public void updateTestConfig(long id, Test actual) {
        List<Entity> rows = db.getEntityList(DataBaseHelper.DB_TABLE_TEST_CONFIG, "id = " + id);
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
     *
     * @param prev   Relation to be updated
     * @param actual Updated relation
     */
    void updatePairTable(PairTable<?, ?> prev, PairTable<?, ?> actual) {
        String table = prev.getTable();
        String where;
        //Integer first = (Integer) prev.getFirst();
        //Integer second = (Integer) prev.getSecond();
        Pair<String, String> params = selectParamsPairTable(table);

        where = params.getFirst() + " = " + prev.getFirst() + " AND " + params.getSecond() + " = " + prev.getSecond();

        List<Entity> rows = db.getEntityList(table, where);
        if (!rows.isEmpty()) {
            Entity ent = rows.get(0);
            ent.setValue(params.getFirst(), actual.getFirst());
            ent.setValue(params.getSecond(), actual.getSecond());
            ent.save();
        }
    }

    /**
     * Updates a user in database
     *
     * @param prev   User to be updated
     * @param actual Updated user
     */
    public void updateUser(User prev, User actual) {
        List<Entity> rows = db.getEntityList(DataBaseHelper.DB_TABLE_USERS, "id = " + prev.getId());
        Entity ent = rows.get(0);
        ent.setValue("userCode", actual.getId());
        ent.setValue("userID", actual.getUserID());
        ent.setValue("userNickname", actual.getUserNickname());
        ent.setValue("userSurname1", actual.getUserSurname1());
        ent.setValue("userSurname2", actual.getUserSurname2());
        ent.setValue("userFirstname", actual.getUserFirstname());
        ent.setValue("userPhoto", actual.getUserPhoto());
        ent.setValue("userRole", actual.getUserRole());
        ent.save();
    }

    /**
     * Updates a Group and the relationship between Groups and Courses
     *
     * @param groupCode    code of the group to be updated
     * @param courseCode   current code of the course related to the group
     * @param currentGroup updated group
     */
    public boolean updateGroup(long groupCode, long courseCode, Group currentGroup, long... groupTypeCode) {
        List<Entity> rows = db.getEntityList(DataBaseHelper.DB_TABLE_GROUPS, "id =" + groupCode);
        if (!rows.isEmpty()) {
            Entity ent = rows.get(0);
            boolean returnValue = true;
            insertEntity(DataBaseHelper.DB_TABLE_GROUPS, currentGroup, ent);

            rows = db.getEntityList(DataBaseHelper.DB_TABLE_GROUPS_COURSES, "grpCod =" + groupCode);
            Course course = (Course) getRow(DataBaseHelper.DB_TABLE_COURSES, "id", String.valueOf(courseCode));
            //course code is a foreign key. Therefore, to avoid a database error,
            //it should not insert/modify rows in the relationship table if the course does not exists
            if (course != null) {
                if (rows.isEmpty()) {
                    ent = new Entity(DataBaseHelper.DB_TABLE_GROUPS_COURSES);
                    ent.setValue("grpCod", groupCode);
                    ent.setValue("crsCod", courseCode);
                    ent.save();
                } else {
                    rows.get(0).setValue("crsCod", courseCode);
                    rows.get(0).save();
                }
            } else returnValue = false;

            if (groupTypeCode.length > 0) {
                GroupType groupType = (GroupType) getRow(DataBaseHelper.DB_TABLE_GROUP_TYPES, "id", String.valueOf(groupTypeCode[0]));
                //group type code is a foreign key. Therefore, to avoid a database error,
                //it should not insert/modify rows in the relationship table if the group type does not exists
                if (groupType != null) {
                    rows = db.getEntityList(DataBaseHelper.DB_TABLE_GROUPS_GROUPTYPES, "grpCod=" + groupCode);
                    if (!rows.isEmpty()) {
                        insertPairTable(new PairTable<Long, Long>(DataBaseHelper.DB_TABLE_GROUPS_GROUPTYPES, groupTypeCode[0], groupCode));

                    } else {
						PairTable<Object, Object> prev = new PairTable<Object, Object>(DataBaseHelper.DB_TABLE_GROUPS_GROUPTYPES, rows.get(0).getValue("grpTypCod"), rows.get(0).getValue("grpCod"));
						PairTable<Object, Object> current = new PairTable<Object, Object>(DataBaseHelper.DB_TABLE_GROUPS_GROUPTYPES, groupTypeCode[0], groupCode);
                        updatePairTable(prev, current);

                    }
                } else returnValue = false;
            }
            return returnValue;
        } else
            return false;
    }

    /**
     * Removes a User from database and user photo from external storage
     *
     * @param u User to be removed
     */
    public void removeUser(User u) {
        removeRow(DataBaseHelper.DB_TABLE_USERS, u.getId());

        File externalPath = Environment.getExternalStorageDirectory();
        String packageName = db.getContext().getPackageName();
        File file = new File(externalPath.getAbsolutePath() + "/Android/data/" + packageName + "/", u.getPhotoFileName());
        //File file = new File(db.getContext().getExternalFilesDir(null), u.getPhotoFileName());
        file.delete();
    }

    /**
     * Removes a Group from database
     *
     * @param g Group to be removed
     */
    public void removeGroup(Group g) {
        removeRow(DataBaseHelper.DB_TABLE_GROUPS, g.getId());

        //Remove also relationships with courses and group types
        removeAllRows(DataBaseHelper.DB_TABLE_GROUPS_GROUPTYPES, "grpCod", g.getId());
        removeAllRows(DataBaseHelper.DB_TABLE_GROUPS_COURSES, "grpCod", g.getId());
    }

    /**
     * Removes a row from a database table
     *
     * @param id Identifier of row to be removed
     */
    public void removeRow(String table, long id) {
        List<Entity> rows = db.getEntityList(table, "id = " + id);
        Entity ent = rows.get(0);
        ent.delete();
    }

    /**
     * Removes all rows from a database table where fieldName has the given value as value
     *
     * @param fieldName Name field to search
     * @param value     Value field of row to be removed
     */
    public void removeAllRows(String table, String fieldName, Object value) {
        List<Entity> rows;
        
        if(value instanceof String) {
        	rows = db.getEntityList(table, fieldName + "='" + value + "'");
        } else {
        	rows = db.getEntityList(table, fieldName + "= " + value);
        }
        
        for (Entity ent : rows) {
            ent.delete();
        }
    }

    /**
     * Removes a PairTable from database
     *
     * @param p PairTable to be removed
     */
    public void removePairTable(PairTable<?, ?> p) {
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
     *
     * @param field A field of last notification
     * @return The field of last notification
     */
    public String getFieldOfLastNotification(String field) {
        String where = null;
        String orderby = "eventTime DESC";
        List<Entity> rows = db.getEntityList(DataBaseHelper.DB_TABLE_NOTIFICATIONS, where, orderby);
        String f = "0";

        if (rows.size() > 0) {
            Entity ent = rows.get(0);
            f = (String) ent.getValue(field);
        }

        return f;
    }

    /**
     * Gets last time the test was updated
     *
     * @param selectedCourseCode Test's course
     * @return Last time the test was updated
     */
    public String getTimeOfLastTestUpdate(long selectedCourseCode) {
        String where = "id=" + selectedCourseCode;
        String orderby = null;
        List<Entity> rows = db.getEntityList(DataBaseHelper.DB_TABLE_TEST_CONFIG, where, orderby);
        String f = "0";

        if (rows.size() > 0) {
            Entity ent = rows.get(0);
            f = (String) ent.getValue("editTime");
        }

        if (f == null) {
            f = "0";
        }

        return f;
    }

    /**
     * Gets the tags of specified course ordered by tagInd field
     *
     * @param selectedCourseCode Test's course
     * @return A list of the tags of specified course ordered by tagInd field
     */
    public List<TestTag> getOrderedCourseTags(long selectedCourseCode) {
        String[] columns = {"T.id", "T.tagTxt", "Q.qstCod", "Q.tagInd"};
        String tables = DataBaseHelper.DB_TABLE_TEST_TAGS + " AS T, " + DataBaseHelper.DB_TABLE_TEST_QUESTION_TAGS
                + " AS Q, " + DataBaseHelper.DB_TABLE_TEST_QUESTIONS_COURSE + " AS C";
        String where = "T.id=Q.tagCod AND Q.qstCod=C.qstCod AND C.crsCod=" + selectedCourseCode;
        String orderBy = "T.tagTxt ASC";
        String groupBy = "T.id";
        Cursor dbCursor = db.getDB().query(tables, columns, where, null, groupBy, null, orderBy);
        List<TestTag> result = new ArrayList<TestTag>();
        List<Integer> qstCodList;
        int idOld = -1;
        TestTag t = null;

        while (dbCursor.moveToNext()) {
            int id = dbCursor.getInt(0);

            if (id != idOld) {
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
     *
     * @param selectedCourseCode Test's course
     * @param tagsList           Tag's list of the questions to be extracted
     * @return A list of the questions of specified course and tags
     */
    public List<TestQuestion> getRandomCourseQuestionsByTagAndAnswerType(long selectedCourseCode, List<TestTag> tagsList,
                                                                         List<String> answerTypesList, int maxQuestions) {
        String select = "SELECT DISTINCT Q.id, Q.ansType, Q.shuffle, Q.stem, Q.feedback";
        String tables = " FROM " + DataBaseHelper.DB_TABLE_TEST_QUESTIONS + " AS Q, "
                + DataBaseHelper.DB_TABLE_TEST_QUESTIONS_COURSE + " AS C, "
                + DataBaseHelper.DB_TABLE_TEST_QUESTION_TAGS + " AS T";
        String where = " WHERE Q.id=C.qstCod AND Q.id=T.qstCod AND C.crsCod=" + selectedCourseCode;
        String orderby = " ORDER BY RANDOM()";
        String limit = " LIMIT " + maxQuestions;
        Cursor dbCursorQuestions, dbCursorAnswers;
        List<TestQuestion> result = new ArrayList<TestQuestion>();
        List<TestAnswer> answers;
        int tagsListSize = tagsList.size();
        int answerTypesListSize = answerTypesList.size();

        if (!tagsList.get(0).getTagTxt().equals("all")) {
            where += " AND (";
            for (int i = 0; i < tagsListSize; i++) {
                where += "T.tagCod=" + tagsList.get(i).getId();
                if (i < tagsListSize - 1) {
                    where += " OR ";
                }
            }
            where += ")";

            if (!answerTypesList.get(0).equals("all")) {
                where += " AND ";
            }
        }

        if (!answerTypesList.get(0).equals("all")) {
            if (tagsList.get(0).getTagTxt().equals("all")) {
                where += " AND ";
            }

            where += "(";
            for (int i = 0; i < answerTypesListSize; i++) {
                where += "Q.ansType='" + answerTypesList.get(i) + "'";

                if (i < answerTypesListSize - 1) {
                    where += " OR ";
                }
            }
            where += ")";
        }

        dbCursorQuestions = db.getDB().rawQuery(select + tables + where + orderby + limit, null);

        select = "SELECT DISTINCT A._id, A.ansInd, A.answer, A.correct, A.answerFeedback";
        tables = " FROM " + DataBaseHelper.DB_TABLE_TEST_ANSWERS + " AS A, "
                + DataBaseHelper.DB_TABLE_TEST_QUESTION_ANSWERS + " AS Q";
        orderby = " ORDER BY A.ansInd";

        while (dbCursorQuestions.moveToNext()) {
            int qstCod = dbCursorQuestions.getInt(0);
            String ansType = dbCursorQuestions.getString(1);
            boolean shuffle = Utils.parseStringBool(dbCursorQuestions.getString(2));
            String stem = dbCursorQuestions.getString(3);
            String questionFeedback = dbCursorQuestions.getString(4);
            TestQuestion q = new TestQuestion(qstCod, selectedCourseCode, stem, ansType, shuffle, questionFeedback);

            where = " WHERE Q.qstCod=" + qstCod + " AND Q.ansCod=A._id";
            dbCursorAnswers = db.getDB().rawQuery(select + tables + where + orderby, null);
            answers = new ArrayList<TestAnswer>();
            while (dbCursorAnswers.moveToNext()) {
                long ansCod = dbCursorAnswers.getLong(0);
                int ansInd = dbCursorAnswers.getInt(1);
                String answer = dbCursorAnswers.getString(2);
                boolean correct = dbCursorAnswers.getString(3).equals("true");
                String aswerFeedback = dbCursorAnswers.getString(4);

                answers.add(new TestAnswer(ansCod, ansInd, qstCod, correct, answer, aswerFeedback));
            }

            q.setAnswers(answers);
            result.add(q);
        }

        return result;
    }

    /**
     * Clear old notifications
     *
     * @param size Max table size
     */
    public void clearOldNotifications(int size) {
        String where = null;
        String orderby = "eventTime ASC";
        List<Entity> rows = db.getEntityList(DataBaseHelper.DB_TABLE_NOTIFICATIONS, where, orderby);
        int numRows = rows.size();
        int numDeletions = numRows - size;

        if (numRows > size) {
            for (int i = 0; i < numDeletions; i++)
                rows.get(i).delete();
        }
    }

    /**
     * Encrypts the notifications data
     */
    public void encryptNotifications() {
        List<Entity> rows = db.getEntityList(DataBaseHelper.DB_TABLE_NOTIFICATIONS);

        for (Entity ent : rows) {
            ent.setValue("eventType", crypto.encrypt(ent.getString("eventType")));
            ent.setValue("userSurname1", crypto.encrypt(ent.getString("userSurname1")));
            ent.setValue("userSurname2", crypto.encrypt(ent.getString("userSurname2")));
            ent.setValue("userFirstname", crypto.encrypt(ent.getString("userFirstname")));
            ent.setValue("userPhoto", crypto.encrypt(ent.getString("userPhoto")));
            ent.setValue("location", crypto.encrypt(ent.getString("location")));
            ent.setValue("summary", crypto.encrypt(ent.getString("summary")));
            ent.setValue("content", crypto.encrypt(ent.getString("content")));
            ent.save();
        }
    }

    /**
     * Encrypts the users data
     */
    public void encryptUsers() {
        List<Entity> rows = db.getEntityList(DataBaseHelper.DB_TABLE_USERS);

        for (Entity ent : rows) {
            ent.setValue("userID", crypto.encrypt(ent.getString("userID")));
            ent.setValue("userNickname", crypto.encrypt(ent.getString("userNickname")));
            ent.setValue("userSurname1", crypto.encrypt(ent.getString("userSurname1")));
            ent.setValue("userSurname2", crypto.encrypt(ent.getString("userSurname2")));
            ent.setValue("userFirstname", crypto.encrypt(ent.getString("userFirstname")));
            ent.setValue("userPhoto", crypto.encrypt(ent.getString("photoPath")));
            ent.save();
        }
    }

    /**
     * Reencrypts the notifications data
     */
    public void reencryptNotifications() {
        List<Entity> rows = db.getEntityList(DataBaseHelper.DB_TABLE_NOTIFICATIONS);
        String type, surname1, surname2, firstname, photo, location, summary, content;

        for (Entity ent : rows) {
            type = crypto.encrypt(OldCrypto.decrypt(mCtx, DBKey, ent.getString("eventType")));
            surname1 = crypto.encrypt(OldCrypto.decrypt(mCtx, DBKey, ent.getString("userSurname1")));
            surname2 = crypto.encrypt(OldCrypto.decrypt(mCtx, DBKey, ent.getString("userSurname2")));
            firstname = crypto.encrypt(OldCrypto.decrypt(mCtx, DBKey, ent.getString("userFirstname")));
            photo = crypto.encrypt(OldCrypto.decrypt(mCtx, DBKey, ent.getString("userPhoto")));
            location = crypto.encrypt(OldCrypto.decrypt(mCtx, DBKey, ent.getString("location")));
            summary = crypto.encrypt(OldCrypto.decrypt(mCtx, DBKey, ent.getString("summary")));
            content = crypto.encrypt(OldCrypto.decrypt(mCtx, DBKey, ent.getString("content")));

            ent.setValue("eventType", type);
            ent.setValue("userSurname1", surname1);
            ent.setValue("userSurname2", surname2);
            ent.setValue("userFirstname", firstname);
            ent.setValue("userPhoto", photo);
            ent.setValue("location", location);
            ent.setValue("summary", summary);
            ent.setValue("content", content);
            ent.save();
        }
    }

    /**
     * Empty table from database
     *
     * @param table Table to be emptied
     */
    public void emptyTable(String table) {
        db.emptyTable(table);
        Log.d(TAG, "Emptied table " + table);
    }

    /**
     * Delete table from database
     *
     * @param table Table to be deleted
     */
    public void deleteTable(String table) {
        db.deleteTable(table);
        Log.d(TAG, "Deleted table " + table);
    }

    /**
     * Delete all tables from database
     */
    public void clearDB() {
        SWADroidTracker.sendScreenView(mCtx, TAG + " clearDB");
        db.deleteTables();
        Log.i(TAG, "All tables deleted");
    }

    /**
     * Clean data of all tables from database. Removes users photos from external storage
     */
    public void cleanTables() {
        SWADroidTracker.sendScreenView(mCtx, TAG + " cleanTables");

        db.emptyTables();
        compactDB();

        // Removes users photos from external storage (Android 2.2 or higher only)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.FROYO) {
            File externalPath = Environment.getExternalStorageDirectory();
            String packageName = db.getContext().getPackageName();
            File dir = new File(externalPath.getAbsolutePath() + "/Android/data/" + packageName + "/");
            //File dir = db.getContext().getExternalFilesDir(null);
            String[] children = dir.list();
            if (children != null) {
                for (String aChildren : children) {
                    File childrenFile = new File(dir, aChildren);
                    if (childrenFile.exists()) childrenFile.delete();
                }
            }
        }

        Log.i(TAG, "All tables emptied");
    }

    /**
     * Begin a database transaction
     */
    public synchronized void beginTransaction() {
    	while(db.inTransaction()) {
	    	try {
				wait();
			} catch (InterruptedException e) {
                //Send exception details to Google Analytics
                SWADroidTracker.sendException(mCtx, e, false);
			}
    	}
    	
        db.startTransaction();
        
        Log.i(TAG, "Database locked");
    }

    /**
     * End a database transaction
     */
    public synchronized void endTransaction(boolean successfulTransaction) {
    	if(db.inTransaction()) {
    		if(successfulTransaction) {
    			db.successfulTransaction();
    		}
    		
	        db.endTransaction();
	        
	        Log.i(TAG, "Database unlocked");
	        
	        notifyAll();
    	} else {
            //Send exception details to Google Analytics
            SWADroidTracker.sendException(mCtx, new DataBaseHelperException("No active transactions"), false);
    	}
    }
    
    public boolean isDbInTransaction() {
    	return db.inTransaction();
    }

    /**
     * Compact the database
     */
    private void compactDB() {
        db.getDB().execSQL("VACUUM;");
        Log.i(TAG, "Database compacted");
    }

    /**
     * Initializes the database structure for the first use
     */
    public void initializeDB() {
        db.getDB().execSQL("CREATE UNIQUE INDEX IF NOT EXISTS " + DataBaseHelper.DB_TABLE_TEST_QUESTION_TAGS + "_unique on "
                + DataBaseHelper.DB_TABLE_TEST_QUESTION_TAGS + "(qstCod, tagCod);");
    }

    /**
     * Upgrades the database structure
     */
    public void upgradeDB(Context context) {
        int dbVersion = db.getDB().getVersion();
		/* 
		 * Modify database keeping data:
		 * 1. Create temporary table __DB_TABLE_GROUPS (with the new model)
		 * 2. insert in the temporary table all the data (in the new model) from the old table
		 * 3. drop table DB_TABLE_GROUPS
		 * 4. create DB_TABLE_GROUPS with the new model. 
		 * 5. insert in DB_TABLE_GROUPS all the data from __DB_TABLE_GROUPS
		 * 6. insert in DB_TABLE_GROUPS_GROUPTYPES the relationships between the deleted groups and group types
		 * 7. drop __DB_TABLE_GROUPS
		 * Just to modify database without to keep data just 7,6.
		 * 
		 * */
        
        /* version 15-16
		 * changes on notifications table: 
		 * - new field notifCode
		 * - changed field id to eventCode
		 * */
        if (dbVersion == 16) {
        	//without keeping data
        	db.getDB().execSQL("DROP TABLE " + DataBaseHelper.DB_TABLE_NOTIFICATIONS + ";");
            db.getDB().execSQL("CREATE TABLE " + DataBaseHelper.DB_TABLE_NOTIFICATIONS + " (_id integer primary key autoincrement, notifCode long, eventCode long, eventType text, eventTime text,"
                    + " userSurname1 text, userSurname2 text, userFirstname text, userPhoto text, location text, summary text, status text, content text, seenLocal text, seenRemote text); ");
        }

        Log.i(TAG, "Database upgraded");

        compactDB();
    }

	/**
	 * Indicates if the db was cleaned
	 */
	public static boolean isDbCleaned() {
	    return dbCleaned;
	}

	/**
	 * Set the fact that the db was cleaned
	 */
	public static void setDbCleaned(boolean state) {
	    dbCleaned = state;
	}
}
