package bb.common.geometry;

public class Rectangle {	
	public float x;
	public float y;
	public float width;
	public float height;
	
	public Rectangle(float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public boolean pointIntersects(Point p, Rectangle target) {
		if ((p.x >= target.x) && (p.x <= target.x + target.width)) {
			if ((p.y >= target.y) && (p.y <= target.y + target.height)) {
				return true;
			}
		}
		return false;
		
	}
	
	public boolean intersects(Rectangle target) {
		Point topLeft = new Point(x, y);
		Point bottomRight = new Point(x + width, y + height);
		
		Point tTopLeft = new Point(target.x, target.y);
		Point tBottomRight = new Point(target.x + target.width, target.y + target.height);
		
		if ((bottomRight.x < tTopLeft.x) || (bottomRight.y < tTopLeft.y) || 
				(topLeft.x > tBottomRight.x) || (topLeft.y > tBottomRight.y)) {
			return false;
		}
		
		return true;
	}
	
	public static void test() {
		Rectangle r1 = new Rectangle(0, 0, 10, 10);
		Rectangle r2 = new Rectangle(11, 0, 10, 10);
		Rectangle r3 = new Rectangle(10, 10, 1, 1);
		
		// TEST: Basic
		if (r1.intersects(r3)) {
			System.out.println("Test1 passed!");
		}
		
		if (!r1.intersects(r2)) {
			System.out.println("Test2 passed!");
		}
		
		Rectangle r6 = new Rectangle(460, 300, 32, 32);
		Rectangle r7 = new Rectangle(1, 300, 500, 100);
		if (r6.intersects(r7)) {
			System.out.println("Test3 passed!");
		}
		
		// TEST: All of r5's points are in r4
		Rectangle r4 = new Rectangle(10, 300, 500, 100);
		Rectangle r5 = new Rectangle(80, 304, 32, 32);
		if (r4.intersects(r5)) {
			System.out.println("Test4 passed!");
		}

//		   +-+
//		   | |
//		+--| |--+
//		|  | |  |
//		|  | |  |
//		+--| |--+
//		   +-+
	}
}
