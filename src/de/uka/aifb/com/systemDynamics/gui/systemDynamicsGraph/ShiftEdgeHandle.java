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

import java.awt.event.MouseEvent;
import org.jgraph.graph.*;

/**
 * This class implements a specialized egde handle using Shift+mouse click for
 * adding or removing additonal control points.
 * 
 * @author Joachim Melcher, Institut AIFB, Universitaet Karlsruhe (TH), Germany
 * @version 1.0
 */
public class ShiftEdgeHandle extends EdgeView.EdgeHandle {
   
   private static final long serialVersionUID = 1L;

   /**
    * Constructor.
    * 
    * @param edgeView edge view
    * @param context graph context
    */
   public ShiftEdgeHandle(EdgeView edgeView, GraphContext context) {
      super(edgeView, context);
   }

   /**
    * Checks whether the specified mouse event signifies an event to add a new control point to an
    * edge.
    * 
    * @param event mouse event
    * @return <code>true</code> if a new control point is to be added - else <code>false</code>
    */
   @Override
public boolean isAddPointEvent(MouseEvent event) {
      // points are added using Shift-click
      return event.isShiftDown();
   }

   /**
    * Checks whether the specified mouse event signifies an event to remove a control point from an
    * edge.
    * 
    * @param event mouse event
    * @return <code>true</code> if a control point is to be remove - else <code>false</code>
    */
   @Override
public boolean isRemovePointEvent(MouseEvent event) {
      // points are removed using Shift-click
      return event.isShiftDown();
   }
}