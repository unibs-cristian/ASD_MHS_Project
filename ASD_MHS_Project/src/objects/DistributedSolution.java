package objects;

import java.util.Map;

public class DistributedSolution extends Solution {
	
	private int nFiles;
	private int nGlobalMHS;
	
	public DistributedSolution(Instance in) {
		super(in);		
	}
	
	public void setnFiles(int nFiles) {
		this.nFiles = nFiles;
	}

	public void setnGlobalMHS(int nGlobalMHS) {
		this.nGlobalMHS = nGlobalMHS;
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
		summary += ";;;Files created: "+nFiles+"\n";
		summary += ";;;Global MHS found (before composition): "+nGlobalMHS+"\n";
		summary += ";;;MHS found (after composition): "+mhsSet.size()+"\n";
		summary += ";;;MHS cardinality distribution:";
		for(Map.Entry<Integer, Integer> entry : cardDistribution.entrySet()) {
			summary += "\n;;; Card " + entry.getKey() + " -> " + entry.getValue();
		}
		return summary;
	}
}
