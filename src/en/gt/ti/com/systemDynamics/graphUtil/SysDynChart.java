package en.gt.ti.com.systemDynamics.graphUtil;
import java.util.Vector;

/**
 * This class implements a charts class. Necessary for storing a chart object from 
 * XML file
 * @author Pradeep Jawahar, Tennenbaum Institute of TEchnology, Ga Tech
 * @version 1.0
 */
public class SysDynChart {
	private String id;
	private String name;
	private String file;
	private String xLabel;
	private String yLabel;
	private Vector<ChartLevelNode> chartLevelNodes;
	private Vector<PlannedRef> plannedNodes;
	
	public Vector<ChartLevelNode> getChartLevelNodes() {
		return chartLevelNodes;
	}

	public void setChartLevelNodes(Vector<ChartLevelNode> chartLevelNodes) {
		this.chartLevelNodes = chartLevelNodes;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public SysDynChart(String id, String name, String file, String label,
			String label2, Vector<ChartLevelNode> chartLevelNodes) {
		super();
		this.id = id;
		this.name = name;
		this.file = file;
		xLabel = label;
		yLabel = label2;
		this.chartLevelNodes = chartLevelNodes;
	}
	
	public SysDynChart(){
		super();
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getXLabel() {
		return xLabel;
	}

	public void setXLabel(String label) {
		xLabel = label;
	}

	public String getYLabel() {
		return yLabel;
	}

	public void setYLabel(String label) {
		yLabel = label;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public Vector<PlannedRef> getPlannedNodes() {
		return plannedNodes;
	}

	public void setPlannedNodes(Vector<PlannedRef> plannedNodes) {
		this.plannedNodes = plannedNodes;
	}

	

}
