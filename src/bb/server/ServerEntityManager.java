package bb.server;

import bb.common.EntityCollection;
import bb.common.EntityIterator;
import bb.common.EntityVector;
import bb.server.entities.*;

public class ServerEntityManager {
	private static ServerEntityManager instance;
	private EntityCollection<Player> players;
	private EntityCollection<PhysEntity> entities;
	
	private ServerEntityManager() {
		players = new EntityVector<Player>();
		entities = new EntityVector<PhysEntity>();
	}
	
	public static ServerEntityManager getInstance() {
		if (instance == null) {
			instance = new ServerEntityManager();
		}
		return instance;
	}
	
	public void add(PhysEntity e) {
		entities.add(e);
	}
	
	public void add(Player p) {
		entities.add(p);
		players.add(p);
	}
	
	public void remove(int id) {
		entities.remove(id);
	}
	
	public PhysEntity getEntityById(int id) {
		return entities.fetch(id);
	}
	
	public EntityIterator<PhysEntity> createEntityIterator() {
		return entities.createIterator();
	}
	
	public EntityIterator<Player> createPlayerIterator() {
		return players.createIterator();
	}
}
