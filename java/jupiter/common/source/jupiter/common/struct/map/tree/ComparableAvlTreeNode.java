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

public class ComparableAvlTreeNode<K extends Comparable<? super K>, V>
		extends ComparableBinaryTreeNode<K, V, ComparableAvlTreeNode<K, V>> {

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
	 * The {@link ComparableAvlTreeMap} of {@code K} and {@code V} types.
	 */
	protected final ComparableAvlTreeMap<K, V> tree;

	/**
	 * The height.
	 */
	protected int height;
	/**
	 * The balance.
	 */
	protected int balance;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link ComparableAvlTreeNode} with the specified key and value belonging to the
	 * specified {@link ComparableAvlTreeMap}.
	 * <p>
	 * @param key   the {@code K} key
	 * @param value the {@code V} value (may be {@code null})
	 * @param tree  the {@link ComparableAvlTreeMap} of {@code K} and {@code V} types
	 */
	public ComparableAvlTreeNode(final K key, final V value,
			final ComparableAvlTreeMap<K, V> tree) {
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
	public int getHeight() {
		return height;
	}

	/**
	 * Returns the balance.
	 * <p>
	 * @return the balance
	 */
	public int getBalance() {
		return balance;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// SETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the parent {@link ComparableAvlTreeNode} and updates all the parent
	 * {@link ComparableAvlTreeNode} if required.
	 * <p>
	 * @param parentNode a {@link ComparableAvlTreeNode} of {@code K} and {@code V} types
	 */
	protected void setParent(final ComparableAvlTreeNode<K, V> parentNode) {
		parent = parentNode;
		if (tree.isUpdate()) {
			updateAllParents();
		}
	}

	/**
	 * Sets the left {@link ComparableAvlTreeNode} and updates all the parent
	 * {@link ComparableAvlTreeNode} if required.
	 * <p>
	 * @param leftNode a {@link ComparableAvlTreeNode} of {@code K} and {@code V} types (may be
	 *                 {@code null})
	 */
	@Override
	public void setLeft(final ComparableAvlTreeNode<K, V> leftNode) {
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
	 * Sets the right {@link ComparableAvlTreeNode} and updates all the parent
	 * {@link ComparableAvlTreeNode} if required.
	 * <p>
	 * @param rightNode a {@link ComparableAvlTreeNode} of {@code K} and {@code V} types (may be
	 *                  {@code null})
	 */
	@Override
	public void setRight(final ComparableAvlTreeNode<K, V> rightNode) {
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
	 * {@link ComparableAvlTreeNode}.
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
			int leftHeight, rightHeight;
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
	 * Updates all the parent {@link ComparableAvlTreeNode}.
	 */
	public void updateAllParents() {
		ComparableAvlTreeNode<K, V> node = parent;
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
