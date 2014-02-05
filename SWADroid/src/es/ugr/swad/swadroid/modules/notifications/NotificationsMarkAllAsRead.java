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

import android.os.Bundle;
import android.util.Log;
import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.modules.Module;
import es.ugr.swad.swadroid.utils.Utils;
import es.ugr.swad.swadroid.webservices.SOAPClient;

import org.ksoap2.serialization.SoapPrimitive;

/**
 * Notifications module for mark as read user's notifications
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class NotificationsMarkAllAsRead extends Module {
    /**
     * Notifications tag name for Logcat
     */
    private static final String TAG = Constants.APP_TAG + " NotificationsMarkAllAsRead";

    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.modules.Module#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setMETHOD_NAME("markNotificationsAsRead");
        runConnection();
    }

    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.modules.Module#requestService()
     */
    @Override
    protected void requestService() throws Exception {

        int numMarkedNotifications = 0;
        String seenNotifCodes = this.getIntent().getStringExtra("seenNotifCodes");
        int numMarkedNotificationsList = this.getIntent().getIntExtra("numMarkedNotificationsList", 0);

        //Request finalized without errors        
        if(isDebuggable) {
        	Log.d(TAG, "seenNotifCodes=" + seenNotifCodes);
        	Log.d(TAG, "numMarkedNotificationsList=" + numMarkedNotificationsList);
        }
        
        //Creates webservice request, adds required params and sends request to webservice
        createRequest(SOAPClient.CLIENT_TYPE);
        addParam("wsKey", Constants.getLoggedUser().getWsKey());
        addParam("notifications", seenNotifCodes);
        sendRequest(Integer.class, false);
        
        if (result != null) {
            SoapPrimitive soap = (SoapPrimitive) result;

            //Stores user data returned by webservice response
            numMarkedNotifications = Integer.parseInt(soap.toString());
        }
        
        Log.i(TAG, "Marked " + numMarkedNotifications + " notifications as readed");
    	dbHelper.updateAllNotifications("seenRemote", Utils.parseBoolString(true));
        
        if(numMarkedNotifications != numMarkedNotificationsList) {	            
        	Log.e(TAG, "numMarkedNotifications (" + numMarkedNotifications + ") != numMarkedNotificationsList (" + numMarkedNotificationsList + ")");
        	setResult(RESULT_CANCELED);   
        } else {
        	setResult(RESULT_OK);   
        }
    }
    
    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.modules.Module#connect()
     */
    @Override
    protected void connect() {
        String progressDescription = getString(R.string.notificationsProgressDescription);
        int progressTitle = R.string.notificationsProgressTitle;

        startConnection(false, progressDescription, progressTitle);
    }

    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.modules.Module#postConnect()
     */
    @Override
    protected void postConnect() {
    	finish();
    }

    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.modules.Module#onError()
     */
    @Override
    protected void onError() {
    	
    }
}
