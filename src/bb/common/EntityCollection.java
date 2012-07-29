package bb.common;

import java.util.Collection;
import java.util.Vector;

//Interface of the collection for the Iterator Design Pattern
public interface EntityCollection<T extends BaseEntity> {
	public EntityIterator<T> createIterator();
	public int size();
	public void add(T entity);
	public void remove(T entity);
	public void remove(int id);
	public T fetch(int id);
	public Vector<T> fetch(Collection<Integer> ids);
}
