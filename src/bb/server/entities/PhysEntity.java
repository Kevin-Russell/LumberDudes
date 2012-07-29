package bb.server.entities;

import bb.common.BaseEntity;
import bb.common.geometry.Point;
import bb.common.geometry.Rectangle;
import bb.server.PhysicsEngine;
import bb.server.ServerEntityManager;

// The basic object to be manipulated by the physics engine
public abstract class PhysEntity extends BaseEntity {
	protected Rectangle hitBox;
	protected float xSpeed;
	protected float ySpeed;
	protected float gravityModifier;
	protected boolean isOnGround;
	
	private PhysicsEngine physicsEngine;
	private ServerEntityManager entityManager;

	public PhysEntity(float x, float y, float width, float height) {
		super(x, y, width, height);
		
		gravityModifier = 1;
		xSpeed = 0; // pixels per second
		ySpeed = 0; // pixels per second
		isOnGround = false;
		hitBox = new Rectangle(x, y, width, height);
		
		physicsEngine = PhysicsEngine.getInstance();
		entityManager = ServerEntityManager.getInstance();
	}

	public void setX(float x) {
		super.setX(x);
		this.hitBox.x = x;
	}

	public void setY(float y) {
		super.setY(y);
		this.hitBox.y = y;
	}

	public void setXSpeed(float dx) {
		this.xSpeed = dx;
	}

	public void setYSpeed(float dy) {
		this.ySpeed = dy;
	}

	public Point getTopLeft() {
		return new Point(hitBox.x, hitBox.y);
	}

	public Point getBottomRight() {
		return new Point(hitBox.x + hitBox.width - 1, hitBox.y + hitBox.height - 1);
	}

	public boolean collidiesWith(PhysEntity target) {
		return (hitBox.intersects(target.hitBox));
	}

	public Rectangle getHitBox() {
		return hitBox;
	}

	public float getGravityModifier() {
		return gravityModifier;
	}
	
	public void modifyGravity(float modifier) {
		gravityModifier += modifier;
	}
	
	public boolean getIsOnGround() {
		return isOnGround;
	}
	
	public void setIsOnGround(boolean isOnGround) {
		this.isOnGround = isOnGround;
	}

	public void performTimeStep(float elapsedTime) {
		ySpeed += physicsEngine.getGravityAcceleration() * getGravityModifier() * elapsedTime;

		float dx = xSpeed * elapsedTime;
		float dy = ySpeed * elapsedTime;
		float oldX = x;
		float oldY = y;
		float newX = oldX + dx;
		float newY = oldY + dy;

		setX(newX);
		setY(newY);
	}

	public void destroy() {
		entityManager.remove(id);
	}
	
	public boolean isPlayer() {
		return false;
	}
	
	public boolean isItem() {
		return false;
	}
	
	public boolean skipCollisionResolution() {
		return false;
	}
	
	public void applyCollisionEffect(PhysEntity target) {
		return;
	}
	
	public void mutuallyApplyCollisionEffects(PhysEntity target) {
		applyCollisionEffect(target);
		target.applyCollisionEffect(this);
	}
}
