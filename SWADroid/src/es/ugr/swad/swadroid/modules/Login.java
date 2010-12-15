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

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import es.ugr.swad.swadroid.Global;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.model.User;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.ksoap2.SoapFault;
import org.xmlpull.v1.XmlPullParserException;

/**
 * Login module for connect to SWAD.
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class Login extends Module {
    /**
     * Digest for user password.
     */
    private MessageDigest md;
    /**
     * User password.
     */
    private String userPassword;

    /**
     * Called when activity is first created.
     * @param savedInstanceState State of activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setMETHOD_NAME("loginByUserPasswordKey");
        connect();
    }

    /**
     * Launches login action in a separate thread while shows a progress dialog
     * in UI thread.
     */
    private void connect() {
        new Connect().execute();
    }

    /**
     * Connects to SWAD and gets user data.
     * @throws NoSuchAlgorithmException
     * @throws IOException
     * @throws XmlPullParserException
     * @throws SoapFault
     */
    private void requestService()
            throws NoSuchAlgorithmException, IOException, XmlPullParserException, SoapFault {

        //Encrypts user password with SHA-512 and encodes it to Base64    	
        md = MessageDigest.getInstance("SHA-512");
        md.update(prefs.getUserPassword().getBytes());
        userPassword = new String(Base64.encode(md.digest(), Base64.URL_SAFE + Base64.NO_PADDING + Base64.NO_WRAP));

        //Creates webservice request, adds required params and sends request to webservice
        createRequest();
        addParam("userID", prefs.getUserID());
        addParam("userPassword", userPassword);
        addParam("appKey", Global.getAppKey());
        sendRequest();

        //Stores user data returned by webservice response
        User.setUserCode(result.get(0).toString());
        User.setUserTypeCode(result.get(1).toString());
        User.setWsKey(result.get(2).toString());
        User.setUserID(result.get(3).toString());
        User.setUserSurname1(result.get(4).toString());
        User.setUserSurname2(result.get(5).toString());
        User.setUserFirstName(result.get(6).toString());
        User.setUserTypeName(result.get(7).toString());

        //Request finalized without errors
        setResult(RESULT_OK);
    }

    /**
     * Shows progress dialog when connecting to SWAD
     */
    private class Connect extends AsyncTask<String, Void, Void> {
        /**
         * Progress dialog.
         */
        ProgressDialog Dialog = new ProgressDialog(Login.this);
        /**
         * Exception pointer.
         */
        Exception e = null;

        /**
         * Called before launch background thread.
         */
        @Override
        protected void onPreExecute() {
            Dialog.setMessage(getString(R.string.loginProgressDescription));
            Dialog.setTitle(R.string.loginProgressTitle);
            Dialog.show();
        }

        /**
         * Called in background thread.
         * @param urls Background thread parameters.
         * @return Nothing.
         */
        @Override
		protected Void doInBackground(String... urls) {
            try {
                //Sends webservice request
                requestService();
            /**
             * If an exception occurs, capture and points exception pointer
             * to it.
             */
            } catch (SoapFault ex) {
                e = ex;
            } catch (Exception ex) {
                e = ex;
            }

            return null;
        }

        /**
         * Called after calling background thread.
         * @param unused Does nothing.
         */
        @Override
        protected void onPostExecute(Void unused) {
            Dialog.dismiss();
            
            if(e != null) {
                /**
                 * If an exception has occurred, shows error message according to
                 * exception type.
                 */
                if(e instanceof SoapFault) {
                    SoapFault es = (SoapFault) e;
                    Log.e(es.getClass().getSimpleName(), es.faultstring);
                    error(es.faultstring);
                } else {
                    Log.e(e.getClass().getSimpleName(), e.toString());
                    error(e.toString());
                }

                //Request finalized with errors
                e.printStackTrace();
                setResult(RESULT_CANCELED);
            }
        }
    }
}
