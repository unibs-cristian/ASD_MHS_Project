package objects;

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
		summary.append(";;;Componenti\n;;; |M|: "+in.getInputFileCols()+"\n;;; |M'|: "+in.getNumUsefulColumns()+"\n");
		summary.append(super.getSummary());
		summary.append("\n;;;File creati: "+nFiles+"\n");
		summary.append(";;;MHS globali trovati (pre composizione): "+nGlobalMHS);
		return summary.toString();
	}

}
