
/**
 * Generic binary tree, storing data of a parametric data in each node
 *
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012
 * @author CBK, Spring 2016, minor updates to testing
 * @author Tim Pierson, Winter 2018, added code to manually build tree in main
 */

/**
 * @author Scott Crawshaw
 * 5/2/19
 * Problem Set 3
 * I borrowed this class from the CS10 website.
 * I deleted functions I didn't need, and edited line 92. I changed "data" to "data.toString()" so that I could print information about a CharData rather than just its memory address. Only used for testing.
 */

public class BinaryTree<E> {
	private BinaryTree<E> left, right;	// children; can be null
	E data;

	/**
	 * Constructs leaf node -- left and right are null
	 */
	public BinaryTree(E data) {
		this.data = data; this.left = null; this.right = null;
	}

	/**
	 * Constructs inner node
	 */
	public BinaryTree(E data, BinaryTree<E> left, BinaryTree<E> right) {
		this.data = data; this.left = left; this.right = right;
	}
	
	/**
	 * Is it a leaf node?
	 */
	public boolean isLeaf() {
		return left == null && right == null;
	}

	/**
	 * Does it have a left child?
	 */
	public boolean hasLeft() {
		return left != null;
	}

	/**
	 * Does it have a right child?
	 */
	public boolean hasRight() {
		return right != null;
	}

	public BinaryTree<E> getLeft() {
		return left;
	}

	public BinaryTree<E> getRight() {
		return right;
	}

	public E getData() {
		return data;
	}

	/**
	 * Number of nodes (inner and leaf) in tree
	 */
	public int size() {
		int num = 1;
		if (hasLeft()) num += left.size();
		if (hasRight()) num += right.size();
		return num;
	}

	/**
	 * Returns a string representation of the tree
	 */
	public String toString() {
		return toStringHelper("");
	}

	/**
	 * Recursively constructs a String representation of the tree from this node, 
	 * starting with the given indentation and indenting further going down the tree
	 */
	public String toStringHelper(String indent) {
		String res = indent + data.toString() + "\n";
		if (hasLeft()) res += left.toStringHelper(indent+"  L");
		if (hasRight()) res += right.toStringHelper(indent+"  R");
		return res;
	}

}