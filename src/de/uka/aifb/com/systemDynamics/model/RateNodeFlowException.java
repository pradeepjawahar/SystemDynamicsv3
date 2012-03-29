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
 * This RateNodeFlowException indicates that a rate node has no incoming <b>or</b> no outgoing flow.
 *
 * @author Joachim Melcher, University of Karlsruhe, AIFB
 * @version 1.0
 */
public class RateNodeFlowException extends Exception {
   
   private static final long serialVersionUID = 1L;
   private static final String MESSAGE = "A rate node has no incoming or no outgoing flow.";
   
   private RateNode problematicRateNode;
   
   /**
    * Constructor.
    * 
    * @param rateNode problematic rate node
    */
   public RateNodeFlowException(RateNode rateNode) {
      super(MESSAGE);
      if (rateNode == null) {
         throw new IllegalArgumentException("'rateNode' must not be null.");
      }
      this.problematicRateNode = rateNode;
   }
   
   /**
    * Gets the problematic rate node
    * 
    * @return problematic rate node
    */
   public RateNode getProblematicRateNode() {
      return problematicRateNode;
   }
}