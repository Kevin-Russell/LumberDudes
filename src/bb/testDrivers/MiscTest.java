package bb.testDrivers;

public class MiscTest {
	public class Runner implements Runnable {
		Thread t;
		
		public void run() {
			for (int i = 0; i < 20; i++) {
				System.out.println("MEOW");
			}
		}
		
		public Runner() {
			t = new Thread(this);
			t.start();
		}
		
		public void join() {
			try {
				t.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void run() {
		Runner r = new Runner();
//		r.join();
		for (int i = 0; i < 200; i++) {
			System.out.println("RAWR");
		}
	}
	
	public static void main(String[] argv) {
		MiscTest m = new MiscTest();
		m.run();
	}
}
