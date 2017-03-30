package objects;

public class MonolithicSolution extends Solution{
	
	public MonolithicSolution(Instance in) {
		super(in);		
	}
	
	public String getSummary() {
		StringBuilder summary = new StringBuilder();
		summary.append(";;;Matrice in input\n;;; righe: "+in.getMatrixNumRows()+"\n;;; colonne: "+in.getInputFileCols()+"\n;;; colonne utili: "+in.getNumUsefulColumns()+"\n");
		summary.append(";;; N. d'ordine colonne soppresse: "+in.listUselessCols()+"\n");
		summary.append(super.getSummary());
		return summary.toString();
	}
}
