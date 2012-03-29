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

/**
 * This FormulaDependencyException indicates that a node is not allowed to be removed from the model
 * because it is part of another node's formula.
 *
 * @author Joachim Melcher, University of Karlsruhe, AIFB
 * @version 1.0
 */
public class FormulaDependencyException extends Exception {
   
   private static final long serialVersionUID = 1L;
   private static final String MESSAGE =
      "The node that should be removed is part of another node's formula.";
   
   // REMARK: The node that cannot be removed has not to be stored because it is known from
   //         the 'removeNode' method invokation.
   
   private AbstractNode nodeWithProblematicFormula;
   
   /**
    * Constructor.
    * 
    * @param node node with problematic formula (must be a rate node or an auxiliary node)
    */
   public FormulaDependencyException(AbstractNode node) {
      super(MESSAGE);
      
      if (node == null) {
         throw new IllegalArgumentException("'node' must not be null.");
      }
      if (!(node instanceof RateNode) && !(node instanceof AuxiliaryNode)) {
         throw new IllegalArgumentException("'node' must be a rate node or an auxiliary node.");
      }
      nodeWithProblematicFormula = node;
   }
   
   /**
    * Gets the node with the problematic formula.
    * 
    * @return node with the problematic formula
    */
   public AbstractNode getNodeWithProblematicFormula() {
      return nodeWithProblematicFormula;
   }
}