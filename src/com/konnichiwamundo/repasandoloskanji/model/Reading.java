/**
 * Project    : Repasando los Kanji
 * Created on : 17 agosto 2011
 */

package com.konnichiwamundo.repasandoloskanji.model;

import com.konnichiwamundo.repasandoloskanji.controller.Utils;

/**
 * Clase que modeliza la lectura de un Kanji.
 * 
 * @author Carlos Llongo
 *
 */
public class Reading implements Comparable<Reading>{

	public String book1reference;
	public String book2reference;
	public String onyomi;
	public String compound;
	public String compoundReading;
	public String compoundMeaning;
	
	public String getBook1reference() {
		return book1reference;
	}
	public void setBook1reference(String book1reference) {
		this.book1reference = book1reference;
	}
	public String getBook2reference() {
		return book2reference;
	}
	public void setBook2reference(String book2reference) {
		this.book2reference = book2reference;
	}
	public String getOnyomi() {
		return onyomi;
	}
	public void setOnyomi(String onyomi) {
		this.onyomi = onyomi;
	}
	public String getCompound() {
		return compound;
	}
	public void setCompound(String compound) {
		this.compound = compound;
	}
	public String getCompoundReading() {
		return compoundReading;
	}
	public void setCompoundReading(String compoundReading) {
		this.compoundReading = compoundReading;
	}
	public String getCompoundMeaning() {
		return compoundMeaning;
	}
	public void setCompoundMeaning(String compoundMeaning) {
		this.compoundMeaning = compoundMeaning;
	}
	
	public int getBook2ReferenceAsInt(){
		return Utils.getReferenceAsInt(book2reference);
	}
	
	@Override
	public String toString() {
		return "Vol1: " + book1reference + "\nVol2: " + book2reference +
		"\nOn-yomi: " + onyomi + "\nCompound: " + compound +
		"\nCompound Reading: " + compoundReading
		+ "\nCompound Meaning: " + compoundMeaning;
	}
	
	public int compareTo(Reading reading) {
		int thisBook2Ref = this.getBook2ReferenceAsInt();
		int otherBook2Ref = reading.getBook2ReferenceAsInt();
		if(thisBook2Ref == otherBook2Ref){
			return 0;
		}
		else if(thisBook2Ref < otherBook2Ref){
			return -1;
		}
		
		return 1;
	}
	
	
}
