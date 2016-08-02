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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.ksoap2.serialization.SoapObject;
import java.util.ArrayList;
import java.util.Vector;
import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.analytics.SWADroidTracker;
import es.ugr.swad.swadroid.model.User;
import es.ugr.swad.swadroid.modules.Module;
import es.ugr.swad.swadroid.modules.login.Login;
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
     * Message's receivers
     */
    private String receivers;
    /**
     * Names of receivers
     */
    private String receiversNames;

    private ArrayList<String> arrayReceivers;

    private ArrayList<String> arrayReceiversNames;
    
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

    private String sender;

    private ViewGroup layout;

 
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
        
        eventCode = getIntent().getLongExtra("eventCode", 0);
        setContentView(R.layout.messages_screen);
        setTitle(R.string.messagesModuleLabel);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
		
        rcvEditText = (EditText) findViewById(R.id.message_receivers_text);
        subjEditText = (EditText) findViewById(R.id.message_subject_text);
        bodyEditText = (EditText) findViewById(R.id.message_body_text);
       
        if (savedInstanceState != null) 
            writeData();

        sender = "";
        if (eventCode != 0) {
            subjEditText.setText("Re: " + getIntent().getStringExtra("summary"));
            sender = getIntent().getStringExtra("sender") + ",\n";
            rcvEditText.setText(sender);
        }

        final ImageButton button = (ImageButton) findViewById(R.id.action_addUser);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent (Messages.this, SearchUsers.class);
                intent.putExtra("receivers", arrayReceivers);
                intent.putExtra("receiversNames", arrayReceiversNames);
                startActivityForResult(intent, Constants.SEARCH_USERS_REQUEST_CODE);
            }
        });

        layout = (ViewGroup) findViewById(R.id.layout_receivers);

        LayoutInflater inflater = LayoutInflater.from(this);
        int id = R.layout.receivers_item;

        LinearLayout linearLayout = (LinearLayout) inflater.inflate(id, null, false);

        TextView textView = (TextView) linearLayout.findViewById(R.id.textView);
        textView.setText("Rubén Martín Hidalgo");

        //layout params
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        params.topMargin = 15;
        linearLayout.setPadding(5, 3, 5, 3);
        linearLayout.setLayoutParams(params);
        ///////

        layout.addView(linearLayout);

        LayoutInflater inflater2 = LayoutInflater.from(this);
        int id2 = R.layout.receivers_item;

        LinearLayout linearLayout2 = (LinearLayout) inflater2.inflate(id2, null, false);

        TextView textView2 = (TextView) linearLayout2.findViewById(R.id.textView);
        textView2.setText("Isabel Ortega Contreras");

        linearLayout2.setPadding(5, 3, 5, 3);
        linearLayout2.setLayoutParams(params);

        layout.addView(linearLayout2);

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
        receiversNames = rcvEditText.getText().toString();
        subject = subjEditText.getText().toString();
        body = bodyEditText.getText().toString();        
    }

    /**
     * Writes user input
     */
    private void writeData() {
        rcvEditText.setText(receiversNames);
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
        
        for(int i=0; i<arrayReceivers.size(); i++)
            receivers += arrayReceivers.get(i);

        createRequest(SOAPClient.CLIENT_TYPE);
        addParam("wsKey", Login.getLoggedUser().getWsKey());
        addParam("messageCode", eventCode.intValue());
        addParam("to", receivers);
        addParam("subject", subject);
        addParam("body", body);
        sendRequest(User.class, false);

        if (result != null) {
            ArrayList<?> res = new ArrayList<Object>((Vector<?>) result);
            SoapObject soap = (SoapObject) res.get(1);
            int csSize = soap.getPropertyCount();
            receiversNames = "";
            for (int i = 0; i < csSize; i++) {
                SoapObject pii = (SoapObject) soap.getProperty(i);
                String nickname = pii.getProperty("userNickname").toString();
                String firstname = pii.getProperty("userFirstname").toString();
                String surname1 = pii.getProperty("userSurname1").toString();
                String surname2 = pii.getProperty("userSurname2").toString();
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

        Toast.makeText(this, R.string.sendingMessageMsg, Toast.LENGTH_SHORT).show();
    }

    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.modules.Module#postConnect()
     */
    @Override
    protected void postConnect() {
        String messageSent = getString(R.string.messageSentMsg) + ":" + receiversNames;

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
            receivers = "";
            receiversNames = sender;
            for(int i=0; i<arrayReceivers.size(); i++){
                receivers += arrayReceivers.get(i) + ", ";
                receiversNames += arrayReceiversNames.get(i) + ",\n";
            }
            Log.d(TAG, "Nickname Receivers: " + receivers);
    		writeData();
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
	            	if((eventCode == 0) && (rcvEditText.getText().length() == 0)) {
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
}
