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

/**
 * Global data of application.
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class Global {
	/**
	 * SWAD application key
	 */
	private static final String AppKey = "HTC-Desire";
    /**
     * User logged flag
     */
    private static boolean logged;
	/**
	 * Name of application preferences
	 */
	private static final String PREFS_NAME = "SWADroidSharedPrefs";
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
     * Student type constant
     */
    public static String STUDENT_TYPE = "student";
    /**
     * Teacher type constant
     */
    public static String TEACHER_TYPE = "teacher";
    /**
     * Class Module's tag name for Logcat
     */
    public static final String MODULE_TAG = "Module";
    /**
     * Login tag name for Logcat
     */
    public static final String LOGIN_TAG = "Login";
    /**
     * Notifications tag name for Logcat
     */
    public static final String NOTIFICATIONS_TAG = "Notifications";
    /*
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
     * Table name for relationship between test's questions and courses
     */ 
    public static final String DB_TABLE_TEST_QUESTIONS_COURSES = "tst_questions_courses";
    
	/**
	 * Gets the SWAD application key
	 * @return SWAD application key
	 */
	public static String getAppKey() {
		return AppKey;
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
	 * Gets Application preferences name
	 * @return SharedPreferences name
	 */
	public static String getPrefsName() {
		return PREFS_NAME;
	}
}
