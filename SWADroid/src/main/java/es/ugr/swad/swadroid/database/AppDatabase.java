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
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SimpleSQLiteQuery;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SupportFactory;

import java.nio.charset.StandardCharsets;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.dao.CourseDao;
import es.ugr.swad.swadroid.dao.EventDao;
import es.ugr.swad.swadroid.dao.FrequentUserDao;
import es.ugr.swad.swadroid.dao.GroupDao;
import es.ugr.swad.swadroid.dao.GroupTypeDao;
import es.ugr.swad.swadroid.dao.RawDao;
import es.ugr.swad.swadroid.dao.SWADNotificationDao;
import es.ugr.swad.swadroid.dao.TestAnswerDao;
import es.ugr.swad.swadroid.dao.TestConfigDao;
import es.ugr.swad.swadroid.dao.TestQuestionDao;
import es.ugr.swad.swadroid.dao.TestTagDao;
import es.ugr.swad.swadroid.dao.TestTagsQuestionsDao;
import es.ugr.swad.swadroid.dao.UserAttendanceDao;
import es.ugr.swad.swadroid.dao.UserDao;
import es.ugr.swad.swadroid.model.Course;
import es.ugr.swad.swadroid.model.Event;
import es.ugr.swad.swadroid.model.FrequentUser;
import es.ugr.swad.swadroid.model.Group;
import es.ugr.swad.swadroid.model.GroupType;
import es.ugr.swad.swadroid.model.Location;
import es.ugr.swad.swadroid.model.LocationTimeStamp;
import es.ugr.swad.swadroid.model.SWADNotification;
import es.ugr.swad.swadroid.model.TestAnswer;
import es.ugr.swad.swadroid.model.TestConfig;
import es.ugr.swad.swadroid.model.TestQuestion;
import es.ugr.swad.swadroid.model.TestTag;
import es.ugr.swad.swadroid.model.TestTagsQuestions;
import es.ugr.swad.swadroid.model.User;
import es.ugr.swad.swadroid.model.UserAttendance;
import es.ugr.swad.swadroid.preferences.Preferences;
import es.ugr.swad.swadroid.utils.Utils;

/**
 * Application database.
 *
 * @author Juan Miguel Boyero Corral <swadroid@gmail.com>.
 */
@Database(entities = {
        Course.class,
        Event.class,
        FrequentUser.class,
        Group.class,
        GroupType.class,
        Location.class,
        LocationTimeStamp.class,
        SWADNotification.class,
        TestAnswer.class,
        TestQuestion.class,
        TestTag.class,
        TestTagsQuestions.class,
        TestConfig.class,
        User.class,
        UserAttendance.class
}, version = 22)
public abstract class AppDatabase extends RoomDatabase {
    /**
     * Class Module's tag name for Logcat
     */
    private static final String TAG = Constants.APP_TAG + " AppDatabase";

    private static final String DATABASE_NAME = "swadroid.db";

    private static final Object LOCK = new Object();

    private static AppDatabase instance;
    /**
     * Database passphrase
     */
    private static String dbKey;
    /**
     * Indicates if there are changes on db
     */
    private static boolean dbCleaned = false;

    /**
     * Open database
     *
     * @param context Application context
     * @return a database instance
     */
    public static AppDatabase getAppDatabase(Context context) {
        SupportFactory factory;

        try {
            if (instance == null) {
                synchronized (LOCK) {
                    SQLiteDatabase.loadLibs(context);
                    // Create database password if required
                    createDbKey();
                    factory = new SupportFactory(dbKey.getBytes(StandardCharsets.UTF_8));

                    instance =
                            Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DATABASE_NAME)
                                    .openHelperFactory(factory)
                                    .fallbackToDestructiveMigration()
                                    // TODO Refactor whole app for asynchronous database access and remove allowMainThreadQueries() for production
                                    .allowMainThreadQueries()
                                    .build();

                    // Migrate from old Database to new Room database if required
                    MigrationUtils.migrateToRoom(context, dbKey, instance);
                }
            }
        } catch (Exception e) {
            // If database migration fails, reset the database
            instance.clearAllTables();

            Log.e(TAG, "Database migration has failed. Database has been reset", e);
        }

        return instance;
    }

    /*
    Close database
     */
    public static void destroyInstance() {
        instance = null;
    }

    /**
     * Vacuum database
     */
    public static void vacuumDb() {
        instance.getRawDao().rawQuery(new SimpleSQLiteQuery("VACUUM"));

        Log.i(TAG, "Database vacuumed successfully");
    }

    /**
     * Indicates if the db was cleaned
     */
    public static boolean isDbCleaned() {
        return dbCleaned;
    }

    /**
     * Set the fact that the db was cleaned
     */
    public static void setDbCleaned(boolean state) {
        dbCleaned = state;
    }

    private static void createDbKey() {
        dbKey = Preferences.getDBKey();

        // If the passphrase is empty, generate a random passphrase
        if (dbKey.equals("")) {
            int dbKeyLength = 128;
            dbKey = Utils.randomString(dbKeyLength);
            Preferences.setDBKey(dbKey);

            Log.i(TAG, "DB Key created successfully");
        }
    }

    public abstract RawDao getRawDao();

    public abstract CourseDao getCourseDao();

    public abstract SWADNotificationDao getSwadNotificationDao();

    public abstract UserDao getUserDao();

    public abstract UserAttendanceDao getUserAttendanceDao();

    public abstract FrequentUserDao getFrequentUserDao();

    public abstract EventDao getEventDao();

    public abstract GroupDao getGroupDao();

    public abstract GroupTypeDao getGroupTypeDao();

    public abstract TestConfigDao getTestConfigDao();

    public abstract TestTagDao getTestTagDao();

    public abstract TestQuestionDao getTestQuestionDao();

    public abstract TestAnswerDao getTestAnswerDao();

    public abstract TestTagsQuestionsDao getTestTagsQuestionsDao();
}
