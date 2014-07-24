/**
 * Module to filter the users list to one course
 *
 * @author Jose Antonio Guerrero Aviles <cany20@gmail.com>
 */

package es.ugr.swad.swadroid.modules.messages;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.gui.MenuActivity;
import es.ugr.swad.swadroid.modules.rollcall.RollcallConfigDownload;

public class UsersList extends MenuActivity {

	public static final String TAG = Constants.APP_TAG + " Users List";
	
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
			}
		});
		
	}

    @Override
    protected void onStart() {
        super.onStart();
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
	        	
				Intent refreshUsersList = new Intent (getBaseContext(), RollcallConfigDownload.class);
				startActivity(refreshUsersList);
				
	            return true;
	
	        default:
	            return super.onOptionsItemSelected(item);
	    }
		    
	}
}