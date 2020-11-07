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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.ugr.swad.swadroid.database.DataBaseHelper;
import es.ugr.swad.swadroid.gui.DialogFactory;
import es.ugr.swad.swadroid.gui.MenuExpandableListActivity;
import es.ugr.swad.swadroid.gui.ProgressScreen;
import es.ugr.swad.swadroid.gui.TextExpandableListAdapter;
import es.ugr.swad.swadroid.model.Course;
import es.ugr.swad.swadroid.model.Model;
import es.ugr.swad.swadroid.modules.courses.Courses;
import es.ugr.swad.swadroid.modules.downloads.DownloadsManager;
import es.ugr.swad.swadroid.modules.games.GamesList;
import es.ugr.swad.swadroid.modules.groups.MyGroupsManager;
import es.ugr.swad.swadroid.modules.indoorlocation.IndoorLocation;
import es.ugr.swad.swadroid.modules.information.Information;
import es.ugr.swad.swadroid.modules.login.Login;
import es.ugr.swad.swadroid.modules.login.LoginActivity;
import es.ugr.swad.swadroid.modules.messages.Messages;
import es.ugr.swad.swadroid.modules.notices.Notices;
import es.ugr.swad.swadroid.modules.notifications.Notifications;
import es.ugr.swad.swadroid.modules.qr.GenerateQR;
import es.ugr.swad.swadroid.modules.rollcall.Rollcall;
import es.ugr.swad.swadroid.modules.tests.Tests;
import es.ugr.swad.swadroid.preferences.Preferences;
import es.ugr.swad.swadroid.sync.AccountAuthenticator;
import es.ugr.swad.swadroid.sync.SyncUtils;
import es.ugr.swad.swadroid.utils.DateTimeUtils;
import es.ugr.swad.swadroid.utils.Utils;

/**
 * Main class of the application.
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 * @author Antonio Aguilera Malagon <aguilerin@gmail.com>
 * @author Helena Rodriguez Gijon <hrgijon@gmail.com>
 * @author Jose Antonio Guerrero Aviles <cany20@gmail.com>
 * @author Sergio Díaz Rueda <sergiodiazrueda8@gmail.com>
 */
public class SWADMain extends MenuExpandableListActivity {
    /**
     * Function name field
     */
    private static final String NAME = "listText";
    /**
     * Function text field
     */
    private static final String IMAGE = "listIcon";
    /**
     * Code of selected course
     */
    private long courseCode;
    /**
     * User courses list
     */
    private List<Model> listCourses;
    /**
     * SWADMain tag name for Logcat
     */
    private static final String TAG = Constants.APP_TAG;

    /**
     * Indicates if it is the first run
     */
    private boolean firstRun = false;


    /**
     * Current role 2 - student 3 - teacher -1 - none role was chosen
     */
    private int currentRole = -1;

    private LinearLayout mNotifyLayout;
    private TextView mNotifyTextView;
    private ImageView mNotifyImageView;
    private ExpandableListView mExpandableListView;
    private TextExpandableListAdapter mExpandableListAdapter;
    private Spinner mCoursesSpinner;

    private final ArrayList<HashMap<String, Object>> mHeaderData = new ArrayList<>();
    private final ArrayList<ArrayList<HashMap<String, Object>>> mChildData = new ArrayList<>();
    private final ArrayList<HashMap<String, Object>> mMessagesData = new ArrayList<>();
    private final ArrayList<HashMap<String, Object>> mUsersData = new ArrayList<>();

    private ProgressScreen mProgressScreen;

    /**
     * Gets the database helper
     *
     * @return the database helper
     */
    public static DataBaseHelper getDbHelper() {
        return dbHelper;
    }

    /**
     * Shows initial dialog after application upgrade.
     *
     * @param context Application context
     */
    private void showUpgradeDialog(Context context) {
        AlertDialog alertDialog = DialogFactory.createWebViewDialog(context,
                R.string.changelogTitle,
                R.raw.changes);

        alertDialog.show();
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onCreate()
     */
    @Override
    public void onCreate(Bundle icicle) {
        int lastVersion;
        int currentVersion;

        //Initialize screen
        super.onCreate(icicle);

        setContentView(R.layout.main);
        initializeMainViews();

        try {
            //Check if this is the first run after an install or upgrade
            lastVersion = Preferences.getLastVersion();
            currentVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
            dbHelper.initializeDB();
            //lastVersion = 67;
            //currentVersion = 68;

            //If this is the first run, show configuration dialog
            if (lastVersion == 0) {
                firstRun(currentVersion);

                //If this is an upgrade, show upgrade dialog
            } else if (lastVersion < currentVersion) {
                //showUpgradeDialog(this);
                upgradeApp(lastVersion, currentVersion);
            }

            loadCourses();

            currentRole = -1;
        } catch (Exception ex) {
            error(ex.getMessage(), ex);
        }
    }

    /*
         * (non-Javadoc)
         * @see android.app.Activity#onResume()
         */
    @Override
    protected void onResume() {
        super.onResume();

        if (isUserOrPasswordEmpty() && listCourses.isEmpty()) {
            startActivityForResult(new Intent(this, LoginActivity.class), Constants.LOGIN_REQUEST_CODE);
        } else {
            if(Preferences.isPreferencesChanged() || DataBaseHelper.isDbCleaned()) {
                DataBaseHelper.setDbCleaned(false);

                Preferences.setPreferencesChanged(false);
                Preferences.initializeSelectedCourse();

                listCourses.clear();
                courseCode = -1;
            } else if(!firstRun) {
                courseCode = Courses.getSelectedCourseCode();
            }

            //If today is the user birthday, show birthday message
            if((Login.getLoggedUser() != null) && (Login.getLoggedUser().getUserBirthday() != null)
                    && DateTimeUtils.isBirthday(Login.getLoggedUser().getUserBirthday())) {

                showBirthdayMessage();
            } else {
                mNotifyLayout.setVisibility(View.GONE);
            }

            createSpinnerAdapter();
            createMenu();
        }
    }

    private void showBirthdayMessage() {
        mNotifyImageView.setImageResource(R.drawable.fabirthdaycake);
        mNotifyImageView.setVisibility(View.VISIBLE);
        mNotifyTextView.setText(getString(R.string.birthdayMsg).replace(
                Constants.USERNAME_TEMPLATE, Login.getLoggedUser().getUserFirstname()));
        mNotifyLayout.setVisibility(View.VISIBLE);
    }

    /**
     * Initializes application on first run
     * @param currentVersion Current version of application
     */
    private void firstRun(int currentVersion) {
        Log.i(TAG, "Running application for first time. Setting current version to " + currentVersion);

        Intent activity = new Intent(this, AccountAuthenticator.class);

        //Configure automatic synchronization
        Preferences.setSyncTime(String.valueOf(Constants.DEFAULT_SYNC_TIME));
        startActivity(activity);

        Preferences.setLastVersion(currentVersion);
        firstRun = true;

        Preferences.initializeSelectedCourse();

        Log.i(TAG, "First initialization completed successfully");
    }

    /**
     * Makes appropriate changes on application upgrade
     * @param lastVersion Version from which the application is updated
     * @param currentVersion Version to which the application is updated
     */
    private void upgradeApp(int lastVersion, int currentVersion) throws NoSuchAlgorithmException {
        Log.i(TAG, "Upgrading application from version " + lastVersion + " to version " + currentVersion);

        dbHelper.upgradeDB();

        if(lastVersion < 52) {
            //Encrypts users table
            dbHelper.encryptUsers();

            //If the app is updating from an unencrypted user password version, encrypt user password
            Preferences.upgradeCredentials();

            Preferences.setSyncTime(String.valueOf(Constants.DEFAULT_SYNC_TIME));
        }

        if(lastVersion < 57) {
            //Reconfigure automatic synchronization
            SyncUtils.removePeriodicSync(Constants.AUTHORITY, Bundle.EMPTY, this);
            if(!Preferences.getSyncTime().equals("0") && Preferences.isSyncEnabled()) {
                SyncUtils.addPeriodicSync(Constants.AUTHORITY, Bundle.EMPTY,
                        Long.parseLong(Preferences.getSyncTime()), this);
            }
        }

        Preferences.setLastVersion(currentVersion);

        Log.i(TAG, "Application upgraded from version " + lastVersion + " to version " + currentVersion);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                //After get the list of courses, a spinner is showed to choice the course
                case Constants.COURSES_REQUEST_CODE:
                    loadCourses();

                    //User credentials are correct. Set periodic synchronization if enabled
                    if (!"0".equals(Preferences.getSyncTime())
                            && Preferences.isSyncEnabled() && SyncUtils.isPeriodicSynced(this)) {
                        SyncUtils.addPeriodicSync(Constants.AUTHORITY, Bundle.EMPTY,
                                Long.parseLong(Preferences.getSyncTime()), this);
                    }

                    mProgressScreen.hide();

                    createSpinnerAdapter();
                    createMenu();

                    break;
                case Constants.LOGIN_REQUEST_CODE:
                    getCurrentCourses();
                    break;
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            switch (requestCode) {
                //After get the list of courses, a dialog is launched to choice the course
                case Constants.COURSES_REQUEST_CODE:
                    //User credentials are wrong. Remove periodic synchronization
                    SyncUtils.removePeriodicSync(Constants.AUTHORITY, Bundle.EMPTY, this);
                    mProgressScreen.hide();
                    break;
                case Constants.LOGIN_REQUEST_CODE:
                    finish();
                    break;
            }
        }
    }

    private void createSpinnerAdapter() {
        listCourses = dbHelper.getAllRows(DataBaseHelper.DB_TABLE_COURSES, null, "shortName");
        /*
          Cursor for database access
         */
        Cursor dbCursor = dbHelper.getDb().getCursor(DataBaseHelper.DB_TABLE_COURSES, null, "shortName");
        startManagingCursor(dbCursor);
        if (!listCourses.isEmpty()) {
            SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                    android.R.layout.simple_spinner_item,
                    dbCursor,
                    new String[]{"shortName"},
                    new int[]{android.R.id.text1});

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mCoursesSpinner.setAdapter(adapter);
            mCoursesSpinner.setOnItemSelectedListener(new onItemSelectedListener());

            if (Preferences.getLastCourseSelected() < listCourses.size())
                mCoursesSpinner.setSelection(Preferences.getLastCourseSelected());
            else
                mCoursesSpinner.setSelection(0);

            mCoursesSpinner.setOnTouchListener(spinnerOnTouch);
            mCoursesSpinner.setVisibility(View.VISIBLE);

            Log.i(TAG, "Created Spinner adapter");
        } else {
            cleanSpinner();
        }
    }

    /**
     * Create an empty spinner. It is called when the database is cleaned.
     */
    private void cleanSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{getString(R.string.clickToGetCourses)});
        mCoursesSpinner.setAdapter(adapter);
        mCoursesSpinner.setOnTouchListener(spinnerOnTouch);

        Log.i(TAG, "Cleaned Spinner adapter");
    }

    private class onItemSelectedListener implements OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position,
                                   long id) {
            if (!listCourses.isEmpty()) {
                Preferences.setLastCourseSelected(position);

                Course courseSelected = (Course) listCourses.get(position);

                courseCode = courseSelected.getId();

                Courses.setSelectedCourseCode(courseCode);
                Courses.setSelectedCourseShortName(courseSelected.getShortName());
                Courses.setSelectedCourseFullName(courseSelected.getFullName());

                Login.setCurrentUserRole(courseSelected.getUserRole());

                createMenu();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {

        }
    }

    private final View.OnTouchListener spinnerOnTouch = (v, event) -> {
        if (event.getAction() == MotionEvent.ACTION_UP) {

            if (dbHelper.getAllRows(DataBaseHelper.DB_TABLE_COURSES).isEmpty()) {
                if (Utils.connectionAvailable(getApplicationContext()))
                    getCurrentCourses();
            } else {
                v.performClick();
            }
        }
        return true;
    };

    private void getCurrentCourses() {
        Log.i(TAG, "Downloading courses...");

        mProgressScreen.show();

        Intent activity = new Intent(this, Courses.class);
        startActivityForResult(activity, Constants.COURSES_REQUEST_CODE);
    }

    private void createMenu() {
        if (!listCourses.isEmpty()) {
            Course courseSelected;

            if (Courses.getSelectedCourseCode() != -1) {
                courseSelected = dbHelper.getRow(DataBaseHelper.DB_TABLE_COURSES, "id", String.valueOf(Courses.getSelectedCourseCode()));

                Log.i(TAG + " createMenu", "Recovered selected course " + courseSelected + " from database");
            } else {
                Log.w(TAG + " createMenu", "There is no selected course");

                int lastSelected = Preferences.getLastCourseSelected();

                if (lastSelected != -1 && lastSelected < listCourses.size()) {
                    courseSelected = (Course) listCourses.get(lastSelected);
                    Preferences.setLastCourseSelected(lastSelected);

                    Log.i(TAG + " createMenu", "Setted last course selected to " + courseSelected);
                } else {
                    courseSelected = (Course) listCourses.get(0);
                    Preferences.setLastCourseSelected(0);

                    Log.w(TAG + " createMenu", "No last course selected. Initialized last course selected to " + courseSelected);
                }

                Courses.setSelectedCourseCode(courseSelected.getId());
                Courses.setSelectedCourseShortName(courseSelected.getShortName());
                Courses.setSelectedCourseFullName(courseSelected.getFullName());
            }

            createBaseMenu();

            int userRole = courseSelected.getUserRole();

            switch(userRole) {
                case Constants.STUDENT_TYPE_CODE:
                    changeToStudentMenu();
                    break;
                case Constants.TEACHER_TYPE_CODE:
                    changeToTeacherMenu();
                    break;
            }

            Login.setCurrentUserRole(userRole);
        } else {
            Preferences.initializeSelectedCourse();

            Log.w(TAG + " createMenu", "No courses available. Resetted selected course and current role to -1");

            createNoCourseMenu();
        }

        refreshMainMenu();
    }

    /**
     * Creates menu when there are no courses available. This menu is common for students and teachers.
     */
    private void createNoCourseMenu() {
        if ((listCourses == null) || listCourses.isEmpty()) {
            clearMenu();

            //Order:
            // 1- Messages
            // 2- Users
            mHeaderData.add(getMenuItem(R.string.users, R.string.fa_users));
            mHeaderData.add(getMenuItem(R.string.messages, R.string.fa_envelope));

            mChildData.add(mUsersData);
            mChildData.add(mMessagesData);

            //Users category
            //Generate QR code
            mUsersData.add(getMenuItem(R.string.generateQRModuleLabel, R.string.fa_qrcode));

            //Messages category
            //Notifications
            mMessagesData.add(getMenuItem(R.string.notificationsModuleLabel, R.string.fa_bell));
            //Messages
            mMessagesData.add(getMenuItem(R.string.messagesModuleLabel, R.string.fa_pencil_square_o));

            Log.i(TAG, "Created No Course Menu");
        }
    }

    /**
     * Creates base menu. The menu base is common for students and teachers.
     * Sets currentRole to student role
     */
    private void createBaseMenu() {
        clearMenu();

        //the menu base is equal to students menu.
        currentRole = Constants.STUDENT_TYPE_CODE;

        //Order:
        // 1- Course
        // 2- Evaluation
        // 3- Files
        // 4- Users
        // 5- Messages
        mHeaderData.add(getMenuItem(R.string.course, R.string.fa_list_ol));
        mHeaderData.add(getMenuItem(R.string.evaluation, R.string.fa_check_square_o));
        mHeaderData.add(getMenuItem(R.string.files, R.string.fa_folder_open));
        mHeaderData.add(getMenuItem(R.string.users, R.string.fa_users));
        mHeaderData.add(getMenuItem(R.string.messages, R.string.fa_envelope));

        final ArrayList<HashMap<String, Object>> courseData = new ArrayList<>();
        mChildData.add(courseData);

        final ArrayList<HashMap<String, Object>> evaluationData = new ArrayList<>();
        mChildData.add(evaluationData);

        final ArrayList<HashMap<String, Object>> filesData = new ArrayList<>();
        mChildData.add(filesData);

        mChildData.add(mUsersData);
        mChildData.add(mMessagesData);

        //Course category
        //Introduction
        courseData.add(getMenuItem(R.string.introductionModuleLabel, R.string.fa_info));
        //Teaching Guide
        courseData.add(getMenuItem(R.string.teachingguideModuleLabel, R.string.fa_file_text));
        //Syllabus (lectures)
        courseData.add(getMenuItem(R.string.syllabusLecturesModuleLabel, R.string.fa_list_ol));
        //Syllabus (practicals)
        courseData.add(getMenuItem(R.string.syllabusPracticalsModuleLabel, R.string.fa_flask));
        //Bibliography
        courseData.add(getMenuItem(R.string.bibliographyModuleLabel, R.string.fa_book));
        //FAQs
        courseData.add(getMenuItem(R.string.faqsModuleLabel, R.string.fa_question));
        //Links
        courseData.add(getMenuItem(R.string.linksModuleLabel, R.string.fa_link));

        //Evaluation category
        //Assessment system
        evaluationData.add(getMenuItem(R.string.assessmentModuleLabel, R.string.fa_info));
        //Test
        evaluationData.add(getMenuItem(R.string.testsModuleLabel, R.string.fa_check_square_o));
        //Games
        evaluationData.add(getMenuItem(R.string.gamesModuleLabel, R.string.fa_trophy));

        //Files category
        //Documents
        filesData.add(getMenuItem(R.string.documentsDownloadModuleLabel, R.string.fa_folder_open));
        //Shared area
        filesData.add(getMenuItem(R.string.sharedsDownloadModuleLabel, R.string.fa_folder_open));
        //Marks
        filesData.add(getMenuItem(R.string.marksModuleLabel, R.string.fa_list_alt));

        //Users category
        //Groups
        mUsersData.add(getMenuItem(R.string.myGroupsModuleLabel, R.string.fa_sitemap));
        //Generate QR code
        mUsersData.add(getMenuItem(R.string.generateQRModuleLabel, R.string.fa_qrcode));
        //Manage location
        mUsersData.add(getMenuItem(R.string.manageLocation, R.string.fa_map_marker));

        //Messages category
        //Notifications
        mMessagesData.add(getMenuItem(R.string.notificationsModuleLabel, R.string.fa_bell));
        //Messages
        mMessagesData.add(getMenuItem(R.string.messagesModuleLabel, R.string.fa_pencil_square_o));

        Log.i(TAG, "Created Base Menu");
    }

    /**
     * Adapts the current menu to students view. Removes options unique to teachers and adds options unique to students
     */
    private void changeToStudentMenu() {
        if (currentRole != Constants.STUDENT_TYPE_CODE) {
            //Removes Publish Note from messages menu
            mExpandableListAdapter.removeChild(Constants.MESSAGES_GROUP, Constants.PUBLISH_NOTE_CHILD);
            //Removes Rollcall from users menu
            mExpandableListAdapter.removeChild(Constants.USERS_GROUP, Constants.ROLLCALL_CHILD);

            currentRole = Constants.STUDENT_TYPE_CODE;

            Log.i(TAG, "Changed to Student Menu");
        }
    }

    /**
     * Adapts the current menu to teachers view. Removes options unique to students and adds options unique to teachers
     */
    private void changeToTeacherMenu() {
        if (currentRole != Constants.TEACHER_TYPE_CODE) {
            //Adds Publish Note to messages menu
            mMessagesData.add(getMenuItem(R.string.noticesModuleLabel, R.string.fa_bullhorn));
            //Adds Rollcall to users menu
            mUsersData.add(getMenuItem(R.string.rollcallModuleLabel, R.string.fa_check_square_o));

            currentRole = Constants.TEACHER_TYPE_CODE;

            Log.i(TAG, "Changed to Teacher Menu");
        }
    }

    /**
     * Launches an action when refresh button is pushed
     *
     * @param v Actual view
     */
    public void onRefreshClick(View v) {
        getCurrentCourses();
    }

    /**
     * @return true if user or password preference is empty
     */
    private boolean isUserOrPasswordEmpty() {
        return TextUtils.isEmpty(Preferences.getUserID())
                || TextUtils.isEmpty(Preferences.getUserPassword());
    }

    private void initializeMainViews() {
        mExpandableListView = (ExpandableListView) findViewById(R.id.expandableList);
        mNotifyLayout = (LinearLayout) findViewById(R.id.notify_layout);
        mNotifyTextView = (TextView) findViewById(R.id.notifyTextView);
        mNotifyImageView = (ImageView) findViewById(R.id.notifyImageView);
        View mProgressScreenView = findViewById(R.id.progress_screen);
        View mCoursesListView = findViewById(R.id.courses_list_view);
        mCoursesSpinner = (Spinner) this.findViewById(R.id.spinner);
        mProgressScreen = new ProgressScreen(mProgressScreenView, mCoursesListView,
                getString(R.string.coursesProgressDescription), this);

        OnChildClickListener mExpandableClickListener = new OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                        int childPosition, long id) {
                // Get the item that was clicked
                Object o = parent.getExpandableListAdapter().getChild(groupPosition, childPosition);
                @SuppressWarnings("unchecked")
                String keyword = (String) ((Map<String, Object>) o).get(NAME);
                Intent activity;
                Context ctx = getApplicationContext();

                if (keyword.equals(getString(R.string.notificationsModuleLabel))) {
                    activity = new Intent(ctx, Notifications.class);
                    startActivityForResult(activity, Constants.NOTIFICATIONS_REQUEST_CODE);
                } else if (keyword.equals(getString(R.string.testsModuleLabel))) {
                    activity = new Intent(ctx, Tests.class);
                    startActivityForResult(activity, Constants.TESTS_REQUEST_CODE);

                } else if (keyword.equals(getString(R.string.gamesModuleLabel))) {
                    activity = new Intent(ctx, GamesList.class);
                    startActivityForResult(activity, Constants.GAMES_REQUEST_CODE);

                } else if (keyword.equals(getString(R.string.messagesModuleLabel))) {
                    activity = new Intent(ctx, Messages.class);
                    activity.putExtra("eventCode", Long.valueOf(0));
                    startActivityForResult(activity, Constants.MESSAGES_REQUEST_CODE);
                } else if (keyword.equals(getString(R.string.noticesModuleLabel))) {
                    activity = new Intent(ctx, Notices.class);
                    startActivityForResult(activity, Constants.NOTICES_REQUEST_CODE);
                } else if (keyword.equals(getString(R.string.rollcallModuleLabel))) {
                    if (Utils.connectionAvailable(ctx)) {
                        activity = new Intent(ctx, Rollcall.class);
                        startActivityForResult(activity, Constants.ROLLCALL_REQUEST_CODE);
                    } else {
                        Toast.makeText(ctx, R.string.noConnectionMsg, Toast.LENGTH_LONG).show();
                    }
                } else if (keyword.equals(getString(R.string.generateQRModuleLabel))) {
                    activity = new Intent(ctx, GenerateQR.class);
                    startActivityForResult(activity, Constants.GENERATE_QR_REQUEST_CODE);
                } else if (keyword.equals(getString(R.string.manageLocation))) {
                    activity = new Intent(ctx, IndoorLocation.class);
                    startActivityForResult(activity, Constants.MANAGE_LOCATION);
                } else if (keyword.equals(getString(R.string.documentsDownloadModuleLabel))) {
                    activity = new Intent(ctx, DownloadsManager.class);
                    activity.putExtra("downloadsAreaCode", Constants.DOCUMENTS_AREA_CODE);
                    startActivityForResult(activity, Constants.DOWNLOADSMANAGER_REQUEST_CODE);
                } else if (keyword.equals(getString(R.string.sharedsDownloadModuleLabel))) {
                    activity = new Intent(ctx, DownloadsManager.class);
                    activity.putExtra("downloadsAreaCode", Constants.SHARE_AREA_CODE);
                    startActivityForResult(activity, Constants.DOWNLOADSMANAGER_REQUEST_CODE);
                } else if (keyword.equals(getString(R.string.marksModuleLabel))) {
                    activity = new Intent(ctx, DownloadsManager.class);
                    activity.putExtra("downloadsAreaCode", Constants.MARKS_AREA_CODE);
                    startActivityForResult(activity, Constants.DOWNLOADSMANAGER_REQUEST_CODE);
                } else if (keyword.equals(getString(R.string.myGroupsModuleLabel))) {
                    activity = new Intent(ctx, MyGroupsManager.class);
                    activity.putExtra("courseCode", Courses.getSelectedCourseCode());
                    startActivityForResult(activity, Constants.MYGROUPSMANAGER_REQUEST_CODE);
                } else if (keyword.equals(getString(R.string.introductionModuleLabel))) {
                    activity = new Intent(ctx, Information.class);
                    activity.putExtra("requestCode", Constants.INTRODUCTION_REQUEST_CODE);
                    startActivityForResult(activity, Constants.INTRODUCTION_REQUEST_CODE);
                } else if (keyword.equals(getString(R.string.faqsModuleLabel))) {
                    activity = new Intent(ctx, Information.class);
                    activity.putExtra("requestCode", Constants.FAQS_REQUEST_CODE);
                    startActivityForResult(activity, Constants.FAQS_REQUEST_CODE);
                } else if (keyword.equals(getString(R.string.bibliographyModuleLabel))) {
                    activity = new Intent(ctx, Information.class);
                    activity.putExtra("requestCode", Constants.BIBLIOGRAPHY_REQUEST_CODE);
                    startActivityForResult(activity, Constants.BIBLIOGRAPHY_REQUEST_CODE);
                } else if (keyword.equals(getString(R.string.syllabusPracticalsModuleLabel))) {
                    activity = new Intent(ctx, Information.class);
                    activity.putExtra("requestCode", Constants.SYLLABUSPRACTICALS_REQUEST_CODE);
                    startActivityForResult(activity, Constants.SYLLABUSPRACTICALS_REQUEST_CODE);
                } else if (keyword.equals(getString(R.string.syllabusLecturesModuleLabel))) {
                    activity = new Intent(ctx, Information.class);
                    activity.putExtra("requestCode", Constants.SYLLABUSLECTURES_REQUEST_CODE);
                    startActivityForResult(activity, Constants.SYLLABUSLECTURES_REQUEST_CODE);
                } else if (keyword.equals(getString(R.string.linksModuleLabel))) {
                    activity = new Intent(ctx, Information.class);
                    activity.putExtra("requestCode", Constants.LINKS_REQUEST_CODE);
                    startActivityForResult(activity, Constants.LINKS_REQUEST_CODE);
                } else if (keyword.equals(getString(R.string.teachingguideModuleLabel))) {
                    activity = new Intent(ctx, Information.class);
                    activity.putExtra("requestCode", Constants.TEACHINGGUIDE_REQUEST_CODE);
                    startActivityForResult(activity, Constants.TEACHINGGUIDE_REQUEST_CODE);
                } else if (keyword.equals(getString(R.string.assessmentModuleLabel))) {
                    activity = new Intent(ctx, Information.class);
                    activity.putExtra("requestCode", Constants.ASSESSMENT_REQUEST_CODE);
                    startActivityForResult(activity, Constants.ASSESSMENT_REQUEST_CODE);
                }

                return true;
            }
        };

        mExpandableListView.setOnChildClickListener(mExpandableClickListener);
    }

    private void refreshMainMenu() {
        mExpandableListAdapter = new TextExpandableListAdapter(this, mHeaderData,
                R.layout.image_list_item,
                new String[] {
                        NAME
                }, new int[] {
                R.id.listText
        },
                mChildData, 0,
                null, new int[] {}
        );

        mExpandableListView.setAdapter(mExpandableListAdapter);
        mExpandableListView.setVisibility(View.VISIBLE);

        Log.i(TAG, "Refreshed Main Menu");
    }

    private HashMap<String, Object> getMenuItem(int resName, int resImage) {
        final HashMap<String, Object> menuItem = new HashMap<>();

        menuItem.put(NAME, getString(resName));
        menuItem.put(IMAGE, getString(resImage));

        return menuItem;
    }

    private void clearMenu() {
        mNotifyLayout.setVisibility(View.GONE);

        mHeaderData.clear();
        mChildData.clear();
        mUsersData.clear();
        mMessagesData.clear();

        Log.i(TAG, "Cleared Main Menu");
    }

    private void loadCourses() {
        listCourses = dbHelper.getAllRows(DataBaseHelper.DB_TABLE_COURSES, "", "shortName");
        if (!listCourses.isEmpty()) {
            Course c = (Course) listCourses.get(0);

            Courses.setSelectedCourseCode(c.getId());
            Courses.setSelectedCourseShortName(c.getShortName());
            Courses.setSelectedCourseFullName(c.getFullName());

            Login.setCurrentUserRole(c.getUserRole());
        } else {
            Preferences.initializeSelectedCourse();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                getCurrentCourses();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

}
