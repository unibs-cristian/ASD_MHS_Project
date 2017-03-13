package mainProgram;
import java.util.ArrayList;
import java.util.Collections;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import objects.Hypothesis;
import objects.Instance;
import objects.MonolithicHypothesis;
import objects.Solution;
import ioUtils.*;

/**
 * Classe contenente il metodo Main e i metodi corrispondenti ai vari moduli che costituiscono l'elaborato
 *
 */
public class Main{
	
	private final static String EXTENSION_INPUT = "matrix";
	private final static String EXTENSION_OUTPUT = "mhs";
	private final static String EXTENSION_DIR = "_dist";
	private final static String MSG_EXECUTION_TIME_1 = "Si desidera fissare una durata massima per l'elaborazione?";
	private final static String MSG_EXECUTION_TIME_2 = "Inserire la durata in secondi";
	private final static String MSG_MONOLITHIC_START = "Iniziato calcolo monolitico dei MHS";
	private final static String MSG_DELETE_FOLDER = "Esiste già una cartella _dist per questo benchmark, vuoi eliminarla con tutti i file in essa contenuti?";
	private final static String TITOLO_SCELTA_ALG = "Seleziona l'algoritmo desiderato";
	private final static String[] OPZIONI_SCELTA_ALG = {"Monolitico","Distribuito"};
	/*
	private final static String MSG_MONOLITHIC_INTERRUPT = "Per interrompere l'elaborazione inserire Q e premere ENTER";
	private final static String EXIT_KEY = "Q";
	*/
	private final static int NANO_TO_SEC = 1000000000;
	
	private static ArrayList<Hypothesis> current;
	private static Solution sol;		
	private static ArrayList<Hypothesis> next;
	private static String inputFilePath;
	private static boolean hasTimeLimit = false;
	public static boolean keyPressed = false;
	private static int timeLimit; 
	
	/**
	 * Main program
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		MyMenu menu = new MyMenu(TITOLO_SCELTA_ALG, OPZIONI_SCELTA_ALG);
		//Monolitico o distribuito (scelta utente)
		int scelta = menu.scegliNZ();
		
		// Viene chiesto all'utente di fissare l'eventuale durata massima dell'elaborazione
		hasTimeLimit = UserInput.yesOrNo(MSG_EXECUTION_TIME_1);
		if(hasTimeLimit)
			timeLimit = UserInput.leggiInt(MSG_EXECUTION_TIME_2);
		
		// Lettura file di input
		Instance in = readInputData();
		if(in != null) {
			switch(scelta) {
				case 1:
					MonolithicHypothesis mh = new MonolithicHypothesis(in.getNumUsefulColumns(), in.getMatrixNumRows());
					exploreH(in, mh);
								
					// Modulo per il calcolo monolitico dei MHS
					System.out.print(sol.getStringForFile());
					
					// Scrittura file di output
					String outputFilePath = inputFilePath.substring(0, inputFilePath.lastIndexOf("."+EXTENSION_INPUT))+"."+EXTENSION_OUTPUT;
					writeOutputData(sol.getStringForFile(),outputFilePath);
					break;
				case 2:
					int a = 0;
					//Creo cartella
					String newDirPath = inputFilePath.substring(0, inputFilePath.lastIndexOf("."+EXTENSION_INPUT))+EXTENSION_DIR;
					File newDir = new File(newDirPath);
					if(newDir.exists()) {
						boolean deleteFolder = UserInput.yesOrNo(MSG_DELETE_FOLDER);
						if(deleteFolder)
							deleteFolder(newDir);
					}
					newDir = new File(newDirPath);
					newDir.mkdir();
					while(a==0){
						int nRigheTolte = 0;
						int rand;
						int i = 1;
						while(nRigheTolte < in.getMatrixNumRows()){
							rand = 1 + (int)(Math.random() * (((in.getMatrixNumRows()-nRigheTolte) - 1) + 1));
							String matrixOutN = "";
							matrixOutN = in.getMatrixRows(nRigheTolte, nRigheTolte+rand);
							writeOutputData(matrixOutN,newDirPath+"/"+inputFilePath.substring(inputFilePath.lastIndexOf("\\"),inputFilePath.lastIndexOf(".")-1)+"_N"+i+"."+EXTENSION_INPUT);
							nRigheTolte+=rand;
							i++;
						}
						a = UserInput.leggiInt("");
					}
					break;
			}
		}
	}
	
	/**
	 * Modulo per la lettura del file di input
	 * 
	 * @return l'oggetto Instance che rappresenta i dati in input
	 */
	private static Instance readInputData() {
		File f;
		// Lettura tramite File Chooser e controllo del formato del file scelto
		do {
			f = UserInput.chooseInputFile();
			if(f==null)
				return null;
		} while(!UserInput.check_extension(f.getName(), EXTENSION_INPUT));	
		inputFilePath = f.getPath();
		Reader r = new Reader(f.getPath());
		return r.read();
	}
	
	/**
	 * Modulo per la scrittura del file di output
	 * 
	 * @return l'oggetto Instance che rappresenta i dati in input
	 */
	private static void writeOutputData(String output,String filename) {
		//File f;
		BufferedWriter bw = null;
		FileWriter fw = null;
		
		try {
			fw = new FileWriter(filename);
			bw = new BufferedWriter(fw);
			bw.write(output);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(bw!=null)
					bw.close();
				if(fw!=null)
					fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
		
	/**
	 * Modulo per effettuare il calcolo monolitico dei MHS sui dati in ingresso 
	 * 
	 * @param in : l'oggetto Instance che rappresenta i dati in input
	 */
	private static void exploreH(Instance in, Hypothesis h) {
		boolean timeLimitReached = false;
		//String key = "";
		System.out.println(MSG_MONOLITHIC_START);
		/*
		if(!hasTimeLimit)
			System.out.println(MSG_MONOLITHIC_INTERRUPT);
		*/
		current = new ArrayList<>();
		sol = new Solution(in);		
		next = new ArrayList<>();
		
		long startTime = System.nanoTime();
		h.setField(in);
		current.add(h);		
		do {
			next.clear();
			for(int i=0; i<current.size(); i++) {
				//key = UserInput.leggiString("");
				//System.out.println(current.get(i).getBin());
				if(current.get(i).check()) {
					sol.add(current.get(i));
					current.remove(i);
					i--;//Se viene rimosso un elemento tutti gli altri shiftano a sx quindi devo decrementare i per non saltarne uno
				}
				else {
					generateChildren(current.get(i), in);
				}
				// Se e' stato fissato un limite di tempo per l'elaborazione, controllo che questo non sia stato superato
				if(hasTimeLimit) {
					if(((double)(System.nanoTime() - startTime)/NANO_TO_SEC) >= timeLimit) {
						timeLimitReached = true;
						break;
					}
				}
				/*
				else { // Se c'e' un tasto per uscire, controllo se questo e' stato premuto
					if(key.equalsIgnoreCase(EXIT_KEY)) {
						keyPressed = true;
						break;
					}
				}
				*/
			}			
			Collections.sort(next, Collections.reverseOrder());
			current = new ArrayList<>(next);
			//System.out.println(next);
			sol.incrementLevelReached();
		} while(!current.isEmpty() && !timeLimitReached && !keyPressed);		
		long endTime = System.nanoTime();
		double executionTime = ((double)(endTime - startTime))/NANO_TO_SEC;
		sol.setTime(executionTime);
		System.out.println("Monolithic Execution time: " + executionTime);
		if(!timeLimitReached && !keyPressed)
			sol.setComplete();						
	}
	
	
	private static void generateChildren(Hypothesis h, Instance in) {
		Hypothesis h1 = h.clone();
		Hypothesis h2 = h.clone();
		Hypothesis pred = h.clone(); 
		Hypothesis last = h.clone();
		//Hypothesis fin = h.clone();
		int cont = 0;
		int predIndex;
		boolean cond = false;
		
		if(h.isEmpty()) {
			for(int i=0; i<h.getDimension(); i++) {		
				h1 = h.clone();
				h1.setBin(i);
				h1.setField(in);
				h1.propagate(h);
				next.add(h1);
			}
			return;
		}
		
		pred = h.clone();
		predIndex = current.indexOf(h);
		do {
			//pred = prev(pred);
			predIndex--;
			if(predIndex<0)
				pred = null;
			else
				pred = current.get(predIndex);
		} while(!(pred == null || h.getHammingDistance(pred) == 2));
				
		if(pred!=null)
			cont = 0;
			
		for(int i=h.getBin().nextSetBit(0)-1; i>=0; i--) {
			if(pred!=null) {
				h1 = h.clone();
				h1.setBin(i);
				h1.setField(in);
				h1.propagate(h);
				
				cond = true;
				for(int j=h.getBin().nextSetBit(0); j<h.getBin().length(); j++) {
					h2 = h1.clone();
					if(h2.getBin().get(j)!=false) {
						h2.set(j,false);
						//TODO codice tratto dallo pseudo-codice (non è corretto)
						/*
						if(!h2.equals(pred)) {
							cond = false;
							fin = h1.clone();
							fin.set(h.getBin().length()-1, false);
							//pred <= fin
							while(pred!=null && pred.compareTo(fin)<=0) {
								do {
									pred = prev(pred);
								} while(!(pred == null || h.getHammingDistance(pred) == 2));
							}
							break;							
						}
						else {
							h1.propagate(h2);
							do {
								pred = prev(pred);
							} while(!(pred == null || h.getHammingDistance(pred) == 2));
						}
						*/
						//metodo rozzo ma corretto (sembra più efficiente)
						/*
						if(!current.contains(h2)) {
							cond = false;
							break;
						}
						*/
						
						//metodo più efficiente
						//TODO controllare gli else
						if(h2.compareTo(pred)!=0) {
							while(pred!=null && pred.compareTo(h2)==-1) {
								//pred = prev(pred);
								predIndex--;
								if(predIndex<0)
									pred = null;
								else
									pred = current.get(predIndex);
							}
							
							if(pred == null || h2.compareTo(pred)!=0) {
								cond = false;
								break;
							}
							else {
								h1.propagate(h2);
							}
						}
						else {
							h1.propagate(h2);
						}
						
					}
				}

				if(cond) {
					if(cont == 0)
						next.add(h1);
					else {
						if(next.isEmpty())
							next.add(h1);
						else
							next.add(next.indexOf(last), h1);
					}
					
					last = h1.clone();
					cont++;
				}
			}
		}
			
	}
	/*
	private static Hypothesis prev(Hypothesis h) {
		if(current.indexOf(h) - 1 < 0)
			return null;
		else 
			return current.get(current.indexOf(h) - 1);
	}
	*/	
	
	public static void deleteFolder(File folder) {
	    File[] files = folder.listFiles();
	    if(files!=null) { //some JVMs return null for empty dirs
	        for(File f: files) {
	            if(f.isDirectory()) {
	                deleteFolder(f);
	            } else {
	                f.delete();
	            }
	        }
	    }
	    folder.delete();
	}
}
