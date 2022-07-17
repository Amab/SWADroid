package es.ugr.swad.swadroid.dao;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import es.ugr.swad.swadroid.model.UserAttendance;

public class UserAttendanceDaoTest extends DaoFixtureTest {

    @Before
    public void setUp() {
        courseDao.insertCourses(COURSES);
        eventDao.insertEvents(EVENTS);
        userDao.insertUsers(USERS);
        userAttendanceDao.insertAttendances(ATTENDANCES);
    }

    @Test
    public void findAll() {
        List<UserAttendance> all = userAttendanceDao.findAll();
        assertEquals(ATTENDANCES, all);
    }

    @Test
    public void findByEventCode() {
        List<UserAttendance> attendances = userAttendanceDao.findByEventCode(EVENTS.get(1).getId());
        List<UserAttendance> expected = Collections.singletonList(
                new UserAttendance(1, 2, false)
        );

        assertEquals(expected, attendances);
    }

    @Test
    public void findByUserCodeAndEventCode() {
        UserAttendance attendance = userAttendanceDao.findByUserCodeAndEventCode(USERS.get(0).getId(), EVENTS.get(1).getId());
        UserAttendance expected = new UserAttendance(1, 2, false);
        assertEquals(expected, attendance);
    }

    @Test
    public void insertAttendances() {
        List<UserAttendance> all = userAttendanceDao.findAll();
        assertEquals(ATTENDANCES, all);
    }

    @Test
    public void updateAttendances() {
        UserAttendance attendance = new UserAttendance(2, 1, true);
        userAttendanceDao.updateAttendances(Collections.singletonList(attendance));

        List<UserAttendance> modified = userAttendanceDao.findAll();
        List<UserAttendance> expected = Arrays.asList(
                new UserAttendance(1, 2, false),
                attendance,
                new UserAttendance(3, 3, false)
        );

        assertEquals(expected, modified);
    }

    @Test
    public void deleteAttendances() {
        List<UserAttendance> all = userAttendanceDao.findAll();
        List<UserAttendance> expected = ATTENDANCES.subList(1, 3);
        assertEquals(ATTENDANCES, all);

        int numDeleted = userAttendanceDao.deleteAttendances(Collections.singletonList(ATTENDANCES.get(0)));
        assertEquals(1, numDeleted);
        assertEquals(expected, userAttendanceDao.findAll());
    }

    @Test
    public void deleteAttendancesByEventCode() {
        List<UserAttendance> all = userAttendanceDao.findAll();
        List<UserAttendance> expected = ATTENDANCES.subList(1, 3);
        assertEquals(ATTENDANCES, all);

        int numDeleted = userAttendanceDao.deleteAttendancesByEventCode(EVENTS.get(1).getId());
        assertEquals(1, numDeleted);
        assertEquals(expected, userAttendanceDao.findAll());
    }

    @Test
    public void deleteAll() {
        List<UserAttendance> all = userAttendanceDao.findAll();
        assertEquals(ATTENDANCES, all);

        int numDeleted = userAttendanceDao.deleteAll();
        assertEquals(3, numDeleted);
        assertEquals(Collections.EMPTY_LIST, userAttendanceDao.findAll());
    }
}
