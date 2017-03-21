package objects;

import java.util.Map;

public class MonolithicSolution extends Solution{
	
	public MonolithicSolution(Instance in) {
		super(in);		
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
		summary.append(";;;Ipotesi create per livello:\n");
		summary.append(listN_HypothesisPerLevel());
		summary.append(";;;Tempo trascorso: "+time+"\n");
		summary.append(";;;MHS trovati: "+mhsSet.size()+"\n");
		summary.append(";;;MHS distribuzione cardinalità:");
		for(Map.Entry<Integer, Integer> entry : cardDistribution.entrySet()) {
			summary.append("\n;;; Card " + entry.getKey() + " -> " + entry.getValue());
		}
		return summary.toString();
	}
}
