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
 * This NodeParameterOutOfRangeException indicates that either a level node's start value or a
 * constant node's constant value is out of range.
 *
 * @author Joachim Melcher, University of Karlsruhe, AIFB
 * @version 1.0
 */
public class NodeParameterOutOfRangeException extends IllegalArgumentException {
   
   private static final long serialVersionUID = 1L;
   private static final String MESSAGE = "The node's (new) value is out of range.";
   
   // REMARK: problematic node is not stored because this exception is also thrown
   //         by the node constructor -> so there is no node object!
   
   private double minValue;
   private double maxValue;
   
   /**
    * Constructor.
    * 
    * @param minValue minimal allowed value
    * @param maxValue maximal allowed value
    */
   public NodeParameterOutOfRangeException(double minValue, double maxValue) {
      super(MESSAGE);

      if (minValue >= maxValue) {
         throw new IllegalArgumentException("'minValue' must be smaller than 'maxValue'.");
      }
      
      this.minValue = minValue;
      this.maxValue = maxValue;
   }
   
   /**
    * Gets the minimal allowed value.
    * 
    * @return minimal allowed value
    */
   public double getMinValue() {
      return minValue;
   }
   
   /**
    * Gets the maximal allowed value.
    * 
    * @return maximal allowed value
    */
   public double getMaxValue() {
      return maxValue;
   }
}