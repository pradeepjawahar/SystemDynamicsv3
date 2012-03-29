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
import de.uka.aifb.com.systemDynamics.csv.CSVExport;
import de.uka.aifb.com.systemDynamics.event.ExportModelExecutionThreadEventListener;
import de.uka.aifb.com.systemDynamics.model.*;
import de.uka.aifb.com.systemDynamics.xml.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.*;
import java.util.*;
import javax.swing.*;

/**
 * This class implements a panel for exporting the values of a model execution.
 * 
 * @author Joachim Melcher, Institut AIFB, Universitaet Karlsruhe (TH), Germany
 * @version 1.0
 */
public class ExportPanel extends JPanel implements ActionListener,
                                                   ExportModelExecutionThreadEventListener,
                                                   FocusListener {

   private static final long serialVersionUID = 1L;
   
   private static final String TEMP_MODEL_FILE_NAME = "temp_model.xml";
   
   private Locale locale;
   private ResourceBundle messages;

   private NumberFormat integerNumberFormatter;
   
   private Model model;
   
   private JTextField numberRoundsField;
   private JButton csvExportButton;
   private JButton xmlExportButton;
   
   private JFileChooser csvFileChooser;
   private JFileChooser xmlFileChooser;
   
   /**
    * Constructor.
    * 
    * @param start {@link de.uka.aifb.com.systemDynamics.SystemDynamics} instance
    * @param model {@link de.uka.aifb.com.systemDynamics.model.Model} instance
    */
   public ExportPanel(SystemDynamics start, Model model) {
      super(null);
      
      if (start == null) {
         throw new IllegalArgumentException("'start' must not be null");
      }
      if (model == null) {
         throw new IllegalArgumentException("'model' must not be null");
      }
      
      this.model = model;
      
      locale = start.getLocale();
      messages = start.getMessages();
      
      integerNumberFormatter = NumberFormat.getIntegerInstance(locale);
      
      csvFileChooser = new JFileChooser();
      csvFileChooser.setFileFilter(new CSVFileFilter(start));
      xmlFileChooser = new JFileChooser();
      xmlFileChooser.setFileFilter(new XMLFileFilter(start));
      
      createPanel();
   }
      
   /**
    * Creates panel.
    */
   private void createPanel() {
      this.setLayout(new GridBagLayout());
      
      JPanel panel1 = new JPanel();
      JLabel label = new JLabel(messages.getString("ExportPanel.NumberRoundsToExport"));
      panel1.add(label);
      numberRoundsField = new JTextField("100", 5);
      numberRoundsField.addFocusListener(this);
      panel1.add(numberRoundsField);
      
      JPanel panel2 = new JPanel();
      csvExportButton = new JButton(messages.getString("ExportPanel.CSVExportButton.Text"));
      csvExportButton.addActionListener(this);
      panel2.add(csvExportButton);
      xmlExportButton = new JButton(messages.getString("ExportPanel.XMLExportButton.Text"));
      xmlExportButton.addActionListener(this);
      panel2.add(xmlExportButton);
      
      GridBagConstraints c = new GridBagConstraints();
      c.anchor = GridBagConstraints.CENTER;
      c.gridx = 0;
      c.gridy = 0;
      c.insets = new Insets(0, 0, 10, 0);
      add(panel1, c);
      c.gridx = 0;
      c.gridy = 1;
      add(panel2, c);
   }
   
////////////////////////////////////////////////////////////////////////////////////////////////////
//                             methods of interface ActionListener
////////////////////////////////////////////////////////////////////////////////////////////////////
   
   /**
    * Performs action events caused by clicking either the CSV or the XML export button.
    * 
    * @param e action event
    */
   public void actionPerformed(ActionEvent e) {
      // (1) check number of rounds
      int numberRounds = 0;
      boolean correctNumber = false;
      try {
         numberRounds = integerNumberFormatter.parse(numberRoundsField.getText()).intValue();
      } catch (ParseException parseExcep) {
         // do nothing
      }
      
      if (numberRounds >= 1) {
         correctNumber = true;
      }
      
      if (!correctNumber) {
         JOptionPane.showMessageDialog(this,
                                       messages.getString("ExportPanel.NumberRoundsError.Message"),
                                       messages.getString("ExportPanel.NumberRoundsError.Title"),
                                       JOptionPane.ERROR_MESSAGE);
         
         // exit method
         return;
      }
      
      // (2) file choose dialog
      File file;
      JFileChooser fileChooser;
      String fileEnd;
      
      if (e.getSource() == csvExportButton) {
         fileChooser = csvFileChooser;
         fileEnd = ".csv";
      } else {
         fileChooser = xmlFileChooser;
         fileEnd = ".xml";
      }
      
      int returnVal = fileChooser.showSaveDialog(this);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
         // file was selected and 'OK' was pressed
         file = fileChooser.getSelectedFile();
         
         // file name should have "fitting" end (".csv" or ".xml")
         if (!file.getName().toLowerCase().endsWith(fileEnd)) {
            file = new File(file.getAbsolutePath() + fileEnd);
         }
         
         // check if existing file should be overwritten -> ask for confirmation!
         if (file.exists()) {
            Object[] options = { messages.getString("ExportPanel.Yes"), messages.getString("ExportPanel.No") };
            int selectedOption = JOptionPane.showOptionDialog(this,
                                                              messages.getString("ExportPanel.ConfirmOverwriting.Message"),
                                                              messages.getString("ExportPanel.ConfirmOverwriting.Title"),
                                                              JOptionPane.YES_NO_OPTION,
                                                              JOptionPane.QUESTION_MESSAGE,
                                                              null, // don't use a custom Icon
                                                              options,
                                                              options[1]); // default button title
            
            if (selectedOption == 1) {
               // do not save
               return;
            }
         }
      } else {
         // no file selected
         return;
      }
      
      // (3) export
      
      // "clone" model
      Model clonedModel;
      try {
         XMLModelWriter.writeXMLModel(model, TEMP_MODEL_FILE_NAME);
         clonedModel = XMLModelReader.readXMLModel(TEMP_MODEL_FILE_NAME);
         clonedModel.validateModelAndSetUnchangeable();
      } catch (Exception excep) {
         JOptionPane.showMessageDialog(this,
                                       messages.getString("ExportPanel.ExportError.Message"),
                                       messages.getString("ExportPanel.ExportError.Title"),
                                       JOptionPane.ERROR_MESSAGE);
         
         // exit method
         return;
      }
      new File(TEMP_MODEL_FILE_NAME).delete();
      
      if (e.getSource() == csvExportButton) {
         // (3a) CSV export
         CSVExportModelExecutionThread executionThread =
            new CSVExportModelExecutionThread(clonedModel, file.getAbsolutePath(), numberRounds);
         executionThread.addExportModelExecutionThreadEventListener(this);
         executionThread.start();
      } else {
         // (3b) XML export
         XMLExportModelExecutionThread executionThread =
            new XMLExportModelExecutionThread(clonedModel, file.getAbsolutePath(), numberRounds);
         executionThread.addExportModelExecutionThreadEventListener(this);
         executionThread.start();
      }
   }

////////////////////////////////////////////////////////////////////////////////////////////////////
//                   methods of interface ExportModelExecutionThreadEventListener
////////////////////////////////////////////////////////////////////////////////////////////////////
   
   /**
    * Performs a export model execution thread event.
    */
   public void performExportModelExecutionThreadEvent() {
      JOptionPane.showMessageDialog(this,
                                    messages.getString("ExportPanel.ExportFinished.Message"),
                                    messages.getString("ExportPanel.ExportFinished.Title"),
                                    JOptionPane.INFORMATION_MESSAGE);
   }
   
////////////////////////////////////////////////////////////////////////////////////////////////////
//                              methods of interface FocusListener
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
   
   /**
    * This inner class implements a thread for model execution and CSV export within the swing GUI.
    */
   private class CSVExportModelExecutionThread extends Thread {
      
      private Model model;
      private String fileName;
      private int numberRounds;
      private boolean canceled;
      private LinkedList<ExportModelExecutionThreadEventListener> listeners;
      
      /**
       * Constructor.
       * 
       * @param model model ("cloned" model that is already validated and set unchangeable)
       * @param fileName file name
       * @param numberRounds number of rounds to execute
       */
      private CSVExportModelExecutionThread(Model model, String fileName, int numberRounds) {
         if (model == null) {
            throw new IllegalArgumentException("'model' must not be null.");
         }
         if (model.isChangeable()) {
            throw new IllegalArgumentException("'model' must not be changeable.");
         }
         if (fileName == null) {
            throw new IllegalArgumentException("'fileName' must not be null.");
         }
         if (numberRounds < 1) {
            throw new IllegalArgumentException("'numberRounds' must be at least 1.");
         }
         
         this.model = model;
         this.fileName = fileName;
         this.numberRounds = numberRounds;
         
         listeners = new LinkedList<ExportModelExecutionThreadEventListener>();
      }
      
      /**
       * Adds the specified
       * {@link de.uka.aifb.com.systemDynamics.event.ExportModelExecutionThreadEventListener} to
       * the list of listeners.
       * 
       * @param listener {@link de.uka.aifb.com.systemDynamics.event.ExportModelExecutionThreadEventListener}
       *                 to add
       */
      private void addExportModelExecutionThreadEventListener(ExportModelExecutionThreadEventListener listener) {
         if (listener == null) {
            throw new IllegalArgumentException("'listener' must not be null.");
         }
         
         listeners.add(listener);
      }
      
      /**
       * Executes the model for the specified number of rounds and exports the data into a CSV file.
       * A progress monitor shows progress information.
       */
      @Override
	public void run() {
         NumberFormat numberFormatter = NumberFormat.getIntegerInstance(locale);
         csvExportButton.setEnabled(false);
         setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
         
         LevelNode[] levelNodes = model.getLevelNodes().toArray(new LevelNode[0]);
         // sort level nodes alphabetically
         Arrays.sort(levelNodes);
         String[] columnNames = new String[levelNodes.length];
         for (int i = 0; i < columnNames.length; i++) {
            columnNames[i] = levelNodes[i].getNodeName();
         }
         
         ProgressMonitor progressMonitor = new ProgressMonitor(ExportPanel.this,
                                                               messages.getString("ExportModelExecutionThread.ProgressMonitor.Text"),
                                                               "",
                                                               0, numberRounds);
         try {
            CSVExport csvExport = new CSVExport(fileName, model.getModelName(), columnNames);

            double[] values = new double[levelNodes.length];
            for (int j = 0; j < values.length; j++) {
               values[j] = levelNodes[j].getCurrentValue();
            }
            csvExport.write(values);
            progressMonitor.setNote(numberFormatter.format(0) + " " + messages.getString("ExportModelExecutionThread.ProgressMonitor.Note.Text1") + " " + numberFormatter.format(numberRounds) + " " + messages.getString("ExportModelExecutionThread.ProgressMonitor.Note.Text2"));
            progressMonitor.setProgress(0);
            for (int i = 1; i <= numberRounds; i++) {
               if (progressMonitor.isCanceled()) {
                  // stop execution (i.e. for loop)
                  canceled = true;
                  break;
               }
               model.computeNextValues();
               values = new double[levelNodes.length];
               for (int j = 0; j < values.length; j++) {
                  values[j] = levelNodes[j].getCurrentValue();
               }
               csvExport.write(values);
               progressMonitor.setNote(numberFormatter.format(i) + " " + messages.getString("ExportModelExecutionThread.ProgressMonitor.Note.Text1") + " " + numberFormatter.format(numberRounds) + " " + messages.getString("ExportModelExecutionThread.ProgressMonitor.Note.Text2"));
               progressMonitor.setProgress(i);
            }
            csvExport.close();
         } catch (IOException e) {
            JOptionPane.showMessageDialog(ExportPanel.this,
                                          messages.getString("ExportModelExecutionThread.IOException.Message"),
                                          messages.getString("ExportModelExecutionThread.IOException.Title"),
                                          JOptionPane.ERROR_MESSAGE);
         }
         progressMonitor.close();
         setCursor(null);
         csvExportButton.setEnabled(true);
         
         // inform listeners
         if (!canceled) {
            for (ExportModelExecutionThreadEventListener listener : listeners) {
               listener.performExportModelExecutionThreadEvent();
            }
         }
      }
   }
   
   /**
    * This inner class implements a thread for model execution and XML export within the swing GUI.
    */
   private class XMLExportModelExecutionThread extends Thread {
      
      private Model model;
      private String fileName;
      private int numberRounds;
      private boolean canceled;
      private LinkedList<ExportModelExecutionThreadEventListener> listeners;
      
      /**
       * Constructor.
       * 
       * @param model model ("cloned" model that is already validated and set unchangeable)
       * @param fileName file name
       * @param numberRounds number of rounds to execute
       */
      private XMLExportModelExecutionThread(Model model, String fileName, int numberRounds) {
         if (model == null) {
            throw new IllegalArgumentException("'model' must not be null.");
         }
         if (model.isChangeable()) {
            throw new IllegalArgumentException("'model' must not be changeable.");
         }
         if (fileName == null) {
            throw new IllegalArgumentException("'fileName' must not be null.");
         }
         if (numberRounds < 1) {
            throw new IllegalArgumentException("'numberRounds' must be at least 1.");
         }
         
         this.model = model;
         this.fileName = fileName;
         this.numberRounds = numberRounds;
         
         listeners = new LinkedList<ExportModelExecutionThreadEventListener>();
      }
      
      /**
       * Adds the specified
       * {@link de.uka.aifb.com.systemDynamics.event.ExportModelExecutionThreadEventListener} to
       * the list of listeners.
       * 
       * @param listener {@link de.uka.aifb.com.systemDynamics.event.ExportModelExecutionThreadEventListener}
       *                 to add
       */
      private void addExportModelExecutionThreadEventListener(ExportModelExecutionThreadEventListener listener) {
         if (listener == null) {
            throw new IllegalArgumentException("'listener' must not be null.");
         }
         
         listeners.add(listener);
      }
      
      /**
       * Executes the model for the specified number of rounds and exports the data into an XML
       * file. A progress monitor shows progress information.
       */
      @Override
	public void run() {
         NumberFormat numberFormatter = NumberFormat.getIntegerInstance(locale);
         xmlExportButton.setEnabled(false);
         setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
         
         LevelNode[] levelNodes = model.getLevelNodes().toArray(new LevelNode[0]);
         // sort level nodes alphabetically
         Arrays.sort(levelNodes);
         String[] nodeNames = new String[levelNodes.length];
         for (int i = 0; i < nodeNames.length; i++) {
            nodeNames[i] = levelNodes[i].getNodeName();
         }
         
         ProgressMonitor progressMonitor = new ProgressMonitor(ExportPanel.this,
                                                               messages.getString("ExportModelExecutionThread.ProgressMonitor.Text"),
                                                               "",
                                                               0, numberRounds);
         try {
            XMLExport xmlExport = new XMLExport(fileName, model.getModelName(), numberRounds, nodeNames);

            double[] values = new double[levelNodes.length];
            for (int j = 0; j < values.length; j++) {
               values[j] = levelNodes[j].getCurrentValue();
            }
            xmlExport.write(values);
            progressMonitor.setNote(numberFormatter.format(0) + " " + messages.getString("ExportModelExecutionThread.ProgressMonitor.Note.Text1") + " " + numberFormatter.format(numberRounds) + " " + messages.getString("ExportModelExecutionThread.ProgressMonitor.Note.Text2"));
            progressMonitor.setProgress(0);
            for (int i = 1; i <= numberRounds; i++) {
               if (progressMonitor.isCanceled()) {
                  // stop execution (i.e. for loop)
                  canceled = true;
                  // delete started file
                  xmlExport.delete();
                  
                  setCursor(null);
                  xmlExportButton.setEnabled(true);
                  
                  JOptionPane.showMessageDialog(ExportPanel.this,
                                                messages.getString("ExportModelExecutionThread.Cancel.Message"),
                                                messages.getString("ExportModelExecutionThread.Cancel.Title"),
                                                JOptionPane.INFORMATION_MESSAGE);
                  
                  return;
               }
               model.computeNextValues();
               values = new double[levelNodes.length];
               for (int j = 0; j < values.length; j++) {
                  values[j] = levelNodes[j].getCurrentValue();
               }
               xmlExport.write(values);
               progressMonitor.setNote(numberFormatter.format(i) + " " + messages.getString("ExportModelExecutionThread.ProgressMonitor.Note.Text1") + " " + numberFormatter.format(numberRounds) + " " + messages.getString("ExportModelExecutionThread.ProgressMonitor.Note.Text2"));
               progressMonitor.setProgress(i);
            }
            xmlExport.close();
         } catch (IOException e) {
            JOptionPane.showMessageDialog(ExportPanel.this,
                                          messages.getString("ExportModelExecutionThread.IOException.Message"),
                                          messages.getString("ExportModelExecutionThread.IOException.Title"),
                                          JOptionPane.ERROR_MESSAGE);
         }
         progressMonitor.close();
         setCursor(null);
         xmlExportButton.setEnabled(true);
         
         // inform listeners
         if (!canceled) {
            for (ExportModelExecutionThreadEventListener listener : listeners) {
               listener.performExportModelExecutionThreadEvent();
            }
         }
      }
   }
}