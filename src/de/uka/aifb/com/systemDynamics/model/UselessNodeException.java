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

/*
 * Changes:
 * ========
 *
 * 2007-08-30: constructor was rewritten: useless node can also be a source/sink node
 */

/**
 * This UselessNodeException indicates that a node of the model is useless, i.e. has no influence
 * on a level node. Only constant nodes and auxiliary nodes can be useless.
 *
 * @author Joachim Melcher, University of Karlsruhe, AIFB
 * @version 1.1
 */
public class UselessNodeException extends Exception {
   
   private static final long serialVersionUID = 1L;
   private static final String MESSAGE = "There is a useless node in the model.";
   
   private AbstractNode uselessNode;
   
   /**
    * Constructor.
    * 
    * @param node useless node (must be a constant, an auxiliary or a source/sink node)
    */
   public UselessNodeException(AbstractNode node) {
      super(MESSAGE);
      if (node == null) {
         throw new IllegalArgumentException("'node' must not be null.");
      }
      if (!(node instanceof ConstantNode) && !(node instanceof AuxiliaryNode)
            && !(node instanceof SourceSinkNode)) {
         throw new IllegalArgumentException("'node' must be a constant, an auxiliary or a source/sink node.");
      }
      uselessNode = node;
   }
   
   /**
    * Gets the useless node.
    * 
    * @return useless node
    */
   public AbstractNode getUselessNode() {
      return uselessNode;
   }
}