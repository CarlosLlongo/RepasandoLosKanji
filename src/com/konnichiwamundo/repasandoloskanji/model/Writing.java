package com.konnichiwamundo.repasandoloskanji.model;

public class Writing {
	private char japaneseCharacter;
	private int heisigNumber;
	private String keyWord;
	
	public Writing(int heisigNumber, char japaneseCharacter, String keyWord){
		this.heisigNumber = heisigNumber;
		this.japaneseCharacter = japaneseCharacter;
		this.keyWord = keyWord;
	}

	public int getHeisigNumber() {
		return this.heisigNumber;
	}

	public String getKeyword() {
		return keyWord;
	}

	public char getJapaneseCharacter() {
		return this.japaneseCharacter;
	}
}