/**
 * Project    : Repasando los Kanji
 * Created on : 13 enero 2011
 */

package com.konnichiwamundo.repasandoloskanji.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.util.Vector;

import com.konnichiwamundo.repasandoloskanji.model.KanjiData;
import com.konnichiwamundo.repasandoloskanji.model.Mazo;
import com.konnichiwamundo.repasandoloskanji.persistence.FileTools;

/**
 * Esta clase se encarga de administrar los mazos con las distintas dificultades
 * de kanji, así como de actualizar las dificultades de los kanji y de suministrarlos
 * para las lecciones.
 * 
 * @author Carlos Llongo
 *
 */
public class MazoControlCenter {
	private Log log = new Log();
	
	public final double EASY_TIME = 10;
	public final double MEDIUM_TIME = 18;
	public final double READING_OFFSET = 4;
	public static final double KUNYOMI_OFFSET = 4;
	
	private Mazo easyDeck;
	private Mazo mediumDeck;
	private Mazo hardDeck;
	
	private double totalReviewTime;
	private double totalReviewCount;
	private double totalReadingsReviews;
	
	public MazoControlCenter(){
		totalReviewCount = 0;
		totalReviewTime = 0;
		totalReadingsReviews = 0;
	}

	/**
	 * Devuelve un vector de Strings, donde estos Strings son las referencias a
	 * los kanji que deben ser repasados en la lección. Se extrae un tercio de
	 * cada uno de los mazos, pero como suelen haber menos difíciles de los
	 * necesarios, estos se extraen de los intermédios.
	 * 
	 * @param neededKanji El número de kanji necesarios para la lección.
	 * @return El vector con las referencias a los kanji a repasar
	 */
	public Vector<String> getTodayReview(int neededKanji) {
		int thirdNeededKanji = neededKanji / 3;
		log.debug("Dividimos en tres partes: " + thirdNeededKanji);
		
		Vector<String> review = new Vector<String>();
		
		Vector<String> partialReview;
		
		log.debug("=== DIFICILES ===");
		partialReview = hardDeck.extractTodayReviewAndRepopulate(hardDeck.getStoreSize());
		review.addAll(partialReview);
		log.debug("Añadidos dificiles: " + partialReview.size());
		
		log.debug("=== INTERMEDIOS ===");
		int mediumNeeded = thirdNeededKanji;
		
		if(thirdNeededKanji > partialReview.size()){
			mediumNeeded += thirdNeededKanji - partialReview.size();
		}
		
		partialReview = mediumDeck.extractTodayReviewAndRepopulate(mediumNeeded);
		review.addAll(partialReview);
		log.debug("Añadidos intermedios: " + partialReview.size());
		
		log.debug("=== FÁCILES ===");
		partialReview = easyDeck.extractTodayReviewAndRepopulate(neededKanji - review.size());
		review.addAll(partialReview);
		log.debug("Añadidos faciles: " + partialReview.size());
		
		log.debug("Review == neededKanji? " + review.size() + " == " + neededKanji);
		
		return review;
	}

	/**
	 * Obtiene la media de tiempo de las últimas 3 escrituras y a partir de ese
	 * valor situa el kanji en uno de los tres mazos de dificultad.
	 * 
	 * @param heisig El número del kanji.
	 * @param data Las estadísticas de escritura del kanji.
	 */
	public void updateDificulty(int heisig, KanjiData data, int learnedKunYomiCount) {
		
		totalReviewTime += data.getLastWrintingTime(learnedKunYomiCount);
		totalReviewCount++;
		
		double classificationTime =
				data.getDifficultyClassificationTime(learnedKunYomiCount);
		System.out.println("Classification Time: " + classificationTime);
		
		if(data.hasLearnedReadings() || learnedKunYomiCount > 0){
			totalReadingsReviews++;
		}
		
		double readingOffset = 0;
		
		if(data.hasLearnedReadings()){
			readingOffset = READING_OFFSET;
		}

		readingOffset += learnedKunYomiCount * KUNYOMI_OFFSET;
		
		if(classificationTime <= EASY_TIME + readingOffset){
			easyDeck.add(heisig);
			mediumDeck.removeByHeisig(heisig);
			hardDeck.removeByHeisig(heisig);
			log.debug("Kanji " + heisig + " added to easyDeck");
		}
		else if(classificationTime <= MEDIUM_TIME + readingOffset){
			easyDeck.removeByHeisig(heisig);
			mediumDeck.add(heisig);
			hardDeck.removeByHeisig(heisig);
			log.debug("Kanji " + heisig + " added to mediumDeck");
		}
		else{
			easyDeck.removeByHeisig(heisig);
			mediumDeck.removeByHeisig(heisig);
			hardDeck.add(heisig);
			log.debug("Kanji " + heisig + " added to hardDeck");
		}
		
	}

	public Mazo getEasyDeck() {
		return easyDeck;
	}

	public void setEasyDeck(Mazo easyDeck) {
		this.easyDeck = easyDeck;
	}

	public Mazo getMediumDeck() {
		return mediumDeck;
	}

	public void setMediumDeck(Mazo mediumDeck) {
		this.mediumDeck = mediumDeck;
	}

	public Mazo getHardDeck() {
		return hardDeck;
	}

	public void setHardDeck(Mazo hardDeck) {
		this.hardDeck = hardDeck;
	}

	/**
	 * Muestra como están repartidos los kanji estudiados entre los distintos
	 * mazos de dificultad, así como las estadísticas de la lección concluida.
	 * Parte de estas estadísticas se guardarán a fichero de texto.
	 */
	public String getResultsHTML(){
		StringBuilder sbHTML = new StringBuilder("<html>")
		.append("<p style='margin-left: 5px;margin-top: 5px;'>");
		
		int easySize = easyDeck.getStoreSize();
		int mediumSize = mediumDeck.getStoreSize();
		int hardSize = hardDeck.getStoreSize();
		
		int totalSize = easySize + mediumSize + hardSize;

		BigDecimal facil = getPorcentage(easySize, totalSize);
		BigDecimal intermedio = getPorcentage(mediumSize, totalSize);
		BigDecimal dificil = getPorcentage(hardSize, totalSize);
		
		BigDecimal [] lastResults = getLastResults();
		
		if (lastResults.length == 0) {
			return "";
		}
		
		BigDecimal [] differences = new BigDecimal[3];
		
		differences[0] = facil.subtract(lastResults[0]);
		differences[1] = intermedio.subtract(lastResults[1]);
		differences[2] = dificil.subtract(lastResults[2]);
		
		sbHTML.append("Tamaño easyDeck  : " + easySize + " (" + facil + "%) ")
		.append(getColouredText(differences, 0)).append("<br />");
			
		sbHTML.append("Tamaño mediumDeck: " + mediumSize + " (" + intermedio + "%) ")
		.append(getColouredText(differences, 1)).append("<br />");
		
		sbHTML.append("Tamaño hardDeck  : " + hardSize + " (" + dificil + "%) ")
		.append(getColouredText(differences, 2)).append("<br />")
		
		.append("<br />Total review time: ") 
		.append((new BigDecimal(totalReviewTime/60)).setScale(2,BigDecimal.ROUND_HALF_UP))
		.append(" minutos")
		.append("<br />Total review count: " + (int)totalReviewCount)
		.append("<br />Total readings review: " + (int)totalReadingsReviews) 
				.append(" (" + getPorcentage(totalReadingsReviews, totalReviewCount) + "%)");
		
		BigDecimal reviewTimeMean = new BigDecimal(totalReviewTime / totalReviewCount);
		reviewTimeMean = reviewTimeMean.setScale(2,BigDecimal.ROUND_HALF_UP);
		
		sbHTML.append("<br />Review time mean: " + reviewTimeMean + " segundos")
		.append("</p></html>");
		
		appendToDifficultyFile(facil + ";" + intermedio + ";" + dificil + ";" + reviewTimeMean);
		
		System.out.println(sbHTML);
		
		return sbHTML.toString();
	}
	
	/**
	 * Compone una cadena de texto, con el valor numérico indicado por el
	 * indice, en formato html y con un color que vendrá dado por su propio
	 * valor así como por el de los otros 2 valores.
	 * 
	 * @param differences Los 3 valores de las diferencias con la lección anterior
	 * @param index El índice de la diferencia que queremos colorear
	 * @return Un string en formato html dando color a la diferencia
	 */
	private String getColouredText(BigDecimal[] differences, int index){
		int [] signs = new int[3];
		signs[0] = differences[0].signum();
		signs[1] = differences[1].signum();
		signs[2] = differences[2].signum();
		
		String color = "";
		String plusSign = "+";
		
		if(index == 0){
			if(signs[0] == -1){
				color = "red";
				plusSign = "";
			}
			else if(signs[0] == 1){
				color = "green";
			}
			else{
				color = "#FF9900";
			}
		}
		else if(index == 1){
			if((signs[0] == -1 && signs[1]  == 0 && signs[2] == 1)
					|| (signs[0] == 0 && signs[1]  == 0 && signs[2] == 0)){
				color = "#FF9900";
			}
			else if((signs[0] == 1 && signs[1]  == 1 && signs[2] == -1) 
					|| (signs[0] == 1 && signs[1]  == -1 && signs[2] == -1)
					|| (signs[0] == 1 && signs[1]  == -1 && signs[2] == 0)
					|| (signs[0] == 1 && signs[1]  == 0 && signs[2] == -1)
					|| (signs[0] == 0 && signs[1]  == 1 && signs[2] == -1)){
				color = "green";
			}
			else if((signs[0] == 1 && signs[1]  == -1 && signs[2] == 1) 
					|| (signs[0] == -1 && signs[1]  == 1 && signs[2] == -1)){
				if(differences[0].compareTo(differences[2]) < 0){
					color = "red";
				}
				else if(differences[0].compareTo(differences[2]) > 0){
					color = "green";
				}
				else{
					color = "#FF9900";
				}
			}
			else if((signs[0] == -1 && signs[1]  == 1 && signs[2] == 1) 
					|| (signs[0] == -1 && signs[1]  == -1 && signs[2] == 1)
					|| (signs[0] == -1 && signs[1]  == 1 && signs[2] == 0)
					|| (signs[0] == 0 && signs[1]  == -1 && signs[2] == 1)){
				color = "red";
			}
			
			if(signs[1] == -1){
				plusSign = "";
			}
		}
		else if(index == 2){
			if(signs[2] == -1){
				color = "green";
				plusSign = "";
			}
			else if(signs[2] == 1){
				color = "red"; 
			}
			else{
				color = "#FF9900";
			}
		}		
		
		StringBuilder sb = new StringBuilder();
		sb.append("<span style='color:").append(color).append("'>")
		.append(plusSign).append(differences[index]).append("%</span>");
		
		return sb.toString();
	}
	
	/**
	 * Almacena los datos de dificultad de la última lección al final del
	 * fichero de texto.
	 * 
	 * @param data Los datos a guardar.
	 */
	private void appendToDifficultyFile(String data){
		String applicationPath = System.getProperty("user.dir");
		File file = new File(applicationPath + "/data/dificulty_data.csv");
		
		BufferedWriter out = null;
		
		try{
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true),"UTF-8"));
			out.write("\n" + data);
			
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
	 * Obtiene los resultados del último repaso.
	 * 
	 * @return Array de BigDecimals con los resultados del último repaso.
	 */
	private BigDecimal[] getLastResults(){		
		String applicationPath = System.getProperty("user.dir");
		String filePath = applicationPath + "/data/dificulty_data.csv";

		String lastLine = FileTools.getFileLastLine(filePath, 25);
		
		if (lastLine.isEmpty()) {
			return new BigDecimal[0];
		}
		
		String [] lastResultsStrings = lastLine.split("\\;");
		BigDecimal [] lastResultsBigDecimals = new BigDecimal[lastResultsStrings.length];
		
		for(int i = 0; i < lastResultsStrings.length; i++){
			lastResultsBigDecimals[i] = new BigDecimal(lastResultsStrings[i]);
		}
		
		return lastResultsBigDecimals;
	}

	/**
	 * Calcula un porcentage.
	 * 
	 * @param parte La parte
	 * @param total El total
	 * @return El porcentage
	 */
	private BigDecimal getPorcentage(double parte, double total) {
		BigDecimal porcentaje = new BigDecimal((parte * 100) / total);
		return porcentaje.setScale(2,BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * Comprueba si los mazos están vacíos, y en ese caso añade todos los valores
	 * al mazo de los fáciles.
	 * 
	 * @param toYesterday El número de kanji aprendidos.
	 */
	public void populateIfEmpty(int toYesterday) {
		int totalKanjiInDecks = 0;
		totalKanjiInDecks += easyDeck.getStoreSize();
		totalKanjiInDecks += mediumDeck.getStoreSize();
		totalKanjiInDecks += hardDeck.getStoreSize();
		
		if(totalKanjiInDecks == 0){
			log.debug("Es la primera vez. Populando easyDeck");
			easyDeck.initializeStore(toYesterday);
		}
		
	}

}
