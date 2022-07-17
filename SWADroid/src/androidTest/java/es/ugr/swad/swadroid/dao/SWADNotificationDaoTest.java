package es.ugr.swad.swadroid.dao;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import es.ugr.swad.swadroid.model.SWADNotification;

public class SWADNotificationDaoTest extends DaoFixtureTest {

    @Before
    public void setUp() {
        swadNotificationDao.insertSWADNotifications(NOTIFICATIONS);
    }

    @Test
    public void findAll() {
        List<SWADNotification> all = swadNotificationDao.findAll();
        assertEquals(NOTIFICATIONS, all);
    }

    @Test
    public void findAllOrderByEventTimeDesc() {
        List<SWADNotification> notifications = swadNotificationDao.findAllOrderByEventTimeDesc(false);
        List<SWADNotification> expected = Arrays.asList(
                NOTIFICATIONS.get(2),
                NOTIFICATIONS.get(1)
        );

        assertEquals(expected, notifications);
    }

    @Test
    public void findAllPendingToRead() {
        List<SWADNotification> notifications = swadNotificationDao.findAllPendingToRead();
        List<SWADNotification> expected = Collections.singletonList(
                NOTIFICATIONS.get(0)
        );

        assertEquals(expected, notifications);
    }

    @Test
    public void findById() {
        SWADNotification notification = swadNotificationDao.findById(1);
        assertEquals(NOTIFICATIONS.get(0), notification);
    }

    @Test
    public void countAll() {
        long count = swadNotificationDao.countAll();
        assertEquals(3, count);
    }

    @Test
    public void findMaxEventTime() {
        long maxEventTime = swadNotificationDao.findMaxEventTime();
        assertEquals(NOTIFICATIONS.get(0).getEventTime(), maxEventTime);
    }

    @Test
    public void insertSWADNotifications() {
        List<SWADNotification> all = swadNotificationDao.findAll();
        assertEquals(NOTIFICATIONS, all);
    }

    @Test
    public void updateSWADNotifications() {
        SWADNotification notification = new SWADNotification(2, 1, "eventType", 604800, "userNickname", "userSurname1", "userSurname2", "userFirstName", "userPhoto", "location", "summary", 0, "content", false, false);
        swadNotificationDao.updateSWADNotifications(Collections.singletonList(notification));

        List<SWADNotification> modified = swadNotificationDao.findAll();
        List<SWADNotification> expected = Arrays.asList(
                NOTIFICATIONS.get(0),
                notification,
                NOTIFICATIONS.get(2)
        );

        assertEquals(expected, modified);
    }

    @Test
    public void updateAllBySeenLocal() {
        swadNotificationDao.updateAllBySeenLocal(true);

        List<SWADNotification> modified = swadNotificationDao.findAll();
        List<SWADNotification> expected = NOTIFICATIONS.stream().peek(n -> n.setSeenLocal(true))
                .collect(Collectors.toList());

        assertEquals(expected, modified);
    }

    @Test
    public void updateAllBySeenRemote() {
        swadNotificationDao.updateAllBySeenRemote(true);

        List<SWADNotification> modified = swadNotificationDao.findAll();
        List<SWADNotification> expected = NOTIFICATIONS.stream().peek(n -> n.setSeenRemote(true))
                .collect(Collectors.toList());

        assertEquals(expected, modified);
    }

    @Test
    public void deleteSWADNotifications() {
        List<SWADNotification> all = swadNotificationDao.findAll();
        List<SWADNotification> expected = NOTIFICATIONS.subList(1, 3);
        assertEquals(NOTIFICATIONS, all);

        int numDeleted = swadNotificationDao.deleteSWADNotifications(Collections.singletonList(NOTIFICATIONS.get(0)));
        assertEquals(1, numDeleted);
        assertEquals(expected, swadNotificationDao.findAll());
    }

    @Test
    public void deleteAllByAge() {
        List<SWADNotification> all = swadNotificationDao.findAll();
        List<SWADNotification> expected = Arrays.asList(
                NOTIFICATIONS.get(0),
                NOTIFICATIONS.get(2)
        );
        assertEquals(NOTIFICATIONS, all);

        int numDeleted = swadNotificationDao.deleteAllByAge(604800);
        assertEquals(1, numDeleted);
        assertEquals(expected, swadNotificationDao.findAll());
    }

    @Test
    public void deleteAll() {
        List<SWADNotification> all = swadNotificationDao.findAll();
        assertEquals(NOTIFICATIONS, all);

        int numDeleted = swadNotificationDao.deleteAll();
        assertEquals(3, numDeleted);
        assertEquals(Collections.EMPTY_LIST, swadNotificationDao.findAll());
    }
}
