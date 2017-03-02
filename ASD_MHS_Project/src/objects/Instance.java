package objects;

import java.util.BitSet;

public class Instance {
	private BitSet usefulColumns;
	private BitSet matrix;
	private int numColumns;
	private int matrixCols;
	private int matrixRows;
	
	public Instance() {
		usefulColumns = new BitSet();		
	}
	
	public Instance(int numColumns) {
		usefulColumns = new BitSet(numColumns);
		this.numColumns = numColumns;
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
	
	public void printUsefulColumns() {
		for(int i=0; i<numColumns; i++) {
			System.out.println(usefulColumns.get(i) + " ");
		}
 	}
	
	public void printMatrix() {
		System.out.println(matrixRows + " "  + matrixCols);
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
