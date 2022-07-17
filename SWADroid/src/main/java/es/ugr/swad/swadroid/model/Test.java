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
package es.ugr.swad.swadroid.model;

import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import es.ugr.swad.swadroid.utils.Utils;
import lombok.Data;

/**
 * Class for store a test
 *
 * @author Juan Miguel Boyero Corral <swadroid@gmail.com>
 */
@Data
public class Test {
    /**
     * Correct answer score
     */
    private static final float CORRECT_ANSWER_SCORE = 1;
    /**
     * None feedback
     */
    public static final String FEEDBACK_NONE = "nothing";
    /**
     * Minimum feedback
     */
    public static final String FEEDBACK_MIN = "totalResult";
    /**
     * Medium feedback
     */
    public static final String FEEDBACK_MEDIUM = "eachResult";
    /**
     * High feedback
     */
    public static final String FEEDBACK_HIGH = "eachGoodBad";
    /**
     * Maximum feedback
     */
    public static final String FEEDBACK_MAX = "fullFeedback";
    /**
     * Feedback values
     */
    public static final List<String> FEEDBACK_VALUES = Arrays.asList(FEEDBACK_NONE, FEEDBACK_MIN, FEEDBACK_MEDIUM, FEEDBACK_HIGH, FEEDBACK_MAX);
    /**
     * Test config
     */
    private final TestConfig testConfig;
    /**
     * List of questions and related answers
     */
    private List<TestAnswersQuestion> answersQuestions;
    /**
     * Total test's score
     */
    private float totalScore;
    /**
     * Individual answers's score
     */
    private List<Float> questionsScore;
    /**
     * Flag for check if the test has been evaluated
     */
    private boolean evaluated;

    public Test(TestConfig testConfig, List<TestAnswersQuestion> answersQuestions) {
        this.testConfig = testConfig;
        this.answersQuestions = answersQuestions;
    }

    /**
     * Gets the question stored in position i and related answers
     *
     * @param i Question's position
     * @return A pair of values <Question, List of answers>
     */
    public TestAnswersQuestion getQuestionAndAnswers(int i) {
        return answersQuestions.get(i);
    }

    /**
     * Gets individual score for question in position i
     *
     * @param pos Questions's position in test
     * @return Individual score for question in position i
     */
    public Float getQuestionScore(int pos) {
        return questionsScore.get(pos);
    }

    /**
     * Sets individual answers's score
     *
     * @param pos   Questions's position in test
     * @param score Questions's score
     */
    public void setQuestionScore(int pos, Float score) {
        this.questionsScore.set(pos, score);
    }

    /**
     * Initializes questions's score and total score
     */
    private void prepareEvaluation() {
        evaluated = false;
        totalScore = 0;
        questionsScore.clear();

        for (int i = 0; i<this.answersQuestions.size(); i++) {
            this.questionsScore.add((float) 0);
        }
    }

    /**
     * Prepares a string for evaluate() method
     *
     * @param s String to be prepared
     * @return String without unnecessary spaces or accents
     */
    private String prepareString(String s) {
        //Remove spaces
        StringTokenizer tokens = new StringTokenizer(s);
        StringBuilder buff = new StringBuilder();
        
        while (tokens.hasMoreTokens()) {
            buff.append(" ").append(tokens.nextToken());
        }
        
        s = buff.toString().trim();

        //Remove accents
        s = Utils.unAccent(s);

        return s;
    }

    /**
     * Calculates total score and individual question's score of the test
     */
    public void evaluate() {
        int totalAnswers;
        int trueAnswers;
        int falseAnswers;
        int correctUserAnswers;
        int errors;
        float score;
        float userFloatAnswer;
        float minFloatRange;
        float maxFloatRange;
        TestAnswersQuestion aQ;
        TestQuestion q;
        TestAnswer a;
        List<TestAnswer> la;
        String answerType;
        String userAnswerText;
        String answerText;
        boolean noneSelected;

        prepareEvaluation();
        for (int i = 0; i < answersQuestions.size(); i++) {
            aQ = answersQuestions.get(i);
            q = aQ.testQuestion;
            la = aQ.testAnswers;
            answerType = q.getAnswerType();
            totalAnswers = 1;
            trueAnswers = 0;
            falseAnswers = 0;
            correctUserAnswers = 0;
            errors = 0;

            switch (answerType) {
                case "float":
                    a = la.get(0);

                    userAnswerText = a.getUserAnswer();
                    if (!userAnswerText.equals("")) {
                        userFloatAnswer = Float.parseFloat(userAnswerText);
                        minFloatRange = Float.parseFloat(a.getAnswer());
                        maxFloatRange = Float.parseFloat(la.get(1).getAnswer());
                        a.setCorrectAnswered((userFloatAnswer >= minFloatRange) && (userFloatAnswer <= maxFloatRange));
                    }

                    if (a.isCorrectAnswered()) {
                        correctUserAnswers++;
                    } else {
                        errors++;
                    }

                    if (userAnswerText.equals("")) {
                        score = 0;
                    } else {
                        score = correctUserAnswers / (float) totalAnswers;
                    }
                    break;
                case "TF":
                    a = la.get(0);

                    userAnswerText = a.getUserAnswer();
                    if (a.isCorrect()) {
                        a.setCorrectAnswered(a.getAnswer().equals(a.getUserAnswer()));
                    } else {
                        a.setCorrectAnswered(!a.getAnswer().equals(a.getUserAnswer()));
                    }

                    if (a.isCorrectAnswered()) {
                        correctUserAnswers++;
                    } else {
                        errors++;
                    }

                    if (userAnswerText.equals("")) {
                        score = 0;
                    } else {
                        score = (correctUserAnswers - errors);
                    }
                    break;
                case "int":
                    a = la.get(0);
                    userAnswerText = a.getUserAnswer();
                    a.setCorrectAnswered(a.getAnswer().equals(a.getUserAnswer()));

                    if (a.isCorrectAnswered()) {
                        correctUserAnswers++;
                    } else {
                        errors++;
                    }

                    if (userAnswerText.equals("")) {
                        score = 0;
                    } else {
                        score = correctUserAnswers / (float) totalAnswers;
                    }
                    break;
                case "text":
                    a = la.get(0);
                    userAnswerText = prepareString(a.getUserAnswer());
                    a.setCorrectAnswered(false);

                    for (TestAnswer ans : la) {
                        answerText = prepareString(ans.getAnswer());

                        if (userAnswerText.equalsIgnoreCase(answerText)) {
                            a.setCorrectAnswered(true);
                            break;
                        }
                    }

                    if (a.isCorrectAnswered()) {
                        correctUserAnswers++;
                    } else {
                        errors++;
                    }

                    if (userAnswerText.equals("")) {
                        score = 0;
                    } else {
                        score = correctUserAnswers / (float) totalAnswers;
                    }
                    break;
                case "uniqueChoice":
                    totalAnswers = la.size();
                    a = la.get(0);
                    a.setCorrectAnswered(false);

                    for (TestAnswer ans : la) {
                        if (ans.isCorrect() && ans.getAnswer().equals(a.getUserAnswer())) {
                            a.setCorrectAnswered(true);
                            break;
                        }
                    }

                    if (a.isCorrectAnswered()) {
                        correctUserAnswers++;
                    } else {
                        errors++;
                    }

                    if (a.getUserAnswer().equals("")) {
                        score = 0;
                    } else {
                        score = correctUserAnswers - (errors / ((float) totalAnswers - 1));
                    }
                    break;
                default:
                    noneSelected = true;
                    totalAnswers = la.size();
                    for (TestAnswer ans : la) {
                        if (ans.isCorrect()) {
                            trueAnswers++;
                            if (ans.getUserAnswer().equals("Y")) {
                                correctUserAnswers++;
                                noneSelected = false;
                            }
                        } else {
                            falseAnswers++;
                            if (ans.getUserAnswer().equals("Y")) {
                                errors++;
                                noneSelected = false;
                            }
                        }
                    }

                    if (noneSelected) {
                        score = 0;
                    } else {
                        if (falseAnswers == 0) {
                            score = correctUserAnswers / (float) totalAnswers;
                        } else {
                            score = (correctUserAnswers / (float) trueAnswers) - (errors / (float) falseAnswers);
                        }
                    }
                    break;
            }

            questionsScore.set(i, score * CORRECT_ANSWER_SCORE);
            totalScore += score * CORRECT_ANSWER_SCORE;
        }

        evaluated = true;
    }

}
