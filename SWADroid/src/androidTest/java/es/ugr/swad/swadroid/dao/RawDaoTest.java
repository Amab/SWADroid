package es.ugr.swad.swadroid.dao;

import static junit.framework.TestCase.assertEquals;

import androidx.sqlite.db.SimpleSQLiteQuery;

import org.junit.Test;

public class RawDaoTest extends DaoFixtureTest {

    @Test
    public void rawQuery() {
        assertEquals(0, rawDao.rawQuery(new SimpleSQLiteQuery("VACUUM")));
    }
}
