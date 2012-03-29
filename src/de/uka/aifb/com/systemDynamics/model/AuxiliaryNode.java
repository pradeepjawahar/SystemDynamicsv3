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
 * This class implements a System Dynamics model node representing an auxiliary node.
 * 
 * All methods of AbstractNode and its subclasses are only visible within
 * this package. Only getter methods whose return values are not changeable
 * from outside can be public. All setter methods must be invoked using
 * an adequate method from class
 * {@link de.uka.aifb.com.systemDynamics.model.Model}.
 * 
 * @author Joachim Melcher, University of Karlsruhe, AIFB
 * @version 1.0
 */
public class AuxiliaryNode extends AbstractNode implements ASTElement {

   private double currentValue;
   
   private ASTElement formula;
   
   /**
    * Constructor.
    * 
    * @param nodeName node name
    */
   protected AuxiliaryNode(String nodeName) {
      if (nodeName == null) {
         throw new IllegalArgumentException("'nodeName' must not be null.");
      }
         
      setNodeName(nodeName);
   }
   
   /**
    * Helper method for creating new instances of this class. Called by JUnit test cases.
    * 
    * @param nodeName node name
    * @return created new instance
    */
   private static AuxiliaryNode createAuxiliaryNode(String nodeName) {
      return new AuxiliaryNode(nodeName);
   }
   
   /**
    * Sets the auxiliary node's formula. A deep copy of the formula is stored. So the stored formula
    * cannot be changed from outside. The formula can also be <code>null</code> in order to
    * delete the formula.
    * 
    * @param formula formula
    */
   void setFormula(ASTElement formula) {
      if (formula == null) {
         this.formula = null;
      } else {
         this.formula = (ASTElement)formula.clone();
      }
   }
   
   /**
    * Checks whether the auxiliary node has a formula.
    * 
    * @return <code>true</code> iff the auxiliary node has a formula
    */
   public boolean hasFormula() {
      return (formula != null);
   }
   
   /**
    * Gets the auxiliary node's formula. A deep copy of the formula is returned. So the stored
    * formula cannot be changed from outside.
    * 
    * @return auxiliary node's formula or <code>null</code> iff there is no formula
    */
   public ASTElement getFormula() {
      if (formula == null) {
         return null;
      }
      
      return (ASTElement)formula.clone();
   }
   
   /**
    * Gets all nodes this auxiliary node depends on.
    * 
    * @return set of all nodes this auxiliary node depends on
    */
   public HashSet<AbstractNode> getAllNodesThisOneDependsOn() {
      return (formula != null) ? formula.getAllNodesInASTSubtree() : new HashSet<AbstractNode>();
   }

   /////////////////////////////////////////////////////////////////////////////////////////////////
   // methods from abstract class AbstractNode
   /////////////////////////////////////////////////////////////////////////////////////////////////
   
   /**
    * Get's the auxiliary node's current value.
    * 
    * @return current value
    */
   @Override
public double getCurrentValue() {
      return currentValue;
   }
   
   /**
    * Computes the node's next value.
    */
   @Override
void computeNextValue() {
      currentValue = formula.evaluate();
   }
   
   /////////////////////////////////////////////////////////////////////////////////////////////////
   // methods from interface ASTElement
   /////////////////////////////////////////////////////////////////////////////////////////////////
   
   /**
    * Evaluates the ASTElement.
    * 
    * @return ASTElement value
    */
   public double evaluate() {
      return currentValue;
   }
   
   /**
    * Gets all nodes in this AST subtree (inclusive this ASTElement).
    * 
    * @return set of all nodes in AST subtree
    */
   public HashSet<AbstractNode> getAllNodesInASTSubtree() {
      HashSet<AbstractNode> nodeSet = new HashSet<AbstractNode>();
      nodeSet.add(this);
      return nodeSet;
   }
   
   /**
    * Gets a <code>String</code> representation of the node's formula.
    * 
    * @return <code>String</code> representation of the node's formula
    */
   public String getStringRepresentation() {
      return getNodeName() + "(AN)";
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
      if (auxiliaryNode2id.isEmpty()) {
         throw new IllegalArgumentException("'auxiliaryNode2id' must not be empty.");
      }
      
      return "AN(" + auxiliaryNode2id.get(this) + ")";
   }
   
   /**
    * Creates and returns a <b>deep</b> copy of this object. Only the nodes in the leaves
    * are not cloned.
    * 
    * @return a deep clone of this instance
    */
   @Override
public Object clone() {
      // return 'this' AuxiliaryNode, no clone!
      return this;
   }
   
   /**
    * Returns an iterator over the subtree of this node (here: only this node).
    * 
    * @return iterator over the subtree of this node (here: only this node)
    */
   public Iterator<ASTElement> iterator() {
      return new AuxiliaryNodeIterator(this);
   }
   
   /**
    * Inner class implementing the {@link java.util.Iterator} interface. 
    */
   private class AuxiliaryNodeIterator implements Iterator<ASTElement> {
      
      private AuxiliaryNode auxiliaryNode;
      
      /**
       * Constructor.
       * 
       * @param auxiliaryNode {@link de.uka.aifb.com.systemDynamics.model.AuxiliaryNode} instance
       */
      private AuxiliaryNodeIterator(AuxiliaryNode auxiliaryNode) {
         if (auxiliaryNode == null) {
            throw new IllegalArgumentException("'auxiliaryNode' must not be null.");
         }
         
         this.auxiliaryNode = auxiliaryNode;
      }
      
      /**
       * Checks if there is a next element in this iteration.
       * 
       * @return <code>true</code> iff there is a next element
       */
      public boolean hasNext() {
         return auxiliaryNode != null;
      }
      
      /**
       * Gets this iteration's next element.
       * 
       * @return next element
       */
      public ASTElement next() {
         if (auxiliaryNode != null) {
            AuxiliaryNode temp = auxiliaryNode;
            auxiliaryNode = null;
            return temp;
         } else {
            throw new NoSuchElementException();
         }
      }
      
      /**
       * Removes the element last returned by this iterator.
       */
      public void remove() {
         throw new UnsupportedOperationException();
      }
   }
}