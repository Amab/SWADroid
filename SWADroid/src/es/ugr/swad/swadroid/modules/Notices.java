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

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.Preferences;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.model.User;

/**
 * Module for send messages.
 * @author Helena Rodriguez Gijon <hrgijon@gmail.com>
 * @author Antonio Aguilera Malagon <aguilerin@gmail.com>
 */
public class Notices extends Module {
	/**
	 * Messages tag name for Logcat
	 */
	public static final String TAG = Constants.APP_TAG + " Notice";

	/**
	 * Notice's body
	 */
	private String body;
	private Dialog noticeDialog;

	/**
	 * Application preferences.
	 */
	protected static Preferences prefs = new Preferences();

	/**
	 * Selected course code
	 */
	private long selectedCourseCode = 0;

	private OnClickListener positiveClickListener = new OnClickListener() {

		public void onClick(View v) {
			if(isDebuggable) {
				Log.i(TAG, "on click positive before send request to server");
			}

			try {										
				/*if(isDebuggable) {
					Log.i(TAG, "selectedCourseCode = " + Long.toString(courseCode));
				}*/

				runConnection();
			} catch (Exception ex) {
				String errorMsg = getString(R.string.errorServerResponseMsg);
				error(TAG, errorMsg, ex);
			}
		}
	};

	private OnClickListener negativeClickListener = new OnClickListener() {
		public void onClick(View v) {
			finish();
		}
	};

	private void launchNoticeDialog(){

		noticeDialog = new Dialog(this);
		Button acceptButton, cancelButton;

		noticeDialog.setTitle(R.string.noticesModuleLabel);
		noticeDialog.setContentView(R.layout.notice_dialog);
		noticeDialog.setCancelable(true);

		noticeDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

		acceptButton = (Button) noticeDialog.findViewById(R.id.notice_button_accept);
		acceptButton.setOnClickListener(positiveClickListener);

		cancelButton = (Button) noticeDialog.findViewById(R.id.notice_button_cancel);
		cancelButton.setOnClickListener(negativeClickListener);

		noticeDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

			public void onCancel(DialogInterface dialog) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});
		noticeDialog.show();

	}
	@Override
	protected void requestService() throws NoSuchAlgorithmException,
	IOException, XmlPullParserException, SoapFault,
	IllegalAccessException, InstantiationException {

		readData();

		createRequest();

		addParam("wsKey", Constants.getLoggedUser().getWsKey());
		addParam("courseCode",(int)selectedCourseCode);
		addParam("body",body);

		sendRequest(User.class,false);
		if(result != null){
			//TODO 
			setResult(RESULT_OK);
		}
	}

	@Override
	protected void connect() {
		String progressDescription = getString(R.string.noticesModuleLabel);
		int progressTitle = R.string.noticesModuleLabel;

		new Connect(false, progressDescription, progressTitle).execute();

		Toast.makeText(this, R.string.publishingNotice, Toast.LENGTH_SHORT).show();
		Log.i(TAG, getString(R.string.publishingNotice));

	}

	@Override
	protected void postConnect() {
		String noticeSended = getString(R.string.noticePublished);
		Toast.makeText(this, noticeSended, Toast.LENGTH_LONG).show();
		Log.i(TAG, noticeSended);
		finish();
	}

	private void readData() {
		EditText bd = (EditText) noticeDialog.findViewById(R.id.notice_body_text);
		body = bd.getText().toString();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setMETHOD_NAME("sendNotice");
	}

	@Override
	protected void onPause() {
		super.onPause();
		noticeDialog.dismiss();
	}

	@Override
	protected void onStart() {
		super.onStart();
		prefs.getPreferences(getBaseContext());
		selectedCourseCode = Constants.getSelectedCourseCode();
		launchNoticeDialog();
	}


	@Override
	protected void onError() {
		// TODO Auto-generated method stub

	}

}