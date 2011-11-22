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
 * Class for manage a pair of values
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class Pair<FIRST, SECOND>
{
	/**
	 * First value
	 */
	private FIRST first;
	/**
	 * Second value
	 */
	private SECOND second;

	/**
	 * Constructor
	 * @param f First value
	 * @param s Second value
	 */
	public Pair(FIRST f, SECOND s)
	{ 
		first = f;
		second = s;   
	}

	/**
	 * Gets first value
	 * @return First value
	 */
	public FIRST getFirst()
	{
		return first;
	}

	/**
	 * Gets second value
	 * @return Second value
	 */
	public SECOND getSecond() 
	{
		return second;
	}

	/**
	 * Sets first value
	 * param first First value
	 */
	public void setFirst(FIRST first) {
		this.first = first;
	}

	/**
	 * Sets second value
	 * param second Second value
	 */
	public void setSecond(SECOND second) {
		this.second = second;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((first == null) ? 0 : first.hashCode());
		result = prime * result + ((second == null) ? 0 : second.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Pair [first=" + first + ", second=" + second + "]";
	}
}