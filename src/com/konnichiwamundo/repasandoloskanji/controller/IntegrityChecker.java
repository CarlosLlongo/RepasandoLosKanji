/**
 * Project    : Repasando los Kanji
 * Created on : 30 agosto 2011
 */

package com.konnichiwamundo.repasandoloskanji.controller;

import java.util.HashMap;
import java.util.Vector;

import com.konnichiwamundo.repasandoloskanji.model.KanjiStatistics;
import com.konnichiwamundo.repasandoloskanji.model.KunYomiDatabase;
import com.konnichiwamundo.repasandoloskanji.model.Mazo;

/**
 * Herramientas para la comprobación de la integridad de los distintos módulos
 * del programa.
 * 
 * @author Carlos Llongo
 *
 */
public class IntegrityChecker {
	
	private Log log = new Log();
	public final static int VOL1_WRITINGS = 2042;
	public final static int VOL2_READINGS = 2243;
	public final static int VOL3_WRITINGS = 3000;
	public final static int VOL3_READINGS = 3201;
	public final static int VOL2_KANJI_WITHOUT_READINGS = 86;
	public final static int VOL2_LESSON5_START = 666;
	
	private int toYesterday;
	private KanjiStatistics statistics;
	private MazoControlCenter mazoCC;
	private KunYomiDatabase kunYomiDb;
	
	public IntegrityChecker(int toYesterday, KanjiStatistics statistics, 
			MazoControlCenter mazoCC, KunYomiDatabase kunYomiDb){
		
		this.toYesterday = toYesterday;
		this.statistics = statistics;
		this.mazoCC = mazoCC;
		this.kunYomiDb = kunYomiDb;
	}

	/**
	 * Comprueba la integridad de los mazos utilizando varios métodos, como el
	 * tamaño o el contenido de los mazos. También se encarga de inicializar los
	 * mazos cuando el programa se ejecuta por primera vez.
	 * 
	 * @param toYesterday El último kanji aprendido.
	 */
	private void checkMazoIntegrity() {
		
		int totalKanjiInDecks = 0;
		totalKanjiInDecks += mazoCC.getEasyDeck().getStoreSize();
		totalKanjiInDecks += mazoCC.getMediumDeck().getStoreSize();
		totalKanjiInDecks += mazoCC.getHardDeck().getStoreSize();
		
		log.debug("Kanji en los mazos: " + totalKanjiInDecks);
		
		if(toYesterday < VOL1_WRITINGS){
			if(toYesterday != totalKanjiInDecks){
				log.debug("Aprendidos hasta ayer: " + toYesterday);
				log.debug("Existe una inconsistencia! " + totalKanjiInDecks 
						+ " != " + toYesterday);
			}
		}
		else{
			if(statistics.getKanjiCount() != totalKanjiInDecks){
				log.debug("Kanji con estadisticas: " + statistics.getKanjiCount());
				log.debug("Existe una inconsistencia! " + totalKanjiInDecks 
						+ " != " + statistics.getKanjiCount());
			}
		}
		
		log.debug("Buscando inconsistencias...");
		HashMap<String,String> hm = new HashMap<String,String>();
		searchForInconsitencies(mazoCC.getEasyDeck(), hm);
		searchForInconsitencies(mazoCC.getMediumDeck(), hm);
		searchForInconsitencies(mazoCC.getHardDeck(), hm);
		
		log.debug("Escaneados " + hm.size() + " elementos.");
		
	}
	
	/**
	 * Analiza el contenido de un mazo en busca de inconsistencias, en este caso
	 * que exista algun caracter repetido.
	 * 
	 * @param mazo El mazo en el que buscar inconsistencias
	 * @param hm Un HashMap con los kanji ya encontrados en otros mazos
	 */
	private void searchForInconsitencies(Mazo mazo, HashMap<String, String> hm){
		int[] kanji = mazo.storeToArray();
		
		String found;
		for(int i : kanji){
			found = hm.get(String.valueOf(i));
			if(found != null){
				log.debug("Inconsistencia!!!: " + found);
			}
			else{
				hm.put(String.valueOf(i), String.valueOf(i));
			}
		}
	}

	/**
	 * Este es el método encargado de lanzar las distintas pruebas que comprueben
	 * la integridad de los mazos.
	 */
	public void startIntegrityCheck() {
		checkMazoIntegrity();
		Vector<String> notLearned = kunYomiDb.getNotLearnedList();
		log.debug("Kun-yomi no aprendidos: " + notLearned.size());
	}
}
