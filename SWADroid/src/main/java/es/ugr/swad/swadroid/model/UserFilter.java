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

import java.util.Hashtable;

import lombok.Data;

/**
 * User data.
 *
 * @author Rubén Martín Hidalgo
 */
@Data
public class UserFilter extends Model {
    /**
     * User identifier.
     */
    private String userNickname;
    /**
     * User first surname.
     */
    private String userSurname1;
    /**
     * User last surname.
     */
    private String userSurname2;
    /**
     * User name.
     */
    private String userFirstname;
    /**
     * Full path where user's picture is stored.
     */
    private String userPhoto;
    /**
     *  Unique identifier for each user
     */
    private int userCode;
    /**
     * Is a receiver?.
     */
    private boolean selectedCheckbox;

    public UserFilter(String userNickname, String userSurname1, String userSurname2, String userFirstname, String userPhoto, int userCode, boolean selectedCheckbox) {
        this.userNickname = userNickname;
        this.userSurname1 = userSurname1;
        this.userSurname2 = userSurname2;
        this.userFirstname = userFirstname;
        this.userPhoto = userPhoto;
        this.userCode = userCode;
        this.selectedCheckbox = selectedCheckbox;
    }

    @Override
    public Object getProperty(int index) {
        return null;
    }

    @Override
    public int getPropertyCount() {
        return 0;
    }

    @Override
    public void setProperty(int index, Object value) {
        // No-op
    }

    @Override
    public void getPropertyInfo(int index, Hashtable properties, PropertyInfo info) {
        // No-op
    }
}
