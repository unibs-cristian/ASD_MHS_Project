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
		summary = ";;;Matrice in input\n;;; righe: "+in.getMatrixNumRows()+"\n;;; colonne: "+in.getInputFileCols()+"\n;;; colonne utili: "+in.getNumUsefulColumns()+"\n";
		if(complete)
			summary += ";;;Esecuzione completata\n";
		else {
			summary += ";;;Esecuzione interrotta\n";
			summary += ";;;Livello raggiunto: "+levelReached+"\n";
		}
		summary += ";;;Tempo trascorso: "+time+"\n";
		summary += ";;;File creati: "+nFiles+"\n";
		summary += ";;;MHS globali trovati (pre composizione): "+nGlobalMHS+"\n";
		summary += ";;;MHS trovati(post composizione): "+mhsSet.size()+"\n";
		summary += ";;;MHS distribuzione cardinalità:";
		for(Map.Entry<Integer, Integer> entry : cardDistribution.entrySet()) {
			summary += "\n;;; Card " + entry.getKey() + " -> " + entry.getValue();
		}
		return summary;
	}

}
