package bb.server.entities.items;

import bb.server.entities.Player;

public class Bomb extends Item {
	public Bomb(float x, float y, float width, float height) {
		super(x, y, width, height);
	}
	
	public void applyItemEffect(Player target) {
		scoreKeeper.modifyScore(target.getId(), -100);
		destroy();
	}
}
