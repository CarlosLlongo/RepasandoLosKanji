/**
 * Project    : Repasando los Kanji
 * Created on : 8 diciembre 2011
 */

package com.konnichiwamundo.repasandoloskanji.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Esta clase modeliza una tabla de conversión de silabas en romaji a su
 * correspondiente caracter japones en formato unicode.
 * 
 * @author Carlos Llongo
 *
 */
public class RomanjiKanaConversionTable {	
	private HashMap<String, String> conversionTable;
	private String kanaType;

	public RomanjiKanaConversionTable(String kanaType){
		this.kanaType = kanaType;
		
		conversionTable = new HashMap<String, String>();

		loadFromFile();
	}

	/**
	 * Carga en la tabla todas las conversiones que se encuentran en el fichero
	 * de texto.
	 */
	private void loadFromFile() {
		String applicationPath = System.getProperty("user.dir");
		String fileName = "romanji_" + kanaType + "_table.txt";
		File romajiKanaFile = new File(applicationPath + "/data/" + fileName);
		BufferedReader in = null;

		try{
			in = new BufferedReader(new InputStreamReader(new FileInputStream(romajiKanaFile),"UTF-8"));

			String fileLine;
			String [] tokens;

			while((fileLine = in.readLine()) != null){
				if(!fileLine.startsWith("#")){
					tokens = fileLine.split("\\:");
					
					conversionTable.put(tokens[0], tokens[1]);
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			if(in != null){
				try{
					in.close();
				}
				catch (Exception e) {
					// No hacer nada
				}
			}
		}
	}
	
	/**
	 * Obtiene el katakana correspondiente a la silaba en formato romaji que se
	 * obtiene como parámetro.
	 * 
	 * @param sound La silaba en formato romaji
	 * @return El caracter japonés asociado a la silaba.
	 */
	public char getKanaForSound(String sound){
		String unicode = conversionTable.get(sound);
		if(unicode == null){
			return ' ';
		}
		
		return (char)Integer.parseInt(sound.substring(2), 16);
	}
	
	/**
	 * Obtiene el valor unicode del katakana asociado a la silaba en formato
	 * romaji que se obtiene como parámetro.
	 * 
	 * @param sound La silaba en formato romaji
	 * @return El valor unicode del caracter japonés asociado a la silaba.
	 */
	public String getUnicodeForSound(String sound){
		return conversionTable.get(sound);
	}
}
