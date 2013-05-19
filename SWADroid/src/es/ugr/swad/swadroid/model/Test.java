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

import es.ugr.swad.swadroid.Constants;
import org.ksoap2.serialization.PropertyInfo;

import java.util.*;

/**
 * Class for store a test
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class Test extends Model {
    /**
     * Correct answer score
     */
    private final float CORRECT_ANSWER_SCORE = 1;
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
     * Tests tag name for Logcat
     */
    private static final String TAG = Constants.APP_TAG + " TestsMake";
    /**
     * List of questions and related answers
     */
    private List<TestQuestion> questions;
    /**
     * Minimum questions in test
     */
    private int min;
    /**
     * Default questions in test
     */
    private int def;
    /**
     * Maximum questions in test
     */
    private int max;
    /**
     * Feedback to be showed to the student
     */
    private String feedback;
    /**
     * Last time test was updated
     */
    private Long editTime;
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
    private static final PropertyInfo PI_min = new PropertyInfo();
    private static final PropertyInfo PI_def = new PropertyInfo();
    private static final PropertyInfo PI_max = new PropertyInfo();
    private static final PropertyInfo PI_feedback = new PropertyInfo();
    @SuppressWarnings("unused")
    private static PropertyInfo[] PI_PROP_ARRAY =
            {
                    PI_min,
                    PI_def,
                    PI_max,
                    PI_feedback
            };

    /**
     * Constructor from fields
     *
     * @param selectedCourseCode Code of test's course
     * @param min                Minimum questions in test
     * @param def                Default questions in test
     * @param max                Maximum questions in test
     * @param feedback           Feedback to be showed to the student
     */
    public Test(long selectedCourseCode, int min, int def, int max, String feedback) {
        super(selectedCourseCode);
        this.min = min;
        this.def = def;
        this.max = max;
        this.feedback = feedback;
        this.totalScore = 0;
        this.questionsScore = new ArrayList<Float>();
    }

    /**
     * Constructor from fields
     *
     * @param crsCode  Code of test's course
     * @param min      Minimum questions in test
     * @param def      Default questions in test
     * @param max      Maximum questions in test
     * @param feedback Feedback to be showed to the student
     * @param editTime Last time test was updated
     */
    public Test(long crsCode, int min, int def, int max, String feedback, Long editTime) {
        this(crsCode, min, def, max, feedback);
        this.editTime = editTime;
    }

    /**
     * Gets the question stored in position i and related answers
     *
     * @param i Question's position
     * @return A pair of values <Question, List of answers>
     */
    public TestQuestion getQuestionAndAnswers(int i) {
        return questions.get(i);
    }

    /**
     * Gets minimum questions in test
     *
     * @return Minimum questions in test
     */
    public int getMin() {
        return min;
    }

    /**
     * Sets minimum questions in test
     *
     * @param min Minimum questions in test
     */
    public void setMin(int min) {
        this.min = min;
    }

    /**
     * Gets default questions in test
     *
     * @return Default questions in test
     */
    public int getDef() {
        return def;
    }

    /**
     * Sets default questions in test
     *
     * @param def Default questions in test
     */
    public void setDef(int def) {
        this.def = def;
    }

    /**
     * Gets maximum questions in test
     *
     * @return Maximum questions in test
     */
    public int getMax() {
        return max;
    }

    /**
     * Sets maximum questions in test
     *
     * @param max Maximum questions in test
     */
    public void setMax(int max) {
        this.max = max;
    }

    /**
     * Gets feedback to be showed to the student
     *
     * @return Feedback to be showed to the student
     */
    public String getFeedback() {
        return feedback;
    }

    /**
     * Sets feedback to be showed to the student
     *
     * @param feedback Feedback to be showed to the student
     */
    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    /**
     * Gets last time test was updated
     *
     * @return Last time test was updated
     */
    public Long getEditTime() {
        return editTime;
    }

    /**
     * Sets last time test was updated
     *
     * @param timestamp Last time test was updated
     */
    public void setEditTime(Long timestamp) {
        this.editTime = timestamp;
    }

    /**
     * Gets total test's score
     *
     * @return Total test's score
     */
    public float getTotalScore() {
        return totalScore;
    }

    /**
     * Sets total test's score
     *
     * @param totalScore Total test's score
     */
    public void setTotalScore(float totalScore) {
        this.totalScore = totalScore;
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
     * Gets the questions list
     *
     * @return The questions list
     */
    public List<TestQuestion> getQuestions() {
        return questions;
    }

    /**
     * Sets the questions list
     *
     * @param questions The questions to set
     */
    public void setQuestions(List<TestQuestion> questions) {
        this.questions = questions;
    }

    /**
     * Gets the questions's score
     *
     * @return The questions's score
     */
    public List<Float> getQuestionsScore() {
        return questionsScore;
    }

    /**
     * Sets the questions's score
     *
     * @param questionsScore The questions's score
     */
    public void setQuestionsScore(List<Float> questionsScore) {
        this.questionsScore = questionsScore;
    }

    /**
     * Checks if the test has been evaluated
     *
     * @return true if the test has been evaluated
     *         false if the test hasn't been evaluated
     */
    public boolean isEvaluated() {
        return evaluated;
    }

    /**
     * Specifies if the test has been evaluated
     *
     * @param evaluated true if the test has been evaluated
     *                  false if the test hasn't been evaluated
     */
    public void setEvaluated(boolean evaluated) {
        this.evaluated = evaluated;
    }

    /**
     * Initializes questions's score and total score
     */
    void prepareEvaluation() {
        evaluated = false;
        totalScore = 0;
        questionsScore.clear();

        for (TestQuestion question : this.questions) {
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
        s = s.replace('á', 'a');
        s = s.replace('é', 'e');
        s = s.replace('í', 'i');
        s = s.replace('ó', 'o');
        s = s.replace('ú', 'u');

        return s;
    }

    /**
     * Calculates total score and individual question's score of the test
     */
    public void evaluate() {
        int totalAnswers, trueAnswers, falseAnswers, correctUserAnswers, errors;
        Float score, userFloatAnswer, minFloatRange, maxFloatRange;
        TestQuestion q;
        TestAnswer a;
        List<TestAnswer> la;
        String answerType, userAnswerText, answerText;
        boolean noneSelected;

        prepareEvaluation();
        for (int i = 0; i < questions.size(); i++) {
            q = questions.get(i);
            la = q.getAnswers();
            answerType = q.getAnswerType();
            totalAnswers = 1;
            trueAnswers = 0;
            falseAnswers = 0;
            correctUserAnswers = 0;
            errors = 0;

            if (answerType.equals("float")) {
                a = la.get(0);

                userAnswerText = a.getUserAnswer();
                if (!userAnswerText.equals("")) {
                    userFloatAnswer = Float.valueOf(userAnswerText);
                    minFloatRange = Float.valueOf(a.getAnswer());
                    maxFloatRange = Float.valueOf(la.get(1).getAnswer());
                    a.setCorrectAnswered((userFloatAnswer >= minFloatRange) && (userFloatAnswer <= maxFloatRange));
                }

                if (a.isCorrectAnswered()) {
                    correctUserAnswers++;
                } else {
                    errors++;
                }

                if (userAnswerText.equals("")) {
                    score = (float) 0;
                } else {
                    score = correctUserAnswers / (float) totalAnswers;
                }
            } else if (answerType.equals("TF")) {
                a = la.get(0);

                userAnswerText = a.getUserAnswer();
                if (a.getCorrect()) {
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
                    score = (float) 0;
                } else {
                    score = (float) (correctUserAnswers - errors);
                }
            } else if (answerType.equals("int")) {
                a = la.get(0);
                userAnswerText = a.getUserAnswer();
                a.setCorrectAnswered(a.getAnswer().equals(a.getUserAnswer()));

                if (a.isCorrectAnswered()) {
                    correctUserAnswers++;
                } else {
                    errors++;
                }

                if (userAnswerText.equals("")) {
                    score = (float) 0;
                } else {
                    score = correctUserAnswers / (float) totalAnswers;
                }
            } else if (answerType.equals("text")) {
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
                    score = (float) 0;
                } else {
                    score = correctUserAnswers / (float) totalAnswers;
                }
            } else if (answerType.equals("uniqueChoice")) {
                totalAnswers = la.size();
                a = la.get(0);
                a.setCorrectAnswered(false);

                for (TestAnswer ans : la) {
                    if (ans.getCorrect() && ans.getAnswer().equals(a.getUserAnswer())) {
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
                    score = (float) 0;
                } else {
                    score = correctUserAnswers - (errors / ((float) totalAnswers - 1));
                }
            } else {
                noneSelected = true;
                totalAnswers = la.size();
                for (TestAnswer ans : la) {
                    if (ans.getCorrect()) {
                        trueAnswers++;
                        if (ans.getUserAnswer().equals("Y")) {
                            correctUserAnswers++;
                            noneSelected = false;
                        }/* else {
                            errors++;
						}*/
                    } else {
                        falseAnswers++;
                        if (ans.getUserAnswer().equals("Y")) {
                            errors++;
                            noneSelected = false;
                        }
                    }
                }

                if (noneSelected) {
                    score = (float) 0;
                } else {
                    if (falseAnswers == 0) {
                        score = correctUserAnswers / (float) totalAnswers;
                    } else {
                        score = (correctUserAnswers / (float) trueAnswers) - (errors / (float) falseAnswers);
                    }
                }
            }

            questionsScore.set(i, score * CORRECT_ANSWER_SCORE);
            totalScore += score * CORRECT_ANSWER_SCORE;
        }

        evaluated = true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Test [CORRECT_ANSWER_SCORE=" + CORRECT_ANSWER_SCORE
                + ", questionsAndAnswers=" + questions + ", min="
                + min + ", def=" + def + ", max=" + max + ", feedback="
                + feedback + ", editTime=" + editTime + ", totalScore="
                + totalScore + ", questionsScore=" + questionsScore
                + ", getId()=" + getId() + "]";
    }

    /* (non-Javadoc)
     * @see org.ksoap2.serialization.KvmSerializable#getProperty(int)
     */
    public Object getProperty(int param) {
        Object object = null;
        switch (param) {
            case 1:
                object = min;
                break;
            case 2:
                object = def;
                break;
            case 3:
                object = max;
                break;
            case 4:
                object = feedback;
                break;
        }

        return object;
    }

    /* (non-Javadoc)
     * @see org.ksoap2.serialization.KvmSerializable#getPropertyCount()
     */
    public int getPropertyCount() {
        return 4;
    }

    /* (non-Javadoc)
     * @see org.ksoap2.serialization.KvmSerializable#getPropertyInfo(int, java.util.Hashtable, org.ksoap2.serialization.PropertyInfo)
     */
    public void getPropertyInfo(int param, @SuppressWarnings("rawtypes") Hashtable arg1, PropertyInfo propertyInfo) {
        switch (param) {
            case 0:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "min";
                break;
            case 1:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "def";
                break;
            case 2:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "max";
                break;
            case 3:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "feedback";
                break;
        }
    }

    /* (non-Javadoc)
     * @see org.ksoap2.serialization.KvmSerializable#setProperty(int, java.lang.Object)
     */
    public void setProperty(int param, Object obj) {
        switch (param) {
            case 0:
                min = (Integer) obj;
                break;
            case 1:
                def = (Integer) obj;
                break;
            case 2:
                max = (Integer) obj;
                break;
            case 3:
                feedback = (String) obj;
                break;
        }
    }
}
