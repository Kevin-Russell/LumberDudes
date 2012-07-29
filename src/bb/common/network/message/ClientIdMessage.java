package bb.common.network.message;

import bb.common.network.ProtocolStrategy.NetworkingStrategyEnum;

public class ClientIdMessage extends Message {

	private static final long serialVersionUID = -230002776843702671L;
	int clientId;
	
	public ClientIdMessage(int clientId) {
		super(MsgType.CLIENTID_TYPE);
		this.clientId = clientId;
	}
	
	public ClientIdMessage() {
		super(MsgType.CLIENTID_TYPE);
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
