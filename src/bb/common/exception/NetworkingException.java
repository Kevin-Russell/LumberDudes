package bb.common.exception;

/**
 * General Exception for problems happening in the Networking subsystem
 */
public class NetworkingException extends Exception {
	
	private static final long serialVersionUID = 3050820364369001773L;

	public NetworkingException() {
		super();
	}
	
	public NetworkingException(String msg) {
		super(msg);
	}
	
}
