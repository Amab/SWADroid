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

package es.ugr.swad.swadroid.modules.login;

import android.os.Bundle;
import android.util.Log;

import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;

import es.ugr.swad.swadroid.Config;
import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.model.LoginInfo;
import es.ugr.swad.swadroid.model.User;
import es.ugr.swad.swadroid.modules.Module;
import es.ugr.swad.swadroid.preferences.Preferences;
import es.ugr.swad.swadroid.utils.Utils;
import es.ugr.swad.swadroid.webservices.SOAPClient;

/**
 * Login module for connect to SWAD.
 * @see <a href="https://openswad.org/ws/#loginByUserPasswordKey">loginByUserPasswordKey</a>
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 * @author Antonio Aguilera Malagon <aguilerin@gmail.com>
 */
public class Login extends Module {
    /**
     * Login tag name for Logcat
     */
    private static final String TAG = Constants.APP_TAG + " Login";
    /**
	 * Time to force relogin
	 */
	public static final int RELOGIN_TIME = 86400000; //24h
    /**
     * Login data
     */
    private static LoginInfo loginInfo;

    //Initialize login data
    static {
        loginInfo = Preferences.getLoginInfo();
        if(loginInfo == null) {
            loginInfo = new LoginInfo();
        }
    }

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
        startConnection();
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
        if (System.currentTimeMillis() - loginInfo.getLastLoginTime() > Login.RELOGIN_TIME) {
            loginInfo.setLogged(false);
        }

        //If the application isn't logged, force login
        if (!loginInfo.isLogged()) {
            String userID = Preferences.getUserID();

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
            addParam("appKey", Config.SWAD_APP_KEY);
            sendRequest(User.class, true);

            if (result != null) {
                SoapObject soap = (SoapObject) result;

                //Stores user data returned by webservice response
                User user = new User(
                        Long.parseLong(soap.getProperty("userCode").toString()),        // userCode
                        soap.getProperty("wsKey").toString(),                           // wsKey
                        soap.getProperty("userID").toString(),                          // userID
                        soap.getProperty("userNickname").toString(),                    // userNickname
                        soap.getProperty("userSurname1").toString(),                    // userSurname1
                        soap.getProperty("userSurname2").toString(),                    // userSurname2
                        soap.getProperty("userFirstname").toString(),                   // userFirstname
                        soap.getProperty("userPhoto").toString(),                       // photoPath
                        soap.getProperty("userBirthday").toString(),                    // userBirthday
                        Integer.parseInt(soap.getProperty("userRole").toString())       // userRole
                );

                loginInfo.setLogged(true);
                loginInfo.setLoggedUser(user);

                //Update application last login time
                loginInfo.setLastLoginTime(System.currentTimeMillis());

        		if(isDebuggable) {
        			Log.d(TAG, "id=" + user.getId());
        			Log.d(TAG, "wsKey=" + user.getWsKey());
        			Log.d(TAG, "userID=" + user.getUserID());
        			Log.d(TAG, "userNickname=" + user.getUserNickname());
        			Log.d(TAG, "userSurname1=" + user.getUserSurname1());
        			Log.d(TAG, "userSurname2=" + user.getUserSurname2());
        			Log.d(TAG, "userFirstName=" + user.getUserFirstname());
        			Log.d(TAG, "userPhoto=" + user.getUserPhoto());
                    Log.d(TAG, "userBirthday=" + ((user.getUserBirthday() != null) ? user.getUserBirthday().getTime(): "null"));
        			Log.d(TAG, "userRole=" + user.getUserRole());
        			Log.d(TAG, "isLogged=" + loginInfo.isLogged());
        			Log.d(TAG, "lastLoginTime=" + loginInfo.getLastLoginTime());
        		}
            }
        }

        //Save login data
        setLoginInfo(loginInfo);

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

    /**
     * Gets the login data
     *
     * @return The login data
     */
    public static LoginInfo getLoginInfo() {
        return loginInfo;
    }

    /**
     * Sets the login data
     *
     * @param loginInfo The login data
     */
    public static void setLoginInfo(LoginInfo loginInfo) {
        Login.loginInfo = loginInfo;
        Preferences.setLoginInfo(loginInfo);

        Log.i(TAG, "LoginInfo saved: " + loginInfo.toString());
    }



    /**
     * Checks if user is already logged on SWAD
     *
     * @return User logged flag
     */
    public static boolean isLogged() {
        return loginInfo.isLogged();
    }

    /**
     * Sets user logged flag
     *
     * @param logged User logged flag
     */
    public static void setLogged(boolean logged) {
        loginInfo.setLogged(logged);
    }

    /**
     * Gets the user logged on SWAD
     */
    public static User getLoggedUser() {
        return loginInfo.getLoggedUser();
    }

    /**
     * Sets the user logged on SWAD
     */
    public static void setLoggedUser(User loggedUser) {
        loginInfo.setLoggedUser(loggedUser);
    }

    /**
     * Gets the last synchronization time
     *
     * @return The last synchronization time
     */
    public static long getLastLoginTime() {
        return loginInfo.getLastLoginTime();
    }

    /**
     * Sets the last synchronization time
     *
     * @param lastLoginTime The last synchronization time
     */
    public static void setLastLoginTime(long lastLoginTime) {
        loginInfo.setLastLoginTime(lastLoginTime);
    }

    /**
     * Gets the role of the logged user in the current selected course
     *
     * @return -1 if the user role has not been fixed,
     *         0  if the user role is unknown
     *         2 (STUDENT_TYPE_CODE) if the user is a student
     *         3 (TEACHER_TYPE_CODE) if the user is a teacher
     */
    public static int getCurrentUserRole() {
        return loginInfo.getCurrentUserRole();
    }

    /**
     * Sets user role in the current selected course
     *
     * @param userRole Role of the user: 0- unknown STUDENT_TYPE_CODE - student TEACHER_TYPE_CODE - teacher
     */
    public static void setCurrentUserRole(int userRole) {
        loginInfo.setCurrentUserRole(userRole);
    }
}
