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
import de.uka.aifb.com.systemDynamics.xml.*;
import java.io.IOException;
import java.util.HashMap;
import junit.framework.*;
import junitx.util.PrivateAccessor;
import org.xml.sax.*;

/**
 * This class implements a test case for the class
 * {@link de.uka.aifb.com.systemDynamics.xml.XMLModelReader}.
 * 
 * @author Joachim Melcher, University of Karlsruhe, AIFB
 * @version 1.0
 */
public class XMLModelReaderTestCase extends TestCase {
   
   public static Test suite() {  
      return new TestSuite(XMLModelReaderTestCase.class);
   }
   
   /**
    * Tests the method
    * {@link de.uka.aifb.com.systemDynamics.xml.XMLModelReader#readXMLModel(String)}.
    */
   public void testReadXMLModel1() {
      // parameter 'null' -> WRONG
      try {
         XMLModelReader.readXMLModel(null);
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'fileName' must not be null.", e.getMessage());
      } catch (Exception e) {
         fail();
      }
   
      // non existing file -> WRONG
      try {
         XMLModelReader.readXMLModel("ABC_XYZ");
         fail();
      } catch (XMLModelReaderWriterException e) {
         assertTrue(e.getException() instanceof IOException);
      } catch (Exception e) {
         fail();
      }
   }
   
   /**
    * Tests the method
    * {@link de.uka.aifb.com.systemDynamics.xml.XMLModelReader#readXMLModel(String)}.
    */
   public void testReadXMLModel2() {
      // not wellformed XML file -> WRONG
      try {
         XMLModelReader.readXMLModel("./testResources/not_wellformed_xml.xml");
         fail();
      } catch (XMLModelReaderWriterException e) {
         Exception innerException = e.getException();
         assertTrue(innerException instanceof SAXException);
         assertTrue(innerException instanceof SAXParseException);
         assertEquals("The element type \"Nodes\" must be terminated by the matching "
                    + "end-tag \"</Nodes>\".", innerException.getMessage());
      } catch (Exception e) {
         fail();
      }
   }
   
   /**
    * Tests the method
    * {@link de.uka.aifb.com.systemDynamics.xml.XMLModelReader#readXMLModel(String)}.
    */
   public void testReadXMLModel3() {
      // not valide model -> WRONG
      try {
         XMLModelReader.readXMLModel("./testResources/not_valide_model.xml");
         fail();
      } catch (XMLModelReaderWriterException e) {
         Exception innerException = e.getException();
         assertTrue(innerException instanceof SAXException);
         assertTrue(innerException instanceof SAXParseException);
         assertEquals("cvc-complex-type.4: Attribute 'startValue' must appear on element 'LevelNode'.",
                      innerException.getMessage());
      } catch (Exception e) {
         fail();
      }
   }
   
   /**
    * Tests the method
    * {@link de.uka.aifb.com.systemDynamics.xml.XMLModelReader#readXMLModel(String)}.
    */
   public void testReadXMLModel4() {
      // XML Schema compliant, but UselessNodeException -> WRONG
      try {
         XMLModelReader.readXMLModel("./testResources/model_XMLUselessNodeException.xml");
         fail();
      } catch (XMLUselessNodeException e) {
         assertEquals("There is a useless node in the model.", e.getMessage());
         assertEquals("CN0001", e.getXMLNodeId());
      } catch (Exception e) {
         fail();
      }
   }
   
   /**
    * Tests the method
    * {@link de.uka.aifb.com.systemDynamics.xml.XMLModelReader#readXMLModel(String)}.
    */
   public void testReadXMLModel5() {
      // XML Schema compliant, but RateNodeFlowException -> WRONG
      try {
         XMLModelReader.readXMLModel("./testResources/model_XMLRateNodeFlowException.xml");
         fail();
      } catch (XMLRateNodeFlowException e) {
         assertEquals("A rate node has no incoming or no outgoing flow.", e.getMessage());
         assertEquals("RN0001", e.getXMLNodeId());
      } catch (Exception e) {
         fail();
      }
   }
   
   /**
    * Tests the method
    * {@link de.uka.aifb.com.systemDynamics.xml.XMLModelReader#readXMLModel(String)}.
    */
   public void testReadXMLModel6() {
      // XML Schema compliant, but AuxiliaryNodesCycleDependencyException -> WRONG
      try {
         XMLModelReader.readXMLModel("./testResources/model_AuxiliaryNodesCycleDependencyException.xml");
         fail();
      } catch (AuxiliaryNodesCycleDependencyException e) {
         assertEquals("The model's auxiliary nodes have a cycle dependency.", e.getMessage());
      } catch (Exception e) {
         fail();
      }
   }
   
   /**
    * Tests the method
    * {@link de.uka.aifb.com.systemDynamics.xml.XMLModelReader#readXMLModel(String)}.
    */
   public void testReadXMLModel7() {
      // start value of level node out of range -> WRONG
      try {
         XMLModelReader.readXMLModel("./testResources/model_XMLNodeParameterOutOfRangeException.xml");
         fail();
      } catch (XMLNodeParameterOutOfRangeException e) {
         assertEquals("LN0001", e.getXMLNodeId());
         assertTrue(e.getMinValue() == LevelNode.MIN_START_VALUE);
         assertTrue(e.getMaxValue() == LevelNode.MAX_START_VALUE);
      } catch (Exception e) {
         fail();
      }
   }
   
   /**
    * Tests the method
    * {@link de.uka.aifb.com.systemDynamics.xml.XMLModelReader#readXMLModel(String)}.
    */
   public void testReadXMLModel8() {
      // existing and correct file, but very small model -> CORRECT
      Model model = null;
      try {
         model = XMLModelReader.readXMLModel("./testResources/model_small.xml");
      } catch (Exception e) {
         fail();
      }
      
      assertEquals("Model name", model.getModelName());
      
      // check level nodes
      assertTrue(model.getLevelNodes().size() == 1);
      boolean levelNode1Found = false;
      for (LevelNode levelNode : model.getLevelNodes()) {
         if (levelNode.getNodeName().equals("Level node 1")) {
            assertTrue(levelNode.getStartValue() == 0);
            levelNode1Found = true;
         }
      }
      assertTrue(levelNode1Found);
   }
    
   /**
    * Tests the method
    * {@link de.uka.aifb.com.systemDynamics.xml.XMLModelReader#readXMLModel(String)}.
    */
   public void testReadXMLModel9() {
      // existing and correct file -> CORRECT
      Model model = null;
      try {
         model = XMLModelReader.readXMLModel("./testResources/model.xml");
      } catch (Exception e) {
         fail();
      }
      
      assertEquals("Model name", model.getModelName());
      
      HashMap<String, AbstractNode> nodeName2node = new HashMap<String, AbstractNode>();
      
      // check level nodes
      assertTrue(model.getLevelNodes().size() == 2);
      boolean levelNode1Found = false;
      boolean levelNode2Found = false;
      for (LevelNode levelNode : model.getLevelNodes()) {
         if (levelNode.getNodeName().equals("Level node 1")) {
            assertTrue(levelNode.getStartValue() == 0);
            nodeName2node.put(levelNode.getNodeName(), levelNode);
            levelNode1Found = true;
         }
         if (levelNode.getNodeName().equals("Level node 2")) {
            assertTrue(levelNode.getStartValue() == 5);
            nodeName2node.put(levelNode.getNodeName(), levelNode);
            levelNode2Found = true;
         }
      }
      assertTrue(levelNode1Found && levelNode2Found);
      
      // check source/sink node
      assertTrue(model.getSourceSinkNodes().size() == 1);
      SourceSinkNode sourceSinkNode = model.getSourceSinkNodes().iterator().next();      
      
      // check rate nodes
      assertTrue(model.getRateNodes().size() == 2);
      boolean rateNode1Found = false;
      boolean rateNode2Found = false;
      for (RateNode rateNode : model.getRateNodes()) {
         if (rateNode.getNodeName().equals("Rate node 1")) {
            nodeName2node.put(rateNode.getNodeName(), rateNode);
            rateNode1Found = true;
         }
         if (rateNode.getNodeName().equals("Rate node 2")) {
            nodeName2node.put(rateNode.getNodeName(), rateNode);
            rateNode2Found = true;
         }
      }
      assertTrue(rateNode1Found && rateNode2Found);
      
      // check auxiliary nodes
      assertTrue(model.getAuxiliaryNodes().size() == 2);
      boolean auxiliaryNode1Found = false;
      boolean auxiliaryNode2Found = false;
      for (AuxiliaryNode auxiliaryNode : model.getAuxiliaryNodes()) {
         if (auxiliaryNode.getNodeName().equals("Auxiliary node 1")) {
            nodeName2node.put(auxiliaryNode.getNodeName(), auxiliaryNode);
            auxiliaryNode1Found = true;
         }
         if (auxiliaryNode.getNodeName().equals("Auxiliary node 2")) {
            nodeName2node.put(auxiliaryNode.getNodeName(), auxiliaryNode);
            auxiliaryNode2Found = true;
         }
      }
      assertTrue(auxiliaryNode1Found && auxiliaryNode2Found);
      
      // check constant nodes
      assertTrue(model.getConstantNodes().size() == 2);
      boolean constantNode1Found = false;
      boolean constantNode2Found = false;
      for (ConstantNode constantNode : model.getConstantNodes()) {
         if (constantNode.getNodeName().equals("Constant node 1")) {
            assertTrue(constantNode.getConstantValue() == 1);
            nodeName2node.put(constantNode.getNodeName(), constantNode);
            constantNode1Found = true;
         }
         if (constantNode.getNodeName().equals("Constant node 2")) {
            assertTrue(constantNode.getConstantValue() == -1);
            nodeName2node.put(constantNode.getNodeName(), constantNode);
            constantNode2Found = true;
         }
      }
      assertTrue(constantNode1Found && constantNode2Found);
      
      AuxiliaryNode auxiliaryNode1 = (AuxiliaryNode)nodeName2node.get("Auxiliary node 1");
      AuxiliaryNode auxiliaryNode2 = (AuxiliaryNode)nodeName2node.get("Auxiliary node 2");
      ConstantNode constantNode1 = (ConstantNode)nodeName2node.get("Constant node 1");
      ConstantNode constantNode2 = (ConstantNode)nodeName2node.get("Constant node 2");
      LevelNode levelNode1 = (LevelNode)nodeName2node.get("Level node 1");
      LevelNode levelNode2 = (LevelNode)nodeName2node.get("Level node 2");
      RateNode rateNode1 = (RateNode)nodeName2node.get("Rate node 1");
      RateNode rateNode2 = (RateNode)nodeName2node.get("Rate node 2");
      
      // check formulas...
      // ... formula of rate node 1
      ASTElement rateNode1Formula = rateNode1.getFormula();
      assertTrue(rateNode1Formula instanceof ASTPlus);
      try {
         assertTrue(PrivateAccessor.getField(rateNode1Formula, "rightElement") == constantNode1);
      } catch (Throwable t) {
         fail();
      }
      ASTPlus subFormula1 = null;
      try {
         subFormula1 = (ASTPlus)PrivateAccessor.getField(rateNode1Formula, "leftElement");
      } catch (Throwable t) {
         fail();
      }
      try {
         assertTrue(PrivateAccessor.getField(subFormula1, "leftElement") == auxiliaryNode1);
      } catch (Throwable t) {
         fail();
      }
      try {
         assertTrue(PrivateAccessor.getField(subFormula1, "rightElement") == auxiliaryNode2);
      } catch (Throwable t) {
         fail();
      }
      // ... formula of rate node 2
      ASTElement rateNode2Formula = rateNode2.getFormula();
      assertTrue(rateNode2Formula instanceof ASTMultiply);
      try {
         assertTrue(PrivateAccessor.getField(rateNode2Formula, "leftElement") == constantNode2);
      } catch (Throwable t) {
         fail();
      }
      ASTMinus subFormula2 = null;
      try {
         subFormula2 = (ASTMinus)PrivateAccessor.getField(rateNode2Formula, "rightElement");
      } catch (Throwable t) {
         fail();
      }
      try {
         assertTrue(PrivateAccessor.getField(subFormula2, "leftElement") == levelNode1);
      } catch (Throwable t) {
         fail();
      }
      try {
         assertTrue(PrivateAccessor.getField(subFormula2, "rightElement") == auxiliaryNode1);
      } catch (Throwable t) {
         fail();
      }
      // ... formula of auxiliary node 1
      assertTrue(auxiliaryNode1.getFormula() == levelNode2);
      // ... formula of auxiliary node 2
      assertTrue(auxiliaryNode2.getFormula() == constantNode2);
      
      // check flows
      assertTrue(levelNode1.getIncomingFlows().isEmpty());
      assertTrue(levelNode1.getOutgoingFlows().size() == 1);
      assertTrue(levelNode1.getOutgoingFlows().contains(rateNode1));
      
      assertTrue(levelNode2.getIncomingFlows().size() == 1);
      assertTrue(levelNode2.getIncomingFlows().contains(rateNode1));
      assertTrue(levelNode2.getOutgoingFlows().size() == 1);
      assertTrue(levelNode2.getOutgoingFlows().contains(rateNode2));
      
      assertTrue(sourceSinkNode.getIncomingFlows().size() == 1);
      assertTrue(sourceSinkNode.getIncomingFlows().contains(rateNode2));
      assertTrue(sourceSinkNode.getOutgoingFlows().isEmpty());
   }
}