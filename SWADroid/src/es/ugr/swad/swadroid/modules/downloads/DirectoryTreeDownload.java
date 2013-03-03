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

import android.content.Intent;
import android.os.Bundle;
import es.ugr.swad.swadroid.Global;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.model.User;
import es.ugr.swad.swadroid.modules.Module;

/**
 * DirectoryTreeDownload  gets directory tree of files of general documents of a course/group
 * or documents from shared area of a course/group
 * @author Helena Rodriguez Gijon <hrgijon@gmail.com>
 * */

public class DirectoryTreeDownload extends Module {
	private int treeCode; //documents of course or common zone of course
	private int group = 0; // documents of the course
	//TODO esta clase tiene q cambiar
	public class DirectoryTree{
		String tree;
	}

	@Override
	protected void requestService() throws NoSuchAlgorithmException,
	IOException, XmlPullParserException, SoapFault,
	IllegalAccessException, InstantiationException {
		createRequest();
		addParam("wsKey", Global.getLoggedUser().getWsKey());
		addParam("courseCode", (int)Global.getSelectedCourseCode());
		addParam("groupCode", group);
		addParam("treeCode", treeCode);
		sendRequest(User.class,true);
		if(result!=null){
			SoapObject soapObject = (SoapObject) result;
			String tree = soapObject.getProperty("tree").toString();
			
			Intent resultIntent = new Intent();
			resultIntent.putExtra("tree", tree);
			setResult(RESULT_OK, resultIntent);
		    }else{
		      setResult(RESULT_CANCELED);
		}
	}

	@Override
	protected void connect() {
		String progessDescription= getString(R.string.documentsDownloadProgressDescription);
		int progressTitle = R.string.documentsDownloadModuleLabel;
		Connect con = new Connect(true,progessDescription,progressTitle,true);
		con.execute();

	}

	@Override
	protected void postConnect() {
		finish();

	}

	@Override
	protected void onError() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setMETHOD_NAME("getDirectoryTree");
	}

	@Override
	protected void onStart() {
		super.onStart();
		treeCode = getIntent().getIntExtra("treeCode", Global.DOCUMENTS_AREA_CODE); 
		group = getIntent().getIntExtra("groupCode", 0);
		runConnection();
		if(!isConnected){
			setResult(RESULT_CANCELED);
			finish();
		}
	}

}
