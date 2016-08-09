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
package es.ugr.swad.swadroid.modules.messages;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.nostra13.universalimageloader.core.ImageLoader;
import org.ksoap2.serialization.SoapObject;
import java.util.ArrayList;
import java.util.Vector;
import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.analytics.SWADroidTracker;
import es.ugr.swad.swadroid.gui.ImageFactory;
import es.ugr.swad.swadroid.model.User;
import es.ugr.swad.swadroid.modules.Module;
import es.ugr.swad.swadroid.modules.login.Login;
import es.ugr.swad.swadroid.utils.Utils;
import es.ugr.swad.swadroid.webservices.SOAPClient;

/**
 * Module for send messages.
 * @see <a href="https://openswad.org/ws/#sendMessage">sendMessage</a>
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 * @author Antonio Aguilera Malagon <aguilerin@gmail.com>
 * @author Jose Antonio Guerrero Aviles <cany20@gmail.com>
 */
public class Messages extends Module {
	/**
     * Messages tag name for Logcat
     */
    private static final String TAG = Constants.APP_TAG + " Messages";
    /**
     * Message code
     */
    private Long eventCode;
    /**
     * Message's receivers (nicknames)
     */
    private String receivers;
    /**
     * Names of receivers
     */
    private String receiversNames;
    /**
     * Array of nicknames
     */
    private ArrayList<String> arrayReceivers;
    /**
     * Array of names
     */
    private ArrayList<String> arrayReceiversNames;
    /**
     * Array of photos
     */
    private ArrayList<String> arrayPhotos;
    /**
     * Message's subject
     */
    private String subject;
    /**
     * Message's body
     */
    private String body;
    /**
     * Receivers EditText
     */
    private EditText rcvEditText;
    /**
     * Subject EditText
     */
    private EditText subjEditText;
    /**
     * Body EditText
     */
    private EditText bodyEditText;
    /**
     * Name of reply notification receiver
     */
    private String sender;

    private String senderPhoto;

    private ImageLoader loader;
    /**
     * View group of receivers
     */
    private ViewGroup layout;
    /**
     * Layout of sending progress
     */
    private LinearLayout progressLayout;
    /**
     * Layout of message screen
     */
    private LinearLayout messageLayout;

    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.modules.Module#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        receivers = "";
        receiversNames = "";
        arrayReceivers = new ArrayList<>();
        arrayReceiversNames = new ArrayList<>();
        arrayPhotos = new ArrayList<>();
        sender = "";

        setContentView(R.layout.messages_screen);
        setTitle(R.string.messagesModuleLabel);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        subjEditText = (EditText) findViewById(R.id.message_subject_text);
        bodyEditText = (EditText) findViewById(R.id.message_body_text);

        if (savedInstanceState != null)
            writeData();

        layout = (ViewGroup) findViewById(R.id.layout_receivers);

        messageLayout = (LinearLayout) findViewById(R.id.message_screen);
        progressLayout = (LinearLayout) findViewById(R.id.progressbar_view);
        TextView textLoading = (TextView) findViewById(R.id.text_progress);
        textLoading.setText(R.string.sendingMessageMsg);

        loader = ImageFactory.init(this, true, true, R.drawable.usr_bl, R.drawable.usr_bl,
                R.drawable.usr_bl);

        eventCode = getIntent().getLongExtra("eventCode", 0);
        if (eventCode != 0) { //is a reply message
            subjEditText.setText("Re: " + getIntent().getStringExtra("summary"));
            sender = getIntent().getStringExtra("sender");
            senderPhoto = getIntent().getStringExtra("photo");

            LayoutInflater inflater = LayoutInflater.from(this);
            final View linearLayout = inflater.inflate(R.layout.receivers_item, null, false);

            final TextView textName = (TextView) linearLayout.findViewById(R.id.textName);
            textName.setText(sender);

            ImageView photo = (ImageView) linearLayout.findViewById(R.id.imageView);
            String userPhoto = senderPhoto;
            if (Utils.connectionAvailable(this)
                    && (userPhoto != null) && !userPhoto.equals("")
                    && !userPhoto.equals(Constants.NULL_VALUE)) {
                ImageFactory.displayImage(loader, userPhoto, photo);
            } else {
                Log.d(TAG, "No connection or no photo " + userPhoto);
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            params.topMargin = 8;
            linearLayout.setPadding(1, 1, 20, 1);
            linearLayout.setLayoutParams(params);

            layout.addView(linearLayout);

            ImageButton button = (ImageButton)linearLayout.findViewById(R.id.buttonDelete);
            button.setVisibility(View.GONE);
        }

        final ImageButton button = (ImageButton) findViewById(R.id.action_addUser);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent (Messages.this, SearchUsers.class);
                intent.putExtra("receivers", arrayReceivers);
                intent.putExtra("receiversNames", arrayReceiversNames);
                intent.putExtra("receiversPhotos", arrayPhotos);
                intent.putExtra("senderName", sender);
                intent.putExtra("senderPhoto", senderPhoto);
                for(int i=0; i<arrayReceivers.size(); i++){
                    receivers += arrayReceivers.get(i) + ",";
                }
                Log.d(TAG, "Receivers of Messages: " + receivers);
                startActivityForResult(intent, Constants.SEARCH_USERS_REQUEST_CODE);
            }
        });

        setMETHOD_NAME("sendMessage");
    }

    @Override
	protected void onStart() {
		super.onStart();
        SWADroidTracker.sendScreenView(getApplicationContext(), TAG);
	}

	/**
     * Reads user input
     */
    private void readData() {
        subject = subjEditText.getText().toString();
        body = bodyEditText.getText().toString();
    }

    /**
     * Writes user input
     */
    private void writeData() {
        subjEditText.setText(subject);
        bodyEditText.setText(body);
    }

    /**
     * Adds the foot to the message body
     */
    private void addFootBody() {
        body = body.replaceAll("\n", "<br />");
        body = body + "<br /><br />" + getString(R.string.footMessageMsg) + " " + getString(R.string.app_name) +
                "<br />" + getString(R.string.marketWebURL);
        //body = body + "<br /><br />"+ getString(R.string.footMessageMsg) + " <a href=\"" +
        //		getString(R.string.marketWebURL) + "\">" + getString(R.string.app_name) + "</a>";
    }

    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.modules.Module#requestService()
     */
    @Override
    protected void requestService() throws Exception {
        readData();
        addFootBody();
        receivers = "";
        for(int i=0; i<arrayReceivers.size(); i++)
            receivers += arrayReceivers.get(i) + ",";

        createRequest(SOAPClient.CLIENT_TYPE);
        addParam("wsKey", Login.getLoggedUser().getWsKey());
        addParam("messageCode", eventCode.intValue());
        addParam("to", receivers);
        addParam("subject", subject);
        addParam("body", body);
        sendRequest(User.class, false);

        if (result != null) { //if there is result
            ArrayList<?> res = new ArrayList<Object>((Vector<?>) result);
            SoapObject soap = (SoapObject) res.get(1);
            int csSize = soap.getPropertyCount();
            receiversNames = "";
            for (int i = 0; i < csSize; i++) {
                SoapObject pii = (SoapObject) soap.getProperty(i);
                String firstname = pii.getProperty("userFirstname").toString();
                String surname1 = pii.getProperty("userSurname1").toString();
                String surname2 = pii.getProperty("userSurname2").toString();
                if (i == csSize-1)
                    receiversNames += firstname + " " + surname1 + " " + surname2 + "";
                else
                    receiversNames += firstname + " " + surname1 + " " + surname2 + ",\n";
            }
        }

        setResult(RESULT_OK);
    }

    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.modules.Module#connect()
     */
    @Override
    protected void connect() {
        startConnection();
        messageLayout.setVisibility(View.INVISIBLE);
        progressLayout.setVisibility(View.VISIBLE);
        //Toast.makeText(this, R.string.sendingMessageMsg, Toast.LENGTH_SHORT).show();
    }

    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.modules.Module#postConnect()
     */
    @Override
    protected void postConnect() {
        progressLayout.setVisibility(View.GONE);
        String messageSent = getString(R.string.messageSentMsg) + ":\n" + receiversNames;
        Toast.makeText(this, messageSent, Toast.LENGTH_LONG).show();
        finish();
    }

    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.modules.Module#onError()
     */
    @Override
    protected void onError() {

    }

    @Override
	protected void onRestart() {
		super.onRestart();
	}

    /* (non-Javadoc)
     * @see android.app.Activity#onRestoreInstanceState(android.os.Bundle)
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    	eventCode = savedInstanceState.getLong("eventCode");
        receivers = savedInstanceState.getString("receivers");
        receiversNames = savedInstanceState.getString("receiversNames");
        subject = savedInstanceState.getString("subject");
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

        outState.putLong("eventCode", eventCode);
        outState.putString("receivers", receivers);
        outState.putString("receiversNames", receiversNames);
        outState.putString("subject", subject);
        outState.putString("body", body);

        super.onSaveInstanceState(outState);
    }

    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (data != null) {
    		arrayReceivers = data.getStringArrayListExtra("receivers");
            arrayReceiversNames = data.getStringArrayListExtra("receiversNames");
            arrayPhotos = data.getStringArrayListExtra("receiversPhotos");
            receivers = "";
            for(int i=0; i<arrayReceivers.size(); i++){
                receivers += arrayReceivers.get(i) + ",";
            }
            Log.d(TAG, "Receivers of SearchUsers: " + receivers);
    		writeData();

            // if there are not receivers, hide view group
            layout.removeAllViewsInLayout();
            layout.setVisibility(View.GONE);

            if(sender != ""){
                layout.setVisibility(View.VISIBLE);
                LayoutInflater inflater = LayoutInflater.from(this);
                final View linearLayout = inflater.inflate(R.layout.receivers_item, null, false);

                final TextView textName = (TextView) linearLayout.findViewById(R.id.textName);
                textName.setText(sender);

                ImageView photo = (ImageView) linearLayout.findViewById(R.id.imageView);
                String userPhoto = senderPhoto;
                if (Utils.connectionAvailable(this)
                        && (userPhoto != null) && !userPhoto.equals("")
                        && !userPhoto.equals(Constants.NULL_VALUE)) {
                    ImageFactory.displayImage(loader, userPhoto, photo);
                } else {
                    Log.d(TAG, "No connection or no photo " + userPhoto);
                }

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                params.topMargin = 8;
                linearLayout.setPadding(1, 1, 25, 1);
                linearLayout.setLayoutParams(params);

                layout.addView(linearLayout);

                ImageButton button = (ImageButton)linearLayout.findViewById(R.id.buttonDelete);
                button.setVisibility(View.GONE);
            }

            for(int i=0; i<arrayReceiversNames.size(); i++){
                layout.setVisibility(View.VISIBLE);
                LayoutInflater inflater = LayoutInflater.from(this);
                final View linearLayout = inflater.inflate(R.layout.receivers_item, null, false);

                final TextView textName = (TextView) linearLayout.findViewById(R.id.textName);
                textName.setText(arrayReceiversNames.get(i).toString());

                final TextView textNickname = (TextView) linearLayout.findViewById(R.id.textNickname);
                textNickname.setText(arrayReceivers.get(i).toString());

                ImageView photo = (ImageView) linearLayout.findViewById(R.id.imageView);
                String userPhoto = arrayPhotos.get(i).toString();
                if (Utils.connectionAvailable(this)
                        && (userPhoto != null) && !userPhoto.equals("")
                        && !userPhoto.equals(Constants.NULL_VALUE)) {
                    ImageFactory.displayImage(loader, userPhoto, photo);
                } else {
                    Log.d(TAG, "No connection or no photo " + userPhoto);
                }

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                params.topMargin = 8;
                linearLayout.setPadding(1, 1, 1, 1);
                linearLayout.setLayoutParams(params);

                layout.addView(linearLayout);

                ImageButton button = (ImageButton)linearLayout.findViewById(R.id.buttonDelete);
                button.setOnClickListener( new View.OnClickListener() {
                    public void onClick(View view){
                        showDialogDelete(linearLayout, textNickname, textName.getText().toString());
                    }
                });
            }
        }
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    getMenuInflater().inflate(R.menu.messages_main_activity_actions, menu);
	    return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.action_sendMsg:
	            try {
                    receivers = "";
                    for(int i=0; i<arrayReceivers.size(); i++){
                        receivers += arrayReceivers.get(i) + ",";
                    }
	            	if((eventCode == 0) && (receivers.length() == 0)) {
	            		Toast.makeText(this, R.string.noReceiversMsg, Toast.LENGTH_LONG).show();
	            	} else if(subjEditText.getText().length() == 0) {
	            		Toast.makeText(this, R.string.noSubjectMessageMsg, Toast.LENGTH_LONG).show();
	            	} else {
	                    runConnection();
	            	}
	            } catch (Exception e) {
	                String errorMsg = getString(R.string.errorServerResponseMsg);
	                error(errorMsg, e, true);
	            }
                return true;
            case android.R.id.home:
                showDialogCancel();
	            return true;

	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

    @Override
    public void onBackPressed()
    {
        showDialogCancel();
    }

    private void showDialogCancel(){
        AlertDialog.Builder builder = new AlertDialog.Builder(Messages.this);
        builder.setTitle(R.string.areYouSure);
        builder.setMessage(R.string.cancelSendMessage);

        builder.setNegativeButton(getString(R.string.cancelMsg), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.setPositiveButton(getString(R.string.acceptMsg), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showDialogDelete(final View linearLayout, final TextView textNickname, String textName){
        AlertDialog.Builder builder = new AlertDialog.Builder(Messages.this);
        builder.setTitle(R.string.areYouSure);
        String dialog = getResources().getString(R.string.cancelRemoveReceivers);
        dialog = dialog.replaceAll("#nameUser#", textName);
        builder.setMessage(dialog);

        builder.setNegativeButton(getString(R.string.cancelMsg), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.setPositiveButton(getString(R.string.acceptMsg), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                layout.removeView(linearLayout);
                int position = arrayReceivers.indexOf(textNickname.getText().toString());
                arrayReceivers.remove(position);
                arrayReceiversNames.remove(position);
                arrayPhotos.remove(position);
                receivers = "";
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }
}
