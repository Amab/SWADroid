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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
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
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.bugsense.trace.BugSenseHandler;

import es.ugr.swad.swadroid.gui.DialogFactory;
import es.ugr.swad.swadroid.gui.ImageExpandableListAdapter;
import es.ugr.swad.swadroid.gui.MenuExpandableListActivity;
import es.ugr.swad.swadroid.model.Course;
import es.ugr.swad.swadroid.model.DataBaseHelper;
import es.ugr.swad.swadroid.model.Model;
import es.ugr.swad.swadroid.modules.Courses;
import es.ugr.swad.swadroid.modules.GenerateQR;
import es.ugr.swad.swadroid.modules.Messages;
import es.ugr.swad.swadroid.modules.Notices;
import es.ugr.swad.swadroid.modules.downloads.DownloadsManager;
import es.ugr.swad.swadroid.modules.groups.MyGroupsManager;
import es.ugr.swad.swadroid.modules.information.Information;
import es.ugr.swad.swadroid.modules.notifications.Notifications;
import es.ugr.swad.swadroid.modules.rollcall.Rollcall;
import es.ugr.swad.swadroid.modules.tests.Tests;
import es.ugr.swad.swadroid.ssl.SecureConnection;
import es.ugr.swad.swadroid.sync.AccountAuthenticator;
import es.ugr.swad.swadroid.sync.SyncUtils;
import es.ugr.swad.swadroid.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Main class of the application.
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 * @author Antonio Aguilera Malagon <aguilerin@gmail.com>
 * @author Helena Rodriguez Gijon <hrgijon@gmail.com>
 * @author Jose Antonio Guerrero Aviles <cany20@gmail.com>
 */
public class SWADMain extends MenuExpandableListActivity {
	/**
	 * Application preferences
	 */
	Preferences prefs;
	/**
	 * SSL connection
	 */
	SecureConnection conn;
    /**
     * Array of strings for main ListView
     */
    protected String[] functions;
    /**
     * Function name field
     */
    private final String NAME = "listText";
    /**
     * Function text field
     */
    private final String IMAGE = "listIcon";
    /**
     * Code of selected course
     */
    private long courseCode;
    /**
     * Cursor for database access
     */
    private Cursor dbCursor;
    /**
     * User courses list
     */
    private List<Model> listCourses;
    /**
     * Tests tag name for Logcat
     */
    public static final String TAG = Constants.APP_TAG;
    
    /**
     * Indicates if it is the first run
     */
    private boolean firstRun = false;

    /**
     * Current role 2 - student 3 - teacher -1 - none role was chosen
     */
    private int currentRole = -1;

    private boolean dBCleaned = false;

    private ExpandableListView mExpandableListView;
    private ImageExpandableListAdapter mExpandableListAdapter;
    private OnChildClickListener mExpandableClickListener;
    private final ArrayList<HashMap<String, Object>> mHeaderData = new ArrayList<HashMap<String, Object>>();
    private final ArrayList<ArrayList<HashMap<String, Object>>> mChildData = new ArrayList<ArrayList<HashMap<String, Object>>>();
    private final ArrayList<HashMap<String, Object>> mMessagesData = new ArrayList<HashMap<String, Object>>();
    private final ArrayList<HashMap<String, Object>> mUsersData = new ArrayList<HashMap<String, Object>>();
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
     */
    public void showUpgradeDialog(Context context) {        
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
        int lastVersion, currentVersion;
        Intent activity;
        
        //Initialize Bugsense plugin
        try {
        	BugSenseHandler.initAndStartSession(this, Constants.BUGSENSE_API_KEY);
        } catch (Exception e) {
        	Log.e(TAG, "Error initializing BugSense", e);
        }
        
        //Initialize screen
        super.onCreate(icicle);

        setContentView(R.layout.main);
        initializeMainViews();
        
        
        //Initialize preferences
        prefs = new Preferences(this);
        
        try {
        	
            //Initialize HTTPS connections
        	/*
        	 * Terena root certificate is not included by default on Gingerbread and older
        	 * If Android API < 11 (HONEYCOMB) add Terena certificate manually
        	 */
        	if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
        		conn = new SecureConnection();
        		conn.initSecureConnection(this);        		
        		Log.i(TAG, "Android API < 11 (HONEYCOMB). Adding Terena certificate manually");
        	} else {
        		Log.i(TAG, "Android API >= 11 (HONEYCOMB). Using Terena built-in certificate");
        	}

            //Check if this is the first run after an install or upgrade
            lastVersion = Preferences.getLastVersion();
            currentVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
            dbHelper.initializeDB();
            //lastVersion = 57;
            //currentVersion = 58;

            //If this is the first run, show configuration dialog
            if (lastVersion == 0) {
                //Configure automatic synchronization
                Preferences.setSyncTime(String.valueOf(Constants.DEFAULT_SYNC_TIME));
                activity = new Intent(this, AccountAuthenticator.class);
                startActivity(activity);

                Preferences.setLastVersion(currentVersion);
                firstRun = true;
                Constants.setSelectedCourseCode(-1);

                Constants.setSelectedCourseShortName("");
                Constants.setSelectedCourseFullName("");

                Constants.setCurrentUserRole(-1);

            //If this is an upgrade, show upgrade dialog
            } else if (lastVersion < currentVersion) {
                showUpgradeDialog(this);
                dbHelper.upgradeDB(this);
                
                if(lastVersion < 52) {
                	//Encrypts users table
                	dbHelper.encryptUsers();
                	
                	//If the app is updating from an unencrypted user password version, encrypt user password
                	Preferences.upgradeCredentials();
                	
                	Preferences.setSyncTime(String.valueOf(Constants.DEFAULT_SYNC_TIME));
                } else if(lastVersion < 57) {
                	//Reconfigure automatic synchronization
                	SyncUtils.removePeriodicSync(Constants.AUTHORITY, Bundle.EMPTY, this);                	
                	if(!Preferences.getSyncTime().equals("0") && Preferences.isSyncEnabled()) {
                		SyncUtils.addPeriodicSync(Constants.AUTHORITY, Bundle.EMPTY,
                				Long.valueOf(Preferences.getSyncTime()), this);
                	}
                }

                Preferences.setLastVersion(currentVersion);
            }

            listCourses = dbHelper.getAllRows(Constants.DB_TABLE_COURSES, "", "shortName");
            if (listCourses.size() > 0) {
                Course c = (Course) listCourses.get(Preferences.getLastCourseSelected());
                Constants.setSelectedCourseCode(c.getId());
                Constants.setSelectedCourseShortName(c.getShortName());
                Constants.setSelectedCourseFullName(c.getFullName());
                Constants.setCurrentUserRole(c.getUserRole()); 
            } else {
                Constants.setSelectedCourseCode(-1);
                Constants.setSelectedCourseShortName("");
                Constants.setSelectedCourseFullName("");
                Constants.setCurrentUserRole(-1);
            }
            currentRole = -1;
        } catch (Exception ex) {
            error(TAG, ex.getMessage(), ex, true);
        }
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();

        if (isUserOrPasswordEmpty() && (listCourses.size() == 0)) {
            startActivityForResult(new Intent(this, LoginActivity.class), Constants.LOGIN_REQUEST_CODE);
        } else {

            if (!Preferences.isPreferencesChanged() && !Utils.isDbCleaned()) {
                createSpinnerAdapter();
                if (!firstRun) {
                    courseCode = Constants.getSelectedCourseCode();
                    createMenu();
                }
            } else {
                Preferences.setPreferencesChanged(false);
                Utils.setDbCleaned(false);
                setMenuDbClean();
            }
        }
    }

    /* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		try {
			BugSenseHandler.closeSession(this);
		} catch (Exception e) {
			Log.e(TAG, "Error initializing BugSense", e);
        }
		
		super.onDestroy();
	}

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                //After get the list of courses, a dialog is launched to choice the course
                case Constants.COURSES_REQUEST_CODE:

                    setMenuDbClean();
                    createSpinnerAdapter();
                    createMenu();

                    //User credentials are correct. Set periodic synchronization if enabled
        	        if (!Preferences.getSyncTime().equals("0")
        	        		&& Preferences.isSyncEnabled() && SyncUtils.isPeriodicSynced(this)) {
        	            SyncUtils.addPeriodicSync(Constants.AUTHORITY, Bundle.EMPTY,
        	            		Long.parseLong(Preferences.getSyncTime()), this);
        	        }
        	        
        	        showProgress(false);
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
                    showProgress(false);
                    break;
                case Constants.LOGIN_REQUEST_CODE:
                    finish();
                    break;
            }
        }
    }

    private void createSpinnerAdapter() {
        Spinner spinner = (Spinner) this.findViewById(R.id.spinner);
        listCourses = dbHelper.getAllRows(Constants.DB_TABLE_COURSES, null, "fullName");
        dbCursor = dbHelper.getDb().getCursor(Constants.DB_TABLE_COURSES, null, "fullName");
        startManagingCursor(dbCursor);
        if (listCourses.size() != 0) {
            SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                    android.R.layout.simple_spinner_item,
                    dbCursor,
                    new String[]{"fullName"},
                    new int[]{android.R.id.text1});

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(new onItemSelectedListener());
            if (Preferences.getLastCourseSelected() < listCourses.size())
                spinner.setSelection(Preferences.getLastCourseSelected());
            else
                spinner.setSelection(0);
            	spinner.setOnTouchListener(Spinner_OnTouch);
        } else {
            cleanSpinner();
        }

    }

    /**
     * Create an empty spinner. It is called when the database is cleaned.
     */
    private void cleanSpinner() {
        Spinner spinner = (Spinner) this.findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new String[]{getString(R.string.clickToGetCourses)});
        spinner.setAdapter(adapter);
        spinner.setOnTouchListener(Spinner_OnTouch);
    }

    private class onItemSelectedListener implements OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position,
                                   long id) {
            if (!listCourses.isEmpty()) {
            	Preferences.setLastCourseSelected(position);
                Course courseSelected = (Course) listCourses.get(position);
                courseCode = courseSelected.getId();
                Constants.setSelectedCourseCode(courseCode);
                Constants.setSelectedCourseShortName(courseSelected.getShortName());
                Constants.setSelectedCourseFullName(courseSelected.getFullName());
                Constants.setCurrentUserRole(courseSelected.getUserRole());
                createMenu();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {

        }
    }

    private final View.OnTouchListener Spinner_OnTouch = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {

                if (dbHelper.getAllRows(Constants.DB_TABLE_COURSES).size() == 0) {
                    if (Utils.connectionAvailable(getApplicationContext()))
                        getCurrentCourses();
                    //else

                } else {
                    v.performClick();
                }
            }
            return true;
        }
    };

    private void getCurrentCourses() {
    	
    	showProgress(true);
    	
        Intent activity;
        activity = new Intent(this, Courses.class);
        startActivityForResult(activity, Constants.COURSES_REQUEST_CODE);
    }

    private void createMenu() {
        if (listCourses.size() != 0) {
            Course courseSelected;
            if (Constants.getSelectedCourseCode() != -1) {
                courseSelected = (Course) dbHelper.getRow(Constants.DB_TABLE_COURSES, "id", String.valueOf(Constants.getSelectedCourseCode()));
            } else {
                int lastSelected = Preferences.getLastCourseSelected();
                if (lastSelected != -1 && lastSelected < listCourses.size()) {
                    courseSelected = (Course) listCourses.get(lastSelected);
                    Constants.setSelectedCourseCode(courseSelected.getId());
                    Constants.setSelectedCourseShortName(courseSelected.getShortName());
                    Constants.setSelectedCourseFullName(courseSelected.getFullName());
                    Constants.setCurrentUserRole(courseSelected.getUserRole());
                    Preferences.setLastCourseSelected(lastSelected);
                } else {
                    courseSelected = (Course) listCourses.get(0);
                    Constants.setSelectedCourseCode(courseSelected.getId());
                    Constants.setSelectedCourseShortName(courseSelected.getShortName());
                    Constants.setSelectedCourseFullName(courseSelected.getFullName());
                    Constants.setCurrentUserRole(courseSelected.getUserRole());
                    Preferences.setLastCourseSelected(0);
                }
            }

            if (courseSelected != null) {
                if ((mExpandableListView.getAdapter() == null) || dBCleaned) {
                    createBaseMenu();
                }
                int userRole = courseSelected.getUserRole();
                if ((userRole == Constants.TEACHER_TYPE_CODE) && (currentRole != Constants.TEACHER_TYPE_CODE))
                    changeToTeacherMenu();
                if ((userRole == Constants.STUDENT_TYPE_CODE) && (currentRole != Constants.STUDENT_TYPE_CODE))
                    changeToStudentMenu();
                dBCleaned = false;
            }
        }
    }

    /**
     * Creates base menu. The menu base is common for students and teachers.
     * Sets currentRole to student role
     */
    private void createBaseMenu() {
        mExpandableListView.setVisibility(View.VISIBLE);
        if (mExpandableListView.getAdapter() == null || currentRole == -1) {
            //the menu base is equal to students menu.
            currentRole = Constants.STUDENT_TYPE_CODE;

            //Order:
            // 1- Course
            // 2- Evaluation
            // 3- Messages
            // 4- Enrollment
            // 5- Users
            final HashMap<String, Object> courses = new HashMap<String, Object>();
            courses.put(NAME, getString(R.string.course));
            courses.put(IMAGE, getResources().getDrawable(R.drawable.crs));
            mHeaderData.add(courses);

            final HashMap<String, Object> evaluation = new HashMap<String, Object>();
            evaluation.put(NAME, getString(R.string.evaluation));
            evaluation.put(IMAGE, getResources().getDrawable(R.drawable.grades));
            mHeaderData.add(evaluation);

            final HashMap<String, Object> users = new HashMap<String, Object>();
            users.put(NAME, getString(R.string.users));
            users.put(IMAGE, getResources().getDrawable(R.drawable.users));
            mHeaderData.add(users);

            final HashMap<String, Object> messages = new HashMap<String, Object>();
            messages.put(NAME, getString(R.string.messages));
            messages.put(IMAGE, getResources().getDrawable(R.drawable.msg));
            mHeaderData.add(messages);

            final ArrayList<HashMap<String, Object>> courseData = new ArrayList<HashMap<String, Object>>();
            mChildData.add(courseData);
            
            final ArrayList<HashMap<String, Object>> evaluationData = new ArrayList<HashMap<String, Object>>();
            mChildData.add(evaluationData);

            mChildData.add(mUsersData);
            mChildData.add(mMessagesData);

            HashMap<String, Object> map;
            
            //Course category
            //Introduction
            map = new HashMap<String, Object>();
            map.put(NAME, getString(R.string.introductionModuleLabel));
            map.put(IMAGE, getResources().getDrawable(R.drawable.info));
            courseData.add(map);
            //Teaching Guide
            map = new HashMap<String, Object>();
            map.put(NAME, getString(R.string.teachingguideModuleLabel));
            map.put(IMAGE, getResources().getDrawable(R.drawable.file));
            courseData.add(map);
            //Syllabus (lectures)
            map = new HashMap<String, Object>();
            map.put(NAME, getString(R.string.syllabusLecturesModuleLabel));
            map.put(IMAGE, getResources().getDrawable(R.drawable.syllabus));
            courseData.add(map);
            //Syllabus (practicals)
            map = new HashMap<String, Object>();
            map.put(NAME, getString(R.string.syllabusPracticalsModuleLabel));
            map.put(IMAGE, getResources().getDrawable(R.drawable.lab));
            courseData.add(map);
            //Documents
            map = new HashMap<String, Object>();
            map.put(NAME, getString(R.string.documentsDownloadModuleLabel));
            map.put(IMAGE, getResources().getDrawable(R.drawable.folder));            courseData.add(map);
            //Shared area 
            map = new HashMap<String, Object>();
            map.put(NAME, getString(R.string.sharedsDownloadModuleLabel));
            map.put(IMAGE, getResources().getDrawable(R.drawable.folder_users));
            courseData.add(map);
            //Bibliography
            map = new HashMap<String, Object>();
            map.put(NAME, getString(R.string.bibliographyModuleLabel));
            map.put(IMAGE, getResources().getDrawable(R.drawable.book));
            courseData.add(map);
            //FAQs
            map = new HashMap<String, Object>();
            map.put(NAME, getString(R.string.faqsModuleLabel));
            map.put(IMAGE, getResources().getDrawable(R.drawable.faq));
            courseData.add(map);
            //Links
            map = new HashMap<String, Object>();
            map.put(NAME, getString(R.string.linksModuleLabel));
            map.put(IMAGE, getResources().getDrawable(R.drawable.link));
            courseData.add(map);
            
            //Evaluation category
            //Assessment system
            map = new HashMap<String, Object>();
            map.put(NAME, getString(R.string.assessmentModuleLabel));
            map.put(IMAGE, getResources().getDrawable(R.drawable.info));
            evaluationData.add(map);
            //Test
            map = new HashMap<String, Object>();
            map.put(NAME, getString(R.string.testsModuleLabel));
            map.put(IMAGE, getResources().getDrawable(R.drawable.test));
            evaluationData.add(map);

            //Users category
            //Groups
            map = new HashMap<String, Object>();
            map.put(NAME, getString(R.string.myGroupsModuleLabel));
            map.put(IMAGE, getResources().getDrawable(R.drawable.my_groups));
            mUsersData.add(map);
            //Generate QR code
            map = new HashMap<String, Object>();
            map.put(NAME, getString(R.string.generateQRModuleLabel));
            map.put(IMAGE, getResources().getDrawable(R.drawable.qr));
            mUsersData.add(map);
            
            //Messages category
            //Notifications
            map = new HashMap<String, Object>();
            map.put(NAME, getString(R.string.notificationsModuleLabel));
            map.put(IMAGE, getResources().getDrawable(R.drawable.notif));
            mMessagesData.add(map);
            //Messages
            map = new HashMap<String, Object>();
            map.put(NAME, getString(R.string.messagesModuleLabel));
            map.put(IMAGE, getResources().getDrawable(R.drawable.msg_write));
            mMessagesData.add(map);
            
            mExpandableListAdapter = new ImageExpandableListAdapter(
                    this,
                    mHeaderData,
                    R.layout.image_list_item,
                    new String[]{NAME},       // the name of the field data
                    new int[]{R.id.listText}, // the text field to populate with the field data
                    mChildData,
                    0,
                    null,
                    new int[]{}
            );
            
            mExpandableListView.setAdapter(mExpandableListAdapter);
        }
    }

    /**
     * Adapts the current menu to students view. Removes options unique to teachers and adds options unique to students
     */
    private void changeToStudentMenu() {
        if (currentRole == Constants.TEACHER_TYPE_CODE) {
            //Removes Publish Note from messages menu
        	mExpandableListAdapter.removeChild(Constants.MESSAGES_GROUP, Constants.PUBLISH_NOTE_CHILD);
            //Removes Rollcall from users menu
        	mExpandableListAdapter.removeChild(Constants.USERS_GROUP, Constants.ROLLCALL_CHILD);
        }
        
        currentRole = Constants.STUDENT_TYPE_CODE;
    }

    /**
     * Adapts the current menu to teachers view. Removes options unique to students and adds options unique to teachers
     */
    private void changeToTeacherMenu() {
        if (currentRole == Constants.STUDENT_TYPE_CODE) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put(NAME, getString(R.string.noticesModuleLabel));
            map.put(IMAGE, getResources().getDrawable(R.drawable.note));
            
            mMessagesData.add(map);

            map = new HashMap<String, Object>();
            map.put(NAME, getString(R.string.rollcallModuleLabel));
            map.put(IMAGE, getResources().getDrawable(R.drawable.roll_call));
            mUsersData.add(map);
            
            mExpandableListAdapter = new ImageExpandableListAdapter(this, mHeaderData,
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
        }
        
        currentRole = Constants.TEACHER_TYPE_CODE;
    }

    /**
     * Creates an empty Menu and spinner when the data base is empty
     */
    protected void setMenuDbClean() {
        Utils.setDbCleaned(false);
        Constants.setSelectedCourseCode(-1);
        Constants.setSelectedCourseShortName("");
        Constants.setSelectedCourseFullName("");
        Constants.setCurrentUserRole(-1);
        Preferences.setLastCourseSelected(-1);
        dBCleaned = true;
        listCourses.clear();
        cleanSpinner();
        
        mExpandableListView.setVisibility(View.GONE);
    }

    // TODO
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
        
        mExpandableClickListener = new OnChildClickListener() {
            
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                    int childPosition, long id) {
                // Get the item that was clicked
                Object o = parent.getExpandableListAdapter().getChild(groupPosition, childPosition);
                @SuppressWarnings("unchecked")
                String keyword = (String) ((Map<String, Object>) o).get(NAME);
                // boolean rollCallAndroidVersionOK =
                // (android.os.Build.VERSION.SDK_INT >=
                // android.os.Build.VERSION_CODES.FROYO);
                // PackageManager pm = getPackageManager();
                // boolean rearCam;

                // It would be safer to use the constant
                // PackageManager.FEATURE_CAMERA_FRONT
                // but since it is not defined for Android 2.2, I substituted
                // the literal value
                // frontCam =
                // pm.hasSystemFeature("android.hardware.camera.front");
                // rearCam = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);

                Intent activity;
                Context ctx = getApplicationContext();
                if (keyword.equals(getString(R.string.notificationsModuleLabel))) {
                    activity = new Intent(ctx, Notifications.class);
                    startActivityForResult(activity, Constants.NOTIFICATIONS_REQUEST_CODE);
                } else if (keyword.equals(getString(R.string.testsModuleLabel))) {
                    activity = new Intent(ctx, Tests.class);
                    startActivityForResult(activity, Constants.TESTS_REQUEST_CODE);
                } else if (keyword.equals(getString(R.string.messagesModuleLabel))) {
                    activity = new Intent(ctx, Messages.class);
                    activity.putExtra("eventCode", Long.valueOf(0));
                    startActivityForResult(activity, Constants.MESSAGES_REQUEST_CODE);
                } else if (keyword.equals(getString(R.string.noticesModuleLabel))) {
                    activity = new Intent(ctx, Notices.class);
                    startActivityForResult(activity, Constants.NOTICES_REQUEST_CODE);
                } else if (keyword.equals(getString(R.string.rollcallModuleLabel))) {
                    activity = new Intent(ctx, Rollcall.class);
                    startActivityForResult(activity, Constants.ROLLCALL_REQUEST_CODE);
                } else if (keyword.equals(getString(R.string.generateQRModuleLabel))) {
                    activity = new Intent(ctx, GenerateQR.class);
                    startActivityForResult(activity, Constants.GENERATE_QR_REQUEST_CODE);
                } else if (keyword.equals(getString(R.string.documentsDownloadModuleLabel))) {
                    activity = new Intent(ctx, DownloadsManager.class);
                    activity.putExtra("downloadsAreaCode", Constants.DOCUMENTS_AREA_CODE);
                    startActivityForResult(activity, Constants.DOWNLOADSMANAGER_REQUEST_CODE);
                } else if (keyword.equals(getString(R.string.sharedsDownloadModuleLabel))) {
                    activity = new Intent(ctx, DownloadsManager.class);
                    activity.putExtra("downloadsAreaCode", Constants.SHARE_AREA_CODE);
                    startActivityForResult(activity, Constants.DOWNLOADSMANAGER_REQUEST_CODE);
                } else if (keyword.equals(getString(R.string.myGroupsModuleLabel))) {
                    activity = new Intent(ctx, MyGroupsManager.class);
                    activity.putExtra("courseCode", Constants.getSelectedCourseCode());
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
 
	
    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
    	
    	final View course_list = findViewById(R.id.courses_list_view);
        final View progressAnimation = findViewById(R.id.get_courses_status);
        
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            
            progressAnimation.setVisibility(View.VISIBLE);
            progressAnimation.animate()
                            .setDuration(shortAnimTime)
                            .alpha(show ? 1 : 0)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    progressAnimation.setVisibility(show ? View.VISIBLE : View.GONE);
                                }
                            });

            course_list.setVisibility(View.VISIBLE);
            course_list.animate()
                          .setDuration(shortAnimTime)
                          .alpha(show ? 0 : 1)
                          .setListener(new AnimatorListenerAdapter() {
                              @Override
                              public void onAnimationEnd(Animator animation) {
                            	  course_list.setVisibility(show ? View.GONE
                                          : View.VISIBLE);
                              }
                          });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
        	progressAnimation.setVisibility(show ? View.VISIBLE : View.GONE);
        	course_list.setVisibility(show ? View.GONE : View.VISIBLE);
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
