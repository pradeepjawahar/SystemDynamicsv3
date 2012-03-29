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
 * This class implements a System Dynamics model node representing a source/sink.
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
public class SourceSinkNode extends AbstractNode {
   
   private HashSet<RateNode> incomingFlows;
   private HashSet<RateNode> outgoingFlows;
   
   /**
    * Constructor.
    */
   protected SourceSinkNode() {
      incomingFlows = new HashSet<RateNode>();
      outgoingFlows = new HashSet<RateNode>();
   }
   
   /**
    * Helper method for creating new instances of this class. Called by JUnit test cases.
    * 
    * @return created new instance
    */
   private static SourceSinkNode createSourceSinkNode() {
      return new SourceSinkNode();
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
   //                        methods from abstract class AbstractNode
   /////////////////////////////////////////////////////////////////////////////////////////////////
   
   /**
    * Sets the node name.
    * <p>
    * A source/sink node does not support this operation.
    * 
    * @param nodeName node name
    * @throws UnsupportedOperationException
    */
   @Override
void setNodeName(String nodeName) {
      throw new UnsupportedOperationException();
   }
   
   /**
    * Gets the node name.
    * <p>
    * A source/sink node does not support this operation.
    * 
    * @return node name
    * @throws UnsupportedOperationException
    */
   @Override
public String getNodeName() {
      throw new UnsupportedOperationException();
   }
   
   /**
    * Gets the node's current value.
    * <p>
    * A source/sink node does not support this operation.
    * 
    * @return current value
    * @throws UnsupportedOperationException
    */
   @Override
public double getCurrentValue() {
      throw new UnsupportedOperationException();
   }
   
   /**
    * Computes the node's next value.
    * <p>
    * This method does nothing because a source/sink node has no value.
    */
   @Override
void computeNextValue() {
      // do nothing
   }
}