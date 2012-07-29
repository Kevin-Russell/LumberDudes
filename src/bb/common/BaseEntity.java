package bb.common;

public abstract class BaseEntity {
	protected int id;
	protected float x;
	protected float y;
	protected float width;
	protected float height;

	public static int idGenerator = 0;
	
	public void setX(float x) {
		this.x = x;
	}
	
	public void setY(float y) {
		this.y = y;
	}
	
	public float getX() {
		return this.x;
	}
	
	public float getY() {
		return this.y;
	}
	
	public float getWidth() {
		return this.width;
	}
	
	public float getHeight() {
		return this.height;
	}
	
	public int getId() {
		return this.id;
	}
	
	public BaseEntity(float x, float y, float width, float height) {
		this.id = idGenerator++;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public String toString() {
		return "BaseEntity[id:" + id + "]";
	}
}