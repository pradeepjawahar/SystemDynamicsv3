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
 * {@link de.uka.aifb.com.systemDynamics.model.ASTPlus}.
 * 
 * @author Joachim Melcher, University of Karlsruhe, AIFB
 * @version 1.0
 */
public class ASTPlusTestCase extends TestCase {
   
   private ASTPlus astPlus;
   
   public static Test suite() {  
      return new TestSuite(ASTPlusTestCase.class);
   }
   
   /**
    * Tests the constructor.
    */
   public void testASTPlus() {
      // (1) null as left element -> WRONG
      try {
         new ASTPlus(null, createConstantNode("Constant Node 1", 1));
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'firstSummand' must not be null.", e.getMessage());
      }
      
      // (2) null as right element -> WRONG
      try {
         new ASTPlus(createConstantNode("Constant Node 2", 2), null);
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'secondSummand' must not be null.", e.getMessage());
      }
      
      // (3) correct parameters
      new ASTPlus(createConstantNode("Constant Node 3", 3), createConstantNode("ConstantNode 4", 4));
   }
   
   /**
    * Tests the method
    * {@link de.uka.aifb.com.systemDynamics.model.ASTPlus#evaluate()}.
    */
   public void testEvaluate() {
      ASTPlus subFormula = new ASTPlus(createConstantNode("B", 2), createConstantNode("C", 3));
      astPlus = new ASTPlus(createConstantNode("A", 1), subFormula);
      assertTrue(astPlus.evaluate() == 6);
   }
   
   /**
    * Tests the method
    * {@link de.uka.aifb.com.systemDynamics.model.ASTPlus#getAllNodesInASTSubtree()}.
    */
   public void testGetAllNodesInASTSubtree() {
      ConstantNode constantNode1 = createConstantNode("A", 1);
      ConstantNode constantNode2 = createConstantNode("B", 1);
      ConstantNode constantNode3 = createConstantNode("C", 1);
      
      ASTPlus subFormula = new ASTPlus(constantNode2, constantNode3);
      astPlus = new ASTPlus(constantNode1, subFormula);
      
      assertTrue(astPlus.getAllNodesInASTSubtree().size() == 3);
      assertTrue(astPlus.getAllNodesInASTSubtree().contains(constantNode1));
      assertTrue(astPlus.getAllNodesInASTSubtree().contains(constantNode2));
      assertTrue(astPlus.getAllNodesInASTSubtree().contains(constantNode3));
   }
   
   /**
    * Tests the method
    * {@link de.uka.aifb.com.systemDynamics.model.ASTPlus#getStringRepresentation()}.
    */
   public void testGetStringRepresentation() {
      ConstantNode constantNode1 = createConstantNode("A", 1);
      ConstantNode constantNode2 = createConstantNode("B", 1);
      ConstantNode constantNode3 = createConstantNode("C", 1);
      ConstantNode constantNode4 = createConstantNode("D", 1);
      
      astPlus = new ASTPlus(constantNode1, constantNode2);
      assertEquals("A(CN) + B(CN)", astPlus.getStringRepresentation());
      
      astPlus = new ASTPlus(constantNode1, new ASTPlus(constantNode2, constantNode3));
      assertEquals("A(CN) + (B(CN) + C(CN))", astPlus.getStringRepresentation());
      
      astPlus = new ASTPlus(new ASTPlus(constantNode1, constantNode2), constantNode3);
      assertEquals("A(CN) + B(CN) + C(CN)", astPlus.getStringRepresentation());
      
      astPlus = new ASTPlus(new ASTPlus(constantNode1, constantNode2), new ASTPlus(constantNode3, constantNode4));
      assertEquals("A(CN) + B(CN) + (C(CN) + D(CN))", astPlus.getStringRepresentation());
   }
   
   /**
    * Tests the method
    * {@link de.uka.aifb.com.systemDynamics.model.ASTPlus#clone()}.
    */
   public void testClone() {
      // (1) construct original ASTPlus instance
      ConstantNode constantNode1 = createConstantNode("A", 1);
      ConstantNode constantNode2 = createConstantNode("B", 1);
      ConstantNode constantNode3 = createConstantNode("C", 1);
      
      ASTPlus subFormula = new ASTPlus(constantNode2, constantNode3);
      astPlus = new ASTPlus(constantNode1, subFormula);
      
      // (2) check that cloned instance is equivalent
      ASTPlus cloned = (ASTPlus)astPlus.clone();
      
      ConstantNode clonedNode = null;
      try {
         clonedNode = (ConstantNode)PrivateAccessor.getField(cloned, "leftElement");
      } catch (NoSuchFieldException e) {
         fail();
      }
      assertTrue(clonedNode == constantNode1);
      
      ASTPlus clonedASTPlus = null;
      try {
         clonedASTPlus = (ASTPlus)PrivateAccessor.getField(cloned, "rightElement");
      } catch (NoSuchFieldException e) {
         fail();
      }
      
      try {
         clonedNode = (ConstantNode)PrivateAccessor.getField(clonedASTPlus, "leftElement");
      } catch (NoSuchFieldException e) {
         fail();
      }
      assertTrue(clonedNode == constantNode2);
      
      try {
         clonedNode = (ConstantNode)PrivateAccessor.getField(clonedASTPlus, "rightElement");
      } catch (NoSuchFieldException e) {
         fail();
      }
      assertTrue(clonedNode == constantNode3);
      
      // (3) check that changes on the cloned instance do not influence original instance
      ASTPlus newSubFormula = new ASTPlus(createConstantNode("X", 9), createConstantNode("Y", 10));
      
      try {
         PrivateAccessor.setField(cloned, "rightElement", newSubFormula);
      } catch (NoSuchFieldException e) {
         fail();
      }
      
      assertTrue(cloned.evaluate() == 20);
      assertTrue(astPlus.evaluate() == 3);
   }
   
   /**
    * Tests the method
    * {@link de.uka.aifb.com.systemDynamics.model.ASTPlus#iterator()}.
    */
   public void testIterator() {
      ConstantNode constantNode1 = createConstantNode("A", 1);
      ConstantNode constantNode2 = createConstantNode("B", 1);
      ConstantNode constantNode3 = createConstantNode("C", 1);
      
      ASTPlus subFormula = new ASTPlus(constantNode2, constantNode3);
      astPlus = new ASTPlus(constantNode1, subFormula);
      
      // (1) first iterator
      Iterator<ASTElement> iterator = astPlus.iterator();
      
      assertTrue(iterator.hasNext());
      ASTElement nextElement = iterator.next();
      assertTrue(nextElement instanceof ASTPlus);
      assertTrue(nextElement != astPlus);  // clone!
      
      assertTrue(iterator.hasNext());
      assertTrue(iterator.next() == constantNode1);
      
      assertTrue(iterator.hasNext());
      nextElement = iterator.next();
      assertTrue(nextElement instanceof ASTPlus);
      assertTrue(nextElement != subFormula);  // clone!
      
      assertTrue(iterator.hasNext());
      assertTrue(iterator.next() == constantNode2);
      
      assertTrue(iterator.hasNext());
      assertTrue(iterator.next() == constantNode3);
      
      try {
         iterator.next();
      } catch (NoSuchElementException e) {
         // do nothing
      }
      
      // (2) second iterator
      iterator = astPlus.iterator();
      
      assertTrue(iterator.hasNext());
      nextElement = iterator.next();
      assertTrue(nextElement instanceof ASTPlus);
      assertTrue(nextElement != astPlus);  // clone!
      
      assertTrue(iterator.hasNext());
      assertTrue(iterator.next() == constantNode1);
      
      assertTrue(iterator.hasNext());
      nextElement = iterator.next();
      assertTrue(nextElement instanceof ASTPlus);
      assertTrue(nextElement != subFormula);  // clone!
      
      assertTrue(iterator.hasNext());
      assertTrue(iterator.next() == constantNode2);
      
      assertTrue(iterator.hasNext());
      assertTrue(iterator.next() == constantNode3);
      
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
    */
   private ConstantNode createConstantNode(String nodeName, double constantValue) {
      ConstantNode node = null;
      try {
         node =
            (ConstantNode)PrivateAccessor.invoke(ConstantNode.class, "createConstantNode",
                                              new Class[] { String.class, double.class },
                                              new Object[] { nodeName, constantValue });
      } catch (IllegalArgumentException e) { 
         throw e;
      } catch (Throwable t) {
         // no other error/exception other than IllegalArgumentException possible
         fail();
      }
      return node;
   }
}