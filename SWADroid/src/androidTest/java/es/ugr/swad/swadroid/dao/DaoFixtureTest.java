package es.ugr.swad.swadroid.dao;

import android.content.Context;

import androidx.room.Room;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.util.Arrays;
import java.util.List;

import es.ugr.swad.swadroid.database.AppDatabase;
import es.ugr.swad.swadroid.model.Course;
import es.ugr.swad.swadroid.model.Event;
import es.ugr.swad.swadroid.model.Group;
import es.ugr.swad.swadroid.model.GroupType;
import es.ugr.swad.swadroid.model.SWADNotification;
import es.ugr.swad.swadroid.model.TestAnswer;
import es.ugr.swad.swadroid.model.TestConfig;
import es.ugr.swad.swadroid.model.TestQuestion;
import es.ugr.swad.swadroid.model.TestTag;
import es.ugr.swad.swadroid.model.TestTagsQuestions;
import es.ugr.swad.swadroid.model.User;
import es.ugr.swad.swadroid.model.UserAttendance;

public abstract class DaoFixtureTest {
    protected static AppDatabase db;

    protected static CourseDao courseDao;
    protected static EventDao eventDao;
    protected static GroupTypeDao groupTypeDao;
    protected static GroupDao groupDao;
    protected static UserDao userDao;
    protected static UserAttendanceDao userAttendanceDao;
    protected static SWADNotificationDao swadNotificationDao;
    protected static TestConfigDao testConfigDao;
    protected static TestTagDao testTagDao;
    protected static TestQuestionDao testQuestionDao;
    protected static TestTagsQuestionsDao testTagsQuestionsDao;
    protected static TestAnswerDao testAnswerDao;
    protected static RawDao rawDao;

    private static final long BASE_TIME = System.currentTimeMillis() / 1000;

    protected final List<Course> COURSES = Arrays.asList(
            new Course(1, 1, "c2", "Course 2"),
            new Course(2, 1, "c1", "Course 1"),
            new Course(3, 1, "c3", "Course 3")
    );
    protected final List<Event> EVENTS = Arrays.asList(
            new Event(1, 2, false, "", "", "", "", 0L, 0L, false, "Event 2", "Description 2", "", ""),
            new Event(2, 1, false, "", "", "", "", 0L, 0L, false, "Event 1", "Description 1", "", ""),
            new Event(3, 3, false, "", "", "", "", 0L, 0L, false, "Event 3", "Description 3", "", "")
    );
    protected final List<UserAttendance> ATTENDANCES = Arrays.asList(
            new UserAttendance(1, 2, false),
            new UserAttendance(2, 1, false),
            new UserAttendance(3, 3, false)
    );
    protected final List<User> USERS = Arrays.asList(
            new User(1, "wsKey", "userID2", "userNickname2", "userSurname1", "userSurname2", "userFirstname", "userPhoto", "20210101", 0),
            new User(2, "wsKey", "userID1", "userNickname1", "userSurname1", "userSurname2", "userFirstname", "userPhoto", "20210101", 0),
            new User(3, "wsKey", "userID3", "userNickname3", "userSurname1", "userSurname2", "userFirstname", "userPhoto", "20210101", 0)
    );
    protected final List<GroupType> GROUP_TYPES = Arrays.asList(
            new GroupType(1, "Type 2", 2, 0, 0, 0),
            new GroupType(2, "Type 1", 1, 0, 0, 0),
            new GroupType(3, "Type 3", 3, 0, 0, 0),
            new GroupType(4, "Type 4", 1, 0, 0, 0)
    );
    protected final List<Group> GROUPS = Arrays.asList(
            new Group(1, "Group 2", 2, 2, 0, 0, 0, 0, 0),
            new Group(2, "Group 1", 1, 1, 0, 0, 0, 0, 0),
            new Group(3, "Group 3", 3, 3, 0, 0, 0, 0, 0)
    );
    protected final List<SWADNotification> NOTIFICATIONS = Arrays.asList(
            new SWADNotification(1, 2, "eventType", BASE_TIME, "userNickname", "userSurname1", "userSurname2", "userFirstName", "userPhoto", "location", "summary", 0, "content", true, false),
            new SWADNotification(2, 1, "eventType", BASE_TIME - 604800, "userNickname", "userSurname1", "userSurname2", "userFirstName", "userPhoto", "location", "summary", 0, "content", false, false),
            new SWADNotification(3, 3, "eventType", BASE_TIME - 86400, "userNickname", "userSurname1", "userSurname2", "userFirstName", "userPhoto", "location", "summary", 0, "content", false, false)
    );
    protected final List<TestConfig> TEST_CONFIGS = Arrays.asList(
            new TestConfig(1, 1, 3, 5, "F1", BASE_TIME),
            new TestConfig(2, 1, 3, 5, "F2", BASE_TIME + 1),
            new TestConfig(3, 1, 3, 5, "F3", BASE_TIME + 2)
    );
    protected final List<TestTag> TEST_TAGS = Arrays.asList(
            new TestTag(2, "Tag2"),
            new TestTag(1, "Tag1"),
            new TestTag(3, "Tag3")
    );
    protected final List<TestQuestion> TEST_QUESTIONS = Arrays.asList(
            new TestQuestion(2, 1, "Stem2", TestAnswer.TYPE_TRUE_FALSE, false, "Feedback2"),
            new TestQuestion(1, 1, "Stem1", TestAnswer.TYPE_UNIQUE_CHOICE, false, "Feedback1"),
            new TestQuestion(3, 1, "Stem3", TestAnswer.TYPE_MULTIPLE_CHOICE, false, "Feedback3"),
            new TestQuestion(5, 2, "Stem5", TestAnswer.TYPE_TRUE_FALSE, true, "Feedback5"),
            new TestQuestion(4, 2, "Stem4", TestAnswer.TYPE_UNIQUE_CHOICE, true, "Feedback4"),
            new TestQuestion(6, 2, "Stem6", TestAnswer.TYPE_MULTIPLE_CHOICE, true, "Feedback6"),
            new TestQuestion(8, 3, "Stem8", TestAnswer.TYPE_TRUE_FALSE, false, "Feedback8"),
            new TestQuestion(7, 3, "Stem7", TestAnswer.TYPE_UNIQUE_CHOICE, false, "Feedback7"),
            new TestQuestion(9, 3, "Stem9", TestAnswer.TYPE_MULTIPLE_CHOICE, false, "Feedback9")
    );
    protected final List<TestTagsQuestions> TEST_TAGS_QUESTIONS = Arrays.asList(
            new TestTagsQuestions(1, 2, 1),
            new TestTagsQuestions(2, 2, 1),
            new TestTagsQuestions(3, 2, 1),
            new TestTagsQuestions(5, 1, 2),
            new TestTagsQuestions(4, 1, 2),
            new TestTagsQuestions(6, 1, 2),
            new TestTagsQuestions(8, 3, 3),
            new TestTagsQuestions(7, 3, 3),
            new TestTagsQuestions(9, 3, 3)
    );
    protected final List<TestAnswer> TEST_ANSWERS = Arrays.asList(
            new TestAnswer(1, 1, 1, true, "Answer1", "Feedback1"),
            new TestAnswer(2, 1, 2, false, "Answer2", "Feedback2"),
            new TestAnswer(3, 2, 1, false, "Answer3", "Feedback3"),
            new TestAnswer(4, 2, 2, true, "Answer4", "Feedback4"),
            new TestAnswer(5, 3, 1, true, "Answer5", "Feedback5"),
            new TestAnswer(6, 3, 2, false, "Answer6", "Feedback6"),
            new TestAnswer(7, 4, 1, true, "Answer7", "Feedback7"),
            new TestAnswer(8, 4, 2, false, "Answer8", "Feedback8"),
            new TestAnswer(9, 5, 1, false, "Answer9", "Feedback9"),
            new TestAnswer(10, 5, 2, true, "Answer10", "Feedback10"),
            new TestAnswer(11, 6, 1, true, "Answer11", "Feedback11"),
            new TestAnswer(12, 6, 2, false, "Answer12", "Feedback12"),
            new TestAnswer(13, 7, 1, true, "Answer13", "Feedback13"),
            new TestAnswer(14, 7, 2, false, "Answer14", "Feedback14"),
            new TestAnswer(15, 8, 1, false, "Answer15", "Feedback15"),
            new TestAnswer(16, 8, 2, true, "Answer16", "Feedback16"),
            new TestAnswer(17, 9, 1, true, "Answer17", "Feedback17"),
            new TestAnswer(18, 9, 2, true, "Answer18", "Feedback18")
    );

    @BeforeClass
    public static void setUpClass() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();

        courseDao = db.getCourseDao();
        eventDao = db.getEventDao();
        groupTypeDao = db.getGroupTypeDao();
        groupDao = db.getGroupDao();
        userDao = db.getUserDao();
        userAttendanceDao = db.getUserAttendanceDao();
        swadNotificationDao = db.getSwadNotificationDao();
        testConfigDao = db.getTestConfigDao();
        testTagDao = db.getTestTagDao();
        testQuestionDao = db.getTestQuestionDao();
        testTagsQuestionsDao = db.getTestTagsQuestionsDao();
        testAnswerDao = db.getTestAnswerDao();
        rawDao = db.getRawDao();
    }

    @After
    public void tearDown() {
        db.clearAllTables();
    }

    @AfterClass
    public static void tearDownClass() {
        db.close();
    }
}
