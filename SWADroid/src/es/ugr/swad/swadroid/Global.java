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

package es.ugr.swad.swadroid;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import es.ugr.swad.swadroid.model.User;

/**
 * Global data of application.
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 * @author Antonio Aguilera Malagon <aguilerin@gmail.com>
 * @author Helena Rodriguez Gijon <hrgijon@gmail.com>
 */
public class Global {
	/**
	 * SWAD application key
	 */
	private static final String AppKey = "";
	/**
	 * Server URL
	 */
	private static final String DEFAULT_SERVER = "swad.ugr.es";
	/**
	 * User logged flag
	 */
	private static boolean logged;
	/**
	 * Logged user
	 */
	private static User loggedUser;
	/**
	 * Time of application's last login
	 */
	private static long lastLoginTime;
	/**
	 * Code of the chosen course. All next actions are referred to this course.
	 */
	private static long selectedCourseCode = -1;
	/**
	 * Short name of the chosen course.
	 * */
	private static String selectedCourseShortName;
	/**
	 * Short name of the full course.
	 * */
	private static String selectedCourseFullName;
		

	/**
	 * Code of the chosen course for rollcall. All next actions are referred to this course.
	 */
	private static long selectedRollcallCourseCode = -1;
	/**
	 * Indicates if there are changes on preferences
	 * */
	private static boolean preferencesChanged = false;
	/**
	 * Indicates if there are changes on db
	 * */
	private static boolean dbCleaned = false;
	/**
	 * Null value returned by webservices when a field is empty
	 */
	public static final String NULL_VALUE = "anytype{}";
	/**
	 * Time to force relogin
	 */
	public static final int RELOGIN_TIME = 86400000; //24h
	/**
	 * Request code for Login module.
	 */
	public static final int LOGIN_REQUEST_CODE = 1;
	/**
	 * Request code for Courses module.
	 */
	public static final int COURSES_REQUEST_CODE = 2;
	/**
	 * Request code for Notifications module.
	 */
	public static final int NOTIFICATIONS_REQUEST_CODE = 3;
	/**
	 * Request code for Tests module.
	 */
	public static final int TESTS_REQUEST_CODE = 4;
	/**
	 * Request code for Tests module.
	 */
	public static final int TESTS_CONFIG_DOWNLOAD_REQUEST_CODE = 5;
	/**
	 * Request code for Tests module.
	 */
	public static final int TESTS_QUESTIONS_DOWNLOAD_REQUEST_CODE = 6;
	/**
	 * Request code for Tests module.
	 */
	public static final int TESTS_MAKE_REQUEST_CODE = 7;
	/**
	 * Request code for Tests module.
	 */
	public static final int MESSAGES_REQUEST_CODE = 8;
	/**
	 * Request code for Notice module
	 */
	public static final int NOTICES_REQUEST_CODE =  9;
	/**
	 * Request code for Rollcall module.
	 */
	public static final int ROLLCALL_REQUEST_CODE = 10;
	/**
	 * Request code for Scan QR module.
	 */
	public static final int SCAN_QR_REQUEST_CODE = 12;
	/**
	 * Request code for Directory Tree Download module
	 * */
	public static final int DIRECTORY_TREE_REQUEST_CODE = 13;
	/**
	 * Request code for Directory Tree Download module
	 * */
	public static final int GROUPS_REQUEST_CODE = 14;
	/**
	 * Request code for Rollcall Config Download module.
	 */
	public static final int ROLLCALL_CONFIG_DOWNLOAD_REQUEST_CODE = 15;
	/**
	 * Request code for Rollcall History module.
	 */
	public static final int ROLLCALL_HISTORY_REQUEST_CODE = 16;
	/**
	 * Request code for Students List module.
	 */
	public static final int STUDENTS_LIST_REQUEST_CODE = 17;
	/**
	 * Request code for Students History module.
	 */
	public static final int STUDENTS_HISTORY_REQUEST_CODE = 18;
	/**
	 * Request code for Sessions List module.
	 */
	public static final int SESSIONS_LIST_REQUEST_CODE = 19;
    /**
     * Request code for Downloads Manager
     * */
    public static final int DOWNLOADSMANAGER_REQUEST_CODE = 20;
    /**
     * Request code for Notify Download
     * */
    public static final int NOTIFYDOWNLOAD_REQUEST_CODE = 21;
    /**
     * Request code for MyGroups Manager
     * */
    public static final int MYGROUPSMANAGER_REQUEST_CODE= 22;
    /**
     * Request code for MyGroups Manager
     * */
    public static final int GROUPTYPES_REQUEST_CODE= 23;
    /**
     * Request code for SendMyGroups Manager
     * */
    public static final int SENDMYGROUPS_REQUEST_CODE = 24;
	/**
	 * Prefix tag name for Logcat
	 */
	public static final String APP_TAG = "SWADroid";
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
	 * */
	public static final String DB_TABLE_GROUPS = "groups";
	/**
	 * Table name for relationship between groups and courses
	 * */
	public static final String DB_TABLE_GROUPS_COURSES = "group_course";
	/**
	 * Table name for group types
	 * */
	public static final String DB_TABLE_GROUP_TYPES = "group_types";
	/**
	 * Table name for relationship between groups and group types
	 * */
	public static final String DB_TABLE_GROUPS_GROUPTYPES = "group_grouptypes";
	/**
	 * Table name for practice sessions
	 * */
	public static final String DB_TABLE_PRACTICE_SESSIONS = "practice_sessions";
	/**
	 * Table name for rollcall
	 * */
	public static final String DB_TABLE_ROLLCALL = "rollcall";
	/**
	 * Student userRole for getUsers web service.
	 */
	public static final int STUDENT_TYPE_CODE = 2;
	/**
	 * Teacher userTypeCode for getUsers web service.
	 */
	public static final int TEACHER_TYPE_CODE = 3;
    /**
     * Code to access to the documents in documents area 
     * */
    public static int DOCUMENTS_AREA_CODE= 1;
    /**
     * Code to access to the documents in share area 
     * */
    public static int SHARE_AREA_CODE= 2;

	/**
	 * Gets the SWAD application key
	 * @return SWAD application key
	 */
	public static String getAppKey() {
		return AppKey;
	}    
	/**
	 * Gets the server URL
	 * @return Server URL
	 */
	public static String getDefaultServer() {
		return DEFAULT_SERVER;
	}
	/**
	 * Checks if user is already logged on SWAD
	 * @return User logged flag
	 */
	public static boolean isLogged() {
		return logged;
	}
	/**
	 * Sets user logged flag
	 * @param logged User logged flag
	 */
	public static void setLogged(boolean logged) {
		Global.logged = logged;
	}
	/**
	 * Gets the user logged on SWAD
	 * @param logged User logged flag
	 */
	public static User getLoggedUser() {
		return loggedUser;
	}
	/**
	 * Sets the user logged on SWAD
	 * @param logged User logged flag
	 */
	public static void setLoggedUser(User loggedUser) {
		Global.loggedUser = loggedUser;
	}

	/**
	 * Gets start time of application
	 * @return Start time of application
	 */
	public static long getLastLoginTime() {
		return lastLoginTime;
	}
	/**
	 * Sets start time of application
	 * @param l Start time of application
	 */
	public static void setLastLoginTime(long l) {
		Global.lastLoginTime = l;
	}
	/**
	 * Function to parse from Integer to Boolean
	 * @param n Integer to be parsed
	 * @return true if n!=0, false in other case
	 */
	public static boolean parseIntBool(int n) {
		return n!=0;
	}

	/**
	 * Function to parse from String to Boolean
	 * @param s String to be parsed
	 * @return true if s equals "Y", false in other case
	 */
	public static boolean parseStringBool(String s) {
		return s.equals("Y") ? true : false;
	}

	/**
	 * Function to parse from Boolean to Integer
	 * @param b Boolean to be parsed
	 * @return 1 if b==true, 0 in other case
	 */
	public static int parseBoolInt(boolean b) {
		return b ? 1 : 0;
	}

	/**
	 * Function to parse from Boolean to String
	 * @param b Boolean to be parsed
	 * @return "Y" if b==true, "N" in other case
	 */
	public static String parseBoolString(boolean b) {
		return b ? "Y" : "N";
	}
	/**
	 * Gets code of current course
	 * return -1 if no course chosen; code of current course in other case
	 * */
	public static long getSelectedCourseCode(){
		return selectedCourseCode;
	}
	/**
	 * Sets code of current course
	 * @param courseCode. Code of the chosen course. It should be courseCode>0. Otherwise nothing will change
	 * */
	public static void setSelectedCourseCode(long currentCourseCode){
		if(currentCourseCode >0) selectedCourseCode = currentCourseCode;
	}

	public static boolean isPreferencesChanged(){
		return preferencesChanged;
	}
	/**
	 * Set the fact that the preferences has changed
	 * @param newState - true when the preferences has changed 
	 * 				   - false after the fact is noticed and handled it
	 * */
	public static void setPreferencesChanged(){
		preferencesChanged = true;
	}
	/**
	 * Indicates if the preferences has changed
	 * @param newState - true when the preferences has changed  and it was not handled it
	 * 				   - false if the preferences has not changed
	 * */
	public static void setPreferencesChanged(boolean newState){
		preferencesChanged = newState;
	}
	
	public static void setSelectedCourseShortName(String currentCourseShortName){
		selectedCourseShortName = currentCourseShortName;
		
	}
	public static void setSelectedCourseFullName(String currentCourseFullName){
		selectedCourseFullName = currentCourseFullName;
		
	}
	public static String getSelectedCourseShortName(){
		return selectedCourseShortName;
		
	}
	public static String getSelectedCourseFullName(){
		return selectedCourseFullName;
		
	}
		/**
	 * Gets code of actual rollcall course
	 * return -1 if no rollcall course chosen; code of actual rollcall course in other case
	 * */
	public static long getSelectedRollcallCourseCode() {
		return selectedRollcallCourseCode;
	}

	/**
	 * Sets code of actual rollcall course
	 * @param actualCourseCode. Code of the chosen rollcall course. It should be actualCourseCode>0. Otherwise nothing will change
	 * */
	public static void setSelectedRollcallCourseCode(long actualCourseCode) {
		selectedRollcallCourseCode = actualCourseCode;
	}
	/**
	 * Checks if any connection is available 
	 * @param ctx Application context
	 * @return true if there is a connection available, false in other case
	 */
	public static boolean connectionAvailable(Context ctx){
	    boolean connAvailable = false;
	    ConnectivityManager connec =  (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
	
	    //Survey all networks (wifi, gprs...)
	    NetworkInfo[] networks = connec.getAllNetworkInfo();
	    
	    for(int i=0; i<networks.length; i++){
	        //If any of them has a connection available, put boolean to true
	        if (networks[i].isConnected()){
	            connAvailable = true;
	        }
	    }
	    
	    //If boolean remains false there is no connection available        
	    return connAvailable;
	}
	/**
	 * Set the fact that the db was cleaned
	 * @param newState - true when the database was cleaned
	 * 				   - false after the fact is noticed and handled it
	 * */
	public static void setDbCleaned(boolean state){
		dbCleaned = state;
	}
	/**
	 * Indicates if the db was cleaned
	 * @param newState - true when the database was cleaned and it was not handled it
	 * 				   - false if the database does not change
	 * */
	public static boolean isDbCleaned(){
		return dbCleaned;
	}
}
