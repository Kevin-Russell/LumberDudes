package bb.common.network.message;

import bb.common.ActionType;
import bb.common.GameProperties;
import bb.common.exception.PropertiesException;
import bb.common.network.ProtocolStrategy.NetworkingStrategyEnum;

public class ActionMessage extends Message {

	private static final long serialVersionUID = -3402358382708580990L;
	
	private int entityId;
	private ActionType action;
	public ActionMessage(int entityId, ActionType action) {
		super(MsgType.ACTION_TYPE);
		this.entityId = entityId;
		this.action = action;
	}
	public ActionMessage() {
		super(MsgType.ACTION_TYPE);
	}
	public int getEntityId() {
		return entityId;
	}
	public void setEntityId(int entityId) {
		this.entityId = entityId;
	}
	public ActionType getAction() {
		return action;
	}
	public void setAction(ActionType action) {
		this.action = action;
	}
	
	@Override
	public NetworkingStrategyEnum getStrategy() {
		try {
			return GameProperties.getInstance().getSocketStrategy();
		} catch (PropertiesException e) {
			// THIS WILL NOT HAPPEN!
			e.printStackTrace();
			System.out.println("Using TCP by default.");
			return NetworkingStrategyEnum.TCP_S;
		}
	}
}
