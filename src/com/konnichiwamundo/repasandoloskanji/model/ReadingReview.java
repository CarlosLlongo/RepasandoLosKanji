/**
 * Project    : Repasando los Kanji
 * Created on : 28 junio 2012
 */
package com.konnichiwamundo.repasandoloskanji.model;

import java.util.Vector;

import com.konnichiwamundo.repasandoloskanji.controller.Utils;

/**
 * Representa el repaso de una lectura en concreto.
 * 
 * @author Carlos Llongo
 *
 */
public class ReadingReview {
	
	private String readingId;
	private int timesReviewed;
	private Vector<Boolean> reviewResults;
	private Boolean reviewedInLastCycle;
	
	public ReadingReview(String readingId) {
		this.readingId = readingId;
		this.timesReviewed = 0;
		this.reviewResults = new Vector<Boolean>();
		this.reviewedInLastCycle = false;
	}
	
	public ReadingReview(String readingId, String timesReviewed,
			String[] reviewResults, boolean reviewedInLastCycle) {
		
		this.readingId = readingId;
		this.timesReviewed = Integer.parseInt(timesReviewed);
		
		this.reviewResults = new Vector<Boolean>();
		for(String result : reviewResults){
			this.reviewResults.add(Utils.stringToBoolean(result));
		}
		
		this.reviewedInLastCycle = reviewedInLastCycle;
	}

	public String getReadingId() {
		return readingId;
	}

	public void setReadingId(String readingId) {
		this.readingId = readingId;
	}

	public int getTimesReviewed() {
		return timesReviewed;
	}

	public void setTimesReviewed(int timesReviewed) {
		this.timesReviewed = timesReviewed;
	}

	public Vector<Boolean> getReviewResults() {
		return reviewResults;
	}

	public void setReviewResults(Vector<Boolean> reviewResults) {
		this.reviewResults = reviewResults;
	}

	public Boolean getReviewedInLastCycle() {
		return reviewedInLastCycle;
	}

	public void setReviewedInLastCycle(Boolean reviewedInLastCycle) {
		this.reviewedInLastCycle = reviewedInLastCycle;
	}

	/**
	 * Añade un nuevo resultado a el vector de resultados e incrementa el
	 * registor que indica el número de veces que se ha repasado la lectura.
	 * 
	 * @param result El resultado del repaso de la lectura.
	 */
	public void addNewResult(boolean result) {
		reviewResults.add(new Boolean(result));
		timesReviewed++;
	}
}
