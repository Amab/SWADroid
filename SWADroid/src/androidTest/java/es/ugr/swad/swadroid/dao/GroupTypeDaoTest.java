package es.ugr.swad.swadroid.dao;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import es.ugr.swad.swadroid.model.GroupType;

public class GroupTypeDaoTest extends DaoFixtureTest {

    @Before
    public void setUp() {
        courseDao.insertCourses(COURSES);
        groupTypeDao.insertGroupTypes(GROUP_TYPES);
    }

    @Test
    public void findAll() {
        List<GroupType> all = groupTypeDao.findAll();
        assertEquals(GROUP_TYPES, all);
    }

    @Test
    public void findAllByCourseCode() {
        List<GroupType> groupTypes = groupTypeDao.findAllByCourseCode(COURSES.get(1).getId());
        List<GroupType> expected = Collections.singletonList(
                new GroupType(1, "Type 2", 2, 0, 0, 0)
        );

        assertEquals(expected, groupTypes);
    }

    @Test
    public void findAllByCourseCodeOrderByGroupTypeNameAsc() {
        List<GroupType> groupTypes = groupTypeDao.findAllByCourseCodeOrderByGroupTypeNameAsc(COURSES.get(0).getId());
        List<GroupType> expected = Arrays.asList(
                new GroupType(2, "Type 1", 1, 0, 0, 0),
                new GroupType(4, "Type 4", 1, 0, 0, 0)
        );

        assertEquals(expected, groupTypes);
    }

    @Test
    public void insertGroupTypes() {
        List<GroupType> all = groupTypeDao.findAll();
        assertEquals(GROUP_TYPES, all);
    }

    @Test
    public void updateGroupTypes() {
        GroupType groupType = new GroupType(2, "Type 1 Modified", 1, 0, 0, 0);
        groupTypeDao.updateGroupTypes(Collections.singletonList(groupType));

        List<GroupType> modified = groupTypeDao.findAll();
        List<GroupType> expected = Arrays.asList(
                new GroupType(1, "Type 2", 2, 0, 0, 0),
                groupType,
                new GroupType(3, "Type 3", 3, 0, 0, 0),
                new GroupType(4, "Type 4", 1, 0, 0, 0)
        );

        assertEquals(expected, modified);
    }

    @Test
    public void deleteGroupTypes() {
        List<GroupType> all = groupTypeDao.findAll();
        List<GroupType> expected = GROUP_TYPES.subList(1, 4);
        assertEquals(GROUP_TYPES, all);

        int numDeleted = groupTypeDao.deleteGroupTypes(Collections.singletonList(GROUP_TYPES.get(0)));
        assertEquals(1, numDeleted);
        assertEquals(expected, groupTypeDao.findAll());
    }

    @Test
    public void deleteAll() {
        List<GroupType> all = groupTypeDao.findAll();
        assertEquals(GROUP_TYPES, all);

        int numDeleted = groupTypeDao.deleteAll();
        assertEquals(4, numDeleted);
        assertEquals(Collections.EMPTY_LIST, groupTypeDao.findAll());
    }
}
