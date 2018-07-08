package com.konnichiwamundo.repasandoloskanji.controller;

public class Log {
	
	public Log(){}
	
	public void debug(String message){
		System.out.println("DEBUG: " + message);
	}
	
	public void error(String message){
		System.out.println("ERROR: " + message);
	}
	
	public void info(String message){
		System.out.println("INFO: " + message);
	}

}
