package bb.common.network;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import bb.common.exception.ConnectionClosedException;
import bb.common.exception.NetworkingException;
import bb.common.network.ProtocolStrategy.NetworkingStrategyEnum;
import bb.common.network.message.Message;

/**
 * Thread for sending messages. Able to handle sending either TCP, UDP, or even both.
 */
public class Sender implements Runnable {
	
	private BaseNetworkingManager manager_;	// Hmm...maybe we don't need this anymore
	private SocketWrapper tcpSocket_;
	private SocketWrapper udpSocket_;
	private BlockingQueue<Message> outQueue_;
	private int id_;	// For the server, this will be the clientID. For the client...it's nothing
	
	public Sender(BaseNetworkingManager m, SocketWrapper tcp, SocketWrapper udp, BlockingQueue<Message> q, int id) {
		manager_ = m;
		tcpSocket_ = tcp;
		udpSocket_ = udp;
		outQueue_ = q;
		id_ = id;
	}
	
	public void run() {
		
		// All work will be done here. Continuously loop and pop messages from the outMessages queue
		boolean willLoop = true;
		
		// Have both strategies ready
		ProtocolStrategy tcpProtocol = new TcpStrategy();
		ProtocolStrategy udpProtocol = new UdpStrategy();
		
		// Exit condition TBD
		while (willLoop) {
			try {
				Message msg = outQueue_.take();
				if (msg.getStrategy() == NetworkingStrategyEnum.TCP_S) {
					tcpProtocol.send(tcpSocket_, msg);
				} else {
					// Hopefully, we send a UDP message ONLY after we have set up the UDP socket
					udpProtocol.send(udpSocket_, msg);
				}
			} catch (NetworkingException e) {
				// Don't die, but print what happened. Perhaps we can ignore the exception
				e.printStackTrace();
			} catch (InterruptedException e) {
				break;
			} catch (ConnectionClosedException e) {
				// Closed connection, so we need to exit the receiver and terminate all connections to the client
				break;
			} catch (IOException e) {
				// Assume that the connection closed for all IOExceptions. We don't want to continue running if there is a networking problem.
				break;
			}
		}
		
		if (tcpSocket_ != null) tcpSocket_.closeSocket();
		if (udpSocket_ != null) udpSocket_.closeSocket();
		manager_.handleDeadConnection(id_);
	}
}
