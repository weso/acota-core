package es.weso.acota.core.exceptions;

/**
 * This class models an exception occurred during the DOM creation.
 */
public class DocumentBuilderException extends Exception {

	private static final long serialVersionUID = 634129594668717812L;


	/**
	 * Constructs a new DocumentBuilderException with the
	 *  specified detail message and cause.
	 * @param message the detail message
	 * @param cause the cause
	 */
	public DocumentBuilderException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new DocumentBuilderException with 
	 * the specified detail message.
	 * @param message the detail message
	 */
	public DocumentBuilderException(String message) {
		super(message);
	}

	/**
	 * Constructs a new DocumentBuilderException with the 
	 * specified cause
	 * @param cause the cause
	 */
	public DocumentBuilderException(Throwable cause) {
		super(cause);
	}

}
