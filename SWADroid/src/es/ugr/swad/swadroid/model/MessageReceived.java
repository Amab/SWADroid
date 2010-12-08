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
 * Class for store a received message
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class MessageReceived extends MessageContent {
	/**
	 * Flag of message notified
	 */
	private boolean notified;
	/**
	 * Flag of message open
	 */
	private boolean open;
	/**
	 * Flag of message replied
	 */
	private boolean replied;
	
	/**
	 * Constructor
	 * @param id Message identifier
	 * @param subject Message subject
	 * @param content Message content
	 * @param notified Flag of message notified
	 * @param open Flag of message open
	 * @param replied Flag of message replied
	 * @param expanded Flag of message expanded
	 */
	public MessageReceived(int id, String subject, String content, int usrcod,
			boolean notified, boolean open, boolean replied, boolean expanded) {
		super(id, subject, content, expanded, usrcod);
		this.notified = notified;
		this.open = open;
		this.replied = replied;
	}

	/**
	 * Gets flag of message notified
	 * @return Flag of message notified
	 */
	public boolean isNotified() {
		return notified;
	}

	/**
	 * Sets flag of message notified
	 * @param notified Flag of message notified
	 */
	public void setNotified(boolean notified) {
		this.notified = notified;
	}

	/**
	 * Gets flag of message open
	 * @return Flag of message open
	 */
	public boolean isOpen() {
		return open;
	}

	/**
	 * Sets flag of message open
	 * @param open Flag of message open
	 */
	public void setOpen(boolean open) {
		this.open = open;
	}

	/**
	 * Gets flag of message replied
	 * @return Flag of message replied
	 */
	public boolean isReplied() {
		return replied;
	}

	/**
	 * Sets flag of message replied
	 * @param replied Flag of message replied
	 */
	public void setReplied(boolean replied) {
		this.replied = replied;
	}
}
