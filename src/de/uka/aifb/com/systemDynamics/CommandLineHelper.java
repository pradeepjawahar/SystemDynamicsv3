/**
 * 
 */
package de.uka.aifb.com.systemDynamics;
import java.util.HashMap;
import com.csvreader.CsvReader;
/**
 * @author Pradeep Jawahar, Tennenbaum Institute of TEchnology, Georgia Tech
 *
 */
public class CommandLineHelper {
	
	public static HashMap<String,String> convertCl(String inputFileName)throws Exception
	{
		HashMap<String,String> cl_map = new HashMap<String,String>();
		CsvReader inputFile = new CsvReader(inputFileName,',');
		while(inputFile.readRecord())
		{
			cl_map.put(inputFile.get(0),inputFile.get(1));
		}
		inputFile.close();
		return cl_map;
	}
	

}
