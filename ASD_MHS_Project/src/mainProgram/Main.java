package mainProgram;
import java.io.File;
import java.util.ArrayList;
import java.util.BitSet;

import objects.DistributedHypothesis;
import objects.DistributedSolution;
import objects.Instance;
import objects.MonolithicHypothesis;
import objects.MonolithicSolution;
import objects.Problem;
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
	private final static String MSG_FILE_NOT_FOUND = "Esecuzione terminata, il file specificato non esiste";
	private final static String TITOLO_SCELTA_ALG = "Seleziona l'algoritmo desiderato";
	private final static String[] OPZIONI_SCELTA_ALG = {"Monolitico","Distribuito"};
	
	/**
	 * Main program
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		boolean hasTimeLimit = false;
		int timeLimit = 0;
		String outputFilePath = "";
		MyMenu menu = new MyMenu(TITOLO_SCELTA_ALG, OPZIONI_SCELTA_ALG);
		// Monolitico o distribuito (scelta utente tramite menu')
		int scelta = menu.scegliNZ();
		
		// Viene chiesto all'utente di fissare l'eventuale durata massima dell'elaborazione
		hasTimeLimit = UserInput.yesOrNo(MSG_EXECUTION_TIME_1);
		if(hasTimeLimit)
			timeLimit = UserInput.leggiInt(MSG_EXECUTION_TIME_2);
		
		// Lettura file di input
		String path = IOFile.selectFile(EXTENSION_INPUT);
		if(path!=null) {
			File fileToOpen = new File(path);		
			if(!fileToOpen.exists())
				System.out.println(MSG_FILE_NOT_FOUND);
			else {				
				Instance in = new Instance(path);
				if(in != null) {
					switch(scelta) {
						case 1: // Calcolo monolitico
							MonolithicHypothesis mh = new MonolithicHypothesis(in.getNumUsefulColumns(), in.getMatrixNumRows());
							MonolithicSolution monoSol = new MonolithicSolution(in);
							Problem mono = new Problem(in, mh, monoSol);
							if(hasTimeLimit)
								mono.setTimeLimit(timeLimit);
							mono.exploreH();
															
							System.out.print(mono.getSol().getStringForFile());
							
							// Scrittura file di output
							outputFilePath = path.substring(0, path.lastIndexOf("."+EXTENSION_INPUT))+"."+EXTENSION_OUTPUT;
							IOFile.writeOutputData(mono.getSol().getStringForFile(),outputFilePath);
							break;
						case 2:
							// Creazione cartella contenente l'input cumulativo per il calcolo distribuito. 
							String newDirPath = path.substring(0, path.lastIndexOf("."+EXTENSION_INPUT))+EXTENSION_DIR;
							File newDir = new File(newDirPath);
							int fileCounter;
							// Se la cartella _dist per quell'istanza esiste gia', e' possibile riutilizzare i file al suo interno oppure rimuoverne il contenuto 
							if(newDir.exists()) {						
								if(UserInput.yesOrNo(MSG_DELETE_FOLDER)) {
									IOFile.deleteFileInFolder(newDir);
									//Creazione dei file N_i e dei componenti
									fileCounter = in.createNiFiles(newDirPath, path.substring(path.lastIndexOf("\\")+1, path.lastIndexOf(".")));
								}
								else
									fileCounter = newDir.listFiles().length;
							}
							else {
								newDir.mkdir();
								//Creazione dei file N_i e dei componenti
								fileCounter = in.createNiFiles(newDirPath, path.substring(path.lastIndexOf("\\")+1, path.lastIndexOf(".")));
							}				 					
							DistributedSolution distSol = new DistributedSolution(in);
							distSol.setnFiles(fileCounter);
							File[] files = newDir.listFiles();
							ArrayList<ArrayList<BitSet>> hsList = new ArrayList<>();
							
							ArrayList<BitSet> Ci = new ArrayList<>();
							ArrayList<BitSet> hsList_iShrink;
							BitSet hsShrink;
							int w;
							
							double totalTime = 0;
							for(File f:files) {
								Instance i = new Instance(f.getPath());
								MonolithicHypothesis mh_i = new MonolithicHypothesis(i.getNumUsefulColumns(), i.getMatrixNumRows()); 
								MonolithicSolution mSol_i = new MonolithicSolution(i);
								Problem p_i = new Problem(i, mh_i, mSol_i);
								p_i.exploreH();
								totalTime += p_i.getSol().getTime();
								
								Ci = p_i.getSol().getMhsSetExpanded();
								hsList_iShrink = new ArrayList<>();
								for(int j = 0; j < Ci.size(); j++) {
									w = 0;
									hsShrink = new BitSet(in.getNumUsefulColumns());
									for(int k=0; k < in.getInputFileCols(); k++) {
										if(in.isUsefulCol(k)) {
											if(Ci.get(j).get(k))
												hsShrink.set(w);
											w++;
										}
									}
									hsList_iShrink.add(hsShrink);
								}
								hsList.add(hsList_iShrink);
							}
							System.out.println(hsList);
							distSol.setnGlobalMHS(hsList.size());
							DistributedHypothesis dh = new DistributedHypothesis(in.getNumUsefulColumns(), fileCounter, hsList);
							Problem dist = new Problem(in, dh, distSol);
							
							if(hasTimeLimit)
								dist.setTimeLimit(timeLimit);
							dist.exploreH();
															
							System.out.print(dist.getSol().getStringForFile());
							
							// Scrittura file di output
							outputFilePath = newDirPath+"."+EXTENSION_OUTPUT;
							IOFile.writeOutputData(dist.getSol().getStringForFile(),outputFilePath);
							
							//Durata totale (calcolo Ci e composizione)
							totalTime+=dist.getSol().getTime();
							System.out.println("\n Total time: "+totalTime);
							break;
					}
				}
			}
		}
	}
}
