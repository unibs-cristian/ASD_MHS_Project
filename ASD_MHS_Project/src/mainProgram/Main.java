package mainProgram;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;

import objects.DistributedHypothesis;
import objects.DistributedSolution;
import objects.Instance;
import objects.MonolithicHypothesis;
import objects.MonolithicSolution;
import objects.Problem;
import objects.Test;
import ioUtils.*;

/**
 * Classe contenente il metodo Main e i metodi corrispondenti ai vari moduli che costituiscono l'elaborato
 *
 */
public class Main{
	
	private final static String EXTENSION_INPUT = "matrix";
	private final static String EXTENSION_OUTPUT = "mhs";
	private final static String TAG_DIST = "_dist";
	private final static String MSG_EXECUTION_CANCELED = "Esecuzione annullata.";
	private final static String MSG_RUN_ALL = "Si desidera eseguire l'algoritmo su tutti i file di una cartella? ";
	private final static String MSG_RECREATE_ALL_PARTITIONS = "Si desidera eliminare tutte le partizioni già presenti? ";
	private final static String MSG_EXECUTION_TIME_1 = "Si desidera fissare una durata massima per l'elaborazione? ";
	private final static String MSG_EXECUTION_TIME_2 = "Inserire la durata in secondi ";
	private final static String MSG_DELETE_FOLDER = "Esiste già una cartella _dist per questo benchmark, vuoi eliminare tutti i file al suo interno? ";
	private final static String MSG_FILE_NOT_FOUND = "Esecuzione terminata, il file specificato non esiste";
	private final static String MSG_INPUT_N_ROWS = "Inserire numero righe ";
	private final static String MSG_INPUT_N_COLS = "Inserire numero colonne ";
	private final static String MSG_INPUT_PROBABILITY = "Inserire probabilità presenza di un 1 (0-100) ";
	private final static String MSG_INPUT_N_FILE = "Inserire numero di test che si vogliono generare ";
	private final static String MSG_START_FINAL_PHASE_DIST = "Inizio fase finale del calcolo distribuito";
	private final static String MSG_INPUT_FILE = "\nInput File: ";
	private final static String TITLE_CHOICE = "Seleziona l'opzione desiderata";
	private final static String[] OPTIONS_CHOICE = {"Monolitico","Distribuito","Confronto","Crea"};
	
	/**
	 * Main program
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		boolean hasTimeLimit = false, runAll = false, recreateAllPartitions = false;
		int timeLimit = 0;
		String path = "", outputFilePath = "";
		File[] inputFiles;
		File fileDir;
		
		MyMenu menu = new MyMenu(TITLE_CHOICE, OPTIONS_CHOICE);
		// Monolitico o distribuito (scelta utente tramite menu')
		int choice = menu.chooseNZ();
		switch(choice) {
			case 1:
			case 2:
				// Viene chiesto all'utente di fissare l'eventuale durata massima dell'elaborazione
				hasTimeLimit = UserInput.yesOrNo(MSG_EXECUTION_TIME_1);
				if(hasTimeLimit)
					timeLimit = UserInput.leggiInt(MSG_EXECUTION_TIME_2);
				
				// Viene chiesto se si vuole eseguire l'algoritmo su tutti i file contenuti in una cartella
				runAll = UserInput.yesOrNo(MSG_RUN_ALL);
				
				// Lettura file di input
				if(runAll) {
					path = IOFile.selectDir();
					if(choice == 2)
						recreateAllPartitions = UserInput.yesOrNo(MSG_RECREATE_ALL_PARTITIONS);
				}
				else
					path = IOFile.selectFile(EXTENSION_INPUT);
				//String path = "C:\\Users\\Daniele\\Desktop\\UniBS LM\\I\\alg e str dati\\progetto\\benchmarks1\\74L85.001.matrix";
				
				if(path!=null) {
					if(runAll){
						fileDir = new File(path);
						inputFiles = fileDir.listFiles();
					}
					else {
						inputFiles = new File[1];
						inputFiles[0] = new File(path);
					}
					
					for(File inF:inputFiles) {
						if(UserInput.check_extension(inF.getName(), EXTENSION_INPUT)){
							System.out.println(MSG_INPUT_FILE+inF.getName());
							path = inF.getAbsolutePath();
							if(path!=null) {
								File fileToOpen = new File(path);		
								if(!fileToOpen.exists())
									System.out.println(MSG_FILE_NOT_FOUND);
								else {				
									Instance in = new Instance(path);
									if(in != null) {
										switch(choice) {
											case 1: // Calcolo monolitico
												MonolithicHypothesis mh = new MonolithicHypothesis(in.getNumUsefulColumns(), in.getMatrixNumRows());
												MonolithicSolution monoSol = new MonolithicSolution(in);
												Problem mono = new Problem(in, mh, monoSol);
												if(hasTimeLimit)
													mono.setTimeLimit(timeLimit);
												mono.exploreH();
																				
												//System.out.print(mono.getSol().getStringForFile());
												
												// Scrittura file di output
												outputFilePath = path.substring(0, path.lastIndexOf("."+EXTENSION_INPUT))+"."+EXTENSION_OUTPUT;
												IOFile.writeOutputData(mono.getSol().getStringForFile(),outputFilePath);
												break;
											case 2:
												// Creazione cartella contenente l'input cumulativo per il calcolo distribuito. 
												String newDirPath = path.substring(0, path.lastIndexOf("."+EXTENSION_INPUT))+TAG_DIST;
												File newDir = new File(newDirPath);
												int fileCounter = 0;
												// Se la cartella _dist per quell'istanza esiste gia', e' possibile riutilizzare i file al suo interno oppure rimuoverne il contenuto 
												if(newDir.exists()) {						
													if((!runAll&&UserInput.yesOrNo(MSG_DELETE_FOLDER))||(runAll&&recreateAllPartitions)) {
														IOFile.deleteFileInFolder(newDir);
														//Creazione dei file N_i e dei componenti
														in.createNiFiles(newDirPath, path.substring(path.lastIndexOf("\\")+1, path.lastIndexOf(".")));
													}
												}
												else {
													newDir.mkdir();
													//Creazione dei file N_i e dei componenti
													in.createNiFiles(newDirPath, path.substring(path.lastIndexOf("\\")+1, path.lastIndexOf(".")));
												}				 					
												DistributedSolution distSol = new DistributedSolution(in);
												
												File[] files = newDir.listFiles();
												ArrayList<ArrayList<BitSet>> hsList = new ArrayList<>();
												
												ArrayList<BitSet> Ci = new ArrayList<>();
												ArrayList<BitSet> hsList_iShrink;
												BitSet hsShrink;
												int w, countMHS = 0;
												double totalTime = 0;
												boolean componentExplorationNotCompleted = false;
												for(File f:files) {
													if(UserInput.check_extension(f.getName(), EXTENSION_INPUT)) {
														System.out.println("Collezione N" + fileCounter);
														fileCounter++;
														Instance i = new Instance(f.getPath());
														MonolithicHypothesis mh_i = new MonolithicHypothesis(i.getNumUsefulColumns(), i.getMatrixNumRows()); 
														MonolithicSolution mSol_i = new MonolithicSolution(i);
														Problem p_i = new Problem(i, mh_i, mSol_i);
														if(hasTimeLimit)
															p_i.setTimeLimit(timeLimit);
														p_i.exploreH();
														totalTime += p_i.getSol().getTime();
														componentExplorationNotCompleted = p_i.hasExplorationStopped();
														if(componentExplorationNotCompleted)
															break;
														
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
														countMHS+=hsList_iShrink.size();
														hsList.add(hsList_iShrink);
													}
												}
												distSol.setnFiles(fileCounter);
												//System.out.println(hsList);
												System.out.println(MSG_START_FINAL_PHASE_DIST);
												//TODO contiene doppioni
												distSol.setnGlobalMHS(countMHS);
												DistributedHypothesis dh = new DistributedHypothesis(in.getNumUsefulColumns(), fileCounter, hsList);
												Problem dist = new Problem(in, dh, distSol);
												
												if(!componentExplorationNotCompleted) {
													if(hasTimeLimit)
														dist.setTimeLimit(timeLimit);
													dist.exploreH();
												}
																				
												//System.out.print(dist.getSol().getStringForFile());
												
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
				}
				else
					System.out.println(MSG_EXECUTION_CANCELED);
				break;
			case 3:
				path = IOFile.selectDir();
				if(path!=null) {
					fileDir = new File(path.substring(0, path.lastIndexOf("\\")));
					inputFiles = fileDir.listFiles();
					String benchmark;
					StringBuilder report = new StringBuilder();
					for(File inF:inputFiles) {
						if(UserInput.check_extension(inF.getName(), EXTENSION_OUTPUT)&&inF.getName().contains(TAG_DIST)){
							System.out.println(inF.getName());
							String pathDist = inF.getAbsolutePath();
							String pathMono = inF.getAbsolutePath().replace(TAG_DIST, "");
							File fileSolMono = new File(pathMono);
							if(fileSolMono.exists()) {
								Instance solDist = new Instance(pathDist);
								Instance solMono = new Instance(pathMono);
								benchmark = inF.getName().substring(0, inF.getName().indexOf(TAG_DIST));
								if(isSolComplete(pathDist)&&isSolComplete(pathMono)) {
									if(solDist.equals(solMono))
										report.append(benchmark+" sol uguali\n");
									else
										report.append(benchmark+" sol diverse\n");
								}
								else {
									report.append(benchmark+" non completo\n");
								}
							}
						}
					}
					IOFile.writeOutputData(report.toString(),fileDir.getAbsolutePath()+"\\report.txt");
				}
				break;
			case 4:
				int nRows = UserInput.leggiInt(MSG_INPUT_N_ROWS);
				int nCols = UserInput.leggiInt(MSG_INPUT_N_COLS);
				int probability1 = UserInput.leggiInt(MSG_INPUT_PROBABILITY);
				int nFile = UserInput.leggiInt(MSG_INPUT_N_FILE);
				Test t;
				for(int i=0; i<nFile; i++) {
					t = new Test(nRows, nCols, probability1);
					IOFile.writeOutputData(t.getStringForFile(),System.currentTimeMillis()+"_"+i+"."+EXTENSION_INPUT);
				}
				break;
		}
	}
	
	public static boolean isSolComplete(String path) {
		FileReader fr = null;
		BufferedReader br;
		try {
			fr = new FileReader(path);
		} catch (FileNotFoundException e) {
			System.out.println("File not found.");
		}
		br = new BufferedReader(fr);

		String sCurrentLine;
		boolean complete = false;

		try {
			while ((sCurrentLine = br.readLine()) != null) {
				if(sCurrentLine.contains("Esecuzione completata")) {
					complete = true;
					break;
				}
				if(sCurrentLine.contains("Esecuzione interrotta")) {
					complete = false;
					break;
				}
			}
			
			if (br != null)
				br.close();

			if (fr != null)
				fr.close();	
						
		} catch (IOException e) {
			System.out.println("I/O error");
		}	
	
		return complete;
	}
}
