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

package de.uka.aifb.com.systemDynamics.xml;

import de.uka.aifb.com.systemDynamics.gui.systemDynamicsGraph.*;
import de.uka.aifb.com.systemDynamics.model.*;
import java.awt.geom.*;
import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;

import org.jgraph.graph.*;
import org.w3c.dom.*;

/*
 * Changes:
 * ========
 *
 * 2007-06-20: writeDocumentToXMLFile(Document, String) was rewritten: root element is search for
 *                (tag name is not important any longer)
 */

/**
 * This class implements an XML output for System Dynamics models to store it in an XML file.
 *
 * @author Joachim Melcher, University of Karlsruhe, AIFB
 * @version 1.1
 */
public class XMLModelWriter {

   private static final String SCHEMA = "http://www.aifb.uni-karlsruhe.de/com/systemDynamics/model-schema";
   private static final String SCHEMA_VERSION = "1.0";

   /**
    * Writes a System Dynamics model into an XML file.
    *
    * @param model model to store
    * @param fileName file name
    * @throws AuxiliaryNodesCycleDependencyException if the model's auxiliary nodes have a cycle
    *                                                dependency
    * @throws NoFormulaException if a rate node or an auxiliary node has no formula
    * @throws NoLevelNodeException if model has no level node
    * @throws RateNodeFlowException if a rate node has no incoming or no outgoing flow
    * @throws UselessNodeException if a node has no influence on a level node
    * @throws XMLModelReaderWriterException if there is any exception (wrapper for inner exception)
    */
   public static void writeXMLModel(Model model, String fileName)
      throws AuxiliaryNodesCycleDependencyException, NoFormulaException, NoLevelNodeException,
             RateNodeFlowException, UselessNodeException, XMLModelReaderWriterException {
      if (model == null) {
         throw new IllegalArgumentException("'model' must not be null.");
      }
      if (fileName == null) {
         throw new IllegalArgumentException("'fileName' must not be null.");
      }

      HashMap<AbstractNode, String> node2Id = createNode2IdMap();

      // create DOM document for model
      Document document = createDocumentForModel(model, node2Id);

      // XML output
      writeDocumentToXMLFile(document, fileName);
   }

   /**
    * Writes a System Dynamics graph into an XML file.
    *
    * @param graph graph to store
    * @param model model to store (must be the same model as stored internally in graph)
    * @param graphNodes list of graph nodes
    * @param flowEdges list of the graph's flow edges
    * @param dependencyEdges list of the graph's dependency edges
    * @param fileName file name
    * @throws AuxiliaryNodesCycleDependencyException if the model's auxiliary nodes have a cycle
    *                                                dependency
    * @throws NoFormulaException if a rate node or an auxiliary node has no formula
    * @throws NoLevelNodeException if model has no level node
    * @throws RateNodeFlowException if a rate node has no incoming or no outgoing flow
    * @throws UselessNodeException if a node has no influence on a level node
    * @throws XMLModelReaderWriterException if there is any exception (wrapper for inner exception)
    */
   public static void writeXMLSystemDynamicsGraph(SystemDynamicsGraph graph,
                                                  Model model,
                                                  LinkedList<DefaultGraphCell> graphNodes,
                                                  LinkedList<FlowEdge> flowEdges,
                                                  LinkedList<DefaultEdge> dependencyEdges,
                                                  String fileName)
      throws AuxiliaryNodesCycleDependencyException, NoFormulaException, NoLevelNodeException,
          RateNodeFlowException, UselessNodeException, XMLModelReaderWriterException {
      if (graph == null) {
         throw new IllegalArgumentException("'graph' must not be null.");
      }
      if (model == null) {
         throw new IllegalArgumentException("'model' must not be null.");
      }
      if (graphNodes == null) {
         throw new IllegalArgumentException("'graphNodes' must not be null.");
      }
      if (flowEdges == null) {
         throw new IllegalArgumentException("'flowEdges' must not be null.");
      }
      if (dependencyEdges == null) {
         throw new IllegalArgumentException("'dependencyEdges' must not be null.");
      }
      if (fileName == null) {
         throw new IllegalArgumentException("'fileName' must not be null.");
      }

      HashMap<AbstractNode, String> node2Id = createNode2IdMap();

      // create DOM document for model
      Document document = createDocumentForModel(model, node2Id);

      // add position information of nodes and additional control points to DOC document
      addPositionInformationToDocument(document, graph, graphNodes, flowEdges, dependencyEdges,
                                       node2Id);

      // XML output
      writeDocumentToXMLFile(document, fileName);
   }

   /**
    * Creates a node to Id mapping.
    *
    * @return node to Id mapping
    */
   protected static HashMap<AbstractNode, String> createNode2IdMap() {
      return new HashMap<AbstractNode, String>();
   }

   /**
    * Creates a DOM document for the specified model. Before, it validates the model.
    *
    * @param model model
    * @param node2Id node to Id mapping
    * @return DOM document
    * @throws AuxiliaryNodesCycleDependencyException if the model's auxiliary nodes have a cycle
    *                                                dependency
    * @throws NoFormulaException if a rate node or an auxiliary node has no formula
    * @throws NoLevelNodeException if model has no level node
    * @throws RateNodeFlowException if a rate node has no incoming or no outgoing flow
    * @throws UselessNodeException if a node has no influence on a level node
    * @throws XMLModelReaderWriterException if there is any exception (wrapper for inner exception)
    */
   protected static Document createDocumentForModel(Model model, HashMap<AbstractNode, String> node2Id)
         throws AuxiliaryNodesCycleDependencyException, NoFormulaException, NoLevelNodeException,
                RateNodeFlowException, UselessNodeException, XMLModelReaderWriterException {
      if (model == null) {
         throw new IllegalArgumentException("'model' must not be null.");
      }
      if (node2Id == null) {
         throw new IllegalArgumentException("'node2Id' must not be null.");
      }

      // check: is model valide?
      model.validateModel();

      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  // can throw FactoryConfiguration Error

      DocumentBuilder builder = null;
      try {
         builder = factory.newDocumentBuilder();
      } catch (ParserConfigurationException e) {
         throw new XMLModelReaderWriterException(e);
      }

      Document document = builder.newDocument();
      Element modelElement = document.createElement("Model");
      modelElement.setAttribute("name", model.getModelName());
      modelElement.setAttribute("schema", SCHEMA);
      modelElement.setAttribute("schemaVersion", SCHEMA_VERSION);
      document.appendChild(modelElement);

      int nextLevelNodeId = 1;
      int nextSourceSinkNodeId = 1;
      int nextRateNodeId = 1;
      int nextAuxiliaryNodeId = 1;
      int nextConstantNodeId = 1;

      // (1) nodes
      Element nodesElement = document.createElement("Nodes");
      modelElement.appendChild(nodesElement);
      // (1a) level nodes
      Element levelNodesElement = document.createElement("LevelNodes");
      nodesElement.appendChild(levelNodesElement);
      for (LevelNode levelNode : model.getLevelNodes()) {
         String id = createId("LN", nextLevelNodeId++);
         node2Id.put(levelNode, id);

         Element levelNodeElement = document.createElement("LevelNode");
         levelNodesElement.appendChild(levelNodeElement);
         levelNodeElement.setAttribute("id", id);
         levelNodeElement.setAttribute("name", levelNode.getNodeName());
         levelNodeElement.setAttribute("startValue", String.valueOf(levelNode.getStartValue()));
      }
      // (1b) source/sink nodes
      if (!model.getSourceSinkNodes().isEmpty()) {
         Element sourceSinkNodesElement = document.createElement("SourceSinkNodes");
         nodesElement.appendChild(sourceSinkNodesElement);
         for (SourceSinkNode sourceSinkNode : model.getSourceSinkNodes()) {
            String id = createId("SN", nextSourceSinkNodeId++);
            node2Id.put(sourceSinkNode, id);

            Element sourceSinkNodeElement = document.createElement("SourceSinkNode");
            sourceSinkNodesElement.appendChild(sourceSinkNodeElement);
            sourceSinkNodeElement.setAttribute("id", id);
         }
      }
      // (1c) rate nodes
      if (!model.getRateNodes().isEmpty()) {
         Element rateNodesElement = document.createElement("RateNodes");
         nodesElement.appendChild(rateNodesElement);
         for (RateNode rateNode : model.getRateNodes()) {
            String id = createId("RN", nextRateNodeId++);
            node2Id.put(rateNode, id);

            Element rateNodeElement = document.createElement("RateNode");
            rateNodesElement.appendChild(rateNodeElement);
            rateNodeElement.setAttribute("id", id);
            rateNodeElement.setAttribute("name", rateNode.getNodeName());
         }
      }
      // (1d) auxiliary nodes
      if (!model.getAuxiliaryNodes().isEmpty()) {
         Element auxiliaryNodesElement = document.createElement("AuxiliaryNodes");
         nodesElement.appendChild(auxiliaryNodesElement);
         for (AuxiliaryNode auxiliaryNode : model.getAuxiliaryNodes()) {
            String id = createId("AN", nextAuxiliaryNodeId++);
            node2Id.put(auxiliaryNode, id);

            Element auxiliaryNodeElement = document.createElement("AuxiliaryNode");
            auxiliaryNodesElement.appendChild(auxiliaryNodeElement);
            auxiliaryNodeElement.setAttribute("id", id);
            auxiliaryNodeElement.setAttribute("name", auxiliaryNode.getNodeName());
         }
      }
      // (1e) constant nodes
      if (!model.getConstantNodes().isEmpty()) {
         Element constantNodesElement = document.createElement("ConstantNodes");
         nodesElement.appendChild(constantNodesElement);
         for (ConstantNode constantNode : model.getConstantNodes()) {
            String id = createId("CN", nextConstantNodeId++);
            node2Id.put(constantNode, id);

            Element constantNodeElement = document.createElement("ConstantNode");
            constantNodesElement.appendChild(constantNodeElement);
            constantNodeElement.setAttribute("id", id);
            constantNodeElement.setAttribute("name", constantNode.getNodeName());
            constantNodeElement.setAttribute("constantValue", String.valueOf(constantNode.getConstantValue()));
         }
      }

      // (2) formulas

      // create XPath object
      XPath xpath = XPathFactory.newInstance().newXPath();

      // (2a) auxiliary nodes
      for (AuxiliaryNode auxiliaryNode : model.getAuxiliaryNodes()) {
         String id = node2Id.get(auxiliaryNode);
         try {
            Element auxiliaryNodeElement =
               (Element)xpath.evaluate("/Model/Nodes/AuxiliaryNodes/AuxiliaryNode[@id='" + id + "']",
                                       document, XPathConstants.NODE);
            Element formulaElement = document.createElement("Formula");
            formulaElement.appendChild(createXMLForFormula(document, auxiliaryNode.getFormula(),
                                                           node2Id));
            auxiliaryNodeElement.appendChild(formulaElement);
         } catch (XPathExpressionException e) {
            // correct xpath expression -> no exception
            throw new XMLModelReaderWriterException(e);
         }
      }
      // (2b) rate nodes
      for (RateNode rateNode : model.getRateNodes()) {
         String id = node2Id.get(rateNode);
         try {
            Element rateNodeElement =
               (Element)xpath.evaluate("/Model/Nodes/RateNodes/RateNode[@id='" + id + "']",
                                       document, XPathConstants.NODE);
            Element formulaElement = document.createElement("Formula");
            formulaElement.appendChild(createXMLForFormula(document, rateNode.getFormula(), node2Id));
            rateNodeElement.appendChild(formulaElement);
         } catch (XPathExpressionException e) {
            // correct xpath expression -> no exception
            throw new XMLModelReaderWriterException(e);
         }
      }

      // (3) flows
      Element flowsElement = null;

      // (3a) from/to level nodes
      for (LevelNode levelNode : model.getLevelNodes()) {
         // outgoing flows
         if (!levelNode.getOutgoingFlows().isEmpty()) {
            if (flowsElement == null) {
               // write <Flows>
               flowsElement = document.createElement("Flows");
               modelElement.appendChild(flowsElement);
            }

            // write outgoing flow(s)
            String levelNodeId = node2Id.get(levelNode);
            for (RateNode rateNode : levelNode.getOutgoingFlows()) {
               String rateNodeId = node2Id.get(rateNode);

               Element levelNode2RateNodeFlowElement = document.createElement("LevelNode2RateNodeFlow");
               flowsElement.appendChild(levelNode2RateNodeFlowElement);
               levelNode2RateNodeFlowElement.setAttribute("fromLevelNodeIdRef", levelNodeId);
               levelNode2RateNodeFlowElement.setAttribute("toRateNodeIdRef", rateNodeId);
            }
         }

         // incoming flows
         if (!levelNode.getIncomingFlows().isEmpty()) {
            if (flowsElement == null) {
               // write <Flows>
               flowsElement = document.createElement("Flows");
               modelElement.appendChild(flowsElement);
            }

            // write incoming flow(s)
            String levelNodeId = node2Id.get(levelNode);
            for (RateNode rateNode : levelNode.getIncomingFlows()) {
               String rateNodeId = node2Id.get(rateNode);

               Element rateNode2LevelNodeFlowElement = document.createElement("RateNode2LevelNodeFlow");
               flowsElement.appendChild(rateNode2LevelNodeFlowElement);
               rateNode2LevelNodeFlowElement.setAttribute("fromRateNodeIdRef", rateNodeId);
               rateNode2LevelNodeFlowElement.setAttribute("toLevelNodeIdRef", levelNodeId);
            }
         }
      }

      // (4a) from/to source/sink nodes
      for (SourceSinkNode sourceSinkNode : model.getSourceSinkNodes()) {
         // outgoing flows
         if (!sourceSinkNode.getOutgoingFlows().isEmpty()) {
            if (flowsElement == null) {
               // write <Flows>
               flowsElement = document.createElement("Flows");
               modelElement.appendChild(flowsElement);
            }

            // write outgoing flow(s)
            String sourceSinkNodeId = node2Id.get(sourceSinkNode);
            for (RateNode rateNode : sourceSinkNode.getOutgoingFlows()) {
               String rateNodeId = node2Id.get(rateNode);

               Element sourceSinkNode2RateNodeFlowElement = document.createElement("SourceSinkNode2RateNodeFlow");
               flowsElement.appendChild(sourceSinkNode2RateNodeFlowElement);
               sourceSinkNode2RateNodeFlowElement.setAttribute("fromSourceSinkNodeIdRef", sourceSinkNodeId);
               sourceSinkNode2RateNodeFlowElement.setAttribute("toRateNodeIdRef", rateNodeId);
            }
         }

         // incoming flows
         if (!sourceSinkNode.getIncomingFlows().isEmpty()) {
            if (flowsElement == null) {
               // write <Flows>
               flowsElement = document.createElement("Flows");
               modelElement.appendChild(flowsElement);
            }

            // write incoming flow(s)
            String sourceSinkNodeId = node2Id.get(sourceSinkNode);
            for (RateNode rateNode : sourceSinkNode.getIncomingFlows()) {
               String rateNodeId = node2Id.get(rateNode);

               Element rateNode2SourceSinkNodeFlowElement = document.createElement("RateNode2SourceSinkNodeFlow");
               flowsElement.appendChild(rateNode2SourceSinkNodeFlowElement);
               rateNode2SourceSinkNodeFlowElement.setAttribute("fromRateNodeIdRef", rateNodeId);
               rateNode2SourceSinkNodeFlowElement.setAttribute("toSourceSinkNodeIdRef", sourceSinkNodeId);
            }
         }
      }

      return document;
   }

   /**
    * Adds postition information of nodes and edges to the specified DOM document.
    *
    * @param document DOM document
    * @param graph graph to store
    * @param graphNodes list of graph nodes
    * @param flowEdges list of the graph's flow edges
    * @param dependencyEdges list of the graph's dependency edges
    * @param node2Id node to Id mapping
    * @throws XMLModelReaderWriterException if there is any exception (wrapper for inner exception)
    */
   protected static void addPositionInformationToDocument(Document document,
                                                          SystemDynamicsGraph graph,
                                                          LinkedList<DefaultGraphCell> graphNodes,
                                                          LinkedList<FlowEdge> flowEdges,
                                                          LinkedList<DefaultEdge> dependencyEdges,
                                                          HashMap<AbstractNode, String>node2Id)
         throws XMLModelReaderWriterException {
      if (document == null) {
         throw new IllegalArgumentException("'document' must not be null.");
      }
      if (graph == null) {
         throw new IllegalArgumentException("'graph' must not be null.");
      }
      if (graphNodes == null) {
         throw new IllegalArgumentException("'graphNodes' must not be null.");
      }
      if (flowEdges == null) {
         throw new IllegalArgumentException("'flowEdges' must not be null.");
      }
      if (dependencyEdges == null) {
         throw new IllegalArgumentException("'dependencyEdges' must not be null.");
      }
      if (node2Id == null) {
         throw new IllegalArgumentException("'node2Id' must not be null.");
      }

      GraphModel graphModel = graph.getModel();

      // create XPath object
      XPath xpath = XPathFactory.newInstance().newXPath();

      // (1) location of nodes
      for (DefaultGraphCell node : graphNodes) {
         Rectangle2D r = GraphConstants.getBounds(node.getAttributes());
         double xCoordinate = r.getX();
         double yCoordinate = r.getY();

         String modelNodeId = node2Id.get(graph.getModelNode(node));

         if (node instanceof AuxiliaryNodeGraphCell) {
            try {
               Element auxiliaryNodeElement =
                  (Element)xpath.evaluate("/Model/Nodes/AuxiliaryNodes/AuxiliaryNode[@id='" + modelNodeId + "']",
                                          document, XPathConstants.NODE);
               auxiliaryNodeElement.setAttribute("xCoordinate", String.valueOf(xCoordinate));
               auxiliaryNodeElement.setAttribute("yCoordinate", String.valueOf(yCoordinate));
            } catch (XPathExpressionException e) {
               // correct xpath expression -> no exception
               throw new XMLModelReaderWriterException(e);
            }
         }
         if (node instanceof ConstantNodeGraphCell) {
            try {
               Element constantNodeElement =
                  (Element)xpath.evaluate("/Model/Nodes/ConstantNodes/ConstantNode[@id='" + modelNodeId + "']",
                                          document, XPathConstants.NODE);
               constantNodeElement.setAttribute("xCoordinate", String.valueOf(xCoordinate));
               constantNodeElement.setAttribute("yCoordinate", String.valueOf(yCoordinate));
            } catch (XPathExpressionException e) {
               // correct xpath expression -> no exception
               throw new XMLModelReaderWriterException(e);
            }
         }
         if (node instanceof LevelNodeGraphCell) {
            try {
               Element levelNodeElement =
                  (Element)xpath.evaluate("/Model/Nodes/LevelNodes/LevelNode[@id='" + modelNodeId + "']",
                                          document, XPathConstants.NODE);
               levelNodeElement.setAttribute("xCoordinate", String.valueOf(xCoordinate));
               levelNodeElement.setAttribute("yCoordinate", String.valueOf(yCoordinate));
            } catch (XPathExpressionException e) {
               // correct xpath expression -> no exception
               throw new XMLModelReaderWriterException(e);
            }
         }
         if (node instanceof RateNodeGraphCell) {
            try {
               Element rateNodeElement =
                  (Element)xpath.evaluate("/Model/Nodes/RateNodes/RateNode[@id='" + modelNodeId + "']",
                                          document, XPathConstants.NODE);
               rateNodeElement.setAttribute("xCoordinate", String.valueOf(xCoordinate));
               rateNodeElement.setAttribute("yCoordinate", String.valueOf(yCoordinate));
            } catch (XPathExpressionException e) {
               // correct xpath expression -> no exception
               throw new XMLModelReaderWriterException(e);
            }
         }
         if (node instanceof SourceSinkNodeGraphCell) {
            try {
               Element sourceSinkNodeElement =
                  (Element)xpath.evaluate("/Model/Nodes/SourceSinkNodes/SourceSinkNode[@id='" + modelNodeId + "']",
                                          document, XPathConstants.NODE);
               sourceSinkNodeElement.setAttribute("xCoordinate", String.valueOf(xCoordinate));
               sourceSinkNodeElement.setAttribute("yCoordinate", String.valueOf(yCoordinate));
            } catch (XPathExpressionException e) {
               // correct xpath expression -> no exception
               throw new XMLModelReaderWriterException(e);
            }
         }
      }

      // (2) location of control points of flows
      for (FlowEdge flowEdge : flowEdges) {
         List<Point2D> points = GraphConstants.getPoints(flowEdge.getAttributes());
         if (points != null && points.size() > 2) {
            // additional control points exist
            Element additionalControlPointsElement = document.createElement("AdditionalControlPoints");
            for (int i = 1; i < points.size() - 1; i++) {
               Element additionalControlPointElement = document.createElement("AdditionalControlPoint");
               double xCoordinate = points.get(i).getX();
               double yCoordinate = points.get(i).getY();
               additionalControlPointElement.setAttribute("xCoordinate", String.valueOf(xCoordinate));
               additionalControlPointElement.setAttribute("yCoordinate", String.valueOf(yCoordinate));
               additionalControlPointsElement.appendChild(additionalControlPointElement);
            }

            DefaultGraphCell edgeSource = (DefaultGraphCell)((DefaultPort)graphModel.getSource(flowEdge)).getParent();
            String edgeSourceId = node2Id.get(graph.getModelNode(edgeSource));
            DefaultGraphCell edgeTarget = (DefaultGraphCell)((DefaultPort)graphModel.getTarget(flowEdge)).getParent();
            String edgeTargetId = node2Id.get(graph.getModelNode(edgeTarget));
            if (edgeSource instanceof LevelNodeGraphCell && edgeTarget instanceof RateNodeGraphCell) {
               try {
                  Element flowElement =
                     (Element)xpath.evaluate("/Model/Flows/LevelNode2RateNodeFlow[@fromLevelNodeIdRef='" + edgeSourceId + "' and @toRateNodeIdRef='" + edgeTargetId + "']",
                                             document, XPathConstants.NODE);
                  flowElement.appendChild(additionalControlPointsElement);
               } catch (XPathExpressionException e) {
                  // correct xpath expression -> no exception
                  throw new XMLModelReaderWriterException(e);
               }
            }
            if (edgeSource instanceof SourceSinkNodeGraphCell && edgeTarget instanceof RateNodeGraphCell) {
               try {
                  Element flowElement =
                     (Element)xpath.evaluate("/Model/Flows/SourceSinkNode2RateNodeFlow[@fromSourceSinkNodeIdRef='" + edgeSourceId + "' and @toRateNodeIdRef='" + edgeTargetId + "']",
                                             document, XPathConstants.NODE);
                  flowElement.appendChild(additionalControlPointsElement);
               } catch (XPathExpressionException e) {
                  // correct xpath expression -> no exception
                  throw new XMLModelReaderWriterException(e);
               }
            }
            if (edgeSource instanceof RateNodeGraphCell && edgeTarget instanceof LevelNodeGraphCell) {
               try {
                  Element flowElement =
                     (Element)xpath.evaluate("/Model/Flows/RateNode2LevelNodeFlow[@fromRateNodeIdRef='" + edgeSourceId + "' and @toLevelNodeIdRef='" + edgeTargetId + "']",
                                             document, XPathConstants.NODE);
                  flowElement.appendChild(additionalControlPointsElement);
               } catch (XPathExpressionException e) {
                  // correct xpath expression -> no exception
                  throw new XMLModelReaderWriterException(e);
               }
            }
            if (edgeSource instanceof RateNodeGraphCell && edgeTarget instanceof SourceSinkNodeGraphCell) {
               try {
                  Element flowElement =
                     (Element)xpath.evaluate("/Model/Flows/RateNode2SourceSinkNodeFlow[@fromRateNodeIdRef='" + edgeSourceId + "' and @toSourceSinkNodeIdRef='" + edgeTargetId + "']",
                                             document, XPathConstants.NODE);
                  flowElement.appendChild(additionalControlPointsElement);
               } catch (XPathExpressionException e) {
                  // correct xpath expression -> no exception
                  throw new XMLModelReaderWriterException(e);
               }
            }
         }
      }

      // (3) location of control points of dependencies
      Element dependenciesElement = document.createElement("Dependencies");
      boolean hasDependencyWithControlPoint = false;
      for (DefaultEdge dependencyEdge : dependencyEdges) {
         List<Point2D> points = GraphConstants.getPoints(dependencyEdge.getAttributes());
         if (points != null && points.size() > 2) {
            // additional control points exist
            hasDependencyWithControlPoint = true;

            Element additionalControlPointsElement = document.createElement("AdditionalControlPoints");
            for (int i = 1; i < points.size() - 1; i++) {
               Element additionalControlPointElement = document.createElement("AdditionalControlPoint");
               double xCoordinate = points.get(i).getX();
               double yCoordinate = points.get(i).getY();
               additionalControlPointElement.setAttribute("xCoordinate", String.valueOf(xCoordinate));
               additionalControlPointElement.setAttribute("yCoordinate", String.valueOf(yCoordinate));
               additionalControlPointsElement.appendChild(additionalControlPointElement);
            }

            DefaultGraphCell edgeSource = (DefaultGraphCell)((DefaultPort)graphModel.getSource(dependencyEdge)).getParent();
            String edgeSourceId = node2Id.get(graph.getModelNode(edgeSource));
            DefaultGraphCell edgeTarget = (DefaultGraphCell)((DefaultPort)graphModel.getTarget(dependencyEdge)).getParent();
            String edgeTargetId = node2Id.get(graph.getModelNode(edgeTarget));
            if (edgeSource instanceof AuxiliaryNodeGraphCell && edgeTarget instanceof AuxiliaryNodeGraphCell) {
               Element dependencyElement = document.createElement("AuxiliaryNode2AuxiliaryNodeDependency");
               dependencyElement.setAttribute("fromAuxiliaryNodeIdRef", edgeSourceId);
               dependencyElement.setAttribute("toAuxiliaryNodeIdRef", edgeTargetId);
               dependencyElement.appendChild(additionalControlPointsElement);
               dependenciesElement.appendChild(dependencyElement);
            }
            if (edgeSource instanceof AuxiliaryNodeGraphCell && edgeTarget instanceof RateNodeGraphCell) {
               Element dependencyElement = document.createElement("AuxiliaryNode2RateNodeDependency");
               dependencyElement.setAttribute("fromAuxiliaryNodeIdRef", edgeSourceId);
               dependencyElement.setAttribute("toRateNodeIdRef", edgeTargetId);
               dependencyElement.appendChild(additionalControlPointsElement);
               dependenciesElement.appendChild(dependencyElement);
            }
            if (edgeSource instanceof ConstantNodeGraphCell && edgeTarget instanceof AuxiliaryNodeGraphCell) {
               Element dependencyElement = document.createElement("ConstantNode2AuxiliaryNodeDependency");
               dependencyElement.setAttribute("fromConstantNodeIdRef", edgeSourceId);
               dependencyElement.setAttribute("toAuxiliaryNodeIdRef", edgeTargetId);
               dependencyElement.appendChild(additionalControlPointsElement);
               dependenciesElement.appendChild(dependencyElement);
            }
            if (edgeSource instanceof ConstantNodeGraphCell && edgeTarget instanceof RateNodeGraphCell) {
               Element dependencyElement = document.createElement("ConstantNode2RateNodeDependency");
               dependencyElement.setAttribute("fromConstantNodeIdRef", edgeSourceId);
               dependencyElement.setAttribute("toRateNodeIdRef", edgeTargetId);
               dependencyElement.appendChild(additionalControlPointsElement);
               dependenciesElement.appendChild(dependencyElement);
            }
            if (edgeSource instanceof LevelNodeGraphCell && edgeTarget instanceof AuxiliaryNodeGraphCell) {
               Element dependencyElement = document.createElement("LevelNode2AuxiliaryNodeDependency");
               dependencyElement.setAttribute("fromLevelNodeIdRef", edgeSourceId);
               dependencyElement.setAttribute("toAuxiliaryNodeIdRef", edgeTargetId);
               dependencyElement.appendChild(additionalControlPointsElement);
               dependenciesElement.appendChild(dependencyElement);
            }
            if (edgeSource instanceof LevelNodeGraphCell && edgeTarget instanceof RateNodeGraphCell) {
               Element dependencyElement = document.createElement("LevelNode2RateNodeDependency");
               dependencyElement.setAttribute("fromLevelNodeIdRef", edgeSourceId);
               dependencyElement.setAttribute("toRateNodeIdRef", edgeTargetId);
               dependencyElement.appendChild(additionalControlPointsElement);
               dependenciesElement.appendChild(dependencyElement);
            }
         }
      }
      if (hasDependencyWithControlPoint) {
         try {
            Element modelElement = (Element)xpath.evaluate("/Model", document, XPathConstants.NODE);
            modelElement.appendChild(dependenciesElement);
         } catch (XPathExpressionException e) {
            // correct xpath expression -> no exception
            throw new XMLModelReaderWriterException(e);
         }
      }
   }

   /**
    * Writes a DOM document into an XML file.
    *
    * @param document DOM document
    * @param fileName file name
    * @throws XMLModelReaderWriterException if there is any exception (wrapper for inner exception)
    */
   protected static void writeDocumentToXMLFile(Document document, String fileName)
      throws XMLModelReaderWriterException {
      if (document == null) {
         throw new IllegalArgumentException("'document' must not be null.");
      }
      if (fileName == null) {
         throw new IllegalArgumentException("'fileName' must not be null.");
      }

      // get root element
      Element rootElement = document.getDocumentElement();

      // XML output
      FileOutputStream fileOutputStream = null;
      OutputStreamWriter outputStreamWriter = null;
      try {
         try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            // work around for indentation (maybe XML parser implementation dependent!)
            transformerFactory.setAttribute("indent-number", new Integer(2));

            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            fileOutputStream = new FileOutputStream(new File(fileName));
            outputStreamWriter = new OutputStreamWriter(fileOutputStream, "utf-8");

            Result result = new StreamResult(outputStreamWriter);
            transformer.transform(new DOMSource(rootElement), result);
         } catch (Exception e) {
            // should not happen -> throw XMLModelWriterException
            throw new XMLModelReaderWriterException(e);
         } finally {
            if (outputStreamWriter != null) {
               outputStreamWriter.close();
            }
            if (fileOutputStream != null) {
               fileOutputStream.close();
            }
         }
      } catch (IOException e) {
         // catch IOException of .close()' -> do nothing
      }
   }

   /**
    * Creates a node Id with the specified prefix and number. Between prefix and number additional
    * zeros are inserted so that the length of the numerical part of the Id is exactly 4.
    *
    * @param prefix prefix
    * @param number number
    * @return <code>String</code> representing the Id
    */
   private static String createId(String prefix, int number) {
      if (prefix == null) {
         throw new IllegalArgumentException("'prefix' must not be null.");
      }

      String numberString = String.valueOf(number);
      StringBuffer id = new StringBuffer();

      int length = numberString.length();
      for (int i = 0; i < 4 - length; i++) {
         id.append("0");
      }
      id.append(numberString);

      return prefix + id;
   }

   /**
    * Creates the XML DOM subtree for the specified formula.
    *
    * @param document DOM document
    * @param formula formula
    * @param node2Id node to Id mapping
    * @return XML DOM subtree for the specified formula
    */
   private static Element createXMLForFormula(Document document, ASTElement formula,
                                       HashMap<AbstractNode, String> node2Id) {
      if (document == null) {
         throw new IllegalArgumentException("'document' must not be null.");
      }
      if (formula == null) {
         throw new IllegalArgumentException("'formula' must not be null.");
      }
      if (node2Id == null) {
         throw new IllegalArgumentException("'node2Id' must not be null.");
      }

      Iterator<ASTElement> iterator = formula.iterator();
      ASTElement rootASTElement = iterator.next();

      if (rootASTElement instanceof AbstractNode) {
         return createXMLForAbstractNode(document, (AbstractNode)rootASTElement, node2Id);
      } else {
         // ASTPlus, ASTMinus or ASTMultiply
         return createXMLForBinaryOperation(document, rootASTElement, iterator, node2Id);
      }
   }

   /**
    * Creates the XML DOM subtree for the specified binary operation.
    *
    * @param document DOM document
    * @param binaryOperation binary operation
    * @param iterator iterator (at current position in formula tree)
    * @param node2Id node to Id mapping
    * @return XML DOM subtree for the specified binary operation
    */
   private static Element createXMLForBinaryOperation(Document document, ASTElement binaryOperation,
                                               Iterator<ASTElement> iterator,
                                               HashMap<AbstractNode, String> node2Id) {
      if (document == null) {
         throw new IllegalArgumentException("'document' must not be null.");
      }
      if (binaryOperation == null) {
         throw new IllegalArgumentException("'binaryOperation' must not be null.");
      }
      if (binaryOperation instanceof AbstractNode) {
         throw new IllegalArgumentException("'binaryOperation' must bot be instance of AbstractNode.");
      }
      if (iterator == null) {
         throw new IllegalArgumentException("'iterator' must not be null.");
      }
      if (node2Id == null) {
         throw new IllegalArgumentException("'node2Id' must not be null.");
      }

      // root of subtree
      Element binaryOperationElement = null;
      if (binaryOperation instanceof ASTMinus) {
         binaryOperationElement = document.createElement("ASTMinus");
      }
      if (binaryOperation instanceof ASTMultiply) {
         binaryOperationElement = document.createElement("ASTMultiply");
      }
      if (binaryOperation instanceof ASTPlus) {
         binaryOperationElement = document.createElement("ASTPlus");
      }

      // left subtree
      ASTElement leftASTElement = iterator.next();
      if (leftASTElement instanceof AbstractNode) {
         binaryOperationElement.appendChild(createXMLForAbstractNode(document, (AbstractNode)leftASTElement, node2Id));
      } else {
         // ASTPlus, ASTMinus or ASTMultiply
         binaryOperationElement.appendChild(createXMLForBinaryOperation(document, leftASTElement, iterator, node2Id));
      }

      // right subtree
      ASTElement rightASTElement = iterator.next();
      if (rightASTElement instanceof AbstractNode) {
         binaryOperationElement.appendChild(createXMLForAbstractNode(document, (AbstractNode)rightASTElement, node2Id));
      } else {
         // ASTPlus, ASTMinus or ASTMultiply
         binaryOperationElement.appendChild(createXMLForBinaryOperation(document, rightASTElement, iterator, node2Id));
      }

      return binaryOperationElement;
   }

   /**
    * Creates the XML DOM subtree (exactly one node) for the specified
    * {@link de.uka.aifb.com.systemDynamics.model.AbstractNode}.
    *
    * @param document DOM document
    * @param node AbstractNode
    * @param node2Id node to Id mapping
    * @return XML DOM subtree (exactly one node) for the specified AbstractNode
    */
   private static Element createXMLForAbstractNode(Document document, AbstractNode node,
                                            HashMap<AbstractNode, String> node2Id) {
      if (document == null) {
         throw new IllegalArgumentException("'document' must not be null.");
      }
      if (node == null) {
         throw new IllegalArgumentException("'node' must not be null.");
      }
      if (node instanceof RateNode) {
         throw new IllegalArgumentException("'node' must not be instance of RateNode.");
      }
      if (node2Id == null) {
         throw new IllegalArgumentException("'node2Id' must not be null.");
      }

      if (node instanceof AuxiliaryNode) {
         Element astAuxiliaryNodeElement = document.createElement("ASTAuxiliaryNode");
         astAuxiliaryNodeElement.setAttribute("auxiliaryNodeIdRef", node2Id.get(node));
         return astAuxiliaryNodeElement;
      }
      if (node instanceof ConstantNode) {
         Element astConstantNodeElement = document.createElement("ASTConstantNode");
         astConstantNodeElement.setAttribute("constantNodeIdRef", node2Id.get(node));
         return astConstantNodeElement;
      }
      if (node instanceof LevelNode) {
         Element astLevelNodeElement = document.createElement("ASTLevelNode");
         astLevelNodeElement.setAttribute("levelNodeIdRef", node2Id.get(node));
         return astLevelNodeElement;
      }

      // not reachable -> just for compiler!
      return null;
   }
}