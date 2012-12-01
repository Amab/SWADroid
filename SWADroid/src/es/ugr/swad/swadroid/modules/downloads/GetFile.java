package es.ugr.swad.swadroid.modules.downloads;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Vector;

import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapPrimitive;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import es.ugr.swad.swadroid.Global;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.model.Group;
import es.ugr.swad.swadroid.modules.Module;
/**
 * Module to get information of a file located in SWAD 
 * It makes use of the web service getFile (see http://swad.ugr.es/ws/#getFile)
 *  It needs as extra data:
 * - (long) courseCode course code . It indicates the course in which the file is stored
 * - (long) groupCode group code. It indicates the group in which the file is stored. If the file is not stored in a group but in the directory of the whole course, this value will be 0.  
 * - (int) treeCode: Value that indicates if the files belongs to the Documents directory (1) or to the Shared Area directory (2).
 * - (String) path: String that contains the full path, including the name, of the file. 
 * It returns as extra data:
 * - (string) name : name of the file 
 * - (string) link : temporal URL to download the file
 * - (int) size : size in bytes
 * - (
 *  @author Helena Rodriguez Gijon <hrgijon@gmail.com>
 * */


public class GetFile extends Module {
	/**
	 * GetFile tag name for Logcat
	 */
	public static final String TAG = Global.APP_TAG + "GetFile";
	/**
	 * Unique identificator of file
	 * */
	private long fileCode = -1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fileCode = getIntent().getLongExtra("fileCode", -1);
		
		if(fileCode == -1){
			Log.i(TAG, "Missing arguments");
			finish();
		}
		
		setMETHOD_NAME("getFile");
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
		addParam("fileCode", fileCode);
		sendRequest(Group.class,false); //TODO this is not the correct class to map. nothing will be the correct
		if(result != null){
			Vector<?> res = (Vector <?>) result;
			SoapPrimitive soapP = (SoapPrimitive) res.get(0);
			String link = soapP.toString();
			Intent resultIntent = new Intent();
			resultIntent.putExtra("link", link);
			setResult(RESULT_OK,resultIntent);
		}else{
			setResult(RESULT_CANCELED);
		}
		
	}

	@Override
	protected void connect() {
		new Connect(false, "", 0).execute();
	}

	@Override
	protected void postConnect() {
		if(isDebuggable)
			Log.i(TAG, "Enrollment requested");
		finish();

	}

	@Override
	protected void onError() {
		// TODO Auto-generated method stub

	}

}
