package com.konnichiwamundo.repasandoloskanji.persistence;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

/**
 * Esta clase se encarga de gestionar la persistencia de distintos parámetros
 * del usuario.
 * 
 * @author Carlos Llongo
 *
 */
public class UserData{
	private String appName;
	private static UserData userData;
	private HashMap<String, String> dataContainer;

	public UserData(String appName) {
		this.appName = appName;
		dataContainer = new HashMap<String, String>();

		loadFromFile();
	}

	/**
	 * Carga los datos de usuario desde un fichero de texto.
	 */
	private void loadFromFile(){
		String applicationPath = System.getProperty("user.dir");
		File userDataFile = new File(applicationPath + "/data/user_data.txt");

		BufferedReader in = null;

		try{
			in = new BufferedReader(new InputStreamReader(new FileInputStream(userDataFile),"UTF-8"));

			String fileLine;
			String [] tokens;

			while((fileLine = in.readLine()) != null){
				if(!fileLine.startsWith("#") && fileLine.contains("=")){
					tokens = fileLine.split("\\=");

					if(tokens.length == 1){
						dataContainer.put(tokens[0], "");
					}
					else{
						dataContainer.put(tokens[0], tokens[1]);
					}
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
	 * Almacena el valor.
	 * 
	 * @param dataName Identificador del valor
	 * @param value El valor a almacenar.
	 */
	public void setUserData(String dataName, String value) {
		dataContainer.put(dataName, value);
	}

	/**
	 * Almacena el array de enteros.
	 * 
	 * @param dataName El Identificador del array
	 * @param intArray El array de enteros a almacenar.
	 */
	public void setIntArray(String dataName, int[] intArray) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < intArray.length; i++) {
			if (i != 0){
				sb.append(",");
			}
			sb.append(intArray[i]);
		}

		dataContainer.put(dataName, sb.toString());
	}

	/**
	 * Obtiene el valor especificado.
	 * 
	 * @param dataName El identificador del valor.
	 * @return El valor.
	 */
	public String getString(String dataName) {
		return dataContainer.get(dataName);
	}

	/**
	 * Obtiene el valor especificado, y en caso de no encontrarse, devuelve
	 * el valor por defecto.
	 * 
	 * @param dataName El identificador del valor.
	 * @param defaultString El valor por defecto.
	 * @return El valor o si no se encuentra, el valor por defecto.
	 */
	public String getString(String dataName, String defaultString) {
		String data = dataContainer.get(dataName);
		if (data == null) {
			dataContainer.put(dataName, defaultString);
			return defaultString;
		}

		return data;
	}

	/**
	 * Obtiene un array de enteros especificado por su identificador.
	 * 
	 * @param dataName El identificador del array
	 * @return El array de enteros especificado.
	 */
	public int[] getIntArray(String dataName) {
		if (getString(dataName) == null || getString(dataName).length() == 0){
			return null;
		}

		String[] stringArray = getString(dataName).split(",");
		int[] intArray = new int[stringArray.length];

		for(int i = 0; i < stringArray.length; i++){
			intArray[i] = Integer.parseInt(stringArray[i]);
		}

		return intArray;
	}

	/**
	 * Almacena todos los identificadores y valores a un fichero de texto.
	 * 
	 * @throws IOException
	 */
	public void commitToFile() throws IOException {
		String applicationPath = System.getProperty("user.dir");
		File userDataFile = new File(applicationPath + "/data/user_data.txt");
		
		Vector<String> keys = new Vector<String>(dataContainer.keySet());
		Collections.sort(keys);
		
		BufferedWriter out = null;

		try{
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(userDataFile),"UTF-8"));
			out.write("# UserData for " + appName);
			out.write("\n# " + new Date().toString());
			
			for(String key : keys){
				out.write("\n" + key + "=" + dataContainer.get(key));
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
	 * Devuelve los datos de usuario, y en caso de no encontrarse, crea un nuevo
	 * objeto.
	 * 
	 * @return Los datos de usuario.
	 */
	public static UserData getUserData() {
		if (userData == null)
			userData = new UserData("Repasando los Kanji");
		return userData;
	}
}