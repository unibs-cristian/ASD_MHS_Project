package objects;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;

public class Component {
	private final static String COMMENT_DELIMITER = ";;;";
	private final static String ROW_DELIMITER = "-";
	private final static String SEPARATOR = " ";
	 
	private BitSet usefulColumns;		// BitSet di lunghezza 'inputFileCols'. Gli elementi aventi valore 'true' indicano le colonne utili
	private ArrayList<BitSet> mhsList;	// BitSet che contiene l'elenco degli mhs
	private int inputFileCols;     		// Indica il numero di colonne della matrice contenuta nel file di input, cioe' la cardinalita' del dominio.
	
	// Il costruttore invoca il metodo per leggere la matrice da file
	public Component(String file) {
		readComponentFromFile(file);
	}
	
	public BitSet getUsefulColum() {
		return usefulColumns;
	}
	
	public BitSet getMHS(int index) {
		return mhsList.get(index);
	}
	
	public int getN_MHS() {
		return mhsList.size();
	}
	
	public int getInputFileCols() {
		return inputFileCols;
	}
	
	private void readComponentFromFile(String path) {
		FileReader fr = null;
		BufferedReader br;
		try {
			fr = new FileReader(path);
		} catch (FileNotFoundException e) {
			System.out.println("File not found.");
		}
		br = new BufferedReader(fr);

		String sCurrentLine;

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
					sCurrentLine = cleanString(sCurrentLine);
					for(int i=0; i<inputFileCols; i++) {
						// Se nella posizione i-esima si trova un 1, allora la relativa colonna della matrice e' useful 
						if(sCurrentLine.charAt(i) == '1')
							usefulColumns.set(i);
					}
				} while((sCurrentLine = br.readLine()) != null);		
				// Creazione del BitSet rappresentante la matrice
				
				try {
					fr = new FileReader(path);
				} catch (FileNotFoundException e) {
					System.out.println("File not found.");
				}
				br = new BufferedReader(fr);
						
				BitSet mhs;
				mhsList = new ArrayList<BitSet>();
				while ((sCurrentLine = br.readLine()) != null) {
					if(!sCurrentLine.startsWith(COMMENT_DELIMITER)) {
						mhs = new BitSet(inputFileCols);
						sCurrentLine = cleanString(sCurrentLine);
						int j = 0, k = 0;
						
						while(j<usefulColumns.cardinality() && k<inputFileCols) {												
							if(usefulColumns.get(k)) {							
								if(sCurrentLine.charAt(k) == '1') {															
									mhs.set(j);								
								}							
								j++;
							}						
							k++;
						}					
						mhsList.add(mhs);
					}				
				}
			}
			else {
				//non è presente una matrice nel file d'ingresso
				inputFileCols = 0;
				mhsList = new ArrayList<BitSet>();
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
}
