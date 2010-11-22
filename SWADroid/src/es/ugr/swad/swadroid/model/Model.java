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
 * Superclass for encapsulate common behavior of all Models.
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com> *
 */
public class Model {
	/**
	 * Model identifier
	 */
	private int id;

	public Model(int id) {
		super();
		this.id = id;
	}

	/**
	 * Gets model identifier
	 * @return Model identifier
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Sets model identifier
	 * @param id Model identifier
	 */
	public void setId(int id) {
		this.id = id;
	}
}
