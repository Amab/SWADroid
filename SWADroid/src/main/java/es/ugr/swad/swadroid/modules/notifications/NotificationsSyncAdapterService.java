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
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.transport.HttpResponseException;
import org.xmlpull.v1.XmlPullParserException;

import java.net.SocketTimeoutException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import javax.net.ssl.SSLException;

import es.ugr.swad.swadroid.Config;
import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.dao.SWADNotificationDao;
import es.ugr.swad.swadroid.database.AppDatabase;
import es.ugr.swad.swadroid.gui.AlertNotificationFactory;
import es.ugr.swad.swadroid.model.SWADNotification;
import es.ugr.swad.swadroid.model.User;
import es.ugr.swad.swadroid.modules.login.Login;
import es.ugr.swad.swadroid.preferences.Preferences;
import es.ugr.swad.swadroid.ssl.SecureConnection;
import es.ugr.swad.swadroid.utils.Utils;
import es.ugr.swad.swadroid.webservices.IWebserviceClient;
import es.ugr.swad.swadroid.webservices.SOAPClient;

/**
 * Service for notifications sync adapter.
 * @see <a href="https://openswad.org/ws/#getNotifications">getNotifications</a>
 *
 * @author Juan Miguel Boyero Corral <swadroid@gmail.com>
 */
public class NotificationsSyncAdapterService extends Service {
    private static final String TAG = "NotificationsSyncAdapterService";
    private static Preferences prefs;
	private static SecureConnection conn;
    private static SyncAdapterImpl sSyncAdapter = null;
    private static int notifCount;
    private static IWebserviceClient webserviceClient;
    private static String METHOD_NAME = "";
    private static Object result;
    private static String errorMessage = "";
    private static boolean isConnected;
    private static boolean isDebuggable;
    private static SWADNotificationDao swadNotificationDao;
    public static final String START_SYNC = "es.ugr.swad.swadroid.sync.start";
    public static final String STOP_SYNC = "es.ugr.swad.swadroid.sync.stop";
    /**
     * Application context
     */
    private static Context mCtx;

    public NotificationsSyncAdapterService() {
        super();
    }

    private static class SyncAdapterImpl extends AbstractThreadedSyncAdapter {
        private final Context mContext;

        public SyncAdapterImpl(Context context) {
            super(context, true);
            mContext = context;
            mCtx = context;
        }

        @Override
        public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
            int httpStatusCode;

        	try {
                NotificationsSyncAdapterService.performSync(mContext);

                //If synchronization was successful, update last synchronization time in preferences
                Preferences.setLastSyncTime(System.currentTimeMillis());
            } catch (Exception e) {
                if (e.getClass() == SoapFault.class) {
                    SoapFault es = (SoapFault) e;

                    switch (es.faultstring) {
                        case "Bad log in":
                            errorMessage = mContext.getString(R.string.errorBadLoginMsg);
                            break;
                        case "Bad web service key":
                            errorMessage = mContext.getString(R.string.errorBadLoginMsg);

                            // Force logout and reset password (this will show again
                            // the login screen)
                            Login.setLogged(false);
                            Preferences.setUserPassword("");
                            break;
                        case "Unknown application key":
                            errorMessage = mContext.getString(R.string.errorBadAppKeyMsg);
                            break;
                        default:
                            errorMessage = "Server error: " + es.getMessage();
                            break;
                    }
                } else if ((e.getClass() == TimeoutException.class) || (e.getClass() == SocketTimeoutException.class)) {
                    errorMessage = mContext.getString(R.string.errorTimeoutMsg);
                } else if ((e.getClass() == CertificateException.class) || (e .getClass() == SSLException.class)) {
                    errorMessage = mContext.getString(R.string.errorServerCertificateMsg);
                } else if (e.getClass() == HttpResponseException.class) {
                    httpStatusCode = ((HttpResponseException) e).getStatusCode();

                    Log.e(TAG, "httpStatusCode=" + httpStatusCode);

                    switch(httpStatusCode) {
                        case 500: errorMessage = mContext.getString(R.string.errorInternalServerMsg);
                            break;

                        case 503: errorMessage = mContext.getString(R.string.errorServiceUnavailableMsg);
                            break;

                        default:  errorMessage = e.getMessage();
                            if ((errorMessage == null) || errorMessage.equals("")) {
                                errorMessage = mContext.getString(R.string.errorConnectionMsg);
                            }
                    }
                } else if (e.getClass() == XmlPullParserException.class) {
                    errorMessage = mContext.getString(R.string.errorServerResponseMsg);
                } else {
                    errorMessage = e.getMessage();
                    if ((errorMessage == null) || errorMessage.equals("")) {
                        errorMessage = mContext.getString(R.string.errorConnectionMsg);
                    }
                }

                Log.e(TAG, errorMessage, e);

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

        try {
            prefs = new Preferences(this);
            //Initialize webservices client
            webserviceClient = null;

            //Initialize DAOs
            swadNotificationDao = AppDatabase.getAppDatabase(this).getSwadNotificationDao();
        } catch (Exception e) {
            Log.e(TAG, "Error initializing database and preferences", e);
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
    private static void createRequest(String clientType) {
    	if(webserviceClient == null && clientType.equals(SOAPClient.CLIENT_TYPE)) {
            webserviceClient = new SOAPClient();
    	}

        webserviceClient.setMethodName(METHOD_NAME);
    	webserviceClient.createRequest();
    }

    /**
     * Adds a parameter to webservice request.
     *
     * @param param Parameter name.
     * @param value Parameter value.
     */
    private static void addParam(String param, Object value) {
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
    private static void sendRequest(Class<?> cl, boolean simple) throws Exception {
    	((SOAPClient) webserviceClient).sendRequest(cl, simple);
    	result = webserviceClient.getResult();
    }

    private static void logUser() throws Exception {
    	Log.d(TAG, "Not logged");

        METHOD_NAME = "loginByUserPasswordKey";

        createRequest(SOAPClient.CLIENT_TYPE);
        addParam("userID", Preferences.getUserID());
        addParam("userPassword", Preferences.getUserPassword());
        addParam("appKey", Config.SWAD_APP_KEY);
        sendRequest(User.class, true);

        if (result != null) {
            SoapObject soap = (SoapObject) result;

            //Stores user data returned by webservice response
            User loggedUser = new User(
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

            Login.setLoggedUser(loggedUser);
            Login.setLogged(true);

            //Update application last login time
            Login.setLastLoginTime(System.currentTimeMillis());
        }
    }

    private static void getNotifications() throws Exception {
        List<SWADNotification> notificationsList = new ArrayList<>();
        long numDeletedNotif;

    	Log.d(TAG, "Logged");

        //Calculates next timestamp to be requested
        long timestamp = swadNotificationDao.findMaxEventTime();
        timestamp++;

        //Creates webservice request, adds required params and sends request to webservice
        METHOD_NAME = "getNotifications";

        createRequest(SOAPClient.CLIENT_TYPE);
        addParam("wsKey", Login.getLoggedUser().getWsKey());
        addParam("beginTime", timestamp);
        sendRequest(SWADNotification.class, false);

        if (result != null) {
            //Stores notifications data returned by webservice response
            ArrayList<?> res = new ArrayList<>((Vector<?>) result);
            SoapObject soap = (SoapObject) res.get(1);
            int numNotif = soap.getPropertyCount();

            notifCount = 0;
            for (int i = 0; i < numNotif; i++) {
            	SoapObject pii = (SoapObject) soap.getProperty(i);
                long notifCode = Long.parseLong(pii.getProperty("notifCode").toString());
                long eventCode = Long.parseLong(pii.getProperty("eventCode").toString());
                String eventType = pii.getProperty("eventType").toString();
                long eventTime = Long.parseLong(pii.getProperty("eventTime").toString());
                String userNickname = pii.getProperty("userNickname").toString();
                String userSurname1 = pii.getProperty("userSurname1").toString();
                String userSurname2 = pii.getProperty("userSurname2").toString();
                String userFirstName = pii.getProperty("userFirstname").toString();
                String userPhoto = pii.getProperty("userPhoto").toString();
                String location = pii.getProperty("location").toString();
                String summary = pii.getProperty("summary").toString();
                int status = Integer.parseInt(pii.getProperty("status").toString());
                String content = pii.getProperty("content").toString();
                boolean notifReadSWAD = (status >= 4);
                boolean notifCancelled = (status >= 8);

                // Add not cancelled notifications only
                if(!notifCancelled) {
                    SWADNotification n = new SWADNotification(notifCode, eventCode, eventType,
                            eventTime, userNickname, userSurname1, userSurname2, userFirstName,
                            userPhoto, location, summary, status, content, notifReadSWAD, notifReadSWAD);

                    notificationsList.add(n);

                    //Count unread notifications only
                    if (!notifReadSWAD) {
                        notifCount++;
                    }
                }
            }

            swadNotificationDao.insertSWADNotifications(notificationsList);

            //Request finalized without errors
            Log.i(TAG, "Retrieved " + numNotif + " notifications (" + notifCount + " unread)");

            //Clean old notifications to control database size
            numDeletedNotif = swadNotificationDao.deleteAllByAge(Constants.CLEAN_NOTIFICATIONS_THRESHOLD);
            Log.i(TAG, "Deleted " + numDeletedNotif + " notifications from database");
        }
    }

    /**
     * Sends to SWAD the "seen notifications" info
     */
    private static void sendReadedNotifications(Context context) {
        List<SWADNotification> markedNotificationsList;
        String seenNotifCodes;
        Intent activity;
        int numMarkedNotificationsList;

    	//Construct a list of seen notifications in state "pending to mark as read in SWAD"
        markedNotificationsList = swadNotificationDao.findAllPendingToRead();

        numMarkedNotificationsList = markedNotificationsList.size();
        if(isDebuggable)
        	Log.d(TAG, "numMarkedNotificationsList=" + numMarkedNotificationsList);

        if(numMarkedNotificationsList > 0) {
            //Creates a string of notification codes separated by commas from the previous list
            seenNotifCodes = markedNotificationsList.stream()
                    .map(n -> String.valueOf(n.getId()))
                    .collect(Collectors.joining(","));

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

    private static void performSync(Context context)
            throws Exception {

		Notification notif;
        Intent notificationIntent = new Intent(context, Notifications.class);
        PendingIntent pendingIntent;

        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //single top to avoid creating many activity stacks queue
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        pendingIntent = PendingIntent.getActivity(context,
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        //Notify synchronization start
        Intent startIntent = new Intent();
        startIntent.setAction(START_SYNC);
        context.sendBroadcast(startIntent);

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
				notif = AlertNotificationFactory.createAlertNotification(context,
						context.getString(R.string.app_name),
						notifCount + " "
						+ context.getString(R.string.notificationsAlertMsg),
						context.getString(R.string.app_name),
						pendingIntent,
						R.drawable.ic_launcher_swadroid_notif,
						R.drawable.ic_launcher_swadroid,
						true,
						false,
						false);

				AlertNotificationFactory.showAlertNotification(context, notif, Notifications.NOTIF_ALERT_ID);
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
