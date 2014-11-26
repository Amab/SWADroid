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

package es.ugr.swad.swadroid.modules.rollcall;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.database.DataBaseHelper;
import es.ugr.swad.swadroid.model.Event;
import es.ugr.swad.swadroid.modules.Courses;
import es.ugr.swad.swadroid.modules.Login;
import es.ugr.swad.swadroid.modules.Module;
import es.ugr.swad.swadroid.utils.Utils;
import es.ugr.swad.swadroid.webservices.SOAPClient;

/**
 * Rollcall events download module.
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class EventsDownload extends Module {
    /**
     * Number of events associated to the selected course
     */
    private int numEvents;
    /**
     * List of downloaded event codes
     */
    private List<Long> eventCodes;
    /**
     * Rollcall Events Download tag name for Logcat
     */
    private static final String TAG = Constants.APP_TAG + " EventsDownload";

    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.modules.Module#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setMETHOD_NAME("getAttendanceEvents");
        getSupportActionBar().hide();
    }

    @Override
    protected void onStart() {
        super.onStart();
        
        try {
            if (isDebuggable) {
                Log.d(TAG, "selectedCourseCode = " + Long.toString(Courses.getSelectedCourseCode()));
            }
            runConnection();
        } catch (Exception e) {
            String errorMsg = getString(R.string.errorServerResponseMsg);
            error(TAG, errorMsg, e, true);
        }
    }

    @Override
    protected void requestService() throws Exception {
        long courseCode = Courses.getSelectedCourseCode();

        // Creates webservice request, adds required params and sends request to webservice
        createRequest(SOAPClient.CLIENT_TYPE);
        addParam("wsKey", Login.getLoggedUser().getWsKey());
        addParam("courseCode", (int) courseCode);
        sendRequest(Event.class, false);

        if (result != null) {
            // Stores events data returned by webservice response
            ArrayList<?> res = new ArrayList<Object>((Vector<?>) result);
            SoapObject soap = (SoapObject) res.get(1);
            numEvents = soap.getPropertyCount();
            eventCodes = new ArrayList<Long>();

            for (int i = 0; i < numEvents; i++) {
                SoapObject pii = (SoapObject) soap.getProperty(i);

                long attendanceEventCode = Long.parseLong(pii.getProperty("attendanceEventCode").toString());
                boolean hidden = Utils.parseStringBool(pii.getProperty("hidden").toString());
                String userSurname1 = pii.getProperty("userSurname1").toString();
                String userSurname2 = pii.getProperty("userSurname2").toString();
                String userFirstName = pii.getProperty("userFirstname").toString();
                String userPhoto = pii.getProperty("userPhoto").toString();
                int startTime = Integer.parseInt(pii.getProperty("startTime").toString());
                int endTime = Integer.parseInt(pii.getProperty("endTime").toString());
                boolean commentsTeachersVisible = Utils.parseStringBool(pii.getProperty("commentsTeachersVisible").toString());
                String title = pii.getProperty("title").toString();
                String text = pii.getProperty("text").toString();
                String groups = (pii.hasProperty("groups")? pii.getProperty("groups").toString() : "");

                if (userSurname1.equalsIgnoreCase(Constants.NULL_VALUE)) userSurname1 = "";
                if (userSurname2.equalsIgnoreCase(Constants.NULL_VALUE)) userSurname2 = "";
                if (userFirstName.equalsIgnoreCase(Constants.NULL_VALUE)) userFirstName = "";
                if (userPhoto.equalsIgnoreCase(Constants.NULL_VALUE)) userPhoto = "";

                if (title.equalsIgnoreCase(Constants.NULL_VALUE)) title = "";
                if (text.equalsIgnoreCase(Constants.NULL_VALUE)) text = "";

                //Inserts or updates event into database
                dbHelper.insertEvent(new Event(attendanceEventCode, hidden, userSurname1,
                        userSurname2, userFirstName, userPhoto, startTime, endTime,
                        commentsTeachersVisible, title, text, groups));

                dbHelper.insertEventCourse(attendanceEventCode, courseCode);

                //Add eventCode to the new events list
                eventCodes.add(attendanceEventCode);
            }

            //Removes old events not listed in eventCodes
            removeOldEvents();

            Log.i(TAG, "Retrieved " + numEvents + " events");
        }    // end if (result != null)

        // Request finalized without errors
        setResult(RESULT_OK);
    }

    @Override
    protected void connect() {
        String progressDescription = getString(R.string.eventsDownloadProgressDescription);
        int progressTitle = R.string.eventsDownloadProgressTitle;

        startConnection(false, progressDescription, progressTitle);
    }

    @Override
    protected void postConnect() {
        if (numEvents == 0) {
            Toast.makeText(this, R.string.noEventsAvailableMsg, Toast.LENGTH_LONG).show();
        } else {
            String msg = String.valueOf(numEvents) + " " + getResources().getString(R.string.eventsUpdated);
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        }
        finish();
    }

    @Override
    protected void onError() {

    }

    private void removeOldEvents() {
        List<Event> dbEvents = dbHelper.getEventsCourse(Courses.getSelectedCourseCode());
        int nEventsRemoved = 0;

        if((dbEvents != null) && (dbEvents.size() > 0)) {
            for (Event e : dbEvents) {
                if (!eventCodes.contains(e.getId())) {
                    dbHelper.removeAllRows(DataBaseHelper.DB_TABLE_EVENTS_COURSES, "eventCode", e.getId());
                    dbHelper.removeAllRows(DataBaseHelper.DB_TABLE_EVENTS_ATTENDANCES, "id", e.getId());
                    nEventsRemoved++;
                }
            }
        }

        Log.i(TAG, "Removed " + nEventsRemoved + " events");
    }
}
