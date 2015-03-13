/**
 * Module to get the users list to one course
 *
 * @author Jose Antonio Guerrero Aviles <cany20@gmail.com>
 */

package es.ugr.swad.swadroid.modules.messages;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.gui.MenuActivity;

public class FilterUsersList extends MenuActivity {

	public static final String TAG = Constants.APP_TAG + " Users List";

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_filter_list);
		setTitle(R.string.filterUsersListModuleLabel);
		getActionBar().setIcon(R.drawable.users);

        
		Button cancelFIlters = (Button) findViewById(R.id.cancelFilters);
		cancelFIlters.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				//setResult(RESULT_CANCELED);
				finish();
			}
		});
		
		Button acceptFIlters = (Button) findViewById(R.id.acceptFilters);
		acceptFIlters.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				//TODO Filtrar la lista y mostrarla en el ListView
			}
		});
			
	}

   
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
}