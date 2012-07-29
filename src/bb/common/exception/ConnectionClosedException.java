package bb.common.exception;

/**
 * Exception for when a connection is detected to have been closed.
 * This is an unchecked exception (RuntimeException) because we may not
 * always need to check for this.
 */
public class ConnectionClosedException extends RuntimeException {

	private static final long serialVersionUID = -1468935443441928054L;
	
	public ConnectionClosedException() {
		super();
	}
	
	public ConnectionClosedException(String msg) {
		super(msg);
	}

}
