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

import java.awt.*;
import java.awt.geom.GeneralPath;
import org.jgraph.graph.*;

/**
 * This class implements a specialized vertex renderer for a System Dynamics source/sink node.
 * <p>
 * This class implements the singleton design pattern.
 * 
 * @author Joachim Melcher, Institut AIFB, Universitaet Karlsruhe (TH), Germany
 * @version 1.0
 */
public class SourceSinkNodeVertexRenderer extends VertexRenderer {
   
   private static final long serialVersionUID = 1L;
   
   private static SourceSinkNodeVertexRenderer instance;
   
   /**
    * Constructor.
    */
   private SourceSinkNodeVertexRenderer() {
   }
   
   /**
    * Gets the <code>SourceSinkNodeVertexRenderer</code instance.
    * 
    * @return instance
    */
   public static SourceSinkNodeVertexRenderer getInstance() {
      if (instance == null) {
         instance = new SourceSinkNodeVertexRenderer();
      }
      return instance;
   }
   
   /**
    * Renders the source/sink node vertex.
    * 
    * @param g graphics
    */
   @Override
public void paint(Graphics g) {
      int b = borderWidth;
      Graphics2D g2 = (Graphics2D) g;
      Dimension d = getSize();
      boolean tmp = selected;
      GeneralPath cloud = new CloudShapeFactory(d.getWidth(), d.getHeight(), borderWidth).createCloudShape();
      if (super.isOpaque()) {
         g.setColor(super.getBackground());
         if (gradientColor != null && !preview) {
            setOpaque(false);
            g2.setPaint(new GradientPaint(0, 0, getBackground(),
                  getWidth(), getHeight(), gradientColor, true));
         }
         g2.fill(cloud);
      }
      try {
         setBorder(null);
         setOpaque(false);
         selected = false;
         super.paint(g);
      } finally {
         selected = tmp;
      }
      if (bordercolor != null) {
         g.setColor(bordercolor);
         g2.setStroke(new BasicStroke(b));
         g2.draw(cloud);
      }
      if (selected) {
         g2.setStroke(GraphConstants.SELECTION_STROKE);
         g.setColor(highlightColor);
         g2.draw(cloud);
      }
   }
}