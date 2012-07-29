package bb.server.entities.items;

import bb.server.ScoreKeeper;
import bb.server.entities.PhysEntity;
import bb.server.entities.Player;

public abstract class Item extends PhysEntity {
	ScoreKeeper scoreKeeper;
	
	public Item(float x, float y, float width, float height) {
		super(x, y, width, height);
		gravityModifier = 0.2f;
		scoreKeeper = ScoreKeeper.getInstance();
	}
	
	public boolean isItem() {
		return true;
	}
	
	public void applyCollisionEffect(PhysEntity target) {
		if (target.isPlayer()) {
			applyItemEffect((Player)target);
		}
	}
	
	public abstract void applyItemEffect(Player target);
}
