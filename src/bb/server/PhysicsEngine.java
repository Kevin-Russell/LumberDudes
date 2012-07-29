package bb.server;

import java.util.HashSet;

import bb.common.*;
import bb.common.geometry.Point;
import bb.server.entities.*;

public class PhysicsEngine {
	private static PhysicsEngine instance;
	private ServerEntityManager entityManager;
	private float gravityAcceleration; // pixels per second^2
	HashSet<String> handledCollisionEffects;

	private PhysicsEngine() {
		gravityAcceleration = 1600;
		entityManager = ServerEntityManager.getInstance();
		handledCollisionEffects = new HashSet<String>();
	}

	public static PhysicsEngine getInstance() {
		if (instance == null) {
			instance = new PhysicsEngine();
		}
		return instance;
	}

	public float getGravityAcceleration() {
		return gravityAcceleration;
	}
	
	// Perform a physics time step based on the elapsedTime
	// Handles collision detection and resolution
	public void performTimeStep(float elapsedTime) {
		EntityIterator<PhysEntity> i = entityManager.createEntityIterator();
		EntityIterator<PhysEntity> j = entityManager.createEntityIterator();
		
		PhysEntity current = null;
		PhysEntity target = null;
		
		float oldX = 0;
		float oldY = 0;
		handledCollisionEffects.clear();
		
		while (i.hasNext()) {
			current = i.next();
			oldX = current.getX();
			oldY = current.getY();
			current.performTimeStep(elapsedTime);
			current.setIsOnGround(false);
			
			while (j.hasNext()) {
				target = j.next();
				
				// Don't check collisions with itself
				if (current.getId() == target.getId()) {
					continue;
				}
				
				if (current.skipCollisionResolution()) {
					continue;
				}
				
				if (current.collidiesWith(target)) {
					if (!handledCollisionEffects.contains(current.getId() + " " + target.getId())) {
						// Indicate that this collision effect has been handled
						// so that we don't handle it again during the same timestep
						handledCollisionEffects.add(current.getId() + " " + target.getId());
						handledCollisionEffects.add(target.getId() + " " + current.getId());
						
						current.mutuallyApplyCollisionEffects(target);
						
						// Collisions between a player and an item does not require
						// collision resolution
						if ((current.isPlayer() && target.isItem()) || (current.isItem() && target.isPlayer())) {
							continue;
						}
					}
					resolveCollision(oldX, oldY, current, target);
				}
			}
			j.reset();
		}
	}

	// Resolve collisions between two entities
	private void resolveCollision(float oldX, float oldY, PhysEntity current, PhysEntity target) {
		float newX = current.getX();
		float newY = current.getY();
		
		// Rewind the position of the entity to before the collision occured
		current.setX(oldX);
		current.setY(oldY);
		
		Point cTopLeft = current.getTopLeft();
		Point cBottomRight = current.getBottomRight();
		Point tTopLeft = target.getTopLeft();
		Point tBottomRight = target.getBottomRight();

		if (cTopLeft.x >= tBottomRight.x) {
			// Collision from the right
			// T <- C
			current.setX(tBottomRight.x + 1);
			current.setY(newY);
		} else if (cBottomRight.y <= tTopLeft.y) {
			// Collision from above
			// C
			// v
			// T
			current.setY(tTopLeft.y - current.getHeight());
			current.setYSpeed(0);
			current.setX(newX);
			if (current instanceof Player) {
				Player p = (Player)current;
				p.setIsOnGround(true);
			}
		}  else if (cTopLeft.y >= tBottomRight.y) {
			// Collision from below
			// T
			// ^
			// C
			current.setY(tBottomRight.y + 1);
			current.setYSpeed(0);
			current.setX(newX);
		} else if (cBottomRight.x <= tTopLeft.x) {
			// Collision from the left
			// C -> T
			current.setX(tTopLeft.x - current.getWidth() - 1);
			current.setY(newY);
		}
	}
}
