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

package de.uka.aifb.com.systemDynamics.model;

import java.util.*;

/**
 * This class implements a System Dynamics model.
 * 
 * @author Joachim Melcher, University of Karlsruhe, AIFB
 * @version 1.0
 */
public class Model {
   
   private String modelName;
   
   protected HashSet<LevelNode> levelNodes;
   protected HashSet<RateNode> rateNodes;
   protected HashSet<ConstantNode> constantNodes;
   protected HashSet<AuxiliaryNode> auxiliaryNodes;
   protected HashSet<SourceSinkNode> sourceSinkNodes;
   
   protected boolean isChangeable;
   
   /** data structure used in method 'haveAuxiliaryNodesCycleDependency()' */
   private HashSet<AuxiliaryNode> visitedAuxiliaryNodes;
   private HashSet<AuxiliaryNode> finishedAuxiliaryNodes;
   private HashMap<AuxiliaryNode, HashSet<AuxiliaryNode>> adjacentListOfAuxiliaryNodes;
   
   /**
    * Constructor.
    */
   public Model() {
      levelNodes = new HashSet<LevelNode>();
      rateNodes = new HashSet<RateNode>();
      constantNodes = new HashSet<ConstantNode>();
      auxiliaryNodes = new HashSet<AuxiliaryNode>();
      sourceSinkNodes = new HashSet<SourceSinkNode>();
      
      isChangeable = true;
   }
   
   /**
    * Sets the model name.
    * 
    * @param modelName model name
    */
   public void setModelName(String modelName) {
      if (!isChangeable) {
         throw new ModelNotChangeableException();
      }
      if (modelName == null) {
         throw new IllegalArgumentException("'modelName' must not be null.");
      }
      
      this.modelName = modelName;
   }
   
   /**
    * Gets the model name.
    * 
    * @return model name
    */
   public String getModelName() {
      return modelName;
   }
   
   /////////////////////////////////////////////////////////////////////////////////////////////////
   // methods for creating new nodes
   /////////////////////////////////////////////////////////////////////////////////////////////////
   
   /**
    * Creates a new level node with the specified parameters and stores it in the model.
    * 
    * @param nodeName node name
    * @param startValue start value
    * @return created level node
    */
   public LevelNode createLevelNode(String nodeName, double startValue) {
      if (!isChangeable) {
         throw new ModelNotChangeableException();
      }
      
      LevelNode levelNode = new LevelNode(nodeName, startValue);
      levelNodes.add(levelNode);
      return levelNode;
   }
   
   /**
    * Creates a new rate node with the specified parameter and stores it in the model.
    * 
    * @param nodeName node name
    * @return created rate node
    */
   public RateNode createRateNode(String nodeName) {
      if (!isChangeable) {
         throw new ModelNotChangeableException();
      }
      
      RateNode rateNode = new RateNode(nodeName);
      rateNodes.add(rateNode);
      return rateNode;
   }
   
   /**
    * Creates a new constant node with the specified parameters and stores it in the model.
    * 
    * @param nodeName node name
    * @param constantValue constant value
    * @return created constant node
    */
   public ConstantNode createConstantNode(String nodeName, double constantValue) {
      if (!isChangeable) {
         throw new ModelNotChangeableException();
      }
      
      ConstantNode constantNode = new ConstantNode(nodeName, constantValue);
      constantNodes.add(constantNode);
      return constantNode;
   }
   
   /**
    * Creates a new auxiliary node with the specified parameter and stores it in the model.
    * 
    * @param nodeName node name
    * @return created auxiliary node
    */
   public AuxiliaryNode createAuxiliaryNode(String nodeName) {
      if (!isChangeable) {
         throw new ModelNotChangeableException();
      }
      
      AuxiliaryNode auxiliaryNode = new AuxiliaryNode(nodeName);
      auxiliaryNodes.add(auxiliaryNode);
      return auxiliaryNode;
   }
   
   /**
    * Creates a new source/sink node and stores it in the model.
    * 
    * @return created source/sink node
    */
   public SourceSinkNode createSourceSinkNode() {
      if (!isChangeable) {
         throw new ModelNotChangeableException();
      }
      
      SourceSinkNode sourceSinkNode = new SourceSinkNode();
      sourceSinkNodes.add(sourceSinkNode);
      return sourceSinkNode;
   }
   
   /////////////////////////////////////////////////////////////////////////////////////////////////
   // methods for changing existing nodes
   /////////////////////////////////////////////////////////////////////////////////////////////////
   
   /**
    * Removes the specified node from this model. If this node is a level node, a rate node or a
    * source/sink node, all incoming and outgoing flows to and from this node are also removed.
    * <p>
    * A node is not allowed to be removed if it is part of another node's formula.
    * 
    * @param node node to remove
    * @throws FormulaDependencyException if this node is part of the formula of another node
    */
   public void removeNode(AbstractNode node) throws FormulaDependencyException {
      if (!isChangeable) {
         throw new ModelNotChangeableException();
      }
      if (node == null) {
         throw new IllegalArgumentException("'node' must not be null.");
      }
      
      // node is a rate node
      if (node instanceof RateNode) {
         RateNode rateNode = (RateNode)node;
         
         // remove incoming flow
         AbstractNode source = rateNode.getFlowSource();
         if (source != null) {
            if (source instanceof LevelNode) {
               removeFlowFromLevelNode2RateNode((LevelNode)source, rateNode);
            }
            if (source instanceof SourceSinkNode) {
               removeFlowFromSourceSinkNode2RateNode((SourceSinkNode)source, rateNode);
            }
         }
         
         // remove outgoing flow
         AbstractNode sink = rateNode.getFlowSink();
         if (sink != null) {
            if (sink instanceof LevelNode) {
               removeFlowFromRateNode2LevelNode(rateNode, (LevelNode)sink);
            }
            if (sink instanceof SourceSinkNode) {
               removeFlowFromRateNode2SourceSinkNode(rateNode, (SourceSinkNode)sink);
            }
         }
         
         rateNodes.remove(node);
      }
      
      // node is a source/sink node
      if (node instanceof SourceSinkNode) {
         SourceSinkNode sourceSinkNode = (SourceSinkNode)node;
         
         // remove incoming flows
         for (RateNode rateNode : sourceSinkNode.getIncomingFlows()) {
            removeFlowFromRateNode2SourceSinkNode(rateNode, sourceSinkNode);
         }
         
         // remove outgoing flows
         for (RateNode rateNode : sourceSinkNode.getOutgoingFlows()) {
            removeFlowFromSourceSinkNode2RateNode(sourceSinkNode, rateNode);
         }
         
         sourceSinkNodes.remove(node);
      }
      
      // node is a level node, a constant node or an auxiliary node

      //   (1) check whether it is part of the formula of *another* rate node or auxiliary node
      for (RateNode rateNode : rateNodes) {
         if (node != rateNode) {
            if (rateNode.getAllNodesThisOneDependsOn().contains(node)) {
               throw new FormulaDependencyException(rateNode);
            }
         }
      }
      for (AuxiliaryNode auxiliaryNode : auxiliaryNodes) {
         if (node != auxiliaryNode) {
            if (auxiliaryNode.getAllNodesThisOneDependsOn().contains(node)) {
               throw new FormulaDependencyException(auxiliaryNode);
            }
         }
      }
      
      //   (2) if node is level node: remove incoming and outgoing flows
      if (node instanceof LevelNode) {
         LevelNode levelNode = (LevelNode)node;
         
         // remove incoming flows
         for (RateNode rateNode : levelNode.getIncomingFlows()) {
            removeFlowFromRateNode2LevelNode(rateNode, levelNode);
         }
         
         // remove outgoing flows
         for (RateNode rateNode : levelNode.getOutgoingFlows()) {
            removeFlowFromLevelNode2RateNode(levelNode, rateNode);
         }
      }
      
      //   (3) remove node
      if (node instanceof AuxiliaryNode) {
         auxiliaryNodes.remove(node);
      }
      if (node instanceof ConstantNode) {
         constantNodes.remove(node);
      }
      if (node instanceof LevelNode) {
         levelNodes.remove(node);
      }
   }
   
   /**
    * Sets the specified node's name.
    * 
    * @param node node to change
    * @param nodeName new node name
    */
   public void setNodeName(AbstractNode node, String nodeName) {
      if (!isChangeable) {
         throw new ModelNotChangeableException();
      }
      if (node == null) {
         throw new IllegalArgumentException("'node' must not be null.");
      }
      
      node.setNodeName(nodeName);
   }
   
   /**
    * Sets the specified level node's start value.
    * 
    * @param levelNode level node to change
    * @param startValue new start value
    */
   public void setStartValue(LevelNode levelNode, double startValue) {
      if (!isChangeable) {
         throw new ModelNotChangeableException();
      }
      if (levelNode == null) {
         throw new IllegalArgumentException("'levelNode' must not be null.");
      }
      
      levelNode.setStartValue(startValue);
   }
   
   /**
    * Sets the specified constant node's constant value.
    * 
    * @param constantNode constant node to change 
    * @param constantValue new constant value
    */
   public void setConstantValue(ConstantNode constantNode, double constantValue) {
      if (!isChangeable) {
         throw new ModelNotChangeableException();
      }
      if (constantNode == null) {
         throw new IllegalArgumentException("'constantNode' must not be null.");
      }
      
      constantNode.setConstantValue(constantValue);
   }
   
   /**
    * Sets the specified node's formula.
    * 
    * @param node node to change
    * @param formula new formula
    */
   public void setFormula(AbstractNode node, ASTElement formula) {
      if (!isChangeable) {
         throw new ModelNotChangeableException();
      }
      if (node == null) {
         throw new IllegalArgumentException("'node' must not be null.");
      }
      if (!(node instanceof AuxiliaryNode) && !(node instanceof RateNode)) {
         throw new IllegalArgumentException("'node' must be of type AuxiliaryNode or RateNode.");
      }
      
      if (node instanceof AuxiliaryNode) {
         ((AuxiliaryNode)node).setFormula(formula);
      } else {
         // node instanceof RateNode
         ((RateNode)node).setFormula(formula);
      }
   }
   
   /**
    * Adds a flow from the specified level node to the specified rate node. If there is already
    * another flow to this rate node, the addition of this flow is not possible.
    * 
    * @param levelNode level node
    * @param rateNode rate node
    * @return <code>true</code> iff the flow could be added
    */
   public boolean addFlowFromLevelNode2RateNode(LevelNode levelNode, RateNode rateNode) {
      if (!isChangeable) {
         throw new ModelNotChangeableException();
      }
      if (levelNode == null) {
         throw new IllegalArgumentException("'levelNode' must not be null.");
      }
      if (rateNode == null) {
         throw new IllegalArgumentException("'rateNode' must not be null.");
      }
      
      if (rateNode.getFlowSource() != null) {
         // there is already another flow to this rate node
         return false;
      }
      
      levelNode.addOutgoingFlow(rateNode);
      rateNode.setFlowSource(levelNode);
      return true;
   }
   
   /**
    * Removes the flow from the specified level node to the specified rate node.
    * 
    * @param levelNode level node
    * @param rateNode rate node
    * @return <code>true</code> iff the specified flow existed (and was removed)
    */
   public boolean removeFlowFromLevelNode2RateNode(LevelNode levelNode, RateNode rateNode) {
      if (!isChangeable) {
         throw new ModelNotChangeableException();
      }
      if (levelNode == null) {
         throw new IllegalArgumentException("'levelNode' must not be null.");
      }
      if (rateNode == null) {
         throw new IllegalArgumentException("'rateNode' must not be null.");
      }
      
      boolean rateNodeCorrect = rateNode.getFlowSource().equals(levelNode);
      boolean levelNodeCorrect = levelNode.getOutgoingFlows().contains(rateNode);
      
      if (rateNodeCorrect && levelNodeCorrect) {
         rateNode.removeFlowSource();
         return levelNode.removeOutgoingFlow(rateNode);
      }
      
      if (!rateNodeCorrect && !levelNodeCorrect) {
         // no such flow -> nothing to remove
         return false;
      }
      
      throw new Error("Flow to remove not stored consistently.");
   }
   
   /**
    * Adds a flow from the specified rate node to the specified level node. If there is already
    * another flow from this rate node, the addition of this flow is not possible.
    * 
    * @param rateNode rate node
    * @param levelNode level node
    * @return <code>true</code> iff the flow could be added
    */
   public boolean addFlowFromRateNode2LevelNode(RateNode rateNode, LevelNode levelNode) {
      if (!isChangeable) {
         throw new ModelNotChangeableException();
      }
      if (rateNode == null) {
         throw new IllegalArgumentException("'rateNode' must not be null.");
      }
      if (levelNode == null) {
         throw new IllegalArgumentException("'levelNode' must not be null.");
      }
      
      if (rateNode.getFlowSink() != null) {
         // there is already another flow from this rate node 
         return false;
      }
      
      rateNode.setFlowSink(levelNode);
      levelNode.addIncomingFlow(rateNode);
      return true;
   }
   
   /**
    * Removes the flow from the specified rate node to the specified level node.
    * 
    * @param rateNode rate node
    * @param levelNode level node
    * @return <code>true</code> iff the specified flow existed (and was removed)
    */
   public boolean removeFlowFromRateNode2LevelNode(RateNode rateNode, LevelNode levelNode) {
      if (!isChangeable) {
         throw new ModelNotChangeableException();
      }
      if (rateNode == null) {
         throw new IllegalArgumentException("'rateNode' must not be null.");
      }
      if (levelNode == null) {
         throw new IllegalArgumentException("'levelNode' must not be null.");
      }
      
      boolean rateNodeCorrect = rateNode.getFlowSink().equals(levelNode);
      boolean levelNodeCorrect = levelNode.getIncomingFlows().contains(rateNode);
      
      if (rateNodeCorrect && levelNodeCorrect) {
         rateNode.removeFlowSink();
         return levelNode.removeIncomingFlow(rateNode);
      }
      
      if (!rateNodeCorrect && !levelNodeCorrect) {
         // no such flow -> nothing to remove
         return false;
      }
      
      throw new Error("Flow to remove not stored consistently.");
   }
   
   /**
    * Adds a flow from the specified source/sink node to the specified rate node. If there is
    * already another flow to this rate node, the addition of this flow is not possible.
    * 
    * @param sourceSinkNode source/sink node
    * @param rateNode rate node
    * @return <code>true</code> iff the flow could be added
    */
   public boolean addFlowFromSourceSinkNode2RateNode(SourceSinkNode sourceSinkNode, RateNode rateNode) {
      if (!isChangeable) {
         throw new ModelNotChangeableException();
      }
      if (sourceSinkNode == null) {
         throw new IllegalArgumentException("'sourceSinkNode' must not be null.");
      }
      if (rateNode == null) {
         throw new IllegalArgumentException("'rateNode' must not be null.");
      }
      
      if (rateNode.getFlowSource() != null) {
         // there is already another flow to this rate node
         return false;
      }
      
      sourceSinkNode.addOutgoingFlow(rateNode);
      rateNode.setFlowSource(sourceSinkNode);
      return true;
   }
   
   /**
    * Removes the flow from the specified source/sink node to the specified rate node.
    * 
    * @param sourceSinkNode source/sink node
    * @param rateNode rate node
    * @return <code>true</code> iff the specified flow existed (and was removed)
    */
   public boolean removeFlowFromSourceSinkNode2RateNode(SourceSinkNode sourceSinkNode, RateNode rateNode) {
      if (!isChangeable) {
         throw new ModelNotChangeableException();
      }
      if (sourceSinkNode == null) {
         throw new IllegalArgumentException("'sourceSinkNode' must not be null.");
      }
      if (rateNode == null) {
         throw new IllegalArgumentException("'rateNode' must not be null.");
      }
      
      boolean rateNodeCorrect = rateNode.getFlowSource().equals(sourceSinkNode);
      boolean sourceSinkNodeCorrect = sourceSinkNode.getOutgoingFlows().contains(rateNode);
      
      if (rateNodeCorrect && sourceSinkNodeCorrect) {
         rateNode.removeFlowSource();
         return sourceSinkNode.removeOutgoingFlow(rateNode);
      }
      
      if (!rateNodeCorrect && !sourceSinkNodeCorrect) {
         // no such flow -> nothing to remove
         return false;
      }
      
      throw new Error("Flow to remove not stored consistently.");
   }
   
   /**
    * Adds a flow from the specified rate node to the specified source/sink node. If there is
    * already another flow from this rate node, the addition of this flow is not possible.
    * 
    * @param rateNode rate node
    * @param sourceSinkNode source/sink node
    * @return <code>true</code> iff the flow could be added
    */
   public boolean addFlowFromRateNode2SourceSinkNode(RateNode rateNode, SourceSinkNode sourceSinkNode) {
      if (!isChangeable) {
         throw new ModelNotChangeableException();
      }
      if (rateNode == null) {
         throw new IllegalArgumentException("'rateNode' must not be null.");
      }
      if (sourceSinkNode == null) {
         throw new IllegalArgumentException("'sourceSinkNode' must not be null.");
      }
      
      if (rateNode.getFlowSink() != null) {
         // there is already another flow from this rate node 
         return false;
      }
      
      rateNode.setFlowSink(sourceSinkNode);
      sourceSinkNode.addIncomingFlow(rateNode);
      return true;
   }
   
   /**
    * Removes the flow from the specified rate node to the specified level node.
    * 
    * @param rateNode rate node
    * @param sourceSinkNode source/sink node
    * @return <code>true</code> iff the specified flow existed (and was removed)
    */
   public boolean removeFlowFromRateNode2SourceSinkNode(RateNode rateNode, SourceSinkNode sourceSinkNode) {
      if (!isChangeable) {
         throw new ModelNotChangeableException();
      }
      if (rateNode == null) {
         throw new IllegalArgumentException("'rateNode' must not be null.");
      }
      if (sourceSinkNode == null) {
         throw new IllegalArgumentException("'sourceSinkNode' must not be null.");
      }
      
      boolean rateNodeCorrect = rateNode.getFlowSink().equals(sourceSinkNode);
      boolean sourceSinkNodeCorrect = sourceSinkNode.getIncomingFlows().contains(rateNode);
      
      if (rateNodeCorrect && sourceSinkNodeCorrect) {
         rateNode.removeFlowSink();
         return sourceSinkNode.removeIncomingFlow(rateNode);
      }
      
      if (!rateNodeCorrect && !sourceSinkNodeCorrect) {
         // no such flow -> nothing to remove
         return false;
      }
      
      throw new Error("Flow to remove not stored consistently.");
   }

   /////////////////////////////////////////////////////////////////////////////////////////////////
   // methods for getting existing nodes
   /////////////////////////////////////////////////////////////////////////////////////////////////
   
   /**
    * Gets the model's level nodes. A shallow clone of the set of level nodes is returned.
    * 
    * @return model's level nodes
    */
   public HashSet<LevelNode> getLevelNodes() {
      return (HashSet<LevelNode>)levelNodes.clone();
   }
   
   /**
    * Gets the model's rate nodes. A shallow clone of the set of rate nodes is returned.
    * 
    * @return model's rate nodes
    */
   public HashSet<RateNode> getRateNodes() {
      return (HashSet<RateNode>)rateNodes.clone();
   }
   
   /**
    * Gets the model's constant nodes. A shallow clone of the set of constant nodes is returned.
    * 
    * @return constant nodes
    */
   public HashSet<ConstantNode> getConstantNodes() {
      return (HashSet<ConstantNode>)constantNodes.clone();
   }
   
   /**
    * Gets the model's auxiliary nodes. A shallow clone of the set of auxiliary nodes is returned.
    * 
    * @return model's auxiliary nodes
    */
   public HashSet<AuxiliaryNode> getAuxiliaryNodes() {
      return (HashSet<AuxiliaryNode>)auxiliaryNodes.clone();
   }
   
   /**
    * Gets the model's source/sink nodes. A shallow clone of the set of source/sink nodes is
    * returned.
    * 
    * @return model's source/sink nodes
    */
   public HashSet<SourceSinkNode> getSourceSinkNodes() {
      return (HashSet<SourceSinkNode>)sourceSinkNodes.clone();
   }
   
   /**
    * Checks whether the model is changeable.
    * 
    * @return <code>true</code> iff the model is changeable
    */
   public boolean isChangeable() {
      return isChangeable;
   }
   
   /**
    * Validates the model and sets it unchangeable. If the model is valid, the method runs without
    * throwing any exception. Otherwise, an appropriate exception is thrown.
    * 
    * @throws AuxiliaryNodesCycleDependency if the model's auxiliary nodes have a cycle dependency
    * @throws NoFormulaException if a rate node or an auxiliary node has no formula
    * @throws NoLevelNodeException if model has no level node
    * @throws RateNodeFlowException if a rate node has no incoming and no outgoing flow
    * @throws UselessNodeException if a node has no influence on a level node
    */
   public void validateModelAndSetUnchangeable() throws AuxiliaryNodesCycleDependencyException,
                                                        NoFormulaException,
                                                        NoLevelNodeException,
                                                        RateNodeFlowException,
                                                        UselessNodeException {
      validateModel();
      isChangeable = false;
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
      // (i) MUST conditions:
      
      // (i) a) at least one level node
      if (levelNodes.isEmpty()) {
         throw new NoLevelNodeException();
      }
      
      // (i) b) each rate node must have an incoming and an outgoing flow
      for (RateNode rateNode : rateNodes) {
         // rate node must have flow source and flow sink
         if (rateNode.getFlowSource() == null || rateNode.getFlowSink() == null) {
            throw new RateNodeFlowException(rateNode);
         }
      }
      
      // (i) c) rate nodes and auxiliary nodes must have formulas
      for (RateNode rateNode : rateNodes) {
         if (!rateNode.hasFormula()) {
            throw new NoFormulaException(rateNode);
         }
      }
      
      for (AuxiliaryNode auxiliaryNode : auxiliaryNodes) {
         if (!auxiliaryNode.hasFormula()) {
            throw new NoFormulaException(auxiliaryNode);
         }
      }
      
      // (i) d) no cycles within auxiliary nodes dependencies
      if (haveAuxiliaryNodesCycleDependency()) {
         throw new AuxiliaryNodesCycleDependencyException();
      }
      
      // (ii) CAN conditions (optinal, but I decided to make them compulsory)
      
      HashSet<AbstractNode> nodesLevelNodesDependOn = getAllNodesLevelNodesDependOn();
      
      // (ii) a) all constant nodes must be useful (influence at least one level node)
      for (ConstantNode constantNode : constantNodes) {
         if (!nodesLevelNodesDependOn.contains(constantNode)) {
            throw new UselessNodeException(constantNode);
         }
      }
      
      // (ii) b) all auxiliary nodes must be useful (influence at least one leve node)
      for (AuxiliaryNode auxiliaryNode : auxiliaryNodes) {
         if (!nodesLevelNodesDependOn.contains(auxiliaryNode)) {
            throw new UselessNodeException(auxiliaryNode);
         }
      }
      
      // (ii) c) all source/sink nodes must be useful (influence at least one leve node)
      for (SourceSinkNode sourceSinkNode : sourceSinkNodes) {
         if (!nodesLevelNodesDependOn.contains(sourceSinkNode)) {
            throw new UselessNodeException(sourceSinkNode);
         }
      }
   }
   
   /**
    * Computes the nodes' values for the next time step.
    */
   public void computeNextValues() {
      if (isChangeable) {
         throw new ModelStillChangeableException();
      }
      
      // compute next values for auxiliary nodes (in topological order!)
      HashMap<AuxiliaryNode, HashSet<AuxiliaryNode>> adjacentList = getAdjacentListOfAuxiliaryNodes();
      HashMap<AuxiliaryNode, Integer> numberOfPredecessorsMap = getNumberOfPredecessorsMap();
      
      while (!numberOfPredecessorsMap.isEmpty()) {
         for (AuxiliaryNode auxiliaryNode : numberOfPredecessorsMap.keySet()) {
            if (numberOfPredecessorsMap.get(auxiliaryNode) == 0) {
               // 'auxiliaryNode' has no not updated auxiliary node predecessor
               
               // compute next value for that auxiliary node
               auxiliaryNode.computeNextValue();
               
               // decrease number of predecessors for all dependant nodes
               // (if there are any dependant nodes)
               HashSet<AuxiliaryNode> dependantAuxiliaryNodes = adjacentList.get(auxiliaryNode);
               if (dependantAuxiliaryNodes != null) {
                  for (AuxiliaryNode dependantAuxiliaryNode : adjacentList.get(auxiliaryNode)) {
                     int numberOfPredecessors = numberOfPredecessorsMap.get(dependantAuxiliaryNode);
                     numberOfPredecessors--;
                     numberOfPredecessorsMap.put(dependantAuxiliaryNode, numberOfPredecessors);
                  }
               }
               
               // remove 'auxiliaryNode' from mapping
               numberOfPredecessorsMap.remove(auxiliaryNode);
               
               // stop for loop
               break;
            }
         }
      }
      
      // compute next values for rate nodes
      for (RateNode rateNode : rateNodes) {
         rateNode.computeNextValue();
      }
      
      // compute next values for level nodes
      for (LevelNode levelNode : levelNodes) {
         levelNode.computeNextValue();
      }
   }
   
   /**
    * Checks whether the auxiliary nodes have a cycle dependency.
    * 
    * @return <code>true</code> iff the auxiliary nodes have a cycle dependency
    */
   private boolean haveAuxiliaryNodesCycleDependency() {
      visitedAuxiliaryNodes = new HashSet<AuxiliaryNode>();
      finishedAuxiliaryNodes = new HashSet<AuxiliaryNode>();
      adjacentListOfAuxiliaryNodes = new HashMap<AuxiliaryNode, HashSet<AuxiliaryNode>>();
      
      // initialize directed graph representation of auxiliary nodes dependencies
      adjacentListOfAuxiliaryNodes = getAdjacentListOfAuxiliaryNodes();
      
      // search for cycles using adapted depth-first search algorithm
      for (AuxiliaryNode auxiliaryNode : auxiliaryNodes) {
         if (DFS_visit(auxiliaryNode)) {
            // cycle found -> return true
            return true;
         }
      }
      
      // no cycle found -> return false
      return false;
   }
   
   /**
    * Searches for cycles using depth-first search (DFS) starting with the specified auxiliary node.
    * This method should only be invoked by method {@link #haveAuxiliaryNodesCycleDependency()}.
    * 
    * @param auxiliaryNode auxiliary node
    * @return <code>true</code> iff cycle was found in examined partial graph
    */
   private boolean DFS_visit(AuxiliaryNode auxiliaryNode) {
      if (auxiliaryNode == null) {
         throw new IllegalArgumentException("'auxiliaryNode' must not be null.");
      }
      
      if (finishedAuxiliaryNodes.contains(auxiliaryNode)) {
         return false;
      }
      if (visitedAuxiliaryNodes.contains(auxiliaryNode)) {
         // cycle found -> return true
         return true;
      }
      visitedAuxiliaryNodes.add(auxiliaryNode);
      
      // for each successor
      HashSet<AuxiliaryNode> successors = adjacentListOfAuxiliaryNodes.get(auxiliaryNode);
      if (successors != null) {
         for (AuxiliaryNode successor : successors) {
            if (DFS_visit(successor)) {
               // cycle found -> return true
               return true;
            }
         }
      }
      
      finishedAuxiliaryNodes.add(auxiliaryNode);
      
      // no cycle found -> return false
      return false;
   }
   
   /**
    * Gets all nodes the model's level nodes depend on.
    * 
    * @return set of all nodes the model's level nodes depend on
    */
   private HashSet<AbstractNode> getAllNodesLevelNodesDependOn() {
      HashSet<AbstractNode> nodeSet = new HashSet<AbstractNode>();
      LinkedList<AbstractNode> todoList = new LinkedList<AbstractNode>();
      HashSet<AbstractNode> nodesAlreadyProcessed = new HashSet<AbstractNode>();
      
      for (LevelNode levelNode : levelNodes) {
         for (RateNode rateNode : levelNode.getIncomingFlows()) {
            if (!nodesAlreadyProcessed.contains(rateNode)) {
               nodeSet.add(rateNode);
               todoList.add(rateNode);
               nodesAlreadyProcessed.add(rateNode);
            }
         }
         
         for (RateNode rateNode : levelNode.getOutgoingFlows()) {
            if (!nodesAlreadyProcessed.contains(rateNode)) {
               nodeSet.add(rateNode);
               todoList.add(rateNode);
               nodesAlreadyProcessed.add(rateNode);
            }
         }
      }
      
      while (!todoList.isEmpty()) {
         AbstractNode nodeToDo = todoList.removeFirst();
         if (nodeToDo instanceof RateNode) {
            RateNode rateNode = (RateNode)nodeToDo;
            for (AbstractNode node : rateNode.getAllNodesThisOneDependsOnAndSourceSinkNodes()) {
               if (!nodesAlreadyProcessed.contains(node)) {
                  nodeSet.add(node);
                  todoList.add(node);
                  nodesAlreadyProcessed.add(node);
               }
            }
         }
         if (nodeToDo instanceof AuxiliaryNode) {
            AuxiliaryNode auxiliaryNode = (AuxiliaryNode)nodeToDo;
            for (AbstractNode node : auxiliaryNode.getAllNodesThisOneDependsOn()) {
               if (!nodesAlreadyProcessed.contains(node)) {
                  nodeSet.add(node);
                  todoList.add(node);
                  nodesAlreadyProcessed.add(node);
               }
            }
         }
         // node instanceof LevelNode: do nothing (level nodes already processed!)
         
         // node instanceof ConstantNode: do nothing (constant nodes do not depend on other nodes!)
         
         // node instanceof SourceSinkNode: do nothing (source/sink nodes do not depend on other nodes!)
      }
      
      return nodeSet;
   }
   
   /**
    * Gets an adjacent list reprensentation of the auxiliary nodes dependency graph.
    * Auxiliary nodes without auxiliary node successor are not elements of the list.
    * 
    * @return adjacent list of the auxiliary nodes dependency graph
    */
   private HashMap<AuxiliaryNode, HashSet<AuxiliaryNode>> getAdjacentListOfAuxiliaryNodes() {
      HashMap<AuxiliaryNode, HashSet<AuxiliaryNode>> adjacentList =
         new HashMap<AuxiliaryNode, HashSet<AuxiliaryNode>>();
      
      // initialize directed graph representation of auxiliary nodes dependencies
      for (AuxiliaryNode auxiliaryNode : auxiliaryNodes) {
         HashSet<AbstractNode> nodesThisAuxiliaryNodeDependsOn =
            auxiliaryNode.getAllNodesThisOneDependsOn();
         for (AbstractNode node : nodesThisAuxiliaryNodeDependsOn) {
            if (node instanceof AuxiliaryNode) {
               AuxiliaryNode auxiliaryNodeThisAuxiliaryNodeDependsOn = (AuxiliaryNode)node;
               
               // insert into adjacent list
               if (!adjacentList.containsKey(auxiliaryNodeThisAuxiliaryNodeDependsOn)) {
                  // no key 'auxiliaryNodeThisAuxiliaryNodeDependsOn'
                  HashSet<AuxiliaryNode> valueNodes = new HashSet<AuxiliaryNode>();
                  valueNodes.add(auxiliaryNode);
                  adjacentList.put(auxiliaryNodeThisAuxiliaryNodeDependsOn, valueNodes);
               } else {
                  // key 'auxiliaryNodeThisAuxiliaryNodeDependsOn' already exists
                  HashSet<AuxiliaryNode> valueNodes =
                     adjacentList.get(auxiliaryNodeThisAuxiliaryNodeDependsOn);
                  valueNodes.add(auxiliaryNode);
               }
            }
         }
      }
      return adjacentList;
   }
   
   /**
    * Gets a mapping from the auxiliary nodes to the number of their predecessor auxiliary nodes.
    * 
    * @return mapping from auxiliary nodes to the number of their predecessor auxiliary nodes
    */
   private HashMap<AuxiliaryNode, Integer> getNumberOfPredecessorsMap() {
      HashMap<AuxiliaryNode, Integer> numberOfPredecessorsMap = new HashMap<AuxiliaryNode, Integer>();
      
      for (AuxiliaryNode auxiliaryNode : auxiliaryNodes) {
         HashSet<AbstractNode> nodesThisAuxiliaryNodeDependsOn =
            auxiliaryNode.getAllNodesThisOneDependsOn();
         
         int numberOfPredecessors = 0;
         for (AbstractNode node : nodesThisAuxiliaryNodeDependsOn) {
            if (node instanceof AuxiliaryNode) {
               numberOfPredecessors++;
            }
         }
         
         numberOfPredecessorsMap.put(auxiliaryNode, numberOfPredecessors);
      }
      
      return numberOfPredecessorsMap;
   }
}