/*
 *  This file is part of SWADroid.
 *
 *  Copyright (C) 2012 Helena Rodriguez Gijon <hrgijon@gmail.com>
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
package es.ugr.swad.swadroid.modules.groups;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.LongSparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.gui.DialogFactory;
import es.ugr.swad.swadroid.gui.MenuExpandableListActivity;
import es.ugr.swad.swadroid.model.Group;
import es.ugr.swad.swadroid.model.Model;
import es.ugr.swad.swadroid.modules.GroupTypes;
import es.ugr.swad.swadroid.modules.Groups;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity to manage the enrollments into groups. It is responsible for maintain the UI and send the appropriate web services
 * It needs as extra data:
 * - (long) courseCode course code . It indicates the course to which the groups belong
 *
 * @author Helena Rodriguez Gijon <hrgijon@gmail.com>
 */


public class MyGroupsManager extends MenuExpandableListActivity {
    /**
     * Tests tag name for Logcat
     */
    public static final String TAG = Constants.APP_TAG + "Groups Manager";
    /**
     * Course code of current selected course
     */
    private long courseCode = -1;

    private ArrayList<Model> groupTypes;

    private boolean groupTypesRequested = false;

    private boolean refreshRequested = false;

    /**
     * ActionBar menu
     */
    private Menu menu;

    private OnClickListener cancelClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
            dialog.cancel();
        }
    };

    private ExpandableListView mExpandableListView;
    
    @Override
    protected void onStart() {
        super.onStart();

        List<Model> groupTypes = dbHelper.getAllRows(Constants.DB_TABLE_GROUP_TYPES, "courseCode = " + courseCode, "groupTypeName");
        List<Group> groups = dbHelper.getGroups(courseCode);
        if ((!groupTypes.isEmpty()) && (!groups.isEmpty())) {
            setMenu();
        } else {
            if (groupTypes.size() != 0) {
                Intent activity = new Intent(this, Groups.class);
                activity.putExtra("courseCode", courseCode);
                startActivityForResult(activity, Constants.GROUPS_REQUEST_CODE);
            } else {
                if (!groupTypesRequested) {
                    Intent activity = new Intent(this, GroupTypes.class);
                    activity.putExtra("courseCode", courseCode);
                    startActivityForResult(activity, Constants.GROUPTYPES_REQUEST_CODE);
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        courseCode = getIntent().getLongExtra("courseCode", -1);

        setContentView(R.layout.group_choice);
        
        mExpandableListView = (ExpandableListView) findViewById(android.R.id.list);

        getSupportActionBar().setSubtitle(Constants.getSelectedCourseShortName());
    	getSupportActionBar().setIcon(R.drawable.my_groups);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Constants.GROUPTYPES_REQUEST_CODE:
                    groupTypesRequested = true;
                    if (dbHelper.getAllRows(Constants.DB_TABLE_GROUP_TYPES, "courseCode = " + courseCode, "groupTypeName").size() > 0) {
                        //If there are not group types, either groups. Therefore, there is no need to request groups
                        Intent activity = new Intent(getApplicationContext(), Groups.class);
                        activity.putExtra("courseCode", courseCode);
                        startActivityForResult(activity, Constants.GROUPS_REQUEST_CODE);
                    } else
                        setEmptyMenu();
                    break;
                case Constants.GROUPS_REQUEST_CODE:
                    if (dbHelper.getGroups(courseCode).size() > 0 || refreshRequested) {
                        mExpandableListView.setVisibility(View.VISIBLE);
                        menu.getItem(0).setVisible(true);
                        this.findViewById(R.id.noGroupsText).setVisibility(View.GONE);

                        refreshRequested = false;

                        setMenu();
                    } else
                        setEmptyMenu();

                    break;
                case Constants.SENDMYGROUPS_REQUEST_CODE:
                    int success = data.getIntExtra("success", 0);
                    if (success == 0) { //no enrollment was made
                        LongSparseArray<ArrayList<Group>> currentGroups = getHashMapGroups(groupTypes);
                        ((EnrollmentExpandableListAdapter) mExpandableListView.getExpandableListAdapter()).resetChildren(currentGroups);
                        ((EnrollmentExpandableListAdapter) mExpandableListView.getExpandableListAdapter()).notifyDataSetChanged();
                        showFailedEnrollmentDialog();
                    } else {
                        LongSparseArray<ArrayList<Group>> currentGroups = getHashMapGroups(groupTypes);
                        ((EnrollmentExpandableListAdapter) mExpandableListView.getExpandableListAdapter()).resetChildren(currentGroups);
                        ((EnrollmentExpandableListAdapter) mExpandableListView.getExpandableListAdapter()).notifyDataSetChanged();
                        showSuccessfulEnrollmentDialog();
                    }
                    break;
            }

        } else {
            if (refreshRequested) {

                refreshRequested = false;
            }
        }
    }

    /**
     * Shows informative dialog on successful enrollment
     */
    void showSuccessfulEnrollmentDialog() {        
    	AlertDialog dialog = DialogFactory.createNeutralDialog(this,
    			-1,
    			R.string.resultEnrollment,
    			R.string.successfullEnrollment,
    			R.string.ok,
    			cancelClickListener);

        dialog.show();
    }

    /**
     * Shows informative dialog on failed enrollment
     */
    void showFailedEnrollmentDialog() {
    	AlertDialog dialog = DialogFactory.createNeutralDialog(this,
    			-1,
    			R.string.resultEnrollment,
    			R.string.failedEnrollment,
    			R.string.ok,
    			cancelClickListener);

        dialog.show();
    }


    /**
     * Shows dialog to ask for confirmation in group enrollments
     */
    private void showConfirmEnrollmentDialog() {
    	OnClickListener positiveClickListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String myGroups = ((EnrollmentExpandableListAdapter) mExpandableListView.getExpandableListAdapter()).getChosenGroupCodesAsString();
                Intent activity = new Intent(getApplicationContext(), SendMyGroups.class);
                activity.putExtra("courseCode", courseCode);
                activity.putExtra("myGroups", myGroups);
                startActivityForResult(activity, Constants.SENDMYGROUPS_REQUEST_CODE);
            }
        };
        
    	AlertDialog dialog = DialogFactory.createWarningDialog(this,
    			-1,
    			R.string.confirmEnrollments,
    			R.string.areYouSureGroups,
    			R.string.yesMsg,
    			R.string.noMsg,
    			false,
    			positiveClickListener,
    			cancelClickListener,
    			null);

        dialog.show();
    }

    private void setEmptyMenu() {
        mExpandableListView.setVisibility(View.GONE);
        menu.getItem(0).setVisible(false);
        this.findViewById(R.id.noGroupsText).setVisibility(View.VISIBLE);
    }

    private void setMenu() {
        groupTypes = (ArrayList<Model>) dbHelper.getAllRows(Constants.DB_TABLE_GROUP_TYPES, "courseCode =" + String.valueOf(courseCode), "groupTypeName");
        LongSparseArray<ArrayList<Group>> children = getHashMapGroups(groupTypes);
        int currentRole = Constants.getCurrentUserRole();
        EnrollmentExpandableListAdapter adapter = new EnrollmentExpandableListAdapter(this, groupTypes, children, R.layout.group_type_list_item, R.layout.group_list_item, currentRole);
        mExpandableListView.setAdapter(adapter);

        int collapsedGroups = mExpandableListView.getExpandableListAdapter().getGroupCount();
        for (int i = 0; i < collapsedGroups; ++i)
            mExpandableListView.expandGroup(i);
    }

    @Override
    protected void onStop() {
        if (mExpandableListView.getExpandableListAdapter() != null) {
            LongSparseArray<ArrayList<Group>> updatedChildren = getHashMapGroups(groupTypes);
            ((EnrollmentExpandableListAdapter) mExpandableListView.getExpandableListAdapter()).resetChildren(updatedChildren);
        }
        super.onStop();
    }

    private LongSparseArray<ArrayList<Group>> getHashMapGroups(ArrayList<Model> groupTypes) {
        LongSparseArray<ArrayList<Group>> children = new LongSparseArray<ArrayList<Group>>();
        for (Model groupType : groupTypes) {
            long groupTypeCode = groupType.getId();
            ArrayList<Group> groups = (ArrayList<Group>) dbHelper.getGroupsOfType(groupTypeCode);
            children.put(groupTypeCode, groups);
        }
        return children;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                Intent activity = new Intent(this, GroupTypes.class);
                activity.putExtra("courseCode", courseCode);
                startActivityForResult(activity, Constants.GROUPTYPES_REQUEST_CODE);
                return true;
                
            case R.id.action_save:
            	showConfirmEnrollmentDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.groups_activity_actions, menu);
        this.menu = menu;
        
        return super.onCreateOptionsMenu(menu);
    }
}
