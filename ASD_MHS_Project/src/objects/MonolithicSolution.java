package objects;

import java.util.Map;

public class MonolithicSolution extends Solution{
	
	public MonolithicSolution(Instance in) {
		super(in);		
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
		summary += ";;;MHS trovati: "+mhsSet.size()+"\n";
		summary += ";;;MHS distribuzione cardinalità:";
		for(Map.Entry<Integer, Integer> entry : cardDistribution.entrySet()) {
			summary += "\n;;; Card " + entry.getKey() + " -> " + entry.getValue();
		}
		return summary;
	}
}
