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
package es.ugr.swad.swadroid.model;

import java.util.Hashtable;

import org.ksoap2.serialization.PropertyInfo;

/**
 * Class for store a test configuration
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class TestConfig extends Model {
	/**
	 * Default value for number of questions in test
	 */
	private int def;
	/**
	 * Maximum value for number of questions in test
	 */
	private int max;
	/**
	 * Minimum value for number of questions in test
	 */
	private int min;
	/**
	 * Minimum time (in seconds) by question between two tests
	 */
	private int mintimenxttstperqst;
	/**
	 * Feedback type received by student
	 */
	private String feedback;
	
	/**
	 * @param id Course code
	 * @param def Default value for number of questions in test
	 * @param max Maximum value for number of questions in test
	 * @param min Minimum value for number of questions in test
	 * @param mintimenxttstperqst Minimum time between two tests
	 * @param feedback Feedback type received by student
	 */
	public TestConfig(int id, int def, int max, int min,
			int mintimenxttstperqst, String feedback) {
		super(id);
		this.def = def;
		this.max = max;
		this.min = min;
		this.mintimenxttstperqst = mintimenxttstperqst;
		this.feedback = feedback;
	}

	/**
	 * Gets default value for number of questions in test
	 * @return Default value for number of questions in test
	 */
	public int getDef() {
		return def;
	}

	/**
	 * Sets default value for number of questions in test
	 * @param def Default value for number of questions in test
	 */
	public void setDef(int def) {
		this.def = def;
	}

	/**
	 * Gets maximum value for number of questions in test
	 * @return Maximum value for number of questions in test
	 */
	public int getMax() {
		return max;
	}

	/**
	 * Sets maximum value for number of questions in test
	 * @param max Maximum value for number of questions in test
	 */
	public void setMax(int max) {
		this.max = max;
	}

	/**
	 * Gets minimum value for number of questions in test
	 * @return Minimum value for number of questions in test
	 */
	public int getMin() {
		return min;
	}

	/**
	 * Sets minimum value for number of questions in test
	 * @param min Minimum value for number of questions in test
	 */
	public void setMin(int min) {
		this.min = min;
	}

	/**
	 * Gets minimum time between two tests
	 * @return Minimum time between two tests
	 */
	public int getMintimenxttstperqst() {
		return mintimenxttstperqst;
	}

	/**
	 * Sets minimum time between two tests
	 * @param mintimenxttstperqst Minimum time between two tests
	 */
	public void setMintimenxttstperqst(int mintimenxttstperqst) {
		this.mintimenxttstperqst = mintimenxttstperqst;
	}

	/**
	 * Gets feedback type received by student
	 * @return Feedback type received by student
	 */
	public String getFeedback() {
		return feedback;
	}

	/**
	 * Sets feedback type received by student
	 * @param feedback Feedback type received by student
	 */
	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}

	public Object getProperty(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public int getPropertyCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void getPropertyInfo(int arg0, Hashtable arg1, PropertyInfo arg2) {
		// TODO Auto-generated method stub
		
	}

	public void setProperty(int arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}	
}
