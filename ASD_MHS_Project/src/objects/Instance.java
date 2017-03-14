package objects;

import java.util.BitSet;

public class Instance {
	private final static String EXTENSION_INPUT = "matrix";
	
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
	
	public BitSet getUsefulCols() {
		return usefulColumns;
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
	
	//Restituisce [firtRow,lastRow) (lastRow esclusa)
	public String getMatrixRows(int firtRow, int lastRow) {
		int k;
		String matrixOut = "";
		for(int i=firtRow; i<lastRow; i++) {
			k = 0;
			matrixOut += "\n";
			for(int j=0; j<inputFileCols; j++) {
				if(usefulColumns.get(j)) {
					if(matrix.get(i*matrixCols+ k))
						matrixOut += "1 ";
					else
						matrixOut += "0 ";
					k++;
				}
				else
					matrixOut += "0 ";
			}
			matrixOut += "-";
		}
		return matrixOut;
	}
	
	//TODO Da decidere se e dove spostare il metodo writeOutputData, al momento non raggiungibile da qui
	/*
	public void createNiFiles(String dirPath, String fileName) {
		int nRigheTolte = 0;
		int rand;
		int i = 1;
		while(nRigheTolte < matrixRows){
			rand = 1 + (int)(Math.random() * (((matrixRows-nRigheTolte) - 1) + 1));
			String matrixOutN = "";
			matrixOutN = getMatrixRows(nRigheTolte, nRigheTolte+rand);
			writeOutputData(matrixOutN,dirPath+"/"+fileName+"_N"+i+"."+EXTENSION_INPUT);
			nRigheTolte+=rand;
			i++;
		}
	}
	*/
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
