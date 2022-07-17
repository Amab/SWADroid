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
import androidx.room.Ignore;

import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

import lombok.Data;

/**
 * Class for store a test config
 *
 * @author Juan Miguel Boyero Corral <swadroid@gmail.com>
 */
@Data
@Entity
public class TestConfig extends Model {
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

    @Ignore
    private static final PropertyInfo PI_min = new PropertyInfo();
    @Ignore
    private static final PropertyInfo PI_def = new PropertyInfo();
    @Ignore
    private static final PropertyInfo PI_max = new PropertyInfo();
    @Ignore
    private static final PropertyInfo PI_feedback = new PropertyInfo();
    @Ignore
    private static PropertyInfo[] PI_PROP_ARRAY =
            {
                    PI_min,
                    PI_def,
                    PI_max,
                    PI_feedback
            };

    public TestConfig(long id, int min, int def, int max, String feedback, Long editTime) {
        super(id);
        this.min = min;
        this.def = def;
        this.max = max;
        this.feedback = feedback;
        this.editTime = editTime;
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
        return PI_PROP_ARRAY.length;
    }

    /* (non-Javadoc)
     * @see org.ksoap2.serialization.KvmSerializable#getPropertyInfo(int, java.util.Hashtable, org.ksoap2.serialization.PropertyInfo)
     */
    public void getPropertyInfo(int param, Hashtable arg1, PropertyInfo propertyInfo) {
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
