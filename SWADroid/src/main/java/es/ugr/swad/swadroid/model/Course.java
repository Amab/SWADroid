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
import androidx.room.Index;

import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

import lombok.Data;

/**
 * Class for store a course
 *
 * @author Juan Miguel Boyero Corral <swadroid@gmail.com>
 * @author Helena Rodriguez Gijon <hrgijon@gmail.com>
 */
@Data
@Entity(indices = {@Index("shortName")})
public class Course extends Model {
    /**
     * Course name
     */
    private int userRole;
    private String shortName;
    private String fullName;

    @Ignore
    private static final PropertyInfo PI_id = new PropertyInfo();
    @Ignore
    private static final PropertyInfo PI_userRole = new PropertyInfo();
    @Ignore
    private static final PropertyInfo PI_shortName = new PropertyInfo();
    @Ignore
    private static final PropertyInfo PI_fullName = new PropertyInfo();
    @Ignore
    private static final PropertyInfo[] PI_PROP_ARRAY =
            {
                    PI_id,
                    PI_userRole,
                    PI_shortName,
                    PI_fullName
            };

    public Course(long id, int userRole, String shortName, String fullName) {
        super(id);
        this.userRole = userRole;
        this.shortName = shortName;
        this.fullName = fullName;
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
                object = userRole;
                break;
            case 2:
                object = shortName;
                break;
            case 3:
                object = fullName;
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
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "userRole";
                break;
            case 2:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "shortName";
                break;
            case 3:
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                propertyInfo.name = "fullName";
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
                userRole = (Integer) obj;
                break;
            case 2:
                shortName = (String) obj;
                break;
            case 3:
                fullName = (String) obj;
                break;

        }
    }

}