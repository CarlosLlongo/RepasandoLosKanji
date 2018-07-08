/**
 * Project    : Repasando los Kanji
 * Created on : 28 octubre 2010
 */

package com.konnichiwamundo.repasandoloskanji.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;

import com.konnichiwamundo.repasandoloskanji.controller.Log;
import com.konnichiwamundo.repasandoloskanji.controller.MazoControlCenter;
import com.konnichiwamundo.repasandoloskanji.controller.ReviewsControlCenter;
import com.konnichiwamundo.repasandoloskanji.model.KanjiData;
import com.konnichiwamundo.repasandoloskanji.model.KanjiStatistics;
import com.konnichiwamundo.repasandoloskanji.model.KunYomiDatabase;
import com.konnichiwamundo.repasandoloskanji.model.KunYomiReading;
import com.konnichiwamundo.repasandoloskanji.model.Reading;
import com.konnichiwamundo.repasandoloskanji.model.ReadingsDatabase;
import com.konnichiwamundo.repasandoloskanji.model.Writing;

/**
 * Esta clase representa el panel mediante el cual se realiza el repaso de la
 * lección.
 * 
 * @author Carlos Llongo
 *
 */
public class LessonPanel extends JPanel {
	private static final long serialVersionUID = -4014993456643585647L;
	private JButton checkButton;
	private JButton finishButton;
	private JButton rightButton;
	private JButton wrongButton;
	
	private JLabel heisigIDlabel;
	private JLabel kanjiDisplay;
	private JLabel keywordLabel;
	private JLabel positionLabel;
	private JLabel jLabelVol2Ref;
	private JLabel jLabelCompoundTitle;
	private JLabel jLabelCompound;
	private JLabel jLabelOnYomiTitle;
	private JLabel jLabelOnYomi;
	private JLabel jLabelKunYomi;
	private JLabel jLabelCompoundReadingTitle;
	private JLabel jLabelCompoundReading;
	private JLabel jLabelCompoundMeaningTitle;
	private JLabel jLabelCompoundMeaning;
	
	private JSeparator jSeparatorSuperior;
	private JSeparator jSeparatorInferior;
	
	private JPanel jPanelGlobal;
	private ResultsPanel jPanelResultados;
	
	private JPanel jPanelKeyWord;
	private JPanel jPanelVol1Ref;
	private JPanel jPanelTop;
	private JPanel jPanelVol2Ref;
	private JPanel jPanelReferences;
	private JPanel jPanelCheck;
	private JPanel jPanelWriting;
	private JPanel jPanelCheckAndWriting;
	private JPanel jPanelWritingAndKunYomi;
	private JPanel jPanelKunYomi;
	private JPanel jPanelReadings;
	private JPanel jPanelCompound;
	private JPanel jPanelOnYomi;
	private JPanel jPanelCompoundReading;
	private JPanel jPanelCompoundMeaning;
	private JPanel jPanelRightWrongButtons;
	private JPanel jPanelPosition;
	private JPanel jPanelFinish;
	private JPanel jPanelBottom;
	
	private Writing currentRecord;
	private Reading currentReading;
	private Vector<KunYomiReading> currentKunYomis;
	
	private ReadingsDatabase readingsDb;
	private KunYomiDatabase kunYomiDb;
	private MazoControlCenter mazoCC;
	private ReviewsControlCenter reviewCC;
	private double startTime;
	private double intermediateTime;
	private double endTime;
	private double startKunYomiTime;
	private double endKunYomiTime;
	private KanjiStatistics statistics;
	
	private Log log = new Log();
	
	private final int COMPOUND_FONT_SIZE = 32;
	private final int KANA_FONT_SIZE = 24;
	private final int STATUS_CHECK_KANJI = 1;
	private final int STATUS_TEST_ONYOMI = 2;
	private final int STATUS_CHECK_ONYOMI = 3;
	private final int STATUS_CHECK_COMPOUND = 4;
	private final int STATUS_TEST_KUNYOMI = 5;
	private final int STATUS_TEST_MEANING = 6;
	private final int STATUS_CHECK_MEANING = 7;
	private int currentStatus;
	private String japaneseFontName;

	public LessonPanel(String fontName, ReviewsControlCenter rCC, 
			ResultsPanel jPanelResultados, MazoControlCenter mazoCC,
			ReadingsDatabase readingsDb, KanjiStatistics statistics,
			KunYomiDatabase kunYomiDb){
		
		this.japaneseFontName = fontName;
		
		initComponents();
		
		this.readingsDb = readingsDb;
		this.mazoCC = mazoCC;
		this.statistics = statistics;
		this.reviewCC = rCC;
		this.kunYomiDb = kunYomiDb;

		this.jPanelResultados = jPanelResultados;
		
		currentKunYomis = new Vector<KunYomiReading>();

		resetDisplayAndShowNextKanji();
	}

	/**
	 * Inicializa los componentes gráficos que se mostrarán en el panel.
	 */
	private void initComponents() {
		jPanelGlobal = new JPanel();
		jSeparatorSuperior = new JSeparator();
		jSeparatorInferior = new JSeparator();
		checkButton = new JButton();
		rightButton = new JButton();
		wrongButton = new JButton();
		kanjiDisplay = new JLabel();
		keywordLabel = new JLabel();
		heisigIDlabel = new JLabel();
		positionLabel = new JLabel();
		finishButton = new JButton();
		
		jPanelKeyWord = new JPanel();
		jPanelVol1Ref = new JPanel();
		jPanelTop = new JPanel();
		jPanelVol2Ref = new JPanel();
		jPanelReferences = new JPanel();
		jPanelCheck = new JPanel();
		jPanelWriting = new JPanel();
		jPanelCheckAndWriting = new JPanel();
		jPanelWritingAndKunYomi = new JPanel();
		jPanelKunYomi = new JPanel();
		jPanelReadings = new JPanel();
		jPanelRightWrongButtons = new JPanel();
		jPanelPosition = new JPanel();
		jPanelFinish = new JPanel();
		jPanelBottom = new JPanel();

		this.setLayout(new BorderLayout());
		
		jPanelGlobal.setLayout(new BoxLayout(jPanelGlobal, BoxLayout.Y_AXIS));
		jPanelGlobal.setBorder(new EtchedBorder());
		
		keywordLabel.setFont(new Font("Dialog", 1, 18));
		keywordLabel.setText("Keyword");
		jPanelKeyWord.setLayout(new FlowLayout(FlowLayout.LEFT));
		jPanelKeyWord.add(keywordLabel);
		
		heisigIDlabel.setFont(new Font("Dialog", 1, 18));
		heisigIDlabel.setHorizontalAlignment(11);
		jPanelVol1Ref.setLayout(new FlowLayout(FlowLayout.RIGHT));
		jPanelVol1Ref.add(heisigIDlabel);
		jLabelVol2Ref = new JLabel("");
		jLabelVol2Ref.setFont(new Font("Dialog", 1, 18));
		jPanelVol2Ref.setLayout(new FlowLayout(FlowLayout.RIGHT));
		jPanelVol2Ref.add(jLabelVol2Ref);
		
		jPanelReferences.setLayout(new GridLayout(2, 1));
		jPanelReferences.add(jPanelVol1Ref);
		jPanelReferences.add(jPanelVol2Ref);
		
		jPanelTop.setLayout(new GridLayout(1, 2));
		jPanelTop.add(jPanelKeyWord);
		jPanelTop.add(jPanelReferences);
		jPanelGlobal.add(jPanelTop);
		
		jPanelGlobal.add(jSeparatorSuperior);
		
		// Botón "comprobar"
		checkButton.setText(MainFrame
				.getTextResource("SESSION_KANJI_CHECK"));
		checkButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				checkButtonActionPerformed();
			}
		});
		jPanelCheck.setLayout(new FlowLayout(FlowLayout.CENTER));
		jPanelCheck.add(checkButton);
		jPanelCheckAndWriting.setLayout(new BoxLayout(jPanelCheckAndWriting, BoxLayout.Y_AXIS));
		jPanelCheckAndWriting.add(jPanelCheck);
		
		kanjiDisplay.setFont(new Font(japaneseFontName, Font.BOLD, 60));
		kanjiDisplay.setHorizontalAlignment(0);
		kanjiDisplay.setBorder(new LineBorder(new Color(0, 0, 0)));
		kanjiDisplay.setMaximumSize(new Dimension(120, 120));
		kanjiDisplay.setMinimumSize(new Dimension(120, 120));
		kanjiDisplay.setPreferredSize(new Dimension(120, 120));
		jPanelWriting.setLayout(new FlowLayout(FlowLayout.CENTER));
		jPanelWriting.add(kanjiDisplay);
		jPanelCheckAndWriting.add(jPanelWriting);
		
		jPanelWritingAndKunYomi.setLayout(new BorderLayout());
		jPanelWritingAndKunYomi.add(jPanelCheckAndWriting, BorderLayout.WEST);
		jPanelWritingAndKunYomi.add(jPanelKunYomi, BorderLayout.CENTER);
		jPanelKunYomi.setBorder(new EtchedBorder());
		jPanelKunYomi.setLayout(new BoxLayout(jPanelKunYomi, BoxLayout.Y_AXIS));
		jPanelKunYomi.add(new JLabel("Kun-yomi:"));
		jLabelKunYomi = new JLabel("");
		jLabelKunYomi.setFont(new Font(japaneseFontName, Font.BOLD, 20));
		jPanelKunYomi.add(jLabelKunYomi);
		jPanelGlobal.add(jPanelWritingAndKunYomi);
		
		
		jLabelCompoundTitle = new JLabel("Compuesto:");
		jLabelCompound = new JLabel("");
		jLabelCompound.setPreferredSize(new Dimension(120, 50));
		jLabelCompound.setFont(new Font(japaneseFontName, Font.BOLD, COMPOUND_FONT_SIZE));
		jPanelCompound = new JPanel();
		jPanelCompound.setLayout(new BoxLayout(jPanelCompound, BoxLayout.Y_AXIS));
		jPanelCompound.setBorder(new EtchedBorder());
		jPanelCompound.add(jLabelCompoundTitle);
		jPanelCompound.add(jLabelCompound);
		
		jLabelOnYomiTitle = new JLabel("On-yomi:");
		jLabelOnYomi = new JLabel("");
		jLabelOnYomi.setPreferredSize(new Dimension(120, 50));
		jLabelOnYomi.setFont(new Font(japaneseFontName, Font.BOLD, KANA_FONT_SIZE));
		jPanelOnYomi = new JPanel();
		jPanelOnYomi.setLayout(new BoxLayout(jPanelOnYomi, BoxLayout.Y_AXIS));
		jPanelOnYomi.setBorder(new EtchedBorder());
		jPanelOnYomi.add(jLabelOnYomiTitle);
		jPanelOnYomi.add(jLabelOnYomi);
		
		jLabelCompoundReadingTitle = new JLabel("Lectura Compuesto:");
		jLabelCompoundReading = new JLabel("");
		jLabelCompoundReading.setPreferredSize(new Dimension(120, 50));
		jLabelCompoundReading.setFont(new Font(japaneseFontName, Font.BOLD, KANA_FONT_SIZE));
		jPanelCompoundReading = new JPanel();
		jPanelCompoundReading.setLayout(new BoxLayout(jPanelCompoundReading, BoxLayout.Y_AXIS));
		jPanelCompoundReading.setBorder(new EtchedBorder());
		jPanelCompoundReading.add(jLabelCompoundReadingTitle);
		jPanelCompoundReading.add(jLabelCompoundReading);
		
		jLabelCompoundMeaningTitle = new JLabel("Significado Compuesto:");
		jLabelCompoundMeaning = new JLabel("");
		jLabelCompoundMeaning.setPreferredSize(new Dimension(120, 50));
		jLabelCompoundMeaning.setForeground(Color.blue);
		jPanelCompoundMeaning = new JPanel();
		jPanelCompoundMeaning.setLayout(new BoxLayout(jPanelCompoundMeaning, BoxLayout.Y_AXIS));
		jPanelCompoundMeaning.setBorder(new EtchedBorder());
		jPanelCompoundMeaning.add(jLabelCompoundMeaningTitle);
		jPanelCompoundMeaning.add(jLabelCompoundMeaning);
		
		jPanelReadings.setLayout(new GridLayout(2,2));
		jPanelReadings.setBorder(new EtchedBorder());
		jPanelReadings.add(jPanelCompound);
		jPanelReadings.add(jPanelOnYomi);
		jPanelReadings.add(jPanelCompoundReading);
		jPanelReadings.add(jPanelCompoundMeaning);
		jPanelGlobal.add(jPanelReadings);
		
		String applicationPath = System.getProperty("user.dir");
		rightButton.setIcon(new ImageIcon(applicationPath + "/img/correct.png"));
		rightButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				rightButtonActionPerformed();
			}
		});
		
		wrongButton.setIcon(new ImageIcon(applicationPath + "/img/wrong.png"));
		wrongButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				wrongButtonActionPerformed();
			}
		});
		
		jPanelRightWrongButtons.setLayout(new FlowLayout(FlowLayout.CENTER));
		jPanelRightWrongButtons.add(rightButton);
		jPanelRightWrongButtons.add(wrongButton);
		jPanelGlobal.add(jPanelRightWrongButtons);
		
		jPanelGlobal.add(jSeparatorInferior);
		
		positionLabel.setText("1000/2000");
		jPanelPosition.setLayout(new FlowLayout(FlowLayout.LEFT));
		jPanelPosition.add(positionLabel);
		
		finishButton.setText(MainFrame
				.getTextResource("SESSION_FINISH"));
		finishButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				goToResultScreen();
			}
		});
		
		jPanelFinish.setLayout(new FlowLayout(FlowLayout.RIGHT));
		jPanelFinish.add(finishButton);
		
		jPanelBottom.setLayout(new GridLayout(1, 2));
		jPanelBottom.add(jPanelPosition);
		jPanelBottom.add(jPanelFinish);
		jPanelGlobal.add(jPanelBottom);

		this.add(this.jPanelGlobal, BorderLayout.CENTER);
	}
	
	/**
	 * Cuando el botón de "Comprobar" es pulsado, se muestra el kanji así como
	 * su número en el volumen 1, y dependiendo de la combinación de on-yomi
	 * y kun-yomi que tenga, se mostrará para el siguiente paso del repaso.
	 */
	private void checkButtonActionPerformed() {
		// Mostramos el kanji y su número en el volumen 1
		this.kanjiDisplay.setText(new Character(this.currentRecord
				.getJapaneseCharacter()).toString());
		this.heisigIDlabel.setText("Vol.1 nº " + currentRecord.getHeisigNumber());
		
		// Intentamos obtener los posibles on-yomi y kun-yomi del kanji
		currentReading = readingsDb.getByWriting(String.valueOf(currentRecord.getHeisigNumber()),
				statistics, reviewCC.getToTodayReading());
		
		currentKunYomis = 
				kunYomiDb.getLearnedKunYomiForHeisig(currentRecord.getHeisigNumber());
		
		// Si tiene on-yomis, mostramos el compuesto
		if(currentReading != null){
			jLabelCompound.setText(currentReading.getCompound());
			intermediateTime = System.nanoTime();
			currentStatus = STATUS_TEST_ONYOMI;
		}
		// Si no tiene on-yomi, pero sí tiene kun-yomis, los mostramos
		else if(!currentKunYomis.isEmpty()){
			endTime = System.nanoTime();
			currentStatus = STATUS_TEST_MEANING;
			StringBuilder sb = new StringBuilder("<html>");
			for(KunYomiReading kunYomiReading : currentKunYomis){
				sb.append(kunYomiReading.getRootWordAndInflection())
				.append("<br />");
			}
			sb.append("</html>");
			jLabelKunYomi.setText(sb.toString());
			startKunYomiTime = System.nanoTime();
		}
		// Si no tiene ni on-yomi ni kun-yomi tomamos el tiempo final
		else{
			endTime = System.nanoTime();
			currentStatus = STATUS_CHECK_KANJI;
		}

		enableButtonsForAnswer();
	}

	/**
	 * Cuando se presiona el botón de "Correcto" se comprueba en que estado de
	 * repaso se encuentra el kanji actual, se realizan las acciones pertinentes
	 * y se pasa al estado siguiente.
	 * 
	 * El orden de los estados es el siguiente:
	 * 1. STATUS_TEST_ONYOMI: El usuario debe escribir el on-yomi del kanji.
	 * 2. STATUS_CHECK_ONYOMI: El usuario comprueba el on-yomi y escribe la
	 *                         pronunciación del compuesto.
	 * 3. STATUS_CHECK_COMPOUND: El usuario comprueba la pronunciación del
	 *                           compuesto y está listo para el siguiente kanji.
	 * 4. STATUS_TEST_KUNYOMI: El usuario comprueba la pronunciación del
	 *                         compuesto y está listo para repasar los kun-yomi.
	 * 5. STATUS_TEST_MEANING: El usuario comprueba la pronunciación del 
	 *                         kun-yomi e intenta adivinar el significado.
	 * 6. STATUS_CHECK_MEANING: El usuario comprueba el significado del kun-yomi
	 *                          y está listo para el siguiente kanji.
	 * 7. STATUS_CHECK_KANJI: Se calcula el tiempo de escritura del kanji y se
	 *                        pasa al siguiente. 
	 */
	private void rightButtonActionPerformed() {
		StringBuilder sb;
		
		switch (currentStatus) {
		case STATUS_TEST_ONYOMI:
			endTime = System.nanoTime();
			jLabelOnYomi.setText(currentReading.getOnyomi());
			jLabelVol2Ref.setText("Vol.2 nº " + currentReading.getBook2reference());
			currentStatus = STATUS_CHECK_ONYOMI;
			break;
			
		case STATUS_CHECK_ONYOMI:
			jLabelCompoundReading.setText(currentReading.getCompoundReading());
			jLabelCompoundMeaning.setText("<html><p style=\"margin-left: 2px\">" +
					convertToMultiLine(currentReading.getCompoundMeaning(),40)
					+ "</p></html>");
			
			if(currentKunYomis.isEmpty()){
				currentStatus = STATUS_CHECK_COMPOUND;
			}
			else{
				currentStatus = STATUS_TEST_KUNYOMI;
			}
			break;
			
		case STATUS_TEST_KUNYOMI:
			sb = new StringBuilder("<html>");
			for(KunYomiReading kunYomiReading : currentKunYomis){
				sb.append(kunYomiReading.getRootWordAndInflection())
				.append("<br />");
			}
			sb.append("</html>");
			jLabelKunYomi.setText(sb.toString());
			startKunYomiTime = System.nanoTime();
			currentStatus = STATUS_TEST_MEANING;
			break;
			
		case STATUS_TEST_MEANING:
			endKunYomiTime = System.nanoTime();
			sb = new StringBuilder("<html>");
			for(KunYomiReading kunYomiReading : currentKunYomis){
				sb.append(kunYomiReading.getRootWordAndInflection())
				.append(" | ").append(kunYomiReading.getKunYomi())
				.append("<br />");
			}
			sb.append("</html>");
			jLabelKunYomi.setText(sb.toString());
			currentStatus = STATUS_CHECK_MEANING;
			break;
			
		case STATUS_CHECK_MEANING:
			sb = new StringBuilder("<html>");
			for(KunYomiReading kunYomiReading : currentKunYomis){
				sb.append(kunYomiReading.getRootWordAndInflection())
				.append(" | ").append(kunYomiReading.getKunYomi())
				.append(" | <span style='font-size:0.5em; font-family:Dialog;'>")
				.append(kunYomiReading.getMeaning())
				.append("</span><br />");
			}
			sb.append("</html>");
			jLabelKunYomi.setText(sb.toString());
			currentStatus = STATUS_CHECK_KANJI;
			break;

		default:
			calculateTimeAndGoToNextKanji(false);
			break;
		}
	}
	
	/**
	 * Cuando se presiona el botón de "Incorrecto" se añade el fallo a las
	 * estadísticas y se pasa al siguiente kanji.
	 */
	private void wrongButtonActionPerformed() {
		calculateTimeAndGoToNextKanji(true);
	}
	
	/**
	 * Se calcula el tiempo de escritura del kanji y se añade a sus
	 * estadisticas. Despues se calcula la dificultad del kanji y se pasa al
	 * siguiente o se va al panel de resultados si era el último.
	 * 
	 * @param wasWrong Indica si se falló en la escritura del kanji
	 */
	private void calculateTimeAndGoToNextKanji(boolean wasWrong){
		double elapsedTime;
		
		log.debug("--------------------------------------");
		
		if(wasWrong){
			elapsedTime = -1;
			log.debug("nº " + currentRecord.getHeisigNumber() + " was wrong.");
		}
		else{
			elapsedTime = endTime - startTime;
			if(!currentKunYomis.isEmpty()){
				elapsedTime += (endKunYomiTime - startKunYomiTime);
			}			
			
			elapsedTime = elapsedTime / 1000000000;
			log.debug("nº " + currentRecord.getHeisigNumber() + ": " + elapsedTime + "s");
		}
		

		KanjiData data = statistics.getKanjiData(currentRecord.getHeisigNumber());
		data.addNewTime(elapsedTime);
		
		if(!wasWrong){
			if(currentReading != null){
				elapsedTime = intermediateTime - startTime;
				elapsedTime = elapsedTime / 1000000000;
				log.debug("Writing Time: " + elapsedTime + "s");
				elapsedTime = endTime - intermediateTime;
				elapsedTime = elapsedTime / 1000000000;
				log.debug("Reading Time: " + elapsedTime + "s");

				data.addNewReadingReview(currentReading, true);
			}

			if(!currentKunYomis.isEmpty()){
				if(currentReading == null){
					elapsedTime = endTime - startTime;
					elapsedTime = elapsedTime / 1000000000;
					log.debug("Writing Time: " + elapsedTime + "s");
				}
				elapsedTime = endKunYomiTime - startKunYomiTime;
				elapsedTime = elapsedTime / 1000000000;
				log.debug("Kun-yomi Time: " + elapsedTime + "s");
			}
		}
		else{
			if(currentReading != null){
				data.addNewReadingReview(currentReading, false);
			}
		}

		mazoCC.updateDificulty(currentRecord.getHeisigNumber(), data, currentKunYomis.size());

		if(isLastKanji()){
			goToResultScreen();
		}
		else{
			resetDisplayAndShowNextKanji();
		}
	}

	/**
	 * Si un String es demasiado largo, lo divide en varias líneas para poder
	 * mostrarlo en un JLabel.
	 * 
	 * @param line El String a mostrar
	 * @return El String en formato HTML en multiples líneas si es necesario.
	 */
	private String convertToMultiLine(String line, int charsInLine) {
		if(line.length() <= charsInLine){
			return line;
		}

		StringBuilder sb = new StringBuilder("<html>");
		boolean finished = false;

		while(!finished){
			//Si la linea es menor que los caracteres por linea, la devolvemos
			if(line.length() <= charsInLine){
				sb.append(line);
				finished = true;
			}
			else{
				//Si existe un espacio en blanco antes del límite, cortamos por ahí
				int lastWhite = line.substring(0, charsInLine).lastIndexOf(" ");
				if(lastWhite != -1){
					sb.append(line.substring(0, lastWhite));
					line = line.substring(lastWhite + 1);
				}
				else{
					//No hay ningun blanco antes del límite, tenemos que cortar una palabra
					int index = charsInLine - 2;
					while(!isAVowel(line.charAt(index)) || nextTwoAreNotVowels(line,index)){
						index--;
						if(isAVowel(line.charAt(index)) &&
								!isAVowel(line.charAt(index+2))){
							index--;
						}
					}
					sb.append(line.substring(0, index + 1)).append("-");
					line = line.substring(index + 1);
				}
			}
			sb.append("<br>");
		}
		sb.append("</html>");
		
		return sb.toString();
	}
	
	/**
	 * Comprueba si los dos siguientes caracteres en la linea a partir del
	 * indice indicado, no son vocales.
	 * 
	 * @param line El texto en el que realizar la comprobación
	 * @param index El indice a partir del cual realizar la comprobación
	 * @return True si las dos siguientes NO son vocales, false en caso contrario.
	 */
	private boolean nextTwoAreNotVowels(String line, int index) {	
		if(line.length() < (index + 3)){
			return true;
		}
		else{
			return !isAVowel(line.charAt(index + 1)) &&
			!isAVowel(line.charAt(index + 2));
		}
	}

	/**
	 * Comprueba si el caracter es una vocal.
	 * 
	 * @param c El caracter a comprobar
	 * @return true si es una vocal, false en caso contrario
	 */
	private boolean isAVowel(char c) {
		return c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u';
	}
	
	/**
	 * Oculta el panel de la lección y muestra el panel de resultados.
	 */
	private void goToResultScreen(){
		this.setVisible(false);
		Container parent = this.getParent();
		parent.remove(this);
		parent.add(jPanelResultados);
		jPanelResultados.setVisible(true);
		jPanelResultados.showResultsAndSaveStatus();
		
		parent.validate();
	}

	/**
	 * Limpia todos los valores del panel y reestablece los botones para iniciar
	 * el repaso del siguiente kanji mostrando la siguiente palabra clave. 
	 * También inicia el tiempo de repaso y actualiza el número de kanjis 
	 * repasados en la lección.
	 */
	private void resetDisplayAndShowNextKanji() {		
		currentRecord = this.reviewCC.next();
		
		currentKunYomis.clear();
		
		kanjiDisplay.setText(" ");
		heisigIDlabel.setText(" ");
		jLabelCompound.setText(" ");
		jLabelOnYomi.setText(" ");
		jLabelCompoundReading.setText(" ");
		jLabelCompoundMeaning.setText(" ");
		jLabelVol2Ref.setText(" ");
		jLabelKunYomi.setText(" ");

		wrongButton.setEnabled(false);
		rightButton.setEnabled(false);

		checkButton.setEnabled(true);
		
		positionLabel.setText(reviewCC.getReviewPosition() + "/"
				+ reviewCC.getReviewSize());
		keywordLabel.setText(convertToMultiLine(this.currentRecord
				.getKeyword(),30));
		
		checkButton.requestFocus();
		startTime = System.nanoTime();
	}

	/**
	 * Deshabilita el botón de "Comprobar" y habilita los botones de "Correcto"
	 * y "Erroneo". Por defecto el foco lo tendrá el botón de "Correcto".
	 */
	private void enableButtonsForAnswer() {
		this.wrongButton.setEnabled(true);
		this.rightButton.setEnabled(true);

		this.checkButton.setEnabled(false);

		this.rightButton.requestFocus();
	}
	
	/**
	 * Compruba si estamos en el último kanji de la lección.
	 * 
	 * @return True si estamos en el último kanji, false en caso contrario.
	 */
	private boolean isLastKanji(){
		return reviewCC.isLastKanjiInReview();
	}
	
	/**
	 * Entrega el foco al botón de "Comprobar".
	 */
	public void giveFocusToCheckButton(){
		checkButton.requestFocus();
	}
}