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

package de.uka.aifb.com.systemDynamics.test;

import de.uka.aifb.com.systemDynamics.model.*;
import java.util.*;
import junit.framework.*;
import junitx.util.PrivateAccessor;

/**
 * This class implements a test case for the class
 * {@link de.uka.aifb.com.systemDynamics.model.ConstantNode}.
 * 
 * @author Joachim Melcher, University of Karlsruhe, AIFB
 * @version 1.0
 */
public class ConstantNodeTestCase extends TestCase {
   
   private static final String INITIAL_NODE_NAME = "NODE NAME";
   private static final double INITIAL_CONSTANT_VALUE = ConstantNode.MAX_CONSTANT / 2;
   
   private ConstantNode constantNode;

   public static Test suite() {  
      return new TestSuite(ConstantNodeTestCase.class);
   }
   
   @Override
protected void setUp() throws Exception {
      constantNode = createConstantNode(INITIAL_NODE_NAME, INITIAL_CONSTANT_VALUE);
   }

   @Override
protected void tearDown() throws Exception {
      constantNode = null;
   }
   
   /**
    * Tests the constructor.
    */
   public void testConstantNode() {
      // (1) null as node name -> WRONG!
      try {
         createConstantNode(null, 0);
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'nodeName' must not be null.", e.getMessage());
      }
      
      // (2) too small constant value -> WRONG!
      try {
         createConstantNode(INITIAL_NODE_NAME, ConstantNode.MIN_CONSTANT - 0.0001);
         fail();
      } catch (NodeParameterOutOfRangeException e) {
      }
      
      // (3) constant value exactly at the lower border -> CORRECT!
      createConstantNode(INITIAL_NODE_NAME, ConstantNode.MIN_CONSTANT);      
      
      // (4) too large constant value
      try {
         createConstantNode(INITIAL_NODE_NAME, ConstantNode.MAX_CONSTANT + 0.0001);
         fail();
      } catch (NodeParameterOutOfRangeException e) {
      }
      
      // (5) constant value exactly at the upper border -> CORRECT!
      createConstantNode(INITIAL_NODE_NAME, ConstantNode.MAX_CONSTANT);
      
      // (6) correct parameters -> CORRECT!
      createConstantNode(INITIAL_NODE_NAME, 0);
   }
   
   /**
    * Tests the methods
    * {@link de.uka.aifb.com.systemDynamics.model.ConstantNode#getConstantValue()} and
    * {@link de.uka.aifb.com.systemDynamics.model.ConstantNode#setConstantValue(double)}.
    */
   public void testGetAndSetConstantValue() {
      assertEquals(constantNode.getConstantValue(), INITIAL_CONSTANT_VALUE);
      
      // (1) too small constant value -> WRONG!
      try {
         PrivateAccessor.invoke(constantNode, "setConstantValue", new Class[] { double.class },
                                new Object[] { ConstantNode.MIN_CONSTANT - 0.0001 });
         fail();
      } catch (NodeParameterOutOfRangeException e) {
         // do nothing
      } catch (Throwable t) {
         fail();
      }
      assertEquals(constantNode.getConstantValue(), INITIAL_CONSTANT_VALUE);
      
      // (2) too large constant value -> WRONG!
      try {
         PrivateAccessor.invoke(constantNode, "setConstantValue", new Class[] { double.class },
                                new Object[] { ConstantNode.MAX_CONSTANT + 0.0001 });
         fail();
      } catch (NodeParameterOutOfRangeException e) {
         // do nothing
      } catch (Throwable t) {
         fail();
      }
      assertEquals(constantNode.getConstantValue(), INITIAL_CONSTANT_VALUE);
      
      // (3) first correct value -> CORRECT!
      try {
         PrivateAccessor.invoke(constantNode, "setConstantValue", new Class[] { double.class },
                                new Object[] { 0 });
      } catch (Throwable t) {
         fail();
      }
      assertEquals(0.0, constantNode.getConstantValue());
      
      // (4) second correct value -> CORRECT!
      try {
         PrivateAccessor.invoke(constantNode, "setConstantValue", new Class[] { double.class },
                                new Object[] { ConstantNode.MIN_CONSTANT / 2 });
      } catch (Throwable t) {
         fail();
      }
      assertEquals(ConstantNode.MIN_CONSTANT / 2, constantNode.getConstantValue());
   }
   
   /**
    * Tests the methods
    * {@link de.uka.aifb.com.systemDynamics.model.ConstantNode#getNodeName()} and
    * {@link de.uka.aifb.com.systemDynamics.model.ConstantNode#setNodeName(String)}.
    */
   public void testGetAndSetNodeName() {
      assertEquals(constantNode.getNodeName(), INITIAL_NODE_NAME);
      
      // (1) null as node name -> WRONG!
      try {
         PrivateAccessor.invoke(constantNode, "setNodeName", new Class[] { String.class },
                                new Object[] { null });
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'nodeName' must not be null.", e.getMessage());
      } catch (Throwable t) {
         fail();
      }
      assertEquals(constantNode.getNodeName(), INITIAL_NODE_NAME);
      
      // (2) first correct new node name -> CORRECT!
      try {
         PrivateAccessor.invoke(constantNode, "setNodeName", new Class[] { String.class },
                                new Object[] { "New Node Name 1" });
      } catch (Throwable t) {
         fail();
      }
      assertEquals(constantNode.getNodeName(), "New Node Name 1");
      
      // (3) second correct value -> CORRECT!
      try {
         PrivateAccessor.invoke(constantNode, "setNodeName", new Class[] { String.class },
                                new Object[] { "New Node Name 2" });
      } catch (Throwable t) {
         fail();
      }
      assertEquals(constantNode.getNodeName(), "New Node Name 2");
   }
   
   /**
    * Tests the methods {@link de.uka.aifb.com.systemDynamics.model.ConstantNode#computeNextValue()}
    * and {@link de.uka.aifb.com.systemDynamics.model.ConstantNode#getCurrentValue()}.
    */
   public void testComputeNextValueAndGetCurrentValue() {
      assertEquals(INITIAL_CONSTANT_VALUE, constantNode.getCurrentValue());
      
      try {
         PrivateAccessor.invoke(constantNode, "computeNextValue", null, null);
      } catch (Throwable t) {
         fail();
      }
      assertEquals(INITIAL_CONSTANT_VALUE, constantNode.getCurrentValue());
      
      try {
         PrivateAccessor.invoke(constantNode, "computeNextValue", null, null);
      } catch (Throwable t) {
         fail();
      }
      assertEquals(INITIAL_CONSTANT_VALUE, constantNode.getCurrentValue());
   }
   
   /**
    * Tests the method {@link de.uka.aifb.com.systemDynamics.model.ConstantNode#evaluate()}.
    */
   public void testEvaluate() {
      assertEquals(INITIAL_CONSTANT_VALUE, constantNode.evaluate());
      
      try {
         PrivateAccessor.invoke(constantNode, "computeNextValue", null, null);
      } catch (Throwable t) {
         fail();
      }
      assertEquals(INITIAL_CONSTANT_VALUE, constantNode.evaluate());
   }
   
   /**
    * Tests the method
    * {@link de.uka.aifb.com.systemDynamics.model.ConstantNode#getAllNodesInASTSubtree()}.
    */
   public void testGetAllNodesInASTSubtree() {
      HashSet<AbstractNode> nodeSet = constantNode.getAllNodesInASTSubtree();
      assertEquals(1, nodeSet.size());
      assertTrue(nodeSet.contains(constantNode));
   }
   
   /**
    * Tests the method
    * {@link de.uka.aifb.com.systemDynamics.model.ConstantNode#getStringRepresentation()}.
    */
   public void testGetStringRepresentation() {
      assertEquals(INITIAL_NODE_NAME + "(CN)", constantNode.getStringRepresentation());
   }
   
   /**
    * Tests the method
    * {@link de.uka.aifb.com.systemDynamics.model.ConstantNode#clone()}.
    */
   public void testClone() {
      assertTrue(constantNode == constantNode.clone());
   }
   
   /**
    * Tests the method
    * {@link de.uka.aifb.com.systemDynamics.model.ConstantNode#iterator()}.
    */
   public void testIterator() {
      // (1) first iterator
      Iterator<ASTElement> iterator = constantNode.iterator();
      
      assertTrue(iterator.hasNext());
      assertTrue(iterator.next() == constantNode);
      assertFalse(iterator.hasNext());
      try {
         iterator.next();
      } catch (NoSuchElementException e) {
         // do nothing
      }
      
      // (2) second iterator
      iterator = constantNode.iterator();
      
      assertTrue(iterator.hasNext());
      assertTrue(iterator.next() == constantNode);
      assertFalse(iterator.hasNext());
      try {
         iterator.next();
      } catch (NoSuchElementException e) {
         // do nothing
      }
   }
   
   /**
    * Helper method for invoking the private constructor of class ConstantNode.
    * 
    * @param nodeName node name
    * @param constantValue constant value
    * @return created new instance of class ConstantNode
    * @throws NodeParameterOutOfRangeException if the constant value is out of range
    */
   private ConstantNode createConstantNode(String nodeName, double constantValue)
         throws NodeParameterOutOfRangeException {
      ConstantNode node = null;
      try {
         node =
            (ConstantNode)PrivateAccessor.invoke(ConstantNode.class,
                                                 "createConstantNode",
                                                 new Class[] { String.class, double.class },
                                                 new Object[] { nodeName, constantValue });
      } catch (NodeParameterOutOfRangeException e) { 
         throw e;
      } catch (IllegalArgumentException e) {
         throw e;
      } catch (Throwable t) {
         // no other error/exception other than NodeParameterOutOfRangeException
         // and IllegalArgumentException possible
         fail();
      }
      return node;
   }
}