package es.ugr.swad.swadroid.dao;

import static junit.framework.TestCase.assertEquals;

import android.database.Cursor;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import es.ugr.swad.swadroid.model.TestConfig;

public class TestConfigDaoTest extends DaoFixtureTest {

    @Before
    public void setUp() {
        testConfigDao.insertTestConfig(TEST_CONFIGS);
    }

    @Test
    public void findAll() {
        List<TestConfig> all = testConfigDao.findAll();
        assertEquals(TEST_CONFIGS, all);
    }

    @Test
    public void findByCourseCode() {
        TestConfig byId = testConfigDao.findByCourseCode(1);
        assertEquals(TEST_CONFIGS.get(0), byId);
    }

    @Test
    public void findCursorByCourseCode() {
        Cursor cursor = testConfigDao.findCursorByCourseCode(1);
        List<TestConfig> ordered = new ArrayList<>();
        List<TestConfig> expected = Collections.singletonList(
                TEST_CONFIGS.get(0)
        );

        int idColNum = cursor.getColumnIndexOrThrow("id");
        int minColNum = cursor.getColumnIndexOrThrow("min");
        int defColNum = cursor.getColumnIndexOrThrow("def");
        int maxColNum = cursor.getColumnIndex("max");
        int feedbackColNum = cursor.getColumnIndex("feedback");
        int editTimeColNum = cursor.getColumnIndex("editTime");

        while (cursor.moveToNext()) {
            long id = cursor.getLong(idColNum);
            int min = cursor.getInt(minColNum);
            int def = cursor.getInt(defColNum);
            int max = cursor.getInt(maxColNum);
            String feedback = cursor.getString(feedbackColNum);
            long editTime = cursor.getLong(editTimeColNum);
            ordered.add(new TestConfig(id, min, def, max, feedback, editTime));
        }

        assertEquals(expected, ordered);
    }

    @Test
    public void insertTestConfig() {
        List<TestConfig> all = testConfigDao.findAll();
        assertEquals(TEST_CONFIGS, all);
    }

    @Test
    public void updateTestConfig() {
        TestConfig testConfig = new TestConfig(2, 1, 3, 5, "F2 Modified", 0L);
        testConfigDao.updateTestConfig(Collections.singletonList(testConfig));

        List<TestConfig> modified = testConfigDao.findAll();
        List<TestConfig> expected = Arrays.asList(
                TEST_CONFIGS.get(0),
                testConfig,
                TEST_CONFIGS.get(2)
        );

        assertEquals(expected, modified);
    }

    @Test
    public void deleteTestConfig() {
        List<TestConfig> all = testConfigDao.findAll();
        List<TestConfig> expected = TEST_CONFIGS.subList(1, 3);
        assertEquals(TEST_CONFIGS, all);

        int numDeleted = testConfigDao.deleteTestConfig(Collections.singletonList(TEST_CONFIGS.get(0)));
        assertEquals(1, numDeleted);
        assertEquals(expected, testConfigDao.findAll());
    }

    @Test
    public void deleteAll() {
        List<TestConfig> all = testConfigDao.findAll();
        assertEquals(TEST_CONFIGS, all);

        int numDeleted = testConfigDao.deleteAll();
        assertEquals(3, numDeleted);
        assertEquals(Collections.EMPTY_LIST, testConfigDao.findAll());
    }
}
