package com.konnichiwamundo.repasandoloskanji.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;


public class KanjiStatistics implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2801640021204673291L;
	
	private HashMap<String, KanjiData> learnedKanji;

	public KanjiStatistics(){
		learnedKanji = new HashMap<String, KanjiData>();
		populateFromFile();
	}
	
	/**
	 * Obtiene los datos del kanji indicado. Si ese kanji no tiene datos, se
	 * crea un nuevo objeto de datos.
	 * 
	 * @param heisig La referencia al vol.1 del kanji
	 * @return Los datos del kanji.
	 */
	public KanjiData getKanjiData(int heisig){
		KanjiData data = learnedKanji.get(String.valueOf(heisig));
		
		if(data == null){
			data = new KanjiData(heisig);
			learnedKanji.put(String.valueOf(heisig), data);
		}
		
		return data;
	}
	
	/**
	 * Comprueba si un kanji tiene datos.
	 * 
	 * @param heisig La referencia al vol.1 del kanji
	 * @return true si tiene datos, false en caso contrario
	 */
	public boolean hasStatistics(int heisig){
		return learnedKanji.get(String.valueOf(heisig)) != null;
	}
	
	/**
	 * Devuelve el número de kanji de los que se tiene estadisticas.
	 * 
	 * @return El número de kanji con estadísticas.
	 */
	public int getKanjiCount(){
		return learnedKanji.size();
	}
	
	/**
	 * Muestra diversas estadísticas sobre las escrituras y lecturas de los
	 * kanji.
	 */
	public void showStatisticsOverview(ReadingsDatabase readingsDb){
		Vector<String> stringKeys = new Vector<String>(learnedKanji.keySet());
		Vector<Integer> integerKeys = new Vector<Integer>();
		for(String key : stringKeys){
			integerKeys.add(new Integer(key));
		}
		Collections.sort(integerKeys);
		
		KanjiData data;
		int vecesRepasado;
		int minimoRepasado = 9999;
		int maximoRepasado = -1;
		for(Integer iKey : integerKeys){
			data = learnedKanji.get(String.valueOf(iKey));
//			System.out.println(data);
			vecesRepasado = data.getTimesReviewed();
			
			if(vecesRepasado < minimoRepasado){
				minimoRepasado = vecesRepasado;
			}
			
			if(vecesRepasado > maximoRepasado){
				maximoRepasado = vecesRepasado;
			}
		}
		
		if (!integerKeys.isEmpty()) {
			int ultimo = integerKeys.get(integerKeys.size() - 1);
			System.out.println("Ultimo kanji: " + ultimo);
		}
		
		
		StringBuilder sbMinimos = new StringBuilder();
		StringBuilder sbMaximos = new StringBuilder();
		
		int cantidadMinimos = 0;
		int cantidadMaximos = 0;
		for(Integer iKey : integerKeys){
			
			data = learnedKanji.get(String.valueOf(iKey));
			if(data.getTimesReviewed() == minimoRepasado){
				sbMinimos.append(data.getKey()).append(",");
				cantidadMinimos++;
			}
			if(data.getTimesReviewed() == maximoRepasado){
				sbMaximos.append(data.getKey()).append(",");
				cantidadMaximos++;
			}
		}
		
		StringBuilder mensaje = new StringBuilder();
		mensaje.append("Kanjis menos repasados (").append(cantidadMinimos)
		.append(" kanjis repasados ").append(minimoRepasado).append(" veces): ")
		.append(sbMinimos);
		
		System.out.println(mensaje.toString());
		
		mensaje = new StringBuilder();
		mensaje.append("Kanjis más repasados (").append(cantidadMaximos)
		.append(" kanjis repasados ").append(maximoRepasado).append(" veces): ")
		.append(sbMaximos);
		
		System.out.println(mensaje.toString());
		
		
		//#####################################################################
		// LECTURAS
		//#####################################################################
		
		HashMap<String, ReadingReview> readings;
		HashMap<String, ReadingReview> allReadings = new HashMap<String, ReadingReview>();
		for(Integer iKey : integerKeys){
			data = learnedKanji.get(String.valueOf(iKey));
			if(data.hasLearnedReadings()){
				readings = data.getReadingsReviewsResults();
				allReadings.putAll(readings);
			}
		}
		
		ReadingReview readingReview;
		int enCiclo = 0;
		
		System.out.println("Total readings: " + allReadings.size());
		stringKeys = new Vector<String>(allReadings.keySet());
		Vector<Dupla> duplaKeys = new Vector<Dupla>();
		for(String key : stringKeys){
			duplaKeys.add(new Dupla(key));
			if(readingsDb.getByReading(key) == null){
				System.out.println("La lectura " + key + " no exite!!!");
			}
			else{
				readingReview = allReadings.get(key);
				if(readingReview.getReviewedInLastCycle()){
					enCiclo++;
				}
			}
		}
		
		System.out.println("Total en el ciclo: " + enCiclo);
		
		Collections.sort(duplaKeys);
		
		minimoRepasado = 9999;
		maximoRepasado = -1;
		for(Dupla dKey : duplaKeys){
			vecesRepasado = allReadings.get(dKey.stringValue).getTimesReviewed();
			
			if(vecesRepasado < minimoRepasado){
				minimoRepasado = vecesRepasado;
			}
			
			if(vecesRepasado > maximoRepasado){
				maximoRepasado = vecesRepasado;
			}
		}
		
		sbMinimos = new StringBuilder();
		sbMaximos = new StringBuilder();
		
		cantidadMinimos = 0;
		cantidadMaximos = 0;
		for(Dupla dKey : duplaKeys){
			vecesRepasado = allReadings.get(dKey.stringValue).getTimesReviewed();
			
			if(vecesRepasado == minimoRepasado){
				sbMinimos.append(dKey.stringValue).append(",");
				cantidadMinimos++;
			}
			if(vecesRepasado == maximoRepasado){
				sbMaximos.append(dKey.stringValue).append(",");
				cantidadMaximos++;
			}
		}
		
		mensaje = new StringBuilder();
		mensaje.append("Lecturas menos repasadas (").append(cantidadMinimos)
		.append(" lecturas repasadas ").append(minimoRepasado).append(" veces): ")
		.append(sbMinimos);
		
		System.out.println(mensaje.toString());
		
		mensaje = new StringBuilder();
		mensaje.append("Lecturas más repasadas (").append(cantidadMaximos)
		.append(" lecturas repasadas ").append(maximoRepasado).append(" veces): ")
		.append(sbMaximos);
		
		System.out.println(mensaje.toString());
	}
	
	/**
	 * Inicializa las estadisticas a partir de los datos del fichero.
	 */
	private void populateFromFile(){
		String applicationPath = System.getProperty("user.dir");
		File statisticsFile = new File(applicationPath + "/data/statistics.txt");
		BufferedReader in = null;
		
		try{
			in = new BufferedReader(new InputStreamReader(new FileInputStream(statisticsFile),"UTF-8"));
			
			String fileLine;
			String [] tokens;
			String [] writingTimes;
			String [] readingReviews;
			KanjiData data;
			int timesWritingReviewed;
			
			while((fileLine = in.readLine()) != null){
				tokens = fileLine.split("\\;");

				writingTimes = tokens[1].split("\\,");
				
				timesWritingReviewed = Integer.parseInt(tokens[2]);
				
				if(tokens.length > 3){
					readingReviews = tokens[3].split("\\,");
					data = new KanjiData(tokens[0], writingTimes, 
							timesWritingReviewed, readingReviews);
				}
				else{
					data = new KanjiData(tokens[0], writingTimes, timesWritingReviewed);
				}
				
				learnedKanji.put(tokens[0], data);

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
	 * Almacena las estadisticas en un fichero de texto.
	 */
	public void toFile(){
		String applicationPath = System.getProperty("user.dir");
		File file = new File(applicationPath + "/data/statistics.txt");
		
		BufferedWriter out = null;
		
		try{
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),"UTF-8"));
			Vector<String> keys = new Vector<String>(learnedKanji.keySet());
			Vector<Integer> keysI = new Vector<Integer>();
			for(String key : keys){
				keysI.add(new Integer(key));
			}
			
			Collections.sort(keysI);
			
			KanjiData data;
			
			for(int i = 0; i < keysI.size(); i++){
				if(i != 0){
					out.write("\n");
				}
				data = learnedKanji.get(String.valueOf(keysI.get(i)));
				out.write(data.toCSV());
			}		
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			if(out != null){
				try{
					out.close();
				}
				catch (Exception e) {
					// Nada
				}
			}
		}
	}

	/**
	 * Obtiene la lectura a repasar a partir de sus estadisticas de repasos
	 * anteriores.
	 * 
	 * @param readings Las posibles lecturas del kanji
	 * @param toTodayReading El número de lecturas aprendidas
	 * @return La lectura a repasar o null si no se ha estudiado ninguna.
	 */
	public Reading getReadingToReview(Vector<Reading> readings, int toTodayReading) {
		String reference = readings.get(0).getBook1reference();
		
		KanjiData data = learnedKanji.get(reference);
		
		if(data == null){
			Reading reading = readings.get(0);
			if (reading.getBook2ReferenceAsInt() <= toTodayReading) {
				return reading;
			}
			else {
				return null;
			}
		}
		
		return data.getReadingToReview(readings, toTodayReading);
	}
}