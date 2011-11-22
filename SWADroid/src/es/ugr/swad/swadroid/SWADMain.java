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

package es.ugr.swad.swadroid;

import com.android.dataframework.DataFramework;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;
import es.ugr.swad.swadroid.model.DataBaseHelper;
import es.ugr.swad.swadroid.modules.Login;
import es.ugr.swad.swadroid.ssl.SecureConnection;

/**
 * Main class of the application.
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class SWADMain extends Activity {
    /**
     * Application preferences.
     */
    protected static Preferences prefs = new Preferences();
    
    /**
     * Database Helper.
     */
    protected static DataBaseHelper dbHelper;
    
    /**
     * Database Framework.
     */
    protected static DataFramework db;

    /**
     * Shows an error message.
     * @param message Error message to show.
     */
    public void error(String message) {
        new AlertDialog.Builder(this).setTitle(R.string.title_error_dialog)
                .setMessage(message)
                .setNeutralButton(R.string.close_dialog, null)
                .setIcon(R.drawable.erroricon).show();
                /*.setNeutralButton(R.string.close_dialog,
                new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                SWADMain.this.finish();
                }
                })*/
    }

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu()
	 */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected()
	 */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
	        case R.id.login_menu:
	            Intent loginActivity = new Intent(getBaseContext(),
	                    Login.class);
	            startActivityForResult(loginActivity, Global.LOGIN_REQUEST_CODE);
	            return true;
            case R.id.preferences_menu:
                Intent settingsActivity = new Intent(getBaseContext(),
                        Preferences.class);
                startActivity(settingsActivity);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult()
	 */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            //Bundle extras = data.getExtras();

            switch(requestCode) {
                case Global.LOGIN_REQUEST_CODE:
                     Global.setLogged(true);
                     Toast.makeText(getBaseContext(),
                             R.string.loginSuccessfulMsg,
                             Toast.LENGTH_LONG).show();
                     Log.d(Global.LOGIN_TAG, getString(R.string.loginSuccessfulMsg));
                     /*Log.d(Global.LOGIN_TAG, "userCode=" + User.getUserCode());
                     Log.d(Global.LOGIN_TAG, "userTypeCode=" + User.getUserTypeCode());
                     Log.d(Global.LOGIN_TAG, "userTypeName=" + User.getUserTypeName());
                     Log.d(Global.LOGIN_TAG, "userID=" + User.getUserID());
                     Log.d(Global.LOGIN_TAG, "userFirstName=" + User.getUserFirstName());
                     Log.d(Global.LOGIN_TAG, "userSurname1=" + User.getUserSurname1());
                     Log.d(Global.LOGIN_TAG, "userSurname2=" + User.getUserSurname2());
                     Log.d(Global.LOGIN_TAG, "wsKey=" + User.getWsKey());*/
                     break;
            }
        }
    }

	/* (non-Javadoc)
	 * @see android.app.Activity#onStart()
	 */
    @Override
    protected void onStart() {
        super.onStart();
        prefs.getPreferences(getBaseContext());
    }

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate()
	 */
    @Override
    public void onCreate(Bundle icicle) {
        try {
            super.onCreate(icicle);
            Window w = getWindow();
            w.requestFeature(Window.FEATURE_LEFT_ICON);
            setContentView(R.layout.main);
            w.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.swadroid);
            
            db = DataFramework.getInstance();
            db.open(this, this.getPackageName());
            dbHelper = new DataBaseHelper(db);
            
            prefs.getPreferences(getBaseContext());
            SecureConnection.initSecureConnection();
        } catch (Exception ex) {
            Log.e(ex.getClass().getSimpleName(), ex.toString());
            error(ex.toString());
            ex.printStackTrace();
        }
    }

	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		dbHelper.close();
		super.onDestroy();
	}
}
