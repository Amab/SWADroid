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

package es.ugr.swad.swadroid.modules;

import android.os.Bundle;
import android.util.Log;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.Preferences;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.model.User;
import es.ugr.swad.swadroid.utils.Utils;
import es.ugr.swad.swadroid.webservices.SOAPClient;

import org.ksoap2.SoapFault;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.SoapObject;

/**
 * Login module for connect to SWAD.
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 * @author Antonio Aguilera Malagon <aguilerin@gmail.com>
 */
public class Login extends Module {
    /**
     * Logged user
     */
    private User loggedUser;
    /**
     * Digest for user password.
     */
    //private MessageDigest md;
    /**
     * User ID.
     */
    private String userID;
    /**
     * User password.
     */
    //private String userPassword;
    /**
     * Login tag name for Logcat
     */
    public static final String TAG = Constants.APP_TAG + " Login";

    /* (non-Javadoc)
     * @see android.app.Activity#onCreate()
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setMETHOD_NAME("loginByUserPasswordKey");
        getSupportActionBar().hide();
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onStart()
     */
    @Override
    protected void onStart() {
        super.onStart();
        
        connect();
    }

    /**
     * Launches action in a separate thread while shows a progress dialog
     * in UI thread.
     */
    protected void connect() {
        String progressDescription = getString(R.string.loginProgressDescription);
        int progressTitle = R.string.loginProgressTitle;

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

        //If last login time > Global.RELOGIN_TIME, force login
        if (System.currentTimeMillis() - Constants.getLastLoginTime() > Constants.RELOGIN_TIME) {
            Constants.setLogged(false);
        }

        //If the application isn't logged, force login
        if (!Constants.isLogged()) {
            userID = Preferences.getUserID();

            //If the user ID is a DNI
            if (Utils.isValidDni(userID)) {
                //If the DNI has no letter, remove left zeros
                if (Utils.isInteger(userID)) {
                    userID = String.valueOf(Integer.parseInt(userID));

                //If the last position of the DNI is a char, remove it
                } else if (Utils.isInteger(userID.substring(0, userID.length() - 1))) {
                    userID = String.valueOf(Integer.parseInt(userID.substring(0, userID.length() - 1)));
                }
            }

            //Creates webservice request, adds required params and sends request to webservice
            createRequest(SOAPClient.CLIENT_TYPE);
            addParam("userID", userID);
            addParam("userPassword", Preferences.getUserPassword());
            addParam("appKey", Constants.SWAD_APP_KEY);
            sendRequest(User.class, true);

            if (result != null) {
                KvmSerializable ks = (KvmSerializable) result;
                SoapObject soap = (SoapObject) result;

                //Stores user data returned by webservice response
                loggedUser = new User(
                        Long.parseLong(ks.getProperty(0).toString()),                    // id
                        soap.getProperty("wsKey").toString(),                            // wsKey
                        soap.getProperty("userID").toString(),                            // userID
                        //soap.getProperty("userNickname").toString(),					// userNickname
                        null,                                                            // userNickname
                        soap.getProperty("userSurname1").toString(),                    // userSurname1
                        soap.getProperty("userSurname2").toString(),                    // userSurname2
                        soap.getProperty("userFirstname").toString(),                    // userFirstname
                        soap.getProperty("userPhoto").toString(),                        // photoPath
                        Integer.parseInt(soap.getProperty("userRole").toString())        // userRole
                );

                Constants.setLogged(true);
                Constants.setLoggedUser(loggedUser);

                //Update application last login time
                Constants.setLastLoginTime(System.currentTimeMillis());

        		if(isDebuggable) {
        			Log.d(TAG, "id=" + loggedUser.getId());
        			Log.d(TAG, "wsKey=" + loggedUser.getWsKey());
        			Log.d(TAG, "userID=" + loggedUser.getUserID());
        			Log.d(TAG, "userNickname=" + loggedUser.getUserNickname());
        			Log.d(TAG, "userSurname1=" + loggedUser.getUserSurname1());
        			Log.d(TAG, "userSurname2=" + loggedUser.getUserSurname2());
        			Log.d(TAG, "userFirstName=" + loggedUser.getUserFirstname());
        			Log.d(TAG, "userPhoto=" + loggedUser.getUserPhoto());
        			Log.d(TAG, "userRole=" + loggedUser.getUserRole());
        			Log.d(TAG, "isLogged=" + Constants.isLogged());
        			Log.d(TAG, "lastLoginTime=" + Constants.getLastLoginTime());
        		}
            }
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
}
