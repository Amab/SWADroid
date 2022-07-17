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

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;

import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

import lombok.Data;

/**
 * Class for store a test answer
 *
 * @author Juan Miguel Boyero Corral <swadroid@gmail.com>
 */
@Data
@Entity(indices = {@Index("qstCod"), @Index("ansInd")},
        foreignKeys = {
                @ForeignKey(entity = TestQuestion.class,
                        parentColumns = "id",
                        childColumns = "qstCod",
                        onDelete = ForeignKey.CASCADE)})
public class TestAnswer extends Model {
    /**
     * Question code;
     */
    private long qstCod;
    /**
     * Answer index;
     */
    private int ansInd;
    /**
     * Flag to know if this is the correct answer of the question
     */
    private boolean correct;
    /**
     * Flag to know if the user has answered correctly
     */
    @Ignore
    private boolean correctAnswered;
    /**
     * Answer's text
     */
    private String answer;
    /**
     * Answer's feedback
     */
    private String feedback;
    /**
     * User answer
     */
    @Ignore
    private String userAnswer;
    /**
     * Type integer
     */
    public static final String TYPE_INT = "int";
    /**
     * Type float
     */
    public static final String TYPE_FLOAT = "float";
    /**
     * Type true/false
     */
    public static final String TYPE_TRUE_FALSE = "TF";
    /**
     * Type unique choice
     */
    public static final String TYPE_UNIQUE_CHOICE = "uniqueChoice";
    /**
     * Type multiple choice
     */
    public static final String TYPE_MULTIPLE_CHOICE = "multipleChoice";
    /**
     * Type text
     */
    public static final String TYPE_TEXT = "text";
    /**
     * True value
     */
    public static final String VALUE_TRUE = "T";
    /**
     * False value
     */
    public static final String VALUE_FALSE = "F";

    @Ignore
    private static final PropertyInfo PI_id = new PropertyInfo();
    @Ignore
    private static final PropertyInfo PI_correct = new PropertyInfo();
    @Ignore
    private static final PropertyInfo PI_correctAnswered = new PropertyInfo();
    @Ignore
    private static final PropertyInfo PI_answer = new PropertyInfo();
    @Ignore
    private static final PropertyInfo PI_ansInd = new PropertyInfo();
    @Ignore
    private static final PropertyInfo PI_feedback = new PropertyInfo();
    @Ignore
    private static PropertyInfo[] PI_PROP_ARRAY =
            {
                    PI_id,
                    PI_correct,
                    PI_correctAnswered,
                    PI_answer,
                    PI_ansInd,
                    PI_feedback
            };

    public TestAnswer(long id, long qstCod, int ansInd, boolean correct, String answer, String feedback) {
        super(id);
        this.qstCod = qstCod;
        this.ansInd = ansInd;
        this.correct = correct;
        this.answer = answer;
        this.feedback = feedback;
    }

    public TestAnswer(long id, int qstCod, int ansInd, boolean correct, boolean correctAnswered, String answer, String feedback, String userAnswer) {
        this(id, qstCod, ansInd, correct, answer, feedback);
        this.correctAnswered = correctAnswered;
        this.userAnswer = userAnswer;
    }

    /* (non-Javadoc)
     * @see org.ksoap2.serialization.KvmSerializable#getProperty(int)
     */
    public Object getProperty(int param) {
        Object object = null;
        switch (param) {
            case 0:
                object = this.getId();
                break;
            case 1:
                object = correct;
                break;
            case 2:
                object = correctAnswered;
                break;
            case 3:
                object = answer;
                break;
            case 4:
                object = ansInd;
                break;
            case 5:
                object = feedback;
                break;
        }

        return object;
    }

    /* (non-Javadoc)
     * @see org.ksoap2.serialization.KvmSerializable#getPropertyCount()
     */
    public int getPropertyCount() {
        return PI_PROP_ARRAY.length;
    }

    /* (non-Javadoc)
     * @see org.ksoap2.serialization.KvmSerializable#getPropertyInfo(int, java.util.Hashtable, org.ksoap2.serialization.PropertyInfo)
     */
    public void getPropertyInfo(int param, Hashtable arg1, PropertyInfo propertyInfo) {
        switch (param) {
            case 0:
                propertyInfo.type = PropertyInfo.LONG_CLASS;
                propertyInfo.name = "id";
                break;
            case 1:
                propertyInfo.type = PropertyInfo.BOOLEAN_CLASS;
                propertyInfo.name = "correct";
                break;
            case 2:
                propertyInfo.type = PropertyInfo.BOOLEAN_CLASS;
                propertyInfo.name = "correctAnswered";
                break;
            case 3:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "answer";
                break;
            case 4:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "ansInd";
                break;
            case 5:
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
                this.setId((Integer) obj);
                break;
            case 1:
                correct = (Boolean) obj;
                break;
            case 2:
                correctAnswered = (Boolean) obj;
                break;
            case 3:
                answer = (String) obj;
                break;
            case 4:
                ansInd = (Integer) obj;
                break;
            case 5:
                feedback = (String) obj;
                break;
        }
    }

}