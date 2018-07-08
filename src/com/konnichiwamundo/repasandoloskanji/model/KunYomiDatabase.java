/**
 * Project    : Repasando los Kanji
 * Created on : 15 diciembre 2011
 */

package com.konnichiwamundo.repasandoloskanji.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

import com.konnichiwamundo.repasandoloskanji.controller.JapaneseConversionTools;
import com.konnichiwamundo.repasandoloskanji.controller.Log;
import com.konnichiwamundo.repasandoloskanji.controller.Utils;

/**
 * Modeliza la base de datos de lecturas de kun-yomi.
 * 
 * @author Carlos Llongo
 *
 */
public class KunYomiDatabase {

	private HashMap<String, KunYomiReading> database;
	private HashMap<String, Boolean> learnedList;

	private Log log = new Log();
	
	private boolean isSaveNecessary;

	public KunYomiDatabase(){
		isSaveNecessary = false;
		
		initDatabase();

		initLearnedList();

		log.debug("Kun-yomi database size: " + database.size());
	}

	/**
	 * Inicializa la base de datos de lecturas con la información que se encuentra
	 * en el fichero de texto de lecturas.
	 */
	public void initDatabase(){
		database = new HashMap<String, KunYomiReading>();

		String applicationPath = System.getProperty("user.dir");
		File readingsFile = new File(applicationPath + "/data/kun_yomi_readings.txt");
		BufferedReader in = null;
		String book1Reference = "";

		try{
			in = new BufferedReader(new InputStreamReader(new FileInputStream(readingsFile),"UTF-8"));

			String fileLine;
			String [] tokens;
			KunYomiReading kunYomiReading;

			while((fileLine = in.readLine()) != null){
				if(!fileLine.startsWith("#")){
					tokens = fileLine.split("\\:");
					
					book1Reference = tokens[0];

					kunYomiReading = new KunYomiReading();
					kunYomiReading.setBook1Reference(book1Reference);
					kunYomiReading.setRootWordAndInflection(JapaneseConversionTools.getJapaneseFromUnicodeString(tokens[1]));
					kunYomiReading.setKunYomi(JapaneseConversionTools.getJapaneseFromUnicodeString(tokens[2]));
					kunYomiReading.setMeaning(tokens[3]);

					database.put(kunYomiReading.getBook1Reference(), kunYomiReading);
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
			System.out.println("ERROR on kanji: " + book1Reference);
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
	 * Inicializa la lista de lecturas que han sido aprendidas.
	 */
	private void initLearnedList() {
		learnedList = new HashMap<String, Boolean>();

		String applicationPath = System.getProperty("user.dir");
		File readingsFile = new File(applicationPath + "/data/learned_kun_yomi.txt");
		BufferedReader in = null;

		try{
			in = new BufferedReader(new InputStreamReader(new FileInputStream(readingsFile),"UTF-8"));

			String fileLine;
			String [] tokens;

			while((fileLine = in.readLine()) != null){
				if(!fileLine.startsWith("#")){
					tokens = fileLine.split("\\:");
					learnedList.put(tokens[0], 
							Utils.stringToBoolean(tokens[1]));
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
	 * Guarda a un fichero de texto las lecturas kun-yomi que han sido
	 * aprendidas. Solo se realiza este guardado en caso de haber alguna
	 * modificación.
	 */
	public void saveLearnedList() {
		if(!isSaveNecessary){
			return;
		}
		
		String applicationPath = System.getProperty("user.dir");
		File readingsFile = new File(applicationPath + "/data/learned_kun_yomi.txt");
		BufferedWriter out = null;

		try{
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(readingsFile),"UTF-8"));
			Vector<String> learnedKeys = new Vector<String>(learnedList.keySet());
			Vector<Dupla> learnedDuplas = new Vector<Dupla>();
			
			for(String key : learnedKeys){
				learnedDuplas.add(new Dupla(key));
			}
			
			Collections.sort(learnedDuplas);
			
			for(int i = 0; i < learnedDuplas.size(); i++){
				if(i != 0){
					out.newLine();
				}
				out.write(learnedDuplas.get(i).stringValue + ":" 
				+ Utils.booleanToInt(
						learnedList.get(learnedDuplas.get(i).stringValue)));
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			if(out != null){
				try{
					out.close();
				}
				catch (Exception e) {
					// No hacer nada
				}
			}
		}
		
		isSaveNecessary = false;
	}

	/**
	 * Realiza una busqueda en la base de datos de todas las lecturas que
	 * comienzan por la cadena indicada.
	 * 
	 * @param searchString La cadena de busqueda.
	 * @return Los kun-yomi que empiezan por la cadena de búsqueda.
	 */
	public Vector<KunYomiReading> searchFor(String searchString) {
		Vector<String> keys = new Vector<String>(database.keySet());

		Vector<KunYomiReading> searchResults = new Vector<KunYomiReading>();

		KunYomiReading kunYomiReading;
		for(String key : keys){
			kunYomiReading = database.get(key);
			if(kunYomiReading.getKunYomi().startsWith(searchString)){
				searchResults.add(kunYomiReading);
			}
		}

		Collections.sort(searchResults);
		return searchResults;
	}
	
	/**
	 * Obtiene todos los kun-yomi de la base de datos.
	 * 
	 * @return Todos los kun-yomi, ordenados por número de heisig.
	 */
	public Vector<KunYomiReading> getAllKunYomi() {
		Vector<String> keys = new Vector<String>(database.keySet());

		Vector<KunYomiReading> allKunYomi = new Vector<KunYomiReading>();

		for(String key : keys){
			allKunYomi.add(database.get(key));
		}
		
		Collections.sort(allKunYomi);
		return allKunYomi;
	}
	
	/**
	 * Obtiene todos los kun-yomis para un determinado valor heisig.
	 * 
	 * @param heisigNumber El valor heisig
	 * @return Un vector con todas las lecturas kun-yomi correspondientes.
	 */
	public Vector<KunYomiReading> getKunYomiForHeisig(int heisigNumber){
		boolean hasMoreSons = true;
		String alphabet = "abcdefghijklmnopqrstuvwxyz";
		int index = 0;
		Vector<KunYomiReading> existingKunYomi = new Vector<KunYomiReading>();
		KunYomiReading reading = database.get(String.valueOf(heisigNumber));
		if(reading == null){
			while(hasMoreSons){
				String kunYomiId = String.valueOf(heisigNumber) + alphabet.charAt(index);
				reading = database.get(kunYomiId);
				if(reading != null){
					log.debug("recuperado kun-yomi " + reading.getBook1Reference());
					existingKunYomi.add(reading);
					index++;
				}
				else{
					hasMoreSons = false;
				}
			}
			hasMoreSons = true;
			index = 0;
		}
		else{
			existingKunYomi.add(reading);
		}
		
		return existingKunYomi;
	}
	
	/**
	 * Obtiene las lecturas kun-yomi aprendidas para un número Heisig
	 * determinado.
	 * 
	 * @param heisigNumber El número Heisig de las lecturas a recuperar
	 * @return Las lecturas kun-yomi aprendidas para el número Heisig.
	 */
	public Vector<KunYomiReading> getLearnedKunYomiForHeisig(int heisigNumber){
		boolean hasMoreSons = true;
		String alphabet = "abcdefghijklmnopqrstuvwxyz";
		int index = 0;
		Vector<KunYomiReading> learnedKunYomi = new Vector<KunYomiReading>();
		KunYomiReading reading = database.get(String.valueOf(heisigNumber));
		if(reading == null){
			while(hasMoreSons){
				String kunYomiId = String.valueOf(heisigNumber) + alphabet.charAt(index);
				reading = database.get(kunYomiId);
				if(reading != null){
					log.debug("recuperado kun-yomi " + reading.getBook1Reference());
					if(isLearned(reading.getBook1Reference())){
						learnedKunYomi.add(reading);
					}
					index++;
				}
				else{
					hasMoreSons = false;
				}
			}
			hasMoreSons = true;
			index = 0;
		}
		else{
			if(isLearned(reading.getBook1Reference())){
				learnedKunYomi.add(reading);
			}
		}
		
		return learnedKunYomi;
	}

	/**
	 * Comprueba sí el kun-yomi ha sido aprendido.
	 * 
	 * @param kunYomiReadingId El identificador de la lectura kun-yomi
	 * @return True si ha sido aprendido, false en caso contrario
	 */
	public boolean isLearned(String kunYomiReadingId){
		return learnedList.get(kunYomiReadingId) != null;
	}
	
	/**
	 * Añade una lectura kun-yomi a la lista de lecturas aprendidas.
	 * 
	 * @param reference El identificador de la lectura kun-yomi.
	 */
	public void addLearned(String reference){		
		if(learnedList.get(reference) == null){
			learnedList.put(reference, new Boolean(false));
			isSaveNecessary = true;
		}
	}

	/**
	 * Elimina una lectura kun-yomi de la lista de lecturas aprendidas.
	 * 
	 * @param reference El identificador de la lectura kun-yomi.
	 */
	public void removeLearned(String reference) {
		if(learnedList.get(reference) != null){
			learnedList.remove(reference);
			isSaveNecessary = true;
		}
	}
	
	/**
	 * Obtiene un listado de los kanji de los que se han aprendido nuevas
	 * lecturas kun-yomi, y que por tando deben ser añadidos a la lección.
	 * 
	 * @return Un listado de loa kanji con nuevos kun-yomi aprendidos.
	 */
	public Vector<String> getHeisigWithNewKunYomi(){
		Vector<String> heisigWithNewKunYomi = new Vector<String>();
		Vector<String> learnedKeys = new Vector<String>(learnedList.keySet());
		boolean isReviewed;
		for(String key : learnedKeys){
			isReviewed = learnedList.get(key).booleanValue(); 
			if(!isReviewed){
				if(!heisigWithNewKunYomi.contains(removeSubLetter(key))){
					heisigWithNewKunYomi.add(removeSubLetter(key));
				}
				learnedList.put(key, new Boolean(true));
			}
		}
		
		if(!heisigWithNewKunYomi.isEmpty()){
			isSaveNecessary = true;
		}
		
		return heisigWithNewKunYomi;
	}
	
	/**
	 * Obtiene la parte del string que representa un entero y devuelve solo
	 * esa parte. Por ejemplo, del valor 1548b, se obtendría el string 1548.
	 * 
	 * @param book1Reference El string del que eliminar la sub-letra
	 * @return El string sin la sub-letra, en caso de que exista.
	 */
	public String removeSubLetter(String book1Reference){
		return String.valueOf(Utils.getReferenceAsInt(book1Reference));
	}

	/**
	 * Obtiene el listado de los kun-yomi en la base de datos que aún no han
	 * sido aprendidos.
	 * 
	 * @return Los kun-yomi de la base de datos que no han sido aprendidos.
	 */
	public Vector<String> getNotLearnedList() {
		Vector<String> notLearned = new Vector<String>();
		Vector<String> keys = new Vector<String>(database.keySet());
		
		for(String key : keys){
			if(learnedList.get(key) == null){
				notLearned.add(key);
			}
		}
		
		return notLearned;
	}
}
