/**
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

package es.ugr.swad.swadroid.modules;

import android.os.Bundle;
import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.model.User;
import es.ugr.swad.swadroid.webservices.SOAPClient;

/**
 * Recover password module in case user does not remember it
 * 
 * @author Alejandro Alcalde <algui91@gmail.com>
 */
public class RecoverPassword extends Module {

    public static final String TAG = Constants.APP_TAG + " RecoverPassword";

	public static final String USER_TO_RECOVER =  "es.ugr.swad.swadroid.USER_TO_RECOVER";

    /**
     * User ID.
     */
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setMETHOD_NAME("getNewPassword");
    }

    @Override
    protected void onStart() {
        super.onStart();
        connect();
    }

    @Override
    protected void requestService() throws Exception {

        createRequest(SOAPClient.CLIENT_TYPE);
        addParam("userID", getIntent().getStringExtra(USER_TO_RECOVER));
        addParam("appKey", Constants.SWAD_APP_KEY);
        sendRequest(User.class, true);

        // if (result != null
        // &&
        // "getNewPasswordOut{success=1;}".equalsIgnoreCase(result.toString().trim()))
        // {
        // KvmSerializable ks = (KvmSerializable) result;
        // SoapObject soap = (SoapObject) result;
        // }

        // Request finalized without errors
        setResult(RESULT_OK);
    }

    @Override
    protected void connect() {
        String progressDescription = getString(R.string.recoverPasswordProgressDescription);
        int progressTitle = R.string.recoverPasswordProgressTitle;

        startConnection(false, progressDescription, progressTitle);
    }

    @Override
    protected void postConnect() {
        finish();
    }

    @Override
    protected void onError() {
    }

}
