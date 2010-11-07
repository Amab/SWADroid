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
 * Class for store a message
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class MessageContent extends Model {
	/**
	 * Message subject
	 */
	private String subject;
	/**
	 * Message content
	 */
	private String content;
	/**
	 * Flag of message expanded
	 */
	private boolean expanded;
	/**
	 * User code
	 */
	private int usrcod;
	
	/**
	 * Constructor
	 * @param id Message identifier
	 * @param subject Message subject
	 * @param content Message content
	 * @param expanded Flag of message expanded
	 * @param usrcod User code
	 */
	public MessageContent(int id, String subject, String content, boolean expanded, int usrcod) {
		super(id);
		this.subject = subject;
		this.content = content;
		this.expanded = expanded;
		this.usrcod = usrcod;
	}

	/**
	 * Gets message subject
	 * @return Message subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * Sets message subject
	 * @param subject Message subject
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * Gets message content
	 * @return Message content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * Sets message content
	 * @param content Message content
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * Gets user code
	 * @return User code
	 */
	public int getUsrcod() {
		return usrcod;
	}

	/**
	 * Sets user code
	 * @param usrcod User code
	 */
	public void setUsrcod(int usrcod) {
		this.usrcod = usrcod;
	}

	/**
	 * Gets flag of message expanded
	 * @return Flag of message expanded
	 */
	public boolean isExpanded() {
		return expanded;
	}

	/**
	 * Sets flag of message expanded
	 * @param expanded Flag of message expanded
	 */
	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}
}
