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

import com.csvreader.CsvReader;

import java.io.File;
import java.io.FileWriter;
import java.io.StringWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.FileReader;
import java.util.HashMap;
import java.util.StringTokenizer;

public class PreProcess {
	String new_filename = null;

	/**
	 * @param fname
	 * @param run
	 * @throws Exception
	 */
	public void checkRun(String fname, int run) throws Exception {

		// take care of Init Phase
		if (run == 0 || run == -1) {
			StringTokenizer st = new StringTokenizer(fname, ".");
			new_filename = st.nextToken() + "_" + String.valueOf(0) + ".xml";
		} else {
			int index = fname.lastIndexOf('_');
			String file = fname.substring(0, index);
			new_filename = file + "_" + String.valueOf((2 * run)) + ".xml";

		}
	}

	/**
	 * @param nRounds
	 * @param outputName
	 * @return HashMap<String, String>
	 * @throws Exception
	 */
	public HashMap<String, String> phase2PreProcess(
			String outputName) throws Exception {

		CsvReader modelOutput = new CsvReader(outputName, ';');
		modelOutput.readHeaders();
		modelOutput.getCurrentRecord();
		modelOutput.readRecord();
		int nHeader = modelOutput.getColumnCount();
		String headers[] = new String[nHeader];

		for (int index = 0; index < nHeader; index++) {
			headers[index] = modelOutput.get(index);
		}
		int index = 0;
		//Read last run length from Xtime file
		BufferedReader br = new BufferedReader(new FileReader("xTime.txt"));
		int nRounds = Integer.parseInt(br.readLine());
		while (index != nRounds) {
			modelOutput.readRecord();

			index++;
		}

		// Get Variables
		HashMap<String, String> map = new HashMap<String, String>();
		modelOutput.readRecord();
		long line = modelOutput.getCurrentRecord();
		for (index = 0; index < nHeader; index++) {
			map.put(headers[index], modelOutput.get(index));
		}

		return map;
	}

	/**
	 * @param fname
	 * @param clist
	 * @param run
	 * @return
	 * @throws Exception
	 */
	public String preprocess(String fname, HashMap<String, String> clist,
			int run) throws Exception {

		// Check run
		checkRun(fname, run);

		String filename = fname;
		HashMap<String, String> hiring_map = new HashMap<String, String>();

		File fXmlFile = new File(filename);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		doc.getDocumentElement().normalize();

		// System.out.println("Root element :"
		// + doc.getDocumentElement().getNodeName());
		// Preprocess Constant nodes
		NodeList nList = doc.getElementsByTagName("ConstantNode");
		for (int temp = 0; temp < nList.getLength(); temp++) {

			Node nNode = nList.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				setAttributeValue(clist, nNode);
			}
		}
		// Prerocess level nodes
		NodeList lnList = doc.getElementsByTagName("LevelNode");
		for (int temp = 0; temp < lnList.getLength(); temp++) {

			Node nNode = lnList.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				setAttributeValue1(clist, nNode);
			}
		}
		// new_filename = "inter_model.xml";
		transformXML(doc, new_filename);

		return new_filename;
	}

	/**
	 * @param doc
	 * @param filename
	 * @throws Exception
	 */
	private void transformXML(Document doc, String filename) throws Exception {
		Transformer transformer = TransformerFactory.newInstance()
				.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		// initialize StreamResult with File object to save to file
		StreamResult result = new StreamResult(new StringWriter());
		DOMSource source = new DOMSource(doc);
		transformer.transform(source, result);

		String xmlString = result.getWriter().toString();
		// System.out.println(xmlString);
		FileWriter writer = new FileWriter(filename);
		writer.write(xmlString);
		writer.flush();
		writer.close();

	}

	/**
	 * @param cl_map
	 * @param nNode
	 */
	private void setAttributeValue(HashMap<String, String> cl_map, Node nNode) {

		NamedNodeMap attrs = nNode.getAttributes();

		for (int i = 0; i < attrs.getLength(); i++) {
			Attr attribute = (Attr) attrs.item(i);

			if (attribute.getName().equals("name")
					&& (cl_map.get(attribute.getValue()) != null)) {
				// System.out.println(attribute.getValue());
				Attr tempAttr = (Attr) attrs.item(i - 2);
				tempAttr.setValue(cl_map.get(attribute.getValue()));
			}
		}
	}

	/**
	 * @param cl_map
	 * @param nNode
	 */
	private void setAttributeValue1(HashMap<String, String> cl_map, Node nNode) {

		NamedNodeMap attrs = nNode.getAttributes();

		for (int i = 0; i < attrs.getLength(); i++) {
			Attr attribute = (Attr) attrs.item(i);

			if (attribute.getName().equals("name")
					&& (cl_map.get(attribute.getValue()) != null)) {
				// System.out.println(attribute.getValue());
				Attr tempAttr = (Attr) attrs.item(i + 1);
				tempAttr.setValue(cl_map.get(attribute.getValue()));
			}
		}
	}
}
