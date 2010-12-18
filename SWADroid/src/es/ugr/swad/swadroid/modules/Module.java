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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import es.ugr.swad.swadroid.Global;
import es.ugr.swad.swadroid.Preferences;
import es.ugr.swad.swadroid.R;
import java.io.IOException;
import java.util.ArrayList;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

/**
 * Superclass for encapsulate common behavior of all modules.
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class Module extends Activity {
    /**
     * SOAP_ACTION param for webservice request.
     */
    String SOAP_ACTION = "";
    /**
     * METHOD_NAME param for webservice request.
     */
    String METHOD_NAME = "";
    /**
     * NAMESPACE param for webservice request.
     */
    String NAMESPACE = "urn:swad";
    /**
     * URL param for webservice request.
     */
    String URL = "https://swad.ugr.es/";
    /**
     * Preferences of the activity.
     */
    Preferences prefs = new Preferences();
    /**
     * Webservice request.
     */
    SoapObject request;
    /**
     * Webservice result.
     */
    ArrayList<Object> result;
    /**
     * Shows error messages.
     */
    AlertDialog errorDialog = null;

    /**
     * Gets METHOD_NAME parameter.
     * @return METHOD_NAME parameter.
     */
    public String getMETHOD_NAME() {
        return METHOD_NAME;
    }

    /**
     * Sets METHOD_NAME parameter.
     * @param METHOD_NAME METHOD_NAME parameter.
     */
    public void setMETHOD_NAME(String METHOD_NAME) {
        this.METHOD_NAME = METHOD_NAME;
    }

    /**
     * Gets NAMESPACE parameter.
     * @return NAMESPACE parameter.
     */
    public String getNAMESPACE() {
        return NAMESPACE;
    }

    /**
     * Sets NAMESPACE parameter.
     * @param NAMESPACE NAMESPACE parameter.
     */
    public void setNAMESPACE(String NAMESPACE) {
        this.NAMESPACE = NAMESPACE;
    }

    /**
     * Gets SOAP_ACTION parameter.
     * @return SOAP_ACTION parameter.
     */
    public String getSOAP_ACTION() {
        return SOAP_ACTION;
    }

    /**
     * Sets SOAP_ACTION parameter.
     * @param SOAP_ACTION SOAP_ACTION parameter.
     */
    public void setSOAP_ACTION(String SOAP_ACTION) {
        this.SOAP_ACTION = SOAP_ACTION;
    }

    /**
     * Gets URL parameter.
     * @return URL parameter.
     */
    public String getURL() {
        return URL;
    }

    /**
     * Sets URL parameter.
     * @param URL URL parameter.
     */
    public void setURL(String URL) {
        this.URL = URL;
    }

    /**
     * Gets preferences of activity.
     * @return Preferences of activity.
     */
    public Preferences getPrefs() {
        return prefs;
    }

    /**
     * Sets preferences of activity.
     * @param prefs Preferences of activity.
     */
    public void setPrefs(Preferences prefs) {
        this.prefs = prefs;
    }

    /**
     * Gets webservice request.
     * @return Webservice request.
     */
    public SoapObject getRequest() {
        return request;
    }

    /**
     * Sets webservice request.
     * @param request Webservice request.
     */
    public void setRequest(SoapObject request) {
        this.request = request;
    }

    /**
     * Gets webservice result.
     * @return Webservice result.
     */
    public ArrayList<Object> getResult() {
        return result;
    }

    /**
     * Sets webservice result.
     * @param result Webservice result.
     */
    public void setResult(ArrayList<Object> result) {
        this.result = result;
    }
    
    /**
     * Called when activity is first created.
     * @param savedInstanceState State of activity.
     */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        prefs.getPreferences(getBaseContext());
        
        //If not logged and this is not the Login module, launch login
        if(!Global.isLogged() && !(this instanceof Login)) {
        	Intent loginActivity = new Intent(getBaseContext(),
                    Login.class);
            startActivityForResult(loginActivity, Global.LOGIN_REQUEST_CODE);
        }
	}
	
	/**
     * Handles the result of launch an activity and performs an action.
     * @param requestCode Identifier of action requested.
     * @param resultCode Status of activity's result (correct or not).
     * @param data Data returned by launched activity.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            //Bundle extras = data.getExtras();

            switch(requestCode) {
                case Global.LOGIN_REQUEST_CODE:
                     Global.setLogged(true);
                     break;
            }
        }
    }

	/**
     * Creates webservice request.
     */
	protected void createRequest() {
        request = new SoapObject(NAMESPACE, METHOD_NAME);
        result = new ArrayList<Object>();
    }

    /**
     * Adds a parameter to webservice request.
     * @param param Parameter name.
     * @param value Parameter value.
     */
    protected void addParam(String param, Object value) {
        request.addProperty(param, value);
    }

    /**
     * Sends request to webservice.
     * @throws IOException
     * @throws XmlPullParserException
     * @throws SoapFault
     */
    protected void sendRequest() throws IOException, XmlPullParserException, SoapFault {
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
        
        //If an XmlPullParserException occurs, retry once in order to workaround an Android emulator bug
        try {
        	androidHttpTransport.call(SOAP_ACTION, envelope);
        } catch(XmlPullParserException ex) {
        	Log.e(Global.MODULE_TAG, ex.getLocalizedMessage());
        	ex.printStackTrace();
        	Log.e(Global.MODULE_TAG, getString(R.string.errorMsgWorkaroundEmulator));
        	androidHttpTransport.call(SOAP_ACTION, envelope);
        }
        
        KvmSerializable ks = (KvmSerializable)envelope.bodyIn;
        for(int i=0;i<ks.getPropertyCount();i++)
        {
           result.add(ks.getProperty(i)); //if complex type is present then you can cast this to SoapObject and if primitive type is returned you can use toString() to get actual value.
        }
    }

    /**
     * Shows an error message.
     * @param message Error message to show.
     */
    protected void error(String message) {
        errorDialog = new AlertDialog
                .Builder(this)
                .setTitle(R.string.title_error_dialog)
                .setMessage(message)
                .setNeutralButton(R.string.close_dialog,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Module.this.finish();
                    }
                })
                .setIcon(R.drawable.erroricon).show();
                
    }

    /**
     * Called when activity is paused.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if(errorDialog != null) {
            errorDialog.dismiss();
        }
    }
}
