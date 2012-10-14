package es.ugr.swad.swadroid.modules;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import es.ugr.swad.swadroid.Global;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.model.Group;
import es.ugr.swad.swadroid.model.GroupType;
import es.ugr.swad.swadroid.model.Model;
import es.ugr.swad.swadroid.modules.Module.Connect;
/**
 * Group type module to get the group types of a given course
 * and stores them in the database
 * @author Helena Rodriguez Gijon <hrgijon@gmail.com>
 * */
public class GroupTypes extends Module {
	/**
	 * Group types name for Logcat
	 * */
	public static final String TAG = Global.APP_TAG + " Group Types";
	/**
	 * Course code to which the request is related 
	 * */
	private long courseCode = -1;
	@Override
	protected void runConnection() {
		super.runConnection();
		if (!isConnected) {
			setResult(RESULT_CANCELED);
			finish();
		}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		courseCode = getIntent().getLongExtra("courseCode", -1);
		setMETHOD_NAME("getGroupTypes");
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
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_CANCELED) {
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
		addParam("courseCode", (int)Global.getSelectedCourseCode());
		sendRequest(GroupTypes.class,false);
		
		if(result != null){
			//Stores groups data returned by webservice response
			List<Model> groupsSWAD = new ArrayList<Model>();
			
			Vector<?> res = (Vector <?>) result;
			SoapObject soap = (SoapObject) res.get(1);	
			int csSize = soap.getPropertyCount();
			
			for (int i = 0; i < csSize; i++) {
				SoapObject pii = (SoapObject)soap.getProperty(i);
				long id = Long.parseLong(pii.getProperty("groupTypeCode").toString());
				String groupTypeName = pii.getProperty("groupTypeName").toString();
				int mandatory = Integer.parseInt(pii.getProperty("mandatory").toString());
				int multiple = Integer.parseInt(pii.getProperty("multiple").toString());
				long openTime = Long.parseLong(pii.getProperty("openTime").toString());
				GroupType g = new GroupType(id,groupTypeName,courseCode,mandatory,multiple,openTime);
				
				groupsSWAD.add(g);
				
				if(isDebuggable){
					Log.i(TAG, g.toString());
	    		}
			}
	
			//TODO remove obsolete groups
			for(int i = 0; i < groupsSWAD.size(); ++i){
				GroupType g = (GroupType) groupsSWAD.get(i);
				//boolean isAdded = dbHelper.insertGroup(g,Global.getSelectedCourseCode());
				//if(!isAdded){
				if(!dbHelper.insertGroupType(g)){
					setResult(RESULT_CANCELED);
				}
			}
			//Request finalized without errors
			setResult(RESULT_OK);
		}
	}
	

	@Override
	protected void connect() {
		String progressDescription = getString(R.string.groupTypesProgressDescription);
		int progressTitle = R.string.groupTypesProgressTitle;

		new Connect(true, progressDescription, progressTitle).execute();
	}

	@Override
	protected void postConnect() {
		finish();
	}

	@Override
	protected void onError() {
		// TODO Auto-generated method stub

	}

}
