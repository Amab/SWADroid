package es.ugr.swad.swadroid.sync;

import es.ugr.swad.swadroid.Global;
import es.ugr.swad.swadroid.Preferences;
import es.ugr.swad.swadroid.R;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.os.Bundle;

public class AccountAuthenticator extends AccountAuthenticatorActivity {	
	@Override
	protected void onCreate(Bundle icicle) {		
		super.onCreate(icicle);
	    Preferences prefs = new Preferences();
	    prefs.getPreferences(getBaseContext()); 
		Account account = new Account(getString(R.string.app_name), Global.getAccountType());
		AccountManager am = AccountManager.get(this);
		boolean accountCreated = am.addAccountExplicitly(account, getString(R.string.app_name), null);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
		 if (accountCreated) {  //Pass the new account back to the account manager
		  AccountAuthenticatorResponse response = extras.getParcelable(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE);
		  Bundle result = new Bundle();
		  result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
		  result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
		  response.onResult(result);
		  
		  //Configure automatic synchronization
    	  ContentResolver.setIsSyncable(account, Global.getAuthority(), 1);
    	  ContentResolver.setMasterSyncAutomatically(true);
    	  ContentResolver.setSyncAutomatically(account, Global.getAuthority(), true);
		 }
		}
		finish();
	}
	
}
