package es.ugr.swad.swadroid.modules.groups;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.xmlpull.v1.XmlPullParserException;

import android.os.Bundle;
import android.util.Log;
import es.ugr.swad.swadroid.Global;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.model.Group;
import es.ugr.swad.swadroid.model.Model;
import es.ugr.swad.swadroid.modules.Module;
/**
 * Module to enroll into groups. 
 * It makes use of the web service sendGroups (see http://swad.ugr.es/ws/#sendMessage)
 *  It needs as extra data:
 * - (long) courseCode course code . It indicates the course to which the groups belong
 * - (string) myGroups: String that contains group codes separated with comma
 * It returns as extra data:
 * - (int) success :0 - it was impossible to satisfy all enrollment. Therefore it was not made any changes. The enrollment remains like before the request.
					other than 0 -if all the requested changes were possible and are made. It that case the groups in database will be also updated * 					!= 0 - 
 * @author Helena Rodriguez Gijon <hrgijon@gmail.com>
 * */


public class SendMyGroups extends Module {
	/**
	 * Course code 
	 * */
	private long courseCode = -1;
	/**
	 * String that contains group codes separated with comma
	 * */
	private String myGroups = null;
	/**
	 * Indicates if the enrollments are done or not
	 * 0 - if the enrollments are not done
	 * other than 0 - if they are correctly done
	 * */
	private int success = 0;
	/**
	 * Groups tag name for Logcat
	 */
	public static final String TAG = Global.APP_TAG + "Send My Groups";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		courseCode = getIntent().getLongExtra("courseCode", -1);
		myGroups = getIntent().getStringExtra("myGroups");
		if(courseCode == -1 || myGroups == null){
			Log.i(TAG, "Missing arguments");
			finish();
		}

		setMETHOD_NAME("sendMyGroups");
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

	@Override
	protected void runConnection() {
		super.runConnection();
		if (!isConnected) {
			setResult(RESULT_CANCELED);
			finish();
		}
	}

	@Override
	protected void requestService() throws NoSuchAlgorithmException,
	IOException, XmlPullParserException, SoapFault,
	IllegalAccessException, InstantiationException {
		createRequest();
		addParam("wsKey", Global.getLoggedUser().getWsKey());
		addParam("courseCode", (int)courseCode);
		addParam("myGroups", myGroups);
		sendRequest(Group.class,false);
		if(result != null){
			Vector<?> res = (Vector <?>) result;
			SoapPrimitive soapP = (SoapPrimitive) res.get(0);
			success = Integer.parseInt(soapP.toString());
			if(success != 0){
				List<Model> groupsSWAD = new ArrayList<Model>();

				SoapObject soapO = (SoapObject) res.get(2) ;
				int propertyCount = soapO.getPropertyCount();

				for(int i = 0; i < propertyCount ; ++i){
					SoapObject pii = (SoapObject)soapO.getProperty(i);

					long id = Long.parseLong(pii.getProperty("groupCode").toString());
					String groupName = pii.getProperty("groupName").toString();
					long groupTypeCode = Integer.parseInt(pii.getProperty("groupTypeCode").toString());
					String groupTypeName = pii.getProperty("groupTypeName").toString();
					int open = Integer.parseInt(pii.getProperty("open").toString());
					int maxStudents = Integer.parseInt(pii.getProperty("maxStudents").toString());
					int numStudents = Integer.parseInt(pii.getProperty("numStudents").toString());
					int fileZones = Integer.parseInt(pii.getProperty("fileZones").toString());
					int member = Integer.parseInt(pii.getProperty("member").toString());

					Group g = new Group(
							id,
							groupName,
							groupTypeCode,
							groupTypeName,
							open,
							maxStudents,
							numStudents,
							fileZones,
							member);
					groupsSWAD.add(g);

					if(isDebuggable){
						Log.i(TAG, g.toString());
					}
				}
				for(int i = 0; i < groupsSWAD.size(); ++i){
					Group g = (Group) groupsSWAD.get(i);
					//boolean isAdded = dbHelper.insertGroup(g,Global.getSelectedCourseCode());
					//if(!isAdded){
					if(!dbHelper.updateGroup(g.getId(), courseCode, g)){
						dbHelper.insertGroup(g,courseCode);
					}
				}
			}
		}else{
			setResult(RESULT_CANCELED);
		}
	}

	@Override
	protected void connect() {
		String progressDescription = getString(R.string.sendMyGroupsProgressDescription);
		int progressTitle = R.string.sendMyGroupsProgressTitle;

		new Connect(true, progressDescription, progressTitle).execute();


	}

	@Override
	protected void postConnect() {
		Log.i(TAG, "Enrollment requested");
		finish();
	}

	@Override
	protected void onError() {
		// TODO Auto-generated method stub

	}

}
