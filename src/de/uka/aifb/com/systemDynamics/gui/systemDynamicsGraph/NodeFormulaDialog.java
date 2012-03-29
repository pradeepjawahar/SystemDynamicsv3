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

package de.uka.aifb.com.systemDynamics.gui.systemDynamicsGraph;

import de.uka.aifb.com.systemDynamics.SystemDynamics;
import de.uka.aifb.com.systemDynamics.model.*;
import de.uka.aifb.com.systemDynamics.parser.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

/**
 * This class implements a dialog for entering a new node formula.
 * 
 * @author Joachim Melcher, Institut AIFB, Universitaet Karlsruhe (TH), Germany
 * @version 1.0
 */
public class NodeFormulaDialog extends JDialog implements ActionListener, FocusListener {
   
   private static final long serialVersionUID = 1L;
   
   private static Color COLOR_NEUTRAL_FIELDS = Color.WHITE;
   private static Color COLOR_WRONG_FIELDS = new Color(255, 160, 122);
   
   private ResourceBundle messages;
   
   private JTextArea formulaOutputArea;
   private JTextField formulaInputField;
   
   private JButton okButton;
   private JButton cancelButton;
   private JButton testButton;
   
   private HashMap<Integer, AuxiliaryNode> id2auxiliaryNode;
   private HashMap<Integer, ConstantNode> id2constantNode;
   private HashMap<Integer, LevelNode> id2levelNode;
   
   private ASTElement initialFormula;
   
   private Formula newFormula;
   
   /**
    * Constructor.
    * 
    * @param start @link{de.uka.aifb.com.systemDynamics.SystemDynamics} instance
    * @param owner frame "owning" this dialog window
    * @param title dialog window title
    * @param initialFormula current formula or <code>null</code> if node has no formula up to now
    * @param id2auxiliaryNode id to auxiliary node mapping
    * @param id2constantNode id to constant node mapping
    * @param id2levelNode id to level node mapping
    */
   private NodeFormulaDialog(SystemDynamics start, JFrame owner, String title,
                             ASTElement initialFormula,
                             HashMap<Integer, AuxiliaryNode> id2auxiliaryNode,
                             HashMap<Integer, ConstantNode> id2constantNode,
                             HashMap<Integer, LevelNode> id2levelNode) {
      // call constructor of super class
      super(owner, true);
      
      if (start == null) {
         throw new IllegalArgumentException("'start' must not be null.");
      }
      if (owner == null) {
         throw new IllegalArgumentException("'owner' must not be null.");
      }
      if (title == null) {
         throw new IllegalArgumentException("'title' must not be null.");
      }
      if (id2auxiliaryNode == null) {
         throw new IllegalArgumentException("'id2auxiliaryNode' must not be null.");
      }
      if (id2constantNode == null) {
         throw new IllegalArgumentException("'id2constantNode' must not be null.");
      }
      if (id2levelNode == null) {
         throw new IllegalArgumentException("'id2levelNode' must not be null.");
      }
      
      messages = start.getMessages();
      
      this.id2auxiliaryNode = id2auxiliaryNode;
      this.id2constantNode = id2constantNode;
      this.id2levelNode = id2levelNode;
      this.initialFormula = initialFormula;
      
      newFormula = new Formula(null, false);
      
      setTitle(title);
            
      getContentPane().add(createPanel());
      
      // set default button: OK button
      getRootPane().setDefaultButton(okButton);
      
      // set necessary size (not resizable)
      pack();
      setResizable(false);
      
      // focus on formula input field
      formulaInputField.requestFocusInWindow();
      
      // set location
      Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
      int xStart = (int)((dimension.getWidth() - getWidth()) / 2);
      int yStart = (int)((dimension.getHeight() - getHeight()) / 2);
      setLocation(xStart, yStart);
            
      setVisible(true);
      
      setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
   }
   
   /**
    * Shows a dialog for changing a node's formula.
    * 
    * @param start @link{de.uka.aifb.com.systemDynamics.SystemDynamics} instance
    * @param owner frame "owning" this dialog window
    * @param title dialog window title
    * @param initialFormula current formula or <code>null</code> if node has no formula up to now
    * @param id2auxiliaryNode id to auxiliary node mapping
    * @param id2constantNode id to constant node mapping
    * @param id2levelNode id to level node mapping
    * @return new formula
    */
   public static Formula showNodeFormulaDialog(SystemDynamics start, JFrame owner, String title,
                                                  ASTElement initialFormula,
                                                  HashMap<Integer, AuxiliaryNode> id2auxiliaryNode,
                                                  HashMap<Integer, ConstantNode> id2constantNode,
                                                  HashMap<Integer, LevelNode> id2levelNode) {
      if (start == null) {
         throw new IllegalArgumentException("'start' must not be null.");
      }
      if (owner == null) {
         throw new IllegalArgumentException("'owner' must not be null.");
      }
      if (title == null) {
         throw new IllegalArgumentException("'title' must not be null.");
      }
      if (id2auxiliaryNode == null) {
         throw new IllegalArgumentException("'id2auxiliaryNode' must not be null.");
      }
      if (id2constantNode == null) {
         throw new IllegalArgumentException("'id2constantNode' must not be null.");
      }
      if (id2levelNode == null) {
         throw new IllegalArgumentException("'id2levelNode' must not be null.");
      }
      
      NodeFormulaDialog nodeFormulaDialog =
         new NodeFormulaDialog(start, owner, title, initialFormula, id2auxiliaryNode,
                               id2constantNode, id2levelNode);
      
      // null if new node formula was not successfully verified
      return nodeFormulaDialog.newFormula;
   }
   
   /**
    * Creates the panel with the node name field.
    * 
    * @return panel with node name field
    */
   private JPanel createPanel() {
      JPanel panel = new JPanel();
            
      /*
       * input panel
       */
      JPanel inputPanel = new JPanel();
      inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      JPanel innerInputPanel = new JPanel();
      innerInputPanel.setLayout(new BoxLayout(innerInputPanel, BoxLayout.PAGE_AXIS));
      inputPanel.add(innerInputPanel);
      innerInputPanel.add(new JLabel(messages.getString("NodeFormulaDialog.FormulaOutputLong")));
      formulaOutputArea = new JTextArea(2, 50);
      if (initialFormula != null) {
         formulaOutputArea.setText(initialFormula.getStringRepresentation());
      }
      formulaOutputArea.setEditable(false);
      JScrollPane formulaOutputScrollPane = new JScrollPane(formulaOutputArea);
      formulaOutputScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
      innerInputPanel.add(formulaOutputScrollPane);
      innerInputPanel.add(Box.createRigidArea(new Dimension(0, 30)));
      
      HashMap<AuxiliaryNode, Integer> auxiliaryNode2id = new HashMap<AuxiliaryNode, Integer>();
      HashMap<ConstantNode, Integer> constantNode2id = new HashMap<ConstantNode, Integer>();
      HashMap<LevelNode, Integer> levelNode2id = new HashMap<LevelNode, Integer>();
      createNode2IdMappings(id2auxiliaryNode, id2constantNode, id2levelNode, auxiliaryNode2id, constantNode2id, levelNode2id);
      
      innerInputPanel.add(new JLabel(messages.getString("NodeFormulaDialog.FormulaInputShort")));
      formulaInputField = new JTextField(50);
      formulaInputField.requestFocusInWindow();
      if (initialFormula != null) {
         formulaInputField.setText(initialFormula.getShortStringRepresentation(auxiliaryNode2id, constantNode2id, levelNode2id));
      }
      formulaInputField.addFocusListener(this);
      innerInputPanel.add(formulaInputField);
      
      /*
       * mapping panel
       */
      JPanel mappingPanel = new JPanel();
      mappingPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      
      String[] columnNames = { messages.getString("NodeFormulaDialog.MappingTable.Abbreviation"),
                               messages.getString("NodeFormulaDialog.MappingTable.NodeName") };
      String[][] data = new String[id2auxiliaryNode.size() + id2constantNode.size() + id2levelNode.size()][2];
      int row = 0;
      for (int i = 1; i <= id2auxiliaryNode.size(); i++) {
         data[row][0] = "AN(" + i + ")";
         data[row][1] = id2auxiliaryNode.get(i).getStringRepresentation();
         row++;
      }
      for (int i = 1; i <= id2constantNode.size(); i++) {
         data[row][0] = "CN(" + i + ")";
         data[row][1] = id2constantNode.get(i).getStringRepresentation();
         row++;
      }
      for (int i = 1; i <= id2levelNode.size(); i++) {
         data[row][0] = "LN(" + i + ")";
         data[row][1] = id2levelNode.get(i).getStringRepresentation();
         row++;
      }
      MyTableModel tableModel = new MyTableModel(data, columnNames);
      JTable mappingTable = new JTable(tableModel);
      mappingTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
      initColumnSizes(mappingTable);
      mappingTable.setRowSelectionAllowed(false);
      JScrollPane scrollPane = new JScrollPane(mappingTable);
      int width = (int)Math.min(300, mappingTable.getPreferredSize().getWidth());
      int height = (int)Math.min(200, mappingTable.getPreferredSize().getHeight());
      scrollPane.getViewport().setPreferredSize(new Dimension(width, height));
      mappingPanel.add(scrollPane);
      
      /*
       * button panel
       */
      JPanel buttonPanel = new JPanel();
      testButton = new JButton(messages.getString("NodeFormulaDialog.TestButton.Text"));
      testButton.setToolTipText(messages.getString("NodeFormulaDialog.TestButton.ToolTipText"));
      testButton.addActionListener(this);
      buttonPanel.add(testButton);
      okButton = new JButton(messages.getString("NodeFormulaDialog.OKButton.Text"));
      okButton.addActionListener(this);
      buttonPanel.add(okButton);
      cancelButton = new JButton(messages.getString("NodeFormulaDialog.CancelButton.Text"));
      cancelButton.addActionListener(this);
      buttonPanel.add(cancelButton);
      
      // set together
      panel.setLayout(new BorderLayout());
      panel.add(inputPanel, BorderLayout.CENTER);
      panel.add(mappingPanel, BorderLayout.LINE_END);
      panel.add(buttonPanel, BorderLayout.PAGE_END);
      
      return panel;
   }
   
   /**
    * Picks good column sizes.
    * 
    * @param table table whose column sizes should be initialized
    */
   private static void initColumnSizes(JTable table) {
       TableModel model = table.getModel();
       TableColumn column = null;
       Component comp = null;
       int headerWidth = 0;
       int cellWidth = 0;
       TableCellRenderer headerRenderer =
           table.getTableHeader().getDefaultRenderer();

       // iterate over columns
       for (int i = 0; i < model.getColumnCount(); i++) {
           column = table.getColumnModel().getColumn(i);

           comp = headerRenderer.getTableCellRendererComponent(
                                null, column.getHeaderValue(),
                                false, false, 0, 0);
           headerWidth = comp.getPreferredSize().width + 10;

           cellWidth = 0;
           // iterate over rows
           for (int j = 0; j < model.getRowCount(); j++) {
              comp = table.getDefaultRenderer(model.getColumnClass(i)).
                                              getTableCellRendererComponent(table,
                                                                            model.getValueAt(j, i),
                                                                            false, false, 0, i);
              cellWidth = Math.max(cellWidth, comp.getPreferredSize().width + 10);
           }

           column.setPreferredWidth(Math.max(headerWidth, cellWidth));
       }
   }
   
   /**
    * Creates node to id mappings.
    * 
    * @param id2auxiliaryNode id to auxiliary node mapping
    * @param id2constantNode id to constant node mapping
    * @param id2levelNode id to level node mapping
    * @param auxiliaryNode2id auxiliary node to id mapping
    * @param constantNode2id constant node to id mapping
    * @param levelNode2id level node to id mapping
    */
   private void createNode2IdMappings(HashMap<Integer, AuxiliaryNode> id2auxiliaryNode,
                                      HashMap<Integer, ConstantNode> id2constantNode,
                                      HashMap<Integer, LevelNode> id2levelNode,
                                      HashMap<AuxiliaryNode, Integer> auxiliaryNode2id,
                                      HashMap<ConstantNode, Integer> constantNode2id,
                                      HashMap<LevelNode, Integer> levelNode2id) {
      if (id2auxiliaryNode == null) {
         throw new IllegalArgumentException("'id2auxiliaryNode' must not be null.");
      }
      if (id2constantNode == null) {
         throw new IllegalArgumentException("'id2constantNode' must not be null.");
      }
      if (id2levelNode == null) {
         throw new IllegalArgumentException("'id2levelNode' must not be null.");
      }
      
      if (auxiliaryNode2id == null) {
         throw new IllegalArgumentException("'auxiliaryNode2id' must not be null.");
      }
      if (!auxiliaryNode2id.isEmpty()) {
         throw new IllegalArgumentException("'auxiliaryNode2id' must be empty.");
      }
      if (constantNode2id == null) {
         throw new IllegalArgumentException("'constantNode2id' must not be null.");
      }
      if (!constantNode2id.isEmpty()) {
         throw new IllegalArgumentException("'constantNode2id' must be empty.");
      }
      if (levelNode2id == null) {
         throw new IllegalArgumentException("'levelNode2id' must not be null.");
      }
      if (!levelNode2id.isEmpty()) {
         throw new IllegalArgumentException("'levelNode2id' must be empty.");
      }
      
      for (Integer id : id2auxiliaryNode.keySet()) {
         auxiliaryNode2id.put(id2auxiliaryNode.get(id), id);
      }
      
      for (Integer id : id2constantNode.keySet()) {
         constantNode2id.put(id2constantNode.get(id), id);
      }
      
      for (Integer id : id2levelNode.keySet()) {
         levelNode2id.put(id2levelNode.get(id), id);
      }
   }
   
   /**
    * Tests the new node formula.
    */
   private void testNewNodeFormula() {
      // (1) set neutral background color
      formulaInputField.setBackground(COLOR_NEUTRAL_FIELDS);
      
      // (2) check node formula in input field
      String formulaString = formulaInputField.getText();
      if (formulaString == null || formulaString.length() == 0) {
         formulaOutputArea.setText("");
         
         // exit method
         return;
      }
      
      // parse formula string
      ASTElement formula = null;
      try {
         formula = FormulaParser.parseFormula(formulaString, id2auxiliaryNode, id2constantNode, id2levelNode);
      } catch (ParseException e) {
         formulaInputField.setBackground(COLOR_WRONG_FIELDS);
         formulaOutputArea.setText("");
         JOptionPane.showMessageDialog(null,
                                       messages.getString("NodeFormulaDialog.FormulaParsingErrorMessage1") + "\r\n"
                                       + messages.getString("NodeFormulaDialog.FormulaParsingErrorMessage2") + ": " +  e.getMessage(),
                                       messages.getString("NodeFormulaDialog.FormulaParsingErrorTitle"),
                                       JOptionPane.ERROR_MESSAGE);
         
         // exit method
         return;
      } catch (TokenMgrError e) {
         formulaInputField.setBackground(COLOR_WRONG_FIELDS);
         formulaOutputArea.setText("");
         JOptionPane.showMessageDialog(null,
                                       messages.getString("NodeFormulaDialog.FormulaParsingErrorMessage1") + "\r\n"
                                       + messages.getString("NodeFormulaDialog.FormulaParsingErrorMessage2") + ": " +  e.getMessage(),
                                       messages.getString("NodeFormulaDialog.FormulaParsingErrorTitle"),
                                       JOptionPane.ERROR_MESSAGE);
         
         // exit method
         return;
      }
      
      // node formula correct
      formulaOutputArea.setText(formula.getStringRepresentation());
   }
   
   /**
    * Verifies the new node formula. If the formula is correct, the node formula is set to the new
    * formula.
    * 
    * @return <code>true</code> iff the new node formula could be verified
    */
   private boolean verifyNewNodeFormula() {
      // (1) set neutral background color
      formulaInputField.setBackground(COLOR_NEUTRAL_FIELDS);
      
      // (2) check node formula in input field
      String formulaString = formulaInputField.getText();
      if (formulaString == null || formulaString.length() == 0) {
         newFormula = new Formula(null, true);
         
         // exit method
         return true;
      }
      
      // parse formula string
      ASTElement formula = null;
      try {
         formula = FormulaParser.parseFormula(formulaString, id2auxiliaryNode, id2constantNode, id2levelNode);
      } catch (ParseException e) {
         formulaInputField.setBackground(COLOR_WRONG_FIELDS);
         formulaOutputArea.setText("");
         JOptionPane.showMessageDialog(null,
                                       messages.getString("NodeFormulaDialog.FormulaParsingErrorMessage1") + "\r\n"
                                       + messages.getString("NodeFormulaDialog.FormulaParsingErrorMessage2") + ": " +  e.getMessage(),
                                       messages.getString("NodeFormulaDialog.FormulaParsingErrorTitle"),
                                       JOptionPane.ERROR_MESSAGE);
         
         // exit method
         return false;
      } catch (TokenMgrError e) {
         formulaInputField.setBackground(COLOR_WRONG_FIELDS);
         formulaOutputArea.setText("");
         JOptionPane.showMessageDialog(null,
                                       messages.getString("NodeFormulaDialog.FormulaParsingErrorMessage1") + "\r\n"
                                       + messages.getString("NodeFormulaDialog.FormulaParsingErrorMessage2") + ": " +  e.getMessage(),
                                       messages.getString("NodeFormulaDialog.FormulaParsingErrorTitle"),
                                       JOptionPane.ERROR_MESSAGE);
         
         // exit method
         return false;
      }
      
      // node formula correct
      newFormula = new Formula(formula, true);
      
      return true;
   }
   
////////////////////////////////////////////////////////////////////////////////////////////////////
//                              methods of interface ActionListener
////////////////////////////////////////////////////////////////////////////////////////////////////

   /**
    * Performs the specified action event that is caused by the OK or cancel button.
    * 
    * @param e event
    */
   public void actionPerformed(ActionEvent e) {
      if (e.getSource() == testButton) {
         testNewNodeFormula();
      }
      
      if (e.getSource() == okButton) {
         boolean nodeNameVerified = verifyNewNodeFormula();
         
         // close this dialog window if node name was verified successfully
         if (nodeNameVerified) {
            dispose();
         }
      }
      
      if (e.getSource() == cancelButton) {
         // close this dialog windows
         dispose();
      }
   }
   
////////////////////////////////////////////////////////////////////////////////////////////////////
//                               methods of interface FocusListener
////////////////////////////////////////////////////////////////////////////////////////////////////
   
   /**
    * Performs a gained focus event.
    * 
    * @param e event
    */
   public void focusGained(FocusEvent e) {
      Component c = e.getComponent();
      if (c instanceof JTextField) {
          ((JTextField)c).selectAll();
      }
   }
   
   /**
    * Performs a lost focus event.
    * 
    * @param e event
    */
   public void focusLost(FocusEvent e) {
      // do nothing
   }
   
////////////////////////////////////////////////////////////////////////////////////////////////////
//                                         inner class
////////////////////////////////////////////////////////////////////////////////////////////////////

   /**
    * Inner class for storing entered formula.
    */
   public static class Formula {

      private ASTElement formula;
      private boolean newFormulaEntered;

      /**
       * Constructor.
       * 
       * @param formula new formula
       * @param newFormulaEntered was new formula entered (<code>false</code> iff 'cancel' was
       *                          pressed to close dialog)
       */
      public Formula(ASTElement formula, boolean newFormulaEntered) {
         this.formula = formula;
         this.newFormulaEntered = newFormulaEntered;
      }
      
      /**
       * Gets new formula (can be <code>null</code>)
       * 
       * @return new formula
       */
      public ASTElement getFormula() {
         return formula;
      }
      
      /**
       * Checks whether new formula was entered or dialog was closed by pressing 'cancel'). 
       * 
       * @return <code>true</code> if new formula was entered - <code>false</code> if dialog was
       *                           closed by pressing 'cancel'
       */
      public boolean wasNewFormulaEntered() {
         return newFormulaEntered;
      }
   }
}