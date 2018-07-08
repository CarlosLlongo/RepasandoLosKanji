package com.konnichiwamundo.repasandoloskanji.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class DataFilesTools {

	/**
	 * Inserta una linea CSV al fichero, decidiendo su localización en el
	 * fichero según el valor del primer dato de la linea.
	 * 
	 * @param lineToInsert La linea a insertar en el fichero.
	 * @param fileName El nombre del fichero donde insertar la linea.
	 * @return true si se insertó la linea, false en caso contrario
	 */
	public static boolean insertCSVLineToFile(String lineToInsert, String fileName){
		
		String applicationPath = System.getProperty("user.dir");
		String dataFilePath = applicationPath + "/data/" + fileName;
		String dataTmpFilePath = dataFilePath + ".tmp";
		File dataFile = new File(dataFilePath);
		File dataTmpFile = new File(dataTmpFilePath);
		
		BufferedReader in = null;
		BufferedWriter out = null;
		boolean allReadyToRename = false;

		try{
			
			String insertId = lineToInsert.split("\\:")[0];
			
			in = new BufferedReader(new InputStreamReader(new FileInputStream(dataFile),"UTF-8"));
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataTmpFile),"UTF-8"));
			
			String fileLine;
			String currentId;
			boolean inserted = false;
			boolean firstLine = true;
			
			while((fileLine = in.readLine()) != null){
				currentId = fileLine.split("\\:")[0];
				
				if(insertIdIsSmaller(insertId, currentId) && !inserted){
					if(!firstLine){
						out.newLine();
					}
					out.write(lineToInsert);
					inserted = true;
					
					if(firstLine){
						out.newLine();
					}
				}
				if(!firstLine){
					out.newLine();
				}
				out.write(fileLine);
				
				if(firstLine){
					firstLine = false;
				}
			}
			
			if(!inserted){
				out.newLine();
				out.write(lineToInsert);
			}
			
			allReadyToRename = true;
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
			if(out != null){
				try{
					out.close();
				}
				catch (Exception e) {
					// No hacer nada
				}
			}
		}
		
		if(allReadyToRename){
			dataFile.delete();
			dataTmpFile.renameTo(dataFile);
			return true;
		}
		
		return false;
	}

	/**
	 * Comprueba si el ID de la fila a insertar (su primer valor) es inferior
	 * al ID de la fila actual.
	 * 
	 * @param insertId El ID de la fila a insertar.
	 * @param currentId El ID de la fila actual.
	 * @return true si el ID a insertar es menor, false si el ID a insertar es mayor
	 */
	private static boolean insertIdIsSmaller(String insertId, String currentId) {
		int insertIdInt = Utils.getReferenceAsInt(insertId);
		int currentIdInt = Utils.getReferenceAsInt(currentId);
		if(insertIdInt < currentIdInt){
			return true;
		}
		else if(insertIdInt == currentIdInt){
			return insertId.compareTo(currentId) < 0;
		}
		
		return false;
	}
}
