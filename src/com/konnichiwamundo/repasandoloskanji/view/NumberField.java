package com.konnichiwamundo.repasandoloskanji.view;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

public class NumberField extends JTextField {
	/**
	 * 
	 */
	private static final long serialVersionUID = -583885024523323456L;

	public NumberField(int cols) {
		super(cols);
	}

	public NumberField() {
	}

	protected Document createDefaultModel() {
		return new UpperCaseDocument();
	}

	static class UpperCaseDocument extends PlainDocument {
		/**
		 * 
		 */
		private static final long serialVersionUID = -8698963071607806341L;

		public void insertString(int offs, String str, AttributeSet a)
		throws BadLocationException {
			if (str == null) {
				return;
			}
			char[] upper = str.toCharArray();
			char[] out = new char[upper.length];
			int n = 0;
			for (int i = 0; i < upper.length; i++) {
				if (Character.isDigit(upper[i])) {
					out[(n++)] = upper[i];
				}
			}
			super.insertString(offs, new String(out, 0, n), a);
		}
	}
}