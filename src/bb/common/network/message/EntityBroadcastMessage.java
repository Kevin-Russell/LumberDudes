package bb.common.network.message;

import java.util.ArrayList;
import java.util.List;

import bb.common.EntityInfo;
import bb.common.GameProperties;
import bb.common.exception.PropertiesException;
import bb.common.network.ProtocolStrategy.NetworkingStrategyEnum;

public class EntityBroadcastMessage extends Message {

	private static final long serialVersionUID = 5708553251588469703L;
	
	private List<EntityInfo> entities;

	public EntityBroadcastMessage(List<EntityInfo> ents) {
		super(MsgType.BROADCAST_TYPE);
		this.entities = new ArrayList<EntityInfo>();
		for (EntityInfo ei : ents) {
			this.entities.add(ei.makeCopy());
		}
	}
	
	public EntityBroadcastMessage() {
		super(MsgType.BROADCAST_TYPE);
	}

	public List<EntityInfo> getEntities() {
		return entities;
	}

	public void setEntities(List<EntityInfo> entities) {
		this.entities = entities;
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
