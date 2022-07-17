package es.ugr.swad.swadroid.dao;

import static junit.framework.TestCase.assertEquals;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import es.ugr.swad.swadroid.model.Model;
import es.ugr.swad.swadroid.model.TestAnswer;

public class TestAnswerDaoTest extends DaoFixtureTest {

    private final Comparator<TestAnswer> compareById = Comparator.comparingLong(Model::getId);

    @Before
    public void setUp() {
        courseDao.insertCourses(COURSES);
        testTagDao.insertTestTag(TEST_TAGS);
        testQuestionDao.insertTestQuestion(TEST_QUESTIONS);
        testTagsQuestionsDao.insertTestTagQuestion(TEST_TAGS_QUESTIONS);
        testAnswerDao.insertTestAnswer(TEST_ANSWERS);
    }

    @Test
    public void findAll() {
        List<TestAnswer> all = testAnswerDao.findAll();
        assertEquals(TEST_ANSWERS, all);
    }

    @Test
    public void insertTestAnswer() {
        List<TestAnswer> all = testAnswerDao.findAll();
        assertEquals(TEST_ANSWERS, all);
    }

    @Test
    public void updateTestAnswer() {
        TestAnswer testAnswer = new TestAnswer(2, 1, 2, false, "Answer2 Modified", "Feedback2");
        testAnswerDao.updateTestAnswer(Collections.singletonList(testAnswer));

        List<TestAnswer> modified = testAnswerDao.findAll();
        List<TestAnswer> expected = new ArrayList<>(TEST_ANSWERS);

        modified.sort(compareById);
        expected.sort(compareById);

        expected.set(1, testAnswer);

        assertEquals(expected, modified);
    }

    @Test
    public void deleteTestAnswer() {
        List<TestAnswer> all = testAnswerDao.findAll();
        List<TestAnswer> expected = new ArrayList<>(TEST_ANSWERS);
        List<TestAnswer> allSorted = new ArrayList<>(TEST_ANSWERS);

        expected.sort(compareById);
        allSorted.sort(compareById);

        TestAnswer deleted = expected.remove(1);

        assertEquals(allSorted, all);

        int numDeleted = testAnswerDao.deleteTestAnswer(Collections.singletonList(deleted));
        assertEquals(1, numDeleted);
        assertEquals(expected, testAnswerDao.findAll());
    }

    @Test
    public void deleteAll() {
        List<TestAnswer> all = testAnswerDao.findAll();
        List<TestAnswer> expected = new ArrayList<>(TEST_ANSWERS);
        expected.sort(compareById);
        assertEquals(expected, all);

        int numDeleted = testAnswerDao.deleteAll();
        assertEquals(18, numDeleted);
        assertEquals(Collections.EMPTY_LIST, testAnswerDao.findAll());
    }
}
