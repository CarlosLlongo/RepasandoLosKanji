package com.konnichiwamundo.repasandoloskanji.persistence;

import java.io.RandomAccessFile;

public class FileTools {
	
	public static String getFileLastLine(String filePath, long maxLineLength){
		String lastLine = "";

		RandomAccessFile raf = null;
		
		try{
			raf = new RandomAccessFile(filePath, "r");
			
			long fileLength = raf.length();
			if(fileLength > maxLineLength) {
				raf.seek(fileLength - maxLineLength);
			}
			String readLine = "";
			while((readLine = raf.readLine()) != null){
				lastLine = readLine;
			}                     
		}
		catch (Exception e){
			e.printStackTrace();
		}
		finally{
			if(raf != null){
				try{
					raf.close();
				}
				catch(Exception e){
					// Nada
				}
			}
		}
		
		return lastLine;
	}
}
