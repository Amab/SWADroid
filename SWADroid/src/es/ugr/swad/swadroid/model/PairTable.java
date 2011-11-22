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
	 * Sets first value
	 * param first First value
	 */
	public void setFirst(FIRST first) {
		this.values.setFirst(first);
	}

	/**
	 * Sets second value
	 * param second Second value
	 */
	public void setSecond(SECOND second) {
		this.values.setSecond(second);
	}

	/**
	 * Gets the table who owns the pair of values
	 * @return Table who owns the pair of values
	 */
	public String getTable() {
		return table;
	}

	/**
	 * @param table the table to set
	 */
	public void setTable(String table) {
		this.table = table;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((table == null) ? 0 : table.hashCode());
		result = prime * result + ((values == null) ? 0 : values.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		PairTable other = (PairTable) obj;
		if (table == null) {
			if (other.table != null)
				return false;
		} else if (!table.equals(other.table))
			return false;
		if (values == null) {
			if (other.values != null)
				return false;
		} else if (!values.equals(other.values))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PairTable [values=" + values + ", table=" + table + "]";
	}	
}
