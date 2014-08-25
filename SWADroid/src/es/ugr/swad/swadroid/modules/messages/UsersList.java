/**
 * Module to filter the users list to one course
 *
 * @author Jose Antonio Guerrero Aviles <cany20@gmail.com>
 */

package es.ugr.swad.swadroid.modules.messages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.gui.MenuActivity;
import es.ugr.swad.swadroid.model.Model;
import es.ugr.swad.swadroid.model.User;
import es.ugr.swad.swadroid.modules.rollcall.students.StudentItemModel;

public class UsersList extends MenuActivity {

	public static final String TAG = Constants.APP_TAG + " Users List";
    private List<StudentItemModel> studentsList;
    private String rcvs = "";
    private String rcvs_Aux = "";
	
    /**
	 * List of groups for ExpandableListView
	 */
	ArrayList<String> groupItem;
	/**
	 * List of childs for ExpandableListView
	 */
	ArrayList<List<Model>> childItem;
	/**
	 * Adapter container for users
	 */
	ExpandableStudentsListAdapter adapter;
	/**
	 * ListView container for notifications
	 */
	ExpandableListView list;
	/**
	 * Id for the teachers group
	 */
	int TEACHERS_GROUP_ID = 0;
	/**
	 * Id for the students group
	 */
	int STUDENTS_GROUP_ID = 1;
	/**
	 * Cursor orderby parameter
	 */
	private final String orderby = "userSurname1 DESC";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.users_listview);
		setTitle(R.string.selectRcvModuleLabel);
		getSupportActionBar().setIcon(R.drawable.users);
		
		list = (ExpandableListView) findViewById(R.id.users_explistview);
		
		groupItem = new ArrayList<String>();
		childItem = new ArrayList<List<Model>>();
		
		//Download the users list
		downloadStudentsList();
		
		//Set ExpandableListView data
		setGroupData();
		setChildGroupData();
		
		
		Button cancelList = (Button) findViewById(R.id.cancelList);
		cancelList.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});
		
		Button acceptList = (Button) findViewById(R.id.acceptList);
		acceptList.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
				//Aceptar la lista y a�adirla a los destinatarios
				for (StudentItemModel user : studentsList){
                    if (user.isSelected()){
                    	String us = user.getUserNickname();
                    	rcvs_Aux = rcvs_Aux + "@" + us + ",";
                    	
                    	//Elimino la ultima coma de la cadena, ya que no hay m�s usuarios para a�adir
                    	rcvs = rcvs_Aux.substring(0, rcvs_Aux.length()-1);
                    }
				}
				
				Intent resultData = new Intent();
				resultData.putExtra("ListaRcvs", rcvs);
				setResult(Activity.RESULT_OK, resultData);
                finish();
			}
		});
		
	}


	@Override
    protected void onStart() {
		super.onStart();
    }

	protected void connect() {
		
	}


	protected void requestService() throws Exception {
		
	}


	protected void postConnect() {
		
	}


	protected void onError() {
		
	}
	
	 private void downloadStudentsList() {
		 
		 	Intent donwloadUsersList = new Intent (getBaseContext(), DownloadUsers.class);
			startActivity(donwloadUsersList);

	}

	
	private void setChildGroupData() {
		
		List<Model> child = null;

		//Clear data
		childItem.clear();

		
		//Add data for teachers 
		child = dbHelper.getAllRows(Constants.DB_TABLE_USERS,"userRole='"
				+ Constants.TEACHER_TYPE_CODE+"'", orderby);		
		childItem.add(0,child);
		
		//Add data for students	
		child.clear();
		child = dbHelper.getAllRows(Constants.DB_TABLE_USERS,"userRole='"
				+ Constants.TEACHER_TYPE_CODE+"'", orderby);

		childItem.add(1,child);
		
		Log.d(TAG, "groups size=" + childItem.size());
		Log.d(TAG, "teachers children size=" + childItem.get(TEACHERS_GROUP_ID).size());
		Log.d(TAG, "students children size=" + childItem.get(STUDENTS_GROUP_ID).size());
		
		adapter = new ExpandableStudentsListAdapter(this, groupItem, childItem, Constants.getCurrentUserRole());
		list.setAdapter(adapter);
		
		if(dbHelper.getAllRowsCount(Constants.DB_TABLE_USERS) > 0) {
			Log.d(TAG, "[setChildGroupData] Users table is not empty");

		} else {
			Log.d(TAG, "[setChildGroupData] Users table is empty");

		}
	}

	private void setGroupData() {
		
		groupItem.add(getString(R.string.Filters_Teachers));
		groupItem.add(getString(R.string.Filters_Students));
		
	}
	
	
}