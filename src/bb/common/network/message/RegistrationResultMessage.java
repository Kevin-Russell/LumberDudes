package bb.common.network.message;

import bb.common.network.ProtocolStrategy.NetworkingStrategyEnum;

public class RegistrationResultMessage extends Message {

	private static final long serialVersionUID = -7486869919655517091L;
	
	private int entityId;
	private String serverUdpIp;
	private int serverUdpPort;
	private int spriteId;	// ???
	private int clientId;
	
	public RegistrationResultMessage(int entityId, int clientId) {
		super(MsgType.REGRESULT_TYPE);
		this.entityId = entityId;
		this.clientId = clientId;
	}
	
	@Override
	public NetworkingStrategyEnum getStrategy() {
		return NetworkingStrategyEnum.TCP_S;
	}
	
	public int getEntityId() {
		return entityId;
	}
	public void setEntityId(int entityId) {
		this.entityId = entityId;
	}
	public String getServerUdpIp() {
		return serverUdpIp;
	}
	public void setServerUdpIp(String serverUdpIp) {
		this.serverUdpIp = serverUdpIp;
	}
	public int getServerUdpPort() {
		return serverUdpPort;
	}
	public void setServerUdpPort(int serverUdpPort) {
		this.serverUdpPort = serverUdpPort;
	}
	public int getSpriteId() {
		return spriteId;
	}
	public void setSpriteId(int spriteId) {
		this.spriteId = spriteId;
	}
	public int getClientId() {
		return clientId;
	}
	public void setClientId(int clientId) {
		this.clientId = clientId;
	}

}
