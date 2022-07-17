package es.ugr.swad.swadroid.dao;

import static junit.framework.TestCase.assertEquals;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import es.ugr.swad.swadroid.model.TestTagsQuestions;

public class TestTagsQuestionsDaoTest extends DaoFixtureTest {

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
        List<TestTagsQuestions> all = testTagsQuestionsDao.findAll();
        assertEquals(TEST_TAGS_QUESTIONS, all);
    }

    @Test
    public void insertTestTagsQuestions() {
        List<TestTagsQuestions> all = testTagsQuestionsDao.findAll();
        assertEquals(TEST_TAGS_QUESTIONS, all);
    }

    @Test
    public void updateTestTagsQuestions() {
        TestTagsQuestions testTagQuestions =  new TestTagsQuestions(2, 2, 2);
        testTagsQuestionsDao.updateTestTagQuestion(Collections.singletonList(testTagQuestions));

        List<TestTagsQuestions> modified = testTagsQuestionsDao.findAll();
        List<TestTagsQuestions> expected = new ArrayList<>(TEST_TAGS_QUESTIONS);

        expected.set(1, testTagQuestions);

        assertEquals(expected, modified);
    }

    @Test
    public void deleteTestTagsQuestions() {
        List<TestTagsQuestions> all = testTagsQuestionsDao.findAll();
        List<TestTagsQuestions> expected = new ArrayList<>(TEST_TAGS_QUESTIONS);
        List<TestTagsQuestions> allSorted = new ArrayList<>(TEST_TAGS_QUESTIONS);

        TestTagsQuestions deleted = expected.remove(1);

        assertEquals(allSorted, all);

        int numDeleted = testTagsQuestionsDao.deleteTestTagQuestion(Collections.singletonList(deleted));
        assertEquals(1, numDeleted);
        assertEquals(expected, testTagsQuestionsDao.findAll());
    }

    @Test
    public void deleteAll() {
        List<TestTagsQuestions> all = testTagsQuestionsDao.findAll();
        List<TestTagsQuestions> expected = new ArrayList<>(TEST_TAGS_QUESTIONS);
        assertEquals(expected, all);

        int numDeleted = testTagsQuestionsDao.deleteAll();
        assertEquals(9, numDeleted);
        assertEquals(Collections.EMPTY_LIST, testTagsQuestionsDao.findAll());
    }
}
