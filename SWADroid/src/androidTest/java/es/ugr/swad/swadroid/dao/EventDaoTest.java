package es.ugr.swad.swadroid.dao;

import static org.junit.Assert.assertEquals;

import android.database.Cursor;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import es.ugr.swad.swadroid.model.Event;

public class EventDaoTest extends DaoFixtureTest {

    @Before
    public void setUp() {
        courseDao.insertCourses(COURSES);
        eventDao.insertEvents(EVENTS);
    }

    @Test
    public void findAll() {
        List<Event> all = eventDao.findAll();
        assertEquals(EVENTS, all);
    }

    @Test
    public void findAllByCourseCode() {
        List<Event> events = eventDao.findAllByCourseCode(COURSES.get(0).getId());
        List<Event> expected = Collections.singletonList(
                new Event(2, 1, false, "", "", "", "", 0L, 0L, false, "Event 1", "Description 1", "", "")
        );

        assertEquals(expected, events);
    }

    @Test
    public void findAllByCourseCodeCursor() {
        Cursor cursor = eventDao.findAllByCourseCodeCursor(COURSES.get(0).getId());
        List<Event> ordered = new ArrayList<>();
        List<Event> expected = Collections.singletonList(
                new Event(2, 1, false, "", "", "", "", 0L, 0L, false, "Event 1", "Description 1", "", "")
        );

        int idColNum = cursor.getColumnIndexOrThrow("id");
        int crsCodColNum = cursor.getColumnIndexOrThrow("crsCod");
        int hiddenColNum = cursor.getColumnIndexOrThrow("hidden");
        int userSurname1ColNum = cursor.getColumnIndexOrThrow("userSurname1");
        int userSurname2ColNum = cursor.getColumnIndex("userSurname2");
        int userFirstNameColNum = cursor.getColumnIndex("userFirstName");
        int userPhotoColNum = cursor.getColumnIndex("userPhoto");
        int startTimeColNum = cursor.getColumnIndex("startTime");
        int endTimeColNum = cursor.getColumnIndex("endTime");
        int commentsTeachersVisibleColNum = cursor.getColumnIndex("commentsTeachersVisible");
        int titleNameColNum = cursor.getColumnIndex("title");
        int textNameColNum = cursor.getColumnIndex("text");
        int groupsNameColNum = cursor.getColumnIndex("groups");
        int statusNameColNum = cursor.getColumnIndex("status");

        while (cursor.moveToNext()) {
            long id = cursor.getLong(idColNum);
            long crsCod = cursor.getLong(crsCodColNum);
            boolean hidden = cursor.getInt(hiddenColNum) > 0;
            String userSurname1 = cursor.getString(userSurname1ColNum);
            String userSurname2 = cursor.getString(userSurname2ColNum);
            String userFirstName = cursor.getString(userFirstNameColNum);
            String userPhoto = cursor.getString(userPhotoColNum);
            long startTime = cursor.getLong(startTimeColNum);
            long endTime = cursor.getLong(endTimeColNum);
            boolean commentsTeachersVisible = cursor.getInt(commentsTeachersVisibleColNum) > 0;
            String title = cursor.getString(titleNameColNum);
            String text = cursor.getString(textNameColNum);
            String groups = cursor.getString(groupsNameColNum);
            String status = cursor.getString(statusNameColNum);
            ordered.add(new Event(id, crsCod, hidden, userSurname1, userSurname2, userFirstName, userPhoto, startTime, endTime, commentsTeachersVisible, title, text, groups, status));
        }

        assertEquals(expected, ordered);
    }

    @Test
    public void findById() {
        Event byId = eventDao.findById(1);
        assertEquals(EVENTS.get(0), byId);
    }

    @Test
    public void insertEvents() {
        List<Event> all = eventDao.findAll();
        assertEquals(EVENTS, all);
    }

    @Test
    public void updateEvents() {
        Event event = new Event(2, 1, false, "", "", "", "", 0L, 0L, false, "Event 1 Modified", "Description 1 Modified", "", "");
        eventDao.updateEvents(Collections.singletonList(event));

        List<Event> modified = eventDao.findAll();
        List<Event> expected = Arrays.asList(
                new Event(1, 2, false, "", "", "", "", 0L, 0L, false, "Event 2", "Description 2", "", ""),
                event,
                new Event(3, 3, false, "", "", "", "", 0L, 0L, false, "Event 3", "Description 3", "", "")
        );

        assertEquals(expected, modified);
    }

    @Test
    public void deleteEvents() {
        List<Event> all = eventDao.findAll();
        List<Event> expected = EVENTS.subList(1, 3);
        assertEquals(EVENTS, all);

        int numDeleted = eventDao.deleteEvents(Collections.singletonList(EVENTS.get(0)));
        assertEquals(1, numDeleted);
        assertEquals(expected, eventDao.findAll());
    }

    @Test
    public void deleteAll() {
        List<Event> all = eventDao.findAll();
        assertEquals(EVENTS, all);

        int numDeleted = eventDao.deleteAll();
        assertEquals(3, numDeleted);
        assertEquals(Collections.EMPTY_LIST, eventDao.findAll());
    }
}
