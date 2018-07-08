package com.konnichiwamundo.repasandoloskanji.view;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.konnichiwamundo.repasandoloskanji.controller.MazoControlCenter;
import com.konnichiwamundo.repasandoloskanji.controller.StatusTools;

public class ResultsPanel extends JPanel{
	
	private static final long serialVersionUID = 697517744267574426L;
	private JLabel resultsLabel;
	private JButton exitButton;
	
	private StatusTools statusTools;
	private MazoControlCenter mazoCC;
	
	public ResultsPanel(StatusTools statusTools, MazoControlCenter mazoCC){
		this.statusTools = statusTools;
		this.mazoCC = mazoCC;
		
		this.setLayout(new BorderLayout());
		
		resultsLabel = new JLabel();
		resultsLabel.setHorizontalAlignment(SwingConstants.CENTER);
		resultsLabel.setText("<html><h2 style='color:blue;'>Resultados</h1></html>");
		this.add(resultsLabel, BorderLayout.NORTH);
		
		resultsLabel = new JLabel();
		resultsLabel.setVerticalAlignment(SwingConstants.TOP);
		this.add(resultsLabel, BorderLayout.CENTER);
		
		exitButton = new JButton("Salir");
		exitButton.setText(MainFrame.getTextResource("MAINFRAME_QUIT"));
		exitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				System.exit(0);
			}
		});		
		this.add(exitButton, BorderLayout.SOUTH);
	}
	
	public void showResultsAndSaveStatus(){
		resultsLabel.setText(mazoCC.getResultsHTML());
		
		statusTools.saveStatus();
	}
}
