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

import de.uka.aifb.com.systemDynamics.SystemDynamics;
import de.uka.aifb.com.systemDynamics.gui.systemDynamicsGraph.*;
import de.uka.aifb.com.systemDynamics.model.*;
import de.uka.aifb.com.systemDynamics.xml.*;
import java.io.*;
import java.util.*;
import javax.swing.JFrame;
import junit.framework.*;

/**
 * This class implements a test case for the class
 * {@link de.uka.aifb.com.systemDynamics.xml.XMLModelWriter}.
 * 
 * @author Joachim Melcher, University of Karlsruhe, AIFB
 * @version 1.0
 */
public class XMLModelWriterTestCase extends TestCase {
   
   private static final String FILE_NAME = "temp_model.xml";
   
   public static Test suite() {  
      return new TestSuite(XMLModelWriterTestCase.class);
   }
   
   /**
    * Tests the method
    * {@link de.uka.aifb.com.systemDynamics.xml.XMLModelWriter#writeXMLModel(Model, String)}.
    */
   public void testWriteXMLModel1() {
      // parameter 'model' = 'null' -> WRONG
      try {
         XMLModelWriter.writeXMLModel(null, FILE_NAME);
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'model' must not be null.", e.getMessage());
      } catch (Exception e) {
         fail();
      }
      
      // parameter 'fileName' = 'null' -> WRONG
      try {
         XMLModelWriter.writeXMLModel(new Model(), null);
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'fileName' must not be null.", e.getMessage());
      } catch (Exception e) {
         fail();
      }
   }
   
   /**
    * Tests the method
    * {@link de.uka.aifb.com.systemDynamics.xml.XMLModelWriter#writeXMLModel(Model, String)}.
    */
   public void testWriteXMLModel2() {
      // model with auxiliary nodes cycle dependency -> WRONG
      Model model = new Model();
      model.setModelName("Model name");
      LevelNode levelNode = model.createLevelNode("Level node", 0);
      SourceSinkNode sourceSinkNode = model.createSourceSinkNode();
      RateNode rateNode = model.createRateNode("Rate node");
      AuxiliaryNode auxiliaryNode1 = model.createAuxiliaryNode("Auxiliary node 1");
      AuxiliaryNode auxiliaryNode2 = model.createAuxiliaryNode("Auxiliary node 2");
      
      model.addFlowFromLevelNode2RateNode(levelNode, rateNode);
      model.addFlowFromRateNode2SourceSinkNode(rateNode, sourceSinkNode);
      model.setFormula(rateNode, auxiliaryNode1);
      model.setFormula(auxiliaryNode1, auxiliaryNode2);
      model.setFormula(auxiliaryNode2, auxiliaryNode1);
      
      try {
         XMLModelWriter.writeXMLModel(model, FILE_NAME);
         fail();
      } catch (AuxiliaryNodesCycleDependencyException e) {
         assertEquals("The model's auxiliary nodes have a cycle dependency.", e.getMessage());
      } catch (Exception e) {
         fail();
      }
   }
   
   /**
    * Tests the method
    * {@link de.uka.aifb.com.systemDynamics.xml.XMLModelWriter#writeXMLModel(Model, String)}.
    */
   public void testWriteXMLModel3() {
      // model without level nodes -> WRONG
      Model model = new Model();
      model.setModelName("Model name");
      
      try {
         XMLModelWriter.writeXMLModel(model, FILE_NAME);
         fail();
      } catch (NoLevelNodeException e) {
         assertEquals("The System Dynamics model has no level node.", e.getMessage());
      } catch (Exception e) {
         fail();
      }
   }
   
   /**
    * Tests the method
    * {@link de.uka.aifb.com.systemDynamics.xml.XMLModelWriter#writeXMLModel(Model, String)}.
    */
   public void testWriteXMLModel4() {
      // model with rate node without formula -> WRONG
      Model model = new Model();
      model.setModelName("Model name");
      LevelNode levelNode = model.createLevelNode("Level node", 0);
      SourceSinkNode sourceSinkNode = model.createSourceSinkNode();
      RateNode rateNode = model.createRateNode("Rate node");
      model.addFlowFromLevelNode2RateNode(levelNode, rateNode);
      model.addFlowFromRateNode2SourceSinkNode(rateNode, sourceSinkNode);
      
      try {
         XMLModelWriter.writeXMLModel(model, FILE_NAME);
         fail();
      } catch (NoFormulaException e) {
         assertEquals("A node has no formula.", e.getMessage());
         assertTrue(e.getNodeWithourFormula() == rateNode);
      } catch (Exception e) {
         fail();
      }
   }
   
   /**
    * Tests the method
    * {@link de.uka.aifb.com.systemDynamics.xml.XMLModelWriter#writeXMLModel(Model, String)}.
    */
   public void testWriteXMLModel5() {
      // model with rate node without flow -> WRONG
      Model model = new Model();
      model.setModelName("Model name");
      LevelNode levelNode = model.createLevelNode("Level node", 0);
      RateNode rateNode = model.createRateNode("Rate node");
      model.setFormula(rateNode, levelNode);
      
      try {
         XMLModelWriter.writeXMLModel(model, FILE_NAME);
         fail();
      } catch (RateNodeFlowException e) {
         assertEquals("A rate node has no incoming or no outgoing flow.", e.getMessage());
         assertTrue(e.getProblematicRateNode() == rateNode);
      } catch (Exception e) {
         fail();
      }
   }
   
   /**
    * Tests the method
    * {@link de.uka.aifb.com.systemDynamics.xml.XMLModelWriter#writeXMLModel(Model, String)}.
    */
   public void testWriteXMLModel6() {
      // model with useless nodes -> WRONG
      Model model = new Model();
      model.setModelName("Model name");
      model.createLevelNode("Level node", 0);
      ConstantNode constantNode = model.createConstantNode("Constant node", 0);
            
      try {
         XMLModelWriter.writeXMLModel(model, FILE_NAME);
         fail();
      } catch (UselessNodeException e) {
         assertEquals("There is a useless node in the model.", e.getMessage());
         assertTrue(e.getUselessNode() == constantNode);
      } catch (Exception e) {
         fail();
      }
   }
   
   /**
    * Tests the method
    * {@link de.uka.aifb.com.systemDynamics.xml.XMLModelWriter#writeXMLModel(Model, String)}.
    */
   public void testWriteXMLModel7() {
      Model model = new Model();
      model.setModelName("Model name");
      model.createLevelNode("Level node", 0);
      
      try {
         XMLModelWriter.writeXMLModel(model, FILE_NAME);
      } catch (Exception e) {
         fail();
      }
      
      Model importedModel = null;
      
      // is output XML Schema compliant?
      try {
         importedModel = XMLModelReader.readXMLModel(FILE_NAME);
      } catch (Exception e) {
         fail();
      }
      
      assertTrue(importedModel.getLevelNodes().size() == 1);
      for (LevelNode levelNode : importedModel.getLevelNodes()) {
         // there is only one level node...
         assertEquals("Level node", levelNode.getNodeName());
         assertTrue(levelNode.getStartValue() == 0);
      }
      
      // delete temporary XML file
      assertTrue(new File(FILE_NAME).delete());
   }
   
   /**
    * Tests the method
    * {@link de.uka.aifb.com.systemDynamics.xml.XMLModelWriter#writeXMLModel(Model, String)}.
    */
   public void testWriteXMLMode8() {
      Model model = new Model();
      model.setModelName("Model name");
      
      LevelNode levelNodeA = model.createLevelNode("Level node A", 1);
      LevelNode levelNodeB = model.createLevelNode("Level node B", -1.234);
      SourceSinkNode sourceSinkNode = model.createSourceSinkNode();
      RateNode rateNode1 = model.createRateNode("Rate node 1");
      RateNode rateNode2 = model.createRateNode("Rate node 2");
      AuxiliaryNode auxiliaryNodeA = model.createAuxiliaryNode("Auxiliary node A");
      AuxiliaryNode auxiliaryNodeB = model.createAuxiliaryNode("Auxiliary node B");
      AuxiliaryNode auxiliaryNodeC = model.createAuxiliaryNode("Auxiliary node C");
      ConstantNode constantNodeA = model.createConstantNode("Constant node A", 2);
      ConstantNode constantNodeB = model.createConstantNode("Constant node B", -2);
      
      model.addFlowFromLevelNode2RateNode(levelNodeA, rateNode1);
      model.addFlowFromRateNode2LevelNode(rateNode1, levelNodeB);
      model.addFlowFromLevelNode2RateNode(levelNodeB, rateNode2);
      model.addFlowFromRateNode2SourceSinkNode(rateNode2, sourceSinkNode);
      model.setFormula(rateNode1, auxiliaryNodeA);
      model.setFormula(rateNode2, constantNodeB);
      model.setFormula(auxiliaryNodeA, new ASTPlus(auxiliaryNodeB, new ASTMultiply(auxiliaryNodeC, auxiliaryNodeC)));
      model.setFormula(auxiliaryNodeB, new ASTMinus(new ASTPlus(constantNodeA, constantNodeB), auxiliaryNodeC));
      model.setFormula(auxiliaryNodeC, constantNodeB);
      
      try {
         XMLModelWriter.writeXMLModel(model, FILE_NAME);
      } catch (Exception e) {
         fail();
      }
      
      HashMap<AbstractNode, AbstractNode> node2ImportedNode = new HashMap<AbstractNode, AbstractNode>();
      
      Model importedModel = null;
      
      // is output XML Schema compliant?
      try {
         importedModel = XMLModelReader.readXMLModel(FILE_NAME);
      } catch (Exception e) {
         fail();
      }
      
      // compare imported model with original one
      assertTrue(importedModel.getLevelNodes().size() == 2);
      for (LevelNode importedLevelNode : importedModel.getLevelNodes()) {
         if (importedLevelNode.getNodeName().equals("Level node A")) {
            node2ImportedNode.put(levelNodeA, importedLevelNode);
            assertTrue(importedLevelNode.getStartValue() == levelNodeA.getStartValue());
         }
         if (importedLevelNode.getNodeName().equals("Level node B")) {
            node2ImportedNode.put(levelNodeB, importedLevelNode);
            assertTrue(importedLevelNode.getStartValue() == levelNodeB.getStartValue());
         }
      }
      assertTrue(node2ImportedNode.size() == 2);
      
      assertTrue(importedModel.getSourceSinkNodes().size() == 1);
      SourceSinkNode importedSourceSinkNode = importedModel.getSourceSinkNodes().iterator().next();
      
      assertTrue(importedModel.getRateNodes().size() == 2);
      for (RateNode importedRateNode : importedModel.getRateNodes()) {
         if (importedRateNode.getNodeName().equals("Rate node 1")) {
            node2ImportedNode.put(rateNode1, importedRateNode);
         }
         if (importedRateNode.getNodeName().equals("Rate node 2")) {
            node2ImportedNode.put(rateNode2, importedRateNode);
         }
      }
      assertTrue(node2ImportedNode.size() == 4);
      
      assertTrue(importedModel.getAuxiliaryNodes().size() == 3);
      for (AuxiliaryNode importedAuxiliaryNode : importedModel.getAuxiliaryNodes()) {
         if (importedAuxiliaryNode.getNodeName().equals("Auxiliary node A")) {
            node2ImportedNode.put(auxiliaryNodeA, importedAuxiliaryNode);
         }
         if (importedAuxiliaryNode.getNodeName().equals("Auxiliary node B")) {
            node2ImportedNode.put(auxiliaryNodeB, importedAuxiliaryNode);
         }
         if (importedAuxiliaryNode.getNodeName().equals("Auxiliary node C")) {
            node2ImportedNode.put(auxiliaryNodeC, importedAuxiliaryNode);
         }
      }
      assertTrue(node2ImportedNode.size() == 7);
      
      assertTrue(importedModel.getConstantNodes().size() == 2);
      for (ConstantNode importedConstantNode : importedModel.getConstantNodes()) {
         if (importedConstantNode.getNodeName().equals("Constant node A")) {
            node2ImportedNode.put(constantNodeA, importedConstantNode);
            assertTrue(importedConstantNode.getConstantValue() == constantNodeA.getConstantValue());
         }
         if (importedConstantNode.getNodeName().equals("Constant node B")) {
            node2ImportedNode.put(constantNodeB, importedConstantNode);
            assertTrue(importedConstantNode.getConstantValue() == constantNodeB.getConstantValue());
         }
      }
      assertTrue(node2ImportedNode.size() == 9);
      
      LevelNode importedLevelNodeA = (LevelNode)node2ImportedNode.get(levelNodeA);
      LevelNode importedLevelNodeB = (LevelNode)node2ImportedNode.get(levelNodeB);
      RateNode importedRateNode1 = (RateNode)node2ImportedNode.get(rateNode1);
      RateNode importedRateNode2 = (RateNode)node2ImportedNode.get(rateNode2);
      
      assertTrue(importedRateNode1.getFlowSource() == importedLevelNodeA);
      assertTrue(importedRateNode1.getFlowSink() == importedLevelNodeB);
      assertTrue(importedSourceSinkNode.getIncomingFlows().size() == 1);
      assertTrue(importedSourceSinkNode.getIncomingFlows().iterator().next() == importedRateNode2);
      assertTrue(importedSourceSinkNode.getOutgoingFlows().isEmpty());
      
      for (AuxiliaryNode auxiliaryNode : model.getAuxiliaryNodes()) {
         ASTElement formula = auxiliaryNode.getFormula();
         Iterator<ASTElement> formulaIterator = formula.iterator();
         ASTElement importedFormula = ((AuxiliaryNode)node2ImportedNode.get(auxiliaryNode)).getFormula();
         Iterator<ASTElement> importedFormulaIterator = importedFormula.iterator();
         
         while (formulaIterator.hasNext()) {
            ASTElement astElement = formulaIterator.next();
            ASTElement importedASTElement = importedFormulaIterator.next();
            
            if (astElement instanceof AbstractNode) {
               assertTrue(importedASTElement == node2ImportedNode.get(astElement));
            } else {
               // ASTMinus, ASTMultiply or ASTPlus
               assertTrue(importedASTElement.getClass() == astElement.getClass());
            }
         }
      }
      
      for (RateNode rateNode_ : model.getRateNodes()) {
         ASTElement formula = rateNode_.getFormula();
         Iterator<ASTElement> formulaIterator = formula.iterator();
         ASTElement importedFormula = ((RateNode)node2ImportedNode.get(rateNode_)).getFormula();
         Iterator<ASTElement> importedFormulaIterator = importedFormula.iterator();
         
         while (formulaIterator.hasNext()) {
            ASTElement astElement = formulaIterator.next();
            ASTElement importedASTElement = importedFormulaIterator.next();
            
            if (astElement instanceof AbstractNode) {
               assertTrue(importedASTElement == node2ImportedNode.get(astElement));
            } else {
               // ASTMinus, ASTMultiply or ASTPlus
               assertTrue(importedASTElement.getClass() == astElement.getClass());
            }
         }
      }
      
      // delete temporary XML file
      assertTrue(new File(FILE_NAME).delete());
   }
   
   /**
    * Tests the method
    * {@link de.uka.aifb.com.systemDynamics.xml.XMLModelWriter#writeXMLSystemDynamicsGraph(SystemDynamicsGraph, Model, LinkedList, LinkedList, LinkedList, String)}.
    */
   public void testWriteXMLGraph() {
      SystemDynamics start = new SystemDynamics();
      
      // create graph
      SystemDynamicsGraph graph = new SystemDynamicsGraph(start, new JFrame());
      
      SourceSinkNodeGraphCell sourceSinkNode1 = graph.createSourceSinkNodeGraphCell(20, 20);
      SourceSinkNodeGraphCell sourceSinkNode2 = graph.createSourceSinkNodeGraphCell(500, 20);
      LevelNodeGraphCell levelNode = graph.createLevelNodeGraphCell("LevelNode", 0, 260, 40);
      RateNodeGraphCell rateNode1 = graph.createRateNodeGraphCell("Rate node 1", 170, 40);
      RateNodeGraphCell rateNode2 = graph.createRateNodeGraphCell("Rate node 2", 400, 40);
      ConstantNodeGraphCell constantNode = graph.createConstantNodeGraphCell("ConstantNode", 0, 275, 200);
      
      graph.setFormula(rateNode1, (ConstantNode)graph.getModelNode(constantNode), false);
      graph.setFormula(rateNode2, (ConstantNode)graph.getModelNode(constantNode), false);
      
      graph.addFlow(sourceSinkNode1, rateNode1);
      graph.addFlow(rateNode1, levelNode);
      graph.addFlow(levelNode, rateNode2);
      graph.addFlow(rateNode2, sourceSinkNode2);
      
      // store graph into XML file
      try {
         graph.storeToXML(FILE_NAME);
      } catch (Exception e) {
         fail();
      }
      
      // load graph from XML file
      try {
         XMLModelReader.readXMLSystemDynamicsGraph(FILE_NAME, start, new JFrame());
      } catch (Exception e) {
         fail();
      }
      
      // delete temporary XML file
      assertTrue(new File(FILE_NAME).delete());
   }
}