package de.uka.aifb.com.systemDynamics.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;



public class ASTMax implements ASTElement {
	  private ASTElement leftElement;
	  private ASTElement rightElement;
	   
	   /**
	    * Constructor.
	    * 
	    * @param firstFactor first factor
	    * @param secondFactor second factor
	    */
	   public ASTMax(ASTElement firstFactor, ASTElement secondFactor) {
	      if (firstFactor == null) {
	         throw new IllegalArgumentException("'firstFactor' must not be null.");
	      }
	      if (secondFactor == null) {
	         throw new IllegalArgumentException("'secondFactor' must not be null.");
	      }
	      
	      leftElement = firstFactor;
	      rightElement = secondFactor;
	   }
	   
	   /**
	    * Evaluates the ASTElement.
	    * 
	    * @return ASTElement value
	    */
	   public double evaluate() {
		 return Math.max(leftElement.evaluate(), rightElement.evaluate());
	   }
	   
	   /**
	    * Gets all nodes in this AST subtree (inclusive this ASTElement).
	    * 
	    * @return set of all nodes in AST subtree
	    */
	   public HashSet<AbstractNode> getAllNodesInASTSubtree() {
	      HashSet<AbstractNode> nodeSet = leftElement.getAllNodesInASTSubtree();
	      nodeSet.addAll(rightElement.getAllNodesInASTSubtree());
	      return nodeSet;
	   }
	   
	   /**
	    * Gets a <code>String</code> representation of the node's formula.
	    * 
	    * @return <code>String</code> representation of the node's formula
	    */
	   public String getStringRepresentation() {
	      if ((leftElement instanceof AbstractNode || leftElement instanceof ASTMax) && rightElement instanceof AbstractNode) {
	         return "MAX(" + leftElement.getStringRepresentation() + " , " + rightElement.getStringRepresentation() + ")";
	      }
	      return null;
	   }
	   
	   /**
	    * Gets a short <code>String</code> representation of the node's formula.
	    * 
	    * @param auxiliaryNode2id auxiliary node to id mapping
	    * @param constantNode2id constant node to id mapping
	    * @param levelNode2id level node to id mapping
	    * @return short <code>String</code> representation of the node's formula
	    */
	   public String getShortStringRepresentation(HashMap<AuxiliaryNode, Integer> auxiliaryNode2id,
	                                              HashMap<ConstantNode, Integer> constantNode2id,
	                                              HashMap<LevelNode, Integer> levelNode2id) {
	      if (auxiliaryNode2id == null) {
	         throw new IllegalArgumentException("'auxiliaryNode2id' must not be null.");
	      }
	      if (constantNode2id == null) {
	         throw new IllegalArgumentException("'constantNode2id' must not be null.");
	      }
	      if (levelNode2id == null) {
	         throw new IllegalArgumentException("'levelNode2id' must not be null.");
	      }
	     if ((leftElement instanceof AbstractNode || leftElement instanceof ASTMax) && rightElement instanceof AbstractNode) {
		         return "MAX(" + leftElement.getShortStringRepresentation(auxiliaryNode2id, constantNode2id, levelNode2id) + " , " + rightElement.getShortStringRepresentation(auxiliaryNode2id, constantNode2id, levelNode2id) + ")";
		      }
	    return null;
	   }
	   
	   /**
	    * Creates and returns a <b>deep</b> copy of this object. Only the nodes in the leaves
	    * are not cloned.
	    * 
	    * @return a deep clone of this instance
	    */
	   @Override
	public Object clone() {
	      ASTElement leftClone = (ASTElement)leftElement.clone();
	      ASTElement rightClone = (ASTElement)rightElement.clone();
	      
	      return new ASTMax(leftClone, rightClone);
	   }
	   
	   /**
	    * Returns an iterator over the subtree of this node (output in preorder).
	    * 
	    * @return iterator over the subtree of this node (output in preorder)
	    */
	   public Iterator<ASTElement> iterator() {
	      return new ASTMaxIterator(this);
	   }
	   
	   /**
	    * Inner class implementing the {@link java.util.Iterator} interface. 
	    */
	   private class ASTMaxIterator implements Iterator<ASTElement> {
	      
	      private ASTMax astMax;
	      private Iterator<ASTElement> leftSubtreeIterator;
	      private Iterator<ASTElement> rightSubtreeIterator;
	      
	      /**
	       * Constructor.
	       * 
	       * @param astMax {@link de.uka.aifb.com.systemDynamics.model.ASTRound} instance
	       */
	      private ASTMaxIterator(ASTMax astMax) {
	         if (astMax == null) {
	            throw new IllegalArgumentException("'astMax' must not be null.");
	         }
	         
	         // deep copy of the ASTMultiply is used -> so it cannot be changed from outside
	         this.astMax = (ASTMax)astMax.clone();
	      }
	      
	      /**
	       * Checks if there is a next element in this iteration.
	       * 
	       * @return <code>true</code> iff there is a next element
	       */
	      public boolean hasNext() {
	         return (rightSubtreeIterator == null || rightSubtreeIterator.hasNext());
	      }
	      
	      /**
	       * Gets this iteration's next element.
	       * 
	       * @return next element
	       */
	      public ASTElement next() {
	         // (1) root node
	         if (leftSubtreeIterator == null) {
	            leftSubtreeIterator = astMax.leftElement.iterator();
	            return astMax;
	         }
	         
	         // (2) nodes of left subtree
	         if (rightSubtreeIterator == null) {
	            ASTElement result = leftSubtreeIterator.next();
	            if (!leftSubtreeIterator.hasNext()) {
	               rightSubtreeIterator = astMax.rightElement.iterator();
	            }
	            return result;
	         }
	         
	         // (3) nodes of right subtree
	         //     (NoSuchElementException will be thrown if there are no nodes any more)
	         return rightSubtreeIterator.next();
	      }
	      
	      /**
	       * Removes the element last returned by this iterator.
	       */
	      public void remove() {
	         throw new UnsupportedOperationException();
	      }
	   }
}
