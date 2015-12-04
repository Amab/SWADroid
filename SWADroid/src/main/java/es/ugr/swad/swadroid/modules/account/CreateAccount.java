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

package es.ugr.swad.swadroid.modules.account;

import android.os.Bundle;

import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.model.User;
import es.ugr.swad.swadroid.modules.Module;
import es.ugr.swad.swadroid.webservices.SOAPClient;

/**
 * Module for create a new account
 * It makes use of the web service createAccount (see {@linktourl https://openswad.org/ws/#createAccount})
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class CreateAccount extends Module {
    /**
     * Login tag name for Logcat
     */
    public static final String TAG = Constants.APP_TAG + " CreateAccount";

    /**
     * User code returned by webservice
     */
    private static Long userCode;
    /**
     * Session key returned by webservice
     */
    private static String wsKey;
    /**
     * User nickname
     */
    private String userNickname;
    /**
     * User email
     */
    private String userEmail;
    /**
     * User password
     */
    private String userPassword;

    /* (non-Javadoc)
     * @see android.app.Activity#onCreate()
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setMETHOD_NAME("createAccount");
        getSupportActionBar().hide();
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onStart()
     */
    @Override
    protected void onStart() {
        super.onStart();

        userNickname = getIntent().getStringExtra("userNickname");
        userEmail = getIntent().getStringExtra("userEmail");
        userPassword = getIntent().getStringExtra("userPassword");
        
        connect();
    }

    /**
     * Launches action in a separate thread while shows a progress dialog
     * in UI thread.
     */
    protected void connect() {
        String progressDescription = getString(R.string.createAccountProgressDescription);
        int progressTitle = R.string.createAccountModuleLabel;

        startConnection(false, progressDescription, progressTitle);
    }

    /**
     * Connects to SWAD and gets user data.
     * @throws Exception 
     *
     * @throws SoapFault
     */
    protected void requestService()
            throws Exception {

        //Creates webservice request, adds required params and sends request to webservice
        createRequest(SOAPClient.CLIENT_TYPE);
        addParam("userNickname", userNickname);
        addParam("userEmail", userEmail);
        addParam("userPassword", userPassword);
        addParam("appKey", Constants.SWAD_APP_KEY);
        sendRequest(User.class, true);

        if (result != null) {
            SoapObject soap = (SoapObject) result;

            //Stores user data returned by webservice response
            userCode = Long.parseLong(soap.getProperty("userCode").toString());
            wsKey = soap.getProperty("wsKey").toString();
        }

        //Request finalized without errors
        setResult(RESULT_OK);
    }

    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.modules.Module#postConnect()
     */
    @Override
    protected void postConnect() {
        finish();
    }

    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.modules.Module#onError()
     */
    @Override
    protected void onError() {

    }

    public static Long getUserCode() {
        return userCode;
    }

    public static String getWsKey() {
        return wsKey;
    }
}
