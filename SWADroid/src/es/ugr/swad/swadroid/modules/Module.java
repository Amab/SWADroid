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
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapFault;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.gui.MenuActivity;
import es.ugr.swad.swadroid.utils.Utils;
import es.ugr.swad.swadroid.webservices.IWebserviceClient;
import es.ugr.swad.swadroid.webservices.RESTClient;
import es.ugr.swad.swadroid.webservices.SOAPClient;

/**
 * Superclass for encapsulate common behavior of all modules.
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public abstract class Module extends MenuActivity {
    /**
     * Class Module's tag name for Logcat
     */
    private static final String TAG = Constants.APP_TAG + " Module";
    /**
     * Async Task for background jobs
     */
    private Connect connect;
    /**
     * Client for SWAD webservices
     */
    private IWebserviceClient webserviceClient;
    /**
     * Webservice result.
     */
    protected Object result;
    /**
     * Shows error messages.
     */
    private AlertDialog errorDialog = null;
    /**
     * Progress dialog
     */
    private ProgressDialog progressDialog = null;
    /**
     * Flag for show the progress dialog
     */
    private final boolean showDialog = false;
    /**
     * Connection available flag
     */
    protected static boolean isConnected;
	/**
	 * METHOD_NAME param for webservice request.
	 */
	private String METHOD_NAME = "";

    /**
     * Connects to SWAD and gets user data.
     *
     * @throws NoSuchAlgorithmException
     * @throws IOException
     * @throws XmlPullParserException
     */
    protected abstract void requestService() throws NoSuchAlgorithmException,
            IOException, XmlPullParserException;

    /**
     * Launches action in a separate thread while shows a progress dialog in UI
     * thread.
     */
    protected abstract void connect();

    /**
     * Launches action after executing connect() method
     */
    protected abstract void postConnect();

    /**
     * Error handler
     */
    protected abstract void onError();

    /**
     * Run connection. Launch Login activity when required
     */
    protected void runConnection() {
        isConnected = Utils.connectionAvailable(this);
        if (!isConnected) {
            Toast.makeText(this, R.string.errorMsgNoConnection,
                    Toast.LENGTH_SHORT).show();
        } else {
            // If this is not the Login module, launch login check
            if (!(this instanceof Login)) {
                Intent loginActivity = new Intent(this, Login.class);
                startActivityForResult(loginActivity,
                        Constants.LOGIN_REQUEST_CODE);
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onCreate()
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Check if debug mode is enabled
        try {
            getPackageManager().getApplicationInfo(getPackageName(), 0);
			isDebuggable = (ApplicationInfo.FLAG_DEBUGGABLE != 0);
        } catch (NameNotFoundException e1) {
            e1.printStackTrace();
        }
        
        //Initialize webservices client
        webserviceClient = null;

        super.onCreate(savedInstanceState);

        // Recover the launched async task if the activity is re-created
        connect = (Connect) getLastNonConfigurationInstance();
        if (connect != null) {
            connect.activity = new WeakReference<Module>(this);
        }
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        return connect;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onPause()
     */
    @Override
    protected void onPause() {

        super.onPause();
        if (errorDialog != null) {
            errorDialog.dismiss();
        }

        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();

        if (showDialog) {
			if (progressDialog != null) {
			    progressDialog.show();
			}
		}
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onStart()
     */
    @Override
    protected void onStart() {
        super.onStart();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onActivityResult()
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Constants.LOGIN_REQUEST_CODE:
                    // Toast.makeText(getApplicationContext(), R.string.loginSuccessfulMsg,
                    // Toast.LENGTH_SHORT).show();

				/*
                 * if(isDebuggable) Log.d(TAG,
				 * getString(R.string.loginSuccessfulMsg));
				 */

                    if (!(this instanceof Login)) {
                        connect();
                    }

                    break;
            }
        } else {
            setResult(RESULT_CANCELED);
        }
    }

    /**
     * Sets METHOD_NAME parameter.
     *
     * @param METHOD_NAME METHOD_NAME parameter.
     */
    protected void setMETHOD_NAME(String METHOD_NAME) {
        this.METHOD_NAME = METHOD_NAME;
    }

    /**
     * Creates webservice request.
     */
    protected void createRequest(String clientType) {
    	if(webserviceClient == null) {
	    	if(clientType.equals(SOAPClient.CLIENT_TYPE)) {
	    		webserviceClient = new SOAPClient();
	    	} else if(clientType.equals(RESTClient.CLIENT_TYPE)) {
	    		webserviceClient = new RESTClient();    		
	    	}
	
	        webserviceClient.setMETHOD_NAME(METHOD_NAME);
    	}
    	
    	webserviceClient.createRequest();
    }

    /**
     * Adds a parameter to webservice request.
     *
     * @param param Parameter name.
     * @param value Parameter value.
     */
    protected void addParam(String param, Object value) {
    	webserviceClient.addParam(param, value);
    }

    /**
     * Sends a SOAP request to the specified webservice in METHOD_NAME class
     * constant of the webservice client.
     * 
     * @param cl     Class to be mapped
     * @param simple Flag for select simple or complex response
     * @throws XmlPullParserException 
     * @throws IOException 
     */
    protected void sendRequest(Class<?> cl, boolean simple) throws IOException, XmlPullParserException {
    	((SOAPClient) webserviceClient).sendRequest(cl, simple);
    	result = webserviceClient.getResult();
    }
    
    /** 
     * Sends a REST request to the specified webservice in METHOD_NAME class
     * constant of the webservice client.
     * 
     * @param cl     Class to be mapped
     * @param simple Flag for select simple or complex response
     * @param type Request type
     * @param json JSON object to be sended
     * @throws ClientProtocolException
     * @throws IOException
     * @throws JSONException
     */
    protected void sendRequest(Class<?> cl, boolean simple, RESTClient.REQUEST_TYPE type, JSONObject json) throws ClientProtocolException, IOException, JSONException {
    	((RESTClient) webserviceClient).sendRequest(cl, simple, RESTClient.REQUEST_TYPE.GET, json);
    	result = webserviceClient.getResult();
    }

    protected void startConnection(boolean showDialog,
                                   String progressDescription, int progressTitle) {

        connect = new Connect(this, showDialog, progressDescription, progressTitle);
        connect.execute();
    }

    /**
     * Shows progress dialog when connecting to SWAD
     */
    class Connect extends AsyncTask<String, Void, Void> {
        /**
         * Activity that launched the task
         */
        WeakReference<Module> activity;
        /**
         * Exception pointer
         */
        Exception e;
        final String progressDescription;
        final int progressTitle;
        final boolean showDialog;

        /**
         * Shows progress dialog and connects to SWAD in background
         *
         * @param progressDescription Description to be showed in dialog
         * @param progressTitle       Title to be showed in dialog
         * @param isLogin             Flag for detect if this is the login module
         */
        public Connect(Module activity, boolean show,
                       String progressDescription, int progressTitle) {

            super();
            this.progressDescription = progressDescription;
            this.progressTitle = progressTitle;
            showDialog = show;
            this.activity = new WeakReference<Module>(activity);
            this.e = null;
            progressDialog = new ProgressDialog(Module.this);
        }

        /*
         * (non-Javadoc)
         *
         * @see android.app.Activity#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            if (showDialog && (progressDialog != null)) {
                progressDialog.setMessage(progressDescription);
                progressDialog.setTitle(progressTitle);
                progressDialog.show();
            }
        }

        /*
         * (non-Javadoc)
         *
         * @see android.app.Activity#doInBackground()
         */
        @Override
        protected Void doInBackground(String... urls) {
            if (isDebuggable)
                Log.d(TAG, "doInBackground()");

            try {
                // Sends webservice request
                requestService();
                /**
                 * If an exception occurs, capture, points exception pointer to
                 * it and shows error message according to exception type.
                 */
            } catch (Exception ex) {
                e = ex;
            }

            return null;

        }

        /*
         * (non-Javadoc)
         *
         * @see android.app.Activity#onPostExecute()
         */
        @Override
        protected void onPostExecute(Void unused) {
            String errorMsg;
            boolean sendException = true;

            if ((progressDialog != null) && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            if (e != null) {
                /**
                 * If an exception has occurred, shows error message according
                 * to exception type.
                 */
                if (e instanceof SoapFault) {
                    SoapFault es = (SoapFault) e;

                    if (es.faultstring.equals("Bad log in")) {
                        errorMsg = getString(R.string.errorBadLoginMsg);
                        sendException = false;
                    } else if (es.faultstring.equals("Unknown application key")) {
                        errorMsg = getString(R.string.errorBadAppKeyMsg);
                    } else {
                        errorMsg = "Server error: " + es.getMessage();
                    }
                } else if (e instanceof XmlPullParserException) {
                    errorMsg = getString(R.string.errorServerResponseMsg);
                } else if (e instanceof TimeoutException) {
                    errorMsg = getString(R.string.errorTimeoutMsg);
                    sendException = false;
                } else {
                    errorMsg = e.getMessage();
                	if((errorMsg == null) || errorMsg.equals("")) {
                		errorMsg = getString(R.string.errorConnectionMsg);
                	}
                }

                // Request finalized with errors
                error(TAG, errorMsg, e, sendException);
                setResult(RESULT_CANCELED);

                onError();
            } else {
                postConnect();
            }
        }
    }
}
