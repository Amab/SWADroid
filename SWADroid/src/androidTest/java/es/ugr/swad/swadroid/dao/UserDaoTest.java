package es.ugr.swad.swadroid.dao;

import static org.junit.Assert.assertEquals;

import android.database.Cursor;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import es.ugr.swad.swadroid.model.User;

public class UserDaoTest extends DaoFixtureTest {

    @Before
    public void setUp() {
        courseDao.insertCourses(COURSES);
        eventDao.insertEvents(EVENTS);
        userDao.insertUsers(USERS);
        userAttendanceDao.insertAttendances(ATTENDANCES);
    }

    @Test
    public void findAll() {
        List<User> all = userDao.findAll();
        assertEquals(USERS, all);
    }

    @Test
    public void findAllByEventCodeCursor() {
        Cursor cursor = userDao.findAllByEventCodeCursor(EVENTS.get(0).getId());
        List<User> ordered = new ArrayList<>();
        List<User> expected = Collections.singletonList(
                new User(2, "wsKey", "userID1", "userNickname1", "userSurname1", "userSurname2", "userFirstname", "userPhoto", "20210101", 0)
        );

        int idColNum = cursor.getColumnIndexOrThrow("id");
        int wsKeyColNum = cursor.getColumnIndexOrThrow("wsKey");
        int userIDColNum = cursor.getColumnIndexOrThrow("userID");
        int userNicknameColNum = cursor.getColumnIndexOrThrow("userNickname");
        int userSurname1ColNum = cursor.getColumnIndex("userSurname1");
        int userSurname2ColNum = cursor.getColumnIndex("userSurname2");
        int userFirstNameColNum = cursor.getColumnIndex("userFirstname");
        int userPhotoColNum = cursor.getColumnIndex("userPhoto");
        int userBirthdayColNum = cursor.getColumnIndex("userBirthday");
        int userRoleColNum = cursor.getColumnIndex("userRole");

        while (cursor.moveToNext()) {
            long id = cursor.getLong(idColNum);
            String wsKey = cursor.getString(wsKeyColNum);
            String userID = cursor.getString(userIDColNum);
            String userNickname = cursor.getString(userNicknameColNum);
            String userSurname1 = cursor.getString(userSurname1ColNum);
            String userSurname2 = cursor.getString(userSurname2ColNum);
            String userFirstName = cursor.getString(userFirstNameColNum);
            String userPhoto = cursor.getString(userPhotoColNum);
            String userBirthday = cursor.getString(userBirthdayColNum);
            int userRole = cursor.getInt(userRoleColNum);
            ordered.add(new User(id, wsKey, userID, userNickname, userSurname1, userSurname2, userFirstName, userPhoto, userBirthday, userRole));
        }

        assertEquals(expected, ordered);
    }

    @Test
    public void findUserByUserNickname() {
        User user = userDao.findUserByUserNickname(USERS.get(0).getUserNickname());
        User expected = USERS.get(0);

        assertEquals(expected, user);
    }

    @Test
    public void insertUsers() {
        List<User> all = userDao.findAll();
        assertEquals(USERS, all);
    }

    @Test
    public void updateUsers() {
        User user = new User(2, "wsKey", "userID1 Modified", "userNickname1", "userSurname1", "userSurname2", "userFirstname", "userPhoto", "20210101", 0);
        userDao.updateUsers(Collections.singletonList(user));

        List<User> modified = userDao.findAll();
        List<User> expected = Arrays.asList(
                new User(1, "wsKey", "userID2", "userNickname2", "userSurname1", "userSurname2", "userFirstname", "userPhoto", "20210101", 0),
                user,
                new User(3, "wsKey", "userID3", "userNickname3", "userSurname1", "userSurname2", "userFirstname", "userPhoto", "20210101", 0)
        );

        assertEquals(expected, modified);
    }

    @Test
    public void deleteUsers() {
        List<User> all = userDao.findAll();
        List<User> expected = USERS.subList(1, 3);
        assertEquals(USERS, all);

        int numDeleted = userDao.deleteUsers(Collections.singletonList(USERS.get(0)));
        assertEquals(1, numDeleted);
        assertEquals(expected, userDao.findAll());
    }

    @Test
    public void deleteAll() {
        List<User> all = userDao.findAll();
        assertEquals(USERS, all);

        int numDeleted = userDao.deleteAll();
        assertEquals(3, numDeleted);
        assertEquals(Collections.EMPTY_LIST, userDao.findAll());
    }
}
