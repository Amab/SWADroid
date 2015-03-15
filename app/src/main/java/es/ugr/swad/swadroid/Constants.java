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

import android.os.Environment;

import java.io.File;

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
     *
     * @deprecated Use {@link Config#SWAD_APP_KEY} instead
     */
    public static final String SWAD_APP_KEY = Config.SWAD_APP_KEY;

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
     * Null value returned by webservices when a field is empty
     */
    public static final String NULL_VALUE = "anyType{}";

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
     * Request code for Rollcall EventsDownload Download module.
     */
    public static final int ROLLCALL_EVENTS_DOWNLOAD_REQUEST_CODE = 15;

    /**
     * Request code for Rollcall EventsSend Download module.
     */
    public static final int ROLLCALL_EVENTS_SEND_REQUEST_CODE = 16;

    /**
     * Request code for Rollcall UsersDownload module.
     */
    public static final int ROLLCALL_USERS_DOWNLOAD_REQUEST_CODE = 17;

    /**
     * Request code for Rollcall UsersSend module.
     */
    public static final int ROLLCALL_USERS_SEND_REQUEST_CODE = 18;

    /**
     * Request code for Downloads Manager
     */
    public static final int DOWNLOADSMANAGER_REQUEST_CODE = 19;

    /**
     * Request code for Notify Download
     */
    public static final int NOTIFYDOWNLOAD_REQUEST_CODE = 20;

    /**
     * Request code for MyGroups Manager
     */
    public static final int MYGROUPSMANAGER_REQUEST_CODE = 21;

    /**
     * Request code for Group Types module
     */
    public static final int GROUPTYPES_REQUEST_CODE = 22;

    /**
     * Request code for SendMyGroups
     */
    public static final int SENDMYGROUPS_REQUEST_CODE = 23;

    /**
     * Request code for GetFile Manager
     */
    public static final int GETFILE_REQUEST_CODE = 24;

    /**
     * Request code for Generate QR
     */
    public static final int GENERATE_QR_REQUEST_CODE = 25;

    /**
     * Request code for Information
     */
    public static final int INFORMATION_REQUEST_CODE = 26;

    /**
     * Request code for Introduction
     */
    public static final int INTRODUCTION_REQUEST_CODE = 27;

    /**
     * Request code for FAQs
     */
    public static final int FAQS_REQUEST_CODE = 28;

    /**
     * Request code for Bibliography
     */
    public static final int BIBLIOGRAPHY_REQUEST_CODE = 29;

    /**
     * Request code for Practices Program
     */
    public static final int SYLLABUSPRACTICALS_REQUEST_CODE = 30;

    /**
     * Request code for Theory Program
     */
    public static final int SYLLABUSLECTURES_REQUEST_CODE = 31;

    /**
     * Request code for Links
     */
    public static final int LINKS_REQUEST_CODE = 32;

    /**
     * Request code for Teaching Guide
     */
    public static final int TEACHINGGUIDE_REQUEST_CODE = 33;

    /**
     * Request code for NotificationMarkAsRead module
     */
    public static final int NOTIFMARKALLASREAD_REQUEST_CODE = 34;

    /**
     * Request code for Assessment
     */
    public static final int ASSESSMENT_REQUEST_CODE = 35;

    /**
     * Request code for recover Password
     */
    public static final int RECOVER_PASSWORD_REQUEST_CODE = 36;

    /**
     * Prefix tag name for Logcat
     */
    public static final String APP_TAG = "SWADroid";

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
     * Group position inside the main menu for User group
     */
    public static final int USERS_GROUP = 2;

    /**
     * Group position inside the main menu for Messages group
     */
    public static final int MESSAGES_GROUP = 3;

    /**
     * Group position inside the main menu for Enrollment group
     */
    public static final int ENROLLMENT_GROUP = 4;

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
     * Child position inside the users menu for Groups
     */
    public static final int MYGROUPS_CHILD = 0;

    /**
     * Child position inside the users menu for Generate QR
     */
    public static final int GENERATE_QR_CHILD = 1;

    /**
     * Child position inside the users menu for Rollcall
     */
    public static final int ROLLCALL_CHILD = 2;

    /**
     * Path for downloaded files
     */
    public static final String DIRECTORY_SWADROID = "SWADroid";

    public static final String DOWNLOADS_PATH =
            Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
                    + DIRECTORY_SWADROID;

    /**
     * Username template for messages
     */
    public static final String USERNAME_TEMPLATE = "{userName}";
}
