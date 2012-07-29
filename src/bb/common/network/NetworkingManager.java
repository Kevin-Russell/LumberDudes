package bb.common.network;

import bb.common.exception.NetworkingException;
import bb.common.network.message.Message;

/**
 * Facade interface for the Networking subsystem.
 */
public interface NetworkingManager {
	
	public void init(NetworkPair ipAndPort) throws NetworkingException;
	
	public void sendMsg(Message msg) throws NetworkingException;
	
	public Message recvMsg() throws NetworkingException;
	
	public void shutdown();
}
