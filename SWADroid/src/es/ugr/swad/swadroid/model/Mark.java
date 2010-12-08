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
 * Class for store a mark
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class Mark extends Model {
	/**
	 * Course code
	 */
	private int crscod;
	/**
	 * Group code
	 */
	private int grpcod;
	/**
	 * Mark file path
	 */
	private String path;
	/**
	 * Mark header
	 */
	private int header;
	/**
	 * Mark footer
	 */
	private int footer;
	
	/**
	 * Constructor
	 * @param id Mark identifier
	 * @param crscod Course code
	 * @param grpcod Group code
	 * @param path Mark file path
	 * @param header Mark header
	 * @param footer Mark footer
	 */
	public Mark(int id, int crscod, int grpcod, String path, int header,
			int footer) {
		super(id);
		this.crscod = crscod;
		this.grpcod = grpcod;
		this.path = path;
		this.header = header;
		this.footer = footer;
	}

	/**
	 * Gets course code
	 * @return Course code
	 */
	public int getCrscod() {
		return crscod;
	}

	/**
	 * Sets course code
	 * @param crscod Course code
	 */
	public void setCrscod(int crscod) {
		this.crscod = crscod;
	}

	/**
	 * Gets group code
	 * @return Group code
	 */
	public int getGrpcod() {
		return grpcod;
	}

	/**
	 * Sets group code
	 * @param grpcod Group code
	 */
	public void setGrpcod(int grpcod) {
		this.grpcod = grpcod;
	}

	/**
	 * Gets mark file path
	 * @return Mark file path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Sets mark file path
	 * @param path Mark file path
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * Gets mark header
	 * @return Mark header
	 */
	public int getHeader() {
		return header;
	}

	/**
	 * Sets mark header
	 * @param header Mark header
	 */
	public void setHeader(int header) {
		this.header = header;
	}

	/**
	 * Gets mark footer
	 * @return Mark footer
	 */
	public int getFooter() {
		return footer;
	}

	/**
	 * Sets mark footer
	 * @param footer Mark footer
	 */
	public void setFooter(int footer) {
		this.footer = footer;
	}	
}
