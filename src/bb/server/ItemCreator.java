package bb.server;

import bb.common.Random;
import bb.server.entities.items.*;

public class ItemCreator {
	private static ItemCreator instance;
	private ServerEntityManager entityManager;
	private float accumulatedTime;
	private Random random;
	
	private ItemCreator() {
		random = Random.getInstance();
		entityManager = ServerEntityManager.getInstance();
		accumulatedTime = 0;
	}
	
	public static ItemCreator getInstance() {
		if (instance == null) {
			instance = new ItemCreator();
		}
		return instance;
	}
	
	// Creates a random item every 1 second
	public void pollItem(float elapsedTime) {
		accumulatedTime += elapsedTime;
		
		if (accumulatedTime >= 1) {
			float xPosition = Game.camera.getX() + random.floatRange((float)(Game.camera.getWidth() / 4), (float)(Game.camera.getWidth() * 3 / 4));
			Item item = null;
			int itemType = random.intRange(0, 3);

			switch (itemType) {
			case 0:
				item = new Coin(xPosition, Game.camera.getY(), 16, 16);
				break;
			case 1:
				item = new Bomb(xPosition, Game.camera.getY(), 16, 16);
				break;
			case 2:
				item = new GravityGum(xPosition, Game.camera.getY(), 16, 16);
				break;
			case 3:
				item = new SpeedSoup(xPosition, Game.camera.getY(), 16, 16);
				break;
			default:
				break;
			}
			
			entityManager.add(item);
			accumulatedTime -= 1;
		}
	}
}
