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

package es.ugr.swad.swadroid.modules.rollcall;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.zxing.integration.android.IntentIntegrator;

import java.util.Collections;
import java.util.List;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.dao.EventDao;
import es.ugr.swad.swadroid.dao.UserAttendanceDao;
import es.ugr.swad.swadroid.dao.UserDao;
import es.ugr.swad.swadroid.gui.DialogFactory;
import es.ugr.swad.swadroid.gui.MenuExpandableListActivity;
import es.ugr.swad.swadroid.gui.ProgressScreen;
import es.ugr.swad.swadroid.model.Event;
import es.ugr.swad.swadroid.model.UserAttendance;
import es.ugr.swad.swadroid.modules.courses.Courses;

/**
 * UsersActivity module.
 *
 * @author Juan Miguel Boyero Corral <swadroid@gmail.com>
 */
public class UsersActivity extends MenuExpandableListActivity implements
        SwipeRefreshLayout.OnRefreshListener {
    /**
     * Rollcall tag name for Logcat
     */
    private static final String TAG = Constants.APP_TAG + " UsersActivity";
    /**
     * Code of event associated to the users list
     */
    private static int eventCode;
    /**
     * ListView of users
     */
    private ListView lvUsers;
    /**
     * Adapter for ListView of users
     */
    private UsersCursorAdapter adapter;
    /**
     * Database cursor for Adapter of users
     */
    private Cursor dbCursor;
    /**
     * Database Access Object for User
     */
    private UserDao userDao;
    /**
     * Database Access Object for UserAttendance
     */
    private UserAttendanceDao userAttendanceDao;
    /**
     * Database Access Object for Event
     */
    private EventDao eventDao;
    /**
     * Layout with "Pull to refresh" function
     */
    private SwipeRefreshLayout refreshLayout;
    /**
     * TextView for the empty users message
     */
    private TextView emptyUsersTextView;
    /**
     * Progress screen
     */
    private ProgressScreen mProgressScreen;
    /**
     * Flag for indicate if device has a rear camera available
     */
    private boolean hasRearCam;

    /**
     * Barcode scanner
     */
    private IntentIntegrator integrator;

    /**
     * Button for send attendances to SWAD
     */
    private MenuItem mSendUsersMenuItem;

    /**
     * Button for clear all users for an event
     */
    private MenuItem mCleanUsersMenuItem;

    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.MenuExpandableListActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize DAOs
        userDao = db.getUserDao();
        userAttendanceDao = db.getUserAttendanceDao();
        eventDao = db.getEventDao();

        setContentView(R.layout.list_items_pulltorefresh);

        refreshLayout = findViewById(R.id.swipe_container_list);
        emptyUsersTextView = findViewById(R.id.list_item_title);

        View mProgressScreenView = findViewById(R.id.progress_screen);
        mProgressScreen = new ProgressScreen(mProgressScreenView, refreshLayout,
                getString(R.string.loadingMsg), this);

        lvUsers = findViewById(R.id.list_pulltorefresh);

        lvUsers.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                // No-op
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                boolean enable = (lvUsers != null) && (lvUsers.getChildCount() == 0);
                refreshLayout.setEnabled(enable);
            }
        });

        refreshLayout.setOnRefreshListener(this);
        setAppearance();

        getSupportActionBar().setSubtitle(Courses.getSelectedCourseShortName());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        eventCode = this.getIntent().getIntExtra("attendanceEventCode", 0);
        hasRearCam = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);

        integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(ContinuousCaptureActivity.class);
        integrator.setBeepEnabled(false);
    }

    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.MenuExpandableListActivity#Override(android.os.Bundle)
     */
    @Override
    protected void onStart() {
        super.onStart();

        //Refresh ListView of users
        refreshAdapter();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        refreshAdapter();
        setActionMenuItemsEnabled(true);

        super.onActivityResult(requestCode, resultCode, intent);
    }

    private void refreshAdapter() {
        /*
         * Database query can be a time consuming task ..
         * so its safe to call database query in another thread
         * Handler, will handle this stuff for you
         */
        new Handler().post(() -> {
            dbCursor = userDao.findAllByEventCodeCursor(eventCode);
            startManagingCursor(dbCursor);

            /*
             * If there aren't users to show, hide the users lvUsers
             * and show the empty users message
             */
            if ((dbCursor == null) || (dbCursor.getCount() == 0)) {
                Log.d(TAG, "Users lvUsers is empty");

                emptyUsersTextView.setText(R.string.usersEmptyListMsg);
                emptyUsersTextView.setVisibility(View.VISIBLE);

                lvUsers.setVisibility(View.GONE);

                refreshLayout.setEnabled(true);
            } else {
                Log.d(TAG, "Users lvUsers is not empty");

                emptyUsersTextView.setVisibility(View.GONE);
                lvUsers.setVisibility(View.VISIBLE);
            }

            adapter = new UsersCursorAdapter(getBaseContext(), dbCursor, eventCode);
            lvUsers.setAdapter(adapter);

            mProgressScreen.hide();
        });
    }

    private void refreshUsers() {
        mProgressScreen.show();

        Intent activity = new Intent(this, UsersDownload.class);
        activity.putExtra("attendanceEventCode",
                eventCode);
        startActivityForResult(activity, Constants.ROLLCALL_USERS_DOWNLOAD_REQUEST_CODE);
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

    /**
     * It must be overriden by parent classes if manual swipe is enabled.
     */
    @Override
    public void onRefresh() {
        setActionMenuItemsEnabled(false);
        showSwipeProgress();

        refreshUsers();

        hideSwipeProgress();
        setActionMenuItemsEnabled(true);
    }

    private String getUsersCodes() {
        StringBuilder usersCodes = new StringBuilder();
        List<UserAttendance> usersList = userAttendanceDao.findByEventCode(eventCode);

        //Concatenate the user code of all users checked as present and separate them with commas
        for(UserAttendance user : usersList) {
            if(user.isUserPresent()) {
                usersCodes.append(user.getId()).append(",");
            }
        }

        //Remove final comma
        if(usersCodes.length() > 0) {
            usersCodes = new StringBuilder(usersCodes.substring(0, usersCodes.length() - 1));
        }

        return usersCodes.toString();
    }

    private void scanQRCode() {
        integrator.initiateScan();
    }

    private void setActionMenuItemsEnabled(boolean enabled) {
        mSendUsersMenuItem.setEnabled(enabled);
        mCleanUsersMenuItem.setEnabled(enabled);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rollcall_activity_actions, menu);

        mSendUsersMenuItem = menu.findItem(R.id.action_sendMsg);
        mCleanUsersMenuItem = menu.findItem(R.id.action_cleanUsers);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_scanQR:
                // Check if device has a rear camera
                if (hasRearCam) {
                    // check Android 6 permission
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.CAMERA},
                                Constants.PERMISSIONS_REQUEST_CAMERA);
                    } else {
                        scanQRCode();
                    }
                } else {
                    //If the device has no rear camera available show error message
                    error(getString(R.string.noCameraFound), null);
                }

                return true;

            case R.id.action_sendMsg:
                setActionMenuItemsEnabled(false);

                String usersCodes = getUsersCodes();

                Intent activity = new Intent(getApplicationContext(),
                        UsersSend.class);
                activity.putExtra("attendanceEventCode",
                        eventCode);
                //Set unmarked users as absent
                activity.putExtra("setOthersAsAbsent",
                        1);
                activity.putExtra("usersCodes",
                        usersCodes);
                startActivityForResult(activity, Constants.ROLLCALL_USERS_SEND_REQUEST_CODE);

                return true;

            case R.id.action_cleanUsers:
                setActionMenuItemsEnabled(false);

                AlertDialog cleanDBDialog = DialogFactory.createWarningDialog(this,
                        -1,
                        R.string.areYouSure,
                        R.string.cleanUsersDialogMsg,
                        R.string.yesMsg,
                        R.string.noMsg,
                        true,
                        (dialog, id) -> {
                            dialog.cancel();

                            userAttendanceDao.deleteAttendancesByEventCode(eventCode);
                            Event event = eventDao.findById(eventCode);
                            event.setStatus("OK");
                            eventDao.updateEvents(Collections.singletonList(event));

                            refreshAdapter();
                        },
                        (dialog, id) -> dialog.cancel(),
                        null);

                cleanDBDialog.show();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static int getEventCode() {
        return eventCode;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.PERMISSIONS_REQUEST_CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    scanQRCode();
                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
