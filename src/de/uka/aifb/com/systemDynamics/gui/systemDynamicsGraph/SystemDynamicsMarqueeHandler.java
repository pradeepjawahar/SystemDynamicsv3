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

import de.uka.aifb.com.systemDynamics.SystemDynamics;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.*;
import java.util.ResourceBundle;
import javax.swing.*;
import org.jgraph.graph.*;

/**
 * This class implements a specialized marquee handler for System Dynamics graphs.
 * 
 * @author Joachim Melcher, Institut AIFB, Universitaet Karlsruhe (TH), Germany
 * @version 1.0
 */
public class SystemDynamicsMarqueeHandler extends BasicMarqueeHandler {
   
   private SystemDynamicsGraph graph;
   private SystemDynamics start;
   private JFrame frame;
   
   private ResourceBundle messages;
   
   // start point and current point
   private Point2D first;
   private Point2D current;

   // first and current port
   private PortView firstPort;
   private PortView currentPort;
   
   private DefaultGraphCell flowSource;
   private DefaultGraphCell flowTarget;
   
   /**
    * Constructor.
    * 
    * @param graph System Dynamics graph
    * @param start {@link de.uka.aifb.com.systemDynamics.SystemDynamics} instance
    * @param frame frame displaying graph
    */
   public SystemDynamicsMarqueeHandler(SystemDynamicsGraph graph, SystemDynamics start, JFrame frame) {
      if (graph == null) {
         throw new IllegalArgumentException("'graph' must not be null.");
      }
      if (start == null) {
         throw new IllegalArgumentException("'start' must not be null.");
      }
      if (frame == null) {
         throw new IllegalArgumentException("'frame' must not be null.");
      }
      
      this.graph = graph;
      this.start = start;
      this.frame = frame;
      
      messages = start.getMessages();
   }
   
   /**
    * Checks whether the specified mouse event should activate the marquee handler before any other
    * possible handler.
    * 
    * @param e mouse event
    * @return <code>true</code> iff the marquee handler should be forced
    */
   @Override
public boolean isForceMarqueeEvent(MouseEvent e) {
      if (e.isShiftDown()) {
         return false;
      }

      // if right mouse button we want to display the popup menu
      if (SwingUtilities.isRightMouseButton(e)) {
         return true;
      }
      // find and remember current port
      currentPort = getSourcePortAt(e.getPoint());
      // if current port found and in connecting mode (=ports visible)
      if (currentPort != null && graph.isPortsVisible()) {
         return true;
      }
      
      // else call superclass
      return super.isForceMarqueeEvent(e);
   }
   
   /**
    * Performs the specified mouse pressed event.
    * <p>
    * If the right mouse button is pressed, a corresponding popup menu is shown. Otherwise, remember
    * start location and first port
    * 
    * @param e mouse event
    */
   @Override
public void mousePressed(MouseEvent e) {
      if (SwingUtilities.isRightMouseButton(e)) {
         graph.createPopupMenu(e);
      } else {
         if (currentPort != null && graph.isPortsVisible()) {
            first = graph.toScreen(currentPort.getLocation());
            firstPort = currentPort;
            flowSource = (DefaultGraphCell)firstPort.getParentView().getCell();
         } else {
            super.mousePressed(e);
         }
      }
   }
   
   /**
    * Performs the specified mouse drapped event.
    * <p>
    * Finds port under mouse and repaints connector.
    * 
    * @param e mouse event
    */
   @Override
public void mouseDragged(MouseEvent e) {
      // If remembered Start Point is Valid
      if (start != null) {
         Graphics g = graph.getGraphics();
         // Reset Remembered Port
         PortView newPort = getTargetPortAt(e.getPoint());
         // Do not flicker (repaint only on real changes)
         if (newPort == null || newPort != currentPort) {
            // Xor-Paint the old Connector (Hide old Connector)
            paintConnector(Color.BLACK, graph.getBackground(), g);
            // If Port was found then Point to Port Location
            currentPort = newPort;
            if (currentPort != null) {
               current = graph.toScreen(currentPort.getLocation());
            } else {
               // Else If no Port was found then Point to Mouse Location   
               current = graph.snap(e.getPoint());
            }
            // Xor-Paint the new Connector
            paintConnector(graph.getBackground(), Color.black, g);
         }
      }
      // Call Superclass
      super.mouseDragged(e);
   }
   
   /**
    * Gets source port at the specified point.
    * 
    * @param point point
    * @return source port (but just of leve, rate or source/sink node) - <code>null</code> otherwise
    */
   public PortView getSourcePortAt(Point2D point) {
      // Disable jumping
      graph.setJumpToDefaultPort(false);
      PortView result = null;
      try {
         // Find a Port View in Model Coordinates and Remember
         PortView tempResult = graph.getPortViewAt(point.getX(), point.getY());
         if (tempResult != null) {
            // check node type
            Object cell = tempResult.getParentView().getCell();
            if (cell instanceof LevelNodeGraphCell || cell instanceof RateNodeGraphCell || cell instanceof SourceSinkNodeGraphCell) {
               result = tempResult;
            }
         }
      } finally {
         graph.setJumpToDefaultPort(true);
      }
      return result;
   }

   /**
    * Gets target port at the specified point.
    * 
    * @param point point
    * @return target port (but just level, rate or source/sink node AND fitting to the flow source
    *         node type) - <code>null</code> otherwise
    */
   protected PortView getTargetPortAt(Point2D point) {
      PortView tempResult = graph.getPortViewAt(point.getX(), point.getY());
      if (tempResult != null) {
         // check node type
         Object cell = tempResult.getParentView().getCell();
         if (flowSource instanceof LevelNodeGraphCell || flowSource instanceof SourceSinkNodeGraphCell) {
            if (cell instanceof RateNodeGraphCell) {
               // level node OR source/sink node -> rate node
               return tempResult;
            }
         } else {
            // flowSource instanceof RateNodeGraphCell
            if (cell instanceof LevelNodeGraphCell || cell instanceof SourceSinkNodeGraphCell) {
               // rate node -> level node OR source/sink node
               return tempResult;
            }
         }
      }
      
      // no (fitting) port found
      return null;
   }
   
   /**
    * Performs the specified mouse released event.
    * <p>
    * Connectes the first port and the current port in the graph or repaints.
    * 
    * @param e mouse event
    */
   @Override
public void mouseReleased(MouseEvent e) {
      if (e != null && currentPort != null && firstPort != null && firstPort != currentPort) {
         // add flow
         boolean successful = true;
         flowTarget = (DefaultGraphCell)currentPort.getParentView().getCell();
         if (flowSource instanceof LevelNodeGraphCell && flowTarget instanceof RateNodeGraphCell) {
            successful = graph.addFlow((LevelNodeGraphCell)flowSource, (RateNodeGraphCell)flowTarget);
         }
         if (flowSource instanceof SourceSinkNodeGraphCell && flowTarget instanceof RateNodeGraphCell) {
            successful = graph.addFlow((SourceSinkNodeGraphCell)flowSource, (RateNodeGraphCell)flowTarget);
         }
         if (flowSource instanceof RateNodeGraphCell && flowTarget instanceof LevelNodeGraphCell) {
            successful = graph.addFlow((RateNodeGraphCell)flowSource, (LevelNodeGraphCell)flowTarget);
         }
         if (flowSource instanceof RateNodeGraphCell && flowTarget instanceof SourceSinkNodeGraphCell) {
            successful = graph.addFlow((RateNodeGraphCell)flowSource, (SourceSinkNodeGraphCell)flowTarget);
         }
         
         if (!successful) {
            // flow could not be added
            JOptionPane.showMessageDialog(frame,
                                          messages.getString("SystemDynamicsMarqueeHandler.AddFlowError.Message"),
                                          messages.getString("SystemDynamicsMarqueeHandler.AddFlowError.Title"),
                                          JOptionPane.ERROR_MESSAGE);
            graph.repaint();
         }
         
         e.consume();
      } else {
         graph.repaint();
      }
      
      // reset global variables
      firstPort = currentPort = null;
      first = current = null;
      flowSource = flowTarget = null;
      
      // call superclass
      super.mouseReleased(e);
   }
   
   /**
    * Performs the specified mouse moved event.
    * <p>
    * Shows special cursor if mouse is over a port (of a level, rate or source/sink node)
    * 
    * @param e mouse event.
    */
   @Override
public void mouseMoved(MouseEvent e) {
      if (e != null && getSourcePortAt(e.getPoint()) != null && graph.isPortsVisible()) {
         graph.setCursor(new Cursor(Cursor.HAND_CURSOR));
         // Consume Event
         // Note: This is to signal the BasicGraphUI's
         // MouseHandle to stop further event processing.
         e.consume();
      } else {
         super.mouseMoved(e);
      }
   }

   /**
    * Paints connector.
    * 
    * @param fg foreground color
    * @param bg bachground color
    * @param g <code>Graphics</code> instance
    */
   private void paintConnector(Color fg, Color bg, Graphics g) {
      // Set Foreground
      g.setColor(fg);
      // Set Xor-Mode Color
      g.setXORMode(bg);
      // Highlight the Current Port
      paintPort(graph.getGraphics());
      // If Valid First Port, Start and Current Point
      if (firstPort != null && start != null && current != null)
         // Then Draw A Line From Start to Current Point
         g.drawLine((int) first.getX(), (int) first.getY(), (int) current.getX(), (int) current.getY());
   }

   /**
    * Paints port.
    * 
    * @param g <code>Graphics</code> instance
    */
   private void paintPort(Graphics g) {
      // If Current Port is Valid
      if (currentPort != null) {
         // If Not Floating Port...
         boolean o = (GraphConstants.getOffset(currentPort.getAllAttributes()) != null);
         // ...Then use Parent's Bounds
         Rectangle2D r = (o) ? currentPort.getBounds() : currentPort.getParentView().getBounds();
         // Scale from Model to Screen
         r = graph.toScreen((Rectangle2D) r.clone());
         // Add Space For the Highlight Border
         r.setFrame(r.getX() - 3, r.getY() - 3, r.getWidth() + 6, r.getHeight() + 6);
         // Paint Port in Preview (=Highlight) Mode
         graph.getUI().paintCell(g, currentPort, r, true);
      }
   }
}