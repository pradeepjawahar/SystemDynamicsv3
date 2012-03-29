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

import java.io.*;
import java.text.*;
import java.util.Locale;

/**
 * This class implements an XML format export to a file.
 * <p>
 * The system's standard encoding and line separator are used.
 * 
 * @author Joachim Melcher, Institut AIFB, Universitaet Karlsruhe (TH), Germany
 * @version 1.0
 */
public class XMLExport {
   
   private static final String SCHEMA = "http://www.aifb.uni-karlsruhe.de/com/systemDynamics/modelExecutionExport-schema";
   private static final String SCHEMA_VERSION = "1.0";
   private static final int INDENTION_STEP = 3;
   
   private File file;
   private FileOutputStream fileOutputStream;
   private OutputStreamWriter outputStreamWriter;
   private BufferedWriter bufferedWriter;
   
   private NumberFormat myFormatter;
   private int numberNodes;
   private int numberRounds;
   private int roundsStored;
   
   private boolean closed;
   
   /**
    * Constructor.
    * <p>
    * Writes the column names into the first line of the file.
    * 
    * @param fileName absolute file name
    * @param modelName model name
    * @param numberRounds number of rounds
    * @param nodeNames node names
    * @throws IOException if any IOException occurs
    */
   public XMLExport(String fileName, String modelName, int numberRounds, String[] nodeNames) throws IOException {
      if (fileName == null) {
         throw new IllegalArgumentException("'fileName' must not be null.");
      }
      if (modelName == null) {
         throw new IllegalArgumentException("'modelName' must not be null.");
      }
      if (numberRounds < 1) {
         throw new IllegalArgumentException("'numberRounds' must be at least 1.");
      }
      if (nodeNames == null) {
         throw new IllegalArgumentException("'nodeNames' must not be null.");
      }
      if (nodeNames.length < 1) {
         throw new IllegalArgumentException("'nodeNames' must have at least length 1.");
      }
      
      numberNodes = nodeNames.length;
      this.numberRounds = numberRounds;
      
      // double values for formatted with nine positions after decimal point
      myFormatter = NumberFormat.getInstance(Locale.US);
      if (myFormatter instanceof DecimalFormat) {
         ((DecimalFormat) myFormatter).setGroupingUsed(false);
         ((DecimalFormat) myFormatter).setMaximumFractionDigits(9);
     }
      
      file = new File(fileName);
      fileOutputStream = new FileOutputStream(file);
      outputStreamWriter = new OutputStreamWriter(fileOutputStream, "UTF-8");
      bufferedWriter = new BufferedWriter(outputStreamWriter);
      
      // write XML header to file
      bufferedWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
      bufferedWriter.newLine();
      
      // write Model element line to file
      bufferedWriter.write("<ModelExecutionExport modelName=\"" + modelName + "\" numberRounds=\"" + numberRounds + "\" schema=\"" + SCHEMA + "\" schemaVersion=\"" + SCHEMA_VERSION + "\">");
      bufferedWriter.newLine();
      
      // write level nodes to file
      bufferedWriter.write(getIndention(INDENTION_STEP) + "<LevelNodes>");
      bufferedWriter.newLine();
      for (int i = 0; i < nodeNames.length; i++) {
         bufferedWriter.write(getIndention(2 * INDENTION_STEP) + "<LevelNode id=\"" + (i + 1) + "\" name=\"" + nodeNames[i] + "\"/>");
         bufferedWriter.newLine();
      }
      bufferedWriter.write(getIndention(INDENTION_STEP) + "</LevelNodes>");
      bufferedWriter.newLine();
      
      // write opening ExecutionValues element
      bufferedWriter.write(getIndention(INDENTION_STEP) + "<ExecutionValues>");
      bufferedWriter.newLine();
   }
   
   /**
    * Writes the values of a round to the file.
    * 
    * @param values values
    * @throws IOException if any IOException occurs
    */
   public void write(double[] values) throws IOException {
      if (values == null) {
         throw new IllegalArgumentException("'values' must not be null.");
      }
      if (values.length != numberNodes) {
         throw new IllegalArgumentException("'values' has not the correct length.");
      }
      
      // write opening RoundValues element
      bufferedWriter.write(getIndention(2 * INDENTION_STEP) + "<RoundValues round=\"" + roundsStored++ + "\">");
      bufferedWriter.newLine();
      
      // write node values to file
      for (int i = 0; i < numberNodes; i++) {
         bufferedWriter.write(getIndention(3 * INDENTION_STEP) + "<LevelNodeValue nodeIdRef=\"" + (i + 1) + "\" value=\"" + myFormatter.format(values[i]) + "\"/>");
         bufferedWriter.newLine();
      }
      
      // write closing RoundValues element
      bufferedWriter.write(getIndention(2 * INDENTION_STEP) + "</RoundValues>");
      bufferedWriter.newLine();
   }
   
   /**
    * Closes the file.
    * 
    * @throws IOException if any IOException occurs
    */
   public void close() throws IOException {
      if (!closed) {
         if (roundsStored != numberRounds + 1) {  // "+1" because of "round 0"
            throw new IllegalStateException("Incorrect number of rounds stored.");
         }
         
         // write closing ExecutionValues element
         bufferedWriter.write(getIndention(INDENTION_STEP) + "</ExecutionValues>");
         
         bufferedWriter.newLine();
         
         // write closing ModelExecutionExport element
         bufferedWriter.write("</ModelExecutionExport>");
         
         bufferedWriter.close();
         outputStreamWriter.close();
         fileOutputStream.close();
         
         closed = true;
      }
   }
   
   /**
    * Deletes the started file.
    * 
    * @throws IOException if any IOException occurs
    */
   public void delete() throws IOException {
      if (!closed) {
         bufferedWriter.close();
         outputStreamWriter.close();
         fileOutputStream.close();
         
         closed = true;
         
         file.delete();
      }
   }
   
   /**
    * Gets a <code>String</code> consisting of spaces ('indention' times).
    * 
    * @param indention indention
    * @return indention String
    */
   private String getIndention(int indention) {
      if (indention < 0) {
         throw new IllegalArgumentException("'indention' must not be negative.");
      }
      
      StringBuffer buffer = new StringBuffer();
      for (int i = 0; i < indention; i++) {
         buffer.append(" ");
      }
      
      return buffer.toString();
   }
}