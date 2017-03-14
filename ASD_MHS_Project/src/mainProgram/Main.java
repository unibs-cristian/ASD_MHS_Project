package mainProgram;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import objects.Instance;
import objects.MonolithicHypothesis;
import objects.Problem;
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
	private final static String MSG_DELETE_FOLDER = "Esiste già una cartella _dist per questo benchmark, vuoi eliminare tutti i file al suo interno?";
	private final static String TITOLO_SCELTA_ALG = "Seleziona l'algoritmo desiderato";
	private final static String[] OPZIONI_SCELTA_ALG = {"Monolitico","Distribuito"};
	/*
	private final static String MSG_MONOLITHIC_INTERRUPT = "Per interrompere l'elaborazione inserire Q e premere ENTER";
	private final static String EXIT_KEY = "Q";
	*/
	
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
		// Monolitico o distribuito (scelta utente)
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
					Solution sol = new Solution(in);
					Problem mono = new Problem(in, mh, sol);
					if(hasTimeLimit)
						mono.setTimeLimit(timeLimit);
					mono.exploreH();
													
					// Modulo per il calcolo monolitico dei MHS
					System.out.print(mono.getSol().getStringForFile());
					
					// Scrittura file di output
					String outputFilePath = inputFilePath.substring(0, inputFilePath.lastIndexOf("."+EXTENSION_INPUT))+"."+EXTENSION_OUTPUT;
					writeOutputData(mono.getSol().getStringForFile(),outputFilePath);
					break;
				case 2:
					//Creo cartella
					String newDirPath = inputFilePath.substring(0, inputFilePath.lastIndexOf("."+EXTENSION_INPUT))+EXTENSION_DIR;
					File newDir = new File(newDirPath);
					if(newDir.exists()) {
						boolean deleteFileInFolder = UserInput.yesOrNo(MSG_DELETE_FOLDER);
						if(deleteFileInFolder)
							deleteFileInFolder(newDir);
						else
							break;
					}
					else
						newDir.mkdir();
					//Creazione vari file N_i
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
	 * @return void
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
	
	public static void deleteFileInFolder(File folder) {
	    File[] files = folder.listFiles();
	    if(files!=null) //some JVMs return null for empty dirs
	        for(File f: files) 
	            f.delete();
	}
}
