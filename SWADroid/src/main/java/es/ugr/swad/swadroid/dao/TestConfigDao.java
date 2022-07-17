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

import es.ugr.swad.swadroid.model.TestConfig;

/**
 * DAO for Test config table.
 *
 * @author Juan Miguel Boyero Corral <swadroid@gmail.com>
 */
@Dao
public interface TestConfigDao {
    @Query("SELECT * FROM TestConfig")
    List<TestConfig> findAll();

    @Query("SELECT * FROM TestConfig WHERE id = :courseCode")
    TestConfig findByCourseCode(long courseCode);

    @Query("SELECT * FROM TestConfig WHERE id = :courseCode")
    Cursor findCursorByCourseCode(long courseCode);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTestConfig(List<TestConfig> testsConfig);

    @Update
    int updateTestConfig(List<TestConfig> testsConfig);

    @Delete
    int deleteTestConfig(List<TestConfig> testsConfig);

    @Query("DELETE FROM TestConfig")
    int deleteAll();
}
