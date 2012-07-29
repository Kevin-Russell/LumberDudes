package bb.common.network.message;

import bb.common.GameProperties;
import bb.common.exception.PropertiesException;
import bb.common.network.ProtocolStrategy.NetworkingStrategyEnum;

public class TerminateMessage extends Message {

	private static final long serialVersionUID = 8864550668572961599L;
	
	private boolean isWinner;
	private int winnerEntityId;
	
	public TerminateMessage() {
		super(MsgType.TERMINATE_TYPE);
	}
	
	public TerminateMessage(boolean iw, int winner) {
		super(MsgType.TERMINATE_TYPE);
		isWinner = iw;
		winnerEntityId = winner;
	}
	
	public TerminateMessage(int winner) {
		super(MsgType.TERMINATE_TYPE);
		winnerEntityId = winner;
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

	public boolean isWinner() {
		return isWinner;
	}

	public void setWinner(boolean isWinner) {
		this.isWinner = isWinner;
	}

	public int getWinnerEntityId() {
		return winnerEntityId;
	}

	public void setWinnerEntityId(int winnerEntityId) {
		this.winnerEntityId = winnerEntityId;
	}

	
}
