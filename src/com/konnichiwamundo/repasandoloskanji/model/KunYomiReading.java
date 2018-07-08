/**
 * Project    : Repasando los Kanji
 * Created on : 12 diciembre 2011
 */
package com.konnichiwamundo.repasandoloskanji.model;

import com.konnichiwamundo.repasandoloskanji.controller.Utils;

/**
 * Clase que modeliza una lectura de kun-yomi.
 * 
 * @author Carlos Llongo
 *
 */
public class KunYomiReading implements Comparable<KunYomiReading>{
	
	public String book1Reference;
	public String rootWordAndInflection;
	public String kunYomi;
	public String meaning;
	
	public String getBook1Reference() {
		return book1Reference;
	}
	public void setBook1Reference(String book1Reference) {
		this.book1Reference = book1Reference;
	}
	public String getRootWordAndInflection() {
		return rootWordAndInflection;
	}
	public void setRootWordAndInflection(String rootWordAndInflection) {
		this.rootWordAndInflection = rootWordAndInflection;
	}
	public String getKunYomi() {
		return kunYomi;
	}
	public void setKunYomi(String kunYomi) {
		this.kunYomi = kunYomi;
	}
	public String getMeaning() {
		return meaning;
	}
	public void setMeaning(String meaning) {
		this.meaning = meaning;
	}
	
	@Override
	public int compareTo(KunYomiReading otherKunYomi) {
		int thisIntValue = Utils.getReferenceAsInt(this.book1Reference);
		int otherIntValue = Utils.getReferenceAsInt(otherKunYomi.book1Reference);
		if(thisIntValue < otherIntValue){
			return -1;
		}
		else if(thisIntValue > otherIntValue){
			return 1;
		}

		return this.book1Reference.compareTo(otherKunYomi.book1Reference);
	}
}
