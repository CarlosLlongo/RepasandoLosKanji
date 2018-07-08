/**
 * Project    : Repasando los Kanji
 * Created on : 8 enero 2013
 */
package com.konnichiwamundo.repasandoloskanji.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.konnichiwamundo.repasandoloskanji.controller.DataFilesTools;
import com.konnichiwamundo.repasandoloskanji.controller.JapaneseConversionTools;
import com.konnichiwamundo.repasandoloskanji.controller.Utils;
import com.konnichiwamundo.repasandoloskanji.model.KunYomiDatabase;
import com.konnichiwamundo.repasandoloskanji.model.KunYomiReading;
import com.konnichiwamundo.repasandoloskanji.model.WritingsDatabase;

/**
 * Este panel permite añadir un nuevo kanji o lectura.
 * 
 * @author Carlos Llongo
 *
 */
public class AddKanjiPanel extends JPanel{
	private static final long serialVersionUID = 1072225022112406793L;
	
	private JTextField vol1ReferenceTextField;
	private JTextField hiddenVol1ReferenceWithoutLetter;
	private JTextField kanjiUnicodeTextField;
	private JTextField keyWordTextField;
	private JTextField firstAppearanceTextField;
	
	private JTextField vol2ReferenceTextField;
	private JTextField onyomiUnicodeTextField;
	private JTextField compoundAndReadingUnicodeTextField;
	private JTextField compoundMeaningTextField;
	
	private JTextField rootWordAndInflectionAndKunyomiUnicodeTextField;
	private JTextField kunyomiMeaningTextField;
	
	private JButton addKanjiButton;
	private JButton goBackButton;
	private JButton clearAllButton;
	private JButton convertKanjiButton;
	private JButton convertOnYomiButton;
	private JButton convertOnYomiCompoundButton;
	private JButton convertKunYomiButton;
	
	private JLabel existingReadingsLabel;
	private JTextArea informationTextArea;
	
	private JPanel homePanel;
	
	private KunYomiDatabase kunYomiDB;
	private WritingsDatabase writingsDB;
	
	private JapaneseConversionTools converter;
	
	
	public AddKanjiPanel(JPanel homePanel, KunYomiDatabase kunYomiDB, WritingsDatabase writingsDB){
		this.homePanel = homePanel;
		this.kunYomiDB = kunYomiDB;
		this.writingsDB = writingsDB;
		this.converter = new JapaneseConversionTools();
		
		initializeComponents();
		
		addComponentsToPanel();
	}

	/**
	 * Inicializa los componentes del panel.
	 */
	private void initializeComponents() {
		vol1ReferenceTextField = new JTextField(5);
		vol1ReferenceTextField.setPreferredSize(new Dimension(50, 30));
		vol1ReferenceTextField.addKeyListener(new KeyAdapter(){
			public void keyReleased(KeyEvent e){
				vol1ReferenceTextField_keyPressed();
			}
		});
		hiddenVol1ReferenceWithoutLetter = new JTextField(5);
		kanjiUnicodeTextField = new JTextField(44);
		kanjiUnicodeTextField.setPreferredSize(new Dimension(480, 30));
		firstAppearanceTextField = new JTextField(5);
		keyWordTextField = new JTextField(20);
		vol2ReferenceTextField = new JTextField(5);
		onyomiUnicodeTextField = new JTextField(42);
		onyomiUnicodeTextField.setPreferredSize(new Dimension(480, 30));
		compoundAndReadingUnicodeTextField = new JTextField(36);
		compoundAndReadingUnicodeTextField.setPreferredSize(new Dimension(480, 30));
		compoundMeaningTextField = new JTextField(20);
		rootWordAndInflectionAndKunyomiUnicodeTextField = new JTextField(42);
		rootWordAndInflectionAndKunyomiUnicodeTextField.setPreferredSize(new Dimension(480, 30));
		kunyomiMeaningTextField = new JTextField(20);
		
		informationTextArea = new JTextArea(3,25);
		existingReadingsLabel = new JLabel("Existentes: ");
		
		addKanjiButton = new JButton("Añadir Kanji");
		addKanjiButton.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				detectAndLauchAdditionAction();
			}
		});
		
		goBackButton = new JButton("Volver");
		goBackButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				goBackButtonActionPerformed();
			}
		});
		
		clearAllButton = new JButton("Limpiar");
		clearAllButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				clearAllTextFieldsAndSetFocus();
			}
		});
		
		convertKanjiButton = new JButton("Kanji");
		convertKanjiButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				convertAndAssign(kanjiUnicodeTextField);
				keyWordTextField.requestFocus();
			}
		});
		
		convertOnYomiButton = new JButton("On-yomi");
		convertOnYomiButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				convertToKatakanaAndAssign(onyomiUnicodeTextField);
			}
		});
		
		convertOnYomiCompoundButton = new JButton("Compuesto On-yomi");
		convertOnYomiCompoundButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				convertAndAssign(compoundAndReadingUnicodeTextField);
				compoundMeaningTextField.requestFocus();
			}
		});
		
		convertKunYomiButton = new JButton("Kun-yomi");
		convertKunYomiButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				convertAndAssign(rootWordAndInflectionAndKunyomiUnicodeTextField);
				kunyomiMeaningTextField.requestFocus();
			}
		});
	}
	
	/**
	 * Añade los componentes al panel.
	 */
	private void addComponentsToPanel() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		this.add(new JLabel("Referencia Vol.1:"));
		JPanel vol1ReferencePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		vol1ReferencePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		vol1ReferencePanel.add(vol1ReferenceTextField);
		vol1ReferencePanel.add(existingReadingsLabel);
		this.add(vol1ReferencePanel);
		
		this.add(new JLabel("Kanji Unicode:"));
		JPanel kanjiPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		kanjiPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		kanjiPanel.add(kanjiUnicodeTextField);
		kanjiPanel.add(convertKanjiButton);
		this.add(kanjiPanel);

		this.add(new JLabel("Primera aparición en lecturas:"));
		this.add(firstAppearanceTextField);
		this.add(new JLabel("Palabra clave:"));
		this.add(keyWordTextField);
		this.add(new JLabel("Referencia Vol.2:"));
		this.add(vol2ReferenceTextField);
		
		this.add(new JLabel("On-yomi Unicode:"));
		JPanel onyomiPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		onyomiPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		onyomiPanel.add(onyomiUnicodeTextField);
		onyomiPanel.add(convertOnYomiButton);
		this.add(onyomiPanel);
		
		this.add(new JLabel("Compuesto y lectura Unicode:"));
		JPanel onyomiCompoundPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		onyomiCompoundPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		onyomiCompoundPanel.add(compoundAndReadingUnicodeTextField);
		onyomiCompoundPanel.add(convertOnYomiCompoundButton);
		this.add(onyomiCompoundPanel);
		
		this.add(new JLabel("Significado del compuesto:"));
		this.add(compoundMeaningTextField);
		
		this.add(new JLabel("Kanji y desinencia Unicode:"));
		JPanel rootWordPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		rootWordPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		rootWordPanel.add(rootWordAndInflectionAndKunyomiUnicodeTextField);
		rootWordPanel.add(convertKunYomiButton);
		this.add(rootWordPanel);
		
		this.add(new JLabel("Significado Kun-yomi:"));
		this.add(kunyomiMeaningTextField);
		
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		buttonPanel.add(goBackButton);
		buttonPanel.add(addKanjiButton);
		buttonPanel.add(clearAllButton);
		buttonPanel.add(new JScrollPane(informationTextArea));
		this.add(buttonPanel);
	}
	
	/**
	 * Obtiene el contenido del portapapeles, convierte los caracteres a su
	 * valor unicode y los muestra en el campo de texto especificado. Tambien
	 * obtiene el valor Heisig del kanji y lo asigna al campo de texto del
	 * valor heisig.
	 * 
	 * @param targetTextField
	 */
	private void convertAndAssign(JTextField targetTextField) {
		char kanji = converter.getKanjiCharacterFromClipboard();
		if(kanji != '#' && vol1ReferenceTextField.getText().length() == 0){
			int heisig = writingsDB.getHeisigForKanji(kanji);
			vol1ReferenceTextField.setText(String.valueOf(heisig));
			char subLetter = vol1ReferenceTextField_keyPressed();
			vol1ReferenceTextField.setText(String.valueOf(heisig) + String.valueOf(subLetter));
		}
		targetTextField.setText(converter.obtaintTextFromClipboardAndConvert());
	}
	
	private void convertToKatakanaAndAssign(JTextField targetTextField){
		if(onyomiUnicodeTextField.getText().trim().length() != 0){
			if((int)onyomiUnicodeTextField.getText().charAt(0) < 255){
				JapaneseConversionTools conversor = new JapaneseConversionTools();
				onyomiUnicodeTextField.setText(
						conversor.convertJapaneseToUnicode(
								conversor.convertRomajiToJapanese(onyomiUnicodeTextField.getText(), "katakana")));
			}
		}
	}
	
	
	/**
	 * Comprueba si hay otros kanjis o lecturas que compartan el número heisig
	 * presente en el campo de referencia al volumen 1. El listado de valores
	 * encontrados se muestra al usuario.
	 * 
	 * @return 
	 */
	private char vol1ReferenceTextField_keyPressed() {
		String alphabet = "abcdefghijklmnopqrstuvwxyz";
		
		if(vol1ReferenceTextField.getText().length() == 0){
			return '#';
		}
		
		int vol1Reference = Utils.getReferenceAsInt(vol1ReferenceTextField.getText());
		
		System.out.println("buscando: " + vol1Reference);

		Vector<KunYomiReading> readings = 
				kunYomiDB.getKunYomiForHeisig(vol1Reference);
		
		System.out.println("tam: " + readings.size());
		
		StringBuilder readingsReferences = new StringBuilder("Existentes: ");
		for(KunYomiReading reading : readings){
			System.out.println("id: " + reading.getBook1Reference());
			readingsReferences.append(reading.getBook1Reference()).append("; ");
		}
		
		System.out.println(readingsReferences.toString());
		
		existingReadingsLabel.setText(readingsReferences.toString());
		
		return alphabet.charAt(readings.size());
	}
	
	/**
	 * Detecta que acción tomar en función de los datos presentes en el
	 * formulario, y lanza dichas acciones.
	 */
	private void detectAndLauchAdditionAction() {
		boolean allOk = true;
		
		informationTextArea.setText("");
		hiddenVol1ReferenceWithoutLetter.setText(
				String.valueOf(Utils.getReferenceAsInt(vol1ReferenceTextField.getText())));
		
		if(keyWordTextField.getText().trim().length() > 0){
			allOk = addNewKanji();
		}
		
		if(vol2ReferenceTextField.getText().trim().length() > 0){
			allOk = addNewOnYomi();
		}
		
		if(rootWordAndInflectionAndKunyomiUnicodeTextField.getText().trim().length() > 0){
			allOk = addNewKunYomi();
		}
		
		if(allOk){
			clearAllTextFields(this);
			vol1ReferenceTextField.requestFocus();
		}
	}

	/**
	 * Añade un nuevo kanji.
	 */
	private boolean addNewKanji() {
		Vector<JTextField> neededData = new Vector<JTextField>();
		neededData.add(hiddenVol1ReferenceWithoutLetter);
		neededData.add(kanjiUnicodeTextField);
		neededData.add(keyWordTextField);
		
		if(firstAppearanceTextField.getText().length() > 0){
			neededData.add(firstAppearanceTextField);
		}
		
		if(checkNeededData(neededData)){
			if(DataFilesTools.insertCSVLineToFile(createCVSLine(neededData), "new_kanji.txt")){
				showInformation("Kanji añadido: " + hiddenVol1ReferenceWithoutLetter.getText());
				return true;
			}
		}
		
		return false;
	}

	/**
	 * Añade un nuevo On-yomi
	 */
	private boolean addNewOnYomi() {
		Vector<JTextField> neededData = new Vector<JTextField>();
		neededData.add(vol2ReferenceTextField);
		neededData.add(hiddenVol1ReferenceWithoutLetter);
		neededData.add(onyomiUnicodeTextField);
		neededData.add(compoundAndReadingUnicodeTextField);
		neededData.add(compoundMeaningTextField);
		
		if(checkNeededData(neededData)){
			if(DataFilesTools.insertCSVLineToFile(createCVSLine(neededData), "kanji_readings.txt")){
				showInformation("On-yomi añadido: " + vol2ReferenceTextField.getText());
				return true;
			}
		}
		
		return false;
		
	}
	
	/**
	 * Añade un nuevo kun-yomi
	 */
	private boolean addNewKunYomi() {
		Vector<JTextField> neededData = new Vector<JTextField>();
		neededData.add(vol1ReferenceTextField);
		neededData.add(rootWordAndInflectionAndKunyomiUnicodeTextField);
		neededData.add(kunyomiMeaningTextField);
		
		if(checkNeededData(neededData)){
			if(DataFilesTools.insertCSVLineToFile(createCVSLine(neededData), "kun_yomi_readings.txt")){
				showInformation("Kun-yomi añadido: " + vol1ReferenceTextField.getText());
				return true;
			}
		}
		
		return false;
	}
	
	private void showInformation(String information) {
		if(informationTextArea.getText().length() > 0){
			informationTextArea.append("\n");
		}
		
		informationTextArea.append(information);
	}
	
	/**
	 * Crea una linea de datos a partir del contenido de los campos de texto
	 * indicados en el vector. Los datos quedarán separados por dos puntos ":"
	 * 
	 * @param neededData Los campos de texto de donde extraer los datos
	 * @return Una linea de datos separados por dos puntos ":"
	 */
	private String createCVSLine(Vector<JTextField> neededData) {
		StringBuilder cvsLine = new StringBuilder();
		for(int i = 0; i < neededData.size(); i++){
			if(i != 0){
				cvsLine.append(":");
			}
			cvsLine.append(neededData.get(i).getText().trim());
		}
		
		return cvsLine.toString();
	}

	/**
	 * Comprueba que los campos de texto incluidos en el vector tienen todos
	 * algo de texto. Los que no tengan texto cambiar su fondo a color rojo como
	 * indicación.
	 * 
	 * @param neededData Vector con los campos de texto a comprobar
	 * @return true si todos los campos de texto tiene algo de texto, false en caso contrario
	 */
	private boolean checkNeededData(Vector<JTextField> neededData) {
		Boolean allDataOK = true;
		
		for(JTextField textField : neededData){
			if(textField.getText().trim().length() == 0){
				textField.setBackground(Utils.getRedColor());
				allDataOK = false;
			}
		}
		
		return allDataOK;
	}
	

	/**
	 * Vuelve al panel inicial.
	 */
	private void goBackButtonActionPerformed() {
		this.setVisible(false);
		Container parent = this.getParent();
		parent.remove(this);
		parent.add(homePanel, BorderLayout.CENTER);
		homePanel.setVisible(true);
		
		parent.validate();
	}
	
	/**
	 * Limpia todos los campos de texto y pone el foco en el campo de texto de
	 * la referencia al volumen 1.
	 */
	private void clearAllTextFieldsAndSetFocus() {
		clearAllTextFields(this);
		vol1ReferenceTextField.requestFocus();
	}
	
	/**
	 * Limpia todos los campos de texto del panel indicado.
	 * 
	 * @param panel El panel que contiene los campos de texto.
	 */
	private void clearAllTextFields(JPanel panel){
		Component [] allComponents = panel.getComponents();
		
		for(Component component : allComponents){
			if(component.getClass() == JTextField.class){
				((JTextField)component).setText("");
				((JTextField)component).setBackground(Color.WHITE);
			}
			else if(component.getClass() == JPanel.class){
				clearAllTextFields((JPanel)component);
			}
		}
	}
}
