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

package de.uka.aifb.com.systemDynamics.gui.systemDynamicsGraph;

import de.uka.aifb.com.systemDynamics.SystemDynamics;
import de.uka.aifb.com.systemDynamics.event.SystemDynamicsGraphModifiedEventListener;
import de.uka.aifb.com.systemDynamics.gui.*;
import de.uka.aifb.com.systemDynamics.model.*;
import de.uka.aifb.com.systemDynamics.xml.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import org.jgraph.JGraph;
import org.jgraph.event.*;
import org.jgraph.graph.*;

/**
 * This class implements a specialized JGraph for System Dynamics graphs.
 * <p>
 * Internally, a {@link de.uka.aifb.com.systemDynamics.model.Model} instance is stored with all the
 * information and business logic of the System Dynamics model. There is also a mapping from
 * the graph vertices to the model nodes and vice versa.
 * 
 * @author Joachim Melcher, Institut AIFB, Universitaet Karlsruhe (TH), Germany
 * @version 1.0
 */
public class SystemDynamicsGraph extends JGraph implements GraphModelListener {
   
   private static final long serialVersionUID = 1L;
   
   private SystemDynamics start;
   
   private ResourceBundle messages;
   private Locale locale;
   
   private JFrame frame;
   
   private Model model;
   private HashMap<AbstractNode, DefaultGraphCell> modelNode2graphNode;
   private HashMap<DefaultGraphCell, AbstractNode> graphNode2modelNode;
   
   private LinkedList<SystemDynamicsGraphModifiedEventListener> listeners;
   
   /**
    * Constructor.
    * 
    * @param start {@link de.uka.aifb.com.systemDynamics.SystemDynamics} instance
    * @param frame frame in which this graph will be displayed
    */
   public SystemDynamicsGraph(SystemDynamics start, JFrame frame) {
      super(new DefaultGraphModel());
      
      if (start == null) {
         throw new IllegalArgumentException("'start' must not be null.");
      }
      if (frame == null) {
         throw new IllegalArgumentException("'frame' must not be null.");
      }
      
      this.start = start;
      
      messages = start.getMessages();
      locale = start.getLocale();
      
      this.frame = frame;
      
      listeners = new LinkedList<SystemDynamicsGraphModifiedEventListener>();
      
      GraphLayoutCache cache = new GraphLayoutCache(getModel(), new SystemDynamicsCellViewFactory());
      setGraphLayoutCache(cache);
      
      getModel().addGraphModelListener(this);
      
      // specialized marquee handler (e.g. for context menues)
      setMarqueeHandler(new SystemDynamicsMarqueeHandler(this, start, frame));
      
      // set vertex names not editable
      setEditable(false);
      
      // set edges not disconnectable and not connectable
      setDisconnectable(false);
      setConnectable(false);
      
      // new created vertices and edges should not be selected automatically
      getGraphLayoutCache().setSelectsAllInsertedCells(false);
      getGraphLayoutCache().setSelectsLocalInsertedCells(false);
      
      // workaround: with "port magic" the overridden getPerimeterPoint methods of the vertex views
      // are not used for edges with at least one additional control point
      PortView.allowPortMagic = false;
      
      // register at ToolTipManager to enable tool tips for nodes
      ToolTipManager.sharedInstance().registerComponent(this);
      
      // create System Dynamics model and mappings
      model = new Model();
      modelNode2graphNode = new HashMap<AbstractNode, DefaultGraphCell>();
      graphNode2modelNode = new HashMap<DefaultGraphCell, AbstractNode>();
   }
   
   /**
    * Adds the specified
    * {@link de.uka.aifb.com.systemDynamics.event.SystemDynamicsGraphModifiedEventListener}.
    * 
    * @param listener listener to add
    */
   public void addSystemDynamicsGraphModifiedEventListener(SystemDynamicsGraphModifiedEventListener listener) {
      if (listener == null) {
         throw new IllegalArgumentException("'listener' must not be null.");
      }
      
      listeners.add(listener);
   }
   
   /**
    * Sets the model name.
    * 
    * @param modelName model name
    */
   public void setModelName(String modelName) {
      if (modelName == null) {
         throw new IllegalArgumentException("'modelName' must not be null.");
      }
      model.setModelName(modelName);
      
      // inform listeners
      for (SystemDynamicsGraphModifiedEventListener listener : listeners) {
         listener.performGraphModifiedEvent();
      }
   }
   
   /**
    * Gets the model name.
    * 
    * @return model name
    */
   public String getModelName() {
      return model.getModelName();
   }
   
   /**
    * Gets the corresponding model node for the specified graph node.
    * 
    * @param vertex graph node
    * @return corresponding model node
    */
   public AbstractNode getModelNode(DefaultGraphCell vertex) {
      if (vertex == null) {
         throw new IllegalArgumentException("'vertex' must not be null.");
      }
      
      return graphNode2modelNode.get(vertex);
   }
   
   /////////////////////////////////////////////////////////////////////////////////////////////////
   //                             methods for creating new vertices
   /////////////////////////////////////////////////////////////////////////////////////////////////
   
   /**
    * Creates a new level node vertex and a corresponding level node with the specified parameters
    * and stores it in the internal System Dynamics model.
    * 
    * @param nodeName node name
    * @param startValue start value
    * @param x x coordinate of node's origin
    * @param y y coordinate of node's origin
    * @return created graph vertex
    */
   public LevelNodeGraphCell createLevelNodeGraphCell(String nodeName, double startValue,
                                                      double x, double y) {
      if (nodeName == null) {
         throw new IllegalArgumentException("'nodeName' must not be null.");
      }
      
      // create node
      LevelNode levelNode = model.createLevelNode(nodeName, startValue);
      LevelNodeGraphCell levelNodeGraphCell = new LevelNodeGraphCell(nodeName, x, y);
      
      // insert vertex to graph
      getGraphLayoutCache().insert(levelNodeGraphCell);
          
      // insert mappings
      modelNode2graphNode.put(levelNode, levelNodeGraphCell);
      graphNode2modelNode.put(levelNodeGraphCell, levelNode);
      
      // inform listeners
      for (SystemDynamicsGraphModifiedEventListener listener : listeners) {
         listener.performGraphModifiedEvent();
      }
      
      return levelNodeGraphCell;
   }
   
   /**
    * Creates a new rate node vertex and a corresponding rate node with the specified parameters
    * and stores it in the internal System Dynamics model.
    * 
    * @param nodeName node name
    * @param x x coordinate of node's origin
    * @param y y coordinate of node's origin
    * @return created graph vertex
    */
   public RateNodeGraphCell createRateNodeGraphCell(String nodeName, double x, double y) {
      if (nodeName == null) {
         throw new IllegalArgumentException("'nodeName' must not be null.");
      }
      
      // create node
      RateNode rateNode = model.createRateNode(nodeName);
      RateNodeGraphCell rateNodeGraphCell = new RateNodeGraphCell(nodeName, x, y);
      
      // insert vertex to graph
      getGraphLayoutCache().insert(rateNodeGraphCell);
      
      // insert mappings
      modelNode2graphNode.put(rateNode, rateNodeGraphCell);
      graphNode2modelNode.put(rateNodeGraphCell, rateNode);
      
      // inform listeners
      for (SystemDynamicsGraphModifiedEventListener listener : listeners) {
         listener.performGraphModifiedEvent();
      }
      
      return rateNodeGraphCell;
   }
   
   /**
    * Creates a new constant node vertex and a corresponding constant node with the specified
    * parameters and stores it in the internal System Dynamics model.
    * 
    * @param nodeName node name
    * @param constantValue constant value
    * @param x x coordinate of node's origin
    * @param y y coordinate of node's origin
    * @return created graph vertex
    */
   public ConstantNodeGraphCell createConstantNodeGraphCell(String nodeName, double constantValue,
                                                            double x, double y) {
      if (nodeName == null) {
         throw new IllegalArgumentException("'nodeName' must not be null.");
      }
      
      // create node
      ConstantNode constantNode = model.createConstantNode(nodeName, constantValue);
      ConstantNodeGraphCell constantNodeGraphCell = new ConstantNodeGraphCell(nodeName, x, y);
      
      // insert vertex to graph
      getGraphLayoutCache().insert(constantNodeGraphCell);
      
      // insert mappings
      modelNode2graphNode.put(constantNode, constantNodeGraphCell);
      graphNode2modelNode.put(constantNodeGraphCell, constantNode);
      
      // inform listeners
      for (SystemDynamicsGraphModifiedEventListener listener : listeners) {
         listener.performGraphModifiedEvent();
      }
      
      return constantNodeGraphCell;
   }
   
   /**
    * Creates a new auxiliary node vertex and a corresponding auxiliary node with the specified
    * parameters and stores it in the internal System Dynamics model.
    * 
    * @param nodeName node name
    * @param x x coordinate of node's origin
    * @param y y coordinate of node's origin
    * @return created graph vertex
    */
   public AuxiliaryNodeGraphCell createAuxiliaryNodeGraphCell(String nodeName, double x, double y) {
      if (nodeName == null) {
         throw new IllegalArgumentException("'nodeName' must not be null.");
      }
      
      // create node
      AuxiliaryNode auxiliaryNode = model.createAuxiliaryNode(nodeName);
      AuxiliaryNodeGraphCell auxiliaryNodeGraphCell = new AuxiliaryNodeGraphCell(nodeName, x, y);
      
      // insert vertex to graph
      getGraphLayoutCache().insert(auxiliaryNodeGraphCell);
      
      // insert mappings
      modelNode2graphNode.put(auxiliaryNode, auxiliaryNodeGraphCell);
      graphNode2modelNode.put(auxiliaryNodeGraphCell, auxiliaryNode);
      
      // inform listeners
      for (SystemDynamicsGraphModifiedEventListener listener : listeners) {
         listener.performGraphModifiedEvent();
      }
      
      return auxiliaryNodeGraphCell;
   }
   
   /**
    * Creates a new source/sink node vertex and a corresponding source/sink node with the specified
    * parameters and stores it in the internal System Dynamics model.
    * 
    * @param x x coordinate of node's origin
    * @param y y coordinate of node's origin
    * @return created graph vertex
    */
   public SourceSinkNodeGraphCell createSourceSinkNodeGraphCell(double x, double y) {
      // create node
      SourceSinkNode sourceSinkNode = model.createSourceSinkNode();
      SourceSinkNodeGraphCell sourceSinkNodeGraphCell = new SourceSinkNodeGraphCell(x, y);
      
      // insert vertex to graph
      getGraphLayoutCache().insert(sourceSinkNodeGraphCell);
      
      // insert mappings
      modelNode2graphNode.put(sourceSinkNode, sourceSinkNodeGraphCell);
      graphNode2modelNode.put(sourceSinkNodeGraphCell, sourceSinkNode);
      
      // inform listeners
      for (SystemDynamicsGraphModifiedEventListener listener : listeners) {
         listener.performGraphModifiedEvent();
      }
      
      return sourceSinkNodeGraphCell;
   }
   
   /**
    * Removes the specified graph vertex and the corresponding node in the System Dynamics model.
    * 
    * @param vertex vertex to remove
    */
   private void removeGraphCell(DefaultGraphCell vertex) {
      AbstractNode node = graphNode2modelNode.get(vertex);
      
      try {
         model.removeNode(node);
      } catch (FormulaDependencyException e) {
         JOptionPane.showMessageDialog(null,
               messages.getString("SystemDynamicsGraph.RemoveGraphCell.FormulaDependencyException.ErrorMessage"),
               messages.getString("SystemDynamicsGraph.RemoveGraphCell.FormulaDependencyException"),
               JOptionPane.ERROR_MESSAGE);
         return;
      }
      
      // model node (and possibly some flows) was/were removed
      // => remove corresponding vertex (and possibly some flow edges) from the graph 
      Set edges = ((DefaultPort)vertex.getChildAt(0)).getEdges();
      for (Object edge : edges) {
         if (edge instanceof FlowEdge) {
            getGraphLayoutCache().remove(new Object[] { edge });
         }
      }
      // remove vertex in graph AND its port ('vertex.getChildAt(0)')
      getGraphLayoutCache().remove(new Object[] { vertex, vertex.getChildAt(0) });
      
      // inform listeners
      for (SystemDynamicsGraphModifiedEventListener listener : listeners) {
         listener.performGraphModifiedEvent();
      }
   }
   
   /**
    * Sets a new node name (both in the graph and the System Dynamics model).
    * 
    * @param vertex vertex to rename
    * @param newNodeName new node name
    */
   private void setNodeName(DefaultGraphCell vertex, String newNodeName) {
      if (vertex == null) {
         throw new IllegalArgumentException("'vertex' must not be null.");
      }
      if (newNodeName == null) {
         throw new IllegalArgumentException("'newNodeName' must not be null.");
      }
      
      AbstractNode node = graphNode2modelNode.get(vertex);
      model.setNodeName(node, newNodeName);
      
      Hashtable map = new Hashtable();
      GraphConstants.setValue(map, newNodeName);
      getGraphLayoutCache().editCell(vertex, map);
      
      // inform listeners
      for (SystemDynamicsGraphModifiedEventListener listener : listeners) {
         listener.performGraphModifiedEvent();
      }
   }
   
   /**
    * Sets a new start value for a level node.
    * 
    * @param vertex vertex with new start value
    * @param newStartValue new start value
    */
   private void setStartValue(LevelNodeGraphCell vertex, double newStartValue) {
      if (vertex == null) {
         throw new IllegalArgumentException("'vertex' must not be null.");
      }
      
      LevelNode levelNode = (LevelNode)graphNode2modelNode.get(vertex);
      model.setStartValue(levelNode, newStartValue);
      
      // inform listeners
      for (SystemDynamicsGraphModifiedEventListener listener : listeners) {
         listener.performGraphModifiedEvent();
      }
   }
   
   /**
    * Sets a new constant value for a constant node.
    * 
    * @param vertex vertex with new constant value
    * @param newConstantValue new constant value
    */
   private void setConstantValue(ConstantNodeGraphCell vertex, double newConstantValue) {
      if (vertex == null) {
         throw new IllegalArgumentException("'vertex' must not be null.");
      }
      
      ConstantNode constantNode = (ConstantNode)graphNode2modelNode.get(vertex);
      model.setConstantValue(constantNode, newConstantValue);
      
      // inform listeners
      for (SystemDynamicsGraphModifiedEventListener listener : listeners) {
         listener.performGraphModifiedEvent();
      }
   }
   
   /**
    * Sets the specified node's formula and adds dependency edges to the graph.
    * 
    * @param vertex node to change
    * @param formula new formula
    * @param addAdditionalPoint indication whether an additional point should be computed and added
    * @return <code>true</code> iff all formula nodes are part of this graph
    */
   public boolean setFormula(DefaultGraphCell vertex, ASTElement formula,
                             boolean addAdditionalPoint) {
      if (vertex == null) {
         throw new IllegalArgumentException("'vertex' must not be null.");
      }
      
      // check if all formula nodes are part of this graph
      if (formula != null) {
         for (AbstractNode node : formula.getAllNodesInASTSubtree()) {
            if (modelNode2graphNode.get(node) == null) {
               // formula node not in graph -> WRONG!
               return false;
            }
         }
      }
      
      // set formula
      model.setFormula(graphNode2modelNode.get(vertex), formula);
      
      // remove old dependency edges and add new ones
      removeIncomingDependencyEdges(vertex);
      if (formula != null) {
         for (AbstractNode node : formula.getAllNodesInASTSubtree()) {
            if (node != graphNode2modelNode.get(vertex)) {
               addDependencyEdge(modelNode2graphNode.get(node), vertex, addAdditionalPoint);
            }
         }
      }
      
      // inform listeners
      for (SystemDynamicsGraphModifiedEventListener listener : listeners) {
         listener.performGraphModifiedEvent();
      }
      
      return true;
   }
   
   /////////////////////////////////////////////////////////////////////////////////////////////////
   //                               methods for adding flows
   /////////////////////////////////////////////////////////////////////////////////////////////////
   
   /**
    * Adds a flow between the specified vertices and in the System Dynamics model.
    * 
    * @param levelNode level node
    * @param rateNode rate node
    * @return <code>true</code> iff the flow could be added
    */
   public boolean addFlow(LevelNodeGraphCell levelNode, RateNodeGraphCell rateNode) {
      if (levelNode == null) {
         throw new IllegalArgumentException("'levelNode' must not be null.");
      }
      if (rateNode == null) {
         throw new IllegalArgumentException("'rateNode' must not be null.");
      }
      
      boolean successful =
         model.addFlowFromLevelNode2RateNode((LevelNode)graphNode2modelNode.get(levelNode),
                                             (RateNode)graphNode2modelNode.get(rateNode));
      
      if (successful) {
         this.insertFlowEdge(levelNode, rateNode);
         
         // inform listeners
         for (SystemDynamicsGraphModifiedEventListener listener : listeners) {
            listener.performGraphModifiedEvent();
         }
      }
      
      return successful;
   }
   
   /**
    * Adds a flow between the specified vertices and in the System Dynamics model.
    *
    * @param rateNode rate node
    * @param levelNode level node
    * @return <code>true</code> iff the flow could be added
    */
   public boolean addFlow(RateNodeGraphCell rateNode, LevelNodeGraphCell levelNode) {
      if (rateNode == null) {
         throw new IllegalArgumentException("'rateNode' must not be null.");
      }
      if (levelNode == null) {
         throw new IllegalArgumentException("'levelNode' must not be null.");
      }
      
      boolean successful =
         model.addFlowFromRateNode2LevelNode((RateNode)graphNode2modelNode.get(rateNode),
                                             (LevelNode)graphNode2modelNode.get(levelNode));
      
      if (successful) {
         this.insertFlowEdge(rateNode, levelNode);
         
         // inform listeners
         for (SystemDynamicsGraphModifiedEventListener listener : listeners) {
            listener.performGraphModifiedEvent();
         }
      }
      
      return successful;
   }
   
   /**
    * Adds a flow between the specified vertices and in the System Dynamics model.
    *
    * @param sourceSinkNode source/sink node
    * @param rateNode rate node
    * @return <code>true</code> iff the flow could be added
    */
   public boolean addFlow(SourceSinkNodeGraphCell sourceSinkNode, RateNodeGraphCell rateNode) {
      if (sourceSinkNode == null) {
         throw new IllegalArgumentException("'sourceSinkNode' must not be null.");
      }
      if (rateNode == null) {
         throw new IllegalArgumentException("'rateNode' must not be null.");
      }
      
      boolean successful =
         model.addFlowFromSourceSinkNode2RateNode((SourceSinkNode)graphNode2modelNode.get(sourceSinkNode),
                                                  (RateNode)graphNode2modelNode.get(rateNode));
      
      if (successful) {
         this.insertFlowEdge(sourceSinkNode, rateNode);
         
         // inform listeners
         for (SystemDynamicsGraphModifiedEventListener listener : listeners) {
            listener.performGraphModifiedEvent();
         }
      }
      
      return successful;
   }
   
   /**
    * Adds a flow between the specified vertices and in the System Dynamics model.
    *
    * @param rateNode rate node
    * @param sourceSinkNode source/sink node
    * @return <code>true</code> iff the flow could be added
    */
   public boolean addFlow(RateNodeGraphCell rateNode, SourceSinkNodeGraphCell sourceSinkNode) {
      if (rateNode == null) {
         throw new IllegalArgumentException("'rateNode' must not be null.");
      }
      if (sourceSinkNode == null) {
         throw new IllegalArgumentException("'sourceSinkNode' must not be null.");
      }
      
      boolean successful =
         model.addFlowFromRateNode2SourceSinkNode((RateNode)graphNode2modelNode.get(rateNode),
                                                  (SourceSinkNode)graphNode2modelNode.get(sourceSinkNode));
      
      if (successful) {
         this.insertFlowEdge(rateNode, sourceSinkNode);
         
         // inform listeners
         for (SystemDynamicsGraphModifiedEventListener listener : listeners) {
            listener.performGraphModifiedEvent();
         }
      }
      
      return successful;
   }
   
   /**
    * Inserts a flow edge. This method does not insert the flow into the internal System Dynamics
    * model.
    * 
    * @param vertex1 edge's source vertex
    * @param vertex2 edge's target vertex
    */
   private void insertFlowEdge(DefaultGraphCell vertex1, DefaultGraphCell vertex2) {
      if (vertex1 == null) {
         throw new IllegalArgumentException("'vertex1' must not be null.");
      }
      if (vertex2 == null) {
         throw new IllegalArgumentException("'vertex2' must not be null.");
      }
      
      FlowEdge edge = new FlowEdge(vertex1, vertex2);
      
      getGraphLayoutCache().insert(edge);
   }
   
   /**
    * Adds a dependency edge between the specified edges.
    * 
    * @param edgeSource edge source
    * @param edgeTarget edge target
    * @param addAdditionalPoint indication whether an additional point should be computed and added
    */
   private void addDependencyEdge(DefaultGraphCell edgeSource, DefaultGraphCell edgeTarget,
                                  boolean addAdditionalPoint) {
      if (edgeSource == null) {
         throw new IllegalArgumentException("'edgeSource' must not be null.");
      }
      if (edgeTarget == null) {
         throw new IllegalArgumentException("'edgeTarget' must not be null.");
      }
      if (!(edgeSource instanceof AuxiliaryNodeGraphCell)
            && !(edgeSource instanceof ConstantNodeGraphCell)
            && !(edgeSource instanceof LevelNodeGraphCell)) {
         throw new IllegalArgumentException("'edgeSource' does not have the correct type.");
      }
      if (!(edgeTarget instanceof AuxiliaryNodeGraphCell)
            && !(edgeTarget instanceof RateNodeGraphCell)) {
         throw new IllegalArgumentException("'edgeTarget' does not have the correct type.");
      }
      
      DefaultEdge edge = new DefaultEdge();
      edge.setSource(edgeSource.getChildAt(0));
      edge.setTarget(edgeTarget.getChildAt(0));
      GraphConstants.setLineStyle(edge.getAttributes(), GraphConstants.STYLE_SPLINE);
      GraphConstants.setLineEnd(edge.getAttributes(), GraphConstants.ARROW_TECHNICAL);
      GraphConstants.setEndFill(edge.getAttributes(), true);
      GraphConstants.setDashPattern(edge.getAttributes(), new float[] { 10, 5 });
      
      if (addAdditionalPoint) {
         // add additional control point
         Point2D controlPoint = computeControlPoint(edgeSource, edgeTarget, edge);
         if (controlPoint != null) {
            LinkedList<Point2D> list = new LinkedList<Point2D>();
            list.add(null);
            list.add(controlPoint);
            list.add(null);
            GraphConstants.setPoints(edge.getAttributes(), list);
            EdgeView edgeView = (EdgeView)getGraphLayoutCache().getMapping(edge, false);
            getGraphLayoutCache().refresh(edgeView, false);  // without this line, control points are not always visible!
         }
      }
      
      getGraphLayoutCache().insert(edge);
   }
   
   /**
    * Computes an additional control point between for the specified dependency edge.
    * 
    * @param edgeSource edge source
    * @param edgeTarget edge target
    * @param edge edge
    * @return additional control point - or <code>null</code> iff edge is horizontal
    */
   private Point2D computeControlPoint(DefaultGraphCell edgeSource, DefaultGraphCell edgeTarget,
                                       DefaultEdge edge) {
      if (edgeSource == null) {
         throw new IllegalArgumentException("'edgeSource' must not be null.");
      }
      if (edgeTarget == null) {
         throw new IllegalArgumentException("'edgeTarget' must not be null.");
      }
      if (edge == null) {
         throw new IllegalArgumentException("'edge' must not be null.");
      }
      
      EdgeView edgeView = (EdgeView)getGraphLayoutCache().getMapping(edge, true);
      
      Point2D sourceCenter =
         AbstractCellView.getCenterPoint(getGraphLayoutCache().getMapping(edgeSource, false));
      Point2D targetCenter =
         AbstractCellView.getCenterPoint(getGraphLayoutCache().getMapping(edgeTarget, false));
      
      // compute perimeter points in an interation
      Point2D sourcePerimeterPoint =
         getGraphLayoutCache().getMapping(edgeSource, false).getPerimeterPoint(edgeView, null, targetCenter);
      Point2D targetPerimeterPoint =
         getGraphLayoutCache().getMapping(edgeTarget, false).getPerimeterPoint(edgeView, null, sourceCenter);
      for (int i = 0; i < 10; i++) {
         sourcePerimeterPoint =
            getGraphLayoutCache().getMapping(edgeSource, false).getPerimeterPoint(edgeView, null, targetPerimeterPoint);
         targetPerimeterPoint =
            getGraphLayoutCache().getMapping(edgeTarget, false).getPerimeterPoint(edgeView, null, sourcePerimeterPoint);
      }
            
      if (sourcePerimeterPoint.getX() == targetPerimeterPoint.getX()) {
         // points have same x-coordinate
         return null;
      }
      
      Point2D middlePoint =
         new Point2D.Double((sourcePerimeterPoint.getX() + targetPerimeterPoint.getX()) / 2 ,
                            (sourcePerimeterPoint.getY() + targetPerimeterPoint.getY()) / 2);
      
      double distanceToLine = sourcePerimeterPoint.distance(targetPerimeterPoint) / 3;
      
      if (sourcePerimeterPoint.getY() == targetPerimeterPoint.getY()) {
         // points have same y-coordinate
         return new Point2D.Double(middlePoint.getX(), middlePoint.getY() + distanceToLine);
      }
      
      double deltaX = (Math.abs(targetPerimeterPoint.getX() - sourcePerimeterPoint.getX()));
      double deltaY = (Math.abs(targetPerimeterPoint.getY() - sourcePerimeterPoint.getY()));
      
      if (targetPerimeterPoint.getX() < sourcePerimeterPoint.getX()
            && targetPerimeterPoint.getY() < sourcePerimeterPoint.getY()) {
         // target left and above source
         double degree = Math.atan(deltaX / deltaY);
         AffineTransform trans =
            AffineTransform.getRotateInstance(-degree, middlePoint.getX(), middlePoint.getY());
         Point2D tmpPoint = new Point2D.Double(middlePoint.getX() - distanceToLine, middlePoint.getY());
         return trans.transform(tmpPoint, null);
      }
      
      if (targetPerimeterPoint.getX() > sourcePerimeterPoint.getX()
            && targetPerimeterPoint.getY() < sourcePerimeterPoint.getY()) {
         // target right and above source
         double degree = Math.atan(deltaX / deltaY);
         AffineTransform trans =
            AffineTransform.getRotateInstance(degree, middlePoint.getX(), middlePoint.getY());
         Point2D tmpPoint = new Point2D.Double(middlePoint.getX() + distanceToLine, middlePoint.getY());
         return trans.transform(tmpPoint, null);
      }
      
      if (targetPerimeterPoint.getX() < sourcePerimeterPoint.getX()
            && targetPerimeterPoint.getY() > sourcePerimeterPoint.getY()) {
         // target left and under source
         double degree = Math.atan(deltaX / deltaY);
         AffineTransform trans =
            AffineTransform.getRotateInstance(degree, middlePoint.getX(), middlePoint.getY());
         Point2D tmpPoint = new Point2D.Double(middlePoint.getX() - distanceToLine, middlePoint.getY());
         return trans.transform(tmpPoint, null);
      }
      
      if (targetPerimeterPoint.getX() > sourcePerimeterPoint.getX()
            && targetPerimeterPoint.getY() > sourcePerimeterPoint.getY()) {
         // target right and under source
         double degree = Math.atan(deltaX / deltaY);
         AffineTransform trans =
            AffineTransform.getRotateInstance(-degree, middlePoint.getX(), middlePoint.getY());
         Point2D tmpPoint = new Point2D.Double(middlePoint.getX() + distanceToLine, middlePoint.getY());
         return trans.transform(tmpPoint, null);
      }
      
      throw new Error("This line of source code should not be reached.");
   }
   
   /**
    * Changes the additional control points of the specified dependency edge.
    * <p>
    * This method does not add a dependency edge that not already exists!
    * 
    * @param edgeSource edge source
    * @param edgeTarget edge target
    * @param points new additional control points (old control points are removed
    * @return <code>true</code> iff the specified dependency edge already exists
    */
   public boolean changeDependencyEdgeControlPoints(DefaultGraphCell edgeSource,
                                                    DefaultGraphCell edgeTarget,
                                                    LinkedList<Point2D> points) {
      if (edgeSource == null) {
         throw new IllegalArgumentException("'edgeSource' must not be null.");
      }
      if (edgeTarget == null) {
         throw new IllegalArgumentException("'edgeTarget' must not be null.");
      }
      if (points == null) {
         throw new IllegalArgumentException("'points' must not be null.");
      }
      
      // check if dependency edge already exists
      boolean existsDependencyEdge = false;
      DefaultEdge dependencyEdge = null;
      Set edges = ((DefaultPort)edgeSource.getChildAt(0)).getEdges();
      for (Object edge : edges) {
         if (!(edge instanceof FlowEdge) && ((DefaultPort)((DefaultEdge)edge).getTarget()).getParent() == edgeTarget) {
            existsDependencyEdge = true;
            dependencyEdge = (DefaultEdge)edge;
            break;
         }
      }
      if (!existsDependencyEdge) {
         return false;
      }
      
      // dependency edge exists -> change control points
      LinkedList<Point2D> list = new LinkedList<Point2D>();
      list.add(null);
      list.addAll(points);
      list.add(null);
      GraphConstants.setPoints(dependencyEdge.getAttributes(), list);
      EdgeView edgeView = (EdgeView)getGraphLayoutCache().getMapping(dependencyEdge, false);
      getGraphLayoutCache().refresh(edgeView, false);  // without this line, control points are not always visible!
      
      return true;
   }
   
   /**
    * Changes the additional control points of the specified flow edge.
    * <p>
    * This method does not add a flow edge that not already exists!
    * 
    * @param edgeSource edge source
    * @param edgeTarget edge target
    * @param points new additional control points (old control points are removed
    * @return <code>true</code> iff the specified flow edge already exists
    */
   public boolean changeFlowEdgeControlPoints(DefaultGraphCell edgeSource,
                                              DefaultGraphCell edgeTarget,
                                              LinkedList<Point2D> points) {
      if (edgeSource == null) {
         throw new IllegalArgumentException("'edgeSource' must not be null.");
      }
      if (edgeTarget == null) {
         throw new IllegalArgumentException("'edgeTarget' must not be null.");
      }
      if (points == null) {
         throw new IllegalArgumentException("'points' must not be null.");
      }
      
      // check if flow edge already exists
      boolean existsFlowEdge = false;
      FlowEdge flowEdge = null;
      Set edges = ((DefaultPort)edgeSource.getChildAt(0)).getEdges();
      for (Object edge : edges) {
         if (edge instanceof FlowEdge && ((DefaultPort)((FlowEdge)edge).getTarget()).getParent() == edgeTarget) {
            existsFlowEdge = true;
            flowEdge = (FlowEdge)edge;
            break;
         }
      }
      if (!existsFlowEdge) {
         return false;
      }
      
      // flow edge exists -> change control points
      LinkedList<Point2D> list = new LinkedList<Point2D>();
      list.add(null);
      list.addAll(points);
      list.add(null);
      GraphConstants.setPoints(flowEdge.getAttributes(), list);     
      EdgeView edgeView = (EdgeView)getGraphLayoutCache().getMapping(flowEdge, false);
      getGraphLayoutCache().refresh(edgeView, false);  // without this line, control points are not always visible!
      
      return true;
   }
   
   /**
    * Removes all incoming dependency edges to the specified edge.
    * 
    * @param edgeTarget edge target
    */
   private void removeIncomingDependencyEdges(DefaultGraphCell edgeTarget) {
      if (edgeTarget == null) {
         throw new IllegalArgumentException("'edgeTarget' must not be null.");
      }
      if (!(edgeTarget instanceof AuxiliaryNodeGraphCell)
            && !(edgeTarget instanceof RateNodeGraphCell)) {
         throw new IllegalArgumentException("'edgeTarget' does not have the correct type.");
      }
      
      HashSet<DefaultEdge> edgesToRemove = new HashSet<DefaultEdge>();
      
      Set edges = ((DefaultPort)edgeTarget.getChildAt(0)).getEdges();
      for (Object edge : edges) {
         if (!(edge instanceof FlowEdge) && ((DefaultPort)((DefaultEdge)edge).getTarget()).getParent() == edgeTarget) {
            edgesToRemove.add((DefaultEdge)edge);
         }
      }
      
      getGraphLayoutCache().remove(edgesToRemove.toArray());
   }
   
   /////////////////////////////////////////////////////////////////////////////////////////////////
   //                               methods for removing flows
   /////////////////////////////////////////////////////////////////////////////////////////////////
   
   /**
    * Removes the flow between the specified vertices and in the System Dynamics model.
    * 
    * @param levelNode level node
    * @param rateNode rate node
    * @return <code>true</code> iff the flow could be removed
    */
   private boolean removeFlow(LevelNodeGraphCell levelNode, RateNodeGraphCell rateNode) {
      if (levelNode == null) {
         throw new IllegalArgumentException("'levelNode' must not be null.");
      }
      if (rateNode == null) {
         throw new IllegalArgumentException("'rateNode' must not be null.");
      }
      
      boolean successful =
         model.removeFlowFromLevelNode2RateNode((LevelNode)graphNode2modelNode.get(levelNode),
                                                (RateNode)graphNode2modelNode.get(rateNode));
      
      if (successful) {
         this.removeFlowEdge(levelNode, rateNode);
         
         // inform listeners
         for (SystemDynamicsGraphModifiedEventListener listener : listeners) {
            listener.performGraphModifiedEvent();
         }
      }
      
      return successful;
   }
   
   /**
    * Removes the flow between the specified vertices and in the System Dynamics model.
    *
    * @param rateNode rate node
    * @param levelNode level node
    * @return <code>true</code> iff the flow could be removed
    */
   private boolean removeFlow(RateNodeGraphCell rateNode, LevelNodeGraphCell levelNode) {
      if (rateNode == null) {
         throw new IllegalArgumentException("'rateNode' must not be null.");
      }
      if (levelNode == null) {
         throw new IllegalArgumentException("'levelNode' must not be null.");
      }
      
      boolean successful =
         model.removeFlowFromRateNode2LevelNode((RateNode)graphNode2modelNode.get(rateNode),
                                                (LevelNode)graphNode2modelNode.get(levelNode));
      
      if (successful) {
         this.removeFlowEdge(rateNode, levelNode);
         
         // inform listeners
         for (SystemDynamicsGraphModifiedEventListener listener : listeners) {
            listener.performGraphModifiedEvent();
         }
      }
      
      return successful;
   }
   
   /**
    * Removes the flow between the specified vertices and in the System Dynamics model.
    *
    * @param sourceSinkNode source/sink node
    * @param rateNode rate node
    * @return <code>true</code> iff the flow could be removed
    */
   private boolean removeFlow(SourceSinkNodeGraphCell sourceSinkNode, RateNodeGraphCell rateNode) {
      if (sourceSinkNode == null) {
         throw new IllegalArgumentException("'sourceSinkNode' must not be null.");
      }
      if (rateNode == null) {
         throw new IllegalArgumentException("'rateNode' must not be null.");
      }
      
      boolean successful =
         model.removeFlowFromSourceSinkNode2RateNode((SourceSinkNode)graphNode2modelNode.get(sourceSinkNode),
                                                     (RateNode)graphNode2modelNode.get(rateNode));
      
      if (successful) {
         this.removeFlowEdge(sourceSinkNode, rateNode);
         
         // inform listeners
         for (SystemDynamicsGraphModifiedEventListener listener : listeners) {
            listener.performGraphModifiedEvent();
         }
      }
      
      return successful;
   }
   
   /**
    * Removes the flow between the specified vertices and in the System Dynamics model.
    *
    * @param rateNode rate node
    * @param sourceSinkNode source/sink node
    * @return <code>true</code> iff the flow could be removed
    */
   private boolean removeFlow(RateNodeGraphCell rateNode, SourceSinkNodeGraphCell sourceSinkNode) {
      if (rateNode == null) {
         throw new IllegalArgumentException("'rateNode' must not be null.");
      }
      if (sourceSinkNode == null) {
         throw new IllegalArgumentException("'sourceSinkNode' must not be null.");
      }
      
      boolean successful =
         model.removeFlowFromRateNode2SourceSinkNode((RateNode)graphNode2modelNode.get(rateNode),
                                                     (SourceSinkNode)graphNode2modelNode.get(sourceSinkNode));
      
      if (successful) {
         this.removeFlowEdge(rateNode, sourceSinkNode);
         
         // inform listeners
         for (SystemDynamicsGraphModifiedEventListener listener : listeners) {
            listener.performGraphModifiedEvent();
         }
      }
      
      return successful;
   }
   
   /**
    * Removes a flow edge. This method does not remove the flow from the internal System Dynamics
    * model.
    * 
    * @param vertex1 edge's source vertex
    * @param vertex2 edge's target vertex
    */
   private void removeFlowEdge(DefaultGraphCell vertex1, DefaultGraphCell vertex2) {
      if (vertex1 == null) {
         throw new IllegalArgumentException("'vertex1' must not be null.");
      }
      if (vertex2 == null) {
         throw new IllegalArgumentException("'vertex2' must not be null.");
      }
      
      Set outgoingEdges = ((DefaultPort)vertex1.getChildAt(0)).getEdges();
      Set incomingEdges = ((DefaultPort)vertex2.getChildAt(0)).getEdges();
      
      // intersection of both sets
      outgoingEdges.retainAll(incomingEdges);
      
      if (outgoingEdges.size() != 1) {
         throw new Error("No unique flow edge to remove.");
      }
      
      getGraphLayoutCache().remove(outgoingEdges.toArray());
   }
   
   /**
    * Creates id to node mappings.
    * 
    * @param id2auxiliaryNode id to auxiliary node mapping
    * @param id2constantNode id to constant node mapping
    * @param id2levelNode id to level node mapping
    */
   private void createId2NodeMappings(HashMap<Integer, AuxiliaryNode> id2auxiliaryNode,
                                             HashMap<Integer, ConstantNode> id2constantNode,
                                             HashMap<Integer, LevelNode> id2levelNode) {
      if (id2auxiliaryNode == null) {
         throw new IllegalArgumentException("'id2auxiliaryNode' must not be null.");
      }
      if (!id2auxiliaryNode.isEmpty()) {
         throw new IllegalArgumentException("'id2auxiliaryNode' must be empty.");
      }
      if (id2constantNode == null) {
         throw new IllegalArgumentException("'id2constantNode' must not be null.");
      }
      if (!id2constantNode.isEmpty()) {
         throw new IllegalArgumentException("'id2constantNode' must be empty.");
      }
      if (id2levelNode == null) {
         throw new IllegalArgumentException("'id2levelNode' must not be null.");
      }
      if (!id2levelNode.isEmpty()) {
         throw new IllegalArgumentException("'id2levelNode' must be empty.");
      }
      
      int nextAuxiliaryNodeIndex = 1;
      int nextConstantNodeIndex = 1;
      int nextLevelNodeIndex = 1;
      
      for (AbstractNode node : modelNode2graphNode.keySet()) {
         if (node instanceof AuxiliaryNode) {
            id2auxiliaryNode.put(nextAuxiliaryNodeIndex++, (AuxiliaryNode)node);
            continue;
         }
         if (node instanceof ConstantNode) {
            id2constantNode.put(nextConstantNodeIndex++, (ConstantNode)node);
            continue;
         }
         if (node instanceof LevelNode) {
            id2levelNode.put(nextLevelNodeIndex++, (LevelNode)node);
         }
      }
   }
   
   /**
    * Creates a popup menu for the specified mouse event.
    * 
    * @param e mouse event
    */
   void createPopupMenu(MouseEvent e) {
      if (model.isChangeable()) {
         // model is changeable -> show popup menu
         
         // find cell in model coordinates
         final Object cell = getFirstCellForLocation(e.getX(), e.getY());
         
         if (cell != null) {
            if (cell instanceof DefaultGraphCell && !(cell instanceof DefaultEdge)) {
               // cell is a graph vertex
               
               JPopupMenu menu = new JPopupMenu();
               
               if (!(cell instanceof SourceSinkNodeGraphCell)) {
                  // change node name
                  JMenuItem changeNodeNameMenuItem =
                     new JMenuItem(messages.getString("SystemDynamicsGraph.PopupMenu.ChangeName"));
                  changeNodeNameMenuItem.addActionListener(new ActionListener() {
                     public void actionPerformed(ActionEvent e) {
                        String initialNodeName = graphNode2modelNode.get(cell).getNodeName();
                        String newNodeName =
                           NodeNameDialog.showNodeNameDialog(start, frame,
                                                             messages.getString("SystemDynamicsGraph.PopupMenu.ChangeName"),
                                                             messages.getString("SystemDynamicsGraph.PopupMenu.ChangeName.Message"),
                                                             initialNodeName);
                        if (newNodeName != null) {
                           setNodeName((DefaultGraphCell)cell, newNodeName);
                        }
                     }
                  });
                  menu.add(changeNodeNameMenuItem);
               }
               
               if (cell instanceof LevelNodeGraphCell) {
                  // change start value
                  JMenuItem changeStartValueMenuItem =
                     new JMenuItem(messages.getString("SystemDynamicsGraph.PopupMenu.ChangeStartValue"));
                  changeStartValueMenuItem.addActionListener(new ActionListener() {
                     public void actionPerformed(ActionEvent e) {
                        double initialStartValue = ((LevelNode)graphNode2modelNode.get(cell)).getStartValue();
                        double minParameter = LevelNode.MIN_START_VALUE;
                        double maxParameter = LevelNode.MAX_START_VALUE;
                        Double newStartValue =
                           NodeParameterDialog.showNodeParameterDialog(
                                 start, frame,
                                 messages.getString("SystemDynamicsGraph.PopupMenu.ChangeStartValue"),
                                 messages.getString("SystemDynamicsGraph.PopupMenu.ChangeStartValue.Message"),
                                 initialStartValue, minParameter, maxParameter);
                        if (newStartValue != null) {
                           setStartValue((LevelNodeGraphCell)cell, newStartValue);
                        }
                     }
                  });
                  menu.add(changeStartValueMenuItem);
               }
               
               if (cell instanceof ConstantNodeGraphCell) {
                  // change constant value
                  JMenuItem changeConstantValueMenuItem =
                     new JMenuItem(messages.getString("SystemDynamicsGraph.PopupMenu.ChangeConstantValue"));
                  changeConstantValueMenuItem.addActionListener(new ActionListener() {
                     public void actionPerformed(ActionEvent e) {
                        double initialConstantValue =
                           ((ConstantNode)graphNode2modelNode.get(cell)).getConstantValue();
                        double minParameter = ConstantNode.MIN_CONSTANT;
                        double maxParameter = ConstantNode.MAX_CONSTANT;
                        Double newConstantValue =
                           NodeParameterDialog.showNodeParameterDialog(
                                 start, frame,
                                 messages.getString("SystemDynamicsGraph.PopupMenu.ChangeConstantValue"),
                                 messages.getString("SystemDynamicsGraph.PopupMenu.ChangeConstantValue.Message"),
                                 initialConstantValue, minParameter, maxParameter);
                        if (newConstantValue != null) {
                           setConstantValue((ConstantNodeGraphCell)cell, newConstantValue);
                        }
                     }
                  });
                  menu.add(changeConstantValueMenuItem);
               }
               
               if (cell instanceof AuxiliaryNodeGraphCell || cell instanceof RateNodeGraphCell) {
                  // change formula
                  JMenuItem changeFormulaMenuItem =
                     new JMenuItem(messages.getString("SystemDynamicsGraph.PopupMenu.ChangeFormula"));
                  changeFormulaMenuItem.addActionListener(new ActionListener() {
                     public void actionPerformed(ActionEvent e) {
                        AbstractNode node = graphNode2modelNode.get(cell);
                        ASTElement initialFormula;
                        if (node instanceof AuxiliaryNode) {
                           initialFormula = ((AuxiliaryNode)node).getFormula();
                        } else {
                           // node instanceof RateNode
                           initialFormula = ((RateNode)node).getFormula();
                        }
                        HashMap<Integer, AuxiliaryNode> id2auxiliaryNode = new HashMap<Integer, AuxiliaryNode>();
                        HashMap<Integer, ConstantNode> id2constantNode = new HashMap<Integer, ConstantNode>();
                        HashMap<Integer, LevelNode> id2levelNode = new HashMap<Integer, LevelNode>();
                        createId2NodeMappings(id2auxiliaryNode, id2constantNode, id2levelNode);
                        NodeFormulaDialog.Formula newFormula =
                           NodeFormulaDialog.showNodeFormulaDialog(
                                 start, frame,
                                 messages.getString("SystemDynamicsGraph.PopupMenu.ChangeFormula"),
                                 initialFormula,
                                 id2auxiliaryNode, id2constantNode, id2levelNode);
                        if (newFormula.wasNewFormulaEntered()) {
                           if (cell instanceof AuxiliaryNodeGraphCell) {
                              setFormula((AuxiliaryNodeGraphCell)cell, newFormula.getFormula(), true);
                           } else {
                              // cell instanceof RateNodeGraphCell
                              setFormula((RateNodeGraphCell)cell, newFormula.getFormula(), true);
                           }
                        }
                     }
                  });
                  menu.add(changeFormulaMenuItem);
               }
               
               // remove node
               JMenuItem removeNodeMenuItem =
                  new JMenuItem(messages.getString("SystemDynamicsGraph.PopupMenu.RemoveNode"));
               removeNodeMenuItem.addActionListener(new ActionListener() {
                  public void actionPerformed(ActionEvent e) {
                     removeGraphCell((DefaultGraphCell)cell);
                  }
               });
               menu.add(removeNodeMenuItem);
               
               menu.show(this, e.getX(), e.getY());            
            }
            
            if (cell instanceof FlowEdge) {
               FlowEdge edge = (FlowEdge)cell;
               GraphModel model = getModel();
               final Object edgeSource = model.getParent(model.getSource(edge));
               final Object edgeTarget = model.getParent(model.getTarget(edge));
                           
               JPopupMenu menu = new JPopupMenu();
               JMenuItem removeFlowMenuItem =
                  new JMenuItem(messages.getString("SystemDynamicsGraph.PopupMenu.RemoveFlow"));
               removeFlowMenuItem.addActionListener(new ActionListener() {
                  public void actionPerformed(ActionEvent e) {
                     if (edgeSource instanceof SourceSinkNodeGraphCell && edgeTarget instanceof RateNodeGraphCell) {
                        removeFlow((SourceSinkNodeGraphCell)edgeSource, (RateNodeGraphCell)edgeTarget);
                     }
                     if (edgeSource instanceof RateNodeGraphCell && edgeTarget instanceof LevelNodeGraphCell) {
                        removeFlow((RateNodeGraphCell)edgeSource, (LevelNodeGraphCell)edgeTarget);
                     }
                     if (edgeSource instanceof LevelNodeGraphCell && edgeTarget instanceof RateNodeGraphCell) {
                        removeFlow((LevelNodeGraphCell)edgeSource, (RateNodeGraphCell)edgeTarget);
                     }
                     if (edgeSource instanceof RateNodeGraphCell && edgeTarget instanceof SourceSinkNodeGraphCell) {
                        removeFlow((RateNodeGraphCell)edgeSource, (SourceSinkNodeGraphCell)edgeTarget);
                     }
                  }
               });
               menu.add(removeFlowMenuItem);
               menu.show(this, e.getX(), e.getY());
            }
         }
      }
   }
   
   /**
    * Gets the tool tip text for the specified mouse event.
    * <p>
    * First, the method checks which cell is placed at the mouse event location.
    * Second, according to the vertex type and vertex status a tool tip text is
    * returned.
    * 
    * @param event mouse event
    * @return tool tip text
    */
   public String getToolTipText(MouseEvent event) {
      NumberFormat formatter = NumberFormat.getInstance(locale);
      formatter.setMaximumFractionDigits(100);  // without this, maximal three fractinal digits are shown
      
      Object cell = getFirstCellForLocation(event.getX(), event.getY());
      if (cell instanceof LevelNodeGraphCell) {
         LevelNode levelNode = (LevelNode)graphNode2modelNode.get(cell);
         return "<html>" + messages.getString("SystemDynamicsGraph.LevelNode") + ": " + levelNode.getNodeName() + "<br>"
              + messages.getString("SystemDynamicsGraph.StartValue") + ": " + formatter.format(levelNode.getStartValue()) + "</html>";
      }
      if (cell instanceof RateNodeGraphCell) {
         RateNode rateNode = (RateNode)graphNode2modelNode.get(cell);
         String formulaString = (rateNode.getFormula() == null) ? messages.getString("SystemDynamicsGraph.ToolTipText.NoFormula") : rateNode.getFormula().getStringRepresentation();
         return "<html>" + messages.getString("SystemDynamicsGraph.RateNode") + ": " + rateNode.getNodeName() + "<br>"
              + messages.getString("SystemDynamicsGraph.Formula") + ": " + formulaString + "</html>";
      }
      if (cell instanceof AuxiliaryNodeGraphCell) {
         AuxiliaryNode auxiliaryNode = (AuxiliaryNode)graphNode2modelNode.get(cell);
         String formulaString = (auxiliaryNode.getFormula() == null) ? messages.getString("SystemDynamicsGraph.ToolTipText.NoFormula") : auxiliaryNode.getFormula().getStringRepresentation();
         return "<html>" + messages.getString("SystemDynamicsGraph.AuxiliaryNode") + ": " + auxiliaryNode.getNodeName() + "<br>"
              + messages.getString("SystemDynamicsGraph.Formula") + ": " + formulaString + "</html>";
      }
      if (cell instanceof ConstantNodeGraphCell) {
         ConstantNode constantNode = (ConstantNode)graphNode2modelNode.get(cell);
         return "<html>" + messages.getString("SystemDynamicsGraph.ConstantNode") + ": " + constantNode.getNodeName() + "<br>"
              + messages.getString("SystemDynamicsGraph.ConstantValue") + ": " + formatter.format(constantNode.getConstantValue()) + "</html>";
      }
      if (cell instanceof SourceSinkNodeGraphCell) {
         return "<html>" + messages.getString("SystemDynamicsGraph.SourceSinkNode") + "</html>";
      }
      
      return null;
   }
   
   /**
    * Validates the model. If the model is valid, the method runs without throwing any exception.
    * Otherwise, an appropriate exception is thrown.
    * 
    * @throws AuxiliaryNodesCycleDependencyException if the model's auxiliary nodes have a cycle
    *                                                dependency
    * @throws NoFormulaException if a rate node or an auxiliary node has no formula
    * @throws NoLevelNodeException if model has no level node
    * @throws RateNodeFlowException if a rate node has no incoming or no outgoing flow
    * @throws UselessNodeException if a node has no influence on a level node
    */
   public void validateModel() throws AuxiliaryNodesCycleDependencyException,
                                      NoFormulaException,
                                      NoLevelNodeException,
                                      RateNodeFlowException,
                                      UselessNodeException {
      model.validateModel();
   }
   
   /**
    * Validates the model and sets it unchangeable. If the model is valid, the method runs without
    * throwing any exception. Otherwise, an appropriate exception is thrown.
    * 
    * @throws AuxiliaryNodesCycleDependencyException if the model's auxiliary nodes have a cycle
    *                                                dependency
    * @throws NoFormulaException if a rate node or an auxiliary node has no formula
    * @throws NoLevelNodeException if model has no level node
    * @throws RateNodeFlowException if a rate node has no incoming or no outgoing flow
    * @throws UselessNodeException if a node has no influence on a level node
    */
   public void validateModelAndSetUnchangeable() throws AuxiliaryNodesCycleDependencyException,
                                                        NoFormulaException,
                                                        NoLevelNodeException,
                                                        RateNodeFlowException,
                                                        UselessNodeException {
      model.validateModelAndSetUnchangeable();
   }
   
   /**
    * Stores this graph to an XML file.
    * 
    * @param fileName file name
    * @throws AuxiliaryNodesCycleDependencyException if the model's auxiliary nodes have a cycle
    *                                                dependency
    * @throws NoFormulaException if a rate node or an auxiliary node has no formula
    * @throws NoLevelNodeException if model has no level node
    * @throws RateNodeFlowException if a rate node has no incoming or no outgoing flow
    * @throws UselessNodeException if a node has no influence on a level node
    * @throws XMLModelReaderWriterException if there is any exception (wrapper for inner exception)
    */
   public void storeToXML(String fileName) throws AuxiliaryNodesCycleDependencyException,
                                                  NoFormulaException,
                                                  NoLevelNodeException,
                                                  RateNodeFlowException,
                                                  UselessNodeException,
                                                  XMLModelReaderWriterException{
      if (fileName == null) {
         throw new IllegalArgumentException("'fileName' must not be null.");
      }
      
      // graph nodes
      LinkedList<DefaultGraphCell> graphNodes = new LinkedList<DefaultGraphCell>();
      Object[] vertices = getGraphLayoutCache().getCells(false, true, false, false);
      for (Object vertex : vertices) {
         graphNodes.add((DefaultGraphCell)vertex);
      }
      
      // flow edges and dependency edges
      Object[] edges = getGraphLayoutCache().getCells(false, false, false, true);
      LinkedList<FlowEdge> flowEdges = new LinkedList<FlowEdge>();
      LinkedList<DefaultEdge> dependencyEdges = new LinkedList<DefaultEdge>();
      for (Object edge : edges) {
         if (edge instanceof FlowEdge) {
            // flow edge
            flowEdges.add((FlowEdge)edge);
         } else {
            // dependency edge
            dependencyEdges.add((DefaultEdge)edge);
         }
      }
      
      XMLModelWriter.writeXMLSystemDynamicsGraph(this, model, graphNodes, flowEdges, dependencyEdges,
                                                 fileName);
   }
   
   /**
    * Gets the corresponding chart panel for this model.
    * 
    * @return chart panel
    */
   public ModelExecutionChartPanel getChartPanel() {
      return new ModelExecutionChartPanel(start, model);
   }
   
   /**
    * Gets the corresponding export panel for this model.
    * 
    * @return export panel
    */
   public ExportPanel getExportPanel() {
      return new ExportPanel(start, model);
   }

////////////////////////////////////////////////////////////////////////////////////////////////////
//                       methods of interface GraphModelListener
////////////////////////////////////////////////////////////////////////////////////////////////////
   
   /**
    * Performs the specified graph model event.
    * <p>
    * If a vertex was moved or a flow or dependency edge was changed, the registered
    * {@link de.uka.aifb.com.systemDynamics.event.SystemDynamicsGraphModifiedEventListener}s are
    * informed about the change.
    * 
    * @param e graph model event to perform
    */
   public void graphChanged(GraphModelEvent e) {
      GraphModelEvent.GraphModelChange change = e.getChange();
      Object[] changedObjects = change.getChanged();
      for (int i = 0; i < changedObjects.length; i++) {
         Object changedObject = changedObjects[i];
         if (changedObject instanceof AuxiliaryNodeGraphCell || changedObject instanceof ConstantNodeGraphCell || changedObject instanceof LevelNodeGraphCell || changedObject instanceof RateNodeGraphCell || changedObject instanceof SourceSinkNodeGraphCell) {
            if (change.getAttributes() != null) {  // after deletion of vertex, getAttributes() is null!
               // current case: vertex was moved (not deleted!)
               Map map1 = (Map)change.getAttributes().get(changedObject);
               Map map2 = (Map)change.getPreviousAttributes().get(changedObject);
               if (map1.get(GraphConstants.BOUNDS) != null && map2.get(GraphConstants.BOUNDS) != null) {
                  if (!map1.get(GraphConstants.BOUNDS).equals(map2.get(GraphConstants.BOUNDS))) {
                     // inform listeners
                     for (SystemDynamicsGraphModifiedEventListener listener : listeners) {
                        listener.performGraphModifiedEvent();
                     }
                  }
               }
            }
         }
         
         if (changedObject instanceof FlowEdge) {
            // flow edge

            Map map1 = (Map)change.getAttributes().get(changedObject);
            Map map2 = (Map)change.getPreviousAttributes().get(changedObject);
            if (map1.get(GraphConstants.POINTS) != null && map2.get(GraphConstants.POINTS) == null) {
               // inform listeners
               for (SystemDynamicsGraphModifiedEventListener listener : listeners) {
                  listener.performGraphModifiedEvent();
               }
            }
            if (map1.get(GraphConstants.POINTS) == null && map2.get(GraphConstants.POINTS) != null) {
               // inform listeners
               for (SystemDynamicsGraphModifiedEventListener listener : listeners) {
                  listener.performGraphModifiedEvent();
               }
            }
            if (map1.get(GraphConstants.POINTS) != null && map2.get(GraphConstants.POINTS) != null) {
               if (!map1.get(GraphConstants.POINTS).equals(map2.get(GraphConstants.POINTS))) {
                  // inform listeners
                  for (SystemDynamicsGraphModifiedEventListener listener : listeners) {
                     listener.performGraphModifiedEvent();
                  }
               }
            }
         }
         
         if (changedObject instanceof DefaultEdge && !(changedObject instanceof FlowEdge)) {
            // dependency edge

            Map map1 = (Map)change.getAttributes().get(changedObject);
            Map map2 = (Map)change.getPreviousAttributes().get(changedObject);
            if (map1.get(GraphConstants.POINTS) != null && map2.get(GraphConstants.POINTS) == null) {
               // inform listeners
               for (SystemDynamicsGraphModifiedEventListener listener : listeners) {
                  listener.performGraphModifiedEvent();
               }
            }
            if (map1.get(GraphConstants.POINTS) == null && map2.get(GraphConstants.POINTS) != null) {
               // inform listeners
               for (SystemDynamicsGraphModifiedEventListener listener : listeners) {
                  listener.performGraphModifiedEvent();
               }
            }
            if (map1.get(GraphConstants.POINTS) != null && map2.get(GraphConstants.POINTS) != null) {
               if (!map1.get(GraphConstants.POINTS).equals(map2.get(GraphConstants.POINTS))) {
                  // inform listeners
                  for (SystemDynamicsGraphModifiedEventListener listener : listeners) {
                     listener.performGraphModifiedEvent();
                  }
               }
            }
         }
      }
   }
}