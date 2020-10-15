package es.ugr.swad.swadroid.modules.games;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.lang.ref.WeakReference;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.gui.DialogFactory;
import es.ugr.swad.swadroid.gui.MenuExpandableListActivity;
import es.ugr.swad.swadroid.gui.ProgressScreen;
import es.ugr.swad.swadroid.modules.courses.Courses;

/**
 * Matches List module.
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 * @author Sergio Díaz Rueda <sergiodiazrueda8@gmail.com>
 */
public class MatchesActiveList extends MenuExpandableListActivity
        implements SwipeRefreshLayout.OnRefreshListener {
    /**
     * MatchesActiveList tag name for Logcat
     */
    private static final String TAG = Constants.APP_TAG + " MatchesActiveList";

    /**
     * ListView of matches
     */
    private ListView lvMatches;
    /**
     * Adapter for ListView of matches
     */
    private static GamesCursorAdapter adapter;
    /**
     * Handler of matches
     */
    private final MatchesActiveList.RefreshAdapterHandler mHandler =
            new MatchesActiveList.RefreshAdapterHandler(this);

    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            /*
             * Database cursor for Adapter of matches
             */
            Cursor dbCursor = dbHelper.getMatchesGameCursor(gameCode);

            startManagingCursor(dbCursor);


            /*
             * If there aren't matches to show, hide the matches lvMatches
             * and show the empty matches message
             */
            if ((dbCursor == null) || (dbCursor.getCount() == 0)) {
                Log.d(TAG, "Matches list is empty");

                emptyMatchesTextView.setText(R.string.matchesEmptyListMsg);
                emptyMatchesTextView.setVisibility(View.VISIBLE);

                lvMatches.setVisibility(View.GONE);
            } else {
                Log.d(TAG, "Matches list is not empty");

                emptyMatchesTextView.setVisibility(View.GONE);
                lvMatches.setVisibility(View.VISIBLE);
            }

            adapter = new GamesCursorAdapter(getBaseContext(), dbCursor, dbHelper);
            lvMatches.setAdapter(adapter);

            mProgressScreen.hide();
        }
    };
    /**
     * TextView for the empty matches message
     */
    private TextView emptyMatchesTextView;
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
            mProgressScreen.show();
            Intent activity = new Intent(getApplicationContext(),
                    GamesPlay.class);
            activity.putExtra("gameCode", (long) gameCode);
            activity.putExtra("matchCode", adapter.getItemId(position));
            startActivity(activity);
        }
    };

    /**
     * Game code
     */
    private long gameCode;


    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.MenuExpandableListActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameCode = getIntent().getLongExtra("gameCode", 0);
        setContentView(R.layout.list_items_pulltorefresh);

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container_list);
        emptyMatchesTextView = (TextView) findViewById(R.id.list_item_title);

        View mProgressScreenView = findViewById(R.id.progress_screen);
        mProgressScreen = new ProgressScreen(mProgressScreenView, refreshLayout,
                getString(R.string.loadingMsg), this);

        lvMatches = (ListView) findViewById(R.id.list_pulltorefresh);
        lvMatches.setOnItemClickListener(clickListener);

        lvMatches.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                boolean enable = true;
                if ((lvMatches != null) && (lvMatches.getChildCount() > 0)) {
                    // check if the first item of the list is visible
                    boolean firstItemVisible = lvMatches.getFirstVisiblePosition() == 0;
                    // check if the top of the first item is visible
                    boolean topOfFirstItemVisible = lvMatches.getChildAt(0).getTop() == 0;
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

        refreshAdapter();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case Constants.MATCHES_ACTIVE_DOWNLOAD_CODE:
                mProgressScreen.hide();
                refreshAdapter();
                break;
        }
    }

    private void refreshAdapter() {
        mHandler.post(mRunnable);
    }

    private void refreshMatches() {
        mProgressScreen.show();
        Intent activity = new Intent(this, MatchesActive.class);
        activity.putExtra("gameCode", gameCode);
        startActivityForResult(activity, Constants.MATCHES_ACTIVE_DOWNLOAD_CODE);
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

    private boolean hasPendingMatches() {
        boolean hasPendingMatches = false;
        TextView sendingStateTextView;
        int i = 0;

        if ((lvMatches != null) && (lvMatches.getChildCount() > 0)) {
            while (!hasPendingMatches && (i < lvMatches.getChildCount())) {
                sendingStateTextView = (TextView) lvMatches.getChildAt(i).findViewById
                        (R.id.sendingStateTextView);
                hasPendingMatches = sendingStateTextView.getText().equals
                        (getString(R.string.sendingStatePending));
                i++;
            }
        }

        return hasPendingMatches;
    }

    private void updateMatches() {
        showSwipeProgress();

        refreshMatches();

        hideSwipeProgress();
    }

    /**
     * It must be overriden by parent classes if manual swipe is enabled.
     */
    @Override
    public void onRefresh() {
        if (!hasPendingMatches()) {
            updateMatches();
        } else {
            AlertDialog cleanMatchesDialog = DialogFactory.createWarningDialog(this,
                    -1,
                    R.string.areYouSure,
                    R.string.updatePendingMatchesMsg,
                    R.string.yesMsg,
                    R.string.noMsg,
                    true,
                    (dialog, id) -> {
                        dialog.cancel();

                        updateMatches();
                    },
                    (dialog, id) -> dialog.cancel(),
                    null);

            cleanMatchesDialog.show();
        }

        hideSwipeProgress();
    }

    private static class RefreshAdapterHandler extends Handler {

        private final WeakReference<MatchesActiveList> mActivity;

        public RefreshAdapterHandler(MatchesActiveList activity) {
            mActivity = new WeakReference<>(activity);
        }

    }


}
