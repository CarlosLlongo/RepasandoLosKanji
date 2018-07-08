/**
 * Project    : Repasando los Kanji
 * Created on : 28 octubre 2010
 */

package com.konnichiwamundo.repasandoloskanji.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.konnichiwamundo.repasandoloskanji.controller.IntegrityChecker;
import com.konnichiwamundo.repasandoloskanji.controller.MazoControlCenter;
import com.konnichiwamundo.repasandoloskanji.controller.ReviewsControlCenter;
import com.konnichiwamundo.repasandoloskanji.controller.StatusTools;
import com.konnichiwamundo.repasandoloskanji.model.KanjiStatistics;
import com.konnichiwamundo.repasandoloskanji.model.KunYomiDatabase;
import com.konnichiwamundo.repasandoloskanji.model.Mazo;
import com.konnichiwamundo.repasandoloskanji.model.ReadingsDatabase;
import com.konnichiwamundo.repasandoloskanji.model.WritingsDatabase;
import com.konnichiwamundo.repasandoloskanji.persistence.UserData;

/**
 * Este es el frame principal de la aplicación. Inicialmente mostrará un panel
 * donde el usuario pueda introducir hasta donde ha aprendido, así como botones
 * para ir a otros paneles utiles como un buscador de kun-yomi o uno donde
 * poder introducir nuevos kanji.
 * 
 * @author Carlos Llongo
 *
 */
public class MainFrame extends JFrame {
	private static final long serialVersionUID = -1950577378176496032L;
	private final String VERSION_NUMBER = "2.4.0";
	
	private String toYesterdayWritings;
	private String toYesterdayReadings;
	private JTextField heisigToTextField;
	private JTextField readingToTextField;
	private JLabel writingToLabel;
	private JLabel readingToLabel;
	private JCheckBox checkBoxReviewLastLearned;
	private JButton startButton;
	private JButton searchByKunYomiButton;
	private JButton addNewKanjiButton;
	
	private WritingsDatabase writingsDb;
	private ReadingsDatabase readingsDb;
	private KunYomiDatabase kunYomiDb;
	private UserData preferences;
	
	private JPanel homePanel;
	private JPanel lessonLauncherPanel;
	private JPanel utilitiesPanel;
	private JPanel writingToPanel;
	private JPanel readingToPanel;
	private JPanel buttonPanel;
	
	private SearchByKunYomiPanel searchByKunYomiPanel;
	private AddKanjiPanel addKanjiPanel;
	
	private MazoControlCenter mazoCC;
	private KanjiStatistics statistics;
	private ReviewsControlCenter reviewsCC;
	private StatusTools statusTools;
	
	private final String japaneseFont = "MS Mincho";
	
	private static ResourceBundle currentResourceBundle;

	private MainFrame(boolean hasSplash) {
		preferences = UserData.getUserData();

		mazoCC = new MazoControlCenter();
		mazoCC.setEasyDeck(inicializarMazo("easy"));
		mazoCC.setMediumDeck(inicializarMazo("medium"));
		mazoCC.setHardDeck(inicializarMazo("hard"));

		//		readStatisticsFromFile();
		statistics = new KanjiStatistics();

		initialiceResourceBundle();

		writingsDb = new WritingsDatabase();
		readingsDb = new ReadingsDatabase();
		kunYomiDb = new KunYomiDatabase();
		
		statusTools = new StatusTools(preferences, mazoCC, statistics, kunYomiDb);


		initComponents();

		heisigToTextField.setText(preferences.getString("number_of_writings_learned","0"));
		toYesterdayWritings = heisigToTextField.getText();
		
		readingToTextField.setText(preferences.getString("number_of_readings_learned","0"));
		toYesterdayReadings = readingToTextField.getText();
		
		mazoCC.populateIfEmpty(Integer.parseInt(toYesterdayWritings));
		
		
		IntegrityChecker iCheck = new IntegrityChecker(
				Integer.parseInt(toYesterdayWritings), statistics, mazoCC, kunYomiDb);
		iCheck.startIntegrityCheck();
		statistics.showStatisticsOverview(readingsDb);
		
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				System.exit(0);
			}
		});
	}
	
	/**
	 * Inicializa el Mazo con los valores recuperados del fichero de preferencias
	 * 
	 * @param tipoMazo El valor de las preferencias para inicializar el Mazo
	 */
	private Mazo inicializarMazo(String tipoMazo){
		Mazo mazo = new Mazo(tipoMazo);
		
		int[] valoresHeisig = preferences.getIntArray(tipoMazo+"_remaining_kanji");
		if (valoresHeisig == null) {
			valoresHeisig = new int[0];
		}
		
		mazo.initializeRemaining(valoresHeisig);
		
		valoresHeisig = preferences.getIntArray(tipoMazo+"_store_kanji");
		if (valoresHeisig == null) {
			valoresHeisig = new int[0];
		}
		
		mazo.initializeStore(valoresHeisig);
		
		mazo.checkConsistency();

		return mazo;
	}

	/**
	 * Inicializa los componentes del panel inicial.
	 */
	private void initComponents() {
		lessonLauncherPanel = new JPanel();
		lessonLauncherPanel.setLayout(new BoxLayout(lessonLauncherPanel, BoxLayout.Y_AXIS));
		writingToPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		readingToPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		
		writingToLabel = new JLabel();
		heisigToTextField = new NumberField();
		startButton = new JButton();

		setTitle(getTextResource("MAINFRAME_TITLE") + " v." + VERSION_NUMBER);

		writingToLabel
				.setText(getTextResource("MAINFRAME_SELECT_BY_HEISIG_NUMBER_TO"));
		writingToPanel.add(writingToLabel);

		heisigToTextField.setText("jTextField1");
		heisigToTextField.setColumns(4);
		writingToPanel.add(heisigToTextField);
		
		readingToLabel = new JLabel("Lecturas aprendidas:");
		readingToPanel.add(readingToLabel);
		readingToTextField = new NumberField();
		readingToTextField.setColumns(4);
		readingToPanel.add(readingToTextField);
		
		checkBoxReviewLastLearned = new JCheckBox("Repasar últimos aprendidos");

		startButton.setText(getTextResource("MAINFRAME_START"));
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				startButtonActionPerformed();
			}
		});		
		buttonPanel.add(startButton);
		
		homePanel = new JPanel(new BorderLayout());
		
		searchByKunYomiPanel = new SearchByKunYomiPanel(homePanel, kunYomiDb);
		searchByKunYomiButton = new JButton("Buscar por kun-yomi");
		searchByKunYomiButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				searchByKunYomiButtonActionPerformed();
			}
		});
		
		addKanjiPanel = new AddKanjiPanel(homePanel, kunYomiDb, writingsDb);
		addNewKanjiButton = new JButton("Añadir un nuevo kanji");
		addNewKanjiButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				addNewKanjiButtonActionPerformed();
			}
		});
		
		lessonLauncherPanel.add(writingToPanel);
		lessonLauncherPanel.add(readingToPanel);
		lessonLauncherPanel.add(checkBoxReviewLastLearned);
		lessonLauncherPanel.add(buttonPanel);
		lessonLauncherPanel.add(Box.createVerticalStrut(400));
			
		homePanel.add(lessonLauncherPanel, BorderLayout.CENTER);
		
		utilitiesPanel = new JPanel(new GridLayout(1, 2));
		utilitiesPanel.add(searchByKunYomiButton);
		utilitiesPanel.add(addNewKanjiButton);
		homePanel.add(utilitiesPanel, BorderLayout.SOUTH);
		
		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(homePanel, BorderLayout.CENTER);
		
		setSize(580, 550);
	}

	/**
	 * Cuando se presiona el botón de iniciar la lección, se obtienen los datos
	 * introducidos por el usuario para crear el repaso, y se crea y muestra el 
	 * panel de la lección. 
	 */
	private void startButtonActionPerformed() {
		if(heisigToTextField.getText().length() == 0 ||
				readingToTextField.getText().length() == 0){
			JOptionPane
			.showMessageDialog(
					this,
					getTextResource("MAINFRAME_SELECT_BY_NUMBER_ERROR"),
					"error", 0);
		}
		else{
			LessonPanel lessonPanel = null;
			ResultsPanel resultsPanel = new ResultsPanel(statusTools, mazoCC);


			int toTodayWritings = Integer.parseInt(heisigToTextField.getText());
			int toTodayReadings = Integer.parseInt(readingToTextField.getText());
			int lastWritingsLearnedCount = Integer.parseInt(
					preferences.getString("last_writings_learned_cout","10"));
			int lastReadingsLearnedCount = Integer.parseInt(
					preferences.getString("last_readings_learned_cout","10"));
			System.out.println("lastReadingsLearnedCount: " + lastReadingsLearnedCount);
			System.out.println("lastWritingsLearnedCount: " + lastWritingsLearnedCount);

			statusTools.setWritingsLearned(heisigToTextField.getText());
			statusTools.setReadingsLearned(readingToTextField.getText());			

			System.out.println(toTodayWritings + " - " + toYesterdayWritings);

			reviewsCC = new ReviewsControlCenter(writingsDb, readingsDb);
			reviewsCC.initializeReview(Integer.parseInt(toYesterdayWritings), 
					toTodayWritings, Integer.parseInt(toYesterdayReadings), 
					toTodayReadings, mazoCC, statistics, kunYomiDb,
					checkBoxReviewLastLearned.isSelected(),
					lastWritingsLearnedCount, lastReadingsLearnedCount);
			
			statusTools.setReviewStatus(Integer.parseInt(toYesterdayWritings), 
					toTodayWritings, Integer.parseInt(toYesterdayReadings), 
					toTodayReadings, lastWritingsLearnedCount, 
					lastReadingsLearnedCount);

			lessonPanel = new LessonPanel(japaneseFont, reviewsCC,
					resultsPanel, mazoCC, readingsDb, statistics, kunYomiDb);

			getContentPane().remove(homePanel);
			getContentPane().add(lessonPanel,BorderLayout.CENTER);
			getContentPane().validate();
			homePanel.setVisible(false);
			lessonPanel.giveFocusToCheckButton();
			lessonPanel.setVisible(true);
		}
	}
	
	/**
	 * Muestra el panel de busqueda de kun-yomis.
	 */
	private void searchByKunYomiButtonActionPerformed() {
		switchToPanel(searchByKunYomiPanel);
	}
	
	/**
	 * Muestra el panel para añadir un nuevo kanji
	 */
	private void addNewKanjiButtonActionPerformed(){
		switchToPanel(addKanjiPanel);
	}
	
	/**
	 * Cambia el panel actual al nuevo indicado
	 * 
	 * @param panelToShow El panel que queremos mostrar.
	 */
	private void switchToPanel(JPanel panelToShow){
		getContentPane().remove(homePanel);
		getContentPane().add(panelToShow,BorderLayout.CENTER);
		getContentPane().validate();
		homePanel.setVisible(false);
		panelToShow.setVisible(true);
	}

	/**
	 * Inicializa los recursos de texto de la aplicación.
	 */
	private void initialiceResourceBundle() {
		String applicationPath = System.getProperty("user.dir");
		File resourcesFile = new File(applicationPath + "/data/text_resources.txt");
		
		try {
			currentResourceBundle = new PropertyResourceBundle(new FileInputStream(resourcesFile));
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	/**
	 * Obtiene el recurso de texto solicitado.
	 * 
	 * @param name El recurso de texto a recuperar
	 * @return El recurso de texto.
	 */
	public static String getTextResource(String name) {
		try {
			return currentResourceBundle.getString(name);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public static void main(String[] args) {
		MainFrame mframe = new MainFrame(false);
		mframe.setLocation(400, 445);
		mframe.setVisible(true);
	}
}