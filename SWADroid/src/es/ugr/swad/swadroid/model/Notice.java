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
 * Class for store a notice
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class Notice extends Model {
	/**
	 * Notice timestamp
	 */
	private int timestamp;
	/**
	 * Notice description
	 */
	private String description;
	
	/**
	 * Constructor
	 * @param id Notice identifier
	 * @param timestamp Notice timestamp
	 * @param description Notice description
	 */
	public Notice(int id, int timestamp, String description) {
		super(id);
		this.timestamp = timestamp;
		this.description = description;
	}

	/**
	 * Gets notice timestamp
	 * @return Notice timestamp
	 */
	public int getTimestamp() {
		return timestamp;
	}

	/**
	 * Sets notice timestamp
	 * @param timestamp Notice timestamp
	 */
	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * Gets notice description
	 * @return Notice description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets notice description
	 * @param description Notice description
	 */
	public void setDescription(String description) {
		this.description = description;
	}	
}
