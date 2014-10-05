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

package es.ugr.swad.swadroid;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.security.NoSuchAlgorithmException;

import es.ugr.swad.swadroid.gui.DialogFactory;
import es.ugr.swad.swadroid.modules.Login;
import es.ugr.swad.swadroid.modules.RecoverPassword;
import es.ugr.swad.swadroid.utils.Crypto;
import es.ugr.swad.swadroid.utils.Utils;


/**
 * 
 * @author Alejandro Alcalde <algui91@gmail.com>
 *
 */
public class LoginActivity extends ActionBarActivity implements OnClickListener {

    public static final String TAG = Constants.APP_TAG + " LoginActivity";

    private boolean mLoginError = false;
    // UI references for the login form.
    private EditText mDniView;
    private EditText mPasswordView;
    private EditText mServerView;
    private View mLoginFormView;
    private View mLoginStatusView;
    // private View mMainScreenView;
    private TextView mLoginStatusMessageView;
    private TextView mWhyPasswordText;
    private TextView mLostPasswordText;
    private boolean mFromPreferece = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_activity);
        
        mFromPreferece = getIntent().getBooleanExtra("fromPreference", false);
        setupLoginForm();
    }

    private void setupLoginForm() {
        
        mLoginFormView = findViewById(R.id.login_form);
        mLoginStatusView = findViewById(R.id.login_status);
        mWhyPasswordText = (TextView) findViewById(R.id.why_password);
        mLostPasswordText = (TextView) findViewById(R.id.lost_password);
        
        SpannableString lostPasswordUnderline = new SpannableString(getString(R.string.lost_password));
        lostPasswordUnderline.setSpan(new UnderlineSpan(), 0, lostPasswordUnderline.length(), 0);
        mLostPasswordText.setText(lostPasswordUnderline);
        
        SpannableString whyPasswordUnderline = new SpannableString(getString(R.string.why_password));
        whyPasswordUnderline.setSpan(new UnderlineSpan(), 0, whyPasswordUnderline.length(), 0);
        mWhyPasswordText.setText(whyPasswordUnderline);
        
        
        mDniView = (EditText) findViewById(R.id.DNI);
        mDniView.setText(Preferences.getUserID());

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setText("");
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

        mServerView = (EditText) findViewById(R.id.server);
        mServerView.setText(Preferences.getServer());
        if (mServerView.getText().toString().equals(Constants.DEFAULT_SERVER)) mPasswordView.setError(getString(R.string.error_password_summaryUGR));

        mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        
        mWhyPasswordText.setOnClickListener(this);
        mLostPasswordText.setOnClickListener(this);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid DNI, missing fields, etc.), the errors
     * are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        SWADroidTracker.sendScreenView(getApplicationContext(), "SWADroid Login");

        // Values for DNI and password at the time of the login attempt.
        String DniValue;
        String passwordValue;
        String serverValue;
        String toastMsg;

        // Reset errors.
        mDniView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        DniValue = mDniView.getText().toString();
        passwordValue = mPasswordView.getText().toString();
        serverValue = mServerView.getText().toString();
        toastMsg =
                mServerView.getText().toString().equals("swad.ugr.es") ? getString(R.string.error_password_summaryUGR)
                        : getString(R.string.error_invalid_password);

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(passwordValue)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if ((passwordValue.length() < 6)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
            Toast.makeText(getApplicationContext(), toastMsg,
                           Toast.LENGTH_LONG).show();
        } else if (Utils.isLong(passwordValue)) {
            mPasswordView.setError(getString(R.string.error_incorrect_password));
            focusView = mPasswordView;
            cancel = true;
            Toast.makeText(getApplicationContext(), toastMsg,
                           Toast.LENGTH_LONG).show();
        }

        // Check for a valid DNI.
        if (TextUtils.isEmpty(DniValue)) {
            mDniView.setError(getString(R.string.error_field_required));
            focusView = mDniView;
            cancel = true;
        }
        
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
            try {
                Preferences.setUserID(DniValue);
                Preferences.setUserPassword(Crypto.encryptPassword(passwordValue));
                Preferences.setServer(serverValue);
            } catch (NoSuchAlgorithmException e) {
                // TODO, solucionar
                //error(TAG, e.getMessage(), e, true);
            }
            showProgress(true);
            startActivityForResult(new Intent(this, Login.class), Constants.LOGIN_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Constants.LOGIN_REQUEST_CODE:
                    showProgress(false);
                    Login.setLogged(true);
                    setResult(RESULT_OK);
                    mFromPreferece = false;
                    mLoginError = false;
                    finish();
                    break;
                case Constants.RECOVER_PASSWORD_REQUEST_CODE:
                    Toast.makeText(getApplicationContext(), R.string.lost_password_success,
                                   Toast.LENGTH_LONG).show();
                    break;
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            switch (requestCode) {
                case Constants.LOGIN_REQUEST_CODE:
                    mLoginError = true;
                    showProgress(false);
                    break;
                case Constants.RECOVER_PASSWORD_REQUEST_CODE:
                    Toast.makeText(this, R.string.lost_password_failure, Toast.LENGTH_LONG).show();
                    break;
            }
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

    private void whyMyPasswordNotWorkDialog() {
        SWADroidTracker.sendScreenView(getApplicationContext(), "SWADroid WhyMyPasswordNotWork");

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
        SWADroidTracker.sendScreenView(getApplicationContext(), "SWADroid RecoverPassword");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final EditText user = new EditText(getApplicationContext());
        user.setTextColor(Color.BLACK);
        user.setHint(getString(R.string.prompt_email));

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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.why_password:
                whyMyPasswordNotWorkDialog();
                break;
            case R.id.sign_in_button:
                attemptLogin();
                break;
            case R.id.lost_password:
                recoverPasswordDialog();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if (mFromPreferece) {
            Intent i = new Intent();
            i.setAction(Intent.ACTION_MAIN);
            i.addCategory(Intent.CATEGORY_HOME);
            startActivity(i);
            finish();
        }
        super.onDestroy();
    }
    
}
