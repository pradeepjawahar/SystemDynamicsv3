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
import junit.framework.*;
import junitx.util.PrivateAccessor;

/**
 * This class implements a test case for the class
 * {@link de.uka.aifb.com.systemDynamics.model.RateNode}.
 * 
 * @author Joachim Melcher, University of Karlsruhe, AIFB
 * @version 1.0
 */
public class RateNodeTestCase extends TestCase {
   
   private static final String INITIAL_NODE_NAME = "NODE NAME";
   
   private RateNode rateNode;
   
   public static Test suite() {  
      return new TestSuite(RateNodeTestCase.class);
   }
   
   @Override
protected void setUp() throws Exception {
      rateNode = createRateNode(INITIAL_NODE_NAME);
   }

   @Override
protected void tearDown() throws Exception {
      rateNode = null;
   }
   
   /**
    * Tests the constructor.
    */
   public void testRateNode() {
      // (1) null as node name -> WRONG!
      try {
         createRateNode(null);
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'nodeName' must not be null.", e.getMessage());
      }
      
      // (2) correct parameter -> CORRECT!
      createRateNode(INITIAL_NODE_NAME);
   }
   
   /**
    * Tests the methods
    * {@link de.uka.aifb.com.systemDynamics.model.RateNode#setFlowSource(AbstractNode)},
    * {@link de.uka.aifb.com.systemDynamics.model.RateNode#removeFlowSource()} and
    * {@link de.uka.aifb.com.systemDynamics.model.RateNode#getFlowSource()}.
    */
   public void testFlowSoure() {
      // (1) no flow source at the beginning
      assertEquals(null, rateNode.getFlowSource());
      
      // (2) set flow source
      LevelNode firstFlowSource = createLevelNode("Level Node", 0);
      try {
         PrivateAccessor.invoke(rateNode, "setFlowSource",
                                new Class[] { AbstractNode.class },
                                new Object[] { firstFlowSource });
      } catch (Throwable t) {
         fail();
      }
      assertTrue(rateNode.getFlowSource() == firstFlowSource);
      
      // (3) set another source (overwrite old source)
      SourceSinkNode secondFlowSource = createSourceSinkNode();
      try {
         PrivateAccessor.invoke(rateNode, "setFlowSource",
                                new Class[] { AbstractNode.class },
                                new Object[] { secondFlowSource });
      } catch (Throwable t) {
         fail();
      }
      assertTrue(rateNode.getFlowSource() == secondFlowSource);
      
      // (4) remove flow source
      try {
         PrivateAccessor.invoke(rateNode, "removeFlowSource", null, null);
      } catch (Throwable t) {
         fail();
      }
      assertEquals(null, rateNode.getFlowSource());
      
      // (5) parameter not 'null' for method 'setFlowSource(LevelNode)'
      try {
         PrivateAccessor.invoke(rateNode, "setFlowSource", new Class[] { AbstractNode.class },
                                new Object[] { null });
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'sourceNode' must not be null.", e.getMessage());
      } catch (Throwable t) {
         fail();
      }
      
      // (6) only level or source/sink node
      try {
         PrivateAccessor.invoke(rateNode, "setFlowSource", new Class[] { AbstractNode.class },
                                new Object[] { createRateNode("DUMMY") });
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'sourceNode' must be a level or a source/sink node.", e.getMessage());
      } catch (Throwable t) {
         fail();
      }
   }
   
   /**
    * Tests the methods
    * {@link de.uka.aifb.com.systemDynamics.model.RateNode#setFlowSink(AbstractNode)},
    * {@link de.uka.aifb.com.systemDynamics.model.RateNode#removeFlowSink()} and
    * {@link de.uka.aifb.com.systemDynamics.model.RateNode#getFlowSink()}.
    */
   public void testFlowSink() {
      // (1) no flow sink at the beginning
      assertEquals(null, rateNode.getFlowSink());
      
      // (2) set flow sink
      LevelNode firstFlowSink = createLevelNode("Level Node", 0);
      try {
         PrivateAccessor.invoke(rateNode, "setFlowSink",
                                new Class[] { AbstractNode.class },
                                new Object[] { firstFlowSink });
      } catch (Throwable t) {
         fail();
      }
      assertTrue(rateNode.getFlowSink() == firstFlowSink);
      
      // (3) set another sink (overwrite old sink)
      SourceSinkNode secondFlowSink = createSourceSinkNode();
      try {
         PrivateAccessor.invoke(rateNode, "setFlowSink",
                                new Class[] { AbstractNode.class },
                                new Object[] { secondFlowSink });
      } catch (Throwable t) {
         fail();
      }
      assertTrue(rateNode.getFlowSink() == secondFlowSink);
      
      // (4) remove flow sink
      try {
         PrivateAccessor.invoke(rateNode, "removeFlowSink", null, null);
      } catch (Throwable t) {
         fail();
      }
      assertEquals(null, rateNode.getFlowSink());
      
      // (5) parameter not 'null' for method 'setFlowSink(LevelNode)'
      try {
         PrivateAccessor.invoke(rateNode, "setFlowSink", new Class[] { AbstractNode.class },
                                new Object[] { null });
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'sinkNode' must not be null.", e.getMessage());
      } catch (Throwable t) {
         fail();
      }
      
      // (6) only level or source/sink node
      try {
         PrivateAccessor.invoke(rateNode, "setFlowSink", new Class[] { AbstractNode.class },
                                new Object[] { createRateNode("DUMMY") });
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'sinkNode' must be a level or a source/sink node.", e.getMessage());
      } catch (Throwable t) {
         fail();
      }
   }
   
   /**
    * Tests the methods
    * {@link de.uka.aifb.com.systemDynamics.model.RateNode#setFormula(ASTElement)},
    * {@link de.uka.aifb.com.systemDynamics.model.RateNode#hasFormula()} and
    * {@link de.uka.aifb.com.systemDynamics.model.RateNode#getFormula()}.
    */
   public void testFormula() {
      LevelNode levelNode1 = createLevelNode("Level node 1", 0);
      LevelNode levelNode2 = createLevelNode("Level node 2", 0);
      LevelNode levelNode3 = createLevelNode("Level node 3", 0);
      
      // (1) no formula at beginning
      assertFalse(rateNode.hasFormula());
      
      // (2) set first formula...
      ASTPlus subFormula = new ASTPlus(levelNode2, levelNode2);
      ASTPlus firstFormula = new ASTPlus(levelNode1, subFormula);
      try {
         PrivateAccessor.invoke(rateNode, "setFormula", new Class[] { ASTElement.class },
                                new Object[] { firstFormula });
      } catch (Throwable t) {
         fail();
      }
      assertTrue(rateNode.hasFormula());
      
      // ... check that clone is stored...
      subFormula = new ASTPlus(levelNode3, levelNode3);
      ASTPlus internFormula = null;
      try {
         internFormula = (ASTPlus)PrivateAccessor.getField(rateNode, "formula");
      } catch (Throwable t) {
         fail();
      }
      ASTPlus subFormulaInInternFormula = null;
      try {
         subFormulaInInternFormula =
            (ASTPlus)PrivateAccessor.getField(internFormula, "rightElement");
      } catch (Throwable t) {
         fail();
      }
      LevelNode nodeInInternFormula = null;
      try {
         nodeInInternFormula =
            (LevelNode)PrivateAccessor.getField(subFormulaInInternFormula, "leftElement");
      } catch (Throwable t) {
         fail();
      }
      assertTrue(nodeInInternFormula == levelNode2);
      
      // ... and another clone is returned
      ASTPlus returnedFormula = (ASTPlus)rateNode.getFormula();
      ASTPlus newSubFormula = new ASTPlus(levelNode3, levelNode3);
      try {
         PrivateAccessor.setField(returnedFormula, "rightElement", newSubFormula);
      } catch (Throwable t) {
         fail();
      }
      internFormula = null;
      try {
         internFormula = (ASTPlus)PrivateAccessor.getField(rateNode, "formula");
      } catch (Throwable t) {
         fail();
      }
      subFormulaInInternFormula = null;
      try {
         subFormulaInInternFormula =
            (ASTPlus)PrivateAccessor.getField(internFormula, "rightElement");
      } catch (Throwable t) {
         fail();
      }
      nodeInInternFormula = null;
      try {
         nodeInInternFormula =
            (LevelNode)PrivateAccessor.getField(subFormulaInInternFormula, "leftElement");
      } catch (Throwable t) {
         fail();
      }
      assertTrue(nodeInInternFormula == levelNode2);
      
      
      // (3) set second formula
      try {
         PrivateAccessor.invoke(rateNode, "setFormula", new Class[] { ASTElement.class },
                                new Object[] { levelNode1 });
      } catch (Throwable t) {
         fail();
      }
      assertTrue(rateNode.hasFormula());
      assertTrue(rateNode.getFormula() == levelNode1);
      
      // (4) formula 'null'
      try {
         PrivateAccessor.invoke(rateNode, "setFormula", new Class[] { ASTElement.class },
                                new Object[] { null });
      } catch (Throwable t) {
         fail();
      }
      assertFalse(rateNode.hasFormula());
   }
   
   /**
    * Tests the method
    * {@link de.uka.aifb.com.systemDynamics.model.RateNode#getAllNodesThisOneDependsOn()}.
    */
   public void testGetAllNodesThisOneDependsOn() {
      // (1) without formula no dependencies
      assertTrue(rateNode.getAllNodesThisOneDependsOn().isEmpty());
      
      // (2) check dependencies with formula
      LevelNode levelNode1 = createLevelNode("Level node 1", 0);
      LevelNode levelNode2 = createLevelNode("Level node 2", 0);
      LevelNode levelNode3 = createLevelNode("Level node 3", 0);
      ASTPlus subFormula = new ASTPlus(levelNode2, levelNode3);
      ASTPlus firstFormula = new ASTPlus(levelNode1, subFormula);
      try {
         PrivateAccessor.invoke(rateNode, "setFormula", new Class[] { ASTElement.class },
                                new Object[] { firstFormula });
      } catch (Throwable t) {
         fail();
      }
      assertTrue(rateNode.getAllNodesThisOneDependsOn().size() == 3);
      assertTrue(rateNode.getAllNodesThisOneDependsOn().contains(levelNode1));
      assertTrue(rateNode.getAllNodesThisOneDependsOn().contains(levelNode2));
      assertTrue(rateNode.getAllNodesThisOneDependsOn().contains(levelNode3));
   }
   
   /**
    * Tests the method
    * {@link de.uka.aifb.com.systemDynamics.model.RateNode#getAllNodesThisOneDependsOnAndSourceSinkNodes()}.
    */
   public void testGetAllNodesThisOneDependsOnAndSourceSinkNodes() {
      // (1) without formula no dependencies and source/sink nodes
      assertTrue(rateNode.getAllNodesThisOneDependsOnAndSourceSinkNodes().isEmpty());
      
      // (2) without formula, but with a source node
      SourceSinkNode flowSource = createSourceSinkNode();
      try {
         PrivateAccessor.invoke(rateNode, "setFlowSource",
                                new Class[] { AbstractNode.class },
                                new Object[] { flowSource });
      } catch (Throwable t) {
         fail();
      }
      assertTrue(rateNode.getAllNodesThisOneDependsOnAndSourceSinkNodes().size() == 1);
      assertTrue(rateNode.getAllNodesThisOneDependsOnAndSourceSinkNodes().contains(flowSource));
      
      // (3) without formula, but with a source and sink node
      SourceSinkNode flowSink = createSourceSinkNode();
      try {
         PrivateAccessor.invoke(rateNode, "setFlowSink",
                                new Class[] { AbstractNode.class },
                                new Object[] { flowSink });
      } catch (Throwable t) {
         fail();
      }
      assertTrue(rateNode.getAllNodesThisOneDependsOnAndSourceSinkNodes().size() == 2);
      assertTrue(rateNode.getAllNodesThisOneDependsOnAndSourceSinkNodes().contains(flowSource));
      assertTrue(rateNode.getAllNodesThisOneDependsOnAndSourceSinkNodes().contains(flowSink));
      
      // (4) with formula and source and sink node
      LevelNode levelNode1 = createLevelNode("Level node 1", 0);
      LevelNode levelNode2 = createLevelNode("Level node 2", 0);
      LevelNode levelNode3 = createLevelNode("Level node 3", 0);
      ASTPlus subFormula = new ASTPlus(levelNode2, levelNode3);
      ASTPlus firstFormula = new ASTPlus(levelNode1, subFormula);
      try {
         PrivateAccessor.invoke(rateNode, "setFormula", new Class[] { ASTElement.class },
                                new Object[] { firstFormula });
      } catch (Throwable t) {
         fail();
      }
      assertTrue(rateNode.getAllNodesThisOneDependsOnAndSourceSinkNodes().size() == 5);
      assertTrue(rateNode.getAllNodesThisOneDependsOnAndSourceSinkNodes().contains(flowSource));
      assertTrue(rateNode.getAllNodesThisOneDependsOnAndSourceSinkNodes().contains(flowSink));
      assertTrue(rateNode.getAllNodesThisOneDependsOnAndSourceSinkNodes().contains(levelNode1));
      assertTrue(rateNode.getAllNodesThisOneDependsOnAndSourceSinkNodes().contains(levelNode2));
      assertTrue(rateNode.getAllNodesThisOneDependsOnAndSourceSinkNodes().contains(levelNode3));
      
      // (5) overwrite flow source with level node
      try {
         PrivateAccessor.invoke(rateNode, "setFlowSource",
                                new Class[] { AbstractNode.class },
                                new Object[] { createLevelNode("DUMMY", 0) });
      } catch (Throwable t) {
         fail();
      }
      assertTrue(rateNode.getAllNodesThisOneDependsOnAndSourceSinkNodes().size() == 4);
      assertTrue(rateNode.getAllNodesThisOneDependsOnAndSourceSinkNodes().contains(flowSink));
      assertTrue(rateNode.getAllNodesThisOneDependsOnAndSourceSinkNodes().contains(levelNode1));
      assertTrue(rateNode.getAllNodesThisOneDependsOnAndSourceSinkNodes().contains(levelNode2));
      assertTrue(rateNode.getAllNodesThisOneDependsOnAndSourceSinkNodes().contains(levelNode3));
      
      // (5) overwrite flow sink with level node
      try {
         PrivateAccessor.invoke(rateNode, "setFlowSink",
                                new Class[] { AbstractNode.class },
                                new Object[] { createLevelNode("DUMMY", 0) });
      } catch (Throwable t) {
         fail();
      }
      assertTrue(rateNode.getAllNodesThisOneDependsOnAndSourceSinkNodes().size() == 3);
      assertTrue(rateNode.getAllNodesThisOneDependsOnAndSourceSinkNodes().contains(levelNode1));
      assertTrue(rateNode.getAllNodesThisOneDependsOnAndSourceSinkNodes().contains(levelNode2));
      assertTrue(rateNode.getAllNodesThisOneDependsOnAndSourceSinkNodes().contains(levelNode3));
   }
   
   /**
    * Tests the methods {@link de.uka.aifb.com.systemDynamics.model.RateNode#computeNextValue()}
    * and {@link de.uka.aifb.com.systemDynamics.model.RateNode#getCurrentValue()}.
    */
   public void testComputeNextValueAndGetCurrentValue() {
      assertEquals(0.0, rateNode.getCurrentValue());
      
      // set formula (constant 5)
      ConstantNode formula = null;
      try {
         formula = (ConstantNode)PrivateAccessor.invoke(ConstantNode.class,
                                                        "createConstantNode",
                                                        new Class[] { String.class, double.class },
                                                        new Object[] { "Level node", 5 });
         PrivateAccessor.invoke(rateNode, "setFormula", new Class[] { ASTElement.class },
                                new Object[] { formula });
      } catch (Throwable t) {
         fail();
      }
      
      // test two time steps
      try {
         PrivateAccessor.invoke(rateNode, "computeNextValue", null, null);
      } catch (Throwable t) {
         fail();
      }
      assertEquals(5.0, rateNode.getCurrentValue());
      
      try {
         PrivateAccessor.invoke(rateNode, "computeNextValue", null, null);
      } catch (Throwable t) {
         fail();
      }
      assertEquals(5.0, rateNode.getCurrentValue());
   }
   
   /**
    * Tests the methods
    * {@link de.uka.aifb.com.systemDynamics.model.RateNode#getNodeName()} and
    * {@link de.uka.aifb.com.systemDynamics.model.RateNode#setNodeName(String)}.
    */
   public void testGetAndSetNodeName() {
      assertEquals(rateNode.getNodeName(), INITIAL_NODE_NAME);
      
      // (1) null as node name -> WRONG!
      try {
         PrivateAccessor.invoke(rateNode, "setNodeName", new Class[] { String.class },
                                new Object[] { null });
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'nodeName' must not be null.", e.getMessage());
      } catch (Throwable t) {
         fail();
      }
      assertEquals(rateNode.getNodeName(), INITIAL_NODE_NAME);
      
      // (2) first correct new node name -> CORRECT!
      try {
         PrivateAccessor.invoke(rateNode, "setNodeName", new Class[] { String.class },
                                new Object[] { "New Node Name 1" });
      } catch (Throwable t) {
         fail();
      }
      assertEquals(rateNode.getNodeName(), "New Node Name 1");
      
      // (3) second correct value -> CORRECT!
      try {
         PrivateAccessor.invoke(rateNode, "setNodeName", new Class[] { String.class },
                                new Object[] { "New Node Name 2" });
      } catch (Throwable t) {
         fail();
      }
      assertEquals(rateNode.getNodeName(), "New Node Name 2");
   }
   
   /**
    * Helper method for invoking the private constructor of class RateNode.
    * 
    * @param nodeName node name
    * @return created new instance of class RateNode
    */
   private RateNode createRateNode(String nodeName) {
      RateNode node = null;
      try {
         node =
            (RateNode)PrivateAccessor.invoke(RateNode.class, "createRateNode",
                                             new Class[] { String.class },
                                             new Object[] { nodeName });
      } catch (IllegalArgumentException e) { 
         throw e;
      } catch (Throwable t) {
         // no other error/exception other than IllegalArgumentException possible
         fail();
      }
      return node;
   }
   
   /**
    * Helper method for invoking the private constructor of class LevelNode.
    * 
    * @param nodeName node name
    * @param startValue start value
    * @return created new instance of class LevelNode
    */
   private LevelNode createLevelNode(String nodeName, double startValue) {
      LevelNode node = null;
      try {
         node =
            (LevelNode)PrivateAccessor.invoke(LevelNode.class,
                                              "createLevelNode",
                                              new Class[] { String.class, double.class },
                                              new Object[] { nodeName, startValue });
      } catch (IllegalArgumentException e) { 
         throw e;
      } catch (Throwable t) {
         // no other error/exception other than IllegalArgumentException possible
         fail();
      }
      return node;
   }
   
   /**
    * Helper method for invoking the private constructor of class SourceSinkNode.
    * 
    * @return created new instance of class SourceSinkNode
    */
   private SourceSinkNode createSourceSinkNode() {
      SourceSinkNode node = null;
      try {
         node =
            (SourceSinkNode)PrivateAccessor.invoke(SourceSinkNode.class, "createSourceSinkNode",
                                                   null, null);
      } catch (IllegalArgumentException e) { 
         throw e;
      } catch (Throwable t) {
         // no other error/exception other than IllegalArgumentException possible
         fail();
      }
      return node;
   }
}