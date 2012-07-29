package bb.common.network.message;

import bb.common.network.ProtocolStrategy.NetworkingStrategyEnum;

/**
 * Message used internally by server to detect dead clients (eg, killed connection).
 */
public class DeadClientMessage extends Message {

	private static final long serialVersionUID = 6317637484295263584L;
	
	private int entityId;
	
	public DeadClientMessage() {
		super(MsgType.DEAD_TYPE);
	}
	
	public DeadClientMessage(int eid) {
		super(MsgType.DEAD_TYPE);
		entityId = eid;
	}

	@Override
	public NetworkingStrategyEnum getStrategy() {
		// WE SHOULDN'T NEED TO USE THIS!
		return null;
	}

	public int getEntityId() {
		return entityId;
	}

	public void setEntityId(int entityId) {
		this.entityId = entityId;
	}

}
