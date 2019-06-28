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
package jupiter.common.struct.map.tree.node;

public class RedBlackTreeNode<K extends Comparable<K>, V>
		extends BinaryTreeNode<K, V, RedBlackTreeNode<K, V>> {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 2437289030870896552L;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	public boolean isRed;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link RedBlackTreeNode} with the specified key and value.
	 * <p>
	 * @param key   the {@code K} key
	 * @param value the {@code V} value
	 */
	public RedBlackTreeNode(final K key, final V value) {
		super(key, value);
		isRed = true;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// SETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the left {@link RedBlackTreeNode}.
	 * <p>
	 * @param leftNode a {@link RedBlackTreeNode} of type {@code K} and {@code V}
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
	 * @param rightNode a {@link RedBlackTreeNode} of type {@code K} and {@code V}
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
