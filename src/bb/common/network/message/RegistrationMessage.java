package bb.common.network.message;

import bb.common.network.ProtocolStrategy.NetworkingStrategyEnum;

public class RegistrationMessage extends Message {
	
	private static final long serialVersionUID = 9015126569059432006L;
	
	// The client is sending UDP credentials as the parameter for registration. However,
	// because no ID has been set yet for the client, it also needs to specify a
	// clientId as a unique identifier for the server to use until registration is done.
	private String clientUdpIp;
	private int clientUdpPort;
	private int clientId;
	
	public RegistrationMessage() {
		super(MsgType.REG_TYPE);
	}
	
	public String getClientUdpIp() {
		return clientUdpIp;
	}
	public void setClientUdpIp(String clientUdpIp) {
		this.clientUdpIp = clientUdpIp;
	}
	public int getClientUdpPort() {
		return clientUdpPort;
	}
	public void setClientUdpPort(int clientUdpPort) {
		this.clientUdpPort = clientUdpPort;
	}
	
	@Override
	public NetworkingStrategyEnum getStrategy() {
		return NetworkingStrategyEnum.TCP_S;
	}
	public int getClientId() {
		return clientId;
	}
	public void setClientId(int clientId) {
		this.clientId = clientId;
	}
	
}
