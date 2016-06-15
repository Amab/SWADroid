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

package es.ugr.swad.swadroid.analytics;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.analytics.ExceptionReporter;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.StandardExceptionParser;
import com.google.android.gms.analytics.Tracker;

import java.util.HashMap;

import es.ugr.swad.swadroid.Config;
import es.ugr.swad.swadroid.Constants;

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
    /**
     * Enum used to identify the tracker that needs to be used for tracking.
     *
     * A single tracker is usually enough for most purposes. In case you do need multiple trackers,
     * storing them all in Application object helps ensure that they are created only once per
     * application instance.
     */
    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
        GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
        ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a company.
    }

    private static HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    private static boolean isTrackerEnabled(Context context) {
        return (!Config.ANALYTICS_API_KEY.isEmpty());
    }

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
        if(isTrackerEnabled(context)) {
            GoogleAnalytics.getInstance(context).newTracker(Config.ANALYTICS_API_KEY);

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
        if(isTrackerEnabled(context)) {
            // Get tracker.
            Tracker t = getTracker(context);

            // Set screen name.
            // Where path is a String representing the screen name.
            t.setScreenName(path);

            // Send a screen view.
            t.send(new HitBuilders.ScreenViewBuilder().build());

            Log.i(TAG, "ScreenView sent for screen " + path);
        }
    }

    public static void sendScreenView(Context context, String path, String category, String action, String label) {
        if(isTrackerEnabled(context)) {
            // Get tracker.
            Tracker t = getTracker(context);

            // Set screen name.
            // Where path is a String representing the screen name.
            t.setScreenName(path);

            // Send a screen view.
            t.send(new HitBuilders.ScreenViewBuilder().build());

            // This event will also be sent with &cd=Home%20Screen.
            // Build and send an Event.
            t.send(new HitBuilders.EventBuilder()
                    .setCategory(category)
                    .setAction(action)
                    .setLabel(label)
                    .build());

            // Clear the screen name field when we're done.
            t.setScreenName(null);

            Log.i(TAG, "ScreenView sent for screen " + path);
        }
    }

    public static void sendException(Context context, Exception e, boolean fatal) {
        if(isTrackerEnabled(context)) {
            // Get tracker.
            Tracker t = getTracker(context);

            StandardExceptionParser exceptionParser =
                    new StandardExceptionParser(context, null) {
                        @Override
                        public String getDescription(String threadName, Throwable t) {
                            return "{" + threadName + "} " + Log.getStackTraceString(t);
                        }
                    };

            t.send(new HitBuilders.ExceptionBuilder()
                            .setDescription(exceptionParser.getDescription(Thread.currentThread().getName(), e))
                            .setFatal(fatal)
                            .build()
            );

            Log.e(TAG, e.getMessage(), e);
        }
    }
}
