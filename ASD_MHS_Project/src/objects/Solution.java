package objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Solution {
	private ArrayList<Hypothesis> mhsSet; 
	private HashMap<Integer, Integer> cardDistribution;
	private Instance in;
	private boolean complete;
	private int levelReached;
	private double time;
	
	public Solution(Instance in) {
		this.in = in;
		mhsSet = new ArrayList<>();
		cardDistribution = new HashMap<>();
		complete = false;
		levelReached = 0;
	}
	
	public void setComplete() {
		complete = true;
	}
	
	public void setTime(double time) {
		this.time = time;
	}
	
	public void incrementLevelReached() {
		levelReached++;
	}
	
	public void add(Hypothesis mhs) {
		mhsSet.add(mhs);
		int value;
		if(cardDistribution.get(mhs.getBin().cardinality()) != null)
			value = cardDistribution.get(mhs.getBin().cardinality());
		else 
			value = 0;
		
		cardDistribution.put(mhs.getBin().cardinality(), ++value);
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
	
	public String toString() {
		String mhsMatrix = "";
		int k;
		for(int i=0; i<mhsSet.size(); i++) {
			k = 0;
			mhsMatrix += "\n";
			for(int j=0; j<in.getInputFileCols(); j++) {
				if(in.getUsefulCols().get(j)) {
					if(mhsSet.get(i).getBin().get(k))
						mhsMatrix += "1 ";
					else
						mhsMatrix += "0 ";
					k++;
				}
				else
					mhsMatrix += "0 ";
			}
			mhsMatrix += "-";
		}
		return mhsMatrix;
	}
	
	public String getStringForFile() {
		return getSummary()+toString();
	}
}
