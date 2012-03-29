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

import de.uka.aifb.com.systemDynamics.model.*;
import de.uka.aifb.com.systemDynamics.test.mocks.RateNodeMockObject;
import java.util.HashSet;
import junit.framework.*;
import junitx.util.PrivateAccessor;

/**
 * This class implements a test case for the class
 * {@link de.uka.aifb.com.systemDynamics.model.SourceSinkNode}.
 * 
 * @author Joachim Melcher, University of Karlsruhe, AIFB
 * @version 1.0
 */
public class SourceSinkNodeTestCase extends TestCase {
   
   private SourceSinkNode sourceSinkNode;
   
   public static Test suite() {  
      return new TestSuite(LevelNodeTestCase.class);
   }
   
   @Override
protected void setUp() throws Exception {
      sourceSinkNode = createSourceSinkNode();
   }

   @Override
protected void tearDown() throws Exception {
      sourceSinkNode = null;
   }
   
   /**
    * Tests the methods
    * {@link de.uka.aifb.com.systemDynamics.model.SourceSinkNode#addIncomingFlow(RateNode)},
    * {@link de.uka.aifb.com.systemDynamics.model.SourceSinkNode#removeIncomingFlow(RateNode)}
    * and {@link de.uka.aifb.com.systemDynamics.model.SourceSinkNode#getIncomingFlows()}.
    */
   public void testIncomingFlows() {
      // (1) no incoming flow at beginning
      assertTrue(sourceSinkNode.getIncomingFlows().isEmpty());
      
      // (2) add first incoming flow
      RateNodeMockObject firstRateNode = createRateNodeMockObject("Rate Node 1", 5);
      Boolean successful = null;
      try {
         successful =
            (Boolean)PrivateAccessor.invoke(sourceSinkNode, "addIncomingFlow",
                                            new Class[] { RateNode.class },
                                            new Object[] { firstRateNode });
      } catch (Throwable t) {
         fail();
      }
      assertTrue(successful.booleanValue());
      assertTrue(sourceSinkNode.getIncomingFlows().size() == 1);
      sourceSinkNode.getIncomingFlows().contains(firstRateNode);
      
      // (3) try to add first incoming flow for the second time
      successful = null;
      try {
         successful =
            (Boolean)PrivateAccessor.invoke(sourceSinkNode, "addIncomingFlow",
                                            new Class[] { RateNode.class },
                                            new Object[] { firstRateNode });
      } catch (Throwable t) {
         fail();
      }
      assertFalse(successful.booleanValue());
      assertTrue(sourceSinkNode.getIncomingFlows().size() == 1);
      sourceSinkNode.getIncomingFlows().contains(firstRateNode);
      
      // (4) add second incoming flow
      RateNodeMockObject secondRateNode = createRateNodeMockObject("Rate Node 2", 2);
      successful = null;
      try {
         successful =
            (Boolean)PrivateAccessor.invoke(sourceSinkNode, "addIncomingFlow",
                                            new Class[] { RateNode.class },
                                            new Object[] { secondRateNode });
      } catch (Throwable t) {
         fail();
      }
      assertTrue(successful.booleanValue());
      assertTrue(sourceSinkNode.getIncomingFlows().size() == 2);
      sourceSinkNode.getIncomingFlows().contains(firstRateNode);
      sourceSinkNode.getIncomingFlows().contains(secondRateNode);
      
      // (5) try to remove not existing incoming flow
      RateNodeMockObject thirdRateNode = createRateNodeMockObject("Rate Node 3", 3);
      successful = null;
      try {
         successful =
            (Boolean)PrivateAccessor.invoke(sourceSinkNode, "removeIncomingFlow",
                                            new Class[] { RateNode.class },
                                            new Object[] { thirdRateNode });
      } catch (Throwable t) {
         fail();
      }
      assertFalse(successful.booleanValue());
      assertTrue(sourceSinkNode.getIncomingFlows().size() == 2);
      sourceSinkNode.getIncomingFlows().contains(firstRateNode);
      sourceSinkNode.getIncomingFlows().contains(secondRateNode);
      
      // (6) remove first added incoming flow
      successful = null;
      try {
         successful =
            (Boolean)PrivateAccessor.invoke(sourceSinkNode, "removeIncomingFlow",
                                            new Class[] { RateNode.class },
                                            new Object[] { firstRateNode });
      } catch (Throwable t) {
         fail();
      }
      assertTrue(successful.booleanValue());
      assertTrue(sourceSinkNode.getIncomingFlows().size() == 1);
      sourceSinkNode.getIncomingFlows().contains(secondRateNode);
      
      // (7) check that 'getIncomingFlows()' returns only a clone of the set
      HashSet<RateNode> incomingFlows = sourceSinkNode.getIncomingFlows();
      incomingFlows.add(thirdRateNode);
      assertTrue(incomingFlows.size() == 2);
      assertTrue(sourceSinkNode.getIncomingFlows().size() == 1);
      
      // (8) parameter not 'null' for method 'addIncomingFlow(RateNode)'
      try {
         PrivateAccessor.invoke(sourceSinkNode, "addIncomingFlow", new Class[] { RateNode.class },
                                new Object[] { null });
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'incomingFlow' must not be null.", e.getMessage());
      } catch (Throwable t) {
         fail();
      }
      
      // (9) parameter not 'null' for method 'removeIncomingFlow(RateNode)'
      try {
         PrivateAccessor.invoke(sourceSinkNode, "removeIncomingFlow", new Class[] { RateNode.class },
                                new Object[] { null });
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'incomingFlow' must not be null.", e.getMessage());
      } catch (Throwable t) {
         fail();
      }
   }
   
   /**
    * Tests the methods
    * {@link de.uka.aifb.com.systemDynamics.model.SourceSinkNode#addOutgoingFlow(RateNode)},
    * {@link de.uka.aifb.com.systemDynamics.model.SourceSinkNode#removeOutgoingFlow(RateNode)}
    * and {@link de.uka.aifb.com.systemDynamics.model.SourceSinkNode#getOutgoingFlows()}.
    */
   public void testOutgoingFlows() {
      // (1) no outgoing flow at beginning
      assertTrue(sourceSinkNode.getOutgoingFlows().isEmpty());
      
      // (2) add first outgoing flow
      RateNodeMockObject firstRateNode = createRateNodeMockObject("Rate Node 1", 5);
      Boolean successful = null;
      try {
         successful =
            (Boolean)PrivateAccessor.invoke(sourceSinkNode, "addOutgoingFlow",
                                            new Class[] { RateNode.class },
                                            new Object[] { firstRateNode });
      } catch (Throwable t) {
         fail();
      }
      assertTrue(successful.booleanValue());
      assertTrue(sourceSinkNode.getOutgoingFlows().size() == 1);
      sourceSinkNode.getOutgoingFlows().contains(firstRateNode);
      
      // (3) try to add first outgoing flow for the second time
      successful = null;
      try {
         successful =
            (Boolean)PrivateAccessor.invoke(sourceSinkNode, "addOutgoingFlow",
                                            new Class[] { RateNode.class },
                                            new Object[] { firstRateNode });
      } catch (Throwable t) {
         fail();
      }
      assertFalse(successful.booleanValue());
      assertTrue(sourceSinkNode.getOutgoingFlows().size() == 1);
      sourceSinkNode.getOutgoingFlows().contains(firstRateNode);
      
      // (4) add second outgoing flow
      RateNodeMockObject secondRateNode = createRateNodeMockObject("Rate Node 2", 2);
      successful = null;
      try {
         successful =
            (Boolean)PrivateAccessor.invoke(sourceSinkNode, "addOutgoingFlow",
                                            new Class[] { RateNode.class },
                                            new Object[] { secondRateNode });
      } catch (Throwable t) {
         fail();
      }
      assertTrue(successful.booleanValue());
      assertTrue(sourceSinkNode.getOutgoingFlows().size() == 2);
      sourceSinkNode.getOutgoingFlows().contains(firstRateNode);
      sourceSinkNode.getOutgoingFlows().contains(secondRateNode);
      
      // (5) try to remove not existing outgoing flow
      RateNodeMockObject thirdRateNode = createRateNodeMockObject("Rate Node 3", 3);
      successful = null;
      try {
         successful =
            (Boolean)PrivateAccessor.invoke(sourceSinkNode, "removeOutgoingFlow",
                                            new Class[] { RateNode.class },
                                            new Object[] { thirdRateNode });
      } catch (Throwable t) {
         fail();
      }
      assertFalse(successful.booleanValue());
      assertTrue(sourceSinkNode.getOutgoingFlows().size() == 2);
      sourceSinkNode.getOutgoingFlows().contains(firstRateNode);
      sourceSinkNode.getOutgoingFlows().contains(secondRateNode);
      
      // (6) remove first added outgoing flow
      successful = null;
      try {
         successful =
            (Boolean)PrivateAccessor.invoke(sourceSinkNode, "removeOutgoingFlow",
                                            new Class[] { RateNode.class },
                                            new Object[] { firstRateNode });
      } catch (Throwable t) {
         fail();
      }
      assertTrue(successful.booleanValue());
      assertTrue(sourceSinkNode.getOutgoingFlows().size() == 1);
      sourceSinkNode.getOutgoingFlows().contains(secondRateNode);
      
      // (7) check that 'getOutgoingFlows()' returns only a clone of the set
      HashSet<RateNode> outgoingFlows = sourceSinkNode.getOutgoingFlows();
      outgoingFlows.add(thirdRateNode);
      assertTrue(outgoingFlows.size() == 2);
      assertTrue(sourceSinkNode.getOutgoingFlows().size() == 1);
      
      // (8) parameter not 'null' for method 'addOutgoingFlow(RateNode)'
      try {
         PrivateAccessor.invoke(sourceSinkNode, "addOutgoingFlow", new Class[] { RateNode.class },
                                new Object[] { null });
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'outgoingFlow' must not be null.", e.getMessage());
      } catch (Throwable t) {
         fail();
      }
      
      // (9) parameter not 'null' for method 'removeOutgoingFlow(RateNode)'
      try {
         PrivateAccessor.invoke(sourceSinkNode, "removeOutgoingFlow", new Class[] { RateNode.class },
                                new Object[] { null });
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'outgoingFlow' must not be null.", e.getMessage());
      } catch (Throwable t) {
         fail();
      }
   }
   
   /**
    * Tests the methods
    * {@link de.uka.aifb.com.systemDynamics.model.SourceSinkNode#getNodeName()} and
    * {@link de.uka.aifb.com.systemDynamics.model.SourceSinkNode#setNodeName(String)}.
    */
   public void testGetAndSetNodeName() {
      
      // (1) set node name -> WRONG!
      try {
         PrivateAccessor.invoke(sourceSinkNode, "setNodeName", new Class[] { String.class },
                                new Object[] { null });
         fail();
      } catch (UnsupportedOperationException e) {
         // do nothing
      } catch (Throwable t) {
         fail();
      }
      
      // (2) get node name -> WRONG!
      try {
         PrivateAccessor.invoke(sourceSinkNode, "getNodeName", null, null);
      } catch (UnsupportedOperationException e) {
         // do nothing
      } catch (Throwable t) {
         fail();
      }
   }
     
   /**
    * Helper method for invoking the private constructor of class SourceSinkNode.
    * 
    * @return created new instance of class SourceSinkNode
    */
   private SourceSinkNode createSourceSinkNode() {
      SourceSinkNode node = null;
      try {
         node =
            (SourceSinkNode)PrivateAccessor.invoke(SourceSinkNode.class,
                                                   "createSourceSinkNode", null, null);
      } catch (Throwable t) {
         // should not happen
         fail();
      }
      return node;
   }
   
   /**
    * Helper method for invoking the private constructor of class RateNodeMockObject.
    * 
    * @param nodeName node name
    * @param currentValue "current" value that will always be returned by the created instance's
    *                     'getCurrentValue()' method
    * @return created new instance of class RateNodeMockObject
    */
   private RateNodeMockObject createRateNodeMockObject(String nodeName, double currentValue) {
      RateNodeMockObject node = null;
      try {
         node =
            (RateNodeMockObject)PrivateAccessor.invoke(RateNodeMockObject.class,
                                                       "createRateNodeMockObject",
                                                       new Class[] { String.class, double.class },
                                                       new Object[] { nodeName, currentValue });
      } catch (IllegalArgumentException e) { 
         throw e;
      } catch (Throwable t) {
         // no other error/exception other than IllegalArgumentException possible
         fail();
      }
      return node;
   }
}