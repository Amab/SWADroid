/**
 * Module to filter the users list to one course
 *
 * @author Jose Antonio Guerrero Aviles <cany20@gmail.com>
 */

package es.ugr.swad.swadroid.modules.messages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.gui.MenuActivity;
import es.ugr.swad.swadroid.model.User;
import es.ugr.swad.swadroid.modules.rollcall.RollcallConfigDownload;
import es.ugr.swad.swadroid.modules.rollcall.students.StudentItemModel;
import es.ugr.swad.swadroid.modules.rollcall.students.StudentsArrayAdapter;

public class UsersList extends MenuActivity {

	public static final String TAG = Constants.APP_TAG + " Users List";
    private List<StudentItemModel> studentsList;
    private CharSequence rcvs = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.users_listview);
		setTitle(R.string.selectRcvModuleLabel);
		getSupportActionBar().setIcon(R.drawable.users);
	        
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
				//TODO Aceptar la lista y añadirla a los destinatarios
				for (StudentItemModel user : studentsList)
                    if (user.isSelected()){
                    	String us = user.getUserNickname();
                    	rcvs = rcvs + us + ",";
                    }
			}
			
		});
		
	}

    @Override
    protected void onStart() {
        super.onStart();

		/*La primera vez deberá cargar una lista vacía y para obtener la primera lista, 
		 * habrá que pulsar en el botón actualizar de la actionbar. A partir de este momento,
		 * tendremos una lista de usuarios en la memoria del teléfono y cuando  entre aquí la segunda
		 * vez y sucesivas, será esa la lista que se cargue. Si queremos, podemos volver a pulsar el
		 * botón actualizar por si hay nuevos usuarios.*/

        	showStudentsList();
	
        /*try {
            runConnection();
        } catch (Exception e) {
            String errorMsg = getString(R.string.errorServerResponseMsg);
            error(TAG, errorMsg, e, true);
        }*/
    }

	protected void connect() {
		/*String progressDescription = getString(R.string.informationProgressDescription);
		int progressTitle = R.string.informationProgressTitle;

		startConnection(true, progressDescription, progressTitle);*/
	}


	protected void requestService() throws Exception {
		
	}


	protected void postConnect() {
		
	}


	protected void onError() {
		
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
	            
	            // Show the list of students in the ListView
	            ListView lv = (ListView) findViewById(R.id.users_listview);
	            lv.setAdapter(new StudentsArrayAdapter(this, studentsList, Constants.ROLLCALL_REQUEST_CODE));

	            
	        } else {
	            Toast.makeText(this, R.string.scan_no_students, Toast.LENGTH_LONG).show();
	        }
	    }
	 
	
	 @Override
		public boolean onCreateOptionsMenu(Menu menu) {
		    getMenuInflater().inflate(R.menu.users_list_activity_actions, menu);
		    return super.onCreateOptionsMenu(menu);
	}
		
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.action_filter_users:
	        	
				Intent callFilterUsersList = new Intent (getBaseContext(), FilterUsersList.class);
				startActivity(callFilterUsersList);
				
	            return true;
	            
	        //Refresh users list    
	        case R.id.action_refresh_users:
	        					
				Intent refreshUsersList;
				Context context = getApplicationContext();
				
				refreshUsersList = new Intent(context, RollcallConfigDownload.class);
				refreshUsersList.putExtra("groupCode", (long) 0);
		        startActivity(refreshUsersList);
				
	            return true;
	
	        default:
	            return super.onOptionsItemSelected(item);
	    }
		    
	}
}