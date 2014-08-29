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
package es.ugr.swad.swadroid.modules;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnShowListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.gui.DialogFactory;
import es.ugr.swad.swadroid.model.User;
import es.ugr.swad.swadroid.webservices.SOAPClient;

/**
 * Module for send messages.
 *
 * @author Helena Rodriguez Gijon <hrgijon@gmail.com>
 * @author Antonio Aguilera Malagon <aguilerin@gmail.com>
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class Notices extends Module {
    /**
     * Messages tag name for Logcat
     */
    private static final String TAG = Constants.APP_TAG + " Notice";

    /**
     * Notice's body
     */
    private String body;
    private Dialog noticeDialog;

    /**
     * Selected course code
     */
    private long selectedCourseCode = 0;

    /*private final OnClickListener positiveClickListener = new OnClickListener() {
		@Override
        public void onClick(DialogInterface dialog, int which) {
            if (isDebuggable) {
                Log.i(TAG, "on click positive before send request to server");
            }

            try {
                /*if(isDebuggable) {
                    Log.i(TAG, "selectedCourseCode = " + Long.toString(courseCode));
				}*/

            /*    runConnection();
            } catch (Exception e) {
                String errorMsg = getString(R.string.errorServerResponseMsg);
                error(TAG, errorMsg, e, true);
            }
        }
    };*/
    
    private final View.OnClickListener positiveClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        	if (isDebuggable) {
                Log.i(TAG, "on click positive before send request to server");
            }

            try {
                /*if(isDebuggable) {
                    Log.i(TAG, "selectedCourseCode = " + Long.toString(courseCode));
				}*/

                runConnection();
            } catch (Exception e) {
                String errorMsg = getString(R.string.errorServerResponseMsg);
                error(TAG, errorMsg, e, true);
            }
        }
    };

    private final OnClickListener negativeClickListener = new OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            finish();
        }
    };
    
    private final OnCancelListener cancelClickListener = new DialogInterface.OnCancelListener() {
        public void onCancel(DialogInterface dialog) {
            setResult(RESULT_CANCELED);
            finish();
        }
    };
    
    private final OnShowListener showListener = new DialogInterface.OnShowListener() {
        @Override
        public void onShow(DialogInterface dialog) {
            Button b = ((AlertDialog) noticeDialog).getButton(AlertDialog.BUTTON_POSITIVE);
            b.setOnClickListener(positiveClickListener);
        }
    };

    private void launchNoticeDialog() {       
        noticeDialog = DialogFactory.createPositiveNegativeDialog(this,
        		R.drawable.announce,
        		R.layout.dialog_notice,
        		R.string.noticesModuleLabel,
        		-1,
        		R.string.sendMsg,
        		R.string.cancelMsg,
        		false,
        		//positiveClickListener,
        		null,
        		negativeClickListener,
        		cancelClickListener);
        
        noticeDialog.setOnShowListener(showListener);
        noticeDialog.show();
    }

    @Override
    protected void requestService() throws Exception {

        readData();

        createRequest(SOAPClient.CLIENT_TYPE);

        addParam("wsKey", Constants.getLoggedUser().getWsKey());
        addParam("courseCode", (int) selectedCourseCode);
        addParam("body", body);

        sendRequest(User.class, false);
        if (result != null) {
            setResult(RESULT_OK);
        }
    }

    @Override
    protected void connect() {
        String progressDescription = getString(R.string.noticesModuleLabel);
        int progressTitle = R.string.noticesModuleLabel;

        startConnection(false, progressDescription, progressTitle);

        Toast.makeText(this, R.string.publishingNotice, Toast.LENGTH_SHORT).show();
        Log.i(TAG, getString(R.string.publishingNotice));

    }

    @Override
    protected void postConnect() {
        String noticeSended = getString(R.string.noticePublished);
        Toast.makeText(this, noticeSended, Toast.LENGTH_LONG).show();
        Log.i(TAG, noticeSended);
        noticeDialog.dismiss();
        finish();
    }

    private void readData() {
        EditText bd = (EditText) noticeDialog.findViewById(R.id.notice_body_text);
        body = bd.getText().toString();
    }

    private void writeData() {
        EditText bd = (EditText) noticeDialog.findViewById(R.id.notice_body_text);
        bd.setText(body);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setMETHOD_NAME("sendNotice");
        getSupportActionBar().hide();
    }

    @Override
    protected void onPause() {
        super.onPause();
        noticeDialog.dismiss();
    }

    @Override
    protected void onStart() {
        super.onStart();
        selectedCourseCode = Constants.getSelectedCourseCode();
        launchNoticeDialog();
    }

    @Override
    protected void onError() {

    }

    /* (non-Javadoc)
     * @see android.app.Activity#onRestoreInstanceState(android.os.Bundle)
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        body = savedInstanceState.getString("body");

        writeData();

        super.onRestoreInstanceState(savedInstanceState);
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        readData();

        outState.putString("body", body);

        super.onSaveInstanceState(outState);
    }

}