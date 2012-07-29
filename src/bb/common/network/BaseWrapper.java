package bb.common.network;

import bb.common.exception.NetworkingException;
import bb.common.network.message.Message;

/**
 * Base parent class for the facade wrappers.
 */
public abstract class BaseWrapper implements NetworkingManager {

	protected BaseNetworkingManager manager_;
	
	public abstract void init(NetworkPair ipAndPort) throws NetworkingException;

	public void sendMsg(Message msg) throws NetworkingException {
		// First, handle the message to be sent, if necessary
		manager_.sendHandler(msg);
		// Then add to queue
		manager_.addToOutQueue(msg);
	}

	public Message recvMsg() throws NetworkingException {
		// First, get the message from the queue
		Message msg = manager_.popInMsg();
		if (msg == null) return null;
		
		// Now, handle the message
		manager_.recvHandler(msg);
		
		return msg;
	}
	
	public void shutdown() {
		try {
			manager_.shutdown();
		} catch (Exception e) {
			// Print just to be verbose, but ignore the exception overall.
			e.printStackTrace();
		}
	}

}
