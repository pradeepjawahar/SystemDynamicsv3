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

import de.uka.aifb.com.systemDynamics.xml.*;
import java.io.*;
import javax.xml.XMLConstants;
import javax.xml.parsers.*;
import javax.xml.validation.*;
import junit.framework.*;
import org.xml.sax.SAXException;

/**
 * This class implements a test case for the class
 * {@link de.uka.aifb.com.systemDynamics.xml.XMLExport}.
 * 
 * @author Joachim Melcher, University of Karlsruhe, AIFB
 * @version 1.0
 */
public class XMLExportTestCase extends TestCase {
   
   private static final String XSD_FILE_NAME = "./xsd/modelExecutionExport.xsd";
   
   private static final String FILE_NAME = "temp_xml_export.xml";
   private static final String MODEL_NAME = "MODEL NAME";
   private static final int NUMBER_ROUNDS = 2;
   private static final String[] NODE_NAMES = { "Level node 1", "Level node 2" };
   
   private XMLExport xmlExport;

   public static Test suite() {  
      return new TestSuite(XMLExportTestCase.class);
   }
   
   @Override
protected void setUp() throws Exception {
      xmlExport = new XMLExport(FILE_NAME, MODEL_NAME, NUMBER_ROUNDS, NODE_NAMES);
   }

   @Override
protected void tearDown() throws Exception {
      xmlExport.close();
      
      // delete temporary XML file
      File file = new File(FILE_NAME);
      if (file.exists()) {
         assertTrue(new File(FILE_NAME).delete());
      }
   }
   
   /**
    * Tests the constructor.
    */
   public void testXMLExport() {
      // (1) 'fileName' null -> WRONG
      try {
         new XMLExport(null, MODEL_NAME, NUMBER_ROUNDS, NODE_NAMES);
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'fileName' must not be null.", e.getMessage());
      } catch (Exception e) {
         // should not happen
         fail();
      }
      
      // (2) 'modelName' null -> WRONG
      try {
         new XMLExport(FILE_NAME, null, NUMBER_ROUNDS, NODE_NAMES);
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'modelName' must not be null.", e.getMessage());
      } catch (Exception e) {
         // should not happen
         fail();
      }
      
      // (3) 'numberRounds' 0 -> WRONG
      try {
         new XMLExport(FILE_NAME, MODEL_NAME, 0, NODE_NAMES);
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'numberRounds' must be at least 1.", e.getMessage());
      } catch (Exception e) {
         // should not happen
         fail();
      }
      
      // (4) 'nodeNames' null -> WRONG
      try {
         new XMLExport(FILE_NAME, MODEL_NAME, NUMBER_ROUNDS, null);
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'nodeNames' must not be null.", e.getMessage());
      } catch (Exception e) {
         // should not happen
         fail();
      }
      
      // (5) 'nodeNames' length 0 -> WRONG
      try {
         new XMLExport(FILE_NAME, MODEL_NAME, NUMBER_ROUNDS, new String[0]);
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'nodeNames' must have at least length 1.", e.getMessage());
      } catch (Exception e) {
         // should not happen
         fail();
      }
      
      // inserted just for closing already existing streams and writers
      try {
         double[] correctValues0 = { 0.0, 1.00000001000 };
         double[] correctValues1 = { -34567892.4597, 1.5e5 };
         double[] correctValues2 = { -24, 0.666 };
         xmlExport.write(correctValues0);
         xmlExport.write(correctValues1);
         xmlExport.write(correctValues2);
         xmlExport.close();
      } catch (IOException e) {
         fail();
      }
      
      // (6) correct parameters -> CORRECT
      try {
         xmlExport = new XMLExport(FILE_NAME, MODEL_NAME, NUMBER_ROUNDS, NODE_NAMES);
      } catch (Exception e) {
         // should not happen
         fail();
      }
      
      double[] correctValues0 = { 0.0, 1.00000001000 };
      double[] correctValues1 = { -34567892.4597, 1.5e5 };
      double[] correctValues2 = { -24, 0.666 };
      try {
         xmlExport.write(correctValues0);
         xmlExport.write(correctValues1);
         xmlExport.write(correctValues2);
      } catch (Exception e) {
         // should not happen
         fail();
      }
   }
   
   /**
    * Tests the method {@link de.uka.aifb.com.systemDynamics.xml.XMLExport#write(double[])}.
    */
   public void testWrite() {
      // (1) 'values' null -> WRONG
      try {
         xmlExport.write(null);
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
         xmlExport.write(wrongValues);
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'values' has not the correct length.", e.getMessage());
      } catch (Exception e) {
         // should not happen
         fail();
      }
      
      // (3) correct parameters -> CORRECT
      double[] correctValues0 = { 0.0, 1.00000001000 };
      double[] correctValues1 = { -34567892.4597, 1.5e5 };
      double[] correctValues2 = { -24, 0.666 };
      try {
         xmlExport.write(correctValues0);
         xmlExport.write(correctValues1);
         xmlExport.write(correctValues2);
         xmlExport.close();
      } catch (Exception e) {
         // should not happen
         fail();
      }
      
      // (3a) check XML schema compliance
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  // can throw FactoryConfiguration Error

      // create schema
      SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
      Schema schema = null;
      try {
         schema = schemaFactory.newSchema(new File(XSD_FILE_NAME));
      } catch (SAXException e) {
         // exception should not happen because schema is correct!
         fail();
      }
      
      factory.setSchema(schema);
      
      DocumentBuilder builder = null;
      try {
         builder = factory.newDocumentBuilder();
      } catch (ParserConfigurationException e) {
         // exception should not happen
         fail();
      }
      
      // set own error handler that throws exception if XML file is not Schema compliant
      builder.setErrorHandler(new MyErrorHandler());
      
      try {
         builder.parse(new File(FILE_NAME));   // can throw IOException
                                               // can throw SAXException
      } catch (Exception e) {
         fail();
      }
      
      // (3b) check content of file
      FileReader fileReader = null;
      try {
         fileReader = new FileReader(FILE_NAME);
      } catch (FileNotFoundException e) {
         fail();
      }
      try {
         BufferedReader bufferedReader = new BufferedReader(fileReader);
         assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", bufferedReader.readLine());
         assertEquals("<ModelExecutionExport modelName=\"MODEL NAME\" numberRounds=\"2\" schema=\"http://www.aifb.uni-karlsruhe.de/com/systemDynamics/modelExecutionExport-schema\" schemaVersion=\"1.0\">", bufferedReader.readLine());
         assertEquals("   <LevelNodes>", bufferedReader.readLine());
         assertEquals("      <LevelNode id=\"1\" name=\"Level node 1\"/>", bufferedReader.readLine());
         assertEquals("      <LevelNode id=\"2\" name=\"Level node 2\"/>", bufferedReader.readLine());
         assertEquals("   </LevelNodes>", bufferedReader.readLine());
         assertEquals("   <ExecutionValues>", bufferedReader.readLine());
         assertEquals("      <RoundValues round=\"0\">", bufferedReader.readLine());
         assertEquals("         <LevelNodeValue nodeIdRef=\"1\" value=\"0\"/>", bufferedReader.readLine());
         assertEquals("         <LevelNodeValue nodeIdRef=\"2\" value=\"1.00000001\"/>", bufferedReader.readLine());
         assertEquals("      </RoundValues>", bufferedReader.readLine());
         assertEquals("      <RoundValues round=\"1\">", bufferedReader.readLine());
         assertEquals("         <LevelNodeValue nodeIdRef=\"1\" value=\"-34567892.4597\"/>", bufferedReader.readLine());
         assertEquals("         <LevelNodeValue nodeIdRef=\"2\" value=\"150000\"/>", bufferedReader.readLine());
         assertEquals("      </RoundValues>", bufferedReader.readLine());
         assertEquals("      <RoundValues round=\"2\">", bufferedReader.readLine());
         assertEquals("         <LevelNodeValue nodeIdRef=\"1\" value=\"-24\"/>", bufferedReader.readLine());
         assertEquals("         <LevelNodeValue nodeIdRef=\"2\" value=\"0.666\"/>", bufferedReader.readLine());
         assertEquals("      </RoundValues>", bufferedReader.readLine());
         assertEquals("   </ExecutionValues>", bufferedReader.readLine());
         assertEquals("</ModelExecutionExport>", bufferedReader.readLine());

         bufferedReader.close();
         fileReader.close();
      } catch (IOException e) {
         fail();
      }
   }
   
   /**
    * Tests the method {@link de.uka.aifb.com.systemDynamics.xml.XMLExport#delete()}.
    */
   public void testDelete() {
      try {
         xmlExport.delete();
      } catch (IOException e) {
         // should not happen
         fail();
      }
      
      assertFalse(new File(FILE_NAME).exists());
   }
}