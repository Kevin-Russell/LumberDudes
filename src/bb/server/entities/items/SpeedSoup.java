package bb.server.entities.items;

import bb.server.entities.Player;

public class SpeedSoup extends Item {
	public SpeedSoup(float x, float y, float width, float height) {
		super(x, y, width, height);
	}
	
	public void applyItemEffect(Player target) {
		SpeedSoupEffect effect = new SpeedSoupEffect();
		effect.apply(target, 3f);
		scoreKeeper.modifyScore(target.getId(), 25);
		destroy();
	}
}