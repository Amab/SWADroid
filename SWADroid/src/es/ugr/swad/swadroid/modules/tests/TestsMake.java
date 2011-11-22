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
package es.ugr.swad.swadroid.modules.tests;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.ksoap2.SoapFault;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import es.ugr.swad.swadroid.Global;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.model.Course;
import es.ugr.swad.swadroid.model.Model;
import es.ugr.swad.swadroid.model.Test;
import es.ugr.swad.swadroid.modules.Module;
import es.ugr.swad.swadroid.widget.NumberPicker;

/**
 * Tests module for evaluate user skills in a course
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class TestsMake extends Module {
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
	private Integer selectedCourseCode;
	/**
	 * Test's number of questions
	 */
	private int numQuestions;
	/**
	 * Test data
	 */
	private Test test;
	/**
	 * Click listener for courses dialog
	 */
	private OnClickListener singleChoiceItemsClickListener;
	/**
	 * Course selection dialog
	 */
	private AlertDialog.Builder coursesDialog;
    /**
     * Tests tag name for Logcat
     */
    public static final String TAG = Global.APP_TAG + " TestsMake";
	
	/**
	 * Sets layout maintaining tests action bar
	 * @param layout Layout to be applied
	 */
	private void setLayout(int layout) {
		ImageView image;
		TextView text;
		
		setContentView(layout);
        
        image = (ImageView)this.findViewById(R.id.moduleIcon);
        image.setBackgroundResource(R.drawable.test);
        
        text = (TextView)this.findViewById(R.id.moduleName);
        text.setText(R.string.testsModuleLabel);
	}
	
	/**
	 * Screen to select the number of questions in the test 
	 */
	private void setNumQuestions() {
		final NumberPicker numberPicker;
		Button acceptButton;
		
		setLayout(R.layout.tests_num_questions);
	    
		numberPicker = (NumberPicker)findViewById(R.id.testNumQuestionsNumberPicker);		
		numberPicker.setRange(test.getMin(), test.getMax());
		numberPicker.setCurrent(test.getDef());
		
		acceptButton = (Button)findViewById(R.id.testNumQuestionsAcceptButton);
		acceptButton.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View v) {
				numQuestions = numberPicker.getCurrent();
				
				if(isDebuggable) {
					Log.d(TAG, "numQuestions="+numQuestions);
				}
			}
		});
	}
	
	/* (non-Javadoc)
	 * @see es.ugr.swad.swadroid.modules.Module#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setLayout(R.layout.tests_make_main);
		
		singleChoiceItemsClickListener = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				Course c = (Course) listCourses.get(whichButton);
				selectedCourseCode = c.getId();
				
				if(isDebuggable) {
					Integer s = whichButton;
					Log.d(TAG, "singleChoice = " + s.toString());
				}
			}
		};
		OnClickListener positiveClickListener = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				if(isDebuggable) {
					Integer s = selectedCourseCode;
					Log.d(TAG, "selectedCourseCode = " + s.toString());
				}
				
				if(selectedCourseCode != 0) {
					test = (Test) dbHelper.getRow(Global.DB_TABLE_TEST_CONFIG, "id", selectedCourseCode.toString());
					
					if(test != null) {
						setNumQuestions();
					} else {
						Toast.makeText(getBaseContext(), R.string.testNoQuestionsCourseMsg, Toast.LENGTH_LONG).show();
						finish();
					}
				} else {
					Toast.makeText(getBaseContext(), R.string.noCourseSelectedMsg, Toast.LENGTH_LONG).show();
				}
			}
		};
		OnClickListener negativeClickListener = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
				finish();
			}
		};
		
		coursesDialog = new AlertDialog.Builder(this);		
		coursesDialog.setTitle(R.string.selectCourseTitle);
		coursesDialog.setPositiveButton(R.string.acceptMsg, positiveClickListener);
		coursesDialog.setNegativeButton(R.string.cancelMsg, negativeClickListener);
	}

	/* (non-Javadoc)
	 * @see es.ugr.swad.swadroid.modules.Module#onStart()
	 */
	@Override
	protected void onStart() {		
		super.onStart();
		if(dbHelper.getDb().getCursor(Global.DB_TABLE_TEST_CONFIG).getCount() > 0) {
			
			dbCursor = dbHelper.getDb().getCursor(Global.DB_TABLE_COURSES);
			listCourses = dbHelper.getAllRows(Global.DB_TABLE_COURSES);
			coursesDialog.setSingleChoiceItems(dbCursor, -1, "name", singleChoiceItemsClickListener);		
			coursesDialog.show();
		} else {
			Toast.makeText(getBaseContext(), R.string.testNoQuestionsMsg, Toast.LENGTH_LONG).show();
		}
	}

	/* (non-Javadoc)
	 * @see es.ugr.swad.swadroid.modules.Module#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		OnClickListener singleChoiceItemsClickListener = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				Course c = (Course) listCourses.get(whichButton);
				selectedCourseCode = c.getId();
				
				if(isDebuggable) {
					Integer s = whichButton;
					Log.d(TAG, "singleChoice = " + s.toString());
				}
			}
		};
		OnClickListener positiveClickListener = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				try {
					if(isDebuggable) {
						Integer s = selectedCourseCode;
						Log.d(TAG, "selectedCourseCode = " + s.toString());
					}
					
					if(selectedCourseCode != 0) {
						test = (Test) dbHelper.getRow(Global.DB_TABLE_TEST_CONFIG, "id", selectedCourseCode.toString());
						
						if(test != null) {
							setNumQuestions();
						} else {
							Toast.makeText(getBaseContext(), R.string.testNoQuestionsMsg, Toast.LENGTH_LONG).show();
						}
					} else {
						Toast.makeText(getBaseContext(), R.string.noCourseSelectedMsg, Toast.LENGTH_LONG).show();
					}
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
		OnClickListener negativeClickListener = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
				finish();
			}
		};
			
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
            switch(requestCode) {
	            case Global.COURSES_REQUEST_CODE:
	            	final AlertDialog.Builder alert = new AlertDialog.Builder(this);
	            	dbCursor = dbHelper.getDb().getCursor(Global.DB_TABLE_COURSES);
	            	listCourses = dbHelper.getAllRows(Global.DB_TABLE_COURSES);
	        		alert.setSingleChoiceItems(dbCursor, -1, "name", singleChoiceItemsClickListener);
	        		alert.setTitle(R.string.selectCourseTitle);
	        		alert.setPositiveButton(R.string.acceptMsg, positiveClickListener);
	        		alert.setNegativeButton(R.string.cancelMsg, negativeClickListener);	        		
	        		alert.show();
	            	break;
            }
        }
	}

	/* (non-Javadoc)
	 * @see es.ugr.swad.swadroid.modules.Module#requestService()
	 */
	@Override
	protected void requestService() throws NoSuchAlgorithmException,
			IOException, XmlPullParserException, SoapFault,
			IllegalAccessException, InstantiationException {

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

}
