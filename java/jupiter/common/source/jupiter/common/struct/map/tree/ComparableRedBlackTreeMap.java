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

import java.util.Map;

import jupiter.common.math.Comparables;
import jupiter.common.model.ICloneable;
import jupiter.common.struct.list.ExtendedLinkedList;
import jupiter.common.struct.tuple.Pair;
import jupiter.common.test.Arguments;

/**
 * {@link ComparableRedBlackTreeMap} is a light sorted synchronized {@link Map} implementation of
 * {@code K} and {@code V} types based on a red-black tree.
 * <p>
 * @param <K> the self {@link Comparable} key type of the {@link ComparableRedBlackTreeMap}
 * @param <V> the value type of the {@link ComparableRedBlackTreeMap}
 */
public class ComparableRedBlackTreeMap<K extends Comparable<? super K>, V>
		extends ComparableBinaryTreeMap<K, V, ComparableRedBlackTreeNode<K, V>> {

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
	 * Constructs an empty {@link ComparableRedBlackTreeMap} of {@code K}, {@code V} and {@code N}
	 * types.
	 */
	public ComparableRedBlackTreeMap() {
		super();
	}

	//////////////////////////////////////////////

	/**
	 * Constructs a {@link ComparableRedBlackTreeMap} of {@code K}, {@code V} and {@code N} types
	 * loaded from the specified key and value arrays containing the key-value mappings.
	 * <p>
	 * @param keys   the {@code K} array containing the keys of the key-value mappings to load
	 * @param values the {@code V} array containing the values of the key-value mappings to load
	 * <p>
	 * @throws ClassCastException   if any {@code keys} cannot be mutually compared
	 * @throws NullPointerException if any {@code keys} is {@code null}
	 */
	protected ComparableRedBlackTreeMap(final K[] keys, final V[] values) {
		super(keys, values);
	}

	/**
	 * Constructs a {@link ComparableRedBlackTreeMap} of {@code K}, {@code V} and {@code N} types
	 * loaded from the specified {@link Map} containing the key-value mappings.
	 * <p>
	 * @param map the {@link Map} of {@code K} and {@code V} subtypes containing the key-value
	 *            mappings to load
	 * <p>
	 * @throws ClassCastException if any {@code map} keys cannot be mutually compared
	 */
	public ComparableRedBlackTreeMap(final Map<? extends K, ? extends V> map) {
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
	@Override
	@SuppressWarnings({"unchecked", "varargs"})
	public int getHeight() {
		// Initialize
		final ExtendedLinkedList<Pair<Integer, ComparableRedBlackTreeNode<K, V>>> nodes = new ExtendedLinkedList<Pair<Integer, ComparableRedBlackTreeNode<K, V>>>(
				new Pair<Integer, ComparableRedBlackTreeNode<K, V>>(0, root));

		// Compute the height
		int currentHeight = 0, nextHeight = 1;
		boolean hasLeaf = false;
		while (!nodes.isEmpty()) {
			final Pair<Integer, ComparableRedBlackTreeNode<K, V>> element = nodes.remove();
			final int height = element.getFirst();
			final ComparableRedBlackTreeNode<K, V> node = element.getSecond();
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
				nodes.add(new Pair<Integer, ComparableRedBlackTreeNode<K, V>>(nextHeight,
						node.left));
				nodes.add(new Pair<Integer, ComparableRedBlackTreeNode<K, V>>(nextHeight,
						node.right));
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
	 * @param node a {@link ComparableRedBlackTreeNode} of {@code K} and {@code V} types (may be
	 *             {@code null})
	 */
	@Override
	protected void setRoot(final ComparableRedBlackTreeNode<K, V> node) {
		root = node;
		if (root != null) {
			root.parent = null;
			root.isBlack = true;
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
	 * @param value the {@code V} value of the key-value mapping to put (may be {@code null})
	 * <p>
	 * @return the previous associated {@code V} value, or {@code null} if it is not present
	 * <p>
	 * @throws ClassCastException   if {@code key} cannot be compared to {@code this} keys
	 * @throws NullPointerException if {@code key} is {@code null}
	 */
	@Override
	public synchronized V put(final K key, final V value) {
		// Check the arguments
		Arguments.requireNonNull(key, "key");

		// Put the key-value mapping
		ComparableRedBlackTreeNode<K, V> tree = root;
		if (tree == null) {
			// The root is set to a new node containing the key and value
			setRoot(new ComparableRedBlackTreeNode<K, V>(key, value));
		} else {
			int comparison;
			ComparableRedBlackTreeNode<K, V> parent;
			do {
				comparison = Comparables.compare(key, tree.key);
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
			final ComparableRedBlackTreeNode<K, V> newNode = new ComparableRedBlackTreeNode<K, V>(
					key, value);
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
	 * Removes the specified {@link ComparableRedBlackTreeNode}.
	 * <p>
	 * @param node the {@link ComparableRedBlackTreeNode} of {@code K} and {@code V} types to remove
	 */
	@Override
	protected void removeNode(final ComparableRedBlackTreeNode<K, V> node) {
		// Get the parent and successor of the node
		final ComparableRedBlackTreeNode<K, V> parent = node.parent;
		// Test whether there is 0 or 1 child or there are 2 children
		if (node.left == null || node.right == null) {
			// • There is 0 or 1 child (so the tree is guaranteed to be balanced)
			// Get the child (if it exists)
			final ComparableRedBlackTreeNode<K, V> child;
			if (node.left != null) {
				child = node.left;
			} else if (node.right != null) {
				child = node.right;
			} else {
				child = null;
				// If the node is black
				if (node.isBlack) {
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
			if (child != null && node.isBlack) {
				// Balance this tree from the child
				balanceAfterDeletion(child);
			}
			// Decrement the number of nodes
			--size;
		} else {
			// • There are 2 children (so the tree is not guaranteed to be balanced)
			// Get the successor of the node to remove
			// @note the successor cannot be null since the node has a right node
			final ComparableRedBlackTreeNode<K, V> successor = getSuccessor(node);
			// Remove the successor
			removeNode(successor);
			// Override the node with the successor
			node.key = successor.key;
			node.value = successor.value;
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Balances after inserting the specified {@link ComparableRedBlackTreeNode}.
	 * <p>
	 * @param node the inserted {@link ComparableRedBlackTreeNode} of {@code K} and {@code V} types
	 */
	@Override
	protected void balanceAfterInsertion(ComparableRedBlackTreeNode<K, V> node) {
		// Get the parent
		// @note the parent cannot be null since the node is inserted after the root
		ComparableRedBlackTreeNode<K, V> parent = node.parent;
		// Get the grand-parent
		ComparableRedBlackTreeNode<K, V> grandParent = parent.parent;
		ComparableRedBlackTreeNode<K, V> uncle;
		while (!parent.isBlack) {
			if (parent.isLeft) {
				// Get the uncle
				uncle = grandParent != null ? grandParent.right : null;
				if (uncle != null && !uncle.isBlack) {
					// Update the colors
					parent.isBlack = true;
					uncle.isBlack = true;
					grandParent.isBlack = false;
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
					parent.isBlack = true;
					if (grandParent != null) {
						grandParent.isBlack = false;
						rotateRight(grandParent);
					}
				}
			} else {
				// Get the uncle
				uncle = grandParent != null ? grandParent.left : null;
				if (uncle != null && !uncle.isBlack) {
					// Update the colors
					parent.isBlack = true;
					uncle.isBlack = true;
					grandParent.isBlack = false;
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
					parent.isBlack = true;
					if (grandParent != null) {
						grandParent.isBlack = false;
						rotateLeft(grandParent);
					}
				}
			}
		}
		root.isBlack = true;
	}

	/**
	 * Balances after deleting the specified {@link ComparableRedBlackTreeNode}.
	 * <p>
	 * @param node the deleted {@link ComparableRedBlackTreeNode} of {@code K} and {@code V} types
	 */
	@Override
	protected void balanceAfterDeletion(ComparableRedBlackTreeNode<K, V> node) {
		ComparableRedBlackTreeNode<K, V> parent, brother, left, right;
		while (node != root && node.isBlack) {
			// Get the parent
			parent = node.parent;
			if (node.isLeft) {
				// Get the brother node
				brother = parent.right;
				if (!brother.isBlack) {
					brother.isBlack = true;
					parent.isBlack = false;
					rotateLeft(parent);
					brother = parent.right;
				}
				left = brother.left;
				right = brother.right;
				if ((left == null || left.isBlack) && (right == null || right.isBlack)) {
					brother.isBlack = false;
					node = parent;
				} else {
					if (right == null || right.isBlack) {
						if (left != null) {
							left.isBlack = true;
						}
						brother.isBlack = false;
						rotateRight(brother);
						brother = parent.right;
					}
					brother.isBlack = parent.isBlack;
					parent.isBlack = true;
					right.isBlack = true;
					rotateLeft(parent);
					node = root;
					break;
				}
			} else {
				// Get the brother node
				brother = parent.left;
				if (!brother.isBlack) {
					brother.isBlack = true;
					parent.isBlack = false;
					rotateRight(parent);
					brother = parent.left;
				}
				left = brother.left;
				right = brother.right;
				if ((left == null || left.isBlack) && (right == null || right.isBlack)) {
					brother.isBlack = false;
					node = parent;
				} else {
					if (left == null || left.isBlack) {
						if (right != null) {
							right.isBlack = true;
						}
						brother.isBlack = false;
						rotateLeft(brother);
						brother = parent.left;
					}
					brother.isBlack = parent.isBlack;
					parent.isBlack = true;
					left.isBlack = true;
					rotateRight(parent);
					node = root;
					break;
				}
			}
		}
		node.isBlack = true;
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
	public ComparableRedBlackTreeMap<K, V> clone() {
		return new ComparableRedBlackTreeMap<K, V>(this);
	}
}
