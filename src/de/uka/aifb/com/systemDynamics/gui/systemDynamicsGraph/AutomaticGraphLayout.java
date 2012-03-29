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

import java.util.LinkedList;

/**
 * This class implements an automatic graph layout using the algorithm described in:
 * 
 * FRUCHTERMAN, Thomas M. J. ; REINGOLD, Edward M.: "Graph Drawing by Force-directed Placement".
 * In: Software - Practice and Experience, vol. 21, no. 11, November 1991, pp. 1129-1164.
 * 
 * @author Joachim Melcher, University of Karlsruhe, AIFB
 * @version 1.1
 */
public class AutomaticGraphLayout {
   
   private static final int NUMBER_ITERATIONS = 100;
   
   private double maxX;
   private double maxY;
   
   private LinkedList<Vertex> vertices;
   private LinkedList<Edge> edges;
   
   private double k;
   
   /**
    * Constructor.
    * 
    * @param maxX maximum x-coordinate used by the layout algorithm
    * @param maxY maximum y-coordinate used by the layout algorithm
    */
   public AutomaticGraphLayout(double maxX, double maxY) {
      if (maxX <= 0) {
         throw new IllegalArgumentException("'maxX' must be positive."); 
      }
      if (maxY <= 0) {
         throw new IllegalArgumentException("'maxY' must be positive."); 
      }
      
      this.maxX = maxX;
      this.maxY = maxY;
      vertices = new LinkedList<Vertex>();
      edges = new LinkedList<Edge>();
   }
   
   /**
    * Creates a vertex.
    * 
    * @return created vertex
    */
   public Vertex createVertex() {
      Vertex vertex = new Vertex();
      vertices.add(vertex);
      return vertex;
   }
   
   /**
    * Creates an edge between the two specified vertices.
    * 
    * @param vertex1 vertex 1
    * @param vertex2 vertex 2
    */
   public void createEdge(Vertex vertex1, Vertex vertex2) {
      if (vertex1 == null) {
         throw new IllegalArgumentException("'vertex1' must not be null.");
      }
      if (vertex2 == null) {
         throw new IllegalArgumentException("'vertex2' must not be null.");
      }
      
      Edge edge = new Edge(vertex1, vertex2);
      edges.add(edge);
   }
   
   /**
    * Does the automatic layout.
    */
   public void doLayout() {
      double area = maxX * maxY;
      k = Math.sqrt(area / vertices.size());
      double initialTemp = maxX / 10;
      double temp = initialTemp;
      
      for (int i = 0; i < NUMBER_ITERATIONS; i++) {
         // (1) calculate repulsive forces
         for (Vertex vertexV : vertices) {
            vertexV.dispX = 0.0;
            vertexV.dispY = 0.0;
            for (Vertex vertexU : vertices) {
               if (vertexV != vertexU) {
                  double distanceX = vertexV.x - vertexU.x;
                  double distanceY = vertexV.y - vertexU.y;
                  double norm = getNorm(distanceX, distanceY);
                  double forceRepulsion = forceRepulsion(norm);
                  
                  if (norm != 0.0) {
                     vertexV.dispX += (distanceX / norm) * forceRepulsion;
                     vertexV.dispY += (distanceY / norm) * forceRepulsion;
                  }
               }
            }
         }
         
         // (2) calculate attractive forces
         for (Edge edge : edges) {
            double distanceX = edge.vertex1.x - edge.vertex2.x;
            double distanceY = edge.vertex1.y - edge.vertex2.y;
            double norm = getNorm(distanceX, distanceY);
            double forceAttraction = forceAttraction(norm);
            
            if (norm != 0.0) {
               edge.vertex1.dispX -= (distanceX / norm) * forceAttraction;
               edge.vertex1.dispY -= (distanceY / norm) * forceAttraction;
               edge.vertex2.dispX += (distanceX / norm) * forceAttraction;
               edge.vertex2.dispY += (distanceY / norm) * forceAttraction;
            }
         }
         
         // (3) calculate now positions
         for (Vertex vertex : vertices) {
            double norm = getNorm(vertex.dispX, vertex.dispY);
            
            if (norm != 0.0) {
               vertex.x += vertex.dispX / norm * Math.min(norm, temp);
               vertex.y += vertex.dispY / norm * Math.min(norm, temp);
            
               vertex.x = Math.min(maxX, Math.max(0, vertex.x));
               vertex.y = Math.min(maxY, Math.max(0, vertex.y));
            }
         }
         
         // (4) reduce temperature
         temp -= initialTemp / NUMBER_ITERATIONS;
      }
   }
   
   /**
    * Computes the attractive force for the specified distance.
    * 
    * @param distance distance
    * @return attractive force
    */
   private double forceAttraction(double distance) {
      if (distance < 0) {
         throw new IllegalArgumentException("'distance' must not be negative.");
      }
      
      return Math.pow(distance, 2) / k;
   }
   
   /**
    * Computes the repulsive force for the specified distance.
    * 
    * @param distance distance
    * @return repulsive force
    */
   private double forceRepulsion(double distance) {
      if (distance < 0) {
         throw new IllegalArgumentException("'distance' must not be negative.");
      }
      
      return Math.pow(k, 2) / distance;
   }
   
   /**
    * Gets the norm of the specified vector.
    * 
    * @param x x-coordinate of the vector
    * @param y y-coordinate of the vector.
    * @return vector norm
    */
   private double getNorm(double x, double y) {
      return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
   }
   
   /////////////////////////////////////////////////////////////////////////////////////////////////
   //                                        inner classes
   /////////////////////////////////////////////////////////////////////////////////////////////////

   public class Vertex {
      private double x;
      private double y;
      private double dispX;
      private double dispY;
      
      private Vertex() {
         x = Math.random() * maxX;
         y = Math.random() * maxY;
         dispX = 0.0;
         dispY = 0.0;
      }
      
      public double getX() {
         return x;
      }
      
      public double getY() {
         return y;
      }
   }
   
   private class Edge {
      private Vertex vertex1;
      private Vertex vertex2;
      
      private Edge(Vertex vertex1, Vertex vertex2) {
         if (vertex1 == null) {
            throw new IllegalArgumentException("'vertex1' must not be null.");
         }
         if (vertex2 == null) {
            throw new IllegalArgumentException("'vertex2' must not be null.");
         }
         
         this.vertex1 = vertex1;
         this.vertex2 = vertex2;
      }
   }
}