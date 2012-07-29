package bb.common.network;

import java.io.IOException;

import bb.common.exception.NetworkingException;
import bb.common.network.message.Message;

/**
 * Interface for the socket protocol that we will use for our methods.
 * This interface and its implementors represents our Strategy Pattern usage.
 */
public interface ProtocolStrategy {
	
	public enum NetworkingStrategyEnum {TCP_S, UDP_S}
	
	public void send(SocketWrapper sock, Message msg) throws NetworkingException, IOException;
	public Message recv(SocketWrapper sock) throws NetworkingException, IOException;
}
