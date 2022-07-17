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
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import es.ugr.swad.swadroid.model.Group;
import es.ugr.swad.swadroid.model.GroupsCourse;
import es.ugr.swad.swadroid.model.GroupsGroupType;

/**
 * DAO for Group table.
 *
 * @author Juan Miguel Boyero Corral <swadroid@gmail.com>
 */
@Dao
public interface GroupDao {
    @Query("SELECT * FROM `Group`")
    List<Group> findAll();

    @Query("SELECT * FROM `Group` WHERE groupTypeCode = :groupTypeCode")
    List<Group> findAllByTypeCode(long groupTypeCode);

    @Transaction
    @Query("SELECT *" +
            " FROM `Group`" +
            " JOIN GroupType ON `Group`.groupTypeCode = GroupType.id" +
            " WHERE `Group`.id = :grpCod")
    GroupsGroupType findGroupsGroupTypeByGroupCode(long grpCod);

    @Transaction
    @Query("SELECT *" +
            " FROM `Group`" +
            " JOIN Course ON `Group`.crsCod = Course.id" +
            " WHERE crsCod = :crsCod")
    GroupsCourse findGroupsCourseByCourseCode(long crsCod);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertGroups(List<Group> groups);

    @Update
    int updateGroups(List<Group> groups);

    @Delete
    int deleteGroups(List<Group> groups);

    @Query("DELETE FROM `Group` WHERE id NOT IN (:ids)")
    int deleteGroupsByIdNotIn(List<Long> ids);

    @Query("DELETE FROM `Group`")
    int deleteAll();
}
