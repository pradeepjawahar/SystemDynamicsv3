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

package de.uka.aifb.com.systemDynamics.test;

import de.uka.aifb.com.systemDynamics.model.NodeParameterOutOfRangeException;
import junit.framework.*;

/**
 * This class implements a test case for the class
 * {@link de.uka.aifb.com.systemDynamics.model.NodeParameterOutOfRangeException}.
 * 
 * @author Joachim Melcher, University of Karlsruhe, AIFB
 * @version 1.0
 */
public class NodeParameterOutOfRangeExceptionTestCase extends TestCase {
   
   private static final double MIN_VALUE = -15.0;
   private static final double MAX_VALUE = 10.0;
   
   private NodeParameterOutOfRangeException exception;
   
   public static Test suite() {  
      return new TestSuite(NodeParameterOutOfRangeExceptionTestCase.class);
   }
   
   @Override
protected void setUp() throws Exception {
      exception = new NodeParameterOutOfRangeException(MIN_VALUE, MAX_VALUE);
   }

   @Override
protected void tearDown() throws Exception {
      exception = null;
   }
   
   /**
    * Tests the constructor.
    */
   public void testNodeParameterOutOfRangeException() {
      // (1) 'minValue' > 'maxValue' -> WRONG
      try {
         new NodeParameterOutOfRangeException(1.0, 0.0);
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'minValue' must be smaller than 'maxValue'.", e.getMessage());
      }
      
      // (2) 'minValue' = 'maxValue' -> WRONG
      try {
         new NodeParameterOutOfRangeException(0.0, 0.0);
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'minValue' must be smaller than 'maxValue'.", e.getMessage());
      }
      
      // (3) 'minValue' < 'maxValue' -> CORRECT
      new NodeParameterOutOfRangeException(0.0, 1.0);
   }
   
   /**
    * Tests the method
    * {@link de.uka.aifb.com.systemDynamics.model.NodeParameterOutOfRangeException#getMinValue()}.
    */
   public void testGetMinValue() {
      assertTrue(exception.getMinValue() == MIN_VALUE);
   }
   
   /**
    * Tests the method
    * {@link de.uka.aifb.com.systemDynamics.model.NodeParameterOutOfRangeException#getMaxValue()}.
    */
   public void testGetMaxValue() {
      assertTrue(exception.getMaxValue() == MAX_VALUE);
   }
   
   /**
    * Tests the method
    * {@link de.uka.aifb.com.systemDynamics.model.NodeParameterOutOfRangeException#getMessage()}.
    */
   public void testGetMessage() {
      assertEquals("The node's (new) value is out of range.", exception.getMessage());
   }
}