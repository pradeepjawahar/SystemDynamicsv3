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
import de.uka.aifb.com.systemDynamics.test.mocks.RateNodeMockObject;
import java.util.*;
import junit.framework.*;
import junitx.util.PrivateAccessor;

/**
 * This class implements a test case for the class
 * {@link de.uka.aifb.com.systemDynamics.model.LevelNode}.
 * 
 * @author Joachim Melcher, University of Karlsruhe, AIFB
 * @version 1.0
 */
public class LevelNodeTestCase extends TestCase {
   
   private static final String INITIAL_NODE_NAME = "NODE NAME";
   private static final double INITIAL_START_VALUE = LevelNode.MAX_START_VALUE / 2;
   
   private LevelNode levelNode;
   
   public static Test suite() {  
      return new TestSuite(LevelNodeTestCase.class);
   }
   
   @Override
protected void setUp() throws Exception {
      levelNode = createLevelNode(INITIAL_NODE_NAME, INITIAL_START_VALUE);
   }

   @Override
protected void tearDown() throws Exception {
      levelNode = null;
   }
   
   /**
    * Tests the constructor.
    */
   public void testLevelNode() {
      // (1) null as node name -> WRONG!
      try {
         createLevelNode(null, 0);
         fail();
      } catch (NodeParameterOutOfRangeException e) {
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'nodeName' must not be null.", e.getMessage());
      }
      
      // (2) too small start value -> WRONG!
      try {
         createLevelNode(INITIAL_NODE_NAME, LevelNode.MIN_START_VALUE - 0.0001);
         fail();
      } catch (NodeParameterOutOfRangeException e) {
         // do nothing
      }
      
      // (3) start value exactly at the lower border -> CORRECT!
      createLevelNode(INITIAL_NODE_NAME, LevelNode.MIN_START_VALUE);
      
      // (4) too large constant value -> WRONG
      try {
         createLevelNode(INITIAL_NODE_NAME, LevelNode.MAX_START_VALUE + 0.0001);
         fail();
      } catch (NodeParameterOutOfRangeException e) {
         // do nothing
      }
      
      // (5) start value exactly at the upper border -> CORRECT!
      createLevelNode(INITIAL_NODE_NAME, LevelNode.MAX_START_VALUE);
      
      // (6) correct parameters -> CORRECT!
      createLevelNode(INITIAL_NODE_NAME, 0);
   }
   
   /**
    * Tests the methods
    * {@link de.uka.aifb.com.systemDynamics.model.LevelNode#getStartValue()} and
    * {@link de.uka.aifb.com.systemDynamics.model.LevelNode#setStartValue(double)}.
    */
   public void testGetAndSetStartValue() {
      assertEquals(levelNode.getStartValue(), INITIAL_START_VALUE);
      
      // (1) too small start value -> WRONG!
      try {
         PrivateAccessor.invoke(levelNode, "setStartValue", new Class[] { double.class },
                                new Object[] { LevelNode.MIN_START_VALUE - 0.0001 });
         fail();
      } catch (NodeParameterOutOfRangeException e) {
         // do nothing
      } catch (Throwable t) {
         fail();
      }
      assertEquals(levelNode.getStartValue(), INITIAL_START_VALUE);
      
      // (2) too large start value -> WRONG!
      try {
         PrivateAccessor.invoke(levelNode, "setStartValue", new Class[] { double.class },
                                new Object[] { LevelNode.MAX_START_VALUE + 0.0001 });
         fail();
      } catch (NodeParameterOutOfRangeException e) {
         // do nothing
      } catch (Throwable t) {
         fail();
      }
      assertEquals(levelNode.getStartValue(), INITIAL_START_VALUE);
      
      // (3) first correct value -> CORRECT!
      try {
         PrivateAccessor.invoke(levelNode, "setStartValue", new Class[] { double.class },
                                new Object[] { 0 });
      } catch (Throwable t) {
         fail();
      }
      assertEquals(0.0, levelNode.getStartValue());
      
      // (4) second correct value -> CORRECT!
      try {
         PrivateAccessor.invoke(levelNode, "setStartValue", new Class[] { double.class },
                                new Object[] { LevelNode.MIN_START_VALUE / 2 });
      } catch (Throwable t) {
         fail();
      }
      assertEquals(LevelNode.MIN_START_VALUE / 2, levelNode.getStartValue());
   }
   
   /**
    * Tests the methods
    * {@link de.uka.aifb.com.systemDynamics.model.LevelNode#addIncomingFlow(RateNode)},
    * {@link de.uka.aifb.com.systemDynamics.model.LevelNode#removeIncomingFlow(RateNode)}
    * and {@link de.uka.aifb.com.systemDynamics.model.LevelNode#getIncomingFlows()}.
    */
   public void testIncomingFlows() {
      // (1) no incoming flow at beginning
      assertTrue(levelNode.getIncomingFlows().isEmpty());
      
      // (2) add first incoming flow
      RateNodeMockObject firstRateNode = createRateNodeMockObject("Rate Node 1", 5);
      Boolean successful = null;
      try {
         successful =
            (Boolean)PrivateAccessor.invoke(levelNode, "addIncomingFlow",
                                            new Class[] { RateNode.class },
                                            new Object[] { firstRateNode });
      } catch (Throwable t) {
         fail();
      }
      assertTrue(successful.booleanValue());
      assertTrue(levelNode.getIncomingFlows().size() == 1);
      levelNode.getIncomingFlows().contains(firstRateNode);
      
      // (3) try to add first incoming flow for the second time
      successful = null;
      try {
         successful =
            (Boolean)PrivateAccessor.invoke(levelNode, "addIncomingFlow",
                                            new Class[] { RateNode.class },
                                            new Object[] { firstRateNode });
      } catch (Throwable t) {
         fail();
      }
      assertFalse(successful.booleanValue());
      assertTrue(levelNode.getIncomingFlows().size() == 1);
      levelNode.getIncomingFlows().contains(firstRateNode);
      
      // (4) add second incoming flow
      RateNodeMockObject secondRateNode = createRateNodeMockObject("Rate Node 2", 2);
      successful = null;
      try {
         successful =
            (Boolean)PrivateAccessor.invoke(levelNode, "addIncomingFlow",
                                            new Class[] { RateNode.class },
                                            new Object[] { secondRateNode });
      } catch (Throwable t) {
         fail();
      }
      assertTrue(successful.booleanValue());
      assertTrue(levelNode.getIncomingFlows().size() == 2);
      levelNode.getIncomingFlows().contains(firstRateNode);
      levelNode.getIncomingFlows().contains(secondRateNode);
      
      // (5) try to remove not existing incoming flow
      RateNodeMockObject thirdRateNode = createRateNodeMockObject("Rate Node 3", 3);
      successful = null;
      try {
         successful =
            (Boolean)PrivateAccessor.invoke(levelNode, "removeIncomingFlow",
                                            new Class[] { RateNode.class },
                                            new Object[] { thirdRateNode });
      } catch (Throwable t) {
         fail();
      }
      assertFalse(successful.booleanValue());
      assertTrue(levelNode.getIncomingFlows().size() == 2);
      levelNode.getIncomingFlows().contains(firstRateNode);
      levelNode.getIncomingFlows().contains(secondRateNode);
      
      // (6) remove first added incoming flow
      successful = null;
      try {
         successful =
            (Boolean)PrivateAccessor.invoke(levelNode, "removeIncomingFlow",
                                            new Class[] { RateNode.class },
                                            new Object[] { firstRateNode });
      } catch (Throwable t) {
         fail();
      }
      assertTrue(successful.booleanValue());
      assertTrue(levelNode.getIncomingFlows().size() == 1);
      levelNode.getIncomingFlows().contains(secondRateNode);
      
      // (7) check that 'getIncomingFlows()' returns only a clone of the set
      HashSet<RateNode> incomingFlows = levelNode.getIncomingFlows();
      incomingFlows.add(thirdRateNode);
      assertTrue(incomingFlows.size() == 2);
      assertTrue(levelNode.getIncomingFlows().size() == 1);
      
      // (8) parameter not 'null' for method 'addIncomingFlow(RateNode)'
      try {
         PrivateAccessor.invoke(levelNode, "addIncomingFlow", new Class[] { RateNode.class },
                                new Object[] { null });
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'incomingFlow' must not be null.", e.getMessage());
      } catch (Throwable t) {
         fail();
      }
      
      // (9) parameter not 'null' for method 'removeIncomingFlow(RateNode)'
      try {
         PrivateAccessor.invoke(levelNode, "removeIncomingFlow", new Class[] { RateNode.class },
                                new Object[] { null });
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'incomingFlow' must not be null.", e.getMessage());
      } catch (Throwable t) {
         fail();
      }
   }
   
   /**
    * Tests the methods
    * {@link de.uka.aifb.com.systemDynamics.model.LevelNode#addOutgoingFlow(RateNode)},
    * {@link de.uka.aifb.com.systemDynamics.model.LevelNode#removeOutgoingFlow(RateNode)}
    * and {@link de.uka.aifb.com.systemDynamics.model.LevelNode#getOutgoingFlows()}.
    */
   public void testOutgoingFlows() {
      // (1) no outgoing flow at beginning
      assertTrue(levelNode.getOutgoingFlows().isEmpty());
      
      // (2) add first outgoing flow
      RateNodeMockObject firstRateNode = createRateNodeMockObject("Rate Node 1", 5);
      Boolean successful = null;
      try {
         successful =
            (Boolean)PrivateAccessor.invoke(levelNode, "addOutgoingFlow",
                                            new Class[] { RateNode.class },
                                            new Object[] { firstRateNode });
      } catch (Throwable t) {
         fail();
      }
      assertTrue(successful.booleanValue());
      assertTrue(levelNode.getOutgoingFlows().size() == 1);
      levelNode.getOutgoingFlows().contains(firstRateNode);
      
      // (3) try to add first outgoing flow for the second time
      successful = null;
      try {
         successful =
            (Boolean)PrivateAccessor.invoke(levelNode, "addOutgoingFlow",
                                            new Class[] { RateNode.class },
                                            new Object[] { firstRateNode });
      } catch (Throwable t) {
         fail();
      }
      assertFalse(successful.booleanValue());
      assertTrue(levelNode.getOutgoingFlows().size() == 1);
      levelNode.getOutgoingFlows().contains(firstRateNode);
      
      // (4) add second outgoing flow
      RateNodeMockObject secondRateNode = createRateNodeMockObject("Rate Node 2", 2);
      successful = null;
      try {
         successful =
            (Boolean)PrivateAccessor.invoke(levelNode, "addOutgoingFlow",
                                            new Class[] { RateNode.class },
                                            new Object[] { secondRateNode });
      } catch (Throwable t) {
         fail();
      }
      assertTrue(successful.booleanValue());
      assertTrue(levelNode.getOutgoingFlows().size() == 2);
      levelNode.getOutgoingFlows().contains(firstRateNode);
      levelNode.getOutgoingFlows().contains(secondRateNode);
      
      // (5) try to remove not existing outgoing flow
      RateNodeMockObject thirdRateNode = createRateNodeMockObject("Rate Node 3", 3);
      successful = null;
      try {
         successful =
            (Boolean)PrivateAccessor.invoke(levelNode, "removeOutgoingFlow",
                                            new Class[] { RateNode.class },
                                            new Object[] { thirdRateNode });
      } catch (Throwable t) {
         fail();
      }
      assertFalse(successful.booleanValue());
      assertTrue(levelNode.getOutgoingFlows().size() == 2);
      levelNode.getOutgoingFlows().contains(firstRateNode);
      levelNode.getOutgoingFlows().contains(secondRateNode);
      
      // (6) remove first added outgoing flow
      successful = null;
      try {
         successful =
            (Boolean)PrivateAccessor.invoke(levelNode, "removeOutgoingFlow",
                                            new Class[] { RateNode.class },
                                            new Object[] { firstRateNode });
      } catch (Throwable t) {
         fail();
      }
      assertTrue(successful.booleanValue());
      assertTrue(levelNode.getOutgoingFlows().size() == 1);
      levelNode.getOutgoingFlows().contains(secondRateNode);
      
      // (7) check that 'getOutgoingFlows()' returns only a clone of the set
      HashSet<RateNode> outgoingFlows = levelNode.getOutgoingFlows();
      outgoingFlows.add(thirdRateNode);
      assertTrue(outgoingFlows.size() == 2);
      assertTrue(levelNode.getOutgoingFlows().size() == 1);
      
      // (8) parameter not 'null' for method 'addOutgoingFlow(RateNode)'
      try {
         PrivateAccessor.invoke(levelNode, "addOutgoingFlow", new Class[] { RateNode.class },
                                new Object[] { null });
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'outgoingFlow' must not be null.", e.getMessage());
      } catch (Throwable t) {
         fail();
      }
      
      // (9) parameter not 'null' for method 'removeOutgoingFlow(RateNode)'
      try {
         PrivateAccessor.invoke(levelNode, "removeOutgoingFlow", new Class[] { RateNode.class },
                                new Object[] { null });
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'outgoingFlow' must not be null.", e.getMessage());
      } catch (Throwable t) {
         fail();
      }
   }
   
   /**
    * Tests the methods
    * {@link de.uka.aifb.com.systemDynamics.model.LevelNode#getNodeName()} and
    * {@link de.uka.aifb.com.systemDynamics.model.LevelNode#setNodeName(String)}.
    */
   public void testGetAndSetNodeName() {
      assertEquals(levelNode.getNodeName(), INITIAL_NODE_NAME);
      
      // (1) null as node name -> WRONG!
      try {
         PrivateAccessor.invoke(levelNode, "setNodeName", new Class[] { String.class },
                                new Object[] { null });
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'nodeName' must not be null.", e.getMessage());
      } catch (Throwable t) {
         fail();
      }
      assertEquals(levelNode.getNodeName(), INITIAL_NODE_NAME);
      
      // (2) first correct new node name -> CORRECT!
      try {
         PrivateAccessor.invoke(levelNode, "setNodeName", new Class[] { String.class },
                                new Object[] { "New Node Name 1" });
      } catch (Throwable t) {
         fail();
      }
      assertEquals(levelNode.getNodeName(), "New Node Name 1");
      
      // (3) second correct value -> CORRECT!
      try {
         PrivateAccessor.invoke(levelNode, "setNodeName", new Class[] { String.class },
                                new Object[] { "New Node Name 2" });
      } catch (Throwable t) {
         fail();
      }
      assertEquals(levelNode.getNodeName(), "New Node Name 2");
   }
   
   /**
    * Tests the methods {@link de.uka.aifb.com.systemDynamics.model.LevelNode#computeNextValue()}
    * and {@link de.uka.aifb.com.systemDynamics.model.LevelNode#getCurrentValue()}.
    */
   public void testComputeNextValueAndGetCurrentValue() {
      assertEquals(INITIAL_START_VALUE, levelNode.getCurrentValue());
      
      // add two incoming flows with stable flow 5
      RateNodeMockObject firstIncomingRateNode = createRateNodeMockObject("Rate Node In 1", 1);
      try {
         PrivateAccessor.invoke(levelNode, "addIncomingFlow", new Class[] { RateNode.class },
                                new Object[] { firstIncomingRateNode });
      } catch (Throwable t) {
         fail();
      }
      
      RateNodeMockObject secondIncomingRateNode = createRateNodeMockObject("Rate Node In 2", 4);
      try {
         PrivateAccessor.invoke(levelNode, "addIncomingFlow", new Class[] { RateNode.class },
                                new Object[] { secondIncomingRateNode });
      } catch (Throwable t) {
         fail();
      }
      
      // add two outgoing flows with stable flow 3
      RateNodeMockObject firstOutgoingRateNode = createRateNodeMockObject("Rate Node Out 1", 1);
      try {
         PrivateAccessor.invoke(levelNode, "addOutgoingFlow", new Class[] { RateNode.class },
                                new Object[] { firstOutgoingRateNode });
      } catch (Throwable t) {
         fail();
      }
      
      RateNodeMockObject secondOutgoingRateNode = createRateNodeMockObject("Rate Node Out 2", 2);
      try {
         PrivateAccessor.invoke(levelNode, "addOutgoingFlow", new Class[] { RateNode.class },
                                new Object[] { secondOutgoingRateNode });
      } catch (Throwable t) {
         fail();
      }
      
      try {
         PrivateAccessor.invoke(levelNode, "computeNextValue", null, null);
      } catch (Throwable t) {
         fail();
      }
      assertEquals(INITIAL_START_VALUE + 2, levelNode.getCurrentValue());
      
      try {
         PrivateAccessor.invoke(levelNode, "computeNextValue", null, null);
      } catch (Throwable t) {
         fail();
      }
      assertEquals(INITIAL_START_VALUE + 2 + 2, levelNode.getCurrentValue());
   }
   
   /**
    * Tests the method {@link de.uka.aifb.com.systemDynamics.model.LevelNode#evaluate()}.
    */
   public void testEvaluate() {
      assertEquals(INITIAL_START_VALUE, levelNode.evaluate());
      
      // add two incoming flows with stable flow 5
      RateNodeMockObject firstIncomingRateNode = createRateNodeMockObject("Rate Node In 1", 1);
      try {
         PrivateAccessor.invoke(levelNode, "addIncomingFlow", new Class[] { RateNode.class },
                                new Object[] { firstIncomingRateNode });
      } catch (Throwable t) {
         fail();
      }
      
      RateNodeMockObject secondIncomingRateNode = createRateNodeMockObject("Rate Node In 2", 4);
      try {
         PrivateAccessor.invoke(levelNode, "addIncomingFlow", new Class[] { RateNode.class },
                                new Object[] { secondIncomingRateNode });
      } catch (Throwable t) {
         fail();
      }
      
      // add two outgoing flows with stable flow 3
      RateNodeMockObject firstOutgoingRateNode = createRateNodeMockObject("Rate Node Out 1", 1);
      try {
         PrivateAccessor.invoke(levelNode, "addOutgoingFlow", new Class[] { RateNode.class },
                                new Object[] { firstOutgoingRateNode });
      } catch (Throwable t) {
         fail();
      }
      
      RateNodeMockObject secondOutgoingRateNode = createRateNodeMockObject("Rate Node Out 2", 2);
      try {
         PrivateAccessor.invoke(levelNode, "addOutgoingFlow", new Class[] { RateNode.class },
                                new Object[] { secondOutgoingRateNode });
      } catch (Throwable t) {
         fail();
      }
      
      try {
         PrivateAccessor.invoke(levelNode, "computeNextValue", null, null);
      } catch (Throwable t) {
         fail();
      }
      assertEquals(levelNode.getCurrentValue(), levelNode.evaluate());
      
      try {
         PrivateAccessor.invoke(levelNode, "computeNextValue", null, null);
      } catch (Throwable t) {
         fail();
      }
      assertEquals(levelNode.getCurrentValue(), levelNode.evaluate());
   }
   
   /**
    * Tests the method
    * {@link de.uka.aifb.com.systemDynamics.model.LevelNode#getAllNodesInASTSubtree()}.
    */
   public void testGetAllNodesInASTSubtree() {
      HashSet<AbstractNode> nodeSet = levelNode.getAllNodesInASTSubtree();
      assertEquals(1, nodeSet.size());
      assertTrue(nodeSet.contains(levelNode));
   }
   
   /**
    * Tests the method
    * {@link de.uka.aifb.com.systemDynamics.model.LevelNode#getStringRepresentation()}.
    */
   public void testGetStringRepresentation() {
      assertEquals(INITIAL_NODE_NAME + "(LN)", levelNode.getStringRepresentation());
   }
   
   /**
    * Tests the method
    * {@link de.uka.aifb.com.systemDynamics.model.LevelNode#clone()}.
    */
   public void testClone() {
      assertTrue(levelNode == levelNode.clone());
   }
   
   /**
    * Tests the method
    * {@link de.uka.aifb.com.systemDynamics.model.LevelNode#iterator()}.
    */
   public void testIterator() {
      // (1) first iterator
      Iterator<ASTElement> iterator = levelNode.iterator();
      
      assertTrue(iterator.hasNext());
      assertTrue(iterator.next() == levelNode);
      assertFalse(iterator.hasNext());
      try {
         iterator.next();
      } catch (NoSuchElementException e) {
         // do nothing
      }
      
      // (2) second iterator
      iterator = levelNode.iterator();
      
      assertTrue(iterator.hasNext());
      assertTrue(iterator.next() == levelNode);
      assertFalse(iterator.hasNext());
      try {
         iterator.next();
      } catch (NoSuchElementException e) {
         // do nothing
      }
   }
   
   /**
    * Helper method for invoking the private constructor of class LevelNode.
    * 
    * @param nodeName node name
    * @param startValue start value
    * @return created new instance of class LevelNode
    * @throws NodeParameterOutOfRangeException if the start value is out of range
    */
   private LevelNode createLevelNode(String nodeName, double startValue)
         throws NodeParameterOutOfRangeException {
      LevelNode node = null;
      try {
         node =
            (LevelNode)PrivateAccessor.invoke(LevelNode.class,
                                              "createLevelNode",
                                              new Class[] { String.class, double.class },
                                              new Object[] { nodeName, startValue });
      } catch (NodeParameterOutOfRangeException e) {
         throw e;
      } catch (IllegalArgumentException e) { 
         throw e;
      } catch (Throwable t) {
         // no other error/exception other than IllegalArgumentException possible
         fail();
      }
      return node;
   }
   
   /**
    * Helper method for invoking the private constructor of class RateNodeMockObject.
    * 
    * @param nodeName node name
    * @param currentValue "current" value that will always be returned by the created instance's
    *                     'getCurrentValue()' method
    * @return created new instance of class RateNodeMockObject
    */
   private RateNodeMockObject createRateNodeMockObject(String nodeName, double currentValue) {
      RateNodeMockObject node = null;
      try {
         node =
            (RateNodeMockObject)PrivateAccessor.invoke(RateNodeMockObject.class,
                                                       "createRateNodeMockObject",
                                                       new Class[] { String.class, double.class },
                                                       new Object[] { nodeName, currentValue });
      } catch (IllegalArgumentException e) { 
         throw e;
      } catch (Throwable t) {
         // no other error/exception other than IllegalArgumentException possible
         fail();
      }
      return node;
   }
}