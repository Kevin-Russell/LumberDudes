package bb.common;

// Interface of the iterator for the Iterator Design Pattern
public interface EntityIterator<T extends BaseEntity> {
	public T next();
	public boolean hasNext();
	public void reset();
}
