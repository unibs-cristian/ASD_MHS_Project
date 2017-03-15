package objects;

import java.util.Map;

public class MonolithicSolution extends Solution{
	
	public MonolithicSolution(Instance in) {
		super(in);		
	}
	
	public String getSummary() {
		String summary;
		summary = ";;;Input matrix\n;;; rows: "+in.getMatrixNumRows()+"\n;;; cols: "+in.getInputFileCols()+"\n;;; used cols: "+in.getNumUsefulColumns()+"\n";
		if(complete)
			summary += ";;;Execution completed\n";
		else {
			summary += ";;;Execution interrupted\n";
			summary += ";;;Level reached: "+levelReached+"\n";
		}
		summary += ";;;Time elapsed: "+time+"\n";
		summary += ";;;MHS found: "+mhsSet.size()+"\n";
		summary += ";;;MHS cardinality distribution:";
		for(Map.Entry<Integer, Integer> entry : cardDistribution.entrySet()) {
			summary += "\n;;; Card " + entry.getKey() + " -> " + entry.getValue();
		}
		return summary;
	}
}
