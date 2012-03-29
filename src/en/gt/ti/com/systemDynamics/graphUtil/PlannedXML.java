package en.gt.ti.com.systemDynamics.graphUtil;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import java.io.File;
import java.util.HashMap;
import java.util.Vector;

public class PlannedXML {

	Document doc;

	public PlannedXML(String filename) {
		try {
			//String filename = "charts.xml";
			File fXmlFile = new File(filename);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
		} catch (Exception e) {

		}
	}

	public HashMap<String, PlannedVariable> readPlannedVariable() {

		HashMap<String, PlannedVariable> plannedMap = new HashMap<String, PlannedVariable>();
		try {

			// System.out.println("Root element :" +
			// doc.getDocumentElement().getNodeName());
			// Parse chart nodes
			NodeList nList = doc.getElementsByTagName("PlanNode");
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					PlannedVariable planned = new PlannedVariable();
					Vector<Increment> incVector = new Vector<Increment>();
					// Process Attribute of Planned Node
					NamedNodeMap attrs = nNode.getAttributes();
					for (int i = 0; i < attrs.getLength(); i++) {
						Attr attribute = (Attr) attrs.item(i);
						if (attribute.getName().equals("id"))
							planned.setId(attribute.getValue());
						if (attribute.getName().equals("name"))
							planned.setName(attribute.getValue());
						if (attribute.getName().equals("startValue"))
							planned.setStartValue(attribute.getValue());
					}
					// Get Planned Increments
					NodeList incList = nNode.getChildNodes();
					for (int i = 0; i < incList.getLength(); i++) {
						Increment plannedIncrement = new Increment();
						Node plannedNode = incList.item(i);
						if (plannedNode.getNodeType() == Node.ELEMENT_NODE
								&& plannedNode.getNodeName().equals(
										"PlanNodeIncrement")) {
							NamedNodeMap plannedAttr = plannedNode
									.getAttributes();
							for (int j = 0; j < plannedAttr.getLength(); j++) {
								Attr attr1 = (Attr) plannedAttr.item(j);
								if (attr1.getName().equals("id")) {
									plannedIncrement.setId(attr1.getValue());
								}
								if (attr1.getName().equals("length")) {
									plannedIncrement.setLength(Double
											.parseDouble(attr1.getValue()));
								}
								if (attr1.getName().equals("slope")) {
									plannedIncrement.setSlope(Double
											.parseDouble(attr1.getValue()));
								}
							}
							incVector.add(plannedIncrement);
						}
					}
					planned.setPlannedIncrement(incVector);
					plannedMap.put(planned.getId(), planned);
				}

			}
			/*
			 * Iterator itMap =plannedMap.entrySet().iterator();
			 * while(itMap.hasNext()){ Map.Entry pairs =
			 * (Map.Entry)itMap.next(); System.out.println(pairs.getKey());
			 * PlannedVariable plan = (PlannedVariable)pairs.getValue(); Vector<Increment>
			 * v = plan.getPlannedIncrement(); for(Increment inc : v){
			 * System.out.println(inc.getId()); } }
			 */
			// Parse Planned variable nodes
		} catch (Exception e) {
			System.err.println(e.getMessage() + "," + e.getLocalizedMessage());
		}
		return plannedMap;
	}

	public HashMap<String, SysDynChart> buildChart() {

		//HashMap<String, PlannedVariable> plannedMap = new HashMap<String, PlannedVariable>();
		HashMap<String, SysDynChart> chartMap = new HashMap<String, SysDynChart>();
		//plannedMap = readPlannedVariable();
		NodeList nList = doc.getElementsByTagName("Chart");
		for (int i = 0; i < nList.getLength(); i++) {
			Node chartNode = nList.item(i);
			if (chartNode.getNodeType() == Node.ELEMENT_NODE) {
				SysDynChart chartVariable = new SysDynChart();
				NamedNodeMap attribute = chartNode.getAttributes();
				for (int j = 0; j < attribute.getLength(); j++) {
					Attr chartAttr = (Attr) attribute.item(j);
					if (chartAttr.getName().equals("id")) {
						chartVariable.setId(chartAttr.getValue());
					}
					if (chartAttr.getName().equals("name")) {
						chartVariable.setName(chartAttr.getValue());
					}
					if (chartAttr.getName().equals("file")) {
						chartVariable.setFile(chartAttr.getValue());
					}
					if (chartAttr.getName().equals("xAxisLabel")) {
						chartVariable.setXLabel(chartAttr.getValue());
					}
					if (chartAttr.getName().equals("yAxisLabel")) {
						chartVariable.setYLabel(chartAttr.getValue());
					}
					// ChartLevelNodes
					NodeList chartLevelNodeList = chartNode.getChildNodes();
					Vector<ChartLevelNode> chartLevelVector = new Vector<ChartLevelNode>();
					Vector<PlannedRef> plannedVector = new Vector<PlannedRef>();
					for (int k = 0; k < chartLevelNodeList.getLength(); k++) {

						Node cNode = chartLevelNodeList.item(k);
						if (cNode.getNodeType() == Node.ELEMENT_NODE
								&& cNode.getNodeName()
										.equals("ChartLevelNodes")) {
							NodeList chartLevelNodes = cNode.getChildNodes();
							for (int temp = 0; temp < chartLevelNodes
									.getLength(); temp++) {
								Node chartLevelNode = chartLevelNodes
										.item(temp);
								if (chartLevelNode.getNodeType() == Node.ELEMENT_NODE
										&& chartLevelNode.getNodeName().equals(
												"ChartLevelNode")) {
									ChartLevelNode chartLevelObj = new ChartLevelNode();
									NamedNodeMap attrMap = chartLevelNode
											.getAttributes();
									for (int x = 0; x < attrMap.getLength(); x++) {
										Attr attr = (Attr) attrMap.item(x);
										if (attr.getName().equals(
												"levelNodeIdRef")) {
											chartLevelObj.setLevelIdRef(attr
													.getValue());
										}
										if (attr.getName().equals("label")) {
											chartLevelObj.setLabel(attr
													.getValue());
										}
									}
									chartLevelVector.add(chartLevelObj);
								}
							}
						}
						// PlannedNodes
						else if (cNode.getNodeType() == Node.ELEMENT_NODE
								&& cNode.getNodeName().equals("ChartPlanNodes")) {
							NodeList chartPlanNodes = cNode.getChildNodes();
							for (int y = 0; y < chartPlanNodes.getLength(); y++) {
								Node planChartNode = chartPlanNodes.item(y);
								if (planChartNode.getNodeType() == Node.ELEMENT_NODE
										&& planChartNode.getNodeName().equals(
												"ChartPlanNode")) {
									PlannedRef plannedRef =new PlannedRef();
									NamedNodeMap attr1 = planChartNode
											.getAttributes();
									for (int z = 0; z < attr1.getLength(); z++) {
										Attr attr2 = (Attr)attr1.item(z);
										if(attr2.getName().equals("planNodeIdRef")){
											plannedRef.setId(attr2.getValue());
										}
										if(attr2.getName().equals("label")){
											plannedRef.setLabel(attr2.getValue());
										}		
									}
									plannedVector.add(plannedRef);
								}
								
							}
						}
						chartVariable.setChartLevelNodes(chartLevelVector);
						chartVariable.setPlannedNodes(plannedVector);
					}
					
				}
				
				chartMap.put(chartVariable.getName(), chartVariable);

			}
			
		}
		
		/*Iterator it = chartMap.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry pairs = (Map.Entry)it.next();
			System.out.println(pairs.getKey());
			SysDynChart chartObj = (SysDynChart)pairs.getValue();
			Vector<PlannedRef> plan = chartObj.getPlannedNodes();
			for(PlannedRef t: plan){
				System.out.println(t.getLabel());
			}
			Vector<ChartLevelNode> cart = chartObj.getChartLevelNodes();
			for(ChartLevelNode s: cart){
				System.out.println(s.getLevelIdRef());
			}
		}
		
*/
		return chartMap;
	}

	
}
