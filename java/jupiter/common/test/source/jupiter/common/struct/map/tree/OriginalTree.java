/*
 * Copyright (c) 1997, 2014, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
package jupiter.common.struct.map.tree;

import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * A Red-Black tree based {@link NavigableMap} implementation. The map is sorted according to the {@linkplain Comparable natural
 * ordering} of its keys, or by a {@link Comparator} provided at map creation time, depending on
 * which constructor is used.
 * <p>
 * <p>
 * This implementation provides guaranteed log(n) time cost for the {@code containsKey},
 * {@code get}, {@code put} and {@code remove} operations. Algorithms are adaptations of those in
 * Cormen, Leiserson and Rivest's <em>Introduction to Algorithms</em>.
 * <p>
 * <p>
 * Note that the ordering maintained by a tree map, like any sorted map and whether or not an
 * explicit comparator is provided, must be <em>consistent with {@code equals}</em> if this sorted
 * map is to correctly implement the {@code Map} interface. (See {@code Comparable} or
 * {@code Comparator} for a precise definition of <em>consistent with equals</em>.) This is so
 * because the {@code Map} interface is defined in terms of the {@code equals} operation, but a
 * sorted map performs all key comparisons using its {@code
 * compareTo} (or {@code compare}) method, so two keys that are deemed equal by this method are,
 * from the standpoint of the sorted map, equal. The behavior of a sorted map <em>is</em>
 * well-defined even if its ordering is inconsistent with {@code equals}; it just fails to obey the
 * general contract of the {@code Map} interface.
 * <p>
 * <p>
 * <strong>Note that this implementation is not synchronized.</strong>
 * If multiple threads access a map concurrently, and at least one of the threads modifies the map
 * structurally, it <em>must</em> be synchronized externally. (A structural modification is any
 * operation that adds or deletes one or more mappings; merely changing the value associated with an
 * existing key is not a structural modification.) This is typically accomplished by synchronizing
 * on some object that naturally encapsulates the map. If no such object exists, the map should be
 * "wrapped" using the {@link Collections#synchronizedSortedMap Collections.synchronizedSortedMap}
 * method. This is best done at creation time, to prevent accidental unsynchronized access to the
 * map:
 * <pre>
 *   SortedMap m = Collections.synchronizedSortedMap(new OriginalTree(...));</pre>
 * <p>
 * <p>
 * The iterators returned by the {@code iterator} method of the collections returned by all of this
 * class's "collection view methods" are
 * <em>fail-fast</em>: if the map is structurally modified at any time after the iterator is
 * created, in any way except through the iterator's own {@code remove} method, the iterator will
 * throw a {@link
 * ConcurrentModificationException}. Thus, in the face of concurrent modification, the iterator
 * fails quickly and cleanly, rather than risking arbitrary, non-deterministic behavior at an
 * undetermined time in the future.
 * <p>
 * <p>
 * Note that the fail-fast behavior of an iterator cannot be guaranteed as it is, generally
 * speaking, impossible to make any hard guarantees in the presence of unsynchronized concurrent
 * modification. Fail-fast iterators throw {@code ConcurrentModificationException} on a best-effort
 * basis. Therefore, it would be wrong to write a program that depended on this exception for its
 * correctness:   <em>the fail-fast behavior of iterators should be used only to detect bugs.</em>
 * <p>
 * <p>
 * All {@code Map.Entry} pairs returned by methods in this class and its views represent snapshots
 * of mappings at the time they were produced. They do <strong>not</strong> support the
 * {@code Entry.setValue} method. (Note however that it is possible to change mappings in the
 * associated map using {@code put}.)
 * <p>
 * <p>
 * This class is a member of the
 * <a href="{@docRoot}/../technotes/guides/collections/index.html">
 * Java Collections Framework</a>.
 *
 * @param <K> the type of the keys maintained by this map
 * @param <V> the type of the mapped values
 *
 * @author Josh Bloch and Doug Lea
 * @see Map
 * @see HashMap
 * @see Hashtable
 * @see Comparable
 * @see Comparator
 * @see Collection
 * @since 1.2
 */

public class OriginalTree<K, V>
		extends AbstractMap<K, V>
		implements NavigableMap<K, V>, Cloneable, java.io.Serializable {

	/**
	 * The comparator used to maintain order in this tree map, or null if it uses the natural
	 * ordering of its keys.
	 *
	 * @serial
	 */
	protected final Comparator<? super K> comparator;

	protected transient Entry<K, V> root;

	/**
	 * The number of entries in the tree
	 */
	protected transient int size = 0;

	/**
	 * The number of structural modifications to the tree.
	 */
	protected transient int modCount = 0;

	/**
	 * Constructs a new, empty tree map, using the natural ordering of its keys. All keys inserted
	 * into the map must implement the {@link
	 * Comparable} interface. Furthermore, all such keys must be
	 * <em>mutually comparable</em>: {@code k1.compareTo(k2)} must not throw a
	 * {@code ClassCastException} for any keys {@code k1} and {@code k2} in the map. If the user
	 * attempts to put a key into the map that violates this constraint (for example, the user
	 * attempts to put a string key into a map whose keys are integers), the
	 * {@code put(Object key, Object value)} call will throw a {@code ClassCastException}.
	 */
	public OriginalTree() {
		comparator = null;
	}

	/**
	 * Constructs a new, empty tree map, ordered according to the given comparator. All keys
	 * inserted into the map must be <em>mutually comparable</em> by the given comparator: {@code comparator.compare(k1,
	 * k2)} must not throw a {@code ClassCastException} for any keys {@code k1} and {@code k2} in
	 * the map. If the user attempts to put a key into the map that violates this constraint, the {@code put(Object
	 * key, Object value)} call will throw a {@code ClassCastException}.
	 *
	 * @param comparator the comparator that will be used to order this map. If {@code null}, the {@linkplain Comparable natural
	 *        ordering} of the keys will be used.
	 */
	public OriginalTree(final Comparator<? super K> comparator) {
		this.comparator = comparator;
	}

	/**
	 * Constructs a new tree map containing the same mappings as the given map, ordered according to
	 * the <em>natural ordering</em> of its keys. All keys inserted into the new map must implement
	 * the {@link
	 * Comparable} interface. Furthermore, all such keys must be
	 * <em>mutually comparable</em>: {@code k1.compareTo(k2)} must not throw a
	 * {@code ClassCastException} for any keys {@code k1} and {@code k2} in the map. This method
	 * runs in n * log(n) time.
	 *
	 * @param m the map whose mappings are to be placed in this map
	 *
	 * @throws ClassCastException   if the keys in m are not {@link Comparable}, or are not mutually
	 *                              comparable
	 * @throws NullPointerException if the specified map is null
	 */
	public OriginalTree(final Map<? extends K, ? extends V> m) {
		comparator = null;
		putAll(m);
	}

	/**
	 * Constructs a new tree map containing the same mappings and using the same ordering as the
	 * specified sorted map. This method runs in linear time.
	 *
	 * @param m the sorted map whose mappings are to be placed in this map, and whose comparator is
	 *          to be used to sort this map
	 *
	 * @throws NullPointerException if the specified map is null
	 */
	public OriginalTree(final SortedMap<K, ? extends V> m) {
		comparator = m.comparator();
		try {
			buildFromSorted(m.size(), m.entrySet().iterator(), null, null);
		} catch (final java.io.IOException cannotHappen) {
		} catch (final ClassNotFoundException cannotHappen) {
		}
	}


	// Query Operations

	/**
	 * Returns the number of key-value mappings in this map.
	 *
	 * @return the number of key-value mappings in this map
	 */
	@Override
	public int size() {
		return size;
	}

	/**
	 * Returns {@code true} if this map contains a mapping for the specified key.
	 *
	 * @param key key whose presence in this map is to be tested
	 *
	 * @return {@code true} if this map contains a mapping for the specified key
	 *
	 * @throws ClassCastException   if the specified key cannot be compared with the keys currently
	 *                              in the map
	 * @throws NullPointerException if the specified key is null and this map uses natural ordering,
	 *                              or its comparator does not permit null keys
	 */
	@Override
	public boolean containsKey(final Object key) {
		return getEntry(key) != null;
	}

	/**
	 * Returns {@code true} if this map maps one or more keys to the specified value. More formally,
	 * returns {@code true} if and only if this map contains at least one mapping to a value
	 * {@code v} such that {@code (value==null ? v==null : value.equals(v))}. This operation will
	 * probably require time linear in the map size for most implementations.
	 *
	 * @param value value whose presence in this map is to be tested
	 *
	 * @return {@code true} if a mapping to {@code value} exists; {@code false} otherwise
	 *
	 * @since 1.2
	 */
	@Override
	public boolean containsValue(final Object value) {
		for (Entry<K, V> e = getFirstEntry(); e != null; e = successor(e)) {
			if (valEquals(value, e.value)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the value to which the specified key is mapped, or {@code null} if this map contains
	 * no mapping for the key.
	 * <p>
	 * <p>
	 * More formally, if this map contains a mapping from a key {@code k} to a value {@code v} such
	 * that {@code key} compares equal to {@code k} according to the map's ordering, then this
	 * method returns {@code v}; otherwise it returns {@code null}. (There can be at most one such
	 * mapping.)
	 * <p>
	 * <p>
	 * A return value of {@code null} does not <em>necessarily</em>
	 * indicate that the map contains no mapping for the key; it's also possible that the map
	 * explicitly maps the key to {@code null}. The {@link #containsKey containsKey} operation may
	 * be used to distinguish these two cases.
	 *
	 * @throws ClassCastException   if the specified key cannot be compared with the keys currently
	 *                              in the map
	 * @throws NullPointerException if the specified key is null and this map uses natural ordering,
	 *                              or its comparator does not permit null keys
	 */
	@Override
	public V get(final Object key) {
		final Entry<K, V> p = getEntry(key);
		return p == null ? null : p.value;
	}

	public Comparator<? super K> comparator() {
		return comparator;
	}

	/**
	 * @throws NoSuchElementException {@inheritDoc}
	 */
	public K firstKey() {
		return key(getFirstEntry());
	}

	/**
	 * @throws NoSuchElementException {@inheritDoc}
	 */
	public K lastKey() {
		return key(getLastEntry());
	}

	/**
	 * Copies all of the mappings from the specified map to this map. These mappings replace any
	 * mappings that this map had for any of the keys currently in the specified map.
	 *
	 * @param map mappings to be stored in this map
	 *
	 * @throws ClassCastException   if the class of a key or value in the specified map prevents it
	 *                              from being stored in this map
	 * @throws NullPointerException if the specified map is null or the specified map contains a
	 *                              null key and this map does not permit null keys
	 */
	@Override
	public void putAll(final Map<? extends K, ? extends V> map) {
		final int mapSize = map.size();
		if (size == 0 && mapSize != 0 && map instanceof SortedMap) {
			final Comparator<?> c = ((SortedMap<?, ?>) map).comparator();
			if (c == comparator || c != null && c.equals(comparator)) {
				++modCount;
				try {
					buildFromSorted(mapSize, map.entrySet().iterator(), null, null);
				} catch (final java.io.IOException cannotHappen) {
				} catch (final ClassNotFoundException cannotHappen) {
				}
				return;
			}
		}
		super.putAll(map);
	}

	/**
	 * Returns this map's entry for the given key, or {@code null} if the map does not contain an
	 * entry for the key.
	 *
	 * @return this map's entry for the given key, or {@code null} if the map does not contain an
	 *         entry for the key
	 *
	 * @throws ClassCastException   if the specified key cannot be compared with the keys currently
	 *                              in the map
	 * @throws NullPointerException if the specified key is null and this map uses natural ordering,
	 *                              or its comparator does not permit null keys
	 */
	final Entry<K, V> getEntry(final Object key) {
		// Offload comparator-based version for sake of performance
		if (comparator != null) {
			return getEntryUsingComparator(key);
		}
		if (key == null) {
			throw new NullPointerException();
		}
		@SuppressWarnings("unchecked")
		final Comparable<? super K> k = (Comparable<? super K>) key;
		Entry<K, V> p = root;
		while (p != null) {
			final int cmp = k.compareTo(p.key);
			if (cmp < 0) {
				p = p.left;
			} else if (cmp > 0) {
				p = p.right;
			} else {
				return p;
			}
		}
		return null;
	}

	/**
	 * Version of getEntry using comparator. Split off from getEntry for performance. (This is not
	 * worth doing for most methods, that are less dependent on comparator performance, but is
	 * worthwhile here.)
	 */
	final Entry<K, V> getEntryUsingComparator(final Object key) {
		@SuppressWarnings("unchecked")
		final K k = (K) key;
		final Comparator<? super K> cpr = comparator;
		if (cpr != null) {
			Entry<K, V> p = root;
			while (p != null) {
				final int cmp = cpr.compare(k, p.key);
				if (cmp < 0) {
					p = p.left;
				} else if (cmp > 0) {
					p = p.right;
				} else {
					return p;
				}
			}
		}
		return null;
	}

	/**
	 * Gets the entry corresponding to the specified key; if no such entry exists, returns the entry
	 * for the least key greater than the specified key; if no such entry exists (i.e., the greatest
	 * key in the Tree is less than the specified key), returns {@code null}.
	 */
	final Entry<K, V> getCeilingEntry(final K key) {
		Entry<K, V> p = root;
		while (p != null) {
			final int cmp = compare(key, p.key);
			if (cmp < 0) {
				if (p.left != null) {
					p = p.left;
				} else {
					return p;
				}
			} else if (cmp > 0) {
				if (p.right != null) {
					p = p.right;
				} else {
					Entry<K, V> parent = p.parent;
					Entry<K, V> ch = p;
					while (parent != null && ch == parent.right) {
						ch = parent;
						parent = parent.parent;
					}
					return parent;
				}
			} else {
				return p;
			}
		}
		return null;
	}

	/**
	 * Gets the entry corresponding to the specified key; if no such entry exists, returns the entry
	 * for the greatest key less than the specified key; if no such entry exists, returns
	 * {@code null}.
	 */
	final Entry<K, V> getFloorEntry(final K key) {
		Entry<K, V> p = root;
		while (p != null) {
			final int cmp = compare(key, p.key);
			if (cmp > 0) {
				if (p.right != null) {
					p = p.right;
				} else {
					return p;
				}
			} else if (cmp < 0) {
				if (p.left != null) {
					p = p.left;
				} else {
					Entry<K, V> parent = p.parent;
					Entry<K, V> ch = p;
					while (parent != null && ch == parent.left) {
						ch = parent;
						parent = parent.parent;
					}
					return parent;
				}
			} else {
				return p;
			}

		}
		return null;
	}

	/**
	 * Gets the entry for the least key greater than the specified key; if no such entry exists,
	 * returns the entry for the least key greater than the specified key; if no such entry exists
	 * returns {@code null}.
	 */
	final Entry<K, V> getHigherEntry(final K key) {
		Entry<K, V> p = root;
		while (p != null) {
			final int cmp = compare(key, p.key);
			if (cmp < 0) {
				if (p.left != null) {
					p = p.left;
				} else {
					return p;
				}
			} else {
				if (p.right != null) {
					p = p.right;
				} else {
					Entry<K, V> parent = p.parent;
					Entry<K, V> ch = p;
					while (parent != null && ch == parent.right) {
						ch = parent;
						parent = parent.parent;
					}
					return parent;
				}
			}
		}
		return null;
	}

	/**
	 * Returns the entry for the greatest key less than the specified key; if no such entry exists
	 * (i.e., the least key in the Tree is greater than the specified key), returns {@code null}.
	 */
	final Entry<K, V> getLowerEntry(final K key) {
		Entry<K, V> p = root;
		while (p != null) {
			final int cmp = compare(key, p.key);
			if (cmp > 0) {
				if (p.right != null) {
					p = p.right;
				} else {
					return p;
				}
			} else {
				if (p.left != null) {
					p = p.left;
				} else {
					Entry<K, V> parent = p.parent;
					Entry<K, V> ch = p;
					while (parent != null && ch == parent.left) {
						ch = parent;
						parent = parent.parent;
					}
					return parent;
				}
			}
		}
		return null;
	}

	/**
	 * Associates the specified value with the specified key in this map. If the map previously
	 * contained a mapping for the key, the old value is replaced.
	 *
	 * @param key   key with which the specified value is to be associated
	 * @param value value to be associated with the specified key
	 *
	 * @return the previous value associated with {@code key}, or {@code null} if there was no
	 *         mapping for {@code key}. (A {@code null} return can also indicate that the map
	 *         previously associated {@code null} with {@code key}.)
	 *
	 * @throws ClassCastException   if the specified key cannot be compared with the keys currently
	 *                              in the map
	 * @throws NullPointerException if the specified key is null and this map uses natural ordering,
	 *                              or its comparator does not permit null keys
	 */
	@Override
	public V put(final K key, final V value) {
		Entry<K, V> t = root;
		if (t == null) {
			compare(key, key); // type (and possibly null) check

			root = new Entry<K, V>(key, value, null);
			size = 1;
			modCount++;
			return null;
		}
		int cmp;
		Entry<K, V> parent;
		// split comparator and comparable paths
		final Comparator<? super K> cpr = comparator;
		if (cpr != null) {
			do {
				parent = t;
				cmp = cpr.compare(key, t.key);
				if (cmp < 0) {
					t = t.left;
				} else if (cmp > 0) {
					t = t.right;
				} else {
					return t.setValue(value);
				}
			} while (t != null);
		} else {
			if (key == null) {
				throw new NullPointerException();
			}
			@SuppressWarnings("unchecked")
			final Comparable<? super K> k = (Comparable<? super K>) key;
			do {
				parent = t;
				cmp = k.compareTo(t.key);
				if (cmp < 0) {
					t = t.left;
				} else if (cmp > 0) {
					t = t.right;
				} else {
					return t.setValue(value);
				}
			} while (t != null);
		}
		final Entry<K, V> e = new Entry<K, V>(key, value, parent);
		if (cmp < 0) {
			parent.left = e;
		} else {
			parent.right = e;
		}
		fixAfterInsertion(e);
		size++;
		modCount++;
		return null;
	}

	/**
	 * Removes the mapping for this key from this OriginalTree if present.
	 *
	 * @param key key for which mapping should be removed
	 *
	 * @return the previous value associated with {@code key}, or {@code null} if there was no
	 *         mapping for {@code key}. (A {@code null} return can also indicate that the map
	 *         previously associated {@code null} with {@code key}.)
	 *
	 * @throws ClassCastException   if the specified key cannot be compared with the keys currently
	 *                              in the map
	 * @throws NullPointerException if the specified key is null and this map uses natural ordering,
	 *                              or its comparator does not permit null keys
	 */
	@Override
	public V remove(final Object key) {
		final Entry<K, V> p = getEntry(key);
		if (p == null) {
			return null;
		}

		final V oldValue = p.value;
		deleteEntry(p);
		return oldValue;
	}

	/**
	 * Removes all of the mappings from this map. The map will be empty after this call returns.
	 */
	@Override
	public void clear() {
		modCount++;
		size = 0;
		root = null;
	}

	/**
	 * Returns a shallow copy of this {@code OriginalTree} instance. (The keys and values themselves
	 * are not cloned.)
	 *
	 * @return a shallow copy of this map
	 */
	@Override
	public Object clone() {
		OriginalTree<?, ?> clone;
		try {
			clone = (OriginalTree<?, ?>) super.clone();
		} catch (final CloneNotSupportedException e) {
			throw new InternalError(e);
		}

		// Put clone into "virgin" state (except for comparator)
		clone.root = null;
		clone.size = 0;
		clone.modCount = 0;
		clone.entrySet = null;
		clone.navigableKeySet = null;
		clone.descendingMap = null;

		// Initialize clone with our mappings
		try {
			clone.buildFromSorted(size, entrySet().iterator(), null, null);
		} catch (final java.io.IOException cannotHappen) {
		} catch (final ClassNotFoundException cannotHappen) {
		}

		return clone;
	}

	// NavigableMap API methods

	/**
	 * @since 1.6
	 */
	public Map.Entry<K, V> firstEntry() {
		return exportEntry(getFirstEntry());
	}

	/**
	 * @since 1.6
	 */
	public Map.Entry<K, V> lastEntry() {
		return exportEntry(getLastEntry());
	}

	/**
	 * @since 1.6
	 */
	public Map.Entry<K, V> pollFirstEntry() {
		final Entry<K, V> p = getFirstEntry();
		final Map.Entry<K, V> result = exportEntry(p);
		if (p != null) {
			deleteEntry(p);
		}
		return result;
	}

	/**
	 * @since 1.6
	 */
	public Map.Entry<K, V> pollLastEntry() {
		final Entry<K, V> p = getLastEntry();
		final Map.Entry<K, V> result = exportEntry(p);
		if (p != null) {
			deleteEntry(p);
		}
		return result;
	}

	/**
	 * @throws ClassCastException   {@inheritDoc}
	 * @throws NullPointerException if the specified key is null and this map uses natural ordering,
	 *                              or its comparator does not permit null keys
	 * @since 1.6
	 */
	public Map.Entry<K, V> lowerEntry(final K key) {
		return exportEntry(getLowerEntry(key));
	}

	/**
	 * @throws ClassCastException   {@inheritDoc}
	 * @throws NullPointerException if the specified key is null and this map uses natural ordering,
	 *                              or its comparator does not permit null keys
	 * @since 1.6
	 */
	public K lowerKey(final K key) {
		return keyOrNull(getLowerEntry(key));
	}

	/**
	 * @throws ClassCastException   {@inheritDoc}
	 * @throws NullPointerException if the specified key is null and this map uses natural ordering,
	 *                              or its comparator does not permit null keys
	 * @since 1.6
	 */
	public Map.Entry<K, V> floorEntry(final K key) {
		return exportEntry(getFloorEntry(key));
	}

	/**
	 * @throws ClassCastException   {@inheritDoc}
	 * @throws NullPointerException if the specified key is null and this map uses natural ordering,
	 *                              or its comparator does not permit null keys
	 * @since 1.6
	 */
	public K floorKey(final K key) {
		return keyOrNull(getFloorEntry(key));
	}

	/**
	 * @throws ClassCastException   {@inheritDoc}
	 * @throws NullPointerException if the specified key is null and this map uses natural ordering,
	 *                              or its comparator does not permit null keys
	 * @since 1.6
	 */
	public Map.Entry<K, V> ceilingEntry(final K key) {
		return exportEntry(getCeilingEntry(key));
	}

	/**
	 * @throws ClassCastException   {@inheritDoc}
	 * @throws NullPointerException if the specified key is null and this map uses natural ordering,
	 *                              or its comparator does not permit null keys
	 * @since 1.6
	 */
	public K ceilingKey(final K key) {
		return keyOrNull(getCeilingEntry(key));
	}

	/**
	 * @throws ClassCastException   {@inheritDoc}
	 * @throws NullPointerException if the specified key is null and this map uses natural ordering,
	 *                              or its comparator does not permit null keys
	 * @since 1.6
	 */
	public Map.Entry<K, V> higherEntry(final K key) {
		return exportEntry(getHigherEntry(key));
	}

	/**
	 * @throws ClassCastException   {@inheritDoc}
	 * @throws NullPointerException if the specified key is null and this map uses natural ordering,
	 *                              or its comparator does not permit null keys
	 * @since 1.6
	 */
	public K higherKey(final K key) {
		return keyOrNull(getHigherEntry(key));
	}

	// Views

	/**
	 * Fields initialized to contain an instance of the entry set view the first time this view is
	 * requested. Views are stateless, so there's no reason to create more than one.
	 */
	protected transient EntrySet entrySet;
	protected transient KeySet<K> navigableKeySet;
	protected transient NavigableMap<K, V> descendingMap;

	/**
	 * Returns a {@link Set} view of the keys contained in this map.
	 * <p>
	 * <p>
	 * The set's iterator returns the keys in ascending order. The set's spliterator is
	 * <em><a href="Spliterator.html#binding">late-binding</a></em>,
	 * <em>fail-fast</em>, and additionally reports {@link Spliterator#SORTED} and
	 * {@link Spliterator#ORDERED} with an encounter order that is ascending key order. The
	 * spliterator's comparator (see {@link java.util.Spliterator#getComparator()}) is {@code null}
	 * if the tree map's comparator (see {@link #comparator()}) is {@code null}. Otherwise, the
	 * spliterator's comparator is the same as or imposes the same total ordering as the tree map's
	 * comparator.
	 * <p>
	 * <p>
	 * The set is backed by the map, so changes to the map are reflected in the set, and vice-versa.
	 * If the map is modified while an iteration over the set is in progress (except through the
	 * iterator's own {@code remove} operation), the results of the iteration are undefined. The set
	 * supports element removal, which removes the corresponding mapping from the map, via the
	 * {@code Iterator.remove}, {@code Set.remove}, {@code removeAll}, {@code retainAll}, and
	 * {@code clear} operations. It does not support the {@code add} or {@code addAll} operations.
	 */
	@Override
	public Set<K> keySet() {
		return navigableKeySet();
	}

	/**
	 * @since 1.6
	 */
	public NavigableSet<K> navigableKeySet() {
		final KeySet<K> nks = navigableKeySet;
		return nks != null ? nks : (navigableKeySet = new KeySet<K>(this));
	}

	/**
	 * @since 1.6
	 */
	public NavigableSet<K> descendingKeySet() {
		return descendingMap().navigableKeySet();
	}

	/**
	 * Returns a {@link Set} view of the mappings contained in this map.
	 * <p>
	 * <p>
	 * The set's iterator returns the entries in ascending key order. The sets's spliterator is
	 * <em><a href="Spliterator.html#binding">late-binding</a></em>,
	 * <em>fail-fast</em>, and additionally reports {@link Spliterator#SORTED} and
	 * {@link Spliterator#ORDERED} with an encounter order that is ascending key order.
	 * <p>
	 * <p>
	 * The set is backed by the map, so changes to the map are reflected in the set, and vice-versa.
	 * If the map is modified while an iteration over the set is in progress (except through the
	 * iterator's own {@code remove} operation, or through the {@code setValue} operation on a map
	 * entry returned by the iterator) the results of the iteration are undefined. The set supports
	 * element removal, which removes the corresponding mapping from the map, via the
	 * {@code Iterator.remove}, {@code Set.remove}, {@code removeAll}, {@code retainAll} and
	 * {@code clear} operations. It does not support the {@code add} or {@code addAll} operations.
	 */
	@Override
	public Set<Map.Entry<K, V>> entrySet() {
		final EntrySet es = entrySet;
		return es != null ? es : (entrySet = new EntrySet());
	}

	/**
	 * @since 1.6
	 */
	public NavigableMap<K, V> descendingMap() {
		final NavigableMap<K, V> km = descendingMap;
		return km != null ? km : (descendingMap = new DescendingSubMap<K, V>(this, true, null, true,
				true, null, true));
	}

	/**
	 * @throws ClassCastException       {@inheritDoc}
	 * @throws NullPointerException     if {@code fromKey} or {@code toKey} is null and this map
	 *                                  uses natural ordering, or its comparator does not permit
	 *                                  null keys
	 * @throws IllegalArgumentException {@inheritDoc}
	 * @since 1.6
	 */
	public NavigableMap<K, V> subMap(final K fromKey, final boolean fromInclusive, final K toKey,
			final boolean toInclusive) {
		return new AscendingSubMap<K, V>(this, false, fromKey, fromInclusive, false, toKey,
				toInclusive);
	}

	/**
	 * @throws ClassCastException       {@inheritDoc}
	 * @throws NullPointerException     if {@code toKey} is null and this map uses natural ordering,
	 *                                  or its comparator does not permit null keys
	 * @throws IllegalArgumentException {@inheritDoc}
	 * @since 1.6
	 */
	public NavigableMap<K, V> headMap(final K toKey, final boolean inclusive) {
		return new AscendingSubMap<K, V>(this, true, null, true, false, toKey, inclusive);
	}

	/**
	 * @throws ClassCastException       {@inheritDoc}
	 * @throws NullPointerException     if {@code fromKey} is null and this map uses natural
	 *                                  ordering, or its comparator does not permit null keys
	 * @throws IllegalArgumentException {@inheritDoc}
	 * @since 1.6
	 */
	public NavigableMap<K, V> tailMap(final K fromKey, final boolean inclusive) {
		return new AscendingSubMap<K, V>(this, false, fromKey, inclusive, true, null, true);
	}

	/**
	 * @throws ClassCastException       {@inheritDoc}
	 * @throws NullPointerException     if {@code fromKey} or {@code toKey} is null and this map
	 *                                  uses natural ordering, or its comparator does not permit
	 *                                  null keys
	 * @throws IllegalArgumentException {@inheritDoc}
	 */
	public SortedMap<K, V> subMap(final K fromKey, final K toKey) {
		return subMap(fromKey, true, toKey, false);
	}

	/**
	 * @throws ClassCastException       {@inheritDoc}
	 * @throws NullPointerException     if {@code toKey} is null and this map uses natural ordering,
	 *                                  or its comparator does not permit null keys
	 * @throws IllegalArgumentException {@inheritDoc}
	 */
	public SortedMap<K, V> headMap(final K toKey) {
		return headMap(toKey, false);
	}

	/**
	 * @throws ClassCastException       {@inheritDoc}
	 * @throws NullPointerException     if {@code fromKey} is null and this map uses natural
	 *                                  ordering, or its comparator does not permit null keys
	 * @throws IllegalArgumentException {@inheritDoc}
	 */
	public SortedMap<K, V> tailMap(final K fromKey) {
		return tailMap(fromKey, true);
	}

	@Override
	public boolean replace(final K key, final V oldValue, final V newValue) {
		final Entry<K, V> p = getEntry(key);
		if (p != null && Objects.equals(oldValue, p.value)) {
			p.value = newValue;
			return true;
		}
		return false;
	}

	@Override
	public V replace(final K key, final V value) {
		final Entry<K, V> p = getEntry(key);
		if (p != null) {
			final V oldValue = p.value;
			p.value = value;
			return oldValue;
		}
		return null;
	}

	@Override
	public void forEach(final BiConsumer<? super K, ? super V> action) {
		Objects.requireNonNull(action);
		final int expectedModCount = modCount;
		for (Entry<K, V> e = getFirstEntry(); e != null; e = successor(e)) {
			action.accept(e.key, e.value);

			if (expectedModCount != modCount) {
				throw new ConcurrentModificationException();
			}
		}
	}

	@Override
	public void replaceAll(final BiFunction<? super K, ? super V, ? extends V> function) {
		Objects.requireNonNull(function);
		final int expectedModCount = modCount;

		for (Entry<K, V> e = getFirstEntry(); e != null; e = successor(e)) {
			e.value = function.apply(e.key, e.value);

			if (expectedModCount != modCount) {
				throw new ConcurrentModificationException();
			}
		}
	}

	// View class support

	class Values
			extends AbstractCollection<V> {
		@Override
		public Iterator<V> iterator() {
			return new ValueIterator(getFirstEntry());
		}

		@Override
		public int size() {
			return OriginalTree.this.size();
		}

		@Override
		public boolean contains(final Object o) {
			return OriginalTree.this.containsValue(o);
		}

		@Override
		public boolean remove(final Object o) {
			for (Entry<K, V> e = getFirstEntry(); e != null; e = successor(e)) {
				if (valEquals(e.getValue(), o)) {
					deleteEntry(e);
					return true;
				}
			}
			return false;
		}

		@Override
		public void clear() {
			OriginalTree.this.clear();
		}

		public Spliterator<V> spliterator() {
			return new ValueSpliterator<K, V>(OriginalTree.this, null, null, 0, -1, 0);
		}
	}

	class EntrySet
			extends AbstractSet<Map.Entry<K, V>> {
		@Override
		public Iterator<Map.Entry<K, V>> iterator() {
			return new EntryIterator(getFirstEntry());
		}

		@Override
		public boolean contains(final Object o) {
			if (!(o instanceof Map.Entry)) {
				return false;
			}
			final Map.Entry<?, ?> entry = (Map.Entry<?, ?>) o;
			final Object value = entry.getValue();
			final Entry<K, V> p = getEntry(entry.getKey());
			return p != null && valEquals(p.getValue(), value);
		}

		@Override
		public boolean remove(final Object o) {
			if (!(o instanceof Map.Entry)) {
				return false;
			}
			final Map.Entry<?, ?> entry = (Map.Entry<?, ?>) o;
			final Object value = entry.getValue();
			final Entry<K, V> p = getEntry(entry.getKey());
			if (p != null && valEquals(p.getValue(), value)) {
				deleteEntry(p);
				return true;
			}
			return false;
		}

		@Override
		public int size() {
			return OriginalTree.this.size();
		}

		@Override
		public void clear() {
			OriginalTree.this.clear();
		}

		public Spliterator<Map.Entry<K, V>> spliterator() {
			return new EntrySpliterator<K, V>(OriginalTree.this, null, null, 0, -1, 0);
		}
	}

	/*
	 * Unlike Values and EntrySet, the KeySet class is static, delegating to a NavigableMap to allow
	 * use by SubMaps, which outweighs the ugliness of needing type-tests for the following Iterator
	 * methods that are defined appropriately in main versus submap classes.
	 */

	Iterator<K> keyIterator() {
		return new KeyIterator(getFirstEntry());
	}

	Iterator<K> descendingKeyIterator() {
		return new DescendingKeyIterator(getLastEntry());
	}

	static final class KeySet<E>
			extends AbstractSet<E>
			implements NavigableSet<E> {
		protected final NavigableMap<E, ?> m;

		KeySet(final NavigableMap<E, ?> map) {
			m = map;
		}

		@Override
		public Iterator<E> iterator() {
			if (m instanceof OriginalTree) {
				return ((OriginalTree<E, ?>) m).keyIterator();
			}
			return ((OriginalTree.NavigableSubMap<E, ?>) m).keyIterator();
		}

		public Iterator<E> descendingIterator() {
			if (m instanceof OriginalTree) {
				return ((OriginalTree<E, ?>) m).descendingKeyIterator();
			}
			return ((OriginalTree.NavigableSubMap<E, ?>) m).descendingKeyIterator();
		}

		@Override
		public int size() {
			return m.size();
		}

		@Override
		public boolean isEmpty() {
			return m.isEmpty();
		}

		@Override
		public boolean contains(final Object o) {
			return m.containsKey(o);
		}

		@Override
		public void clear() {
			m.clear();
		}

		public E lower(final E e) {
			return m.lowerKey(e);
		}

		public E floor(final E e) {
			return m.floorKey(e);
		}

		public E ceiling(final E e) {
			return m.ceilingKey(e);
		}

		public E higher(final E e) {
			return m.higherKey(e);
		}

		public E first() {
			return m.firstKey();
		}

		public E last() {
			return m.lastKey();
		}

		public Comparator<? super E> comparator() {
			return m.comparator();
		}

		public E pollFirst() {
			final Map.Entry<E, ?> e = m.pollFirstEntry();
			return e == null ? null : e.getKey();
		}

		public E pollLast() {
			final Map.Entry<E, ?> e = m.pollLastEntry();
			return e == null ? null : e.getKey();
		}

		@Override
		public boolean remove(final Object o) {
			final int oldSize = size();
			m.remove(o);
			return size() != oldSize;
		}

		public NavigableSet<E> subSet(final E fromElement, final boolean fromInclusive,
				final E toElement, final boolean toInclusive) {
			return new KeySet<E>(m.subMap(fromElement, fromInclusive, toElement, toInclusive));
		}

		public NavigableSet<E> headSet(final E toElement, final boolean inclusive) {
			return new KeySet<E>(m.headMap(toElement, inclusive));
		}

		public NavigableSet<E> tailSet(final E fromElement, final boolean inclusive) {
			return new KeySet<E>(m.tailMap(fromElement, inclusive));
		}

		public SortedSet<E> subSet(final E fromElement, final E toElement) {
			return subSet(fromElement, true, toElement, false);
		}

		public SortedSet<E> headSet(final E toElement) {
			return headSet(toElement, false);
		}

		public SortedSet<E> tailSet(final E fromElement) {
			return tailSet(fromElement, true);
		}

		public NavigableSet<E> descendingSet() {
			return new KeySet<E>(m.descendingMap());
		}

		public Spliterator<E> spliterator() {
			return keySpliteratorFor(m);
		}
	}

	/**
	 * Base class for OriginalTree Iterators
	 */
	abstract class PrivateEntryIterator<T>
			implements Iterator<T> {
		Entry<K, V> next;
		Entry<K, V> lastReturned;
		int expectedModCount;

		PrivateEntryIterator(final Entry<K, V> first) {
			expectedModCount = modCount;
			lastReturned = null;
			next = first;
		}

		public final boolean hasNext() {
			return next != null;
		}

		final Entry<K, V> nextEntry() {
			final Entry<K, V> e = next;
			if (e == null) {
				throw new NoSuchElementException();
			}
			if (modCount != expectedModCount) {
				throw new ConcurrentModificationException();
			}
			next = successor(e);
			lastReturned = e;
			return e;
		}

		final Entry<K, V> prevEntry() {
			final Entry<K, V> e = next;
			if (e == null) {
				throw new NoSuchElementException();
			}
			if (modCount != expectedModCount) {
				throw new ConcurrentModificationException();
			}
			next = predecessor(e);
			lastReturned = e;
			return e;
		}

		public void remove() {
			if (lastReturned == null) {
				throw new IllegalStateException();
			}
			if (modCount != expectedModCount) {
				throw new ConcurrentModificationException();
			}
			// deleted entries are replaced by their successors
			if (lastReturned.left != null && lastReturned.right != null) {
				next = lastReturned;
			}
			deleteEntry(lastReturned);
			expectedModCount = modCount;
			lastReturned = null;
		}
	}

	final class EntryIterator
			extends PrivateEntryIterator<Map.Entry<K, V>> {
		EntryIterator(final Entry<K, V> first) {
			super(first);
		}

		public Map.Entry<K, V> next() {
			return nextEntry();
		}
	}

	final class ValueIterator
			extends PrivateEntryIterator<V> {
		ValueIterator(final Entry<K, V> first) {
			super(first);
		}

		public V next() {
			return nextEntry().value;
		}
	}

	final class KeyIterator
			extends PrivateEntryIterator<K> {
		KeyIterator(final Entry<K, V> first) {
			super(first);
		}

		public K next() {
			return nextEntry().key;
		}
	}

	final class DescendingKeyIterator
			extends PrivateEntryIterator<K> {
		DescendingKeyIterator(final Entry<K, V> first) {
			super(first);
		}

		public K next() {
			return prevEntry().key;
		}

		@Override
		public void remove() {
			if (lastReturned == null) {
				throw new IllegalStateException();
			}
			if (modCount != expectedModCount) {
				throw new ConcurrentModificationException();
			}
			deleteEntry(lastReturned);
			lastReturned = null;
			expectedModCount = modCount;
		}
	}

	// Little utilities

	/**
	 * Compares two keys using the correct comparison method for this OriginalTree.
	 */
	@SuppressWarnings("unchecked")
	final int compare(final Object k1, final Object k2) {
		return comparator == null ? ((Comparable<? super K>) k1).compareTo((K) k2) :
				comparator.compare((K) k1, (K) k2);
	}

	/**
	 * Test two values for equality. Differs from o1.equals(o2) only in that it copes with
	 * {@code null} o1 properly.
	 */
	static final boolean valEquals(final Object o1, final Object o2) {
		return o1 == null ? o2 == null : o1.equals(o2);
	}

	/**
	 * Return SimpleImmutableEntry for entry, or null if null
	 */
	static <K, V> Map.Entry<K, V> exportEntry(final OriginalTree.Entry<K, V> e) {
		return e == null ? null : new AbstractMap.SimpleImmutableEntry<K, V>(e);
	}

	/**
	 * Return key for entry, or null if null
	 */
	static <K, V> K keyOrNull(final OriginalTree.Entry<K, V> e) {
		return e == null ? null : e.key;
	}

	/**
	 * Returns the key corresponding to the specified Entry.
	 *
	 * @throws NoSuchElementException if the Entry is null
	 */
	static <K> K key(final Entry<K, ?> e) {
		if (e == null) {
			throw new NoSuchElementException();
		}
		return e.key;
	}


	// SubMaps

	/**
	 * Dummy value serving as unmatchable fence key for unbounded SubMapIterators
	 */
	protected static final Object UNBOUNDED = new Object();

	/**
	 * @serial include
	 */
	abstract static class NavigableSubMap<K, V>
			extends AbstractMap<K, V>
			implements NavigableMap<K, V>, java.io.Serializable {
		private static final long serialVersionUID = -2102997345730753016L;
		/**
		 * The backing map.
		 */
		final OriginalTree<K, V> m;

		/**
		 * Endpoints are represented as triples (fromStart, lo, loInclusive) and (toEnd, hi,
		 * hiInclusive). If fromStart is true, then the low (absolute) bound is the start of the
		 * backing map, and the other values are ignored. Otherwise, if loInclusive is true, lo is
		 * the inclusive bound, else lo is the exclusive bound. Similarly for the upper bound.
		 */
		final K lo, hi;
		final boolean fromStart, toEnd;
		final boolean loInclusive, hiInclusive;

		NavigableSubMap(final OriginalTree<K, V> m, final boolean fromStart, final K lo,
				final boolean loInclusive, final boolean toEnd, final K hi,
				final boolean hiInclusive) {
			if (!fromStart && !toEnd) {
				if (m.compare(lo, hi) > 0) {
					throw new IllegalArgumentException("fromKey > toKey");
				}
			} else {
				if (!fromStart) // type check
				{
					m.compare(lo, lo);
				}
				if (!toEnd) {
					m.compare(hi, hi);
				}
			}

			this.m = m;
			this.fromStart = fromStart;
			this.lo = lo;
			this.loInclusive = loInclusive;
			this.toEnd = toEnd;
			this.hi = hi;
			this.hiInclusive = hiInclusive;
		}

		// internal utilities

		final boolean tooLow(final Object key) {
			if (!fromStart) {
				final int c = m.compare(key, lo);
				if (c < 0 || c == 0 && !loInclusive) {
					return true;
				}
			}
			return false;
		}

		final boolean tooHigh(final Object key) {
			if (!toEnd) {
				final int c = m.compare(key, hi);
				if (c > 0 || c == 0 && !hiInclusive) {
					return true;
				}
			}
			return false;
		}

		final boolean inRange(final Object key) {
			return !tooLow(key) && !tooHigh(key);
		}

		final boolean inClosedRange(final Object key) {
			return (fromStart || m.compare(key, lo) >= 0) && (toEnd || m.compare(hi, key) >= 0);
		}

		final boolean inRange(final Object key, final boolean inclusive) {
			return inclusive ? inRange(key) : inClosedRange(key);
		}

		/*
		 * Absolute versions of relation operations. Subclasses map to these using like-named "sub"
		 * versions that invert senses for descending maps
		 */

		final OriginalTree.Entry<K, V> absLowest() {
			final OriginalTree.Entry<K, V> e = fromStart ? m.getFirstEntry() :
					loInclusive ? m.getCeilingEntry(lo) : m.getHigherEntry(lo);
			return e == null || tooHigh(e.key) ? null : e;
		}

		final OriginalTree.Entry<K, V> absHighest() {
			final OriginalTree.Entry<K, V> e = toEnd ? m.getLastEntry() :
					hiInclusive ? m.getFloorEntry(hi) : m.getLowerEntry(hi);
			return e == null || tooLow(e.key) ? null : e;
		}

		final OriginalTree.Entry<K, V> absCeiling(final K key) {
			if (tooLow(key)) {
				return absLowest();
			}
			final OriginalTree.Entry<K, V> e = m.getCeilingEntry(key);
			return e == null || tooHigh(e.key) ? null : e;
		}

		final OriginalTree.Entry<K, V> absHigher(final K key) {
			if (tooLow(key)) {
				return absLowest();
			}
			final OriginalTree.Entry<K, V> e = m.getHigherEntry(key);
			return e == null || tooHigh(e.key) ? null : e;
		}

		final OriginalTree.Entry<K, V> absFloor(final K key) {
			if (tooHigh(key)) {
				return absHighest();
			}
			final OriginalTree.Entry<K, V> e = m.getFloorEntry(key);
			return e == null || tooLow(e.key) ? null : e;
		}

		final OriginalTree.Entry<K, V> absLower(final K key) {
			if (tooHigh(key)) {
				return absHighest();
			}
			final OriginalTree.Entry<K, V> e = m.getLowerEntry(key);
			return e == null || tooLow(e.key) ? null : e;
		}

		/**
		 * Returns the absolute high fence for ascending traversal
		 */
		final OriginalTree.Entry<K, V> absHighFence() {
			return toEnd ? null : hiInclusive ? m.getHigherEntry(hi) : m.getCeilingEntry(hi);
		}

		/**
		 * Return the absolute low fence for descending traversal
		 */
		final OriginalTree.Entry<K, V> absLowFence() {
			return fromStart ? null : loInclusive ? m.getLowerEntry(lo) : m.getFloorEntry(lo);
		}

		// Abstract methods defined in ascending vs descending classes
		// These relay to the appropriate absolute versions

		abstract OriginalTree.Entry<K, V> subLowest();

		abstract OriginalTree.Entry<K, V> subHighest();

		abstract OriginalTree.Entry<K, V> subCeiling(K key);

		abstract OriginalTree.Entry<K, V> subHigher(K key);

		abstract OriginalTree.Entry<K, V> subFloor(K key);

		abstract OriginalTree.Entry<K, V> subLower(K key);

		/**
		 * Returns ascending iterator from the perspective of this submap
		 */
		abstract Iterator<K> keyIterator();

		abstract Spliterator<K> keySpliterator();

		/**
		 * Returns descending iterator from the perspective of this submap
		 */
		abstract Iterator<K> descendingKeyIterator();

		// public methods

		@Override
		public boolean isEmpty() {
			return fromStart && toEnd ? m.isEmpty() : entrySet().isEmpty();
		}

		@Override
		public int size() {
			return fromStart && toEnd ? m.size() : entrySet().size();
		}

		@Override
		public final boolean containsKey(final Object key) {
			return inRange(key) && m.containsKey(key);
		}

		@Override
		public final V put(final K key, final V value) {
			if (!inRange(key)) {
				throw new IllegalArgumentException("key out of range");
			}
			return m.put(key, value);
		}

		@Override
		public final V get(final Object key) {
			return !inRange(key) ? null : m.get(key);
		}

		@Override
		public final V remove(final Object key) {
			return !inRange(key) ? null : m.remove(key);
		}

		public final Map.Entry<K, V> ceilingEntry(final K key) {
			return exportEntry(subCeiling(key));
		}

		public final K ceilingKey(final K key) {
			return keyOrNull(subCeiling(key));
		}

		public final Map.Entry<K, V> higherEntry(final K key) {
			return exportEntry(subHigher(key));
		}

		public final K higherKey(final K key) {
			return keyOrNull(subHigher(key));
		}

		public final Map.Entry<K, V> floorEntry(final K key) {
			return exportEntry(subFloor(key));
		}

		public final K floorKey(final K key) {
			return keyOrNull(subFloor(key));
		}

		public final Map.Entry<K, V> lowerEntry(final K key) {
			return exportEntry(subLower(key));
		}

		public final K lowerKey(final K key) {
			return keyOrNull(subLower(key));
		}

		public final K firstKey() {
			return key(subLowest());
		}

		public final K lastKey() {
			return key(subHighest());
		}

		public final Map.Entry<K, V> firstEntry() {
			return exportEntry(subLowest());
		}

		public final Map.Entry<K, V> lastEntry() {
			return exportEntry(subHighest());
		}

		public final Map.Entry<K, V> pollFirstEntry() {
			final OriginalTree.Entry<K, V> e = subLowest();
			final Map.Entry<K, V> result = exportEntry(e);
			if (e != null) {
				m.deleteEntry(e);
			}
			return result;
		}

		public final Map.Entry<K, V> pollLastEntry() {
			final OriginalTree.Entry<K, V> e = subHighest();
			final Map.Entry<K, V> result = exportEntry(e);
			if (e != null) {
				m.deleteEntry(e);
			}
			return result;
		}

		// Views
		transient NavigableMap<K, V> descendingMapView;
		transient EntrySetView entrySetView;
		transient KeySet<K> navigableKeySetView;

		public final NavigableSet<K> navigableKeySet() {
			final KeySet<K> nksv = navigableKeySetView;
			return nksv != null ? nksv : (navigableKeySetView = new OriginalTree.KeySet<K>(this));
		}

		@Override
		public final Set<K> keySet() {
			return navigableKeySet();
		}

		public NavigableSet<K> descendingKeySet() {
			return descendingMap().navigableKeySet();
		}

		public final SortedMap<K, V> subMap(final K fromKey, final K toKey) {
			return subMap(fromKey, true, toKey, false);
		}

		public final SortedMap<K, V> headMap(final K toKey) {
			return headMap(toKey, false);
		}

		public final SortedMap<K, V> tailMap(final K fromKey) {
			return tailMap(fromKey, true);
		}

		// View classes

		abstract class EntrySetView
				extends AbstractSet<Map.Entry<K, V>> {
			protected transient int size = -1, sizeModCount;

			@Override
			public int size() {
				if (fromStart && toEnd) {
					return m.size();
				}
				if (size == -1 || sizeModCount != m.modCount) {
					sizeModCount = m.modCount;
					size = 0;
					final Iterator<?> i = iterator();
					while (i.hasNext()) {
						size++;
						i.next();
					}
				}
				return size;
			}

			@Override
			public boolean isEmpty() {
				final OriginalTree.Entry<K, V> n = absLowest();
				return n == null || tooHigh(n.key);
			}

			@Override
			public boolean contains(final Object o) {
				if (!(o instanceof Map.Entry)) {
					return false;
				}
				final Map.Entry<?, ?> entry = (Map.Entry<?, ?>) o;
				final Object key = entry.getKey();
				if (!inRange(key)) {
					return false;
				}
				final OriginalTree.Entry<?, ?> node = m.getEntry(key);
				return node != null && valEquals(node.getValue(), entry.getValue());
			}

			@Override
			public boolean remove(final Object o) {
				if (!(o instanceof Map.Entry)) {
					return false;
				}
				final Map.Entry<?, ?> entry = (Map.Entry<?, ?>) o;
				final Object key = entry.getKey();
				if (!inRange(key)) {
					return false;
				}
				final OriginalTree.Entry<K, V> node = m.getEntry(key);
				if (node != null && valEquals(node.getValue(), entry.getValue())) {
					m.deleteEntry(node);
					return true;
				}
				return false;
			}
		}

		/**
		 * Iterators for SubMaps
		 */
		abstract class SubMapIterator<T>
				implements Iterator<T> {
			OriginalTree.Entry<K, V> lastReturned;
			OriginalTree.Entry<K, V> next;
			final Object fenceKey;
			int expectedModCount;

			SubMapIterator(final OriginalTree.Entry<K, V> first,
					final OriginalTree.Entry<K, V> fence) {
				expectedModCount = m.modCount;
				lastReturned = null;
				next = first;
				fenceKey = fence == null ? UNBOUNDED : fence.key;
			}

			public final boolean hasNext() {
				return next != null && next.key != fenceKey;
			}

			final OriginalTree.Entry<K, V> nextEntry() {
				final OriginalTree.Entry<K, V> e = next;
				if (e == null || e.key == fenceKey) {
					throw new NoSuchElementException();
				}
				if (m.modCount != expectedModCount) {
					throw new ConcurrentModificationException();
				}
				next = successor(e);
				lastReturned = e;
				return e;
			}

			final OriginalTree.Entry<K, V> prevEntry() {
				final OriginalTree.Entry<K, V> e = next;
				if (e == null || e.key == fenceKey) {
					throw new NoSuchElementException();
				}
				if (m.modCount != expectedModCount) {
					throw new ConcurrentModificationException();
				}
				next = predecessor(e);
				lastReturned = e;
				return e;
			}

			final void removeAscending() {
				if (lastReturned == null) {
					throw new IllegalStateException();
				}
				if (m.modCount != expectedModCount) {
					throw new ConcurrentModificationException();
				}
				// deleted entries are replaced by their successors
				if (lastReturned.left != null && lastReturned.right != null) {
					next = lastReturned;
				}
				m.deleteEntry(lastReturned);
				lastReturned = null;
				expectedModCount = m.modCount;
			}

			final void removeDescending() {
				if (lastReturned == null) {
					throw new IllegalStateException();
				}
				if (m.modCount != expectedModCount) {
					throw new ConcurrentModificationException();
				}
				m.deleteEntry(lastReturned);
				lastReturned = null;
				expectedModCount = m.modCount;
			}

		}

		final class SubMapEntryIterator
				extends SubMapIterator<Map.Entry<K, V>> {
			SubMapEntryIterator(final OriginalTree.Entry<K, V> first,
					final OriginalTree.Entry<K, V> fence) {
				super(first, fence);
			}

			public Map.Entry<K, V> next() {
				return nextEntry();
			}

			public void remove() {
				removeAscending();
			}
		}

		final class DescendingSubMapEntryIterator
				extends SubMapIterator<Map.Entry<K, V>> {
			DescendingSubMapEntryIterator(final OriginalTree.Entry<K, V> last,
					final OriginalTree.Entry<K, V> fence) {
				super(last, fence);
			}

			public Map.Entry<K, V> next() {
				return prevEntry();
			}

			public void remove() {
				removeDescending();
			}
		}

		// Implement minimal Spliterator as KeySpliterator backup
		final class SubMapKeyIterator
				extends SubMapIterator<K>
				implements Spliterator<K> {
			SubMapKeyIterator(final OriginalTree.Entry<K, V> first,
					final OriginalTree.Entry<K, V> fence) {
				super(first, fence);
			}

			public K next() {
				return nextEntry().key;
			}

			public void remove() {
				removeAscending();
			}

			public Spliterator<K> trySplit() {
				return null;
			}

			public void forEachRemaining(final Consumer<? super K> action) {
				while (hasNext()) {
					action.accept(next());
				}
			}

			public boolean tryAdvance(final Consumer<? super K> action) {
				if (hasNext()) {
					action.accept(next());
					return true;
				}
				return false;
			}

			public long estimateSize() {
				return Long.MAX_VALUE;
			}

			public int characteristics() {
				return Spliterator.DISTINCT | Spliterator.ORDERED | Spliterator.SORTED;
			}

			public final Comparator<? super K> getComparator() {
				return comparator();
			}
		}

		final class DescendingSubMapKeyIterator
				extends SubMapIterator<K>
				implements Spliterator<K> {
			DescendingSubMapKeyIterator(final OriginalTree.Entry<K, V> last,
					final OriginalTree.Entry<K, V> fence) {
				super(last, fence);
			}

			public K next() {
				return prevEntry().key;
			}

			public void remove() {
				removeDescending();
			}

			public Spliterator<K> trySplit() {
				return null;
			}

			public void forEachRemaining(final Consumer<? super K> action) {
				while (hasNext()) {
					action.accept(next());
				}
			}

			public boolean tryAdvance(final Consumer<? super K> action) {
				if (hasNext()) {
					action.accept(next());
					return true;
				}
				return false;
			}

			public long estimateSize() {
				return Long.MAX_VALUE;
			}

			public int characteristics() {
				return Spliterator.DISTINCT | Spliterator.ORDERED;
			}
		}
	}

	/**
	 * @serial include
	 */
	static final class AscendingSubMap<K, V>
			extends NavigableSubMap<K, V> {
		private static final long serialVersionUID = 912986545866124060L;

		AscendingSubMap(final OriginalTree<K, V> m, final boolean fromStart, final K lo,
				final boolean loInclusive, final boolean toEnd, final K hi,
				final boolean hiInclusive) {
			super(m, fromStart, lo, loInclusive, toEnd, hi, hiInclusive);
		}

		public Comparator<? super K> comparator() {
			return m.comparator();
		}

		public NavigableMap<K, V> subMap(final K fromKey, final boolean fromInclusive,
				final K toKey, final boolean toInclusive) {
			if (!inRange(fromKey, fromInclusive)) {
				throw new IllegalArgumentException("fromKey out of range");
			}
			if (!inRange(toKey, toInclusive)) {
				throw new IllegalArgumentException("toKey out of range");
			}
			return new AscendingSubMap<K, V>(m, false, fromKey, fromInclusive, false, toKey,
					toInclusive);
		}

		public NavigableMap<K, V> headMap(final K toKey, final boolean inclusive) {
			if (!inRange(toKey, inclusive)) {
				throw new IllegalArgumentException("toKey out of range");
			}
			return new AscendingSubMap<K, V>(m, fromStart, lo, loInclusive, false, toKey,
					inclusive);
		}

		public NavigableMap<K, V> tailMap(final K fromKey, final boolean inclusive) {
			if (!inRange(fromKey, inclusive)) {
				throw new IllegalArgumentException("fromKey out of range");
			}
			return new AscendingSubMap<K, V>(m, false, fromKey, inclusive, toEnd, hi, hiInclusive);
		}

		public NavigableMap<K, V> descendingMap() {
			final NavigableMap<K, V> mv = descendingMapView;
			return mv != null ? mv : (descendingMapView = new DescendingSubMap<K, V>(m, fromStart,
					lo, loInclusive, toEnd, hi, hiInclusive));
		}

		@Override
		Iterator<K> keyIterator() {
			return new SubMapKeyIterator(absLowest(), absHighFence());
		}

		@Override
		Spliterator<K> keySpliterator() {
			return new SubMapKeyIterator(absLowest(), absHighFence());
		}

		@Override
		Iterator<K> descendingKeyIterator() {
			return new DescendingSubMapKeyIterator(absHighest(), absLowFence());
		}

		final class AscendingEntrySetView
				extends EntrySetView {
			@Override
			public Iterator<Map.Entry<K, V>> iterator() {
				return new SubMapEntryIterator(absLowest(), absHighFence());
			}
		}

		@Override
		public Set<Map.Entry<K, V>> entrySet() {
			final EntrySetView es = entrySetView;
			return es != null ? es : (entrySetView = new AscendingEntrySetView());
		}

		@Override
		OriginalTree.Entry<K, V> subLowest() {
			return absLowest();
		}

		@Override
		OriginalTree.Entry<K, V> subHighest() {
			return absHighest();
		}

		@Override
		OriginalTree.Entry<K, V> subCeiling(final K key) {
			return absCeiling(key);
		}

		@Override
		OriginalTree.Entry<K, V> subHigher(final K key) {
			return absHigher(key);
		}

		@Override
		OriginalTree.Entry<K, V> subFloor(final K key) {
			return absFloor(key);
		}

		@Override
		OriginalTree.Entry<K, V> subLower(final K key) {
			return absLower(key);
		}
	}

	/**
	 * @serial include
	 */
	static final class DescendingSubMap<K, V>
			extends NavigableSubMap<K, V> {
		private static final long serialVersionUID = 912986545866120460L;

		DescendingSubMap(final OriginalTree<K, V> m, final boolean fromStart, final K lo,
				final boolean loInclusive, final boolean toEnd, final K hi,
				final boolean hiInclusive) {
			super(m, fromStart, lo, loInclusive, toEnd, hi, hiInclusive);
		}

		protected final Comparator<? super K> reverseComparator = Collections
				.reverseOrder(m.comparator);

		public Comparator<? super K> comparator() {
			return reverseComparator;
		}

		public NavigableMap<K, V> subMap(final K fromKey, final boolean fromInclusive,
				final K toKey, final boolean toInclusive) {
			if (!inRange(fromKey, fromInclusive)) {
				throw new IllegalArgumentException("fromKey out of range");
			}
			if (!inRange(toKey, toInclusive)) {
				throw new IllegalArgumentException("toKey out of range");
			}
			return new DescendingSubMap<K, V>(m, false, toKey, toInclusive, false, fromKey,
					fromInclusive);
		}

		public NavigableMap<K, V> headMap(final K toKey, final boolean inclusive) {
			if (!inRange(toKey, inclusive)) {
				throw new IllegalArgumentException("toKey out of range");
			}
			return new DescendingSubMap<K, V>(m, false, toKey, inclusive, toEnd, hi, hiInclusive);
		}

		public NavigableMap<K, V> tailMap(final K fromKey, final boolean inclusive) {
			if (!inRange(fromKey, inclusive)) {
				throw new IllegalArgumentException("fromKey out of range");
			}
			return new DescendingSubMap<K, V>(m, fromStart, lo, loInclusive, false, fromKey,
					inclusive);
		}

		public NavigableMap<K, V> descendingMap() {
			final NavigableMap<K, V> mv = descendingMapView;
			return mv != null ? mv : (descendingMapView = new AscendingSubMap<K, V>(m, fromStart,
					lo, loInclusive, toEnd, hi, hiInclusive));
		}

		@Override
		Iterator<K> keyIterator() {
			return new DescendingSubMapKeyIterator(absHighest(), absLowFence());
		}

		@Override
		Spliterator<K> keySpliterator() {
			return new DescendingSubMapKeyIterator(absHighest(), absLowFence());
		}

		@Override
		Iterator<K> descendingKeyIterator() {
			return new SubMapKeyIterator(absLowest(), absHighFence());
		}

		final class DescendingEntrySetView
				extends EntrySetView {
			@Override
			public Iterator<Map.Entry<K, V>> iterator() {
				return new DescendingSubMapEntryIterator(absHighest(), absLowFence());
			}
		}

		@Override
		public Set<Map.Entry<K, V>> entrySet() {
			final EntrySetView es = entrySetView;
			return es != null ? es : (entrySetView = new DescendingEntrySetView());
		}

		@Override
		OriginalTree.Entry<K, V> subLowest() {
			return absHighest();
		}

		@Override
		OriginalTree.Entry<K, V> subHighest() {
			return absLowest();
		}

		@Override
		OriginalTree.Entry<K, V> subCeiling(final K key) {
			return absFloor(key);
		}

		@Override
		OriginalTree.Entry<K, V> subHigher(final K key) {
			return absLower(key);
		}

		@Override
		OriginalTree.Entry<K, V> subFloor(final K key) {
			return absCeiling(key);
		}

		@Override
		OriginalTree.Entry<K, V> subLower(final K key) {
			return absHigher(key);
		}
	}

	/**
	 * This class exists solely for the sake of serialization compatibility with previous releases
	 * of OriginalTree that did not support NavigableMap. It translates an old-version SubMap into a
	 * new-version AscendingSubMap. This class is never otherwise used.
	 *
	 * @serial include
	 */
	protected class SubMap
			extends AbstractMap<K, V>
			implements SortedMap<K, V>, java.io.Serializable {
		private static final long serialVersionUID = -6520786458950516097L;
		protected final boolean fromStart = false, toEnd = false;
		protected K fromKey, toKey;

		protected Object readResolve() {
			return new AscendingSubMap<K, V>(OriginalTree.this, fromStart, fromKey, true, toEnd,
					toKey, false);
		}

		@Override
		public Set<Map.Entry<K, V>> entrySet() {
			throw new InternalError();
		}

		public K lastKey() {
			throw new InternalError();
		}

		public K firstKey() {
			throw new InternalError();
		}

		public SortedMap<K, V> subMap(final K fromKey, final K toKey) {
			throw new InternalError();
		}

		public SortedMap<K, V> headMap(final K toKey) {
			throw new InternalError();
		}

		public SortedMap<K, V> tailMap(final K fromKey) {
			throw new InternalError();
		}

		public Comparator<? super K> comparator() {
			throw new InternalError();
		}
	}


	// Red-black mechanics

	protected static final boolean RED = false;
	protected static final boolean BLACK = true;

	/**
	 * Node in the Tree. Doubles as a means to pass key-value pairs back to user (see Map.Entry).
	 */

	static final class Entry<K, V>
			implements Map.Entry<K, V> {
		K key;
		V value;
		Entry<K, V> left;
		Entry<K, V> right;
		Entry<K, V> parent;
		boolean color = BLACK;

		/**
		 * Make a new cell with given key, value, and parent, and with {@code null} child links, and
		 * BLACK color.
		 */
		Entry(final K key, final V value, final Entry<K, V> parent) {
			this.key = key;
			this.value = value;
			this.parent = parent;
		}

		/**
		 * Returns the key.
		 *
		 * @return the key
		 */
		public K getKey() {
			return key;
		}

		/**
		 * Returns the value associated with the key.
		 *
		 * @return the value associated with the key
		 */
		public V getValue() {
			return value;
		}

		/**
		 * Replaces the value currently associated with the key with the given value.
		 *
		 * @return the value associated with the key before this method was called
		 */
		public V setValue(final V value) {
			final V oldValue = this.value;
			this.value = value;
			return oldValue;
		}

		@Override
		public boolean equals(final Object o) {
			if (!(o instanceof Map.Entry)) {
				return false;
			}
			final Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;

			return valEquals(key, e.getKey()) && valEquals(value, e.getValue());
		}

		@Override
		public int hashCode() {
			final int keyHash = key == null ? 0 : key.hashCode();
			final int valueHash = value == null ? 0 : value.hashCode();
			return keyHash ^ valueHash;
		}

		@Override
		public String toString() {
			return key + "=" + value;
		}
	}

	/**
	 * Returns the root Entry in the OriginalTree (according to the OriginalTree's key-sort
	 * function). Returns null if the OriginalTree is empty.
	 */
	final Entry<K, V> getRootEntry() {
		return root;
	}

	/**
	 * Returns the first Entry in the OriginalTree (according to the OriginalTree's key-sort
	 * function). Returns null if the OriginalTree is empty.
	 */
	final Entry<K, V> getFirstEntry() {
		Entry<K, V> p = root;
		if (p != null) {
			while (p.left != null) {
				p = p.left;
			}
		}
		return p;
	}

	/**
	 * Returns the last Entry in the OriginalTree (according to the OriginalTree's key-sort
	 * function). Returns null if the OriginalTree is empty.
	 */
	final Entry<K, V> getLastEntry() {
		Entry<K, V> p = root;
		if (p != null) {
			while (p.right != null) {
				p = p.right;
			}
		}
		return p;
	}

	/**
	 * Returns the successor of the specified Entry, or null if no such.
	 */
	static <K, V> OriginalTree.Entry<K, V> successor(final Entry<K, V> t) {
		if (t == null) {
			return null;
		} else if (t.right != null) {
			Entry<K, V> p = t.right;
			while (p.left != null) {
				p = p.left;
			}
			return p;
		} else {
			Entry<K, V> p = t.parent;
			Entry<K, V> ch = t;
			while (p != null && ch == p.right) {
				ch = p;
				p = p.parent;
			}
			return p;
		}
	}

	/**
	 * Returns the predecessor of the specified Entry, or null if no such.
	 */
	static <K, V> Entry<K, V> predecessor(final Entry<K, V> t) {
		if (t == null) {
			return null;
		} else if (t.left != null) {
			Entry<K, V> p = t.left;
			while (p.right != null) {
				p = p.right;
			}
			return p;
		} else {
			Entry<K, V> p = t.parent;
			Entry<K, V> ch = t;
			while (p != null && ch == p.left) {
				ch = p;
				p = p.parent;
			}
			return p;
		}
	}

	/**
	 * Balancing operations.
	 * <p>
	 * Implementations of rebalancings during insertion and deletion are slightly different than the
	 * CLR version. Rather than using dummy nilnodes, we use a set of accessors that deal properly
	 * with null. They are used to avoid messiness surrounding nullness checks in the main
	 * algorithms.
	 */

	protected static <K, V> boolean colorOf(final Entry<K, V> p) {
		return p == null ? BLACK : p.color;
	}

	protected static <K, V> Entry<K, V> parentOf(final Entry<K, V> p) {
		return p == null ? null : p.parent;
	}

	protected static <K, V> void setColor(final Entry<K, V> p, final boolean c) {
		if (p != null) {
			p.color = c;
		}
	}

	protected static <K, V> Entry<K, V> leftOf(final Entry<K, V> p) {
		return p == null ? null : p.left;
	}

	protected static <K, V> Entry<K, V> rightOf(final Entry<K, V> p) {
		return p == null ? null : p.right;
	}

	/**
	 * From CLR
	 */
	protected void rotateLeft(final Entry<K, V> p) {
		if (p != null) {
			final Entry<K, V> r = p.right;
			p.right = r.left;
			if (r.left != null) {
				r.left.parent = p;
			}
			r.parent = p.parent;
			if (p.parent == null) {
				root = r;
			} else if (p.parent.left == p) {
				p.parent.left = r;
			} else {
				p.parent.right = r;
			}
			r.left = p;
			p.parent = r;
		}
	}

	/**
	 * From CLR
	 */
	protected void rotateRight(final Entry<K, V> p) {
		if (p != null) {
			final Entry<K, V> l = p.left;
			p.left = l.right;
			if (l.right != null) {
				l.right.parent = p;
			}
			l.parent = p.parent;
			if (p.parent == null) {
				root = l;
			} else if (p.parent.right == p) {
				p.parent.right = l;
			} else {
				p.parent.left = l;
			}
			l.right = p;
			p.parent = l;
		}
	}

	/**
	 * From CLR
	 */
	protected void fixAfterInsertion(Entry<K, V> x) {
		x.color = RED;

		while (x != null && x != root && x.parent.color == RED) {
			if (parentOf(x) == leftOf(parentOf(parentOf(x)))) {
				final Entry<K, V> y = rightOf(parentOf(parentOf(x)));
				if (colorOf(y) == RED) {
					setColor(parentOf(x), BLACK);
					setColor(y, BLACK);
					setColor(parentOf(parentOf(x)), RED);
					x = parentOf(parentOf(x));
				} else {
					if (x == rightOf(parentOf(x))) {
						x = parentOf(x);
						rotateLeft(x);
					}
					setColor(parentOf(x), BLACK);
					setColor(parentOf(parentOf(x)), RED);
					rotateRight(parentOf(parentOf(x)));
				}
			} else {
				final Entry<K, V> y = leftOf(parentOf(parentOf(x)));
				if (colorOf(y) == RED) {
					setColor(parentOf(x), BLACK);
					setColor(y, BLACK);
					setColor(parentOf(parentOf(x)), RED);
					x = parentOf(parentOf(x));
				} else {
					if (x == leftOf(parentOf(x))) {
						x = parentOf(x);
						rotateRight(x);
					}
					setColor(parentOf(x), BLACK);
					setColor(parentOf(parentOf(x)), RED);
					rotateLeft(parentOf(parentOf(x)));
				}
			}
		}
		root.color = BLACK;
	}

	/**
	 * Delete node p, and then rebalance the tree.
	 */
	protected void deleteEntry(Entry<K, V> p) {
		modCount++;
		size--;

		// If strictly internal, copy successor's element to p and then make p
		// point to successor.
		if (p.left != null && p.right != null) {
			final Entry<K, V> s = successor(p);
			p.key = s.key;
			p.value = s.value;
			p = s;
		} // p has 2 children

		// Start fixup at replacement node, if it exists.
		final Entry<K, V> replacement = p.left != null ? p.left : p.right;

		if (replacement != null) {
			// Link replacement to parent
			replacement.parent = p.parent;
			if (p.parent == null) {
				root = replacement;
			} else if (p == p.parent.left) {
				p.parent.left = replacement;
			} else {
				p.parent.right = replacement;
			}

			// Null out links so they are OK to use by fixAfterDeletion.
			p.left = p.right = p.parent = null;

			// Fix replacement
			if (p.color == BLACK) {
				fixAfterDeletion(replacement);
			}
		} else if (p.parent == null) { // return if we are the only node.
			root = null;
		} else { //  No children. Use self as phantom replacement and unlink.
			if (p.color == BLACK) {
				fixAfterDeletion(p);
			}

			if (p.parent != null) {
				if (p == p.parent.left) {
					p.parent.left = null;
				} else if (p == p.parent.right) {
					p.parent.right = null;
				}
				p.parent = null;
			}
		}
	}

	/**
	 * From CLR
	 */
	protected void fixAfterDeletion(Entry<K, V> x) {
		while (x != root && colorOf(x) == BLACK) {
			if (x == leftOf(parentOf(x))) {
				Entry<K, V> sib = rightOf(parentOf(x));

				if (colorOf(sib) == RED) {
					setColor(sib, BLACK);
					setColor(parentOf(x), RED);
					rotateLeft(parentOf(x));
					sib = rightOf(parentOf(x));
				}

				if (colorOf(leftOf(sib)) == BLACK && colorOf(rightOf(sib)) == BLACK) {
					setColor(sib, RED);
					x = parentOf(x);
				} else {
					if (colorOf(rightOf(sib)) == BLACK) {
						setColor(leftOf(sib), BLACK);
						setColor(sib, RED);
						rotateRight(sib);
						sib = rightOf(parentOf(x));
					}
					setColor(sib, colorOf(parentOf(x)));
					setColor(parentOf(x), BLACK);
					setColor(rightOf(sib), BLACK);
					rotateLeft(parentOf(x));
					x = root;
				}
			} else { // symmetric
				Entry<K, V> sib = leftOf(parentOf(x));

				if (colorOf(sib) == RED) {
					setColor(sib, BLACK);
					setColor(parentOf(x), RED);
					rotateRight(parentOf(x));
					sib = leftOf(parentOf(x));
				}

				if (colorOf(rightOf(sib)) == BLACK && colorOf(leftOf(sib)) == BLACK) {
					setColor(sib, RED);
					x = parentOf(x);
				} else {
					if (colorOf(leftOf(sib)) == BLACK) {
						setColor(rightOf(sib), BLACK);
						setColor(sib, RED);
						rotateLeft(sib);
						sib = leftOf(parentOf(x));
					}
					setColor(sib, colorOf(parentOf(x)));
					setColor(parentOf(x), BLACK);
					setColor(leftOf(sib), BLACK);
					rotateRight(parentOf(x));
					x = root;
				}
			}
		}

		setColor(x, BLACK);
	}

	private static final long serialVersionUID = 919286545866124006L;

	/**
	 * Save the state of the {@code OriginalTree} instance to a stream (i.e., serialize it).
	 *
	 * @serialData The <em>size</em> of the OriginalTree (the number of key-value mappings) is
	 * emitted (int), followed by the key (Object) and value (Object) for each key-value mapping
	 * represented by the OriginalTree. The key-value mappings are emitted in key-order (as
	 * determined by the OriginalTree's Comparator, or by the keys' natural ordering if the
	 * OriginalTree has no Comparator).
	 */
	protected void writeObject(final java.io.ObjectOutputStream s)
			throws java.io.IOException {
		// Write out the Comparator and any hidden stuff
		s.defaultWriteObject();

		// Write out size (number of Mappings)
		s.writeInt(size);

		// Write out keys and values (alternating)
		for (final java.util.Map.Entry<K, V> e : entrySet()) {
			s.writeObject(e.getKey());
			s.writeObject(e.getValue());
		}
	}

	/**
	 * Reconstitute the {@code OriginalTree} instance from a stream (i.e., deserialize it).
	 */
	protected void readObject(final java.io.ObjectInputStream s)
			throws java.io.IOException, ClassNotFoundException {
		// Read in the Comparator and any hidden stuff
		s.defaultReadObject();

		// Read in size
		final int size = s.readInt();

		buildFromSorted(size, null, s, null);
	}

	/**
	 * Intended to be called only from TreeSet.readObject
	 */
	void readTreeSet(final int size, final java.io.ObjectInputStream s, final V defaultVal)
			throws java.io.IOException, ClassNotFoundException {
		buildFromSorted(size, null, s, defaultVal);
	}

	/**
	 * Intended to be called only from TreeSet.addAll
	 */
	void addAllForTreeSet(final SortedSet<? extends K> set, final V defaultVal) {
		try {
			buildFromSorted(set.size(), set.iterator(), null, defaultVal);
		} catch (final java.io.IOException cannotHappen) {
		} catch (final ClassNotFoundException cannotHappen) {
		}
	}


	/**
	 * Linear time tree building algorithm from sorted data. Can accept keys and/or values from
	 * iterator or stream. This leads to too many parameters, but seems better than alternatives.
	 * The four formats that this method accepts are:
	 * <p>
	 * 1) An iterator of Map.Entries. (it != null, defaultVal == null). 2) An iterator of keys. (it
	 * != null, defaultVal != null). 3) A stream of alternating serialized keys and values. (it ==
	 * null, defaultVal == null). 4) A stream of serialized keys. (it == null, defaultVal != null).
	 * <p>
	 * It is assumed that the comparator of the OriginalTree is already set prior to calling this
	 * method.
	 *
	 * @param size       the number of keys (or key-value pairs) to be read from the iterator or
	 *                   stream
	 * @param it         If non-null, new entries are created from entries or keys read from this
	 *                   iterator.
	 * @param str        If non-null, new entries are created from keys and possibly values read
	 *                   from this stream in serialized form. Exactly one of it and str should be
	 *                   non-null.
	 * @param defaultVal if non-null, this default value is used for each value in the map. If null,
	 *                   each value is read from iterator or stream, as described above.
	 *
	 * @throws java.io.IOException    propagated from stream reads. This cannot occur if str is
	 *                                null.
	 * @throws ClassNotFoundException propagated from readObject. This cannot occur if str is null.
	 */
	protected void buildFromSorted(final int size, final Iterator<?> it,
			final java.io.ObjectInputStream str, final V defaultVal)
			throws java.io.IOException, ClassNotFoundException {
		this.size = size;
		root = buildFromSorted(0, 0, size - 1, computeRedLevel(size), it, str, defaultVal);
	}

	/**
	 * Recursive "helper method" that does the real work of the previous method. Identically named
	 * parameters have identical definitions. Additional parameters are documented below. It is
	 * assumed that the comparator and size fields of the OriginalTree are already set prior to
	 * calling this method. (It ignores both fields.)
	 *
	 * @param level    the current level of tree. Initial call should be 0.
	 * @param lo       the first element index of this subtree. Initial should be 0.
	 * @param hi       the last element index of this subtree. Initial should be size-1.
	 * @param redLevel the level at which nodes should be red. Must be equal to computeRedLevel for
	 *                 tree of this size.
	 */
	@SuppressWarnings("unchecked")
	protected final Entry<K, V> buildFromSorted(final int level, final int lo, final int hi,
			final int redLevel, final Iterator<?> it, final java.io.ObjectInputStream str,
			final V defaultVal)
			throws java.io.IOException, ClassNotFoundException {
		/*
		 * Strategy: The root is the middlemost element. To get to it, we have to first recursively
		 * construct the entire left subtree, so as to grab all of its elements. We can then proceed
		 * with right subtree.
		 *
		 * The lo and hi arguments are the minimum and maximum indices to pull out of the iterator
		 * or stream for current subtree. They are not actually indexed, we just proceed
		 * sequentially, ensuring that items are extracted in corresponding order.
		 */

		if (hi < lo) {
			return null;
		}

		final int mid = lo + hi >>> 1;

		Entry<K, V> left = null;
		if (lo < mid) {
			left = buildFromSorted(level + 1, lo, mid - 1, redLevel, it, str, defaultVal);
		}

		// extract key and/or value from iterator or stream
		K key;
		V value;
		if (it != null) {
			if (defaultVal == null) {
				final Map.Entry<?, ?> entry = (Map.Entry<?, ?>) it.next();
				key = (K) entry.getKey();
				value = (V) entry.getValue();
			} else {
				key = (K) it.next();
				value = defaultVal;
			}
		} else { // use stream
			key = (K) str.readObject();
			value = defaultVal != null ? defaultVal : (V) str.readObject();
		}

		final Entry<K, V> middle = new Entry<K, V>(key, value, null);

		// color nodes in non-full bottommost level red
		if (level == redLevel) {
			middle.color = RED;
		}

		if (left != null) {
			middle.left = left;
			left.parent = middle;
		}

		if (mid < hi) {
			final Entry<K, V> right = buildFromSorted(level + 1, mid + 1, hi, redLevel, it, str,
					defaultVal);
			middle.right = right;
			right.parent = middle;
		}

		return middle;
	}

	/**
	 * Find the level down to which to assign all nodes BLACK. This is the last `full' level of the
	 * complete binary tree produced by buildTree. The remaining nodes are colored RED. (This makes
	 * a `nice' set of color assignments wrt future insertions.) This level number is computed by
	 * finding the number of splits needed to reach the zeroeth node. (The answer is ~lg(N), but in
	 * any case must be computed by same quick O(lg(N)) loop.)
	 */
	protected static int computeRedLevel(final int sz) {
		int level = 0;
		for (int m = sz - 1; m >= 0; m = m / 2 - 1) {
			++level;
		}
		return level;
	}

	/**
	 * Currently, we support Spliterator-based versions only for the full map, in either plain of
	 * descending form, otherwise relying on defaults because size estimation for submaps would
	 * dominate costs. The type tests needed to check these for key views are not very nice but
	 * avoid disrupting existing class structures. Callers must use plain default spliterators if
	 * this returns null.
	 */
	static <K> Spliterator<K> keySpliteratorFor(final NavigableMap<K, ?> m) {
		if (m instanceof OriginalTree) {
			@SuppressWarnings("unchecked")
			final OriginalTree<K, Object> t = (OriginalTree<K, Object>) m;
			return t.keySpliterator();
		}
		if (m instanceof DescendingSubMap) {
			@SuppressWarnings("unchecked")
			final DescendingSubMap<K, ?> dm = (DescendingSubMap<K, ?>) m;
			final OriginalTree<K, ?> tm = dm.m;
			if (dm == tm.descendingMap) {
				@SuppressWarnings("unchecked")
				final OriginalTree<K, Object> t = (OriginalTree<K, Object>) tm;
				return t.descendingKeySpliterator();
			}
		}
		@SuppressWarnings("unchecked")
		final NavigableSubMap<K, ?> sm = (NavigableSubMap<K, ?>) m;
		return sm.keySpliterator();
	}

	final Spliterator<K> keySpliterator() {
		return new KeySpliterator<K, V>(this, null, null, 0, -1, 0);
	}

	final Spliterator<K> descendingKeySpliterator() {
		return new DescendingKeySpliterator<K, V>(this, null, null, 0, -2, 0);
	}

	/**
	 * Base class for spliterators. Iteration starts at a given origin and continues up to but not
	 * including a given fence (or null for end). At top-level, for ascending cases, the first split
	 * uses the root as left-fence/right-origin. From there, right-hand splits replace the current
	 * fence with its left child, also serving as origin for the split-off spliterator. Left-hands
	 * are symmetric. Descending versions place the origin at the end and invert ascending split
	 * rules. This base class is non-commital about directionality, or whether the top-level
	 * spliterator covers the whole tree. This means that the actual split mechanics are located in
	 * subclasses. Some of the subclass trySplit methods are identical (except for return types),
	 * but not nicely factorable.
	 * <p>
	 * Currently, subclass versions exist only for the full map (including descending keys via its
	 * descendingMap). Others are possible but currently not worthwhile because submaps require O(n)
	 * computations to determine size, which substantially limits potential speed-ups of using
	 * custom Spliterators versus default mechanics.
	 * <p>
	 * To boostrap initialization, external constructors use negative size estimates: -1 for ascend,
	 * -2 for descend.
	 */
	static class OriginalTreeSpliterator<K, V> {
		final OriginalTree<K, V> tree;
		OriginalTree.Entry<K, V> current; // traverser; initially first node in range
		OriginalTree.Entry<K, V> fence; // one past last, or null
		int side; // 0: top, -1: is a left split, +1: right
		int est; // size estimate (exact only for top-level)
		int expectedModCount; // for CME checks

		OriginalTreeSpliterator(final OriginalTree<K, V> tree,
				final OriginalTree.Entry<K, V> origin, final OriginalTree.Entry<K, V> fence,
				final int side, final int est, final int expectedModCount) {
			this.tree = tree;
			this.current = origin;
			this.fence = fence;
			this.side = side;
			this.est = est;
			this.expectedModCount = expectedModCount;
		}

		final int getEstimate() { // force initialization
			int s;
			OriginalTree<K, V> t;
			if ((s = est) < 0) {
				if ((t = tree) != null) {
					current = s == -1 ? t.getFirstEntry() : t.getLastEntry();
					s = est = t.size;
					expectedModCount = t.modCount;
				} else {
					s = est = 0;
				}
			}
			return s;
		}

		public final long estimateSize() {
			return getEstimate();
		}
	}

	static final class KeySpliterator<K, V>
			extends OriginalTreeSpliterator<K, V>
			implements Spliterator<K> {
		KeySpliterator(final OriginalTree<K, V> tree, final OriginalTree.Entry<K, V> origin,
				final OriginalTree.Entry<K, V> fence, final int side, final int est,
				final int expectedModCount) {
			super(tree, origin, fence, side, est, expectedModCount);
		}

		public KeySpliterator<K, V> trySplit() {
			if (est < 0) {
				getEstimate(); // force initialization
			}
			final int d = side;
			final OriginalTree.Entry<K, V> e = current;
			final OriginalTree.Entry<K, V> f = fence, s = e == null || e == f ? null : // empty
					d == 0 ? tree.root : // was top
							d > 0 ? e.right : // was right
									d < 0 && f != null ? f.left : // was left
											null;
			if (s != null && s != e && s != f && tree.compare(e.key, s.key) < 0) { // e not already past s
				side = 1;
				return new KeySpliterator<K, V>(tree, e, current = s, -1, est >>>= 1,
						expectedModCount);
			}
			return null;
		}

		public void forEachRemaining(final Consumer<? super K> action) {
			if (action == null) {
				throw new NullPointerException();
			}
			if (est < 0) {
				getEstimate(); // force initialization
			}
			final OriginalTree.Entry<K, V> f = fence;
			OriginalTree.Entry<K, V> e, p, pl;
			if ((e = current) != null && e != f) {
				current = f; // exhaust
				do {
					action.accept(e.key);
					if ((p = e.right) != null) {
						while ((pl = p.left) != null) {
							p = pl;
						}
					} else {
						while ((p = e.parent) != null && e == p.right) {
							e = p;
						}
					}
				} while ((e = p) != null && e != f);
				if (tree.modCount != expectedModCount) {
					throw new ConcurrentModificationException();
				}
			}
		}

		public boolean tryAdvance(final Consumer<? super K> action) {
			OriginalTree.Entry<K, V> e;
			if (action == null) {
				throw new NullPointerException();
			}
			if (est < 0) {
				getEstimate(); // force initialization
			}
			if ((e = current) == null || e == fence) {
				return false;
			}
			current = successor(e);
			action.accept(e.key);
			if (tree.modCount != expectedModCount) {
				throw new ConcurrentModificationException();
			}
			return true;
		}

		public int characteristics() {
			return (side == 0 ? Spliterator.SIZED : 0) | Spliterator.DISTINCT | Spliterator.SORTED |
					Spliterator.ORDERED;
		}

		public final Comparator<? super K> getComparator() {
			return tree.comparator;
		}

	}

	static final class DescendingKeySpliterator<K, V>
			extends OriginalTreeSpliterator<K, V>
			implements Spliterator<K> {
		DescendingKeySpliterator(final OriginalTree<K, V> tree,
				final OriginalTree.Entry<K, V> origin, final OriginalTree.Entry<K, V> fence,
				final int side, final int est, final int expectedModCount) {
			super(tree, origin, fence, side, est, expectedModCount);
		}

		public DescendingKeySpliterator<K, V> trySplit() {
			if (est < 0) {
				getEstimate(); // force initialization
			}
			final int d = side;
			final OriginalTree.Entry<K, V> e = current;
			final OriginalTree.Entry<K, V> f = fence, s = e == null || e == f ? null : // empty
					d == 0 ? tree.root : // was top
							d < 0 ? e.left : // was left
									d > 0 && f != null ? f.right : // was right
											null;
			if (s != null && s != e && s != f && tree.compare(e.key, s.key) > 0) { // e not already past s
				side = 1;
				return new DescendingKeySpliterator<K, V>(tree, e, current = s, -1, est >>>= 1,
						expectedModCount);
			}
			return null;
		}

		public void forEachRemaining(final Consumer<? super K> action) {
			if (action == null) {
				throw new NullPointerException();
			}
			if (est < 0) {
				getEstimate(); // force initialization
			}
			final OriginalTree.Entry<K, V> f = fence;
			OriginalTree.Entry<K, V> e, p, pr;
			if ((e = current) != null && e != f) {
				current = f; // exhaust
				do {
					action.accept(e.key);
					if ((p = e.left) != null) {
						while ((pr = p.right) != null) {
							p = pr;
						}
					} else {
						while ((p = e.parent) != null && e == p.left) {
							e = p;
						}
					}
				} while ((e = p) != null && e != f);
				if (tree.modCount != expectedModCount) {
					throw new ConcurrentModificationException();
				}
			}
		}

		public boolean tryAdvance(final Consumer<? super K> action) {
			OriginalTree.Entry<K, V> e;
			if (action == null) {
				throw new NullPointerException();
			}
			if (est < 0) {
				getEstimate(); // force initialization
			}
			if ((e = current) == null || e == fence) {
				return false;
			}
			current = predecessor(e);
			action.accept(e.key);
			if (tree.modCount != expectedModCount) {
				throw new ConcurrentModificationException();
			}
			return true;
		}

		public int characteristics() {
			return (side == 0 ? Spliterator.SIZED : 0) | Spliterator.DISTINCT | Spliterator.ORDERED;
		}
	}

	static final class ValueSpliterator<K, V>
			extends OriginalTreeSpliterator<K, V>
			implements Spliterator<V> {
		ValueSpliterator(final OriginalTree<K, V> tree, final OriginalTree.Entry<K, V> origin,
				final OriginalTree.Entry<K, V> fence, final int side, final int est,
				final int expectedModCount) {
			super(tree, origin, fence, side, est, expectedModCount);
		}

		public ValueSpliterator<K, V> trySplit() {
			if (est < 0) {
				getEstimate(); // force initialization
			}
			final int d = side;
			final OriginalTree.Entry<K, V> e = current;
			final OriginalTree.Entry<K, V> f = fence, s = e == null || e == f ? null : // empty
					d == 0 ? tree.root : // was top
							d > 0 ? e.right : // was right
									d < 0 && f != null ? f.left : // was left
											null;
			if (s != null && s != e && s != f && tree.compare(e.key, s.key) < 0) { // e not already past s
				side = 1;
				return new ValueSpliterator<K, V>(tree, e, current = s, -1, est >>>= 1,
						expectedModCount);
			}
			return null;
		}

		public void forEachRemaining(final Consumer<? super V> action) {
			if (action == null) {
				throw new NullPointerException();
			}
			if (est < 0) {
				getEstimate(); // force initialization
			}
			final OriginalTree.Entry<K, V> f = fence;
			OriginalTree.Entry<K, V> e, p, pl;
			if ((e = current) != null && e != f) {
				current = f; // exhaust
				do {
					action.accept(e.value);
					if ((p = e.right) != null) {
						while ((pl = p.left) != null) {
							p = pl;
						}
					} else {
						while ((p = e.parent) != null && e == p.right) {
							e = p;
						}
					}
				} while ((e = p) != null && e != f);
				if (tree.modCount != expectedModCount) {
					throw new ConcurrentModificationException();
				}
			}
		}

		public boolean tryAdvance(final Consumer<? super V> action) {
			OriginalTree.Entry<K, V> e;
			if (action == null) {
				throw new NullPointerException();
			}
			if (est < 0) {
				getEstimate(); // force initialization
			}
			if ((e = current) == null || e == fence) {
				return false;
			}
			current = successor(e);
			action.accept(e.value);
			if (tree.modCount != expectedModCount) {
				throw new ConcurrentModificationException();
			}
			return true;
		}

		public int characteristics() {
			return (side == 0 ? Spliterator.SIZED : 0) | Spliterator.ORDERED;
		}
	}

	static final class EntrySpliterator<K, V>
			extends OriginalTreeSpliterator<K, V>
			implements Spliterator<Map.Entry<K, V>> {
		EntrySpliterator(final OriginalTree<K, V> tree, final OriginalTree.Entry<K, V> origin,
				final OriginalTree.Entry<K, V> fence, final int side, final int est,
				final int expectedModCount) {
			super(tree, origin, fence, side, est, expectedModCount);
		}

		public EntrySpliterator<K, V> trySplit() {
			if (est < 0) {
				getEstimate(); // force initialization
			}
			final int d = side;
			final OriginalTree.Entry<K, V> e = current;
			final OriginalTree.Entry<K, V> f = fence, s = e == null || e == f ? null : // empty
					d == 0 ? tree.root : // was top
							d > 0 ? e.right : // was right
									d < 0 && f != null ? f.left : // was left
											null;
			if (s != null && s != e && s != f && tree.compare(e.key, s.key) < 0) { // e not already past s
				side = 1;
				return new EntrySpliterator<K, V>(tree, e, current = s, -1, est >>>= 1,
						expectedModCount);
			}
			return null;
		}

		public void forEachRemaining(final Consumer<? super Map.Entry<K, V>> action) {
			if (action == null) {
				throw new NullPointerException();
			}
			if (est < 0) {
				getEstimate(); // force initialization
			}
			final OriginalTree.Entry<K, V> f = fence;
			OriginalTree.Entry<K, V> e, p, pl;
			if ((e = current) != null && e != f) {
				current = f; // exhaust
				do {
					action.accept(e);
					if ((p = e.right) != null) {
						while ((pl = p.left) != null) {
							p = pl;
						}
					} else {
						while ((p = e.parent) != null && e == p.right) {
							e = p;
						}
					}
				} while ((e = p) != null && e != f);
				if (tree.modCount != expectedModCount) {
					throw new ConcurrentModificationException();
				}
			}
		}

		public boolean tryAdvance(final Consumer<? super Map.Entry<K, V>> action) {
			OriginalTree.Entry<K, V> e;
			if (action == null) {
				throw new NullPointerException();
			}
			if (est < 0) {
				getEstimate(); // force initialization
			}
			if ((e = current) == null || e == fence) {
				return false;
			}
			current = successor(e);
			action.accept(e);
			if (tree.modCount != expectedModCount) {
				throw new ConcurrentModificationException();
			}
			return true;
		}

		public int characteristics() {
			return (side == 0 ? Spliterator.SIZED : 0) | Spliterator.DISTINCT | Spliterator.SORTED |
					Spliterator.ORDERED;
		}

		//		@Override
		//		public Comparator<Map.Entry<K, V>> getComparator() {
		//			// Adapt or create a key-based comparator
		//			if (tree.comparator != null) {
		//				return Map.Entry.comparingByKey(tree.comparator);
		//			} else {
		//				return (Comparator<Map.Entry<K, V>> & Serializable) (e1, e2) -> {
		//					@SuppressWarnings("unchecked")
		//					Comparable<? super K> k1 = (Comparable<? super K>) e1.getKey();
		//					return k1.compareTo(e2.getKey());
		//				};
		//			}
		//		}
	}
}
