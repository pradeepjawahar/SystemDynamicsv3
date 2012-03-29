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

/**
 * This XMLModelReaderWriterException indicates that an error occured during reading a model from
 * an XML file or writing a model into an XML file.
 *
 * @author Joachim Melcher, University of Karlsruhe, AIFB
 * @version 1.0
 */
public class XMLModelReaderWriterException extends Exception {
   
   private static final long serialVersionUID = 1L;
   private static final String MESSAGE = "An error occured during reading a model from an XML file"
                                       + " or writing a model into an XML file.";
   
   private Exception innerException;
   
   /**
    * Constructor.
    * 
    * @param exception the exception to be wrapped
    */
   public XMLModelReaderWriterException(Exception exception) {
      super(MESSAGE);
      
      if (exception == null) {
         throw new IllegalArgumentException("'exception' must not be null.");
      }
      
      this.innerException = exception;
   }
   
   /**
    * Returns the embedded exception.
    *  
    * @return embedded exception
    */
   public Exception getException() {
      return innerException;
   }
}