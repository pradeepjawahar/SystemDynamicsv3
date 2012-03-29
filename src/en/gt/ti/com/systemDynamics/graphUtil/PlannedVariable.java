package en.gt.ti.com.systemDynamics.graphUtil;
import java.util.Vector;
public class PlannedVariable {
	String id;
	String name;
	String startValue;
	Vector<Increment> plannedIncrement;	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStartValue() {
		return startValue;
	}

	public void setStartValue(String startValue) {
		this.startValue = startValue;
	}

	public Vector<Increment> getPlannedIncrement() {
		return plannedIncrement;
	}

	public void setPlannedIncrement(Vector<Increment> plannedIncrement) {
		this.plannedIncrement = plannedIncrement;
	}

}
