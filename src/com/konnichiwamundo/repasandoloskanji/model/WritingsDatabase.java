/**
 * Project    : Repasando los Kanji
 * Created on : 28 octubre 2010
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

final public class WritingsDatabase{
	private HashMap<String, Writing> writingsDatabase;
	private HashMap<Integer, Integer> readingsLinkDatabase; 

	public WritingsDatabase(){
		writingsDatabase = new HashMap<String, Writing>();
		readingsLinkDatabase = new HashMap<Integer, Integer>();
		
		addKanjisFromFile("kanji_writings.txt");
		addKanjisFromFile("new_kanji.txt");
	}

	public int getSize() {
		return writingsDatabase.size();
	}
	
	public Writing getByHeisigNumber(String heisigNumber){
		return writingsDatabase.get(heisigNumber);
	}
	
	/**
	 * Obtiene el valor heisig para un determinado kanji.
	 * 
	 * @param kanji El kanji que queremos saber su heisig
	 * @return El valor heisig del kanji o -1 si el kanji no se encuentra.
	 */
	public int getHeisigForKanji(char kanji){
		Vector<String> keys = new Vector<String>(writingsDatabase.keySet());
		
		boolean found = false;
		int index = 0;
		Writing writing;
		int heisig = -1;
		
		while(!found && index < keys.size()){
			writing = writingsDatabase.get(keys.get(index));
			if(writing.getJapaneseCharacter() == kanji){
				found = true;
				heisig = writing.getHeisigNumber();
			}
			
			index++;
		}
		
		return heisig;
	}
	
	public void saveDatabaseToTextFile() {
		Vector<String> sKeys = new Vector<String>(writingsDatabase.keySet());
		Vector<Integer> iKeys = new Vector<Integer>();

		for(String key : sKeys){
			iKeys.add(new Integer(key));
		}

		Collections.sort(iKeys);

		System.out.println("iKeys.size: "+ iKeys.size());

		String applicationPath = System.getProperty("user.dir");
		File kanjiWritingsFile = new File(applicationPath + "/data/kanji_writings.txt");
		BufferedWriter out = null;
		
		Writing hr;
		String unicode;

		try{
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(kanjiWritingsFile),"UTF-8"));
			
			out.write("#Vol.1 Ref:kanji:palabra clave");

			for(Integer key : iKeys){
				hr = writingsDatabase.get(key.toString());
				unicode = "\\u" + Integer.toHexString(hr.getJapaneseCharacter() | 0x10000).substring(1);
				
				out.write("\n" + hr.getHeisigNumber() + ":" + unicode + ":" 
				+ hr.getKeyword());
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
	 * Lee el fichero donde estÃ¡n definidos los kanji y los aÃ±ade a la
	 * base de datos de kanji.
	 */
	private void addKanjisFromFile(String fileName) {
		String applicationPath = System.getProperty("user.dir");
		File kanjiFile = new File(applicationPath + "/data/" + fileName);
		BufferedReader in = null;
		
		try{
			in = new BufferedReader(new InputStreamReader(new FileInputStream(kanjiFile),"UTF-8"));
			
			String fileLine;
			String [] tokens;
			Writing hr;
			int heisigNumber;
			char kanji;
			String keyWord;
			int readingLink;
			
			while((fileLine = in.readLine()) != null){
				if(!fileLine.startsWith("#")){
					tokens = fileLine.split("\\:");
					
					heisigNumber = Integer.parseInt(tokens[0]);
					kanji = (char)Integer.parseInt(tokens[1].substring(2), 16);
					keyWord = tokens[2];
					
					if(tokens.length > 3){
						readingLink = Integer.parseInt(tokens[3]);
						readingsLinkDatabase.put(new Integer(readingLink), 
								new Integer(heisigNumber));
					}
					
					hr = new Writing(heisigNumber, kanji, keyWord);
					
					writingsDatabase.put(tokens[0], hr);
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
	
	public Vector<Integer> getReadingsLinks(){
		return new Vector<Integer>(readingsLinkDatabase.keySet());
	}

	
	public int getHeisigFromReadingsLinks(Integer readingLink) {
		return readingsLinkDatabase.get(readingLink);
	}
	
	public int getNumberOfLearnedWritings(int toTodayWriting){
		int learnedWritings = 0;
		
		Vector<String> keys = new Vector<String>(writingsDatabase.keySet());
		int writingReference;
		
		for(String key : keys){
			writingReference = Integer.parseInt(key);
			if(writingReference <= toTodayWriting){
				learnedWritings++;
			}
		}
		
		return learnedWritings;
	}
}