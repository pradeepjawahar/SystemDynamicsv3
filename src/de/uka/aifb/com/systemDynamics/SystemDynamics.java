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

package de.uka.aifb.com.systemDynamics;

import de.uka.aifb.com.systemDynamics.gui.MainFrame;
import java.io.*;
import java.util.*;
import javax.swing.UIManager;

/*
 * Changes:
 * ========
 *
 * 2008-01-24: constructor was rewritten: additional code for Spanish GUI
 * 2008-01-24: storeNewLocale was rewritten: additional code for Spanish GUI
 */

/**
 * This class implements the starting point for the System Dynamics application.
 * <p>
 * It initializes the internationalized view.
 * 
 * @author Joachim Melcher, Institut AIFB, Universitaet Karlsruhe (TH), Germany
 * @version 1.1
 */
public class SystemDynamics {
   private static final String RESOURCE_BUNDLE = "messages";
   private static final String SYSTEM_DYNAMICS_PROPERTIES_FILE = "systemDynamics.properties";
   
   private Locale locale;
   private ResourceBundle messages;
   
   /**
    * Constructor.
    */
   public SystemDynamics() {
      // (1) set locale to standard
      locale = Locale.US;
      
      // (2) try to load locale from properties file
      Properties properties = new Properties();
      try {
         properties.load(new FileInputStream(SYSTEM_DYNAMICS_PROPERTIES_FILE));
      } catch (IOException e) {
         // do nothing
      }
      String localeString = properties.getProperty("locale");
      if (localeString != null) {
         if (localeString.equals("de_DE")) {
            locale = Locale.GERMANY;
         }
         if (localeString.equals("en_US")) {
            locale = Locale.US;
         }
         if (localeString.equals("es_ES")) {
            locale = new Locale("es", "ES");
         }
      }
      
      // sets default locale to this application's locale (otherwise system locale is used)
      // without this, e.g. open or save file dialogs are displayed using another language as the
      // rest of the application
      Locale.setDefault(locale);
      
      messages = ResourceBundle.getBundle(RESOURCE_BUNDLE, locale);
   }
   
   /**
    * Gets the current locale of the application.
    * 
    * @return current locale
    */
   public Locale getLocale() {
      return locale;
   }
   
   /**
    * Stores the new locale of the application into preperties file but does not change GUI language
    * before restart.
    * 
    * @param newLocale new locale
    */
   public void storeNewLocale(Locale newLocale) {
      String lineSeparator = System.getProperty("line.separator");
      if (newLocale == null) {
         throw new IllegalArgumentException("'newLocale' must not be null.");
      }
      
      // store to properties file
      Properties properties = new Properties();
      properties.setProperty("locale", newLocale.getLanguage() + "_" + newLocale.getCountry());
      try {
         String comment = " GUI language for application SystemDynamics" + lineSeparator
                        + "# Key 'locale' can be either 'en_US' (US English), 'de_DE' (German) or 'es_ES' (Spanish)" + lineSeparator
                        + "#";
         properties.store(new FileOutputStream(SYSTEM_DYNAMICS_PROPERTIES_FILE), comment);
      } catch (IOException e) {
         // do nothing
      }
   }
   
   /**
    * Gets the internationlized messages for the current locale.
    * 
    * @return messages for current locale
    */
   public ResourceBundle getMessages() {
      return messages;
   }
   
   /**
    * <Code>Main</code> method.
    * 
    * @param args command line arguments (not used by this method)
    */
   public static void main(String[] args) {
      SystemDynamics start = new SystemDynamics();
      
      // set system dependent look & feel
      try {
         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      } catch (Exception e) {
         e.printStackTrace();
      }
      
      new MainFrame(start);
   }
}