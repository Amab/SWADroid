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

package es.ugr.swad.swadroid.modules.rollcall;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.client.android.Intents;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.gui.ImageExpandableListAdapter;
import es.ugr.swad.swadroid.gui.MenuExpandableListActivity;
import es.ugr.swad.swadroid.model.Model;
import es.ugr.swad.swadroid.model.PracticeSession;
import es.ugr.swad.swadroid.model.User;
import es.ugr.swad.swadroid.modules.GroupTypes;
import es.ugr.swad.swadroid.modules.Groups;
import es.ugr.swad.swadroid.modules.rollcall.sessions.NewPracticeSession;
import es.ugr.swad.swadroid.modules.rollcall.sessions.SessionsHistory;
import es.ugr.swad.swadroid.modules.rollcall.students.StudentItemModel;
import es.ugr.swad.swadroid.modules.rollcall.students.StudentsArrayAdapter;
import es.ugr.swad.swadroid.modules.rollcall.students.StudentsHistory;
import es.ugr.swad.swadroid.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Rollcall module.
 *
 * @author Antonio Aguilera Malagon <aguilerin@gmail.com>
 */
public class Rollcall extends MenuExpandableListActivity {
    /**
     * Function name field
     */
    private final String NAME = "listText";
    /**
     * Function text field
     */
    private final String IMAGE = "listIcon";
    /**
     * Rollcall tag name for Logcat
     */
    public static final String TAG = Constants.APP_TAG + " Rollcall";
    /**
     * Course code of current selected course
     */
    private long courseCode = -1;
    /**
     * Practice group spinner
     */
    private Spinner practiceGroup;
    /**
     * Students list
     */
    private List<StudentItemModel> studentsList;

    private boolean groupTypesRequested = false;
    private boolean refreshRequested = false;

    private ProgressBar progressbar;
    private ImageButton updateButton;
    private ExpandableListView mExpandableListView;
    private OnChildClickListener mExpandableListListener;

    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.MenuExpandableListActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_rollcall);

    	getSupportActionBar().setIcon(R.drawable.roll_call);

        courseCode = Constants.getSelectedCourseCode();

        ImageView image = (ImageView) this.findViewById(R.id.moduleIcon);
        image.setBackgroundResource(R.drawable.roll_call);

        TextView text = (TextView) this.findViewById(R.id.moduleName);
        text.setText(R.string.rollcallModuleLabel);

        progressbar = (ProgressBar) this.findViewById(R.id.progress_refresh);
        progressbar.setVisibility(View.GONE);
        updateButton = (ImageButton) this.findViewById(R.id.refresh);
        updateButton.setVisibility(View.VISIBLE);
        mExpandableListView = (ExpandableListView) findViewById(android.R.id.list);
        mExpandableListListener = new OnChildClickListener() {
            
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                    int childPosition, long id) {
             // Get the item that was clicked
                Object o = parent.getExpandableListAdapter().getChild(groupPosition, childPosition);
                @SuppressWarnings("unchecked")
                String keyword = (String) ((Map<String, Object>) o).get(NAME);
                Intent activity;
                Context context = getApplicationContext();
                Cursor selectedGroup = (Cursor) practiceGroup.getSelectedItem();
                String groupName = selectedGroup.getString(2) + getString(R.string.groupSeparator) + selectedGroup.getString(3);
                PackageManager pm = getPackageManager();
                //boolean rollCallAndroidVersionFROYO = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.FROYO);
                boolean hasRearCam = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);

                if (keyword.equals(getString(R.string.studentsUpdate))) {
                    activity = new Intent(context, RollcallConfigDownload.class);
                    activity.putExtra("groupCode", (long) 0);
                    startActivity(activity);
                } else if (keyword.equals(getString(R.string.studentsSelect))) {
                    activity = new Intent(context, StudentsHistory.class);
                    activity.putExtra("groupName", groupName);
                    startActivityForResult(activity, Constants.STUDENTS_HISTORY_REQUEST_CODE);
                } else if (keyword.equals(getString(R.string.newTitle))) {
                    activity = new Intent(context, NewPracticeSession.class);
                    activity.putExtra("groupCode", selectedGroup.getLong(1));
                    activity.putExtra("groupName", groupName);
                    startActivity(activity);
                } else if (keyword.equals(getString(R.string.sessionsSelect))) {
                    activity = new Intent(context, SessionsHistory.class);
                    activity.putExtra("groupCode", selectedGroup.getLong(1));
                    activity.putExtra("groupName", groupName);
                    startActivityForResult(activity, Constants.ROLLCALL_HISTORY_REQUEST_CODE);
                } else if (keyword.equals(getString(R.string.rollcallScanQR))) {
                    //This module requires Android 2.2 or higher
                    //if(rollCallAndroidVersionFROYO) {
                    // Check if device has a rear camera
                    if (hasRearCam) {
                        activity = new Intent(Intents.Scan.ACTION);
                        activity.putExtra("SCAN_MODE", "QR_CODE_MODE");
                        activity.putExtra("SCAN_FORMATS", "QR_CODE");
                        startActivityForResult(activity, Constants.SCAN_QR_REQUEST_CODE);
                    } else {
                        //If the device has no rear camera available show error message
                        //error(getString(R.string.noRearCamera));
                        error(TAG, getString(R.string.noCameraFound), null, false);
                    }
                    /*} else {
                        //If Android version < 2.2 show error message
                        error(getString(R.string.froyoFunctionMsg) + "\n(System: " + android.os.Build.VERSION.RELEASE + ")");
                    }*/
                } else if (keyword.equals(getString(R.string.rollcallManual))) {
                    showStudentsList();
                }

                return true;
            }
        };

        mExpandableListView.setOnChildClickListener(mExpandableListListener);
        
        TextView courseNameText = (TextView) this.findViewById(R.id.courseSelectedText);
        courseNameText.setText(Constants.getSelectedCourseShortName());
    }

    @Override
    protected void onStart() {
        super.onStart();

        List<Model> groupTypes = dbHelper.getAllRows(Constants.DB_TABLE_GROUP_TYPES, "courseCode = " + courseCode, "groupTypeName");
        Cursor c = dbHelper.getPracticeGroups(Constants.getSelectedCourseCode());
        startManagingCursor(c);

        if (!groupTypes.isEmpty() && c.getCount() > 0) {
            // Fill spinner with practice groups list from database
            fillGroupsSpinner(c);
        } else if (!groupTypes.isEmpty()) {
            Intent activity = new Intent(this, Groups.class);
            activity.putExtra("courseCode", courseCode);
            startActivityForResult(activity, Constants.GROUPS_REQUEST_CODE);
        } else if (!groupTypesRequested) {
            Intent activity = new Intent(this, GroupTypes.class);
            activity.putExtra("courseCode", courseCode);
            startActivityForResult(activity, Constants.GROUPTYPES_REQUEST_CODE);
        }
    }

    private void fillGroupsSpinner(Cursor c) {
        practiceGroup = (Spinner) this.findViewById(R.id.spGroup);

        startManagingCursor(c);
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                R.layout.group_item,
                c,
                new String[]{"groupTypeName", "groupName"},
                new int[]{R.id.textView1, R.id.textView2}
        );
        adapter.setDropDownViewResource(R.layout.group_dropdown_item);
        practiceGroup.setAdapter(adapter);
        practiceGroup.setEnabled(true);

        createBaseMenu();
    }

    /**
     * Launches an action when refresh button is pushed.
     * <p/>
     * The listener onClick is set in action_bar.xml
     *
     * @param v Actual view
     */
    public void onRefreshClick(View v) {
        updateButton.setVisibility(View.GONE);
        progressbar.setVisibility(View.VISIBLE);

        refreshRequested = true;

        Intent activity = new Intent(this, GroupTypes.class);
        activity.putExtra("courseCode", courseCode);
        startActivityForResult(activity, Constants.GROUPTYPES_REQUEST_CODE);
    }

    private void showStudentsList() {
        List<Long> idList = dbHelper.getUsersCourse(Constants.getSelectedCourseCode());
        if (!idList.isEmpty()) {
            studentsList = new ArrayList<StudentItemModel>();

            for (Long userCode : idList) {
                User u = dbHelper.getUser("userCode", String.valueOf(userCode));
                studentsList.add(new StudentItemModel(u));
            }
            // Arrange the list alphabetically
            Collections.sort(studentsList);

            // Show a dialog with the list of students
            ListView lv = new ListView(this);
            lv.setAdapter(new StudentsArrayAdapter(this, studentsList, Constants.ROLLCALL_REQUEST_CODE));

            AlertDialog.Builder mBuilder = createDialog();
            mBuilder.setView(lv).show();
        } else {
            Toast.makeText(this, R.string.scan_no_students, Toast.LENGTH_LONG).show();
        }
    }

    private AlertDialog.Builder createDialog() {
        return new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_Translucent_NoTitleBar))
                .setTitle(getString(R.string.studentsList))
                .setPositiveButton(getString(R.string.sendMsg), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Context ctx = getApplicationContext();

                        storeRollcallData();
                        if (!Utils.connectionAvailable(ctx)) {
                            Toast.makeText(ctx, R.string.rollcallErrorNoConnection, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(ctx, R.string.rollcallWebServiceNotAvailable, Toast.LENGTH_LONG).show();
                            // TODO: send rollcall data to SWAD
                        }
                    }
                })
                .setNeutralButton(getString(R.string.saveMsg), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        storeRollcallData();
                    }
                })
                .setNegativeButton(getString(R.string.cancelMsg), null);
    }

    private void storeRollcallData() {
        long selectedCourse = Constants.getSelectedCourseCode();
        Cursor selectedGroup = (Cursor) practiceGroup.getSelectedItem();
        long groupId = selectedGroup.getLong(1);
        PracticeSession ps;
        int numSelected = 0;

        // Check if no students are selected
        for (StudentItemModel user : studentsList)
            if (user.isSelected())
                numSelected++;

        if (numSelected > 0) {
            // If there is an ongoing practice session for the subject and
            // practice group selected (can be only one), store rollcall data
            // for that session
            ps = dbHelper.getPracticeSessionInProgress(selectedCourse, groupId);
            if (ps != null) {
                for (StudentItemModel user : studentsList)
                    if (user.isSelected())
                        dbHelper.insertRollcallData(user.getId(), ps.getId());
                Toast.makeText(Rollcall.this, R.string.rollcallSaved, Toast.LENGTH_LONG).show();
            } else {
                // Show the list of practice sessions for that subject and group practices
                final List<PracticeSession> sessions = dbHelper.getPracticeSessions(selectedCourse, groupId);
                int numSessions = sessions.size();
                if (numSessions > 0) {
                    final CharSequence[] items = new CharSequence[numSessions];
                    for (int i = 0; i < numSessions; i++) {
                        items[i] = sessions.get(i).getSessionStart();
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(Rollcall.this)
                            .setTitle(R.string.sessionChoose)
                            .setItems(items, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int item) {
                                    for (StudentItemModel user : studentsList)
                                        if (user.isSelected())
                                            dbHelper.insertRollcallData(user.getId(), sessions.get(item).getId());
                                    Toast.makeText(Rollcall.this, R.string.rollcallSaved, Toast.LENGTH_LONG).show();
                                }
                            });
                    builder.show();
                } else {
                    error(TAG, getString(R.string.rollcallNoPracticeSessions), null, false);
                }
            }
        } else {
            Toast.makeText(Rollcall.this, R.string.rollcallNoStudentsSelected, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case Constants.GROUPTYPES_REQUEST_CODE:
                groupTypesRequested = true;
                List<Model> groupTypes = dbHelper.getAllRows(Constants.DB_TABLE_GROUP_TYPES, "courseCode = " + courseCode, "groupTypeName");
                if (!groupTypes.isEmpty()) {
                    // If there are not group types, either groups. Therefore, there is no need to request groups
                    Intent activity = new Intent(getApplicationContext(), Groups.class);
                    activity.putExtra("courseCode", courseCode);
                    startActivityForResult(activity, Constants.GROUPS_REQUEST_CODE);
                } else {
                    mExpandableListView.setVisibility(View.GONE);
                }
                break;
            case Constants.GROUPS_REQUEST_CODE:
                // Check if course has practice groups
                Cursor c = dbHelper.getPracticeGroups(Constants.getSelectedCourseCode());
                startManagingCursor(c);

                if (c.getCount() > 0 || refreshRequested) {
                    mExpandableListView.setVisibility(View.VISIBLE);
                    updateButton.setVisibility(View.VISIBLE);
                    progressbar.setVisibility(View.GONE);

                    refreshRequested = false;
                    fillGroupsSpinner(c);
                } else {
                    mExpandableListView.setVisibility(View.GONE);
                    practiceGroup = (Spinner) this.findViewById(R.id.spGroup);
                    practiceGroup.setEnabled(false);

                    error(TAG, getString(R.string.noGroupsAvailableMsg), null, false);
                }
                break;
            case Constants.SCAN_QR_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    ArrayList<String> idList = intent.getStringArrayListExtra("id_list");
                    if (!idList.isEmpty()) {
                        studentsList = new ArrayList<StudentItemModel>();
                        ArrayList<Boolean> enrolledStudents = new ArrayList<Boolean>();

                        for (String id : idList) {
                            User u = dbHelper.getUser("userID", id);
                            if (u != null) {
                                studentsList.add(new StudentItemModel(u));
                                // Check if the specified user is enrolled in the selected course
                                enrolledStudents.add(dbHelper.isUserEnrolledCourse(id, Constants.getSelectedCourseCode()));
                            }
                        }
                        // Mark as attending the students enrolled in selected course
                        int listSize = studentsList.size();
                        for (int i = 0; i < listSize; i++) {
                            studentsList.get(i).setSelected(enrolledStudents.get(i));
                        }

                        // Arrange the list alphabetically
                        Collections.sort(studentsList);
                        ListView lv = new ListView(this);
                        lv.setAdapter(new StudentsArrayAdapter(this, studentsList, Constants.ROLLCALL_REQUEST_CODE));

                        // Show a dialog with the list of students
                        AlertDialog.Builder mBuilder = createDialog();
                        mBuilder.setView(lv).show();
                    } else {
                        Toast.makeText(this, R.string.scan_no_codes, Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    /**
     * Creates base menu
     */
    private void createBaseMenu() {
        final ArrayList<HashMap<String, Object>> headerData = new ArrayList<HashMap<String, Object>>();
        final ArrayList<ArrayList<HashMap<String, Object>>> childData = new ArrayList<ArrayList<HashMap<String, Object>>>();

        // Students category
        final HashMap<String, Object> students = new HashMap<String, Object>();
        students.put(NAME, getString(R.string.studentsTitle));
        students.put(IMAGE, getResources().getDrawable(R.drawable.students));
        headerData.add(students);

        final ArrayList<HashMap<String, Object>> studentsData = new ArrayList<HashMap<String, Object>>();
        childData.add(studentsData);

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put(NAME, getString(R.string.studentsUpdate));
        map.put(IMAGE, getResources().getDrawable(R.drawable.students_update));
        studentsData.add(map);

        map = new HashMap<String, Object>();
        map.put(NAME, getString(R.string.studentsSelect));
        map.put(IMAGE, getResources().getDrawable(R.drawable.students_check));
        studentsData.add(map);

        // Practice sessions category
        final HashMap<String, Object> sessions = new HashMap<String, Object>();
        sessions.put(NAME, getString(R.string.sessionsTitle));
        sessions.put(IMAGE, getResources().getDrawable(R.drawable.sessions));
        headerData.add(sessions);

        final ArrayList<HashMap<String, Object>> sessionsData = new ArrayList<HashMap<String, Object>>();
        childData.add(sessionsData);

        map = new HashMap<String, Object>();
        map.put(NAME, getString(R.string.newTitle));
        map.put(IMAGE, getResources().getDrawable(R.drawable.session_new));
        sessionsData.add(map);

        map = new HashMap<String, Object>();
        map.put(NAME, getString(R.string.sessionsSelect));
        map.put(IMAGE, getResources().getDrawable(R.drawable.session_check));
        sessionsData.add(map);

        // Rollcall category
        final HashMap<String, Object> rollcall = new HashMap<String, Object>();
        rollcall.put(NAME, getString(R.string.rollcallModuleLabel));
        rollcall.put(IMAGE, getResources().getDrawable(R.drawable.roll_call));
        headerData.add(rollcall);

        final ArrayList<HashMap<String, Object>> rollcallData = new ArrayList<HashMap<String, Object>>();
        childData.add(rollcallData);

        map = new HashMap<String, Object>();
        map.put(NAME, getString(R.string.rollcallScanQR));
        map.put(IMAGE, getResources().getDrawable(R.drawable.scan_qr));
        rollcallData.add(map);

        map = new HashMap<String, Object>();
        map.put(NAME, getString(R.string.rollcallManual));
        map.put(IMAGE, getResources().getDrawable(R.drawable.rollcall_manual));
        rollcallData.add(map);

        mExpandableListView.setAdapter(new ImageExpandableListAdapter(
                                                                      this,
                                                                      headerData,
                                                                      R.layout.image_list_item,
                                                                      new String[] {
                                                                          NAME
                                                                      }, // the
                                                                         // name
                                                                         // of
                                                                         // the
                                                                         // field
                                                                         // data
                                                                      new int[] {
                                                                          R.id.listText
                                                                      }, // the
                                                                         // text
                                                                         // field
                                                                         // to
                                                                         // populate
                                                                         // with
                                                                         // the
                                                                         // field
                                                                         // data
                                                                      childData,
                                                                      0,
                                                                      null,
                                                                      new int[] {}
                           ));

        //getExpandableListView().setOnChildClickListener(this);
    }

}
