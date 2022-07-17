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
import android.graphics.Paint;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
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
import java.util.List;
import java.util.Vector;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.database.DataBaseHelper;
import es.ugr.swad.swadroid.gui.ImageFactory;
import es.ugr.swad.swadroid.gui.ProgressScreen;
import es.ugr.swad.swadroid.model.FrequentUser;
import es.ugr.swad.swadroid.model.User;
import es.ugr.swad.swadroid.model.UserFilter;
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
 * @author Rubén Martín Hidalgo <rubenmartin1991@gmail.com>
 */
public class Messages extends Module {
	/**
     * Messages tag name for Logcat
     */
    private static final String TAG = Constants.APP_TAG + " Messages";

    /**
     * Constants to manage the frequents recipients
     */
    private static final Double MIN_SCORE = 0.5;

    private static final Double MAX_SCORE = 20.0;

    private static final Double INITIAL_SCORE = 1.0;

    private static final Double INCREASE_FACTOR = 1.2;

    private static final Double DECREASE_FACTOR = 0.95;

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
     * Array of receivers
     */
    private ArrayList<UserFilter> arrayReceivers;
    /**
     * Message's subject
     */
    private String subject;
    /**
     * Message's body
     */
    private String body;
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
    /**
     * Photo of reply notification receiver
     */
    private String senderPhoto;
    /**
     * Image of every receiver
     */
    private ImageLoader loader;
    /**
     * View group of receivers
     */
    private ViewGroup layout;
    /**
     * Layout of sending progress
     */
    private ProgressScreen progressLayout;
    /**
     * Layout of message screen
     */
    private LinearLayout messageLayout;
    /**
     * Link to expand the receivers
     */
    private TextView seeAll;
    /**
     * Save if the list is expanded
     */
    private boolean showAll;
    /**
     * List where requests of database are saved
     */
    private List<FrequentUser> frequentsList;
    /**
     * User logged identifier
     */
    private String userLogged;

    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.modules.Module#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        receivers = "";
        receiversNames = "";
        arrayReceivers = new ArrayList<UserFilter>();
        sender = "";

        userLogged = Login.getLoggedUser().getUserID();

        setContentView(R.layout.messages_screen);
        setTitle(R.string.messagesModuleLabel);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        subjEditText = (EditText) findViewById(R.id.message_subject_text);
        bodyEditText = (EditText) findViewById(R.id.message_body_text);

        if (savedInstanceState != null)
            writeData();

        layout = (ViewGroup) findViewById(R.id.layout_receivers);

        messageLayout = (LinearLayout) findViewById(R.id.message_screen);
        View mMessageView = findViewById(R.id.message_screen);
        View mProgressScreenView = findViewById(R.id.progress_screen);
        progressLayout = new ProgressScreen(mProgressScreenView, mMessageView,
                getString(R.string.sendingMessageMsg), this);

        loader = ImageFactory.init(this, true, true, R.drawable.usr_bl, R.drawable.usr_bl,
                R.drawable.usr_bl);

        eventCode = getIntent().getLongExtra("eventCode", 0);
        if (eventCode != 0) { //is a reply message
            subjEditText.setText("Re: " + getIntent().getStringExtra("summary"));
            sender = getIntent().getStringExtra("sender");
            senderPhoto = getIntent().getStringExtra("photo");
            setTitle(getResources().getString(R.string.replyModuleLabel));

            showSenderReplyMessage();
        }

        final ImageButton button = (ImageButton) findViewById(R.id.action_addUser);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent (Messages.this, SearchUsers.class);
                intent.putExtra("receivers", arrayReceivers);
                intent.putExtra("senderName", sender);
                intent.putExtra("senderPhoto", senderPhoto);
                startActivityForResult(intent, Constants.SEARCH_USERS_REQUEST_CODE);
            }
        });

        showAll = false;
        seeAll = (TextView) findViewById(R.id.see_more_receivers);
        seeAll.setPaintFlags(seeAll.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        seeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAll = !showAll;
                showReceivers();
            }
        });
        seeAll.setText(getResources().getString(R.string.see_all));

        setMETHOD_NAME("sendMessage");
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
            receivers += "@" + arrayReceivers.get(i).getUserNickname() + ",";

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
                String firstname = pii.getPrimitiveProperty("userFirstname").toString();
                String surname1 = pii.getPrimitiveProperty("userSurname1").toString();
                String surname2 = pii.getPrimitiveProperty("userSurname2").toString();
                if (i == csSize-1) {
                    receiversNames += firstname + " " + surname1;
                    if (!surname2.isEmpty())
                        receiversNames += " " + surname2;
                }
                else {
                    receiversNames += firstname + " " + surname1;
                    if (!surname2.isEmpty())
                        receiversNames += " " + surname2;
                    receiversNames += ",\n";
                }
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
        progressLayout.show();
    }

    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.modules.Module#postConnect()
     */
    @Override
    protected void postConnect() {
        boolean frequent = false;
        double score;
        String nickname;

        //get data of frequent users
        frequentsList = dbHelper.getAllRows(DataBaseHelper.DB_TABLE_FREQUENT_RECIPIENTS, "idUser='" + userLogged + "'", "");

        //modify data in memory
        for(int i=0; i < frequentsList.size(); i++){
            nickname = frequentsList.get(i).getUserNickname();
            for(int j=0; j < arrayReceivers.size(); j++){
                if(nickname.equals(arrayReceivers.get(j).getUserNickname())){
                    frequent = true;
                    score = frequentsList.get(i).getScore() * INCREASE_FACTOR;

                    if(score > MAX_SCORE)
                        score = MAX_SCORE;
                    frequentsList.get(i).setScore(score);
                    arrayReceivers.remove(j);
                    j = arrayReceivers.size();
                    Log.d(TAG, "frequent user '" + nickname + "' updated, score = " + score);
                }
            }
            if(frequent == false){
                score = frequentsList.get(i).getScore() * DECREASE_FACTOR;
                if(score < MIN_SCORE) {
                    frequentsList.remove(i);
                    Log.d(TAG, "frequent user '" + nickname + "' removed");
                }else {
                    frequentsList.get(i).setScore(score);
                    Log.d(TAG, "frequent user '" + nickname + "' updated, score = " + score);
                }
            }
            frequent = false;
        }

        for(int i=0; i < arrayReceivers.size(); i++){
            frequentsList.add(new FrequentUser(userLogged, arrayReceivers.get(i).getUserNickname(), arrayReceivers.get(i).getUserSurname1(), arrayReceivers.get(i).getUserSurname2(), arrayReceivers.get(i).getUserFirstname(), arrayReceivers.get(i).getUserPhoto(), arrayReceivers.get(i).getUserCode(), false, INITIAL_SCORE));
            Log.d(TAG, "frequent user '" + arrayReceivers.get(i).getUserNickname() + "' added = " + INITIAL_SCORE + " (sender = '" + userLogged + "')");
        }

        dbHelper.beginTransaction();

        //delete frequent recipients of user logged
        dbHelper.removeAllRows(DataBaseHelper.DB_TABLE_FREQUENT_RECIPIENTS, "idUser='" + userLogged + "'");

        //insert new data in data base
        dbHelper.insertFrequentsList(frequentsList);

        dbHelper.endTransaction(true);

        progressLayout.hide();
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
    		arrayReceivers = (ArrayList) data.getSerializableExtra("receivers");
            receivers = "";
            for(int i=0; i<arrayReceivers.size(); i++){
                receivers += arrayReceivers.get(i) + ",";
            }

            if(arrayReceivers.size() <= 3)
                showAll = false;

    		writeData();
            showReceivers();
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
	            	if((eventCode == 0) && (receivers.length() == 0)) {
	            		Toast.makeText(this, R.string.noReceiversMsg, Toast.LENGTH_LONG).show();
	            	} else if(subjEditText.getText().length() == 0) {
	            		Toast.makeText(this, R.string.noSubjectMessageMsg, Toast.LENGTH_LONG).show();
	            	} else {
	                    runConnection();
	            	}
	            } catch (Exception e) {
	                String errorMsg = getString(R.string.errorServerResponseMsg);
	                error(errorMsg, e);
	            }
                return true;
            case android.R.id.home:
                showCancelDialog();
	            return true;

	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

    @Override
    public void onBackPressed()
    {
        showCancelDialog();
    }

    private void showCancelDialog(){
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

    private void showDeleteDialog(final View linearLayout, final TextView textNickname, String textName){
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
                for(int i=0; i<arrayReceivers.size(); i++) {
                    if (arrayReceivers.get(i).getUserNickname().equals(textNickname.getText().toString()))
                        arrayReceivers.remove(i);
                }

                if(arrayReceivers.size() <= 3){
                    seeAll.setVisibility(View.GONE);
                    showAll = false;
                }

                showReceivers();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showSenderReplyMessage(){
        LayoutInflater inflater = LayoutInflater.from(this);
        final View linearLayout = inflater.inflate(R.layout.receivers_item, null, false);

        final TextView textName = (TextView) linearLayout.findViewById(R.id.textName);
        textName.setText(sender + " (" + getResources().getString(R.string.primaryReceiver) + ")");

        ImageView photo = (ImageView) linearLayout.findViewById(R.id.imageView);
        String userPhoto = senderPhoto;
        if (Utils.connectionAvailable(this)
                && (userPhoto != null) && !userPhoto.equals("")
                && !userPhoto.equals(Constants.NULL_VALUE)) {
            ImageFactory.displayImage(loader, userPhoto, photo);
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        params.topMargin = 8;
        linearLayout.setPadding(1, 1, 15, 1);
        linearLayout.setLayoutParams(params);

        layout.addView(linearLayout);

        ImageButton button = (ImageButton)linearLayout.findViewById(R.id.buttonDelete);
        button.setVisibility(View.GONE);
    }

    private void showReceivers(){

        // restart layout
        layout.removeAllViewsInLayout();
        layout.setVisibility(View.GONE);

        if(sender != ""){ // add the sender of reply message to receivers list
            layout.setVisibility(View.VISIBLE);
            showSenderReplyMessage();
        }

        int i;
        if(showAll){
            i = 0;
            seeAll.setText(getResources().getString(R.string.see_less));
        }
        else if (arrayReceivers.size() > 3) {
            i = arrayReceivers.size() - 3;
            seeAll.setVisibility(View.VISIBLE);
            seeAll.setText(getResources().getString(R.string.see_all));
        }
        else {
            i = 0;
            seeAll.setVisibility(View.GONE);
        }


        while (i < arrayReceivers.size()){
            layout.setVisibility(View.VISIBLE);
            LayoutInflater inflater = LayoutInflater.from(this);
            final View linearLayout = inflater.inflate(R.layout.receivers_item, null, false);

            final TextView textName = (TextView) linearLayout.findViewById(R.id.textName);
            textName.setText(arrayReceivers.get(i).getUserFirstname() + " "
                            + arrayReceivers.get(i).getUserSurname1() + " "
                            + arrayReceivers.get(i).getUserSurname2());

            final TextView textNickname = (TextView) linearLayout.findViewById(R.id.textNickname);
            textNickname.setText(arrayReceivers.get(i).getUserNickname());

            ImageView photo = (ImageView) linearLayout.findViewById(R.id.imageView);
            String userPhoto = arrayReceivers.get(i).getUserPhoto();
            if (Utils.connectionAvailable(this)
                    && (userPhoto != null) && !userPhoto.equals("")
                    && !userPhoto.equals(Constants.NULL_VALUE)) {
                ImageFactory.displayImage(loader, userPhoto, photo);
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            params.topMargin = 8;
            linearLayout.setPadding(1, 1, 1, 1);
            linearLayout.setLayoutParams(params);

            layout.addView(linearLayout);

            ImageButton button = (ImageButton)linearLayout.findViewById(R.id.buttonDelete);
            button.setOnClickListener( new View.OnClickListener() {
                public void onClick(View view){
                    showDeleteDialog(linearLayout, textNickname, textName.getText().toString());
                }
            });
            i++;
        }
    }
}
