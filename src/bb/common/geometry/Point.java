package bb.common.geometry;

import java.io.Serializable;

public class Point implements Serializable {
	private static final long serialVersionUID = 8497030078164097738L;
	public float x;
	public float y;
	
	public Point(float x, float y) {
		this.x = x;
		this.y = y;
	}
}
