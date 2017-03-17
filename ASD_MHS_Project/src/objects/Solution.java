package objects;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;

public abstract class Solution {
	protected ArrayList<Hypothesis> mhsSet; 
	protected HashMap<Integer, Integer> cardDistribution;
	protected Instance in;
	protected boolean complete;
	protected int levelReached;
	protected double time;
	
	public Solution(Instance in) {
		this.in = in;
		mhsSet = new ArrayList<>();
		cardDistribution = new HashMap<>();
		complete = false;
		levelReached = -1;
	}
	
	public ArrayList<Hypothesis> getMhsSet() {
		return mhsSet;
	}
	
	public BitSet getMhsSetElementExpanded(int index) {
		int k=0;
		BitSet mhs = new BitSet(in.getInputFileCols());
		for(int j=0; j<in.getInputFileCols(); j++) {
			if(in.isUsefulCol(j)) {
				if(mhsSet.get(index).getBin().get(k))
					mhs.set(j);
				k++;
			}
		}
		return mhs;
	}
	
	public ArrayList<BitSet> getMhsSetExpanded() {
		ArrayList<BitSet> list = new ArrayList<>();
		for(int j=0; j<mhsSet.size(); j++)
			list.add(getMhsSetElementExpanded(j));
		return list;
	}
	
	public double getTime() {
		return time;
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
	
	public String toString() {
		String mhsMatrix = "";
		int k;
		for(int i=0; i<mhsSet.size(); i++) {
			k = 0;
			mhsMatrix += "\n";
			for(int j=0; j<in.getInputFileCols(); j++) {
				if(in.isUsefulCol(j)) {
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
	
	public abstract String getSummary();
}