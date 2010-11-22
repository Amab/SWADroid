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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Clas for store a test question
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class TestQuestion extends Model {
	/**
	 * Question's text
	 */
	private String question;
	/**
	 * Answer type
	 */
	private String anstype;
	/**
	 * Number of hits
	 */
	private int numhits;
	/**
	 * Flag to shuffle answers in test
	 */
	private boolean shuffle;
	/**
	 * Test score
	 */
	private float score;
	/**
	 * Question's answers
	 */
	private List<TestAnswer> answers;
	
	/**
	 * @param id Test identifier
	 * @param question Question's text
	 * @param anstype Answer type
	 * @param numhits Number of hits
	 * @param shuffle Flag to shuffle answers in test
	 * @param score Test score
	 */
	public TestQuestion(int id, String question, String anstype, int numhits,
			boolean shuffle, float score) {
		super(id);
		this.question = question;
		this.anstype = anstype;
		this.numhits = numhits;
		this.shuffle = shuffle;
		this.score = score;
		this.answers = new ArrayList<TestAnswer>();
	}

	/**
	 * Gets question's text
	 * @return Question's text
	 */
	public String getQuestion() {
		return question;
	}

	/**
	 * Sets question's text
	 * @param question Question's text
	 */
	public void setQuestion(String question) {
		this.question = question;
	}

	/**
	 * Gets answer type
	 * @return Answer type
	 */
	public String getAnstype() {
		return anstype;
	}

	/**
	 * Sets answer type
	 * @param anstype Answer type
	 */
	public void setAnstype(String anstype) {
		this.anstype = anstype;
	}

	/**
	 * Gets number of hits
	 * @return Number of hits
	 */
	public int getNumhits() {
		return numhits;
	}

	/**
	 * Sets number of hits
	 * @param numhits Number of hits
	 */
	public void setNumhits(int numhits) {
		this.numhits = numhits;
	}

	/**
	 * Gets shuffle flag
	 * @return Shuffle flag
	 */
	public boolean getShuffle() {
		return shuffle;
	}

	/**
	 * Sets shuffle flag
	 * @param shuffle Shuffle flag
	 */
	public void setShuffle(boolean shuffle) {
		this.shuffle = shuffle;
	}

	/**
	 * Gets test score
	 * @return Test score
	 */
	public float getScore() {
		return score;
	}

	/**
	 * Sets test score
	 * @param score Test score
	 */
	public void setScore(float score) {
		this.score = score;
	}

	/**
	 * Gets question's answers
	 * @return Question's answers
	 */
	public List<TestAnswer> getAnswers() {
		return answers;
	}

	/**
	 * Sets question's answers
	 * @param answers Question's answers
	 */
	public void setAnswers(List<TestAnswer> answers) {
		this.answers = answers;
	}
	
	/**
	 * Gets correct answer for this test
	 * @return Correct test's answer
	 */
	public List<TestAnswer> getCorrectAnswers() {
		List<TestAnswer> corrects = new ArrayList<TestAnswer>();
		TestAnswer current = null;
		Iterator<TestAnswer> it = this.answers.iterator();
		
		while(it.hasNext()) {
			current = it.next();
			if(current.getCorrect()) {
				corrects.add(current);
			}
		}
		
		if(corrects.isEmpty()) {
			corrects = null;
		}
		
		return corrects;
	}
}