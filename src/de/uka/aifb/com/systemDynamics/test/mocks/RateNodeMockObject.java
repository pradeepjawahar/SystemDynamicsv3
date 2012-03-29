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

package de.uka.aifb.com.systemDynamics.test.mocks;

import de.uka.aifb.com.systemDynamics.model.RateNode;

/**
 * This class implements a mock object for a rate node that always returns a specified "current"
 * value.
 * 
 * @author Joachim Melcher, University of Karlsruhe, AIFB
 * @version 1.0
 */
public class RateNodeMockObject extends RateNode {
   
   private double currentValue;
   
   /**
    * Constructor.
    * 
    * @param nodeName node name
    * @param currentValue "current" value that will always be returned by 'getCurrentValue()'
    */
   RateNodeMockObject(String nodeName, double currentValue) {
      super(nodeName);
      this.currentValue = currentValue;
   }
   
   /**
    * Helper method for creating new instances of this class. Called by JUnit test cases.
    * 
    * @param nodeName node name
    * @param currentValue "current" value that will always be returned by 'getCurrentValue()'
    * @return created new instance
    */
   private static RateNodeMockObject createRateNodeMockObject(String nodeName, double currentValue) {
      return new RateNodeMockObject(nodeName, currentValue);
   }
   
   /**
    * Gets the "current" value. But this is only a dummy value!
    * 
    * @return "current" value
    */
   @Override
public double getCurrentValue() {
      return currentValue;
   }
}