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
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import es.ugr.swad.swadroid.model.TestTag;

/**
 * DAO for Test tags table.
 *
 * @author Juan Miguel Boyero Corral <swadroid@gmail.com>
 */
@Dao
public interface TestTagDao {
    @Query("SELECT * FROM TestTag")
    List<TestTag> findAll();

    @Query("SELECT TestTag.id, TestTag.tagTxt" +
            " FROM TestTag" +
            " JOIN TestTagsQuestions ON TestTag.id=TestTagsQuestions.tagCod" +
            " JOIN TestQuestion ON TestTagsQuestions.qstCod=TestQuestion.id AND TestQuestion.crsCod = :selectedCourseCode")
    List<TestTag> findAllTagsByCourseOrderByTagInd(long selectedCourseCode);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTestTag(List<TestTag> testsTag);

    @Update
    int updateTestTag(List<TestTag> testsTag);

    @Delete
    int deleteTestTag(List<TestTag> testsTag);

    @Query("DELETE FROM TestTag")
    int deleteAll();
}
