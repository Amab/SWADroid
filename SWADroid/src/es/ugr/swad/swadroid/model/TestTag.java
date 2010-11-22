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
 * Class for store a test tag
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class TestTag extends Model {
	/**
	 * Course code
	 */
	private int crsCod;
	/**
	 * Tag's text
	 */
	private String tagTxt;
	/**
	 * Flag for know if the tag is hidden
	 */
	private boolean tagHidden;
	
	/**
	 * Constructor
	 * @param id Tag identifier
	 * @param crsCod Course code
	 * @param tagTxt Tag's text
	 * @param tagHidden Flag for know if the tag is hidden
	 */
	public TestTag(int id, int crsCod, String tagTxt, boolean tagHidden) {
		super(id);
		this.crsCod = crsCod;
		this.tagTxt = tagTxt;
		this.tagHidden = tagHidden;
	}

	/**
	 * Gets course code
	 * @return Course code
	 */
	public int getCrsCod() {
		return crsCod;
	}

	/**
	 * Sets course code
	 * @param crsCod Course code
	 */
	public void setCrsCod(int crsCod) {
		this.crsCod = crsCod;
	}

	/**
	 * Gets tag's text
	 * @return Tag's text
	 */
	public String getTagTxt() {
		return tagTxt;
	}

	/**
	 * Sets tag's text
	 * @param tagTxt Tag's text
	 */
	public void setTagTxt(String tagTxt) {
		this.tagTxt = tagTxt;
	}

	/**
	 * Gets hidden tag flag
	 * @return Hidden tag flag
	 */
	public boolean isTagHidden() {
		return tagHidden;
	}

	/**
	 * Sets hidden tag flag
	 * @param tagHidden Hidden tag flag
	 */
	public void setTagHidden(boolean tagHidden) {
		this.tagHidden = tagHidden;
	}	
}
