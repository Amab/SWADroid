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

import java.util.Hashtable;

import org.ksoap2.serialization.PropertyInfo;

import es.ugr.swad.swadroid.Global;

/**
 * Class for store a test answer
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class TestAnswer extends Model {
	/**
	 * Question code;
	 */
	private int qstCod;
	/**
	 * Flag to know if this is the correct answer of the question
	 */
	private boolean correct;
	/**
	 * Answer's text
	 */
	private String answer;
	private static PropertyInfo PI_id = new PropertyInfo();
	private static PropertyInfo PI_correct = new PropertyInfo();
	private static PropertyInfo PI_answer = new PropertyInfo();
    private static PropertyInfo[] PI_PROP_ARRAY =
    {
    	PI_id,
    	PI_correct,
    	PI_answer
    };
	
	/**
	 * Constructor
	 * @param id Answer id
	 * @param qstCod Question code
	 * @param correct Flag to know if this is the correct answer of the question
	 * @param answer Answer's text
	 */
	public TestAnswer(int id, int qstCod, boolean correct, String answer) {
		super(id);
		this.qstCod = qstCod;
		this.correct = correct;
		this.answer = answer;
	}

	/**
	 * Gets answer correct flag
	 * @return Answer correct flag
	 */
	public boolean getCorrect() {
		return correct;
	}

	/**
	 * Sets answer correct flag
	 * @param Answer correct flag
	 */
	public void setCorrect(boolean correct) {
		this.correct = correct;
	}

	/**
	 * Gets answer text
	 * @return Answer text
	 */
	public String getAnswer() {
		return answer;
	}

	/**
	 * Sets answer text
	 * @param answer Answer text
	 */
	public void setAnswer(String answer) {
		this.answer = answer;
	}

	/**
	 * Gets question code
	 * @return Question code
	 */
	public int getQstCod() {
		return qstCod;
	}

	/**
	 * Sets question code
	 * @param qstCod question code
	 */
	public void setQstCod(int qstCod) {
		this.qstCod = qstCod;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + qstCod;
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
		TestAnswer other = (TestAnswer) obj;
		if (qstCod != other.qstCod)
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TestAnswer [correct=" + correct + ", answer=" + answer
				+ ", getId()=" + getId() + "]";
	}

	/* (non-Javadoc)
	 * @see org.ksoap2.serialization.KvmSerializable#getProperty(int)
	 */
	public Object getProperty(int param) {
		Object object = null;
        switch(param)
        {
            case 0 : object = this.getId();break;
            case 1 : object = correct;break;
            case 2 : object = answer;break;
        }
        
        return object;
	}

	/* (non-Javadoc)
	 * @see org.ksoap2.serialization.KvmSerializable#getPropertyCount()
	 */
	public int getPropertyCount() {
		return 3;
	}

	/* (non-Javadoc)
	 * @see org.ksoap2.serialization.KvmSerializable#getPropertyInfo(int, java.util.Hashtable, org.ksoap2.serialization.PropertyInfo)
	 */
	public void getPropertyInfo(int param, Hashtable arg1, PropertyInfo propertyInfo) {
		switch(param){
	        case 0:
	            propertyInfo.type = PropertyInfo.INTEGER_CLASS;
	            propertyInfo.name = "id";
	            break;   
	        case 1:
	            propertyInfo.type = PropertyInfo.STRING_CLASS;
	            propertyInfo.name = "correct";
	            break;    
	        case 2:
	            propertyInfo.type = PropertyInfo.STRING_CLASS;
	            propertyInfo.name = "answer";
	            break;           
		}
	}

	/* (non-Javadoc)
	 * @see org.ksoap2.serialization.KvmSerializable#setProperty(int, java.lang.Object)
	 */
	public void setProperty(int param, Object obj) {
		switch(param)
		{
			case 0  : this.setId((Integer)obj); break;
			case 1  : correct = Global.parseStringBool((String)obj); break;
			case 2  : answer = (String)obj; break;
		}    
	}
}