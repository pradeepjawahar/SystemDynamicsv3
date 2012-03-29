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

import de.uka.aifb.com.systemDynamics.xml.XMLNodeParameterOutOfRangeException;
import junit.framework.*;

/**
 * This class implements a test case for the class
 * {@link de.uka.aifb.com.systemDynamics.xml.XMLNodeParameterOutOfRangeException}.
 * 
 * @author Joachim Melcher, University of Karlsruhe, AIFB
 * @version 1.0
 */
public class XMLNodeParameterOutOfRangeExceptionTestCase extends TestCase {
   
   private static final String NODE_ID = "LN_0001";
   private static final double MIN_VALUE = -15.0;
   private static final double MAX_VALUE = 10.0;
   
   private XMLNodeParameterOutOfRangeException exception;
   
   public static Test suite() {  
      return new TestSuite(XMLNodeParameterOutOfRangeExceptionTestCase.class);
   }
   
   @Override
protected void setUp() throws Exception {
      exception = new XMLNodeParameterOutOfRangeException(NODE_ID, MIN_VALUE, MAX_VALUE);
   }

   @Override
protected void tearDown() throws Exception {
      exception = null;
   }
   
   /**
    * Tests the constructor.
    */
   public void testXMLNodeParameterOutOfRangeException() {
      // (1) 'nodeId' = null -> WRONG
      try {
         new XMLNodeParameterOutOfRangeException(null, 0.0, 1.0);
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'xmlNodeId' must not be null.", e.getMessage());
      }
      
      // (2) 'minValue' > 'maxValue' -> WRONG
      try {
         new XMLNodeParameterOutOfRangeException(NODE_ID, 1.0, 0.0);
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'minValue' must be smaller than 'maxValue'.", e.getMessage());
      }
      
      // (3) 'minValue' = 'maxValue' -> WRONG
      try {
         new XMLNodeParameterOutOfRangeException(NODE_ID, 0.0, 0.0);
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'minValue' must be smaller than 'maxValue'.", e.getMessage());
      }
      
      // (4) 'minValue' < 'maxValue' -> CORRECT
      new XMLNodeParameterOutOfRangeException(NODE_ID, 0.0, 1.0);
   }
   
   /**
    * Tests the method
    * {@link de.uka.aifb.com.systemDynamics.xml.XMLNodeParameterOutOfRangeException#getXMLNodeId()}.
    */
   public void testGetXMLNodeId() {
      assertEquals(NODE_ID, exception.getXMLNodeId());
   }
   
   /**
    * Tests the method
    * {@link de.uka.aifb.com.systemDynamics.xml.XMLNodeParameterOutOfRangeException#getMinValue()}.
    */
   public void testGetMinValue() {
      assertTrue(exception.getMinValue() == MIN_VALUE);
   }
   
   /**
    * Tests the method
    * {@link de.uka.aifb.com.systemDynamics.xml.XMLNodeParameterOutOfRangeException#getMaxValue()}.
    */
   public void testGetMaxValue() {
      assertTrue(exception.getMaxValue() == MAX_VALUE);
   }
   
   /**
    * Tests the method
    * {@link de.uka.aifb.com.systemDynamics.xml.XMLNodeParameterOutOfRangeException#getMessage()}.
    */
   public void testGetMessage() {
      assertEquals("A node's value is out of range.", exception.getMessage());
   }
}