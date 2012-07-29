package bb.common.exception;

/**
 * Exception for EITHER marshalling or unmarshalling problems.
 */
public class MarshalUnmarshalException extends Exception {


	private static final long serialVersionUID = -4499838436487771882L;

	public MarshalUnmarshalException() {
		super();
	}
	
	public MarshalUnmarshalException(String msg) {
		super(msg);
	}
}
