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

/**
 * Class for store a test tag
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class TestTag extends Model {
	/**
	 * Question code;
	 */
	private int qstCod;
	/**
	 * Tag text
	 */
	private String tagTxt;
	/**
	 * Tag index
	 */
	private int tagInd;
	private static PropertyInfo PI_id = new PropertyInfo();
	private static PropertyInfo PI_tagText = new PropertyInfo();
	private static PropertyInfo PI_tagInd = new PropertyInfo();
    private static PropertyInfo[] PI_PROP_ARRAY =
    {
    	PI_id,
    	PI_tagText,
    	PI_tagInd
    };

	/**
	 * Constructor
	 * @param id tag id
	 * @param tagTxt Tag text
	 * @param tagInd Tag index
	 */
	public TestTag(long id, String tagTxt, int tagInd) {
		super(id);
		this.tagTxt = tagTxt;
		this.tagInd = tagInd;
	}

	/**
	 * Constructor
	 * @param id tag id
	 * @param qstCod question id
	 * @param tagTxt Tag text
	 * @param tagInd Tag index
	 */
	public TestTag(long id, int qstCod, String tagTxt, int tagInd) {
		super(id);
		this.qstCod = qstCod;
		this.tagTxt = tagTxt;
		this.tagInd = tagInd;
	}
	
	/**
	 * Gets tag text
	 * @return Tag text
	 */
	public String getTagTxt() {
		return tagTxt;
	}

	/**
	 * Sets tag text
	 * @param tagTxt Tag text
	 */
	public void setTagTxt(String tagTxt) {
		this.tagTxt = tagTxt;
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

	/**
	 * Gets tag index
	 * @return Tag index
	 */
	public int getTagInd() {
		return tagInd;
	}

	/**
	 * Sets tag index
	 * @param tagInd Tag index
	 */
	public void setTagInd(int tagInd) {
		this.tagInd = tagInd;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + qstCod;
		result = prime * result + tagInd;
		result = prime * result + ((tagTxt == null) ? 0 : tagTxt.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TestTag [qstCod=" + qstCod + ", tagTxt=" + tagTxt + ", tagInd="
				+ tagInd + ", getId()=" + getId() + "]";
	}

	/* (non-Javadoc)
	 * @see org.ksoap2.serialization.KvmSerializable#getProperty(int)
	 */
	public Object getProperty(int param) {
		Object object = null;
        switch(param)
        {
            case 0 : object = this.getId();break;
            case 1 : object = tagTxt;break;
            case 2 : object = tagInd;break;
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
	            propertyInfo.type = PropertyInfo.LONG_CLASS;
	            propertyInfo.name = "id";
	            break;   
	        case 1:
	            propertyInfo.type = PropertyInfo.STRING_CLASS;
	            propertyInfo.name = "tagTxt";
	            break;           
	        case 2:
	            propertyInfo.type = PropertyInfo.INTEGER_CLASS;
	            propertyInfo.name = "tagInd";
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
			case 1  : tagTxt = (String)obj; break;
			case 2  : tagInd = (Integer)obj; break;
		}    
	}

}
