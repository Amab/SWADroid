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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import java.util.Arrays;
import java.util.List;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.gui.ProgressScreen;
import es.ugr.swad.swadroid.preferences.Preferences;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.gui.DialogFactory;
import es.ugr.swad.swadroid.utils.Utils;


/**
 * 
 * @author Alejandro Alcalde <algui91@gmail.com>
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 *
 */
public class CreateAccountActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = Constants.APP_TAG + " CreateAccountActivity";

    private static List<String> serversList;

    private EditText mNicknameView;
    private EditText mEmailView;
    private EditText mPasswordView;
    private EditText mServerTextView;
    private Spinner mServerView;
    private ArrayAdapter<String> serverAdapter;
    private ProgressScreen mProgressScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.create_account_activity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        serversList = Arrays.asList(getResources().getStringArray(R.array.servers_array));

        setupCreateAccountForm();
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

    private void setupCreateAccountForm() {
        View mLoginFormView = findViewById(R.id.create_account_form);
        View mProgressScreenView = findViewById(R.id.progress_screen);
        mProgressScreen = new ProgressScreen(mProgressScreenView, mLoginFormView,
                getString(R.string.createAccountProgressDescription), this);
        Button mCreateAccountButton = (Button) findViewById(R.id.create_account_button);
        
        mNicknameView = (EditText) findViewById(R.id.nickname);
        mNicknameView.setText(Preferences.getUserID());

        mPasswordView = (EditText) findViewById(R.id.password);
        if (mPasswordView != null) {
            mPasswordView.setText("");
        }
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

        mServerTextView = (EditText) findViewById(R.id.serverEditText);
        mServerTextView.setText(Preferences.getServer());
        mServerTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                String value = mServerTextView.getText().toString();

                if (!hasFocus) {
                    if (value.isEmpty()) {
                        mServerTextView.setError(getString(R.string.noServer));
                    } else {
                        mServerTextView.setError(null);

                        if (value.startsWith("http://")) {
                            value = value.substring(7);
                        } else if (value.startsWith("https://")) {
                            value = value.substring(8);
                        }

                        setSelectedServer(value);
                        Preferences.setServer(value);

                        Log.i(TAG, "Server setted to " + Preferences.getServer());
                    }
                }
            }
        });

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
     * Creates a new account
     */
    private void createAccount() {
        Intent intent;

        // Values for text field at the time of the create account attempt.
        String nicknameValue;
        String emailValue;
        String passwordValue;
        String serverValue;

        // Reset errors.
        mNicknameView.setError(null);
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mServerTextView.setError(null);

        // Store values at the time of the login attempt.
        nicknameValue = mNicknameView.getText().toString();
        passwordValue = mPasswordView.getText().toString();
        emailValue = mEmailView.getText().toString();
        serverValue = mServerView.getSelectedItem().toString();
        if(serverValue.equals(serversList.get(0))) {
            spinnerSetError(getString(R.string.noServer));
            serverValue = "";
        } else if(serverValue.contains(getString(R.string.otherMsg))) {
            serverValue = mServerTextView.getText().toString().replaceFirst("^(http://|https://)","");
        }

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

        // Check for a valid server.
        if (TextUtils.isEmpty(serverValue)) {
            mServerTextView.setError(getString(R.string.error_field_required));
            focusView = mServerTextView;
            cancel = true;
        }
        
        if (cancel) {
            // There was an error; don't attempt create account and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            mProgressScreen.show();
            intent = new Intent(this, CreateAccount.class);
            intent.putExtra("userNickname", nicknameValue);
            intent.putExtra("userEmail", emailValue);
            intent.putExtra("userPassword", passwordValue);
            startActivityForResult(intent, Constants.CREATE_ACCOUNT_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String errorMsg = "";
        AlertDialog errorDialog;

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Constants.CREATE_ACCOUNT_REQUEST_CODE:
                    mProgressScreen.hide();
                    //Finished successfully
                    if (CreateAccount.getUserCode() > 0) {
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
                                errorMsg, null, false, new DialogInterface.OnClickListener() {

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
                    mProgressScreen.hide();
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
