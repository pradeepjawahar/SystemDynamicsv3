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
 * {@link de.uka.aifb.com.systemDynamics.model.Model}.
 * 
 * @author Joachim Melcher, University of Karlsruhe, AIFB
 * @version 1.0
 */
public class ModelTestCase extends TestCase {
   
   private static final String MODEL_NAME = "Model name";
   
   private Model model;

   public static Test suite() {  
      return new TestSuite(ModelTestCase.class);
   }
   
   protected void setUp() throws Exception {
      model = new Model();
      model.setModelName(MODEL_NAME);
   }

   protected void tearDown() throws Exception {
      model = null;
   }
   
   /**
    * Tests the method {@link de.uka.aifb.com.systemDynamics.model.Model#setModelName(String)}.
    */
   public void testsetModelName() {
      // (1) parameter 'null' -> WRONG
      try {
         model.setModelName(null);
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'modelName' must not be null.", e.getMessage());
      }
      
      // (2) correct parameter -> CORRECT
      model.setModelName("Model name");
      
      // (3) set model name while model is unchangeable -> WRONG
      model.createLevelNode("Level node", 0);
      try {
         model.validateModelAndSetUnchangeable();
      } catch (Exception e) {
         fail();
      }
      
      try {
         model.setModelName("New model name");
         fail();
      } catch (ModelNotChangeableException e) {
         // do nothing
      }
   }
   
   /**
    * Tests the method
    * {@link de.uka.aifb.com.systemDynamics.model.Model#getModelName()}.
    */
   public void testGetModelName() {
      assertEquals(MODEL_NAME, model.getModelName());      
   }
   
   /**
    * Tests the methods
    * {@link de.uka.aifb.com.systemDynamics.model.Model#createLevelNode(String, double)}
    * and {@link de.uka.aifb.com.systemDynamics.model.Model#getLevelNodes()}.
    */
   public void testCreateLevelNodeAndGetLevelNodes() {
      // (1) check that set of level nodes is empty at the beginning
      assertTrue(model.getLevelNodes().isEmpty());
      
      // (2) create first level node
      LevelNode levelNode1 = model.createLevelNode("Level node 1", 0);
      
      // (3) check that first level node is in set of level nodes
      assertTrue(model.getLevelNodes().size() == 1);
      assertTrue(model.getLevelNodes().contains(levelNode1));
      
      // (4) check that cloned level node set is returned
      HashSet<LevelNode> levelNodes = model.getLevelNodes();
      LevelNode levelNode = null;
      try {
         levelNode = (LevelNode)PrivateAccessor.invoke(LevelNode.class, "createLevelNode",
                                                       new Class[] { String.class, double.class },
                                                       new Object[] { "A", 0 });
      } catch (Throwable t) {
         fail();
      }
      levelNodes.add(levelNode);
      assertTrue(model.getLevelNodes().size() == 1);
      
      // (5) create second level node
      LevelNode levelNode2 = model.createLevelNode("Level node 2", 0);
      
      // (6) check that first and second level nodes are in set of level nodes
      assertTrue(model.getLevelNodes().size() == 2);
      assertTrue(model.getLevelNodes().contains(levelNode1));
      assertTrue(model.getLevelNodes().contains(levelNode2));
      
      // (7) check that no new level node can be created after model was set unchangeable
      try {
         PrivateAccessor.setField(model, "isChangeable", Boolean.FALSE);
      } catch (NoSuchFieldException e) {
         fail();
      }
      
      try {
         model.createLevelNode("Level node 3", 0);
         fail();
      } catch (ModelNotChangeableException e) {
         // do nothing
      }
   }
   
   /**
    * Tests the methods
    * {@link de.uka.aifb.com.systemDynamics.model.Model#createRateNode(String)}
    * and {@link de.uka.aifb.com.systemDynamics.model.Model#getRateNodes()}.
    */
   public void testCreateRateNodeAndGetRateNodes() {
      // (1) check that set of rate nodes is empty at the beginning
      assertTrue(model.getRateNodes().isEmpty());
      
      // (2) create first rate node
      RateNode rateNode1 = model.createRateNode("Rate node 1");
      
      // (3) check that first rate node is in set of rate nodes
      assertTrue(model.getRateNodes().size() == 1);
      assertTrue(model.getRateNodes().contains(rateNode1));
      
      // (4) check that cloned rate node set is returned
      HashSet<RateNode> rateNodes = model.getRateNodes();
      RateNode rateNode = null;
      try {
         rateNode = (RateNode)PrivateAccessor.invoke(RateNode.class, "createRateNode",
                                                     new Class[] { String.class },
                                                     new Object[] { "A" });
      } catch (Throwable t) {
         fail();
      }
      rateNodes.add(rateNode);
      assertTrue(model.getRateNodes().size() == 1);
      
      // (5) create second rate node
      RateNode rateNode2 = model.createRateNode("Rate node 2");
      
      // (6) check that first and second rate nodes are in set of rate nodes
      assertTrue(model.getRateNodes().size() == 2);
      assertTrue(model.getRateNodes().contains(rateNode1));
      assertTrue(model.getRateNodes().contains(rateNode2));
      
      // (7) check that no new rate node can be created after model was set unchangeable
      try {
         PrivateAccessor.setField(model, "isChangeable", Boolean.FALSE);
      } catch (NoSuchFieldException e) {
         fail();
      }
      
      try {
         model.createRateNode("Rate node 3");
         fail();
      } catch (ModelNotChangeableException e) {
         // do nothing
      }
   }
   
   /**
    * Tests the methods
    * {@link de.uka.aifb.com.systemDynamics.model.Model#createConstantNode(String, double)}
    * and {@link de.uka.aifb.com.systemDynamics.model.Model#getConstantNodes()}.
    */
   public void testCreateConstantNodeAndGetConstantNodes() {
      // (1) check that set of constant nodes is empty at the beginning
      assertTrue(model.getConstantNodes().isEmpty());
      
      // (2) create first constant node
      ConstantNode constantNode1 = model.createConstantNode("Constant node 1", 0);
      
      // (3) check that first constant node is in set of constant nodes
      assertTrue(model.getConstantNodes().size() == 1);
      assertTrue(model.getConstantNodes().contains(constantNode1));
      
      // (4) check that cloned constant node set is returned
      HashSet<ConstantNode> constantNodes = model.getConstantNodes();
      ConstantNode constantNode = null;
      try {
         constantNode = (ConstantNode)PrivateAccessor.invoke(ConstantNode.class,
                                                             "createConstantNode",
                                                             new Class[] { String.class, double.class },
                                                             new Object[] { "A", 0 });
      } catch (Throwable t) {
         fail();
      }
      constantNodes.add(constantNode);
      assertTrue(model.getConstantNodes().size() == 1);
      
      // (5) create second constant node
      ConstantNode constantNode2 = model.createConstantNode("Constant node 2", 0);
      
      // (6) check that first and second constant nodes are in set of constant nodes
      assertTrue(model.getConstantNodes().size() == 2);
      assertTrue(model.getConstantNodes().contains(constantNode1));
      assertTrue(model.getConstantNodes().contains(constantNode2));
      
      // (7) check that no new constant node can be created after model was set unchangeable
      try {
         PrivateAccessor.setField(model, "isChangeable", Boolean.FALSE);
      } catch (NoSuchFieldException e) {
         fail();
      }
      
      try {
         model.createConstantNode("Constant node 3", 0);
         fail();
      } catch (ModelNotChangeableException e) {
         // do nothing
      }
   }
   
   /**
    * Tests the methods
    * {@link de.uka.aifb.com.systemDynamics.model.Model#createAuxiliaryNode(String)}
    * and {@link de.uka.aifb.com.systemDynamics.model.Model#getAuxiliaryNodes()}.
    */
   public void testCreateAuxiliaryNodeAndGetAuxiliaryNodes() {
      // (1) check that set of auxiliary nodes is empty at the beginning
      assertTrue(model.getAuxiliaryNodes().isEmpty());
      
      // (2) create first auxiliary node
      AuxiliaryNode auxiliaryNode1 = model.createAuxiliaryNode("Auxiliary node 1");
      
      // (3) check that first auxiliary node is in set of auxiliary nodes
      assertTrue(model.getAuxiliaryNodes().size() == 1);
      assertTrue(model.getAuxiliaryNodes().contains(auxiliaryNode1));
      
      // (4) check that cloned auxiliary node set is returned
      HashSet<AuxiliaryNode> auxiliaryNodes = model.getAuxiliaryNodes();
      AuxiliaryNode auxiliaryNode = null;
      try {
         auxiliaryNode = (AuxiliaryNode)PrivateAccessor.invoke(AuxiliaryNode.class,
                                                               "createAuxiliaryNode",
                                                               new Class[] { String.class },
                                                               new Object[] { "A" });
      } catch (Throwable t) {
         fail();
      }
      auxiliaryNodes.add(auxiliaryNode);
      assertTrue(model.getAuxiliaryNodes().size() == 1);
      
      // (5) create second auxiliary node
      AuxiliaryNode auxiliaryNode2 = model.createAuxiliaryNode("Auxiliary node 2");
      
      // (6) check that first and second auxiliary nodes are in set of auxiliary nodes
      assertTrue(model.getAuxiliaryNodes().size() == 2);
      assertTrue(model.getAuxiliaryNodes().contains(auxiliaryNode1));
      assertTrue(model.getAuxiliaryNodes().contains(auxiliaryNode2));
      
      // (7) check that no new auxiliary node can be created after model was set unchangeable
      try {
         PrivateAccessor.setField(model, "isChangeable", Boolean.FALSE);
      } catch (NoSuchFieldException e) {
         fail();
      }
      
      try {
         model.createAuxiliaryNode("Auxiliary node 3");
         fail();
      } catch (ModelNotChangeableException e) {
         // do nothing
      }
   }
   
   /**
    * Tests the methods
    * {@link de.uka.aifb.com.systemDynamics.model.Model#createSourceSinkNode()}
    * and {@link de.uka.aifb.com.systemDynamics.model.Model#getSourceSinkNodes()}.
    */
   public void testCreateSourceSinkNodeAndGetSourceSinkNodes() {
      // (1) check that set of source/sink nodes is empty at the beginning
      assertTrue(model.getSourceSinkNodes().isEmpty());
      
      // (2) create first source/sink node
      SourceSinkNode sourceSinkNode1 = model.createSourceSinkNode();
      
      // (3) check that first source/sink node is in set of source/sink nodes
      assertTrue(model.getSourceSinkNodes().size() == 1);
      assertTrue(model.getSourceSinkNodes().contains(sourceSinkNode1));
      
      // (4) check that cloned source/sink node set is returned
      HashSet<SourceSinkNode> sourceSinkNodes = model.getSourceSinkNodes();
      SourceSinkNode sourceSinkNode = null;
      try {
         sourceSinkNode = (SourceSinkNode)PrivateAccessor.invoke(SourceSinkNode.class,
                                                               "createSourceSinkNode", null, null);
      } catch (Throwable t) {
         fail();
      }
      sourceSinkNodes.add(sourceSinkNode);
      assertTrue(model.getSourceSinkNodes().size() == 1);
      
      // (5) create second source/sink node
      SourceSinkNode sourceSinkNode2 = model.createSourceSinkNode();
      
      // (6) check that first and second source/sink nodes are in set of source/sink nodes
      assertTrue(model.getSourceSinkNodes().size() == 2);
      assertTrue(model.getSourceSinkNodes().contains(sourceSinkNode1));
      assertTrue(model.getSourceSinkNodes().contains(sourceSinkNode2));
      
      // (7) check that no new source/sink node can be created after model was set unchangeable
      try {
         PrivateAccessor.setField(model, "isChangeable", Boolean.FALSE);
      } catch (NoSuchFieldException e) {
         fail();
      }
      
      try {
         model.createSourceSinkNode();
         fail();
      } catch (ModelNotChangeableException e) {
         // do nothing
      }
   }
   
   /**
    * Tests the method
    * {@link de.uka.aifb.com.systemDynamics.model.Model#removeNode(AbstractNode)}.
    */
   public void testRemoveNode1() {
      // (1) parameter 'null' -> WRONG
      try {
         model.removeNode(null);
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'node' must not be null.", e.getMessage());
      } catch (FormulaDependencyException e) {
         fail();
      }
      
      // (2) check that no node can be removed after model was set unchangeable
      LevelNode levelNode = model.createLevelNode("Level node", 0);
      
      try {
         PrivateAccessor.setField(model, "isChangeable", Boolean.FALSE);
      } catch (NoSuchFieldException e) {
         fail();
      }
      
      try {
         model.removeNode(levelNode);
         fail();
      } catch (ModelNotChangeableException e) {
         // do nothing
      } catch (FormulaDependencyException e) {
         fail();
      }
   }
   
   /**
    * Tests the method
    * {@link de.uka.aifb.com.systemDynamics.model.Model#removeNode(AbstractNode)}.
    */
   public void testRemoveNode2() {
      // create model
      LevelNode levelNode1 = model.createLevelNode("Level node 1", 0);
      LevelNode levelNode2 = model.createLevelNode("Level node 2", 0);
      RateNode rateNode1 = model.createRateNode("Rate node 1");
      RateNode rateNode2 = model.createRateNode("Rate node 2");
      ConstantNode constantNode1 = model.createConstantNode("Constant node 1", 0);
      ConstantNode constantNode2 = model.createConstantNode("Constant node 2", 0);
      AuxiliaryNode auxiliaryNode1 = model.createAuxiliaryNode("Auxiliary node 1");
      AuxiliaryNode auxiliaryNode2 = model.createAuxiliaryNode("Auxiliary node 2");
      
      model.addFlowFromRateNode2LevelNode(rateNode1, levelNode1);
      model.addFlowFromLevelNode2RateNode(levelNode1, rateNode2);
      model.addFlowFromRateNode2LevelNode(rateNode2, levelNode2);
      model.setFormula(rateNode1, constantNode1);
      model.setFormula(rateNode2, new ASTPlus(auxiliaryNode2, levelNode2));
      
      // remove 'constantNode1' -> WRONG
      try {
         model.removeNode(constantNode1);
         fail();
      } catch (FormulaDependencyException e) {
         assertEquals("The node that should be removed is part of another node's formula.",
                      e.getMessage());
         assertTrue(e.getNodeWithProblematicFormula() == rateNode1);
      }
      
      // remove 'constantNode2' -> CORRECT
      try {
         model.removeNode(constantNode2);
      } catch (FormulaDependencyException e) {
         fail();
      }
      assertFalse(model.getConstantNodes().contains(constantNode2));
      
      // remove 'auxiliaryNode1' -> CORRECT
      try {
         model.removeNode(auxiliaryNode1);
      } catch (FormulaDependencyException e) {
         fail();
      }
      assertFalse(model.getAuxiliaryNodes().contains(auxiliaryNode1));
      
      // remove 'auxiliaryNode2' -> WRONG
      try {
         model.removeNode(auxiliaryNode2);
         fail();
      } catch (FormulaDependencyException e) {
         assertEquals("The node that should be removed is part of another node's formula.",
                      e.getMessage());
         assertTrue(e.getNodeWithProblematicFormula() == rateNode2);
      }
      
      // remove 'levelNode2' -> WRONG
      try {
         model.removeNode(levelNode2);
         fail();
      } catch (FormulaDependencyException e) {
         assertEquals("The node that should be removed is part of another node's formula.",
                      e.getMessage());
         assertTrue(e.getNodeWithProblematicFormula() == rateNode2);
      }
      
      // remove 'levelNode1' -> CORRECT
      try {
         model.removeNode(levelNode1);
      } catch (FormulaDependencyException e) {
         fail();
      }
      assertFalse(model.getLevelNodes().contains(levelNode1));
      assertTrue(rateNode1.getFlowSink() == null);
      assertTrue(rateNode2.getFlowSource() == null);
   }
   
   /**
    * Tests the method
    * {@link de.uka.aifb.com.systemDynamics.model.Model#removeNode(AbstractNode)}.
    */
   public void testRemoveNode3() {
      // create model
      LevelNode levelNode = model.createLevelNode("Level node", 0);
      RateNode rateNode1 = model.createRateNode("Rate node 1");
      RateNode rateNode2 = model.createRateNode("Rate node 2");
      ConstantNode constantNode = model.createConstantNode("Constant node", 0);
      AuxiliaryNode auxiliaryNode = model.createAuxiliaryNode("Auxiliary node");
      SourceSinkNode sourceSinkNode = model.createSourceSinkNode();
      
      model.addFlowFromSourceSinkNode2RateNode(sourceSinkNode, rateNode1);
      model.addFlowFromRateNode2LevelNode(rateNode1, levelNode);
      model.addFlowFromLevelNode2RateNode(levelNode, rateNode2);
      model.addFlowFromRateNode2SourceSinkNode(rateNode2, sourceSinkNode);
      model.setFormula(rateNode1, constantNode);
      model.setFormula(rateNode2, new ASTPlus(auxiliaryNode, levelNode));
      
      // remove 'rateNode1'
      try {
         model.removeNode(rateNode1);
      } catch (FormulaDependencyException e) {
         fail();
      }
      assertTrue(sourceSinkNode.getOutgoingFlows().isEmpty());
      assertFalse(model.getRateNodes().contains(rateNode1));
      assertTrue(levelNode.getIncomingFlows().isEmpty());
      
      // remove 'sourceSinkNode'
      try {
         model.removeNode(sourceSinkNode);
      } catch (FormulaDependencyException e) {
         fail();
      }
      assertFalse(model.getSourceSinkNodes().contains(sourceSinkNode));
      assertTrue(rateNode2.getFlowSink() == null);
   }
   
   /**
    * Tests the method
    * {@link de.uka.aifb.com.systemDynamics.model.Model#setNodeName(AbstractNode, String)}.
    */
   public void testSetNodeName() {
      LevelNode levelNode = model.createLevelNode("Level node", 0);
      RateNode rateNode = model.createRateNode("Rate node");
      ConstantNode constantNode = model.createConstantNode("Constant node", 0);
      AuxiliaryNode auxiliaryNode = model.createAuxiliaryNode("Auxiliary node");
      
      // (1) node 'null' -> WRONG
      try {
         model.setNodeName(null, "Node name");
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'node' must not be null.", e.getMessage());
      }
      
      // (2) LevelNode
      model.setNodeName(levelNode, "New level node name");
      
      // (3) RateNode
      model.setNodeName(rateNode, "New rate node name");
      
      // (4) ConstantNode
      model.setNodeName(constantNode, "New constant node name");
      
      // (5) AuxiliaryNode
      model.setNodeName(auxiliaryNode, "New auxiliary node name");
      
      // (6) check that node name cannot be changed after model was set unchangeable
      try {
         PrivateAccessor.setField(model, "isChangeable", Boolean.FALSE);
      } catch (NoSuchFieldException e) {
         fail();
      }
      
      try {
         model.setNodeName(levelNode, "Level node name");
         fail();
      } catch (ModelNotChangeableException e) {
         // do nothing
      }
      
      try {
         model.setNodeName(rateNode, "Rate node name");
         fail();
      } catch (ModelNotChangeableException e) {
         // do nothing
      }
      
      try {
         model.setNodeName(constantNode, "Constant node name");
         fail();
      } catch (ModelNotChangeableException e) {
         // do nothing
      }
      
      try {
         model.setNodeName(auxiliaryNode, "Auxiliary node name");
         fail();
      } catch (ModelNotChangeableException e) {
         // do nothing
      }
   }
   
   /**
    * Tests the method
    * {@link de.uka.aifb.com.systemDynamics.model.Model#setStartValue(LevelNode, double)}.
    */
   public void testSetStartValue() {
      LevelNode levelNode = model.createLevelNode("Level node", 0);
      
      // (1) node 'null' -> WRONG
      try {
         model.setStartValue(null, 0);
         fail();
      } catch (NodeParameterOutOfRangeException e) {
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'levelNode' must not be null.", e.getMessage());
      }
      
      // (2) correct parameters -> CORRECT
      model.setStartValue(levelNode, 5);
      
      // (3) check that start value cannot be changed after model was set unchangeable
      try {
         PrivateAccessor.setField(model, "isChangeable", Boolean.FALSE);
      } catch (NoSuchFieldException e) {
         fail();
      }
      
      try {
         model.setStartValue(levelNode, 0);
         fail();
      } catch (ModelNotChangeableException e) {
         // do nothing
      }
   }
   
   /**
    * Tests the method
    * {@link de.uka.aifb.com.systemDynamics.model.Model#setConstantValue(ConstantNode, double)}.
    */
   public void testSetConstantValue() {
      ConstantNode constantNode = model.createConstantNode("Constant node", 0);
      
      // (1) node 'null' -> WRONG
      try {
         model.setConstantValue(null, 0);
         fail();
      } catch (NodeParameterOutOfRangeException e) {
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'constantNode' must not be null.", e.getMessage());
      }
      
      // (2) correct parameters -> CORRECT
      model.setConstantValue(constantNode, 5);
      
      // (3) check that constant value cannot be changed after model was set unchangeable
      try {
         PrivateAccessor.setField(model, "isChangeable", Boolean.FALSE);
      } catch (NoSuchFieldException e) {
         fail();
      }
      
      try {
         model.setConstantValue(constantNode, 0);
         fail();
      } catch (ModelNotChangeableException e) {
         // do nothing
      }
   }
   
   /**
    * Tests the method
    * {@link de.uka.aifb.com.systemDynamics.model.Model#setFormula(AbstractNode, ASTElement)}.
    */
   public void testSetFormula()  {
      LevelNode levelNode = model.createLevelNode("Level node", 0);
      RateNode rateNode = model.createRateNode("Rate node");
      ConstantNode constantNode = model.createConstantNode("Constant node", 0);
      AuxiliaryNode auxiliaryNode = model.createAuxiliaryNode("Auxiliary node");
      
      // (1) node ' null' -> WRONG
      try {
         model.setFormula(null, constantNode);
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'node' must not be null.", e.getMessage());
      }
      
      // (2) formula 'null' -> CORRECT (!)
      model.setFormula(rateNode, constantNode);
      
      // (3) node of type LevelNode -> WRONG
      try {
         model.setFormula(levelNode, constantNode);
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'node' must be of type AuxiliaryNode or RateNode.", e.getMessage());
      }
      
      // (4) node of type ConstantNode -> WRONG
      try {
         model.setFormula(constantNode, constantNode);
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'node' must be of type AuxiliaryNode or RateNode.", e.getMessage());
      }
      
      // (5) node of type RateNode -> CORRECT
      model.setFormula(rateNode, constantNode);
      
      // (6) node of type AuxiliaryNode -> CORRECT
      model.setFormula(auxiliaryNode, constantNode);
      
      // (7) check that formula cannot be changed after model was set unchangeable
      try {
         PrivateAccessor.setField(model, "isChangeable", Boolean.FALSE);
      } catch (NoSuchFieldException e) {
         fail();
      }
      
      try {
         model.setFormula(rateNode, constantNode);
         fail();
      } catch (ModelNotChangeableException e) {
         // do nothing
      }
      
      try {
         model.setFormula(auxiliaryNode, constantNode);
         fail();
      } catch (ModelNotChangeableException e) {
         // do nothing
      }
   }
   
   /**
    * Tests the methods
    * {@link de.uka.aifb.com.systemDynamics.model.Model#addFlowFromLevelNode2RateNode(LevelNode, RateNode)}
    * and
    * {@link de.uka.aifb.com.systemDynamics.model.Model#removeFlowFromLevelNode2RateNode(LevelNode, RateNode)}.
    *
    */
   public void testFlowFromLevelNode2RateNode() {
      LevelNode levelNodeA = model.createLevelNode("Level node A", 0); 
      LevelNode levelNodeB = model.createLevelNode("Level node B", 0);
      RateNode rateNodeA = model.createRateNode("Rate node A");
      RateNode rateNodeB = model.createRateNode("Rate node B");
      
      assertTrue(levelNodeA.getOutgoingFlows().isEmpty());
      assertTrue(levelNodeB.getOutgoingFlows().isEmpty());
      assertTrue(rateNodeA.getFlowSource() == null);
      assertTrue(rateNodeB.getFlowSource() == null);
      
      // (a1) "add" level node 'null' -> WRONG
      try {
         model.addFlowFromLevelNode2RateNode(null, rateNodeA);
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'levelNode' must not be null.", e.getMessage());
      }
      assertTrue(levelNodeA.getOutgoingFlows().isEmpty());
      assertTrue(levelNodeB.getOutgoingFlows().isEmpty());
      assertTrue(rateNodeA.getFlowSource() == null);
      assertTrue(rateNodeB.getFlowSource() == null);
      
      // (a2) "add" rate node 'null' -> WRONG
      try {
         model.addFlowFromLevelNode2RateNode(levelNodeA, null);
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'rateNode' must not be null.", e.getMessage());
      }
      assertTrue(levelNodeA.getOutgoingFlows().isEmpty());
      assertTrue(levelNodeB.getOutgoingFlows().isEmpty());
      assertTrue(rateNodeA.getFlowSource() == null);
      assertTrue(rateNodeB.getFlowSource() == null);
      
      // (a3) "add" levelNodeA -> rateNodeA -> CORRECT (returns 'true')
      assertTrue(model.addFlowFromLevelNode2RateNode(levelNodeA, rateNodeA));
      assertTrue(levelNodeA.getOutgoingFlows().size() == 1);
      assertTrue(levelNodeA.getOutgoingFlows().contains(rateNodeA));
      assertTrue(rateNodeA.getFlowSource() == levelNodeA);
      assertTrue(levelNodeB.getOutgoingFlows().isEmpty());
      assertTrue(rateNodeB.getFlowSource() == null);
      
      // (a4) "add" levelNodeB -> rateNodeB -> CORRECT (returns 'true')
      assertTrue(model.addFlowFromLevelNode2RateNode(levelNodeB, rateNodeB));
      assertTrue(levelNodeA.getOutgoingFlows().size() == 1);
      assertTrue(levelNodeA.getOutgoingFlows().contains(rateNodeA));
      assertTrue(rateNodeA.getFlowSource() == levelNodeA);
      assertTrue(levelNodeB.getOutgoingFlows().size() == 1);
      assertTrue(levelNodeB.getOutgoingFlows().contains(rateNodeB));
      assertTrue(rateNodeB.getFlowSource() == levelNodeB);
      
      // (a5) "add" levelNodeB -> rateNodeA -> WRONG (returns 'false')
      assertFalse(model.addFlowFromLevelNode2RateNode(levelNodeB, rateNodeA));
      assertTrue(levelNodeA.getOutgoingFlows().size() == 1);
      assertTrue(levelNodeA.getOutgoingFlows().contains(rateNodeA));
      assertTrue(rateNodeA.getFlowSource() == levelNodeA);
      assertTrue(levelNodeB.getOutgoingFlows().size() == 1);
      assertTrue(levelNodeB.getOutgoingFlows().contains(rateNodeB));
      assertTrue(rateNodeB.getFlowSource() == levelNodeB);
      
      // (b1) "remove" level node 'null' -> WRONG
      try {
         model.removeFlowFromLevelNode2RateNode(null, rateNodeA);
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'levelNode' must not be null.", e.getMessage());
      }
      assertTrue(levelNodeA.getOutgoingFlows().size() == 1);
      assertTrue(levelNodeA.getOutgoingFlows().contains(rateNodeA));
      assertTrue(rateNodeA.getFlowSource() == levelNodeA);
      assertTrue(levelNodeB.getOutgoingFlows().size() == 1);
      assertTrue(levelNodeB.getOutgoingFlows().contains(rateNodeB));
      assertTrue(rateNodeB.getFlowSource() == levelNodeB);
      
      // (b2) "remove" rate node 'null' -> WRONG
      try {
         model.removeFlowFromLevelNode2RateNode(levelNodeA, null);
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'rateNode' must not be null.", e.getMessage());
      }
      assertTrue(levelNodeA.getOutgoingFlows().size() == 1);
      assertTrue(levelNodeA.getOutgoingFlows().contains(rateNodeA));
      assertTrue(rateNodeA.getFlowSource() == levelNodeA);
      assertTrue(levelNodeB.getOutgoingFlows().size() == 1);
      assertTrue(levelNodeB.getOutgoingFlows().contains(rateNodeB));
      assertTrue(rateNodeB.getFlowSource() == levelNodeB);
      
      // (b3) "remove" levelNodeB -> rateNodeA (not existing) -> WRONG (returns 'false')
      assertFalse(model.removeFlowFromLevelNode2RateNode(levelNodeB, rateNodeA));
      assertTrue(levelNodeA.getOutgoingFlows().size() == 1);
      assertTrue(levelNodeA.getOutgoingFlows().contains(rateNodeA));
      assertTrue(rateNodeA.getFlowSource() == levelNodeA);
      assertTrue(levelNodeB.getOutgoingFlows().size() == 1);
      assertTrue(levelNodeB.getOutgoingFlows().contains(rateNodeB));
      assertTrue(rateNodeB.getFlowSource() == levelNodeB);
      
      // (b4) "remove" levelNodeA -> rateNodeA -> CORRECT (returns 'true')
      assertTrue(model.removeFlowFromLevelNode2RateNode(levelNodeA, rateNodeA));
      assertTrue(levelNodeA.getOutgoingFlows().isEmpty());
      assertTrue(rateNodeA.getFlowSource() == null);
      assertTrue(levelNodeB.getOutgoingFlows().size() == 1);
      assertTrue(levelNodeB.getOutgoingFlows().contains(rateNodeB));
      assertTrue(rateNodeB.getFlowSource() == levelNodeB);
      
      // (a6) "add" levelNodeB -> rateNodeA -> now CORRECT (returns 'true')
      assertTrue(model.addFlowFromLevelNode2RateNode(levelNodeB, rateNodeA));
      assertTrue(levelNodeA.getOutgoingFlows().isEmpty());
      assertTrue(levelNodeB.getOutgoingFlows().size() == 2);
      assertTrue(levelNodeB.getOutgoingFlows().contains(rateNodeA));
      assertTrue(levelNodeB.getOutgoingFlows().contains(rateNodeB));
      assertTrue(rateNodeA.getFlowSource() == levelNodeB);
      assertTrue(rateNodeB.getFlowSource() == levelNodeB);

      // (a7) check that no flow can be added after model was set unchangeable
      try {
         PrivateAccessor.setField(model, "isChangeable", Boolean.FALSE);
      } catch (NoSuchFieldException e) {
         fail();
      }
      
      try {
         model.addFlowFromLevelNode2RateNode(levelNodeA, rateNodeA);
         fail();
      } catch (ModelNotChangeableException e) {
         // do nothing
      }
      assertTrue(levelNodeA.getOutgoingFlows().isEmpty());
      assertTrue(levelNodeB.getOutgoingFlows().size() == 2);
      assertTrue(levelNodeB.getOutgoingFlows().contains(rateNodeA));
      assertTrue(levelNodeB.getOutgoingFlows().contains(rateNodeB));
      assertTrue(rateNodeA.getFlowSource() == levelNodeB);
      assertTrue(rateNodeB.getFlowSource() == levelNodeB);
      
      // (b5) check that no flow can be removed after model was set unchangeable
      try {
         model.removeFlowFromLevelNode2RateNode(levelNodeA, rateNodeA);
         fail();
      } catch (ModelNotChangeableException e) {
         // do nothing
      }
      assertTrue(levelNodeA.getOutgoingFlows().isEmpty());
      assertTrue(levelNodeB.getOutgoingFlows().size() == 2);
      assertTrue(levelNodeB.getOutgoingFlows().contains(rateNodeA));
      assertTrue(levelNodeB.getOutgoingFlows().contains(rateNodeB));
      assertTrue(rateNodeA.getFlowSource() == levelNodeB);
      assertTrue(rateNodeB.getFlowSource() == levelNodeB);
   }
   
   /**
    * Tests the methods
    * {@link de.uka.aifb.com.systemDynamics.model.Model#addFlowFromRateNode2LevelNode(RateNode, LevelNode)}
    * and
    * {@link de.uka.aifb.com.systemDynamics.model.Model#removeFlowFromRateNode2LevelNode(RateNode, LevelNode)}.
    */
   public void testFlowFromRateNode2LevelNode() {
      RateNode rateNodeA = model.createRateNode("Rate node A");
      RateNode rateNodeB = model.createRateNode("Rate node B");
      LevelNode levelNodeA = model.createLevelNode("Level node A", 0);
      LevelNode levelNodeB = model.createLevelNode("Level node B", 0);
      
      assertTrue(rateNodeA.getFlowSink() == null);
      assertTrue(rateNodeB.getFlowSink() == null);
      assertTrue(levelNodeA.getIncomingFlows().isEmpty());
      assertTrue(levelNodeB.getIncomingFlows().isEmpty());
            
      // (a1) "add" rate node 'null' -> WRONG
      try {
         model.addFlowFromRateNode2LevelNode(null, levelNodeA);
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'rateNode' must not be null.", e.getMessage());
      }
      assertTrue(rateNodeA.getFlowSink() == null);
      assertTrue(rateNodeB.getFlowSink() == null);
      assertTrue(levelNodeA.getIncomingFlows().isEmpty());
      assertTrue(levelNodeB.getIncomingFlows().isEmpty());
      
      // (a2) "add" level node 'null' -> WRONG
      try {
         model.addFlowFromRateNode2LevelNode(rateNodeA, null);
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'levelNode' must not be null.", e.getMessage());
      }
      assertTrue(rateNodeA.getFlowSink() == null);
      assertTrue(rateNodeB.getFlowSink() == null);
      assertTrue(levelNodeA.getIncomingFlows().isEmpty());
      assertTrue(levelNodeB.getIncomingFlows().isEmpty());
      
      // (a3) "add" rateNodeA -> levelNodeA -> CORRECT (returns 'true')
      assertTrue(model.addFlowFromRateNode2LevelNode(rateNodeA, levelNodeA));
      assertTrue(rateNodeA.getFlowSink() == levelNodeA);
      assertTrue(rateNodeB.getFlowSink() == null);
      assertTrue(levelNodeA.getIncomingFlows().size() == 1);
      assertTrue(levelNodeA.getIncomingFlows().contains(rateNodeA));
      assertTrue(levelNodeB.getIncomingFlows().isEmpty());
      
      // (a4) "add" rateNodeB -> levelNodeB -> CORRECT (returns 'true')
      assertTrue(model.addFlowFromRateNode2LevelNode(rateNodeB, levelNodeB));
      assertTrue(rateNodeA.getFlowSink() == levelNodeA);
      assertTrue(rateNodeB.getFlowSink() == levelNodeB);
      assertTrue(levelNodeA.getIncomingFlows().size() == 1);
      assertTrue(levelNodeA.getIncomingFlows().contains(rateNodeA));
      assertTrue(levelNodeB.getIncomingFlows().size() == 1);
      assertTrue(levelNodeB.getIncomingFlows().contains(rateNodeB));
      
      // (a5) "add" rateNodeA -> levelNodeB -> WRONG (returns 'false')
      assertFalse(model.addFlowFromRateNode2LevelNode(rateNodeA, levelNodeB));
      assertTrue(rateNodeA.getFlowSink() == levelNodeA);
      assertTrue(rateNodeB.getFlowSink() == levelNodeB);
      assertTrue(levelNodeA.getIncomingFlows().size() == 1);
      assertTrue(levelNodeA.getIncomingFlows().contains(rateNodeA));
      assertTrue(levelNodeB.getIncomingFlows().size() == 1);
      assertTrue(levelNodeB.getIncomingFlows().contains(rateNodeB));
      
      // (b1) "remove" rate node 'null' -> WRONG
      try {
         model.removeFlowFromRateNode2LevelNode(null, levelNodeA);
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'rateNode' must not be null.", e.getMessage());
      }
      assertTrue(rateNodeA.getFlowSink() == levelNodeA);
      assertTrue(rateNodeB.getFlowSink() == levelNodeB);
      assertTrue(levelNodeA.getIncomingFlows().size() == 1);
      assertTrue(levelNodeA.getIncomingFlows().contains(rateNodeA));
      assertTrue(levelNodeB.getIncomingFlows().size() == 1);
      assertTrue(levelNodeB.getIncomingFlows().contains(rateNodeB));
      
      // (b2) "remove" level node 'null' -> WRONG
      try {
         model.removeFlowFromRateNode2LevelNode(rateNodeA, null);
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'levelNode' must not be null.", e.getMessage());
      }
      assertTrue(rateNodeA.getFlowSink() == levelNodeA);
      assertTrue(rateNodeB.getFlowSink() == levelNodeB);
      assertTrue(levelNodeA.getIncomingFlows().size() == 1);
      assertTrue(levelNodeA.getIncomingFlows().contains(rateNodeA));
      assertTrue(levelNodeB.getIncomingFlows().size() == 1);
      assertTrue(levelNodeB.getIncomingFlows().contains(rateNodeB));
      
      // (b3) "remove" rateNodeB -> rateNodeA (not existing) -> WRONG (returns 'false')
      assertFalse(model.removeFlowFromRateNode2LevelNode(rateNodeB, levelNodeA));
      assertTrue(rateNodeA.getFlowSink() == levelNodeA);
      assertTrue(rateNodeB.getFlowSink() == levelNodeB);
      assertTrue(levelNodeA.getIncomingFlows().size() == 1);
      assertTrue(levelNodeA.getIncomingFlows().contains(rateNodeA));
      assertTrue(levelNodeB.getIncomingFlows().size() == 1);
      assertTrue(levelNodeB.getIncomingFlows().contains(rateNodeB));
      
      // (b4) "remove" rateNodeA -> levelNodeA -> CORRECT (returns 'true')
      assertTrue(model.removeFlowFromRateNode2LevelNode(rateNodeA, levelNodeA));
      assertTrue(rateNodeA.getFlowSink() == null);
      assertTrue(rateNodeB.getFlowSink() == levelNodeB);
      assertTrue(levelNodeA.getIncomingFlows().isEmpty());
      assertTrue(levelNodeB.getIncomingFlows().size() == 1);
      assertTrue(levelNodeB.getIncomingFlows().contains(rateNodeB));
      
      // (a6) "add" rateNodeA -> levelNodeB -> now CORRECT (returns 'true')
      assertTrue(model.addFlowFromRateNode2LevelNode(rateNodeA, levelNodeB));
      assertTrue(rateNodeA.getFlowSink() == levelNodeB);
      assertTrue(rateNodeB.getFlowSink() == levelNodeB);
      assertTrue(levelNodeA.getIncomingFlows().isEmpty());
      assertTrue(levelNodeB.getIncomingFlows().size() == 2);
      assertTrue(levelNodeB.getIncomingFlows().contains(rateNodeA));
      assertTrue(levelNodeB.getIncomingFlows().contains(rateNodeB));

      // (a7) check that no flow can be added after model was set unchangeable
      try {
         PrivateAccessor.setField(model, "isChangeable", Boolean.FALSE);
      } catch (NoSuchFieldException e) {
         fail();
      }
      
      try {
         model.addFlowFromRateNode2LevelNode(rateNodeA, levelNodeA);
         fail();
      } catch (ModelNotChangeableException e) {
         // do nothing
      }
      assertTrue(rateNodeA.getFlowSink() == levelNodeB);
      assertTrue(rateNodeB.getFlowSink() == levelNodeB);
      assertTrue(levelNodeA.getIncomingFlows().isEmpty());
      assertTrue(levelNodeB.getIncomingFlows().size() == 2);
      assertTrue(levelNodeB.getIncomingFlows().contains(rateNodeA));
      assertTrue(levelNodeB.getIncomingFlows().contains(rateNodeB));
      
      // (b5) check that no flow can be removed after model was set unchangeable
      try {
         model.removeFlowFromRateNode2LevelNode(rateNodeA, levelNodeA);
         fail();
      } catch (ModelNotChangeableException e) {
         // do nothing
      }
   }
   
   /**
    * Tests the methods
    * {@link de.uka.aifb.com.systemDynamics.model.Model#addFlowFromSourceSinkNode2RateNode(SourceSinkNode, RateNode)}
    * and
    * {@link de.uka.aifb.com.systemDynamics.model.Model#removeFlowFromSourceSinkNode2RateNode(SourceSinkNode, RateNode)}.
    *
    */
   public void testFlowFromSourceSinkNode2RateNode() {
      SourceSinkNode sourceSinkNodeA = model.createSourceSinkNode(); 
      SourceSinkNode sourceSinkNodeB = model.createSourceSinkNode();
      RateNode rateNodeA = model.createRateNode("Rate node A");
      RateNode rateNodeB = model.createRateNode("Rate node B");
      
      assertTrue(sourceSinkNodeA.getOutgoingFlows().isEmpty());
      assertTrue(sourceSinkNodeB.getOutgoingFlows().isEmpty());
      assertTrue(rateNodeA.getFlowSource() == null);
      assertTrue(rateNodeB.getFlowSource() == null);
      
      // (a1) "add" source/sink node 'null' -> WRONG
      try {
         model.addFlowFromSourceSinkNode2RateNode(null, rateNodeA);
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'sourceSinkNode' must not be null.", e.getMessage());
      }
      assertTrue(sourceSinkNodeA.getOutgoingFlows().isEmpty());
      assertTrue(sourceSinkNodeB.getOutgoingFlows().isEmpty());
      assertTrue(rateNodeA.getFlowSource() == null);
      assertTrue(rateNodeB.getFlowSource() == null);
      
      // (a2) "add" rate node 'null' -> WRONG
      try {
         model.addFlowFromSourceSinkNode2RateNode(sourceSinkNodeA, null);
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'rateNode' must not be null.", e.getMessage());
      }
      assertTrue(sourceSinkNodeA.getOutgoingFlows().isEmpty());
      assertTrue(sourceSinkNodeB.getOutgoingFlows().isEmpty());
      assertTrue(rateNodeA.getFlowSource() == null);
      assertTrue(rateNodeB.getFlowSource() == null);
      
      // (a3) "add" sourceSinkNodeA -> rateNodeA -> CORRECT (returns 'true')
      assertTrue(model.addFlowFromSourceSinkNode2RateNode(sourceSinkNodeA, rateNodeA));
      assertTrue(sourceSinkNodeA.getOutgoingFlows().size() == 1);
      assertTrue(sourceSinkNodeA.getOutgoingFlows().contains(rateNodeA));
      assertTrue(rateNodeA.getFlowSource() == sourceSinkNodeA);
      assertTrue(sourceSinkNodeB.getOutgoingFlows().isEmpty());
      assertTrue(rateNodeB.getFlowSource() == null);
      
      // (a4) "add" sourceSinkNodeB -> rateNodeB -> CORRECT (returns 'true')
      assertTrue(model.addFlowFromSourceSinkNode2RateNode(sourceSinkNodeB, rateNodeB));
      assertTrue(sourceSinkNodeA.getOutgoingFlows().size() == 1);
      assertTrue(sourceSinkNodeA.getOutgoingFlows().contains(rateNodeA));
      assertTrue(rateNodeA.getFlowSource() == sourceSinkNodeA);
      assertTrue(sourceSinkNodeB.getOutgoingFlows().size() == 1);
      assertTrue(sourceSinkNodeB.getOutgoingFlows().contains(rateNodeB));
      assertTrue(rateNodeB.getFlowSource() == sourceSinkNodeB);
      
      // (a5) "add" sourceSinkNodeB -> rateNodeA -> WRONG (returns 'false')
      assertFalse(model.addFlowFromSourceSinkNode2RateNode(sourceSinkNodeB, rateNodeA));
      assertTrue(sourceSinkNodeA.getOutgoingFlows().size() == 1);
      assertTrue(sourceSinkNodeA.getOutgoingFlows().contains(rateNodeA));
      assertTrue(rateNodeA.getFlowSource() == sourceSinkNodeA);
      assertTrue(sourceSinkNodeB.getOutgoingFlows().size() == 1);
      assertTrue(sourceSinkNodeB.getOutgoingFlows().contains(rateNodeB));
      assertTrue(rateNodeB.getFlowSource() == sourceSinkNodeB);
      
      // (b1) "remove" sourceSink node 'null' -> WRONG
      try {
         model.removeFlowFromSourceSinkNode2RateNode(null, rateNodeA);
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'sourceSinkNode' must not be null.", e.getMessage());
      }
      assertTrue(sourceSinkNodeA.getOutgoingFlows().size() == 1);
      assertTrue(sourceSinkNodeA.getOutgoingFlows().contains(rateNodeA));
      assertTrue(rateNodeA.getFlowSource() == sourceSinkNodeA);
      assertTrue(sourceSinkNodeB.getOutgoingFlows().size() == 1);
      assertTrue(sourceSinkNodeB.getOutgoingFlows().contains(rateNodeB));
      assertTrue(rateNodeB.getFlowSource() == sourceSinkNodeB);
      
      // (b2) "remove" rate node 'null' -> WRONG
      try {
         model.removeFlowFromSourceSinkNode2RateNode(sourceSinkNodeA, null);
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'rateNode' must not be null.", e.getMessage());
      }
      assertTrue(sourceSinkNodeA.getOutgoingFlows().size() == 1);
      assertTrue(sourceSinkNodeA.getOutgoingFlows().contains(rateNodeA));
      assertTrue(rateNodeA.getFlowSource() == sourceSinkNodeA);
      assertTrue(sourceSinkNodeB.getOutgoingFlows().size() == 1);
      assertTrue(sourceSinkNodeB.getOutgoingFlows().contains(rateNodeB));
      assertTrue(rateNodeB.getFlowSource() == sourceSinkNodeB);
      
      // (b3) "remove" sourceSinkNodeB -> rateNodeA (not existing) -> WRONG (returns 'false')
      assertFalse(model.removeFlowFromSourceSinkNode2RateNode(sourceSinkNodeB, rateNodeA));
      assertTrue(sourceSinkNodeA.getOutgoingFlows().size() == 1);
      assertTrue(sourceSinkNodeA.getOutgoingFlows().contains(rateNodeA));
      assertTrue(rateNodeA.getFlowSource() == sourceSinkNodeA);
      assertTrue(sourceSinkNodeB.getOutgoingFlows().size() == 1);
      assertTrue(sourceSinkNodeB.getOutgoingFlows().contains(rateNodeB));
      assertTrue(rateNodeB.getFlowSource() == sourceSinkNodeB);
      
      // (b4) "remove" sourceSinkNodeA -> rateNodeA -> CORRECT (returns 'true')
      assertTrue(model.removeFlowFromSourceSinkNode2RateNode(sourceSinkNodeA, rateNodeA));
      assertTrue(sourceSinkNodeA.getOutgoingFlows().isEmpty());
      assertTrue(rateNodeA.getFlowSource() == null);
      assertTrue(sourceSinkNodeB.getOutgoingFlows().size() == 1);
      assertTrue(sourceSinkNodeB.getOutgoingFlows().contains(rateNodeB));
      assertTrue(rateNodeB.getFlowSource() == sourceSinkNodeB);
      
      // (a6) "add" sourceSinkNodeB -> rateNodeA -> now CORRECT (returns 'true')
      assertTrue(model.addFlowFromSourceSinkNode2RateNode(sourceSinkNodeB, rateNodeA));
      assertTrue(sourceSinkNodeA.getOutgoingFlows().isEmpty());
      assertTrue(sourceSinkNodeB.getOutgoingFlows().size() == 2);
      assertTrue(sourceSinkNodeB.getOutgoingFlows().contains(rateNodeA));
      assertTrue(sourceSinkNodeB.getOutgoingFlows().contains(rateNodeB));
      assertTrue(rateNodeA.getFlowSource() == sourceSinkNodeB);
      assertTrue(rateNodeB.getFlowSource() == sourceSinkNodeB);

      // (a7) check that no flow can be added after model was set unchangeable
      try {
         PrivateAccessor.setField(model, "isChangeable", Boolean.FALSE);
      } catch (NoSuchFieldException e) {
         fail();
      }
      
      try {
         model.addFlowFromSourceSinkNode2RateNode(sourceSinkNodeA, rateNodeA);
         fail();
      } catch (ModelNotChangeableException e) {
         // do nothing
      }
      assertTrue(sourceSinkNodeA.getOutgoingFlows().isEmpty());
      assertTrue(sourceSinkNodeB.getOutgoingFlows().size() == 2);
      assertTrue(sourceSinkNodeB.getOutgoingFlows().contains(rateNodeA));
      assertTrue(sourceSinkNodeB.getOutgoingFlows().contains(rateNodeB));
      assertTrue(rateNodeA.getFlowSource() == sourceSinkNodeB);
      assertTrue(rateNodeB.getFlowSource() == sourceSinkNodeB);
      
      // (b5) check that no flow can be removed after model was set unchangeable
      try {
         model.removeFlowFromSourceSinkNode2RateNode(sourceSinkNodeA, rateNodeA);
         fail();
      } catch (ModelNotChangeableException e) {
         // do nothing
      }
      assertTrue(sourceSinkNodeA.getOutgoingFlows().isEmpty());
      assertTrue(sourceSinkNodeB.getOutgoingFlows().size() == 2);
      assertTrue(sourceSinkNodeB.getOutgoingFlows().contains(rateNodeA));
      assertTrue(sourceSinkNodeB.getOutgoingFlows().contains(rateNodeB));
      assertTrue(rateNodeA.getFlowSource() == sourceSinkNodeB);
      assertTrue(rateNodeB.getFlowSource() == sourceSinkNodeB);
   }
   
   /**
    * Tests the methods
    * {@link de.uka.aifb.com.systemDynamics.model.Model#addFlowFromRateNode2SourceSinkNode(RateNode, SourceSinkNode)}
    * and
    * {@link de.uka.aifb.com.systemDynamics.model.Model#removeFlowFromRateNode2SourceSinkNode(RateNode, SourceSinkNode)}.
    */
   public void testFlowFromRateNode2SourceSinkNode() {
      RateNode rateNodeA = model.createRateNode("Rate node A");
      RateNode rateNodeB = model.createRateNode("Rate node B");
      SourceSinkNode sourceSinkNodeA = model.createSourceSinkNode();
      SourceSinkNode sourceSinkNodeB = model.createSourceSinkNode();
      
      assertTrue(rateNodeA.getFlowSink() == null);
      assertTrue(rateNodeB.getFlowSink() == null);
      assertTrue(sourceSinkNodeA.getIncomingFlows().isEmpty());
      assertTrue(sourceSinkNodeB.getIncomingFlows().isEmpty());
            
      // (a1) "add" rate node 'null' -> WRONG
      try {
         model.addFlowFromRateNode2SourceSinkNode(null, sourceSinkNodeA);
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'rateNode' must not be null.", e.getMessage());
      }
      assertTrue(rateNodeA.getFlowSink() == null);
      assertTrue(rateNodeB.getFlowSink() == null);
      assertTrue(sourceSinkNodeA.getIncomingFlows().isEmpty());
      assertTrue(sourceSinkNodeB.getIncomingFlows().isEmpty());
      
      // (a2) "add" source/sink node 'null' -> WRONG
      try {
         model.addFlowFromRateNode2LevelNode(rateNodeA, null);
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'levelNode' must not be null.", e.getMessage());
      }
      assertTrue(rateNodeA.getFlowSink() == null);
      assertTrue(rateNodeB.getFlowSink() == null);
      assertTrue(sourceSinkNodeA.getIncomingFlows().isEmpty());
      assertTrue(sourceSinkNodeB.getIncomingFlows().isEmpty());
      
      // (a3) "add" rateNodeA -> sourceSinkNodeA -> CORRECT (returns 'true')
      assertTrue(model.addFlowFromRateNode2SourceSinkNode(rateNodeA, sourceSinkNodeA));
      assertTrue(rateNodeA.getFlowSink() == sourceSinkNodeA);
      assertTrue(rateNodeB.getFlowSink() == null);
      assertTrue(sourceSinkNodeA.getIncomingFlows().size() == 1);
      assertTrue(sourceSinkNodeA.getIncomingFlows().contains(rateNodeA));
      assertTrue(sourceSinkNodeB.getIncomingFlows().isEmpty());
      
      // (a4) "add" rateNodeB -> sourceSinkNodeB -> CORRECT (returns 'true')
      assertTrue(model.addFlowFromRateNode2SourceSinkNode(rateNodeB, sourceSinkNodeB));
      assertTrue(rateNodeA.getFlowSink() == sourceSinkNodeA);
      assertTrue(rateNodeB.getFlowSink() == sourceSinkNodeB);
      assertTrue(sourceSinkNodeA.getIncomingFlows().size() == 1);
      assertTrue(sourceSinkNodeA.getIncomingFlows().contains(rateNodeA));
      assertTrue(sourceSinkNodeB.getIncomingFlows().size() == 1);
      assertTrue(sourceSinkNodeB.getIncomingFlows().contains(rateNodeB));
      
      // (a5) "add" rateNodeA -> sourceSinkNodeB -> WRONG (returns 'false')
      assertFalse(model.addFlowFromRateNode2SourceSinkNode(rateNodeA, sourceSinkNodeB));
      assertTrue(rateNodeA.getFlowSink() == sourceSinkNodeA);
      assertTrue(rateNodeB.getFlowSink() == sourceSinkNodeB);
      assertTrue(sourceSinkNodeA.getIncomingFlows().size() == 1);
      assertTrue(sourceSinkNodeA.getIncomingFlows().contains(rateNodeA));
      assertTrue(sourceSinkNodeB.getIncomingFlows().size() == 1);
      assertTrue(sourceSinkNodeB.getIncomingFlows().contains(rateNodeB));
      
      // (b1) "remove" rate node 'null' -> WRONG
      try {
         model.removeFlowFromRateNode2SourceSinkNode(null, sourceSinkNodeA);
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'rateNode' must not be null.", e.getMessage());
      }
      assertTrue(rateNodeA.getFlowSink() == sourceSinkNodeA);
      assertTrue(rateNodeB.getFlowSink() == sourceSinkNodeB);
      assertTrue(sourceSinkNodeA.getIncomingFlows().size() == 1);
      assertTrue(sourceSinkNodeA.getIncomingFlows().contains(rateNodeA));
      assertTrue(sourceSinkNodeB.getIncomingFlows().size() == 1);
      assertTrue(sourceSinkNodeB.getIncomingFlows().contains(rateNodeB));
      
      // (b2) "remove" source/sink node 'null' -> WRONG
      try {
         model.removeFlowFromRateNode2SourceSinkNode(rateNodeA, null);
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'sourceSinkNode' must not be null.", e.getMessage());
      }
      assertTrue(rateNodeA.getFlowSink() == sourceSinkNodeA);
      assertTrue(rateNodeB.getFlowSink() == sourceSinkNodeB);
      assertTrue(sourceSinkNodeA.getIncomingFlows().size() == 1);
      assertTrue(sourceSinkNodeA.getIncomingFlows().contains(rateNodeA));
      assertTrue(sourceSinkNodeB.getIncomingFlows().size() == 1);
      assertTrue(sourceSinkNodeB.getIncomingFlows().contains(rateNodeB));
      
      // (b3) "remove" rateNodeB -> rateNodeA (not existing) -> WRONG (returns 'false')
      assertFalse(model.removeFlowFromRateNode2SourceSinkNode(rateNodeB, sourceSinkNodeA));
      assertTrue(rateNodeA.getFlowSink() == sourceSinkNodeA);
      assertTrue(rateNodeB.getFlowSink() == sourceSinkNodeB);
      assertTrue(sourceSinkNodeA.getIncomingFlows().size() == 1);
      assertTrue(sourceSinkNodeA.getIncomingFlows().contains(rateNodeA));
      assertTrue(sourceSinkNodeB.getIncomingFlows().size() == 1);
      assertTrue(sourceSinkNodeB.getIncomingFlows().contains(rateNodeB));
      
      // (b4) "remove" rateNodeA -> sourceSinkNodeA -> CORRECT (returns 'true')
      assertTrue(model.removeFlowFromRateNode2SourceSinkNode(rateNodeA, sourceSinkNodeA));
      assertTrue(rateNodeA.getFlowSink() == null);
      assertTrue(rateNodeB.getFlowSink() == sourceSinkNodeB);
      assertTrue(sourceSinkNodeA.getIncomingFlows().isEmpty());
      assertTrue(sourceSinkNodeB.getIncomingFlows().size() == 1);
      assertTrue(sourceSinkNodeB.getIncomingFlows().contains(rateNodeB));
      
      // (a6) "add" rateNodeA -> sourceSinkNodeB -> now CORRECT (returns 'true')
      assertTrue(model.addFlowFromRateNode2SourceSinkNode(rateNodeA, sourceSinkNodeB));
      assertTrue(rateNodeA.getFlowSink() == sourceSinkNodeB);
      assertTrue(rateNodeB.getFlowSink() == sourceSinkNodeB);
      assertTrue(sourceSinkNodeA.getIncomingFlows().isEmpty());
      assertTrue(sourceSinkNodeB.getIncomingFlows().size() == 2);
      assertTrue(sourceSinkNodeB.getIncomingFlows().contains(rateNodeA));
      assertTrue(sourceSinkNodeB.getIncomingFlows().contains(rateNodeB));

      // (a7) check that no flow can be added after model was set unchangeable
      try {
         PrivateAccessor.setField(model, "isChangeable", Boolean.FALSE);
      } catch (NoSuchFieldException e) {
         fail();
      }
      
      try {
         model.addFlowFromRateNode2SourceSinkNode(rateNodeA, sourceSinkNodeA);
         fail();
      } catch (ModelNotChangeableException e) {
         // do nothing
      }
      assertTrue(rateNodeA.getFlowSink() == sourceSinkNodeB);
      assertTrue(rateNodeB.getFlowSink() == sourceSinkNodeB);
      assertTrue(sourceSinkNodeA.getIncomingFlows().isEmpty());
      assertTrue(sourceSinkNodeB.getIncomingFlows().size() == 2);
      assertTrue(sourceSinkNodeB.getIncomingFlows().contains(rateNodeA));
      assertTrue(sourceSinkNodeB.getIncomingFlows().contains(rateNodeB));
      
      // (b5) check that no flow can be removed after model was set unchangeable
      try {
         model.removeFlowFromRateNode2SourceSinkNode(rateNodeA, sourceSinkNodeA);
         fail();
      } catch (ModelNotChangeableException e) {
         // do nothing
      }
   }
   
   /**
    * Tests the method {@link de.uka.aifb.com.systemDynamics.model.Model#validateModel()}.
    */
   public void testValidateModel1() {
      // no level nodes -> WRONG
      try {
         model.validateModel();
         fail();
      } catch (AuxiliaryNodesCycleDependencyException e) {
         fail();
      } catch (NoFormulaException e) {
         fail();
      } catch (NoLevelNodeException e) {
         assertEquals("The System Dynamics model has no level node.", e.getMessage());
      } catch (RateNodeFlowException e) {
         fail();
      } catch (UselessNodeException e) {
         fail();
      }
   }
   
   /**
    * Tests the method {@link de.uka.aifb.com.systemDynamics.model.Model#validateModel()}.
    */
   public void testValidateModel2() {
      LevelNode levelNode1 = model.createLevelNode("Level node 1", 0);
      LevelNode levelNode2 = model.createLevelNode("Level node 2", 0);
      ConstantNode constantNode = model.createConstantNode("Constant node", 0);
      SourceSinkNode sourceSinkNode = model.createSourceSinkNode();
      
      // 'rateNode1' without any flow -> WRONG
      RateNode rateNode1 = model.createRateNode("Rate node 1");
      
      try {
         model.validateModel();
         fail();
      } catch (AuxiliaryNodesCycleDependencyException e) {
         fail();
      } catch (NoFormulaException e) {
         fail();
      } catch (NoLevelNodeException e) {
         fail();
      } catch (RateNodeFlowException e) {
         assertEquals("A rate node has no incoming or no outgoing flow.", e.getMessage());
      } catch (UselessNodeException e) {
         fail();
      }
      
      // 'rateNode1' has incoming flow -> WRONG
      model.addFlowFromLevelNode2RateNode(levelNode1, rateNode1);
      model.setFormula(rateNode1, constantNode);
      
      try {
         model.validateModel();
      } catch (AuxiliaryNodesCycleDependencyException e) {
         fail();
      } catch (NoFormulaException e) {
         fail();
      } catch (NoLevelNodeException e) {
         fail();
      } catch (RateNodeFlowException e) {
         assertEquals("A rate node has no incoming or no outgoing flow.", e.getMessage());
      } catch (UselessNodeException e) {
         fail();
      }
      
      // 'rateNode1' has incoming and outgoing flow -> CORRECT
      model.addFlowFromRateNode2SourceSinkNode(rateNode1, sourceSinkNode);
      
      try {
         model.validateModel();
      } catch (AuxiliaryNodesCycleDependencyException e) {
         fail();
      } catch (NoFormulaException e) {
         fail();
      } catch (NoLevelNodeException e) {
         fail();
      } catch (RateNodeFlowException e) {
         fail();
      } catch (UselessNodeException e) {
         fail();
      }
      
      // 'rateNode2' has outgoing flow -> WRONG
      RateNode rateNode2 = model.createRateNode("Rate node 2");
      model.addFlowFromRateNode2LevelNode(rateNode2, levelNode2);
      model.setFormula(rateNode2, constantNode);
      
      try {
         model.validateModel();
      } catch (AuxiliaryNodesCycleDependencyException e) {
         fail();
      } catch (NoFormulaException e) {
         fail();
      } catch (NoLevelNodeException e) {
         fail();
      } catch (RateNodeFlowException e) {
         assertEquals("A rate node has no incoming or no outgoing flow.", e.getMessage());
      } catch (UselessNodeException e) {
         fail();
      }
      
      // 'rateNode2' has incoming flow -> CORRECT
      model.addFlowFromSourceSinkNode2RateNode(sourceSinkNode, rateNode2);
      
      try {
         model.validateModel();
      } catch (AuxiliaryNodesCycleDependencyException e) {
         fail();
      } catch (NoFormulaException e) {
         fail();
      } catch (NoLevelNodeException e) {
         fail();
      } catch (RateNodeFlowException e) {
         fail();
      } catch (UselessNodeException e) {
         fail();
      }
   }
   
   /**
    * Tests the method {@link de.uka.aifb.com.systemDynamics.model.Model#validateModel()}.
    */
   public void testValidateModel3() {
      LevelNode levelNode = model.createLevelNode("Level node", 0);
      ConstantNode constantNode = model.createConstantNode("ConstantNode", 0);
      SourceSinkNode sourceSinkNode = model.createSourceSinkNode();
      
      // (1) rate node without formula -> WRONG
      RateNode rateNode = model.createRateNode("Rate node");
      model.addFlowFromLevelNode2RateNode(levelNode, rateNode);
      model.addFlowFromRateNode2SourceSinkNode(rateNode, sourceSinkNode);
      
      try {
         model.validateModel();
         fail();
      } catch (AuxiliaryNodesCycleDependencyException e) {
         fail();
      } catch (NoFormulaException e) {
         assertEquals("A node has no formula.", e.getMessage());
         assertTrue(e.getNodeWithourFormula() == rateNode);
      } catch (NoLevelNodeException e) {
         fail();
      } catch (RateNodeFlowException e) {
         fail();
      } catch (UselessNodeException e) {
         fail();
      }
      
      // (2) auxiliary node without formula -> WRONG
      model.setFormula(rateNode, constantNode);
      
      AuxiliaryNode auxiliaryNode = model.createAuxiliaryNode("Auxiliary node");
      
      try {
         model.validateModel();
         fail();
      } catch (AuxiliaryNodesCycleDependencyException e) {
         fail();
      } catch (NoFormulaException e) {
         assertEquals("A node has no formula.", e.getMessage());
         assertTrue(e.getNodeWithourFormula() == auxiliaryNode);
      } catch (NoLevelNodeException e) {
         fail();
      } catch (RateNodeFlowException e) {
         fail();
      } catch (UselessNodeException e) {
         fail();
      }
   }
   
   /**
    * Tests the method {@link de.uka.aifb.com.systemDynamics.model.Model#validateModel()}.
    */
   public void testValidateModel4() {
      // auxiliary nodes with cycle dependency -> WRONG
      model.createLevelNode("Level node", 0);
      AuxiliaryNode auxiliaryNode1 = model.createAuxiliaryNode("Auxiliary node 1");
      AuxiliaryNode auxiliaryNode2 = model.createAuxiliaryNode("Auxiliary node 2");
      model.setFormula(auxiliaryNode1, auxiliaryNode2);
      model.setFormula(auxiliaryNode2, auxiliaryNode1);
      
      try {
         model.validateModel();
         fail();
      } catch (AuxiliaryNodesCycleDependencyException e) {
         assertEquals("The model's auxiliary nodes have a cycle dependency.", e.getMessage());
      } catch (NoFormulaException e) {
         fail();
      } catch (NoLevelNodeException e) {
         fail();
      } catch (RateNodeFlowException e) {
         fail();
      } catch (UselessNodeException e) {
         fail();
      }
   }
   
   /**
    * Tests the method {@link de.uka.aifb.com.systemDynamics.model.Model#validateModel()}.
    */
   public void testValidateModel5() {
      model.createLevelNode("Level node", 0);
      
      // uselss constant node -> WRONG
      ConstantNode constantNode = null;
      constantNode = model.createConstantNode("Constant node", 0);
      
      try {
         model.validateModel();
         fail();
      } catch (AuxiliaryNodesCycleDependencyException e) {
         fail();
      } catch (NoFormulaException e) {
         fail();
      } catch (NoLevelNodeException e) {
         fail();
      } catch (RateNodeFlowException e) {
         fail();
      } catch (UselessNodeException e) {
         assertEquals("There is a useless node in the model.", e.getMessage());
         assertTrue(e.getUselessNode() == constantNode);
      }
   }
   
   /**
    * Tests the method {@link de.uka.aifb.com.systemDynamics.model.Model#validateModel()}.
    */
   public void testValidateModel6() {
      LevelNode levelNode = model.createLevelNode("Level node", 0);
      
      // uselss auxiliary node -> WRONG
      AuxiliaryNode auxiliaryNode = model.createAuxiliaryNode("Auxiliary node");
      model.setFormula(auxiliaryNode, levelNode);
      
      try {
         model.validateModel();
         fail();
      } catch (AuxiliaryNodesCycleDependencyException e) {
         fail();
      } catch (NoFormulaException e) {
         fail();
      } catch (NoLevelNodeException e) {
         fail();
      } catch (RateNodeFlowException e) {
         fail();
      } catch (UselessNodeException e) {
         assertEquals("There is a useless node in the model.", e.getMessage());
         assertTrue(e.getUselessNode() == auxiliaryNode);
      }
   }
   
   /**
    * Tests the method {@link de.uka.aifb.com.systemDynamics.model.Model#validateModel()}.
    */
   public void testValidateModel7() {
      // valid model -> CORRECT
      
      LevelNode levelNode1 = model.createLevelNode("Level node 1", 0);
      LevelNode levelNode2 = model.createLevelNode("Level node 2", 0);
      RateNode rateNode = model.createRateNode("Rate node");
      AuxiliaryNode auxiliaryNode1 = model.createAuxiliaryNode("Auxiliary node 1");
      AuxiliaryNode auxiliaryNode2 = model.createAuxiliaryNode("Auxiliary node 2");
      AuxiliaryNode auxiliaryNode3 = model.createAuxiliaryNode("Auxiliary node 3");
      ConstantNode constantNode1 = model.createConstantNode("Constant node 1", 0);
      ConstantNode constantNode2 = model.createConstantNode("Constant node 2", 0);
      
      model.addFlowFromLevelNode2RateNode(levelNode1, rateNode);
      model.addFlowFromRateNode2LevelNode(rateNode, levelNode2);
      model.setFormula(rateNode, auxiliaryNode1);
      model.setFormula(auxiliaryNode1, new ASTPlus(auxiliaryNode2, auxiliaryNode3));
      model.setFormula(auxiliaryNode2, new ASTPlus(constantNode1, auxiliaryNode3));
      model.setFormula(auxiliaryNode3, constantNode2);
      
      try {
         model.validateModel();
      } catch (AuxiliaryNodesCycleDependencyException e) {
         fail();
      } catch (NoFormulaException e) {
         fail();
      } catch (NoLevelNodeException e) {
         fail();
      } catch (RateNodeFlowException e) {
         fail();
      } catch (UselessNodeException e) {
         fail();
      }
   }
   
   /**
    * Tests the method
    * {@link de.uka.aifb.com.systemDynamics.model.Model#validateModelAndSetUnchangeable()}.
    */
   public void testValidateModelAndSetUnchangeable() {
      // test with valid model
      
      LevelNode levelNode1 = model.createLevelNode("Level node 1", 0);
      LevelNode levelNode2 = model.createLevelNode("Level node 2", 0);
      RateNode rateNode = model.createRateNode("Rate node");
      AuxiliaryNode auxiliaryNode1 = model.createAuxiliaryNode("Auxiliary node 1");
      AuxiliaryNode auxiliaryNode2 = model.createAuxiliaryNode("Auxiliary node 2");
      AuxiliaryNode auxiliaryNode3 = model.createAuxiliaryNode("Auxiliary node 3");
      ConstantNode constantNode1 = model.createConstantNode("Constant node 1", 0);
      ConstantNode constantNode2 = model.createConstantNode("Constant node 2", 0);
      
      model.addFlowFromLevelNode2RateNode(levelNode1, rateNode);
      model.addFlowFromRateNode2LevelNode(rateNode, levelNode2);
      model.setFormula(rateNode, auxiliaryNode1);
      model.setFormula(auxiliaryNode1, new ASTPlus(auxiliaryNode2, auxiliaryNode3));
      model.setFormula(auxiliaryNode2, new ASTPlus(constantNode1, auxiliaryNode3));
      model.setFormula(auxiliaryNode3, constantNode2);
      
      try {
         model.validateModelAndSetUnchangeable();
      } catch (AuxiliaryNodesCycleDependencyException e) {
         fail();
      } catch (NoFormulaException e) {
         fail();
      } catch (NoLevelNodeException e) {
         fail();
      } catch (RateNodeFlowException e) {
         fail();
      } catch (UselessNodeException e) {
         fail();
      }
      
      Boolean isChangeable = null;
      try {
         isChangeable = (Boolean)PrivateAccessor.getField(model, "isChangeable");
      } catch (Throwable t) {
         fail();
      }
      
      assertFalse(isChangeable);
   }
   
   /**
    * Tests the method {@link de.uka.aifb.com.systemDynamics.model.Model#computeNextValues()}. 
    */
   public void testComputeNextValues1() {
      // method invokation without having called 'validateModelAndSetUnchangeable()' first
      try {
         model.computeNextValues();
         fail();
      } catch (ModelStillChangeableException e) {
         // do nothing
      }
   }
   
   /**
    * Tests the method {@link de.uka.aifb.com.systemDynamics.model.Model#computeNextValues()}. 
    */
   public void testComputeNextValues2() {
      // model with one level node and only one incoming flow
      
      LevelNode levelNode = model.createLevelNode("Level node", 0);
      RateNode rateNode = model.createRateNode("Rate node");
      ConstantNode constantNode = model.createConstantNode("Constant node", 1);
      SourceSinkNode sourceSinkNode = model.createSourceSinkNode();
      model.addFlowFromSourceSinkNode2RateNode(sourceSinkNode, rateNode);
      model.addFlowFromRateNode2LevelNode(rateNode, levelNode);
      model.setFormula(rateNode, constantNode);
      
      // validate model and set unchangeable
      try {
         model.validateModelAndSetUnchangeable();
      } catch (Exception e) {
         fail();
      }
      
      // 1. compute next values
      model.computeNextValues();
      assertTrue(levelNode.getCurrentValue() == 1);
      
      // 2. compute next values
      model.computeNextValues();
      assertTrue(levelNode.getCurrentValue() == 2);
      
      // 3. compute next values
      model.computeNextValues();
      assertTrue(levelNode.getCurrentValue() == 3);
      
      // 4. compute next values
      model.computeNextValues();
      assertTrue(levelNode.getCurrentValue() == 4);
      
      // 5. compute next values
      model.computeNextValues();
      assertTrue(levelNode.getCurrentValue() == 5);
   }
   
   /**
    * Tests the method {@link de.uka.aifb.com.systemDynamics.model.Model#computeNextValues()}. 
    */
   public void testComputeNextValues3() {
      // model with one level one and only one outgoing flow
      
      LevelNode levelNode = model.createLevelNode("Level node", 0);
      RateNode rateNode = model.createRateNode("Rate node");
      ConstantNode constantNode = model.createConstantNode("Constant node", 1);
      SourceSinkNode sourceSinkNode = model.createSourceSinkNode();
      model.addFlowFromLevelNode2RateNode(levelNode, rateNode);
      model.addFlowFromRateNode2SourceSinkNode(rateNode, sourceSinkNode);
      model.setFormula(rateNode, constantNode);
      
      // validate model and set unchangeable
      try {
         model.validateModelAndSetUnchangeable();
      } catch (Exception e) {
         fail();
      }
      
      // 1. compute next values
      model.computeNextValues();
      assertTrue(levelNode.getCurrentValue() == -1);
      
      // 2. compute next values
      model.computeNextValues();
      assertTrue(levelNode.getCurrentValue() == -2);
      
      // 3. compute next values
      model.computeNextValues();
      assertTrue(levelNode.getCurrentValue() == -3);
      
      // 4. compute next values
      model.computeNextValues();
      assertTrue(levelNode.getCurrentValue() == -4);
      
      // 5. compute next values
      model.computeNextValues();
      assertTrue(levelNode.getCurrentValue() == -5);
   }
   
   /**
    * Tests the method {@link de.uka.aifb.com.systemDynamics.model.Model#computeNextValues()}. 
    */
   public void testComputeNextValues4() {
      // model with one level one and only one outgoing flow
      
      LevelNode levelNode = model.createLevelNode("Level node", 0);
      RateNode rateNode = model.createRateNode("Rate node");
      ConstantNode constantNode = model.createConstantNode("Constant node", 1);
      SourceSinkNode sourceSinkNode = model.createSourceSinkNode();
      model.addFlowFromLevelNode2RateNode(levelNode, rateNode);
      model.addFlowFromRateNode2SourceSinkNode(rateNode, sourceSinkNode);
      model.setFormula(rateNode, constantNode);
      
      // validate model and set unchangeable
      try {
         model.validateModelAndSetUnchangeable();
      } catch (Exception e) {
         fail();
      }
      
      // 1. compute next values
      model.computeNextValues();
      assertTrue(levelNode.getCurrentValue() == -1);
      
      // 2. compute next values
      model.computeNextValues();
      assertTrue(levelNode.getCurrentValue() == -2);
      
      // 3. compute next values
      model.computeNextValues();
      assertTrue(levelNode.getCurrentValue() == -3);
      
      // 4. compute next values
      model.computeNextValues();
      assertTrue(levelNode.getCurrentValue() == -4);
      
      // 5. compute next values
      model.computeNextValues();
      assertTrue(levelNode.getCurrentValue() == -5);
   }
   
   /**
    * Tests the method {@link de.uka.aifb.com.systemDynamics.model.Model#computeNextValues()}. 
    */
   public void testComputeNextValues5() {
      // more "complex" model
      
      LevelNode levelNode1 = model.createLevelNode("Level node 1", 10);
      LevelNode levelNode2 = model.createLevelNode("Level node 2", 0);
      RateNode rateNode = model.createRateNode("Rate node");
      AuxiliaryNode auxiliaryNode1 = model.createAuxiliaryNode("Auxiliary node 1");
      AuxiliaryNode auxiliaryNode2 = model.createAuxiliaryNode("Auxiliary node 2");
      AuxiliaryNode auxiliaryNode3 = model.createAuxiliaryNode("Auxiliary node 3");
      ConstantNode constantNode1 = model.createConstantNode("Constant node 1", 1);
      ConstantNode constantNode2 = model.createConstantNode("Constant node 2", 2);
      
      model.addFlowFromLevelNode2RateNode(levelNode1, rateNode);
      model.addFlowFromRateNode2LevelNode(rateNode, levelNode2);
      model.setFormula(rateNode, auxiliaryNode1);
      model.setFormula(auxiliaryNode1, new ASTPlus(auxiliaryNode2, auxiliaryNode3));
      model.setFormula(auxiliaryNode2, new ASTPlus(constantNode1, auxiliaryNode3));
      model.setFormula(auxiliaryNode3, constantNode2);
      
      // validate model and set unchangeable
      try {
         model.validateModelAndSetUnchangeable();
      } catch (Exception e) {
         fail();
      }
      
      // 1. compute next values
      model.computeNextValues();
      assertTrue(levelNode1.getCurrentValue() == 5);
      assertTrue(levelNode2.getCurrentValue() == 5);
      
      // 2. compute next values
      model.computeNextValues();
      assertTrue(levelNode1.getCurrentValue() == 0);
      assertTrue(levelNode2.getCurrentValue() == 10);
      
      // 3. compute next values
      model.computeNextValues();
      assertTrue(levelNode1.getCurrentValue() == -5);
      assertTrue(levelNode2.getCurrentValue() == 15);
      
      // 4. compute next values
      model.computeNextValues();
      assertTrue(levelNode1.getCurrentValue() == -10);
      assertTrue(levelNode2.getCurrentValue() == 20);
      
      // 5. compute next values
      model.computeNextValues();
      assertTrue(levelNode1.getCurrentValue() == -15);
      assertTrue(levelNode2.getCurrentValue() == 25);
   }
   
   /**
    * Tests the method {@link de.uka.aifb.com.systemDynamics.model.Model#computeNextValues()}. 
    */
   public void testComputeNextValues6() {
      // population dynamics model
      
      LevelNode levelNodeChildren = model.createLevelNode("Children", 10);
      LevelNode levelNodeParents = model.createLevelNode("Parents", 10);
      LevelNode levelNodeSeniors = model.createLevelNode("Seniors", 10);
      RateNode rateNodeBirths = model.createRateNode("Births");
      RateNode rateNodeChildrenParents = model.createRateNode("Children -> Parents");
      RateNode rateNodeParentsSeniors = model.createRateNode("Parents -> Seniors");
      RateNode rateNodeDeathChildren = model.createRateNode("Death children");
      RateNode rateNodeDeathParents = model.createRateNode("Death parents");
      RateNode rateNodeDeathSeniors = model.createRateNode("Death seniors");
      ConstantNode constantNodeDeathRateChildren = model.createConstantNode("Death rate children", 0.004);
      ConstantNode constantNodeDeathRateParents = model.createConstantNode("Death rate parents", 0.005);
      ConstantNode constantNodeDeathRateSeniors = model.createConstantNode("Death rate seniors", 0.025);
      ConstantNode constantNodeBirthRate = model.createConstantNode("Birth rate", 1.25);
      ConstantNode constantNodeRatioWomen = model.createConstantNode("RatioWomen", 0.5);
      ConstantNode constantNodeDurationChildren = model.createConstantNode("Duration children", 1.0/16);
      ConstantNode constantNodeDurationParents = model.createConstantNode("Duration parents", 1.0/29);
      SourceSinkNode sourceSinkNode = model.createSourceSinkNode();
            
      model.addFlowFromSourceSinkNode2RateNode(sourceSinkNode, rateNodeBirths);
      model.addFlowFromRateNode2LevelNode(rateNodeBirths, levelNodeChildren);
      model.addFlowFromLevelNode2RateNode(levelNodeChildren, rateNodeChildrenParents);
      model.addFlowFromLevelNode2RateNode(levelNodeChildren, rateNodeDeathChildren);
      model.addFlowFromRateNode2SourceSinkNode(rateNodeDeathChildren, sourceSinkNode);
      model.addFlowFromRateNode2LevelNode(rateNodeChildrenParents, levelNodeParents);
      model.addFlowFromLevelNode2RateNode(levelNodeParents, rateNodeParentsSeniors);
      model.addFlowFromLevelNode2RateNode(levelNodeParents, rateNodeDeathParents);
      model.addFlowFromRateNode2SourceSinkNode(rateNodeDeathParents, sourceSinkNode);
      model.addFlowFromRateNode2LevelNode(rateNodeParentsSeniors, levelNodeSeniors);
      model.addFlowFromLevelNode2RateNode(levelNodeSeniors, rateNodeDeathSeniors);
      model.addFlowFromRateNode2SourceSinkNode(rateNodeDeathSeniors, sourceSinkNode);
      
      model.setFormula(rateNodeBirths, new ASTMultiply(new ASTMultiply(new ASTMultiply(constantNodeBirthRate, constantNodeRatioWomen), constantNodeDurationParents), levelNodeParents));
      model.setFormula(rateNodeChildrenParents, new ASTMultiply(constantNodeDurationChildren, levelNodeChildren));
      model.setFormula(rateNodeDeathChildren, new ASTMultiply(constantNodeDeathRateChildren, levelNodeChildren));
      model.setFormula(rateNodeParentsSeniors, new ASTMultiply(constantNodeDurationParents, levelNodeParents));
      model.setFormula(rateNodeDeathParents, new ASTMultiply(constantNodeDeathRateParents, levelNodeParents));
      model.setFormula(rateNodeDeathSeniors, new ASTMultiply(constantNodeDeathRateSeniors, levelNodeSeniors));
            
      // validate model and set unchangeable
      try {
         model.validateModelAndSetUnchangeable();
      } catch (Exception e) {
         fail();
      }
      
      // 1. compute next values
      model.computeNextValues();
      assertTrue(Math.abs(levelNodeChildren.getCurrentValue() - 9.5505) < 0.0001);
      assertTrue(Math.abs(levelNodeParents.getCurrentValue() - 10.2302) < 0.0001);
      assertTrue(Math.abs(levelNodeSeniors.getCurrentValue() - 10.0948) < 0.0001);
            
      // 2. compute next values
      model.computeNextValues();
      assertTrue(Math.abs(levelNodeChildren.getCurrentValue() - 9.1359) < 0.0001);
      assertTrue(Math.abs(levelNodeParents.getCurrentValue() - 10.4232) < 0.0001);
      assertTrue(Math.abs(levelNodeSeniors.getCurrentValue() - 10.1952) < 0.0001);
      
      // 3. compute next values
      model.computeNextValues();
      assertTrue(Math.abs(levelNodeChildren.getCurrentValue() - 8.7530) < 0.0001);
      assertTrue(Math.abs(levelNodeParents.getCurrentValue() - 10.5826) < 0.0001);
      assertTrue(Math.abs(levelNodeSeniors.getCurrentValue() - 10.2998) < 0.0001);
      
      // 4. compute next values
      model.computeNextValues();
      assertTrue(Math.abs(levelNodeChildren.getCurrentValue() - 8.3990) < 0.0001);
      assertTrue(Math.abs(levelNodeParents.getCurrentValue() - 10.7119) < 0.0001);
      assertTrue(Math.abs(levelNodeSeniors.getCurrentValue() - 10.4072) < 0.0001);
      
      // 5. compute next values
      model.computeNextValues();
      assertTrue(Math.abs(levelNodeChildren.getCurrentValue() - 8.0713) < 0.0001);
      assertTrue(Math.abs(levelNodeParents.getCurrentValue() - 10.8139) < 0.0001);
      assertTrue(Math.abs(levelNodeSeniors.getCurrentValue() - 10.5164) < 0.0001);
   }
   
   /**
    * Tests the method
    * {@link de.uka.aifb.com.systemDynamics.model.Model#haveAuxiliaryNodesCycleDependency()}.
    */
   public void testHaveAuxiliaryNodesCycleDependency() {
      // (1) model without any nodes -> return 'false'
      Boolean returnedValue = null;
      
      try {
         returnedValue = (Boolean)PrivateAccessor.invoke(model, "haveAuxiliaryNodesCycleDependency",
                                                         null, null);
      } catch (Throwable t) {
         fail();
      }
      
      assertFalse(returnedValue);
      
      // (2) only one auxiliary node, nothing else in model -> return 'false'
      AuxiliaryNode auxiliaryNodeA = model.createAuxiliaryNode("Auxiliary node 1");
      
      try {
         returnedValue = (Boolean)PrivateAccessor.invoke(model, "haveAuxiliaryNodesCycleDependency",
                                                         null, null);
      } catch (Throwable t) {
         fail();
      }
      
      assertFalse(returnedValue);
      
      // (3) two auxiliary nodes with cycle dependency -> return 'true'
      AuxiliaryNode auxiliaryNodeB = model.createAuxiliaryNode("Auxiliary node 2");
      model.setFormula(auxiliaryNodeA, auxiliaryNodeB);
      model.setFormula(auxiliaryNodeB, auxiliaryNodeA);
      
      try {
         returnedValue = (Boolean)PrivateAccessor.invoke(model, "haveAuxiliaryNodesCycleDependency",
                                                         null, null);
      } catch (Throwable t) {
         fail();
      }
      
      assertTrue(returnedValue);
      
      // (4) "normel" model -> return 'false'
      model.setFormula(auxiliaryNodeA, null);  // remove cycle
      model.setFormula(auxiliaryNodeB, null);  // remove cycle
      
      LevelNode levelNode1 = model.createLevelNode("Level node 1", 0);
      LevelNode levelNode2 = model.createLevelNode("Level node 2", 0);
      RateNode rateNode = model.createRateNode("Rate node");
      ConstantNode constantNode1 = model.createConstantNode("Constant node 1", 0);
      ConstantNode constantNode2 = model.createConstantNode("Constant node 2", 0);
      AuxiliaryNode auxiliaryNode1 = model.createAuxiliaryNode("Auxiliary node 1");
      AuxiliaryNode auxiliaryNode2 = model.createAuxiliaryNode("Auxiliary node 2");
      AuxiliaryNode auxiliaryNode3 = model.createAuxiliaryNode("Auxiliary node 3");
      model.addFlowFromLevelNode2RateNode(levelNode1, rateNode);
      model.addFlowFromRateNode2LevelNode(rateNode, levelNode2);
      model.setFormula(rateNode, auxiliaryNode1);
      model.setFormula(auxiliaryNode1, new ASTPlus(auxiliaryNode2, auxiliaryNode3));
      model.setFormula(auxiliaryNode2, new ASTPlus(constantNode1, auxiliaryNode3));
      model.setFormula(auxiliaryNode3, constantNode2);
      
      try {
         returnedValue = (Boolean)PrivateAccessor.invoke(model, "haveAuxiliaryNodesCycleDependency",
                                                         null, null);
      } catch (Throwable t) {
         fail();
      }
      
      assertFalse(returnedValue);
   }
   
   /**
    * Tests the method
    * {@link de.uka.aifb.com.systemDynamics.model.Model#getAllNodesLevelNodesDependOn()}.
    */
   public void testGetAllNodesLevelNodesDependOn() {
      HashSet<AbstractNode> nodes = null;
      
      // (1) model without any nodes -> empty set
      try {
         nodes = (HashSet<AbstractNode>)PrivateAccessor.invoke(model, "getAllNodesLevelNodesDependOn",
                                                               null, null);
      } catch (Throwable t) {
         fail();
      }
      assertTrue(nodes.isEmpty());
      
      // (2) model with nodes, but no level nodes -> empty set
      RateNode rateNode = model.createRateNode("Rate node");
      AuxiliaryNode auxiliaryNode1 = model.createAuxiliaryNode("Auxiliary node 1");
      ConstantNode constantNode1 = model.createConstantNode("Constant node 1", 0);
      ConstantNode constantNode2 = model.createConstantNode("Constant node 2", 0);
      model.setFormula(auxiliaryNode1, constantNode2);
      model.setFormula(rateNode, new ASTPlus(constantNode1, auxiliaryNode1));
      
      try {
         nodes = (HashSet<AbstractNode>)PrivateAccessor.invoke(model, "getAllNodesLevelNodesDependOn",
                                                               null, null);
      } catch (Throwable t) {
         fail();
      }
      assertTrue(nodes.isEmpty());
      
      // (3) auxiliary node depends on level node (but not vice versa)
      LevelNode levelNodeA = model.createLevelNode("Level node A", 0);
      AuxiliaryNode auxiliaryNodeA = model.createAuxiliaryNode("Auxiliary node A");
      model.setFormula(auxiliaryNodeA, levelNodeA);
      
      try {
         nodes = (HashSet<AbstractNode>)PrivateAccessor.invoke(model, "getAllNodesLevelNodesDependOn",
                                                               null, null);
      } catch (Throwable t) {
         fail();
      }
      assertTrue(nodes.isEmpty());
      
      // (4) "normal" model with level nodes
      LevelNode levelNode1 = model.createLevelNode("Level node 1", 0);
      LevelNode levelNode2 = model.createLevelNode("Level node 2", 0);
      model.addFlowFromLevelNode2RateNode(levelNode1, rateNode);
      model.addFlowFromRateNode2LevelNode(rateNode, levelNode2);
      
      try {
         nodes = (HashSet<AbstractNode>)PrivateAccessor.invoke(model, "getAllNodesLevelNodesDependOn",
                                                               null, null);
      } catch (Throwable t) {
         fail();
      }
      assertTrue(nodes.size() == 4);
      assertTrue(nodes.contains(rateNode));
      assertTrue(nodes.contains(auxiliaryNode1));
      assertTrue(nodes.contains(constantNode1));
      assertTrue(nodes.contains(constantNode2));
      
      // (5) cycle in dependencies of auxiliary nodes (incorrect model)
      AuxiliaryNode auxiliaryNode2 = model.createAuxiliaryNode("Auxiliary node 2");
      model.setFormula(auxiliaryNode1, auxiliaryNode2);  // this overwrites formula of auxiliaryNode1
      model.setFormula(auxiliaryNode2, auxiliaryNode1);
      
      try {
         nodes = (HashSet<AbstractNode>)PrivateAccessor.invoke(model, "getAllNodesLevelNodesDependOn",
                                                               null, null);
      } catch (Throwable t) {
         fail();
      }
      assertTrue(nodes.size() == 4);
      assertTrue(nodes.contains(rateNode));
      assertTrue(nodes.contains(constantNode1));
      assertTrue(nodes.contains(auxiliaryNode1));
      assertTrue(nodes.contains(auxiliaryNode2));
   }
   
   /**
    * Tests the method
    * {@link de.uka.aifb.com.systemDynamics.model.Model#getAdjacentListOfAuxiliaryNodes()}.
    */
   public void testGetAdjacentListOfAuxiliaryNodes() {
      HashMap<AuxiliaryNode, HashSet<AuxiliaryNode>> adjacentList = null;
      
      // (1) empty model -> empty adjacent list
      try {
         adjacentList =
            (HashMap<AuxiliaryNode, HashSet<AuxiliaryNode>>)PrivateAccessor.invoke(model,
                                                                  "getAdjacentListOfAuxiliaryNodes",
                                                                  null, null);
      } catch (Throwable t) {
         fail();
      }
      assertTrue(adjacentList.isEmpty());
      
      // (2) only one auxiliary node, nothing else in model -> empty adjacent list
      AuxiliaryNode auxiliaryNodeA = model.createAuxiliaryNode("Auxiliary node 1");
      
      try {
         adjacentList =
            (HashMap<AuxiliaryNode, HashSet<AuxiliaryNode>>)PrivateAccessor.invoke(model,
                                                                  "getAdjacentListOfAuxiliaryNodes",
                                                                  null, null);
      } catch (Throwable t) {
         fail();
      }
      
      assertTrue(adjacentList.isEmpty());
      
      // (3) two auxiliary nodes with cycle dependency
      AuxiliaryNode auxiliaryNodeB = model.createAuxiliaryNode("Auxiliary node 2");
      model.setFormula(auxiliaryNodeA, auxiliaryNodeB);
      model.setFormula(auxiliaryNodeB, auxiliaryNodeA);
      
      try {
         adjacentList =
            (HashMap<AuxiliaryNode, HashSet<AuxiliaryNode>>)PrivateAccessor.invoke(model,
                                                                  "getAdjacentListOfAuxiliaryNodes",
                                                                  null, null);
      } catch (Throwable t) {
         fail();
      }
      
      assertTrue(adjacentList.size() == 2);
      assertTrue(adjacentList.get(auxiliaryNodeA).size() == 1);
      assertTrue(adjacentList.get(auxiliaryNodeA).contains(auxiliaryNodeB));
      assertTrue(adjacentList.get(auxiliaryNodeB).size() == 1);
      assertTrue(adjacentList.get(auxiliaryNodeB).contains(auxiliaryNodeA));
      
      
      // (4) "normel" model
      LevelNode levelNode1 = model.createLevelNode("Level node 1", 0);
      LevelNode levelNode2 = model.createLevelNode("Level node 2", 0);
      RateNode rateNode = model.createRateNode("Rate node");
      ConstantNode constantNode1 = model.createConstantNode("Constant node 1", 0);
      ConstantNode constantNode2 = model.createConstantNode("Constant node 2", 0);
      AuxiliaryNode auxiliaryNode1 = model.createAuxiliaryNode("Auxiliary node 1");
      AuxiliaryNode auxiliaryNode2 = model.createAuxiliaryNode("Auxiliary node 2");
      AuxiliaryNode auxiliaryNode3 = model.createAuxiliaryNode("Auxiliary node 3");
      model.addFlowFromLevelNode2RateNode(levelNode1, rateNode);
      model.addFlowFromRateNode2LevelNode(rateNode, levelNode2);
      model.setFormula(rateNode, auxiliaryNode1);
      model.setFormula(auxiliaryNode1, new ASTPlus(auxiliaryNode2, auxiliaryNode3));
      model.setFormula(auxiliaryNode2, new ASTPlus(constantNode1, auxiliaryNode3));
      model.setFormula(auxiliaryNode3, constantNode2);
      
      try {
         adjacentList =
            (HashMap<AuxiliaryNode, HashSet<AuxiliaryNode>>)PrivateAccessor.invoke(model,
                                                                  "getAdjacentListOfAuxiliaryNodes",
                                                                  null, null);
      } catch (Throwable t) {
         fail();
      }
      
      assertTrue(adjacentList.size() == 4);
      assertTrue(adjacentList.get(auxiliaryNodeA).size() == 1);
      assertTrue(adjacentList.get(auxiliaryNodeA).contains(auxiliaryNodeB));
      assertTrue(adjacentList.get(auxiliaryNodeB).size() == 1);
      assertTrue(adjacentList.get(auxiliaryNodeB).contains(auxiliaryNodeA));
      assertTrue(adjacentList.get(auxiliaryNode2).size() == 1);
      assertTrue(adjacentList.get(auxiliaryNode2).contains(auxiliaryNode1));
      assertTrue(adjacentList.get(auxiliaryNode3).size() == 2);
      assertTrue(adjacentList.get(auxiliaryNode3).contains(auxiliaryNode1));
      assertTrue(adjacentList.get(auxiliaryNode3).contains(auxiliaryNode2));
   }
   
   /**
    * Tests the method
    * {@link de.uka.aifb.com.systemDynamics.model.Model#getNumberOfPredecessorsMap()}.
    */
   public void testGetNumberOfPredecessorsMap() {
      HashMap<AuxiliaryNode, Integer> mapping = null;
      
      // (1) empty model -> empty map
      try {
         mapping =
            (HashMap<AuxiliaryNode, Integer>)PrivateAccessor.invoke(model,
                                                                    "getNumberOfPredecessorsMap",
                                                                    null, null);
      } catch (Throwable t) {
         fail();
      }
      assertTrue(mapping.isEmpty());
      
      // (2) only one auxiliary node, nothing else in model
      AuxiliaryNode auxiliaryNodeA = model.createAuxiliaryNode("Auxiliary node 1");
      
      try {
         mapping =
            (HashMap<AuxiliaryNode, Integer>)PrivateAccessor.invoke(model,
                                                                    "getNumberOfPredecessorsMap",
                                                                    null, null);
      } catch (Throwable t) {
         fail();
      }
      
      assertTrue(mapping.size() == 1);
      assertTrue(mapping.get(auxiliaryNodeA) == 0);
      
      // (3) two auxiliary nodes with cycle dependency
      AuxiliaryNode auxiliaryNodeB = model.createAuxiliaryNode("Auxiliary node 2");
      model.setFormula(auxiliaryNodeA, auxiliaryNodeB);
      model.setFormula(auxiliaryNodeB, auxiliaryNodeA);
      
      try {
         mapping =
            (HashMap<AuxiliaryNode, Integer>)PrivateAccessor.invoke(model,
                                                                    "getNumberOfPredecessorsMap",
                                                                    null, null);
      } catch (Throwable t) {
         fail();
      }
      
      assertTrue(mapping.size() == 2);
      assertTrue(mapping.get(auxiliaryNodeA) == 1);
      assertTrue(mapping.get(auxiliaryNodeB) == 1);
      
      // (4) "normel" model
      LevelNode levelNode1 = model.createLevelNode("Level node 1", 0);
      LevelNode levelNode2 = model.createLevelNode("Level node 2", 0);
      RateNode rateNode = model.createRateNode("Rate node");
      ConstantNode constantNode1 = model.createConstantNode("Constant node 1", 0);
      ConstantNode constantNode2 = model.createConstantNode("Constant node 2", 0);
      AuxiliaryNode auxiliaryNode1 = model.createAuxiliaryNode("Auxiliary node 1");
      AuxiliaryNode auxiliaryNode2 = model.createAuxiliaryNode("Auxiliary node 2");
      AuxiliaryNode auxiliaryNode3 = model.createAuxiliaryNode("Auxiliary node 3");
      model.addFlowFromLevelNode2RateNode(levelNode1, rateNode);
      model.addFlowFromRateNode2LevelNode(rateNode, levelNode2);
      model.setFormula(rateNode, auxiliaryNode1);
      model.setFormula(auxiliaryNode1, new ASTPlus(auxiliaryNode2, auxiliaryNode3));
      model.setFormula(auxiliaryNode2, constantNode1);
      model.setFormula(auxiliaryNode3, constantNode2);
      
      try {
         mapping =
            (HashMap<AuxiliaryNode, Integer>)PrivateAccessor.invoke(model,
                                                                    "getNumberOfPredecessorsMap",
                                                                    null, null);
      } catch (Throwable t) {
         fail();
      }
      
      assertTrue(mapping.size() == 5);
      assertTrue(mapping.get(auxiliaryNodeA) == 1);
      assertTrue(mapping.get(auxiliaryNodeB) == 1);
      assertTrue(mapping.get(auxiliaryNode1) == 2);
      assertTrue(mapping.get(auxiliaryNode2) == 0);
      assertTrue(mapping.get(auxiliaryNode3) == 0);
   }
}