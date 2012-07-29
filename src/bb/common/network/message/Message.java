package bb.common.network.message;

import java.io.Serializable;

import bb.common.network.ProtocolStrategy;
import bb.common.network.UdpStrategy;
import bb.common.network.ProtocolStrategy.NetworkingStrategyEnum;
import bb.common.network.TcpStrategy;

public abstract class Message implements Serializable {
	
	/**
	 * UID for Serializable
	 */
	private static final long serialVersionUID = 7245778081795675591L;

	public enum MsgType { REG_TYPE, REGRESULT_TYPE, ACTION_TYPE, BROADCAST_TYPE,
		TERMINATE_TYPE, START_TYPE, CLIENTID_TYPE, DEAD_TYPE }
		
	protected MsgType type_;
	
	// Transient because it shouldn't be serialized.
	//private transient ProtocolStrategy protocol_;
	
	protected long timestamp_;
	
	/**
	 * All subclasses need to call this.
	 * @param type
	 */
	protected Message(MsgType type) {
		this.type_ = type;
	}
	
	/**
	 * Empty constructor
	 */
	protected Message() {}
	
//	public static Message constructByType(MsgType type) {
//		switch (type) {
//		case REG_TYPE:
//			return new RegistrationMessage();
//		case REGRESULT_TYPE:
//			return new RegistrationResultMessage();
//		case ACTION_TYPE:
//			return new ActionMessage();
//		case BROADCAST_TYPE:
//			return new EntityBroadcastMessage();
//		case TERMINATE_TYPE:
//			return new TerminateMessage();
//		case START_TYPE:
//			return new StartGameMessage();
//		case CLIENTID_TYPE:
//			return new ClientIdMessage();
//		default:
//			System.out.println("WARNING: Unrecognizable message type in constructByType()");
//			return null;
//		}
//	}
	
	protected ProtocolStrategy determineProtocol() {
		switch (this.getStrategy()) {
		case TCP_S:
			return new TcpStrategy();
		case UDP_S:
			return new UdpStrategy();
		default:
			return null;	
		}
	}
	
	public MsgType getType() {
		return this.type_;
	}
	
	public abstract NetworkingStrategyEnum getStrategy();
}
