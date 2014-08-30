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

import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.model.Model;
import es.ugr.swad.swadroid.model.Test;
import es.ugr.swad.swadroid.model.TestAnswer;
import es.ugr.swad.swadroid.model.TestQuestion;
import es.ugr.swad.swadroid.model.TestTag;
import es.ugr.swad.swadroid.modules.Module;
import es.ugr.swad.swadroid.utils.TimeUtils;
import es.ugr.swad.swadroid.utils.Utils;
import es.ugr.swad.swadroid.webservices.SOAPClient;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Tests module for download and update questions
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 * @author Antonio Aguilera Malagon <aguilerin@gmail.com>
 */
public class TestsQuestionsDownload extends Module {
    /**
     * Next timestamp to be requested
     */
    private Long timestamp;
    /**
     * Tests tag name for Logcat
     */
    private static final String TAG = Constants.APP_TAG + " TestsQuestionsDownload";

    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.modules.Module#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setMETHOD_NAME("getTests");
        getSupportActionBar().hide();
    }

    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.modules.Module#onStart()
     */
    @Override
    protected void onStart() {
        super.onStart();
        timestamp = getIntent().getLongExtra("timestamp", 0);
        runConnection();
    }

    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.modules.Module#requestService()
     */
    @Override
    protected void requestService() throws Exception {
    	long timeBefore = System.currentTimeMillis();
    	long timeAfter;

        //Creates webservice request, adds required params and sends request to webservice
    	createRequest(SOAPClient.CLIENT_TYPE);
        addParam("wsKey", Constants.getLoggedUser().getWsKey());
        addParam("courseCode", (int) Constants.getSelectedCourseCode());
        addParam("beginTime", timestamp);
        sendRequest(Test.class, false);

        if (result != null) {
            //Stores tests data returned by webservice response
            ArrayList<?> res = new ArrayList<Object>((Vector<?>) result);

            SoapObject tagsListObject = (SoapObject) res.get(0);
            SoapObject questionsListObject = (SoapObject) res.get(1);
            SoapObject answersListObject = (SoapObject) res.get(2);
            SoapObject questionTagsListObject = (SoapObject) res.get(3);
            List<TestTag> tagsList = new ArrayList<TestTag>();
            List<Model> tagsListDB = dbHelper.getAllRows(Constants.DB_TABLE_TEST_TAGS);
            List<Model> questionsListDB = dbHelper.getAllRows(Constants.DB_TABLE_TEST_QUESTIONS);
            List<Model> answersListDB = dbHelper.getAllRows(Constants.DB_TABLE_TEST_ANSWERS);

            int listSizeTags = tagsListObject.getPropertyCount();
            int listSizeQuestions = questionsListObject.getPropertyCount();
            int listSizeAnswers = answersListObject.getPropertyCount();
            int listSizeQuestionTags = questionTagsListObject.getPropertyCount();

            //Read tags info from webservice response
            for (int i = 0; i < listSizeTags; i++) {
                SoapObject pii = (SoapObject) tagsListObject.getProperty(i);
                Integer tagCod = Integer.valueOf(pii.getProperty("tagCode").toString());
                String tagTxt = pii.getProperty("tagText").toString();
                TestTag tag = new TestTag(tagCod, null, tagTxt, 0);
                tagsList.add(tag);

                if (isDebuggable)
                    Log.d(TAG, tag.toString());
            }

            //Read questions info from webservice response
            dbHelper.beginTransaction();

            for (int i = 0; i < listSizeQuestions; i++) {
                SoapObject pii = (SoapObject) questionsListObject.getProperty(i);
                Integer qstCod = Integer.valueOf(pii.getProperty("questionCode").toString());
                String anstype = pii.getProperty("answerType").toString();
                Integer shuffle = Integer.valueOf(pii.getProperty("shuffle").toString());
                String stem = pii.getProperty("stem").toString();
                String questionFeedback = pii.getProperty("feedback").toString();
                TestQuestion q = new TestQuestion(qstCod, stem, anstype, Utils.parseIntBool(shuffle), questionFeedback);

                //If it's a new question, insert in database
                try {
                    dbHelper.insertTestQuestion(q, Constants.getSelectedCourseCode());

                    if (isDebuggable)
                        Log.d(TAG, "INSERTED: " + q.toString());

                    //If it's an updated question, update it's row in database
                } catch (SQLException e) {
                    TestQuestion old = (TestQuestion) questionsListDB.get(questionsListDB.indexOf(q));
                    dbHelper.updateTestQuestion(old, q, Constants.getSelectedCourseCode());

                    if (isDebuggable)
                        Log.d(TAG, "UPDATED: " + q.toString());
                }
            }

            //Read answers info from webservice response
            for (int i = 0; i < listSizeAnswers; i++) {
                SoapObject pii = (SoapObject) answersListObject.getProperty(i);
                Integer qstCod = Integer.valueOf(pii.getProperty("questionCode").toString());
                Integer ansIndex = Integer.valueOf(pii.getProperty("answerIndex").toString());
                Integer correct = Integer.valueOf(pii.getProperty("correct").toString());
                String answer = pii.getProperty("answerText").toString();
                String answerFeeback = pii.getProperty("answerFeedback").toString();
                TestAnswer a = new TestAnswer(0, ansIndex, qstCod, Utils.parseIntBool(correct), answer, answerFeeback);

                //If it's a new answer, insert in database
                try {
                    dbHelper.insertTestAnswer(a, qstCod);

                    if (isDebuggable)
                        Log.d(TAG, "INSERTED: " + a.toString());

                    //If it's an updated answer, update it's row in database
                } catch (SQLException e) {
                    TestAnswer old = (TestAnswer) answersListDB.get(answersListDB.indexOf(a));
                    dbHelper.updateTestAnswer(old, a, qstCod);

                    if (isDebuggable)
                        Log.d(TAG, "UPDATED: " + a.toString());
                }
            }

            //Read relationships between questions and tags from webservice response
            for (int i = 0; i < listSizeQuestionTags; i++) {
                SoapObject pii = (SoapObject) questionTagsListObject.getProperty(i);
                Integer qstCod = Integer.valueOf(pii.getProperty("questionCode").toString());
                Integer tagCod = Integer.valueOf(pii.getProperty("tagCode").toString());
                Integer tagIndex = Integer.valueOf(pii.getProperty("tagIndex").toString());
                TestTag tag = tagsList.get(tagsList.indexOf(new TestTag(tagCod, "", 0)));
                tag.addQstCod(qstCod);
                tag.setTagInd(tagIndex);

                //If it's a new tag, insert in database
                try {
                    dbHelper.insertTestTag(tag);
                    tagsListDB.add(tag);

                    if (isDebuggable)
                        Log.d(TAG, "INSERTED: " + tag.toString());

                    //If it's an updated tag, update it's rows in database
                } catch (SQLException e) {
                    TestTag old = (TestTag) tagsListDB.get(tagsListDB.indexOf(tag));
                    tag.setQstCodList(old.getQstCodList());
                    tag.addQstCod(qstCod);
                    dbHelper.updateTestTag(old, tag);

                    if (isDebuggable)
                        Log.d(TAG, "UPDATED: " + tag.toString());
                }
            }

            //Update last time test was updated
            //Test testConfig = (Test) dbHelper.getRow(Constants.DB_TABLE_TEST_CONFIG, "id", Long.toString(Constants.getSelectedCourseCode()));
            //testConfig.setEditTime(System.currentTimeMillis() / 1000L);
            //dbHelper.updateTestConfig(testConfig.getId(), testConfig);
            dbHelper.endTransaction(true);
            
            timeAfter = System.currentTimeMillis();

            Log.i(TAG, "Retrieved " + listSizeTags + " tags");
            Log.i(TAG, "Retrieved " + listSizeQuestions + " questions");
            Log.i(TAG, "Retrieved " + listSizeAnswers + " answers");
            Log.i(TAG, "Retrieved " + listSizeQuestionTags + " relationships between questions and tags");
            Log.i(TAG, "Time elapsed = " + TimeUtils.millisToLongDHMS(timeAfter - timeBefore));
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

        startConnection(true, progressDescription, progressTitle);
    }

    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.modules.Module#postConnect()
     */
    @Override
    protected void postConnect() {
        Toast.makeText(this, R.string.questionsTestsDownloadSuccesfulMsg, Toast.LENGTH_LONG).show();
        finish();
    }

    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.modules.Module#onError()
     */
    @Override
    protected void onError() {

    }
}
