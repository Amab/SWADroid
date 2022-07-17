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
package es.ugr.swad.swadroid.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.model.Course;
import es.ugr.swad.swadroid.model.Event;
import es.ugr.swad.swadroid.model.FrequentUser;
import es.ugr.swad.swadroid.model.Group;
import es.ugr.swad.swadroid.model.GroupType;
import es.ugr.swad.swadroid.model.SWADNotification;
import es.ugr.swad.swadroid.model.TestAnswer;
import es.ugr.swad.swadroid.model.TestConfig;
import es.ugr.swad.swadroid.model.TestQuestion;
import es.ugr.swad.swadroid.model.TestTag;
import es.ugr.swad.swadroid.model.TestTagsQuestions;
import es.ugr.swad.swadroid.model.User;
import es.ugr.swad.swadroid.model.UserAttendance;
import es.ugr.swad.swadroid.utils.CryptoUtils;
import es.ugr.swad.swadroid.utils.Utils;

public class MigrationUtils {
    /**
     * Class Module's tag name for Logcat
     */
    private static final String TAG = Constants.APP_TAG + " MigrationUtils";

    private static final String DATABASE_NAME_OLD = "swadroid_db_crypt";

    private static CryptoUtils cryptoUtils;

    private static SQLiteDatabase dbOld;

    private static AppDatabase db;

    public static void migrateToRoom(Context context, String dbKey, AppDatabase instance) throws IOException {
        File dbOldFile = context.getDatabasePath(DATABASE_NAME_OLD);

        if (dbOldFile.exists()) {
            Log.i(TAG, "Migrating old database " + DATABASE_NAME_OLD + " to Room");

            cryptoUtils = new CryptoUtils(dbKey);
            db = instance;
            dbOld = SQLiteDatabase.openDatabase(dbOldFile.getPath(), null, SQLiteDatabase.OPEN_READONLY);

            Log.d(TAG, "Opened old database " + DATABASE_NAME_OLD + " in READONLY mode");

            migrateCourseTableToRoom();
            migrateGroupTypeTableToRoom();
            migrateGroupTableToRoom();
            migrateEventTableToRoom();
            migrateUserTableToRoom();
            migrateUserAttendanceTableToRoom();
            migrateFrequentUserTableToRoom();
            migrateSWADNotificationTableToRoom();
            migrateTestConfigTableToRoom();
            migrateTesTagTableToRoom();
            migrateTestQuestionTableToRoom();
            migrateTesTagsQuestionsToRoom();
            migrateTestAnswerTableToRoom();

            dbOld.close();
            Log.d(TAG, "Closed old database " + DATABASE_NAME_OLD);

            if (context.deleteDatabase(DATABASE_NAME_OLD)) {
                Log.i(TAG, "Old database " + DATABASE_NAME_OLD + " deleted successfully");
            } else {
                throw new IOException("Error deleting old database " + DATABASE_NAME_OLD);
            }

            Log.i(TAG, DATABASE_NAME_OLD + " database migration to Room completed successfully");
        } else {
            Log.i(TAG, "Old database " + DATABASE_NAME_OLD + " not present. Database already migrated. Migration skipped");
        }
    }

    private static void migrateCourseTableToRoom() {
        String tableName = "courses";
        String query = "SELECT * FROM " + tableName;
        List<Course> list = new ArrayList<>();

        Log.d(TAG, "Migrating " + tableName + " table");

        Cursor c = dbOld.rawQuery(query, null);
        while (c.moveToNext()) {
            Course item = new Course(
                    c.getLong(c.getColumnIndex("id")),
                    c.getInt(c.getColumnIndex("userRole")),
                    c.getString(c.getColumnIndex("shortName")),
                    c.getString(c.getColumnIndex("fullName"))
            );

            list.add(item);
        }
        c.close();

        if (!list.isEmpty()) {
            db.getCourseDao().insertCourses(list);
        }

        Log.d(TAG, tableName + " table migrated successfully");
    }

    private static void migrateGroupTypeTableToRoom() {
        String tableName = "group_types";
        String query = "SELECT * FROM " + tableName;
        List<GroupType> list = new ArrayList<>();

        Log.d(TAG, "Migrating " + tableName + " table");

        Cursor c = dbOld.rawQuery(query, null);
        while (c.moveToNext()) {
            GroupType item = new GroupType(
                    c.getLong(c.getColumnIndex("id")),
                    c.getString(c.getColumnIndex("groupTypeName")),
                    c.getLong(c.getColumnIndex("courseCode")),
                    c.getInt(c.getColumnIndex("mandatory")),
                    c.getInt(c.getColumnIndex("multiple")),
                    c.getLong(c.getColumnIndex("openTime"))
            );

            list.add(item);
        }
        c.close();

        if (!list.isEmpty()) {
            db.getGroupTypeDao().insertGroupTypes(list);
        }

        Log.d(TAG, tableName + " table migrated successfully");
    }

    private static void migrateGroupTableToRoom() {
        String tableName = "groups";
        String query = "SELECT * FROM " + tableName + " g" +
                " INNER JOIN group_grouptypes ggt ON g.id = ggt.grpCod" +
                " INNER JOIN group_types gt ON gt.id = ggt.grpTypCod";
        List<Group> list = new ArrayList<>();

        Log.d(TAG, "Migrating " + tableName + " table");

        Cursor c = dbOld.rawQuery(query, null);
        while (c.moveToNext()) {
            Group item = new Group(
                    c.getLong(c.getColumnIndex("g.id")),
                    c.getString(c.getColumnIndex("groupName")),
                    c.getLong(c.getColumnIndex("ggt.grpTypCod")),
                    c.getLong(c.getColumnIndex("gt.courseCode")),
                    c.getInt(c.getColumnIndex("maxStudents")),
                    c.getInt(c.getColumnIndex("open")),
                    c.getInt(c.getColumnIndex("students")),
                    c.getInt(c.getColumnIndex("fileZones")),
                    c.getInt(c.getColumnIndex("member"))
            );

            list.add(item);
        }
        c.close();

        if (!list.isEmpty()) {
            db.getGroupDao().insertGroups(list);
        }

        Log.d(TAG, tableName + " table migrated successfully");
    }

    private static void migrateEventTableToRoom() {
        String tableName = "events_attendances";
        String query = "SELECT * FROM " + tableName + " e" +
                " INNER JOIN events_courses ec ON e.id = ec.eventCode";
        List<Event> list = new ArrayList<>();

        Log.d(TAG, "Migrating " + tableName + " table");

        Cursor c = dbOld.rawQuery(query, null);
        while (c.moveToNext()) {
            Event item = new Event(
                    c.getLong(c.getColumnIndex("g.id")),
                    c.getLong(c.getColumnIndex("ec.crsCod")),
                    Utils.parseIntBool(c.getInt(c.getColumnIndex("hidden"))),
                    cryptoUtils.decrypt(c.getString(c.getColumnIndex("userSurname1"))),
                    cryptoUtils.decrypt(c.getString(c.getColumnIndex("userSurname2"))),
                    cryptoUtils.decrypt(c.getString(c.getColumnIndex("userFirstName"))),
                    cryptoUtils.decrypt(c.getString(c.getColumnIndex("userPhoto"))),
                    c.getLong(c.getColumnIndex("startTime")),
                    c.getLong(c.getColumnIndex("endTime")),
                    Utils.parseIntBool(c.getInt(c.getColumnIndex("commentsTeachersVisible"))),
                    cryptoUtils.decrypt(c.getString(c.getColumnIndex("title"))),
                    cryptoUtils.decrypt(c.getString(c.getColumnIndex("text"))),
                    cryptoUtils.decrypt(c.getString(c.getColumnIndex("groups"))),
                    cryptoUtils.decrypt(c.getString(c.getColumnIndex("status")))
            );

            list.add(item);
        }
        c.close();

        if (!list.isEmpty()) {
            db.getEventDao().insertEvents(list);
        }

        Log.d(TAG, tableName + " table migrated successfully");
    }

    private static void migrateUserTableToRoom() {
        String tableName = "users";
        String query = "SELECT * FROM " + tableName;
        List<User> list = new ArrayList<>();

        Log.d(TAG, "Migrating " + tableName + " table");

        Cursor c = dbOld.rawQuery(query, null);
        while (c.moveToNext()) {
            User item = new User(
                    c.getLong(c.getColumnIndex("id")),
                    c.getString(c.getColumnIndex("wsKey")),
                    c.getString(c.getColumnIndex("userID")),
                    c.getString(c.getColumnIndex("userNickname")),
                    c.getString(c.getColumnIndex("userSurname1")),
                    c.getString(c.getColumnIndex("userSurname2")),
                    c.getString(c.getColumnIndex("userFirstname")),
                    c.getString(c.getColumnIndex("photoPath")),
                    c.getString(c.getColumnIndex("userBirthday")),
                    c.getInt(c.getColumnIndex("userRole"))
            );

            list.add(item);
        }
        c.close();

        if (!list.isEmpty()) {
            db.getUserDao().insertUsers(list);
        }

        Log.d(TAG, tableName + " table migrated successfully");
    }

    private static void migrateUserAttendanceTableToRoom() {
        String tableName = "users_attendances";
        String query = "SELECT * FROM " + tableName;
        List<UserAttendance> list = new ArrayList<>();

        Log.d(TAG, "Migrating " + tableName + " table");

        Cursor c = dbOld.rawQuery(query, null);
        while (c.moveToNext()) {
            UserAttendance item = new UserAttendance(
                    c.getLong(c.getColumnIndex("id")),
                    c.getInt(c.getColumnIndex("eventCode")),
                    Utils.parseIntBool(c.getInt(c.getColumnIndex("present")))
            );

            list.add(item);
        }
        c.close();

        if (!list.isEmpty()) {
            db.getUserAttendanceDao().insertAttendances(list);
        }

        Log.d(TAG, tableName + " table migrated successfully");
    }

    private static void migrateFrequentUserTableToRoom() {
        String tableName = "frequent_recipients";
        String query = "SELECT * FROM " + tableName;
        List<FrequentUser> list = new ArrayList<>();

        Log.d(TAG, "Migrating " + tableName + " table");

        Cursor c = dbOld.rawQuery(query, null);
        while (c.moveToNext()) {
            FrequentUser item = new FrequentUser(
                    c.getLong(c.getColumnIndex("id")),
                    c.getString(c.getColumnIndex("idUser")),
                    cryptoUtils.decrypt(c.getString(c.getColumnIndex("nicknameRecipient"))),
                    cryptoUtils.decrypt(c.getString(c.getColumnIndex("surname1Recipient"))),
                    cryptoUtils.decrypt(c.getString(c.getColumnIndex("surname2Recipient"))),
                    cryptoUtils.decrypt(c.getString(c.getColumnIndex("firstnameRecipient"))),
                    cryptoUtils.decrypt(c.getString(c.getColumnIndex("photoRecipient"))),
                    c.getInt(c.getColumnIndex("userCode")),
                    Utils.parseIntBool(c.getInt(c.getColumnIndex("selectedCheckbox"))),
                    c.getDouble(c.getColumnIndex("score"))
            );

            list.add(item);
        }
        c.close();

        if (!list.isEmpty()) {
            db.getFrequentUserDao().insertUsers(list);
        }

        Log.d(TAG, tableName + " table migrated successfully");
    }

    private static void migrateSWADNotificationTableToRoom() {
        String tableName = "notifications";
        String query = "SELECT * FROM " + tableName;
        List<SWADNotification> list = new ArrayList<>();

        Log.d(TAG, "Migrating " + tableName + " table");

        Cursor c = dbOld.rawQuery(query, null);
        while (c.moveToNext()) {
            String nickName = c.getString(c.getColumnIndex("userNickname"));
            String decryptedNickname = (nickName != null && !nickName.isEmpty())? cryptoUtils.decrypt(nickName) : "";

            SWADNotification item = new SWADNotification(
                    c.getLong(c.getColumnIndex("id")),
                    c.getInt(c.getColumnIndex("eventCode")),
                    cryptoUtils.decrypt(c.getString(c.getColumnIndex("eventType"))),
                    c.getLong(c.getColumnIndex("eventTime")),
                    decryptedNickname,
                    cryptoUtils.decrypt(c.getString(c.getColumnIndex("userSurname1"))),
                    cryptoUtils.decrypt(c.getString(c.getColumnIndex("userSurname2"))),
                    cryptoUtils.decrypt(c.getString(c.getColumnIndex("userFirstname"))),
                    cryptoUtils.decrypt(c.getString(c.getColumnIndex("userPhoto"))),
                    cryptoUtils.decrypt(c.getString(c.getColumnIndex("location"))),
                    cryptoUtils.decrypt(c.getString(c.getColumnIndex("summary"))),
                    c.getInt(c.getColumnIndex("status")),
                    cryptoUtils.decrypt(c.getString(c.getColumnIndex("content"))),
                    Utils.parseIntBool(c.getInt(c.getColumnIndex("seenLocal"))),
                    Utils.parseIntBool(c.getInt(c.getColumnIndex("seenRemote")))
            );

            list.add(item);
        }
        c.close();

        if (!list.isEmpty()) {
            db.getSwadNotificationDao().insertSWADNotifications(list);
        }

        Log.d(TAG, tableName + " table migrated successfully");
    }

    private static void migrateTestConfigTableToRoom() {
        String tableName = "tst_config";
        String query = "SELECT * FROM " + tableName;
        List<TestConfig> list = new ArrayList<>();

        Log.d(TAG, "Migrating " + tableName + " table");

        Cursor c = dbOld.rawQuery(query, null);
        while (c.moveToNext()) {
            TestConfig item = new TestConfig(
                    c.getLong(c.getColumnIndex("id")),
                    c.getInt(c.getColumnIndex("min")),
                    c.getInt(c.getColumnIndex("def")),
                    c.getInt(c.getColumnIndex("max")),
                    c.getString(c.getColumnIndex("feedback")),
                    c.getLong(c.getColumnIndex("editTime"))
            );

            list.add(item);
        }
        c.close();

        if (!list.isEmpty()) {
            db.getTestConfigDao().insertTestConfig(list);
        }

        Log.d(TAG, tableName + " table migrated successfully");
    }

    private static void migrateTesTagTableToRoom() {
        String tableName = "tst_tags";
        String query = "SELECT * FROM " + tableName;
        List<TestTag> list = new ArrayList<>();

        Log.d(TAG, "Migrating " + tableName + " table");

        Cursor c = dbOld.rawQuery(query, null);
        while (c.moveToNext()) {
            TestTag item = new TestTag(
                    c.getLong(c.getColumnIndex("id")),
                    c.getString(c.getColumnIndex("tagTxt"))
            );

            list.add(item);
        }
        c.close();

        if (!list.isEmpty()) {
            db.getTestTagDao().insertTestTag(list);
        }

        Log.d(TAG, tableName + " table migrated successfully");
    }

    private static void migrateTestQuestionTableToRoom() {
        String tableName = "tst_questions";
        String query = "SELECT * FROM " + tableName + " tq" +
                " INNER JOIN tst_questions_course tqc ON tq.id = tqc.qstCod";
        List<TestQuestion> list = new ArrayList<>();

        Log.d(TAG, "Migrating " + tableName + " table");

        Cursor c = dbOld.rawQuery(query, null);
        while (c.moveToNext()) {
            TestQuestion item = new TestQuestion(
                    c.getLong(c.getColumnIndex("id")),
                    c.getLong(c.getColumnIndex("tqc.crsCod")),
                    c.getString(c.getColumnIndex("stem")),
                    c.getString(c.getColumnIndex("ansType")),
                    Utils.parseStringBool(c.getString(c.getColumnIndex("shuffle"))),
                    c.getString(c.getColumnIndex("feedback"))
            );

            list.add(item);
        }
        c.close();

        if (!list.isEmpty()) {
            db.getTestQuestionDao().insertTestQuestion(list);
        }

        Log.d(TAG, tableName + " table migrated successfully");
    }

    private static void migrateTesTagsQuestionsToRoom() {
        String tableName = "tst_question_tags";
        String query = "SELECT * FROM " + tableName;
        List<TestTagsQuestions> list = new ArrayList<>();

        Log.d(TAG, "Migrating " + tableName + " table");

        Cursor c = dbOld.rawQuery(query, null);
        while (c.moveToNext()) {
            TestTagsQuestions item = new TestTagsQuestions(
                    c.getLong(c.getColumnIndex("qstCod")),
                    c.getLong(c.getColumnIndex("tagCod")),
                    c.getInt(c.getColumnIndex("tagInd"))
            );

            list.add(item);
        }
        c.close();

        if (!list.isEmpty()) {
            db.getTestTagsQuestionsDao().insertTestTagQuestion(list);
        }

        Log.d(TAG, tableName + " table migrated successfully");
    }

    private static void migrateTestAnswerTableToRoom() {
        String tableName = "tst_answers";
        String query = "SELECT * FROM " + tableName + " tqt" +
                " INNER JOIN tst_question_answers tqa ON tqt.id = tqa.ansCod";
        List<TestAnswer> list = new ArrayList<>();

        Log.d(TAG, "Migrating " + tableName + " table");

        Cursor c = dbOld.rawQuery(query, null);
        while (c.moveToNext()) {
            TestAnswer item = new TestAnswer(
                    c.getLong(c.getColumnIndex("id")),
                    c.getLong(c.getColumnIndex("tqa.qstCod")),
                    c.getInt(c.getColumnIndex("ansInd")),
                    Utils.parseIntBool(c.getInt(c.getColumnIndex("correct"))),
                    c.getString(c.getColumnIndex("answer")),
                    c.getString(c.getColumnIndex("answerFeedback"))
            );

            list.add(item);
        }
        c.close();

        if (!list.isEmpty()) {
            db.getTestAnswerDao().insertTestAnswer(list);
        }

        Log.d(TAG, tableName + " table migrated successfully");
    }
}
