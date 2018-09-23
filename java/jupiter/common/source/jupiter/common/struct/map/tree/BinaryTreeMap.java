/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2018 Florian Barras <https://barras.io> (florian@barras.io)
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

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import jupiter.common.struct.list.ExtendedList;
import jupiter.common.struct.map.tree.node.BinaryTreeNode;
import jupiter.common.test.Arguments;
import jupiter.common.util.Objects;
import jupiter.common.util.Strings;

public abstract class BinaryTreeMap<K extends Comparable<K>, V, N extends BinaryTreeNode<K, V, N>>
		extends TreeMap<K, V, N> {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = -5922531484900310242L;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected BinaryTreeMap() {
		super();
	}

	protected BinaryTreeMap(final Map<? extends K, ? extends V> map) {
		super(map);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Performs the in-order traversal and returns the keys of the visited nodes in an
	 * {@link ExtendedList}.
	 * <p>
	 * @return the keys of the visited nodes in an {@link ExtendedList}
	 */
	public ExtendedList<K> getKeys() {
		final ExtendedList<K> keys = new ExtendedList<K>(size);
		getKeys(root, keys);
		return keys;
	}

	/**
	 * Performs the in-order traversal of the specified tree and returns the keys of the visited
	 * nodes in the specified {@link List}.
	 * <p>
	 * @param tree the tree to get the keys from
	 * @param keys the {@link List} to store the keys in
	 */
	protected void getKeys(final N tree, final List<K> keys) {
		if (tree != null) {
			getKeys(tree.left, keys);
			keys.add(tree.key);
			getKeys(tree.right, keys);
		}
	}

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
	 * Performs the in-order traversal of the specified tree and returns the values of the visited
	 * nodes in the specified {@link List}.
	 * <p>
	 * @param tree   the tree to get the values from
	 * @param values the {@link List} to store the values in
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
	 * Returns the parent of the specified node, or {@code null} if the specified node is
	 * {@code null}.
	 * <p>
	 * @param node the node to get the parent from
	 * <p>
	 * @return the parent of the specified node, or {@code null} if the specified node is
	 *         {@code null}
	 */
	protected N getParent(final N node) {
		return node == null ? null : node.parent;
	}

	/**
	 * Returns the node associated to the specified key, or {@code null} if not present.
	 * <p>
	 * @param key the key of the node to get
	 * <p>
	 * @return the node associated to the specified key, or {@code null} if not present
	 * <p>
	 * @throws NullPointerException if {@code key} is {@code null}
	 */
	@Override
	protected N getNode(final Comparable<? super K> key) {
		// Check the arguments
		Arguments.requireNonNull(key, "The specified key is null");

		// Get the node
		N node = root;
		int comparison;
		while (node != null) {
			comparison = key.compareTo(node.key);
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

	/**
	 * Returns the predecessor of the specified node.
	 * <p>
	 * @param node the successor of the node to get
	 * <p>
	 * @return the predecessor of the specified node
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
	 * Returns the successor of the specified node.
	 * <p>
	 * @param node the predecessor of the node to get
	 * <p>
	 * @return the successor of the specified node
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


	////////////////////////////////////////////////////////////////////////////////////////////////
	// SETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the root.
	 * <p>
	 * @param node a {@code N} object
	 */
	protected abstract void setRoot(final N node);


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Removes the key-value mapping of the specified key and returns the previous associated value,
	 * or {@code null} if not present.
	 * <p>
	 * @param key the key of the key-value mapping to remove
	 * <p>
	 * @return the previous associated value, or {@code null} if not present
	 * <p>
	 * @throws ClassCastException   if {@code key} cannot be compared with the current keys
	 * @throws NullPointerException if {@code key} is {@code null}
	 */
	public synchronized V remove(final Object key) {
		// Check the arguments
		Arguments.requireNonNull(key, "The specified key is null");

		// Remove the key-value mapping
		final N node = getNode(key);
		if (node != null) {
			removeNode(node);
			return node.value;
		}
		return null;
	}

	/**
	 * Removes the specified node.
	 * <p>
	 * @param node the node to remove
	 */
	protected abstract void removeNode(final N node);

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Balances the tree after insertion.
	 * <p>
	 * @param tree the tree to balance
	 */
	protected abstract void balanceAfterInsertion(N tree);

	/**
	 * Balances the tree after deletion.
	 * <p>
	 * @param tree the tree to balance
	 */
	protected abstract void balanceAfterDeletion(N tree);

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Rotates the specified tree to the left. Corrects a RR imbalance.
	 * <p>
	 * @param tree the tree to rotate
	 * <p>
	 * @return the rotated tree
	 */
	protected N rotateLeft(final N tree) {
		N rotatedTree;
		// Set the root of the rotated tree
		rotatedTree = tree.right;
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
	 * Rotates the specified tree to the right. Corrects a LL imbalance.
	 * <p>
	 * @param tree the tree to rotate
	 * <p>
	 * @return the rotated tree
	 */
	protected N rotateRight(final N tree) {
		N rotatedTree;
		// Set the root of the rotated tree
		rotatedTree = tree.left;
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
	 * Rotates the left part of the specified tree to the left and rotates the specified tree to the
	 * right. Corrects a LR imbalance.
	 * <p>
	 * @param tree the tree to rotate
	 * <p>
	 * @return the rotated tree
	 */
	protected N rotateLeftRight(final N tree) {
		tree.setLeft(rotateLeft(tree.left));
		return rotateRight(tree);
	}

	/**
	 * Rotates the right part of the specified tree to the right and rotates the specified tree to
	 * the left. Corrects a RL imbalance.
	 * <p>
	 * @param tree the tree to rotate
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
	 * Prints the specified tree.
	 * <p>
	 * @param tree the tree to print
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
	public synchronized void clear() {
		root = null;
		size = 0;
	}

	/**
	 * Tests whether a mapping for the specified key exists.
	 * <p>
	 * @param key the key of the key-value mapping to test for presence
	 * <p>
	 * @return {@code true} if a mapping for the specified key exists, {@code false} otherwise
	 * <p>
	 * @throws ClassCastException   if {@code key} cannot be compared with the current keys
	 * @throws NullPointerException if {@code key} is {@code null}
	 */
	public boolean containsKey(final Object key) {
		// Check the arguments
		Arguments.requireNonNull(key, "The specified key is null");

		// Test the presence of the key
		return getNode(key) != null;
	}

	/**
	 * Tests whether a mapping for the specified value exists.
	 * <p>
	 * @param value the value of the key-value mapping to test for presence
	 * <p>
	 * @return {@code true} if a mapping for the specified value exists, {@code false} otherwise
	 */
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
	 * Returns a set view of the key-value mappings. The iterator of the set returns the entries in
	 * ascending key order. The set is backed by {@code this}, so changes to {@code this} are
	 * reflected in the set and vice-versa. If {@code this} is modified while an iteration over the
	 * set is in progress (except through the operations {@code remove} or {@code setValue} of the
	 * iterator), the results of the iteration are undefined. The set supports element removal,
	 * which removes the corresponding mapping from {@code this}, via the
	 * {@link Iterator#remove}, {@link Set#remove}, {@code removeAll}, {@code retainAll} and
	 * {@code clear} operations. It does not support the {@code add} or {@code addAll} operations.
	 * <p>
	 * @return a {@link Set} view of the key-value mappings
	 */
	public Set<Entry<K, V>> entrySet() {
		return getEntries(root, new TreeSet<Entry<K, V>>());
	}

	/**
	 * Returns a set view of the key-value mappings. Performs the in-order traversal of the
	 * specified tree and returns the key-value mappings of the visited nodes in the specified set.
	 * <p>
	 * @param tree the tree to get the key-value mappings from
	 * @param set  the {@link Set} to fill with the key-value mappings
	 * <p>
	 * @return a {@link Set} view of the key-value mappings
	 */
	protected Set<Entry<K, V>> getEntries(final N tree, final Set<Entry<K, V>> set) {
		if (tree != null) {
			getEntries(tree.left, set);
			set.add(tree);
			getEntries(tree.right, set);
		}
		return set;
	}

	/**
	 * Returns the number of key-value mappings.
	 * <p>
	 * @return the number of key-value mappings
	 */
	public int size() {
		return size;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public abstract BinaryTreeMap<K, V, N> clone();

	@Override
	public String toString() {
		final StringBuilder lineBuilder = Strings.createBuilder();
		lineBuilder.append("<").append(toString(root)).append(">");
		return lineBuilder.toString();
	}

	public String toString(final N node) {
		final StringBuilder lineBuilder = Strings.createBuilder();
		lineBuilder.append("<").append(node.parent).append("|").append(node).append("|")
				.append(node.left).append("|").append(node.right).append(">");
		return lineBuilder.toString();
	}
}
