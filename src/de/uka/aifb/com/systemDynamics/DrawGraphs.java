package de.uka.aifb.com.systemDynamics;

import com.csvreader.CsvReader;

import en.gt.ti.com.systemDynamics.graphUtil.ChartLevelNode;
import en.gt.ti.com.systemDynamics.graphUtil.Increment;
import en.gt.ti.com.systemDynamics.graphUtil.LevelNodeGraphInfo;
import en.gt.ti.com.systemDynamics.graphUtil.PlannedRef;
import en.gt.ti.com.systemDynamics.graphUtil.PlannedVariable;
import en.gt.ti.com.systemDynamics.graphUtil.PlannedVariableExt;
import en.gt.ti.com.systemDynamics.graphUtil.PlannedXML;
import en.gt.ti.com.systemDynamics.graphUtil.SysDynChart;

import java.awt.Color;
import java.io.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.ChartUtilities;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

public class DrawGraphs {

	HashMap<String, PlannedVariable> plannedMap;
	HashMap<String, SysDynChart> chartMap;
	HashMap<String, String> levelNodes;
	int runValue;

	public DrawGraphs(HashMap<String, String> levelNodes,String chartFilename) {
		PlannedXML planned = new PlannedXML(chartFilename);
		plannedMap = planned.readPlannedVariable();
		chartMap = planned.buildChart();
		this.levelNodes = levelNodes;
		try
		{
			runValue = 0;
		}
		catch(Exception e)
		{
			
		}
	}

	/**
	 * @param run
	 * @param fname
	 * @param folder
	 * @throws Exception
	 */
	public void drawGraphs(int run, String fname, String folder) throws Exception {
		int xValue = 0;
		Iterator it = chartMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			System.out.println(pairs.getKey());
			SysDynChart chartObj = (SysDynChart) pairs.getValue();
			xValue = this.createGraph(chartObj, fname, run, folder);
		}
		writeTime(xValue);
	}

	/**
	 * @param chartObj
	 * @param fname
	 * @param run
	 * @param folder
	 * @return
	 * @throws Exception
	 */
	public int createGraph(SysDynChart chartObj, String fname, int run,
			String folder) throws Exception {
		// Using charts XML

		Vector<ChartLevelNode> levelNodesVector = chartObj.getChartLevelNodes();
		Vector<LevelNodeGraphInfo> levelVector = new Vector<LevelNodeGraphInfo>();

		for (ChartLevelNode lNode : levelNodesVector) {
			LevelNodeGraphInfo levelNodeInfo = new LevelNodeGraphInfo();
			levelNodeInfo.setId(lNode.getLevelIdRef());
			levelNodeInfo.setNodeName(levelNodes.get(lNode.getLevelIdRef()));
			levelNodeInfo.setSeries(new XYSeries(lNode.getLabel()));
			levelVector.add(levelNodeInfo);
		}
		//handle the init phase separately for phase 2 alone
		int i = 0;
		if(folder.equals("phase2"))
			i = -1;
		int xIntercept = 0;
		int rValue = 0;
		for (; i < run; i++) {
			String outputFile = getFileName(i, fname);
			CsvReader products = new CsvReader(outputFile, ';');
			products.skipLine();
			products.readHeaders();
			while (products.readRecord()) {
				for (LevelNodeGraphInfo lnode : levelVector) {
					lnode.getSeries().add(
							xIntercept,
							Double.parseDouble(products
									.get(lnode.getNodeName())));
				}
				xIntercept++;
				
			}
			rValue =--xIntercept;
			products.close();
		}
		final XYSeriesCollection dataset = new XYSeriesCollection();
		for (LevelNodeGraphInfo lnode : levelVector) {
			dataset.addSeries(lnode.getSeries());
		}
		// Planned Variable
		Vector<PlannedRef> planRefVector = chartObj.getPlannedNodes();
		Vector<PlannedVariableExt> planVector = new Vector<PlannedVariableExt>();
		for (PlannedRef planRef : planRefVector) {
			PlannedVariable plan = plannedMap.get(planRef.getId());
			PlannedVariableExt planExt = new PlannedVariableExt();
			planExt.setLabel(planRef.getLabel());
			planExt.setName(plan.getName());
			planExt.setStartValue(plan.getStartValue());
			planExt.setPlannedIncrement(plan.getPlannedIncrement());
			planVector.add(planExt);
		}

		// create the chart...
		int x1 = 0, x2 = 0;
		double y1, y2;
		for (PlannedVariableExt plannedExt : planVector) {
			if (x2 >= xIntercept)
				break;
			XYSeries planSeries = new XYSeries(plannedExt.getLabel());
			y1 = Double.parseDouble(plannedExt.getStartValue());
			for (Increment inc : plannedExt.getPlannedIncrement()) {
				if (x2 >= xIntercept)
					break;
				for (i = 0; i < inc.getLength(); i++) {
					if (x2 >= xIntercept)
						break;
					y2 = inc.getSlope() * (x2 - x1) + y1;
					planSeries.add(x2, y2);
					x1 = x2;
					y1 = y2;
					x2++;
				}

			}
			dataset.addSeries(planSeries);
		}
		final JFreeChart chart = ChartFactory.createXYLineChart(chartObj
				.getName(), // chart title
				chartObj.getXLabel(), // x axis label
				chartObj.getYLabel(), // y axis label
				dataset, // data
				PlotOrientation.VERTICAL, true, // include legend
				true, // tooltips
				false // urls
				);

		// NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
		chart.setBackgroundPaint(Color.white);

		// final StandardLegend legend = (StandardLegend) chart.getLegend();
		// legend.setDisplaySeriesShapes(true);

		// get a reference to the plot for further customisation...
		final XYPlot plot = chart.getXYPlot();
		plot.setBackgroundPaint(Color.lightGray);
		// plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));
		// plot.setDomainGridlinePaint(Color.white);
		// plot.setRangeGridlinePaint(Color.white);

		final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		renderer.setSeriesLinesVisible(0, false);
		renderer.setSeriesShapesVisible(1, false);
		plot.setRenderer(renderer);

		// change the auto tick unit selection to integer units only...
		// final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		// rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		File imgFile = new File(folder + "/" + chartObj.getFile() + "_" + run
				+ ".jpg");
		ChartUtilities.saveChartAsJPEG(imgFile, chart, 550, 400);
		//System.out.println("Run value:" + xIntercept);
		//writeTime(runValue);
		return rValue;
	}

	/*private int readRunValue() throws Exception {
		File timeFile = new File("xTime.txt");
		if(!timeFile.exists())
			return 0;
		BufferedReader reader = new BufferedReader(new BufferedReader(new FileReader(timeFile)));
		 return Integer.parseInt(reader.readLine());
	}*/

	public String getFileName(int run, String fname) {
		int index = fname.lastIndexOf('_');
		String file = fname.substring(0, index);
		String new_filename = file + "_" + String.valueOf(run);
		return new_filename;
	}
	private void writeTime(int intercept) throws Exception {
		File timeFile = new File("xTime.txt");
		if(timeFile.exists())
			timeFile.delete();
		BufferedWriter writer= new BufferedWriter(new FileWriter("xTime.txt"));
		writer.write(String.valueOf(intercept));
		writer.close();
	}

}
