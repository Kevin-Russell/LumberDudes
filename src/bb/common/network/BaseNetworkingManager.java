package bb.common.network;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import bb.common.network.message.Message;

/**
 * Parent class for the individual NetworkingManagers
 */
public abstract class BaseNetworkingManager {
	
	protected BlockingQueue<Message> inMessages_;
	
	protected BaseNetworkingManager() {
		// No need to ever construct this parent class.
		// Must be visible to child classes, though.
	}
	
	public void init() {
		// Set up the protected message queues
		inMessages_ = new ArrayBlockingQueue<Message>(10000);
	}
	
	public void pushInMsg(Message m) {
		inMessages_.add(m);
	}
	
	/**
	 * Pop Message from the outMessages Queue for TCP.  By default is a blocking call.
	 * One thing that we need to ensure is that if the system is currently terminating,
	 * then we should no longer block and instead return a null value
	 * @return The popped message, or null if termination is underway.
	 */
	public Message popInMsg() {
		try {
			return inMessages_.take();
		} catch (InterruptedException e) {
			// Need to figure out why we were interrupted. Return null for now.
			return null;
		}
	}
	
	public void handleDeadConnection(int deadId) {
		// By default, do nothing
	}
	
	protected abstract void sendHandler(Message m);
	
	protected abstract void recvHandler(Message m);
	
	public abstract boolean setupSockets();
	
	public abstract void addToOutQueue(Message m);
	
	public abstract void shutdown() throws Exception;
	
}
