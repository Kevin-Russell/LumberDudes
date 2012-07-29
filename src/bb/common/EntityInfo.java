package bb.common;

import java.io.Serializable;

/**
 * Class containing information about the Entity, to be sent over the network in
 * the entity broadcast message.
 */
public class EntityInfo implements Serializable {
	
	private static final long serialVersionUID = 3632635848518818852L;
	
	private int id;
	private float x;
	private float y;
	private float width;
	private float height;
	private int spriteId;
	private boolean isPlayer;
	private boolean isRemoved;
	private int score;
	
	public EntityInfo(int id, float x, float y, float width, float height, int spriteId, boolean isPlayer, int score) {
		super();
		this.id = id;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.spriteId = spriteId;
		this.isPlayer = isPlayer;
		this.score = score;
		this.isRemoved = false;
	}
	
	public int getId() {
		return id;
	}
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
	
	public float getWidth() {
		return width;
	}
	
	public float getHeight() {
		return height;
	}

	public int getSpriteId() {
		return spriteId;
	}

	public boolean isPlayer() {
		return isPlayer;
	}

	public int getScore() {
		return score;
	}
	
	public void setIsRemoved(boolean isRemoved) {
		this.isRemoved = isRemoved;
	}
	
	public boolean isRemoved() {
		return isRemoved;
	}
	
	public boolean equals(EntityInfo target) {
		return this.id == target.getId();
	}
	
	public EntityInfo makeCopy() {
		return new EntityInfo(id, x, y, width, height, spriteId, isPlayer, score);
	}
}
