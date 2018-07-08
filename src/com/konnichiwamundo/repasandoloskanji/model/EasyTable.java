/**
 * Project    : Repasando los Kanji
 * Created on : 5 diciembre 2011
 */

package com.konnichiwamundo.repasandoloskanji.model;

import java.util.Vector;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

/**
 * Esta clase permite la creación rápida y sencilla de una tabla, así como
 * ofrecer métodos para manejar sus columnas sin tener que trabajar con un
 * modelo de tabla.
 * 
 * @author Carlos Llongo
 *
 */

public class EasyTable extends JTable{
	
	private static final long serialVersionUID = 5796892644429810527L;
	private MyTableModel tableModel;
	
	public EasyTable(){
		tableModel = new MyTableModel();
		
		this.setModel(tableModel);
	}
	
	/**
	 * Añade una columna a la tabla
	 * 
	 * @param columnName El nombre de la columna
	 */
	public void addColumn(String columnName){
		tableModel.addColumn(columnName);
	}
	
	/**
	 * Añade una columna a la tabla y le asigna un ancho preferido
	 * 
	 * @param columnName El nombre de la columna
	 * @param preferredWidth El ancho preferido para la columna
	 */
	public void addColumn(String columnName, int preferredWidth){
		tableModel.addColumn(columnName);
		
		this.getColumnModel().getColumn(tableModel.getColumnCount() - 1)
		.setPreferredWidth(preferredWidth);
	}
	
	/**
	 * Añade varias columnas a la tabla, segun el número de valores en el 
	 * parámetro de entrada.
	 * 
	 * @param columnNames Los nombres de las columnas a añadir.
	 */
	public void addColumns(String [] columnNames){
		for(String columnName : columnNames){
			tableModel.addColumn(columnName);
		}
	}
	
	/**
	 * Establece el tamaño preferido de multiples columnas de la tabla.
	 * 
	 * @param columnWidths Los tamaños preferidos para las columnas
	 */
	public void setColumnWidths(int[] columnWidths) {
		TableColumnModel columnModel = this.getColumnModel();
		for(int i = 0; i < columnWidths.length; i++){
			columnModel.getColumn(i).setPreferredWidth(columnWidths[i]);
		}
	}
	
	/**
	 * Elimina todas las columnas de la tabla.
	 */
	public void removeAllRows(){
		tableModel.setRowCount(0);
	}
	
	/**
	 * Añade una fila a la tabla con los datos indicados.
	 * 
	 * @param rowData Los datos que deben ser añadidos a la tabla.
	 */
	public void addRow(Object [] rowData){
		tableModel.addRow(rowData);
	}
	
	/**
	 * Obtiene el numero de filas en la tabla.
	 */
	public int getRowCount(){
		return tableModel.getRowCount();
	}
	
	/**
	 * Obtiene los valores de una fila de la tabla.
	 * 
	 * @param rowIndex El indice de la fila
	 * @return Los valores de la fila de la tabla
	 */
	public Vector<Object> getRow(int rowIndex) {
		return tableModel.getRow(rowIndex);	
	}
	
	private class MyTableModel extends DefaultTableModel {
		
		private static final long serialVersionUID = 6910326113876687166L;

		public MyTableModel(){
			super();
		}
		
        /*
         * JTable uses this method to determine the default renderer/
         * editor for each cell.
         */
        @SuppressWarnings({ "unchecked", "rawtypes" })
		public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }
        
        @SuppressWarnings("unchecked")
		public Vector<Object> getRow(int rowIndex){
        	return (Vector<Object>)dataVector.get(rowIndex);
        }
    }
}
