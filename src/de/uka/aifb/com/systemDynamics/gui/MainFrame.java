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
import de.uka.aifb.com.systemDynamics.event.SystemDynamicsGraphModifiedEventListener;
import de.uka.aifb.com.systemDynamics.gui.systemDynamicsGraph.SystemDynamicsGraph;
import de.uka.aifb.com.systemDynamics.model.*;
import de.uka.aifb.com.systemDynamics.xml.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.event.*;

/*
 * Changes:
 * ========
 *
 * 2007-08-30: ExecuteModelAction.actionPerformed was rewritten: other text (without node name)
 *             for useless source/sink node
 * 2008-01-24: createMenuBar was rewritten: additional language 'Spanish'
 * 2008-01-24: actionPerformed was rewritten: additional code for Spanish GUI
 */

/**
 * This class implements the main frame of this application.
 * 
 * @author Joachim Melcher, Institut AIFB, Universitaet Karlsruhe (TH), Germany
 * @version 1.2
 */
public class MainFrame extends JFrame implements ActionListener, ChangeListener,
                                                 SystemDynamicsGraphModifiedEventListener,
                                                 WindowListener {
   
   private static final long serialVersionUID = 1L;
   
   private static final double DEFAULT_COORDINATE = 20.0;
   
   private static final String FILE_ICON = "resources/icon.png";
   private static final String FILE_GPL_TEXT = "resources/license-GPL.txt";
   private static final String FILE_LGPL_TEXT = "resources/license-LGPL.txt";
   private static final String FILE_CC_BY_TEXT = "resources/license-CC-BY-2.5.txt";
   
   private static final String FILE_NEW_ICON = "resources/page_white.png";
   private static final String FILE_OPEN_ICON = "resources/folder_page_white.png";
   private static final String FILE_SAVE_ICON = "resources/disk.png";
   
   private static final String FILE_NEW_AN_ICON = "resources/new_auxiliary_node_en_US.png";
   private static final String FILE_NEW_AN_de_DE_ICON = "resources/new_auxiliary_node_de_DE.png";
   private static final String FILE_NEW_CN_ICON = "resources/new_constant_node_en_US.png";
   private static final String FILE_NEW_CN_de_DE_ICON = "resources/new_constant_node_de_DE.png";
   private static final String FILE_NEW_LN_ICON = "resources/new_level_node_en_US.png";
   private static final String FILE_NEW_LN_de_DE_ICON = "resources/new_level_node_de_DE.png";
   private static final String FILE_NEW_RN_ICON = "resources/new_rate_node_en_US.png";
   private static final String FILE_NEW_RN_de_DE_ICON = "resources/new_rate_node_de_DE.png";
   private static final String FILE_NEW_SSN_ICON = "resources/new_source_sink_node_en_US.png";
   private static final String FILE_NEW_SSN_de_DE_ICON = "resources/new_source_sink_node_de_DE.png";
   
   private static final String FILE_ENTER_ADD_FLOW_MODE_ICON = "resources/connect.png";
   private static final String FILE_LEAVE_ADD_FLOW_MODE_ICON = "resources/disconnect.png";
   private static final String FILE_CHANGE_MODEL_NAME_ICON = "resources/page_white_edit.png";
   private static final String FILE_EXECUTE_MODEL_ICON = "resources/page_white_gear.png";
   
   private static final String FILE_ZOOM_STANDARD_ICON = "resources/zoom.png";
   private static final String FILE_ZOOM_IN_ICON = "resources/zoom_in.png";
   private static final String FILE_ZOOM_OUT_ICON = "resources/zoom_out.png";
   
   private static final String LANGUAGE_ENGLISH = "English";
   private static final String LANGUAGE_GERMAN = "Deutsch";
   private static final String LANGUAGE_SPANISH = "Español";
   
   private SystemDynamics start;
   
   private ResourceBundle messages;
   
   private SystemDynamicsGraph graph;
   private boolean graphModified;
   
   private JPanel contentPanel;
   private JTabbedPane tabbedPane;
   private JScrollPane scrollPane;
   
   private File xmlFile;
   private String fileName;
   
   private Action newAction;
   private Action openAction;
   private Action closeAction;
   private Action saveAction;
   private Action saveAsAction;
   private Action newAuxiliaryNodeAction;
   private Action newConstantNodeAction;
   private Action newLevelNodeAction;
   private Action newRateNodeAction;
   private Action newSourceSinkNodeAction;
   private Action toggleAddFlowAction;
   private Action changeModelNameAction;
   private Action executeModelAction;
   private Action zoomStandardAction;
   private Action zoomInAction;
   private Action zoomOutAction;
   private JCheckBoxMenuItem addFlowModeCheckBoxMenuItem;
   private JRadioButtonMenuItem rbMenuItemEnglish;
   private JRadioButtonMenuItem rbMenuItemGerman;
   private JRadioButtonMenuItem rbMenuItemSpanish;
   
   private JFileChooser fileChooser;
   
   /**
    * Constructor.
    * 
    * @param start {@link de.uka.aifb.com.systemDynamics.SystemDynamics} instance
    */
   public MainFrame(SystemDynamics start) {
      if (start == null) {
         throw new IllegalArgumentException("'start' must not be null.");
      }
      
      this.start = start;
      messages = start.getMessages();
      
      graphModified = false;
      
      fileChooser = new JFileChooser();
      fileChooser.setFileFilter(new XMLFileFilter(start));
      
      setTitle(messages.getString("MainFrame.Title"));
      
      // set frame size and location
      Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
      setSize((int)(dimension.getWidth() * 0.75), (int)(dimension.getHeight() * 0.75));
      setLocation((int)(dimension.getWidth() * 0.125), (int)(dimension.getHeight() * 0.125));
      
      // set icon
      setIconImage(new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_ICON)).getImage());

      // initialize actions
      initializeActions();
      
      // create menu
      setJMenuBar(createMenuBar());
      
      // create tool bar
      getContentPane().setLayout(new BorderLayout());
      getContentPane().add(createToolBar(), BorderLayout.PAGE_START);
      contentPanel = new JPanel(new BorderLayout());
      getContentPane().add(contentPanel, BorderLayout.CENTER);
      
      setVisible(true);

      setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      addWindowListener(this);
   }
   
   /**
    * Initializes the used actions.
    */
   private void initializeActions() {
      newAction = new NewAction(messages.getString("MainFrame.MenuBar.File.New"),
                                new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_NEW_ICON)),
                                messages.getString("MainFrame.MenuBar.File.New"));
      openAction = new OpenAction(messages.getString("MainFrame.MenuBar.File.Open"),
                                  new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_OPEN_ICON)),
                                  messages.getString("MainFrame.MenuBar.File.Open"));
      
      closeAction = new CloseAction(messages.getString("MainFrame.MenuBar.File.Close"));
      closeAction.setEnabled(false);
      
      saveAction = new SaveAction(messages.getString("MainFrame.MenuBar.File.Save"),
                                  new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_SAVE_ICON)),
                                  messages.getString("MainFrame.MenuBar.File.Save"));
      saveAction.setEnabled(false);
      saveAsAction = new SaveAsAction(messages.getString("MainFrame.MenuBar.File.SaveAs"));
      saveAsAction.setEnabled(false);
      
      if (start.getLocale() == Locale.GERMANY) {
         newAuxiliaryNodeAction = new NewAuxiliaryNodeAction(messages.getString("MainFrame.MenuBar.Edit.NewAuxiliaryNode"),
                                                             new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_NEW_AN_de_DE_ICON)),
                                                             messages.getString("MainFrame.MenuBar.Edit.NewAuxiliaryNode"));
         newAuxiliaryNodeAction.setEnabled(false);
         newConstantNodeAction = new NewConstantNodeAction(messages.getString("MainFrame.MenuBar.Edit.NewConstantNode"),
                                                           new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_NEW_CN_de_DE_ICON)),
                                                           messages.getString("MainFrame.MenuBar.Edit.NewConstantNode"));
         newConstantNodeAction.setEnabled(false);
         newLevelNodeAction = new NewLevelNodeAction(messages.getString("MainFrame.MenuBar.Edit.NewLevelNode"),
                                                     new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_NEW_LN_de_DE_ICON)),
                                                     messages.getString("MainFrame.MenuBar.Edit.NewLevelNode"));
         newLevelNodeAction.setEnabled(false);
         newRateNodeAction = new NewRateNodeAction(messages.getString("MainFrame.MenuBar.Edit.NewRateNode"),
                                                   new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_NEW_RN_de_DE_ICON)),
                                                   messages.getString("MainFrame.MenuBar.Edit.NewRateNode"));
         newRateNodeAction.setEnabled(false);
         newSourceSinkNodeAction = new NewSourceSinkNodeAction(messages.getString("MainFrame.MenuBar.Edit.NewSourceSinkNode"),
                                                               new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_NEW_SSN_de_DE_ICON)),
                                                               messages.getString("MainFrame.MenuBar.Edit.NewSourceSinkNode"));
         newSourceSinkNodeAction.setEnabled(false);
      } else {
         // english
         newAuxiliaryNodeAction = new NewAuxiliaryNodeAction(messages.getString("MainFrame.MenuBar.Edit.NewAuxiliaryNode"),
                                                             new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_NEW_AN_ICON)),
                                                             messages.getString("MainFrame.MenuBar.Edit.NewAuxiliaryNode"));
         newAuxiliaryNodeAction.setEnabled(false);
         newConstantNodeAction = new NewConstantNodeAction(messages.getString("MainFrame.MenuBar.Edit.NewConstantNode"),
                                                           new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_NEW_CN_ICON)),
                                                           messages.getString("MainFrame.MenuBar.Edit.NewConstantNode"));
         newConstantNodeAction.setEnabled(false);
         newLevelNodeAction = new NewLevelNodeAction(messages.getString("MainFrame.MenuBar.Edit.NewLevelNode"),
                                                     new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_NEW_LN_ICON)),
                                                     messages.getString("MainFrame.MenuBar.Edit.NewLevelNode"));
         newLevelNodeAction.setEnabled(false);
         newRateNodeAction = new NewRateNodeAction(messages.getString("MainFrame.MenuBar.Edit.NewRateNode"),
                                                   new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_NEW_RN_ICON)),
                                                   messages.getString("MainFrame.MenuBar.Edit.NewRateNode"));
         newRateNodeAction.setEnabled(false);
         newSourceSinkNodeAction = new NewSourceSinkNodeAction(messages.getString("MainFrame.MenuBar.Edit.NewSourceSinkNode"),
                                                               new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_NEW_SSN_ICON)),
                                                               messages.getString("MainFrame.MenuBar.Edit.NewSourceSinkNode"));
         newSourceSinkNodeAction.setEnabled(false);
      }
      
      toggleAddFlowAction = new ToggleAddFlowAction(messages.getString("MainFrame.MenuBar.Edit.ToggleAddFlowMode.EnterAddFlowMode"),
                                                    new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_ENTER_ADD_FLOW_MODE_ICON)),
                                                    messages.getString("MainFrame.MenuBar.Edit.ToggleAddFlowMode.EnterAddFlowMode"));
      toggleAddFlowAction.setEnabled(false);
      
      changeModelNameAction = new ChangeModelNameAction(messages.getString("MainFrame.MenuBar.Edit.ChangeModelName"),
                                                        new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_CHANGE_MODEL_NAME_ICON)),
                                                        messages.getString("MainFrame.MenuBar.Edit.ChangeModelName"));
      changeModelNameAction.setEnabled(false);
      
      executeModelAction = new ExecuteModelAction(messages.getString("MainFrame.MenuBar.Edit.ExecuteModel"),
                                                  new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_EXECUTE_MODEL_ICON)),
                                                  messages.getString("MainFrame.MenuBar.Edit.ExecuteModel"));
      executeModelAction.setEnabled(false);
      
      zoomStandardAction = new ZoomStandardAction(messages.getString("MainFrame.MenuBar.View.ZoomStandard"),
                                                  new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_ZOOM_STANDARD_ICON)),
                                                  messages.getString("MainFrame.MenuBar.View.ZoomStandard"));
      zoomStandardAction.setEnabled(false);
      zoomInAction = new ZoomInAction(messages.getString("MainFrame.MenuBar.View.ZoomIn"),
                                      new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_ZOOM_IN_ICON)),
                                      messages.getString("MainFrame.MenuBar.View.ZoomIn"));
      zoomInAction.setEnabled(false);
      zoomOutAction = new ZoomOutAction(messages.getString("MainFrame.MenuBar.View.ZoomOut"),
                                        new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_ZOOM_OUT_ICON)),
                                        messages.getString("MainFrame.MenuBar.View.ZoomOut"));
      zoomOutAction.setEnabled(false);
   }
      
   /**
    * Creates a new menu bar.
    * 
    * @return menu bar
    */
   private JMenuBar createMenuBar() {
      JMenuBar menuBar = new JMenuBar();
      
      // create file menu
      JMenu fileMenu = new JMenu(messages.getString("MainFrame.MenuBar.File"));
      JMenuItem menuItem = new JMenuItem(newAction);
      menuItem.setToolTipText(null);
      fileMenu.add(menuItem);
      menuItem = new JMenuItem(openAction);
      menuItem.setToolTipText(null);
      fileMenu.add(menuItem);
      fileMenu.addSeparator();
      fileMenu.add(closeAction);
      fileMenu.addSeparator();
      menuItem = new JMenuItem(saveAction);
      menuItem.setToolTipText(null);
      fileMenu.add(menuItem);
      fileMenu.add(saveAsAction);
      fileMenu.addSeparator();
      JMenuItem exitMenuItem = new JMenuItem(messages.getString("MainFrame.MenuBar.File.Exit"));
      exitMenuItem.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            if (graphModified) {
               Object[] options = { messages.getString("MainFrame.Yes"), messages.getString("MainFrame.No") };
               int selectedOption = JOptionPane.showOptionDialog(MainFrame.this,
                                                                 messages.getString("MainFrame.ConfirmExiting.Message"),
                                                                 messages.getString("MainFrame.ConfirmExiting.Title"),
                                                                 JOptionPane.YES_NO_OPTION,
                                                                 JOptionPane.QUESTION_MESSAGE,
                                                                 null, // don't use a custom Icon
                                                                 options,
                                                                 options[1]); // default button title
               
               if (selectedOption == 1) {
                  // do not close graph
                  return;
               }
            }
            
            // exit application with "normal" exit code 0
            System.exit(0);      
         }
      });
      fileMenu.add(exitMenuItem);
      menuBar.add(fileMenu);
      
      // create edit menu
      JMenu editMenu = new JMenu(messages.getString("MainFrame.MenuBar.Edit"));
      menuItem = new JMenuItem(newAuxiliaryNodeAction);
      menuItem.setToolTipText(null);
      editMenu.add(menuItem);
      menuItem = new JMenuItem(newConstantNodeAction);
      menuItem.setToolTipText(null);
      editMenu.add(menuItem);
      menuItem = new JMenuItem(newLevelNodeAction);
      menuItem.setToolTipText(null);
      editMenu.add(menuItem);
      menuItem = new JMenuItem(newRateNodeAction);
      menuItem.setToolTipText(null);
      editMenu.add(menuItem);
      menuItem = new JMenuItem(newSourceSinkNodeAction);
      menuItem.setToolTipText(null);
      editMenu.add(menuItem);
      editMenu.addSeparator();
      addFlowModeCheckBoxMenuItem = new JCheckBoxMenuItem(toggleAddFlowAction);
      addFlowModeCheckBoxMenuItem.setToolTipText(null);
      editMenu.add(addFlowModeCheckBoxMenuItem);
      editMenu.addSeparator();
      menuItem = new JMenuItem(changeModelNameAction);
      menuItem.setToolTipText(null);
      editMenu.add(menuItem);
      editMenu.addSeparator();
      menuItem = new JMenuItem(executeModelAction);
      menuItem.setToolTipText(null);
      editMenu.add(menuItem);
      menuBar.add(editMenu);
      
      // create view menu
      JMenu viewMenu = new JMenu(messages.getString("MainFrame.MenuBar.View"));
      menuItem = new JMenuItem(zoomStandardAction);
      menuItem.setToolTipText(null);
      viewMenu.add(menuItem);
      menuItem = new JMenuItem(zoomInAction);
      menuItem.setToolTipText(null);
      viewMenu.add(menuItem);
      menuItem = new JMenuItem(zoomOutAction);
      menuItem.setToolTipText(null);
      viewMenu.add(menuItem);
      menuBar.add(viewMenu);
      
      // create options menu
      JMenu optionsMenu = new JMenu(messages.getString("MainFrame.MenuBar.Options"));
      menuBar.add(optionsMenu);
      
      // create options -> language menu
      JMenu languageMenu = new JMenu(messages.getString("MainFrame.MenuBar.Options.Language"));
      ButtonGroup group = new ButtonGroup();
      rbMenuItemGerman = new JRadioButtonMenuItem(LANGUAGE_GERMAN);
      if (start.getLocale() == Locale.GERMANY) {
         rbMenuItemGerman.setSelected(true);
      }
      rbMenuItemGerman.addActionListener(this);
      group.add(rbMenuItemGerman);
      languageMenu.add(rbMenuItemGerman);
      rbMenuItemEnglish = new JRadioButtonMenuItem(LANGUAGE_ENGLISH);
      if (start.getLocale() == Locale.US) {
         rbMenuItemEnglish.setSelected(true);
      }
      rbMenuItemEnglish.addActionListener(this);
      group.add(rbMenuItemEnglish);
      languageMenu.add(rbMenuItemEnglish);
      rbMenuItemSpanish = new JRadioButtonMenuItem(LANGUAGE_SPANISH);
      if (start.getLocale().equals(new Locale("es", "ES"))) {
         rbMenuItemSpanish.setSelected(true);
      }
      rbMenuItemSpanish.addActionListener(this);
      group.add(rbMenuItemSpanish);
      languageMenu.add(rbMenuItemSpanish);
      optionsMenu.add(languageMenu);
      
      // create help menu
      JMenu helpMenu = new JMenu(messages.getString("MainFrame.MenuBar.Help"));
      JMenuItem explanationMenuItem = new JMenuItem(messages.getString("MainFrame.MenuBar.Help.Explanation"));
      explanationMenuItem.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(null,
                                          messages.getString("MainFrame.MenuBar.Help.Explanation.Message"),
                                          messages.getString("MainFrame.MenuBar.Help.Explanation"),
                                          JOptionPane.INFORMATION_MESSAGE);            
         }
      });
      helpMenu.add(explanationMenuItem);
      helpMenu.addSeparator();
      JMenuItem aboutMenuItem = new JMenuItem(messages.getString("MainFrame.MenuBar.Help.About"));
      aboutMenuItem.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            JPanel panel = new JPanel(new BorderLayout());
            JLabel label = new JLabel(messages.getString("MainFrame.MenuBar.Help.About.Message"));
            label.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
            panel.add(label, BorderLayout.PAGE_START);
            
            JPanel subPanel = new JPanel(new BorderLayout());
            JLabel licenseLabel = new JLabel(messages.getString("MainFrame.MenuBar.Help.About.License"));
            licenseLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
            subPanel.add(licenseLabel, BorderLayout.PAGE_START);
            
            JTextArea textAreaGPL = new JTextArea(15, 83);
            InputStream inGPL = null;
            InputStreamReader readerGPL = null;
            try {
               try {
                  inGPL = Thread.currentThread().getContextClassLoader().getResourceAsStream(FILE_GPL_TEXT);
                  readerGPL = new InputStreamReader(inGPL);
                  textAreaGPL.read(readerGPL, null);
               } catch (IOException exception) {
                  textAreaGPL.setText(messages.getString("MainFrame.MenuBar.Help.About.License.GPL.Error"));
               } catch (NullPointerException exception) {
                  textAreaGPL.setText(messages.getString("MainFrame.MenuBar.Help.About.License.GPL.Error"));
               } finally {
                  if (readerGPL != null) {
                     readerGPL.close();
                  }
                  if (inGPL != null) {
                     inGPL.close();
                  }
               }
            } catch (IOException exception) {
               // catch IOException of 'fileReader.close()' -> do nothing
            }
            textAreaGPL.setEditable(false);
            JScrollPane areaScrollPaneGPL = new JScrollPane(textAreaGPL);
            
            JTextArea textAreaLGPL = new JTextArea(15, 83);
            InputStream inLGPL = null;
            InputStreamReader readerLGPL = null;
            try {
               try {
                  inLGPL = Thread.currentThread().getContextClassLoader().getResourceAsStream(FILE_LGPL_TEXT);
                  readerLGPL = new InputStreamReader(inLGPL);
                  textAreaLGPL.read(readerLGPL, null);
               } catch (IOException exception) {
                  textAreaLGPL.setText(messages.getString("MainFrame.MenuBar.Help.About.License.LGPL.Error"));
               } catch (NullPointerException exception) {
                  textAreaLGPL.setText(messages.getString("MainFrame.MenuBar.Help.About.License.LGPL.Error"));
               } finally {
                  if (readerLGPL != null) {
                     readerLGPL.close();
                  }
                  if (inLGPL != null) {
                     inLGPL.close();
                  }
               }
            } catch (IOException exception) {
               // catch IOException of 'fileReader.close()' -> do nothing
            }
            textAreaLGPL.setEditable(false);
            JScrollPane areaScrollPaneLGPL = new JScrollPane(textAreaLGPL);
            
            JTextArea textAreaCCBY = new JTextArea(15, 83);
            InputStream inCCBY = null;
            InputStreamReader readerCCBY = null;
            try {
               try {
                  inCCBY = Thread.currentThread().getContextClassLoader().getResourceAsStream(FILE_CC_BY_TEXT);
                  readerCCBY = new InputStreamReader(inCCBY);
                  textAreaCCBY.read(readerCCBY, null);
               } catch (IOException exception) {
                  textAreaCCBY.setText(messages.getString("MainFrame.MenuBar.Help.About.License.CCBY.Error"));
               } catch (NullPointerException exception) {
                  textAreaCCBY.setText(messages.getString("MainFrame.MenuBar.Help.About.License.CCBY.Error"));
               } finally {
                  if (readerCCBY != null) {
                     readerCCBY.close();
                  }
                  if (inCCBY != null) {
                     inCCBY.close();
                  }
               }
            } catch (IOException exception) {
               // catch IOException of 'fileReader.close()' -> do nothing
            }
            textAreaCCBY.setEditable(false);
            JScrollPane areaScrollPaneCCBY = new JScrollPane(textAreaCCBY);
            
            JTabbedPane tabbedPane = new JTabbedPane();
            tabbedPane.add(messages.getString("MainFrame.MenuBar.Help.About.License.GPL"), areaScrollPaneGPL);
            tabbedPane.add(messages.getString("MainFrame.MenuBar.Help.About.License.LGPL"), areaScrollPaneLGPL);
            tabbedPane.add(messages.getString("MainFrame.MenuBar.Help.About.License.CCBY"), areaScrollPaneCCBY);
            
            subPanel.add(tabbedPane, BorderLayout.CENTER);
            subPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            
            JPanel helpPanel = new JPanel();
            helpPanel.add(subPanel);
            helpPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
            
            panel.add(helpPanel, BorderLayout.CENTER);
            
            JOptionPane.showMessageDialog(null, panel,
                                          messages.getString("MainFrame.MenuBar.Help.About"),
                                          JOptionPane.PLAIN_MESSAGE);            
         }
      });
      helpMenu.add(aboutMenuItem);
      menuBar.add(helpMenu);
   
      return menuBar;      
   }
   
   /**
    * Creates a new tool bar.
    * 
    * @return tool bar
    */
   private JToolBar createToolBar() {
      JToolBar toolBar = new JToolBar();
      toolBar.setRollover(true);
      toolBar.setFloatable(false);
      
      toolBar.add(newAction);
      toolBar.add(openAction);
      toolBar.add(saveAction);
      
      toolBar.addSeparator();
      
      toolBar.add(newAuxiliaryNodeAction);
      toolBar.add(newConstantNodeAction);
      toolBar.add(newLevelNodeAction);
      toolBar.add(newRateNodeAction);
      toolBar.add(newSourceSinkNodeAction);
      toolBar.add(toggleAddFlowAction);
      toolBar.add(changeModelNameAction);
      
      toolBar.addSeparator();
      
      toolBar.add(executeModelAction);
      
      toolBar.addSeparator();
      
      toolBar.add(zoomStandardAction);
      toolBar.add(zoomInAction);
      toolBar.add(zoomOutAction);
      
      return toolBar;
   }
   
   /**
    * Creates the title for this frame.
    * 
    * @param modelName model name (or <code>null</code> if no model opened)
    * @param changed model changed (ignored if first parameter is <code>null</code>)
    * @return title for this frame
    */
   private String createTitle(String modelName, boolean changed) {
      StringBuffer title = new StringBuffer();
      title.append(messages.getString("MainFrame.Title"));
      
      if (modelName != null) {
         String zoomString = ((int)(graph.getScale() * 100)) + "%";
         title.append(" - ");
         title.append(modelName);
         title.append(" (" + messages.getString("MainFrame.Title.Zoom") + " " + zoomString + ") - ");
         title.append("[ " + fileName + " ");
         if (changed) {
            title.append("*");
         }
         title.append("]");
      }
      
      return title.toString();
   }

////////////////////////////////////////////////////////////////////////////////////////////////////
//                           methods of interface ActionListener
////////////////////////////////////////////////////////////////////////////////////////////////////
   
   /**
    * Performs the specified action event that is caused by one of the language selection
    * <code>JRadioButtonMenuItem</code>s.
    * 
    * @param e event
    */
   public void actionPerformed(ActionEvent e) {
      boolean changed = false;
      
      if (e.getSource() == rbMenuItemEnglish) {
         // change language to English
         start.storeNewLocale(Locale.US);
         changed = true;
      }
      
      if (e.getSource() == rbMenuItemGerman) {
         // change language to German
         start.storeNewLocale(Locale.GERMANY);
         changed = true;
      }
      
      if (e.getSource() == rbMenuItemSpanish) {
         // change language to Spanish
         start.storeNewLocale(new Locale("es", "ES"));
         changed = true;
      }
      
      if (changed) {
         JOptionPane.showMessageDialog(null,
               messages.getString("MainFrame.MenuBar.Options.Language.Message"),
               messages.getString("MainFrame.MenuBar.Options.Language"),
               JOptionPane.INFORMATION_MESSAGE);  
      }
   }
   
////////////////////////////////////////////////////////////////////////////////////////////////////
//                           methods of interface ChangeListener
////////////////////////////////////////////////////////////////////////////////////////////////////

   /**
    * Performs the specified change event that is caused by the tabbed pane.
    * 
    * @param e event
    */
   public void stateChanged(ChangeEvent e) {
      if (e.getSource() == tabbedPane) {
         if (tabbedPane.getSelectedIndex() == 0) {
            // tab "model" selected
            zoomStandardAction.setEnabled(true);
            zoomInAction.setEnabled(true);
            zoomOutAction.setEnabled(true);
         } else {
            // tab "chart" or "export" selected
            zoomStandardAction.setEnabled(false);
            zoomInAction.setEnabled(false);
            zoomOutAction.setEnabled(false);
         }
      }
   }
   
////////////////////////////////////////////////////////////////////////////////////////////////////
//                  methods of interface SystemDynamicsGraphModifiedEventListener
////////////////////////////////////////////////////////////////////////////////////////////////////

   /**
    * Performs a graph modified event.
    */
   public void performGraphModifiedEvent() {
      if (!graphModified) {
         graphModified = true;
         
         setTitle(createTitle(graph.getModelName(), graphModified));
         
         saveAction.setEnabled(true);
         saveAsAction.setEnabled(true);
      }
   }
   
////////////////////////////////////////////////////////////////////////////////////////////////////
//                             methods of interface WindowListener
////////////////////////////////////////////////////////////////////////////////////////////////////
   
   public void windowActivated(WindowEvent e) {
      // do noting
   }
   
   public void windowClosed(WindowEvent e) {
      // do nothing
   }
   
   public void windowClosing(WindowEvent e) {
      if (graphModified) {
         Object[] options = { messages.getString("MainFrame.Yes"), messages.getString("MainFrame.No") };
         int selectedOption = JOptionPane.showOptionDialog(MainFrame.this,
                                                           messages.getString("MainFrame.ConfirmExiting.Message"),
                                                           messages.getString("MainFrame.ConfirmExiting.Title"),
                                                           JOptionPane.YES_NO_OPTION,
                                                           JOptionPane.QUESTION_MESSAGE,
                                                           null, // don't use a custom Icon
                                                           options,
                                                           options[1]); // default button title
         
         if (selectedOption == 1) {
            // do not close graph
            return;
         }
      }
      
      // exit application with "normal" exit code 0
      System.exit(0);      
   }

   public void windowDeactivated(WindowEvent e) {
      // do nothing
   }

   public void windowDeiconified(WindowEvent e) {
      // do nothing
   }

   public void windowIconified(WindowEvent e) {
      // do nothing
   }
 
   public void windowOpened(WindowEvent e) {
      // do nothing
   }

////////////////////////////////////////////////////////////////////////////////////////////////////
//                                           Actions
////////////////////////////////////////////////////////////////////////////////////////////////////   
   
   private class NewAction extends AbstractAction {
      
      private static final long serialVersionUID = 1L;
      
      public NewAction(String name, Icon icon, String toolTipText) {
         super(name, icon);
         
         putValue(Action.SHORT_DESCRIPTION, toolTipText);
      }
      
      public void actionPerformed(ActionEvent e) {
         String modelName =
            ModelNameDialog.showModelNameDialog(start, MainFrame.this,
                                                messages.getString("MainFrame.MenuBar.File.NewModel"),
                                                messages.getString("MainFrame.MenuBar.File.NewModelName"),
                                                "");
         
         graph = new SystemDynamicsGraph(start, MainFrame.this);
         if (modelName != null) {
            graph.setModelName(modelName);
            graph.addSystemDynamicsGraphModifiedEventListener(MainFrame.this);
            scrollPane = new JScrollPane(graph);
            contentPanel.removeAll();
            contentPanel.add(scrollPane, BorderLayout.CENTER);
            getContentPane().validate();
            
            fileName = messages.getString("MainFrame.NewDocument");
            setTitle(createTitle(graph.getModelName(), false));
            
            newAction.setEnabled(false);
            openAction.setEnabled(false);
            closeAction.setEnabled(true);
            saveAction.setEnabled(false);
            saveAsAction.setEnabled(false);
            
            newAuxiliaryNodeAction.setEnabled(true);
            newConstantNodeAction.setEnabled(true);
            newLevelNodeAction.setEnabled(true);
            newRateNodeAction.setEnabled(true);
            newSourceSinkNodeAction.setEnabled(true);
            toggleAddFlowAction.setEnabled(true);
            changeModelNameAction.setEnabled(true);
            executeModelAction.setEnabled(true);
            
            zoomStandardAction.setEnabled(true);
            zoomInAction.setEnabled(true);
            zoomOutAction.setEnabled(true);
         }
      }
   }
   
   private class OpenAction extends AbstractAction {
      
      private static final long serialVersionUID = 1L;
      
      public OpenAction(String name, Icon icon, String toolTipText) {
         super(name, icon);
         
         putValue(Action.SHORT_DESCRIPTION, toolTipText);
      }
      
      public void actionPerformed(ActionEvent e) {
         int returnVal = fileChooser.showOpenDialog(MainFrame.this);
         if (returnVal == JFileChooser.APPROVE_OPTION) {
            // file was selected and 'OK' was pressed
            File file = fileChooser.getSelectedFile();
            try {
               graph = XMLModelReader.readXMLSystemDynamicsGraph(file.getAbsolutePath(), start, MainFrame.this);
               graph.addSystemDynamicsGraphModifiedEventListener(MainFrame.this);
            } catch (AuxiliaryNodesCycleDependencyException excep) {
               JOptionPane.showMessageDialog(MainFrame.this,
                                             messages.getString("MainFrame.OpenFile.AuxiliaryNodesCycleDependencyException.Text"),
                                             messages.getString("MainFrame.OpenFile.Error"),
                                             JOptionPane.ERROR_MESSAGE);

               return;
            } catch (XMLNodeParameterOutOfRangeException excep) {
               JOptionPane.showMessageDialog(MainFrame.this,
                                             messages.getString("MainFrame.OpenFile.NodeParameterOutOfRangeException.Text1") + " '" + excep.getXMLNodeId() + "' " + messages.getString("MainFrame.OpenFile.NodeParameterOutOfRangeException.Text2"),
                                             messages.getString("MainFrame.OpenFile.Error"),
                                             JOptionPane.ERROR_MESSAGE);

               return;
            } catch (XMLRateNodeFlowException excep) {
               JOptionPane.showMessageDialog(MainFrame.this,
                                             messages.getString("MainFrame.OpenFile.RateNodeFlowException.Text1") + " '" + excep.getXMLNodeId() + "' " + messages.getString("MainFrame.OpenFile.RateNodeFlowException.Text2"),
                                             messages.getString("MainFrame.OpenFile.Error"),
                                             JOptionPane.ERROR_MESSAGE);

               return;
            } catch (XMLUselessNodeException excep) {
               JOptionPane.showMessageDialog(MainFrame.this,
                                             messages.getString("MainFrame.OpenFile.UselessNodeException.Text1") + " '" + excep.getXMLNodeId() + "' " + messages.getString("MainFrame.OpenFile.UselessNodeException.Text2"),
                                             messages.getString("MainFrame.OpenFile.Error"),
                                             JOptionPane.ERROR_MESSAGE);

               return;
            } catch (XMLModelReaderWriterException excep) {
               JOptionPane.showMessageDialog(MainFrame.this,
                                             messages.getString("MainFrame.OpenFile.XMLModelReaderWriterException.Text") + " " + excep.getException().getMessage(),
                                             messages.getString("MainFrame.OpenFile.Error"),
                                             JOptionPane.ERROR_MESSAGE);
               
               return;
            }
            
            // opening successful
            xmlFile = file;
            
            scrollPane = new JScrollPane(graph);
            contentPanel.removeAll();
            contentPanel.add(scrollPane, BorderLayout.CENTER);
            getContentPane().validate();

            fileName = xmlFile.getAbsolutePath();
            setTitle(createTitle(graph.getModelName(), false));
            
            newAction.setEnabled(false);
            openAction.setEnabled(false);
            closeAction.setEnabled(true);
            saveAction.setEnabled(false);
            saveAsAction.setEnabled(true);
            
            newAuxiliaryNodeAction.setEnabled(true);
            newConstantNodeAction.setEnabled(true);
            newLevelNodeAction.setEnabled(true);
            newRateNodeAction.setEnabled(true);
            newSourceSinkNodeAction.setEnabled(true);
            toggleAddFlowAction.setEnabled(true);
            changeModelNameAction.setEnabled(true);
            executeModelAction.setEnabled(true);
            
            zoomStandardAction.setEnabled(true);
            zoomInAction.setEnabled(true);
            zoomOutAction.setEnabled(true);
         }
      }
   }
   
   private class CloseAction extends AbstractAction {
      
      private static final long serialVersionUID = 1L;
      
      public CloseAction(String name) {
         super(name);
      }
      
      public void actionPerformed(ActionEvent e) {
         if (graphModified) {
            Object[] options = { messages.getString("MainFrame.Yes"), messages.getString("MainFrame.No") };
            int selectedOption = JOptionPane.showOptionDialog(MainFrame.this,
                                                              messages.getString("MainFrame.ConfirmClosing.Message"),
                                                              messages.getString("MainFrame.ConfirmClosing.Title"),
                                                              JOptionPane.YES_NO_OPTION,
                                                              JOptionPane.QUESTION_MESSAGE,
                                                              null, // don't use a custom Icon
                                                              options,
                                                              options[1]); // default button title
            
            if (selectedOption == 1) {
               // do not close graph
               return;
            }
         }
         
         if (((ToggleAddFlowAction)toggleAddFlowAction).isInAddFlowMode()) {
            // first leave "add flow mode" -> then close model
            toggleAddFlowAction.actionPerformed(null);            
         }
         
         contentPanel.removeAll();
         contentPanel.add(new JPanel(), BorderLayout.CENTER);
         getContentPane().validate();
         
         fileName = null;
         setTitle(createTitle(null, false));
         
         graph = null;
         graphModified = false;
         xmlFile = null;
         scrollPane = null;
         tabbedPane = null;
         
         // reset file chooser (otherwise, old selected file is pre-selected and could be overwritten!)
         File currentDirectory = fileChooser.getCurrentDirectory();
         fileChooser = new JFileChooser();
         fileChooser.setFileFilter(new XMLFileFilter(start));
         fileChooser.setCurrentDirectory(currentDirectory);
         
         newAction.setEnabled(true);
         openAction.setEnabled(true);
         closeAction.setEnabled(false);
         saveAction.setEnabled(false);
         saveAsAction.setEnabled(false);
         
         newAuxiliaryNodeAction.setEnabled(false);
         newConstantNodeAction.setEnabled(false);
         newLevelNodeAction.setEnabled(false);
         newRateNodeAction.setEnabled(false);
         newSourceSinkNodeAction.setEnabled(false);
         toggleAddFlowAction.setEnabled(false);
         addFlowModeCheckBoxMenuItem.setState(false);
         changeModelNameAction.setEnabled(false);
         executeModelAction.setEnabled(false);
         
         zoomStandardAction.setEnabled(false);
         zoomInAction.setEnabled(false);
         zoomOutAction.setEnabled(false);
      }
   }
   
   private class SaveAction extends AbstractAction {
      
      private static final long serialVersionUID = 1L;
      
      public SaveAction(String name, Icon icon, String toolTipText) {
         super(name, icon);
         
         putValue(Action.SHORT_DESCRIPTION, toolTipText);
      }
      
      public void actionPerformed(ActionEvent e) {
         File file = xmlFile;
         if (file == null) {
            int returnVal = fileChooser.showSaveDialog(MainFrame.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
               // file was selected and 'OK' was pressed
               file = fileChooser.getSelectedFile();
               
               // file name should end with ".xml"
               if (!file.getName().toLowerCase().endsWith(".xml")) {
                  file = new File(file.getAbsolutePath() + ".xml");
               }
               
               // check if existing file should be overwritten -> ask for confirmation!
               if (file.exists()) {
                  Object[] options = { messages.getString("MainFrame.Yes"), messages.getString("MainFrame.No") };
                  int selectedOption = JOptionPane.showOptionDialog(MainFrame.this,
                                                                    messages.getString("MainFrame.ConfirmOverwriting.Message"),
                                                                    messages.getString("MainFrame.ConfirmOverwriting.Title"),
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
         }
         
         try {
            graph.storeToXML(file.getAbsolutePath());
         } catch (AuxiliaryNodesCycleDependencyException excep) {
            JOptionPane.showMessageDialog(MainFrame.this,
                                          messages.getString("MainFrame.SaveFile.AuxiliaryNodesCycleDependencyException.Text"),
                                          messages.getString("MainFrame.SaveFile.Error"),
                                          JOptionPane.ERROR_MESSAGE);

            return;
         } catch (NoFormulaException excep) {
            JOptionPane.showMessageDialog(MainFrame.this,
                                          messages.getString("MainFrame.SaveFile.NoFormulaException.Text1") + " '" + excep.getNodeWithourFormula().getNodeName() + "' " + messages.getString("MainFrame.SaveFile.NoFormulaException.Text2"),
                                          messages.getString("MainFrame.SaveFile.Error"),
                                          JOptionPane.ERROR_MESSAGE);

            return;
         } catch (NoLevelNodeException excep) {
            JOptionPane.showMessageDialog(MainFrame.this,
                                          messages.getString("MainFrame.SaveFile.NoLevelNodeException.Text"),
                                          messages.getString("MainFrame.SaveFile.Error"),
                                          JOptionPane.ERROR_MESSAGE);

            return;
         } catch (RateNodeFlowException excep) {
            JOptionPane.showMessageDialog(MainFrame.this,
                                          messages.getString("MainFrame.SaveFile.RateNodeFlowException.Text1") + " '" + excep.getProblematicRateNode().getNodeName() + "' " + messages.getString("MainFrame.SaveFile.RateNodeFlowException.Text2"),
                                          messages.getString("MainFrame.SaveFile.Error"),
                                          JOptionPane.ERROR_MESSAGE);

            return;
         } catch (UselessNodeException excep ) {
            JOptionPane.showMessageDialog(MainFrame.this,
                                          messages.getString("MainFrame.SaveFile.UselessNodeException.Text1") + " '" + excep.getUselessNode().getNodeName() + "' " + messages.getString("MainFrame.SaveFile.UselessNodeException.Text2"),
                                          messages.getString("MainFrame.SaveFile.Error"),
                                          JOptionPane.ERROR_MESSAGE);

            return;
         } catch (XMLModelReaderWriterException excep) {
            JOptionPane.showMessageDialog(MainFrame.this,
                                          messages.getString("MainFrame.SaveFile.XMLModelReaderWriterException.Text") + " " + excep.getException().getMessage(),
                                          messages.getString("MainFrame.SaveFile.Error"),
                                          JOptionPane.ERROR_MESSAGE);

            return;
         }
         
         // saving was successful
         xmlFile = file;
         graphModified = false;
         saveAction.setEnabled(false);
         
         fileName = xmlFile.getAbsolutePath();
         setTitle(createTitle(graph.getModelName(), graphModified));
      }
   }
   
   private class SaveAsAction extends AbstractAction {
      
      private static final long serialVersionUID = 1L;
      
      public SaveAsAction(String name) {
         super(name);
      }
      
      public void actionPerformed(ActionEvent e) {
         File file = null;
         fileChooser.setDialogTitle(messages.getString("MainFrame.MenuBar.File.SaveAs"));
         int returnVal = fileChooser.showSaveDialog(MainFrame.this);
         if (returnVal == JFileChooser.APPROVE_OPTION) {
            // file was selected and 'OK' was pressed
            file = fileChooser.getSelectedFile();
            
            // file name should end with ".xml"
            if (!file.getName().toLowerCase().endsWith(".xml")) {
               file = new File(file.getAbsolutePath() + ".xml");
            }
            
            // check if existing file should be overwritten -> ask for confirmation!
            if (file.exists()) {
               Object[] options = { messages.getString("MainFrame.Yes"), messages.getString("MainFrame.No") };
               int selectedOption = JOptionPane.showOptionDialog(MainFrame.this,
                                                                 messages.getString("MainFrame.ConfirmOverwriting.Message"),
                                                                 messages.getString("MainFrame.ConfirmOverwriting.Title"),
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
         
         try {
            graph.storeToXML(file.getAbsolutePath());
         } catch (AuxiliaryNodesCycleDependencyException excep) {
            JOptionPane.showMessageDialog(MainFrame.this,
                                          messages.getString("MainFrame.SaveFile.AuxiliaryNodesCycleDependencyException.Text"),
                                          messages.getString("MainFrame.SaveFile.Error"),
                                          JOptionPane.ERROR_MESSAGE);

            return;
         } catch (NoFormulaException excep) {
            JOptionPane.showMessageDialog(MainFrame.this,
                                          messages.getString("MainFrame.SaveFile.NoFormulaException.Text1") + " '" + excep.getNodeWithourFormula().getNodeName() + "' " + messages.getString("MainFrame.SaveFile.NoFormulaException.Text2"),
                                          messages.getString("MainFrame.SaveFile.Error"),
                                          JOptionPane.ERROR_MESSAGE);

            return;
         } catch (NoLevelNodeException excep) {
            JOptionPane.showMessageDialog(MainFrame.this,
                                          messages.getString("MainFrame.SaveFile.NoLevelNodeException.Text"),
                                          messages.getString("MainFrame.SaveFile.Error"),
                                          JOptionPane.ERROR_MESSAGE);

            return;
         } catch (RateNodeFlowException excep) {
            JOptionPane.showMessageDialog(MainFrame.this,
                                          messages.getString("MainFrame.SaveFile.RateNodeFlowException.Text1") + " '" + excep.getProblematicRateNode().getNodeName() + "' " + messages.getString("MainFrame.SaveFile.RateNodeFlowException.Text2"),
                                          messages.getString("MainFrame.SaveFile.Error"),
                                          JOptionPane.ERROR_MESSAGE);

            return;
         } catch (UselessNodeException excep ) {
            JOptionPane.showMessageDialog(MainFrame.this,
                                          messages.getString("MainFrame.SaveFile.UselessNodeException.Text1") + " '" + excep.getUselessNode().getNodeName() + "' " + messages.getString("MainFrame.SaveFile.UselessNodeException.Text2"),
                                          messages.getString("MainFrame.SaveFile.Error"),
                                          JOptionPane.ERROR_MESSAGE);

            return;
         } catch (XMLModelReaderWriterException excep) {
            JOptionPane.showMessageDialog(MainFrame.this,
                                          messages.getString("MainFrame.SaveFile.XMLModelReaderWriterException.Text") + " " + excep.getException().getMessage(),
                                          messages.getString("MainFrame.SaveFile.Error"),
                                          JOptionPane.ERROR_MESSAGE);

            return;
         }
         
         // saving was successful
         xmlFile = file;
         graphModified = false;
         saveAction.setEnabled(false);
         
         fileName = xmlFile.getAbsolutePath();
         setTitle(createTitle(graph.getModelName(), graphModified));
      }
   }
   
   private class NewAuxiliaryNodeAction extends AbstractAction {
      
      private static final long serialVersionUID = 1L;
      
      public NewAuxiliaryNodeAction(String name, Icon icon, String toolTipText) {
         super(name, icon);
         
         putValue(Action.SHORT_DESCRIPTION, toolTipText);
      }
      
      public void actionPerformed(ActionEvent e) {
         String nodeName =
            NodeNameDialog.showNodeNameDialog(start, MainFrame.this,
                                              messages.getString("MainFrame.MenuBar.Edit.NewAuxiliaryNode"),
                                              messages.getString("MainFrame.MenuBar.Edit.NewNodeName"));
         
         if (nodeName != null) {
            graph.createAuxiliaryNodeGraphCell(nodeName, MainFrame.DEFAULT_COORDINATE, MainFrame.DEFAULT_COORDINATE);
         }
      }
   }
   
   private class NewConstantNodeAction extends AbstractAction {
      
      private static final long serialVersionUID = 1L;
      
      public NewConstantNodeAction(String name, Icon icon, String toolTipText) {
         super(name, icon);
         
         putValue(Action.SHORT_DESCRIPTION, toolTipText);
      }
      
      public void actionPerformed(ActionEvent e) {
         NodeNameParameterDialog.NodeNameParameter newNodeNameParameter =
            NodeNameParameterDialog.showNodeNameDialog(start, MainFrame.this,
                                                       messages.getString("MainFrame.MenuBar.Edit.NewConstantNode"),
                                                       messages.getString("MainFrame.MenuBar.Edit.NewNodeName"),
                                                       messages.getString("MainFrame.MenuBar.Edit.NewNodeParameter.ConstantNode"),
                                                       ConstantNode.MIN_CONSTANT,
                                                       ConstantNode.MAX_CONSTANT);
         if (newNodeNameParameter != null) {
            graph.createConstantNodeGraphCell(newNodeNameParameter.getNodeName(),
                                              newNodeNameParameter.getNodeParameter(),
                                              MainFrame.DEFAULT_COORDINATE, MainFrame.DEFAULT_COORDINATE);
         }
      }
   }
   
   private class NewLevelNodeAction extends AbstractAction {
      
      private static final long serialVersionUID = 1L;
      
      public NewLevelNodeAction(String name, Icon icon, String toolTipText) {
         super(name, icon);
         
         putValue(Action.SHORT_DESCRIPTION, toolTipText);
      }
      
      public void actionPerformed(ActionEvent e) {
         NodeNameParameterDialog.NodeNameParameter newNodeNameParameter =
            NodeNameParameterDialog.showNodeNameDialog(start, MainFrame.this,
                                                       messages.getString("MainFrame.MenuBar.Edit.NewLevelNode"),
                                                       messages.getString("MainFrame.MenuBar.Edit.NewNodeName"),
                                                       messages.getString("MainFrame.MenuBar.Edit.NewNodeParameter.LevelNode"),
                                                       LevelNode.MIN_START_VALUE,
                                                       LevelNode.MAX_START_VALUE);
         if (newNodeNameParameter != null) {
            graph.createLevelNodeGraphCell(newNodeNameParameter.getNodeName(),
                                           newNodeNameParameter.getNodeParameter(),
                                           MainFrame.DEFAULT_COORDINATE, MainFrame.DEFAULT_COORDINATE);
         }
      }
   }
   
   private class NewRateNodeAction extends AbstractAction {
      
      private static final long serialVersionUID = 1L;
      
      public NewRateNodeAction(String name, Icon icon, String toolTipText) {
         super(name, icon);
         
         putValue(Action.SHORT_DESCRIPTION, toolTipText);
      }
      
      public void actionPerformed(ActionEvent e) {
         String nodeName =
            NodeNameDialog.showNodeNameDialog(start, MainFrame.this,
                                              messages.getString("MainFrame.MenuBar.Edit.NewRateNode"),
                                              messages.getString("MainFrame.MenuBar.Edit.NewNodeName"));
         
         if (nodeName != null) {
            graph.createRateNodeGraphCell(nodeName, MainFrame.DEFAULT_COORDINATE, MainFrame.DEFAULT_COORDINATE);
         }
      }
   }
   
   private class NewSourceSinkNodeAction extends AbstractAction {
      
      private static final long serialVersionUID = 1L;
      
      public NewSourceSinkNodeAction(String name, Icon icon, String toolTipText) {
         super(name, icon);
         
         putValue(Action.SHORT_DESCRIPTION, toolTipText);
      }
      
      public void actionPerformed(ActionEvent e) {
         graph.createSourceSinkNodeGraphCell(MainFrame.DEFAULT_COORDINATE, MainFrame.DEFAULT_COORDINATE);
      }
   }
   
   private class ToggleAddFlowAction extends AbstractAction {
      
      private static final long serialVersionUID = 1L;
      
      private boolean inAddFlowMode;
      
      public ToggleAddFlowAction(String name, Icon icon, String toolTipText) {
         super(name, icon);
         
         putValue(Action.SHORT_DESCRIPTION, toolTipText);
         
         inAddFlowMode = false;
      }
      
      public boolean isInAddFlowMode() {
         return inAddFlowMode;
      }
      
      public void actionPerformed(ActionEvent e) {
         // toggle ports visibility
         graph.setPortsVisible(!graph.isPortsVisible());
         inAddFlowMode = !inAddFlowMode;
         if (inAddFlowMode) {
            putValue(Action.SMALL_ICON, new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_LEAVE_ADD_FLOW_MODE_ICON)));
            putValue(Action.NAME, messages.getString("MainFrame.MenuBar.Edit.ToggleAddFlowMode.LeaveAddFlowMode"));
            putValue(Action.SHORT_DESCRIPTION, messages.getString("MainFrame.MenuBar.Edit.ToggleAddFlowMode.LeaveAddFlowMode"));
            addFlowModeCheckBoxMenuItem.setToolTipText(null);
         } else {
            // not in "add flow modus"
            putValue(Action.SMALL_ICON, new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_ENTER_ADD_FLOW_MODE_ICON)));
            putValue(Action.NAME, messages.getString("MainFrame.MenuBar.Edit.ToggleAddFlowMode.EnterAddFlowMode"));
            putValue(Action.SHORT_DESCRIPTION, messages.getString("MainFrame.MenuBar.Edit.ToggleAddFlowMode.EnterAddFlowMode"));
            addFlowModeCheckBoxMenuItem.setToolTipText(null);
         }
      }
   }
   
   private class ChangeModelNameAction extends AbstractAction {
      
      private static final long serialVersionUID = 1L;
      
      public ChangeModelNameAction(String name, Icon icon, String toolTipText) {
         super(name, icon);
         
         putValue(Action.SHORT_DESCRIPTION, toolTipText);
      }
      
      public void actionPerformed(ActionEvent e) {
         String newModelName =
            ModelNameDialog.showModelNameDialog(start, MainFrame.this,
                                                messages.getString("MainFrame.MenuBar.Edit.ChangeModelName"),
                                                messages.getString("MainFrame.MenuBar.Edit.NewModelName"),
                                                graph.getModelName());
         
         if (newModelName != null) {
            graph.setModelName(newModelName);
            
            setTitle(createTitle(graph.getModelName(), graphModified));
         }
      }
   }
   
   private class ExecuteModelAction extends AbstractAction {
      
      private static final long serialVersionUID = 1L;
      
      public ExecuteModelAction(String name, Icon icon, String toolTipText) {
         super(name, icon);
         
         putValue(Action.SHORT_DESCRIPTION, toolTipText);
      }
      
      public void actionPerformed(ActionEvent e) {
         try {
            graph.validateModelAndSetUnchangeable();
         } catch (AuxiliaryNodesCycleDependencyException excep) {
            JOptionPane.showMessageDialog(MainFrame.this,
                                          messages.getString("MainFrame.MenuBar.Edit.ExecuteModel.AuxiliaryNodesCycleDependencyException.Text"),
                                          messages.getString("MainFrame.MenuBar.Edit.ExecuteModel.Error"),
                                          JOptionPane.ERROR_MESSAGE);

            return;
         } catch (NoFormulaException excep) {
            JOptionPane.showMessageDialog(MainFrame.this,
                                          messages.getString("MainFrame.MenuBar.Edit.ExecuteModel.NoFormulaException.Text1") + " '" + excep.getNodeWithourFormula().getNodeName() + "' " + messages.getString("MainFrame.SaveFile.NoFormulaException.Text2"),
                                          messages.getString("MainFrame.MenuBar.Edit.ExecuteModel.Error"),
                                          JOptionPane.ERROR_MESSAGE);

            return;
         } catch (NoLevelNodeException excep) {
            JOptionPane.showMessageDialog(MainFrame.this,
                                          messages.getString("MainFrame.MenuBar.Edit.ExecuteModel.NoLevelNodeException.Text"),
                                          messages.getString("MainFrame.MenuBar.Edit.ExecuteModel.Error"),
                                          JOptionPane.ERROR_MESSAGE);

            return;
         } catch (RateNodeFlowException excep) {
            JOptionPane.showMessageDialog(MainFrame.this,
                                          messages.getString("MainFrame.MenuBar.Edit.ExecuteModel.RateNodeFlowException.Text1") + " '" + excep.getProblematicRateNode().getNodeName() + "' " + messages.getString("MainFrame.SaveFile.RateNodeFlowException.Text2"),
                                          messages.getString("MainFrame.MenuBar.Edit.ExecuteModel.Error"),
                                          JOptionPane.ERROR_MESSAGE);

            return;
         } catch (UselessNodeException excep ) {
            if (excep.getUselessNode() instanceof SourceSinkNode) {
               JOptionPane.showMessageDialog(MainFrame.this,
                     messages.getString("MainFrame.MenuBar.Edit.ExecuteModel.UselessNodeException.SourceSinkNodeText"),
                     messages.getString("MainFrame.MenuBar.Edit.ExecuteModel.Error"),
                     JOptionPane.ERROR_MESSAGE);
            } else { 
               JOptionPane.showMessageDialog(MainFrame.this,
                                             messages.getString("MainFrame.MenuBar.Edit.ExecuteModel.UselessNodeException.Text1") + " '" + excep.getUselessNode().getNodeName() + "' " + messages.getString("MainFrame.SaveFile.UselessNodeException.Text2"),
                                             messages.getString("MainFrame.MenuBar.Edit.ExecuteModel.Error"),
                                             JOptionPane.ERROR_MESSAGE);
            }

            return;
         }
         
         // successful
         newAuxiliaryNodeAction.setEnabled(false);
         newConstantNodeAction.setEnabled(false);
         newLevelNodeAction.setEnabled(false);
         newRateNodeAction.setEnabled(false);
         newSourceSinkNodeAction.setEnabled(false);
         toggleAddFlowAction.setEnabled(false);
         changeModelNameAction.setEnabled(false);
         executeModelAction.setEnabled(false);
         
         tabbedPane = new JTabbedPane();
         tabbedPane.addTab(messages.getString("MainFrame.TabbedPane.Model"), scrollPane);
         ModelExecutionChartPanel chartPanel = graph.getChartPanel();
         tabbedPane.addTab(messages.getString("MainFrame.TabbedPane.Chart"), chartPanel);
         ExportPanel exportPanel = graph.getExportPanel();
         tabbedPane.addTab(messages.getString("MainFrame.TabbedPane.Export"), exportPanel);
         tabbedPane.addChangeListener(MainFrame.this);
         tabbedPane.setSelectedIndex(1);
         
         MainFrame.this.getRootPane().setDefaultButton(chartPanel.getExecutionButton());
         
         contentPanel.removeAll();
         contentPanel.add(tabbedPane);
         MainFrame.this.getContentPane().validate();
      }
   }
   
   private class ZoomStandardAction extends AbstractAction {
      
      private static final long serialVersionUID = 1L;
      
      public ZoomStandardAction(String name, Icon icon, String toolTipText) {
         super(name, icon);
         
         putValue(Action.SHORT_DESCRIPTION, toolTipText);
      }
      
      public void actionPerformed(ActionEvent e) {
         graph.setScale(1.0);
         
         setTitle(createTitle(graph.getModelName(), graphModified));
      }
   }
   
   private class ZoomInAction extends AbstractAction {
      
      private static final long serialVersionUID = 1L;
      
      public ZoomInAction(String name, Icon icon, String toolTipText) {
         super(name, icon);
         
         putValue(Action.SHORT_DESCRIPTION, toolTipText);
      }
      
      public void actionPerformed(ActionEvent e) {
         graph.setScale(2 * graph.getScale());
         
         setTitle(createTitle(graph.getModelName(), graphModified));
      }
   }
   
   private class ZoomOutAction extends AbstractAction {
      
      private static final long serialVersionUID = 1L;
      
      public ZoomOutAction(String name, Icon icon, String toolTipText) {
         super(name, icon);
         
         putValue(Action.SHORT_DESCRIPTION, toolTipText);
      }
      
      public void actionPerformed(ActionEvent e) {
         graph.setScale(graph.getScale() / 2);
         
         setTitle(createTitle(graph.getModelName(), graphModified));
      }
   }
}