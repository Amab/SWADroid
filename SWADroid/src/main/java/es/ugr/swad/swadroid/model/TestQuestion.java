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

import es.ugr.swad.swadroid.utils.Utils;
import lombok.Data;

/**
 * Class for store a test question
 *
 * @author Juan Miguel Boyero Corral <swadroid@gmail.com>
 */
@Data
@Entity(indices = {@Index("crsCod"), @Index("answerType"), @Index("shuffle")},
        foreignKeys = {
                @ForeignKey(entity = Course.class,
                        parentColumns = "id",
                        childColumns = "crsCod",
                        onDelete = ForeignKey.CASCADE)})
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

    @Ignore
    private static final PropertyInfo PI_id = new PropertyInfo();
    @Ignore
    private static final PropertyInfo PI_stem = new PropertyInfo();
    @Ignore
    private static final PropertyInfo PI_editTime = new PropertyInfo();
    @Ignore
    private static final PropertyInfo PI_ansType = new PropertyInfo();
    @Ignore
    private static final PropertyInfo PI_shuffle = new PropertyInfo();
    @Ignore
    private static final PropertyInfo PI_feedback = new PropertyInfo();
    @Ignore
    private static PropertyInfo[] PI_PROP_ARRAY =
            {
                    PI_id,
                    PI_stem,
                    PI_editTime,
                    PI_ansType,
                    PI_shuffle,
                    PI_feedback
            };

    public TestQuestion(long id, long crsCod, String stem, String answerType, boolean shuffle, String feedback) {
        super(id);
        this.crsCod = crsCod;
        this.stem = stem;
        this.answerType = answerType;
        this.shuffle = shuffle;
        this.feedback = feedback;
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
                object = stem;
                break;
            case 2:
                object = answerType;
                break;
            case 3:
                object = shuffle;
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
        switch (param) {
            case 0:
                this.setId((Long) obj);
                break;
            case 1:
                stem = (String) obj;
                break;
            case 2:
                answerType = (String) obj;
                break;
            case 3:
                shuffle = Utils.parseStringBool((String) obj);
                break;
            case 4:
                feedback = (String) obj;
                break;
        }
    }

}