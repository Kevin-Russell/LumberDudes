package bb.common;

public class Random {
	private static Random instance;
	private java.util.Random r;
	
	private Random() {
		r = new java.util.Random();
		r.setSeed(System.currentTimeMillis());
	}
	
	public static Random getInstance() {
		if (instance == null) {
			instance = new Random();
		}
		return instance;
	}
	
	// Generate a float within a range
	public float floatRange(float min, float max) {
		return min + (r.nextFloat() * (max - min + 1));
	}
	
	// Generate an int within a range
	public int intRange(int min, int max) {
		return (r.nextInt(max + 1) + min);
	}
}
