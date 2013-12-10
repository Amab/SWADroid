package es.ugr.swad.swadroid.sync;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.os.Bundle;
import android.util.Log;
import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.Preferences;
import es.ugr.swad.swadroid.R;

public class AccountAuthenticator extends AccountAuthenticatorActivity {
    /**
     * Login tag name for Logcat
     */
    public static final String TAG = Constants.APP_TAG + " AccountAuthenticator";
    
    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Account account = new Account(getString(R.string.app_name), Constants.ACCOUNT_TYPE);
        AccountManager am = AccountManager.get(this);
        boolean accountCreated = am.addAccountExplicitly(account, getString(R.string.app_name), null);

        Bundle extras = getIntent().getExtras();
        
        Log.d(TAG, "accountCreated=" + accountCreated);
        Log.d(TAG, "extras=" + extras);
        
        if (accountCreated) {  //Pass the new account back to the account manager
            if (extras != null) {
	            AccountAuthenticatorResponse response = extras.getParcelable(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE);
	            Bundle result = new Bundle();
	            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
	            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
	            response.onResult(result);
            }
            
            Log.i(TAG, "Account for automatic synchronization created successfully");
        } else {
        	Log.w(TAG, "Account for automatic synchronization was not created");
        }

        //Configure automatic synchronization
        ContentResolver.setIsSyncable(account, Constants.AUTHORITY, 1);
        Log.i(TAG, "Account setted as syncable");
        
        ContentResolver.setMasterSyncAutomatically(true);        
        Log.i(TAG, "Master auto-sync setting enabled");
        
        SyncUtils.addPeriodicSync(Constants.AUTHORITY, Bundle.EMPTY, Long.valueOf(Preferences.getSyncTime()), this);

        finish();
    }

}
