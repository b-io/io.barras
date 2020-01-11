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

public class AvlTreeNode<K extends Comparable<K>, V>
		extends BinaryTreeNode<K, V, AvlTreeNode<K, V>> {

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
	 * The {@link AvlTreeMap} of types {@code K} and {@code V}.
	 */
	protected final AvlTreeMap<K, V> tree;

	/**
	 * The height.
	 */
	protected long height;
	/**
	 * The balance.
	 */
	protected long balance;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs an {@link AvlTreeNode} with the specified key and value belonging to the specified
	 * {@link AvlTreeMap}.
	 * <p>
	 * @param key   the {@code K} key
	 * @param value the {@code V} value
	 * @param tree  the {@link AvlTreeMap} of types {@code K} and {@code V}
	 */
	public AvlTreeNode(final K key, final V value, final AvlTreeMap<K, V> tree) {
		super(key, value);
		this.tree = tree;
		height = 0;
		balance = 0;
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
		return height;
	}

	/**
	 * Returns the balance.
	 * <p>
	 * @return the balance
	 */
	public long getBalance() {
		return balance;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// SETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the parent {@link AvlTreeNode} and updates all the parent {@link AvlTreeNode} if
	 * required.
	 * <p>
	 * @param parentNode an {@link AvlTreeNode} of types {@code K} and {@code V}
	 */
	protected void setParent(final AvlTreeNode<K, V> parentNode) {
		parent = parentNode;
		if (tree.isUpdate()) {
			updateAllParents();
		}
	}

	/**
	 * Sets the left {@link AvlTreeNode} and updates all the parent {@link AvlTreeNode} if required.
	 * <p>
	 * @param leftNode an {@link AvlTreeNode} of types {@code K} and {@code V}
	 */
	@Override
	public void setLeft(final AvlTreeNode<K, V> leftNode) {
		left = leftNode;
		if (tree.isUpdate()) {
			update();
		}
		if (left != null) {
			left.isLeft = true;
			left.setParent(this);
		}
	}

	/**
	 * Sets the right {@link AvlTreeNode} and updates all the parent {@link AvlTreeNode} if
	 * required.
	 * <p>
	 * @param rightNode an {@link AvlTreeNode} of types {@code K} and {@code V}
	 */
	@Override
	public void setRight(final AvlTreeNode<K, V> rightNode) {
		right = rightNode;
		if (tree.isUpdate()) {
			update();
		}
		if (right != null) {
			right.isLeft = false;
			right.setParent(this);
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// UPDATERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Updates {@code this} and if {@code this} is a leaf, updates all the parent
	 * {@link AvlTreeNode}.
	 */
	public void updateAll() {
		update();
		if (isLeaf()) {
			updateAllParents();
		}
	}

	/**
	 * Updates the height and balance.
	 */
	public void update() {
		if (isLeaf()) {
			height = 0;
			balance = 0;
		} else {
			long leftHeight;
			long rightHeight;
			// Update the height
			if (left == null) {
				leftHeight = -1;
				rightHeight = right.height;
				height = 1 + right.height;
			} else if (right == null) {
				leftHeight = left.height;
				rightHeight = -1;
				height = 1 + left.height;
			} else {
				leftHeight = left.height;
				rightHeight = right.height;
				height = 1 + (leftHeight >= rightHeight ? leftHeight : rightHeight);
			}
			// Update the balance
			balance = rightHeight - leftHeight;
		}
	}

	/**
	 * Updates all the parent {@link AvlTreeNode}.
	 */
	public void updateAllParents() {
		AvlTreeNode<K, V> node = parent;
		while (node != null) {
			node.update();
			node = node.parent;
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code this} is a leaf.
	 * <p>
	 * @return {@code true} if {@code this} is a leaf, {@code false} otherwise
	 */
	public boolean isLeaf() {
		return left == null && right == null;
	}
}
