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

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.dao.TestAnswerDao;
import es.ugr.swad.swadroid.dao.TestQuestionDao;
import es.ugr.swad.swadroid.dao.TestTagDao;
import es.ugr.swad.swadroid.dao.TestTagsQuestionsDao;
import es.ugr.swad.swadroid.model.Test;
import es.ugr.swad.swadroid.model.TestAnswer;
import es.ugr.swad.swadroid.model.TestQuestion;
import es.ugr.swad.swadroid.model.TestTag;
import es.ugr.swad.swadroid.model.TestTagsQuestions;
import es.ugr.swad.swadroid.modules.Module;
import es.ugr.swad.swadroid.modules.courses.Courses;
import es.ugr.swad.swadroid.modules.login.Login;
import es.ugr.swad.swadroid.utils.DateTimeUtils;
import es.ugr.swad.swadroid.utils.Utils;
import es.ugr.swad.swadroid.webservices.SOAPClient;

/**
 * Tests module for download and update questions
 * @see <a href="https://openswad.org/ws/#getTests">getTests</a>
 *
 * @author Juan Miguel Boyero Corral <swadroid@gmail.com>
 * @author Antonio Aguilera Malagon <aguilerin@gmail.com>
 */
public class TestsQuestionsDownload extends Module {
    /**
     * Next timestamp to be requested
     */
    private Long timestamp;
    /**
     * DAO for TestTag
     */
    private TestTagDao testTagDao;
    /**
     * DAO for TestQuestion
     */
    private TestQuestionDao testQuestionDao;
    /**
     * DAO for TestAnswer
     */
    private TestAnswerDao testAnswerDao;
    /**
     * DAO for TestTagsQuestions
     */
    private TestTagsQuestionsDao testTagsQuestionsDao;
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

        //Initialize DAOs
        testTagDao = db.getTestTagDao();
        testQuestionDao = db.getTestQuestionDao();
        testAnswerDao = db.getTestAnswerDao();
        testTagsQuestionsDao = db.getTestTagsQuestionsDao();
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
        addParam("wsKey", Login.getLoggedUser().getWsKey());
        addParam("courseCode", (int) Courses.getSelectedCourseCode());
        addParam("beginTime", timestamp);
        sendRequest(Test.class, false);

        if (result != null) {
            //Stores tests data returned by webservice response
            ArrayList<?> res = new ArrayList<>((Vector<?>) result);

            SoapObject tagsListObject = (SoapObject) res.get(0);
            SoapObject questionsListObject = (SoapObject) res.get(1);
            SoapObject answersListObject = (SoapObject) res.get(2);
            SoapObject questionTagsListObject = (SoapObject) res.get(3);
            List<TestTag> tagsList = new ArrayList<>();
            List<TestQuestion> questionsList = new ArrayList<>();
            List<TestAnswer> answersList = new ArrayList<>();
            List<TestTagsQuestions> tagsQuestionsList = new ArrayList<>();

            int listSizeTags = tagsListObject.getPropertyCount();
            int listSizeQuestions = questionsListObject.getPropertyCount();
            int listSizeAnswers = answersListObject.getPropertyCount();
            int listSizeQuestionTags = questionTagsListObject.getPropertyCount();

            //Read tags info from webservice response
            for (int i = 0; i < listSizeTags; i++) {
                SoapObject pii = (SoapObject) tagsListObject.getProperty(i);
                int tagCod = Integer.parseInt(pii.getProperty("tagCode").toString());
                String tagTxt = pii.getProperty("tagText").toString();
                TestTag tag = new TestTag(tagCod, tagTxt);
                tagsList.add(tag);

                if (isDebuggable)
                    Log.d(TAG, tag.toString());
            }

            testTagDao.insertTestTag(tagsList);

            //Read questions info from webservice response
            for (int i = 0; i < listSizeQuestions; i++) {
                SoapObject pii = (SoapObject) questionsListObject.getProperty(i);
                int qstCod = Integer.parseInt(pii.getProperty("questionCode").toString());
                String anstype = pii.getProperty("answerType").toString();
                int shuffle = Integer.parseInt(pii.getProperty("shuffle").toString());
                String stem = pii.getProperty("stem").toString();
                String questionFeedback = pii.getProperty("feedback").toString();
                TestQuestion q = new TestQuestion(qstCod, Courses.getSelectedCourseCode(), stem, anstype, Utils.parseIntBool(shuffle), questionFeedback);
                questionsList.add(q);
            }

            testQuestionDao.insertTestQuestion(questionsList);

            //Read answers info from webservice response
            for (int i = 0; i < listSizeAnswers; i++) {
                SoapObject pii = (SoapObject) answersListObject.getProperty(i);
                int qstCod = Integer.parseInt(pii.getProperty("questionCode").toString());
                int ansIndex = Integer.parseInt(pii.getProperty("answerIndex").toString());
                int correct = Integer.parseInt(pii.getProperty("correct").toString());
                String answer = pii.getProperty("answerText").toString();
                String answerFeeback = pii.getProperty("answerFeedback").toString();
                TestAnswer a = new TestAnswer(0L, qstCod, ansIndex, Utils.parseIntBool(correct), false, answer, answerFeeback, "");
                answersList.add(a);
            }

            testAnswerDao.insertTestAnswer(answersList);

            //Read relationships between questions and tags from webservice response
            for (int i = 0; i < listSizeQuestionTags; i++) {
                SoapObject pii = (SoapObject) questionTagsListObject.getProperty(i);
                int qstCod = Integer.parseInt(pii.getProperty("questionCode").toString());
                int tagCod = Integer.parseInt(pii.getProperty("tagCode").toString());
                int tagIndex = Integer.parseInt(pii.getProperty("tagIndex").toString());
                tagsQuestionsList.add(new TestTagsQuestions(qstCod, tagCod, tagIndex));
            }

            testTagsQuestionsDao.insertTestTagQuestion(tagsQuestionsList);

            timeAfter = System.currentTimeMillis();

            Log.i(TAG, "Retrieved " + listSizeTags + " tags");
            Log.i(TAG, "Retrieved " + listSizeQuestions + " questions");
            Log.i(TAG, "Retrieved " + listSizeAnswers + " answers");
            Log.i(TAG, "Retrieved " + listSizeQuestionTags + " relationships between questions and tags");
            Log.i(TAG, "Time elapsed = " + DateTimeUtils.millisToLongDHMS(timeAfter - timeBefore));
        }

        //Request finalized without errors
        setResult(RESULT_OK);
    }

    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.modules.Module#connect()
     */
    @Override
    protected void connect() {
        startConnection();
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
        // No-op
    }
}
