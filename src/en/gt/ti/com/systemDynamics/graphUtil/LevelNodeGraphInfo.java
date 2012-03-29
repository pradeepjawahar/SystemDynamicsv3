package en.gt.ti.com.systemDynamics.graphUtil;

import org.jfree.data.xy.XYSeries;

public class LevelNodeGraphInfo {
	
	String id;
	XYSeries series;
	String nodeName;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public XYSeries getSeries() {
		return series;
	}
	public void setSeries(XYSeries series) {
		this.series = series;
	}
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

}
