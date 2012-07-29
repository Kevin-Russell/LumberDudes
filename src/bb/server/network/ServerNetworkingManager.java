package bb.server.network;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import bb.common.network.BaseNetworkingManager;
import bb.common.network.ProtocolStrategy.NetworkingStrategyEnum;
import bb.common.network.Receiver;
import bb.common.network.Sender;
import bb.common.network.SocketWrapper;
import bb.common.network.message.ClientIdMessage;
import bb.common.network.message.DeadClientMessage;
import bb.common.network.message.EntityBroadcastMessage;
import bb.common.network.message.Message;
import bb.common.network.message.RegistrationMessage;
import bb.common.network.message.RegistrationResultMessage;
import bb.common.network.message.StartGameMessage;
import bb.common.network.message.TerminateMessage;

/**
 * Networking manager for the Server application. 
 */
public class ServerNetworkingManager extends BaseNetworkingManager {
	
	private static int availableId = 1;
	
	private static ServerNetworkingManager thisInstance_ = null;
	
	private ServerSocket tcpListeningSocket_ = null;
	
	private int listeningPort_;
	
	private Thread listeningThread = null;
	
	// ALL MAPS HAVE clientId AS THE KEY
	private Map<Integer, SocketWrapper> clientTcpSockets_;
	private Map<Integer, SocketWrapper> clientUdpSockets_; 

	private Map<Integer, Thread> clientSendThreads_;
	private Map<Integer, Thread> clientTcpRecvThreads_;
	private Map<Integer, Thread> clientUdpRecvThreads_;
	
	private Map<Integer, Long> currentTimestamps_;
	
	/** Map the client network pair (string representation) to its out queue. */
	private Map<Integer, BlockingQueue<Message>> outMessageMap_;
	
	/** Keep a queue of client IDs that have quit so that the server knows to close resources */
	private BlockingQueue<Integer> deadClientQueue_;
	
	/** Map clientIDs to entityIDs, and vice-versa. ONE-TO-ONE MAPPING */
	private Map<Integer, Integer> clientToEntityId_;
	private Map<Integer, Integer> entityToClientId_;
	
	private List<Integer> alreadyDeadClientIds_;
	
	/**
	 * As a Singleton class, no public access to constructor
	 */
	private ServerNetworkingManager() {
		clientTcpSockets_ = new ConcurrentHashMap<Integer, SocketWrapper>();
		clientUdpSockets_ = new ConcurrentHashMap<Integer, SocketWrapper>();
		clientSendThreads_ = new ConcurrentHashMap<Integer, Thread>();
		clientTcpRecvThreads_ = new ConcurrentHashMap<Integer, Thread>();
		clientUdpRecvThreads_ = new ConcurrentHashMap<Integer, Thread>();
		currentTimestamps_ = new ConcurrentHashMap<Integer, Long>();
		outMessageMap_ = new ConcurrentHashMap<Integer, BlockingQueue<Message>>();
		deadClientQueue_ = new ArrayBlockingQueue<Integer>(100);
		clientToEntityId_ = new ConcurrentHashMap<Integer, Integer>();
		entityToClientId_ = new ConcurrentHashMap<Integer, Integer>();
		alreadyDeadClientIds_ = new ArrayList<Integer>();
	}
	
	/**
	 * Gets the single instance of ServerNetworkingManager.
	 *
	 * @return single instance of ServerNetworkingManager
	 */
	public static ServerNetworkingManager getInstance() {
		if (thisInstance_ == null) {
			thisInstance_ = new ServerNetworkingManager();
		}
		return thisInstance_;
	}
	
	/**
	 * Establishes the client's sockets.
	 * @return true, it successful.
	 */
	public boolean setupSockets() {
		try {
			// Setup the server's listener socket and start listening thread
			this.tcpListeningSocket_ = new ServerSocket(listeningPort_);
			Listener listener = new Listener();
			this.listeningThread = new Thread(listener);
			
			listeningThread.start();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public void init() {
		super.init();
		
	}
	
	protected void sendHandler(Message m) {
		switch (m.getType()) {
		case REGRESULT_TYPE:
			sendRegistrationResultMessage((RegistrationResultMessage)m);
			break;
		case BROADCAST_TYPE:
			sendBroadcastMessage((EntityBroadcastMessage)m);
			break;
		case TERMINATE_TYPE:
			sendTerminateMessage((TerminateMessage)m);
			break;
		case START_TYPE:
			sendStartMessage((StartGameMessage)m);
			break;
		default:
			// No need to handle other types of messages
		}
	}
	
	private void sendStartMessage(StartGameMessage m) {
		// Send message to all clients
		addToAllOutQueues(m);
	}

	private void sendTerminateMessage(TerminateMessage m) {
		// Send message to all active clients. isWinner is only true for the one whose entityId matches the winner
		Integer winnerClientId = entityToClientId_.get(Integer.valueOf(m.getWinnerEntityId()));
		TerminateMessage winMsg = new TerminateMessage(true, m.getWinnerEntityId());
		outMessageMap_.get(winnerClientId).add(winMsg);
		// Add to all other queues
		TerminateMessage loseMsg = new TerminateMessage(false, m.getWinnerEntityId());
		Set<Entry<Integer, BlockingQueue<Message>>> entries = outMessageMap_.entrySet();
		for (Entry<Integer, BlockingQueue<Message>> entry : entries) {
			if (entry.getKey().intValue() != winnerClientId) {
				entry.getValue().add(loseMsg);
			}
		}
	}

	private void sendBroadcastMessage(EntityBroadcastMessage m) {
		// TODO Need to send to all connected clients
		
		addToAllOutQueues(m);
	}

	private void sendRegistrationResultMessage(RegistrationResultMessage m) {
		SocketWrapper udp = clientUdpSockets_.get(m.getClientId());
		if (m.getEntityId() > 0) {
			// SUCCESS! We can now create and start the udp receiving thread for the client 
			Receiver udpReceiver = new Receiver(thisInstance_, NetworkingStrategyEnum.UDP_S, udp, m.getClientId());
			Thread udpThread = new Thread(udpReceiver);
			this.clientUdpRecvThreads_.put(m.getClientId(), udpThread);
			// Also, set the server's UDP credentials into the message. One UDP socket per client
			SocketWrapper clientUdpSocket = this.clientUdpSockets_.get(m.getClientId()); 
			try {
				m.setServerUdpIp(InetAddress.getLocalHost().getHostAddress());
				m.setServerUdpPort(clientUdpSocket.getUdpSocket().getLocalPort());
			} catch (Exception e) {
				// THIS SHOULDN'T HAPPEN!
				e.printStackTrace();
			}
			clientToEntityId_.put(m.getClientId(), m.getEntityId());
			entityToClientId_.put(m.getEntityId(), m.getClientId());
			udpThread.start();
		} else {
			// FAILURE! We need to clear the ip/port credentials in the udp socket wrapper
			udp.setIpAddr(null);
			udp.setPort(null);
		}
		// Send message to the single queue
		outMessageMap_.get(m.getClientId()).add(m);
	}
	
	/**
	 * Server needed to subclass the popInMsg function. We need a non-blocking POP.
	 * @return The popped message from the inMessages queue, or null if not found.
	 */
	public Message popInMsg() {
		Integer deadClientId = popDeadClient();
		if (deadClientId != null) {
			// We need to create a DeadClientMessage and return that instead of this queue
			int entityId = clientToEntityId_.get(deadClientId).intValue();
			return new DeadClientMessage(entityId);
			// TODO: Also remove the clientID and entityID-related resources from the manager?
		} else {
			return inMessages_.poll();
		}
	}
	
	@Override
	protected void recvHandler(Message m) {
		switch (m.getType()) {
		case REG_TYPE:
			recvRegistrationMessage((RegistrationMessage)m);
			break;
		case DEAD_TYPE:
			System.out.println("DEBUG: Receiving a DeadClientMessage");
			break;
		default:
			// No need to handle other types of messages
		}
	}
	
	private void recvRegistrationMessage(RegistrationMessage m) {
		// We can retrieve the udp socket by searching using the client's generated id
		SocketWrapper udp = clientUdpSockets_.get(m.getClientId());
		udp.setIpAddr(m.getClientUdpIp());
		udp.setPort(m.getClientUdpPort());
	}
	
	/**
	 * Compare the incoming message's timestamp to the current timestamp in the local DB,
	 * in order to determine if the message is "in-order"
	 * @param m The message.
	 * @return True if the networking manager can accept the message as "in-order". 
	 */
	@SuppressWarnings("unused")
	private boolean handleOutOfOrder(Message m) {
		// Somehow, get the client's ID and the action timestamp from the Message.
		int id = 1;		// DEFAULT
		long msgTs = 1L;	// DEFAULT
		long curTs = this.currentTimestamps_.get(Integer.valueOf(id));
		if (curTs >= msgTs) {
			// Incoming message is arriving late.
			// Also, don't handle messages arriving at the same instant (impossible to ensure consistency at that point).
			return false;
		}
		// Otherwise, the incoming message is "in-order" and we can accept it.
		return true;
	}
	
	public void setListeningPort(int p) {
		this.listeningPort_ = p;
	}
	
	public void addToOutQueue(Message m) {
		// ...Do nothing? This may be handled by the send handler.
	}
	
	/**
	 * Add a message to all active outMessage queues
	 */
	private void addToAllOutQueues(Message m) {
		Collection<BlockingQueue<Message>> queues = outMessageMap_.values();
		for (BlockingQueue<Message> bq : queues) {
			bq.add(m);
		}
	}
	
	private int generateId() {
		return availableId++;
	}
	
	@Override
	public synchronized void handleDeadConnection(int deadId) {
		// Only push the dead clientId if it was not already added to queue before
		Integer obj = Integer.valueOf(deadId);
		if (!alreadyDeadClientIds_.contains(obj)) {
			pushDeadClient(obj);
			alreadyDeadClientIds_.add(obj);
		}
	}
	
	// Functions for using the queue for dead clients
	private void pushDeadClient(Integer cid) {
		try {
			this.deadClientQueue_.put(cid);
		} catch (InterruptedException e) {
			System.err.println("ERROR IN pushDeadClient(int). Probably a game shutdown.");
			e.printStackTrace();
		}
	}
	
	private Integer popDeadClient() {
		return this.deadClientQueue_.poll();
	}
	
	@Override
	// TODO: Low priority to properly implement this!
	public void shutdown() throws Exception {
		// Interrupt and close all threads
		Thread[] threads = {};
		for (Thread t : threads) {
			if (t.isAlive()) {
				t.interrupt();
			}
		}
	}
	
	/**
	 * Private class for listening loop required in order to access members of the
	 * {@link ServerNetworkingManager} class.
	 */
	private class Listener implements Runnable {
		
		public void run() {
			connectionListener();
		}
		
		/**
		 * Connection listener that continuously loops waiting to accept new
		 * connections to clients.
		 */
		public void connectionListener() {
			// TODO: Exit condition
			while (true) {
				try {
					// For each new socket created, create the threads for tcp
					Socket newSock = tcpListeningSocket_.accept();
					int generatedClientId = generateId();					
					
					SocketWrapper clientTcpSocket = new SocketWrapper(newSock);
					SocketWrapper clientUdpSocket = new SocketWrapper(new DatagramSocket());
					clientTcpSockets_.put(generatedClientId, clientTcpSocket);
					clientUdpSockets_.put(generatedClientId, clientUdpSocket);	// The ONLY UDP socket per client
					// Later, we need to add in the ip and port to the client's udp
					
					BlockingQueue<Message> outMsgQueue = new ArrayBlockingQueue<Message>(10000);
					outMessageMap_.put(generatedClientId, outMsgQueue);
					Sender sender = new Sender(ServerNetworkingManager.this, clientTcpSocket, clientUdpSocket, outMsgQueue, generatedClientId);
					Thread sendThread = new Thread(sender);
					Receiver receiver = new Receiver(ServerNetworkingManager.this, NetworkingStrategyEnum.TCP_S, clientTcpSocket, generatedClientId);
					Thread recvThread = new Thread(receiver);
					
					// Store threads globally. Later, we will join the threads that have a failed registration
					clientSendThreads_.put(generatedClientId, sendThread);
					clientTcpRecvThreads_.put(generatedClientId, recvThread);
					sendThread.start();
					recvThread.start();
					
					// Instantly send the client id to the client
					Message cm = new ClientIdMessage(generatedClientId);
					outMsgQueue.add(cm);
					
					// TODO: After we have enough successful registrations, we should no longer loop
				} catch (IOException e) {
					System.out.println("Problem with accepting client connection");
				}
			}
		}
		
	}
	
}
