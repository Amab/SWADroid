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

package es.ugr.swad.swadroid.modules.account;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.Preferences;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.SWADroidTracker;
import es.ugr.swad.swadroid.gui.DialogFactory;
import es.ugr.swad.swadroid.utils.Utils;


/**
 * 
 * @author Alejandro Alcalde <algui91@gmail.com>
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 *
 */
public class CreateAccountActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static final String TAG = Constants.APP_TAG + " CreateAccountActivity";

    private static List<String> serversList;

    private boolean mLoginError = false;
    private boolean showServerDialog = true;

    // UI references for the create account form.
    private View mLoginFormView;
    private View mLoginStatusView;
    private TextView mLoginStatusMessageView;
    private EditText mNicknameView;
    private EditText mEmailView;
    private EditText mPasswordView;
    private Spinner mServerView;
    private ArrayAdapter<CharSequence> serverAdapter;
    private AlertDialog serverDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.create_account_activity);

        serversList = Arrays.asList(getResources().getStringArray(R.array.servers_array));

        setupCreateAccountForm();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!mServerView.getSelectedItem().equals(Preferences.getServer())) {
            setSelectedServer(Preferences.getServer());
        }
    }

    private void setupCreateAccountForm() {

        mLoginFormView = findViewById(R.id.login_form);
        mLoginStatusView = findViewById(R.id.login_status);
        Button mCreateAccountButton = (Button) findViewById(R.id.create_account_button);
        
        mNicknameView = (EditText) findViewById(R.id.nickname);
        mNicknameView.setText(Preferences.getUserID());

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setText("");
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    createAccount();
                    return true;
                }
                return false;
            }
        });

        mEmailView = (EditText) findViewById(R.id.email);

        mServerView = (Spinner) findViewById(R.id.serverSpinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        serverAdapter = ArrayAdapter.createFromResource(this,
                R.array.servers_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        serverAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mServerView.setAdapter(serverAdapter);
        mServerView.setOnItemSelectedListener(this);

        if (serverAdapter.getItem(0).equals("swad.ugr.es"))
            mPasswordView.setError(getString(R.string.error_password_summaryUGR));

        mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

        mCreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount();
            }
        });
    }

    /**
     * Creates a new account
     */
    public void createAccount() {
        SWADroidTracker.sendScreenView(getApplicationContext(), "SWADroid CreateAccount");

        Intent intent;

        // Values for text field at the time of the create account attempt.
        String nicknameValue;
        String emailValue;
        String passwordValue;

        // Reset errors.
        mNicknameView.setError(null);
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        nicknameValue = mNicknameView.getText().toString();
        passwordValue = mPasswordView.getText().toString();
        emailValue = mEmailView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(passwordValue)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (passwordValue.length() < 6) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        } else if (Utils.isLong(passwordValue)) {
            mPasswordView.setError(getString(R.string.error_incorrect_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid nickname.
        if (TextUtils.isEmpty(nicknameValue)) {
            mNicknameView.setError(getString(R.string.error_field_required));
            focusView = mNicknameView;
            cancel = true;
        } else if(!nicknameValue.startsWith("@")) {
            mNicknameView.setError(getString(R.string.error_nickname_syntax));
            focusView = mNicknameView;
            cancel = true;
        }

        // Check for a valid email.
        if (TextUtils.isEmpty(emailValue)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }
        
        if (cancel) {
            // There was an error; don't attempt create account and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the create account attempt.
            mLoginStatusMessageView.setText(R.string.createAccountProgressDescription);

            showProgress(true);
            intent = new Intent(this, CreateAccount.class);
            intent.putExtra("userNickname", nicknameValue);
            intent.putExtra("userEmail", emailValue);
            intent.putExtra("userPassword", passwordValue);
            startActivityForResult(intent, Constants.CREATE_ACCOUNT_REQUEST_CODE);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginStatusView.setVisibility(View.VISIBLE);
            mLoginStatusView.animate()
                    .setDuration(shortAnimTime)
                    .alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
                        }
                    });

            mLoginFormView.setVisibility(View.VISIBLE);
            mLoginFormView.animate()
                    .setDuration(shortAnimTime)
                    .alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mLoginFormView.setVisibility(mLoginError ? View.VISIBLE
                                    : View.GONE);
                        }
                    });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String errorMsg = "";
        AlertDialog errorDialog;

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Constants.CREATE_ACCOUNT_REQUEST_CODE:
                    showProgress(false);
                    //Finished successfully
                    if (CreateAccount.getUserCode() > 0) {

                        mLoginError = false;
                        Toast.makeText(getApplicationContext(), R.string.create_account_success,
                                Toast.LENGTH_LONG).show();
                        finish();

                        //Finished with errors
                    } else {
                        switch (CreateAccount.getUserCode().intValue()) {
                            case -1:
                                errorMsg = getString(R.string.errorNicknameNotValid);
                                break;
                            case -2:
                                errorMsg = getString(R.string.errorNicknameRegistered);
                                break;
                            case -3:
                                errorMsg = getString(R.string.errorEmailNotValid);
                                break;
                            case -4:
                                errorMsg = getString(R.string.errorEmailRegistered);
                                break;
                            case -5:
                                errorMsg = getString(R.string.errorPasswordNotValid);
                                break;
                        }

                        errorDialog = DialogFactory.createErrorDialog(this, TAG,
                                errorMsg, null, false, false, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Do nothing
                            }
                        });
                        errorDialog.show();
                        break;
                    }
            }
            switch (requestCode) {
                case Constants.CREATE_ACCOUNT_REQUEST_CODE:
                    mLoginError = true;
                    showProgress(false);
                    break;
            }
        }
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
        String serverValue = serverAdapter.getItem(position).toString();

        //Reset password error
        mPasswordView.setError(null);
        if ("swad.ugr.es".equals(serverValue))
            mPasswordView.setError(getString(R.string.error_password_summaryUGR));

        if(serverValue.contains(getString(R.string.otherMsg)) && showServerDialog) {
            serverDialog = DialogFactory.createPositiveNegativeDialog(this,
                    R.drawable.ic_launcher_swadroid ,
                    R.layout.dialog_server,
                    R.string.serverTitle_preferences ,
                    -1,
                    R.string.saveMsg,
                    R.string.cancelMsg,
                    true,
                    null,
                    null,
                    null);

            serverDialog.setOnShowListener(showListener);
            serverDialog.show();
        } else {
            Preferences.setServer(serverValue);
        }

        showServerDialog = true;

        Log.i(TAG, "Server setted to " + Preferences.getServer());
    }

    private final DialogInterface.OnShowListener showListener = new DialogInterface.OnShowListener() {
        @Override
        public void onShow(DialogInterface dialog) {
            EditText bodyEditText = (EditText) serverDialog.findViewById(R.id.server_body_text);
            Button b = serverDialog.getButton(AlertDialog.BUTTON_POSITIVE);

            b.setOnClickListener(positiveClickListener);
            bodyEditText.setText(Preferences.getServer());
        }
    };

    private final View.OnClickListener positiveClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            EditText bodyEditText = (EditText) serverDialog.findViewById(R.id.server_body_text);
            String bodyValue = bodyEditText.getText().toString();

            if(bodyValue.isEmpty()) {
                bodyEditText.setError(getString(R.string.noServer));
            } else {
                if(bodyValue.startsWith("http://")) {
                    bodyValue = bodyValue.substring(7);
                } else if(bodyValue.startsWith("https://")) {
                    bodyValue = bodyValue.substring(8);
                }

                setSelectedServer(bodyValue);
                Preferences.setServer(bodyValue);

                serverDialog.dismiss();

                Log.i(TAG, "Server setted to " + Preferences.getServer());
            }
        }
    };

    private void setSelectedServer(String server) {
        int serverPosition;

        showServerDialog = false;

        if(serversList.contains(server)) {
            serverPosition = serverAdapter.getPosition(server);
        } else {
            serverPosition = serversList.size() - 1;
        }

        mServerView.setSelection(serverPosition);

        showServerDialog = true;
    }
    
}
