/**
 * Project    : Repasando los Kanji
 * Created on : 21 octubre 2011
 */

package com.konnichiwamundo.repasandoloskanji.controller;

import com.konnichiwamundo.repasandoloskanji.model.RomanjiKanaConversionTable;
import com.konnichiwamundo.repasandoloskanji.persistence.ClipboardTools;


/**
 * Esta clase ofrece varios métodos para realizar conversiones entre palabras
 * en romaji, su representación en katakana y su representación en valores
 * unicode.
 * 
 * @author Carlos Llongo
 *
 */
public class JapaneseConversionTools {
	private RomanjiKanaConversionTable romajiKataTable;
	
	private String kanaType;

	/**
	 * Convierte una palabra en romaji a su representación en valores hiragana.
	 * 
	 * @param romaji La palabra en formato romaji.
	 * @return La palabra en formato hiragana.
	 */
	public String convertRomajiToJapanese(String romaji, String kanaType) {
		if(romajiKataTable == null || this.kanaType != kanaType){
			this.kanaType = kanaType;
			romajiKataTable = new RomanjiKanaConversionTable(kanaType);
		}
		
		String unicode = convertRomajiToUnicode(romaji);
		
		return getJapaneseFromUnicodeString(unicode);
	}
	
	/**
	 * Convierte una palabra en romaji a su representación en formato unicode.
	 * 
	 * @param romaji La palabra en formato romaji.
	 * @return La palabra japonesa en formato unicode.
	 */
	private String convertRomajiToUnicode(String romaji){
		
		StringBuilder unicode = new StringBuilder();
		int index = 0;
		String sound;
		int offset = 1;
		String silaba;
		
		while(index < romaji.length()){
			// Comprobamos sonido de 1 letra
			silaba = romaji.substring(index, index + 1);
			if(silaba.equals("n") && (index + 1) < romaji.length() && isAVowel(romaji.charAt(index + 1))){
				sound = null;
			}
			else{
				sound = romajiKataTable.getUnicodeForSound(romaji.substring(index, index + 1));
			}			
			if(sound == null){
				sound = romajiKataTable.getUnicodeForSound(romaji.substring(index, index + 2));
				if(sound == null){
					sound = romajiKataTable.getUnicodeForSound(romaji.substring(index, index + 3));
					offset = 3;
				}
				else{
					offset = 2;
				}
			}
			
			if(sound == null && !silaba.equals(" ")){
				return "romaji is not valid";
			}
			
			if(!silaba.equals(" ")){
				unicode.append(sound);
			}
			else{
				offset = 1;
			}
			
			index += offset;
			offset = 1;
		}
		
		
		
		return unicode.toString();
	}
	
	/**
	 * Comprueba si el caracter es una vocal. Consideramos la 'y' también como
	 * vocal.
	 * 
	 * @param c El caracter a comprobar
	 * @return true si es una vocal, false en caso contrario
	 */
	private boolean isAVowel(char c) {
		return c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u' || c == 'y';
	}
	
	/**
	 * Obtiene un String de caracteres japoneses a partir de un String de
	 * valores Unicode.
	 * 
	 * @param unicodeValues Los valores Unicode que representan caracteres japoneses
	 * @return Un String de caracteres japoneses.
	 */
	public static String getJapaneseFromUnicodeString(String unicodeValues){
		String [] unicodeValuesArray = unicodeValues.split("(?<=\\G.{6})");
		
		StringBuilder japanese = new StringBuilder();
		for(String unicodeValue : unicodeValuesArray){
			japanese.append((char)Integer.parseInt(unicodeValue.substring(2), 16));
		}
		
		return japanese.toString();
	}
	
	/**
	 * Convierte un texto en japones a su representación en valores unicode.
	 * 
	 * @param japaneseText El texto japones
	 * @return El texto representado en formato unicode.
	 */
	public String convertJapaneseToUnicode(String japaneseText){
		String unicode = "";
		
		for(int i = 0; i < japaneseText.length(); i++){
			unicode += "\\u" + Integer.toHexString(japaneseText.charAt(i) | 0x10000).substring(1);
		}
		
		return unicode;
	}
	
	/**
	 * Obtiene el texto japones del portapapeles y lo convierte a valores
	 * unicode. Si existe un salto de linea, separa los datos con dos puntos ":"
	 * 
	 * @return El texto japones del portapapeles convertidoa valores unicode
	 */
	public String obtaintTextFromClipboardAndConvert() {
		String clipboardText = ClipboardTools.getTextFromClipboard();
		
		int lineJumpIndex = clipboardText.indexOf("\n");
		
		if(lineJumpIndex == -1){
			return convertJapaneseToUnicode(clipboardText);
		}
		else{	
			String kana = clipboardText.substring(0, lineJumpIndex);
			String kanji = clipboardText.substring(lineJumpIndex + 1);
			
			return convertJapaneseToUnicode(kanji)
					+ ":" + convertJapaneseToUnicode(kana);
		}
	}
	
	/**
	 * Obtiene el primer caracter kanji del texto japones en el portapapeles.
	 * 
	 * @return El primer kanji del portapapeles.
	 */
	public char getKanjiCharacterFromClipboard(){
		String clipboardText = ClipboardTools.getTextFromClipboard();
		if(clipboardText.length() == 0){
			return '#';
		}
		
		if(clipboardText.length() == 1){
			return clipboardText.charAt(0);
		}
		
		int lineJumpIndex = clipboardText.indexOf("\n");
		
		if(lineJumpIndex == -1){
			return clipboardText.charAt(0);
		}
		else{	
			return clipboardText.substring(lineJumpIndex + 1).charAt(0);
		}
	}
}
