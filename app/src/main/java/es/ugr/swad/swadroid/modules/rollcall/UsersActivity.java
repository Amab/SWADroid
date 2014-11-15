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

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.client.android.Intents;

import java.util.List;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.gui.MenuExpandableListActivity;
import es.ugr.swad.swadroid.model.UserAttendance;
import es.ugr.swad.swadroid.modules.Courses;

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
    private int eventCode;
    /**
     * List of users associates to the selected event
     */
    public static List<UserAttendance> usersList;
    /**
     * ListView of users
     */
    private static ListView lvUsers;
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
    /**
     * Flag for exit module when back button is pressed twice
     */
    boolean doubleBackToExitPressedOnce;

    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.MenuExpandableListActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_items_pulltorefresh);

    	getSupportActionBar().setIcon(R.drawable.roll_call);

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container_list);
        emptyUsersTextView = (TextView) findViewById(R.id.list_item_title);
        lvUsers = (ListView) findViewById(R.id.list_pulltorefresh);

        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setEnabled(true);
        setAppearance();

        getSupportActionBar().setSubtitle(Courses.getSelectedCourseShortName());
    	getSupportActionBar().setIcon(R.drawable.roll_call);

        eventCode = this.getIntent().getIntExtra("attendanceEventCode", 0);
        hasRearCam = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);

        refreshUsers();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case Constants.ROLLCALL_USERS_DOWNLOAD_REQUEST_CODE:
                usersList = Rollcall.usersMap.get(eventCode);
                /*
                 * If there aren't users to show, hide the users lvUsers
                 * and show the empty users message
                 */
                if ((usersList == null) || (usersList.size() == 0)) {
                    Log.d(TAG, "Users lvUsers is empty");

                    emptyUsersTextView.setText(R.string.usersEmptyListMsg);
                    emptyUsersTextView.setVisibility(View.VISIBLE);

                    lvUsers.setVisibility(View.GONE);
                } else {
                    Log.d(TAG, "Users lvUsers is not empty");

                    lvUsers.setAdapter(new UsersArrayAdapter(this, usersList));

                    emptyUsersTextView.setVisibility(View.GONE);
                    lvUsers.setVisibility(View.VISIBLE);
                }
        }
    }

    private void refreshUsers() {
        Intent activity = new Intent(this, UsersDownload.class);
        startActivityForResult(activity, Constants.ROLLCALL_USERS_DOWNLOAD_REQUEST_CODE);
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

        //Concatenate the user ID of all users checked as present and separate them with commas
        for(UserAttendance user : usersList) {
            if(user.isUserPresent()) {
                usersCodes += user.getUserID() + ",";
            }
        }

        //Remove final comma
        if(!usersCodes.isEmpty()) {
            usersCodes = usersCodes.substring(0, usersCodes.length()-2);
        }

        return usersCodes;
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.doubleBackToExitMsg, Toast.LENGTH_LONG).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
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
                    Intent activity = new Intent(Intents.Scan.ACTION);
                    activity.putExtra("SCAN_MODE", "QR_CODE_MODE");
                    activity.putExtra("SCAN_FORMATS", "QR_CODE");
                    startActivityForResult(activity, Constants.SCAN_QR_REQUEST_CODE);
                } else {
                    //If the device has no rear camera available show error message
                    error(TAG, getString(R.string.noCameraFound), null, false);
                }

                return true;

            case R.id.action_sendMsg:
                String usersCodes = getUsersCodes();

                if(!usersCodes.isEmpty()) {
                    Intent activity = new Intent(getApplicationContext(),
                            UsersSend.class);
                    activity.putExtra("attendanceEventCode",
                            eventCode);
                    //Set unmarked users as absent
                    activity.putExtra("setOthersAsAbsent",
                            1);
                    activity.putExtra("usersCodes",
                            usersCodes);
                    startActivity(activity);
                } else {
                    Toast.makeText(getApplicationContext(), R.string.noUsersCheckedMsg,
                            Toast.LENGTH_LONG).show();
                }

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
