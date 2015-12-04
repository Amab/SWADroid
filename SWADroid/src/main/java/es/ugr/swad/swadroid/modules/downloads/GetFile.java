/*
 *  This file is part of SWADroid.
 *
 *  Copyright (C) 2010 Juan Miguel Boyero Corral <juanmi1982@gmail.com>
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

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.ksoap2.serialization.SoapPrimitive;

import java.util.ArrayList;
import java.util.Vector;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.analytics.SWADroidTracker;
import es.ugr.swad.swadroid.model.Group;
import es.ugr.swad.swadroid.modules.login.Login;
import es.ugr.swad.swadroid.modules.Module;
import es.ugr.swad.swadroid.webservices.SOAPClient;

/**
 * Module to get information of a file located in SWAD
 * It makes use of the web service getFile (see {@linktourl https://openswad.org/ws/#getFile})
 * 
 * @param fileCode It indicates the file which information is requested
 * @return link temporal URL to download the file
 *
 * @author Helena Rodriguez Gijon <hrgijon@gmail.com>
 */


public class GetFile extends Module {
    /**
     * GetFile tag name for Logcat
     */
    private static final String TAG = Constants.APP_TAG + " GetFile";

    /**
     * Unique identificator of file
     */
    private long fileCode = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        fileCode = getIntent().getLongExtra("fileCode", -1);

        if (fileCode == -1) {
            Log.i(TAG, "Missing arguments");
            finish();
        }

        setMETHOD_NAME("getFile");
    }

    @Override
    protected void onStart() {
        super.onStart();

        SWADroidTracker.sendScreenView(getApplicationContext(), TAG);
        
        try {
            runConnection();
        } catch (Exception e) {
            String errorMsg = getString(R.string.errorServerResponseMsg);
            error(TAG, errorMsg, e, true);
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
    protected void requestService() throws Exception {
    	
    	createRequest(SOAPClient.CLIENT_TYPE);
        addParam("wsKey", Login.getLoggedUser().getWsKey());
        addParam("fileCode", (int) fileCode);
        sendRequest(Group.class, false);
        
        if (result != null) {
            ArrayList<?> res = new ArrayList<Object>((Vector<?>) result);
            SoapPrimitive soapP = (SoapPrimitive) res.get(1);
            String link = soapP.toString();

            Intent resultIntent = new Intent();
            resultIntent.putExtra("link", link);
            setResult(RESULT_OK, resultIntent);
        } else {
            setResult(RESULT_CANCELED);
        }

    }

    @Override
    protected void connect() {
        startConnection(false, "", 0);
    }

    @Override
    protected void postConnect() {
        if (isDebuggable)
            Log.i(TAG, "File requested");
        finish();

    }

    @Override
    protected void onError() {

    }

}
