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
 * Specific Pair class for database tables
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class PairTable<FIRST, SECOND> extends Model {
	/**
	 * Pair of values
	 */
	private Pair<FIRST, SECOND> values;
	/**
	 * Table who owns the pair of values
	 */
	private String table;

	/**
	 * Constructor
	 * @param table Table who owns the pair of values
	 * @param first First value
	 * @param second Second value
	 */
	public PairTable(String table, FIRST first, SECOND second) {
		super(0);
		this.values = new Pair<FIRST, SECOND>(first, second);
		this.table = table;
	}

	/**
	 * Gets the pair of values
	 * @return Pair of values
	 */
	public Pair<FIRST, SECOND> getValues() {
		return values;
	}
	
	/**
	 * Gets first value
	 * @return First value
	 */
	public FIRST getFirst() {
		return this.values.getFirst();
	}
	
	/**
	 * Gets second value
	 * @return Second value
	 */
	public SECOND getSecond() {
		return this.values.getSecond();
	}

	/**
	 * Gets the table who owns the pair of values
	 * @return Table who owns the pair of values
	 */
	public String getTable() {
		return table;
	}	
}
