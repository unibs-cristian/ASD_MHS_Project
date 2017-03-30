package objects;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public abstract class Solution {
	// Struttura dati contenente le ipotesi che sono i MHS del problema
	protected ArrayList<Hypothesis> mhsSet; 
	// Struttura dati contenente la distribuzione per cardinalita' degli MHS 
	protected HashMap<Integer, Integer> cardDistribution;
	// Oggetto contenente informazioni sulla matrice 
	protected Instance in;
	// Flag che dice se l'esplorazione e' stata completata
	protected boolean complete;
	// Il livello raggiunto dal modulo per l'esplorazione dello spazio di ricerca
	protected int levelReached;
	// Tempo impiegato dal modulo per l'esplorazione dello spazio di ricerca
	protected double time;
	// Struttura dati che tiene traccia di quante ipotesi sono state generate in ciascun livello
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
	
	// Espande un MHS 
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
	
	public String getSummary() {
		StringBuilder summary = new StringBuilder();
		if(complete)
			summary.append(";;;Esecuzione completata\n");
		else {
			summary.append(";;;Esecuzione interrotta\n");
			if(levelReached == -1)
				summary.append(";;;Nessun livello esplorato");
			else
				summary.append(";;;Livello raggiunto: "+levelReached+"\n");
		}
		if(levelReached >= 0) {
			summary.append(";;;Ipotesi create per livello:\n");
			summary.append(listN_HypothesisPerLevel());
			summary.append(";;;Tempo trascorso: "+time+"\n");
			summary.append(";;;MHS trovati: "+mhsSet.size());
			if(mhsSet.size() > 0) {
				summary.append("\n;;;MHS distribuzione cardinalita':");
				for(Map.Entry<Integer, Integer> entry : cardDistribution.entrySet()) {
					summary.append("\n;;; Card " + entry.getKey() + " -> " + entry.getValue());
				}
			}
		}
		return summary.toString();
	}
}