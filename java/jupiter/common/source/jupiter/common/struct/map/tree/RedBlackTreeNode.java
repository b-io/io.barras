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
package jupiter.common.struct.map.tree;

import java.util.Comparator;

public class RedBlackTreeNode<K, V>
		extends BinaryTreeNode<K, V, RedBlackTreeNode<K, V>> {

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

	protected boolean isBlack;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link RedBlackTreeNode} with the specified {@code K} key, {@code V} value and
	 * key {@link Comparator}.
	 * <p>
	 * @param key           the {@code K} key
	 * @param value         the {@code V} value (may be {@code null})
	 * @param keyComparator the key {@link Comparator} of {@code K} supertype to determine the order
	 */
	public RedBlackTreeNode(final K key, final V value, final Comparator<? super K> keyComparator) {
		super(key, value, keyComparator);
		isBlack = false;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ACCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the left {@link RedBlackTreeNode}.
	 * <p>
	 * @param leftNode a {@link RedBlackTreeNode} of {@code K} and {@code V} types (may be
	 *                 {@code null})
	 */
	@Override
	public void setLeft(final RedBlackTreeNode<K, V> leftNode) {
		left = leftNode;
		if (left != null) {
			left.isLeft = true;
			left.parent = this;
		}
	}

	/**
	 * Sets the right {@link RedBlackTreeNode}.
	 * <p>
	 * @param rightNode a {@link RedBlackTreeNode} of {@code K} and {@code V} types (may be
	 *                  {@code null})
	 */
	@Override
	public void setRight(final RedBlackTreeNode<K, V> rightNode) {
		right = rightNode;
		if (right != null) {
			right.isLeft = false;
			right.parent = this;
		}
	}
}
