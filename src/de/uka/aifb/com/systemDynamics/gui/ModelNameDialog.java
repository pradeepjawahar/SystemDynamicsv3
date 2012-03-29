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
import java.awt.*;
import java.awt.event.*;
import java.util.ResourceBundle;
import javax.swing.*;

/**
 * This class implements a dialog for entering a new model name.
 * 
 * @author Joachim Melcher, Institut AIFB, Universitaet Karlsruhe (TH), Germany
 * @version 1.0
 */
public class ModelNameDialog extends JDialog implements ActionListener, FocusListener {
   
   private static final long serialVersionUID = 1L;
   
   private static Color COLOR_NEUTRAL_FIELDS = Color.WHITE;
   private static Color COLOR_WRONG_FIELDS = new Color(255, 160, 122);
   
   private ResourceBundle messages;
   
   private JTextField modelNameField;
   
   private JButton okButton;
   private JButton cancelButton;
   
   private String modelNameMessage;
   private String initialModelName;
   
   private String verifiedModelName;
   
   /**
    * Constructor.
    * 
    * @param start @link{de.uka.aifb.com.systemDynamics.SystemDynamics} instance
    * @param owner frame "owning" this dialog window
    * @param title dialog window title
    * @param modelNameMessage message to explain model name field
    * @param initialModelName initial model name
    */
   private ModelNameDialog(SystemDynamics start, JFrame owner, String title, String modelNameMessage,
                           String initialModelName) {
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
      if (modelNameMessage == null) {
         throw new IllegalArgumentException("'modelNameMessage' must not be null.");
      }
      if (initialModelName == null) {
         throw new IllegalArgumentException("'initialModelName' must not be null.");
      }
      
      messages = start.getMessages();
      
      this.modelNameMessage = modelNameMessage;
      this.initialModelName = initialModelName;
      
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
    * Shows a dialog for changing a model's name.
    * 
    * @param start @link{de.uka.aifb.com.systemDynamics.SystemDynamics} instance
    * @param owner frame "owning" this dialog window
    * @param title dialog window title
    * @param modelNameMessage message to explain model name field
    * @param initialModelName initial model name
    * @return new node name or <code>null</code> if no (valid) new model name was entered
    */
   public static String showModelNameDialog(SystemDynamics start, JFrame owner, String title,
                                            String modelNameMessage, String initialModelName) {
      if (start == null) {
         throw new IllegalArgumentException("'start' must not be null.");
      }
      if (owner == null) {
         throw new IllegalArgumentException("'owner' must not be null.");
      }
      if (title == null) {
         throw new IllegalArgumentException("'title' must not be null.");
      }
      if (modelNameMessage == null) {
         throw new IllegalArgumentException("'modelNameMessage' must not be null.");
      }
      if (initialModelName == null) {
         throw new IllegalArgumentException("'initialModelName' must not be null.");
      }
      
      ModelNameDialog modelNameDialog =
         new ModelNameDialog(start, owner, title, modelNameMessage, initialModelName);
      
      // null if new model name was not successfully verified
      return modelNameDialog.verifiedModelName;
   }
   
   /**
    * Creates the panel with the model name field.
    * 
    * @return panel with model name field
    */
   private JPanel createPanel() {
      JPanel panel = new JPanel();
            
      /*
       * input panel
       */
      JPanel inputPanel = new JPanel();
      inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      inputPanel.add(new JLabel(modelNameMessage + ":"));
      modelNameField = new JTextField(initialModelName, 20);
      modelNameField.addFocusListener(this);
      inputPanel.add(modelNameField);
      
      /*
       * button panel
       */
      JPanel buttonPanel = new JPanel();
      okButton = new JButton(messages.getString("ModelNameDialog.OKButton.Text"));
      okButton.addActionListener(this);
      buttonPanel.add(okButton);
      cancelButton = new JButton(messages.getString("ModelNameDialog.CancelButton.Text"));
      cancelButton.addActionListener(this);
      buttonPanel.add(cancelButton);
      
      // set together
      panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
      panel.add(inputPanel);
      panel.add(buttonPanel);
      
      return panel;
   }
   
   /**
    * Verifies the new model name.
    * 
    * @return <code>true</code> iff the new model name could be verified
    *         (<code>String</code> with at least length 1)
    */
   private boolean verifyNewModelName() {
      // (1) set neutral background color
      modelNameField.setBackground(COLOR_NEUTRAL_FIELDS);
      
      // (2) check model name in field
      String modelName = modelNameField.getText();
      if (modelName == null || modelName.length() == 0) {
         modelNameField.setBackground(COLOR_WRONG_FIELDS);
         JOptionPane.showMessageDialog(null,
                                       messages.getString("ModelNameDialog.ErrorMessage"),
                                       messages.getString("ModelNameDialog.ErrorTitle"),
                                       JOptionPane.ERROR_MESSAGE);
         
         // exit method
         return false;
      }
      
      // model name correct
      verifiedModelName = modelName;
      
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
         boolean nodeNameVerified = verifyNewModelName();
         
         // close this dialog window if model name was verified successfully
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