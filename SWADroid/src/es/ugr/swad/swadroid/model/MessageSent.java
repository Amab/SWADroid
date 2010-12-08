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
 * Class for store a sent message
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class MessageSent extends MessageContent {
	/**
	 * Course code
	 */
	private int crscod;
	/**
	 * Date and time's creation of message
	 */
	private String creattime;
	
	/**
	 * Constructor
	 * @param id Message identifier
	 * @param subject Message subject
	 * @param content Message content
	 * @param expanded Flag of message expanded
	 * @param usrcod User code
	 * @param crscod Course code
	 * @param creattime Date and time's creation of message
	 */
	public MessageSent(int id, String subject, String content,
			boolean expanded, int usrcod, int crscod, String creattime) {
		super(id, subject, content, expanded, usrcod);
		this.crscod = crscod;
		this.creattime = creattime;
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
	 * Gets date and time's creation of message
	 * @return Date and time's creation of message
	 */
	public String getCreattime() {
		return creattime;
	}

	/**
	 * Sets date and time's creation of message
	 * @param creattime Date and time's creation of message
	 */
	public void setCreattime(String creattime) {
		this.creattime = creattime;
	}	
}
