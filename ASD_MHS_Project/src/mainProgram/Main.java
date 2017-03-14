package mainProgram;
import java.io.File;
import objects.DistributedSolution;
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
	private final static String MSG_DELETE_FOLDER = "Esiste gi� una cartella _dist per questo benchmark, vuoi eliminare tutti i file al suo interno?";
	private final static String TITOLO_SCELTA_ALG = "Seleziona l'algoritmo desiderato";
	private final static String[] OPZIONI_SCELTA_ALG = {"Monolitico","Distribuito"};
	
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
					String outputFilePath = inputFilePath.substring(0, inputFilePath.lastIndexOf("."+EXTENSION_INPUT))+"."+EXTENSION_OUTPUT;
					IOFile.writeOutputData(mono.getSol().getStringForFile(),outputFilePath);
					break;
				case 2:
					//Creo cartella
					String newDirPath = inputFilePath.substring(0, inputFilePath.lastIndexOf("."+EXTENSION_INPUT))+EXTENSION_DIR;
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
					int nRigheTolte = 0;
					int rand;
					int i = 1;
					while(nRigheTolte < in.getMatrixNumRows()){
						rand = 1 + (int)(Math.random() * (((in.getMatrixNumRows()-nRigheTolte) - 1) + 1));
						String matrixOutN = "";
						matrixOutN = in.getMatrixRows(nRigheTolte, nRigheTolte+rand);
						IOFile.writeOutputData(matrixOutN,newDirPath+"/"+inputFilePath.substring(inputFilePath.lastIndexOf("\\"),inputFilePath.lastIndexOf(".")-1)+"_N"+i+"."+EXTENSION_INPUT);
						nRigheTolte+=rand;
						i++;
					}
					DistributedSolution distSol = new DistributedSolution(in);
					File[] files = newDir.listFiles();
					int fileCounter = 0;
					for(File f:files) {
						fileCounter++;
					}					
					distSol.setnFiles(fileCounter);
					break;
			}
		}
	}
}
