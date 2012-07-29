package bb.server.entities;

import java.util.Vector;

import bb.server.entities.items.Effect;

public class Player extends PhysEntity {
	public Vector<Effect> effects;
	private float moveSpeed;
	
	public Player(float x, float y, float width, float height) {
		super(x, y, width, height);
		effects = new Vector<Effect>();
		moveSpeed = 200;
	}
	
	public void jump() {
		if (isOnGround) {
			ySpeed = -600;
		}
	}
	
	public void moveRight() {
		xSpeed = getMoveSpeed();
	}
	
	public void moveLeft() {
		xSpeed = -getMoveSpeed();
	}
	
	public void stop() {
		xSpeed = 0;
	}
	
	public void applyEffect(Effect e) {
		effects.add(e);
	}
	
	public void removeEffect(Effect e) {
		removeEffect(e.getId());
	}
	
	public void removeEffect(int id) {
		Effect effect;
		
		for (int i = 0; i < effects.size(); i++) {
			effect = effects.get(i);
			if (effect.getId() == id) {
				effects.remove(i);
				break;
			}
		}
	}
	
	public void performTimeStep(float elapsedTime) {
		super.performTimeStep(elapsedTime);
		
		// Remove any expired item effects
		Effect e;
		for (int i = 0; i < effects.size(); i++) {
			e = effects.get(i);
			e.performTimeStep(elapsedTime);
			if (e.isExpired()) {
				removeEffect(e);
			}
		}
	}
	
	public boolean isPlayer() {
		return true;
	}
	
	public String toString() {
		return "Player[id:" + id + "]";
	}

	public float getMoveSpeed() {
		return moveSpeed;
	}

	public void modifyMoveSpeed(float moveSpeed) {
		this.moveSpeed += moveSpeed;
	}
}
