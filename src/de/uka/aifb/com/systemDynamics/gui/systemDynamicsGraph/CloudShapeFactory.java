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

import java.awt.geom.GeneralPath;

/**
 * This class implements a factory for cloud shapes.
 *  
 * @author Joachim Melcher, Institut AIFB, Universitaet Karlsruhe (TH), Germany
 * @version 1.0
 */
public class CloudShapeFactory {
   
   private double width;
   private double height;
   private int borderWidth;
   
   /**
    * Constructor.
    * 
    * @param width width of cloud
    * @param height height of cloud
    * @param borderWidth border width of cloud
    */
   public CloudShapeFactory(double width, double height, int borderWidth) {
      if (width <= 0) {
         throw new IllegalArgumentException("'width' must be positive.");
      }
      if (height <= 0) {
         throw new IllegalArgumentException("'height' must be positive.");
      }
      if (borderWidth < 0) {
         throw new IllegalArgumentException("'borderWidth' must not be negative.");
      }
      
      this.width = width;
      this.height = height;
      this.borderWidth = borderWidth;
   }

   /**
    * Creates cloud shape with this factory's parameters.
    * 
    * @return cloud shape
    */
   public GeneralPath createCloudShape() {
      GeneralPath cloud = new GeneralPath();
      cloud.moveTo( x( 25.53), y( 29.41));
      cloud.curveTo(x( 25.18), y( 27.55), x( 25.00), y( 25.59), x( 25.00), y( 23.53));
      cloud.curveTo(x( 25.00), y(  9.74), x( 33.28), y(  0.00), x( 45.00), y(  0.00));
      cloud.curveTo(x( 54.65), y(  0.00), x( 61.97), y(  6.61), x( 64.25), y( 16.65));
      cloud.curveTo(x( 66.90), y( 13.56), x( 70.63), y( 11.74), x( 75.00), y( 11.76));
      cloud.curveTo(x( 83.78), y( 11.76), x( 90.00), y( 19.07), x( 90.00), y( 29.41));
      cloud.curveTo(x( 90.00), y( 32.31), x( 89.51), y( 34.96), x( 88.61), y( 37.29));
      cloud.curveTo(x( 95.57), y( 40.82), x(100.00), y( 48.74), x(100.00), y( 58.82));
      cloud.curveTo(x(100.00), y( 72.01), x( 92.40), y( 81.52), x( 81.47), y( 82.29));
      cloud.curveTo(x( 76.58), y( 93.19), x( 66.99), y(100.00), x( 55.00), y(100.00));
      cloud.curveTo(x( 45.01), y(100.00), x( 36.69), y( 95.27), x( 31.36), y( 87.41));
      cloud.curveTo(x( 29.35), y( 87.95), x( 27.22), y( 88.24), x( 25.00), y( 88.24));
      cloud.curveTo(x( 10.35), y( 88.24), x(  0.00), y( 76.05), x(  0.00), y( 58.82));
      cloud.curveTo(x(  0.00), y( 41.59), x( 10.35), y( 29.41), x( 25.00), y( 29.41));
      cloud.curveTo(x( 25.18), y( 29.41), x( 25.35), y( 29.41), x( 25.53), y( 29.41));
      cloud.closePath();
      
      return cloud;
   }
   
   /**
    * Computes x value for specified parameter (0% - 100%).
    * <p>
    * The method returns n% of the factory's width parameter.
    * 
    * @param factor factor (0 - 100)
    * @return x value
    */
   private float x(double factor) {
      if (factor < 0 || factor > 100) {
         throw new IllegalArgumentException("'factor' must be in the interval [0, 100].");
      }
      
      float out = (float)(factor * width / 100);
      if (out < borderWidth) {
         out = borderWidth;
      }
      if (out > width - borderWidth) {
         out = (float)(width - borderWidth);
      }
      
      return out;
   }
   
   /**
    * Computes y factor for specified parameter (0% - 100%).
    * <p>
    * The method returns n% of the factory's height parameter.
    * 
    * @param factor factor (0 - 100)
    * @return x value
    */
   private float y(double factor) {
      if (factor < 0 || factor > 100) {
         throw new IllegalArgumentException("'factor' must be in the interval [0, 100].");
      }
      
      float out = (float)(factor * height / 100);
      if (out < borderWidth) {
         out = borderWidth;
      }
      if (out > height - borderWidth) {
         out = (float)(height - borderWidth);
      }
      
      return out;
   }
}