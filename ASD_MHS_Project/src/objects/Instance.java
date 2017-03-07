package objects;

import java.util.BitSet;

public class Instance {
	private BitSet usefulColumns;
	private BitSet matrix;		   
	private int inputFileCols;        // Indica il numero di colonne della matrice contenuta nel file di input
	private int matrixCols;        // Indica il numero di colonne della matrice composta solo dalle colonne utili
	private int matrixRows; 	
	
	public Instance() {
		usefulColumns = new BitSet();		
	}
	
	public Instance(int numColumns) {
		usefulColumns = new BitSet(numColumns);
		this.inputFileCols = numColumns;
	}
	
	public BitSet getMatrix() {
		return matrix;
	}
	
	public int getMatrixNumRows() {
		return matrixRows;
	}
	
	public int getMatrixNumCols() {
		return matrixCols;
	}
	
	public BitSet getMatrixColumn(int column) {
		BitSet col = new BitSet(matrixRows);
		for(int i=0; i<matrixRows; i++) 
			col.set(i, matrix.get(i*matrixCols + column));
	
		return col;				
	}
	
	public int getInputFileCols() {
		return inputFileCols;
	}
	
	public int getNumUsefulColumns() {
		return usefulColumns.cardinality();
	}
	
	public void setUsefulColumn(int index) {
		usefulColumns.set(index);
	}
	
	public void createMatrix(int numRows) {
		matrix = new BitSet(numRows*usefulColumns.cardinality());
		matrixRows = numRows;
		matrixCols = usefulColumns.cardinality();
	}
	
	public void setElement(int row, int column) {		
		matrix.set(row*usefulColumns.cardinality() + column);
	}
	
	public boolean isUseful(int col) {
		return usefulColumns.get(col);
	}
	
	public void printUsefulColumns() {
		for(int i=0; i<inputFileCols; i++) {
			System.out.println(usefulColumns.get(i) + " ");
		}
 	}
	
	public void printMatrix() {
		System.out.println("Numero di righe: " + matrixRows + " Numero di colonne: "  + matrixCols);
		for(int i=0; i<matrixRows; i++) {
			for(int j=0; j<matrixCols; j++) {
				System.out.print(matrix.get(i*matrixCols + j) + " ");
			}
			System.out.println();
		}
	}
	
	public void printMatrixElem(int i, int j) {
		System.out.println(matrix.get(i*matrixCols+ j));
	}
}
