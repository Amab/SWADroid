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
 * Games List module.
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 * @author Sergio Díaz Rueda <sergiodiazrueda8@gmail.com>
 */
public class GamesList extends MenuExpandableListActivity
        implements SwipeRefreshLayout.OnRefreshListener {
    /**
     * GamesActiveList tag name for Logcat
     */
    private static final String TAG = Constants.APP_TAG + " GamesActiveList";
    /**
     * ListView of games
     */
    private ListView lvGames;
    /**
     * Adapter for ListView of games
     */
    private GamesCursorAdapter adapter;
    /**
     * Handler for games
     */
    private final GamesList.RefreshAdapterHandler mHandler =
            new GamesList.RefreshAdapterHandler(this);


    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            /*
            Database cursor for Adapter of games
            */
            Cursor dbCursor = dbHelper.getGamesCourseCursor(Courses.getSelectedCourseCode());
            startManagingCursor(dbCursor);


            /*
             * If there aren't games to show, hide the games lvGames
             * and show the empty games message
             */
            if ((dbCursor == null) || (dbCursor.getCount() == 0)) {
                Log.d(TAG, "Games list is empty");

                emptyGamesTextView.setText(R.string.gamesEmptyListMsg);
                emptyGamesTextView.setVisibility(View.VISIBLE);

                lvGames.setVisibility(View.GONE);
            } else {
                Log.d(TAG, "Games list is not empty");

                emptyGamesTextView.setVisibility(View.GONE);
                lvGames.setVisibility(View.VISIBLE);
            }

            adapter = new GamesCursorAdapter(getBaseContext(), dbCursor, dbHelper);
            lvGames.setAdapter(adapter);

            mProgressScreen.hide();
        }
    };
    /**
     * TextView for the empty games message
     */
    private TextView emptyGamesTextView;
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
    private final ListView.OnItemClickListener clickListener = new ListView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mProgressScreen.show();
            Intent activity = new Intent(getApplicationContext(),
                    Matches.class);
            activity.putExtra("gameCode", adapter.getItemId(position));
            startActivityForResult(activity, Constants.MATCHES_ACTIVE_DOWNLOAD_CODE);
        }
    };

    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.MenuExpandableListActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_items_pulltorefresh);

        refreshLayout = findViewById(R.id.swipe_container_list);
        emptyGamesTextView = findViewById(R.id.list_item_title);

        View mProgressScreenView = findViewById(R.id.progress_screen);
        mProgressScreen = new ProgressScreen(mProgressScreenView, refreshLayout,
                getString(R.string.loadingMsg), this);

        lvGames = findViewById(R.id.list_pulltorefresh);
        lvGames.setOnItemClickListener(clickListener);

        lvGames.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                boolean enable = true;
                if ((lvGames != null) && (lvGames.getChildCount() > 0)) {
                    // check if the first item of the list is visible
                    boolean firstItemVisible = lvGames.getFirstVisiblePosition() == 0;
                    // check if the top of the first item is visible
                    boolean topOfFirstItemVisible = lvGames.getChildAt(0).getTop() == 0;
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
                Intent activity;
                activity = new Intent(getApplicationContext(), MatchesList.class);
                activity.putExtra("gameCode", Long.parseLong(intent.getDataString()));
                startActivityForResult(activity, Constants.MATCHES_ACTIVE_LIST_CODE);
                refreshAdapter();
                break;

        }
    }

    private void refreshAdapter() {
        mHandler.post(mRunnable);
    }

    private void refreshGames() {
        mProgressScreen.show();
        Intent activity = new Intent(this, Games.class);
        startActivityForResult(activity, Constants.GAMES_ACTIVE_LIST_CODE);
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

    private boolean hasPendingGames() {
        boolean hasPendingGames = false;
        TextView sendingStateTextView;
        int i = 0;

        if ((lvGames != null) && (lvGames.getChildCount() > 0)) {
            while (!hasPendingGames && (i < lvGames.getChildCount())) {
                sendingStateTextView = (TextView) lvGames.getChildAt(i).findViewById
                        (R.id.sendingStateTextView);
                hasPendingGames = sendingStateTextView.getText().equals(
                        getString(R.string.sendingStatePending));
                i++;
            }
        }

        return hasPendingGames;
    }

    private void updateGames() {
        showSwipeProgress();

        refreshGames();

        hideSwipeProgress();
    }

    /**
     * It must be overriden by parent classes if manual swipe is enabled.
     */
    @Override
    public void onRefresh() {
        if (!hasPendingGames()) {
            updateGames();
        } else {
            AlertDialog cleanGamesDialog = DialogFactory.createWarningDialog(this,
                    -1,
                    R.string.areYouSure,
                    R.string.updatePendingGamesMsg,
                    R.string.yesMsg,
                    R.string.noMsg,
                    true,
                    (dialog, id) -> {
                        dialog.cancel();

                        updateGames();
                    },
                    (dialog, id) -> dialog.cancel(),
                    null);

            cleanGamesDialog.show();
        }

        hideSwipeProgress();
    }

    private static class RefreshAdapterHandler extends Handler {

        private final WeakReference<GamesList> mActivity;

        public RefreshAdapterHandler(GamesList activity) {
            mActivity = new WeakReference<>(activity);
        }
    }


}
