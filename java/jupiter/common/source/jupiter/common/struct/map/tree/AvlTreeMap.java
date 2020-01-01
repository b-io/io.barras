/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2019 Florian Barras <https://barras.io> (florian@barras.io)
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

import java.util.Map;

import jupiter.common.struct.list.ExtendedList;
import jupiter.common.struct.map.tree.node.AvlTreeNode;
import jupiter.common.test.Arguments;

public class AvlTreeMap<K extends Comparable<K>, V>
		extends BinaryTreeMap<K, V, AvlTreeNode<K, V>> {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 1L;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The flag specifying whether to update the {@link AvlTreeNode}.
	 */
	protected boolean update;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs an {@link AvlTreeMap} of type {@code K} and {@code V}.
	 */
	public AvlTreeMap() {
		super();
		update = true;
	}

	/**
	 * Constructs an {@link AvlTreeMap} of type {@code K} and {@code V} loaded from the specified
	 * {@link Map} containing the key-value mappings.
	 * <p>
	 * @param map the {@link Map} containing the key-value {@code K} and {@code V} mappings to load
	 */
	public AvlTreeMap(final Map<? extends K, ? extends V> map) {
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
	public long getHeight() {
		return getHeight(root);
	}

	/**
	 * Returns the height of the specified {@link AvlTreeNode}, or {@code 0L} if it is {@code null}.
	 * <p>
	 * @param node an {@link AvlTreeNode} of type {@code K} and {@code V}
	 * <p>
	 * @return the height of the specified {@link AvlTreeNode}, or {@code 0L} if it is {@code null}
	 */
	protected long getHeight(final AvlTreeNode<K, V> node) {
		return node != null ? node.height : 0L;
	}

	public ExtendedList<Long> getBalances() {
		updateAll();
		return getBalances(root, new ExtendedList<Long>(size));
	}

	protected ExtendedList<Long> getBalances(final AvlTreeNode<K, V> node,
			final ExtendedList<Long> list) {
		if (node != null) {
			list.add(node.balance);
			getBalances(node.left, list);
			getBalances(node.right, list);
		}
		return list;
	}

	/**
	 * Returns the flag specifying whether to update the tree {@link AvlTreeNode}.
	 * <p>
	 * @return the flag specifying whether to update the tree {@link AvlTreeNode}
	 */
	public boolean isUpdate() {
		return update;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// SETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the root.
	 * <p>
	 * @param node an {@link AvlTreeNode} of type {@code K} and {@code V}
	 */
	@Override
	protected void setRoot(final AvlTreeNode<K, V> node) {
		root = node;
		if (root != null) {
			root.parent = null;
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Associates the specified {@code V} value to the specified {@code K} key and returns the
	 * previous associated {@code V} value, or {@code null} if it is not present.
	 * <p>
	 * @param key   the {@code K} key of the key-value mapping to put
	 * @param value the {@code V} value of the key-value mapping to put
	 * <p>
	 * @return the previous associated {@code V} value, or {@code null} if it is not present
	 * <p>
	 * @throws ClassCastException   if {@code key} cannot be compared with the current {@code K}
	 *                              keys
	 * @throws NullPointerException if {@code key} is {@code null}
	 */
	@Override
	public synchronized V put(final K key, final V value) {
		// Check the arguments
		Arguments.requireNonNull(key, "The specified key is null");

		// Put the key-value mapping
		AvlTreeNode<K, V> tree = root;
		if (tree == null) {
			// The root is set to a new node containing the specified key and value
			setRoot(new AvlTreeNode<K, V>(key, value, this));
		} else {
			int comparison;
			AvlTreeNode<K, V> parent;
			do {
				comparison = key.compareTo(tree.key);
				if (comparison < 0) {
					parent = tree;
					tree = tree.left;
				} else if (comparison > 0) {
					parent = tree;
					tree = tree.right;
				} else {
					// Replace and return the previous associated value
					return tree.setValue(value);
				}
			} while (tree != null);
			// Create a node containing the specified key and value
			final AvlTreeNode<K, V> newNode = new AvlTreeNode<K, V>(key, value, this);
			if (comparison < 0) {
				// The new node is the left node of the parent
				parent.setLeft(newNode);
			} else {
				// The new node is the right node of the parent
				parent.setRight(newNode);
			}
			// Balance this tree if required
			balanceAfterInsertion(newNode);
		}
		// Increment the number of nodes
		++size;
		// Return null since no previous associated value exists
		return null;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Removes the specified {@link AvlTreeNode}.
	 * <p>
	 * @param node an {@link AvlTreeNode} of type {@code K} and {@code V}
	 */
	@Override
	protected void removeNode(final AvlTreeNode<K, V> node) {
		// Get the parent and successor of the specified node
		final AvlTreeNode<K, V> parent = node.parent;
		// Test whether there is 0 or 1 child or there are 2 children
		if (node.left == null || node.right == null) {
			// - There is 0 or 1 child (so the tree is guaranteed to be balanced)
			// Get the child (if it exists)
			AvlTreeNode<K, V> child;
			if (node.left != null) {
				child = node.left;
			} else if (node.right != null) {
				child = node.right;
			} else {
				child = null;
			}
			// Update the parent
			if (parent == null) {
				setRoot(child);
			} else if (node.isLeft) {
				parent.setLeft(child);
			} else {
				parent.setRight(child);
			}
			// Decrement the number of nodes
			--size;
		} else {
			// - There are 2 children (so the tree is not guaranteed to be balanced)
			// Get the successor of the node to remove
			// @note the successor cannot be null since the node has a right node
			final AvlTreeNode<K, V> successor = getSuccessor(node);
			// Remove the successor
			removeNode(successor);
			// Override the node with the successor
			node.key = successor.key;
			node.value = successor.value;
			// Balance this tree from the node
			balanceAfterDeletion(node);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Balances after inserting the specified {@link AvlTreeNode}.
	 * <p>
	 * @param node the inserted {@link AvlTreeNode} of type {@code K} and {@code V}
	 */
	@Override
	protected void balanceAfterInsertion(AvlTreeNode<K, V> node) {
		balance(node);
	}

	/**
	 * Balances after deleting the specified {@link AvlTreeNode}.
	 * <p>
	 * @param node the deleted {@link AvlTreeNode} of type {@code K} and {@code V}
	 */
	@Override
	protected void balanceAfterDeletion(AvlTreeNode<K, V> node) {
		balance(node);
	}

	/**
	 * Balances the specified {@link AvlTreeNode}.
	 * <p>
	 * @param node the {@link AvlTreeNode} of type {@code K} and {@code V} to balance
	 */
	protected void balance(AvlTreeNode<K, V> node) {
		AvlTreeNode<K, V> parent = node;
		do {
			node = parent;
			// Get the balance of the node
			final long balance = node.balance;
			// Test the imbalance
			if (balance == -2) {
				// Get the left node of the specified node
				// @note the left node cannot be null since the balance is negative
				final AvlTreeNode<K, V> leftNode = node.left;
				// Test whether the imbalance is LL or LR
				if (getHeight(leftNode.left) >= getHeight(leftNode.right)) {
					// Correct the LL imbalance
					node = rotateRight(node);
				} else {
					// Correct the LR imbalance
					node = rotateLeftRight(node);
				}
			} else if (balance == 2) {
				// Get the right node of the specified node
				// @note the right node cannot be null since the balance is positive
				final AvlTreeNode<K, V> rightNode = node.right;
				// Test whether the imbalance is RR or RL
				if (getHeight(rightNode.right) >= getHeight(rightNode.left)) {
					// Correct the RR imbalance
					node = rotateLeft(node);
				} else {
					// Correct the RL imbalance
					node = rotateRightLeft(node);
				}
			}
			parent = node.parent;
		} while (parent != null);
		// The node has no parent, set the root
		setRoot(node);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Rotates the specified tree {@link AvlTreeNode} to the left. Corrects a RR imbalance.
	 * <p>
	 * @param tree the tree {@link AvlTreeNode} of type {@code K} and {@code V} to rotate
	 * <p>
	 * @return the rotated tree {@link AvlTreeNode} of type {@code K} and {@code V}
	 */
	@Override
	protected AvlTreeNode<K, V> rotateLeft(final AvlTreeNode<K, V> tree) {
		update = false;
		final AvlTreeNode<K, V> rotatedTreeRoot = super.rotateLeft(tree);
		update = true;
		tree.update();
		tree.updateAllParents();
		return rotatedTreeRoot;
	}

	/**
	 * Rotates the specified tree {@link AvlTreeNode} to the right. Corrects a LL imbalance.
	 * <p>
	 * @param tree the tree {@link AvlTreeNode} of type {@code K} and {@code V} to rotate
	 * <p>
	 * @return the rotated tree {@link AvlTreeNode} of type {@code K} and {@code V}
	 */
	@Override
	protected AvlTreeNode<K, V> rotateRight(final AvlTreeNode<K, V> tree) {
		update = false;
		final AvlTreeNode<K, V> rotatedTreeRoot = super.rotateRight(tree);
		update = true;
		tree.update();
		tree.updateAllParents();
		return rotatedTreeRoot;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public void updateAll() {
		updateAll(root);
	}

	protected void updateAll(final AvlTreeNode<K, V> node) {
		if (node != null) {
			node.updateAll();
			updateAll(node.left);
			updateAll(node.right);
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a copy of {@code this}.
	 * <p>
	 * @return a copy of {@code this}
	 *
	 * @see jupiter.common.model.ICloneable
	 */
	@Override
	public AvlTreeMap<K, V> clone() {
		return new AvlTreeMap<K, V>(this);
	}
}
