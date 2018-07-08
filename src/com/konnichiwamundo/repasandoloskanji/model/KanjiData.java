/**
 * Project    : Repasando los Kanji
 * Created on : 13 enero 2011
 */

package com.konnichiwamundo.repasandoloskanji.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

import com.konnichiwamundo.repasandoloskanji.controller.MazoControlCenter;
import com.konnichiwamundo.repasandoloskanji.controller.Utils;

/**
 * Representa los datos de un Kanji estudiado, principalmente los tiempos de
 * escritura realizados y los aciertos y fallos en las lecturas.
 * 
 * @author Carlos Llongo
 *
 */
public class KanjiData implements Serializable {
	private static final long serialVersionUID = -6453806531915587161L;
	
	private int heisigNumber;
	private Vector<Double> writingTimes;
	private int timesWritingReviewed;
	private HashMap<String, ReadingReview> readingReviews;
	public final static double WRONG_TIME = 30;
	
	public KanjiData(int heisigNumber){
		this.heisigNumber = heisigNumber;
		this.writingTimes = new Vector<Double>();
		this.readingReviews = new HashMap<String, ReadingReview>();
	}
	
	public KanjiData(String heisigNumber, String[] writingTimes, 
			int timesWritingReviewed){
		this.heisigNumber = Integer.parseInt(heisigNumber);
		this.timesWritingReviewed = timesWritingReviewed;
		this.writingTimes = new Vector<Double>();
		this.readingReviews = new HashMap<String, ReadingReview>();
		
		for(String time : writingTimes){
			try{
				this.writingTimes.add(new Double(time));
			}
			catch(Exception e){
				System.out.println(heisigNumber + " " + time);
			}
		}
	}
	
	public KanjiData(String heisigNumber, String[] writingTimes, 
			int timesWritingReviewed, String[] readingReviews){
		this.heisigNumber = Integer.parseInt(heisigNumber);
		this.timesWritingReviewed = timesWritingReviewed;
		this.writingTimes = new Vector<Double>();
		this.readingReviews = new HashMap<String, ReadingReview>();
		
		for(String time : writingTimes){
			this.writingTimes.add(new Double(time));
		}
		
		String [] tokens;
		String [] readings;

		for(int i = 0; i < readingReviews.length; i++){
			tokens = readingReviews[i].split("\\-");
			readings = tokens[1].split("\\/");
			
			if(tokens.length > 3){
				this.readingReviews.put(tokens[0], 
						new ReadingReview(tokens[0], tokens[2], readings, 
								Utils.stringToBoolean(tokens[3])));
			}
			else if(tokens.length > 2){
				this.readingReviews.put(tokens[0], 
						new ReadingReview(tokens[0], tokens[2], readings, false));
			}
			else{
				this.readingReviews.put(tokens[0], 
						new ReadingReview(tokens[0], "0", readings, false));
			}
		}
	}

	/**
	 * Obtiene la lectura que se debe repasar. Primero comprueba si hay alguna
	 * lectura que no se ha repasado nunca. Si todas se han repasado alguna vez,
	 * obtiene aquella que más veces se ha fallado en sus últimos 3 repasos.
	 * Si los fallos también son los mismos, devuelve aquella que se ha 
	 * repasado un menor número de veces.
	 * 
	 * @param readings El vector con todas las lecturas del kanji
	 * @param toTodayReading Hasta que número de lectura se ha aprendido
	 * @return La lectura del kanji que se debe repasar
	 */
	public Reading getReadingToReview(Vector<Reading> readings, int toTodayReading){
		/* Si no tenemos ninguna estadística, devolvemos la primera lectura
		 * que se haya estudidado. 
		 */
		Collections.sort(readings);

		if(readingReviews.isEmpty()){
			Reading reading = readings.get(0);
			System.out.println(reading.getBook2reference());
			if(reading.getBook2ReferenceAsInt() <= toTodayReading){
				return reading;
			}
			return null;
		}
		
		// Si alguno no se ha estudiado sus estadisticas serán null o su numero
		// de veces repasado será cero.
		for(Reading reading : readings){
			if((readingReviews.get(reading.getBook2reference()) == null || 
					readingReviews.get(reading.getBook2reference()).getTimesReviewed() == 0)
					&& reading.getBook2ReferenceAsInt() <= toTodayReading){
				return reading;
			}
		}
		
		// Si todos se han repasado, devolvemos el más dificil
		int maxMisses = -1;
		int currentMisses;
		Reading mostDifficultReading = null;
		ReadingReview readingReview;
		
		for(Reading reading : readings){
			if(reading.getBook2ReferenceAsInt() <= toTodayReading){
				readingReview = readingReviews.get(reading.getBook2reference());
				
				currentMisses = getMissesInLastThreeReviews(readingReview.getReviewResults());
				
				if(currentMisses > maxMisses){
					maxMisses = currentMisses;
					mostDifficultReading = reading;
				}
			}
		}
		
		if(maxMisses > 0){
			return mostDifficultReading;
		}
		
		/* 
		 * Si todos son igual de difíciles, devolvemos el primero no repasado
		 * en el ciclo actual.
		 */
		
		for(Reading reading : readings){
			if(reading.getBook2ReferenceAsInt() <= toTodayReading){
				readingReview = readingReviews.get(reading.getBook2reference());
				if(!readingReview.getReviewedInLastCycle()){
					return reading;
				}
			}
		}
		
		/*
		 * Todas se han repasado en el último ciclo, por lo que reinicio el
		 * ciclo y devuelvo la primera lectura.
		 */
		
		boolean firstReading = true;
		Reading readingToReturn = null;
		
		for(Reading reading : readings){
			if(reading.getBook2ReferenceAsInt() <= toTodayReading){
				readingReview = readingReviews.get(reading.getBook2reference());
				
				readingReview.setReviewedInLastCycle(false);
				
				if(firstReading){
					readingToReturn = reading;
					firstReading = false;
				}
			}
		}
		
		return readingToReturn;
	}
	
	/**
	 * Obtiene el número de fallos en los 3 últimos repasos de la lectura. Si se
	 * ha repasado menos de 3 veces, se consideran fallos, para así forzar a
	 * repasar las nuevas lecturas.
	 * 
	 * @param reviews El vector con los resultados de los repasos para la lectura
	 * @return El número de fallos en los 3 últimos repasos
	 */
	private int getMissesInLastThreeReviews(Vector<Boolean> reviews) {
		if(reviews.size() == 1){
			return 2 + (reviews.get(0)? 0 : 1);
		}
		
		if(reviews.size() == 2){
			return 1 + (reviews.get(0)? 0 : 1) + (reviews.get(1)? 0 : 1);
		}
		
		int misses = 0;
		for(int i = reviews.size() - 1; i >= reviews.size() - 3; i--){
			if(!reviews.get(i)){
				misses++;
			}
		}
		return misses;
	}

	/**
	 * Añade un nuevo tiempo de escritura a el vector de tiempos.
	 * @param time El último tiempo de escritura realizado.
	 */
	public void addNewTime(double time){
		writingTimes.add(new Double(time));
		timesWritingReviewed++;
	}
	
	/**
	 * Obtiene el número de Heisig en formato de String
	 * @return El heisig como un String
	 */
	public String getKey(){
		return String.valueOf(heisigNumber);
	}
	
	/**
	 * Obtiene el número de veces que se ha repasado el kanji.
	 * @return El número de veces que se ha repasado.
	 */
	public int getTimesReviewed(){
		return timesWritingReviewed;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Heisig nº: ").append(heisigNumber)
		.append("\nTimes reviewed: ").append(timesWritingReviewed)
		.append("\nWriting times: ");
		for(Double time : writingTimes){
			sb.append(time).append(";");
		}
		
		return sb.toString();
	}
	
	/**
	 * Genera la representación de los datos como valores separados por comas
	 * para su almacenamiento en fichero.
	 * 
	 * @return Un string con los datos separados por comas
	 */
	public String toCSV(){
		StringBuilder sb = new StringBuilder();
		sb.append(heisigNumber).append(";");
		
		// Guardamos solo los tiempos de las últimas 3 escrituras
		int startingIndex = 0;
		if(writingTimes.size() > 3){
			startingIndex = writingTimes.size() - 3;
		}
		
		for(int i = startingIndex; i < writingTimes.size(); i++){
			if(i != startingIndex){
				sb.append(",");
			}
			sb.append(writingTimes.get(i));
		}
		
		sb.append(";").append(timesWritingReviewed);
		
		if(!readingReviews.isEmpty()){
			
			sb.append(";");
			
			Vector<String> keys = new Vector<String>(readingReviews.keySet());
			Vector<Dupla> keysI = new Vector<Dupla>();
			
			for(String key : keys){
				keysI.add(new Dupla(key));
			}
			
			Collections.sort(keysI);
			
			Vector<Boolean> readings;
			ReadingReview readingReview;
	
						
			for(int i = 0; i < keysI.size(); i++){
				if(i != 0){
					sb.append(",");
				}
				sb.append(keysI.get(i).stringValue).append("-");
				
				readingReview = readingReviews.get(String.valueOf(keysI.get(i).stringValue));
				
				readings = readingReview.getReviewResults();
				
				// Guardamos solo el resultado de las últimas 3 lecturas
				if(readings.size() > 3){
					startingIndex = readings.size() - 3;
				}
				else{
					startingIndex = 0;
				}
				
				for(int j = startingIndex; j < readings.size(); j++){
					if(j != startingIndex){
						sb.append("/");
					}
					sb.append(Utils.booleanToInt(readings.get(j)));
				}
				
				sb.append("-").append(readingReview.getTimesReviewed());
				sb.append("-")
				.append(Utils.booleanToInt(readingReview.getReviewedInLastCycle()));
			}
		}
		
		return sb.toString();
	}

	/**
	 * Obtiene el tiempo de clasificación, que será la máxima entre el último
	 * tiempo realizado y el tiempo medio.
	 * 
	 * @return El tiempo para clasificar la dificultad del kanji.
	 */
	public double getDifficultyClassificationTime(int learnedKunYomiCount){
		return Math.max(getLastWrintingTime(learnedKunYomiCount), 
				getLastThreeTimesMean(learnedKunYomiCount));
	}

	/**
	 * Obtiene la media de los últimos 3 tiempos de escritura del kanji. En el
	 * cálculo, los tiempos superiores al máximo o cuando se falló la escritura,
	 * se asigna el tiempo máximo (30 segundos).
	 * 
	 * @return La média de las últimas 3 escrituras.
	 */
	public double getLastThreeTimesMean(int learnedKunYomiCount) {
		double threeTimes = 0;
		
		int length = writingTimes.size();
		double time;
		
		if(length >= 3){
			for(int i = length - 1; i > length - 4; i--){
				time = writingTimes.get(i).doubleValue();
				threeTimes += scaleWritingTime(time, learnedKunYomiCount);
				
			}
		}
		else{
			double offset = (3 - writingTimes.size()) 
					* (KanjiData.WRONG_TIME 
							+ learnedKunYomiCount * MazoControlCenter.KUNYOMI_OFFSET);
			threeTimes += offset;
			for(int i = 0; i < length; i++){
				time = writingTimes.get(i).doubleValue();
				threeTimes += scaleWritingTime(time, learnedKunYomiCount);
			}
		}
		
		System.out.println("Mean time: " + (threeTimes / 3));
		return threeTimes / 3;
	}
	
	/**
	 * Devuelve un valor válido de tiempo de escritura. Si ha sido correcto
	 * será el mismo, y si ha sido un fallo se obtiene el tiempo de fallo.
	 * 
	 * @param time El tiempo a comprobar.
	 * @return Un tiempo válido para hacer la media.
	 */
	private double scaleWritingTime(double time, int learnedKunYomiCount){
		if(time == -1){
			return KanjiData.WRONG_TIME 
					+ learnedKunYomiCount * MazoControlCenter.KUNYOMI_OFFSET;
		}
		
		return time;
	}
	
	/**
	 * Obtiene el último tiempo de escritura.
	 * 
	 * @return El último tiempo de escritura.
	 */
	public double getLastWrintingTime(int learnedKunYomiCount){
		return scaleWritingTime(writingTimes.get(writingTimes.size() - 1), learnedKunYomiCount);
	}
	
	/**
	 * Conprueba si el kanji tiene lecturas que se hayan aprendido.
	 * 
	 * @return true si tiene lecturas aprendidas, false en caso contrario
	 */
	public boolean hasLearnedReadings(){
		return !readingReviews.isEmpty();
	}
	
	/**
	 * Obtiene el HashMap con los resultados de los repasos de las distintas
	 * lecturas.
	 * 
	 * @return El HashMap con los resultados de los repasos de lecturas.
	 */
	public HashMap<String, ReadingReview> getReadingsReviewsResults(){
		return readingReviews;
	}

	/**
	 * Añade un nuevo resultado de repaso de lecturas.
	 * 
	 * @param reading La lectura que se ha repasado
	 * @param correct Indica el resultado del repaso, true si se acertó, false en caso contrario
	 */
	public void addNewReadingReview(Reading reading, boolean correct) {
		ReadingReview readingReview = readingReviews.get(reading.getBook2reference());
		
		if(readingReview == null){
			readingReview = new ReadingReview(reading.getBook2reference());
			readingReviews.put(reading.getBook2reference(), readingReview);
		}
		
		readingReview.addNewResult(correct);
		readingReview.setReviewedInLastCycle(true);
	}

}
