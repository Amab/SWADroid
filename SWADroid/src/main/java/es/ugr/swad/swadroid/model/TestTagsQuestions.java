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

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import lombok.Data;

/**
 * Relation between Test tags and questions.
 *
 * @author Juan Miguel Boyero Corral <swadroid@gmail.com>.
 */
@Data
@Entity(indices = {@Index("tagCod"), @Index("qstCod")},
        primaryKeys = {"tagCod", "qstCod"},
        foreignKeys = {
                @ForeignKey(entity = TestQuestion.class,
                        parentColumns = "id",
                        childColumns = "qstCod",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = TestTag.class,
                        parentColumns = "id",
                        childColumns = "tagCod",
                        onDelete = ForeignKey.CASCADE)
        })
public class TestTagsQuestions {
    private long qstCod;
    private long tagCod;
    private int tagIndex;

    public TestTagsQuestions(long qstCod, long tagCod, int tagIndex) {
        this.qstCod = qstCod;
        this.tagCod = tagCod;
        this.tagIndex = tagIndex;
    }
}
