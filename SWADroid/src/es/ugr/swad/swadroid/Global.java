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
     * Request code for Login module.
     */
    public static final int LOGIN_REQUEST_CODE = 1;
    /**
     * Table name for courses
     */
    public static final String DB_TABLE_COURSES = "courses";
    /**
     * Table name for notices
     */
    public static final String DB_TABLE_NOTICES = "notices";
    /**
     * Table name for students
     */
    public static final String DB_TABLE_STUDENTS = "students";
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
    public static final String DB_TABLE_TEST_tags = "tst_tags";
    /**
     * Table name for test's configuration
     */ 
    public static final String DB_TABLE_TEST_CONFIG = "tst_config";
    /**
     * Table name for test's configuration
     */ 
    public static final String DB_TABLE_MSG_CONTENT = "msg_content";
    /**
     * Table name for test's configuration
     */ 
    public static final String DB_TABLE_MSG_RCV = "msg_rcv";
    /**
     * Table name for test's configuration
     */ 
    public static final String DB_TABLE_MSG_SNT = "msg_snt";
    /**
     * Table name for test's configuration
     */ 
    public static final String DB_TABLE_MARKS = "marks";
    /**
     * Table name for relationship between notices and courses
     */
    public static final String DB_TABLE_NOTICES_COURSES = "notices_courses";
    /**
     * Table name for relationship between students and courses
     */ 
    public static final String DB_TABLE_STUDENTS_COURSES = "students_courses";
    /**
     * Table name for relationship between test's questions and courses
     */ 
    public static final String DB_TABLE_TEST_QUESTIONS_COURSES = "tst_questions_courses";
    /**
     * Table name for for relationship between test's questions and tags
     */ 
    public static final String DB_TABLE_TEST_QUESTIONS_TAGS = "tst_questions_tags";
    /*
     * User logged flag
     */
    public static boolean logged;
    /**
     * Class Module's tag name for Logcat
     */
    public static final String MODULE_TAG = "Module";
}
