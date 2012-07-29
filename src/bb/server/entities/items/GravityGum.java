package bb.server.entities.items;

import bb.server.entities.Player;

public class GravityGum extends Item {
	
	public GravityGum(float x, float y, float width, float height) {
		super(x, y, width, height);
	}
	
	public void applyItemEffect(Player target) {
		GravityGumEffect effect = new GravityGumEffect();
		effect.apply(target, 3f);
		scoreKeeper.modifyScore(target.getId(), 25);
		destroy();
	}
}
