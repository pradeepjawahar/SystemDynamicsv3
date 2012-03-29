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

import de.uka.aifb.com.systemDynamics.xml.XMLUselessNodeException;
import junit.framework.*;

/**
 * This class implements a test case for the class
 * {@link de.uka.aifb.com.systemDynamics.xml.XMLUselessNodeException}.
 * 
 * @author Joachim Melcher, University of Karlsruhe, AIFB
 * @version 1.0
 */
public class XMLUselessNodeExceptionTestCase extends TestCase {

   private static final String NODE_ID = "LN_0001";
   
   private XMLUselessNodeException exception;
   
   public static Test suite() {  
      return new TestSuite(XMLUselessNodeExceptionTestCase.class);
   }
   
   @Override
protected void setUp() throws Exception {
      exception = new XMLUselessNodeException(NODE_ID);
   }

   @Override
protected void tearDown() throws Exception {
      exception = null;
   }
   
   /**
    * Tests the constructor.
    */
   public void testXMLUselessNodeException() {
      // (1) 'nodeId' = null -> WRONG
      try {
         new XMLUselessNodeException(null);
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'xmlNodeId' must not be null.", e.getMessage());
      }
      
      // (2) correct parameter -> CORRECT
      new XMLUselessNodeException(NODE_ID);
   }
   
   /**
    * Tests the method
    * {@link de.uka.aifb.com.systemDynamics.xml.XMLUselessNodeException#getXMLNodeId()}.
    */
   public void testGetXMLNodeId() {
      assertEquals(NODE_ID, exception.getXMLNodeId());
   }
}