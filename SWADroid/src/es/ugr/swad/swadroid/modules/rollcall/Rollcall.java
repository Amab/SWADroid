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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.client.android.Intents;

import es.ugr.swad.swadroid.Global;
import es.ugr.swad.swadroid.ImageExpandableListAdapter;
import es.ugr.swad.swadroid.MenuExpandableListActivity;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.model.Model;
import es.ugr.swad.swadroid.model.PracticeSession;
import es.ugr.swad.swadroid.model.User;
import es.ugr.swad.swadroid.modules.Groups;
import es.ugr.swad.swadroid.modules.rollcall.sessions.NewPracticeSession;
import es.ugr.swad.swadroid.modules.rollcall.sessions.SessionsHistory;
import es.ugr.swad.swadroid.modules.rollcall.students.StudentItemModel;
import es.ugr.swad.swadroid.modules.rollcall.students.StudentsArrayAdapter;
import es.ugr.swad.swadroid.modules.rollcall.students.StudentsHistory;

/**
 * Rollcall module.
 * @author Antonio Aguilera Malagon <aguilerin@gmail.com>
 */
public class Rollcall extends MenuExpandableListActivity {
	/**
	 * Function name field
	 */
	final String NAME = "listText";
	/**
	 * Function text field
	 */
	final String IMAGE = "listIcon";
	/**
	 * Cursor for database access
	 */
	private Cursor dbCursor;
	/**
	 * Rollcall tag name for Logcat
	 */
	public static final String TAG = Global.APP_TAG + " Rollcall";
	/**
	 * Practice group spinner
	 */
	private Spinner practiceGroup;
	/**
	 * User courses list
	 */
	private List<Model> listCourses;
	/**
	 * Students list
	 */
	List<StudentItemModel> studentsList;

	/* (non-Javadoc)
	 * @see android.app.ExpandableListActivity#onChildClick(android.widget.ExpandableListView, android.view.View, int, int, long)
	 */
	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
		// Get the item that was clicked
		Object o = getExpandableListAdapter().getChild(groupPosition, childPosition);
		@SuppressWarnings("unchecked")
		String keyword = (String) ((Map<String,Object>) o).get(NAME);
		Intent activity;
		Context context = getBaseContext();
		Cursor selectedGroup = (Cursor) practiceGroup.getSelectedItem();
		String groupName = selectedGroup.getString(2) + getString(R.string.groupSeparator) + selectedGroup.getString(3);

		if (keyword.equals(getString(R.string.studentsUpdate))) {
			activity = new Intent(context, RollcallConfigDownload.class);
			activity.putExtra("groupCode", (long) 0);
			startActivity(activity);
		} else if (keyword.equals(getString(R.string.studentsSelect))) {
			activity = new Intent(context, StudentsHistory.class);
			activity.putExtra("groupName", groupName);
			startActivityForResult(activity, Global.STUDENTS_HISTORY_REQUEST_CODE);
		} else if (keyword.equals(getString(R.string.newTitle))) {
			activity = new Intent(context, NewPracticeSession.class);
			activity.putExtra("groupCode", selectedGroup.getLong(1));
			activity.putExtra("groupName", groupName);
			startActivity(activity);
		} else if (keyword.equals(getString(R.string.sessionsSelect))) {
			activity = new Intent(context, SessionsHistory.class);
			activity.putExtra("groupCode", selectedGroup.getLong(1));
			activity.putExtra("groupName", groupName);
			startActivityForResult(activity, Global.ROLLCALL_HISTORY_REQUEST_CODE);
		} else if (keyword.equals(getString(R.string.rollcallScanQR))) {
			// Check if device has a camera
			if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
				activity = new Intent(Intents.Scan.ACTION);
				activity.putExtra("SCAN_MODE", "QR_CODE_MODE");
				activity.putExtra("SCAN_FORMATS", "QR_CODE");
				startActivityForResult(activity, Global.SCAN_QR_REQUEST_CODE);
			} else {
				error(getString(R.string.noCameraFound));
			}
		} else if (keyword.equals(getString(R.string.rollcallManual))) {
			showStudentsList();
		}

		return true;
	}

	private void showStudentsList() {
		List<Long> idList = dbHelper.getUsersCourse(Global.getSelectedCourseCode());
		if (!idList.isEmpty()) {
			studentsList = new ArrayList<StudentItemModel>();

			for (Long userCode: idList) {
				User u = (User) dbHelper.getRow(Global.DB_TABLE_USERS, "userCode", String.valueOf(userCode));
				studentsList.add(new StudentItemModel(u));
			}
			// Arrange the list alphabetically
			Collections.sort(studentsList);

			// Show a dialog with the list of students
			ListView lv = new ListView(this);
			lv.setAdapter(new StudentsArrayAdapter(this, studentsList, Global.ROLLCALL_REQUEST_CODE));

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
				if (!Global.connectionAvailable(ctx)) {
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
		long selectedCourse = Global.getSelectedCourseCode();
		Cursor selectedGroup = (Cursor) practiceGroup.getSelectedItem();
		long groupId = selectedGroup.getLong(1);
		PracticeSession ps;
		int numSelected = 0;

		// Check if no students are selected
		for(StudentItemModel user : studentsList)
			if (user.isSelected())
				numSelected++;

		if (numSelected > 0) {
			// If there is an ongoing practice session for the subject and 
			// practice group selected (can be only one), store rollcall data
			// for that session
			ps = dbHelper.getPracticeSessionInProgress(selectedCourse, groupId);
			if (ps != null) {
				for(StudentItemModel user : studentsList)
					if (user.isSelected())
						dbHelper.insertRollcallData(user.getId(), ps.getId());
				Toast.makeText(Rollcall.this, R.string.rollcallSaved, Toast.LENGTH_LONG).show();
			} else {
				// Show the list of practice sessions for that subject and group practices
				final List<PracticeSession> sessions = dbHelper.getPracticeSessions(selectedCourse, groupId);
				int numSessions = sessions.size();
				if (numSessions > 0) {
					final CharSequence [] items = new CharSequence[numSessions];
					for(int i=0; i < numSessions; i++) {
						items[i] = sessions.get(i).getSessionStart();
					}

					AlertDialog.Builder builder = new AlertDialog.Builder(Rollcall.this)
					.setTitle(R.string.sessionChoose)
					.setItems(items, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							for(StudentItemModel user : studentsList)
								if (user.isSelected())
									dbHelper.insertRollcallData(user.getId(), sessions.get(item).getId());
							Toast.makeText(Rollcall.this, R.string.rollcallSaved, Toast.LENGTH_LONG).show();
						}
					});
					builder.show();
				} else {
					error(getString(R.string.rollcallNoPracticeSessions));
				}
			}
		} else {
			Toast.makeText(Rollcall.this, R.string.rollcallNoStudentsSelected, Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		switch(requestCode) {
		case Global.GROUPS_REQUEST_CODE:
			// Check if course has practice groups
			Cursor c = dbHelper.getPracticeGroups(Global.getSelectedRollcallCourseCode());

			if (c.getCount() > 0) {
				fillGroupsSpinner(c);
			} else {
				practiceGroup.setEnabled(false);

				int groupCount = getExpandableListAdapter().getGroupCount();
				for (int i=groupCount-1; i >= 0; i--)
					((ImageExpandableListAdapter) getExpandableListAdapter()).removeGroup(i);

				error(getString(R.string.noGroupsAvailableMsg));
			}
			break;
		case Global.SCAN_QR_REQUEST_CODE:
			if (resultCode == Activity.RESULT_OK) {
				ArrayList<String> idList = intent.getStringArrayListExtra("id_list");
				if (!idList.isEmpty()) {
					studentsList = new ArrayList<StudentItemModel>();
					ArrayList<Boolean> enrolledStudents = new ArrayList<Boolean>();

					for (String id: idList) {
						User u = (User) dbHelper.getRow(Global.DB_TABLE_USERS, "userID", id);
						if (u != null) {
							studentsList.add(new StudentItemModel(u));
							// Check if the specified user is enrolled in the selected course
							enrolledStudents.add(dbHelper.isUserEnrolledCourse(id, Global.getSelectedCourseCode()));
						}
					}
					// Mark as attending the students enrolled in selected course
					int listSize = studentsList.size();
					for (int i=0; i < listSize; i++) {
						studentsList.get(i).setSelected(enrolledStudents.get(i));
					}

					// Arrange the list alphabetically
					Collections.sort(studentsList);
					ListView lv = new ListView(this);
					lv.setAdapter(new StudentsArrayAdapter(this, studentsList, Global.ROLLCALL_REQUEST_CODE));

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

	/* (non-Javadoc)
	 * @see es.ugr.swad.swadroid.MenuExpandableListActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_rollcall);

		ImageView image = (ImageView) this.findViewById(R.id.moduleIcon);
		image.setBackgroundResource(R.drawable.rollcall);

		TextView text = (TextView) this.findViewById(R.id.moduleName);
		text.setText(R.string.rollcallModuleLabel);

		listCourses = dbHelper.getAllRows(Global.DB_TABLE_COURSES, "userRole = 3", "fullName");

		// Update rollcall course
		long courseCode = Global.getSelectedCourseCode();

		Global.setSelectedRollcallCourseCode(courseCode);
		prefs.setRollcallCourseSelected(courseCode);

		createSpinnerAdapter();

		// Fill spinner with practice groups list from database
		Cursor c = dbHelper.getPracticeGroups(Global.getSelectedRollcallCourseCode());
		if (c.getCount() > 0) {
			fillGroupsSpinner(c);
		}
	}

	private void fillGroupsSpinner(Cursor c) {
		practiceGroup = (Spinner) this.findViewById(R.id.spGroup);

		startManagingCursor(c);
		SimpleCursorAdapter adapter = new SimpleCursorAdapter (this,
				R.layout.group_item,
				c,
				new String[] { "groupTypeName", "groupName" },
				new int[] { R.id.textView1, R.id.textView2 }
				);
		adapter.setDropDownViewResource(R.layout.group_dropdown_item);
		practiceGroup.setAdapter(adapter);
		practiceGroup.setEnabled(true);

		createBaseMenu();
	}

	private void createSpinnerAdapter() {
		Spinner spinner = (Spinner) this.findViewById(R.id.spCourse);
		dbCursor =  dbHelper.getDb().getCursor(Global.DB_TABLE_COURSES, "userRole = 3", "fullName");
		startManagingCursor(dbCursor);	

		SimpleCursorAdapter adapter = new SimpleCursorAdapter (this,
				android.R.layout.simple_spinner_item, 
				dbCursor, 
				new String[] { "fullName" }, 
				new int[] { android.R.id.text1 });
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);

		if(Global.getSelectedCourseCode() != -1) {
			boolean found = false;
			int i = 0;
			while (!found && i < listCourses.size()) {
				if (listCourses.get(i).getId() == Global.getSelectedCourseCode()) {
					found = true;
				} else {
					++i;
				}
			}
			if (i < listCourses.size())
				spinner.setSelection(i);
		}
		spinner.setOnItemSelectedListener(new onItemSelectedListener());
	}

	private class onItemSelectedListener implements OnItemSelectedListener{
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			long courseCode = listCourses.get(position).getId();

			Global.setSelectedRollcallCourseCode(courseCode);
			prefs.setRollcallCourseSelected(courseCode);

			// Fill spinner with practice groups list from database
			Cursor c = dbHelper.getPracticeGroups(Global.getSelectedRollcallCourseCode());
			if (c.getCount() > 0) {
				fillGroupsSpinner(c);
			} else {
				Intent activity = new Intent(getBaseContext(), Groups.class);
				activity.putExtra("courseCode", Global.getSelectedRollcallCourseCode());
				startActivityForResult(activity, Global.GROUPS_REQUEST_CODE);
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}
	}

	/**
	 * Creates base menu
	 * */
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

		HashMap<String, Object> map = new HashMap<String,Object>();
		map.put(NAME, getString(R.string.studentsUpdate));
		map.put(IMAGE, getResources().getDrawable(R.drawable.students_update));
		studentsData.add(map); 

		map = new HashMap<String,Object>();        
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

		map = new HashMap<String,Object>();
		map.put(NAME, getString(R.string.newTitle));
		map.put(IMAGE, getResources().getDrawable(R.drawable.session_new));
		sessionsData.add(map);

		map = new HashMap<String,Object>();
		map.put(NAME, getString(R.string.sessionsSelect));
		map.put(IMAGE, getResources().getDrawable(R.drawable.session_check));
		sessionsData.add(map);

		// Rollcall category
		final HashMap<String, Object> rollcall = new HashMap<String,Object>();
		rollcall.put(NAME, getString(R.string.rollcallModuleLabel));
		rollcall.put(IMAGE, getResources().getDrawable(R.drawable.rollcall));
		headerData.add(rollcall);

		final ArrayList<HashMap<String,Object>> rollcallData = new ArrayList<HashMap<String, Object>>();
		childData.add(rollcallData);

		map = new HashMap<String,Object>();
		map.put(NAME, getString(R.string.rollcallScanQR));
		map.put(IMAGE, getResources().getDrawable(R.drawable.scan_qr));
		rollcallData.add(map);

		map = new HashMap<String,Object>();
		map.put(NAME, getString(R.string.rollcallManual));
		map.put(IMAGE, getResources().getDrawable(R.drawable.rollcall_manual));
		rollcallData.add(map);

		setListAdapter( new ImageExpandableListAdapter(
				this,
				headerData,
				R.layout.image_list_item,
				new String[] { NAME },			// the name of the field data
				new int[] { R.id.listText },	// the text field to populate with the field data
				childData,
				0,
				null,
				new int[] {}
				));

		getExpandableListView().setOnChildClickListener(this);
	}

}
