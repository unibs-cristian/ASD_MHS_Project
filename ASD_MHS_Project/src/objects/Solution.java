package objects;

import java.util.ArrayList;
import java.util.HashMap;

public class Solution {
	private ArrayList<Hypothesis> mhsSet; 
	private HashMap<Integer, Integer> cardDistribution;
	
	public Solution() {
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
	
	public String getSummary(Instance in) {
		String summary;
		summary = ";;;Input matrix\n;;; rows: "+in.getMatrixNumRows()+"\n;;; cols: "+in.getMatrixNumCols()+"\n";
		summary += ";;;MHS found: "+mhsSet.size()+"\n";
		summary += ";;;MHS cardinality distribution: \n";
		summary += cardDistribution.toString();
		return summary;
	}
	
	public String toString() {
		return mhsSet.toString();
	}
}
