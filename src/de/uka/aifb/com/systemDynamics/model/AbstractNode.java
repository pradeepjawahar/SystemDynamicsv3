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
 * This class implements an abstract node of a System Dynamics model.
 * It only centralizes common properties of all model node types.
 * 
 * All methods of AbstractNode and its subclasses are only visible within
 * this package. Only getter methods whose return values are not changeable
 * from outside can be public. All setter methods must be invoked using
 * an adequate method from class
 * {@link de.uka.aifb.com.systemDynamics.model.Model}.
 * 
 * @author Joachim Melcher, University of Karlsruhe, AIFB
 * @version 1.0
 */
public abstract class AbstractNode {
   
   private String nodeName;
   
   /**
    * Sets the node name.
    * 
    * @param nodeName node name
    */
   void setNodeName(String nodeName) {
      if (nodeName == null) {
         throw new IllegalArgumentException("'nodeName' must not be null.");
      }
      
      this.nodeName = nodeName;
   }
   
   /**
    * Gets the node name.
    * 
    * @return node name
    */
   public String getNodeName() {
      return nodeName;
   }
   
   /**
    * Gets the node's current value.
    * 
    * @return current value
    */
   public abstract double getCurrentValue();
   
   /**
    * Computes the node's next value.
    */
   abstract void computeNextValue();
}