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
 * This class implements a specialized cell view factory for a System Dynamics graph.
 * <p>
 * It returns different cell views for each vertex type.
 * 
 * @author Joachim Melcher, Institut AIFB, Universitaet Karlsruhe (TH), Germany
 * @version 1.0
 */
public class SystemDynamicsCellViewFactory extends DefaultCellViewFactory {
   
   private static final long serialVersionUID = 1L;
   
   /**
    * Creates an edge view for the specified object.
    * 
    * @param cell edge
    * @return edge view
    */
   @Override
protected EdgeView createEdgeView(Object cell) {
      return new SystemDynamicsEdgeView(cell);
   }
   
   /**
    * Creates a vertex view for the specified object.
    * <p>
    * According to the vertex type, the method returns a specialized vertex view.
    * 
    * @param v vertex
    * @return vertex view
    */
   @Override
protected VertexView createVertexView(Object v) {
      // auxiliary node
      if (v instanceof AuxiliaryNodeGraphCell) {
         VertexView view = new AuxiliaryNodeVertexView(v);
         return view;
      }
      
      // constant node
      if (v instanceof ConstantNodeGraphCell) {
         VertexView view = new ConstantNodeVertexView(v);
         return view;
      }
      
      // rate node
      if (v instanceof RateNodeGraphCell) {
         VertexView view = new RateNodeVertexView(v);
         return view;
      }
      
      // source sink node
      if (v instanceof SourceSinkNodeGraphCell) {
         VertexView view = new SourceSinkNodeVertexView(v);
         return view;
      }
      
      return super.createVertexView(v);
   }
}