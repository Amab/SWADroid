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
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bugsense.trace.BugSenseHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import es.ugr.swad.swadroid.modules.notifications.Notifications;
import es.ugr.swad.swadroid.modules.rollcall.Rollcall;
import es.ugr.swad.swadroid.modules.tests.Tests;
import es.ugr.swad.swadroid.ssl.SecureConnection;
import es.ugr.swad.swadroid.sync.AccountAuthenticator;
import es.ugr.swad.swadroid.sync.SyncUtils;
import es.ugr.swad.swadroid.utils.Utils;

/**
 * Main class of the application.
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 * @author Antonio Aguilera Malagon <aguilerin@gmail.com>
 * @author Helena Rodriguez Gijon <hrgijon@gmail.com>
 */
public class SWADMain extends MenuExpandableListActivity {
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

    /**
     * Gets the database helper
     *
     * @return the database helper
     */
    public static DataBaseHelper getDbHelper() {
        return dbHelper;
    }

    /**
     * Shows configuration dialog on first run.
     */
    void showConfigurationDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.initialDialogTitle)
                .setMessage(R.string.firstRunMsg)
                .setCancelable(false)
                .setPositiveButton(R.string.yesMsg, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        viewPreferences();
                    }
                })
                .setNegativeButton(R.string.noMsg, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        createSpinnerAdapter();
                    }
                }).show();
    }

    /**
     * Shows initial dialog after application upgrade.
     */
    public void showUpgradeDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.initialDialogTitle)
                .setMessage(R.string.upgradeMsg)
                .setCancelable(false)
                .setNeutralButton(R.string.close_dialog, null)
                .show();
    }

    /* (non-Javadoc)
     * @see android.app.ExpandableListActivity#onChildClick(android.widget.ExpandableListView, android.view.View, int, int, long)
     */
    @Override
    public boolean onChildClick(ExpandableListView parent, View v,
                                int groupPosition, int childPosition, long id) {
        // Get the item that was clicked
        Object o = this.getExpandableListAdapter().getChild(groupPosition, childPosition);
        @SuppressWarnings("unchecked")
		String keyword = (String) ((Map<String, Object>) o).get(NAME);
        //boolean rollCallAndroidVersionOK = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.FROYO);
        //PackageManager pm = getPackageManager();
        //boolean rearCam;

        //It would be safer to use the constant PackageManager.FEATURE_CAMERA_FRONT
        //but since it is not defined for Android 2.2, I substituted the literal value
        //frontCam = pm.hasSystemFeature("android.hardware.camera.front");
        //rearCam = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);

        Intent activity;
        if (keyword.equals(getString(R.string.notificationsModuleLabel))) {
            activity = new Intent(getBaseContext(), Notifications.class);
            startActivityForResult(activity, Constants.NOTIFICATIONS_REQUEST_CODE);
        } else if (keyword.equals(getString(R.string.testsModuleLabel))) {
            activity = new Intent(getBaseContext(), Tests.class);
            startActivityForResult(activity, Constants.TESTS_REQUEST_CODE);
        } else if (keyword.equals(getString(R.string.messagesModuleLabel))) {
            activity = new Intent(getBaseContext(), Messages.class);
            activity.putExtra("notificationCode", Long.valueOf(0));
            startActivityForResult(activity, Constants.MESSAGES_REQUEST_CODE);
        } else if (keyword.equals(getString(R.string.noticesModuleLabel))) {
            activity = new Intent(getBaseContext(), Notices.class);
            startActivityForResult(activity, Constants.NOTICES_REQUEST_CODE);
        } else if (keyword.equals(getString(R.string.rollcallModuleLabel))) {
            activity = new Intent(getBaseContext(), Rollcall.class);
            startActivityForResult(activity, Constants.ROLLCALL_REQUEST_CODE);
        } else if (keyword.equals(getString(R.string.generateQRModuleLabel))) {
            activity = new Intent(getBaseContext(), GenerateQR.class);
            startActivityForResult(activity, Constants.GENERATE_QR_REQUEST_CODE);
        } else if (keyword.equals(getString(R.string.documentsDownloadModuleLabel))) {
            activity = new Intent(getBaseContext(), DownloadsManager.class);
            activity.putExtra("downloadsAreaCode", Constants.DOCUMENTS_AREA_CODE);
            startActivityForResult(activity, Constants.DOWNLOADSMANAGER_REQUEST_CODE);
        } else if (keyword.equals(getString(R.string.sharedsDownloadModuleLabel))) {
            activity = new Intent(getBaseContext(), DownloadsManager.class);
            activity.putExtra("downloadsAreaCode", Constants.SHARE_AREA_CODE);
            startActivityForResult(activity, Constants.DOWNLOADSMANAGER_REQUEST_CODE);
        } else if (keyword.equals(getString(R.string.myGroupsModuleLabel))) {
            activity = new Intent(getBaseContext(), MyGroupsManager.class);
            activity.putExtra("courseCode", Constants.getSelectedCourseCode());
            startActivityForResult(activity, Constants.MYGROUPSMANAGER_REQUEST_CODE);
        }

        return true;
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onCreate()
     */
    @Override
    public void onCreate(Bundle icicle) {
        int lastVersion, currentVersion;
        ImageView image;
        TextView text;

        //Initialize Bugsense plugin
        try {
        	BugSenseHandler.initAndStartSession(this, Constants.BUGSENSE_API_KEY);
        } catch (Exception e) {
        	Log.e(TAG, "Error initializing BugSense", e);
        }

        //Initialize screen
        super.onCreate(icicle);
        setContentView(R.layout.main);

        image = (ImageView) this.findViewById(R.id.moduleIcon);
        image.setBackgroundResource(R.drawable.ic_launcher_swadroid);

        text = (TextView) this.findViewById(R.id.moduleName);
        text.setText(R.string.app_name);


        ImageButton updateButton = (ImageButton) this.findViewById(R.id.refresh);
        updateButton.setVisibility(View.VISIBLE);

        try {
            //Initialize preferences
            prefs.getPreferences(getBaseContext());

            //Initialize HTTPS connections
            SecureConnection.initSecureConnection();

            //Check if this is the first run after an install or upgrade
            lastVersion = prefs.getLastVersion();
            currentVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
            dbHelper.initializeDB();
            //lastVersion = 51;
            //currentVersion = 52;

            //If this is the first run, show configuration dialog
            if (lastVersion == 0) {
                showConfigurationDialog();

                //Configure automatic synchronization
                Intent activity = new Intent(getBaseContext(), AccountAuthenticator.class);
                startActivity(activity);
                SyncUtils.addPeriodicSync(Constants.AUTHORITY, Bundle.EMPTY, Constants.DEFAULT_SYNC_TIME, this);
                prefs.setSyncTime(String.valueOf(Constants.DEFAULT_SYNC_TIME));

                prefs.setLastVersion(currentVersion);
                firstRun = true;
                Constants.setSelectedCourseCode(-1);

                Constants.setSelectedCourseShortName("");
                Constants.setSelectedCourseFullName("");

                Constants.setCurrentUserRole(-1);

            //If this is an upgrade, show upgrade dialog
            } else if (lastVersion < currentVersion) {
                //showUpgradeDialog();
                dbHelper.upgradeDB(this);

                //If the app is updating from an unencrypted user password version, encrypt user password
                if(lastVersion < 52) {
                	prefs.upgradeCredentials();
                	prefs.setSyncTime(String.valueOf(Constants.DEFAULT_SYNC_TIME));
                }
                
                //If the app is updating from an unencrypted version, encrypt already downloaded notifications
            	if (lastVersion < 45) {
                    dbHelper.encryptNotifications();

                /*
                 * If the app is updating from the bugged encrypted version,
                 * re-encrypt the notifications using the new method
                 */
                } else if (lastVersion == 45) {
                    dbHelper.reencryptNotifications();
                }

                //Configure automatic synchronization
                if (lastVersion < 49) {
                    Intent activity = new Intent(getBaseContext(), AccountAuthenticator.class);
                    startActivity(activity);
                    SyncUtils.addPeriodicSync(Constants.AUTHORITY, Bundle.EMPTY, Constants.DEFAULT_SYNC_TIME, this);
                }

                prefs.setLastVersion(currentVersion);
            }

            listCourses = dbHelper.getAllRows(Constants.DB_TABLE_COURSES, "", "shortName");
            if (listCourses.size() > 0) {
                Course c = (Course) listCourses.get(prefs.getLastCourseSelected());
                Constants.setSelectedCourseCode(c.getId());
                Constants.setSelectedCourseShortName(c.getShortName());
                Constants.setSelectedCourseFullName(c.getFullName());
                Constants.setCurrentUserRole(c.getUserRole());
            } else {
                Constants.setSelectedCourseCode(-1);
                Constants.setSelectedCourseShortName("");
                Constants.setSelectedCourseFullName("");
                Constants.setCurrentUserRole(-1);
                if (!firstRun && Utils.connectionAvailable(this))
                    getCurrentCourses(); //at the first run, this will be launched after the preferences menu
            }
            currentRole = -1;
        } catch (Exception ex) {
            error(TAG, ex.getMessage(), ex, true);
        }
        /*if(!firstRun && Module.connectionAvailable(this)){
            getActualCourses();
		}*/

    }

    /* (non-Javadoc)
     * @see android.app.Activity#onStart()
     */
    @Override
    protected void onStart() {
        super.onStart();
        prefs.getPreferences(getBaseContext());

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!Constants.isPreferencesChanged() && !Utils.isDbCleaned()) {
            createSpinnerAdapter();
            if (!firstRun) {
                courseCode = Constants.getSelectedCourseCode();
                createMenu();
            }
        } else {
            Constants.setPreferencesChanged(false);
            Utils.setDbCleaned(false);
            setMenuDbClean();
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
                    ImageButton updateButton = (ImageButton) this.findViewById(R.id.refresh);
                    ProgressBar pb = (ProgressBar) this.findViewById(R.id.progress_refresh);

                    pb.setVisibility(View.GONE);
                    updateButton.setVisibility(View.VISIBLE);

                    setMenuDbClean();
                    createSpinnerAdapter();
                    createMenu();
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
            if (prefs.getLastCourseSelected() < listCourses.size())
                spinner.setSelection(prefs.getLastCourseSelected());
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
                prefs.setLastCourseSelected(position);
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
                    if (Utils.connectionAvailable(getBaseContext()))
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
        Intent activity;
        activity = new Intent(getBaseContext(), Courses.class);
        Toast.makeText(getBaseContext(), R.string.coursesProgressDescription, Toast.LENGTH_LONG).show();
        startActivityForResult(activity, Constants.COURSES_REQUEST_CODE);
    }

    private void createMenu() {
        if (listCourses.size() != 0) {
            Course courseSelected;
            if (Constants.getSelectedCourseCode() != -1) {
                courseSelected = (Course) dbHelper.getRow(Constants.DB_TABLE_COURSES, "id", String.valueOf(Constants.getSelectedCourseCode()));
            } else {
                int lastSelected = prefs.getLastCourseSelected();
                if (lastSelected != -1 && lastSelected < listCourses.size()) {
                    courseSelected = (Course) listCourses.get(lastSelected);
                    Constants.setSelectedCourseCode(courseSelected.getId());
                    Constants.setSelectedCourseShortName(courseSelected.getShortName());
                    Constants.setSelectedCourseFullName(courseSelected.getFullName());
                    Constants.setCurrentUserRole(courseSelected.getUserRole());
                    prefs.setLastCourseSelected(lastSelected);
                } else {
                    courseSelected = (Course) listCourses.get(0);
                    Constants.setSelectedCourseCode(courseSelected.getId());
                    Constants.setSelectedCourseShortName(courseSelected.getShortName());
                    Constants.setSelectedCourseFullName(courseSelected.getFullName());
                    Constants.setCurrentUserRole(courseSelected.getUserRole());
                    prefs.setLastCourseSelected(0);
                }
            }

            if (courseSelected != null) {
                if ((getExpandableListAdapter() == null) || dBCleaned) {
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
        ExpandableListView list = (ExpandableListView) this.findViewById(android.R.id.list);
        list.setVisibility(View.VISIBLE);
        if (getExpandableListAdapter() == null || currentRole == -1) {
            //the menu base is equal to students menu.
            currentRole = Constants.STUDENT_TYPE_CODE;
            //Construct Expandable List
            final ArrayList<HashMap<String, Object>> headerData = new ArrayList<HashMap<String, Object>>();

            //Order:
            // 1- Course
            // 2- Evaluation
            // 3- Messages
            // 4- Enrollment
            // 5- Users
            final HashMap<String, Object> courses = new HashMap<String, Object>();
            courses.put(NAME, getString(R.string.course));
            courses.put(IMAGE, getResources().getDrawable(R.drawable.blackboard));
            headerData.add(courses);

            final HashMap<String, Object> evaluation = new HashMap<String, Object>();
            evaluation.put(NAME, getString(R.string.evaluation));
            evaluation.put(IMAGE, getResources().getDrawable(R.drawable.grades));
            headerData.add(evaluation);

            final HashMap<String, Object> messages = new HashMap<String, Object>();
            messages.put(NAME, getString(R.string.messages));
            messages.put(IMAGE, getResources().getDrawable(R.drawable.msg));
            headerData.add(messages);

            final HashMap<String, Object> enrolment = new HashMap<String, Object>();
            enrolment.put(NAME, getString(R.string.enrollment));
            enrolment.put(IMAGE, getResources().getDrawable(R.drawable.enrollment));
            headerData.add(enrolment);

            final HashMap<String, Object> users = new HashMap<String, Object>();
            users.put(NAME, getString(R.string.users));
            users.put(IMAGE, getResources().getDrawable(R.drawable.users));
            headerData.add(users);


            final ArrayList<ArrayList<HashMap<String, Object>>> childData = new ArrayList<ArrayList<HashMap<String, Object>>>();

            final ArrayList<HashMap<String, Object>> courseData = new ArrayList<HashMap<String, Object>>();
            childData.add(courseData);

            final ArrayList<HashMap<String, Object>> evaluationData = new ArrayList<HashMap<String, Object>>();
            childData.add(evaluationData);

            final ArrayList<HashMap<String, Object>> messagesData = new ArrayList<HashMap<String, Object>>();
            childData.add(messagesData);

            final ArrayList<HashMap<String, Object>> enrollmentData = new ArrayList<HashMap<String, Object>>();
            childData.add(enrollmentData);

            ArrayList<HashMap<String, Object>> usersData = new ArrayList<HashMap<String, Object>>();
            childData.add(usersData);

            HashMap<String, Object> map = new HashMap<String, Object>();

            //Documents category
            map.put(NAME, getString(R.string.documentsDownloadModuleLabel));
            map.put(IMAGE, getResources().getDrawable(R.drawable.folder));
            courseData.add(map);
            //shared area category
            map = new HashMap<String, Object>();
            map.put(NAME, getString(R.string.sharedsDownloadModuleLabel));
            map.put(IMAGE, getResources().getDrawable(R.drawable.folder_users));
            courseData.add(map);

            //Evaluation category
            map = new HashMap<String, Object>();
            map.put(NAME, getString(R.string.testsModuleLabel));
            map.put(IMAGE, getResources().getDrawable(R.drawable.test));
            evaluationData.add(map);

            //Messages category
            map = new HashMap<String, Object>();
            map.put(NAME, getString(R.string.notificationsModuleLabel));
            map.put(IMAGE, getResources().getDrawable(R.drawable.bell));
            messagesData.add(map);

            map = new HashMap<String, Object>();
            map.put(NAME, getString(R.string.messagesModuleLabel));
            map.put(IMAGE, getResources().getDrawable(R.drawable.msg_write));
            messagesData.add(map);

            //Enrollment category
            map = new HashMap<String, Object>();
            map.put(NAME, getString(R.string.myGroupsModuleLabel));
            map.put(IMAGE, getResources().getDrawable(R.drawable.my_groups));
            enrollmentData.add(map);

            map = new HashMap<String, Object>();
            map.put(NAME, getString(R.string.generateQRModuleLabel));
            map.put(IMAGE, getResources().getDrawable(R.drawable.scan_qr));
            usersData.add(map);

            setListAdapter(new ImageExpandableListAdapter(
                    this,
                    headerData,
                    R.layout.image_list_item,
                    new String[]{NAME},            // the name of the field data
                    new int[]{R.id.listText}, // the text field to populate with the field data
                    childData,
                    0,
                    null,
                    new int[]{}
            ));

            getExpandableListView().setOnChildClickListener(this);
        }
    }

    /**
     * Adapts the current menu to students view. Removes options unique to teachers and adds options unique to students
     */
    private void changeToStudentMenu() {
        if (currentRole == Constants.TEACHER_TYPE_CODE) {
            //Removes Publish Note from messages menu
            ((ImageExpandableListAdapter) getExpandableListAdapter()).removeChild(Constants.MESSAGES_GROUP, Constants.PUBLISH_NOTE_CHILD);
            //Removes Rollcall from users menu
            ((ImageExpandableListAdapter) getExpandableListAdapter()).removeChild(Constants.USERS_GROUP, Constants.ROLLCALL_CHILD);
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
            ((ImageExpandableListAdapter) getExpandableListAdapter()).addChild(Constants.MESSAGES_GROUP, Constants.PUBLISH_NOTE_CHILD, map);

            map = new HashMap<String, Object>();
            map.put(NAME, getString(R.string.rollcallModuleLabel));
            map.put(IMAGE, getResources().getDrawable(R.drawable.roll_call));
            ((ImageExpandableListAdapter) getExpandableListAdapter()).addChild(Constants.USERS_GROUP, Constants.ROLLCALL_CHILD, map);
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
        prefs.setLastCourseSelected(-1);
        dBCleaned = true;
        listCourses.clear();
        cleanSpinner();
        ExpandableListView list = (ExpandableListView) this.findViewById(android.R.id.list);
        list.setVisibility(View.INVISIBLE);
    }

    /**
     * Launches an action when refresh button is pushed
     *
     * @param v Actual view
     */
    public void onRefreshClick(View v) {
        ImageButton updateButton = (ImageButton) this.findViewById(R.id.refresh);
        ProgressBar pb = (ProgressBar) this.findViewById(R.id.progress_refresh);

        updateButton.setVisibility(View.GONE);
        pb.setVisibility(View.VISIBLE);

        getCurrentCourses();
    }
}
