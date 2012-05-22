package es.ugr.swad.swadroid.modules.downloads;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParserException;

import android.os.Bundle;
import android.util.Log;

import es.ugr.swad.swadroid.Global;
import es.ugr.swad.swadroid.modules.Module;


/**
 * NotifyDownload notifies to SWAD when a download of a file takes place
 * Params:
 * 	- wskey: code to identify the current session
 *  - fileCode: code to identify the downloaded file
 * Returns: Nothing 
 * @author Helena Rodríguez Gijón <hrgijon@gmail.com>
 * */

public class NotifyDownload extends Module {

	private int fileCode = -1;
	
	@Override
	protected void requestService() throws NoSuchAlgorithmException,
			IOException, XmlPullParserException, SoapFault,
			IllegalAccessException, InstantiationException {
		createRequest();
		addParam("wsKey", Global.getLoggedUser().getWsKey());
		sendRequest(Integer.class,true);
		
		//TODO podríamos utilizar una confirmación para en caso de negativa, notificar de nuevo.
		/*if(result != null){
			SoapObject soapObject = (SoapObject) result;
			Integer confirmation = (Integer) soapObject.getProperty("confirmation");
			setResult(RESULT_OK, this.getIntent());
		}*/
		setResult(RESULT_OK, this.getIntent());
	}

	@Override
	protected void connect() {
		//this web services does not need to show any progress dialog
		Connect con = new Connect(false,"",0);
		con.execute();
	}

	@Override
	protected void postConnect() {
		Log.i(TAG, "Download notified");	
		finish();
	}

	@Override
	protected void onError() {
		// TODO Auto-generated method stub

	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setMETHOD_NAME("notifyDownload");
	}
	@Override
	protected void onStart() {
		super.onStart();
		fileCode = getIntent().getIntExtra("fileCode", -1); 
		runConnection();
		if(!isConnected){
			setResult(RESULT_CANCELED);
			finish();
		}
	}

}
