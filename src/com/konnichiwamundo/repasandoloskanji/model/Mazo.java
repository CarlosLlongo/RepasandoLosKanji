/**
 * Project    : Repasando los Kanji
 * Created on : 28 octubre 2010
 */

package com.konnichiwamundo.repasandoloskanji.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

import com.konnichiwamundo.repasandoloskanji.controller.Log;

/**
 * Esta clase se encarga de modelizar un Mazo, que está compuesto por un almacen
 * y un listado de kanji restantes. También ofrece métodos para extraer la
 * lección del día.
 * 
 * @author Carlos Llongo
 *
 */
public class Mazo{
	private Log log = new Log();
	
	private String name;
	private HashMap<String,String> store;
	private HashMap<String,String> remaining;
	
	public Mazo(String name){
		this.name = name;
	}
	
	/**
	 * Inicializa el almacen de kanji a partir de un array de enteros.
	 * 
	 * @param values Un array de enteros con el valor Heisig de los kanji
	 */
	public void initializeStore(int [] values){
		store = new HashMap<String, String>();
		
		for(int s : values){
			store.put(String.valueOf(s), String.valueOf(s));
		}
	}
	
	/**
	 * Inicializa los kanji restantes a partir de un array de enteros.
	 * 
	 * @param values Un array de enteros con el valor Heisig de los kanji
	 */
	public void initializeRemaining(int [] values){
		remaining = new HashMap<String, String>();
		
		for(int s : values){
			remaining.put(String.valueOf(s), String.valueOf(s));
		}
	}
	
	/**
	 * Comprueba que todos los valores restantes se encuentran en el almacén.
	 */
	public void checkConsistency(){
		Vector<String> v = new Vector<String>(remaining.keySet());
		
		for(String kanji : v){
			if(store.get(kanji) == null){
				log.debug("Eliminando " + kanji + " del mazo " + name);
				remaining.remove(kanji);
			}
		}
	}

	/**
	 * Genera un vector con los Heisig de los kanji necesarios para el repaso,
	 * siempre y cuando estos valores no se encuentren en la lista de excluidos.
	 * 
	 * @param neededKanji El número de kanji necesarios para la lección
	 * @param excludeList Los kanji que ya se han añadido a la lección
	 * @return Un vector con los valores Heisig de los kanji a repasar
	 */
	public Vector<String> extractTodayReview(int neededKanji, Vector<String> excludeList){
		Vector<String> allKeys = new Vector<String>(remaining.keySet());

		Collections.shuffle(allKeys);

		Vector<String> todayReview = new Vector<String>();
		int addedKanjis = 0;
		int index = 0;
		String key;
		while(addedKanjis < neededKanji){
			key = allKeys.get(index);
			if(!excludeList.contains(key)){
				todayReview.add(key);
				remaining.remove(key);
				addedKanjis++;
			}
			index++;
		}

		return todayReview;
	}

	/**
	 * Se encarga de generar un vector con los valores Heisig de los kanji a
	 * repasar, dependiendo del estado del almacen y los restantes.
	 * 
	 * 1. Si el almacen es menor que los necesarios, se añade todo el almacen.
	 * 
	 * 2. Si los necesarios son menos que los restantes, se extraen de manera
	 * normal.
	 * 
	 * 3. Si hay menos restantes que necesarios, se extraen todos los restantes,
	 * se vuelve a popular los restantes con el contenido del almacen, y se
	 * extraen los restantes que faltan pasando como vector de excluidos los 
	 * extraidos anteriormente.
	 * 
	 * @param neededKanji Los kanji necesarios para la lección
	 * @return Un vector con los valores Heisig de los kanji para la lección
	 */
	public Vector<String> extractTodayReviewAndRepopulate(int neededKanji){
		Vector<String> todayReview;
		
		if(neededKanji >= store.size()){
			log.debug("Almacen menor/igual que necesarias: " + store.size() + " <= " + neededKanji);
			remaining.clear();
			populate();
			return extractTodayReview(remaining.size(), new Vector<String>());
		}
		else if(neededKanji <= remaining.size()){ // Devolvemos las necesarias
			log.debug("Necesarias menor/igual que restantes: " + neededKanji + " <= " + remaining.size());
			return this.extractTodayReview(neededKanji, new Vector<String>());
		}		 
		else{ // No hay suficientes
			log.debug("No quedan suficientes kanji en el mazo (" + remaining.size() + ").");
			int remainingKanji = neededKanji - remaining.size();
			todayReview = this.extractTodayReview(remaining.size(), new Vector<String>());
			log.debug("Kanji extraidos del mazo: " + todayReview.size());
			log.debug("El tamaño del mazo debe pasar a ser cero: " + remaining.size());
			log.debug("Populamos el mazo de restantes con las del almacen.");
			this.populate();
			log.debug("El tamaño de los mazos deben ser iguales: " 
					+ remaining.size() + " == " + store.size());
			log.debug("Extraemos del mazo los kanji restantes (" + remainingKanji + ")");
			todayReview.addAll(this.extractTodayReview(remainingKanji, todayReview));
			log.debug("Kanji en la leccion de hoy: " + todayReview.size());
		}
		
		return todayReview;
	}
	
	/**
	 * Popula los restantes con el contenido del almacen.
	 */
	private void populate(){
		remaining.putAll(store);
	}
	
	/**
	 * Convierte el contenido del almacen en un array de enteros.
	 * 
	 * @return Un array de enteros con el contenido del almacen.
	 */
	public int[] storeToArray(){
		return toArray(store);
	}
	
	/**
	 * Convierte el contenido de los restantes en un array de enteros.
	 * 
	 * @return Un array de enteros con el contenido de los restantes.
	 */
	public int[] remainingToArray(){
		return toArray(remaining);
	}
	
	/**
	 * Convierte el contenido de un HashMap en un array de enteros.
	 * 
	 * @param map El HashMap a convertir
	 * @return Un array de enteros con el contenido del HashMap
	 */
	private int[] toArray(HashMap<String,String> map){
		int[] values = new int[map.size()];
		Vector<String> keys = new Vector<String>(map.keySet());
		
		for(int i = 0; i < keys.size(); i++){
			values[i] = Integer.parseInt(keys.get(i));
		}
		
		return values;
	}

	/**
	 * Elimina un valor del almacen segun su valor Heisig.
	 * 
	 * @param heisig El valor Heisig a eliminar del almacen.
	 */
	public void removeByHeisig(int heisig) {
		store.remove(String.valueOf(heisig));
	}

	/**
	 * Añade un valor Heisig al almacen
	 * 
	 * @param heisig El valor Heisig a añadir al almacen.
	 */
	public void add(int heisig) {
		store.put(String.valueOf(heisig), String.valueOf(heisig));
	}

	/**
	 * Obtiene el tamaño del almacen.
	 * 
	 * @return El tamaño del almacen.
	 */
	public int getStoreSize() {
		return store.size();
	}

	/**
	 * Añade valores Heisig al almacen desde el 1 hasta el valor toYesterday.
	 * Este método solo tiene utilidad la primera vez que se usa el programa.
	 * 
	 * @param toYesterday El número de Kanji aprendidos.
	 */
	public void initializeStore(int toYesterday) {
		for(int i = 1; i <= toYesterday; i++){
			store.put(String.valueOf(i), String.valueOf(i));
		}
	}
}
