package bb.common.network;

import java.io.IOException;

import bb.common.exception.ConnectionClosedException;
import bb.common.exception.NetworkingException;
import bb.common.network.ProtocolStrategy.NetworkingStrategyEnum;
import bb.common.network.message.Message;

/**
 * Thread for receiving messages. Each thread is meant only to receive either UDP or TCP messages.
 */
public class Receiver implements Runnable {
	
	private BaseNetworkingManager manager_;
	private NetworkingStrategyEnum strategy_;
	private SocketWrapper socket_;
	private int id_;
	
	public Receiver(BaseNetworkingManager m, NetworkingStrategyEnum s, SocketWrapper sock, int id) {
		manager_ = m;
		strategy_ = s;
		socket_ = sock;
		id_ = id;
	}
	
	public void run() {
		
		// All work will be done here. Continuously loop and push received messages onto
		// the inMessages queue
		boolean willLoop = true;

		// Determine how we are going to receive messages, and through which socket
		ProtocolStrategy protocol;
		if (strategy_ == NetworkingStrategyEnum.TCP_S) {
			protocol = new TcpStrategy();
		} else {
			protocol = new UdpStrategy();
		}

		// Exit condition TBD
		while (willLoop) {
			try {
				Message msg = protocol.recv(socket_);
				manager_.pushInMsg(msg);
			} catch (NetworkingException e) {
				// Don't die, but print what happened. Perhaps we can ignore the exception
				e.printStackTrace();
			} catch (ConnectionClosedException e) {
				// Closed connection, so we need to exit the receiver and terminate all connections to the client
				break;
			} catch (IOException e) {
				// Assume that the connection closed for all IOExceptions. We don't want to continue running if there is a networking problem.
				break;
			}
		}
		
		socket_.closeSocket();
		manager_.handleDeadConnection(id_);
	}
}
