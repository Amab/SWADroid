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
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.gui.DialogFactory;
import es.ugr.swad.swadroid.gui.MenuExpandableListActivity;
import es.ugr.swad.swadroid.gui.ProgressScreen;
import es.ugr.swad.swadroid.modules.courses.Courses;

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
  private static final String TAG = Constants.APP_TAG + " Rollcall";
  /**
   * ListView of events
   */
  private static ListView lvEvents;
  /**
   * Adapter for ListView of events
   */
  private static EventsCursorAdapter adapter;
  private final RefreshAdapterHandler mHandler = new RefreshAdapterHandler(this);
  private final Runnable mRunnable = new Runnable() {
    @Override
    public void run() {
      /*
    Database cursor for Adapter of events
   */
      Cursor dbCursor = dbHelper.getEventsCourseCursor(Courses.getSelectedCourseCode());
      startManagingCursor(dbCursor);


                /*
                 * If there aren't events to show, hide the events lvEvents
                 * and show the empty events message
                 */
      if ((dbCursor == null) || (dbCursor.getCount() == 0)) {
        Log.d(TAG, "Events list is empty");

        emptyEventsTextView.setText(R.string.eventsEmptyListMsg);
        emptyEventsTextView.setVisibility(View.VISIBLE);

        lvEvents.setVisibility(View.GONE);
      } else {
        Log.d(TAG, "Events list is not empty");

        emptyEventsTextView.setVisibility(View.GONE);
        lvEvents.setVisibility(View.VISIBLE);
      }

      adapter = new EventsCursorAdapter(getBaseContext(), dbCursor, dbHelper);
      lvEvents.setAdapter(adapter);

      mProgressScreen.hide();
    }
  };
  /**
   * TextView for the empty events message
   */
  private TextView emptyEventsTextView;
  /**
   * Layout with "Pull to refresh" function
   */
  private SwipeRefreshLayout refreshLayout;
  /**
   * Progress screen
   */
  private ProgressScreen mProgressScreen;
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

  /* (non-Javadoc)
   * @see es.ugr.swad.swadroid.MenuExpandableListActivity#onCreate(android.os.Bundle)
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.list_items_pulltorefresh);

    refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container_list);
    emptyEventsTextView = (TextView) findViewById(R.id.list_item_title);

    View mProgressScreenView = findViewById(R.id.progress_screen);
    mProgressScreen = new ProgressScreen(mProgressScreenView, refreshLayout,
            getString(R.string.loadingMsg), this);

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
        if ((lvEvents != null) && (lvEvents.getChildCount() > 0)) {
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

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
  }

  /* (non-Javadoc)
   * @see es.ugr.swad.swadroid.MenuExpandableListActivity#Override(android.os.Bundle)
   */
  @Override
  protected void onStart() {
    super.onStart();

    //Refresh ListView of events
    refreshAdapter();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    switch (requestCode) {
      case Constants.ROLLCALL_EVENTS_DOWNLOAD_REQUEST_CODE:
        refreshAdapter();
        break;
    }
  }

  private void refreshAdapter() {
    mHandler.post(mRunnable);
  }

  private void refreshEvents() {
    mProgressScreen.show();
    Intent activity = new Intent(this, EventsDownload.class);
    startActivityForResult(activity, Constants.ROLLCALL_EVENTS_DOWNLOAD_REQUEST_CODE);
  }

  /**
   * It shows the SwipeRefreshLayout progress
   */
  private void showSwipeProgress() {
    refreshLayout.setRefreshing(true);
  }

  /**
   * It shows the SwipeRefreshLayout progress
   */
  private void hideSwipeProgress() {
    refreshLayout.setRefreshing(false);
  }

  @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
  private void setAppearance() {
    refreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light);
  }

  private boolean hasPendingEvents() {
    boolean hasPendingEvents = false;
    TextView sendingStateTextView;
      int i = 0;

    if ((lvEvents != null) && (lvEvents.getChildCount() > 0)) {
       while(!hasPendingEvents && (i<lvEvents.getChildCount())) {
          sendingStateTextView = (TextView) lvEvents.getChildAt(i).findViewById(R.id.sendingStateTextView);
          hasPendingEvents = sendingStateTextView.getText().equals(getString(R.string.sendingStatePending));
          i++;
        }
    }

    return hasPendingEvents;
  }

    private void updateEvents() {
        showSwipeProgress();

        refreshEvents();

        hideSwipeProgress();
    }

  /**
   * It must be overriden by parent classes if manual swipe is enabled.
   */
  @Override
  public void onRefresh() {
    if(!hasPendingEvents()) {
        updateEvents();
    } else {
        AlertDialog cleanEventsDialog = DialogFactory.createWarningDialog(this,
                -1,
                R.string.areYouSure,
                R.string.updatePendingEventsMsg,
                R.string.yesMsg,
                R.string.noMsg,
                true,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                        updateEvents();
                    }
                },
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                },
                null);

        cleanEventsDialog.show();
    }

      hideSwipeProgress();
  }

  private static class RefreshAdapterHandler extends Handler {

    private final WeakReference<Rollcall> mActivity;

    public RefreshAdapterHandler(Rollcall activity) {
      mActivity = new WeakReference<>(activity);
    }

  }
}
