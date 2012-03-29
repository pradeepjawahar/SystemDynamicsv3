package en.gt.ti.com.systemDynamics.graphUtil;

public class ChartLevelNode {
	
	private String levelIdRef;
	private String label;
	public ChartLevelNode(String levelIdRef, String label) {
		super();
		this.levelIdRef = levelIdRef;
		this.label = label;
	}
	public ChartLevelNode(){
		super();
	}
	public String getLevelIdRef() {
		return levelIdRef;
	}
	public void setLevelIdRef(String levelIdRef) {
		this.levelIdRef = levelIdRef;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	
}
