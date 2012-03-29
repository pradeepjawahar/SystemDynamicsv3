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

import javax.swing.table.DefaultTableModel;

/**
 * This class implements a table model for a non editable table.
 * 
 * @author Joachim Melcher, Institut AIFB, Universitaet Karlsruhe (TH), Germany
 * @version 1.0
 */
public class MyTableModel extends DefaultTableModel {
   
   private static final long serialVersionUID = 1L;
   
   /**
    * Constructor.
    * 
    * @param data rows with data
    * @param columnNames column names
    */
   public MyTableModel(Object[][] data, Object[] columnNames) {
      super(data, columnNames);
   }
   
   /**
    * Returns <code>false</code> for each cell.
    * 
    * @param row row whose value to be queried
    * @param column whose value to be queried
    * @return <code>false</code> for each cell
    */
   @Override
public boolean isCellEditable(int row, int column) {
      return false;
   }
}