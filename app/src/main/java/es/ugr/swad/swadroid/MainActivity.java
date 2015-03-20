package es.ugr.swad.swadroid;

/**
 * @author Alejandro Alcalde (elbauldelprogramador.com) on 15/03/15.
 */

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import es.ugr.swad.swadroid.database.DataBaseHelper;
import es.ugr.swad.swadroid.gui.CourseFragment;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialAccount;
import it.neokree.materialnavigationdrawer.elements.MaterialSection;
import it.neokree.materialnavigationdrawer.elements.listeners.MaterialAccountListener;

/**
 * @author Alejandro Alcalde (elbauldelprogramador.com)
 */
public class MainActivity extends MaterialNavigationDrawer implements MaterialAccountListener {

    /**
     * Database Helper.
     */
    protected static DataBaseHelper dbHelper;

    /**
     * Application preferences
     */
    Preferences prefs;

    @Override
    public void init(Bundle bundle) {
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

        try {
            //Initialize preferences
            prefs = new Preferences(this);

            //Initialize database
            dbHelper = new DataBaseHelper(this);
            getPackageManager().getApplicationInfo(
                    getPackageName(), 0);
//            isDebuggable = (ApplicationInfo.FLAG_DEBUGGABLE != 0);
//            isSWADMain = this instanceof SWADMain;
        } catch (Exception ex) {
            Log.e(Constants.APP_TAG, ex.getMessage());
        }
    }

    @Override
    public void onAccountOpening(MaterialAccount materialAccount) {
        Log.w(Constants.APP_TAG, "OnAccountOppening");
    }

    @Override
    public void onChangeAccount(MaterialAccount materialAccount) {
        Log.w(Constants.APP_TAG, "OnChangeAccount");
    }

    @Override
    protected void onPause() {
        dbHelper.close();
        super.onPause();
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        //Initialize database
        try {
            dbHelper = new DataBaseHelper(this);
        } catch (Exception ex) {
            Log.e(Constants.APP_TAG, ex.getMessage());
        }
    }
}
