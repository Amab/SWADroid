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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import es.ugr.swad.swadroid.Global;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.gui.MenuExpandableListActivity;
import es.ugr.swad.swadroid.model.Group;
import es.ugr.swad.swadroid.model.GroupType;
import es.ugr.swad.swadroid.model.Model;
import es.ugr.swad.swadroid.modules.GroupTypes;
import es.ugr.swad.swadroid.modules.Groups;
/**
 * Activity to manage the enrollments into groups. It is responsible for maintain the UI and send the appropriate web services
 *   It needs as extra data:
 * - (long) courseCode course code . It indicates the course to which the groups belong
 * 
 * @author Helena Rodriguez Gijon <hrgijon@gmail.com>
 * */


public class MyGroupsManager extends MenuExpandableListActivity {
	/**
	 * Tests tag name for Logcat
	 */
	public static final String TAG = Global.APP_TAG + "Groups Manager";
	/**
	 * Course code of current selected course
	 * */
	private long courseCode = -1;

	private ArrayList<Model> groupTypes;
	
	private HashMap<Long,ArrayList<Group>> groups;
	
	private boolean groupTypesRequested = false;
	
	private boolean refreshRequested = false;
	
	
	private ImageButton updateButton;
	private ProgressBar progressbar;
	
	@Override
	protected void onStart() {
		super.onStart();
		
		List<Model> groupTypes = dbHelper.getAllRows(Global.DB_TABLE_GROUP_TYPES, "courseCode = " + courseCode , "groupTypeName");
		List<Group> groups = dbHelper.getGroups(courseCode);
		if((!groupTypes.isEmpty()) && (!groups.isEmpty())){
			setMenu();
		}else{
			if(groupTypes.size() != 0){
				Intent activity = new Intent(getBaseContext(),Groups.class);
				activity.putExtra("courseCode", courseCode);
				startActivityForResult(activity,Global.GROUPS_REQUEST_CODE);
			}else{
				if(!groupTypesRequested){
					Intent activity = new Intent(getBaseContext(),GroupTypes.class);
					activity.putExtra("courseCode",  courseCode);
					startActivityForResult(activity,Global.GROUPTYPES_REQUEST_CODE);
				}
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		courseCode = getIntent().getLongExtra("courseCode",-1);
		
		setContentView(R.layout.group_choice);
		this.findViewById(R.id.courseSelectedText).setVisibility(View.VISIBLE);
		//in this module the group will not be chosen through the spinner, 
		//only will be shown the selected course name 
		this.findViewById(R.id.groupSpinner).setVisibility(View.GONE);
		
		TextView courseNameText = (TextView) this.findViewById(R.id.courseSelectedText);
		courseNameText.setText(Global.getSelectedCourseShortName());
		
		ImageView moduleIcon = (ImageView) this.findViewById(R.id.moduleIcon);
		moduleIcon.setBackgroundResource(R.drawable.my_groups);

		TextView moduleText = (TextView) this.findViewById(R.id.moduleName);
		moduleText.setText(R.string.myGroupsModuleLabel);
		
		progressbar = (ProgressBar) this.findViewById(R.id.progress_refresh);
		progressbar.setVisibility(View.GONE);
		updateButton = (ImageButton)this.findViewById(R.id.refresh);
		updateButton.setVisibility(View.VISIBLE);
		
		
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case Global.GROUPTYPES_REQUEST_CODE:
				groupTypesRequested = true;
				if(dbHelper.getAllRows(Global.DB_TABLE_GROUP_TYPES, "courseCode = " + courseCode , "groupTypeName").size() > 0){
					//If there are not group types, either groups. Therefore, there is no need to request groups
					Intent activity = new Intent(getBaseContext(),Groups.class);
					activity.putExtra("courseCode",  courseCode);
					startActivityForResult(activity,Global.GROUPS_REQUEST_CODE);
				}else
					setEmptyMenu();
				break;
			case Global.GROUPS_REQUEST_CODE:
				if(dbHelper.getGroups(courseCode).size() > 0 || refreshRequested){
					getExpandableListView().setVisibility(View.VISIBLE);
					this.findViewById(R.id.sendMyGroupsButton).setVisibility(View.VISIBLE);
					this.findViewById(R.id.noGroupsText).setVisibility(View.GONE);
					
					updateButton.setVisibility(View.VISIBLE);
					progressbar.setVisibility(View.GONE);
					
					refreshRequested = false;
					
					setMenu();
				}else
					setEmptyMenu();
					
				break;
			case Global.SENDMYGROUPS_REQUEST_CODE:
				int success = data.getIntExtra("success",0);
				if(success == 0){ //no enrollment was made
					HashMap<Long,ArrayList<Group>> currentGroups = getHashMapGroups(groupTypes);
					((EnrollmentExpandableListAdapter)getExpandableListView().getExpandableListAdapter()).resetChildren(currentGroups);
					((EnrollmentExpandableListAdapter)getExpandableListView().getExpandableListAdapter()).notifyDataSetChanged();
					showFailedEnrollmentDialog();
				}else{
					HashMap<Long,ArrayList<Group>> currentGroups = getHashMapGroups(groupTypes);
					((EnrollmentExpandableListAdapter)getExpandableListView().getExpandableListAdapter()).resetChildren(currentGroups);
					((EnrollmentExpandableListAdapter)getExpandableListView().getExpandableListAdapter()).notifyDataSetChanged();
					showSuccessfulEnrollmentDialog();
				}
				break;
			}
			
		}else{
			if(refreshRequested){
				updateButton.setVisibility(View.VISIBLE);
				progressbar.setVisibility(View.GONE);
				
				refreshRequested = false;
			}
		}
	}
	
	/**
	 * Shows informative dialog on successful enrollment 
	 */
	public void showSuccessfulEnrollmentDialog() {
		new AlertDialog.Builder(this)
		.setTitle(R.string.resultEnrollment)
		.setMessage(R.string.successfullEnrollment)
		.setCancelable(false)
		.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		})
		.show();
	}
	
	/**
	 *  Shows informative dialog on failed enrollment
	 */
	public void showFailedEnrollmentDialog() {
		new AlertDialog.Builder(this)
		.setTitle(R.string.resultEnrollment)
		.setMessage(R.string.failedEnrollment)
		.setCancelable(false)
		.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		})
		.show();
	}
	
	
	
	/**
	 * Shows dialog to ask for confirmation in group enrollments
	 */
	private void showConfirmEnrollmentDialog(){
		new AlertDialog.Builder(this)
		.setTitle(R.string.confirmEnrollments)
		.setMessage(R.string.areYouSure)
		.setCancelable(false)
		.setPositiveButton(R.string.yesMsg, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
				String myGroups = ((EnrollmentExpandableListAdapter)getExpandableListView().getExpandableListAdapter()).getChosenGroupCodesAsString();
				Intent activity = new  Intent(getBaseContext(), SendMyGroups.class);
				activity.putExtra("courseCode", courseCode);
				activity.putExtra("myGroups", myGroups);
				startActivityForResult(activity,Global.SENDMYGROUPS_REQUEST_CODE);
			}
		})
		.setNegativeButton(R.string.noMsg, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		})
		.show();
		
	}
	
	private String getRequestedGroups(){
		ArrayList<Long> requestedGroupCodes = ((EnrollmentExpandableListAdapter)getExpandableListView().getExpandableListAdapter()).getChosenGroupCodes();
		String text = "";
		for(int i = 0; i < requestedGroupCodes.size(); ++i){
			long groupCode = requestedGroupCodes.get(i).longValue();
			Group g = dbHelper.getGroup(groupCode);
			GroupType gT = dbHelper.getGroupTypeFromGroup(groupCode);
			text += gT.getGroupTypeName() + " : " + g.getGroupName() + "\n";
		}
		return text;
	}
	
	
	private void setEmptyMenu(){
		getExpandableListView().setVisibility(View.GONE);
		this.findViewById(R.id.sendMyGroupsButton).setVisibility(View.GONE);
		this.findViewById(R.id.noGroupsText).setVisibility(View.VISIBLE);
	}
	
	private void setMenu(){
		groupTypes = (ArrayList<Model>) dbHelper.getAllRows(Global.DB_TABLE_GROUP_TYPES, "courseCode ="+ String.valueOf(courseCode), "groupTypeName");
		HashMap<Long,ArrayList<Group>> children = getHashMapGroups(groupTypes);
		int currentRole = Global.getCurrentUserRole();
		EnrollmentExpandableListAdapter adapter = new EnrollmentExpandableListAdapter(getBaseContext(), groupTypes, children, R.layout.group_type_list_item, R.layout.group_list_item,currentRole);
		getExpandableListView().setAdapter(adapter);
		
		int collapsedGroups = getExpandableListView().getExpandableListAdapter().getGroupCount();
		for(int i=0; i < collapsedGroups; ++i)
			getExpandableListView().expandGroup(i);
		
		getExpandableListView().setOnChildClickListener(this);
		
		Button button = (Button) this.findViewById(R.id.sendMyGroupsButton);
		
		View.OnClickListener buttonListener = new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				showConfirmEnrollmentDialog();
			}
			
		};
		button.setOnClickListener(buttonListener);
		
	}


	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		boolean result = ((EnrollmentExpandableListAdapter) getExpandableListView().getExpandableListAdapter()).checkItem(groupPosition,childPosition);
		((EnrollmentExpandableListAdapter) getExpandableListView().getExpandableListAdapter()).notifyDataSetChanged();
		return  result;
	}

	@Override
	protected void onStop() {
		
		if(getExpandableListView().getExpandableListAdapter() != null){
			HashMap<Long,ArrayList<Group>> updatedChildren = getHashMapGroups(groupTypes);
			((EnrollmentExpandableListAdapter) getExpandableListView().getExpandableListAdapter() ).resetChildren(updatedChildren);
		}
		super.onStop();
	}
	
	private HashMap<Long,ArrayList<Group>> getHashMapGroups(ArrayList<Model> groupTypes){
		HashMap<Long,ArrayList<Group>> children = new HashMap<Long,ArrayList<Group>>();
		for(int i = 0; i < groupTypes.size(); ++i){
			long groupTypeCode = ((GroupType)groupTypes.get(i)).getId();
			ArrayList<Group> groups = (ArrayList<Group>)dbHelper.getGroupsOfType(groupTypeCode);
			children.put(Long.valueOf(groupTypeCode), groups);
		}
		return children;
	}
	
	/**
	 * Launches an action when refresh button is pushed.
	 *  
	 * The listener onClick is set in action_bar.xml
	 * @param v Actual view
	 */
	public void onRefreshClick(View v)
	{        
		updateButton.setVisibility(View.GONE);
		progressbar.setVisibility(View.VISIBLE);
		
		refreshRequested = true;
		
		Intent activity = new Intent(getBaseContext(),GroupTypes.class);
		activity.putExtra("courseCode",  courseCode);
		startActivityForResult(activity,Global.GROUPTYPES_REQUEST_CODE);

	}

	
}
