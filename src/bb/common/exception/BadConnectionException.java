package bb.common.exception;

/**
 * Exception that encompasses connection problems.
 */
public class BadConnectionException extends Exception {

	private static final long serialVersionUID = 8775260754619506415L;

	public BadConnectionException() {
		super();
	}
	
	public BadConnectionException(String msg) {
		super(msg);
	}
	
}
