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
 * This class implements a System Dynamics model node representing a constant.
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
public class ConstantNode extends AbstractNode implements ASTElement {
   
   /** minimal constant value */
   public static final double MIN_CONSTANT = -1000000000;
   
   /** maximal constant value */
   public static final double MAX_CONSTANT = 1000000000;

   private double constantValue;
   
   /**
    * Constructor.
    * 
    * @param nodeName node name
    * @param constantValue constant value (only from MIN_CONSTANT to MAX_CONSTANT!)
    */
   protected ConstantNode(String nodeName, double constantValue) {
      if (nodeName == null) {
         throw new IllegalArgumentException("'nodeName' must not be null.");
      }
      if (constantValue < MIN_CONSTANT || constantValue > MAX_CONSTANT) {
         throw new NodeParameterOutOfRangeException(MIN_CONSTANT, MAX_CONSTANT);
      }
      
      setNodeName(nodeName);
      setConstantValue(constantValue);
   }
   
   /**
    * Helper method for creating new instances of this class. Called by JUnit test cases.
    * 
    * @param nodeName node name
    * @param constantValue constant value
    * @return created new instance
    */
   private static ConstantNode createConstantNode(String nodeName, double constantValue) {
      return new ConstantNode(nodeName, constantValue);
   }
   
   /**
    * Sets the constant node's constant value.
    * 
    * @param constantValue constant value (only from MIN_CONSTANT to MAX_CONSTANT!)
    */
   void setConstantValue(double constantValue) {
      if (constantValue < MIN_CONSTANT || constantValue > MAX_CONSTANT) {
         throw new NodeParameterOutOfRangeException(MIN_CONSTANT, MAX_CONSTANT);
      }
      
      this.constantValue = constantValue;
   }
   
   /**
    * Gets the constant node's constant value.
    * 
    * @return constant value
    */
   public double getConstantValue() {
      return constantValue;
   }

   /////////////////////////////////////////////////////////////////////////////////////////////////
   // methods from abstract class AbstractNode
   /////////////////////////////////////////////////////////////////////////////////////////////////
   
   /**
    * Gets the constant node's current value.
    * 
    * This method is only implemented for fulfill the requirements of the abstract superclass. It
    * just returns the constant node's constant value.
    */
   @Override
public double getCurrentValue() {
      return constantValue;
   }
   
   /**
    * Computes the node's next value. This method does nothing as it is a constant node.
    */
   @Override
void computeNextValue() {
      // to nothing (this is a constant!)
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
      return constantValue;
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
      return getNodeName() + "(CN)";
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
      if (constantNode2id == null) {
         throw new IllegalArgumentException("'constantNode2id' must not be null.");
      }
      if (constantNode2id.isEmpty()) {
         throw new IllegalArgumentException("'constantNode2id' must not be empty.");
      }
      
      return "CN(" + constantNode2id.get(this) + ")";
   }
   
   /**
    * Creates and returns a <b>deep</b> copy of this object. Only the nodes in the leaves
    * are not cloned.
    * 
    * @return a deep clone of this instance
    */
   @Override
public Object clone() {
      // return 'this' ConstantNode, no clone!
      return this;
   }
   
   /**
    * Returns an iterator over the subtree of this node (here: only this node).
    * 
    * @return iterator over the subtree of this node (here: only this node)
    */
   public Iterator<ASTElement> iterator() {
      return new ConstantNodeIterator(this);
   }
   
   /**
    * Inner class implementing the {@link java.util.Iterator} interface. 
    */
   private class ConstantNodeIterator implements Iterator<ASTElement> {
      
      private ConstantNode constantNode;
      
      /**
       * Constructor.
       * 
       * @param constantNode {@link de.uka.aifb.com.systemDynamics.model.ConstantNode} instance
       */
      private ConstantNodeIterator(ConstantNode constantNode) {
         if (constantNode == null) {
            throw new IllegalArgumentException("'constantNode' must not be null.");
         }
         
         this.constantNode = constantNode;
      }
      
      /**
       * Checks if there is a next element in this iteration.
       * 
       * @return <code>true</code> iff there is a next element
       */
      public boolean hasNext() {
         return constantNode != null;
      }
      
      /**
       * Gets this iteration's next element.
       * 
       * @return next element
       */
      public ASTElement next() {
         if (constantNode != null) {
            ConstantNode temp = constantNode;
            constantNode = null;
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