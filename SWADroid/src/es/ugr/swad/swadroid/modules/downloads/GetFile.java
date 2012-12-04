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
	 * Course code 
	 * */
	private long courseCode = -1;
	/**
	 * Group code 
	 * */
	private long groupCode = -1;
	/**
	 * Tree code 
	 * */
	private int treeCode = -1;
	/**
	 * Complete path to the file
	 * */
	private String path = null;
	/**
	 * Unique identificator of file
	 * */
	private long fileCode = -1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		courseCode = getIntent().getLongExtra("courseCode", -1);
		groupCode = getIntent().getLongExtra("groupCode",-1);
		treeCode = getIntent().getIntExtra("treeCode", -1);
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
//		addParam("courseCode", (int)courseCode);
//		addParam("groupCode", (int)groupCode);
//		addParam("treeCode", (int)treeCode);
		addParam("fileCode", fileCode);
/*		Intent resultIntent = new Intent();
		resultIntent.putExtra("link", "https://swad.ugr.es/swad/tmp/beuIeebR9OA9ym3XB-2SYYxE1-fy_k1xVtx3E7YCGC0/suma_03_SC_c.c");
		setResult(RESULT_OK,resultIntent);*/
		sendRequest(Group.class,false); //TODO this is not the correct class to map. nothing will be the correct
		if(result != null){
			Vector<?> res = (Vector <?>) result;
			SoapPrimitive soapP = (SoapPrimitive) res.get(0);
/*			String name = soapP.toString();
			soapP = (SoapPrimitive) res.get(1);*/
			String link = soapP.toString();
/*			soapP = (SoapPrimitive) res.get(2);
			int size = Integer.parseInt(soapP.toString());
			soapP = (SoapPrimitive) res.get(3);
			long time = Long.parseLong(soapP.toString());
			soapP = (SoapPrimitive) res.get(4);
			String license = soapP.toString();
			soapP = (SoapPrimitive) res.get(5);
			String publischerName = soapP.toString();
			soapP = (SoapPrimitive) res.get(6);
			String publisherPhoto = soapP.toString();*/
			
			
			Intent resultIntent = new Intent();
			resultIntent.putExtra("link", link);
			//resultIntent.putExtra("link", "https://swad.ugr.es/swad/tmp/tUC8BWNM40Q7iQNBqwTln9ZOVB6P2oby5DdeJI1e-bQ/Guion%20Practica%202.pdf");
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
