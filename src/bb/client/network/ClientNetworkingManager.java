package bb.client.network;


import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import bb.common.exception.NetworkingException;
import bb.common.network.BaseNetworkingManager;
import bb.common.network.NetworkPair;
import bb.common.network.Receiver;
import bb.common.network.Sender;
import bb.common.network.SocketWrapper;
import bb.common.network.ProtocolStrategy.NetworkingStrategyEnum;
import bb.common.network.message.*;

/**
 * Networking manager for the Client application. 
 */
public class ClientNetworkingManager extends BaseNetworkingManager {
	
	private static ClientNetworkingManager thisInstance_ = null;
	
	private SocketWrapper tcpSock_ = null;
	private SocketWrapper udpSock_ = null;
	
	/** We need to know what our clientID, assigned by the server, is. */
	private int assignedClientId_;
	
	/** Only one sending queue/thread for the client. */
	private BlockingQueue<Message> outMessages_;
	private Thread senderThread_ = null;
	/** Only one thread for receiving TCP messages. */
	private Thread tcpReceiverThread_ = null;
	private Thread udpReceiverThread_ = null;

	// This networking manager needs to keep track of the server's TCP and UDP network credentials
	private NetworkPair serverTcp_ = null;
	private NetworkPair serverUdp_ = null;
	
	/**
	 * As a Singleton class, no public access to constructor
	 */
	private ClientNetworkingManager() {
		
	}
	
	/**
	 * Gets the single instance of ServerNetworkingManager.
	 *
	 * @return single instance of ServerNetworkingManager
	 */
	public static ClientNetworkingManager getInstance() {
		if (thisInstance_ == null) {
			thisInstance_ = new ClientNetworkingManager();
		}
		return thisInstance_;
	}
	
	public void setServerTcp(NetworkPair sTcp) {
		serverTcp_ = sTcp;
	}
	
	public void setServerUdp(NetworkPair sUdp) {
		serverUdp_ = sUdp;
	}
	
	/**
	 * Establishes the client's sockets.
	 * @return true, it successful.
	 */
	public boolean setupSockets() {
		try {
			Socket s = new Socket(InetAddress.getByName(serverTcp_.getIpAddr()), serverTcp_.getPort());
			// At this point, the socket has connected
			this.tcpSock_ = new SocketWrapper(s);
			// Also create Datagram socket at any port
			DatagramSocket d = new DatagramSocket(0, InetAddress.getLocalHost());
			this.udpSock_ = new SocketWrapper(d);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public void init() {
		super.init();
		outMessages_ = new ArrayBlockingQueue<Message>(10000);
		senderThread_ = new Thread(new Sender(this, tcpSock_, udpSock_, this.outMessages_, -1));
		tcpReceiverThread_ = new Thread(new Receiver(this, NetworkingStrategyEnum.TCP_S, tcpSock_, -1));
		udpReceiverThread_ = new Thread(new Receiver(this, NetworkingStrategyEnum.UDP_S, udpSock_, -1)); 
		senderThread_.start();
		tcpReceiverThread_.start();
	}
	
	protected void sendHandler(Message m) {
		switch (m.getType()) {
		case REG_TYPE:
			sendRegistrationMessage((RegistrationMessage)m);
			break;
		case TERMINATE_TYPE:
			sendTerminateMessage((TerminateMessage)m);
			break;
		default:
			// No need to do extra handling for other message types
		}
	}
	
	private void sendRegistrationMessage(RegistrationMessage m) {
		// Need to populate the message with the client's IP and port (couldn't be done outside of this subsytem)
		try {
			m.setClientUdpIp(InetAddress.getLocalHost().getHostAddress());
			m.setClientUdpPort(this.udpSock_.getUdpSocket().getLocalPort());
			m.setClientId(this.assignedClientId_);
		} catch (NetworkingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void sendTerminateMessage(TerminateMessage m) {
		// TODO Auto-generated method stub
		// We will need to implement some sort of termination control to end the client
	}

	@Override
	protected void recvHandler(Message m) {
		switch (m.getType()) {
		case REGRESULT_TYPE:
			recvRegResultMessage((RegistrationResultMessage)m);
			break;
		case CLIENTID_TYPE:
			recvClientIdMessage((ClientIdMessage)m);
			break;
		default:
			// No need to do extra handling for other message types
		}
	}
	
	private void recvRegResultMessage(RegistrationResultMessage msg) {
		// If it was a success, then we can set the server's listening UDP port
		if (msg.getEntityId() > 0) {
			// SUCCESS! Create the network pair for UDP, and change the current socket wrapper
			// so that it knows the ip and port to send messages to
			serverUdp_ = new NetworkPair(msg.getServerUdpIp(), msg.getServerUdpPort());
			udpSock_.setIpAddr(serverUdp_.getIpAddr());
			udpSock_.setPort(serverUdp_.getPort());
			try {
				udpSock_.getUdpSocket().connect(InetAddress.getByName(serverUdp_.getIpAddr()), serverUdp_.getPort());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// Also because UDP is ready, we can start the thread
			this.udpReceiverThread_.start();
		}
	}
	
	private void recvClientIdMessage(ClientIdMessage m) {
		this.assignedClientId_ = m.getClientId();
	}
	
	public SocketWrapper getTcp() {
		return this.tcpSock_;
	}
	
	public void addToOutQueue(Message m) {
		this.outMessages_.add(m);
	}
	
	public SocketWrapper getUdp() {
		return this.udpSock_;
	}

	@Override
	public void shutdown() throws Exception {
		// Interrupt and close all threads
		Thread[] threads = {senderThread_, tcpReceiverThread_, udpReceiverThread_};
		for (Thread t : threads) {
			if (t.isAlive()) {
				t.interrupt();
				t.join();
			}
		}
		// Close socket connections (this may have been done already, though)
		tcpSock_.closeSocket();
		udpSock_.closeSocket();
	}
	
}
