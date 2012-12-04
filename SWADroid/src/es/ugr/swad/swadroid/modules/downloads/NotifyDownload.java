/*
 *  This file is part of SWADroid.
 *
 *  Copyright (C) 2012 Helena Rodriguez Gijon <hrgijon@gmail.com>
 *
 *  SWADroid is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  SWADroid is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with SWADroid.  If not, see <http://www.gnu.org/licenses/>.
 */
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
 * @author Helena Rodriguez Gijon <hrgijon@gmail.com>
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
