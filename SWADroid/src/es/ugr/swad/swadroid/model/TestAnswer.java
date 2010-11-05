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
 * Class for store a test answer
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class TestAnswer extends Model {
	/**
	 * Flag to know if this is the correct answer of the question
	 */
	private boolean correct;
	/**
	 * Answer's text
	 */
	private String answer;
	
	/**
	 * Constructor
	 * @param id Answer id
	 * @param correct Flag to know if this is the correct answer of the question
	 * @param answer Answer's text
	 */
	public TestAnswer(int id, boolean correct, String answer) {
		super(id);
		this.correct = correct;
		this.answer = answer;
	}

	/**
	 * Gets answer correct flag
	 * @return Answer correct flag
	 */
	public boolean getCorrect() {
		return correct;
	}

	/**
	 * Sets answer correct flag
	 * @param Answer correct flag
	 */
	public void setCorrect(boolean correct) {
		this.correct = correct;
	}

	/**
	 * Gets answer text
	 * @return Answer text
	 */
	public String getAnswer() {
		return answer;
	}

	/**
	 * Sets answer text
	 * @param answer Answer text
	 */
	public void setAnswer(String answer) {
		this.answer = answer;
	}	
}