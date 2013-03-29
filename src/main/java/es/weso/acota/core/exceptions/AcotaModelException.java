package es.weso.acota.core.exceptions;
/**
 * 
 * @author César Luis Alvargonzález
 *
 */
public class AcotaModelException extends RuntimeException {

	private static final long serialVersionUID = -2568767031496911589L;

	public AcotaModelException(Exception e) {
		super(e);
	}

	public AcotaModelException(String string) {
		super(string);
	}

	public AcotaModelException(Exception e, String string) {
		super(string, e);
	}
	
}
