/**
 * Project    : Repasando los Kanji
 * Created on : 12 diciembre 2011
 */
package com.konnichiwamundo.repasandoloskanji.view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import com.konnichiwamundo.repasandoloskanji.controller.JapaneseConversionTools;
import com.konnichiwamundo.repasandoloskanji.controller.Log;
import com.konnichiwamundo.repasandoloskanji.model.EasyTable;
import com.konnichiwamundo.repasandoloskanji.model.KunYomiDatabase;
import com.konnichiwamundo.repasandoloskanji.model.KunYomiReading;

/**
 * Este panel permite buscar los kun-yomi y marcar aquellos que han sido
 * aprendidos.
 * 
 * @author Carlos Llongo
 *
 */
public class SearchByKunYomiPanel extends JPanel{
	private static final long serialVersionUID = 2340397478181090698L;
	private JTextField searchField;
	private JCheckBox onlyNewCheck;
	
	private JButton searchButton;
	private JButton saveChangesButton;
	private JButton goBackButton;
	private JButton selectAllButton;
	
	private EasyTable table;
	
	private JPanel top;
	private JPanel bottom;
	
	private JPanel homePanel;
	
	private KunYomiDatabase kunYomiDb;
	
	private Log log = new Log();
	
	public SearchByKunYomiPanel(JPanel homePanel, KunYomiDatabase kunYomiDb){
		this.homePanel = homePanel;
		this.kunYomiDb = kunYomiDb;
		
		initComponents();
	}

	/**
	 * Inicializa los componentes del panel.
	 */
	private void initComponents() {
		searchField = new JTextField(15);
		searchField.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e){
				if(e.getKeyCode() == KeyEvent.VK_ENTER){
					searchButtonActionPerformed();
					searchField.requestFocus();
				}
			}
		});
		searchButton = new JButton("Buscar");
		searchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				searchButtonActionPerformed();
			}
		});
		onlyNewCheck = new JCheckBox("Mostrar solo los no marcados");
		
		table = new EasyTable();
		String [] columnNames = {"Aprendido","Ref.", "Kanji+desi.", "kun-yomi", "Significado"};
		table.addColumns(columnNames);
		int [] columnWidths = {60, 40, 74, 84, 185};
		table.setColumnWidths(columnWidths);
		
		selectAllButton = new JButton("Seleccionar todos");
		selectAllButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				selectAllCheckboxes();
			}
		});
		
		saveChangesButton = new JButton("Guardar cambios");
		saveChangesButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				saveChangesButtonActionPerformed();
			}
		});
		
		goBackButton = new JButton("Volver");
		goBackButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				goBackButtonActionPerformed();
			}
		});
		
		this.setLayout(new BorderLayout());
		
		top = new JPanel(new FlowLayout());
		top.add(searchField);
		top.add(searchButton);
		top.add(onlyNewCheck);
		this.add(top, BorderLayout.NORTH);
		
		this.add(new JScrollPane(table), BorderLayout.CENTER);
		
		bottom = new JPanel(new FlowLayout());
		bottom.add(goBackButton);
		bottom.add(selectAllButton);
		bottom.add(saveChangesButton);
		this.add(bottom, BorderLayout.SOUTH);
	}
	
	/**
	 * Cuando se presiona el botón de guardar cambios, todas las filas marcadas
	 * son añadidas a la lista de kun-yomi aprendidos, y todos los que no están
	 * marcados se eliminan de la lista. Por último se guarda la lista a un
	 * fichero de texto.
	 */
	private void saveChangesButtonActionPerformed() {
		int rowCount = table.getRowCount();
		Vector<Object> rowData;
		
		for(int i = 0; i < rowCount; i++){
			rowData = table.getRow(i);
			
			if((Boolean)rowData.get(0)){
				kunYomiDb.addLearned((String)rowData.get(1));
			}
			else{
				kunYomiDb.removeLearned((String)rowData.get(1));
			}
		}
		
		kunYomiDb.saveLearnedList();
	}
	
	private void selectAllCheckboxes(){
		int rowCount = table.getRowCount();
		Vector<Object> rowData;
		
		for(int i = 0; i < rowCount; i++){
			rowData = table.getRow(i);
			if(!(Boolean)rowData.get(0)){
				table.setValueAt(new Boolean(true), i, 0);
			}
		}
	}
	
	/**
	 * Realiza una busqueda de todos los kun-yomi que empiezan por la cadena
	 * de busqueda. Si la cadena está escrita en hiragana, la busqueda se
	 * realiza directamente, pero si está escrita en romaji, primero se realiza
	 * una conversión a hiragana y despues se realiza la busqueda. Por último,
	 * se limpia la tabla y se añaden todos los resultados de la busqueda.
	 * 
	 * Si la cadena de búsqueda se deja vacía, se mostrarán todos los kun-yomi
	 * de la base de datos.
	 */
	private void searchButtonActionPerformed() {
		Vector<KunYomiReading> searchResults;
		
		if(searchField.getText().trim().length() != 0){
			if((int)searchField.getText().charAt(0) < 255){
				JapaneseConversionTools conversor = new JapaneseConversionTools();
				searchField.setText(
						conversor.convertRomajiToJapanese(searchField.getText(), "hiragana"));
			}
			
			searchResults = 
					kunYomiDb.searchFor(searchField.getText().trim());
			
			log.debug("Number of search results: " + searchResults.size());	
		}
		else{
			searchResults = kunYomiDb.getAllKunYomi();
		}
		
		table.removeAllRows();
		addSearchResultsToTable(searchResults);
	}
	
	/**
	 * Añade todos los resultados del vector a la tabla.
	 * 
	 * @param searchResults El vector de resultados con las lecturas kun-yomi.
	 */
	private void addSearchResultsToTable(Vector<KunYomiReading> searchResults) {
		Object [] rowData;
		for(KunYomiReading kunYomiReading : searchResults){
			if(!onlyNewCheck.isSelected() || !kunYomiDb.isLearned(kunYomiReading.getBook1Reference())){
				rowData = new Object[]{
				new Boolean(kunYomiDb.isLearned(kunYomiReading.getBook1Reference()))
				, kunYomiReading.getBook1Reference()
				, kunYomiReading.getRootWordAndInflection()
				, kunYomiReading.getKunYomi()
				, kunYomiReading.getMeaning()};

				table.addRow(rowData);
			}
		}
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
}
