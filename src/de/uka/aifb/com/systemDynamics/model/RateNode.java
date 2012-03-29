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

import java.util.HashSet;

/**
 * This class implements a System Dynamics model node representing a change rate.
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
public class RateNode extends AbstractNode {
   
   private double currentValue;
   
   private AbstractNode flowSource;
   private AbstractNode flowSink;
   
   private ASTElement formula;
   
   /**
    * Constructor.
    * 
    * @param nodeName node name
    */
   protected RateNode(String nodeName) {
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
   private static RateNode createRateNode(String nodeName) {
      return new RateNode(nodeName);
   }
   
   /**
    * Sets the source of this flow.
    * 
    * @param sourceNode level or source/sink node at this flow's source
    */
   void setFlowSource(AbstractNode sourceNode) {
      if (sourceNode == null) {
         throw new IllegalArgumentException("'sourceNode' must not be null.");
      }
      if (!(sourceNode instanceof LevelNode) && !(sourceNode instanceof SourceSinkNode)) {
         throw new IllegalArgumentException("'sourceNode' must be a level or a source/sink node.");
      }
      
      flowSource = sourceNode;
   }
   
   /**
    * Removes the source of this flow.
    */
   void removeFlowSource() {
      flowSource = null;
   }
   
   /**
    * Gets the source of this flow.
    * 
    * @return level or source/sink node at this flow's source
    */
   public AbstractNode getFlowSource() {
      return flowSource;
   }
   
   /**
    * Sets the sink of this flow.
    * 
    * @param sinkNode level or source/sink node at this flow's sink
    */
   void setFlowSink(AbstractNode sinkNode) {
      if (sinkNode == null) {
         throw new IllegalArgumentException("'sinkNode' must not be null.");
      }
      if (!(sinkNode instanceof LevelNode) && !(sinkNode instanceof SourceSinkNode)) {
         throw new IllegalArgumentException("'sinkNode' must be a level or a source/sink node.");
      }
      
      flowSink = sinkNode;
   }
   
   /**
    * Removes the sink of this flow.
    */
   void removeFlowSink() {
      flowSink = null;
   }
   
   /**
    * Gets the sink of this flow.
    * 
    * @return level or source/sink node at this flow's sink
    */
   public AbstractNode getFlowSink() {
      return flowSink;
   }
   
   /**
    * Sets the rate node's formula. A deep copy of the formula is stored. So the stored formula
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
    * Checks whether the rate node has a formula.
    * 
    * @return <code>true</code> iff the rate node has a formula
    */
   public boolean hasFormula() {
      return (formula != null);
   }
   
   /**
    * Gets the rate node's formula. A deep copy of the formula is returned. So the stored formula
    * cannot be changed from outside.
    * 
    * @return rate node's formula or <code>null</code> iff there is no formula
    */
   public ASTElement getFormula() {
      if (formula == null) {
         return null;
      }
      
      return (ASTElement)formula.clone();
   }
   
   /**
    * Gets all nodes this rate node depends on (only nodes of node formula).
    * 
    * @return set of all nodes this rate node depends on (only nodes of node formula)
    */
   public HashSet<AbstractNode> getAllNodesThisOneDependsOn() {
      return (formula != null) ? formula.getAllNodesInASTSubtree() : new HashSet<AbstractNode>();
   }
   
   /**
    * Gets all nodes this rate node depends on (nodes of node formula and source/sink nodes of
    * incoming or outgoing flow).
    * 
    * @return set of all nodes this rate node depends on (nodes of node formula and source/sink
    *         nodes of incoming or outgoing flow)
    */
   public HashSet<AbstractNode> getAllNodesThisOneDependsOnAndSourceSinkNodes() {
      HashSet<AbstractNode> result = getAllNodesThisOneDependsOn();
      if (flowSource != null) {
         if (flowSource instanceof SourceSinkNode) {
            result.add(flowSource);
         }
      }
      if (flowSink != null) {
         if (flowSink instanceof SourceSinkNode) {
            result.add(flowSink);
         }
      }
      
      return result;
   }

   /////////////////////////////////////////////////////////////////////////////////////////////////
   // methods from abstract class AbstractNode
   /////////////////////////////////////////////////////////////////////////////////////////////////
   
   /**
    * Get's the rate node's current value.
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
}