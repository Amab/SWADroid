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

import java.util.Collections;
import java.util.List;

/**
 * Class for store a test
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 */
public class Test extends Model {
	/**
	 * List of questions and related answers
	 */
	List<Pair<TestQuestion, List<TestAnswer>>> questionsAndAnswers;

	/**
	 * Constructor
	 */
	public Test() {
		super(0);
	}

	/**
	 * @param questionsAndAnswers List of questions and related answers
	 */
	public Test(List<Pair<TestQuestion, List<TestAnswer>>> questionsAndAnswers) {
		super(0);
		this.questionsAndAnswers = questionsAndAnswers;
	}
	
	/**
	 * Gets the list of questions and related answers
	 * @return List of questions and related answers
	 */
	public List<Pair<TestQuestion, List<TestAnswer>>> getQuestionsAndAnswers() {
		return questionsAndAnswers;
	}

	/**
	 * Sets the list of questions and related answers
	 * @param questionsAndAnswers List of questions and related answers
	 */
	public void setQuestionsAndAnswers(
			List<Pair<TestQuestion, List<TestAnswer>>> questionsAndAnswers) {
		this.questionsAndAnswers = questionsAndAnswers;
	}

	/**
	 * Gets the question stored in position i and related answers
	 * @param i Question's position
	 * @return A pair of values <Question, List of answers>
	 */
	public Pair<TestQuestion, List<TestAnswer>> getQuestionAnswers(int i) {
		return questionsAndAnswers.get(i);
	}
	
	/**
	 * Adds a question and related answers
	 * @param qa A pair of values <Question, List of answers>
	 */
	public void addQuestionAnswers(Pair<TestQuestion, List<TestAnswer>> qa) {
		questionsAndAnswers.add(qa);
	}
	
	/**
	 * Removes a question and related answers
	 * @param qa A pair of values <Question, List of answers>
	 */
	public void removeQuestionAnswers(Pair<TestQuestion, List<TestAnswer>> qa) {
		questionsAndAnswers.remove(qa);
	}
	
	/**
	 * Removes a question and related answers
	 * @param i Question's position
	 */
	public void removeQuestionAnswers(int i) {
		questionsAndAnswers.remove(i);
	}
	
	/**
	 * Shuffles the list of questions and related answers
	 */
	public void shuffle() {
		Collections.shuffle(questionsAndAnswers);
	}
}
