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

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.Preferences;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLException;

/**
 * REST client for SWAD webservices
 * 
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 * 
 */
public class RESTClient implements IWebserviceClient {
	/**
	 * Class Module's tag name for Logcat
	 */
	private static final String TAG = Constants.APP_TAG + " RESTClient";
	/**
	 * Client type for webservices
	 */
	public static final String CLIENT_TYPE = "REST";
	/**
	 * SERVER param for webservice request.
	 */
	private String SERVER;
	/**
	 * METHOD_NAME param for webservice request.
	 */
	@SuppressWarnings("unused")
    private String METHOD_NAME;
	/**
	 * Complete URL for webservice request.
	 */
	private String URL;
	/**
	 * Webservice result.
	 */
	private Object result;

	/**
	 * Supported request types
	 */
	public enum REQUEST_TYPE {
		GET, POST, PUT, DELETE
	};

	/**
	 * Default constructor
	 */
	public RESTClient() {
		SERVER = Preferences.getServer(); // = "swad.ugr.es";
		METHOD_NAME = "";
		URL = "";
	}

	/**
	 * Field constructor
	 * 
	 * @param SERVER
	 *            SERVER param for webservice request
	 * @param METHOD_NAME
	 *            METHOD_NAME param for webservice request
	 */
	public RESTClient(String SERVER, String METHOD_NAME) {
		this.SERVER = SERVER;
		this.METHOD_NAME = METHOD_NAME;
		this.URL = SERVER + METHOD_NAME;
	}

	@Override
	public void createRequest() {
		// TODO Auto-generated method stub

	}

	@Override
	public void addParam(String param, Object value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setMETHOD_NAME(String METHOD_NAME) {
		this.METHOD_NAME = METHOD_NAME;
		this.URL = SERVER + METHOD_NAME;
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

	public void sendRequest(Class<?> cl, boolean simple, REQUEST_TYPE type,
			JSONObject json) throws ClientProtocolException,
			SSLException, CertificateException, IOException, JSONException {

		Log.i(TAG, "Sending REST request (" + type + ") to " + SERVER
				+ " and URL " + URL);

		switch (type) {
		case GET:
			result = RestEasy.doGet(URL);
			break;
		case POST:
			result = RestEasy.doPost(URL, json);
			break;
		case PUT:
			result = RestEasy.doPut(URL, json);
			break;
		case DELETE:
			result = false;
			RestEasy.doDelete(URL);
			result = true;
			break;
		default:
			throw new ClientProtocolException(type + " method not supported");
		}

		if (!simple) {
			GsonBuilder gsonBuilder = new GsonBuilder();
			// gsonBuilder.setDateFormat("M/d/yy hh:mm a");
			Gson gson = gsonBuilder.create();
			result = gson.fromJson(((JSONObject) result).toString(), cl);
		}
	}
}
