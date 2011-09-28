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

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.ksoap2.SoapFault;
import org.xmlpull.v1.XmlPullParserException;

import es.ugr.swad.swadroid.Global;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.model.User;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

/**
 * Module for send messages.
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class Messages extends Module {
    /**
     * Messages tag name for Logcat
     */
    public static final String TAG = Global.APP_TAG + " Messages";
    /**
     * Message code
     */
    private Long notificationCode;
    /**
     * Message's receivers
     */
    private String receivers;
    /**
     * Message's subject
     */
    private String subject;
    /**
     * Message's body
     */
    private String body;
    private Dialog messageDialog;
	private OnClickListener positiveClickListener = new OnClickListener() {
		public void onClick(View v) {
			try {										
				if(isDebuggable) {
					Log.d(TAG, "notificationCode = " + Long.toString(notificationCode));
				}

				runConnection();
			} catch (Exception ex) {
            	String errorMsg = getString(R.string.errorServerResponseMsg);
				error(errorMsg);
				
        		if(isDebuggable) {
        			Log.e(ex.getClass().getSimpleName(), errorMsg);        		
        			ex.printStackTrace();
        		}
	        }				
		}
	};
	private OnClickListener negativeClickListener = new OnClickListener() {
		public void onClick(View v) {
			finish();
		}
	};
    
	/* (non-Javadoc)
	 * @see es.ugr.swad.swadroid.modules.Module#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setMETHOD_NAME("sendMessage");
	}

	/* (non-Javadoc)
	 * @see es.ugr.swad.swadroid.modules.Module#onStart()
	 */
	@Override
	protected void onStart() {
		messageDialog = new Dialog(this);
		Button acceptButton, cancelButton;
		EditText receiversText, subjectText;
		
		super.onStart();
		notificationCode = getIntent().getLongExtra("notificationCode", 0);

		messageDialog.setTitle(R.string.messagesModuleLabel);
		messageDialog.setContentView(R.layout.messages_dialog);
		messageDialog.setCancelable(true);
		
		messageDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		
		acceptButton = (Button) messageDialog.findViewById(R.id.message_button_accept);
		acceptButton.setOnClickListener(positiveClickListener);
		
		cancelButton = (Button) messageDialog.findViewById(R.id.message_button_cancel);
		cancelButton.setOnClickListener(negativeClickListener);
		
		if(notificationCode != 0) {
			subject = getIntent().getStringExtra("summary");
			
			receiversText = (EditText) messageDialog.findViewById(R.id.message_receivers_text);
			subjectText = (EditText) messageDialog.findViewById(R.id.message_subject_text);

			subjectText.setText("Re: " + subject);
			receiversText.setVisibility(View.GONE);
		}
		
		messageDialog.show();
	}

	/* (non-Javadoc)
	 * @see es.ugr.swad.swadroid.modules.Module#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
		messageDialog.dismiss();
	}

	private void readData() {
		EditText rcv = (EditText) messageDialog.findViewById(R.id.message_receivers_text);
		receivers = rcv.getText().toString();
		
		EditText subj = (EditText) messageDialog.findViewById(R.id.message_subject_text);
		subject = subj.getText().toString();
		
		EditText bd = (EditText) messageDialog.findViewById(R.id.message_body_text);
		body = bd.getText().toString();
	}
	
	/* (non-Javadoc)
	 * @see es.ugr.swad.swadroid.modules.Module#requestService()
	 */
	@Override
	protected void requestService() throws NoSuchAlgorithmException,
			IOException, XmlPullParserException, SoapFault,
			IllegalAccessException, InstantiationException {
		
		readData();

		createRequest();
        addParam("wsKey", User.getWsKey());
        addParam("messageCode", notificationCode.intValue());
        addParam("to", receivers);
        addParam("subject", subject);
        addParam("body", body);
        sendRequest(User.class, false);
        
        setResult(RESULT_OK);
	}

	/* (non-Javadoc)
	 * @see es.ugr.swad.swadroid.modules.Module#connect()
	 */
	@Override
	protected void connect() {
		String progressDescription = getString(R.string.sendingMessageMsg);
    	int progressTitle = R.string.messagesModuleLabel;
  	    
        new Connect(false, progressDescription, progressTitle).execute();
		
		Toast.makeText(this, R.string.sendingMessageMsg, Toast.LENGTH_SHORT).show();
		Log.i(TAG, getString(R.string.sendingMessageMsg));
	}

	/* (non-Javadoc)
	 * @see es.ugr.swad.swadroid.modules.Module#postConnect()
	 */
	@Override
	protected void postConnect() {		
		Toast.makeText(this, R.string.messageSendedMsg, Toast.LENGTH_SHORT).show();
		Log.i(TAG, getString(R.string.messageSendedMsg));
		finish();
	}

}
