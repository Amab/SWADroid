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

import es.ugr.swad.swadroid.model.FrequentUser;

/**
 * DAO for FrequentUser table.
 *
 * @author Juan Miguel Boyero Corral <swadroid@gmail.com>
 */
@Dao
public interface FrequentUserDao {
    @Query("SELECT * FROM FrequentUser")
    List<FrequentUser> findAll();

    @Query("SELECT * FROM FrequentUser WHERE idUser = :idUser")
    List<FrequentUser> findByUserByIdUser(String idUser);

    @Query("SELECT * FROM FrequentUser WHERE idUser = :idUser ORDER BY score DESC")
    List<FrequentUser> findByUserByIdUserOrderByScoreDesc(String idUser);

    @Insert
    void insertUsers(List<FrequentUser> users);

    @Update
    int updateUsers(List<FrequentUser> users);

    @Delete
    int deleteUsers(List<FrequentUser> users);

    @Query("DELETE FROM FrequentUser WHERE idUser = :idUser")
    int deleteById(String idUser);

    @Query("DELETE FROM FrequentUser")
    int deleteAll();
}
