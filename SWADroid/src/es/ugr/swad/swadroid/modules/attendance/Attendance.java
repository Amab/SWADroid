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
package es.ugr.swad.swadroid.modules.attendance;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.ksoap2.SoapFault;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.dataframework.DataFramework;

import es.ugr.swad.swadroid.Global;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.model.DataBaseHelper;
import es.ugr.swad.swadroid.model.User;
import es.ugr.swad.swadroid.modules.Module;

/**
 * Attendance module for roll call in class
 * @author Antonio Aguilera Malagon <aguilerin@gmail.com>
 */
public class Attendance extends Module {
	private ListView lv;
	private AlertDialog mAlertDialog;
	private List<ListItemModel> listModel;
	private ArrayList<String> idList = null;
	private ArrayList<Boolean> enrolledStudents = null;
	private long selectedCourseCode;
	/**
	 * Database Helper.
	 */
	protected static DataBaseHelper dbHelper;    
	/**
	 * Database Framework.
	 */
	protected static DataFramework db;  
	/**
	 * Array adapter for showing menu options
	 */
	private ArrayAdapter<String> adapter;
	/**
	 * Attendance tag name for Logcat
	 */
	public static final String TAG = Global.APP_TAG + " Attendance";

	/* (non-Javadoc)
	 * @see es.ugr.swad.swadroid.modules.Module#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		ImageView image;
		TextView text;
		ListView list;
		String[] items = getResources().getStringArray(R.array.attendanceMenuItems);

		OnItemClickListener clickListener = new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent activity;
				switch(position) {
				case 0:
					activity = new Intent(getBaseContext(), AttendanceConfigDownload.class);
					startActivityForResult(activity, Global.ATTENDANCE_CONFIG_DOWNLOAD_REQUEST_CODE);					
					break;
				case 1:
					// TODO: Manual mode
					break;
				}
			}    	
		};

		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_items);

		image = (ImageView) this.findViewById(R.id.moduleIcon);
		image.setBackgroundResource(R.drawable.rollcall);

		text = (TextView) this.findViewById(R.id.moduleName);
		text.setText(R.string.attendanceModuleLabel);

		this.findViewById(R.id.courseSelectedText).setVisibility(View.VISIBLE);
		this.findViewById(R.id.groupSpinner).setVisibility(View.GONE);
		
		text = (TextView) this.findViewById(R.id.courseSelectedText);
		text.setText(Global.getSelectedCourseShortName());
		
		adapter = new ArrayAdapter<String>(this, R.layout.simple_list_item, R.id.listText, items);
		list = (ListView) this.findViewById(R.id.listItems);
		list.setAdapter(adapter);
		list.setOnItemClickListener(clickListener);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		switch(requestCode) {
		case Global.ATTENDANCE_CONFIG_DOWNLOAD_REQUEST_CODE:
			if (resultCode == Activity.RESULT_OK) {
				idList = intent.getStringArrayListExtra("id_list");
				selectedCourseCode = Global.getSelectedCourseCode();

				if (idList.isEmpty()) {
					String noCodes = getString(R.string.scan_no_codes);
					Toast.makeText(this, noCodes, Toast.LENGTH_LONG).show();
				} else {
					listModel = new ArrayList<ListItemModel>();
					enrolledStudents = new ArrayList<Boolean>();

					// Initialize database
					try {
						db = DataFramework.getInstance();
						db.open(this, getPackageName());
						dbHelper = new DataBaseHelper(db);
					} catch (Exception ex) {
						Log.e(ex.getClass().getSimpleName(), ex.getMessage());
						ex.printStackTrace();
					}

					for (String dni: idList) {
						User u = (User) dbHelper.getRow(Global.DB_TABLE_USERS, "userID", dni);						
						if (u != null) {
							// Default photo for each item in the list
							listModel.add(new ListItemModel(u.getUserFirstname(),
									u.getUserSurname1(),
									u.getUserSurname2(),
									R.drawable.usr_bl));
							// Check if the specified user is enrolled in the selected course
							enrolledStudents.add(dbHelper.getUserCourse(dni, selectedCourseCode));
						}
					}
					// Mark as attending the students enrolled in selected course
					int listSize = listModel.size();
					for (int i=0; i < listSize; i++) {
						listModel.get(i).setSelected(enrolledStudents.get(i));
					}

					// Arrange the list alphabetically
					Collections.sort(listModel);
					ArrayAdapter<ListItemModel> modeAdapter = new InteractiveArrayAdapter(this, listModel);		
					lv = new ListView(this);
					lv.setAdapter(modeAdapter);

					prepareAlertDialog();
					// Show a dialog with the list of students
					mAlertDialog.show();
				}
			}
			break;
		}
	}

	public void prepareAlertDialog() {
		AlertDialog.Builder mBuider = new AlertDialog.Builder(this);
		mBuider.setTitle(getString(R.string.usersPresent));
		mBuider.setPositiveButton(getString(R.string.sendMsg), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		mBuider.setNegativeButton(getString(R.string.cancelMsg), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		mBuider.setView(lv);
		mAlertDialog = mBuider.create();
	}

	/* (non-Javadoc)
	 * @see es.ugr.swad.swadroid.modules.Module#requestService()
	 */
	@Override
	protected void requestService() throws NoSuchAlgorithmException, IOException, XmlPullParserException, SoapFault, IllegalAccessException, InstantiationException {
	}

	/* (non-Javadoc)
	 * @see es.ugr.swad.swadroid.modules.Module#connect()
	 */
	@Override
	protected void connect() {
	}

	/* (non-Javadoc)
	 * @see es.ugr.swad.swadroid.modules.Module#postConnect()
	 */
	@Override
	protected void postConnect() {
	}

	@Override
	protected void onError() {
	}

}
