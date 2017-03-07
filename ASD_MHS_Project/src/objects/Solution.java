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
			mhsMatrix += "-\n";
		}
		return mhsMatrix;
	}
}
