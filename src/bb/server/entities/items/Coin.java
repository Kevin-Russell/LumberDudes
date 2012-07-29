package bb.server.entities.items;

import bb.server.entities.Player;

public class Coin extends Item {
	public Coin(float x, float y, float width, float height) {
		super(x, y, width, height);
	}
	
	public void applyItemEffect(Player target) {
		scoreKeeper.modifyScore(target.getId(), 100);
		destroy();
	}
}
