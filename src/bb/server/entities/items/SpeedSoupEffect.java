package bb.server.entities.items;

import bb.server.entities.Player;

public class SpeedSoupEffect extends Effect{
	private float modifiedAmount;
	
	public void apply(Player target, float expireDuration) {
		super.apply(target, expireDuration);
		modifiedAmount = target.getMoveSpeed();
		target.modifyMoveSpeed(modifiedAmount);
	}
	
	public void expire() {
		expired = true;
		target.modifyMoveSpeed(-1 * modifiedAmount);
	}
}
