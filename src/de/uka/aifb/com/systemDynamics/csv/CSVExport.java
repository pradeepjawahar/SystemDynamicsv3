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

package de.uka.aifb.com.systemDynamics.csv;

import java.io.*;
import java.text.DecimalFormat;

/**
 * This class implements a CSV format export to a file.
 * <p>
 * The system's standard encoding and line separator are used.
 * 
 * @author Joachim Melcher, Institut AIFB, Universitaet Karlsruhe (TH), Germany
 * @version 1.0
 */
public class CSVExport {
   
   /** column separator */
   public static final String SEPARATOR = ";";
   
   /** comment start symbol */
   public static final String COMMENT_START_SYMBOL = "#";
   
   private FileWriter fileWriter;
   private BufferedWriter bufferedWriter;
   private DecimalFormat myFormatter;
   private int numberColumns;
   
   /**
    * Constructor.
    * <p>
    * Writes the column names into the first line of the file.
    * 
    * @param fileName absolute file name
    * @param modelName model name
    * @param columnNames column names
    * @throws IOException if any IOException occurs
    */
   public CSVExport(String fileName, String modelName, String[] columnNames) throws IOException {
      if (fileName == null) {
         throw new IllegalArgumentException("'fileName' must not be null.");
      }
      if (modelName == null) {
         throw new IllegalArgumentException("'modelName' must not be null.");
      }
      if (columnNames == null) {
         throw new IllegalArgumentException("'columnNames' must not be null.");
      }
      if (columnNames.length < 1) {
         throw new IllegalArgumentException("'columnNames' must have at least length 1.");
      }
      
      numberColumns = columnNames.length;
      
      // double values for formatted with nine positions after decimal point
      myFormatter = new DecimalFormat("#.#########");
      
      File file = new File(fileName);
      fileWriter = new FileWriter(file);
      bufferedWriter = new BufferedWriter(fileWriter);
      
      // write model name to file
      bufferedWriter.write(COMMENT_START_SYMBOL + " " + modelName);
      
      bufferedWriter.newLine();
      
      // write column names to file
      StringBuffer columnNamesString = new StringBuffer();
      for (int i = 0; i < columnNames.length - 1; i++) {
         columnNamesString.append(columnNames[i]);
         columnNamesString.append(SEPARATOR);
      }
      columnNamesString.append(columnNames[columnNames.length - 1]);
      bufferedWriter.write(columnNamesString.toString());
   }
   
   /**
    * Writes the values into a separat line. The <code>double</code> values are formatted using the
    * system's standard locale decimal dot and maximal nine decimal places.
    * 
    * @param values values
    * @throws IOException if any IOException occurs
    */
   public void write(double[] values) throws IOException {
      if (values == null) {
         throw new IllegalArgumentException("'values' must not be null.");
      }
      if (values.length != numberColumns) {
         throw new IllegalArgumentException("'values' has not the correct length.");
      }
      
      bufferedWriter.newLine();
      StringBuffer valuesString = new StringBuffer();
      for (int i = 0; i < values.length - 1; i++) {
         valuesString.append(myFormatter.format(values[i]));
         valuesString.append(SEPARATOR);
      }
      valuesString.append(myFormatter.format(values[values.length - 1]));
      bufferedWriter.write(valuesString.toString());
   }
   
   /**
    * Closes the file.
    * 
    * @throws IOException if any IOException occurs
    */
   public void close() throws IOException {
      bufferedWriter.close();
      fileWriter.close();
   }
}