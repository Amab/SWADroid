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

import android.database.Cursor;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import es.ugr.swad.swadroid.model.Event;

/**
 * DAO for Event table.
 *
 * @author Juan Miguel Boyero Corral <swadroid@gmail.com>
 */
@Dao
public interface EventDao {
    @Query("SELECT * FROM Event")
    List<Event> findAll();

    @Query("SELECT * FROM Event WHERE crsCod = :courseCode")
    List<Event> findAllByCourseCode(long courseCode);

    @Query("SELECT * FROM Event WHERE crsCod = :courseCode")
    Cursor findAllByCourseCodeCursor(long courseCode);

    @Query("SELECT * FROM Event WHERE id = :id")
    Event findById(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertEvents(List<Event> events);

    @Update
    int updateEvents(List<Event> events);

    @Delete
    int deleteEvents(List<Event> events);

    @Query("DELETE FROM Event")
    int deleteAll();
}
