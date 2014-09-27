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

import android.accounts.Account;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.splunk.mint.Mint;

import org.apache.http.client.HttpResponseException;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParserException;

import java.net.SocketTimeoutException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.TimeoutException;

import javax.net.ssl.SSLException;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.Preferences;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.database.DataBaseHelper;
import es.ugr.swad.swadroid.gui.AlertNotificationFactory;
import es.ugr.swad.swadroid.model.Model;
import es.ugr.swad.swadroid.model.SWADNotification;
import es.ugr.swad.swadroid.model.User;
import es.ugr.swad.swadroid.modules.Login;
import es.ugr.swad.swadroid.ssl.SecureConnection;
import es.ugr.swad.swadroid.utils.Utils;
import es.ugr.swad.swadroid.webservices.IWebserviceClient;
import es.ugr.swad.swadroid.webservices.RESTClient;
import es.ugr.swad.swadroid.webservices.SOAPClient;

/**
 * Service for notifications sync adapter.
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class NotificationsSyncAdapterService extends Service {
    private static final String TAG = "NotificationsSyncAdapterService";
    private static Preferences prefs;
	private static SecureConnection conn;
    private static SyncAdapterImpl sSyncAdapter = null;
    private static int notifCount;
    private static final int NOTIF_ALERT_ID = 1982;
    private static int SIZE_LIMIT;
    private static DataBaseHelper dbHelper;
    private static IWebserviceClient webserviceClient;
    private static String METHOD_NAME = "";
    private static Object result;
    private static String errorMessage = "";
    public static final String START_SYNC = "es.ugr.swad.swadroid.sync.start";
    public static final String STOP_SYNC = "es.ugr.swad.swadroid.sync.stop";
    public static boolean isConnected;
    public static boolean isDebuggable;

    public NotificationsSyncAdapterService() {
        super();
    }

    private static class SyncAdapterImpl extends AbstractThreadedSyncAdapter {
        private final Context mContext;

        public SyncAdapterImpl(Context context) {
            super(context, true);
            mContext = context;
        }

        @Override
        public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
            boolean sendException = true;
            int httpStatusCode;
        	
        	try {
                SIZE_LIMIT = Preferences.getNotifLimit();
                NotificationsSyncAdapterService.performSync(mContext, account, extras, authority, provider, syncResult);
                
                //If synchronization was successful, update last synchronization time in preferences
                Preferences.setLastSyncTime(System.currentTimeMillis());
            } catch (Exception e) {                
                if (e instanceof SoapFault) {
                    SoapFault es = (SoapFault) e;

                    if (es.faultstring.equals("Bad log in")) {
                    	errorMessage = mContext.getString(R.string.errorBadLoginMsg);
                    	sendException = false;
                	} else if (es.faultstring.equals("Bad web service key")) {
                		errorMessage = mContext.getString(R.string.errorBadLoginMsg);
                		sendException = false;
                		
                		//Force logout and reset password (this will show again the login screen)
                		Login.setLogged(false);
                		Preferences.setUserPassword("");
                    } else if (es.faultstring.equals("Unknown application key")) {
                    	errorMessage = mContext.getString(R.string.errorBadAppKeyMsg);
                    } else {
                    	errorMessage = "Server error: " + es.getMessage();
                    }
                } else if ((e instanceof CertificateException) || (e instanceof SSLException)) {
                	errorMessage = mContext.getString(R.string.errorServerCertificateMsg);
                } else if (e instanceof XmlPullParserException) {
                	errorMessage = mContext.getString(R.string.errorServerResponseMsg);
                } else if ((e instanceof TimeoutException) || (e instanceof SocketTimeoutException)) {
                	errorMessage = mContext.getString(R.string.errorTimeoutMsg);
                	sendException = false;
                } else if (e instanceof HttpResponseException) {
                	httpStatusCode = ((HttpResponseException) e).getStatusCode();
                	
                	if(httpStatusCode == 503) { // Service Unavailable
                		errorMessage = mContext.getString(R.string.errorServiceUnavailableMsg);
                		sendException = false;
                	} else {
                		errorMessage = e.getMessage();
                    	if((errorMessage == null) || errorMessage.equals("")) {
                    		errorMessage = mContext.getString(R.string.errorConnectionMsg);
                    	}
                	}
                } else {
                	errorMessage = e.getMessage();
                	if((errorMessage == null) || errorMessage.equals("")) {
                		errorMessage = mContext.getString(R.string.errorConnectionMsg);
                	}  
                }           
                
                // Launch database rollback
                if(dbHelper.isDbInTransaction()) {
                	dbHelper.endTransaction(false);
                }
                
	            Log.e(TAG, errorMessage, e);

                //Send exception details to Mint
                if(sendException) {
                	Mint.logException(e);
                }
                
                //Notify synchronization stop
                Intent stopIntent = new Intent();
                stopIntent.setAction(STOP_SYNC);
                stopIntent.putExtra("notifCount", notifCount);  
                stopIntent.putExtra("errorMessage", errorMessage);        
                mContext.sendBroadcast(stopIntent);
            }
        }
    }

    /* (non-Javadoc)
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate() {        
        isConnected = Utils.connectionAvailable(this);
		// Check if debug mode is enabled
        try {
            getPackageManager().getApplicationInfo(
                    getPackageName(), 0);
			isDebuggable = (ApplicationInfo.FLAG_DEBUGGABLE != 0);
        } catch (NameNotFoundException e) {
        	Log.e(TAG, "Error getting debuggable flag", e);
        }
        
		//Initialize Mint plugin
        try {
        	Mint.initAndStartSession(this, Constants.MINT_API_KEY);
        } catch (Exception e) {
        	Log.e(TAG, "Error initializing Mint", e);
        }
        
        try {
            prefs = new Preferences(this);
            dbHelper = new DataBaseHelper(this);            
            //Initialize webservices client
            webserviceClient = null;
        } catch (Exception e) {
            Log.e(TAG, "Error initializing database and preferences", e);

            //Send exception details to Mint
            Mint.logException(e);
        }
        
		super.onCreate();
	}

	/* (non-Javadoc)
	 * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		 // return START_NOT_STICKY - we want this Service to be left running 
        //  unless explicitly stopped, and it's process is killed, we want it to
        //  be restarted
        return START_STICKY;
	}

	/* (non-Javadoc)
	 * @see android.app.Service#onDestroy()
	 */
	@Override
	public void onDestroy() {
		try {
			Mint.closeSession(this);
		} catch (Exception e) {
			Log.e(TAG, "Error closing Mint", e);
        }
		
		super.onDestroy();
	}

	@Override
    public IBinder onBind(Intent intent) {        
        return getSyncAdapter().getSyncAdapterBinder();
    }

	/* (non-Javadoc)
	 * @see android.app.Service#onUnbind(android.content.Intent)
	 */
	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

	private SyncAdapterImpl getSyncAdapter() {
        if (sSyncAdapter == null)
            sSyncAdapter = new SyncAdapterImpl(this);
        return sSyncAdapter;
    }

    /**
     * Creates webservice request.
     */
    protected static void createRequest(String clientType) {
    	if(webserviceClient == null) {
	    	if(clientType.equals(SOAPClient.CLIENT_TYPE)) {
	    		webserviceClient = new SOAPClient();
	    	} else if(clientType.equals(RESTClient.CLIENT_TYPE)) {
	    		webserviceClient = new RESTClient();    		
	    	}
	
    	}

        webserviceClient.setMETHOD_NAME(METHOD_NAME);
    	webserviceClient.createRequest();
    }

    /**
     * Adds a parameter to webservice request.
     *
     * @param param Parameter name.
     * @param value Parameter value.
     */
    protected static void addParam(String param, Object value) {
    	webserviceClient.addParam(param, value);
    }

    /**
     * Sends a SOAP request to the specified webservice in METHOD_NAME class
     * constant of the webservice client.
     * 
     * @param cl     Class to be mapped
     * @param simple Flag for select simple or complex response
     * @throws Exception 
     */
    protected static void sendRequest(Class<?> cl, boolean simple) throws Exception {
    	((SOAPClient) webserviceClient).sendRequest(cl, simple);
    	result = webserviceClient.getResult();
    }
    
    private static void logUser() throws Exception {
    	Log.d(TAG, "Not logged");

        METHOD_NAME = "loginByUserPasswordKey";

        createRequest(SOAPClient.CLIENT_TYPE);
        addParam("userID", Preferences.getUserID());
        addParam("userPassword", Preferences.getUserPassword());
        addParam("appKey", Constants.SWAD_APP_KEY);
        sendRequest(User.class, true);

        if (result != null) {
            KvmSerializable ks = (KvmSerializable) result;
            SoapObject soap = (SoapObject) result;

            //Stores user data returned by webservice response
            User loggedUser = new User(
                    Long.parseLong(ks.getProperty(0).toString()),                    // id
                    soap.getProperty("wsKey").toString(),                            // wsKey
                    soap.getProperty("userID").toString(),                            // userID
                    //soap.getProperty("userNickname").toString(),					// userNickname
                    null,                                                            // userNickname
                    soap.getProperty("userSurname1").toString(),                    // userSurname1
                    soap.getProperty("userSurname2").toString(),                    // userSurname2
                    soap.getProperty("userFirstname").toString(),                    // userFirstname
                    soap.getProperty("userPhoto").toString(),                        // userPhoto
                    Integer.parseInt(soap.getProperty("userRole").toString())        // userRole
            );

            Login.setLoggedUser(loggedUser);
            Login.setLogged(true);

            //Update application last login time
            Login.setLastLoginTime(System.currentTimeMillis());
        }
    }
    
    private static void getNotifications() throws Exception {
    	Log.d(TAG, "Logged");

        //Calculates next timestamp to be requested
        Long timestamp = Long.valueOf(dbHelper.getFieldOfLastNotification("eventTime"));
        timestamp++;

        //Creates webservice request, adds required params and sends request to webservice
        METHOD_NAME = "getNotifications";
        
        createRequest(SOAPClient.CLIENT_TYPE);
        addParam("wsKey", Login.getLoggedUser().getWsKey());
        addParam("beginTime", timestamp);
        sendRequest(SWADNotification.class, false);

        if (result != null) {
            dbHelper.beginTransaction();

            //Stores notifications data returned by webservice response
            ArrayList<?> res = new ArrayList<Object>((Vector<?>) result);
            SoapObject soap = (SoapObject) res.get(1);
            int numNotif = soap.getPropertyCount();
            
            notifCount = 0;
            for (int i = 0; i < numNotif; i++) {
            	SoapObject pii = (SoapObject) soap.getProperty(i);
                Long notifCode = Long.valueOf(pii.getProperty("notifCode").toString());
                Long eventCode = Long.valueOf(pii.getProperty("notificationCode").toString());
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
                boolean notifReadSWAD = (status >= 4);
                
                SWADNotification n = new SWADNotification(notifCode, eventCode, eventType, eventTime, userSurname1, userSurname2, userFirstName, userPhoto, location, summary, status, content, notifReadSWAD, notifReadSWAD);
                dbHelper.insertNotification(n);
                
                //Count unread notifications only
                if(!notifReadSWAD) {
                	notifCount++;
                }

                //Log.d(TAG, n.toString());
            }

            //Request finalized without errors
            Log.i(TAG, "Retrieved " + numNotif + " notifications (" + notifCount + " unread)");

            //Clear old notifications to control database size
            dbHelper.clearOldNotifications(SIZE_LIMIT);

            dbHelper.endTransaction(true);
        }
    }
    
    /**
     * Sends to SWAD the "seen notifications" info
     */
    private static void sendReadedNotifications(Context context) {
        List<Model> markedNotificationsList;
        String seenNotifCodes;
        Intent activity;
        int numMarkedNotificationsList;
        
    	//Construct a list of seen notifications in state "pending to mark as read in SWAD" 
        markedNotificationsList = dbHelper.getAllRows(DataBaseHelper.DB_TABLE_NOTIFICATIONS,
        		"seenLocal='" + Utils.parseBoolString(true)
        		+ "' AND seenRemote='" + Utils.parseBoolString(false) + "'", null);
        
        numMarkedNotificationsList = markedNotificationsList.size();
        if(isDebuggable)
        	Log.d(TAG, "numMarkedNotificationsList=" + numMarkedNotificationsList);
        
        if(numMarkedNotificationsList > 0) {            
            //Creates a string of notification codes separated by commas from the previous list
            seenNotifCodes = Utils.getSeenNotificationCodes(markedNotificationsList);
	        if(isDebuggable)
	        	Log.d(TAG, "seenNotifCodes=" + seenNotifCodes);

            //Sends "seen notifications" info to the server
	        activity = new Intent(context, NotificationsMarkAllAsRead.class);
	        activity.putExtra("seenNotifCodes", seenNotifCodes);
	        activity.putExtra("numMarkedNotificationsList", numMarkedNotificationsList);
	        activity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        context.startActivity(activity);
        }
    }

    private static void performSync(Context context, Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult)
            throws Exception {

		Notification notif;
		PendingIntent pendingIntent = PendingIntent.getActivity(context,
				0, 
				new Intent(context, Notifications.class), 
				Intent.FLAG_ACTIVITY_NEW_TASK);
        
        //Notify synchronization start
        Intent startIntent = new Intent();
        startIntent.setAction(START_SYNC);
        context.sendBroadcast(startIntent);

        //Initialize HTTPS connections
    	/*
    	 * Terena root certificate is not included by default on Gingerbread and older
    	 * If Android API < 11 (HONEYCOMB) add Terena certificate manually
    	 */
    	if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
    		conn = new SecureConnection();
    		conn.initSecureConnection(context);        		
    		Log.i(TAG, "Android API < 11 (HONEYCOMB). Adding Terena certificate manually");
    	} else {
    		Log.i(TAG, "Android API >= 11 (HONEYCOMB). Using Terena built-in certificate");
    	}

        //If last login time > Global.RELOGIN_TIME, force login
        if (Login.isLogged() &&
        		((System.currentTimeMillis() - Login.getLastLoginTime()) > Login.RELOGIN_TIME)) {
        	
            Login.setLogged(false);
        }

        if (!Login.isLogged()) {
        	logUser();
        }

        if (Login.isLogged()) {
        	getNotifications();
        	
        	if (notifCount > 0) {
	            //If the notifications counter exceeds the limit, set it to the max allowed
	            if (notifCount > SIZE_LIMIT) {
	                notifCount = SIZE_LIMIT;
	            }

				notif = AlertNotificationFactory.createAlertNotification(context,
						context.getString(R.string.app_name),
						notifCount + " "
						+ context.getString(R.string.notificationsAlertMsg),
						context.getString(R.string.app_name),
						pendingIntent,
						R.drawable.ic_launcher_swadroid,
						R.drawable.ic_launcher_swadroid,
						true,
						true,
						false,
						false);
				
				AlertNotificationFactory.showAlertNotification(context, notif, NOTIF_ALERT_ID);
        	}
        	
        	sendReadedNotifications(context);
        }

        //Notify synchronization stop
        Intent stopIntent = new Intent();
        stopIntent.setAction(STOP_SYNC);
        stopIntent.putExtra("notifCount", notifCount);  
        stopIntent.putExtra("errorMessage", errorMessage);        
        context.sendBroadcast(stopIntent);
    }
}
