/*
 *  This file is part of SWADroid.
 *
 *  Copyright (C) 2010 Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 *
 *  SWADroid is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  SWADroid is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with SWADroid.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.ugr.swad.swadroid.test;

import java.util.List;

import junit.framework.Assert;

import android.test.ActivityInstrumentationTestCase2;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;

import es.ugr.swad.swadroid.Global;
import es.ugr.swad.swadroid.SWADMain;
import es.ugr.swad.swadroid.model.Course;
import es.ugr.swad.swadroid.model.DataBaseHelper;
import es.ugr.swad.swadroid.model.Model;
import es.ugr.swad.swadroid.model.PairTable;
/**
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 *
 */
public class DataBaseHelperTest extends ActivityInstrumentationTestCase2<SWADMain> {
	private DataBaseHelper dbHelper;
    private DataFramework db;
	List<Model> rows;
	List<Entity> entities;
    Course c, d;
	PairTable<Integer, Integer> p, q;
    
    public DataBaseHelperTest() {
        super("es.ugr.swad.swadroid", SWADMain.class);
    }

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		db = DataFramework.getInstance();
        db.open(this.getActivity(), this.getActivity().getPackageName());
        dbHelper = new DataBaseHelper(db);
        
        c = new Course(0, "Test Course");
        d = new Course(1, "Test Course D");
		//p = new PairTable<Integer, Integer>(Global.DB_TABLE_NOTICES_COURSES, 0, 0);
		//q = new PairTable<Integer, Integer>(Global.DB_TABLE_NOTICES_COURSES, 1, 1);
		
		dbHelper.insertCourse(c);
		//dbHelper.insertPairTable(p);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		db.emptyTables();
		dbHelper.close();
		super.tearDown();
	}

	/**
	 * Test method for {@link es.ugr.swad.swadroid.model.DataBaseHelper#getAllRows(java.lang.String)}.
	 */
	public void testGetAllRows() {
		List<Model> rows;
		
		rows = dbHelper.getAllRows(Global.DB_TABLE_COURSES);
		Assert.assertEquals("", c, rows.get(0));
	}

	/**
	 * Test method for {@link es.ugr.swad.swadroid.model.DataBaseHelper#insertCourse(es.ugr.swad.swadroid.model.Course)}.
	 */
	public void testInsertCourse() {
		Course t;
		Entity ent;
		
		dbHelper.insertCourse(d);		
		entities = db.getEntityList(Global.DB_TABLE_COURSES, "id = " + d.getId());
		ent = entities.get(0);
		
		t = new Course(ent.getInt("id"), ent.getString("name"));
		
		Assert.assertEquals(d, t);
	}

	/**
	 * Test method for {@link es.ugr.swad.swadroid.model.DataBaseHelper#insertPairTable(es.ugr.swad.swadroid.model.PairTable)}.
	 */
	/*public void testInsertPairTable() {
		PairTable<Integer, Integer> t;
		Entity ent;
		String where;
		
		dbHelper.insertPairTable(q);
		where = "idcourse = " + q.getFirst() + " AND " + "idnotice = " + q.getSecond();
		
		entities = db.getEntityList(Global.DB_TABLE_NOTICES_COURSES, where);
		ent = entities.get(0);
		t = new PairTable<Integer, Integer>(Global.DB_TABLE_NOTICES_COURSES, ent.getInt("idcourse"), ent.getInt("idnotice"));
		
		Assert.assertEquals(q, t);
	}*/

	/**
	 * Test method for {@link es.ugr.swad.swadroid.model.DataBaseHelper#updateCourse(es.ugr.swad.swadroid.model.Course)}.
	 */
	public void testUpdateCourse() {
		Course t;
		Entity ent;
		
		dbHelper.updateCourse(c, d);
		entities = db.getEntityList(Global.DB_TABLE_COURSES, "id = " + d.getId());
		ent = entities.get(0);
		
		t = new Course(ent.getInt("id"), ent.getString("name"));
		
		Assert.assertEquals(d, t);
	}

	/**
	 * Test method for {@link es.ugr.swad.swadroid.model.DataBaseHelper#updatePairTable(es.ugr.swad.swadroid.model.PairTable)}.
	 */
/*	public void testUpdatePairTable() {
		PairTable<Integer, Integer> t;
		Entity ent;
		String where;

		dbHelper.updatePairTable(p, q);
		where = "idcourse = " + q.getFirst() + " AND " + "idnotice = " + q.getSecond();
		entities = db.getEntityList(Global.DB_TABLE_NOTICES_COURSES, where);
		ent = entities.get(0);
		
		t = new PairTable<Integer, Integer>(Global.DB_TABLE_NOTICES_COURSES, ent.getInt("idcourse"), ent.getInt("idnotice"));
		
		Assert.assertEquals(q, t);
	}*/

	/**
	 * Test method for {@link es.ugr.swad.swadroid.model.DataBaseHelper#removeCourse(int)}.
	 */
	public void testRemoveCourse() {
		int id = c.getId();
		String where;
		
		dbHelper.removeCourse(id);
		where = "id = " + id;
		entities = db.getEntityList(Global.DB_TABLE_COURSES, where);
		Assert.assertEquals("Course has not been removed", 0, entities.size());
		
		/*where = "idcourse = " + id;
		entities = db.getEntityList(Global.DB_TABLE_NOTICES_COURSES, where);
		Assert.assertEquals("Relations has not been removed", 0, entities.size());*/
	}
}
