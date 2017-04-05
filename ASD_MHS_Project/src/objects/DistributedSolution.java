package objects;

import java.util.HashMap;
import java.util.Map;

public class DistributedSolution extends Solution {
	// Numero di componenenti (numero files generati)
	private int nFiles;
	// Numero globale di MHS trovati (pre-composizione)
	private int nGlobalMHS;
	// Struttura dati contenente coppie chiave-valore che indicano gli MHS in ciascun componente
	private HashMap<Integer, Integer> mhsPerComponents;
	
	public DistributedSolution(Instance in) {
		super(in);
		mhsPerComponents = new HashMap<>();
	}
	
	public void setnFiles(int nFiles) {
		this.nFiles = nFiles;
	}

	public void setnGlobalMHS(int nGlobalMHS) {
		this.nGlobalMHS = nGlobalMHS;
	}
	
	public void setMHSPerComponents(HashMap<Integer, Integer> mhsPerComponents) {
		this.mhsPerComponents = mhsPerComponents;
	}

	public String getSummary() {
		StringBuilder summary = new StringBuilder();
		summary.append(";;;Componenti\n;;; Cardinalità M: "+in.getInputFileCols()+"\n;;; Cardinalità M': "+in.getNumUsefulColumns()+"\n");
		summary.append(super.getSummary());
		summary.append("\n;;;File creati: "+nFiles+"\n");
		summary.append(";;;MHS globali trovati (pre composizione): "+nGlobalMHS);
		for(Map.Entry<Integer, Integer> entry : mhsPerComponents.entrySet()) {
			summary.append("\n;;; Componente " + entry.getKey() + " -> " + entry.getValue());
		}
		return summary.toString();
	}

}
