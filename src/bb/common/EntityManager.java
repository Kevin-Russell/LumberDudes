package bb.common;

import bb.client.painter.PaintEntity;

public class EntityManager {
	private static EntityManager instance;
	private EntityCollection<PaintEntity> entities;
	
	private EntityManager() {
		entities = new EntityVector<PaintEntity>();
	}
	
	public static EntityManager getInstance() {
		if (instance == null) {
			instance = new EntityManager();
		}
		return instance;
	}
	
	public void add(PaintEntity e) {
		entities.add(e);
	}
	
	public void remove(int id) {
		entities.remove(id);
	}
	
	public PaintEntity getEntityById(int id) {
		return entities.fetch(id);
	}
	
	public EntityIterator<PaintEntity> createEntityIterator() {
		return entities.createIterator();
	}
}
