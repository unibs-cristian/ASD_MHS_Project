package objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Solution {
	private ArrayList<Hypothesis> mhsSet; 
	private HashMap<Integer, Integer> cardDistribution;
	private Instance in;
	
	public Solution(Instance in) {
		this.in = in;
		mhsSet = new ArrayList<>();
		cardDistribution = new HashMap<>();
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
		summary = ";;;Input matrix\n;;; rows: "+in.getMatrixNumRows()+"\n;;; cols: "+in.getInputFileCols()+"\n";
		summary += ";;;MHS found: "+mhsSet.size()+"\n";
		summary += ";;;MHS cardinality distribution: \n";
		for(Map.Entry<Integer, Integer> entry : cardDistribution.entrySet()) {
			summary += ";;; Card " + entry.getKey() + " -> " + entry.getValue() + "\n";
		}
		return summary;
	}
	
	public String toString() {
		return mhsSet.toString();
	}
}
