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

import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import org.ksoap2.serialization.PropertyInfo;

/**
 * Class for store a test
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class Test extends Model {
	/**
	 * List of questions and related answers
	 */
	private List<Pair<TestQuestion, List<TestAnswer>>> questionsAndAnswers;
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
	private static PropertyInfo PI_min = new PropertyInfo();
	private static PropertyInfo PI_def = new PropertyInfo();
	private static PropertyInfo PI_max = new PropertyInfo();
	private static PropertyInfo PI_feedback = new PropertyInfo();
    private static PropertyInfo[] PI_PROP_ARRAY =
    {
    	PI_min,
    	PI_def,
    	PI_max,
    	PI_feedback
    };
	
	/**
	 * Constructor from fields
	 * @param questionsAndAnswers List of questions and related answers
	 * @param min Minimum questions in test
	 * @param def Default questions in test
	 * @param max Maximum questions in test
	 * @param feedback Feedback to be showed to the student
	 */
	public Test(List<Pair<TestQuestion, List<TestAnswer>>> questionsAndAnswers,
			int min, int def, int max, String feedback) {
		super(0);
		this.questionsAndAnswers = questionsAndAnswers;
		this.min = min;
		this.def = def;
		this.max = max;
		this.feedback = feedback;
	}

	/**
	 * Gets the list of questions and related answers
	 * @return List of questions and related answers
	 */
	public List<Pair<TestQuestion, List<TestAnswer>>> getQuestionsAndAnswers() {
		return questionsAndAnswers;
	}

	/**
	 * Sets the list of questions and related answers
	 * @param questionsAndAnswers List of questions and related answers
	 */
	public void setQuestionsAndAnswers(
			List<Pair<TestQuestion, List<TestAnswer>>> questionsAndAnswers) {
		this.questionsAndAnswers = questionsAndAnswers;
	}

	/**
	 * Gets the question stored in position i and related answers
	 * @param i Question's position
	 * @return A pair of values <Question, List of answers>
	 */
	public Pair<TestQuestion, List<TestAnswer>> getQuestionAnswers(int i) {
		return questionsAndAnswers.get(i);
	}
	
	/**
	 * Gets minimum questions in test
	 * @return Minimum questions in test
	 */
	public int getMin() {
		return min;
	}

	/**
	 * Sets minimum questions in test
	 * @param min Minimum questions in test
	 */
	public void setMin(int min) {
		this.min = min;
	}

	/**
	 * Gets default questions in test
	 * @return Default questions in test
	 */
	public int getDef() {
		return def;
	}

	/**
	 * Sets default questions in test
	 * @param def Default questions in test
	 */
	public void setDef(int def) {
		this.def = def;
	}

	/**
	 * Gets maximum questions in test
	 * @return Maximum questions in test
	 */
	public int getMax() {
		return max;
	}

	/**
	 * Sets maximum questions in test
	 * @param max Maximum questions in test
	 */
	public void setMax(int max) {
		this.max = max;
	}

	/**
	 * Gets feedback to be showed to the student
	 * @return Feedback to be showed to the student
	 */
	public String getFeedback() {
		return feedback;
	}

	/**
	 * Sets feedback to be showed to the student
	 * @param feedback Feedback to be showed to the student
	 */
	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}

	/**
	 * Adds a question and related answers
	 * @param qa A pair of values <Question, List of answers>
	 */
	public void addQuestionAnswers(Pair<TestQuestion, List<TestAnswer>> qa) {
		questionsAndAnswers.add(qa);
	}
	
	/**
	 * Removes a question and related answers
	 * @param qa A pair of values <Question, List of answers>
	 */
	public void removeQuestionAnswers(Pair<TestQuestion, List<TestAnswer>> qa) {
		questionsAndAnswers.remove(qa);
	}
	
	/**
	 * Removes a question and related answers
	 * @param i Question's position
	 */
	public void removeQuestionAnswers(int i) {
		questionsAndAnswers.remove(i);
	}
	
	/**
	 * Shuffles the list of questions and related answers
	 */
	public void shuffle() {
		Collections.shuffle(questionsAndAnswers);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + def;
		result = prime * result
				+ ((feedback == null) ? 0 : feedback.hashCode());
		result = prime * result + max;
		result = prime * result + min;
		result = prime
				* result
				+ ((questionsAndAnswers == null) ? 0 : questionsAndAnswers
						.hashCode());
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
		Test other = (Test) obj;
		if (def != other.def)
			return false;
		if (feedback == null) {
			if (other.feedback != null)
				return false;
		} else if (!feedback.equals(other.feedback))
			return false;
		if (max != other.max)
			return false;
		if (min != other.min)
			return false;
		if (questionsAndAnswers == null) {
			if (other.questionsAndAnswers != null)
				return false;
		} else if (!questionsAndAnswers.equals(other.questionsAndAnswers))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Test [questionsAndAnswers=" + questionsAndAnswers + ", min="
				+ min + ", def=" + def + ", max=" + max + ", feedback="
				+ feedback + ", getId()=" + getId() + "]";
	}

	/* (non-Javadoc)
	 * @see org.ksoap2.serialization.KvmSerializable#getProperty(int)
	 */
	public Object getProperty(int param) {
		Object object = null;
        switch(param)
        {
            case 1 : object = min;break;
            case 2 : object = def;break;
            case 3 : object = max;break;
            case 4 : object = feedback;break;
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
	public void getPropertyInfo(int param, Hashtable arg1, PropertyInfo propertyInfo) {
		switch(param){
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
		switch(param)
		{
			case 0  : min = (Integer)obj; break;
			case 1  : def = (Integer)obj; break;
			case 2  : max = (Integer)obj; break;
			case 3  : feedback = (String)obj; break;
		}    
	}
}
