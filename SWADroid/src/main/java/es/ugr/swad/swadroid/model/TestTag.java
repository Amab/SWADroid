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

import org.ksoap2.serialization.PropertyInfo;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Class for store a test tag
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class TestTag extends Model {
    /**
     * Question codes;
     */
    private List<Integer> qstCodList;
    /**
     * Tag text
     */
    private String tagTxt;
    /**
     * Tag index
     */
    private int tagInd;
    private static final PropertyInfo PI_id = new PropertyInfo();
    private static final PropertyInfo PI_tagText = new PropertyInfo();
    private static final PropertyInfo PI_tagInd = new PropertyInfo();
    @SuppressWarnings("unused")
    private static PropertyInfo[] PI_PROP_ARRAY =
            {
                    PI_id,
                    PI_tagText,
                    PI_tagInd
            };

    /**
     * Constructor
     *
     * @param id     tag id
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
     *
     * @param id     tag id
     * @param tagTxt Tag text
     * @param tagInd Tag index
     */
    public TestTag(long id, List<Integer> qstCodList, String tagTxt, int tagInd) {
        super(id);
        this.qstCodList = qstCodList;
        this.tagTxt = tagTxt;
        this.tagInd = tagInd;
    }

    /**
     * Gets tag text
     *
     * @return Tag text
     */
    public String getTagTxt() {
        return tagTxt;
    }

    /**
     * Sets tag text
     *
     * @param tagTxt Tag text
     */
    public void setTagTxt(String tagTxt) {
        this.tagTxt = tagTxt;
    }

    /**
     * Gets tag index
     *
     * @return Tag index
     */
    public int getTagInd() {
        return tagInd;
    }

    /**
     * Sets tag index
     *
     * @param tagInd Tag index
     */
    public void setTagInd(int tagInd) {
        this.tagInd = tagInd;
    }

    @Override
    public String toString() {
        return "TestTag [id=" + getId() + ", qstCodList=" + qstCodList + ", tagTxt=" + tagTxt
                + ", tagInd=" + tagInd + "]";
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
                object = tagTxt;
                break;
            case 2:
                object = tagInd;
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
    public void getPropertyInfo(int param, @SuppressWarnings("rawtypes") Hashtable arg1, PropertyInfo propertyInfo) {
        switch (param) {
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
        switch (param) {
            case 0:
                this.setId((Long) obj);
                break;
            case 1:
                tagTxt = (String) obj;
                break;
            case 2:
                tagInd = (Integer) obj;
                break;
        }
    }

    /**
     * Gets question codes
     *
     * @return Question codes
     */
    public List<Integer> getQstCodList() {
        return qstCodList;
    }

    /**
     * Sets question codes
     *
     * @param qstCodList Question codes
     */
    public void setQstCodList(List<Integer> qstCodList) {
        this.qstCodList = qstCodList;
    }

    /**
     * Gets the question code in position i
     *
     * @param i Position of question code
     * @return Question code
     */
    public Integer getQstCod(int i) {
        return this.qstCodList.get(i);
    }

    /**
     * Adds a question code to the list
     *
     * @param qstCod Question code to be added
     */
    public void addQstCod(Integer qstCod) {
        if (this.qstCodList == null) {
            this.qstCodList = new ArrayList<>();
        }

        this.qstCodList.add(qstCod);
    }
}
