/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2022 Florian Barras <https://barras.io> (florian@barras.io)
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
import java.util.NoSuchElementException;

import jupiter.common.model.ICloneable;
import jupiter.common.struct.list.ExtendedLinkedList;
import jupiter.common.test.Arguments;
import jupiter.common.test.ArrayArguments;
import jupiter.common.test.CollectionArguments;
import jupiter.common.util.Collections;
import jupiter.common.util.Integers;
import jupiter.common.util.Maps;
import jupiter.common.util.Objects;

/**
 * {@link CollectionHashMap} is the {@link ExtendedHashMap} of {@code K} type and value type
 * {@link Collection} of {@code E} element type.
 * <p>
 * @param <K> the key type of the {@link CollectionHashMap}
 * @param <E> the element type of the {@link Collection} which is the value type of the
 *            {@link CollectionHashMap}
 */
public class CollectionHashMap<K, E>
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

	/**
	 * The default flag specifying whether to remove empty {@link Collection} of {@code E} element
	 * type.
	 */
	public final boolean DEFAULT_REMOVE_EMPTY = true;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The model {@link Collection} of {@code E} element type.
	 */
	protected final Collection<E> model;

	/**
	 * The flag specifying whether to remove empty {@link Collection} of {@code E} element type.
	 */
	protected final boolean removeEmpty;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs an empty {@link CollectionHashMap} of {@code K} type and value type
	 * {@link Collection} of {@code E} element type by default.
	 */
	public CollectionHashMap() {
		super(Maps.DEFAULT_CAPACITY);
		this.model = DEFAULT_MODEL;
		this.removeEmpty = DEFAULT_REMOVE_EMPTY;
	}

	/**
	 * Constructs an empty {@link CollectionHashMap} of {@code K} type and value type
	 * {@link Collection} of {@code E} element type by default.
	 * <p>
	 * @param model the model {@link Collection} of {@code E} element type
	 */
	public CollectionHashMap(final Collection<E> model) {
		super(Maps.DEFAULT_CAPACITY);
		this.model = model;
		this.removeEmpty = DEFAULT_REMOVE_EMPTY;
	}

	/**
	 * Constructs an empty {@link CollectionHashMap} of {@code K} type and value type
	 * {@link Collection} of {@code E} element type by default.
	 * <p>
	 * @param model       the model {@link Collection} of {@code E} element type
	 * @param removeEmpty the flag specifying whether to remove empty {@link Collection} of
	 *                    {@code E} element type
	 */
	public CollectionHashMap(final Collection<E> model, final boolean removeEmpty) {
		super(Maps.DEFAULT_CAPACITY);
		this.model = model;
		this.removeEmpty = removeEmpty;
	}

	/**
	 * Constructs an empty {@link CollectionHashMap} of {@code K} type and value type
	 * {@link Collection} of {@code E} element type with the specified initial capacity.
	 * <p>
	 * @param initialCapacity the initial capacity
	 * <p>
	 * @throws IllegalArgumentException if {@code initialCapacity} is negative
	 */
	public CollectionHashMap(final int initialCapacity) {
		super(initialCapacity);
		this.model = DEFAULT_MODEL;
		this.removeEmpty = DEFAULT_REMOVE_EMPTY;
	}

	/**
	 * Constructs an empty {@link CollectionHashMap} of {@code K} type and value type
	 * {@link Collection} of {@code E} element type with the specified initial capacity.
	 * <p>
	 * @param initialCapacity the initial capacity
	 * @param model           the model {@link Collection} of {@code E} element type
	 * <p>
	 * @throws IllegalArgumentException if {@code initialCapacity} is negative
	 */
	public CollectionHashMap(final int initialCapacity, final Collection<E> model) {
		super(initialCapacity);
		this.model = model;
		this.removeEmpty = DEFAULT_REMOVE_EMPTY;
	}

	/**
	 * Constructs an empty {@link CollectionHashMap} of {@code K} type and value type
	 * {@link Collection} of {@code E} element type with the specified initial capacity.
	 * <p>
	 * @param initialCapacity the initial capacity
	 * @param model           the model {@link Collection} of {@code E} element type
	 * @param removeEmpty     the flag specifying whether to remove empty {@link Collection} of
	 *                        {@code E} element type
	 * <p>
	 * @throws IllegalArgumentException if {@code initialCapacity} is negative
	 */
	public CollectionHashMap(final int initialCapacity, final Collection<E> model,
			final boolean removeEmpty) {
		super(initialCapacity);
		this.model = model;
		this.removeEmpty = removeEmpty;
	}

	//////////////////////////////////////////////

	/**
	 * Constructs a {@link CollectionHashMap} of {@code K} type and value type {@link Collection} of
	 * {@code E} element type loaded from the specified key and value arrays containing the
	 * key-value mappings.
	 * <p>
	 * @param keys   the {@code K} array containing the keys of the key-value mappings to load
	 * @param values the {@code E} array containing the values of the key-value mappings to load
	 * <p>
	 * @throws NullPointerException if any {@code keys} is {@code null}
	 */
	public CollectionHashMap(final K[] keys, final Collection<E>[] values) {
		super(Arguments.requireNonNull(keys, "keys").length);
		this.model = DEFAULT_MODEL;
		this.removeEmpty = DEFAULT_REMOVE_EMPTY;

		// Check the arguments
		ArrayArguments.requireSameLength(keys, "keys", values, "values");

		// Put all the key-value mappings
		putAll(keys, values);
	}

	/**
	 * Constructs a {@link CollectionHashMap} of {@code K} type and value type {@link Collection} of
	 * {@code E} element type loaded from the specified key and value arrays containing the
	 * key-value mappings.
	 * <p>
	 * @param keys   the {@code K} array containing the keys of the key-value mappings to load
	 * @param values the {@code E} array containing the values of the key-value mappings to load
	 * @param model  the model {@link Collection} of {@code E} element type
	 * <p>
	 * @throws NullPointerException if any {@code keys} is {@code null}
	 */
	public CollectionHashMap(final K[] keys, final Collection<E>[] values,
			final Collection<E> model) {
		super(Arguments.requireNonNull(keys, "keys").length);
		this.model = model;
		this.removeEmpty = DEFAULT_REMOVE_EMPTY;

		// Check the arguments
		ArrayArguments.requireSameLength(keys, "keys", values, "values");

		// Put all the key-value mappings
		putAll(keys, values);
	}

	/**
	 * Constructs a {@link CollectionHashMap} of {@code K} type and value type {@link Collection} of
	 * {@code E} element type loaded from the specified key and value arrays containing the
	 * key-value mappings.
	 * <p>
	 * @param keys        the {@code K} array containing the keys of the key-value mappings to load
	 * @param values      the {@code E} array containing the values of the key-value mappings to
	 *                    load
	 * @param model       the model {@link Collection} of {@code E} element type
	 * @param removeEmpty the flag specifying whether to remove empty {@link Collection} of
	 *                    {@code E} element type
	 * <p>
	 * @throws NullPointerException if any {@code keys} is {@code null}
	 */
	public CollectionHashMap(final K[] keys, final Collection<E>[] values,
			final Collection<E> model, final boolean removeEmpty) {
		super(Arguments.requireNonNull(keys, "keys").length);
		this.model = model;
		this.removeEmpty = removeEmpty;

		// Check the arguments
		ArrayArguments.requireSameLength(keys, "keys", values, "values");

		// Put all the key-value mappings
		putAll(keys, values);
	}

	/////////////////////

	/**
	 * Constructs a {@link CollectionHashMap} of {@code K} type and value type {@link Collection} of
	 * {@code E} element type loaded from the specified key array and value {@link Collection}
	 * containing the key-value mappings.
	 * <p>
	 * @param keys   the {@code K} array containing the keys of the key-value mappings to load
	 * @param values the {@link Collection} of {@link Collection} of {@code E} element type
	 *               containing the values of the key-value mappings to load
	 * <p>
	 * @throws NullPointerException if any {@code keys} is {@code null}
	 */
	public CollectionHashMap(final K[] keys,
			final Collection<? extends Collection<E>> values) {
		super(Arguments.requireNonNull(keys, "keys").length);
		this.model = DEFAULT_MODEL;
		this.removeEmpty = DEFAULT_REMOVE_EMPTY;

		// Check the arguments
		ArrayArguments.requireSameLength(keys, "keys", values, "values");

		// Put all the key-value mappings
		putAll(keys, values);
	}

	/**
	 * Constructs a {@link CollectionHashMap} of {@code K} type and value type {@link Collection} of
	 * {@code E} element type loaded from the specified key array and value {@link Collection}
	 * containing the key-value mappings.
	 * <p>
	 * @param keys   the {@code K} array containing the keys of the key-value mappings to load
	 * @param values the {@link Collection} of {@link Collection} of {@code E} element type
	 *               containing the values of the key-value mappings to load
	 * @param model  the model {@link Collection} of {@code E} element type
	 * <p>
	 * @throws NullPointerException if any {@code keys} is {@code null}
	 */
	public CollectionHashMap(final K[] keys,
			final Collection<? extends Collection<E>> values, final Collection<E> model) {
		super(Arguments.requireNonNull(keys, "keys").length);
		this.model = model;
		this.removeEmpty = DEFAULT_REMOVE_EMPTY;

		// Check the arguments
		ArrayArguments.requireSameLength(keys, "keys", values, "values");

		// Put all the key-value mappings
		putAll(keys, values);
	}

	/**
	 * Constructs a {@link CollectionHashMap} of {@code K} type and value type {@link Collection} of
	 * {@code E} element type loaded from the specified key array and value {@link Collection}
	 * containing the key-value mappings.
	 * <p>
	 * @param keys        the {@code K} array containing the keys of the key-value mappings to load
	 * @param values      the {@link Collection} of {@link Collection} of {@code E} element type
	 *                    containing the values of the key-value mappings to load
	 * @param model       the model {@link Collection} of {@code E} element type
	 * @param removeEmpty the flag specifying whether to remove empty {@link Collection} of
	 *                    {@code E} element type
	 * <p>
	 * @throws NullPointerException if any {@code keys} is {@code null}
	 */
	public CollectionHashMap(final K[] keys,
			final Collection<? extends Collection<E>> values, final Collection<E> model,
			final boolean removeEmpty) {
		super(Arguments.requireNonNull(keys, "keys").length);
		this.model = model;
		this.removeEmpty = removeEmpty;

		// Check the arguments
		ArrayArguments.requireSameLength(keys, "keys", values, "values");

		// Put all the key-value mappings
		putAll(keys, values);
	}

	/////////////////////

	/**
	 * Constructs a {@link CollectionHashMap} of {@code K} type and value type {@link Collection} of
	 * {@code E} element type loaded from the specified key and value {@link Collection} containing
	 * the key-value mappings.
	 * <p>
	 * @param keys   the {@link Collection} of {@code K} element subtype containing the keys of the
	 *               key-value mappings to load
	 * @param values the {@link Collection} of {@link Collection} of {@code E} element type
	 *               containing the values of the key-value mappings to load
	 * <p>
	 * @throws NullPointerException if any {@code keys} is {@code null}
	 */
	public CollectionHashMap(final Collection<? extends K> keys,
			final Collection<? extends Collection<E>> values) {
		super(Arguments.requireNonNull(keys, "keys").size());
		this.model = DEFAULT_MODEL;
		this.removeEmpty = DEFAULT_REMOVE_EMPTY;

		// Check the arguments
		CollectionArguments.requireSameSize(keys, "keys", values, "values");

		// Put all the key-value mappings
		putAll(keys, values);
	}

	/**
	 * Constructs a {@link CollectionHashMap} of {@code K} type and value type {@link Collection} of
	 * {@code E} element type loaded from the specified key and value {@link Collection} containing
	 * the key-value mappings.
	 * <p>
	 * @param keys   the {@link Collection} of {@code K} element subtype containing the keys of the
	 *               key-value mappings to load
	 * @param values the {@link Collection} of {@link Collection} of {@code E} element type
	 *               containing the values of the key-value mappings to load
	 * @param model  the model {@link Collection} of {@code E} element type
	 * <p>
	 * @throws NullPointerException if any {@code keys} is {@code null}
	 */
	public CollectionHashMap(final Collection<? extends K> keys,
			final Collection<? extends Collection<E>> values, final Collection<E> model) {
		super(Arguments.requireNonNull(keys, "keys").size());
		this.model = model;
		this.removeEmpty = DEFAULT_REMOVE_EMPTY;

		// Check the arguments
		CollectionArguments.requireSameSize(keys, "keys", values, "values");

		// Put all the key-value mappings
		putAll(keys, values);
	}

	/**
	 * Constructs a {@link CollectionHashMap} of {@code K} type and value type {@link Collection} of
	 * {@code E} element type loaded from the specified key and value {@link Collection} containing
	 * the key-value mappings.
	 * <p>
	 * @param keys        the {@link Collection} of {@code K} element subtype containing the keys of
	 *                    the key-value mappings to load
	 * @param values      the {@link Collection} of {@link Collection} of {@code E} element type
	 *                    containing the values of the key-value mappings to load
	 * @param model       the model {@link Collection} of {@code E} element type
	 * @param removeEmpty the flag specifying whether to remove empty {@link Collection} of
	 *                    {@code E} element type
	 * <p>
	 * @throws NullPointerException if any {@code keys} is {@code null}
	 */
	public CollectionHashMap(final Collection<? extends K> keys,
			final Collection<? extends Collection<E>> values, final Collection<E> model,
			final boolean removeEmpty) {
		super(Arguments.requireNonNull(keys, "keys").size());
		this.model = model;
		this.removeEmpty = removeEmpty;

		// Check the arguments
		CollectionArguments.requireSameSize(keys, "keys", values, "values");

		// Put all the key-value mappings
		putAll(keys, values);
	}

	//////////////////////////////////////////////

	/**
	 * Constructs a {@link CollectionHashMap} of {@code K} type and value type {@link Collection} of
	 * {@code E} element type loaded from the specified {@link Map} containing the key-value
	 * mappings.
	 * <p>
	 * @param map the {@link Map} containing the key-value mappings of {@code K} and {@code E} types
	 *            to load
	 */
	public CollectionHashMap(final Map<? extends K, ? extends Collection<E>> map) {
		super(Arguments.requireNonNull(map, "map").size());
		this.model = DEFAULT_MODEL;
		this.removeEmpty = DEFAULT_REMOVE_EMPTY;

		// Put all the key-value mappings
		putAll(map);
	}

	/**
	 * Constructs a {@link CollectionHashMap} of {@code K} type and value type {@link Collection} of
	 * {@code E} element type loaded from the specified {@link Map} containing the key-value
	 * mappings.
	 * <p>
	 * @param map   the {@link Map} containing the key-value mappings of {@code K} and {@code E}
	 *              types to load
	 * @param model the model {@link Collection} of {@code E} element type
	 */
	public CollectionHashMap(final Map<? extends K, ? extends Collection<E>> map,
			final Collection<E> model) {
		super(Arguments.requireNonNull(map, "map").size());
		this.model = model;
		this.removeEmpty = DEFAULT_REMOVE_EMPTY;

		// Put all the key-value mappings
		putAll(map);
	}

	/**
	 * Constructs a {@link CollectionHashMap} of {@code K} type and value type {@link Collection} of
	 * {@code E} element type loaded from the specified {@link Map} containing the key-value
	 * mappings.
	 * <p>
	 * @param map         the {@link Map} containing the key-value mappings of {@code K} and
	 *                    {@code E} types to load
	 * @param model       the model {@link Collection} of {@code E} element type
	 * @param removeEmpty the flag specifying whether to remove empty {@link Collection} of
	 *                    {@code E} element type
	 */
	public CollectionHashMap(final Map<? extends K, ? extends Collection<E>> map,
			final Collection<E> model, final boolean removeEmpty) {
		super(Arguments.requireNonNull(map, "map").size());
		this.model = model;
		this.removeEmpty = removeEmpty;

		// Put all the key-value mappings
		putAll(map);
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
	 * @throws ClassCastException   if {@code key} cannot be compared to {@code this} keys
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

	//////////////////////////////////////////////

	/**
	 * Returns the element at the specified index in the {@link Collection} at the specified key
	 * {@link Object}.
	 * <p>
	 * @param key   the key {@link Object} of the {@link Collection} to get
	 * @param index the index of the element to return
	 * <p>
	 * @return the element at the specified index in the {@link Collection} at the specified key
	 *         {@link Object}
	 * <p>
	 * @throws NoSuchElementException if the iteration has no more elements
	 */
	public E getAt(final Object key, final int index)
			throws NoSuchElementException {
		return Collections.get(get(key), index);
	}

	/**
	 * Returns the first occurrence of the specified {@link Object} in the {@link Collection} at the
	 * specified key {@link Object}, {@code null} otherwise.
	 * <p>
	 * @param key    the key {@link Object} of the {@link Collection} to get
	 * @param object the {@link Object} to get (may be {@code null})
	 * <p>
	 * @return the first occurrence of the specified {@link Object} in the {@link Collection} at the
	 *         specified key {@link Object}, {@code null} otherwise
	 */
	public E getFirstAt(final Object key, final Object object)
			throws NoSuchElementException {
		final Collection<E> collection = get(key);
		if (collection == null) {
			return null;
		}
		return Collections.getFirst(collection, object);

	}

	/**
	 * Returns all the occurrences of the specified {@link Object} in the {@link Collection} at the
	 * specified key {@link Object} in an {@link ExtendedLinkedList}.
	 * <p>
	 * @param key    the key {@link Object} of the {@link Collection} to get
	 * @param object the {@link Object} to get (may be {@code null})
	 * <p>
	 * @return all the occurrences of the specified {@link Object} in the {@link Collection} at the
	 *         specified key {@link Object} in an {@link ExtendedLinkedList}
	 */
	public ExtendedLinkedList<E> getAllAt(final Object key, final Object object)
			throws NoSuchElementException {
		final Collection<E> collection = get(key);
		if (collection == null) {
			return new ExtendedLinkedList<E>();
		}
		return Collections.getAll(collection, object);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PROCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Adds the specified element to the {@link Collection} at the specified key.
	 * <p>
	 * @param key     the {@code K} key of the {@link Collection} to add to
	 * @param element the {@code T} element to add (may be {@code null})
	 * <p>
	 * @return {@code true} if the {@link Collection} at the specified key has changed as a result
	 *         of the call, {@code false} otherwise
	 * <p>
	 * @throws ClassCastException   if {@code key} cannot be compared to {@code this} keys
	 * @throws NullPointerException if {@code key} is {@code null}
	 */
	public boolean addAt(final K key, final E element) {
		return getOrCreate(key).add(element);
	}

	/**
	 * Adds all the specified elements to the {@link Collection} at the specified key.
	 * <p>
	 * @param <T>      the type of the elements to add
	 * @param key      the {@code K} key of the {@link Collection} to add to
	 * @param elements the {@code T} elements to add (may be {@code null})
	 * <p>
	 * @return {@code true} if the {@link Collection} at the specified key has changed as a result
	 *         of the call, {@code false} otherwise
	 * <p>
	 * @throws ClassCastException   if {@code key} cannot be compared to {@code this} keys
	 * @throws NullPointerException if {@code key} is {@code null}
	 */
	public <T extends E> boolean addAllAt(final K key, final T[] elements) {
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
	public boolean removeAt(final Object key, final Object object) {
		if (containsKey(key)) {
			final Collection<E> value = get(key);
			final boolean hasChanged = value.remove(object);
			if (removeEmpty && value.isEmpty()) {
				remove(key);
			}
			return hasChanged;
		}
		return false;
	}

	/**
	 * Removes all the elements that are contained in the specified {@link Collection} from the
	 * {@link Collection} at the specified key {@link Object}.
	 * <p>
	 * @param key        the key {@link Object} of the {@link Collection} to remove from
	 * @param collection the {@link Collection} to remove (may be {@code null})
	 * <p>
	 * @return {@code true} if the {@link Collection} at the specified key {@link Object} has
	 *         changed as a result of the call, {@code false} otherwise
	 * <p>
	 * @throws NullPointerException if {@code key} is {@code null}
	 */
	public boolean removeAllAt(final Object key, final Collection<?> collection) {
		if (containsKey(key)) {
			final Collection<E> value = get(key);
			final boolean hasChanged = value.removeAll(collection);
			if (removeEmpty && value.isEmpty()) {
				remove(key);
			}
			return hasChanged;
		}
		return false;
	}

	//////////////////////////////////////////////

	/**
	 * Removes the element at the specified index from the {@link Collection} at the specified key
	 * {@link Object}.
	 * <p>
	 * @param key   the key {@link Object} of the {@link Collection} to remove from
	 * @param index the index of the element to remove
	 * <p>
	 * @return the index of the removed element, or {@code -1} if it is not present
	 * <p>
	 * @throws NoSuchElementException if the iteration has no more elements
	 */
	public int removeAt(final Object key, final int index)
			throws NoSuchElementException {
		if (containsKey(key)) {
			final Collection<E> value = get(key);
			Collections.remove(value, index);
			if (removeEmpty && value.isEmpty()) {
				remove(key);
			}
			return index;
		}
		return -1;
	}

	/**
	 * Removes the first occurrence of the specified {@link Object} from the {@link Collection} at
	 * the specified key {@link Object}.
	 * <p>
	 * @param key    the key {@link Object} of the {@link Collection} to remove from
	 * @param object the {@link Object} to remove (may be {@code null})
	 * <p>
	 * @return the index of the removed element, or {@code -1} if it is not present
	 */
	public int removeFirstAt(final Object key, final Object object) {
		if (containsKey(key)) {
			final Collection<E> value = get(key);
			final int index = Collections.removeFirst(value, object);
			if (removeEmpty && value.isEmpty()) {
				remove(key);
			}
			return index;
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
	public int[] removeAllAt(final Object key, final Object object) {
		if (containsKey(key)) {
			final Collection<E> value = get(key);
			final int[] indices = Collections.removeAll(value, object);
			if (removeEmpty && value.isEmpty()) {
				remove(key);
			}
			return indices;
		}
		return Integers.EMPTY_PRIMITIVE_ARRAY;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Removes all the elements that are not contained in the specified {@link Collection} from the
	 * {@link Collection} at the specified key {@link Object}.
	 * <p>
	 * @param key        the key {@link Object} of the {@link Collection} to remove from
	 * @param collection the {@link Collection} to retain (may be {@code null})
	 * <p>
	 * @return {@code true} if the {@link Collection} at the specified key {@link Object} has
	 *         changed as a result of the call, {@code false} otherwise
	 * <p>
	 * @throws NullPointerException if {@code key} is {@code null}
	 */
	public boolean retainAllAt(final Object key, final Collection<?> collection) {
		if (containsKey(key)) {
			final Collection<E> value = get(key);
			final boolean hasChanged = value.retainAll(collection);
			if (removeEmpty && value.isEmpty()) {
				remove(key);
			}
			return hasChanged;
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
	public CollectionHashMap<K, E> clone() {
		final CollectionHashMap<K, E> clone = new CollectionHashMap<K, E>(size(), model,
				removeEmpty);
		for (final Entry<K, Collection<E>> entry : entrySet()) {
			clone.put(Objects.clone(entry.getKey()), Objects.clone(entry.getValue()));
		}
		return clone;
	}
}
