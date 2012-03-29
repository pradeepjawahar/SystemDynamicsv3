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

package de.uka.aifb.com.systemDynamics.xml;

import org.xml.sax.*;

/**
 * This class implements the <code>ErrorHandler</code> interface. It just "puts through" any
 * <code>SAXParseException</code>s for methods <code>error</code> and <code>fatalError</code>.
 * <p>
 * This class is necessary because Java 5 and Java 6 have different default error handlers what
 * causes different behaviour in the case of a non Schema compliant XML file.
 * 
 * @author Joachim Melcher, University of Karlsruhe, AIFB
 * @version 1.0
 */
public class MyErrorHandler implements ErrorHandler {

   /**
    * Receive notification of a recoverable error.
    * 
    * @param exception error information encapsulated in a SAX parse exception
    * @throws same SAX parse exception
    */
   public void error(SAXParseException exception) throws SAXParseException {
      throw exception;
   }
   
   /**
    * Receive notification of a non-recoverable error.
    * 
    * @param exception error information encapsulated in a SAX parse exception
    * @throws same SAX parse exception
    */
   public void fatalError(SAXParseException exception) throws SAXParseException {
      throw exception;
   }
   
   /**
    * Receive notification of a warning. This implementation just ignores the exception.
    * 
    * @param exception error information encapsulated in a SAX parse exception
    */
   public void warning(SAXParseException exception) {
      // do nothing
   }
}