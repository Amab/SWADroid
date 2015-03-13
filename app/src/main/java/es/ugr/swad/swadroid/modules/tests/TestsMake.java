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

import android.annotation.TargetApi;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;
import es.ugr.swad.swadroid.SWADroidTracker;
import es.ugr.swad.swadroid.database.DataBaseHelper;
import es.ugr.swad.swadroid.gui.MenuActivity;
import es.ugr.swad.swadroid.gui.widget.CheckableLinearLayout;
import es.ugr.swad.swadroid.gui.widget.TextProgressBar;
import es.ugr.swad.swadroid.model.Test;
import es.ugr.swad.swadroid.model.TestAnswer;
import es.ugr.swad.swadroid.model.TestQuestion;
import es.ugr.swad.swadroid.model.TestTag;
import es.ugr.swad.swadroid.modules.Courses;
import es.ugr.swad.swadroid.utils.Utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Tests module for evaluate user skills in a course
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 * @author Helena Rodríguez Gijon <hrgijon@gmail.com>
 */
public class TestsMake extends MenuActivity {
    /**
     * Test's number of questions
     */
    private int numQuestions;
    /**
     * Test data
     */
    private Test test;
    /**
     * Tags's list of the test
     */
    private List<TestTag> tagsList;
    /**
     * Answer types's list of the test
     */
    private List<String> answerTypesList;
    /**
     * Click listener for courses dialog cancel button
     */
    private OnItemClickListener tagsAnswersTypeItemClickListener;
    /**
     * Adapter for answer TF questions
     */
    private ArrayAdapter<String> tfAdapter;
    /**
     * Test question being showed
     */
    private int actualQuestion;
    /**
     * ActionBar menu
     */
    private Menu menu;
    /**
     * Possible values for screen steps
     */
    private enum ScreenStep {MENU, NUM_QUESTIONS, TAGS, ANSWER_TYPES, TEST}
    /**
     * Step of actual screen
     */
    private ScreenStep screenStep;
    /**
     * Tests tag name for Logcat
     */
    private static final String TAG = Constants.APP_TAG + " TestsMake";

    /**
     * Sets layout maintaining tests action bar
     *
     * @param layout Layout to be applied
     */
    private void setLayout(int layout) {
        setContentView(layout);
    }
    
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setNumQuestions() {
    	if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.widget.NumberPicker numberPicker = 
            		(android.widget.NumberPicker) findViewById(R.id.testNumQuestionsNumberPicker);
            
    		numQuestions = numberPicker.getValue();
    	} else {
        	es.ugr.swad.swadroid.gui.widget.NumberPicker numberPickerOld =
            		(es.ugr.swad.swadroid.gui.widget.NumberPicker) findViewById(R.id.testNumQuestionsNumberPickerOld);
        	
    		numQuestions = numberPickerOld.getCurrent();
    	}

        if (isDebuggable) {
            Log.d(TAG, "numQuestions=" + numQuestions);
        }

        selectTags();
    }
    
    /**
     * Screen to select the number of questions in the test
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void selectNumQuestions() {          
        screenStep = ScreenStep.NUM_QUESTIONS;

        setLayout(R.layout.tests_num_questions);

        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.widget.NumberPicker numberPicker = 
            		(android.widget.NumberPicker) findViewById(R.id.testNumQuestionsNumberPicker);
            
	        numberPicker.setMaxValue(test.getMax());
	        numberPicker.setMinValue(test.getMin());
	        numberPicker.setValue(test.getDef());
	        numberPicker.setVisibility(View.VISIBLE);
        } else {
        	es.ugr.swad.swadroid.gui.widget.NumberPicker numberPickerOld =
        		(es.ugr.swad.swadroid.gui.widget.NumberPicker) findViewById(R.id.testNumQuestionsNumberPickerOld);
    		
        	numberPickerOld.setRange(test.getMin(), test.getMax());
        	numberPickerOld.setCurrent(test.getDef()); 
        	numberPickerOld.setVisibility(View.VISIBLE);
        }

        SWADroidTracker.sendScreenView(getApplicationContext(), TAG + " NumQuestions");
    }

    /**
     * Function to set the tags that will be present in the test
     */
    private void setTags() {
        ListView checkBoxesList = (ListView) findViewById(R.id.testTagsList);
        TagsArrayAdapter tagsAdapter = (TagsArrayAdapter) checkBoxesList.getAdapter();
    	int childsCount = checkBoxesList.getCount();
        SparseBooleanArray checkedItems = checkBoxesList.getCheckedItemPositions();
        tagsList = new ArrayList<TestTag>();

        //If "All tags" item checked, add the whole list to the list of selected tags
        if (checkedItems.get(0, false)) {
            tagsList.add(new TestTag(0, null, "all", 0));

        //If "All tags" item is not checked, add the selected items to the list of selected tags
        } else {
            for (int i = 0; i < childsCount; i++) {
                if (checkedItems.get(i, false)) {
                    tagsList.add(tagsAdapter.getItem(i));
                }
            }
        }

        if (isDebuggable) {
            Log.d(TAG, "tagsList=" + tagsList.toString());
        }

        //If no tags selected, show a message to notice user
        if (tagsList.isEmpty()) {
            Toast.makeText(getApplicationContext(), R.string.testNoTagsSelectedMsg, Toast.LENGTH_LONG).show();

            //If any tag is selected, show the answer types selection screen
        } else {
            selectAnswerTypes();
        }
    }

    /**
     * Screen to select the tags that will be present in the test
     */
    private void selectTags() {
        ListView checkBoxesList;
        TagsArrayAdapter tagsAdapter;
        List<TestTag> allTagsList = dbHelper.getOrderedCourseTags(Courses.getSelectedCourseCode());
        
        screenStep = ScreenStep.TAGS;

        //Add "All tags" item in list's top
        allTagsList.add(0, new TestTag(0, getResources().getString(R.string.allMsg), 0));

        setLayout(R.layout.tests_tags);

        checkBoxesList = (ListView) findViewById(R.id.testTagsList);
        tagsAdapter = new TagsArrayAdapter(this, R.layout.list_item_multiple_choice, allTagsList);
        checkBoxesList.setAdapter(tagsAdapter);
        checkBoxesList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        checkBoxesList.setOnItemClickListener(tagsAnswersTypeItemClickListener);
        checkBoxesList.setDividerHeight(0);

        SWADroidTracker.sendScreenView(getApplicationContext(), TAG + " Tags");
    }

    /**
     * Function to set the answer types that will be present in the test
     */
    private void setAnswerTypes() {
        ListView checkBoxesList = (ListView) findViewById(R.id.testAnswerTypesList);
        AnswerTypesArrayAdapter answerTypesAdapter = (AnswerTypesArrayAdapter) checkBoxesList.getAdapter();
    	int childsCount = checkBoxesList.getCount();
        SparseBooleanArray checkedItems = checkBoxesList.getCheckedItemPositions();
        answerTypesList = new ArrayList<String>();

		/*
         * If "All tags" item checked, add the whole list to the list of selected answer types,
		 * else, add the selected items to the list of selected answer types
		 */
        if (checkedItems.get(0, false)) {
            answerTypesList.add("all");
        } else {
            for (int i = 1; i < childsCount; i++) {
                if (checkedItems.get(i, false)) {
                    answerTypesList.add((String) answerTypesAdapter.getItem(i));
                }
            }
        }

        if (isDebuggable) {
            Log.d(TAG, "answerTypesList=" + answerTypesList.toString());
        }

        //If no answer types selected, show a message to notice user
        if (answerTypesList.isEmpty()) {
            Toast.makeText(getApplicationContext(), R.string.testNoAnswerTypesSelectedMsg, Toast.LENGTH_LONG)
                    .show();

            //If any answer type is selected, generate the test and show the first question screen
        } else {
            makeTest();
        }
    }

    /**
     * Screen to select the answer types that will be present in the test
     */
    private void selectAnswerTypes() {
        ListView checkBoxesList;
        AnswerTypesArrayAdapter answerTypesAdapter;
        
        screenStep = ScreenStep.ANSWER_TYPES;

        setLayout(R.layout.tests_answer_types);

        checkBoxesList = (ListView) findViewById(R.id.testAnswerTypesList);
        answerTypesAdapter = new AnswerTypesArrayAdapter(this, R.array.testAnswerTypes,
                R.array.testAnswerTypesNames, R.layout.list_item_multiple_choice);
        checkBoxesList.setAdapter(answerTypesAdapter);
        checkBoxesList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        checkBoxesList.setOnItemClickListener(tagsAnswersTypeItemClickListener);
        checkBoxesList.setDividerHeight(0);

        SWADroidTracker.sendScreenView(getApplicationContext(), TAG + " AnswerTypes");
    }

    /**
     * Shows a test question on screen
     *
     * @param pos Question's position in questions's list of the test
     */
    private void showQuestion(int pos) {
        TestQuestion question = test.getQuestions().get(pos);
        List<TestAnswer> answers = question.getAnswers();
        TestAnswer a;
        ScrollView scrollContent = (ScrollView) findViewById(R.id.testMakeScroll);
        LinearLayout testMakeList = (LinearLayout) findViewById(R.id.testMakeList);
        TextView stem = (TextView) findViewById(R.id.testMakeQuestionStem);
        TextView questionFeedback = (TextView) findViewById(R.id.testMakeQuestionFeedback);
        TextView answerFeedback = (TextView) findViewById(R.id.testMakeAnswerFeedback);
        TextView score = (TextView) findViewById(R.id.testMakeQuestionScore);
        TextView textCorrectAnswer = (TextView) findViewById(R.id.testMakeCorrectAnswer);
        EditText textAnswer = (EditText) findViewById(R.id.testMakeEditText);
        ImageView img = (ImageView) findViewById(R.id.testMakeCorrectAnswerImage);
    	MenuItem actionScoreItem = menu.findItem(R.id.action_score);
        CheckedAnswersArrayAdapter checkedAnswersAdapter;
        String answerType = question.getAnswerType();
        String feedback = test.getFeedback();
        String questionFeedbackText = question.getFeedback();
        String correctAnswer = "";
        int numAnswers = answers.size();
        Float questionScore;
        DecimalFormat df = new DecimalFormat("0.00");
        int feedbackLevel;
        int mediumFeedbackLevel = Test.FEEDBACK_VALUES.indexOf(Test.FEEDBACK_MEDIUM);
        int maxFeedbackLevel = Test.FEEDBACK_VALUES.indexOf(Test.FEEDBACK_MAX);

        scrollContent.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                findViewById(R.id.testMakeList).getParent().requestDisallowInterceptTouchEvent(false);
                return false;
            }
        });
        testMakeList.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        questionFeedback.setVisibility(View.GONE);
        answerFeedback.setVisibility(View.GONE);
        textAnswer.setVisibility(View.GONE);
        textCorrectAnswer.setVisibility(View.GONE);
        testMakeList.setVisibility(View.GONE);
        img.setVisibility(View.GONE);

        testMakeList.removeAllViews();
        stem.setText(Html.fromHtml(question.getStem()));

        if ((questionFeedbackText != null) && (!questionFeedbackText.equals(Constants.NULL_VALUE))) {
            questionFeedback.setText(Html.fromHtml(questionFeedbackText));
        }

        feedbackLevel = Test.FEEDBACK_VALUES.indexOf(feedback);

        if (test.isEvaluated() && (feedbackLevel == maxFeedbackLevel) && !question.getFeedback().equals(Constants.NULL_VALUE)) {
            questionFeedback.setVisibility(View.VISIBLE);
        } else {
            questionFeedback.setVisibility(View.GONE);
        }

        if (answerType.equals(TestAnswer.TYPE_TEXT)
                || answerType.equals(TestAnswer.TYPE_INT)
                || answerType.equals(TestAnswer.TYPE_FLOAT)) {

            if (answerType.equals(TestAnswer.TYPE_INT)) {
                textAnswer.setInputType(
                        InputType.TYPE_CLASS_NUMBER
                                | InputType.TYPE_NUMBER_FLAG_SIGNED);
            } else if (answerType.equals(TestAnswer.TYPE_FLOAT)) {
                textAnswer.setInputType(
                        InputType.TYPE_CLASS_NUMBER
                                | InputType.TYPE_NUMBER_FLAG_DECIMAL
                                | InputType.TYPE_NUMBER_FLAG_SIGNED);
            } else {
                textAnswer.setInputType(InputType.TYPE_CLASS_TEXT);
            }

            a = answers.get(0);
            textAnswer.setText(a.getUserAnswer());
            textAnswer.setVisibility(View.VISIBLE);

            answerFeedback.setText(Html.fromHtml(a.getFeedback()));

            if (test.isEvaluated() && (feedbackLevel > mediumFeedbackLevel)) {
                if (answerType.equals(TestAnswer.TYPE_FLOAT)) {
                    correctAnswer = "[" + a.getAnswer() + ";" + answers.get(1).getAnswer() + "]";

                    if ((feedbackLevel == maxFeedbackLevel) && !a.getFeedback().equals(Constants.NULL_VALUE)) {
                        answerFeedback.setVisibility(View.VISIBLE);
                    } else {
                        answerFeedback.setVisibility(View.GONE);
                    }
                } else {
                    for (int i = 0; i < numAnswers; i++) {
                        a = answers.get(i);

                        if ((feedbackLevel == maxFeedbackLevel) && !a.getFeedback().equals(Constants.NULL_VALUE)) {
                            correctAnswer += "<strong>" + a.getAnswer() + "</strong><br/>";
                            correctAnswer += "<i>" + a.getFeedback() + "</i><br/><br/>";
                        } else {
                            correctAnswer += a.getAnswer() + "<br/>";
                        }
                    }
                }

                textCorrectAnswer.setText(Html.fromHtml(correctAnswer));
                textCorrectAnswer.setVisibility(View.VISIBLE);
            }
        } else if (answerType.equals(TestAnswer.TYPE_MULTIPLE_CHOICE)) {
            checkedAnswersAdapter = new CheckedAnswersArrayAdapter(this, R.layout.list_item_multiple_choice,
                    answers, test.isEvaluated(), test.getFeedback(), answerType);

            for (int i = 0; i < numAnswers; i++) {
                a = answers.get(i);
                CheckableLinearLayout item = (CheckableLinearLayout) checkedAnswersAdapter.getView(i, null, null);
                item.setChecked(Utils.parseStringBool(a.getUserAnswer()));
                testMakeList.addView(item);
            }

            testMakeList.setVisibility(View.VISIBLE);
        } else {
            if (answerType.equals(TestAnswer.TYPE_TRUE_FALSE) && (numAnswers < 2)) {
                if (answers.get(0).getAnswer().equals(TestAnswer.VALUE_TRUE)) {
                    answers.add(1, new TestAnswer(0, 1, 0, false, TestAnswer.VALUE_FALSE, answers.get(0).getFeedback()));
                } else {
                    answers.add(0, new TestAnswer(0, 0, 0, false, TestAnswer.VALUE_TRUE, answers.get(0).getFeedback()));
                }

                numAnswers = 2;
            }

            checkedAnswersAdapter = new CheckedAnswersArrayAdapter(this, R.layout.list_item_single_choice,
                    answers, test.isEvaluated(), test.getFeedback(), answerType);

            for (int i = 0; i < numAnswers; i++) {
                a = answers.get(i);
                CheckableLinearLayout item = (CheckableLinearLayout) checkedAnswersAdapter.getView(i, null, null);
                item.setChecked(a.getAnswer().equals(answers.get(0).getUserAnswer()));
                testMakeList.addView(item);
            }

            testMakeList.setVisibility(View.VISIBLE);
        }

        if (test.isEvaluated() && (feedbackLevel > mediumFeedbackLevel)) {
            textAnswer.setEnabled(false);
            textAnswer.setOnClickListener(null);

            if (feedback.equals(Test.FEEDBACK_HIGH)) {
                img.setImageResource(R.drawable.btn_check_buttonless_on);
                if (!answerType.equals(TestAnswer.TYPE_TRUE_FALSE) && !answerType.equals(TestAnswer.TYPE_MULTIPLE_CHOICE)
                        && !answerType.equals(TestAnswer.TYPE_UNIQUE_CHOICE)) {

                    if (!answers.get(0).isCorrectAnswered()) {
                        img.setImageResource(android.R.drawable.ic_delete);
                    }

                    img.setVisibility(View.VISIBLE);
                }
            }

            questionScore = test.getQuestionScore(pos);
            if (questionScore > 0) {
                score.setTextColor(getResources().getColor(R.color.green));
            } else if (questionScore < 0) {
                score.setTextColor(getResources().getColor(R.color.red));
            } else {
                score.setTextColor(Color.BLACK);
            }
            
            score.setText(df.format(questionScore));

            MenuItemCompat.setActionView(actionScoreItem, score);
            actionScoreItem.setVisible(true);
        }
    }

    private int getCheckedItemPosition(LinearLayout parent) {
        int selectedPos = -1;
        int childCount = parent.getChildCount();
        boolean found = false;
        CheckableLinearLayout tv;

        for (int i = 0; !found && (i < childCount); i++) {
            tv = (CheckableLinearLayout) parent.getChildAt(i);
            found = tv.isChecked();

            if (found) {
                selectedPos = i;
            }
        }

        return selectedPos;
    }

    private SparseBooleanArray getCheckedItemPositions(LinearLayout parent) {
        SparseBooleanArray checkedItems = new SparseBooleanArray();
        int childCount = parent.getChildCount();
        CheckableLinearLayout tv;

        for (int i = 0; i < childCount; i++) {
            tv = (CheckableLinearLayout) parent.getChildAt(i);
            checkedItems.append(i, tv.isChecked());
        }

        return checkedItems;
    }

    /**
     * Reads the user answer of a question
     *
     * @param q Question to read the answer
     */
    private void readUserAnswer(TestQuestion q) {
        LinearLayout testMakeList = (LinearLayout) findViewById(R.id.testMakeList);
        EditText textAnswer = (EditText) findViewById(R.id.testMakeEditText);
        List<TestAnswer> la = q.getAnswers();
        int checkedListCount, selectedPos;
        String answerType, userAnswer;
        SparseBooleanArray checkedItems;

        answerType = q.getAnswerType();
        if (answerType.equals(TestAnswer.TYPE_TEXT)
                || answerType.equals(TestAnswer.TYPE_INT)
                || answerType.equals(TestAnswer.TYPE_FLOAT)) {

            la.get(0).setUserAnswer(String.valueOf(textAnswer.getText()));
        } else if (answerType.equals(TestAnswer.TYPE_MULTIPLE_CHOICE)) {
            checkedItems = getCheckedItemPositions(testMakeList);
            checkedListCount = checkedItems.size();
            for (int i = 0; i < checkedListCount; i++) {
                la.get(i).setUserAnswer(Utils.parseBoolString(checkedItems.get(i, false)));
            }
        } else {
            selectedPos = getCheckedItemPosition(testMakeList);
            if (selectedPos == -1) {
                userAnswer = "";
            } else {
                userAnswer = la.get(selectedPos).getAnswer();
            }

            la.get(0).setUserAnswer(userAnswer);
        }
    }

    /**
     * Shows the test
     */
    private void showTest() {
        final TextProgressBar bar;
        Button prev, next;
        final int size = test.getQuestions().size();

        setLayout(R.layout.tests_make_questions);
        prev = (Button) findViewById(R.id.testMakePrevButton);
        next = (Button) findViewById(R.id.testMakeNextButton);
        bar = (TextProgressBar) findViewById(R.id.test_questions_bar);

        bar.setMax(size);
        bar.setProgress(1);
        bar.setText(1 + "/" + size);
        bar.setTextColor(Color.BLUE);
        bar.setTextSize(20);
        
        if(!test.isEvaluated()) {
        	//Show evaluate button only
        	menu.findItem(R.id.action_evaluate).setVisible(true);
			menu.findItem(R.id.action_accept).setVisible(false);
        }

        actualQuestion = 0;
        prev.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TestQuestion question = test.getQuestionAndAnswers(actualQuestion);
                int pos;

                if (!test.isEvaluated()) {
                    readUserAnswer(question);
                }

                actualQuestion--;
                if (actualQuestion < 0) {
                    actualQuestion = size - 1;
                }

                pos = actualQuestion + 1;

                showQuestion(actualQuestion);
                bar.setProgress(pos);
                bar.setText(pos + "/" + size);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TestQuestion question = test.getQuestionAndAnswers(actualQuestion);
                int pos;

                if (!test.isEvaluated()) {
                    readUserAnswer(question);
                }

                actualQuestion++;
                actualQuestion %= size;
                pos = actualQuestion + 1;

                showQuestion(actualQuestion);
                bar.setProgress(pos);
                bar.setText(pos + "/" + size);
            }
        });

        showQuestion(0);

        SWADroidTracker.sendScreenView(getApplicationContext(), TAG + " Question");
    }

    /**
     * Generates the test
     */
    private void makeTest() {
        List<TestQuestion> questions;

        //Generates the test
        questions = dbHelper.getRandomCourseQuestionsByTagAndAnswerType(Courses.getSelectedCourseCode(), tagsList, answerTypesList,
                numQuestions);
        if (!questions.isEmpty()) {
            test.setQuestions(questions);

            //Shuffles related answers in a question if necessary
            for (TestQuestion q : questions) {
                if (q.getShuffle()) {
                    q.shuffleAnswers();
                }
            }

            //Shows the test
            showTest();
        } else {
            SWADroidTracker.sendScreenView(getApplicationContext(), TAG + " No questions criteria");

            Toast.makeText(this, R.string.testNoQuestionsMeetsSpecifiedCriteriaMsg, Toast.LENGTH_LONG).show();
            finish();
        }
    }
    
    private void evaluateTest() {
    	TextView textView;
        Float score, scoreDec;
        DecimalFormat df = new DecimalFormat("0.00");
        String feedback = test.getFeedback();

        readUserAnswer(test.getQuestionAndAnswers(actualQuestion));

        setLayout(R.layout.tests_make_results);
        if (!feedback.equals(Test.FEEDBACK_NONE)) {
            if (!test.isEvaluated()) {
                test.evaluate();
                
            	//Hide evaluate button
            	menu.findItem(R.id.action_evaluate).setVisible(false);
            }

            score = test.getTotalScore();
            scoreDec = (score / test.getQuestions().size()) * 10;

            textView = (TextView) findViewById(R.id.testResultsScore);
            textView.setText(df.format(score) + "/" + test.getQuestions().size() + "\n"
                    + df.format(scoreDec) + "/10");

            if (scoreDec < 5) {
                textView.setTextColor(getResources().getColor(R.color.red));
            }
            
            textView.setVisibility(View.VISIBLE);
            
        	//Show details button only
        	menu.findItem(R.id.action_show_details).setVisible(true);
        	menu.findItem(R.id.action_show_totals).setVisible(false);
        } else {
            textView = (TextView) findViewById(R.id.testResultsText);
            textView.setText(R.string.testNoResultsMsg);

            SWADroidTracker.sendScreenView(getApplicationContext(), TAG + " Feedback " + Test.FEEDBACK_NONE);
        }
    }

    /**
     * Launches an action when evaluate button is pushed
     *
     * @param v Actual view
     */
    public void onEvaluateClick(View v) {
    	evaluateTest();
    }

    /**
     * Launches an action when show results details button is pushed
     *
     * @param v Actual view
     */
    public void onShowResultsDetailsClick(View v) {
        Button evalBt, resBt;

        showTest();

        evalBt = (Button) findViewById(R.id.testEvaluateButton);
        resBt = (Button) findViewById(R.id.testShowResultsButton);

        evalBt.setVisibility(View.GONE);
        resBt.setVisibility(View.VISIBLE);
    }

    /* (non-Javadoc)
     * @see es.ugr.swad.swadroid.modules.Module#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        screenStep = ScreenStep.MENU;

        getActionBar().setSubtitle(Courses.getSelectedCourseShortName());
    	getActionBar().setIcon(R.drawable.test);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        tagsAnswersTypeItemClickListener = new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position,
                                    long id) {

                ListView lv = (ListView) parent;
                int childCount = lv.getCount();
                SparseBooleanArray checkedItems = lv.getCheckedItemPositions();
                boolean allChecked = true;

                if (position == 0) {
                    for (int i = 1; i < childCount; i++) {
                        lv.setItemChecked(i, checkedItems.get(0, false));
                    }
                } else {
                    for (int i = 1; i < childCount; i++) {
                        if (!checkedItems.get(i, false)) {
                            allChecked = false;
                        }
                    }

                    if (allChecked) {
                        lv.setItemChecked(0, true);
                    } else {
                        lv.setItemChecked(0, false);
                    }
                }
            }
        };

        tfAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        tfAdapter.add(getString(R.string.trueMsg));
        tfAdapter.add(getString(R.string.falseMsg));

        String selection = "id=" + Long.toString(Courses.getSelectedCourseCode());
        Cursor dbCursor = dbHelper.getDb().getCursor(DataBaseHelper.DB_TABLE_TEST_CONFIG, selection, null);
        startManagingCursor(dbCursor);
        
        if (dbCursor.getCount() > 0) {
            if (isDebuggable) {
                Log.d(TAG, "selectedCourseCode = " + Long.toString(Courses.getSelectedCourseCode()));
            }

            test = (Test) dbHelper.getRow(DataBaseHelper.DB_TABLE_TEST_CONFIG, "id",
                    Long.toString(Courses.getSelectedCourseCode()));

            if (test != null) {
                selectNumQuestions();
            } else {
                SWADroidTracker.sendScreenView(getApplicationContext(), TAG + " No questions");

                Toast.makeText(this, R.string.testNoQuestionsCourseMsg, Toast.LENGTH_LONG).show();
                finish();
            }
        } else {
            Toast.makeText(this, R.string.testNoQuestionsMsg, Toast.LENGTH_LONG).show();
            finish();
        }

        setResult(RESULT_OK);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tests_activity_actions, menu);
        this.menu = menu;
        
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        	case R.id.action_accept:
        	switch (screenStep) {
        		case NUM_QUESTIONS:
        			setNumQuestions();
        			
        			break;
        		case TAGS:        			
        			setTags();
        			
        			break;
        		case ANSWER_TYPES:
        			setAnswerTypes();
        			
        			break;
        		default:
        			menu.findItem(R.id.action_accept).setVisible(false);
        	}
        		
        		return true;
            case R.id.action_evaluate: 
            case R.id.action_show_totals:
            	menu.findItem(R.id.action_score).setVisible(false);
            	evaluateTest();
            	
                return true;
            case R.id.action_show_details:
            	if (test.getFeedback().equals(Test.FEEDBACK_MIN)) {
                    SWADroidTracker.sendScreenView(getApplicationContext(), TAG + " Feedback " + Test.FEEDBACK_MIN);
                    Toast.makeText(this, R.string.testNoDetailsMsg, Toast.LENGTH_LONG).show();
                } else {            	
	            	//Show totals button only
	            	menu.findItem(R.id.action_evaluate).setVisible(false);
	            	menu.findItem(R.id.action_show_details).setVisible(false);
	            	menu.findItem(R.id.action_show_totals).setVisible(true);
	            	
	            	showTest();
                }
            	            	
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }        
    }
}
