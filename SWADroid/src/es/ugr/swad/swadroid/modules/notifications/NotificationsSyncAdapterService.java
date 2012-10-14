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
	private static int notifCount;
	private static int NOTIF_ALERT_ID = 1982;
	private static final int SIZE_LIMIT = 25;
	private static Preferences prefs;
	private static DataFramework db;
	private static DataBaseHelper dbHelper;
	private static String METHOD_NAME = "";
	private static String NAMESPACE = "urn:swad";
	private static String SOAP_ACTION = "";
	private static SoapObject request;
	private static Object result;
	public static final String START_SYNC = "es.ugr.swad.swadroid.sync.start";
	public static final String STOP_SYNC = "es.ugr.swad.swadroid.sync.stop";

	public NotificationsSyncAdapterService() {
		super();
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
		    //Notify synchronization stop
			Intent stopIntent = new Intent();
			stopIntent.setAction(STOP_SYNC);
		    mContext.sendBroadcast(stopIntent);
		    
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
	 
	 protected static void alertNotif(Context context) {
			if(notifCount > 0) {
				//Obtain a reference to the notification service
				String ns = Context.NOTIFICATION_SERVICE;
				NotificationManager notManager =
						(NotificationManager) context.getSystemService(ns);

				//Configure the alert
				int icon = R.drawable.ic_launcher_swadroid;
				long hour = System.currentTimeMillis();

				Notification notif =
						new Notification(icon, context.getString(R.string.app_name), hour);

				//Configure the Intent
				Intent notIntent = new Intent(context,
						Notifications.class);

				PendingIntent contIntent = PendingIntent.getActivity(
						context, 0, notIntent, 0);

				notif.setLatestEventInfo(
						context, context.getString(R.string.app_name), notifCount + " " + 
								context.getString(R.string.notificationsAlertMsg), contIntent);

				//AutoCancel: alert disappears when pushed
				notif.flags |= Notification.FLAG_AUTO_CANCEL;

				//Add sound, vibration and lights
				notif.defaults |= Notification.DEFAULT_SOUND;
				//notif.defaults |= Notification.DEFAULT_VIBRATE;
				notif.defaults |= Notification.DEFAULT_LIGHTS;

				//Send alert
				notManager.notify(NOTIF_ALERT_ID, notif);
			}
		}
	 
	 protected static void createRequest() {
	        request = new SoapObject(NAMESPACE, METHOD_NAME);
	        result = null;
	 }
	 
	 protected static void addParam(String param, Object value) {
	        request.addProperty(param, value);
	 }
	 
	 protected static void sendRequest(boolean simple)
		    	throws IOException, SoapFault, IllegalAccessException, InstantiationException, XmlPullParserException {

		    	/**
		    	 * Use of KeepAliveHttpsTransport deals with the problems with the Android ssl libraries having trouble
		    	 * with certificates and certificate authorities somehow messing up connecting/needing reconnects.
		    	 */
		    	String URL = prefs.getServer();
		    	int TIMEOUT = 10000;
		    	KeepAliveHttpsTransportSE connection;
		    	
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
	 
	 private static void performSync(Context context, Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult)
	   throws OperationCanceledException, SoapFault, IOException, IllegalAccessException, InstantiationException, XmlPullParserException, NoSuchAlgorithmException, KeyManagementException {		 

		 //Notify synchronization start
		 Intent startIntent = new Intent();
		 startIntent.setAction(START_SYNC);
	     context.sendBroadcast(startIntent);
		 
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
				SoapObject soap = (SoapObject)result;

				//Stores user data returned by webservice response
				User loggedUser = new User(
						Long.parseLong(ks.getProperty(0).toString()),					// id
						soap.getProperty("wsKey").toString(),							// wsKey
						soap.getProperty("userID").toString(),							// userID
						//soap.getProperty("userNickname").toString(),					// userNickname
						null,															// userNickname
						soap.getProperty("userSurname1").toString(),					// userSurname1
						soap.getProperty("userSurname2").toString(),					// userSurname2
						soap.getProperty("userFirstname").toString(),					// userFirstname
						soap.getProperty("userPhoto").toString(),						// userPhoto
						Integer.parseInt(soap.getProperty("userRole").toString())		// userRole
						);

				Global.setLoggedUser(loggedUser);
				Global.setLogged(true);

				//Update application last login time
				Global.setLastLoginTime(System.currentTimeMillis());
			}
		}
		
		if(Global.isLogged()) {
			Log.d(TAG, "Logged");
			
			//Calculates next timestamp to be requested
			Long timestamp = Long.valueOf(dbHelper.getFieldOfLastNotification("eventTime"));
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
					Long notificationCode = Long.valueOf(pii.getProperty("notificationCode").toString());
					String eventType = pii.getProperty("eventType").toString();
					Long eventTime = Long.valueOf(pii.getProperty("eventTime").toString());
					String userSurname1 = pii.getProperty("userSurname1").toString();
					String userSurname2 = pii.getProperty("userSurname2").toString();
					String userFirstName = pii.getProperty("userFirstname").toString();
					String userPhoto = pii.getProperty("userPhoto").toString();
					String location = pii.getProperty("location").toString();
					String summary = pii.getProperty("summary").toString();
					Integer status = Integer.valueOf(pii.getProperty("status").toString());
					String content = pii.getProperty("content").toString();
					SWADNotification n = new SWADNotification(notificationCode, eventType, eventTime, userSurname1, userSurname2, userFirstName, userPhoto, location, summary, status, content);
					dbHelper.insertNotification(n);
	
					//Log.d(TAG, n.toString());
				}
	
				//Request finalized without errors
				Log.i(TAG, "Retrieved " + notifCount + " notifications");
	
				//Clear old notifications to control database size
				dbHelper.clearOldNotifications(SIZE_LIMIT);
	
				dbHelper.endTransaction();
			}
			
			alertNotif(context);
		}
		
		//Notify synchronization stop
		Intent stopIntent = new Intent();
		stopIntent.setAction(STOP_SYNC);
	    context.sendBroadcast(stopIntent);
	 }
}
