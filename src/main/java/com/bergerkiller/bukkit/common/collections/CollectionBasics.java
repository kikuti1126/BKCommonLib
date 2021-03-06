package com.bergerkiller.bukkit.common.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.bergerkiller.bukkit.common.utils.LogicUtil;

/**
 * Provides basic implementations for various methods used in collections
 */
public class CollectionBasics {
	/**
	 * A basic retainAll implementation. (does not call collection.retainAll)
	 * After this call all elements not contained in elements are removed.
	 * Essentially all elements are removed except those contained in the elements Collection.
	 * 
	 * @param collection
	 * @param elements to retain
	 * @return True if the list changed, False if not
	 */
	public static boolean retainAll(Collection<?> collection, Collection<?> elements) {
		Iterator<?> iter = collection.iterator();
		boolean changed = false;
		while (iter.hasNext()) {
			if (!elements.contains(iter.next())) {
				iter.remove();
				changed = true;
			}
		}
		return changed;
	}

	/**
	 * A basic toArray implementation. (does not call collection.toArray)
	 * A new array of Objects is allocated and filled with the contents of the collection
	 * 
	 * @param collection to convert to an array
	 * @return a new Object[] array
	 */
	public static Object[] toArray(Collection<?> collection) {
		Object[] array = new Object[collection.size()];
		Iterator<?> iter = collection.iterator();
		for (int i = 0; i < array.length; i++) {
			array[i] = iter.next();
		}
		return array;
	}

	/**
	 * A basic toArray implementation. (does not call collection.toArray)
	 * If the array specified is not large enough, a new array with the right size is allocated.
	 * If the array specified is larger than the collection, the element right after the last
	 * collection element is set to null to indicate the end.
	 * 
	 * @param collection to convert to an array
	 * @param array to fill with the contents (can not be null)
	 * @return the array filled with the contents, or a new array of the same type
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] toArray(Collection<?> collection, T[] array) {
		final int size = collection.size();
		if (array.length < size) {
			array = (T[]) LogicUtil.createArray(array.getClass().getComponentType(), size);
		}
		Iterator<?> iter = collection.iterator();
		for (int i = 0; i < array.length; i++) {
			if (iter.hasNext()) {
				array[i] = (T) iter.next();
			} else {
				array[i] = null;
				break;
			}
		}
		return array;
	}

	/**
	 * Obtains a reference to an element in a collection of lists
	 * 
	 * @param lists to pick from
	 * @param index of the element
	 * @return list entry for the element at the index
	 */
	public static <T> ListEntry<T> getEntry(Collection<List<T>> lists, int index) {
		int size;
		for (List<T> list : lists) {
			size = list.size();
			if (size >= index) {
				index -= size;
			} else {
				return new ListEntry<T>(list, index);
			}
		}
		throw new IndexOutOfBoundsException();
	}

	public static class ListEntry<T> {
		public final List<T> list;
		public final int index;

		public ListEntry(List<T> list, int index) {
			this.list = list;
			this.index = index;
		}

		public void add(T element) {
			list.add(index, element);
		}

		public T set(T newElement) {
			return list.set(index, newElement);
		}

		public T get() {
			return list.get(index);
		}

		public T remove() {
			return list.remove(index);
		}

		public boolean addAll(Collection<? extends T> c) {
			return list.addAll(index, c);
		}
	}
}
