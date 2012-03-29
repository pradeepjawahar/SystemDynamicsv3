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
 * This class implements a specialized vertex view for a System Dynamics source/sink node.
 * 
 * @author Joachim Melcher, Institut AIFB, Universitaet Karlsruhe (TH), Germany
 * @version 1.0
 */
public class SourceSinkNodeVertexView extends VertexView {
   
   private static final long serialVersionUID = 1L;
   
   private static SourceSinkNodeVertexRenderer renderer = SourceSinkNodeVertexRenderer.getInstance();

   /**
    * Constructor.
    * 
    * @param cell source/sink node vertex
    */
   public SourceSinkNodeVertexView(Object cell) {
      super(cell);
   }
   
   /**
    * Gets the perimeter point.
    * 
    * @param edge edge view (is ignored)
    * @param source (is ignored)
    * @param p other point
    * @return perimeter point
    */
   @Override
public Point2D getPerimeterPoint(EdgeView edge, Point2D source, Point2D p) {
      Rectangle2D r = getBounds();
      GeneralPath cloud = new CloudShapeFactory(r.getWidth(), r.getHeight(), 0).createCloudShape();

      double x = r.getX();
      double y = r.getY();

      // x0,y0 - "center" of cloud
      double x0 = x + r.getWidth() / 2;
      double y0 = y + r.getHeight() / 2;
      
      // x1, y1 - point
      double x1 = p.getX();
      double y1 = p.getY();
      
      double xout = 0;
      double yout = 0;
      
      if (x0 == x1) {
         xout = x0;

         if (cloud.contains(x1 - x, y1 - y)) {  // parameter coordinates relative to cloud!
            // point p inside cloud
            if (y1 < y0) {
               yout = y;
            } else {
               yout = y + r.getHeight();
            }
         } else {
            // point p outside cloud
            double diffY = y1 - y0;
            double tMin = 0;
            double tMax = 1;
            double t = 0.5;
            for (int i = 0; i < 10; i++) {
               yout = y0 + t * diffY;
               if (cloud.contains(xout - x, yout - y)) {  // parameter coordinates relative to cloud!
                  tMin = t;
                  t = (t + tMax) / 2;
               } else {
                  tMax = t;
                  t = (t + tMin) / 2;
               }
            }
         }
      } else {
         // x0 != x1
         if (cloud.contains(x1 - x, y1 - y)) {  // parameter coordinates relative to cloud!
            // point p inside cloud
            xout = x0;
            if (y1 < y0) {
               yout = y;
            } else {
               yout = y + r.getHeight();
            }
         } else {
            // point p outside cloud
            
            // calculate straight line equation through point and circle center
            // y = d * x + h
            double dx = x1 - x0;
            double dy = y1 - y0;

            double d = dy / dx;
            double h = y0 - d * x0;
            
            double diffX = x1 - x0;
            double tMin = 0;
            double tMax = 1;
            double t = 0.5;
            for (int i = 0; i < 10; i++) {
               xout = x0 + t * diffX;
               yout = d * xout + h;
               if (cloud.contains(xout - x, yout - y)) {  // parameter coordinates relative to cloud!
                  tMin = t;
                  t = (t + tMax) / 2;
               } else {
                  tMax = t;
                  t = (t + tMin) / 2;
               }
            }
         }
      }
      
      return getAttributes().createPoint(xout, yout);
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