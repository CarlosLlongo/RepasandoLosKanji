/**
 * Project    : Repasando los Kanji
 * Created on : 1 diciembre 2011
 */

package com.konnichiwamundo.repasandoloskanji.controller;

import java.io.IOException;

import javax.swing.JOptionPane;

import com.konnichiwamundo.repasandoloskanji.model.KanjiStatistics;
import com.konnichiwamundo.repasandoloskanji.model.KunYomiDatabase;
import com.konnichiwamundo.repasandoloskanji.persistence.UserData;

/**
 * Clase con herramientas para almacenar el estado actual del programa.
 * 
 * @author Carlos Llongo
 *
 */
public class StatusTools {

	private UserData preferences;
	private MazoControlCenter mazoCC;
	private KanjiStatistics statistics;
	private String writingsLearned;
	private KunYomiDatabase kunYomiDb;
	private int lastWritingsLearnedCount;
	private int lastReadingsLearnedCount;

	private String readingsLearned;
	
	public StatusTools(UserData preferences, MazoControlCenter mazoCC,
			KanjiStatistics statistics, KunYomiDatabase kunYomiDb){
		
		this.preferences = preferences;
		this.mazoCC = mazoCC;
		this.statistics = statistics;
		this.kunYomiDb = kunYomiDb;
	}
	
	/**
	 * Almacena diversos elementos del estado de la aplicación a ficheros de
	 * texto, como las preferencias, las estadísticas o los kun-yomi
	 * aprendidos.
	 * 
	 */
	public void saveStatus() {
		try {
			preferences.setUserData("number_of_writings_learned", writingsLearned);
			preferences.setUserData("number_of_readings_learned", readingsLearned);
			preferences.setUserData("last_writings_learned_cout", String.valueOf(lastWritingsLearnedCount));
			preferences.setUserData("last_readings_learned_cout", String.valueOf(lastReadingsLearnedCount));
			preferences.setIntArray("easy_remaining_kanji", mazoCC.getEasyDeck().remainingToArray());
			preferences.setIntArray("medium_remaining_kanji", mazoCC.getMediumDeck().remainingToArray());
			preferences.setIntArray("hard_remaining_kanji", mazoCC.getHardDeck().remainingToArray());
			preferences.setIntArray("easy_store_kanji", mazoCC.getEasyDeck().storeToArray());
			preferences.setIntArray("medium_store_kanji", mazoCC.getMediumDeck().storeToArray());
			preferences.setIntArray("hard_store_kanji", mazoCC.getHardDeck().storeToArray());
			
			preferences.commitToFile();
		}
		catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Unable to save prefereces.",
					"Error", 0);
		}
		
		statistics.toFile();
		
		kunYomiDb.saveLearnedList();
	}
	
	public void setWritingsLearned(String writingsLearned) {
		this.writingsLearned = writingsLearned;
	}

	public void setReadingsLearned(String readingsLearned) {
		this.readingsLearned = readingsLearned;
	}

	public void setReviewStatus(int toYesterdayWriting, int toTodayWritings,
			int toYesterdayReading, int toTodayReadings,
			int lastWritingsLearnedCount, int lastReadingsLearnedCount) {
		int yesterdayLearnedWritings = toTodayWritings - toYesterdayWriting;
		int yesterdayLearnedReadings = toTodayReadings - toYesterdayReading;
		
		if(yesterdayLearnedWritings > 0){
			this.lastWritingsLearnedCount = yesterdayLearnedWritings;
		}
		else{
			this.lastWritingsLearnedCount = lastWritingsLearnedCount;
		}
		
		if(yesterdayLearnedReadings > 0){
			this.lastReadingsLearnedCount = yesterdayLearnedReadings;
		}
		else{
			this.lastReadingsLearnedCount = lastReadingsLearnedCount;
		}
		
	}
}
