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

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.SWADroidTracker;
import es.ugr.swad.swadroid.gui.MenuExpandableListActivity;
import es.ugr.swad.swadroid.model.Event;
import es.ugr.swad.swadroid.modules.Courses;

/**
 * Rollcall module.
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class Rollcall extends MenuExpandableListActivity implements
        SwipeRefreshLayout.OnRefreshListener {
    /**
     * Rollcall tag name for Logcat
     */
    public static final String TAG = Constants.APP_TAG + " Rollcall";
    /**
     * List of events associated to the selected course
     */
    static List<Event> eventsList;
    /**
     * ListView of events
     */
    private static ListView lvEvents;
    /**
     * Adapter for ListView of events
     */
    private static EventsListAdapter adapter;
    /**
     * Layout with "Pull to refresh" function
     */
    private SwipeRefreshLayout refreshLayout;
    /**
     * TextView for the empty events message
     */
    TextView emptyEventsTextView;

    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.MenuExpandableListActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_items_pulltorefresh);

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container_list);
        emptyEventsTextView = (TextView) findViewById(R.id.list_item_title);
        lvEvents = (ListView) findViewById(R.id.list_pulltorefresh);

        lvEvents.setOnItemClickListener(clickListener);
        lvEvents.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                boolean enable = true;
                if(lvEvents != null && lvEvents.getChildCount() > 0){
                    // check if the first item of the list is visible
                    boolean firstItemVisible = lvEvents.getFirstVisiblePosition() == 0;
                    // check if the top of the first item is visible
                    boolean topOfFirstItemVisible = lvEvents.getChildAt(0).getTop() == 0;
                    // enabling or disabling the refresh layout
                    enable = firstItemVisible && topOfFirstItemVisible;
                }
                refreshLayout.setEnabled(enable);
            }
        });

        refreshLayout.setOnRefreshListener(this);
        setAppearance();

        getSupportActionBar().setSubtitle(Courses.getSelectedCourseShortName());
    	getSupportActionBar().setIcon(R.drawable.roll_call);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        refreshEvents();
    }/* (non-Javadoc)
     * @see es.ugr.swad.swadroid.MenuExpandableListActivity#Override(android.os.Bundle)
     */
    @Override
    protected void onStart() {
        super.onStart();
        SWADroidTracker.sendScreenView(getApplicationContext(), TAG);

        refreshScreen();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case Constants.ROLLCALL_EVENTS_DOWNLOAD_REQUEST_CODE:
                refreshScreen();
                break;
        }
    }

    private void refreshScreen() {
        /*
         * If there aren't events to show, hide the events list
         * and show the empty events message
         */
        if ((eventsList == null) || (eventsList.size() == 0)) {
            Log.d(TAG, "Events list is empty");

            emptyEventsTextView.setText(R.string.eventsEmptyListMsg);
            emptyEventsTextView.setVisibility(View.VISIBLE);

            lvEvents.setVisibility(View.GONE);
        } else {
            Log.d(TAG, "Events list is not empty");

            adapter = new EventsListAdapter(this, eventsList, dbHelper);
            lvEvents.setAdapter(adapter);

            emptyEventsTextView.setVisibility(View.GONE);
            lvEvents.setVisibility(View.VISIBLE);
        }
    }

    private void refreshEvents() {
        Intent activity = new Intent(this, EventsDownload.class);
        startActivityForResult(activity, Constants.ROLLCALL_EVENTS_DOWNLOAD_REQUEST_CODE);
    }

    /**
     * It shows the SwipeRefreshLayout progress
     */
    public void showSwipeProgress() {
        refreshLayout.setRefreshing(true);
    }

    /**
     * It shows the SwipeRefreshLayout progress
     */
    public void hideSwipeProgress() {
        refreshLayout.setRefreshing(false);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
     private void setAppearance() {
        refreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    /**
     * It must be overriden by parent classes if manual swipe is enabled.
     */
    @Override
    public void onRefresh() {
        showSwipeProgress();

        refreshEvents();

        hideSwipeProgress();
    }

    /**
     * ListView click listener
     */
     private ListView.OnItemClickListener clickListener = new ListView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent activity = new Intent(getApplicationContext(),
                    UsersActivity.class);
            activity.putExtra("attendanceEventCode",
                    (int) adapter.getItemId(position));
            startActivity(activity);
        }
    };
}
