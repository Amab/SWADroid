package es.ugr.swad.swadroid;

/**
 * @author Alejandro Alcalde (elbauldelprogramador.com) on 15/03/15.
 */

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.List;

import es.ugr.swad.swadroid.database.DataBaseHelper;
import es.ugr.swad.swadroid.gui.CourseFragment;
import es.ugr.swad.swadroid.model.Model;
import es.ugr.swad.swadroid.modules.Courses;
import es.ugr.swad.swadroid.sync.SyncUtils;
import es.ugr.swad.swadroid.utils.Utils;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialAccount;
import it.neokree.materialnavigationdrawer.elements.MaterialSection;
import it.neokree.materialnavigationdrawer.elements.listeners.MaterialAccountListener;

/**
 * @author Alejandro Alcalde (elbauldelprogramador.com)
 */
public class MainActivity extends MaterialNavigationDrawer implements MaterialAccountListener {

    @Override
    public void init(Bundle bundle) {

        initDrawer();

        try {
//            isDebuggable = (ApplicationInfo.FLAG_DEBUGGABLE != 0);
//            isSWADMain = this instanceof SWADMain;
        } catch (Exception ex) {
            Log.e(Constants.APP_TAG, ex.getMessage());
        }
    }

    private void initDrawer() {

        this.disableLearningPattern();

        List<Model> listCourses;
        listCourses = Constants.dbHelper
                .getAllRows(DataBaseHelper.DB_TABLE_COURSES, null, "fullName");
        /*
          Cursor for database access
        */
        Cursor dbCursor = Constants.dbHelper.getDb()
                .getCursor(DataBaseHelper.DB_TABLE_COURSES, null, "fullName");
        // TODO Is deprecated, replace with Loader and CursorLoader
        startManagingCursor(dbCursor);

        if (listCourses.size() != 0) {
            MaterialAccount account;
            Model course;
            for (int i = 0; i < listCourses.size(); i++) {
                course = listCourses.get(i);
                account = new MaterialAccount(
                        getResources(),
                        course.getProperty(2).toString(),
                        course.getProperty(3).toString(),
                        R.drawable.photo2,
                        R.drawable.bamboo);
                addAccount(account);
            }
        }

        MaterialSection section1 = newSection(getString(R.string.course), R.drawable.crs,
                new CourseFragment());
        MaterialSection section2 = newSection(getString(R.string.evaluation), R.drawable.ass,
                new CourseFragment());
        MaterialSection section3 = newSection(getString(R.string.users), R.drawable.users,
                new CourseFragment());
        MaterialSection section4 = newSection(getString(R.string.messages), R.drawable.msg,
                new CourseFragment()).setSectionColor(Color.parseColor("#ff0000"));

        addSection(section1);
        addSection(section2);
        addSection(section3);
        addSection(section4);

        setDrawerHeaderImage(R.drawable.mat3);
        setAccountListener(this);
        addBottomSection(newSection("Bottom Section", R.mipmap.ic_settings_black_24dp,
                new Intent(this, PreferencesActivity.class)));

    }

    @Override
    public void onAccountOpening(MaterialAccount materialAccount) {
        Log.w(Constants.APP_TAG, "OnAccountOppening");
    }

    @Override
    public void onChangeAccount(MaterialAccount materialAccount) {
        Log.w(Constants.APP_TAG, "OnChangeAccount");
        if (Constants.dbHelper.getAllRows(DataBaseHelper.DB_TABLE_COURSES).size() == 0) {
            if (Utils.connectionAvailable(getApplicationContext())) {
                getCurrentCourses();
            }
        }
    }

    private void getCurrentCourses() {
//        showProgress(true);

        Intent activity = new Intent(this, Courses.class);
        startActivityForResult(activity, Constants.COURSES_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                //After get the list of courses, a dialog is launched to choice the course
                case Constants.COURSES_REQUEST_CODE:

//                    setMenuDbClean();
//                    createSpinnerAdapter();
//                    createMenu();

                    //User credentials are correct. Set periodic synchronization if enabled
                    if (!Preferences.getSyncTime().equals("0")
                            && Preferences.isSyncEnabled() && SyncUtils.isPeriodicSynced(this)) {
                        SyncUtils.addPeriodicSync(Constants.AUTHORITY, Bundle.EMPTY,
                                Long.parseLong(Preferences.getSyncTime()), this);
                    }

                    showProgress(false);
                    break;
                case Constants.LOGIN_REQUEST_CODE:
                    getCurrentCourses();
                    break;
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            switch (requestCode) {
                //After get the list of courses, a dialog is launched to choice the course
                case Constants.COURSES_REQUEST_CODE:
                    //User credentials are wrong. Remove periodic synchronization
                    SyncUtils.removePeriodicSync(Constants.AUTHORITY, Bundle.EMPTY, this);
                    showProgress(false);
                    break;
                case Constants.LOGIN_REQUEST_CODE:
                    finish();
                    break;
            }
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private void showProgress(final boolean show) {

        final View course_list = findViewById(R.id.courses_list_view);
        final View progressAnimation = findViewById(R.id.get_courses_status);

        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            progressAnimation.setVisibility(View.VISIBLE);
            progressAnimation.animate()
                    .setDuration(shortAnimTime)
                    .alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            progressAnimation.setVisibility(show ? View.VISIBLE : View.GONE);
                        }
                    });

            course_list.setVisibility(View.VISIBLE);
            course_list.animate()
                    .setDuration(shortAnimTime)
                    .alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            course_list.setVisibility(show ? View.GONE
                                    : View.VISIBLE);
                        }
                    });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressAnimation.setVisibility(show ? View.VISIBLE : View.GONE);
            course_list.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    @Override
    protected void onPause() {
        Constants.dbHelper.close();
        super.onPause();
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        //Initialize database
        try {
            Constants.dbHelper = new DataBaseHelper(this);
        } catch (Exception ex) {
            Log.e(Constants.APP_TAG, ex.getMessage());
        }
    }
}
