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
package es.ugr.swad.swadroid.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import es.ugr.swad.swadroid.model.UserAttendance;

/**
 * DAO for UserAttendance table.
 *
 * @author Juan Miguel Boyero Corral <swadroid@gmail.com>
 */
@Dao
public interface UserAttendanceDao {
    @Query("SELECT * FROM UserAttendance")
    List<UserAttendance> findAll();

    @Query("SELECT * FROM UserAttendance WHERE eventCode = :eventCode")
    List<UserAttendance> findByEventCode(long eventCode);

    @Query("SELECT * FROM UserAttendance WHERE id = :userCode AND eventCode = :eventCode")
    UserAttendance findByUserCodeAndEventCode(long userCode, long eventCode);

    @Insert
    void insertAttendances(List<UserAttendance> attendances);

    @Update
    int updateAttendances(List<UserAttendance> attendances);

    @Delete
    int deleteAttendances(List<UserAttendance> attendances);

    @Query("DELETE FROM UserAttendance WHERE eventCode = :eventCode")
    int deleteAttendancesByEventCode(long eventCode);

    @Query("DELETE FROM UserAttendance")
    int deleteAll();
}
