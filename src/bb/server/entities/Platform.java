package bb.server.entities;

public class Platform extends PhysEntity {
	public Platform(float x, float y, float width, float height) {
		super(x, y, width, height);
	}
	
	public boolean skipCollisionResolution() {
		return true;
	}
	
	public float getGravityModifier() {
		return 0f;
	}
}
