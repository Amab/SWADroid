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
package es.ugr.swad.swadroid.modules.tests;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParserException;

import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import es.ugr.swad.swadroid.Global;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.model.Model;
import es.ugr.swad.swadroid.model.Test;
import es.ugr.swad.swadroid.model.TestAnswer;
import es.ugr.swad.swadroid.model.TestQuestion;
import es.ugr.swad.swadroid.model.TestTag;
import es.ugr.swad.swadroid.model.User;
import es.ugr.swad.swadroid.modules.Module;

/**
 * Tests module for download and update questions
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class TestsQuestionsDownload extends Module {
	/**
	 * Selected course code
	 */
	private long selectedCourseCode;
	/**
	 * Next timestamp to be requested
	 */
	private Long timestamp;
    /**
     * Tests tag name for Logcat
     */
    public static final String TAG = Global.APP_TAG + " TestsQuestionsDownload";
	
	/* (non-Javadoc)
	 * @see es.ugr.swad.swadroid.modules.Module#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
        setMETHOD_NAME("getTests");
	}

	/* (non-Javadoc)
	 * @see es.ugr.swad.swadroid.modules.Module#onStart()
	 */
	@Override
	protected void onStart() {
		super.onStart();
		selectedCourseCode = getIntent().getLongExtra("selectedCourseCode", 0);
		timestamp = getIntent().getLongExtra("timestamp", 0);
		runConnection();	
	}

	/* (non-Javadoc)
	 * @see es.ugr.swad.swadroid.modules.Module#requestService()
	 */
	@Override
	protected void requestService() throws NoSuchAlgorithmException,
			IOException, XmlPullParserException, SoapFault,
			IllegalAccessException, InstantiationException {
		
		//Creates webservice request, adds required params and sends request to webservice
	    createRequest();
	    addParam("wsKey", User.getWsKey());
	    addParam("courseCode", (int)selectedCourseCode);
	    addParam("beginTime", timestamp);
	    sendRequest(Test.class, false);

	    if (result != null) {
	        //Stores tests data returned by webservice response
			Vector<?> res = (Vector<?>) result;

            SoapObject tagsListObject = (SoapObject)res.get(0);
            SoapObject questionsListObject = (SoapObject)res.get(1);
            SoapObject answersListObject = (SoapObject)res.get(2);
            SoapObject questionTagsListObject = (SoapObject)res.get(3);
            List<TestTag> tagsList = new ArrayList<TestTag>();
            List<Model> tagsListDB = dbHelper.getAllRows(Global.DB_TABLE_TEST_TAGS);
            List<Model> questionsListDB = dbHelper.getAllRows(Global.DB_TABLE_TEST_QUESTIONS);
            List<Model> answersListDB = dbHelper.getAllRows(Global.DB_TABLE_TEST_ANSWERS);
            
            //Read tags info from webservice response
	    	int listSize = tagsListObject.getPropertyCount();
	        for (int i = 0; i < listSize; i++) {
	            SoapObject pii = (SoapObject)tagsListObject.getProperty(i);
	            Integer tagCod = new Integer(pii.getProperty("tagCode").toString());
	            String tagTxt = pii.getProperty("tagText").toString();
	            TestTag tag = new TestTag(tagCod, 0, tagTxt, 0);
	            tagsList.add(tag);
	            
	    		if(isDebuggable)
	    			Log.d(TAG, tag.toString());
	        }
	        
			Log.i(TAG, "Retrieved " + listSize + " tags");
            
			//Read questions info from webservice response
	    	listSize = questionsListObject.getPropertyCount();
	        for (int i = 0; i < listSize; i++) {
	            SoapObject pii = (SoapObject)questionsListObject.getProperty(i);
	            Integer qstCod = new Integer(pii.getProperty("questionCode").toString());
	            String anstype = pii.getProperty("answerType").toString();
	            Integer shuffle = new Integer(pii.getProperty("shuffle").toString());
	            String stem = pii.getProperty("stem").toString();
	            TestQuestion q = new TestQuestion(qstCod, stem, anstype, Global.parseIntBool(shuffle));
	            
	            //If it's a new question, insert in database
	            try {
	            	dbHelper.insertTestQuestion(q, selectedCourseCode);
		            
		    		if(isDebuggable)
		    			Log.d(TAG, "INSERTED: " + q.toString());
	            	
	            //If it's an updated question, update it's row in database
	            } catch (SQLException e) {
	            	TestQuestion old = (TestQuestion) questionsListDB.get(questionsListDB.indexOf(q));
	            	dbHelper.updateTestQuestion(old, q, selectedCourseCode);
		            
		    		if(isDebuggable)
		    			Log.d(TAG, "UPDATED: " + q.toString());
	            }
	        }
	        
			Log.i(TAG, "Retrieved " + listSize + " questions");
            
			//Read answers info from webservice response
	    	listSize = answersListObject.getPropertyCount();
	        for (int i = 0; i < listSize; i++) {
	            SoapObject pii = (SoapObject)answersListObject.getProperty(i);
	            Integer qstCod = new Integer(pii.getProperty("questionCode").toString());
	            Integer ansIndex = new Integer(pii.getProperty("answerIndex").toString());
	            Integer correct = new Integer(pii.getProperty("correct").toString());
	            String answer = pii.getProperty("answerText").toString();
	            TestAnswer a = new TestAnswer(0, ansIndex, qstCod, Global.parseIntBool(correct), answer);
	            
	            //If it's a new answer, insert in database
	            try {
	            	dbHelper.insertTestAnswer(a, qstCod);
		            
		    		if(isDebuggable)
		    			Log.d(TAG, "INSERTED: " + a.toString());
	            	
	            //If it's an updated answer, update it's row in database
	            } catch (SQLException e) {
	            	TestAnswer old = (TestAnswer) answersListDB.get(answersListDB.indexOf(a));
	            	dbHelper.updateTestAnswer(old, a, qstCod);
		            
		    		if(isDebuggable)
		    			Log.d(TAG, "UPDATED: " + a.toString());
            	}
	        }
	        
			Log.i(TAG, "Retrieved " + listSize + " answers");
            
			//Read relationships between questions and tags from webservice response
	    	listSize = questionTagsListObject.getPropertyCount();
	        for (int i = 0; i < listSize; i++) {
	            SoapObject pii = (SoapObject)questionTagsListObject.getProperty(i);
	            Integer qstCod = new Integer(pii.getProperty("questionCode").toString());
	            Integer tagCod = new Integer(pii.getProperty("tagCode").toString());
	            Integer tagIndex = new Integer(pii.getProperty("tagIndex").toString());
	            TestTag tag = tagsList.get(tagsList.indexOf(new TestTag(tagCod, "", 0)));
	            tag.setQstCod(qstCod);
	            tag.setTagInd(tagIndex);

	            //If it's a new tag, insert in database
	            try {
	            	dbHelper.insertTestTag(tag);
	                tagsListDB.add(tag);
		            
		    		if(isDebuggable)
		    			Log.d(TAG, "INSERTED: " + tag.toString());

	            //If it's an updated tag, update it's rows in database
	            } catch (SQLException e) {
	            	TestTag old = (TestTag) tagsListDB.get(tagsListDB.indexOf(tag));
	            	dbHelper.updateTestTag(old, tag);
		            
		    		if(isDebuggable)
		    			Log.d(TAG, "UPDATED: " + tag.toString());
	            }
	        }

			Log.i(TAG, "Retrieved " + listSize + " relationships between questions and tags");
			
			//Update last time test was updated
			Test oldTestConfigDB = (Test) dbHelper.getRow(Global.DB_TABLE_TEST_CONFIG, "id", Long.toString(selectedCourseCode));
			Test testConfig = oldTestConfigDB;
			testConfig.setEditTime(System.currentTimeMillis() / 1000L);
			dbHelper.updateTestConfig(oldTestConfigDB, testConfig);
	    }
        
        //Request finalized without errors
        setResult(RESULT_OK);
	}

	/* (non-Javadoc)
	 * @see es.ugr.swad.swadroid.modules.Module#connect()
	 */
	@Override
	protected void connect() {
		String progressDescription = getString(R.string.testsDownloadProgressDescription);
    	int progressTitle = R.string.testsDownloadProgressTitle;
  	    
        new Connect(true, progressDescription, progressTitle).execute();
	}

	/* (non-Javadoc)
	 * @see es.ugr.swad.swadroid.modules.Module#postConnect()
	 */
	@Override
	protected void postConnect() {		
		Toast.makeText(this, R.string.questionsTestsDownloadSuccesfulMsg, Toast.LENGTH_LONG).show();
        finish();
	}
}
