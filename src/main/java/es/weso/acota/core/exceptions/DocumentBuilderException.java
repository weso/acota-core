package es.weso.acota.core.exceptions;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 * This class models an exception occurred during the DOM creation.
 */
public class DocumentBuilderException extends Exception {

	private static final long serialVersionUID = 634129594668717812L;

	/**
	 * One-parameter Constructor
	 * @param e Models an exception occurred during the DOM creation.
	 */
	public DocumentBuilderException(DocumentBuilderException e) {
        super(e);
    }

	/**
	 * One-parameter Constructor
	 * @param e Signals that an I/O exception of some sort has occurred. 
	 */
    public DocumentBuilderException(IOException e) {
        super(e);
    }

    /**
     * One-parameter Constructor
     * @param e Indicates a serious configuration error.
     */
    public DocumentBuilderException(ParserConfigurationException e) {
        super(e);
    }

    /**
     * One-parameter Constructor
     * @param e Encapsulate a general SAX error or warning.
     */
    public DocumentBuilderException(SAXException e) {
        super(e);
    }

}
