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
	private static final long serialVersionUID = -3501331197847125490L;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The option specifying whether to update the nodes.
	 */
	protected boolean update;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public AvlTreeMap() {
		super();
		update = true;
	}

	public AvlTreeMap(final Map<? extends K, ? extends V> map) {
		super(map);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS & SETTERS
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
	 * Returns the height of the specified node, or {@code 0L} if the specified node is
	 * {@code null}.
	 * <p>
	 * @param node the node to get the height from
	 * <p>
	 * @return the height of the specified node, or {@code 0L} if the specified node is {@code null}
	 */
	protected long getHeight(final AvlTreeNode<K, V> node) {
		return node == null ? 0L : node.height;
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
	 * Returns the option specifying whether to update the nodes.
	 * <p>
	 * @return the option specifying whether to update the nodes
	 */
	public boolean isUpdate() {
		return update;
	}

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
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Associates the specified value with the specified key and returns the previous associated
	 * value, or {@code null} if not present.
	 * <p>
	 * @param key   the key of the key-value mapping to put
	 * @param value the value of the key-value mapping to put
	 * <p>
	 * @return the previous associated value, or {@code null} if not present
	 * <p>
	 * @throws ClassCastException   if the specified key cannot be compared with the current keys
	 * @throws NullPointerException if the specified key is {@code null}
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
			// Create a new node containing the specified key and value
			final AvlTreeNode<K, V> newNode = new AvlTreeNode<K, V>(key, value, this);
			if (comparison < 0) {
				// The new node is the left node of the parent
				parent.setLeft(newNode);
			} else {
				// The new node is the right node of the parent
				parent.setRight(newNode);
			}
			// Balance this tree if necessary
			balanceAfterInsertion(newNode);
		}
		// Increment the number of nodes
		++size;
		// Return null since no previous associated value exists
		return null;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Removes the specified node.
	 * <p>
	 * @param node the node to remove
	 */
	@Override
	protected void removeNode(final AvlTreeNode<K, V> node) {
		// Get the parent and the successor of the specified node
		final AvlTreeNode<K, V> parent = node.parent;
		// Test whether there is 0 or 1 child or there are 2 children
		if (node.left == null || node.right == null) {
			// - There is 0 or 1 child (so the tree is guaranteed to be balanced)
			// Get the child if present
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
	 * Balances after inserting the specified node.
	 * <p>
	 * @param node the inserted node
	 */
	@Override
	protected void balanceAfterInsertion(final AvlTreeNode<K, V> node) {
		balance(node);
	}

	/**
	 * Balances after deleting the specified node.
	 * <p>
	 * @param node the deleted node
	 */
	@Override
	protected void balanceAfterDeletion(final AvlTreeNode<K, V> node) {
		balance(node);
	}

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
	 * Rotates the specified tree to the left. Corrects a RR imbalance.
	 * <p>
	 * @param tree the tree to rotate
	 * <p>
	 * @return the rotated tree
	 */
	@Override
	protected AvlTreeNode<K, V> rotateLeft(final AvlTreeNode<K, V> tree) {
		update = false;
		final AvlTreeNode<K, V> rotatedTreeRoot = super.rotateLeft(tree);
		update = true;
		tree.update();
		tree.updateParents();
		return rotatedTreeRoot;
	}

	/**
	 * Rotates the specified tree to the right. Corrects a LL imbalance.
	 * <p>
	 * @param tree the tree to rotate
	 * <p>
	 * @return the rotated tree
	 */
	@Override
	protected AvlTreeNode<K, V> rotateRight(final AvlTreeNode<K, V> tree) {
		update = false;
		final AvlTreeNode<K, V> rotatedTreeRoot = super.rotateRight(tree);
		update = true;
		tree.update();
		tree.updateParents();
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

	@Override
	public AvlTreeMap<K, V> clone() {
		return new AvlTreeMap<K, V>(this);
	}
}
