package es.weso.acota.core.exceptions;

/**
 * An exception that occurs while performing the REST call
 * @author César Luis Alvargonzález
 */
public class RESTException extends Exception {

	private static final long serialVersionUID = 3954496741296248493L;

	/**
	 * Constructs a new RESTException with the
	 *  specified detail message and cause.
	 * @param message the detail message
	 * @param cause the cause
	 */
	public RESTException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new RESTException with 
	 * the specified detail message.
	 * @param message the detail message
	 */
	public RESTException(String message) {
		super(message);
	}

	/**
	 * Constructs a new RESTException with the 
	 * specified cause
	 * @param cause the cause
	 */
	public RESTException(Throwable cause) {
		super(cause);
	}

	
}
