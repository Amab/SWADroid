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

import java.util.Random;

import es.ugr.swad.swadroid.model.User;

/**
 * Constants of application.
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 * @author Antonio Aguilera Malagon <aguilerin@gmail.com>
 * @author Helena Rodriguez Gijon <hrgijon@gmail.com>
 * @author Jose Antonio Guerrero Aviles <cany20@gmail.com>
 */
public class Constants {
	/**
     * SWAD application key
     */
    public static final String SWAD_APP_KEY = ""; //DELETE THE KEY BEFORE COMMIT!!!
    /**
     * BugSense application key
     */
    public static final String BUGSENSE_API_KEY = ""; //DELETE THE KEY BEFORE COMMIT!!!
    /**
     * Server URL
     */
    public static final String DEFAULT_SERVER = "swad.ugr.es";
    /**
     * Account type
     */
    public static final String ACCOUNT_TYPE = "es.ugr.swad.swadroid";
    /**
     * Synchronization authority
     */
    public static final String AUTHORITY = "es.ugr.swad.swadroid.content";
    /**
     * Default synchronization time for notifications (in minutes)
     */
    public static final long DEFAULT_SYNC_TIME = 60;
    /**
     * Connection timeout (in milliseconds)
     */
    public static final int CONNECTION_TIMEOUT = 60000;
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
     */
    private static String selectedCourseShortName;
    /**
     * Short name of the full course.
     */
    private static String selectedCourseFullName;
    /**
     * Role of the logged User in the current selected course
     */
    private static int currentUserRole = -1;
    /**
     * Indicates if there are changes on preferences
     */
    private static boolean preferencesChanged = false;
    /**
     * Indicates if there are changes on db
     */
    public static boolean dbCleaned = false;
    /**
     * Base string to generate random alphanumeric strings
     */
    public static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    /**
     * Random generator
     */
    public static final Random rnd = new Random();
    /**
     * Null value returned by webservices when a field is empty
     */
    public static final String NULL_VALUE = "anyType{}";
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
    public static final int NOTICES_REQUEST_CODE = 9;
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
     */
    public static final int DIRECTORY_TREE_REQUEST_CODE = 13;
    /**
     * Request code for Directory Tree Download module
     */
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
     */
    public static final int DOWNLOADSMANAGER_REQUEST_CODE = 20;
    /**
     * Request code for Notify Download
     */
    public static final int NOTIFYDOWNLOAD_REQUEST_CODE = 21;
    /**
     * Request code for MyGroups Manager
     */
    public static final int MYGROUPSMANAGER_REQUEST_CODE = 22;
    /**
     * Request code for Group Types module
     */
    public static final int GROUPTYPES_REQUEST_CODE = 23;
    /**
     * Request code for SendMyGroups
     */
    public static final int SENDMYGROUPS_REQUEST_CODE = 24;
    /**
     * Request code for GetFile Manager
     */
    public static final int GETFILE_REQUEST_CODE = 25;
    /**
     * Request code for Generate QR
     */
    public static final int GENERATE_QR_REQUEST_CODE = 26;    
    /**
     * Request code for Information
     */
    public static final int INFORMATION_REQUEST_CODE = 27;
    
    /**
     * Request code for Introduction
     */
    public static final int INTRODUCTION_REQUEST_CODE = 28;
    
    /**
     * Request code for FAQs
     */
    public static final int FAQS_REQUEST_CODE = 29;
    
    /**
     * Request code for Bibliography
     */
    public static final int BIBLIOGRAPHY_REQUEST_CODE = 30;
    
    /**
     * Request code for Practices Program
     */
    public static final int PRACTICESPROGRAM_REQUEST_CODE = 31;
    
    /**
     * Request code for Theory Program
     */
    public static final int THEORYPROGRAM_REQUEST_CODE = 32;

    /**
     * Request code for Links
     */
    public static final int LINKS_REQUEST_CODE = 33;
    
    /**
     * Request code for Teaching Guide
     */
    public static final int TEACHINGGUIDE_REQUEST_CODE = 34; 
    /**
     * Request code for NotificationMarkAsRead module
     */
    public static final int NOTIFMARKALLASREAD_REQUEST_CODE = 35; 
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
     * Student userRole for getUsers web service.
     */
    public static final int STUDENT_TYPE_CODE = 2;
    /**
     * Teacher userTypeCode for getUsers web service.
     */
    public static final int TEACHER_TYPE_CODE = 3;
    /**
     * Code to access to the documents in documents area
     */
    public static final int DOCUMENTS_AREA_CODE = 1;
    /**
     * Code to access to the documents in share area
     */
    public static final int SHARE_AREA_CODE = 2;
    /**
     * Group position inside the main menu for Course group
     */
    public static final int COURSE_GROUP = 0;
    /**
     * Group position inside the main menu for Evaluation group
     */
    public static final int EVALUATION_GROUP = 1;
    /**
     * Group position inside the main menu for Messages group
     */
    public static final int MESSAGES_GROUP = 2;
    /**
     * Group position inside the main menu for Enrollment group
     */
    public static final int ENROLLMENT_GROUP = 3;
    /**
     * Group position inside the main menu for User group
     */
    public static final int USERS_GROUP = 4;
    /**
     * Child position inside the messages menu for Notification
     */
    public static final int NOTIFICATION_CHILD = 0;
    /**
     * Child position inside the messages menu for Send message
     */
    public static final int SEND_MESSAGES_CHILD = 1;
    /**
     * Child position inside the messages menu for Publish Note
     */
    public static final int PUBLISH_NOTE_CHILD = 2;
    /**
     * Child position inside the evaluation menu for Tests
     */
    public static final int TESTS_CHILD = 0;
    /**
     * Child position inside the course menu for Documents
     */
    public static final int DOCUMENTS_CHILD = 0;
    /**
     * Child position inside the course menu for Shared area
     */
    public static final int SHARED_AREA_CHILD = 1;
    /**
     * Child position inside the users menu for Generate QR
     */
    public static final int GENERATE_QR_CHILD = 0;
    /**
     * Child position inside the users menu for Rollcall
     */
    public static final int ROLLCALL_CHILD = 1;
    /**
     * Child position inside the enrollment menu for My Groups
     */
    public static final int MYGROUPS_CHILD = 0;

    /**
     * Checks if user is already logged on SWAD
     *
     * @return User logged flag
     */
    public static boolean isLogged() {
        return logged;
    }

    /**
     * Sets user logged flag
     *
     * @param logged User logged flag
     */
    public static void setLogged(boolean logged) {
        Constants.logged = logged;
    }

    /**
     * Gets the user logged on SWAD
     */
    public static User getLoggedUser() {
        return loggedUser;
    }

    /**
     * Sets the user logged on SWAD
     */
    public static void setLoggedUser(User loggedUser) {
        Constants.loggedUser = loggedUser;
    }

    /**
     * Gets start time of application
     *
     * @return Start time of application
     */
    public static long getLastLoginTime() {
        return lastLoginTime;
    }

    /**
     * Sets start time of application
     *
     * @param l Start time of application
     */
    public static void setLastLoginTime(long l) {
        Constants.lastLoginTime = l;
    }

    /**
     * Gets code of current course
     * return -1 if no course chosen; code of current course in other case
     */
    public static long getSelectedCourseCode() {
        return selectedCourseCode;
    }

    /**
     * Sets code of current course
     */
    public static void setSelectedCourseCode(long currentCourseCode) {
        //if (currentCourseCode > 0) selectedCourseCode = currentCourseCode;
        selectedCourseCode = currentCourseCode;
    }

    /**
     * Sets user role in the current selected course
     *
     * @param userRole: Role of the user: 0- unknown STUDENT_TYPE_CODE - student TEACHER_TYPE_CODE - teacher
     */
    public static void setCurrentUserRole(int userRole) {
        if (userRole == 0 || userRole == TEACHER_TYPE_CODE || userRole == STUDENT_TYPE_CODE)
            currentUserRole = userRole;
        else
            currentUserRole = -1;
    }

    public static boolean isPreferencesChanged() {
        return preferencesChanged;
    }

    /**
     * Set the fact that the preferences has changed
     */
    public static void setPreferencesChanged() {
        preferencesChanged = true;
    }

    /**
     * Indicates if the preferences has changed
     *
     * @param newState - true when the preferences has changed  and it was not handled it
     *                 - false if the preferences has not changed
     */
    public static void setPreferencesChanged(boolean newState) {
        preferencesChanged = newState;
    }

    public static void setSelectedCourseShortName(String currentCourseShortName) {
        selectedCourseShortName = currentCourseShortName;

    }

    public static void setSelectedCourseFullName(String currentCourseFullName) {
        selectedCourseFullName = currentCourseFullName;

    }

    public static String getSelectedCourseShortName() {
        return selectedCourseShortName;

    }

    public static String getSelectedCourseFullName() {
        return selectedCourseFullName;

    }

    /**
     * Gets the role of the logged user in the current selected course
     *
     * @return -1 if the user role has not been fixed,
     *         0  if the user role is unknown
     *         2 (STUDENT_TYPE_CODE) if the user is a student
     *         3 (TEACHER_TYPE_CODE) if the user is a teacher
     */
    public static int getCurrentUserRole() {
        return currentUserRole;
    }
}
