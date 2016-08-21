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

/**
 * User data.
 *
 * @author Rubén Martín Hidalgo
 */
public class FrequentUser extends Model {
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
     * Is a receiver?.
     */
    private boolean selectedCheckbox;
    /**
     * Score of frequent recipient
     */
    private double score;

    /**
     * Constructor.
     *
     * @param userNickname  User identifier.
     * @param userSurname1  User first surname.
     * @param userSurname2  User last surname.
     * @param userFirstname User name.
     * @param userPhoto     Full path where user's picture is stored.
     * @param selectedCheckbox  Is a receiver?.
     * @param score         Score of frequent recipient
     */
    public FrequentUser(String userNickname, String userSurname1, String userSurname2, String userFirstname, String userPhoto, boolean selectedCheckbox, double score) {
        this.userNickname = userNickname;
        this.userSurname1 = userSurname1;
        this.userSurname2 = userSurname2;
        this.userFirstname = userFirstname;
        this.userPhoto = userPhoto;
        this.selectedCheckbox = selectedCheckbox;
        this.score = score;
    }

    /**
     * Gets user name.
     *
     * @return User name.
     */
    public String getUserFirstname() {
        return userFirstname;
    }

    /**
     * Sets user name.
     *
     * @param userFirstname User name.
     */
    public void setUserFirstname(String userFirstname) {
        this.userFirstname = userFirstname;
    }

    /**
     * Gets user identifier.
     *
     * @return User identifier.
     */
    public String getUserNickname() {
        return userNickname;
    }

    /**
     * Sets user identifier.
     *
     * @param userNickname User identifier.
     */
    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    /**
     * Gets user first surname.
     *
     * @return User first surname.
     */
    public String getUserSurname1() {
        return userSurname1;
    }

    /**
     * Sets user first surname.
     *
     * @param userSurname1 User first surname.
     */
    public void setUserSurname1(String userSurname1) {
        this.userSurname1 = userSurname1;
    }

    /**
     * Gets user last surname.
     *
     * @return User last surname.
     */
    public String getUserSurname2() {
        return userSurname2;
    }

    /**
     * Sets user last surname.
     *
     * @param userSurname2 User last surname.
     */
    public void setUserSurname2(String userSurname2) {
        this.userSurname2 = userSurname2;
    }

    /**
     * Gets Full path where user's picture is stored.
     *
     * @return the userPhoto
     */
    public String getUserPhoto() {
        return userPhoto;
    }

    /**
     * Sets Full path where user's picture is stored.
     *
     * @param userPhoto the userPhoto to set
     */
    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    /**
     * Get if checkbox is selected.
     *
     * @return if checkbox is selected (true / false)
     */
    public boolean getCheckbox() {
        return selectedCheckbox;
    }

    /**
     * Sets Full path where user's picture is stored.
     *
     * @param selected the checkbox is selected or not
     */
    public void setCheckbox(boolean selected){
        this.selectedCheckbox = selected;
    }

    /**
     * Get score of frequent recipient.
     *
     * @return score
     */
    public double getScore() {
        return score;
    }

    /**
     * Set score of frequent recipient.
     *
     * @param score
     */
    public void setScore(double score){
        this.score = score;
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

    }

    @Override
    public void getPropertyInfo(int index, Hashtable properties, PropertyInfo info) {

    }
}
