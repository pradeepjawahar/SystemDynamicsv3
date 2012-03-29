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
 * This class implements a System Dynamics model node representing a level.
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
public class LevelNode extends AbstractNode implements ASTElement, Comparable<LevelNode> {
   
   /** minimal start value */
   public static final double MIN_START_VALUE = -1000000000;
   
   /** maximal start value */
   public static final double MAX_START_VALUE = 1000000000;
   
   private double startValue;
   private double currentValue;
   private HashSet<RateNode> incomingFlows;
   private HashSet<RateNode> outgoingFlows;

   /**
    * Constructor.
    * 
    * @param nodeName node name
    * @param startValue start value (only from MIN_START_VALUE to MAX_START_VALUE!)
    */
   protected LevelNode(String nodeName, double startValue) {
      if (nodeName == null) {
         throw new IllegalArgumentException("'nodeName' must not be null.");
      }
      if (startValue < MIN_START_VALUE || startValue > MAX_START_VALUE) {
         throw new NodeParameterOutOfRangeException(MIN_START_VALUE, MAX_START_VALUE);
      }
      
      setNodeName(nodeName);
      setStartValue(startValue);
      
      incomingFlows = new HashSet<RateNode>();
      outgoingFlows = new HashSet<RateNode>();
   }
   
   /**
    * Helper method for creating new instances of this class. Called by JUnit test cases.
    * 
    * @param nodeName node name
    * @param startValue start value
    * @return created new instance
    */
   private static LevelNode createLevelNode(String nodeName, double startValue) {
      return new LevelNode(nodeName, startValue);
   }
   
   /**
    * Sets the level node's start value (and the <i>first</i> current value).
    * 
    * @param startValue start value (only from MIN_START_VALUE to MAX_START_VALUE!)
    */
   void setStartValue(double startValue) {
      if (startValue < MIN_START_VALUE || startValue > MAX_START_VALUE) {
         throw new NodeParameterOutOfRangeException(MIN_START_VALUE, MAX_START_VALUE);
      }
      
      this.startValue = startValue;
      currentValue = startValue;
   }
   
   /**
    * Gets the level node's start value.
    * 
    * @return start value
    */
   public double getStartValue() {
      return startValue;
   }
   
   /**
    * Adds the specified incoming flow.
    * 
    * @param incomingFlow incoming flow
    * @return <code>true</code> iff this incoming flow was not already element of the set of
    *         incoming flows
    */
   boolean addIncomingFlow(RateNode incomingFlow) {
      if (incomingFlow == null) {
         throw new IllegalArgumentException("'incomingFlow' must not be null.");
      }
      
      return incomingFlows.add(incomingFlow);
   }
   
   /**
    * Removes the specified incoming flow.
    * 
    * @param incomingFlow incoming flow
    * @return <code>true</code> iff this incoming flow was element of the set of incoming flows
    */
   boolean removeIncomingFlow(RateNode incomingFlow) {
      if (incomingFlow == null) {
         throw new IllegalArgumentException("'incomingFlow' must not be null.");
      }
      
      return incomingFlows.remove(incomingFlow);
   }
   
   /**
    * Gets the set of all incoming flows. A shallow clone of the set of incoming flows is returned.
    * 
    * @return incoming flows
    */
   public HashSet<RateNode> getIncomingFlows() {
      return (HashSet<RateNode>)incomingFlows.clone();
   }
   
   /**
    * Adds the specified outgoing flow.
    * 
    * @param outgoingFlow outgoing flow
    * @return <code>true</code> iff this outgoing flow was not already element of the set of
    *         outgoing flows
    */
   boolean addOutgoingFlow(RateNode outgoingFlow) {
      if (outgoingFlow == null) {
         throw new IllegalArgumentException("'outgoingFlow' must not be null.");
      }
      
      return outgoingFlows.add(outgoingFlow);
   }
   
   /**
    * Removes the specified outgoing flow.
    * 
    * @param outgoingFlow outgoing flow
    * @return <code>true</code> iff this outgoing flow was element of the set of outgoing flows
    */
   boolean removeOutgoingFlow(RateNode outgoingFlow) {
      if (outgoingFlow == null) {
         throw new IllegalArgumentException("'outgoingFlow' must not be null.");
      }
      
      return outgoingFlows.remove(outgoingFlow);
   }

   /**
    * Gets the set of all outgoing flows. A shallow clone of the set of outgoing flows is returned.
    * 
    * @return outgoing flows
    */
   public HashSet<RateNode> getOutgoingFlows() {
      return (HashSet<RateNode>)outgoingFlows.clone();
   }
   
   /////////////////////////////////////////////////////////////////////////////////////////////////
   // methods from abstract class AbstractNode
   /////////////////////////////////////////////////////////////////////////////////////////////////
   
   /**
    * Gets the level node's current value.
    * 
    * @return current value
    */
   @Override
public double getCurrentValue() {
      return currentValue;
   }
   
   /**
    * Computes the node's next value.
    * 
    * The next value is computed using the following formula:
    * 
    * next value = current value + sum of incoming flows - sum of outgoing flows
    */
   @Override
void computeNextValue() {
      // add incoming flows...
      for (RateNode incomingFlow : incomingFlows) {
         currentValue += incomingFlow.getCurrentValue();
      }
      
      // ... and subtract outgoing flows
      for (RateNode outgoingFlow : outgoingFlows) {
         currentValue -= outgoingFlow.getCurrentValue();
      }
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
    * Gets all nodes in this AST subtree.
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
      return getNodeName() + "(LN)";
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
      if (levelNode2id == null) {
         throw new IllegalArgumentException("'levelNode2id' must not be null.");
      }
      if (levelNode2id.isEmpty()) {
         throw new IllegalArgumentException("'levelNode2id' must not be empty.");
      }
      
      return "LN(" + levelNode2id.get(this) + ")";
   }
   
   /**
    * Creates and returns a <b>deep</b> copy of this object. Only the nodes in the leaves
    * are not cloned.
    * 
    * @return a deep clone of this instance
    */
   @Override
public Object clone() {
      // return 'this' LevelNode, no clone!
      return this;
   }
   
   /**
    * Returns an iterator over the subtree of this node (here: only this node).
    * 
    * @return iterator over the subtree of this node (here: only this node)
    */
   public Iterator<ASTElement> iterator() {
      return new LevelNodeIterator(this);
   }
   
////////////////////////////////////////////////////////////////////////////////////////////////////
//                               methods of interface Comparable
////////////////////////////////////////////////////////////////////////////////////////////////////
   
   /**
    * Compares this level node with the specified level node for order.
    * <p>
    * Returns a negative integer, zero, or a positive integer as this object is less than, equal to,
    * or greater than the specified object.
    * 
    * @param levelNode level node to compare to
    * @return a negative integer, zero, or a positive integer as this object is less than, equal to,
    *         or greater than the specified object
    */
   public int compareTo(LevelNode levelNode) {
      if (levelNode == null) {
         throw new IllegalArgumentException("'levelNode' must not be null.");
      }
      
      return getNodeName().compareTo(levelNode.getNodeName());
   }
   
   /**
    * Inner class implementing the {@link java.util.Iterator} interface. 
    */
   private class LevelNodeIterator implements Iterator<ASTElement> {
      
      private LevelNode levelNode;
      
      /**
       * Constructor.
       * 
       * @param levelNode {@link de.uka.aifb.com.systemDynamics.model.LevelNode} instance
       */
      private LevelNodeIterator(LevelNode levelNode) {
         if (levelNode == null) {
            throw new IllegalArgumentException("'levelNode' must not be null.");
         }
         
         this.levelNode = levelNode;
      }
      
      /**
       * Checks if there is a next element in this iteration.
       * 
       * @return <code>true</code> iff there is a next element
       */
      public boolean hasNext() {
         return levelNode != null;
      }
      
      /**
       * Gets this iteration's next element.
       * 
       * @return next element
       */
      public ASTElement next() {
         if (levelNode != null) {
            LevelNode temp = levelNode;
            levelNode = null;
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