/* ======================================================================================================
 * SystemDynamics: Java application for modeling, visualization and execution of System Dynamics models
 * ======================================================================================================
 *
 * Modified by Pradeep Jawahar (Georgia Institute of Technology)
 * 11/15/2011
 * Class Round 
 */

package de.uka.aifb.com.systemDynamics.model;

/**
 * This class implements an AST element representing a Round.
 * 
 * @author Pradeep Jawahar , Georgia Institute of Technology, Tennenbaum
 *         Institute
 * @version 1.0
 */
public class RoundNode extends ConstantNode {

	protected RoundNode(String nodeName, double constantValue) {
		super(nodeName,constantValue);
	}

	@Override
	void setConstantValue(double constantValue) {
		// TODO Auto-generated method stub
		super.setConstantValue(Math.round(constantValue));
	}
	

}