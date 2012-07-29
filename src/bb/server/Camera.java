package bb.server;

import java.util.ArrayList;
import java.util.List;

import bb.common.BaseEntity;
import bb.common.EntityIterator;
import bb.common.Random;
import bb.common.geometry.Rectangle;
import bb.server.entities.PhysEntity;
import bb.server.entities.Player;

public class Camera extends BaseEntity {
	private Rectangle frame;
	private int killPenalty = -50;
	private float cameraSpeed = 100;
	private ScoreKeeper scoreKeeper;
	private ServerEntityManager entityManager;
	private boolean startMovement;
	private static Camera instance;
	
	public static Camera getInstance() {
		if (instance == null) {
			instance = new Camera(0, 0, Game.WIDTH , Game.HEIGHT);
		}
		return instance;
	}
	
	private Camera(float x, float y, float width, float height) {
		super(x, y, width, height);
		frame = new Rectangle(x, y, width, height);
		
		entityManager = ServerEntityManager.getInstance();
		scoreKeeper = ScoreKeeper.getInstance();
		startMovement = false;
	}
	
	private void respawnPlayer(Player p) {
		float xPosition = x + Random.getInstance().floatRange((float)(width / 4), (float)(width * 3 / 4));
		p.setX(xPosition);
		p.setY(0);
		p.setYSpeed(0);
	}
	
	// Checks for intersections between a player and the bottom/left of the camera
	// If detected, players are re-spawned at the top of the screen with a penalty in points
	private void checkPlayerIntersect() {
		EntityIterator<Player> i = entityManager.createPlayerIterator();
		
		Player player;
		int id;
		boolean penalty = false;
		
		while (i.hasNext()) {
			player = i.next();
			id = player.getId();
			penalty = false;
			
			// Check if a player is intersecting with the left side of the camera
			if (player.getX() < x) {
				penalty = true;
			}
			
			// Check if a player is intersecting with the bottom of the camera
			if (player.getY() + player.getHeight() > y + height) {
				penalty = true;
			}
			
			// Check if a player is intersecting with the right side of the camera
			if (player.getX() + player.getWidth() >= x + width) {
				player.setX(x + width - player.getWidth());
			}
			
			// If a player is out of the camera's bounds, remove some of their points
			// and re-spawn them
			if (penalty) {
				scoreKeeper.modifyScore(id, killPenalty);
				respawnPlayer(player);
			}
		}
	}
	
	public List<PhysEntity> getEntitiesInFrame() {
		EntityIterator<PhysEntity> i = entityManager.createEntityIterator();
		
		PhysEntity entity = null;
		Rectangle cameraFrame = frame;
		Rectangle tHitBox = null;
		
		List<PhysEntity> results = new ArrayList<PhysEntity>();
		
		while (i.hasNext()) {
			entity = i.next();
			tHitBox = entity.getHitBox();
			
			if (cameraFrame.intersects(tHitBox) || entity.isPlayer()) {
				results.add(entity);
			}
		}
		
		return results;
	}
	
	public void startMovement() {
		startMovement = true;
	}
	
	public void performTimeStep(float elapsedTime) {
		// Camera only starts moving when the game says it's okay to go
		if (startMovement) {
			float dx = elapsedTime * cameraSpeed;
			float newX = getX() + dx;

			this.x = newX;
			frame.x = newX;
		}
		checkPlayerIntersect();
	}
}
