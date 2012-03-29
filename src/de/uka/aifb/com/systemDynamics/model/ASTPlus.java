/* ======================================================================================================
 * SystemDynamics: Java application for modeling, visualization and execution of System Dynamics models
 * ======================================================================================================
 *
 * (C) Copyright 2007-2008, Joachim Melcher, Institut AIFB, Universitaet Karlsruhe (TH), Germany
 *
 * Project Info:  http://sourceforge.net/projects/system-dynamics
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program; if
 * not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA  02110-1301, USA.
 */

package de.uka.aifb.com.systemDynamics.model;

import java.util.*;

/**
 * This class implements an AST element representing an addition.
 * 
 * @author Joachim Melcher, University of Karlsruhe, AIFB
 * @version 1.0
 */
public class ASTPlus implements ASTElement {

   private ASTElement leftElement;
   private ASTElement rightElement;
   
   /**
    * Constructor.
    * 
    * @param firstSummand first summand
    * @param secondSummand second summand
    */
   public ASTPlus(ASTElement firstSummand, ASTElement secondSummand) {
      if (firstSummand == null) {
         throw new IllegalArgumentException("'firstSummand' must not be null.");
      }
      if (secondSummand == null) {
         throw new IllegalArgumentException("'secondSummand' must not be null.");
      }
      
      leftElement = firstSummand;
      rightElement = secondSummand;
   }
   
   /**
    * Evaluates the ASTElement.
    * 
    * @return ASTElement value
    */
   public double evaluate() {
      return leftElement.evaluate() + rightElement.evaluate();
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
      if (rightElement instanceof ASTPlus || rightElement instanceof ASTMinus) {
         return leftElement.getStringRepresentation() + " + (" + rightElement.getStringRepresentation() + ")";
      } else {
         return leftElement.getStringRepresentation() + " + " + rightElement.getStringRepresentation();
      }
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
      
      if (rightElement instanceof ASTPlus || rightElement instanceof ASTMinus) {
         return leftElement.getShortStringRepresentation(auxiliaryNode2id, constantNode2id, levelNode2id) + " + (" + rightElement.getShortStringRepresentation(auxiliaryNode2id, constantNode2id, levelNode2id) + ")";
      } else {
         return leftElement.getShortStringRepresentation(auxiliaryNode2id, constantNode2id, levelNode2id) + " + " + rightElement.getShortStringRepresentation(auxiliaryNode2id, constantNode2id, levelNode2id);
      }
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
      
      return new ASTPlus(leftClone, rightClone);
   }
   
   /**
    * Returns an iterator over the subtree of this node (output in preorder).
    * 
    * @return iterator over the subtree of this node (output in preorder)
    */
   public Iterator<ASTElement> iterator() {
      return new ASTPlusIterator(this);
   }
   
   /**
    * Inner class implementing the {@link java.util.Iterator} interface. 
    */
   private class ASTPlusIterator implements Iterator<ASTElement> {
      
      private ASTPlus astPlus;
      private Iterator<ASTElement> leftSubtreeIterator;
      private Iterator<ASTElement> rightSubtreeIterator;
      
      /**
       * Constructor.
       * 
       * @param astPlus {@link de.uka.aifb.com.systemDynamics.model.ASTPlus} instance
       */
      private ASTPlusIterator(ASTPlus astPlus) {
         if (astPlus == null) {
            throw new IllegalArgumentException("'astPlus' must not be null.");
         }
         
         // deep copy of the ASTPlus is used -> so it cannot be changed from outside
         this.astPlus = (ASTPlus)astPlus.clone();
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
            leftSubtreeIterator = astPlus.leftElement.iterator();
            return astPlus;
         }
         
         // (2) nodes of left subtree
         if (rightSubtreeIterator == null) {
            ASTElement result = leftSubtreeIterator.next();
            if (!leftSubtreeIterator.hasNext()) {
               rightSubtreeIterator = astPlus.rightElement.iterator();
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