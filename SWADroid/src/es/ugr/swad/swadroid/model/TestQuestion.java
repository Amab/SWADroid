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
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.ksoap2.serialization.PropertyInfo;

import es.ugr.swad.swadroid.Global;

/**
 * Clas for store a test question
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class TestQuestion extends Model {
	/**
	 * Course code
	 */
	private int crsCod;
	/**
	 * Question's text
	 */
	private String stem;
	/**
	 * Answer type
	 */
	private String anstype;
	/**
	 * Flag to shuffle answers in test
	 */
	private boolean shuffle;
	/**
	 * Question's answers
	 */
	private List<TestAnswer> answers;
	private static PropertyInfo PI_id = new PropertyInfo();
	private static PropertyInfo PI_stem = new PropertyInfo();
	private static PropertyInfo PI_editTime = new PropertyInfo();
	private static PropertyInfo PI_ansType = new PropertyInfo();
	private static PropertyInfo PI_shuffle = new PropertyInfo();
    private static PropertyInfo[] PI_PROP_ARRAY =
    {
    	PI_id,
    	PI_stem,
    	PI_editTime,
    	PI_ansType,
    	PI_shuffle
    };
	
	/**
	 * @param id Test identifier
	 * @param stem Question's text
	 * @param anstype Answer type
	 * @param shuffle Flag to shuffle answers in test
	 */
	public TestQuestion(int id, String stem, String anstype, boolean shuffle) {
		super(id);
		this.stem = stem;
		this.anstype = anstype;
		this.shuffle = shuffle;
		this.answers = new ArrayList<TestAnswer>();
	}
	
	/**
	 * @param id Test identifier
	 * @param crsCod Course code
	 * @param stem Question's text
	 * @param anstype Answer type
	 * @param shuffle Flag to shuffle answers in test
	 */
	public TestQuestion(int id, int crsCod, String stem, String anstype, boolean shuffle) {
		super(id);
		this.crsCod = crsCod;
		this.stem = stem;
		this.anstype = anstype;
		this.shuffle = shuffle;
		this.answers = new ArrayList<TestAnswer>();
	}

	/**
	 * Gets question code
	 * @return Question code
	 */
	public int getCrsCod() {
		return crsCod;
	}

	/**
	 * Sets question code
	 * @param crsCod Question code
	 */
	public void setCrsCod(int crsCod) {
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
	public String getAnstype() {
		return anstype;
	}

	/**
	 * Sets answer type
	 * @param anstype Answer type
	 */
	public void setAnstype(String anstype) {
		this.anstype = anstype;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((anstype == null) ? 0 : anstype.hashCode());
		result = prime * result + ((answers == null) ? 0 : answers.hashCode());
		result = prime * result + (shuffle ? 1231 : 1237);
		result = prime * result + ((stem == null) ? 0 : stem.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TestQuestion [stem=" + stem + ", anstype=" + anstype + ", shuffle=" + shuffle
				+ ", answers=" + answers + ", getId()=" + getId() + "]";
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
            case 2 : object = anstype;break;
            case 3 : object = shuffle;break;
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
	            propertyInfo.type = PropertyInfo.STRING_CLASS;
	            propertyInfo.name = "shuffle";
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
			case 1  : stem = (String)obj; break;
			case 2  : anstype = (String)obj; break;
			case 3  : shuffle = Global.parseStringBool((String)obj); break;
		}    
	}
}