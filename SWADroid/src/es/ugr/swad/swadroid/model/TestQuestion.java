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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.ksoap2.serialization.PropertyInfo;

import es.ugr.swad.swadroid.utils.Utils;

/**
 * Clas for store a test question
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class TestQuestion extends Model {
	/**
	 * Course code
	 */
	private long crsCod;
	/**
	 * Question's text
	 */
	private String stem;
	/**
	 * Answer type
	 */
	private String answerType;
	/**
	 * Flag to shuffle answers in test
	 */
	private boolean shuffle;
	/**
	 * Question's feedback
	 */
	private String feedback;

	/**
	 * Question's answers
	 */
	private List<TestAnswer> answers;
	private static PropertyInfo PI_id = new PropertyInfo();
	private static PropertyInfo PI_stem = new PropertyInfo();
	private static PropertyInfo PI_editTime = new PropertyInfo();
	private static PropertyInfo PI_ansType = new PropertyInfo();
	private static PropertyInfo PI_shuffle = new PropertyInfo();
	private static PropertyInfo PI_feedback = new PropertyInfo();
	@SuppressWarnings("unused")
	private static PropertyInfo[] PI_PROP_ARRAY =
{
		PI_id,
		PI_stem,
		PI_editTime,
		PI_ansType,
		PI_shuffle,
		PI_feedback
};

	/**
	 * @param id Test identifier
	 * @param stem Question's text
	 * @param anstype Answer type
	 * @param shuffle Flag to shuffle answers in test
	 * @param feedback Text of the question's feedback
	 */
	public TestQuestion(long id, String stem, String anstype, boolean shuffle, String feedback) {
		super(id);
		this.stem = stem;
		this.answerType = anstype;
		this.shuffle = shuffle;
		this.answers = new ArrayList<TestAnswer>();
		this.feedback = feedback;
	}

	/**
	 * @param id Test identifier
	 * @param selectedCourseCode Course code
	 * @param stem Question's text
	 * @param anstype Answer type
	 * @param shuffle Flag to shuffle answers in test
	 * @param feedback Text of the question's feedback
	 */
	public TestQuestion(long id, long selectedCourseCode, String stem, String anstype, boolean shuffle, String feedback) {
		super(id);
		this.crsCod = selectedCourseCode;
		this.stem = stem;
		this.answerType = anstype;
		this.shuffle = shuffle;
		this.answers = new ArrayList<TestAnswer>();
		this.feedback = feedback;
	}

	/**
	 * Gets question code
	 * @return Question code
	 */
	public long getCrsCod() {
		return crsCod;
	}

	/**
	 * Sets question code
	 * @param crsCod Question code
	 */
	public void setCrsCod(long crsCod) {
		this.crsCod = crsCod;
	}

	/**
	 * Gets question's text
	 * @return Question's text
	 */
	public String getStem() {
		return stem;
	}

	/**
	 * Sets question's text
	 * @param stem Question's text
	 */
	public void setStem(String stem) {
		this.stem = stem;
	}

	/**
	 * Gets answer type
	 * @return Answer type
	 */
	public String getAnswerType() {
		return answerType;
	}

	/**
	 * Sets answer type
	 * @param anstype Answer type
	 */
	public void setAnswerType(String anstype) {
		this.answerType = anstype;
	}

	/**
	 * Gets shuffle flag
	 * @return Shuffle flag
	 */
	public boolean getShuffle() {
		return shuffle;
	}

	/**
	 * Sets shuffle flag
	 * @param shuffle Shuffle flag
	 */
	public void setShuffle(boolean shuffle) {
		this.shuffle = shuffle;
	}

	/**
	 * Gets question's answers
	 * @return Question's answers
	 */
	public List<TestAnswer> getAnswers() {
		return answers;
	}

	/**
	 * Sets question's answers
	 * @param answers Question's answers
	 */
	public void setAnswers(List<TestAnswer> answers) {
		this.answers = answers;
	}

	/** Gets the text of the question's feedback
	 * @return Text of the question's feedback
	 */
	public String getFeedback() {
		return feedback;
	}

	/**
	 * Sets the text of the question's feedback
	 * @param feedback Text of the question's feedback
	 */
	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}
	
	/**
	 * Gets correct answer for this test
	 * @return Correct test's answer
	 */
	public List<TestAnswer> getCorrectAnswers() {
		List<TestAnswer> corrects = new ArrayList<TestAnswer>();
		TestAnswer current = null;
		Iterator<TestAnswer> it = this.answers.iterator();

		while(it.hasNext()) {
			current = it.next();
			if(current.getCorrect()) {
				corrects.add(current);
			}
		}

		if(corrects.isEmpty()) {
			corrects = null;
		}

		return corrects;
	}

	/**
	 * Shuffles the list of related answers in a question
	 */
	public void shuffleAnswers() {
		Collections.shuffle(answers);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((answerType == null) ? 0 : answerType.hashCode());
		result = prime * result + ((answers == null) ? 0 : answers.hashCode());
		result = prime * result + (int) (crsCod ^ (crsCod >>> 32));
		result = prime * result
				+ ((feedback == null) ? 0 : feedback.hashCode());
		result = prime * result + (shuffle ? 1231 : 1237);
		result = prime * result + ((stem == null) ? 0 : stem.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TestQuestion [crsCod=" + crsCod + ", stem=" + stem
				+ ", answerType=" + answerType + ", shuffle=" + shuffle
				+ ", feedback=" + feedback + ", answers=" + answers + "]";
	}

	/* (non-Javadoc)
	 * @see org.ksoap2.serialization.KvmSerializable#getProperty(int)
	 */
	public Object getProperty(int param) {
		Object object = null;
		switch(param)
		{
		case 0 : object = this.getId();break;
		case 1 : object = stem;break;
		case 2 : object = answerType;break;
		case 3 : object = shuffle;break;
		case 4 : object = feedback;break;
		}

		return object;
	}

	/* (non-Javadoc)
	 * @see org.ksoap2.serialization.KvmSerializable#getPropertyCount()
	 */
	public int getPropertyCount() {
		return 5;
	}

	/* (non-Javadoc)
	 * @see org.ksoap2.serialization.KvmSerializable#getPropertyInfo(int, java.util.Hashtable, org.ksoap2.serialization.PropertyInfo)
	 */
	public void getPropertyInfo(int param, @SuppressWarnings("rawtypes") Hashtable arg1, PropertyInfo propertyInfo) {
		switch(param){
		case 0:
			propertyInfo.type = PropertyInfo.LONG_CLASS;
			propertyInfo.name = "id";
			break;   
		case 1:
			propertyInfo.type = PropertyInfo.STRING_CLASS;
			propertyInfo.name = "stem";
			break;  
		case 2:
			propertyInfo.type = PropertyInfo.STRING_CLASS;
			propertyInfo.name = "anstype";
			break;  
		case 3:
			propertyInfo.type = PropertyInfo.BOOLEAN_CLASS;
			propertyInfo.name = "shuffle";
			break; 
		case 4:
			propertyInfo.type = PropertyInfo.STRING_CLASS;
			propertyInfo.name = "feedback";
			break;
		}
	}

	/* (non-Javadoc)
	 * @see org.ksoap2.serialization.KvmSerializable#setProperty(int, java.lang.Object)
	 */
	public void setProperty(int param, Object obj) {
		switch(param)
		{
		case 0  : this.setId((Long)obj); break;
		case 1  : stem = (String)obj; break;
		case 2  : answerType = (String)obj; break;
		case 3  : shuffle = Utils.parseStringBool((String)obj); break;
		case 4  : feedback = (String)obj; break;
		}    
	}
}