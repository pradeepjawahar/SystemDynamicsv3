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

package de.uka.aifb.com.systemDynamics.gui;

import de.uka.aifb.com.systemDynamics.SystemDynamics;
import java.io.File;
import java.util.ResourceBundle;
import javax.swing.filechooser.FileFilter;

/**
 * This class implements a file filter for CSV files.
 * 
 * @author Joachim Melcher, Institut AIFB, Universitaet Karlsruhe (TH), Germany
 * @version 1.0
 */
public class CSVFileFilter extends FileFilter {
   
   private ResourceBundle messages;
   
   /**
    * Constructor.
    * 
    * @param start {@link de.uka.aifb.com.systemDynamics.SystemDynamics} instance
    */
   public CSVFileFilter(SystemDynamics start) {
      if (start == null) {
         throw new IllegalArgumentException("'start' must not be null.");
      }
      
      messages = start.getMessages();
   }

   /**
    * Accepts directories and files ending with '.csv' (not case-sensitive).
    * 
    * @param file file
    * @return <code>true</code> iff the file is a directory or a CSV file
    */
   @Override
public boolean accept(File file) {
      if (file == null) {
         throw new IllegalArgumentException("'file' must not be null.");
      }

      if (file.isDirectory()) {
         return true;
      }

      String fileName = file.getName().toLowerCase();
      return fileName.endsWith(".csv");
   }

   /**
    * Gets a description of this filter.
    * 
    * @return description of this filter
    */
   @Override
public String getDescription() {
      return messages.getString("CSVFileFilter.Description");
   }
}