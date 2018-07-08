/**
 * Project    : Repasando los Kanji
 * Created on : 17 agosto 2011
 */

package com.konnichiwamundo.repasandoloskanji.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Vector;

import com.konnichiwamundo.repasandoloskanji.controller.IntegrityChecker;
import com.konnichiwamundo.repasandoloskanji.controller.JapaneseConversionTools;

/**
 * Clase que modeliza la base de datos de lecturas de kanji.
 * 
 * @author Carlos Llongo
 *
 */
public class ReadingsDatabase {

	private HashMap<String, Vector<Reading>> vol1References;
	private HashMap<String, Reading> vol2References;
	
	public ReadingsDatabase(){
		initDatabase();
	}
	
	/**
	 * Inicializa la base de datos de lecturas con la información que se encuentra
	 * en el fichero de texto de lecturas.
	 */
	public void initDatabase(){
		vol1References = new HashMap<String, Vector<Reading>>();
		vol2References = new HashMap<String, Reading>();
		
		String applicationPath = System.getProperty("user.dir");
		File readingsFile = new File(applicationPath + "/data/kanji_readings.txt");
		BufferedReader in = null;
		
		try{
			in = new BufferedReader(new InputStreamReader(new FileInputStream(readingsFile),"UTF-8"));
			
			String fileLine;
			String [] tokens;
			Reading reading;
			Vector<Reading> readings;
			
			while((fileLine = in.readLine()) != null){
				if(!fileLine.startsWith("#")){
					tokens = fileLine.split("\\:");

					reading = new Reading();
					reading.setBook2reference(tokens[0]);
					reading.setBook1reference(tokens[1]);
					reading.setOnyomi(JapaneseConversionTools.getJapaneseFromUnicodeString(tokens[2]));
					reading.setCompound(JapaneseConversionTools.getJapaneseFromUnicodeString(tokens[3]));
					reading.setCompoundReading(JapaneseConversionTools.getJapaneseFromUnicodeString(tokens[4]));
					reading.setCompoundMeaning(tokens[5]);
					
					readings = vol1References.get(reading.getBook1reference());
					if(readings == null){
						readings = new Vector<Reading>();
						vol1References.put(reading.getBook1reference(), readings);
					}
					readings.add(reading);
					
					if(vol2References.get(reading.getBook2reference()) != null){
						System.out.println("ALERTA! ¿Posible fallo en"
								+ " lectura número " 
								+ reading.getBook2reference()
								+"?");
					}
					vol2References.put(reading.getBook2reference(), reading);
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
	 * Obtiene una lectura a partir de su referencia en el volumen 2.
	 * 
	 * @param reference La referencia en el volumen 2.
	 * @return La lectura a la que se hace referencia.
	 */
	public Reading getByReading(String reference){
		return vol2References.get(reference);
	}
	
	/**
	 * Obtiene una lectura de entre las disponibles para el kanji referenciado
	 * por su número en el volumen 1. Para obtener la lectura se tiene en cuenta
	 * los repasos anteriores y hasta que lectura se ha aprendido.
	 * 
	 * @param vol1Ref La referencia del kanji en el volumen 1.
	 * @param statistics Las estadísticas de repaso.
	 * @param toTodayReading Hasta que lectura se ha aprendido.
	 * @return Una de las posibles lecturas del kanji (la más dificil)
	 */
	public Reading getByWriting(String vol1Ref, KanjiStatistics statistics, int toTodayReading){
		if(vol1References.get(vol1Ref) == null){
			return null;
		}
		
		return statistics.getReadingToReview(vol1References.get(vol1Ref), toTodayReading);
	}

	/**
	 * Obtiene el tamaño de la base de datos de lecturas.
	 * 
	 * @return El tamaño de la base de datos de lecturas.
	 */
	public int getSize(){
		return vol2References.size();
	}
	
	/**
	 * 
	 * @param toTodayReading
	 * @return
	 */
	public int getNumberOfLearnedReadings(int toTodayReading){
		HashMap<String,String> learnedReadings = new HashMap<String,String>();
		
		Reading reading;
		int readingReference;
		Vector<String> keys = new Vector<String>(vol2References.keySet());
		for(String key : keys){
			reading = vol2References.get(key);
			readingReference = reading.getBook2ReferenceAsInt();
			if(readingReference <= toTodayReading){
				learnedReadings.put(String.valueOf(readingReference), "");
			}
		}
		
		if(toTodayReading >= IntegrityChecker.VOL2_LESSON5_START){
			return learnedReadings.size() + IntegrityChecker.VOL2_KANJI_WITHOUT_READINGS;
		}
		
		return learnedReadings.size();
		
	}
}
