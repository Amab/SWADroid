/**
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package es.ugr.swad.swadroid.modules.login;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.gui.DialogFactory;
import es.ugr.swad.swadroid.gui.ProgressScreen;
import es.ugr.swad.swadroid.modules.account.CreateAccountActivity;
import es.ugr.swad.swadroid.modules.password.RecoverPassword;
import es.ugr.swad.swadroid.preferences.Preferences;
import es.ugr.swad.swadroid.utils.CryptoUtils;
import es.ugr.swad.swadroid.utils.Utils;


/**
 * 
 * @author Alejandro Alcalde <algui91@gmail.com>
 * @author Juan Miguel Boyero Corral <swadroid@gmail.com>
 *
 */
public class LoginActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = Constants.APP_TAG + " LoginActivity";

    private static List<String> serversList;

    // UI references for the login form.
    private EditText mIDView;
    private EditText mPasswordView;
    private EditText mServerTextView;
    private Spinner mServerView;
    private boolean mFromPreference = false;
    private ArrayAdapter<String> serverAdapter;
    private ProgressScreen mProgressScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_activity);
        
        mFromPreference = getIntent().getBooleanExtra("fromPreference", false);
        serversList = Arrays.asList(getResources().getStringArray(R.array.servers_array));

        setupLoginForm();
    }

    @Override
    protected void onResume() {
        super.onResume();

        String server = Preferences.getServer();

        if((server != null) && (!server.isEmpty()) &&
                !mServerView.getSelectedItem().equals(server)) {

            setSelectedServer(server);
        }
    }

    private void setupLoginForm() {
        View mLoginFormView = findViewById(R.id.create_account_form);
        View mProgressScreenView = findViewById(R.id.progress_screen);

        mProgressScreen = new ProgressScreen(mProgressScreenView, mLoginFormView,
                getString(R.string.login_progress_signing_in), this);

        Button mSignInButton = (Button) findViewById(R.id.sign_in_button);
        Button mLostPasswordButton = (Button) findViewById(R.id.lost_password);
        Button mCreateAccountButton = (Button) findViewById(R.id.create_account);
        
        mIDView = (EditText) findViewById(R.id.ID);
        mIDView.setText(Preferences.getUserID());

        mPasswordView = (EditText) findViewById(R.id.password);
        if (mPasswordView != null) {
            mPasswordView.setText("");
        }
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mServerTextView = (EditText) findViewById(R.id.serverEditText);
        mServerTextView.setText(Preferences.getServer());
        mServerTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                String value = mServerTextView.getText().toString();

                if (!hasFocus) {
                    if(value.isEmpty()) {
                        mServerTextView.setError(getString(R.string.noServer));
                    } else {
                        mServerTextView.setError(null);

                        if(value.startsWith("http://")) {
                            value = value.substring(7);
                        } else if(value.startsWith("https://")) {
                            value = value.substring(8);
                        }

                        setSelectedServer(value);
                        Preferences.setServer(value);

                        Log.i(TAG, "Server setted to " + Preferences.getServer());
                    }
                }
            }
        });

        mServerView = (Spinner) findViewById(R.id.serverSpinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        serverAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, serversList) {

            @Override
            public boolean isEnabled(int position){
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {

                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;

                if(position == 0) {
                    // Set the disable item text color
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        // Specify the layout to use when the list of choices appears
        serverAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mServerView.setAdapter(serverAdapter);
        mServerView.setOnItemSelectedListener(this);

        if (mSignInButton != null) {
            mSignInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    attemptLogin();
                }
            });
        }

        if (mLostPasswordButton != null) {
            mLostPasswordButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recoverPasswordDialog();
                }
            });
        }

        if (mCreateAccountButton != null) {
            mCreateAccountButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createAccount();
                }
            });
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid DNI, missing fields, etc.), the errors
     * are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Values for ID and password at the time of the login attempt.
        String idValue;
        String passwordValue;
        String serverValue;
        String toastMsg;

        // Reset errors.
        mIDView.setError(null);
        mPasswordView.setError(null);
        mServerTextView.setError(null);
        spinnerSetError(null);

        boolean cancel = false;
        View focusView = null;

        // Store values at the time of the login attempt.
        idValue = mIDView.getText().toString();
        passwordValue = mPasswordView.getText().toString();
        serverValue = mServerView.getSelectedItem().toString();

        if(serverValue.equals(serversList.get(0))) {
            spinnerSetError(getString(R.string.noServer));
            serverValue = "";
        } else if(serverValue.contains(getString(R.string.otherMsg))) {
            serverValue = mServerTextView.getText().toString().replaceFirst("^(http://|https://)","");
        }

        toastMsg =
                serverValue.equals(Constants.SWAD_UGR_SERVER) ? getString(R.string.error_password_summaryUGR)
                        : getString(R.string.error_invalid_password);

        // Check for a valid password.
        if (TextUtils.isEmpty(passwordValue)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (passwordValue.length() < 6) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
            if(passwordValue.length() == 4) {
                whyMyPasswordNotWorkDialog();
            } else {
                Toast.makeText(getApplicationContext(), toastMsg,
                        Toast.LENGTH_LONG).show();
            }
        } else if (Utils.isLong(passwordValue)) {
            mPasswordView.setError(getString(R.string.error_incorrect_password));
            focusView = mPasswordView;
            cancel = true;
            Toast.makeText(getApplicationContext(), toastMsg,
                           Toast.LENGTH_LONG).show();
        }

        // Check for a valid ID.
        if (TextUtils.isEmpty(idValue)) {
            mIDView.setError(getString(R.string.error_field_required));
            focusView = mIDView;
            cancel = true;
        }

        // Check for a valid server.
        if (TextUtils.isEmpty(serverValue)) {
            mServerTextView.setError(getString(R.string.error_field_required));
            focusView = mServerTextView;
            cancel = true;
        }
        
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            try {
                Preferences.setUserID(idValue);
                Preferences.setUserPassword(CryptoUtils.encryptPassword(passwordValue));
            } catch (NoSuchAlgorithmException e) {
                //error(TAG, e.getMessage(), e, true);
            }

            mProgressScreen.show();
            startActivityForResult(new Intent(this, Login.class), Constants.LOGIN_REQUEST_CODE);
        }
    }

    /**
     * Creates a new account
     */
    private void createAccount() {
        startActivityForResult(new Intent(this, CreateAccountActivity.class), Constants.CREATE_ACCOUNT_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Constants.LOGIN_REQUEST_CODE:
                    mProgressScreen.hide();
                    Login.getLoginInfo().setLogged(true);
                    setResult(RESULT_OK);
                    mFromPreference = false;
                    finish();
                    break;
                case Constants.RECOVER_PASSWORD_REQUEST_CODE:
                    Toast.makeText(getApplicationContext(), R.string.lost_password_success,
                                   Toast.LENGTH_LONG).show();
                    break;
                case Constants.CREATE_ACCOUNT_REQUEST_CODE:
                    Toast.makeText(getApplicationContext(), R.string.create_account_success,
                            Toast.LENGTH_LONG).show();
                    break;
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            switch (requestCode) {
                case Constants.LOGIN_REQUEST_CODE:
                    mProgressScreen.hide();
                    break;
                case Constants.RECOVER_PASSWORD_REQUEST_CODE:
                    Toast.makeText(this, R.string.lost_password_failure, Toast.LENGTH_LONG).show();
                    break;
                case Constants.CREATE_ACCOUNT_REQUEST_CODE:
                    break;
            }
        }
    }

    private void whyMyPasswordNotWorkDialog() {
        AlertDialog passwordNotWorkDialog =
                DialogFactory.createNeutralDialog(this,
                        R.layout.dialog_why_password,
                        R.string.why_password_dialog_title,
                        -1,
                        R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        });

        passwordNotWorkDialog.show();
    }

    private void recoverPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final EditText user = new EditText(getApplicationContext());
        user.setTextColor(Color.BLACK);
        user.setHintTextColor(Color.GRAY);
        user.setHint(getString(R.string.prompt_email));
        user.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        builder.setView(user)
               .setTitle(R.string.lost_password_dialog_title)
               .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       dialog.dismiss();
                       Intent i = new Intent(getBaseContext(), RecoverPassword.class);
                       i.putExtra(RecoverPassword.USER_TO_RECOVER, user.getText().toString());
                       startActivityForResult(i, Constants.RECOVER_PASSWORD_REQUEST_CODE);
                   }
               })
               .setNegativeButton(R.string.cancelMsg, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       dialog.dismiss();
                   }
               })
               .setCancelable(true);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        if (mFromPreference) {
            Intent i = new Intent();
            i.setAction(Intent.ACTION_MAIN);
            i.addCategory(Intent.CATEGORY_HOME);
            startActivity(i);
            finish();
        }
        super.onDestroy();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        setServer(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //Do nothing
    }

    private void setServer(int position) {
        String serverValue = serverAdapter.getItem(position);

        //Reset password error
        mPasswordView.setError(null);

        if (serverValue.isEmpty() || serverValue.equals(serversList.get(0))) {
            spinnerSetError(getString(R.string.noServer));
        } else if (Constants.SWAD_UGR_SERVER.equals(serverValue)) {
            mPasswordView.setError(getString(R.string.error_password_summaryUGR));
            Preferences.setServer(serverValue);
        } else if(serverValue.contains(getString(R.string.otherMsg))) {
            mServerTextView.setText(Preferences.getServer());
            mServerTextView.setVisibility(View.VISIBLE);
        } else {
            mServerTextView.setVisibility(View.GONE);
            Preferences.setServer(serverValue);
        }

        Log.i(TAG, "Server setted to " + Preferences.getServer());
    }

    private void setSelectedServer(String server) {
        int serverPosition;

        if(serversList.contains(server)) {
            serverPosition = serverAdapter.getPosition(server);
        } else {
            serverPosition = serversList.size() - 1;
        }

        mServerView.setSelection(serverPosition);
    }

    private void spinnerSetError(String error) {
        View selectedView = mServerView.getSelectedView();

        if ((selectedView != null) && (selectedView instanceof TextView)) {
            TextView selectedTextView = (TextView) selectedView;

            if ((error == null) || (error.isEmpty())) {
                selectedTextView.setError(null);
            } else {
                selectedTextView.setError(error);
            }
        }
    }
}
