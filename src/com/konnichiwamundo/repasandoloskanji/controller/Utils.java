/**
 * Project    : Repasando los Kanji
 * Created on : 4 diciembre 2011
 */

package com.konnichiwamundo.repasandoloskanji.controller;

import java.awt.Color;

/**
 * Clase con herramientas útiles para todo tipo de conversiones, desde tipos de
 * datos hasta entre formatos (p.e. de japonés a unicode).
 * 
 * @author Carlos Llongo
 *
 */

public class Utils {
	
	/**
	 * Convierte el String a un Boolean. Cuando el String representa el número
	 * "1" se considera true. En cualquier otro caso se considera false.
	 * 
	 * @param boolInt El String a convertir
	 * @return Un Boolean representando el String
	 */
	public static Boolean stringToBoolean(String boolInt) {
		if(boolInt.equals("1")){
			return new Boolean(true);
		}
		
		return new Boolean(false);
	}
	
	/**
	 * Convierte un Boolean a una representación binaria, siendo el 1 true y el
	 * 0 false.
	 * 
	 * @param aBoolean el Boolean a convertir
	 * @return 1 si el Boolean es true, 0 si el Boolean es false
	 */
	public static int booleanToInt(Boolean aBoolean) {
		return aBoolean? 1 : 0;
	}
	/**
	 * Devuelve un color rojo suave para el color de fondo de los campos de
	 * texto que tengan un error.
	 * 
	 * @return Un color rojo suave.
	 */
	public static Color getRedColor() {
		return new Color(255,215,215);
	}
	
	/**
	 * Obtiene la parte numérica de la referencia y la convierte a un entero.
	 * 
	 * @param reference La referencia de la que obtener el valor numérico.
	 * @return El valor numérico de la referencia.
	 */
	public static int getReferenceAsInt(String reference){
		if(Character.isLetter(reference.charAt(reference.length() - 1))){
			return Integer.parseInt(reference.substring(0, reference.length() - 1));
		}
		
		return Integer.parseInt(reference);
	}
}
