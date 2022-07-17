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

import es.ugr.swad.swadroid.model.TestAnswersQuestion;
import es.ugr.swad.swadroid.model.TestQuestion;

/**
 * DAO for Test questions table.
 *
 * @author Juan Miguel Boyero Corral <swadroid@gmail.com>
 */
@Dao
public interface TestQuestionDao {
    @Query("SELECT * FROM TestQuestion")
    List<TestQuestion> findAll();

    @Transaction
    @Query("SELECT DISTINCT TestQuestion.id, TestQuestion.crsCod, TestQuestion.stem, TestQuestion.answerType, TestQuestion.shuffle, TestQuestion.feedback" +
            " FROM TestQuestion" +
            " JOIN TestAnswer ON TestQuestion.id=TestAnswer.qstCod" +
            " WHERE TestQuestion.crsCod=:selectedCourseCode" +
            " ORDER BY RANDOM(), TestAnswer.ansInd" +
            " LIMIT :maxQuestions")
    List<TestAnswersQuestion> findAllCourseQuestionsOrderByRandomAndAnswerIndex(long selectedCourseCode, int maxQuestions);

    @Transaction
    @Query("SELECT DISTINCT TestQuestion.id, TestQuestion.crsCod, TestQuestion.stem, TestQuestion.answerType, TestQuestion.shuffle, TestQuestion.feedback" +
            " FROM TestQuestion" +
            " JOIN TestTagsQuestions ON TestQuestion.id=TestTagsQuestions.qstCod" +
            " JOIN TestTag ON TestTag.id=TestTagsQuestions.tagCod" +
            " JOIN TestAnswer ON TestQuestion.id=TestAnswer.qstCod" +
            " WHERE TestQuestion.crsCod=:selectedCourseCode AND TestTag.tagTxt IN (:tagsList)" +
            " ORDER BY RANDOM(), TestAnswer.ansInd" +
            " LIMIT :maxQuestions")
    List<TestAnswersQuestion> findAllCourseQuestionsByTagOrderByRandomAndAnswerIndex(long selectedCourseCode,
                                                                                     List<String> tagsList,
                                                                                     int maxQuestions);

    @Transaction
    @Query("SELECT DISTINCT TestQuestion.id, TestQuestion.crsCod, TestQuestion.stem, TestQuestion.answerType, TestQuestion.shuffle, TestQuestion.feedback" +
            " FROM TestQuestion" +
            " JOIN TestAnswer ON TestQuestion.id=TestAnswer.qstCod" +
            " WHERE TestQuestion.crsCod=:selectedCourseCode AND TestQuestion.answerType IN (:answerTypesList)" +
            " ORDER BY RANDOM(), TestAnswer.ansInd" +
            " LIMIT :maxQuestions")
    List<TestAnswersQuestion> findAllCourseQuestionsByAnswerTypeOrderByRandomAndAnswerIndex(long selectedCourseCode,
                                                                                            List<String> answerTypesList,
                                                                                            int maxQuestions);

    @Transaction
    @Query("SELECT DISTINCT TestQuestion.id, TestQuestion.crsCod, TestQuestion.stem, TestQuestion.answerType, TestQuestion.shuffle, TestQuestion.feedback" +
            " FROM TestQuestion" +
            " JOIN TestTagsQuestions ON TestQuestion.id=TestTagsQuestions.qstCod" +
            " JOIN TestTag ON TestTag.id=TestTagsQuestions.tagCod" +
            " JOIN TestAnswer ON TestQuestion.id=TestAnswer.qstCod" +
            " WHERE TestQuestion.crsCod=:selectedCourseCode AND TestTag.tagTxt IN (:tagsList) AND TestQuestion.answerType IN (:answerTypesList)" +
            " ORDER BY RANDOM(), TestAnswer.ansInd" +
            " LIMIT :maxQuestions")
    List<TestAnswersQuestion> findAllCourseQuestionsByTagAndAnswerTypeOrderByRandomAndAnswerIndex(long selectedCourseCode,
                                                                                                  List<String> tagsList,
                                                                                                  List<String> answerTypesList,
                                                                                                  int maxQuestions);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTestQuestion(List<TestQuestion> testsQuestion);

    @Update
    int updateTestQuestion(List<TestQuestion> testsQuestion);

    @Delete
    int deleteTestQuestion(List<TestQuestion> testsQuestion);

    @Query("DELETE FROM TestQuestion")
    int deleteAll();
}
