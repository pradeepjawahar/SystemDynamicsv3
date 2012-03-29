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
 * This interface describes an abstract syntax tree (AST) element.
 * 
 * @author Joachim Melcher, University of Karlsruhe, AIFB
 * @version 1.0
 */
public interface ASTElement extends Iterable<ASTElement> {

   /**
    * Evaluates the ASTElement.
    * 
    * @return ASTElement value
    */
   public double evaluate();
      
   /**
    * Gets all nodes in this AST subtree (inclusive this ASTElement).
    * 
    * @return set of all nodes in AST subtree
    */
   public HashSet<AbstractNode> getAllNodesInASTSubtree();
   
   /**
    * Gets a <code>String</code> representation of the node's formula.
    * 
    * @return <code>String</code> representation of the node's formula
    */
   public String getStringRepresentation();
   
   /**
    * Gets a short <code>String</code> representation of the node's formula.
    * 
    * @param auxiliaryNode2id auxiliary node to id mapping
    * @param constantNode2id constant node to id mapping
    * @param levelNode2id level node to id mapping
    * @return short <code>String</code> representation of the node's formula
    */
   public String getShortStringRepresentation(HashMap<AuxiliaryNode, Integer> auxiliaryNode2id,
                                              HashMap<ConstantNode, Integer> constantNode2id,
                                              HashMap<LevelNode, Integer> levelNode2id);
   
   /**
    * Creates and returns a <b>deep</b> copy of this object. Only the nodes in the leaves
    * are not cloned.
    * 
    * @return a deep clone of this instance
    */
   public Object clone();
}