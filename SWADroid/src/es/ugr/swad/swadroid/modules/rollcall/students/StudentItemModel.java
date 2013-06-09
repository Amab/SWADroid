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

package es.ugr.swad.swadroid.modules.rollcall.students;

import es.ugr.swad.swadroid.model.User;

/**
 * Student item model.
 *
 * @author Antonio Aguilera Malagon <aguilerin@gmail.com>
 */
public class StudentItemModel implements Comparable<StudentItemModel> {
    private final User student;
    private boolean selected;

    public StudentItemModel(User u) {
        this.student = u;
        this.selected = false;
    }

    public long getId() {
        return this.student.getId();
    }

    public String getUserID() {
        return this.student.getUserID();
    }

    public String toString() {
        return this.student.getUserSurname1() + " " + student.getUserSurname2() + ", " + this.student.getUserFirstname();
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getPhotoFileName() {
        return student.getPhotoFileName();
    }

    @Override
    public int compareTo(StudentItemModel item) {
        if (this.student.getUserSurname1().compareToIgnoreCase(item.student.getUserSurname1()) == 0) {
            if (this.student.getUserSurname2().compareToIgnoreCase(item.student.getUserSurname2()) == 0) {
                return this.student.getUserFirstname().compareTo(item.student.getUserFirstname());
            } else {
                return this.student.getUserSurname2().compareToIgnoreCase(item.student.getUserSurname2());
            }
        } else {
            return this.student.getUserSurname1().compareToIgnoreCase(item.student.getUserSurname1());
        }
    }
}
