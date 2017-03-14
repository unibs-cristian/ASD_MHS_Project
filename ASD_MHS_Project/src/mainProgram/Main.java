package mainProgram;
import java.io.File;
import java.util.ArrayList;
import java.util.BitSet;

import objects.DistributedHypothesis;
import objects.DistributedSolution;
import objects.Hypothesis;
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
	
	/**
	 * Main program
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		boolean hasTimeLimit = false;
		int timeLimit = 0;
		MyMenu menu = new MyMenu(TITOLO_SCELTA_ALG, OPZIONI_SCELTA_ALG);
		// Monolitico o distribuito (scelta utente)
		int scelta = menu.scegliNZ();
		
		// Viene chiesto all'utente di fissare l'eventuale durata massima dell'elaborazione
		hasTimeLimit = UserInput.yesOrNo(MSG_EXECUTION_TIME_1);
		if(hasTimeLimit)
			timeLimit = UserInput.leggiInt(MSG_EXECUTION_TIME_2);
		
		// Lettura file di input
		String path = IOFile.selectFile(EXTENSION_INPUT);		
		Instance in = new Instance(path);
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
					String outputFilePath = path.substring(0, path.lastIndexOf("."+EXTENSION_INPUT))+"."+EXTENSION_OUTPUT;
					IOFile.writeOutputData(mono.getSol().getStringForFile(),outputFilePath);
					break;
				case 2:
					//Creo cartella
					String newDirPath = path.substring(0, path.lastIndexOf("."+EXTENSION_INPUT))+EXTENSION_DIR;
					File newDir = new File(newDirPath);
					if(newDir.exists()) {
						boolean deleteFileInFolder = UserInput.yesOrNo(MSG_DELETE_FOLDER);
						if(deleteFileInFolder)
							IOFile.deleteFileInFolder(newDir);
						else
							break;
					}
					else
						newDir.mkdir();
					//Creazione vari file N_i					
					int fileCounter = in.createNiFiles(newDirPath, path.substring(path.lastIndexOf("\\")+1, path.lastIndexOf(".")));					
					DistributedSolution distSol = new DistributedSolution(in);
					distSol.setnFiles(fileCounter);
					File[] files = newDir.listFiles();
					ArrayList<Hypothesis> hsList = new ArrayList<>();
					double totalTime = 0;
					int nFile = 0;
					
					for(File f:files) {
						Instance i = new Instance(f.getPath());
						MonolithicHypothesis mh_i = new MonolithicHypothesis(i.getNumUsefulColumns(), i.getMatrixNumRows()); 
						Solution sol_i = new Solution(i);
						Problem p_i = new Problem(i, mh_i, sol_i);
						p_i.exploreH();
						totalTime += p_i.getSol().getTime();
						DistributedHypothesis dh;
						Hypothesis hCurrent;
						for(int j=0; j<p_i.getSol().getMhsSet().size(); j++) {
							hCurrent = p_i.getSol().getMhsSet().get(j);
							dh = new DistributedHypothesis(hCurrent.getDimension(), fileCounter);
							dh.setBin((BitSet)hCurrent.getBin().clone());
							dh.setVector(nFile);
							nFile ++;
							hsList.add(dh);
						}							
					}
					System.out.println(hsList);
					distSol.setnGlobalMHS(hsList.size());
					
					distSol.setTime(totalTime);
					break;
			}
		}
	}
}
