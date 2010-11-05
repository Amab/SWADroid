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

/**
 * Class for store a test configuration
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class TestConfig extends Model {
	int def;
	int max;
	int min;
	/**
	 * Minimum time between two tests
	 */
	int mintimenxttstperqst;
	/**
	 * Feedback type received by student
	 */
	String feedback;
	
	/**
	 * @param id
	 * @param def
	 * @param max
	 * @param min
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
	 * @return the def
	 */
	public int getDef() {
		return def;
	}

	/**
	 * @param def the def to set
	 */
	public void setDef(int def) {
		this.def = def;
	}

	/**
	 * @return the max
	 */
	public int getMax() {
		return max;
	}

	/**
	 * @param max the max to set
	 */
	public void setMax(int max) {
		this.max = max;
	}

	/**
	 * @return the min
	 */
	public int getMin() {
		return min;
	}

	/**
	 * @param min the min to set
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
}
