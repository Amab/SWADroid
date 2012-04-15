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

import es.ugr.swad.swadroid.model.User;

/**
 * Global data of application.
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 * @author Antonio Aguilera Malagon <aguilerin@gmail.com>
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
	 * Indicates if there are changes on db
	 * */
	private static boolean preferencesChanged = false;
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
	public static final int NOTICES_REQUESET_CODE =  9;
	/**
	 * Request code for Attendance module.
	 */
	public static final int ATTENDANCE_REQUEST_CODE = 10;
	/**
	 * Request code for Attendance module.
	 */
	public static final int ATTENDANCE_CONFIG_DOWNLOAD_REQUEST_CODE = 11;
	/**
	 * Request code for Attendance module.
	 */
	public static final int SCAN_QR_REQUEST_CODE = 12;
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
	 * Student userTypeCode for getUsers web service.
	 */
	public static final int STUDENT_TYPE_CODE = 2;
	/**
	 * Teacher userTypeCode for getUsers web service.
	 */
	public static final int TEACHER_TYPE_CODE = 3;

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
	 * Gets code of actual course
	 * return -1 if no course chosen; code of actual course in other case
	 * */
	public static long getSelectedCourseCode(){
		return selectedCourseCode;
	}
	/**
	 * Sets code of actual course
	 * @param courseCode. Code of the chosen course. It should be courseCode>0. Otherwise nothing will change
	 * */
	public static void setSelectedCourseCode(long actualCourseCode){
		if(actualCourseCode >0) selectedCourseCode = actualCourseCode;
	}

	public static boolean isPreferencesChanged(){
		return preferencesChanged;
	}

	public static void setPreferencesChanged(){
		preferencesChanged = true;
	}
	public static void setPreferencesChanged(boolean newState){
		preferencesChanged = newState;
	}
}
