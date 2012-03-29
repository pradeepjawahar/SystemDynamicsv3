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
import de.uka.aifb.com.systemDynamics.parser.*;
import java.util.HashMap;
import junit.framework.*;
import junitx.util.PrivateAccessor;

/**
 * This class implements a test case for the class
 * {@link de.uka.aifb.com.systemDynamics.parser.FormulaParser}.
 * 
 * @author Joachim Melcher, University of Karlsruhe, AIFB
 * @version 1.0
 */
public class FormulaParserTestCase extends TestCase {
   
   private HashMap<Integer, AuxiliaryNode> id2auxiliaryNode;
   private HashMap<Integer, ConstantNode> id2constantNode;
   private HashMap<Integer, LevelNode> id2levelNode;
   Model model = new Model();
   private AuxiliaryNode auxiliaryNode1 = model.createAuxiliaryNode("Auxiliary node 1");
   private AuxiliaryNode auxiliaryNode2 = model.createAuxiliaryNode("Auxiliary node 2");
   private ConstantNode constantNode1 = model.createConstantNode("Constant node 1", 0);
   private ConstantNode constantNode2 = model.createConstantNode("Constant node 2", 0);
   private LevelNode levelNode1 = model.createLevelNode("Level node 1", 0);
   private LevelNode levelNode2 = model.createLevelNode("Level node 2", 0);
   
   public static Test suite() {  
      return new TestSuite(FormulaParserTestCase.class);
   }
   
   protected void setUp() throws Exception {
      id2auxiliaryNode = new HashMap<Integer, AuxiliaryNode>();
      id2auxiliaryNode.put(1, auxiliaryNode1);
      id2auxiliaryNode.put(2, auxiliaryNode2);
      
      id2constantNode = new HashMap<Integer, ConstantNode>();
      id2constantNode.put(1, constantNode1);
      id2constantNode.put(2, constantNode2);
      
      id2levelNode = new HashMap<Integer, LevelNode>();
      id2levelNode.put(1, levelNode1);
      id2levelNode.put(2, levelNode2);
   }

   protected void tearDown() throws Exception {
      id2auxiliaryNode = null;
      id2constantNode = null;
      id2levelNode = null;
   }
   
   /**
    * Tests the method
    * {@link de.uka.aifb.com.systemDynamics.parser.FormulaParser#parseFormula(String, HashMap, HashMap, HashMap)}.
    */
   public void testParseFormula1() {
      // (1) 'id2auxiliaryNode' == null => WRONG
      try {
         FormulaParser.parseFormula("", null, id2constantNode, id2levelNode);
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'id2auxiliaryNode' must not be null.", e.getMessage());
      } catch (Exception e) {
         fail();
      }
      
      // (2) 'id2constantNode' == null => WRONG
      try {
         FormulaParser.parseFormula("", id2auxiliaryNode, null, id2levelNode);
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'id2constantNode' must not be null.", e.getMessage());
      } catch (Exception e) {
         fail();
      }
      
      // (3) 'id2levelNode' == null => WRONG
      try {
         FormulaParser.parseFormula("", id2auxiliaryNode, id2constantNode, null);
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("'id2levelNode' must not be null.", e.getMessage());
      } catch (Exception e) {
         fail();
      }
   }
   
   /**
    * Tests the method
    * {@link de.uka.aifb.com.systemDynamics.parser.FormulaParser#parseFormula(String, HashMap, HashMap, HashMap)}.
    */
   public void testParseFormula2() {
      // (1) "AN(0)" => WRONG
      try {
         FormulaParser.parseFormula("AN(0)", id2auxiliaryNode, id2constantNode, id2levelNode);
         fail();
      } catch (ParseException e) {
         assertEquals("Auxiliary node with Id 0 does not exist.", e.getMessage());
      }
      
      // (2) "AN(1)" => CORRECT
      try {
         ASTElement formula =
            FormulaParser.parseFormula("AN(1)", id2auxiliaryNode, id2constantNode, id2levelNode);
         assertTrue(formula == auxiliaryNode1);
      } catch (Exception e) {
         fail();
      }
      
      // (3) "AN(2)" => CORRECT
      try {
         ASTElement formula =
            FormulaParser.parseFormula("AN(2)", id2auxiliaryNode, id2constantNode, id2levelNode);
         assertTrue(formula == auxiliaryNode2);
      } catch (Exception e) {
         fail();
      }
      
      // (4) "CN(0)" => WRONG
      try {
         FormulaParser.parseFormula("CN(0)", id2auxiliaryNode, id2constantNode, id2levelNode);
         fail();
      } catch (ParseException e) {
         assertEquals("Constant node with Id 0 does not exist.", e.getMessage());
      }
      
      // (5) "CN(1)" => CORRECT
      try {
         ASTElement formula =
            FormulaParser.parseFormula("CN(1)", id2auxiliaryNode, id2constantNode, id2levelNode);
         assertTrue(formula == constantNode1);
      } catch (Exception e) {
         fail();
      }
      
      // (6) "CN(2)" => CORRECT
      try {
         ASTElement formula =
            FormulaParser.parseFormula("CN(2)", id2auxiliaryNode, id2constantNode, id2levelNode);
         assertTrue(formula == constantNode2);
      } catch (Exception e) {
         fail();
      }
      
      // (7) "LN(0)" => WRONG
      try {
         FormulaParser.parseFormula("LN(0)", id2auxiliaryNode, id2constantNode, id2levelNode);
         fail();
      } catch (ParseException e) {
         assertEquals("Level node with Id 0 does not exist.", e.getMessage());
      }
      
      // (8) "LN(1)" => CORRECT
      try {
         ASTElement formula =
            FormulaParser.parseFormula("LN(1)", id2auxiliaryNode, id2constantNode, id2levelNode);
         assertTrue(formula == levelNode1);
      } catch (Exception e) {
         fail();
      }
      
      // (9) "LN(2)" => CORRECT
      try {
         ASTElement formula =
            FormulaParser.parseFormula("LN(2)", id2auxiliaryNode, id2constantNode, id2levelNode);
         assertTrue(formula == levelNode2);
      } catch (Exception e) {
         fail();
      }
      
      // (10) "LN(2" => WRONG
      try {
         FormulaParser.parseFormula("LN(2", id2auxiliaryNode, id2constantNode, id2levelNode);
         fail();
      } catch (ParseException e) {
         // do nothing
      } catch (Exception e) {
         fail();
      }
      
      // (11) "LN(a)" => WRONG
      try {
         FormulaParser.parseFormula("LN(a)", id2auxiliaryNode, id2constantNode, id2levelNode);
         fail();
      } catch (TokenMgrError e) {
         // do nothing
      } catch (Exception e) {
         fail();
      }
   }
   
   /**
    * Tests the method
    * {@link de.uka.aifb.com.systemDynamics.parser.FormulaParser#parseFormula(String, HashMap, HashMap, HashMap)}.
    */
   public void testParseFormula3() {
      // (1) CN(1) + CN(2) => CORRECT
      try {
         ASTElement formula =
            FormulaParser.parseFormula("CN(1) + CN(2)", id2auxiliaryNode, id2constantNode, id2levelNode);
         
         assertTrue(formula instanceof ASTPlus);
         try {
            assertTrue(PrivateAccessor.getField(formula, "leftElement") == constantNode1);
            assertTrue(PrivateAccessor.getField(formula, "rightElement") == constantNode2);
         } catch (NoSuchFieldException e) {
            fail();
         }
      } catch (Exception e) {
         fail();
      }
      
      // (2) CN(1)+CN(2) => CORRECT
      try {
         ASTElement formula =
            FormulaParser.parseFormula("CN(1) + CN(2)", id2auxiliaryNode, id2constantNode, id2levelNode);
         
         assertTrue(formula instanceof ASTPlus);
         try {
            assertTrue(PrivateAccessor.getField(formula, "leftElement") == constantNode1);
            assertTrue(PrivateAccessor.getField(formula, "rightElement") == constantNode2);
         } catch (NoSuchFieldException e) {
            fail();
         }
      } catch (Exception e) {
         fail();
      }
      
      // (3) AN(1) - CN(2) => CORRECT
      try {
         ASTElement formula =
            FormulaParser.parseFormula("AN(1) - CN(2)", id2auxiliaryNode, id2constantNode, id2levelNode);
         
         assertTrue(formula instanceof ASTMinus);
         try {
            assertTrue(PrivateAccessor.getField(formula, "leftElement") == auxiliaryNode1);
            assertTrue(PrivateAccessor.getField(formula, "rightElement") == constantNode2);
         } catch (NoSuchFieldException e) {
            fail();
         }
      } catch (Exception e) {
         fail();
      }
      
      // (4) AN(1)-CN(2) => CORRECT
      try {
         ASTElement formula =
            FormulaParser.parseFormula("AN(1)-CN(2)", id2auxiliaryNode, id2constantNode, id2levelNode);
         
         assertTrue(formula instanceof ASTMinus);
         try {
            assertTrue(PrivateAccessor.getField(formula, "leftElement") == auxiliaryNode1);
            assertTrue(PrivateAccessor.getField(formula, "rightElement") == constantNode2);
         } catch (NoSuchFieldException e) {
            fail();
         }
      } catch (Exception e) {
         fail();
      }
      
      // (5) AN(1) * LN(2) => CORRECT
      try {
         ASTElement formula =
            FormulaParser.parseFormula("AN(1) * LN(2)", id2auxiliaryNode, id2constantNode, id2levelNode);
         
         assertTrue(formula instanceof ASTMultiply);
         try {
            assertTrue(PrivateAccessor.getField(formula, "leftElement") == auxiliaryNode1);
            assertTrue(PrivateAccessor.getField(formula, "rightElement") == levelNode2);
         } catch (NoSuchFieldException e) {
            fail();
         }
      } catch (Exception e) {
         fail();
      }
      
      // (6) AN(1)*LN(2) => CORRECT
      try {
         ASTElement formula =
            FormulaParser.parseFormula("AN(1) * LN(2)", id2auxiliaryNode, id2constantNode, id2levelNode);
         
         assertTrue(formula instanceof ASTMultiply);
         try {
            assertTrue(PrivateAccessor.getField(formula, "leftElement") == auxiliaryNode1);
            assertTrue(PrivateAccessor.getField(formula, "rightElement") == levelNode2);
         } catch (NoSuchFieldException e) {
            fail();
         }
      } catch (Exception e) {
         fail();
      }
   }
   
   /**
    * Tests the method
    * {@link de.uka.aifb.com.systemDynamics.parser.FormulaParser#parseFormula(String, HashMap, HashMap, HashMap)}.
    */
   public void testParseFormula4() {
      // (1) CN(1) + CN(2) + AN(1) => CORRECT
      try {
         ASTElement formula =
            FormulaParser.parseFormula("CN(1) + CN(2) + AN(1)", id2auxiliaryNode, id2constantNode, id2levelNode);
         
         try {
            assertTrue(formula instanceof ASTPlus);
            assertTrue(PrivateAccessor.getField(formula, "leftElement") instanceof ASTPlus);
            assertTrue(PrivateAccessor.getField(formula, "rightElement") == auxiliaryNode1);
            ASTPlus subFormula = (ASTPlus)PrivateAccessor.getField(formula, "leftElement");
            assertTrue(PrivateAccessor.getField(subFormula, "leftElement") == constantNode1);
            assertTrue(PrivateAccessor.getField(subFormula, "rightElement") == constantNode2);
         } catch (NoSuchFieldException e) {
            fail();
         }
      } catch (Exception e) {
         fail();
      }
      
      // (2) CN(1) - CN(2) - AN(1) => CORRECT
      try {
         ASTElement formula =
            FormulaParser.parseFormula("CN(1) - CN(2) - AN(1)", id2auxiliaryNode, id2constantNode, id2levelNode);
         
         try {
            assertTrue(formula instanceof ASTMinus);
            assertTrue(PrivateAccessor.getField(formula, "leftElement") instanceof ASTMinus);
            assertTrue(PrivateAccessor.getField(formula, "rightElement") == auxiliaryNode1);
            ASTMinus subFormula = (ASTMinus)PrivateAccessor.getField(formula, "leftElement");
            assertTrue(PrivateAccessor.getField(subFormula, "leftElement") == constantNode1);
            assertTrue(PrivateAccessor.getField(subFormula, "rightElement") == constantNode2);
         } catch (NoSuchFieldException e) {
            fail();
         }
      } catch (Exception e) {
         fail();
      }
      
      // (3) CN(1) - CN(2) + AN(1) => CORRECT
      try {
         ASTElement formula =
            FormulaParser.parseFormula("CN(1) - CN(2) + AN(1)", id2auxiliaryNode, id2constantNode, id2levelNode);
         
         try {
            assertTrue(formula instanceof ASTPlus);
            assertTrue(PrivateAccessor.getField(formula, "leftElement") instanceof ASTMinus);
            assertTrue(PrivateAccessor.getField(formula, "rightElement") == auxiliaryNode1);
            ASTMinus subFormula = (ASTMinus)PrivateAccessor.getField(formula, "leftElement");
            assertTrue(PrivateAccessor.getField(subFormula, "leftElement") == constantNode1);
            assertTrue(PrivateAccessor.getField(subFormula, "rightElement") == constantNode2);
         } catch (NoSuchFieldException e) {
            fail();
         }
      } catch (Exception e) {
         fail();
      }
      
      // (4) CN(1) * CN(2) * AN(1) => CORRECT
      try {
         ASTElement formula =
            FormulaParser.parseFormula("CN(1) * CN(2) * AN(1)", id2auxiliaryNode, id2constantNode, id2levelNode);
         
         try {
            assertTrue(formula instanceof ASTMultiply);
            assertTrue(PrivateAccessor.getField(formula, "leftElement") instanceof ASTMultiply);
            assertTrue(PrivateAccessor.getField(formula, "rightElement") == auxiliaryNode1);
            ASTMultiply subFormula = (ASTMultiply)PrivateAccessor.getField(formula, "leftElement");
            assertTrue(PrivateAccessor.getField(subFormula, "leftElement") == constantNode1);
            assertTrue(PrivateAccessor.getField(subFormula, "rightElement") == constantNode2);
         } catch (NoSuchFieldException e) {
            fail();
         }
      } catch (Exception e) {
         fail();
      }
   }
   
   /**
    * Tests the method
    * {@link de.uka.aifb.com.systemDynamics.parser.FormulaParser#parseFormula(String, HashMap, HashMap, HashMap)}.
    */
   public void testParseFormula5() {
      // (1) CN(1) + CN(2) * AN(1) => CORRECT
      try {
         ASTElement formula =
            FormulaParser.parseFormula("CN(1) + CN(2) * AN(1)", id2auxiliaryNode, id2constantNode, id2levelNode);
         
         try {
            assertTrue(formula instanceof ASTPlus);
            assertTrue(PrivateAccessor.getField(formula, "leftElement") == constantNode1);
            assertTrue(PrivateAccessor.getField(formula, "rightElement") instanceof ASTMultiply);
            ASTMultiply subFormula = (ASTMultiply)PrivateAccessor.getField(formula, "rightElement");
            assertTrue(PrivateAccessor.getField(subFormula, "leftElement") == constantNode2);
            assertTrue(PrivateAccessor.getField(subFormula, "rightElement") == auxiliaryNode1);
         } catch (NoSuchFieldException e) {
            fail();
         }
      } catch (Exception e) {
         fail();
      }
      
      // (2) CN(1) - CN(2) * AN(1) => CORRECT
      try {
         ASTElement formula =
            FormulaParser.parseFormula("CN(1) - CN(2) * AN(1)", id2auxiliaryNode, id2constantNode, id2levelNode);
         
         try {
            assertTrue(formula instanceof ASTMinus);
            assertTrue(PrivateAccessor.getField(formula, "leftElement") == constantNode1);
            assertTrue(PrivateAccessor.getField(formula, "rightElement") instanceof ASTMultiply);
            ASTMultiply subFormula = (ASTMultiply)PrivateAccessor.getField(formula, "rightElement");
            assertTrue(PrivateAccessor.getField(subFormula, "leftElement") == constantNode2);
            assertTrue(PrivateAccessor.getField(subFormula, "rightElement") == auxiliaryNode1);
         } catch (NoSuchFieldException e) {
            fail();
         }
      } catch (Exception e) {
         fail();
      }
      
      // (3) CN(1) * CN(2) + AN(1) => CORRECT
      try {
         ASTElement formula =
            FormulaParser.parseFormula("CN(1) * CN(2) + AN(1)", id2auxiliaryNode, id2constantNode, id2levelNode);
         
         try {
            assertTrue(formula instanceof ASTPlus);
            assertTrue(PrivateAccessor.getField(formula, "leftElement") instanceof ASTMultiply);
            assertTrue(PrivateAccessor.getField(formula, "rightElement") == auxiliaryNode1);
            ASTMultiply subFormula = (ASTMultiply)PrivateAccessor.getField(formula, "leftElement");
            assertTrue(PrivateAccessor.getField(subFormula, "leftElement") == constantNode1);
            assertTrue(PrivateAccessor.getField(subFormula, "rightElement") == constantNode2);
         } catch (NoSuchFieldException e) {
            fail();
         }
      } catch (Exception e) {
         fail();
      }
      
      // (4) CN(1) * CN(2) - AN(1) => CORRECT
      try {
         ASTElement formula =
            FormulaParser.parseFormula("CN(1) * CN(2) - AN(1)", id2auxiliaryNode, id2constantNode, id2levelNode);
         
         try {
            assertTrue(formula instanceof ASTMinus);
            assertTrue(PrivateAccessor.getField(formula, "leftElement") instanceof ASTMultiply);
            assertTrue(PrivateAccessor.getField(formula, "rightElement") == auxiliaryNode1);
            ASTMultiply subFormula = (ASTMultiply)PrivateAccessor.getField(formula, "leftElement");
            assertTrue(PrivateAccessor.getField(subFormula, "leftElement") == constantNode1);
            assertTrue(PrivateAccessor.getField(subFormula, "rightElement") == constantNode2);
         } catch (NoSuchFieldException e) {
            fail();
         }
      } catch (Exception e) {
         fail();
      }
   }
   
   /**
    * Tests the method
    * {@link de.uka.aifb.com.systemDynamics.parser.FormulaParser#parseFormula(String, HashMap, HashMap, HashMap)}.
    */
   public void testParseFormula6() {
      // (1) (CN(1)) => CORRECT
      try {
         ASTElement formula =
            FormulaParser.parseFormula("(CN(1))", id2auxiliaryNode, id2constantNode, id2levelNode);
         
         assertTrue(formula == constantNode1);
      } catch (Exception e) {
         fail();
      }
      
      // (2) CN(1) + (CN(2) + AN(1)) => CORRECT
      try {
         ASTElement formula =
            FormulaParser.parseFormula("CN(1) + (CN(2) + AN(1))", id2auxiliaryNode, id2constantNode, id2levelNode);
         
         try {
            assertTrue(formula instanceof ASTPlus);
            assertTrue(PrivateAccessor.getField(formula, "leftElement") == constantNode1);
            assertTrue(PrivateAccessor.getField(formula, "rightElement") instanceof ASTPlus);
            ASTPlus subFormula = (ASTPlus)PrivateAccessor.getField(formula, "rightElement");
            assertTrue(PrivateAccessor.getField(subFormula, "leftElement") == constantNode2);
            assertTrue(PrivateAccessor.getField(subFormula, "rightElement") == auxiliaryNode1);
         } catch (NoSuchFieldException e) {
            fail();
         }
      } catch (Exception e) {
         fail();
      }
      
      // (3) (CN(1) - CN(2)) * AN(1) => CORRECT
      try {
         ASTElement formula =
            FormulaParser.parseFormula("(CN(1) - CN(2)) * AN(1)", id2auxiliaryNode, id2constantNode, id2levelNode);
         
         try {
            assertTrue(formula instanceof ASTMultiply);
            assertTrue(PrivateAccessor.getField(formula, "leftElement") instanceof ASTMinus);
            assertTrue(PrivateAccessor.getField(formula, "rightElement") == auxiliaryNode1);
            ASTMinus subFormula = (ASTMinus)PrivateAccessor.getField(formula, "leftElement");
            assertTrue(PrivateAccessor.getField(subFormula, "leftElement") == constantNode1);
            assertTrue(PrivateAccessor.getField(subFormula, "rightElement") == constantNode2);
         } catch (NoSuchFieldException e) {
            fail();
         }
      } catch (Exception e) {
         fail();
      }
   }
}