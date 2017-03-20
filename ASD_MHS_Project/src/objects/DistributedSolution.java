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
		StringBuilder summary = new StringBuilder();
		summary.append(";;;Matrice in input\n;;; righe: "+in.getMatrixNumRows()+"\n;;; colonne: "+in.getInputFileCols()+"\n;;; colonne utili: "+in.getNumUsefulColumns()+"\n");
		summary.append(";;; N. d'ordine colonne soppresse: "+in.listUselessCols()+"\n");
		if(complete)
			summary.append(";;;Esecuzione completata\n");
		else {
			summary.append(";;;Esecuzione interrotta\n");
			summary.append(";;;Livello raggiunto: "+levelReached+"\n");
		}
		summary.append(";;;Tempo trascorso: "+time+"\n");
		summary.append(";;;File creati: "+nFiles+"\n");
		summary.append(";;;MHS globali trovati (pre composizione): "+nGlobalMHS+"\n");
		summary.append(";;;MHS trovati(post composizione): "+mhsSet.size()+"\n");
		summary.append(";;;MHS distribuzione cardinalità:");
		for(Map.Entry<Integer, Integer> entry : cardDistribution.entrySet()) {
			summary.append("\n;;; Card " + entry.getKey() + " -> " + entry.getValue());
		}
		return summary.toString();
	}

}
