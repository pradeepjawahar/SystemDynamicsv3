package de.uka.aifb.com.systemDynamics;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import java.io.File;
import java.io.FileWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import com.csvreader.CsvReader;
import java.io.*;

public class PostProcess{
 
 public static void postProcess(int nRounds,String modelName, String outputName, int outputRun) throws Exception {
 
  /*build hashmap of lastline of file and header*/
  System.out.println("Preparing model for next run..generating model for next cycle");
  CsvReader modelOutput = new CsvReader(outputName,';');
  modelOutput.readHeaders();
  modelOutput.getCurrentRecord();
  modelOutput.readRecord();
  int nHeader = modelOutput.getColumnCount();
  String headers [] = new String[nHeader];
  
  for(int index = 0; index < nHeader ; index++)
  {
	  headers[index] = modelOutput.get(index);
  }
  int index = 0;
  while(index!=nRounds)
  {
	  modelOutput.readRecord();
	  
	  index++;
  }
  
  //Get Variables
  HashMap<String,String> map = new HashMap<String,String>();
  modelOutput.readRecord();
  long line = modelOutput.getCurrentRecord();
  for(index = 0; index < nHeader ; index++)
  {
	  map.put(headers[index], modelOutput.get(index));
  }
 
  //
  String filename = modelName;
  File fXmlFile = new File(filename);
  DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
  DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
  Document doc = dBuilder.parse(fXmlFile);
  doc.getDocumentElement().normalize();
 // System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
  
  NodeList nList = doc.getElementsByTagName("LevelNode");
  for (int temp = 0; temp < nList.getLength(); temp++) {   	 
        Node nNode = nList.item(temp);	    
        if (nNode.getNodeType() == Node.ELEMENT_NODE) {
		setAttributeValue(map, nNode);
	}
  }
  transformXML(doc, getFileName(modelName,outputRun));   
 }
 
 
 /**
 * @param modelName
 * @param outputRun
 * @return
 */
private static String getFileName(String modelName, int outputRun) {
	 
	 int index = modelName.lastIndexOf('_');
	 String file = modelName.substring(0,index);
	 String newFilename;
	 if(outputRun==0)
		 newFilename = file + "_1.xml";
	 else
		 newFilename = file + "_" + String.valueOf((2*outputRun)-1) + ".xml";
	 return newFilename;
}

/**
 * @param hiring_map
 * @param nNode
 */
private static void setAttributeValue(HashMap<String,String> hiring_map, Node nNode) {
		    
		 NamedNodeMap attrs = nNode.getAttributes();
	
	      for (int i = 0; i < attrs.getLength(); i++) {
	        Attr attribute = (Attr) attrs.item(i);
		
	        if(attribute.getName().equals("name") && ( hiring_map.get(attribute.getValue()) != null )  )
	        {
		//	System.out.println(attribute.getValue());	        	
			Attr tempAttr = (Attr) attrs.item(i + 1);
	        	tempAttr.setValue(hiring_map.get(attribute.getValue()));
	        }
	      }
		     
 }// setAttribute Value

 /**
 * @param doc
 * @param filename
 * @throws Exception
 */
private static void transformXML(Document doc, String filename) throws Exception {
		 Transformer transformer = TransformerFactory.newInstance().newTransformer();
		 transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		 //initialize StreamResult with File object to save to file
		 StreamResult result = new StreamResult(new StringWriter());
		 DOMSource source = new DOMSource(doc);
		 transformer.transform(source, result);

		 String xmlString = result.getWriter().toString();
		 //System.out.println(xmlString);
		 FileWriter writer = new FileWriter(filename);
		 writer.write(xmlString);
		 writer.close();

		
	}// End of transform XML
 
 /**
 * @param inputFile
 * @param outputFileName
 * @param nRounds
 * @param phase
 * @throws Exception
 */
public static void writeInputPhase(String inputFile, String outputFileName, int nRounds, int phase) throws Exception
 {
	 //Get the input file in a hash map\
	 HashMap<String,String>  clist = CommandLineHelper.convertCl(inputFile);
	
	 Iterator<Entry<String, String>> mapIterator = clist.entrySet().iterator();
	
	 CsvReader modelOutput = new CsvReader(outputFileName,';');
	  modelOutput.skipLine();
	  modelOutput.readHeaders();
	  int index = 0;
	  while(index!=nRounds)
	  {
		  modelOutput.readRecord();
		  index++;
	  }
	  BufferedWriter writer= new BufferedWriter(new FileWriter("input_" +phase +".txt"));
	  
	  while(mapIterator.hasNext())
	 {
		  Map.Entry pairs = mapIterator.next();
		  if(modelOutput.get((String)pairs.getKey())!="")
			  writer.write(pairs.getKey() + "," + modelOutput.get((String)pairs.getKey()));
		  else
			  writer.write(pairs.getKey() + "," + pairs.getValue());
		  writer.newLine();
	 } 
	  writer.close();
 }
 
 /**
 * @author pradeep
 * @Description - Writes the final vales of specified level nodes to the 
 * input files of next phase
 * @param outputFileName 
 * @param nRounds
 * @param phase
 * @throws Exception
 */
public static void writeInput(String outputFileName, int nRounds, int phase) throws Exception
 {
	 CsvReader modelOutput = new CsvReader(outputFileName,';');
	  modelOutput.skipLine();
	  modelOutput.readHeaders();
	  int index = 0;
	  while(index!=nRounds)
	  {
		  modelOutput.readRecord();
		  index++;
	  }
	  phase = phase + 1;
	  BufferedWriter writer= new BufferedWriter(new FileWriter("input_" +phase +".txt"));
	  phase--;
	  if(phase==5)
	  {
		  writer.write("Range_Target," + modelOutput.get("Range_Target"));
		  writer.newLine();
		  writer.write("Avg_Fleet_Range,"+ modelOutput.get("Avg_Fleet_Range"));
		  writer.newLine();
		  writer.write("EML_Integration," + modelOutput.get("EML_Integration"));
		  writer.newLine();
		  writer.write("Affordability," + modelOutput.get("Affordability"));
	  }
	  else
	  {
		  writer.write("Range_Target," + modelOutput.get("Range_Target"));
		  writer.newLine();
		  if(phase==2)
			  writer.write("Aero_Eff,13.0");
		  else
			  writer.write("Aero_Eff," + modelOutput.get("Aero_Eff"));
		  writer.newLine();
		  writer.write("Weight_Ratio," + modelOutput.get("Weight_Ratio"));
		  writer.newLine();
		  writer.write("Range," + modelOutput.get("Range"));
		  writer.newLine();
		  writer.write("SW_Errors_P1_Detected_S2," + modelOutput.get("SW_Errors_P1_Detected_S2"));
		  writer.newLine();
		  writer.write("SW_Errors_P2_Detected_S2," + modelOutput.get("SW_Errors_P2_Detected_S2"));
		  writer.newLine();
		  writer.write("SW_Errors_P1_Undetected_S2," + modelOutput.get("SW_Errors_P1_Undetected_S2"));
		  writer.newLine();
		  writer.write("SW_Errors_P1_Undetected_S2," + modelOutput.get("SW_Errors_P1_Undetected_S2"));
		  writer.newLine();
	  }
	  writer.close();
 }
  
}
