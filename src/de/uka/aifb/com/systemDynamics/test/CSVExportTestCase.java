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

import de.uka.aifb.com.systemDynamics.csv.CSVExport;
import java.io.*;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * This class implements a test case for the class
 * {@link de.uka.aifb.com.systemDynamics.csv.CSVExport}.
 * 
 * @author Joachim Melcher, University of Karlsruhe, AIFB
 * @version 1.0
 */
public class CSVExportTestCase extends TestCase {
   
   private static final String FILE_NAME = "temp_csv_export.csv";
   private static final String MODEL_NAME = "MODEL NAME";
   private static final String[] COLUMN_NAMES = { "Column 1", "Column 2" };
   
   private CSVExport csvExport;

   public static Test suite() {  
      return new TestSuite(CSVExportTestCase.class);
   }
   
   @Override
protected void setUp() throws Exception {
      csvExport = new CSVExport(FILE_NAME, MODEL_NAME, COLUMN_NAMES);
   }

   @Override
protected void tearDown() throws Exception {
      csvExport.close();
      
      // delete temporary CSV file
      assertTrue(new File(FILE_NAME).delete());
   }
   
   /**
    * Tests the constructor.
    */
   public void testCSVExport() {
      // (1) 'fileName' null -> WRONG
      try {
         new CSVExport(null, MODEL_NAME, COLUMN_NAMES);
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'fileName' must not be null.", e.getMessage());
      } catch (Exception e) {
         // should not happen
         fail();
      }
      
      // (2) 'modelName' null -> WRONG
      try {
         new CSVExport(FILE_NAME, null, COLUMN_NAMES);
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'modelName' must not be null.", e.getMessage());
      } catch (Exception e) {
         // should not happen
         fail();
      }
      
      // (3) 'columnNames' null -> WRONG
      try {
         new CSVExport(FILE_NAME, MODEL_NAME, null);
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'columnNames' must not be null.", e.getMessage());
      } catch (Exception e) {
         // should not happen
         fail();
      }
      
      // (4) 'columnNames' length 0 -> WRONG
      try {
         new CSVExport(FILE_NAME, MODEL_NAME, new String[0]);
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'columnNames' must have at least length 1.", e.getMessage());
      } catch (Exception e) {
         // should not happen
         fail();
      }
      
      // (5) correct parameters -> CORRECT
      try {
         CSVExport csvExport = new CSVExport(FILE_NAME, MODEL_NAME, COLUMN_NAMES);
         csvExport.close();         
      } catch (Exception e) {
         // should not happen
         fail();
      }
   }
   
   /**
    * Tests the method {@link de.uka.aifb.com.systemDynamics.csv.CSVExport#write(double[])}.
    */
   public void testWrite() {
      // (1) 'values' null -> WRONG
      try {
         csvExport.write(null);
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'values' must not be null.", e.getMessage());
      } catch (Exception e) {
         // should not happen
         fail();
      }
      
      // (2) 'values' wrong length -> WRONG
      double[] wrongValues = { 0.0, 1.0, 2.0 };
      try {
         csvExport.write(wrongValues);
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'values' has not the correct length.", e.getMessage());
      } catch (Exception e) {
         // should not happen
         fail();
      }
      
      // (3) correct parameters -> CORRECT
      double[] correctValues1 = { 0.0, 1.00000001000 };
      double[] correctValues2 = { -34567892.4597, 1.5e5 };
      try {
         csvExport.write(correctValues1);
         csvExport.write(correctValues2);
         csvExport.close();
      } catch (Exception e) {
         // should not happen
         fail();
      }
      
      FileReader fileReader = null;
      try {
         fileReader = new FileReader(FILE_NAME);
      } catch (FileNotFoundException e) {
         fail();
      }
      try {
         BufferedReader bufferedReader = new BufferedReader(fileReader);
         assertEquals(CSVExport.COMMENT_START_SYMBOL + " " + MODEL_NAME, bufferedReader.readLine());
         assertEquals("Column 1;Column 2", bufferedReader.readLine());
         assertEquals("0;1,00000001", bufferedReader.readLine());
         assertEquals("-34567892,4597;150000", bufferedReader.readLine());

         bufferedReader.close();
         fileReader.close();
      } catch (IOException e) {
         fail();
      }
   }
}