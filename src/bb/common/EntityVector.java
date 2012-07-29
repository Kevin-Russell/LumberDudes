package bb.common;

import java.util.Collection;
import java.util.Vector;

public class EntityVector<T extends BaseEntity> implements EntityCollection<T> {
	Vector<T> entityVector;
	
	public EntityVector() {
		entityVector = new Vector<T>();
	}
	
	public EntityVectorIterator<T> createIterator() {
		return new EntityVectorIterator<T>(entityVector);
	}
	
	public int size() {
		return entityVector.size();
	}
	
	public void add(T entity) {
		entityVector.add(entity);
	}
	
	public void remove(T entity) {
		remove(entity.getId());
	}
	
	public void remove(int id) {
		T b = null;
		
		for (int i = 0; i < entityVector.size(); i++) {
			b = entityVector.get(i);
			if (b.getId() == id) {
				entityVector.remove(i);
				break;
			}
		}
	}
	
	public T fetch(int id) {
		T b = null;
		
		for (int i = 0; i < entityVector.size(); i++) {
			b = entityVector.get(i);
			if (b.getId() == id) {
				return b;
			}
		}
		
		return null;
	}

	public Vector<T> fetch(Collection<Integer> ids) {
		Vector<T> results = new Vector<T>();
		T e;
		
		for (int i = 0; i < entityVector.size(); i++) {
			e = entityVector.get(i);
			if (ids.contains(e.getId())) { 
				results.add(e);
			}
		}
		
		return results;
	}
	
//	public static void test() {
//		EntityCollection e = new EntityVector();
//		PhysEntity p1 = new Platform(0, 0, 1, 0);
//		PhysEntity p2 = new Platform(0, 0, 2, 0);
//		PhysEntity p3 = new Platform(0, 0, 3, 0);
//		
//		e.add(p1);
//		e.add(p2);
//		e.add(p3);
//		
//		if (e.size() == 3) {
//			System.out.println("1. count test passed!");
//		}
//		
//		EntityIterator i = e.createIterator();
//		
//		int width = 1;
//		while (i.hasNext()) {
//			if (i.next().getWidth() == width) {
//				System.out.println((width + 1) + ". width test passed!");
//			}
//			width++;
//		}
//		
//		i.reset();
//		
//		if (i.next().getWidth() == 1) {
//			System.out.println("5. reset test passed!");
//		}
//		
//		e.remove(5);
//		
//		if (e.size() == 3) {
//			System.out.println("6. count test passed!");
//		}
//		
//		e.remove(1);
//		
//		if (e.size() == 2) {
//			System.out.println("7. count test passed!");
//		}
//		
//		BaseEntity b1 = e.fetch(1);
//		
//		if (b1 == null) {
//			System.out.println("8. fetch test passed!");
//		}
//		
//		b1 = e.fetch(2);
//		
//		if (b1 != null) {
//			System.out.println("9. fetch test passed!");
//		}
//	}
}
