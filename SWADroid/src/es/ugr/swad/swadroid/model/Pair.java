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
public class Pair<FIRST, SECOND> implements Comparable<Pair<FIRST, SECOND>> {
	public final FIRST first;
	public final SECOND second;

	private Pair(FIRST first, SECOND second) {
	    this.first = first;
	    this.second = second;
	}

	public static <FIRST, SECOND> Pair<FIRST, SECOND> of(FIRST first, SECOND second) {
	    return new Pair<FIRST, SECOND>(first, second);
	}

	public int compareTo(Pair<FIRST, SECOND> o) {
	    int cmp = compare(first, o.first);
	    return cmp == 0 ? compare(second, o.second) : cmp;
	}
	
	private static int compare(Object o1, Object o2) {
	    return o1 == null ? o2 == null ? 0 : -1 : o2 == null ? +1 : ((Comparable) o1).compareTo(o2);
	}
}