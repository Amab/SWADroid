package es.ugr.swad.swadroid.modules.groups;

import java.util.ArrayList;
import java.util.List;

import com.android.dataframework.DataFramework;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.TextView;
import es.ugr.swad.swadroid.Global;
import es.ugr.swad.swadroid.MenuActivity;
import es.ugr.swad.swadroid.model.DataBaseHelper;
import es.ugr.swad.swadroid.model.Group;
import es.ugr.swad.swadroid.model.GroupType;
import es.ugr.swad.swadroid.model.Model;
import es.ugr.swad.swadroid.modules.GroupTypes;
import es.ugr.swad.swadroid.modules.Groups;
import es.ugr.swad.swadroid.R;
/**
 * Activity to manage the enrollments into groups. It is responsible for maintain the UI and send the appropriate web services
 *   It needs as extra data:
 * - (long) courseCode course code . It indicates the course to which the groups belong
 * */


public class MyGroupsManager extends MenuActivity {
	
	/**
	 * Course code of current selected course
	 * */
	private long courseCode = -1;
	/**
	 * Database Framework.
	 */
	protected static DataFramework db; 
	/**
	 * List of Group Types of the selected Course 
	 */
	//private List<Model> groupTypes = null;
	/**
	 * List of Group Types of the selected Course 
	 */
	//private List<Group> groups = null; 
	
	private ExpandableListView expandableList = null;
	private GroupsResourceCursorTreeAdapter listAdapter = null;
	
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
				Intent activity = new Intent(getBaseContext(),GroupTypes.class);
				activity.putExtra("courseCode",  courseCode);
				startActivityForResult(activity,Global.GROUPTYPES_REQUEST_CODE);
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Initialize database
		try {
			db = DataFramework.getInstance();
			db.open(this, getPackageName());
			dbHelper = new DataBaseHelper(db);
		} catch (Exception ex) {
			Log.e(ex.getClass().getSimpleName(), ex.getMessage());
			ex.printStackTrace();
		}
		
		courseCode = getIntent().getLongExtra("courseCode",-1);
		
		
		
		setContentView(R.layout.group_choice);
		this.findViewById(R.id.courseSelectedText).setVisibility(View.VISIBLE);
		//in this module the group will not be chosen through the spinner, 
		//only will be shown the selected course name 
		this.findViewById(R.id.groupSpinner).setVisibility(View.GONE);
		
		TextView courseNameText = (TextView) this.findViewById(R.id.courseSelectedText);
		courseNameText.setText(Global.getSelectedCourseShortName());
		
		//TODO Add icon for My Groups Module
		//ImageView moduleIcon = (ImageView) this.findViewById(R.id.moduleIcon);
		//moduleIcon.setBackgroundResource(R.drawable.folder);

		TextView moduleText = (TextView) this.findViewById(R.id.moduleName);
		moduleText.setText(R.string.myGroupsModuleLabel);
		
		expandableList = (ExpandableListView) this.findViewById(R.id.expandableListGroups);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case Global.GROUPTYPES_REQUEST_CODE:
				Intent activity = new Intent(getBaseContext(),GroupTypes.class);
				activity.putExtra("courseCode",  Global.getSelectedCourseCode());
				startActivityForResult(activity,Global.GROUPS_REQUEST_CODE);
				break;
			case Global.GROUPS_REQUEST_CODE:
				setMenu();
				break;	
			}
			
		}
	}
	
	private void setMenu(){
		Cursor gTCursor = dbHelper.getCursorGroupType(courseCode);
		listAdapter = new GroupsResourceCursorTreeAdapter(getBaseContext(), gTCursor,
				R.layout.group_type_list_item, R.layout.group_list_item);
		expandableList.setAdapter(listAdapter);
		
		int collapsedGroups = expandableList.getExpandableListAdapter().getGroupCount();
		for(int i=0; i < collapsedGroups; ++i){
			expandableList.expandGroup(i);
		}
		
	}

}
