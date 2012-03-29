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

import de.uka.aifb.com.systemDynamics.xml.XMLModelReaderWriterException;
import junit.framework.*;

/**
 * This class implements a test case for the class
 * {@link de.uka.aifb.com.systemDynamics.xml.XMLModelReaderWriterException}.
 * 
 * @author Joachim Melcher, University of Karlsruhe, AIFB
 * @version 1.0
 */
public class XMLModelReaderWriterExceptionTestCase extends TestCase {
   
   private static final Exception INNER_EXCEPTION = new Exception("DUMMY EXCEPTION");
   
   private XMLModelReaderWriterException exception;
   
   public static Test suite() {  
      return new TestSuite(XMLModelReaderWriterExceptionTestCase.class);
   }
   
   @Override
protected void setUp() throws Exception {
      exception = new XMLModelReaderWriterException(INNER_EXCEPTION);
   }

   @Override
protected void tearDown() throws Exception {
      exception = null;
   }
   
   /**
    * Tests the constructor.
    */
   public void testXMLModelReaderWriterException() {
      // (1) parameter 'null' -> WRONG
      try {
         new XMLModelReaderWriterException(null);
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'exception' must not be null.", e.getMessage());
      }
      
      // (2) correct parameter
      new XMLModelReaderWriterException(INNER_EXCEPTION);
   }
   
   /**
    * Tests the method
    * {@link de.uka.aifb.com.systemDynamics.xml.XMLModelReaderWriterException#getException()}.
    */
   public void testGetException() {
      assertTrue(exception.getException() == INNER_EXCEPTION);
   }
}