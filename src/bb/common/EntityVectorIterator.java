package bb.common;

import java.util.Vector;

public class EntityVectorIterator<T extends BaseEntity> implements EntityIterator<T> {
	private Vector<T> entityVector;
	private int index;
	
	public EntityVectorIterator (Vector<T> v) {
		index = 0;
		entityVector = v;
		
		if (entityVector == null) {
			entityVector = new Vector<T>();
		}
	}
	
	public T next() {
		if (hasNext()) {
			return entityVector.get(index++);
		} else {
			return null;
		}
	}
	
	public boolean hasNext() {
		if (index < entityVector.size()) {
			return true;
		} else {
			return false;
		}
	}
	
	public void reset() {
		index = 0;
	}
}
