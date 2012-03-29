***************************************
*     SystemDynamics: Version 1.3     *
***************************************

12 October 2009

(C)opyright 2007-2009, by Joachim Melcher, Institut AIFB, Karlsruher Institut fuer Technologie (KIT), Germany

e-mail: melcher@users.sourceforge.net

-----------------
1.  INTRODUCTION
-----------------
SystemDynamics is a graphical Java application for modeling, visualization and execution
of System Dynamics models. It runs on the Java 5 Platform (JDK 1.5 or later).

SystemDynamics is licensed under the terms of the GNU General Public License (GPL)
version 2 (or any later version). A copy of the license is included in the distribution.

This ZIP file contains the source code of the application. If you only want to run the
application without any modifications, you can download the file
systemDynamics-1.3-bin.zip from the project's SourceForge page.

Please note that SystemDynamics is distributed WITHOUT ANY WARRANTY; without even the
implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. Please refer
to the license for details.

-------------------
2.  LATEST VERSION
-------------------
The latest version of this application can be obtained from

    http://sourceforge.net/projects/system-dynamics/

If you have any comments, suggestions or bugs to report, please post a
message on this site.

------------------------------
3.  FURTHER PROJECT RESOURCES
------------------------------
You can download the "binary" version of this application and further documentation
from the project page on SourceForge.

-----------------
4.  DEPENDENCIES
-----------------
SystemDynamics has the following dependencies:

(a)  JDK 1.5 or higher - SystemDynamics uses generics which is offered by Java
starting with this version.

(b)  JavaCC - a parser/scanner generator.

JavaCC is licensed under the terms of the Berkeley Software Distribution License.

JavaCC is not part of this distribution because it is not necessary for changing
the source code. You only need it if you want to change the node formula parser.

You can find out more about JavaCC and/or download the latest version from:

    https://javacc.dev.java.net

The node formula parser included with SystemDynamics has been created using JavaCC 4.0.

(c)  JCommon - The runtime jar file and the source code (version 1.0.12) are included
in this source code distribution of SystemDynamics. You can obtain the newest version
of JCommon from:

    http://www.jfree.org/jcommon/

JCommon is licensed under the terms of the GNU Lesser General Public License (LGPL)
version 2.1 (or any later version).

(d)  JFreeChart - The runtime jar file and the source code (version 1.0.9) are
included in this source code distribution of SystemDynamics. You can obtain the
newest version of JFreeChart from:

    http://www.jfree.org/jfreechart/

JFreeChart is licensed under the terms of the GNU Lesser General Public License (LGPL)
version 2.1 (or any later version).

(e)  JGraph - The runtime jar file and the source code (version 5.11.0.1) are
included in this source distribution of SystemDynamics. You can obtain the newest
version of JGraph from:

    http://www.jgraph.com

JGraph is licensed under the terms of the GNU Lesser General Public License (LGPL)
version 2.1.

(f)  JUnit - a unit testing framework.

JUnit is licensed under the terms of the Common Public License.

The JUnit runtime JAR file is not part of this distribution because it is not
necessary for changing the source code. You only need it if you want to
run the JUnit tests included in source code form in this distribution.

You can find out more about JUnit and/or download the latest version from:

    http://www.junit.org

The JUnit tests included with SystemDynamics have been created using JUnit 3.8.1.

(g)  JUnit-addons - version 1.4 or later. JUnit-addons adds some additional
features to the JUnit testing framework.

JUnit-addons is licensed under the terms of the JUnit-addons Software License,
a license based on the Apache Software License.

The JUnit-addons runtime JAR file is not part of this distribution because it
is not necessary for changing the source code. You only need it if you
want to run the JUnit tests included in source code form in this distribution.

You can find out more about JUnit-addons and/or download the latest version
from:

    http://sourceforge.net/projects/junit-addons/

(h)  Silk Icons - The used icons of this set (version 1.3) are included in
this source code distribution of SystemDynamics. You can obtain the newest
version of Silk Icons from:

    http://www.famfamfam.com/lab/icons/silk/

Silk Icons are licensed under the terms of the Creative Commons Attribution 2.5
License (CC-BY) and created by Mark James.

---------------------------------------------
5.  ATTENTION: RESOURCES AND XSD DIRECTORIES
---------------------------------------------
The directories 'resources' and 'xsd' with all their files must be accessable by the
class loader. So if you do not want to store all CLASS files, the resources and
XSD files within a JAR file, you have to copy the 'resources' and 'xsd' directories
within the output directory for the CLASS files.

------------
6.  HISTORY
------------
A list of changes in recent versions:

1.0 :  (23-May-2007)
       - first version released
1.1 :  (18-Sep-2007)
       - library JFreeChart updated to version 1.0.6
       - library JGraph updated to version 5.10.1.5
       - bug fixed: de.uka.aifb.com.systemDynamics.model.UselessNodeException: constructor
         now, the problematic node can also be a source/sink node
       - de.uka.aifb.com.systemDynamics.gui.systemDynamicsGraph.AutomaticGraphLayout
         new class added for automatic graph layout
       - de.uka.aifb.com.systemDynamics.gui.MainFrame.ExecuteModelAction: method 'actionPerformed(ActionEvent)'
         If there is a useless source/sink node, another text is shown.
       - de.uka.aifb.com.systemDynamics.xml.XMLModelReader: method 'readModelFromXML'
         name of root element can now be chosen
       - de.uka.aifb.com.systemDynamics.xml.XMLModelReader: methods 'createModelFromXML(...)' and 'createGraphFromXML(...)'
         XMLUselessNodeException can be caused by a source/sink node
       - de.uka.aifb.com.systemDynamics.xml.XMLModelReader: method 'createGraphFromXML(...)'
         automatic graph layout, if no node positions are stored in the XML file
       - de.uka.aifb.com.systemDynamics.xml.XMLModelWriter: method 'writeDocumentToXMLFile(Document, String)'
         selected root element is searched for (instead of "hard coded" tag)
1.2 :  (15-Feb-2008)
       - library JCommon updated to version 1.0.12
       - library JFreeChart updated to version 1.0.9
       - library JGraph updated to version 5.11.0.1
       - Spanish translation of GUI added (translation by Diego Alberto Godoy)
       - de.uka.aifb.com.systemDynamics.SystemDynamics: constructor
         additional code for Spanish GUI
       - de.uka.aifb.com.systemDynamics.SystemDynamics: method 'storeNewLocale'
         additional code for Spanish GUI
       - de.uka.aifb.com.systemDynamics.gui.MainFrame: method 'createMenuBar'
         additional code for Spanish GUI
       - de.uka.aifb.com.systemDynamics.gui.MainFrame: method 'actionPerformed'
         additional code for Spanish GUI
       - de.uka.aifb.com.systemDynamics.gui.ModelExecutionChartPanel: method 'createPanel'
         table column names internationalization
       - small changes in English and German GUI texts
1.3 :  (13-Oct-2009)
       - model.xsd: typing error in element "AuxiliaryNode2RateNodeDependency" corrected