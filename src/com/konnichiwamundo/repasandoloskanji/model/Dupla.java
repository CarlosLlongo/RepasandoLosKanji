/**
 * Project    : Repasando los Kanji
 * Created on : 24 octubre 2011
 */

package com.konnichiwamundo.repasandoloskanji.model;

import com.konnichiwamundo.repasandoloskanji.controller.Utils;

/**
 * Esta clase representa una dupla de valores, uno int y el otro string. El 
 * string será una representación de un int pero puede tener alguna coletilla,
 * como por ejemplo 1024a, 1024b, 1024c ...
 * 
 * De esta manera, una colección de duplas puede ser ordenada por su valor
 * entero, mientras mantenemos accesible su valor de String.
 * 
 * @author Carlos Llongo
 *
 */
public class Dupla implements Comparable<Dupla>{
	public int intValue;
	public String stringValue;

	public Dupla (String stringValue){
		this.stringValue = stringValue;
		this.intValue = Utils.getReferenceAsInt(stringValue);
	}

	@Override
	public int compareTo(Dupla otraDupla) {
		if(this.intValue < otraDupla.intValue){
			return -1;
		}
		else if(this.intValue > otraDupla.intValue){
			return 1;
		}

		return this.stringValue.compareTo(otraDupla.stringValue);
	}
}