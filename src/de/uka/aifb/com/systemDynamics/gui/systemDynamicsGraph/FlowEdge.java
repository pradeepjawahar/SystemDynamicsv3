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

import org.jgraph.graph.*;

/**
 * This class implements a specialized edge for flows in System Dynamics graphs.
 * 
 * @author Joachim Melcher, Institut AIFB, Universitaet Karlsruhe (TH), Germany
 * @version 1.0
 */
public class FlowEdge extends DefaultEdge {
   
   private static final long serialVersionUID = 1L;
   
   /**
    * Constructor.
    * 
    * @param edgeSource edge source
    * @param edgeTarget edge target
    */
   public FlowEdge(DefaultGraphCell edgeSource, DefaultGraphCell edgeTarget) {
      if (edgeSource == null) {
         throw new IllegalArgumentException("'edgeSource' must not be null.");
      }
      if (edgeTarget == null) {
         throw new IllegalArgumentException("'edgeTarget' must not be null.");
      }
      if (!(edgeSource instanceof LevelNodeGraphCell)
            && !(edgeSource instanceof RateNodeGraphCell)
            && !(edgeSource instanceof SourceSinkNodeGraphCell)
            && !(edgeSource instanceof AuxiliaryNodeGraphCell)
            && !(edgeSource instanceof ConstantNodeGraphCell)) {
         throw new IllegalArgumentException("'edgeSource' must have a correct type.");
      }
      if (!(edgeTarget instanceof LevelNodeGraphCell)
            && !(edgeTarget instanceof RateNodeGraphCell)
            && !(edgeTarget instanceof SourceSinkNodeGraphCell)
            && !(edgeTarget instanceof AuxiliaryNodeGraphCell)
            && !(edgeTarget instanceof ConstantNodeGraphCell)) {
         throw new IllegalArgumentException("'edgeTarget' must have a correct type.");
      }
      
      setSource(edgeSource.getChildAt(0));
      setTarget(edgeTarget.getChildAt(0));
      
      GraphConstants.setLineWidth(getAttributes(), 3);
      GraphConstants.setLineEnd(getAttributes(), GraphConstants.ARROW_TECHNICAL);
   }
}