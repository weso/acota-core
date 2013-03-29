package es.weso.acota.core.exceptions;

/**
 * An exception that occurs while installing and configuration Acota
 * @author César Luis Alvargonzález
 *
 */
public class AcotaConfigurationException extends Exception{

	private static final long serialVersionUID = 4631423426748612360L;

	/**
	 * Constructs a new AcotaConfigurationException with the 
	 * specified cause 
	 * @param cause the cause
	 */
	public AcotaConfigurationException(Exception cause){
		super(cause);
	}
	
	/**
	 * Constructs a new AcotaConfigurationException with 
	 * the specified detail message.
	 * @param text the detail message
	 */
	public AcotaConfigurationException(String message){
		super(message);
	}
	
	/**
	 * Constructs a new AcotaConfigurationException with the
	 *  specified detail message and cause.
	 * @param message the detail message
	 * @param cause the cause
	 */
	public AcotaConfigurationException(String message, Exception cause){
		super(message, cause);
	}
	
}
