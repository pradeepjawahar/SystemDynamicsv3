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
import java.awt.*;
import java.awt.event.*;
import java.util.ResourceBundle;
import javax.swing.*;

/**
 * This class implements a dialog for entering a new node name.
 * 
 * @author Joachim Melcher, Institut AIFB, Universitaet Karlsruhe (TH), Germany
 * @version 1.0
 */
public class NodeNameDialog extends JDialog implements ActionListener, FocusListener {
   
   private static final long serialVersionUID = 1L;
   
   private static Color COLOR_NEUTRAL_FIELDS = Color.WHITE;
   private static Color COLOR_WRONG_FIELDS = new Color(255, 160, 122);
   
   private ResourceBundle messages;
   
   private JTextField nodeNameField;
   
   private JButton okButton;
   private JButton cancelButton;
   
   private String nodeNameMessage;
   private String initialNodeName;
   
   private String verifiedNodeName;
   
   /**
    * Constructor.
    * 
    * @param start @link{de.uka.aifb.com.systemDynamics.SystemDynamics} instance
    * @param owner frame "owning" this dialog window
    * @param title dialog window title
    * @param nodeNameMessage message to explain node name field
    * @param initialNodeName initial node name
    */
   private NodeNameDialog(SystemDynamics start, JFrame owner, String title, String nodeNameMessage,
                          String initialNodeName) {
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
      if (nodeNameMessage == null) {
         throw new IllegalArgumentException("'nodeNameMessage' must not be null.");
      }
      if (initialNodeName == null) {
         throw new IllegalArgumentException("'initialNodeName' must not be null.");
      }
      
      messages = start.getMessages();
      
      this.nodeNameMessage = nodeNameMessage;
      this.initialNodeName = initialNodeName;
      
      setTitle(title);
            
      getContentPane().add(createPanel());
      
      // set default button: OK button
      getRootPane().setDefaultButton(okButton);
      
      // set necessary size (not resizable)
      pack();
      setResizable(false);
      
      // set location
      Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
      int xStart = (int)((dimension.getWidth() - getWidth()) / 2);
      int yStart = (int)((dimension.getHeight() - getHeight()) / 2);
      setLocation(xStart, yStart);
            
      setVisible(true);
      setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
   }
   
   /**
    * Shows a dialog for changing a node's name.
    * 
    * @param start @link{de.uka.aifb.com.systemDynamics.SystemDynamics} instance
    * @param owner frame "owning" this dialog window
    * @param title dialog window title
    * @param nodeNameMessage message to explain node name field
    * @param initialNodeName initial node name
    * @return new node name or <code>null</code> if no (valid) new node name was entered
    */
   public static String showNodeNameDialog(SystemDynamics start, JFrame owner, String title,
                                           String nodeNameMessage, String initialNodeName) {
      if (start == null) {
         throw new IllegalArgumentException("'start' must not be null.");
      }
      if (owner == null) {
         throw new IllegalArgumentException("'owner' must not be null.");
      }
      if (title == null) {
         throw new IllegalArgumentException("'title' must not be null.");
      }
      if (nodeNameMessage == null) {
         throw new IllegalArgumentException("'nodeNameMessage' must not be null.");
      }
      if (initialNodeName == null) {
         throw new IllegalArgumentException("'initialNodeName' must not be null.");
      }
      
      NodeNameDialog nodeNameDialog =
         new NodeNameDialog(start, owner, title, nodeNameMessage, initialNodeName);
      
      // null if new node name was not successfully verified
      return nodeNameDialog.verifiedNodeName;
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
      inputPanel.add(new JLabel(nodeNameMessage + ":"));
      nodeNameField = new JTextField(initialNodeName, 20);
      nodeNameField.addFocusListener(this);
      inputPanel.add(nodeNameField);
      
      /*
       * button panel
       */
      JPanel buttonPanel = new JPanel();
      okButton = new JButton(messages.getString("NodeNameDialog.OKButton.Text"));
      okButton.addActionListener(this);
      buttonPanel.add(okButton);
      cancelButton = new JButton(messages.getString("NodeNameDialog.CancelButton.Text"));
      cancelButton.addActionListener(this);
      buttonPanel.add(cancelButton);
      
      // set together
      panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
      panel.add(inputPanel);
      panel.add(buttonPanel);
      
      return panel;
   }
   
   /**
    * Verifies the new node name.
    * 
    * @return <code>true</code> iff the new node name could be verified
    *         (<code>String</code> with at least length 1)
    */
   private boolean verifyNewNodeName() {
      // (1) set neutral background color
      nodeNameField.setBackground(COLOR_NEUTRAL_FIELDS);
      
      // (2) check node name in field
      String nodeName = nodeNameField.getText();
      if (nodeName == null || nodeName.length() == 0) {
         nodeNameField.setBackground(COLOR_WRONG_FIELDS);
         JOptionPane.showMessageDialog(null,
               messages.getString("NodeNameDialog.ErrorMessage"),
               messages.getString("NodeNameDialog.ErrorTitle"),
               JOptionPane.ERROR_MESSAGE);
         
         // exit method
         return false;
      }
      
      // node name correct
      verifiedNodeName = nodeName;
      
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
      if (e.getSource() == okButton) {
         boolean nodeNameVerified = verifyNewNodeName();
         
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
}