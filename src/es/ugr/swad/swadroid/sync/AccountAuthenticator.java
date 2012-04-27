package es.ugr.swad.swadroid.sync;

import es.ugr.swad.swadroid.Preferences;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.os.Bundle;

public class AccountAuthenticator extends AccountAuthenticatorActivity {
	@Override
	protected void onCreate(Bundle icicle) {		
		super.onCreate(icicle);
	    Preferences prefs = new Preferences();
	    prefs.getPreferences(getBaseContext()); 
		Account account = new Account(prefs.getUserID(), "es.ugr.swad.swadroid");
		AccountManager am = AccountManager.get(this);
		boolean accountCreated = am.addAccountExplicitly(account, prefs.getUserPassword(), null);
		 
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
		 if (accountCreated) {  //Pass the new account back to the account manager
		  AccountAuthenticatorResponse response = extras.getParcelable(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE);
		  Bundle result = new Bundle();
		  result.putString(AccountManager.KEY_ACCOUNT_NAME, prefs.getUserID());
		  result.putString(AccountManager.KEY_ACCOUNT_TYPE, "es.ugr.swad.swadroid");
		  response.onResult(result);
		 }
		 finish();
		}
	}
	
}
