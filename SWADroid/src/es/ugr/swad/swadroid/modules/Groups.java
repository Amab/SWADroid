package es.ugr.swad.swadroid.modules;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Vector;

import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import es.ugr.swad.swadroid.Global;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.model.Group;

/**
 * Groups module gets user's groups inside the current course
 * and stores them in the database
 * @author Helena Rodriguez Gijon <hrgijon@gmail.com>
 * @author Antonio Aguilera Malagon <aguilerin@gmail.com>
 */
public class Groups extends Module {
	/**
	 * Groups counter
	 */
	private int numGroups;
	/**
	 * Groups tag name for Logcat
	 */
	public static final String TAG = Global.APP_TAG + " Groups";

	@Override
	protected void runConnection() {
		super.runConnection();
		if (!isConnected) {
			setResult(RESULT_CANCELED);
			finish();
		}
	}

	/* (non-Javadoc)
	 * @see es.ugr.swad.swadroid.modules.Module#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setMETHOD_NAME("getGroups");
	}

	@Override
	protected void onStart() {
		super.onStart();      
		try {
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

	/* (non-Javadoc)
	 * @see es.ugr.swad.swadroid.modules.Module#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_CANCELED) {
			setResult(RESULT_CANCELED);
			finish();
		}
	}

	@Override
	protected void connect() {
		String progressDescription = getString(R.string.groupsProgressDescription);
		int progressTitle = R.string.groupsProgressTitle;

		new Connect(true, progressDescription, progressTitle).execute();
	}

	@Override
	protected void requestService() throws NoSuchAlgorithmException, IOException, XmlPullParserException, SoapFault, IllegalAccessException, InstantiationException {
		// Creates webservice request, adds required params and sends request to webservice
		createRequest();
		addParam("wsKey", Global.getLoggedUser().getWsKey());
		Log.i(TAG, "selectedCourseCode=" + Global.getSelectedCourseCode());
		addParam("courseCode", Integer.valueOf(String.valueOf(Global.getSelectedCourseCode())));
		sendRequest(Group.class, false);

		if (result != null) {
			// Stores users data returned by webservice response
			Vector<?> res = (Vector<?>) result;
			SoapObject soap = (SoapObject) res.get(1);
			numGroups = soap.getPropertyCount();
			for (int i = 0; i < numGroups; i++) {
				SoapObject pii = (SoapObject) soap.getProperty(i);
				long groupCode = new Long(pii.getProperty("groupCode").toString());
				String groupName = pii.getProperty("groupName").toString();
				int groupTypeCode = Integer.parseInt(pii.getProperty("groupTypeCode").toString());
				String groupTypeName = pii.getProperty("groupTypeName").toString();

				Group g = new Group(
						groupCode,
						groupName,
						groupTypeCode,
						groupTypeName);

				dbHelper.insertGroup(g, Global.getSelectedCourseCode());
				Log.d(TAG, g.toString());
			}	// end for (int i=0; i < usersCount; i++)

			if(isDebuggable) {
				Log.d(TAG, "Retrieved " + numGroups + " groups");
			}
		} // end if (result != null)

		// Request finalized without errors
		setResult(RESULT_OK);
	}

	@Override
	protected void postConnect() {
		finish();
	}

	@Override
	protected void onError() {
	}
}
