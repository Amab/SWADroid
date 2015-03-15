/*
 *
 *  *  This file is part of SWADroid.
 *  *
 *  *  Copyright (C) 2010 Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 *  *
 *  *  SWADroid is free software: you can redistribute it and/or modify
 *  *  it under the terms of the GNU General Public License as published by
 *  *  the Free Software Foundation, either version 3 of the License, or
 *  *  (at your option) any later version.
 *  *
 *  *  SWADroid is distributed in the hope that it will be useful,
 *  *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  *  GNU General Public License for more details.
 *  *
 *  *  You should have received a copy of the GNU General Public License
 *  *  along with SWADroid.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package es.ugr.swad.swadroid;

import com.google.android.gms.analytics.ExceptionReporter;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.StandardExceptionParser;
import com.google.android.gms.analytics.Tracker;

import android.content.Context;
import android.util.Log;

import java.util.HashMap;

/**
 * Tracker for Google Play
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class SWADroidTracker {

    /**
     * SWADroidTracker tag name for Logcat
     */
    public static final String TAG = Constants.APP_TAG + " SWADroidTracker";

    private static HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    private static boolean isTrackerEnabled(Context context) {
        /*return (!Config.ANALYTICS_API_KEY.isEmpty()
                && (GooglePlayServicesUtil.isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS));*/
        return (!Config.ANALYTICS_API_KEY.isEmpty());
    }

    /*static synchronized Tracker getTracker(Context context, TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
            Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(Config.ANALYTICS_API_KEY)
                    : (trackerId == TrackerName.GLOBAL_TRACKER) ? analytics.newTracker(R.xml.global_tracker)
                    : analytics.newTracker(R.xml.ecommerce_tracker);
            mTrackers.put(trackerId, t);

        }
        return mTrackers.get(trackerId);
    }*/

    private static synchronized Tracker getTracker(Context context) {
        if (!mTrackers.containsKey(TrackerName.APP_TRACKER)) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
            Tracker t = analytics.newTracker(Config.ANALYTICS_API_KEY);
            t.enableExceptionReporting(true);
            t.enableAutoActivityTracking(true);
            mTrackers.put(TrackerName.APP_TRACKER, t);

        }
        return mTrackers.get(TrackerName.APP_TRACKER);
    }

    public static void initTracker(Context context) {
        // Initialize a tracker using a Google Analytics property ID.
        if (isTrackerEnabled(context)) {
            GoogleAnalytics.getInstance(context).newTracker(Config.ANALYTICS_API_KEY);

            /*Thread.UncaughtExceptionHandler exceptionHandler = new ExceptionReporter(
                    getTracker(context),                              // Currently used Tracker.
                    Thread.getDefaultUncaughtExceptionHandler(),      // Current default uncaught exception handler.
                    context);                                         // Context of the application.

            // Make exceptionHandler the new default uncaught exception handler.
            Thread.setDefaultUncaughtExceptionHandler(exceptionHandler);*/

            ExceptionReporter exceptionHandler =
                    new ExceptionReporter(
                            getTracker(context),
                            Thread.getDefaultUncaughtExceptionHandler(),
                            context);

            StandardExceptionParser exceptionParser =
                    new StandardExceptionParser(context, null) {
                        @Override
                        public String getDescription(String threadName, Throwable t) {
                            return "{" + threadName + "} " + Log.getStackTraceString(t);
                        }
                    };

            exceptionHandler.setExceptionParser(exceptionParser);

            // Make myHandler the new default uncaught exception handler.
            Thread.setDefaultUncaughtExceptionHandler(exceptionHandler);

            Log.i(TAG, "Google Play Services available. SWADroidTracker enabled");
        } else {
            Log.w(TAG, "Google Play Services not available. SWADroidTracker disabled");
        }
    }

    public static void sendScreenView(Context context, String path) {
        if (isTrackerEnabled(context)) {
            // Get tracker.
            Tracker t = getTracker(context);

            // Set screen name.
            // Where path is a String representing the screen name.
            t.setScreenName(path);

            // Send a screen view.
            t.send(new HitBuilders.AppViewBuilder().build());

            Log.i(TAG, "ScreenView sended for screen " + path);
        }
    }

    public static void sendScreenView(Context context, String path, String category, String action,
            String label) {
        if (isTrackerEnabled(context)) {
            // Get tracker.
            Tracker t = getTracker(context);

            // Set screen name.
            // Where path is a String representing the screen name.
            t.setScreenName(path);

            // Send a screen view.
            t.send(new HitBuilders.AppViewBuilder().build());

            // This event will also be sent with &cd=Home%20Screen.
            // Build and send an Event.
            t.send(new HitBuilders.EventBuilder()
                    .setCategory(category)
                    .setAction(action)
                    .setLabel(label)
                    .build());

            // Clear the screen name field when we're done.
            t.setScreenName(null);

            Log.i(TAG, "ScreenView sended for screen " + path);
        }
    }

    public static void sendException(Context context, Exception e, boolean fatal) {
        if (isTrackerEnabled(context)) {
            // Get tracker.
            Tracker t = getTracker(context);

            // Build and send exception.
            /*t.send(new HitBuilders.ExceptionBuilder()
                    .setDescription(e.getCause().toString())
                    .setFatal(fatal)
                    .build());*/

            StandardExceptionParser exceptionParser =
                    new StandardExceptionParser(context, null) {
                        @Override
                        public String getDescription(String threadName, Throwable t) {
                            return "{" + threadName + "} " + Log.getStackTraceString(t);
                        }
                    };

            t.send(new HitBuilders.ExceptionBuilder()
                            .setDescription(exceptionParser
                                    .getDescription(Thread.currentThread().getName(), e))
                            .setFatal(fatal)
                            .build()
            );

            Log.e(TAG, e.getMessage(), e);
        }
    }

    /**
     * Enum used to identify the tracker that needs to be used for tracking.
     *
     * A single tracker is usually enough for most purposes. In case you do need multiple trackers,
     * storing them all in Application object helps ensure that they are created only once per
     * application instance.
     */
    public static enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
        GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
        ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a company.
    }
}
