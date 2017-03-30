package objects;

import java.util.BitSet;

public class Test {
	private int nRows;         // n. righe matrice da creare
	private int nCols;         // n. colonne matrice da creare 
	private int probability1;  // probabilita' che ciascun elemento sia 1
	private BitSet matrix;     // Struttura dati che rappresenta la matrice creata
	
	public Test(int nRows, int nCols, int probability1) {
		this.nRows = nRows;
		this.nCols = nCols;
		this.probability1 = probability1;
		matrix = new BitSet(nRows*nCols);
		createMatrix();
	}
	
	// Crea la matrice avente le informazioni specificate
	private void createMatrix() {
		boolean isRowEmpty = true;
		for(int i=0; i< nRows; i++) {
			isRowEmpty = true;
			for(int j=0; j< nCols; j++) {
				//In caso tutta la riga sia vuota automaticamente l'ultimo elemento viene settato a 1
				if(Math.random() < probability1/100.0 || ((j==(nCols-1))&& isRowEmpty)) {
					matrix.set(i*nCols+j);
					isRowEmpty = false;
				}
			}
		}
	}
	
	public String getStringForFile() {
		StringBuilder outString = new StringBuilder();
		outString.append(";;;Righe: "+nRows+"\n");
		outString.append(";;;Colonne: "+nCols+"\n");
		outString.append(";;;Probabilità 1: "+probability1+"\n");
		for(int i=0; i< nRows; i++) {
			for(int j=0; j< nCols; j++) {
				if(matrix.get(i*nCols+j))
					outString.append("1 ");
				else
					outString.append("0 ");
			}
			outString.append("-\n");
		}
		//elimino l'ultimo \n
		return outString.toString().substring(0, outString.toString().length()-1);
	}
	
}
