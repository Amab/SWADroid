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

import es.ugr.swad.swadroid.model.SWADNotification;

/**
 * DAO for SWADNotification table.
 *
 * @author Juan Miguel Boyero Corral <swadroid@gmail.com>
 */
@Dao
public interface SWADNotificationDao {
    @Query("SELECT * FROM SWADNotification")
    List<SWADNotification> findAll();

    @Query("SELECT * FROM SWADNotification WHERE seenLocal=:seenLocal ORDER BY eventTime DESC")
    List<SWADNotification> findAllOrderByEventTimeDesc(boolean seenLocal);

    @Query("SELECT * FROM SWADNotification WHERE seenLocal AND NOT seenRemote")
    List<SWADNotification> findAllPendingToRead();

    @Query("SELECT * FROM SWADNotification WHERE id=:notifCode")
    SWADNotification findById(long notifCode);

    @Query("SELECT COUNT(*) from SWADNotification")
    long countAll();

    @Query("SELECT MAX(eventTime) from SWADNotification")
    long findMaxEventTime();

    @Insert
    void insertSWADNotifications(List<SWADNotification> notifications);

    @Update
    int updateSWADNotifications(List<SWADNotification> notifications);

    @Query("UPDATE SWADNotification SET seenLocal=:seenLocal")
    int updateAllBySeenLocal(boolean seenLocal);

    @Query("UPDATE SWADNotification SET seenRemote=:seenRemote")
    int updateAllBySeenRemote(boolean seenRemote);

    @Delete
    int deleteSWADNotifications(List<SWADNotification> notifications);

    @Query("DELETE FROM SWADNotification WHERE eventTime <= strftime('%s','now') - :age")
    int deleteAllByAge(long age);

    @Query("DELETE FROM SWADNotification")
    int deleteAll();
}
