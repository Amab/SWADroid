package es.ugr.swad.swadroid.dao;

import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertEquals;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import es.ugr.swad.swadroid.model.Model;
import es.ugr.swad.swadroid.model.TestAnswer;
import es.ugr.swad.swadroid.model.TestAnswersQuestion;
import es.ugr.swad.swadroid.model.TestQuestion;
import es.ugr.swad.swadroid.model.TestTag;

public class TestQuestionDaoTest extends DaoFixtureTest {

    private final Comparator<TestQuestion> testQuestionComparator = Comparator.comparingLong(Model::getId);
    private final Comparator<TestAnswer> testAnswerComparator = Comparator.comparingLong(Model::getId);

    @Before
    public void setUp() throws Exception {
        courseDao.insertCourses(COURSES);
        testTagDao.insertTestTag(TEST_TAGS);
        testQuestionDao.insertTestQuestion(TEST_QUESTIONS);
        testTagsQuestionsDao.insertTestTagQuestion(TEST_TAGS_QUESTIONS);
        testAnswerDao.insertTestAnswer(TEST_ANSWERS);
    }

    @Test
    public void findAll() {
        List<TestQuestion> all = testQuestionDao.findAll();
        List<TestQuestion> expected = TEST_QUESTIONS;
        expected.sort(testQuestionComparator);
        assertEquals(expected, all);
    }

    @Test
    public void findAllCourseQuestionsOrderByRandomAndAnswerIndex() {
        int crsCod = 1;
        List<Long> questions = TEST_QUESTIONS.stream().filter(t -> t.getCrsCod() == crsCod).map(TestQuestion::getId).collect(Collectors.toList());
        TestAnswersQuestion answersQuestion = testQuestionDao.findAllCourseQuestionsOrderByRandomAndAnswerIndex(crsCod, 1).get(0);

        answersQuestion.getTestAnswers().sort(testAnswerComparator);

        assertTrue(questions.contains(answersQuestion.getTestQuestion().getId()));
        assertEquals(TEST_ANSWERS.stream().filter(a -> a.getQstCod() == answersQuestion.getTestQuestion().getId()).collect(Collectors.toList()),
                answersQuestion.getTestAnswers());
    }

    @Test
    public void findAllCourseQuestionsByTagOrderByRandomAndAnswerIndex() {
        int crsCod = 1;
        List<String> tags = TEST_TAGS.stream().map(TestTag::getTagTxt).collect(Collectors.toList());
        List<Long> questions = TEST_QUESTIONS.stream().filter(t -> t.getCrsCod() == crsCod).map(TestQuestion::getId).collect(Collectors.toList());
        TestAnswersQuestion answersQuestion = testQuestionDao.findAllCourseQuestionsByTagOrderByRandomAndAnswerIndex(crsCod, tags, 1).get(0);

        answersQuestion.getTestAnswers().sort(testAnswerComparator);

        assertTrue(questions.contains(answersQuestion.getTestQuestion().getId()));
        assertEquals(TEST_ANSWERS.stream().filter(a -> a.getQstCod() == answersQuestion.getTestQuestion().getId()).collect(Collectors.toList()),
                answersQuestion.getTestAnswers());
    }

    @Test
    public void findAllCourseQuestionsByAnswerTypeOrderByRandomAndAnswerIndex() {
        int crsCod = 1;
        List<String> answerTypes = Collections.singletonList(TestAnswer.TYPE_TRUE_FALSE);
        List<Long> questions = TEST_QUESTIONS.stream().filter(t -> t.getCrsCod() == crsCod).map(TestQuestion::getId).collect(Collectors.toList());
        TestAnswersQuestion answersQuestion = testQuestionDao.findAllCourseQuestionsByAnswerTypeOrderByRandomAndAnswerIndex(crsCod, answerTypes, 1).get(0);

        answersQuestion.getTestAnswers().sort(testAnswerComparator);

        assertTrue(questions.contains(answersQuestion.getTestQuestion().getId()));
        assertEquals(TEST_ANSWERS.stream().filter(a -> a.getQstCod() == answersQuestion.getTestQuestion().getId()).collect(Collectors.toList()),
                answersQuestion.getTestAnswers());
    }

    @Test
    public void findAllCourseQuestionsByTagAndAnswerTypeOrderByRandomAndAnswerIndex() {
        int crsCod = 1;
        List<String> tags = TEST_TAGS.stream().map(TestTag::getTagTxt).collect(Collectors.toList());
        List<String> answerTypes = Arrays.asList(TestAnswer.TYPE_UNIQUE_CHOICE, TestAnswer.TYPE_MULTIPLE_CHOICE);
        List<Long> questions = TEST_QUESTIONS.stream().filter(t -> t.getCrsCod() == crsCod).map(TestQuestion::getId).collect(Collectors.toList());
        TestAnswersQuestion answersQuestion = testQuestionDao.findAllCourseQuestionsByTagAndAnswerTypeOrderByRandomAndAnswerIndex(crsCod, tags, answerTypes, 1).get(0);

        answersQuestion.getTestAnswers().sort(testAnswerComparator);

        assertTrue(questions.contains(answersQuestion.getTestQuestion().getId()));
        assertEquals(TEST_ANSWERS.stream().filter(a -> a.getQstCod() == answersQuestion.getTestQuestion().getId()).collect(Collectors.toList()),
                answersQuestion.getTestAnswers());
    }

    @Test
    public void insertTestQuestion() {
        List<TestQuestion> all = testQuestionDao.findAll();
        List<TestQuestion> expected = TEST_QUESTIONS;
        expected.sort(testQuestionComparator);
        assertEquals(expected, all);
    }

    @Test
    public void updateTestQuestion() {
        TestQuestion testQuestion = new TestQuestion(1, 1, "Stem1 Modified", "TF", false, "Feedback1");
        testQuestionDao.updateTestQuestion(Collections.singletonList(testQuestion));

        List<TestQuestion> modified = testQuestionDao.findAll();
        List<TestQuestion> expected = new ArrayList<>(TEST_QUESTIONS);

        modified.sort(testQuestionComparator);
        expected.sort(testQuestionComparator);

        expected.set(0, testQuestion);

        assertEquals(expected, modified);
    }

    @Test
    public void deleteTestQuestion() {
        List<TestQuestion> all = testQuestionDao.findAll();
        List<TestQuestion> expected = new ArrayList<>(TEST_QUESTIONS);
        List<TestQuestion> allSorted = new ArrayList<>(TEST_QUESTIONS);

        expected.sort(testQuestionComparator);
        allSorted.sort(testQuestionComparator);

        TestQuestion deleted = expected.remove(1);

        assertEquals(allSorted, all);

        int numDeleted = testQuestionDao.deleteTestQuestion(Collections.singletonList(deleted));
        assertEquals(1, numDeleted);
        assertEquals(expected, testQuestionDao.findAll());
    }

    @Test
    public void deleteAll() {
        List<TestQuestion> all = testQuestionDao.findAll();
        List<TestQuestion> expected = new ArrayList<>(TEST_QUESTIONS);
        expected.sort(testQuestionComparator);
        assertEquals(expected, all);

        int numDeleted = testQuestionDao.deleteAll();
        assertEquals(9, numDeleted);
        assertEquals(Collections.EMPTY_LIST, testQuestionDao.findAll());
    }
}