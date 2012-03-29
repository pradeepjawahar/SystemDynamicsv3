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
 * {@link de.uka.aifb.com.systemDynamics.model.AuxiliaryNode}.
 * 
 * @author Joachim Melcher, University of Karlsruhe, AIFB
 * @version 1.0
 */
public class AuxiliaryNodeTestCase extends TestCase {

   private static final String INITIAL_NODE_NAME = "NODE NAME";
   
   private AuxiliaryNode auxiliaryNode;
   
   public static Test suite() {  
      return new TestSuite(AuxiliaryNodeTestCase.class);
   }
   
   @Override
protected void setUp() throws Exception {
      auxiliaryNode = createAuxiliaryNode(INITIAL_NODE_NAME);
   }

   @Override
protected void tearDown() throws Exception {
      auxiliaryNode = null;
   }
   
   /**
    * Tests the constructor.
    */
   public void testAuxiliaryNode() {
      // (1) null as node name -> WRONG!
      try {
         createAuxiliaryNode(null);
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'nodeName' must not be null.", e.getMessage());
      }
      
      // (2) correct parameter -> CORRECT!
      createAuxiliaryNode(INITIAL_NODE_NAME);
   }
   
   /**
    * Tests the methods
    * {@link de.uka.aifb.com.systemDynamics.model.AuxiliaryNode#setFormula(ASTElement)},
    * {@link de.uka.aifb.com.systemDynamics.model.AuxiliaryNode#hasFormula()} and
    * {@link de.uka.aifb.com.systemDynamics.model.AuxiliaryNode#getFormula()}.
    */
   public void testFormula() {
      LevelNode levelNode1 = createLevelNode("Level node 1", 0);
      LevelNode levelNode2 = createLevelNode("Level node 2", 0);
      LevelNode levelNode3 = createLevelNode("Level node 3", 0);
      
      // (1) no formula at beginning
      assertFalse(auxiliaryNode.hasFormula());
      
      // (2) set first formula...
      ASTPlus subFormula = new ASTPlus(levelNode2, levelNode2);
      ASTPlus firstFormula = new ASTPlus(levelNode1, subFormula);
      try {
         PrivateAccessor.invoke(auxiliaryNode, "setFormula", new Class[] { ASTElement.class },
                                new Object[] { firstFormula });
      } catch (Throwable t) {
         fail();
      }
      assertTrue(auxiliaryNode.hasFormula());
      
      // ... check that clone is stored...
      subFormula = new ASTPlus(levelNode3, levelNode3);
      ASTPlus internFormula = null;
      try {
         internFormula = (ASTPlus)PrivateAccessor.getField(auxiliaryNode, "formula");
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
      ASTPlus returnedFormula = (ASTPlus)auxiliaryNode.getFormula();
      ASTPlus newSubFormula = new ASTPlus(levelNode3, levelNode3);
      try {
         PrivateAccessor.setField(returnedFormula, "rightElement", newSubFormula);
      } catch (Throwable t) {
         fail();
      }
      internFormula = null;
      try {
         internFormula = (ASTPlus)PrivateAccessor.getField(auxiliaryNode, "formula");
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
         PrivateAccessor.invoke(auxiliaryNode, "setFormula", new Class[] { ASTElement.class },
                                new Object[] { levelNode1 });
      } catch (Throwable t) {
         fail();
      }
      assertTrue(auxiliaryNode.hasFormula());
      assertTrue(auxiliaryNode.getFormula() == levelNode1);
      
      // (4) formula 'null'
      try {
         PrivateAccessor.invoke(auxiliaryNode, "setFormula", new Class[] { ASTElement.class },
                                new Object[] { null });
      } catch (Throwable t) {
         fail();
      }
      assertFalse(auxiliaryNode.hasFormula());
   }
   
   /**
    * Tests the method
    * {@link de.uka.aifb.com.systemDynamics.model.AuxiliaryNode#getAllNodesThisOneDependsOn()}.
    */
   public void testGetAllNodesThisOneDependsOn() {
      // (1) without formula no dependencies
      assertTrue(auxiliaryNode.getAllNodesThisOneDependsOn().isEmpty());
      
      // (2) check dependencies with formula
      LevelNode levelNode1 = createLevelNode("Level node 1", 0);
      LevelNode levelNode2 = createLevelNode("Level node 2", 0);
      LevelNode levelNode3 = createLevelNode("Level node 3", 0);
      ASTPlus subFormula = new ASTPlus(levelNode2, levelNode3);
      ASTPlus firstFormula = new ASTPlus(levelNode1, subFormula);
      try {
         PrivateAccessor.invoke(auxiliaryNode, "setFormula", new Class[] { ASTElement.class },
                                new Object[] { firstFormula });
      } catch (Throwable t) {
         fail();
      }
      assertTrue(auxiliaryNode.getAllNodesThisOneDependsOn().size() == 3);
      assertTrue(auxiliaryNode.getAllNodesThisOneDependsOn().contains(levelNode1));
      assertTrue(auxiliaryNode.getAllNodesThisOneDependsOn().contains(levelNode2));
      assertTrue(auxiliaryNode.getAllNodesThisOneDependsOn().contains(levelNode3));
   }
   
   /**
    * Tests the methods
    * {@link de.uka.aifb.com.systemDynamics.model.AuxiliaryNode#computeNextValue()} and
    * {@link de.uka.aifb.com.systemDynamics.model.AuxiliaryNode#getCurrentValue()}.
    */
   public void testComputeNextValueAndGetCurrentValue() {
      assertEquals(0.0, auxiliaryNode.getCurrentValue());
      
      // set formula (constant 5)
      ConstantNode formula = null;
      try {
         formula = (ConstantNode)PrivateAccessor.invoke(ConstantNode.class,
                                                        "createConstantNode",
                                                        new Class[] { String.class, double.class },
                                                        new Object[] { "Level node", 5 });
         PrivateAccessor.invoke(auxiliaryNode, "setFormula", new Class[] { ASTElement.class },
                                new Object[] { formula });
      } catch (Throwable t) {
         fail();
      }
      
      // test two time steps
      try {
         PrivateAccessor.invoke(auxiliaryNode, "computeNextValue", null, null);
      } catch (Throwable t) {
         fail();
      }
      assertEquals(5.0, auxiliaryNode.getCurrentValue());
      
      try {
         PrivateAccessor.invoke(auxiliaryNode, "computeNextValue", null, null);
      } catch (Throwable t) {
         fail();
      }
      assertEquals(5.0, auxiliaryNode.getCurrentValue());
   }
   
   /**
    * Tests the methods
    * {@link de.uka.aifb.com.systemDynamics.model.AuxiliaryNode#getNodeName()} and
    * {@link de.uka.aifb.com.systemDynamics.model.AuxiliaryNode#setNodeName(String)}.
    */
   public void testGetAndSetNodeName() {
      assertEquals(auxiliaryNode.getNodeName(), INITIAL_NODE_NAME);
      
      // (1) null as node name -> WRONG!
      try {
         PrivateAccessor.invoke(auxiliaryNode, "setNodeName", new Class[] { String.class },
                                new Object[] { null });
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'nodeName' must not be null.", e.getMessage());
      } catch (Throwable t) {
         fail();
      }
      assertEquals(auxiliaryNode.getNodeName(), INITIAL_NODE_NAME);
      
      // (2) first correct new node name -> CORRECT!
      try {
         PrivateAccessor.invoke(auxiliaryNode, "setNodeName", new Class[] { String.class },
                                new Object[] { "New Node Name 1" });
      } catch (Throwable t) {
         fail();
      }
      assertEquals(auxiliaryNode.getNodeName(), "New Node Name 1");
      
      // (3) second correct value -> CORRECT!
      try {
         PrivateAccessor.invoke(auxiliaryNode, "setNodeName", new Class[] { String.class },
                                new Object[] { "New Node Name 2" });
      } catch (Throwable t) {
         fail();
      }
      assertEquals(auxiliaryNode.getNodeName(), "New Node Name 2");
   }
   
   /**
    * Tests the method {@link de.uka.aifb.com.systemDynamics.model.AuxiliaryNode#evaluate()}.
    */
   public void testEvaluate() {
      assertEquals(0.0, auxiliaryNode.evaluate());
      
      // set formula (constant 5)
      ConstantNode formula = null;
      try {
         formula = (ConstantNode)PrivateAccessor.invoke(ConstantNode.class,
                                                        "createConstantNode",
                                                        new Class[] { String.class, double.class },
                                                        new Object[] { "Level node", 5 });
         PrivateAccessor.invoke(auxiliaryNode, "setFormula", new Class[] { ASTElement.class },
                                new Object[] { formula });
      } catch (Throwable t) {
         fail();
      }
      
      try {
         PrivateAccessor.invoke(auxiliaryNode, "computeNextValue", null, null);
      } catch (Throwable t) {
         fail();
      }
      assertEquals(auxiliaryNode.getCurrentValue(), auxiliaryNode.evaluate());
      
      try {
         PrivateAccessor.invoke(auxiliaryNode, "computeNextValue", null, null);
      } catch (Throwable t) {
         fail();
      }
      assertEquals(auxiliaryNode.getCurrentValue(), auxiliaryNode.evaluate());
   }
   
   /**
    * Tests the method
    * {@link de.uka.aifb.com.systemDynamics.model.AuxiliaryNode#getAllNodesInASTSubtree()}.
    */
   public void testGetAllNodesInASTSubtree() {
      HashSet<AbstractNode> nodeSet = auxiliaryNode.getAllNodesInASTSubtree();
      assertEquals(1, nodeSet.size());
      assertTrue(nodeSet.contains(auxiliaryNode));
   }
   
   /**
    * Tests the method
    * {@link de.uka.aifb.com.systemDynamics.model.AuxiliaryNode#getStringRepresentation()}.
    */
   public void testGetStringRepresentation() {
      assertEquals(INITIAL_NODE_NAME + "(AN)", auxiliaryNode.getStringRepresentation());
   }
   
   /**
    * Tests the method
    * {@link de.uka.aifb.com.systemDynamics.model.AuxiliaryNode#clone()}.
    */
   public void testClone() {
      assertTrue(auxiliaryNode == auxiliaryNode.clone());
   }
   
   /**
    * Tests the method
    * {@link de.uka.aifb.com.systemDynamics.model.AuxiliaryNode#iterator()}.
    */
   public void testIterator() {
      // (1) first iterator
      Iterator<ASTElement> iterator = auxiliaryNode.iterator();
      
      assertTrue(iterator.hasNext());
      assertTrue(iterator.next() == auxiliaryNode);
      assertFalse(iterator.hasNext());
      try {
         iterator.next();
      } catch (NoSuchElementException e) {
         // do nothing
      }
      
      // (2) second iterator
      iterator = auxiliaryNode.iterator();
      
      assertTrue(iterator.hasNext());
      assertTrue(iterator.next() == auxiliaryNode);
      assertFalse(iterator.hasNext());
      try {
         iterator.next();
      } catch (NoSuchElementException e) {
         // do nothing
      }
   }
   
   /**
    * Helper method for invoking the private constructor of class AuxiliaryNode.
    * 
    * @param nodeName node name
    * @return created new instance of class AuxiliaryNode
    */
   private AuxiliaryNode createAuxiliaryNode(String nodeName) {
      AuxiliaryNode node = null;
      try {
         node =
            (AuxiliaryNode)PrivateAccessor.invoke(AuxiliaryNode.class, "createAuxiliaryNode",
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
}
