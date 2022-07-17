package es.ugr.swad.swadroid.dao;

import static junit.framework.TestCase.assertEquals;

import android.database.Cursor;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import es.ugr.swad.swadroid.model.Course;

public class CourseDaoTest extends DaoFixtureTest {

    @Before
    public void setUp() {
        courseDao.insertCourses(COURSES);
    }

    @Test
    public void findAll() {
        List<Course> all = courseDao.findAll();
        assertEquals(COURSES, all);
    }

    @Test
    public void findAllOrderedByShortNameAsc() {
        List<Course> ordered = courseDao.findAllOrderedByShortNameAsc();
        List<Course> expected = Arrays.asList(
                new Course(2, 1, "c1", "Course 1"),
                new Course(1, 1, "c2", "Course 2"),
                new Course(3, 1, "c3", "Course 3")
        );

        assertEquals(expected, ordered);
    }

    @Test
    public void findAllOrderedByShortNameAscCursor() {
        Cursor cursor = courseDao.findAllOrderedByShortNameAscCursor();
        List<Course> ordered = new ArrayList<>();
        List<Course> expected = Arrays.asList(
                new Course(1, 1, "c1", "Course 1"),
                new Course(2, 1, "c2", "Course 2"),
                new Course(3, 1, "c3", "Course 3")
        );

        int idColNum = cursor.getColumnIndexOrThrow("id");
        int userRoleColNum = cursor.getColumnIndexOrThrow("userRole");
        int shortNameColNum = cursor.getColumnIndexOrThrow("shortName");
        int fullNameColNum = cursor.getColumnIndex("fullName");

        while (cursor.moveToNext()) {
            long id = cursor.getLong(idColNum);
            int userRole = cursor.getInt(userRoleColNum);
            String shortName = cursor.getString(shortNameColNum);
            String fullName = cursor.getString(fullNameColNum);
            ordered.add(new Course(id, userRole, shortName, fullName));
        }

        assertEquals(expected, ordered);
    }

    @Test
    public void findCourseById() {
        Course byId = courseDao.findCourseById(1);
        assertEquals(COURSES.get(0), byId);
    }

    @Test
    public void insertCourses() {
        List<Course> all = courseDao.findAll();
        assertEquals(COURSES, all);
    }

    @Test
    public void updateCourses() {
        Course course = new Course(2, 1, "c1", "Course 1 Modified");
        courseDao.updateCourses(Collections.singletonList(course));

        List<Course> modified = courseDao.findAll();
        List<Course> expected = Arrays.asList(
                new Course(1, 1, "c2", "Course 2"),
                course,
                new Course(3, 1, "c3", "Course 3")
        );

        assertEquals(expected, modified);
    }

    @Test
    public void deleteCourses() {
        List<Course> all = courseDao.findAll();
        List<Course> expected = COURSES.subList(1, 3);
        assertEquals(COURSES, all);

        int numDeleted = courseDao.deleteCourses(Collections.singletonList(COURSES.get(0)));
        assertEquals(1, numDeleted);
        assertEquals(expected, courseDao.findAll());
    }

    @Test
    public void deleteAll() {
        List<Course> all = courseDao.findAll();
        assertEquals(COURSES, all);

        int numDeleted = courseDao.deleteAll();
        assertEquals(3, numDeleted);
        assertEquals(Collections.EMPTY_LIST, courseDao.findAll());
    }
}
