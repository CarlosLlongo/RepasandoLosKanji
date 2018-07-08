/**
 * Project    : Repasando los Kanji
 * Created on : 17 agosto 2011
 */

package com.konnichiwamundo.repasandoloskanji.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

import com.konnichiwamundo.repasandoloskanji.model.KanjiStatistics;
import com.konnichiwamundo.repasandoloskanji.model.KunYomiDatabase;
import com.konnichiwamundo.repasandoloskanji.model.Reading;
import com.konnichiwamundo.repasandoloskanji.model.ReadingsDatabase;
import com.konnichiwamundo.repasandoloskanji.model.Writing;
import com.konnichiwamundo.repasandoloskanji.model.WritingsDatabase;

/**
 * Clase encargada de generar el repaso diario.
 * 
 * @author Carlos Llongo
 *
 */
public class ReviewsControlCenter {
	
	private static final int TOTAL_REVIEW_SIZE = 100;
	
	private WritingsDatabase writingsDb;
	private ReadingsDatabase readingsDb;
	private Vector<Writing> reviewKanjis;
	private int reviewPosition;
	private int toTodayReading;
	
	private Log log = new Log();
	
	public ReviewsControlCenter(WritingsDatabase writingsDb, ReadingsDatabase readingsDb) {
		this.writingsDb = writingsDb;
		this.readingsDb = readingsDb;
		this.reviewPosition = 0;
	}
	
	/**
	 * Obtiene un Vector con los registros de los kanji que deben ser repasados.
	 * 
	 * @param toYesterdayWriting Escrituras aprendidas hasta ayer
	 * @param toTodayWriting Escrituras aprendidas hasta hoy
	 * @param toYesterdayReading Lecturas aprendidas hasta ayer
	 * @param toTodayReading Lecturas aprendidas hasta hoy
	 * @param mazoCC El control del mazo con las dificultades de los kanji
	 * @return El Vector con los registros a repasar
	 */
	public void initializeReview(int toYesterdayWriting,
			int toTodayWriting, int toYesterdayReading, int toTodayReading, 
			MazoControlCenter mazoCC, KanjiStatistics statistics,
			KunYomiDatabase kunYomiDb, boolean reviewLastLearned,
			int lastWritingsLearnedCount, int lastReadingsLearnedCount) {
		
		this.toTodayReading = toTodayReading;
		
		Vector<String> todayReview;
		int yesterdayLearnedWritings = toTodayWriting - toYesterdayWriting;
		int yesterdayLearnedReadings = toTodayReading - toYesterdayReading;
		log.debug("Kanji aprendidos ayer: " + yesterdayLearnedWritings);
		log.debug("Lecturas aprendidas ayer: " + yesterdayLearnedReadings);

		int neededKanji;
		if(yesterdayLearnedWritings > 0){
			neededKanji = TOTAL_REVIEW_SIZE - yesterdayLearnedWritings;
		}
		else{
			neededKanji = TOTAL_REVIEW_SIZE;
		}
		log.debug("Kanji necesarios para hoy: " + neededKanji);		
		
		todayReview = mazoCC.getTodayReview(neededKanji);
		
		// Añadimos las escrituras aprendidas ayer
		for(int i = toYesterdayWriting + 1; i <= toTodayWriting; i++){
			todayReview.add(String.valueOf(i));
		}
		
		// Añadimos las lecturas aprendidas ayer
		addReadings(toYesterdayReading + 1, toTodayReading, todayReview);
		
		// Si ayer no aprendimos ninguno, repasamos los 10 últimos a no se que
		// ya no queda ninguno por aprender!!! :D
		if(yesterdayLearnedWritings == 0 && toTodayWriting < IntegrityChecker.VOL1_WRITINGS
				&& reviewLastLearned){
			int startingIndex = toTodayWriting - (lastWritingsLearnedCount - 1);
			for(int i = startingIndex; i <= toTodayWriting; i++){
				if(!todayReview.contains(String.valueOf(i))){
					todayReview.add(String.valueOf(i));
				}
			}
		}
		
		// Si no hemos aprendido ninguna lectura desde el último repaso, añadimos
		// las 10 últimas lecturas.
		// TODO: Cuando se saben menos de 10 lecturas y cuando se saben todas
		if(yesterdayLearnedReadings == 0 && toTodayReading != 0 && reviewLastLearned){
			int startingIndex = toTodayReading - (lastReadingsLearnedCount - 1);
			addReadings(startingIndex, toTodayReading, todayReview);
		}
		
		// Añadimos los kanji nuevos que aparecen por primera vez como parte de
		// un compuesto, y por tanto no se añaden de forma automática con las
		// lecturas.
		Vector<Integer> readingsLinks = writingsDb.getReadingsLinks();
		Collections.sort(readingsLinks);
		
		if(!readingsLinks.isEmpty()){
			int index = 0;
			
			while(index < readingsLinks.size() && readingsLinks.get(index) <= toTodayReading){
				int heisig = writingsDb.getHeisigFromReadingsLinks(readingsLinks.get(index));
				if(!statistics.hasStatistics(heisig)){
					todayReview.add(String.valueOf(heisig));
				}
				
				index++;
			}
		}
		
		// Añadimos los nuevos kun-yomi aprendidos
		Vector<String> heisigWithNewKunYomi = kunYomiDb.getHeisigWithNewKunYomi();
		log.debug("HeisigWithNewKunYomi: " + heisigWithNewKunYomi.size());
		for(String heisig : heisigWithNewKunYomi){
			if(!todayReview.contains(heisig)){
				todayReview.add(heisig);
			}
		}
		
		log.debug("Kanji a repasar hoy: " + todayReview.size());
		
		searchForInconsitencies(todayReview);
		
		reviewKanjis = new Vector<Writing>();
		log.debug("Kanji en la base de datos: " + writingsDb.getSize());
		log.debug("Progreso escritura: " 
				+ ((writingsDb.getNumberOfLearnedWritings(toTodayWriting) * 100) / IntegrityChecker.VOL3_WRITINGS) + "%");
		log.debug("Progreso lecturas: " 
				+ ((readingsDb.getNumberOfLearnedReadings(toTodayReading) * 100) / IntegrityChecker.VOL3_READINGS) + "%");
		
//		todayReview.add(0, "1713");
		
		Writing hr;
		for(String heisigNumber : todayReview){
			hr = writingsDb.getByHeisigNumber(heisigNumber);
			if(hr != null){
				reviewKanjis.add(hr);
			}
			else{
				log.debug("No se encontró el registro: " + heisigNumber);
			}			
		}
		
		log.debug("Kanji extraidos de la BD: " + reviewKanjis.size());
		
		Collections.shuffle(reviewKanjis);
	}
	
	/**
	 * Añade las lecturas en el rango dado, teniendo en cuenta que las lecturas
	 * no solo están representadas por un valor numérico, sí no que además
	 * una lectura puede incluir varios kanji, que se representarían por números
	 * p.e: 1034a, 1034b, 1034c
	 * 
	 * @param startingReading Inicio del rango de las lecturas
	 * @param endingReading Fin del rango de las lecturas
	 * @param review El repaso al que añadir los kanji con lecturas
	 */
	private void addReadings(int startingReading, int endingReading, Vector<String> review){
		String book1Ref;
		String book2Ref;
		Reading reading;
		boolean hasMoreSons = true;
		String alphabet = "abcdefghijklmnopqrstuvwxyz";
		int index = 0;
		for(int i = startingReading; i <= endingReading; i++){
			book2Ref = String.valueOf(i);
			reading = readingsDb.getByReading(book2Ref);
			if(reading == null){
				log.debug("book2Ref " + book2Ref + " tiene varias lecturas o ninguna.");
				while(hasMoreSons){
					reading = readingsDb.getByReading(book2Ref + alphabet.charAt(index));
					if(reading != null){
						log.debug("recuperada lectura " + reading.getBook2reference());
						book1Ref = reading.getBook1reference();
						if(!review.contains(book1Ref)){
							review.add(book1Ref);
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
				book1Ref = reading.getBook1reference();
				if(!review.contains(book1Ref)){
					review.add(book1Ref);
				}
			}
		}
	}
	
	public int getToTodayReading(){
		return toTodayReading;
	}
	
	/**
	 * Busca inconsistencias al comprobar si un kanji se ha añadido dos veces
	 * a la lección
	 * 
	 * @param todayReview El listado de kanjis a repasar
	 */
	private void searchForInconsitencies(Vector<String> todayReview) {
		HashMap<String, String> hm = new HashMap<String, String>();
		
		log.debug("Buscando kanji repetidos en la lección...");
		String found;
		for(String kanji : todayReview){
			found = hm.get(kanji);
			if(found != null){
				log.debug("Inconsistencia!!!: " + kanji);
			}
			else{
				hm.put(kanji, kanji);
			}
		}
	}
	
	/**
	 * Devuelve el próximo kanji que debe ser revisado y pasa a la siguiente
	 * posición.
	 * 
	 * @return El próximo kanji a revisar
	 */
	public Writing next() {
		if (this.reviewPosition < this.reviewKanjis.size()) {
			return this.reviewKanjis.elementAt(this.reviewPosition++);
		}
		
		return null;
	}
	
	public int getReviewPosition(){
		return reviewPosition;
	}
	
	public int getReviewSize(){
		return reviewKanjis.size();
	}
	
	/**
	 * Comprueba si se ha llegado al último kanji de la lección.
	 * 
	 * @return true si se ha llegado al último kanji, false en caso contrario.
	 */
	public boolean isLastKanjiInReview(){
		return reviewPosition == reviewKanjis.size();
	}
}
