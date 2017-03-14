package objects;

import ioUtils.IOFile;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.BitSet;

public class Instance {
	private final static String EXTENSION_INPUT = "matrix";
	private final static String COMMENT_DELIMITER = ";;;";
	private final static String ROW_DELIMITER = "-";
	private final static String SEPARATOR = " ";
	
	private BitSet usefulColumns;
	private BitSet matrix;		   
	private int inputFileCols;        // Indica il numero di colonne della matrice contenuta nel file di input
	private int matrixCols;        // Indica il numero di colonne della matrice composta solo dalle colonne utili
	private int matrixRows; 	
	
	public Instance(String file) {
		readMatrixFromFile(file);
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
	public String getMatrixRows(int firstRow, int lastRow) {
		int k;
		String matrixOut = "";
		for(int i=firstRow; i<lastRow; i++) {
			k = 0;
			if(i > firstRow)
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
	
	public void readMatrixFromFile(String path) {
		FileReader fr = null;
		BufferedReader br;
		try {
			fr = new FileReader(path);
		} catch (FileNotFoundException e) {
			System.out.println("File not found.");
		}
		br = new BufferedReader(fr);

		String sCurrentLine;
		int numRows = 0;

		try {
			while ((sCurrentLine = br.readLine()) != null) {
				if(!sCurrentLine.startsWith(COMMENT_DELIMITER)) {
					sCurrentLine = cleanString(sCurrentLine);
					break;
				}
			}
			
			usefulColumns = new BitSet(sCurrentLine.length());
			inputFileCols = sCurrentLine.length();
			
			do {
				numRows ++;
				sCurrentLine = cleanString(sCurrentLine);
				for(int i=0; i<sCurrentLine.length(); i++) {
					if(sCurrentLine.charAt(i) == '1')
						setUsefulColumn(i);
				}
			} while((sCurrentLine = br.readLine()) != null);		

			createMatrix(numRows);
			try {
				fr = new FileReader(path);
			} catch (FileNotFoundException e) {
				System.out.println("File not found.");
			}
			br = new BufferedReader(fr);
					
			int i = 0;
			
			while ((sCurrentLine = br.readLine()) != null) {
				if(!sCurrentLine.startsWith(COMMENT_DELIMITER)) {
					sCurrentLine = cleanString(sCurrentLine);
					int j = 0, k = 0;
					
					while(j<getNumUsefulColumns() && k<sCurrentLine.length()) {												
						if(isUseful(k)) {							
							if(sCurrentLine.charAt(k) == '1') {															
								setElement(i, j);								
							}							
							j++;
						}						
						k++;
					}					
					i++;
				}				
			}
			
			if (br != null)
				br.close();

			if (fr != null)
				fr.close();	
						
		} catch (IOException e) {
			System.out.println("I/O error");
		}	
	
	}
	
	private String cleanString(String str) {
		str = str.replace(SEPARATOR, "");		
		return str.replace(ROW_DELIMITER, "");
	}
	
	public void createNiFiles(String dirPath, String fileName) {
		int nRigheTolte = 0;
		int rand;
		int i = 1;
		while(nRigheTolte < matrixRows){
			rand = 1 + (int)(Math.random() * (((matrixRows-nRigheTolte) - 1) + 1));
			String matrixOutN = "";
			matrixOutN = getMatrixRows(nRigheTolte, nRigheTolte+rand);
			IOFile.writeOutputData(matrixOutN,dirPath+"/"+fileName+"_N"+i+"."+EXTENSION_INPUT);
			nRigheTolte+=rand;
			i++;
		}
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
