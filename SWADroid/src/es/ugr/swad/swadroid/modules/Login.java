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
import android.widget.Toast;
import es.ugr.swad.swadroid.Global;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.model.User;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.kobjects.base64.Base64;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.KvmSerializable;
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
     * Connection available flag
     */
    private boolean isConnected;

    /* (non-Javadoc)
	 * @see android.app.Activity#onCreate()
	 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setMETHOD_NAME("loginByUserPasswordKey");        
        connect();
    }

    /**
     * Launches action in a separate thread while shows a progress dialog
     * in UI thread.
     */
    protected void connect() {
    	String progressDescription = getString(R.string.loginProgressDescription);
    	int progressTitle = R.string.loginProgressTitle;
    	Toast.makeText(this, progressDescription, Toast.LENGTH_LONG).show();
    	
        new Connect(false, progressDescription, progressTitle).execute();
    }

    /**
     * Connects to SWAD and gets user data.
     * @throws NoSuchAlgorithmException
     * @throws IOException
     * @throws XmlPullParserException
     * @throws SoapFault
     * @throws InstantiationException 
     * @throws IllegalAccessException 
     */
    protected void requestService()
            throws NoSuchAlgorithmException, IOException, XmlPullParserException, SoapFault, IllegalAccessException, InstantiationException {

        //Encrypts user password with SHA-512 and encodes it to Base64UrlSafe   	
        md = MessageDigest.getInstance("SHA-512");
        md.update(prefs.getUserPassword().getBytes());
        //userPassword = new String(Base64.encode(md.digest(), Base64.URL_SAFE + Base64.NO_PADDING + Base64.NO_WRAP));
        userPassword = new String(Base64.encode(md.digest()));
        userPassword = userPassword.replace('+','-').replace('/','_').replace('=', ' ').replaceAll("\\s+", "").trim();

        //Creates webservice request, adds required params and sends request to webservice
        createRequest();
        addParam("userID", prefs.getUserID());
        addParam("userPassword", userPassword);
        addParam("appKey", Global.getAppKey());
        sendRequest(User.class, true);

        if (result != null) {
        	KvmSerializable ks = (KvmSerializable) result;
        	
	        //Stores user data returned by webservice response
	        User.setUserCode(ks.getProperty(0).toString());
	        User.setUserTypeCode(ks.getProperty(1).toString());
	        User.setWsKey(ks.getProperty(2).toString());
	        User.setUserID(ks.getProperty(3).toString());
	        User.setUserSurname1(ks.getProperty(4).toString());
	        User.setUserSurname2(ks.getProperty(5).toString());
	        User.setUserFirstName(ks.getProperty(6).toString());
	        User.setUserTypeName(ks.getProperty(7).toString());
	        
	        //Request finalized without errors
	        setResult(RESULT_OK);
        }
    	
        finish();
	}
}
