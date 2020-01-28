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

import java.util.Comparator;
import java.util.Map;

import jupiter.common.struct.list.ExtendedLinkedList;
import jupiter.common.struct.tuple.Pair;
import jupiter.common.test.Arguments;

/**
 * {@link RedBlackTreeMap} is a light sorted {@link Map} implementation based on a red-black tree
 * with a {@link Comparator} to determine the order of the entries.
 * <p>
 * @param <K> the key type of the {@link RedBlackTreeMap}
 * @param <V> the value type of the {@link RedBlackTreeMap}
 */
public class RedBlackTreeMap<K, V>
		extends BinaryTreeMap<K, V, RedBlackTreeNode<K, V>> {

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
	 * Constructs a {@link RedBlackTreeMap} of types {@code K}, {@code V} and {@code N}.
	 * <p>
	 * @param c the key {@link Class} of type {@code K}
	 */
	public RedBlackTreeMap(final Class<K> c) {
		super(c);
	}

	/**
	 * Constructs a {@link RedBlackTreeMap} of types {@code K}, {@code V} and {@code N} loaded from
	 * the specified {@link Map} containing the key-value mappings.
	 * <p>
	 * @param c   the key {@link Class} of type {@code K}
	 * @param map the {@link Map} containing the {@code K} and {@code V} key-value mappings to load
	 */
	public RedBlackTreeMap(final Class<K> c, final Map<? extends K, ? extends V> map) {
		super(c, map);
	}

	//////////////////////////////////////////////

	/**
	 * Constructs a {@link RedBlackTreeMap} of types {@code K}, {@code V} and {@code N} with the
	 * specified key {@link Comparator}.
	 * <p>
	 * @param keyComparator the key {@link Comparator} of supertype {@code K} to determine the order
	 */
	public RedBlackTreeMap(final Comparator<? super K> keyComparator) {
		super(keyComparator);
	}

	/**
	 * Constructs a {@link RedBlackTreeMap} of types {@code K}, {@code V} and {@code N} with the
	 * specified key {@link Comparator} loaded from the specified {@link Map} containing the
	 * key-value mappings .
	 * <p>
	 * @param keyComparator the key {@link Comparator} of supertype {@code K} to determine the order
	 * @param map           the {@link Map} containing the {@code K} and {@code V} key-value
	 *                      mappings to load
	 */
	public RedBlackTreeMap(final Comparator<? super K> keyComparator,
			final Map<? extends K, ? extends V> map) {
		super(keyComparator, map);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the height.
	 * <p>
	 * @return the height
	 */
	@Override
	@SuppressWarnings("unchecked")
	public int getHeight() {
		// Initialize
		final ExtendedLinkedList<Pair<Integer, RedBlackTreeNode<K, V>>> nodes = new ExtendedLinkedList<Pair<Integer, RedBlackTreeNode<K, V>>>(
				new Pair<Integer, RedBlackTreeNode<K, V>>(0, root));

		// Compute the height
		int currentHeight = 0, nextHeight = 1;
		boolean hasLeaf = false;
		while (!nodes.isEmpty()) {
			final Pair<Integer, RedBlackTreeNode<K, V>> element = nodes.remove();
			final int height = element.getFirst();
			final RedBlackTreeNode<K, V> node = element.getSecond();
			if (currentHeight < height) {
				if (!hasLeaf) {
					break;
				}
				++currentHeight;
				++nextHeight;
				hasLeaf = false;
			}
			if (node != null) {
				hasLeaf = hasLeaf || node.left != null || node.right != null;
				nodes.add(new Pair<Integer, RedBlackTreeNode<K, V>>(nextHeight, node.left));
				nodes.add(new Pair<Integer, RedBlackTreeNode<K, V>>(nextHeight, node.right));
			}
		}
		return currentHeight + 1;
	}

	/**
	 * Returns the maximum height.
	 * <p>
	 * @return the maximum height
	 */
	@Override
	public int getMaxHeight() {
		return 2 * getOptimalHeight();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// SETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the root.
	 * <p>
	 * @param node a {@link RedBlackTreeNode} of types {@code K} and {@code V}
	 */
	@Override
	protected void setRoot(final RedBlackTreeNode<K, V> node) {
		root = node;
		if (root != null) {
			root.parent = null;
			root.isRed = false;
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
	 * @throws ClassCastException   if {@code key} cannot be compared with the current keys
	 * @throws NullPointerException if {@code key} is {@code null}
	 */
	@Override
	public synchronized V put(final K key, final V value) {
		// Check the arguments
		Arguments.requireNonNull(key, "key");

		// Put the key-value mapping
		RedBlackTreeNode<K, V> tree = root;
		if (tree == null) {
			// The root is set to a new node containing the key and value
			setRoot(new RedBlackTreeNode<K, V>(key, value, keyComparator));
		} else {
			int comparison;
			RedBlackTreeNode<K, V> parent;
			do {
				comparison = keyComparator.compare(key, tree.key);
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
			// Create the node containing the key and value
			final RedBlackTreeNode<K, V> newNode = new RedBlackTreeNode<K, V>(key, value,
					keyComparator);
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
	 * Removes the specified {@link RedBlackTreeNode}.
	 * <p>
	 * @param node the {@link RedBlackTreeNode} of types {@code K} and {@code V} to remove
	 */
	@Override
	protected void removeNode(final RedBlackTreeNode<K, V> node) {
		// Get the parent and successor of the node
		final RedBlackTreeNode<K, V> parent = node.parent;
		// Test whether there is 0 or 1 child or there are 2 children
		if (node.left == null || node.right == null) {
			// • There is 0 or 1 child (so the tree is guaranteed to be balanced)
			// Get the child (if it exists)
			RedBlackTreeNode<K, V> child;
			if (node.left != null) {
				child = node.left;
			} else if (node.right != null) {
				child = node.right;
			} else {
				child = null;
				// If the node is black
				if (!node.isRed) {
					// Balance this tree from the node
					balanceAfterDeletion(node);
				}
			}
			// Update the parent
			if (parent == null) {
				setRoot(child);
			} else if (node.isLeft) {
				parent.setLeft(child);
			} else {
				parent.setRight(child);
			}
			// If the node has at least one child and is black
			if (child != null && !node.isRed) {
				// Balance this tree from the child
				balanceAfterDeletion(child);
			}
			// Decrement the number of nodes
			--size;
		} else {
			// • There are 2 children (so the tree is not guaranteed to be balanced)
			// Get the successor of the node to remove
			// @note the successor cannot be null since the node has a right node
			final RedBlackTreeNode<K, V> successor = getSuccessor(node);
			// Remove the successor
			removeNode(successor);
			// Override the node with the successor
			node.key = successor.key;
			node.value = successor.value;
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Balances after inserting the specified {@link RedBlackTreeNode}.
	 * <p>
	 * @param node the inserted {@link RedBlackTreeNode} of types {@code K} and {@code V}
	 */
	@Override
	protected void balanceAfterInsertion(RedBlackTreeNode<K, V> node) {
		// Get the parent
		// @note the parent cannot be null since the node is inserted after the root
		RedBlackTreeNode<K, V> parent = node.parent;
		// Get the grand-parent
		RedBlackTreeNode<K, V> grandParent = parent.parent;
		RedBlackTreeNode<K, V> uncle;
		while (parent.isRed) {
			if (parent.isLeft) {
				// Get the uncle
				uncle = grandParent != null ? grandParent.right : null;
				if (uncle != null && uncle.isRed) {
					// Update the colors
					parent.isRed = false;
					uncle.isRed = false;
					grandParent.isRed = true;
					// Update the references
					node = grandParent;
					parent = node.parent;
					if (parent != null) {
						grandParent = parent.parent;
					} else {
						break;
					}
				} else {
					if (!node.isLeft) {
						// Update the references and rotate left
						node = parent;
						parent = rotateLeft(node);
						if (parent != null) {
							grandParent = parent.parent;
						} else {
							break;
						}
					}
					// Update the colors and rotate right
					parent.isRed = false;
					if (grandParent != null) {
						grandParent.isRed = true;
						rotateRight(grandParent);
					}
				}
			} else {
				// Get the uncle
				uncle = grandParent != null ? grandParent.left : null;
				if (uncle != null && uncle.isRed) {
					// Update the colors
					parent.isRed = false;
					uncle.isRed = false;
					grandParent.isRed = true;
					// Update the references
					node = grandParent;
					parent = node.parent;
					if (parent != null) {
						grandParent = parent.parent;
					} else {
						break;
					}
				} else {
					if (node.isLeft) {
						// Update the references and rotate right
						node = parent;
						parent = rotateRight(node);
						if (parent != null) {
							grandParent = parent.parent;
						} else {
							break;
						}
					}
					// Update the colors and rotate left
					parent.isRed = false;
					if (grandParent != null) {
						grandParent.isRed = true;
						rotateLeft(grandParent);
					}
				}
			}
		}
		root.isRed = false;
	}

	/**
	 * Balances after deleting the specified {@link RedBlackTreeNode}.
	 * <p>
	 * @param node the deleted {@link RedBlackTreeNode} of types {@code K} and {@code V}
	 */
	@Override
	protected void balanceAfterDeletion(RedBlackTreeNode<K, V> node) {
		RedBlackTreeNode<K, V> parent, brother, left, right;
		while (node != root && !node.isRed) {
			// Get the parent
			parent = node.parent;
			if (node.isLeft) {
				// Get the brother node
				brother = parent.right;
				if (brother.isRed) {
					brother.isRed = false;
					parent.isRed = true;
					rotateLeft(parent);
					brother = parent.right;
				}
				left = brother.left;
				right = brother.right;
				if ((left == null || !left.isRed) && (right == null || !right.isRed)) {
					brother.isRed = true;
					node = parent;
				} else {
					if (right == null || !right.isRed) {
						if (left != null) {
							left.isRed = false;
						}
						brother.isRed = true;
						rotateRight(brother);
						brother = parent.right;
					}
					brother.isRed = parent.isRed;
					parent.isRed = false;
					right.isRed = false;
					rotateLeft(parent);
					node = root;
					break;
				}
			} else {
				// Get the brother node
				brother = parent.left;
				if (brother.isRed) {
					brother.isRed = false;
					parent.isRed = true;
					rotateRight(parent);
					brother = parent.left;
				}
				left = brother.left;
				right = brother.right;
				if ((left == null || !left.isRed) && (right == null || !right.isRed)) {
					brother.isRed = true;
					node = parent;
				} else {
					if (left == null || !left.isRed) {
						if (right != null) {
							right.isRed = false;
						}
						brother.isRed = true;
						rotateLeft(brother);
						brother = parent.left;
					}
					brother.isRed = parent.isRed;
					parent.isRed = false;
					left.isRed = false;
					rotateRight(parent);
					node = root;
					break;
				}
			}
		}
		node.isRed = false;
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
	public RedBlackTreeMap<K, V> clone() {
		return new RedBlackTreeMap<K, V>(keyComparator, this);
	}
}
