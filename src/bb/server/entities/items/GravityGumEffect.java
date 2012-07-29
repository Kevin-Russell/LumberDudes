package bb.server.entities.items;

import bb.server.entities.Player;

public class GravityGumEffect extends Effect {
	private float modifiedAmount;
	
	public void apply(Player target, float expireDuration) {
		super.apply(target, expireDuration);
		modifiedAmount = -1 * (target.getGravityModifier() / 2);
		target.modifyGravity(modifiedAmount);
	}
	
	public void expire() {
		expired = true;
		target.modifyGravity(-1 * modifiedAmount);
	}
}
