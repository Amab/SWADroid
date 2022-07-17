package es.ugr.swad.swadroid.dao;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import es.ugr.swad.swadroid.model.Group;
import es.ugr.swad.swadroid.model.GroupsCourse;
import es.ugr.swad.swadroid.model.GroupsGroupType;

public class GroupDaoTest extends DaoFixtureTest {

    @Before
    public void setUp() {
        courseDao.insertCourses(COURSES);
        groupTypeDao.insertGroupTypes(GROUP_TYPES);
        groupDao.insertGroups(GROUPS);
    }

    @Test
    public void findAll() {
        List<Group> all = groupDao.findAll();
        assertEquals(GROUPS, all);
    }

    @Test
    public void findAllByTypeCode() {
        List<Group> groups = groupDao.findAllByTypeCode(GROUP_TYPES.get(1).getId());
        List<Group> expected = Collections.singletonList(
                new Group(1, "Group 2", 2, 2, 0, 0, 0, 0, 0)
        );

        assertEquals(expected, groups);
    }

    @Test
    public void findGroupsGroupTypeByGroupCode() {
        GroupsGroupType groupsGroupTypeByGroupCode = groupDao.findGroupsGroupTypeByGroupCode(GROUPS.get(0).getId());
        GroupsGroupType expected = new GroupsGroupType();
        expected.setGroupType(GROUP_TYPES.get(1));
        expected.setGroups(Collections.singletonList(
                new Group(1, "Group 2", 2, 2, 0, 0, 0, 0, 0)
        ));

        assertEquals(expected, groupsGroupTypeByGroupCode);
    }

    @Test
    public void findGroupsCourseByCourseCode() {
        GroupsCourse groupsCourseByCourseCode = groupDao.findGroupsCourseByCourseCode(COURSES.get(1).getId());
        GroupsCourse expected = new GroupsCourse();
        expected.setCourse(COURSES.get(1));
        expected.setGroups(Collections.singletonList(
                new Group(1, "Group 2", 2, 2, 0, 0, 0, 0, 0)
        ));

        assertEquals(expected, groupsCourseByCourseCode);
    }

    @Test
    public void insertGroups() {
        List<Group> all = groupDao.findAll();
        assertEquals(GROUPS, all);
    }

    @Test
    public void updateGroups() {
        Group group = new Group(2, "Group 1 Modified", 1, 1, 0, 0, 0, 0, 0);
        groupDao.updateGroups(Collections.singletonList(group));

        List<Group> modified = groupDao.findAll();
        List<Group> expected = Arrays.asList(
                new Group(1, "Group 2", 2, 2, 0, 0, 0, 0, 0),
                group,
                new Group(3, "Group 3", 3, 3, 0, 0, 0, 0, 0)
        );

        assertEquals(expected, modified);
    }

    @Test
    public void deleteGroups() {
        List<Group> all = groupDao.findAll();
        List<Group> expected = GROUPS.subList(1, 3);
        assertEquals(GROUPS, all);

        int numDeleted = groupDao.deleteGroups(Collections.singletonList(GROUPS.get(0)));
        assertEquals(1, numDeleted);
        assertEquals(expected, groupDao.findAll());
    }

    @Test
    public void deleteGroupsByIdNotIn() {
        List<Group> all = groupDao.findAll();
        List<Group> expected = Collections.singletonList(GROUPS.get(0));
        assertEquals(GROUPS, all);

        int numDeleted = groupDao.deleteGroupsByIdNotIn(Collections.singletonList(GROUPS.get(0).getId()));
        assertEquals(2, numDeleted);
        assertEquals(expected, groupDao.findAll());
    }

    @Test
    public void deleteAll() {
        List<Group> all = groupDao.findAll();
        assertEquals(GROUPS, all);

        int numDeleted = groupDao.deleteAll();
        assertEquals(3, numDeleted);
        assertEquals(Collections.EMPTY_LIST, groupDao.findAll());
    }
}
