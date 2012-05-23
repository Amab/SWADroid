package es.ugr.swad.swadroid.modules;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParserException;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import es.ugr.swad.swadroid.Global;
import es.ugr.swad.swadroid.model.Course;
import es.ugr.swad.swadroid.model.Group;
import es.ugr.swad.swadroid.model.Model;

/**
 * Groups module gets user's groups inside the current course
 * and stores them in the database
 * @author Helena Rodríguez Gijón <hrgijon@gmail.com>
 */
public class Groups extends Module {


	@Override
	protected void requestService() throws NoSuchAlgorithmException,
			IOException, XmlPullParserException, SoapFault,
			IllegalAccessException, InstantiationException {
		createRequest();
		addParam("wsKey", Global.getLoggedUser().getWsKey());
		addParam("courseCode", (int)Global.getSelectedCourseCode());
		sendRequest(Group.class,false);
		
		if(result != null){
			
			//Stores groups data returned by webservice response
			List<Model> groupsSWAD = new ArrayList<Model>();
			
			
			Vector<?> res = (Vector <?>) result;
			SoapObject soap = (SoapObject) res.get(1);	
			int csSize = soap.getPropertyCount();
			
			
			for (int i = 0; i < csSize; i++) {
				SoapObject pii = (SoapObject)soap.getProperty(i);
				long id = Long.parseLong(pii.getProperty("groupCode").toString());
				String groupName = pii.getProperty("groupName").toString();
				int groupTypeCode = Integer.parseInt(pii.getProperty("groupTypeCode").toString());
				String groupTypeName = pii.getProperty("groupTypeName").toString();
				Group g = new Group(id,groupName,groupTypeCode,groupTypeName);
				
				groupsSWAD.add(g);
				
				if(isDebuggable){
					Log.i(TAG, g.toString());
        		}
			}

			//TODO remove obsolete groups
			for(int i = 0; i < groupsSWAD.size(); ++i){
				Group g = (Group) groupsSWAD.get(i);
				//boolean isAdded = dbHelper.insertGroup(g,Global.getSelectedCourseCode());
				//if(!isAdded){
				if(!dbHelper.insertGroup(g,Global.getSelectedCourseCode())){
					Log.i(TAG, "group to update");
					dbHelper.updateGroup(g.getId(), Global.getSelectedCourseCode(), g);
					Log.i(TAG, "group updated");
				}
			}
			//Request finalized without errors
			setResult(RESULT_OK);
		}
		
		
		
		
	}

	@Override
	protected void connect() {
		 Toast.makeText(this,"Getting Groups", Toast.LENGTH_LONG).show();
		Connect con = new Connect(false,null,0);
		con.execute();
	}

	@Override
	protected void postConnect() {
		Toast.makeText(this, "got groups", Toast.LENGTH_LONG).show();
		Log.i(TAG, "got groups");
		finish();
	}

	@Override
	protected void onError() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setMETHOD_NAME("getGroups");
	}

	@Override
	protected void onStart() {
		super.onStart();
		runConnection();
		if(!isConnected){
			setResult(RESULT_CANCELED);
			finish();
		}
		
	}

}
