package utils;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

// TODO: Auto-generated Javadoc
/**
 * The Class Validador.
 */
public class Validador extends PlainDocument {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The limite. */
	private int limite;

	/**
	 * Instantiates a new validador.
	 *
	 * @param limite the limite
	 */
	public Validador(int limite) {
		super();
		this.limite = limite;
	}

	/**
	 * Insert string.
	 *
	 * @param ofs the ofs
	 * @param str the str
	 * @param a the a
	 * @throws BadLocationException the bad location exception
	 */
	public void insertString(int ofs, String str, AttributeSet a) throws BadLocationException {
		if ((getLength() + str.length()) <= limite) {
			super.insertString(ofs, str, a);
		}
	}

}
