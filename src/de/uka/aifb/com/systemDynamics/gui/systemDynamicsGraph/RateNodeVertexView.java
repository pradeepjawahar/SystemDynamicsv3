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

import java.awt.geom.*;
import org.jgraph.graph.*;

/**
 * This class implements a specialized vertex view for a System Dynamics rate node.
 * 
 * @author Joachim Melcher, Institut AIFB, Universitaet Karlsruhe (TH), Germany
 * @version 1.0
 */
public class RateNodeVertexView extends VertexView {
   
   private static final long serialVersionUID = 1L;
   
   private static RateNodeVertexRenderer renderer = RateNodeVertexRenderer.getInstance();

   /**
    * Constructor.
    * 
    * @param cell rate node vertex
    */
   public RateNodeVertexView(Object cell) {
      super(cell);
   }
   
   /**
    * Gets the perimeter point.
    * <p>
    * For flows edges, the center is returned - otherwise the upper or bottom middle
    * 
    * @param edge edge view
    * @param source (is ignored)
    * @param p other point
    * @return perimeter point
    */
   @Override
public Point2D getPerimeterPoint(EdgeView edge, Point2D source, Point2D p) {
      Rectangle2D r = getBounds();
      
      if (edge.getCell() instanceof FlowEdge) {
         return getAttributes().createPoint(r.getCenterX(), r.getCenterY());
      }

      double x = r.getX() + r.getWidth() / 2;
      double y;
      if (p.getY() < r.getY() + r.getHeight() / 2) {
         y = r.getY();
      } else {
         y = r.getY() + r.getHeight();
      }

      return getAttributes().createPoint(x, y);
   }

   /**
    * Gets the vertex renderer.
    * 
    * @return vertex renderer
    */
   @Override
public CellViewRenderer getRenderer() {
      return renderer;
   }
}