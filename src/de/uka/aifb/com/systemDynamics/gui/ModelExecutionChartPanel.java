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
import de.uka.aifb.com.systemDynamics.model.*;
import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import org.jfree.chart.*;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.RangeType;
import org.jfree.data.xy.*;
import org.jfree.ui.*;

/*
 * Changes:
 * ========
 *
 * 2008-01-24: createPanel was rewritten: table column names internationalization
 */

/**
 * This class implements a panel for drawing the charts of the model execution.
 * 
 * @author Joachim Melcher, Institut AIFB, Universitaet Karlsruhe (TH), Germany
 * @version 1.2
 */
public class ModelExecutionChartPanel extends JPanel implements FocusListener {
  
   private static final long serialVersionUID = 1L;
   
   private Locale locale;
   private ResourceBundle messages;
   
   private NumberFormat integerNumberFormatter;
   
   private Model model;
   private LevelNode[] levelNodes;
   
   private XYSeries[] xySeriesArray;
   private JFreeChart chart;
   private int nextRound;
   
   private JButton axesButton;
   private JButton executionButton;
   
   /**
    * Constructor.
    * 
    * @param start {@link de.uka.aifb.com.systemDynamics.SystemDynamics} instance
    * @param model {@link de.uka.aifb.com.systemDynamics.model.Model} instance
    */
   public ModelExecutionChartPanel(SystemDynamics start, Model model) {
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
      
      createPanel();
   }
   
   /**
    * Gets the execution button.
    * 
    * @return execution button
    */
   public JButton getExecutionButton() {
      return executionButton;
   }
   
   /**
    * Creates panel.
    */
   private void createPanel() {
      setLayout(new BorderLayout());
      
      // CENTER: chart
      ChartPanel chartPanel = new ChartPanel(createChart());
      // no context menu
      chartPanel.setPopupMenu(null);
      // not zoomable
      chartPanel.setMouseZoomable(false);
      add(chartPanel, BorderLayout.CENTER);
      
      // LINE_END: series table
      JPanel tablePanel = new JPanel(new GridBagLayout());
      String[] columnNames = { messages.getString("ModelExecutionChartPanel.Table.ColumnNames.ExtraAxis"),
                               messages.getString("ModelExecutionChartPanel.Table.ColumnNames.LevelNode") };
      final MyTableModel tableModel = new MyTableModel(columnNames, xySeriesArray.length);
      for (int i = 0; i < xySeriesArray.length; i++) {
         tableModel.addEntry((String)xySeriesArray[i].getKey());
      }
      JTable table = new JTable(tableModel);
      table.setRowSelectionAllowed(false);
      JScrollPane tableScrollPane = new JScrollPane(table);
      int width = (int)Math.min(300, table.getPreferredSize().getWidth());
      int height = (int)Math.min(200, table.getPreferredSize().getHeight());
      tableScrollPane.getViewport().setPreferredSize(new Dimension(width, height));
      tableScrollPane.setMaximumSize(tableScrollPane.getViewport().getPreferredSize());
      axesButton = new JButton(messages.getString("ModelExecutionChartPanel.AxesButton.Text"));
      axesButton.setToolTipText(messages.getString("ModelExecutionChartPanel.AxesButton.ToolTipText"));
      axesButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            // create XYSeriesCollections (and renderer)
            XYSeriesCollection standardData = new XYSeriesCollection();
            XYLineAndShapeRenderer standardRenderer = new XYLineAndShapeRenderer(true, false);
            LinkedList<XYSeriesCollection> extraDataList = new LinkedList<XYSeriesCollection>();
            LinkedList<XYLineAndShapeRenderer> extraRendererList = new LinkedList<XYLineAndShapeRenderer>();
            for (int i = 0; i < tableModel.getRowCount(); i++) {
               if (tableModel.getValueAt(i, 0).equals(Boolean.FALSE)) {
                  standardData.addSeries(xySeriesArray[i]);
                  standardRenderer.setSeriesPaint(standardData.getSeriesCount() - 1, DefaultDrawingSupplier.DEFAULT_PAINT_SEQUENCE[i % DefaultDrawingSupplier.DEFAULT_PAINT_SEQUENCE.length]);
               } else {
                  // extra axis
                  XYSeriesCollection extraData = new XYSeriesCollection();
                  extraData.addSeries(xySeriesArray[i]);
                  extraDataList.add(extraData);
                  XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
                  extraRendererList.add(renderer);
                  renderer.setSeriesPaint(0, DefaultDrawingSupplier.DEFAULT_PAINT_SEQUENCE[i % DefaultDrawingSupplier.DEFAULT_PAINT_SEQUENCE.length]);
               }
            }
            LinkedList<XYSeriesCollection> dataList = new LinkedList<XYSeriesCollection>();
            LinkedList<XYLineAndShapeRenderer> rendererList = new LinkedList<XYLineAndShapeRenderer>();
            if (!standardData.getSeries().isEmpty()) {
               dataList.add(standardData);
               rendererList.add(standardRenderer);
            }
            for (XYSeriesCollection data : extraDataList) {
               dataList.add(data);
            }
            for (XYLineAndShapeRenderer renderer : extraRendererList) {
               rendererList.add(renderer);
            }
            
            // creates axes
            LinkedList<NumberAxis> axesList = new LinkedList<NumberAxis>();
            if (!standardData.getSeries().isEmpty()) {
               NumberAxis axis = new NumberAxis(messages.getString("ModelExecutionChartPanel.Value"));
               axis.setNumberFormatOverride(NumberFormat.getInstance(locale));
               axesList.add(axis);
            }
            for (XYSeriesCollection data : extraDataList) {
               NumberAxis axis = new NumberAxis((String)data.getSeries(0).getKey());
               axis.setNumberFormatOverride(NumberFormat.getInstance(locale));
               axesList.add(axis);
            }
            
            // store data and axes in plot
            XYPlot plot = chart.getXYPlot();
            plot.clearRangeAxes();
            plot.setRangeAxes(axesList.toArray(new NumberAxis[0]));
            for (int i = 0; i < plot.getDatasetCount(); i++) {
               plot.setDataset(i, null);
            }
            int datasetIndex = 0;
            Iterator<XYSeriesCollection> datasetIterator = dataList.iterator();
            Iterator<XYLineAndShapeRenderer> rendererIterator = rendererList.iterator();
            while (datasetIterator.hasNext()) {
               plot.setDataset(datasetIndex, datasetIterator.next());
               plot.setRenderer(datasetIndex, rendererIterator.next());
               datasetIndex++;
            }
            for (int i = 0; i < plot.getDatasetCount(); i++) {
               plot.mapDatasetToRangeAxis(i, i);
            }
         }
      });
      GridBagConstraints c = new GridBagConstraints();
      c.anchor = GridBagConstraints.CENTER;
      c.gridx = 0;
      c.gridy = 0;
      c.insets = new Insets(0, 0, 10, 0);
      tablePanel.add(tableScrollPane, c);
      c.gridx = 0;
      c.gridy = 1;
      tablePanel.add(axesButton, c);
      add(tablePanel, BorderLayout.LINE_END);
      
      // PAGE_END: number of rounds and execution button
      JPanel commandPanel = new JPanel();
      commandPanel.add(new JLabel(messages.getString("ModelExecutionChartPanel.NumberRounds")));
      final JTextField numberRoundsField = new JTextField("1", 5);
      numberRoundsField.addFocusListener(this);
      commandPanel.add(numberRoundsField);
      executionButton = new JButton(messages.getString("ModelExecutionChartPanel.ExecutionButton.Text"));
      executionButton.setToolTipText(messages.getString("ModelExecutionChartPanel.ExecutionButton.ToolTipText"));
      executionButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
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
            
            if (correctNumber) {
               ModelExecutionThread executionThread = new ModelExecutionThread(numberRounds);
               executionThread.start();              
            } else {
               JOptionPane.showMessageDialog(null,
                                             messages.getString("ModelExecutionChartPanel.Error.Message"),
                                             messages.getString("ModelExecutionChartPanel.Error.Title"),
                                             JOptionPane.ERROR_MESSAGE);
            }
         }
      });
      commandPanel.add(executionButton);
      add(commandPanel, BorderLayout.PAGE_END);
   }
   
   /**
    * Creates the XY line chart.
    * 
    * @return XY line chart
    */
   private JFreeChart createChart() {
      levelNodes = new LevelNode[model.getLevelNodes().size()];
      int i = 0;
      for (LevelNode levelNode : model.getLevelNodes()) {
         levelNodes[i++] = levelNode;
      }
      // sort level nodes alphabetically
      Arrays.sort(levelNodes);
      
      xySeriesArray = new XYSeries[levelNodes.length];
      XYSeriesCollection data = new XYSeriesCollection();
      for (i = 0; i < xySeriesArray.length; i++) {
         XYSeries xySeries = new XYSeries(levelNodes[i].getNodeName());
         xySeries.add(0.0, levelNodes[i].getCurrentValue());
         data.addSeries(xySeries);
         xySeriesArray[i] = xySeries;
      }
      nextRound = 1;
      
      chart = ChartFactory.createXYLineChart(null,
                                             messages.getString("ModelExecutionChartPanel.Round"), 
                                             messages.getString("ModelExecutionChartPanel.Value"), 
                                             data,
                                             PlotOrientation.VERTICAL,
                                             true, false, false);
      XYPlot plot = chart.getXYPlot();
      
      // horizontal axis range: 0 ... maximal rounds
      ((NumberAxis)(chart.getXYPlot().getDomainAxis())).setRangeType(RangeType.POSITIVE);
      plot.getDomainAxis().setAutoRangeMinimumSize(20);
      
      // only integer values as labels for horizontal axis
      plot.getDomainAxis().setStandardTickUnits(NumberAxis.createIntegerTickUnits());
      
      // number formatting according to current locale
      ((NumberAxis)(plot.getDomainAxis())).setNumberFormatOverride(NumberFormat.getIntegerInstance(locale));
      ((NumberAxis)(plot.getRangeAxis())).setNumberFormatOverride(NumberFormat.getInstance(locale));
      
      // legend at top position
      chart.getLegend().setPosition(RectangleEdge.TOP);
      
      return chart;
   }
   
////////////////////////////////////////////////////////////////////////////////////////////////////
//                             methods of interface FocusListener
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
    * This inner class implements a table model with a boolean and a String column.
    */
   private class MyTableModel extends AbstractTableModel {
      
      private static final long serialVersionUID = 1L;
      
      private String[] columnNames;
      private Object[][] data;
      private int nextRow;
      
      /**
       * Constructor.
       * 
       * @param columnNames column names
       * @param numberRows number of rows
       */
      private MyTableModel(String[] columnNames, int numberRows) {
         if (columnNames == null) {
            throw new IllegalArgumentException("'columnNames' must not be null.");
         }
         
         this.columnNames = columnNames;
         data = new Object[numberRows][columnNames.length];
      }
      
      /**
       * Adds the specified entry (with boolean value <code>false</close> to the next free row.
       * 
       * @param entryName entry name
       */
      private void addEntry(String entryName) {
         if (entryName == null) {
            throw new IllegalArgumentException("'entryName' must not be null.");
         }
         
         data[nextRow][0] = false;
         data[nextRow][1] = entryName;
         
         nextRow++;
      }

      /**
       * Gets the number of columns.
       * 
       * @return number of columns
       */
      public int getColumnCount() {
         return columnNames.length;
      }

      /**
       * Gets the number of rows.
       * 
       * @return number of rows
       */
      public int getRowCount() {
         return data.length;
      }

      /**
       * Gets the column name of the specified column.
       * 
       * @param col column index
       * @return column name
       */
      @Override
	public String getColumnName(int col) {
         return columnNames[col];
      }

      /**
       * Gets the value stored at the specified position.
       * 
       * @param row row index
       * @param col column index
       * @return stored value
       */
      public Object getValueAt(int row, int col) {
         return data[row][col];
      }
      
      /**
       * Sets the specified value at the given position.
       * 
       * @param value value to store
       * @param row row index
       * @param col column index
       */
      @Override
	public void setValueAt(Object value, int row, int col) {
         if (col == 0) {
            data[row][col] = value;
            fireTableCellUpdated(row, col);
         }
      }

      /**
       * Gets the <code>Class</code> type of the specified column.
       * 
       * @param c column index
       * @return <code>Class</code> type
       */
      @Override
	public Class getColumnClass(int c) {
         if (c < 0 || c > 1) {
            throw new ArrayIndexOutOfBoundsException();
         }
          
         if (c == 0) {
            return Boolean.class;
         } else {
            return String.class;
         }
      }
      
      /**
       * Checks whether the specified cell is editable.
       * 
       * @param row row index
       * @param col column index
       * @return <code>true</code> iff the specified cell is editable
       */
      @Override
	public boolean isCellEditable(int row, int col) {
         return (col == 0);
      }
   }
   
   /**
    * This inner class implements a thread for model execution within the swing GUI.
    */
   private class ModelExecutionThread extends Thread {
      
      private int numberRounds;
      
      /**
       * Constructor.
       * 
       * @param numberRounds number of rounds to execute
       */
      private ModelExecutionThread(int numberRounds) {
         if (numberRounds < 1) {
            throw new IllegalArgumentException("'numberRounds' must be at least 1.");
         }
         
         this.numberRounds = numberRounds;
      }
      
      /**
       * Executes the model for the specified number of rounds. A progress monitor shows progress
       * information.
       */
      @Override
	public void run() {
         NumberFormat numberFormatter = NumberFormat.getIntegerInstance(locale);
         executionButton.setEnabled(false);
         setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
         
         ProgressMonitor progressMonitor = new ProgressMonitor(ModelExecutionChartPanel.this,
                                                               messages.getString("ModelExecutionThread.ProgressMonitor.Text"),
                                                               "",
                                                               0, numberRounds);
         for (int i = 0; i < numberRounds; i++) {
            if (progressMonitor.isCanceled()) {
               // stop execution (i.e. for loop)
               break;
            }
            progressMonitor.setNote(numberFormatter.format(i) + " " + messages.getString("ModelExecutionThread.ProgressMonitor.Note.Text1") + " " + numberFormatter.format(numberRounds) + " " + messages.getString("ModelExecutionThread.ProgressMonitor.Note.Text2"));
            progressMonitor.setProgress(i);
            model.computeNextValues();
            for (int j = 0; j < xySeriesArray.length; j++) {
               xySeriesArray[j].add(nextRound, levelNodes[j].getCurrentValue());
            }
            nextRound++;
         }
         progressMonitor.close();
         
         setCursor(null);
         executionButton.setEnabled(true);
      }
   }
}