package es.weso.acota.core.exceptions;

/**
 * An exception that occurs while performing the REST call
 * @author César Luis Alvargonzález
 */
public class AcotaRESTException extends Exception {

	private static final long serialVersionUID = 3954496741296248493L;

	/**
	 * Constructs a new AcotaRESTException with the
	 *  specified detail message and cause.
	 * @param message the detail message
	 * @param cause the cause
	 */
	public AcotaRESTException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new AcotaRESTException with 
	 * the specified detail message.
	 * @param message the detail message
	 */
	public AcotaRESTException(String message) {
		super(message);
	}

	/**
	 * Constructs a new acotaRESTException with the 
	 * specified cause
	 * @param cause the cause
	 */
	public AcotaRESTException(Throwable cause) {
		super(cause);
	}

	
}
