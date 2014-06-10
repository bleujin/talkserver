/**
 * Synesketch 
 * Copyright (C) 2008  Uros Krcadinac
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package net.ion.emotion;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import net.ion.emotion.Emotion.EType;
import net.ion.framework.util.ListUtil;

/**
 * Defines emotional content of the text.
 * <p>
 * That is:
 * <ul>
 * <li>General emotional weight
 * <li>General valence (emotion is positive or negative)
 * <li>Six specific emotional weights, defined by Ekman's categories: happiness weight, sadness weight, fear weight, anger weight, disgust weight, surprise weight. These specific emotions are defined by the class {@link Emotion}.
 * <li>Previous {@link EmotionalState} (so that whole emotional history of one conversation can be accessed from the Processing applet ({@link PApplet})).
 * </ul>
 * <p>
 * Weights have values between 0 and 1 (0 for no emotion, 1 for full emotion, 0.5 for the emotion of average intesity). Valence can be -1, 0, or 1 (negative, neutral, and positive emotion, respectively).
 * 
 * @author Uros Krcadinac email: uros@krcadinac.com
 * @version 1.0
 * 
 */
public class EmotionalState extends SynesketchState {

	private double generalWeight = 0.0;

	private int valence = 0;

	private EmotionalState previous;

	private SortedSet<Emotion> emotions;


	/**
	 * Class constuctor which sets the text, general emotional weight, emotional valence, and all of the emotional weights (in a form of a SortedSet).
	 * 
	 * @param text
	 *            {@link String} representing the text
	 * @param emotions
	 *            {@link SortedSet} containing all of the specific Ekman emotinal weights, defined by the {@link Emotion} class
	 * @param generalWeight
	 *            double representing the general emotional weight
	 * @param valence
	 *            int representing the emotinal valence
	 */
	public EmotionalState(String text, SortedSet<Emotion> emotions, double generalWeight, int valence) {
		super(text);
		this.generalWeight = generalWeight;
		this.valence = valence;
		this.emotions = emotions;
	}

	/**
	 * Returns {@link Emotion} with the highest weight.
	 * 
	 * @return Emotion with the highest weight
	 */

	public Emotion getStrongestEmotion() {
		return emotions.first();
	}

	
	public Emotion emotion(EType etype){
		Emotion value = new Emotion(0.0, etype);
		for (Emotion e : emotions) {
			if (e.etype() == etype) {
				value = e;
			}
		}
		return value;

	}
	
	public EmotionalState getPrevious() {
		return previous;
	}

	public void setPrevious(EmotionalState previous) {
		this.previous = previous;
	}

	public int getValence() {
		return valence;
	}

	public double getGeneralWeight() {
		return generalWeight;
	}

	public String toString() {
		return "Text: " + text + "\nGeneral weight: " + generalWeight + "\nValence: " + valence + "\nHappiness weight: " + emotion(EType.HAPPINESS).weight() + "\nSadness weight: " + emotion(EType.SADNESS).weight() + "\nAnger weight: " + emotion(EType.ANGER).weight() + "\nFear weight: " + emotion(EType.FEAR).weight() + "\nDisgust weight: "
				+ emotion(EType.DISGUST).weight() + "\nSurprise weight: " + emotion(EType.SURPRISE).weight() + "\n";
	}

	public String toString(String separator) {
		return text + separator + generalWeight + separator + valence + separator + emotion(EType.HAPPINESS).weight() + separator + emotion(EType.SADNESS).weight() + separator + emotion(EType.ANGER).weight() + separator + emotion(EType.FEAR).weight() + separator + emotion(EType.DISGUST).weight() + separator + emotion(EType.SURPRISE).weight() ;
	}

}
