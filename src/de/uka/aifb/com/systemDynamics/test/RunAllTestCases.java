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

import junit.framework.*;
 
/**
 * This class creates a test suite in order to execute all existing test cases.
 * 
 * @author Joachim Melcher, University of Karlsruhe, AIFB
 * @version 1.0
 */
public class RunAllTestCases extends TestCase {

   /**
    * Adds all existing test cases to the test suite so they can be executed at once.
    */
   public static Test suite() {
      TestSuite suite = new TestSuite();

      suite.addTest(ASTMinusTestCase.suite());
      suite.addTest(ASTMultiplyTestCase.suite());
      suite.addTest(ASTPlusTestCase.suite());
      suite.addTest(AuxiliaryNodeTestCase.suite());
      suite.addTest(ConstantNodeTestCase.suite());
      suite.addTest(CSVExportTestCase.suite());
      suite.addTest(FormulaParserTestCase.suite());
      suite.addTest(LevelNodeTestCase.suite());
      suite.addTest(ModelTestCase.suite());
      suite.addTest(NodeParameterOutOfRangeExceptionTestCase.suite());
      suite.addTest(RateNodeTestCase.suite());
      suite.addTest(SourceSinkNodeTestCase.suite());
      suite.addTest(XMLExportTestCase.suite());
      suite.addTest(XMLModelReaderTestCase.suite());
      suite.addTest(XMLModelReaderWriterExceptionTestCase.suite());
      suite.addTest(XMLModelWriterTestCase.suite());
      suite.addTest(XMLNodeParameterOutOfRangeExceptionTestCase.suite());
      suite.addTest(XMLRateNodeFlowExceptionTestCase.suite());
      suite.addTest(XMLUselessNodeExceptionTestCase.suite());
      
      return suite;
   }
}