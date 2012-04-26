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

package es.ugr.swad.swadroid.modules.notifications;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Vector;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.KeepAliveHttpsTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import com.android.dataframework.DataFramework;

import es.ugr.swad.swadroid.Base64;
import es.ugr.swad.swadroid.Global;
import es.ugr.swad.swadroid.Preferences;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.model.DataBaseHelper;
import es.ugr.swad.swadroid.model.SWADNotification;
import es.ugr.swad.swadroid.model.User;
import es.ugr.swad.swadroid.ssl.SecureConnection;

import android.accounts.Account;
import android.accounts.OperationCanceledException;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

/**
 * Service for notifications sync adapter.
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class NotificationsSyncAdapterService extends Service {
	private static final String TAG = "NotificationsSyncAdapterService";
	private static SyncAdapterImpl sSyncAdapter = null;

	public NotificationsSyncAdapterService() {
		super();
	}

	private static class SyncAdapterImpl extends AbstractThreadedSyncAdapter {
		private Context mContext;

		public SyncAdapterImpl(Context context) {
			super(context, true);
			mContext = context;
		}

		@Override
		public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
			try {
				NotificationsSyncAdapterService.performSync(mContext, account, extras, authority, provider, syncResult);
			} catch (OperationCanceledException e) {
			}
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		IBinder ret = null;
		ret = getSyncAdapter().getSyncAdapterBinder();
		return ret;
	}

	private SyncAdapterImpl getSyncAdapter() {
		if (sSyncAdapter == null)
			sSyncAdapter = new SyncAdapterImpl(this);
		return sSyncAdapter;
	}

	private static void performSync(Context context, Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult)
			throws OperationCanceledException {
		mContentResolver = context.getContentResolver();
		Log.i(TAG, "performSync: " + account.toString());
		//This is where the magic will happen!
    	/**
    	 * Use of KeepAliveHttpsTransport deals with the problems with the Android ssl libraries having trouble
    	 * with certificates and certificate authorities somehow messing up connecting/needing reconnects.
    	 */
    	URL = prefs.getServer();
        connection = new KeepAliveHttpsTransportSE(URL, 443, "", TIMEOUT);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        System.setProperty("http.keepAlive", "false");
        envelope.setOutputSoapObject(request);
        //connection.debug = true;
    	connection.call(SOAP_ACTION, envelope);
        //Log.d(TAG, connection.requestDump.toString());
        //Log.d(TAG, connection.responseDump.toString());
    	
    	if(simple && !(envelope.getResponse() instanceof SoapFault)) {
    		result = envelope.bodyIn;
    	} else {
    		result = envelope.getResponse();
    	}
     }
	 
	 private static class SyncAdapterImpl extends AbstractThreadedSyncAdapter {
	  private Context mContext;
	 
	  public SyncAdapterImpl(Context context) {
	   super(context, true);
	   mContext = context;
	   prefs = new Preferences();
	   try {
		  db = DataFramework.getInstance();
		  db.open(mContext, mContext.getPackageName());		  
		  dbHelper = new DataBaseHelper(db);
	   } catch (Exception e) {
		  e.printStackTrace();
	   }
	  }
	 
	  @Override
	  public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
	   try {
		   prefs.getPreferences(mContext);
		   NotificationsSyncAdapterService.performSync(mContext, account, extras, authority, provider, syncResult);	   
	   } catch (Exception e) {
			e.printStackTrace();		
	   }
	  }
	 }
	 
	 @Override
	 public IBinder onBind(Intent intent) {
	  IBinder ret = null;
	  ret = getSyncAdapter().getSyncAdapterBinder();
	  return ret;
	 }
	 
	 private SyncAdapterImpl getSyncAdapter() {
	  if (sSyncAdapter == null)
	   sSyncAdapter = new SyncAdapterImpl(this);
	  return sSyncAdapter;
	 }
	 
	 private static void performSync(Context context, Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult)
	   throws OperationCanceledException, SoapFault, IOException, IllegalAccessException, InstantiationException, XmlPullParserException, NoSuchAlgorithmException, KeyManagementException {
		 
		//Initialize HTTPS connections 
		SecureConnection.initSecureConnection();
		
		//If last login time > Global.RELOGIN_TIME, force login
		if(System.currentTimeMillis()-Global.getLastLoginTime() > Global.RELOGIN_TIME) {
			Global.setLogged(false);
		} 
			
		if(!Global.isLogged()) {
			Log.d(TAG, "Not logged");
			
			METHOD_NAME = "loginByUserPasswordKey";
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			md.update(prefs.getUserPassword().getBytes());
			String userPassword = new String(Base64.encodeBytes(md.digest()));
			userPassword = userPassword.replace('+','-').replace('/','_').replace('=', ' ').replaceAll("\\s+", "").trim();
			
			createRequest();
			addParam("userID", prefs.getUserID());
			addParam("userPassword", userPassword);
			addParam("appKey", Global.getAppKey());
			sendRequest(true);		

			if (result != null) {
				KvmSerializable ks = (KvmSerializable) result;

				//Stores user data returned by webservice response
				User loggedUser = new User(
						Long.parseLong(ks.getProperty(0).toString()),	// id
						Integer.parseInt(ks.getProperty(1).toString()),	// userTypeCode
						ks.getProperty(2).toString(),					// wsKey
						ks.getProperty(3).toString(),					// userID
						null,											// userNickname
						ks.getProperty(4).toString(),					// userSurname1
						ks.getProperty(5).toString(),					// userSurname2
						ks.getProperty(6).toString(),					// userFirstName
						ks.getProperty(7).toString(),					// userTypeName
						null,											// photoPath
						Integer.parseInt(ks.getProperty(8).toString())	// userRole
						);

				Global.setLoggedUser(loggedUser);

				//Update application last login time
				Global.setLastLoginTime(System.currentTimeMillis());
			}
		}
		
		if(Global.isLogged()) {
			Log.d(TAG, "Logged");
			
			//Calculates next timestamp to be requested
			Long timestamp = new Long(dbHelper.getFieldOfLastNotification("eventTime"));
			timestamp++;
	
			//Creates webservice request, adds required params and sends request to webservice
			METHOD_NAME = "getNotifications";
			createRequest();
			addParam("wsKey", Global.getLoggedUser().getWsKey());
			addParam("beginTime", timestamp);
			sendRequest(false);
	
			if (result != null) {
				dbHelper.beginTransaction();
	
				//Stores notifications data returned by webservice response
				Vector<?> res = (Vector<?>) result;
				SoapObject soap = (SoapObject) res.get(1);
				notifCount = soap.getPropertyCount();
				for (int i = 0; i < notifCount; i++) {
					SoapObject pii = (SoapObject)soap.getProperty(i);
					Long notificationCode = new Long(pii.getProperty("notificationCode").toString());
					String eventType = pii.getProperty("eventType").toString();
					Long eventTime = new Long(pii.getProperty("eventTime").toString());
					String userSurname1 = pii.getProperty("userSurname1").toString();
					String userSurname2 = pii.getProperty("userSurname2").toString();
					String userFirstName = pii.getProperty("userFirstname").toString();
					String location = pii.getProperty("location").toString();
					String summary = pii.getProperty("summary").toString();
					Integer status = new Integer(pii.getProperty("status").toString());
					String content = pii.getProperty("content").toString();
					SWADNotification n = new SWADNotification(notificationCode, eventType, eventTime, userSurname1, userSurname2, userFirstName, location, summary, status, content);
					dbHelper.insertNotification(n);
	
					/*if(isDebuggable)
		    			Log.d(TAG, n.toString());*/
				}
	
				//Request finalized without errors
				Log.i(TAG, "Retrieved " + notifCount + " notifications");
	
				//Clear old notifications to control database size
				dbHelper.clearOldNotifications(SIZE_LIMIT);
	
				dbHelper.endTransaction();
			}
			
			alertNotif(context);
		 }
	 }
	}
}
