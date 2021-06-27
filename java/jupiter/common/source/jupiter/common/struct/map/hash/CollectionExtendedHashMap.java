/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2021 Florian Barras <https://barras.io> (florian@barras.io)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package jupiter.common.struct.map.hash;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import jupiter.common.model.ICloneable;
import jupiter.common.struct.list.ExtendedLinkedList;
import jupiter.common.test.Arguments;
import jupiter.common.test.ArrayArguments;
import jupiter.common.util.Collections;
import jupiter.common.util.Integers;
import jupiter.common.util.Maps;
import jupiter.common.util.Objects;

/**
 * {@link CollectionExtendedHashMap} is the {@link ExtendedHashMap} of {@code K} type and value type
 * {@link Collection} of {@code E} element type.
 * <p>
 * @param <K> the key type of the {@link ExtendedHashMap}
 * @param <E> the element type of the {@link Collection} which is the value type of the
 *            {@link ExtendedHashMap}
 */
public class CollectionExtendedHashMap<K, E>
		extends ExtendedHashMap<K, Collection<E>> {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 1L;

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The default model {@link Collection} of {@code E} element type.
	 */
	public final Collection<E> DEFAULT_MODEL = new ExtendedLinkedList<E>();


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The model {@link Collection} of {@code E} element type.
	 */
	protected final Collection<E> model;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs an empty {@link CollectionExtendedHashMap} of {@code K} type and value type
	 * {@link Collection} of {@code E} element type by default.
	 */
	public CollectionExtendedHashMap() {
		super(Maps.DEFAULT_CAPACITY);
		this.model = DEFAULT_MODEL;
	}

	/**
	 * Constructs an empty {@link CollectionExtendedHashMap} of {@code K} type and value type
	 * {@link Collection} of {@code E} element type by default.
	 * <p>
	 * @param model the model {@link Collection} of {@code E} element type
	 */
	public CollectionExtendedHashMap(final Collection<E> model) {
		super(Maps.DEFAULT_CAPACITY);
		this.model = model;
	}

	/**
	 * Constructs an empty {@link ExtendedHashMap} of {@code K} type and value type
	 * {@link Collection} of {@code E} element type with the specified initial capacity.
	 * <p>
	 * @param initialCapacity the initial capacity
	 * <p>
	 * @throws IllegalArgumentException if {@code initialCapacity} is negative
	 */
	public CollectionExtendedHashMap(final int initialCapacity) {
		super(initialCapacity);
		this.model = DEFAULT_MODEL;
	}

	/**
	 * Constructs an empty {@link ExtendedHashMap} of {@code K} type and value type
	 * {@link Collection} of {@code E} element type with the specified initial capacity.
	 * <p>
	 * @param model           the model {@link Collection} of {@code E} element type
	 * @param initialCapacity the initial capacity
	 * <p>
	 * @throws IllegalArgumentException if {@code initialCapacity} is negative
	 */
	public CollectionExtendedHashMap(final Collection<E> model, final int initialCapacity) {
		super(initialCapacity);
		this.model = model;
	}

	//////////////////////////////////////////////

	/**
	 * Constructs an {@link ExtendedHashMap} of {@code K} type and value type {@link Collection} of
	 * {@code E} element type loaded from the specified key and value arrays containing the
	 * key-value mappings.
	 * <p>
	 * @param keys   the {@code K} array containing the keys of the key-value mappings to load
	 * @param values the {@code E} array containing the values of the key-value mappings to load
	 * <p>
	 * @throws NullPointerException if any {@code keys} is {@code null}
	 */
	public CollectionExtendedHashMap(final K[] keys, final Collection<E>[] values) {
		super(keys.length);
		this.model = DEFAULT_MODEL;

		// Check the arguments
		ArrayArguments.requireSameLength(keys, values);

		// Put all the key-value mappings
		putAll(keys, values);
	}

	/**
	 * Constructs an {@link ExtendedHashMap} of {@code K} type and value type {@link Collection} of
	 * {@code E} element type loaded from the specified key and value arrays containing the
	 * key-value mappings.
	 * <p>
	 * @param model  the model {@link Collection} of {@code E} element type
	 * @param keys   the {@code K} array containing the keys of the key-value mappings to load
	 * @param values the {@code E} array containing the values of the key-value mappings to load
	 * <p>
	 * @throws NullPointerException if any {@code keys} is {@code null}
	 */
	public CollectionExtendedHashMap(final Collection<E> model, final K[] keys,
			final Collection<E>[] values) {
		super(keys.length);
		this.model = model;

		// Check the arguments
		ArrayArguments.requireSameLength(keys, values);

		// Put all the key-value mappings
		putAll(keys, values);
	}

	/**
	 * Constructs an {@link ExtendedHashMap} of {@code K} type and value type {@link Collection} of
	 * {@code E} element type loaded from the specified key array and value {@link Collection}
	 * containing the key-value mappings.
	 * <p>
	 * @param keys   the {@code K} array containing the keys of the key-value mappings to load
	 * @param values the {@link Collection} of {@code E} element type containing the values of the
	 *               key-value mappings to load
	 * <p>
	 * @throws NullPointerException if any {@code keys} is {@code null}
	 */
	public CollectionExtendedHashMap(final K[] keys,
			final Collection<? extends Collection<E>> values) {
		super(keys.length);
		this.model = DEFAULT_MODEL;

		// Check the arguments
		ArrayArguments.requireSameLength(keys, Arguments.requireNonNull(values, "values").size());

		// Put all the key-value mappings
		putAll(keys, values);
	}

	/**
	 * Constructs an {@link ExtendedHashMap} of {@code K} type and value type {@link Collection} of
	 * {@code E} element type loaded from the specified key array and value {@link Collection}
	 * containing the key-value mappings.
	 * <p>
	 * @param model  the model {@link Collection} of {@code E} element type
	 * @param keys   the {@code K} array containing the keys of the key-value mappings to load
	 * @param values the {@link Collection} of {@code E} element type containing the values of the
	 *               key-value mappings to load
	 * <p>
	 * @throws NullPointerException if any {@code keys} is {@code null}
	 */
	public CollectionExtendedHashMap(final Collection<E> model, final K[] keys,
			final Collection<? extends Collection<E>> values) {
		super(keys.length);
		this.model = model;

		// Check the arguments
		ArrayArguments.requireSameLength(keys, Arguments.requireNonNull(values, "values").size());

		// Put all the key-value mappings
		putAll(keys, values);
	}

	/**
	 * Constructs an {@link ExtendedHashMap} of {@code K} type and value type {@link Collection} of
	 * {@code E} element type loaded from the specified {@link Map} containing the key-value
	 * mappings.
	 * <p>
	 * @param map the {@link Map} containing the key-value mappings of {@code K} and {@code E} types
	 *            to load
	 */
	public CollectionExtendedHashMap(
			final Map<? extends K, ? extends Collection<E>> map) {
		super(map);
		this.model = DEFAULT_MODEL;
	}

	/**
	 * Constructs an {@link ExtendedHashMap} of {@code K} type and value type {@link Collection} of
	 * {@code E} element type loaded from the specified {@link Map} containing the key-value
	 * mappings.
	 * <p>
	 * @param model the model {@link Collection} of {@code E} element type
	 * @param map   the {@link Map} containing the key-value mappings of {@code K} and {@code E}
	 *              types to load
	 */
	public CollectionExtendedHashMap(final Collection<E> model,
			final Map<? extends K, ? extends Collection<E>> map) {
		super(map);
		this.model = model;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ACCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@link Collection} associated to the specified key, or creates and returns a
	 * {@link Collection} associated to it if it is not present.
	 * <p>
	 * @param key the {@code K} key of the {@link Collection} to get
	 * <p>
	 * @return the {@link Collection} associated to the specified key, or creates and returns a
	 *         {@link Collection} associated to it if it is not present
	 * <p>
	 * @throws NullPointerException if {@code key} is {@code null}
	 */
	public Collection<E> getOrCreate(final K key) {
		final Collection<E> collection;
		if (!containsKey(key)) {
			collection = Objects.clone(model);
			put(key, collection);
		} else {
			collection = super.get(key);
		}
		return collection;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PROCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Adds the specified element to the {@link Collection} at the specified key {@link Object}.
	 * <p>
	 * @param key     the key {@link Object} of the {@link Collection} to add to
	 * @param element the {@code T} element to add (may be {@code null})
	 * <p>
	 * @return {@code true} if the {@link Collection} at the specified key {@link Object} has
	 *         changed as a result of the call, {@code false} otherwise
	 * <p>
	 * @throws NullPointerException if {@code key} is {@code null}
	 */
	public boolean addAt(final K key, final E element) {
		return getOrCreate(key).add(element);
	}

	/**
	 * Adds all the specified elements to the {@link Collection} at the specified key
	 * {@link Object}.
	 * <p>
	 * @param <T>      the type of the elements to add
	 * @param key      the key {@link Object} of the {@link Collection} to add to
	 * @param elements the {@code T} elements to add (may be {@code null})
	 * <p>
	 * @return {@code true} if the {@link Collection} at the specified key {@link Object} has
	 *         changed as a result of the call, {@code false} otherwise
	 * <p>
	 * @throws NullPointerException if {@code key} is {@code null}
	 */
	public synchronized <T extends E> boolean addAllAt(final K key, final T[] elements) {
		return Collections.<T>addAll(getOrCreate(key), elements);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Removes a single instance of the specified {@link Object} from the {@link Collection} at the
	 * specified key {@link Object}.
	 * <p>
	 * @param key    the key {@link Object} of the {@link Collection} to remove from
	 * @param object the {@link Object} to remove (may be {@code null})
	 * <p>
	 * @return {@code true} if the {@link Collection} at the specified key {@link Object} has
	 *         changed as a result of the call, {@code false} otherwise
	 * <p>
	 * @throws NullPointerException if {@code key} is {@code null}
	 */
	public synchronized boolean removeAt(final Object key, final Object object) {
		if (containsKey(key)) {
			return get(key).remove(object);
		}
		return false;
	}

	/**
	 * Removes all the elements that are contained in the specified {@link Collection} from the
	 * {@link Collection} at the specified key {@link Object}.
	 * <p>
	 * @param key        the key {@link Object} of the {@link Collection} to remove from
	 * @param collection the {@link Collection} of {@link Object} to remove (may be {@code null})
	 * <p>
	 * @return {@code true} if the {@link Collection} at the specified key {@link Object} has
	 *         changed as a result of the call, {@code false} otherwise
	 * <p>
	 * @throws NullPointerException if {@code key} is {@code null}
	 */
	public synchronized boolean removeAllAt(final Object key, final Collection<?> collection) {
		if (containsKey(key)) {
			return get(key).removeAll(collection);
		}
		return false;
	}

	//////////////////////////////////////////////

	/**
	 * Removes the first occurrence of the specified {@link Object} from the {@link Collection} at
	 * the specified key {@link Object}.
	 * <p>
	 * @param key    the key {@link Object} of the {@link Collection} to remove from
	 * @param object the {@link Object} to remove (may be {@code null})
	 * <p>
	 * @return the index of the removed element, or {@code -1} if it is not present
	 */
	public synchronized int removeFirstAt(final Object key, final Object object) {
		if (containsKey(key)) {
			return Collections.removeFirst(get(key), object);
		}
		return -1;
	}

	/**
	 * Removes all the occurrences of the specified {@link Object} from the {@link Collection} at
	 * the specified key {@link Object}.
	 * <p>
	 * @param key    the key {@link Object} of the {@link Collection} to remove from
	 * @param object the {@link Object} to remove (may be {@code null})
	 * <p>
	 * @return the indices of the removed elements
	 * <p>
	 * @throws NullPointerException if {@code key} is {@code null}
	 */
	public synchronized int[] removeAllAt(final Object key, final Object object) {
		if (containsKey(key)) {
			return Collections.removeAll(get(key), object);
		}
		return Integers.EMPTY_PRIMITIVE_ARRAY;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Removes all the elements that are not contained in the specified {@link Collection} from the
	 * {@link Collection} at the specified key {@link Object}.
	 * <p>
	 * @param key        the key {@link Object} of the {@link Collection} to remove from
	 * @param collection the {@link Collection} of {@link Object} to retain (may be {@code null})
	 * <p>
	 * @return {@code true} if the {@link Collection} at the specified key {@link Object} has
	 *         changed as a result of the call, {@code false} otherwise
	 * <p>
	 * @throws NullPointerException if {@code key} is {@code null}
	 */
	public synchronized boolean retainAllAt(final Object key, final Collection<?> collection) {
		if (containsKey(key)) {
			return get(key).retainAll(collection);
		}
		return false;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Clones {@code this}.
	 * <p>
	 * @return a clone of {@code this}
	 *
	 * @see ICloneable
	 */
	@Override
	public CollectionExtendedHashMap<K, E> clone() {
		final CollectionExtendedHashMap<K, E> clone = new CollectionExtendedHashMap<K, E>(model,
				size());
		final Set<Entry<K, Collection<E>>> entries = entrySet();
		for (final Entry<K, Collection<E>> entry : entries) {
			clone.put(Objects.clone(entry.getKey()), Objects.clone(entry.getValue()));
		}
		return clone;
	}
}