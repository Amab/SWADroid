package es.ugr.swad.swadroid;

/**
 * @author Alejandro Alcalde (elbauldelprogramador.com) on 15/03/15.
 */

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import es.ugr.swad.swadroid.database.DataBaseHelper;
import es.ugr.swad.swadroid.gui.CourseFragment;
import es.ugr.swad.swadroid.modules.Courses;
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
            getPackageManager().getApplicationInfo(
                    getPackageName(), 0);
//            isDebuggable = (ApplicationInfo.FLAG_DEBUGGABLE != 0);
//            isSWADMain = this instanceof SWADMain;
        } catch (Exception ex) {
            Log.e(Constants.APP_TAG, ex.getMessage());
        }
    }

    private void initDrawer() {

        this.disableLearningPattern();

        MaterialAccount account1 = new MaterialAccount(
                this.getResources(),
                "LMD",
                "Dep. Álgebra",
                R.drawable.photo,
                R.drawable.bamboo);
        MaterialAccount account2 = new MaterialAccount(
                this.getResources(),
                "CRIP",
                "Dep. Álgebra",
                R.drawable.photo2,
                R.drawable.mat2);
        MaterialAccount account3 = new MaterialAccount(
                this.getResources(),
                "MH",
                "Decsai",
                R.drawable.photo,
                R.drawable.mat3);

        addAccount(account1);
        addAccount(account2);
        addAccount(account3);

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
        addBottomSection(newSection("Bottom Section", R.mipmap.ic_settings_black_24dp, new Intent(this, PreferencesActivity.class)));

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
