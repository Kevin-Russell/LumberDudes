package bb.server.entities;

public class MovingPlatform extends Platform {
	private float xRange;
	private float xAnchor;
	
	public MovingPlatform(float x, float y, float width, float height, float xRange, float xSpeed) {
		super(x, y, width, height);
		xAnchor = x;
		this.xSpeed = Math.abs(xSpeed) * -1;
		this.xRange = xRange;
	}
	
	public boolean skipCollisionResolution() {
		return true;
	}
	
	public void performTimeStep(float elapsedTime) {
		super.performTimeStep(elapsedTime);
		if (x < (xAnchor - xRange)) {
			xSpeed = Math.abs(xSpeed);
		}
		
		if (x > (xAnchor + xRange)) {
			xSpeed = Math.abs(xSpeed) * -1;
		}
	}
	
	public float getGravityModifier() {
		return 0f;
	}
}
