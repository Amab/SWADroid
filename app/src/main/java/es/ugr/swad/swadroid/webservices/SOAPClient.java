/*
 *  This file is part of SWADroid.
 *
 *  Copyright (C) 2010 Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 *
 *  SWADroid Ant is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  SWADroid Ant is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with SWADroid.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.ugr.swad.swadroid.webservices;

import android.content.pm.ApplicationInfo;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.KeepAliveHttpsTransportSE;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.Preferences;

/**
 * SOAP client for SWAD webservices
 * 
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 * 
 */
public class SOAPClient implements IWebserviceClient {
	/**
	 * Class Module's tag name for Logcat
	 */
	private static final String TAG = Constants.APP_TAG + " SOAPClient";
	/**
	 * Client type for webservices
	 */
	public static final String CLIENT_TYPE = "SOAP";
	/**
	 * SOAP_ACTION param for webservice request.
	 */
	private String SOAP_ACTION = "";
	/**
	 * METHOD_NAME param for webservice request.
	 */
	private String METHOD_NAME = "";
	/**
	 * NAMESPACE param for webservice request.
	 */
	private String NAMESPACE;
    /**
	 * Webservice request.
	 */
	private SoapObject request;
	/**
	 * Webservice result.
	 */
	private Object result;
	/**
	 * Application debuggable flag
	 */
	protected static boolean isDebuggable;

	/**
	 * Default constructor
	 */
	public SOAPClient() {
		NAMESPACE = "urn:swad";
		isDebuggable = (ApplicationInfo.FLAG_DEBUGGABLE != 0);
	}

	/**
	 * Field constructor
	 *
	 * @param SOAP_ACTION
	 *            SOAP_ACTION param for webservice request
	 * @param METHOD_NAME
	 *            METHOD_NAME param for webservice request
	 * @param NAMESPACE
	 *            NAMESPACE param for webservice request
	 */
	public SOAPClient(String SOAP_ACTION, String METHOD_NAME,
			String NAMESPACE) {
		this.SOAP_ACTION = SOAP_ACTION;
		this.METHOD_NAME = METHOD_NAME;
		this.NAMESPACE = NAMESPACE;
		isDebuggable = (ApplicationInfo.FLAG_DEBUGGABLE != 0);
	}

	/**
	 * Sets METHOD_NAME parameter.
	 * 
	 * @param METHOD_NAME
	 *            METHOD_NAME parameter.
	 */
	@Override
	public void setMETHOD_NAME(String METHOD_NAME) {
		this.METHOD_NAME = METHOD_NAME;
	}

	/**
	 * Gets the result returned by the webservice
	 * 
	 * @return The result returned by the webservice
	 */
	@Override
	public Object getResult() {
		return result;
	}

	/**
	 * Creates webservice request.
	 */
	@Override
	public void createRequest() {
		request = new SoapObject(NAMESPACE, METHOD_NAME);
		result = null;
	}

	/**
	 * Adds a parameter to webservice request.
	 * 
	 * @param param
	 *            Parameter name.
	 * @param value
	 *            Parameter value.
	 */
	@Override
	public void addParam(String param, Object value) {
		request.addProperty(param, value);
	}

	/**
	 * Sends a request to the specified webservice in METHOD_NAME class
	 * constant.
	 * 
	 * @param cl
	 *            Class to be mapped
	 * @param simple
	 *            Flag for select simple or complex response
	 * @throws Exception 
	 */
	public void sendRequest(Class<?> cl, boolean simple)
			throws Exception {

		String server = Preferences.getServer();

		Log.i(TAG, "Sending SOAP request to " + server + " and method "
				+ METHOD_NAME);

		// Variables for URL splitting
		String delimiter = "/";
		String PATH;
		String[] URLArray;
		String URL;

		// Split URL
		URLArray = server.split(delimiter, 2);
		URL = URLArray[0];
		if (URLArray.length == 2) {
			PATH = delimiter + URLArray[1];
		} else {
			PATH = "";
		}

		/**
		 * Use of KeepAliveHttpsTransport deals with the problems with the
		 * Android ssl libraries having trouble with certificates and
		 * certificate authorities somehow messing up connecting/needing
		 * reconnects.
         *
         * SOAP connection
         */
        KeepAliveHttpsTransportSE connection = new KeepAliveHttpsTransportSE(URL, 443, PATH,
                Constants.CONNECTION_TIMEOUT);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		System.setProperty("http.keepAlive", "false");
		envelope.encodingStyle = SoapEnvelope.ENC;
		envelope.setAddAdornments(false);
		envelope.implicitTypes = true;
		envelope.dotNet = false;
		envelope.setOutputSoapObject(request);

		if (cl != null) {
			envelope.addMapping(NAMESPACE, cl.getSimpleName(), cl);
		}

		if (isDebuggable) {
			connection.debug = true;
			try {
				connection.call(SOAP_ACTION, envelope);
				Log.d(TAG, connection.getHost() + " " + connection.getPath()
						+ " " + connection.getPort());
				Log.d(TAG, connection.requestDump);
				Log.d(TAG, connection.responseDump);
			} catch (Exception e) {
				Log.e(TAG, connection.getHost() + " " + connection.getPath()
						+ " " + connection.getPort());

                if (connection.requestDump != null) {
                    Log.e(TAG, connection.requestDump);
                }
				if (connection.responseDump != null) {
					Log.e(TAG, connection.responseDump);
				}
				
				throw e;
			}
		} else {
			connection.call(SOAP_ACTION, envelope);
		}

		if (simple && !(envelope.getResponse() instanceof SoapFault)) {
			result = envelope.bodyIn;
		} else {
			result = envelope.getResponse();
		}
	}

}
