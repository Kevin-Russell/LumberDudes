package bb.common.network.message;

import bb.common.network.ProtocolStrategy.NetworkingStrategyEnum;

public class StartGameMessage extends Message {

	private static final long serialVersionUID = 3390123430852325823L;
	
	public StartGameMessage() {
		super(MsgType.START_TYPE);
	}
	
	@Override
	public NetworkingStrategyEnum getStrategy() {
		return NetworkingStrategyEnum.TCP_S;
	}

}
