package avlg;

import avlg.exceptions.UnimplementedMethodException;
import avlg.exceptions.EmptyTreeException;
import avlg.exceptions.InvalidBalanceException;

/** <p>{@link AVLGTree}  is a class representing an <a href="https://en.wikipedia.org/wiki/AVL_tree">AVL Tree</a> with
 * a relaxed balance condition. Its constructor receives a strictly  positive parameter which controls the <b>maximum</b>
 * imbalance allowed on any subtree of the tree which it creates. So, for example:</p>
 *  <ul>
 *      <li>An AVL-1 tree is a classic AVL tree, which only allows for perfectly balanced binary
 *      subtrees (imbalance of 0 everywhere), or subtrees with a maximum imbalance of 1 (somewhere). </li>
 *      <li>An AVL-2 tree relaxes the criteria of AVL-1 trees, by also allowing for subtrees
 *      that have an imbalance of 2.</li>
 *      <li>AVL-3 trees allow an imbalance of 3.</li>
 *      <li>...</li>
 *  </ul>
 *
 *  <p>The idea behind AVL-G trees is that rotations cost time, so maybe we would be willing to
 *  accept bad search performance now and then if it would mean less rotations. On the other hand, increasing
 *  the balance parameter also means that we will be making <b>insertions</b> faster.</p>
 *
 * @author Jemimah E.P. Salvacion
 *
 * @see EmptyTreeException
 * @see InvalidBalanceException
 * @see StudentTests
 */
public class AVLGTree<T extends Comparable<T>> {

    /* ********************************************************* *
     * Write any private data elements or private methods here...*
     * ********************************************************* */
	private class TreeNode {
		private T data;
		private TreeNode lChild;
		private TreeNode rChild;
		
		public TreeNode() {
			data = null;
			lChild = null;
			rChild = null;
		}
	}
	
	private TreeNode root;
	private int maxImbalance;
	private int size;


    /* ******************************************************** *
     * ************************ PUBLIC METHODS **************** *
     * ******************************************************** */

    /**
     * The class constructor provides the tree with the maximum imbalance allowed.
     * @param maxImbalance The maximum imbalance allowed by the AVL-G Tree.
     * @throws InvalidBalanceException if maxImbalance is a value smaller than 1.
     */
    public AVLGTree(int maxImbalance) throws InvalidBalanceException {
    	root = null;
    	this.maxImbalance = maxImbalance;
    	size = 0;
    }

    /**
     * Insert key in the tree. You will <b>not</b> be tested on
     * duplicates! This means that in a deletion test, any key that has been
     * inserted and subsequently deleted should <b>not</b> be found in the tree!
     * s
     * @param key The key to insert in the tree.
     */
    public void insert(T key) {
    	root = insertHelper(root,key);
    }
    
    /**
     * Helper method for insert()
     * Insertions must involve checking if maxImbalance is reached
     * If so, rotations must be done.
     */
    private TreeNode insertHelper(TreeNode rt, T keyInput) {
    	if (rt == null) {
    		rt = new TreeNode();
    		rt.data = keyInput;
    		size++;
    	}
    	if (keyInput.compareTo(rt.data) < 0) {
    		rt.lChild = insertHelper(rt.lChild,keyInput);
        	rotator(rt);
    	}
    	if (keyInput.compareTo(rt.data) > 0) {
    		rt.rChild = insertHelper(rt.rChild,keyInput);
        	rotator(rt);
    	}
    	 return rt;
    }
    
    /* rotator(TreeNode rt): 
     * Helper method to check balance of current node and determine whether rotations are needed
     */
    private void rotator(TreeNode rt) {
    	if (getCurrBalance(rt) < -1*maxImbalance) {
    		if (getCurrBalance(rt.rChild) <= 0 || getCurrBalance(rt.rChild) == maxImbalance)
    			rotateLeft(rt);
    		else if (getCurrBalance(rt.rChild) > 0)
    			rotateRL(rt);
    	} else if (getCurrBalance(rt) > maxImbalance) {
    		if (getCurrBalance(rt.lChild) >= 0 || getCurrBalance(rt.lChild) == maxImbalance )
    			rotateRight(rt);
    		else if (getCurrBalance(rt.lChild) < 0)
    			rotateLR(rt);
    	}
    }

    /**
     * Delete the key from the data structure and return it to the caller.
     * @param key The key to delete from the structure.
     * @return The key that was removed, or {@code null} if the key was not found.
     * @throws EmptyTreeException if the tree is empty.
     */
    public T delete(T key) throws EmptyTreeException {
    	root = deleteHelper(root,key);
    	rotator(root);
        return key;
    }
    
    /* deleteHelper(TreeNode curr, T keyInput)
     * 
     */
    private TreeNode deleteHelper(TreeNode node, T keyInput) {
    	TreeNode curr = node;
    	if (node != null) {
	    	if (keyInput.compareTo(node.data) == 0) {
	    		if (isLeaf(curr)) {
	    			curr = null;
	    		} else {
	    			if (curr.rChild.lChild == null) {
	    				curr.data = curr.rChild.data;
	    				curr.rChild = deleteHelper(curr.rChild,curr.data);
	    			} else {
	    				T temp = fetch(curr.rChild.lChild);
	    				curr.data = temp;
	    				curr.rChild = deleteHelper(curr.rChild,temp);
	    			}
	    		}
	    	}    		
	    	else if (keyInput.compareTo(node.data) < 0)
	    		curr.lChild = deleteHelper(node.lChild,keyInput);
	    	else if (keyInput.compareTo(node.data) > 0)
	    		curr.rChild = deleteHelper(node.rChild,keyInput);
    	}
    	return curr;
    }
    /* fetch(TreeNode curr):
     * Will fetch leftmost leaf on specified branch of curr
     */
    private T fetch(TreeNode curr) {
    	if (curr.lChild == null) 
    		return curr.data;
    	return fetch(curr.lChild);
    }

    /**
     * <p>Search for key in the tree. Return a reference to it if it's in there,
     * or {@code null} otherwise.</p>
     * @param key The key to search for.
     * @return key if key is in the tree, or {@code null} otherwise.
     * @throws EmptyTreeException if the tree is empty.
     */
    public T search(T key) throws EmptyTreeException {
        return searchHelper(root,key).data;
    }
    
    private TreeNode searchHelper(TreeNode node,T keyInput) {
    	TreeNode toReturn = node;
    	if (node != null) {
	    	if (keyInput.compareTo(node.data) == 0)
	    		return toReturn;
	    	else if (keyInput.compareTo(node.data) < 0)
	    		toReturn = searchHelper(node.lChild,keyInput);
	    	else if (keyInput.compareTo(node.data) > 0)
	    		toReturn = searchHelper(node.rChild,keyInput);
    	}
    	return toReturn;
    }
    
    

    /**
     * Retrieves the maximum imbalance parameter.
     * @return The maximum imbalance parameter provided as a constructor parameter.
     */
    public int getMaxImbalance(){
    	return maxImbalance;
    }


    /**
     * <p>Return the height of the tree. The height of the tree is defined as the length of the
     * longest path between the root and the leaf level. By definition of path length, a
     * stub tree has a height of 0, and we define an empty tree to have a height of -1.</p>
     * @return The height of the tree. If the tree is empty, returns -1.
     */
    public int getHeight() {
    	return heightHelper(root);
    }
     
    /**
     * Query the tree for emptiness. A tree is empty iff it has zero keys stored.
     * @return {@code true} if the tree is empty, {@code false} otherwise.
     */
    public boolean isEmpty() {
        if (size == 0)
        	return true;
        return false;
    }

    /**
     * Return the key at the tree's root node.
     * @return The key at the tree's root node.
     * @throws  EmptyTreeException if the tree is empty.
     */
    public T getRoot() throws EmptyTreeException{
    	if (isEmpty())
    		throw new EmptyTreeException("No elements in tree.");
    	return root.data;
    }


    /**
     * <p>Establishes whether the AVL-G tree <em>globally</em> satisfies the BST condition. This method is
     * <b>terrifically useful for testing!</b></p>
     * @return {@code true} if the tree satisfies the Binary Search Tree property,
     * {@code false} otherwise.
     */
    public boolean isBST() {        
        return traverse(root);
    }
    
    private boolean traverse(TreeNode curr) {
    	boolean ret = false;
    	if (curr == null) {
    		return false;
    	}
    	traverse(curr.lChild);
    	if (!isLeaf(curr) && curr.lChild != null || curr.rChild != null) {
    		boolean condition = false;
    		if (curr.lChild != null && curr.rChild != null)
    			condition = curr.lChild.data.compareTo(curr.data) < 0 && curr.rChild.data.compareTo(curr.data) > 0;
    		else if (curr.lChild != null)
    			condition = curr.lChild.data.compareTo(curr.data) < 0;
    		else if (curr.rChild != null)
    			condition = curr.rChild.data.compareTo(curr.data) > 0;
    			
    		if (!condition) {
    			ret = false;
    			return ret;
    		} else {
    			ret = true;
    		}
    	}
    	traverse(curr.rChild);
    	return ret;
    }


    /**
     * <p>Establishes whether the AVL-G tree <em>globally</em> satisfies the AVL-G condition. This method is
     * <b>terrifically useful for testing!</b></p>
     * @return {@code true} if the tree satisfies the balance requirements of an AVLG tree, {@code false}
     * otherwise.
     */
    public boolean isAVLGBalanced() {
        throw new UnimplementedMethodException();       // ERASE THIS LINE AFTER YOU IMPLEMENT THIS METHOD!
    }

    /**
     * <p>Empties the AVL-G Tree of all its elements. After a call to this method, the
     * tree should have <b>0</b> elements.</p>
     */
    public void clear(){
    	root = null;
    	size = 0;
    }


    /**
     * <p>Return the number of elements in the tree.</p>
     * @return  The number of elements in the tree.
     */
    public int getCount(){
        throw new UnimplementedMethodException();       // ERASE THIS LINE AFTER YOU IMPLEMENT THIS METHOD!
    }
    
    /* ******************************************************** *
     * ************************ PRIVATE METHODS **************** *
     * ******************************************************** */
    
    /* heightHelper(TreeNode rt):
     * rt - root node
     * 
     * Helper method for getHeight()
     */
    private int heightHelper(TreeNode rt) {
		if (rt == null) {
			return -1;
		} else {
			int lHeight = heightHelper(rt.lChild);
			int rHeight = heightHelper(rt.rChild);
			
			if (rHeight > lHeight)
				return rHeight + 1;
			else
				return lHeight + 1;
		}		
	}
    
    /* Balance:
     * B(n) = h(LTree) - h(RTree)
     */
    private int getCurrBalance(TreeNode node) {
    	return heightHelper(node.lChild) - heightHelper(node.rChild);
    }
    
    /* Rotations:
     * - After insertion of new element, must check the balance of root
     * - Left Rotation requires B(n) < -1 && B(r) < 0
     */
    private void rotateLeft(TreeNode node) {
    	if (node.lChild != null) {
			rotateLeft(node.lChild);
		}
    	if (!isLeaf(node)) {
			TreeNode temp = node.rChild; 
			if (node.lChild == null)
				node.lChild = new TreeNode();
			node.lChild.data = node.data;
			if (node.rChild != null) {
				node.data = node.rChild.data;
				if (temp.lChild != null) {
					if (node.lChild.rChild == null)
						node.lChild.rChild = new TreeNode();
					node.lChild.rChild.data = temp.lChild.data;
				}
				node.rChild = temp.rChild;
			} 
		} 
		if (isLeaf(node)) {
			if (node.lChild == null)
				node.lChild = new TreeNode();
			node.lChild.data = node.data;
			return;
		}   	
    }
    
    /*rotateRight(TreeNode right)
     * - After insertion of new element, must check the balance of root
     * - Right Rotation requires B(n) > 1 && B(r) > 0
     */
    private void rotateRight(TreeNode node) {
    	if (node.rChild != null) {
    		rotateRight(node.rChild);
    	}
    	if (!isLeaf(node)) {
    		TreeNode temp = node.lChild; 
			if (node.rChild == null)
				node.rChild = new TreeNode();
			node.rChild.data = node.data;
			if (node.lChild != null) {
				node.data = node.lChild.data;
				if (temp.rChild != null) {
					if (node.rChild.lChild == null)
						node.rChild.lChild = new TreeNode();
					node.rChild.lChild.data = temp.rChild.data;
				}
				node.lChild = temp.lChild;
			} 
    	}
    	if (isLeaf(node)) {
			if (node.rChild == null)
				node.rChild = new TreeNode();
			node.rChild.data = node.data;
			return;
		} 
    }
    
    /*rotateRL(TreeNode node)
     * - Rotate right at root of left subtree, then rotate
     * left at tree node
     */
    private void rotateRL(TreeNode node) {
    	 rotateRight(node.rChild);
    	 rotateLeft(node);
    }
    
    /*rotateLR(TreeNode node)
     * - Rotate left at root of right subtree, then rotate
     * left at tree node
     */
    private void rotateLR(TreeNode node) {
    	rotateLeft(node.lChild);
    	rotateRight(node);
    }
    
    /*isLeaf(TreeNode curr): checks if current node is a leaf
     * Parameters:
     * curr - node input
     * Other notes:
     * Will assume that curr is not null
     */
    private boolean isLeaf(TreeNode curr) {
    	return curr.lChild == null && curr.rChild == null;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
