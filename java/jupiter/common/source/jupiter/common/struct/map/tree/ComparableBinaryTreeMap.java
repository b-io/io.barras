/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2020 Florian Barras <https://barras.io> (florian@barras.io)
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
package jupiter.common.struct.map.tree;

import static jupiter.common.io.IO.IO;
import static jupiter.common.util.Formats.NEW_LINE;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import jupiter.common.math.Comparables;
import jupiter.common.math.Maths;
import jupiter.common.model.ICloneable;
import jupiter.common.struct.list.ExtendedLinkedList;
import jupiter.common.struct.list.ExtendedList;
import jupiter.common.struct.tuple.Pair;
import jupiter.common.test.Arguments;
import jupiter.common.util.Objects;
import jupiter.common.util.Strings;

/**
 * {@link ComparableBinaryTreeMap} is a light sorted {@link Map} implementation based on a binary
 * tree.
 * <p>
 * @param <K> the self {@link Comparable} key type of the {@link ComparableBinaryTreeMap}
 * @param <V> the value type of the {@link ComparableBinaryTreeMap}
 * @param <N> the {@link ComparableBinaryTreeNode} type of the {@link ComparableBinaryTreeMap}
 */
public abstract class ComparableBinaryTreeMap<K extends Comparable<K>, V, N extends ComparableBinaryTreeNode<K, V, N>>
		extends ComparableTreeMap<K, V, N> {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 1L;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link ComparableBinaryTreeMap} of {@code K}, {@code V} and {@code N} types.
	 */
	protected ComparableBinaryTreeMap() {
		super();
	}

	/**
	 * Constructs a {@link ComparableBinaryTreeMap} of {@code K}, {@code V} and {@code N} types
	 * loaded from the specified {@link Map} containing the key-value mappings.
	 * <p>
	 * @param map the {@link Map} containing the key-value mappings of {@code K} and {@code V}
	 *            subtypes to load
	 */
	protected ComparableBinaryTreeMap(final Map<? extends K, ? extends V> map) {
		super(map);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the height.
	 * <p>
	 * @return the height
	 */
	public abstract int getHeight();

	/**
	 * Returns the maximum height.
	 * <p>
	 * @return the maximum height
	 */
	public abstract int getMaxHeight();

	/**
	 * Returns the optimal height.
	 * <p>
	 * @return the optimal height
	 */
	public int getOptimalHeight() {
		return Maths.floorToInt(Math.log(size()) / Maths.LOG_2) + 1;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Performs the in-order traversal and returns the {@code K} keys of the visited {@code N} nodes
	 * in an {@link ExtendedList}.
	 * <p>
	 * @return the {@code K} keys of the visited {@code N} nodes in an {@link ExtendedList}
	 */
	public ExtendedList<K> getKeys() {
		final ExtendedList<K> keys = new ExtendedList<K>(size);
		getKeys(root, keys);
		return keys;
	}

	/**
	 * Performs the in-order traversal of the specified {@code N} tree and stores the {@code K} keys
	 * of the visited {@code N} nodes in the specified {@link List}.
	 * <p>
	 * @param tree the {@code N} tree to get the {@code K} keys from (may be {@code null})
	 * @param keys the {@link List} of {@code K} type to store the {@code K} keys in
	 */
	protected void getKeys(final N tree, final List<K> keys) {
		if (tree != null) {
			getKeys(tree.left, keys);
			keys.add(tree.key);
			getKeys(tree.right, keys);
		}
	}

	//////////////////////////////////////////////

	/**
	 * Performs the in-order traversal and returns the values of the visited nodes in an
	 * {@link ExtendedList}.
	 * <p>
	 * @return the values of the visited nodes in an {@link ExtendedList}
	 */
	public ExtendedList<V> getValues() {
		final ExtendedList<V> values = new ExtendedList<V>(size);
		getValues(root, values);
		return values;
	}

	/**
	 * Performs the in-order traversal of the specified {@code N} tree and returns the values of the
	 * visited nodes in the specified {@link List}.
	 * <p>
	 * @param tree   the {@code N} tree to get the values from (may be {@code null})
	 * @param values the {@link List} of {@code V} type to store the values in
	 */
	protected void getValues(final N tree, final List<V> values) {
		if (tree != null) {
			getValues(tree.left, values);
			values.add(tree.value);
			getValues(tree.right, values);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the first node (according to the key-sort function), or {@code null} if {@code this}
	 * is empty.
	 * <p>
	 * @return the first node (according to the key-sort function), or {@code null} if {@code this}
	 *         is empty
	 */
	protected N getFirstEntry() {
		N node = root;
		if (node != null) {
			while (node.left != null) {
				node = node.left;
			}
		}
		return node;
	}

	/**
	 * Returns the last node (according to the key-sort function), or {@code null} if {@code this}
	 * is empty.
	 * <p>
	 * @return the last node (according to the key-sort function), or {@code null} if {@code this}
	 *         is empty
	 */
	protected N getLastEntry() {
		N node = root;
		if (node != null) {
			while (node.right != null) {
				node = node.right;
			}
		}
		return node;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the {@code N} parent of the specified {@code N} node, or {@code null} if it is
	 * {@code null}.
	 * <p>
	 * @param node the {@code N} node to get the parent from
	 * <p>
	 * @return the {@code N} parent of the specified {@code N} node, or {@code null} if it is
	 *         {@code null}
	 */
	protected N getParent(final N node) {
		return node != null ? node.parent : null;
	}

	/**
	 * Returns the {@code N} predecessor of the specified {@code N} node.
	 * <p>
	 * @param node a {@code N} node
	 * <p>
	 * @return the {@code N} predecessor of the specified {@code N} node
	 */
	protected N getPredecessor(final N node) {
		N predecessor;
		if (node.left != null) {
			predecessor = node.left;
			while (predecessor.right != null) {
				predecessor = predecessor.right;
			}
		} else {
			N successor = node;
			predecessor = successor.parent;
			while (predecessor != null && predecessor.isLeft) {
				successor = predecessor;
				predecessor = successor.parent;
			}
		}
		return predecessor;
	}

	/**
	 * Returns the {@code N} successor of the specified {@code N} node.
	 * <p>
	 * @param node a {@code N} node
	 * <p>
	 * @return the {@code N} successor of the specified {@code N} node
	 */
	protected N getSuccessor(final N node) {
		N successor;
		if (node.right != null) {
			successor = node.right;
			while (successor.left != null) {
				successor = successor.left;
			}
		} else {
			N predecessor = node;
			successor = predecessor.parent;
			while (successor != null && !predecessor.isLeft) {
				predecessor = successor;
				successor = predecessor.parent;
			}
		}
		return successor;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the {@code N} node associated to the specified key {@link Comparable}, or
	 * {@code null} if it is not present.
	 * <p>
	 * @param keyComparable the key {@link Comparable} of {@code K} supertype to find
	 * <p>
	 * @return the {@code N} node associated to the specified key {@link Comparable}, or
	 *         {@code null} if it is not present
	 * <p>
	 * @throws ClassCastException   if {@code key} cannot be compared with the current keys
	 * @throws NullPointerException if {@code keyComparable} is {@code null}
	 */
	@Override
	protected N findNode(final Comparable<? super K> keyComparable) {
		// Check the arguments
		Arguments.requireNonNull(keyComparable, "key comparable");

		// Get the node
		N node = root;
		while (node != null) {
			final int comparison = Comparables.compare(keyComparable, node.key);
			if (comparison < 0) {
				node = node.left;
			} else if (comparison > 0) {
				node = node.right;
			} else {
				return node;
			}
		}
		return null;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// SETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the root.
	 * <p>
	 * @param node a {@code N} node
	 */
	protected abstract void setRoot(final N node);


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Removes the key-value mapping of the specified key {@link Object} and returns the previous
	 * associated {@code V} value, or {@code null} if it is not present.
	 * <p>
	 * @param key the key {@link Object} of the key-value mapping to remove
	 * <p>
	 * @return the previous associated {@code V} value, or {@code null} if it is not present
	 * <p>
	 * @throws ClassCastException   if {@code key} cannot be compared with the current keys
	 * @throws NullPointerException if {@code key} is {@code null}
	 */
	@Override
	public synchronized V remove(final Object key) {
		// Check the arguments
		Arguments.requireNonNull(key, "key");

		// Remove the key-value mapping
		final N node = getNode(key);
		if (node != null) {
			removeNode(node);
			return node.value;
		}
		return null;
	}

	/**
	 * Removes the specified {@code N} node.
	 * <p>
	 * @param node the {@code N} node to remove
	 */
	protected abstract void removeNode(final N node);

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Balances after inserting the specified {@code N} node.
	 * <p>
	 * @param node the inserted {@code N} node
	 */
	protected abstract void balanceAfterInsertion(N node);

	/**
	 * Balances after deleting the specified {@code N} node.
	 * <p>
	 * @param node the deleted {@code N} node
	 */
	protected abstract void balanceAfterDeletion(N node);

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Rotates the specified {@code N} tree to the left. Corrects a RR imbalance.
	 * <p>
	 * @param tree the {@code N} tree to rotate
	 * <p>
	 * @return the rotated {@code N} tree
	 */
	protected N rotateLeft(final N tree) {
		// Set the root of the rotated tree
		final N rotatedTree = tree.right;
		// Update the parent
		final N parent = tree.parent;
		if (parent == null) {
			setRoot(rotatedTree);
		} else if (tree.isLeft) {
			parent.setLeft(rotatedTree);
		} else {
			parent.setRight(rotatedTree);
		}
		// Update the children of the rotated tree
		tree.setRight(rotatedTree.left);
		rotatedTree.setLeft(tree);
		return rotatedTree;
	}

	/**
	 * Rotates the specified {@code N} tree to the right. Corrects a LL imbalance.
	 * <p>
	 * @param tree the {@code N} tree to rotate
	 * <p>
	 * @return the rotated {@code N} tree
	 */
	protected N rotateRight(final N tree) {
		// Set the root of the rotated tree
		final N rotatedTree = tree.left;
		// Update the parent
		final N treeParent = tree.parent;
		if (treeParent == null) {
			setRoot(rotatedTree);
		} else if (tree.isLeft) {
			treeParent.setLeft(rotatedTree);
		} else {
			treeParent.setRight(rotatedTree);
		}
		// Update the children of the rotated tree
		tree.setLeft(rotatedTree.right);
		rotatedTree.setRight(tree);
		return rotatedTree;
	}

	/**
	 * Rotates the left part of the specified {@code N} tree to the left and rotates it to the
	 * right. Corrects a LR imbalance.
	 * <p>
	 * @param tree the {@code N} tree to rotate
	 * <p>
	 * @return the rotated tree
	 */
	protected N rotateLeftRight(final N tree) {
		tree.setLeft(rotateLeft(tree.left));
		return rotateRight(tree);
	}

	/**
	 * Rotates the right part of the specified {@code N} tree to the right and rotates it to the
	 * left. Corrects a RL imbalance.
	 * <p>
	 * @param tree the {@code N} tree to rotate
	 * <p>
	 * @return the rotated tree
	 */
	protected N rotateRightLeft(final N tree) {
		tree.setRight(rotateRight(tree.right));
		return rotateLeft(tree);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PRINTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prints {@code this}.
	 */
	@Override
	public void print() {
		print(root);
	}

	/**
	 * Prints the specified {@code N} tree.
	 * <p>
	 * @param tree the {@code N} tree to print
	 */
	protected void print(final N tree) {
		IO.result(tree);
		if (tree.left != null) {
			print(tree.left);
		}
		if (tree.right != null) {
			print(tree.right);
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ABSTRACT MAP
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Removes all the key-value mappings.
	 */
	@Override
	public synchronized void clear() {
		root = null;
		size = 0;
	}

	/**
	 * Tests whether a key-value mapping for the specified key {@link Object} exists.
	 * <p>
	 * @param key the key {@link Object} of the key-value mapping to test for presence
	 * <p>
	 * @return {@code true} if a mapping for the specified key {@link Object} exists, {@code false}
	 *         otherwise
	 * <p>
	 * @throws ClassCastException   if {@code key} cannot be compared with the current keys
	 * @throws NullPointerException if {@code key} is {@code null}
	 */
	@Override
	public boolean containsKey(final Object key) {
		// Check the arguments
		Arguments.requireNonNull(key, "key");

		// Test the presence of the key
		return getNode(key) != null;
	}

	/**
	 * Tests whether a mapping for the specified value {@link Object} exists.
	 * <p>
	 * @param value the value {@link Object} of the key-value mapping to test for presence (may be
	 *              {@code null})
	 * <p>
	 * @return {@code true} if a mapping for the specified value {@link Object} exists,
	 *         {@code false} otherwise
	 */
	@Override
	public boolean containsValue(final Object value) {
		// Test the presence of the value
		for (N node = getFirstEntry(); node != null; node = getSuccessor(node)) {
			if (Objects.equals(value, node.value)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Performs the in-order traversal of {@code this} and returns a {@link Set} view of the
	 * key-value {@link Entry} of {@code K} and {@code V} types of the visited nodes. The iterator
	 * of the {@link Set} returns the entries in ascending key order. The {@link Set} is backed by
	 * {@code this}, so changes to {@code this} are reflected in the {@link Set} and vice-versa. If
	 * {@code this} is modified while an iteration over the {@link Set} is in progress (except
	 * through the operations {@code remove} or {@code setValue} of the iterator), the results of
	 * the iteration are undefined. The set supports element removal, which removes the
	 * corresponding key-value mapping from {@code this}, via the {@link Iterator#remove},
	 * {@link Set#remove}, {@code removeAll}, {@code retainAll} and {@code clear} operations. It
	 * does not support the {@code add} or {@code addAll} operations.
	 * <p>
	 * @return a {@link Set} view of the key-value {@link Entry} of {@code K} and {@code V} types
	 */
	@Override
	public Set<Entry<K, V>> entrySet() {
		return entrySet(root, new LinkedHashSet<Entry<K, V>>());
	}

	/**
	 * Performs the in-order traversal of the specified {@code N} tree and returns a {@link Set}
	 * view of the key-value {@link Entry} of {@code K} and {@code V} types of the visited nodes
	 * added to the specified {@link Set}.
	 * <p>
	 * @param tree a {@code N} tree (may be {@code null})
	 * @param set  a {@link Set} of {@link Entry} of {@code K} and {@code V} types
	 * <p>
	 * @return a {@link Set} view of the key-value {@link Entry} of {@code K} and {@code V} types of
	 *         the visited nodes added to the specified {@link Set}
	 */
	protected Set<Entry<K, V>> entrySet(final N tree, final Set<Entry<K, V>> set) {
		if (tree != null) {
			entrySet(tree.left, set);
			set.add(tree);
			entrySet(tree.right, set);
		}
		return set;
	}

	/**
	 * Returns the number of key-value mappings.
	 * <p>
	 * @return the number of key-value mappings
	 */
	@Override
	public int size() {
		return size;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a copy of {@code this}.
	 * <p>
	 * @return a copy of {@code this}
	 *
	 * @see ICloneable
	 */
	@Override
	public abstract ComparableBinaryTreeMap<K, V, N> clone();

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of {@code this}.
	 * <p>
	 * @return a representative {@link String} of {@code this}
	 */
	@Override
	public String toString() {
		return toString(getHeight(), 10, false);
	}

	/**
	 * Returns a representative {@link String} of the specified {@code N} node.
	 * <p>
	 * @param toHeight   the height index to finish representing at (exclusive)
	 * @param nodeLength the length of the representative {@link String} of each node
	 * @param center     the flag specifying whether to center-pad
	 * <p>
	 * @return a representative {@link String} of the specified {@code N} node
	 */
	@SuppressWarnings("unchecked")
	public String toString(final int toHeight, final int nodeLength, final boolean center) {
		// Initialize
		final int leafCount = Maths.pow2(toHeight - 1);
		final int length = leafCount * (nodeLength + 1) + 1;
		final StringBuilder builder = Strings.createBuilder(toHeight * (length + 1));
		final ExtendedLinkedList<Pair<Integer, N>> nodes = new ExtendedLinkedList<Pair<Integer, N>>(
				new Pair<Integer, N>(0, root));

		// Convert this to a representative string
		int currentHeight = 0, nextHeight = 1, nodeCount = 1;
		boolean hasLeaf = false;
		while (!nodes.isEmpty()) {
			final Pair<Integer, N> element = nodes.remove();
			final int height = element.getFirst();
			final N node = element.getSecond();
			if (currentHeight < height) {
				builder.append(NEW_LINE);
				if (!hasLeaf) {
					break;
				}
				++currentHeight;
				++nextHeight;
				nodeCount = Maths.pow2(currentHeight);
				hasLeaf = false;
			}
			final String nodeString = Strings.toString(node, nodeLength);
			builder.append(center ? Strings.centerPad(nodeString, length / nodeCount) :
					Strings.rightPad(nodeString, length / nodeCount));
			if (nextHeight < toHeight) {
				hasLeaf = hasLeaf || node != null && (node.left != null || node.right != null);
				nodes.add(new Pair<Integer, N>(nextHeight, node != null ? node.left : null));
				nodes.add(new Pair<Integer, N>(nextHeight, node != null ? node.right : null));
			}
		}
		return builder.toString();
	}
}
