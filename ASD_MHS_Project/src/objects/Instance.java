package objects;

import java.util.BitSet;

public class Instance {
	private BitSet usefulColumns;
	private BitSet matrix;
	
	public Instance() {
		usefulColumns = new BitSet();		
	}
	
	public Instance(int numColumns) {
		usefulColumns = new BitSet(numColumns);
	}
	
	public int getNumUsefulColumns() {
		return usefulColumns.cardinality();
	}
	
	public void setUsefulColumn(int index) {
		usefulColumns.set(index);
	}
	
	public void createMatrix(int numRows) {
		matrix = new BitSet(numRows*usefulColumns.cardinality());
	}
	
	public void setElement(int row, int column) {
		matrix.set(row*usefulColumns.cardinality() + column);
	}
	
}
