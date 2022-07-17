package es.ugr.swad.swadroid.dao;

import static junit.framework.TestCase.assertEquals;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import es.ugr.swad.swadroid.model.Model;
import es.ugr.swad.swadroid.model.TestTag;

public class TestTagDaoTest extends DaoFixtureTest {

    private final Comparator<TestTag> compareById = Comparator.comparingLong(Model::getId);

    @Before
    public void setUp() {
        testTagDao.insertTestTag(TEST_TAGS);
    }

    @Test
    public void findAll() {
        List<TestTag> all = testTagDao.findAll();
        List<TestTag> expected = TEST_TAGS;
        expected.sort(compareById);
        assertEquals(expected, all);
    }

    @Test
    public void insertTestTag() {
        List<TestTag> all = testTagDao.findAll();
        List<TestTag> expected = TEST_TAGS;
        expected.sort(compareById);
        assertEquals(expected, all);
    }

    @Test
    public void updateTestTag() {
        TestTag TestTag = new TestTag(1, "Tag 1 Modified");
        testTagDao.updateTestTag(Collections.singletonList(TestTag));

        List<TestTag> modified = testTagDao.findAll();
        List<TestTag> expected = Arrays.asList(
                TestTag,
                TEST_TAGS.get(0),
                TEST_TAGS.get(2)
        );

        assertEquals(expected, modified);
    }

    @Test
    public void deleteTestTag() {
        List<TestTag> all = testTagDao.findAll();
        List<TestTag> expected = TEST_TAGS.subList(1, 3);
        List<TestTag> allSorted = TEST_TAGS;

        expected.sort(compareById);
        allSorted.sort(compareById);
        assertEquals(allSorted, all);

        int numDeleted = testTagDao.deleteTestTag(Collections.singletonList(TEST_TAGS.get(0)));
        assertEquals(1, numDeleted);
        assertEquals(expected, testTagDao.findAll());
    }

    @Test
    public void deleteAll() {
        List<TestTag> all = testTagDao.findAll();
        List<TestTag> expected = TEST_TAGS;
        expected.sort(compareById);
        assertEquals(expected, all);

        int numDeleted = testTagDao.deleteAll();
        assertEquals(3, numDeleted);
        assertEquals(Collections.EMPTY_LIST, testTagDao.findAll());
    }
}
