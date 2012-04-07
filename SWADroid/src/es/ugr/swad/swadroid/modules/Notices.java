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
package es.ugr.swad.swadroid.modules;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.ksoap2.SoapFault;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import es.ugr.swad.swadroid.Global;
import es.ugr.swad.swadroid.Preferences;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.model.Course;
import es.ugr.swad.swadroid.model.Model;
import es.ugr.swad.swadroid.model.User;

/**
 * Module for send messages.
 * @author Helena Rodriguez Gijon <helena.rodriguez.gijon@gmail.com>
 */
public class Notices extends Module {
	/**
     * Messages tag name for Logcat
     */
    public static final String TAG = Global.APP_TAG + " Notice";
    /**
     * Course code
     */
    private Long courseCode;
    /**
     * Notice's body
    */
    private String body;
    private Dialog noticeDialog;
    
    /**
     * Application preferences.
     */
    protected static Preferences prefs = new Preferences();
	
    /**
	 * Cursor for database access
	 */
	private Cursor dbCursor;
    
	/**
	 * User courses list
	 */
	private List<Model>listCourses;
	/**
	 * Selected course code
	 */
	private long selectedCourseCode = 0;
    
    private OnClickListener positiveClickListener = new OnClickListener() {
		
		public void onClick(View v) {
			if(isDebuggable) {
				Log.i(TAG, "on click positive before send request to server");
			}
				
			try {										
				/*if(isDebuggable) {
					Log.i(TAG, "selectedCourseCode = " + Long.toString(courseCode));
				}*/

				runConnection();
			} catch (Exception ex) {
            	String errorMsg = getString(R.string.errorServerResponseMsg);
				error(errorMsg);
				
        		if(isDebuggable) {
        			Log.e(ex.getClass().getSimpleName(), errorMsg);        		
        			ex.printStackTrace();
        		}
	        }
		}
	};
    
	private OnClickListener negativeClickListener = new OnClickListener() {
		public void onClick(View v) {
			finish();
		}
	};
	
	private void launchNoticeDialog(){

		noticeDialog = new Dialog(this);
		Button acceptButton, cancelButton;
		
		//Course selectedCourse = (Course)listCourses.get(selectedCourseCode);
		//String selectedCourseName = selectedCourse.getName();
		
		noticeDialog.setTitle(R.string.noticesModuleLabel);
		//TODO noticeDialog.setTitle(R.string.noticeModuleLabel + listCourses.get(selectedCourseCode));
		noticeDialog.setContentView(R.layout.notice_dialog);
		noticeDialog.setCancelable(true);
		
		noticeDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		
		acceptButton = (Button) noticeDialog.findViewById(R.id.notice_button_accept);
		acceptButton.setOnClickListener(positiveClickListener);
		
		cancelButton = (Button) noticeDialog.findViewById(R.id.notice_button_cancel);
		cancelButton.setOnClickListener(negativeClickListener);
		
		noticeDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			
			public void onCancel(DialogInterface dialog) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});
		noticeDialog.show();
		
	}
	@Override
	protected void requestService() throws NoSuchAlgorithmException,
			IOException, XmlPullParserException, SoapFault,
			IllegalAccessException, InstantiationException {
		
		readData();
		
		createRequest();
		
		addParam("wsKey",User.getWsKey());
		addParam("courseCode",(int)selectedCourseCode);
		addParam("body",body);
		
		sendRequest(User.class,false);
		
		setResult(RESULT_OK);
	}

	@Override
	protected void connect() {
		String progressDescription = getString(R.string.noticesModuleLabel);
    	int progressTitle = R.string.noticesModuleLabel;
  	    
        new Connect(false, progressDescription, progressTitle).execute();
		
		Toast.makeText(this, R.string.publishingNotice, Toast.LENGTH_SHORT).show();
		Log.i(TAG, getString(R.string.publishingNotice));

	}

	@Override
	protected void postConnect() {
		String noticeSended = getString(R.string.noticePublished);
		Toast.makeText(this, noticeSended, Toast.LENGTH_LONG).show();
		Log.i(TAG, noticeSended);
		finish();
	}

	private void readData() {
		EditText bd = (EditText) noticeDialog.findViewById(R.id.notice_body_text);
		body = bd.getText().toString();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setMETHOD_NAME("sendNotice");
	}

	@Override
	protected void onPause() {
		super.onPause();
		//noticeDialog.dismiss();
	}

	@Override
	protected void onStart() {
		Intent activity;
		
		super.onStart();
		prefs.getPreferences(getBaseContext());
		activity = new Intent(getBaseContext(), Courses.class );
		Toast.makeText(getBaseContext(), R.string.coursesProgressDescription, Toast.LENGTH_LONG).show();
		startActivityForResult(activity,Global.COURSES_REQUEST_CODE);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		int lastCourseSelected;
		
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == Activity.RESULT_OK){
			switch(requestCode){
			//After get the list of courses, a dialog is launched to choice the course
			case Global.COURSES_REQUEST_CODE:
				final AlertDialog.Builder coursesDialog = new AlertDialog.Builder(this);
				
				dbCursor = dbHelper.getDb().getCursor(Global.DB_TABLE_COURSES, "userRole>=3", "name");
				listCourses = dbHelper.getAllRows(Global.DB_TABLE_COURSES, "userRole>=3", "name");
				lastCourseSelected = prefs.getLastCourseSelected();
				coursesDialog.setSingleChoiceItems(dbCursor, lastCourseSelected, "name", new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int whichButton) {
						Course c = (Course) listCourses.get(whichButton);
						selectedCourseCode = c.getId();
						prefs.setLastCourseSelected(whichButton);
						
						if(isDebuggable){
							Integer s = whichButton;
							Log.i(TAG, "singleChoice = " + s.toString());
						}
						
					}
				});
				coursesDialog.setTitle(R.string.selectCourseTitle);
				coursesDialog.setPositiveButton(R.string.acceptMsg, new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						try {					
							if(selectedCourseCode == 0) { 
								Course c = (Course) listCourses.get(prefs.getLastCourseSelected());
								selectedCourseCode = c.getId();
							}
							
							if(isDebuggable) {
								Log.i(TAG, "selectedCourseCode = " + Long.toString(selectedCourseCode));
							}
							dialog.dismiss();
							launchNoticeDialog();
						} catch (Exception ex) {
		                	String errorMsg = getString(R.string.errorServerResponseMsg);
							error(errorMsg);
							
			        		if(isDebuggable) {
			        			Log.e(ex.getClass().getSimpleName(), errorMsg);        		
			        			ex.printStackTrace();
			        		}
				        }
					}
				});
				coursesDialog.setNegativeButton(R.string.cancelMsg, new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						setResult(RESULT_CANCELED);
						finish();
					}
				});
				coursesDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
					
					public void onCancel(DialogInterface dialog) {
						//dialog.cancel();
						setResult(RESULT_CANCELED);
						finish();
						
					}
				});
				coursesDialog.show();
				break;
			}
			
		} else {
        	setResult(RESULT_CANCELED);
        	finish();
        }
	}
	
	

}