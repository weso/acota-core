package es.weso.acota.core.exceptions;

public class RESTException extends Exception {

	private static final long serialVersionUID = 3954496741296248493L;

	public RESTException() {
		super();
	}

	public RESTException(String message, Throwable cause) {
		super(message, cause);
	}

	public RESTException(String message) {
		super(message);
	}

	public RESTException(Throwable cause) {
		super(cause);
	}

	
}
