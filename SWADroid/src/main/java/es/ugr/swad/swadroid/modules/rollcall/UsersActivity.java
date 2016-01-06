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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.zxing.client.android.Intents;

import java.util.List;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.analytics.SWADroidTracker;
import es.ugr.swad.swadroid.database.DataBaseHelper;
import es.ugr.swad.swadroid.gui.DialogFactory;
import es.ugr.swad.swadroid.gui.MenuExpandableListActivity;
import es.ugr.swad.swadroid.model.UserAttendance;
import es.ugr.swad.swadroid.modules.courses.Courses;

/**
 * UsersActivity module.
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class UsersActivity extends MenuExpandableListActivity implements
        SwipeRefreshLayout.OnRefreshListener {
    /**
     * Rollcall tag name for Logcat
     */
    public static final String TAG = Constants.APP_TAG + " UsersActivity";
    /**
     * Code of event associated to the users list
     */
    private static int eventCode;
    /**
     * ListView of users
     */
    private static ListView lvUsers;
    /**
     * Adapter for ListView of users
     */
    UsersCursorAdapter adapter;
    /**
     * Database cursor for Adapter of users
     */
    Cursor dbCursor;
    /**
     * Layout with "Pull to refresh" function
     */
    private SwipeRefreshLayout refreshLayout;
    /**
     * TextView for the empty users message
     */
    TextView emptyUsersTextView;
    /**
     * Flag for indicate if device has a rear camera available
     */
    boolean hasRearCam;

    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.MenuExpandableListActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_items_pulltorefresh);

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container_list);
        emptyUsersTextView = (TextView) findViewById(R.id.list_item_title);
        lvUsers = (ListView) findViewById(R.id.list_pulltorefresh);

        lvUsers.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                /*boolean enable = true;
                if ((lvUsers != null) && (lvUsers.getChildCount() > 0)) {

                    // check if the first item of the list is visible
                    boolean firstItemVisible = lvUsers.getFirstVisiblePosition() == 0;
                    // check if the top of the first item is visible
                    boolean topOfFirstItemVisible = lvUsers.getChildAt(0).getTop() == 0;
                    // enabling or disabling the refresh layout
                    enable = firstItemVisible && topOfFirstItemVisible;
                }*/
                boolean enable = (lvUsers != null) && (lvUsers.getChildCount() == 0);
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

        eventCode = this.getIntent().getIntExtra("attendanceEventCode", 0);
        hasRearCam = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.MenuExpandableListActivity#Override(android.os.Bundle)
     */
    @Override
    protected void onStart() {
        super.onStart();
        SWADroidTracker.sendScreenView(getApplicationContext(), TAG);

        //Refresh ListView of users
        refreshAdapter();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case Constants.ROLLCALL_USERS_DOWNLOAD_REQUEST_CODE:
                refreshAdapter();
                break;
            case Constants.ROLLCALL_USERS_SEND_REQUEST_CODE:
                refreshAdapter();
                break;
            case Constants.SCAN_QR_REQUEST_CODE:
                refreshAdapter();
                break;
        }
    }

    private void refreshAdapter() {
        /*
         * Database query can be a time consuming task ..
         * so its safe to call database query in another thread
         * Handler, will handle this stuff for you
         */
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                dbCursor = dbHelper.getUsersEventCursor(eventCode);
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

                adapter = new UsersCursorAdapter(getBaseContext(), dbCursor, dbHelper, eventCode);
                lvUsers.setAdapter(adapter);

                showProgress(false);
            }
        });
    }

    private void refreshUsers() {
        showProgress(true);

        Intent activity = new Intent(this, UsersDownload.class);
        activity.putExtra("attendanceEventCode",
                eventCode);
        startActivityForResult(activity, Constants.ROLLCALL_USERS_DOWNLOAD_REQUEST_CODE);
    }

    public void showProgress(boolean show) {
        DialogFactory.showProgress(this, show, R.id.swipe_container_list, R.id.loading_status);
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

        refreshUsers();

        hideSwipeProgress();
    }

    private String getUsersCodes() {
        String usersCodes = "";
        List<UserAttendance> usersList = dbHelper.getUsersEvent(eventCode);

        //Concatenate the user code of all users checked as present and separate them with commas
        for(UserAttendance user : usersList) {
            if(user.isUserPresent()) {
                usersCodes += user.getId() + ",";
            }
        }

        //Remove final comma
        if(!usersCodes.isEmpty()) {
            usersCodes = usersCodes.substring(0, usersCodes.length()-1);
        }

        return usersCodes;
    }

    private void scanQRCode() {
        Intent activity = new Intent(Intents.Scan.ACTION);
        activity.putExtra("SCAN_MODE", "QR_CODE_MODE,ONE_D_MODE");
        activity.putExtra("SCAN_FORMATS", "QR_CODE,ONE_D_FORMATS");
        startActivityForResult(activity, Constants.SCAN_QR_REQUEST_CODE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rollcall_activity_actions, menu);
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
                    error(TAG, getString(R.string.noCameraFound), null, false);
                }

                return true;

            case R.id.action_sendMsg:
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
                AlertDialog cleanDBDialog = DialogFactory.createWarningDialog(this,
                        -1,
                        R.string.areYouSure,
                        R.string.cleanUsersDialogMsg,
                        R.string.yesMsg,
                        R.string.noMsg,
                        true,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                                dbHelper.beginTransaction();
                                dbHelper.removeAllRows(DataBaseHelper.DB_TABLE_USERS_ATTENDANCES, "eventCode", eventCode);
                                dbHelper.updateEventStatus(eventCode, "OK");
                                dbHelper.endTransaction(true);

                                refreshAdapter();
                            }
                        },
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        },
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.PERMISSIONS_REQUEST_CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    scanQRCode();
                }
            }
        }
    }
}
