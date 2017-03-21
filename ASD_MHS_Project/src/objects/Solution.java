package objects;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

public abstract class Solution {
	protected ArrayList<Hypothesis> mhsSet; 
	protected HashMap<Integer, Integer> cardDistribution;
	protected Instance in;
	protected boolean complete;
	protected int levelReached;
	protected double time;
	protected Vector<Integer> nHypothesisPerLevel;
	
	public Solution(Instance in) {
		this.in = in;
		mhsSet = new ArrayList<>();
		cardDistribution = new HashMap<>();
		complete = false;
		levelReached = -1;
		nHypothesisPerLevel = new Vector<Integer>();
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
		//Ordinamento descrescente effetuato per ottimizzare isMHSinCi
		Collections.sort(mhsSet, Collections.reverseOrder());
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
	
	public void setN_HypothesisPerLevel(int nHypothesis) {
		nHypothesisPerLevel.addElement(nHypothesis);
	}
	
	public String listN_HypothesisPerLevel() {
		StringBuilder list = new StringBuilder();
		for(int i = 0; i< nHypothesisPerLevel.size(); i++)
			list.append(";;; lv "+i+" -> "+nHypothesisPerLevel.get(i)+"\n");
		return list.toString();
	}
	
	public void add(Hypothesis mhs) {
		mhsSet.add(mhs);
		int value;
		int mhsCardinality = mhs.getBin().cardinality();
		if(cardDistribution.get(mhsCardinality) != null)
			value = cardDistribution.get(mhsCardinality);
		else 
			value = 0;
		
		cardDistribution.put(mhsCardinality, ++value);
	}
	
	public String toString() {
		StringBuilder mhsMatrix = new StringBuilder();
		int k;
		for(int i=0; i<mhsSet.size(); i++) {
			k = 0;
			mhsMatrix.append("\n");
			for(int j=0; j<in.getInputFileCols(); j++) {
				if(in.isUsefulCol(j)) {
					if(mhsSet.get(i).getBin().get(k))
						mhsMatrix.append("1 ");
					else
						mhsMatrix.append("0 ");
					k++;
				}
				else
					mhsMatrix.append("0 ");
			}
			mhsMatrix.append("-");
		}
		return mhsMatrix.toString();
	}
	
	public String getStringForFile() {
		return getSummary()+toString();
	}
	
	public abstract String getSummary();
}