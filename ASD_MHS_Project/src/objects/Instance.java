package objects;

import ioUtils.IOFile;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.BitSet;
import java.util.Random;

public class Instance{
	private final static String EXTENSION_INPUT = "matrix";
	private final static String COMMENT_DELIMITER = ";;;";
	private final static String ROW_DELIMITER = "-";
	private final static String SEPARATOR = " ";
	 
	private BitSet usefulColumns;  // BitSet di lunghezza 'inputFileCols'. Gli elementi aventi valore 'true' indicano le colonne utili
	private BitSet matrix;		   // BitSet che contiene i singoli elementi della matrice
	private int inputFileCols;     // Indica il numero di colonne della matrice contenuta nel file di input, cioe' la cardinalita' del dominio.
	private int matrixCols;        // Indica il numero di colonne della matrice composta solo dalle colonne utili.
	private int matrixRows;        // Indica il numero di righe della matrice, cioe' il numero di elementi della collezione.
	
	// Il costruttore invoca il metodo per leggere la matrice da file
	public Instance(String file) {
		readMatrixFromFile(file);
	}
	
	public Instance(BitSet usefulColumns, int inputFileCols) {
		this.usefulColumns = usefulColumns;
		this.inputFileCols = inputFileCols;
		this.matrix = new BitSet();		   
		this.matrixCols = usefulColumns.cardinality();
		this.matrixRows = 0; 
	}
	
	public BitSet getMatrix() {
		return matrix;
	}
	
	public boolean isUsefulCol(int col) {
		return usefulColumns.get(col);
	}
	
	public int getMatrixNumRows() {
		return matrixRows;
	}
	
	public int getNumUsefulColumns() {
		return matrixCols;
	}
	
	public BitSet getMatrixColumn(int column) {
		BitSet col = new BitSet(matrixRows);
		for(int i=0; i<matrixRows; i++) 
			col.set(i, matrix.get(i*matrixCols + column));
	
		return col;				
	}
	
	public BitSet getMatrixRow(int rowIndex) {
		int k = 0;
		BitSet row = new BitSet(inputFileCols);
		for(int j=0; j<inputFileCols; j++) {
			if(usefulColumns.get(j)) {
				if(matrix.get(rowIndex*matrixCols+ k))
					row.set(j);
				k++;
			}
		}
		return row;
	}
	
	//Restituisce [firtRow,lastRow) (lastRow esclusa)
	private String getMatrixRows(int firstRow, int lastRow) {
		int k;
		StringBuilder matrixOut = new StringBuilder();
		for(int i=firstRow; i<lastRow; i++) {
			k = 0;
			if(i > firstRow)
				matrixOut.append("\n");
			for(int j=0; j<inputFileCols; j++) {
				if(usefulColumns.get(j)) {
					if(matrix.get(i*matrixCols+ k))
						matrixOut.append("1 ");
					else
						matrixOut.append("0 ");
					k++;
				}
				else
					matrixOut.append("0 ");
			}
			matrixOut.append("-");
		}
		return matrixOut.toString();
	}
	
	private void readMatrixFromFile(String path) {
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
			// Lettura riga per riga con rimozione di spazi bianchi e separatori di riga (si ottiene una sequenza di 0 e 1)
			while ((sCurrentLine = br.readLine()) != null) {
				if(!sCurrentLine.startsWith(COMMENT_DELIMITER)) {
					sCurrentLine = cleanString(sCurrentLine);
					break;
				}
			}
			
			if(sCurrentLine!=null&&sCurrentLine!="") {
				inputFileCols = sCurrentLine.length();
				usefulColumns = new BitSet(inputFileCols);
								
				do {
					numRows ++;
					sCurrentLine = cleanString(sCurrentLine);
					for(int i=0; i<inputFileCols; i++) {
						// Se nella posizione i-esima si trova un 1, allora la relativa colonna della matrice e' useful 
						if(sCurrentLine.charAt(i) == '1')
							setUsefulColumn(i);
					}
				} while((sCurrentLine = br.readLine()) != null);		
				// Creazione del BitSet rappresentante la matrice
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
						
						while(j<getNumUsefulColumns() && k<inputFileCols) {												
							if(isUseful(k)) {							
								if(sCurrentLine.charAt(k) == '1') {															
									matrix.set(i*matrixCols + j);								
								}							
								j++;
							}						
							k++;
						}					
						i++;
					}				
				}
			}
			else {
				//non è presente una matrice nel file d'ingresso
				matrixCols = 0;
				matrixRows = 0;
				inputFileCols = 0;
				matrix = new BitSet();
				usefulColumns = new BitSet();
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
	
	// Partiziona randomicamente per righe la matrice (utilizzato per creare le partizioni nel metodo distribuito)
	public void createNiFiles(String dirPath, String fileName) {
		int nRowsPicked = 0;
		int rand;
		int nFiles = 0;
		Random r = new Random();
		while(nRowsPicked < matrixRows){
			// 1 <= rand < righeRimanenti
			if((matrixRows-nRowsPicked) == 1)
				rand = 1;
			else
				rand = r.nextInt((matrixRows-nRowsPicked)-1)+1;
			String matrixOutN = "";
			matrixOutN = getMatrixRows(nRowsPicked, nRowsPicked+rand);
			IOFile.writeOutputData(matrixOutN,dirPath+"/"+fileName+"_N"+nFiles+"."+EXTENSION_INPUT);
			nRowsPicked+=rand;
			nFiles++;
		}
	}
	
	public int getInputFileCols() {
		return inputFileCols;
	}
	
	private void setUsefulColumn(int index) {
		usefulColumns.set(index);
	}
	
	private void createMatrix(int numRows) {
		matrixRows = numRows;
		matrixCols = usefulColumns.cardinality();
		matrix = new BitSet(numRows*matrixCols);
		
	}
	
	private boolean isUseful(int col) {
		return usefulColumns.get(col);
	}
	
	public String listUselessCols() {
		StringBuilder list = new StringBuilder();
		if(inputFileCols == matrixCols)
			return "-";
		else {
			for(int i=0; i < inputFileCols; i++)
				if(!usefulColumns.get(i))
					list.append(i+",");
				return list.toString().substring(0, list.toString().length()-1);
		}
	}

	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Instance other = (Instance) obj;
		if (inputFileCols != other.inputFileCols)
			return false;
		if (matrix == null) {
			if (other.matrix != null)
				return false;
		} else if (!matrix.equals(other.matrix))
			return false;
		if (matrixCols != other.matrixCols)
			return false;
		if (matrixRows != other.matrixRows)
			return false;
		if (usefulColumns == null) {
			if (other.usefulColumns != null)
				return false;
		} else if (!usefulColumns.equals(other.usefulColumns))
			return false;
		return true;
	}
}
