/**
 * Project    : Repasando los Kanji
 * Created on : 21 octubre 2011
 */

package com.konnichiwamundo.repasandoloskanji.persistence;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * Esta clase contiene métodos que permiten interactuar con el contenido del
 * portapapeles.
 * 
 * @author Carlos Llongo
 *
 */
public class ClipboardTools {

	/**
	 * Obtiene el contenido del portapapeles en formato texto.
	 * 
	 * @return El contenido del portapeles en formato texto.
	 */
	public static String getTextFromClipboard(){
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		String clipboardText = "";
		
		try {
			clipboardText = (String)clipboard.getContents("")
					.getTransferData(DataFlavor.stringFlavor);
		} catch (UnsupportedFlavorException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return clipboardText;
	}
	
	/**
	 * Almacena el texto en el portapeles.
	 * 
	 * @param text El texto que se desea almacenar en el portapapeles.
	 */
	public static void sendTextToClipboard(String text) {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		StringSelection transferText = new StringSelection(text);
		clipboard.setContents(transferText, transferText);
		
	}
}
