package mainProgram;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;

import objects.Component;
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
	private final static String MSG_USE_COMPONENTS = "Si desidera utilizzare componenti già creati (In caso di risposta negativa i componenti già presenti verranno cancellati)? ";
	private final static String MSG_EXECUTION_TIME_1 = "Si desidera fissare una durata massima per l'elaborazione? ";
	private final static String MSG_EXECUTION_TIME_2 = "Inserire la durata in secondi ";
	private final static String MSG_EXECUTION_COMPLETE = "Esecuzione completata";
	private final static String MSG__EXECUTION_INTERRUPTED = "Esecuzione interrotta";
	private final static String MSG_FILE_NOT_FOUND = "Esecuzione terminata, il file specificato non esiste";
	private final static String MSG_INPUT_N_ROWS = "Inserire numero righe ";
	private final static String MSG_INPUT_N_COLS = "Inserire numero colonne ";
	private final static String MSG_INPUT_PROBABILITY = "Inserire probabilità presenza di un 1 (0-100) ";
	private final static String MSG_INPUT_N_FILE = "Inserire numero di test che si vogliono generare ";
	private final static String MSG_START_FINAL_PHASE_DIST = "Inizio fase finale del calcolo distribuito";
	private final static String MSG_INPUT_FILE = "\nInput File: ";
	private final static String MSG_FILES_CREATED = "Creazione dei file terminata. I file sono contenuti nella cartella ";
	private final static String TITLE_CHOICE = "Seleziona l'opzione desiderata";
	private final static String[] OPTIONS_CHOICE = {"Monolitico","Distribuito","Confronto","Crea"};
	private final static int MONO = 1;
	private final static int DIST = 2;
	
	/**
	 * Main program
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		boolean hasTimeLimit = false, runAll = false, useComponents = false;
		int timeLimit = 0;
		String path = "", outputFilePath = "";
		File[] inputFiles;
		File fileDir;

		MyMenu menu = new MyMenu(TITLE_CHOICE, OPTIONS_CHOICE);
		// Monolitico o distribuito (scelta utente tramite menu')
		int choice = menu.chooseNZ();
		switch(choice) {
			case MONO:
			case DIST:
				// Viene chiesto all'utente di fissare l'eventuale durata massima dell'elaborazione
				hasTimeLimit = UserInput.yesOrNo(MSG_EXECUTION_TIME_1);
				if(hasTimeLimit)
					timeLimit = UserInput.leggiInt(MSG_EXECUTION_TIME_2);
				
				// Viene chiesto se si vuole eseguire l'algoritmo su tutti i file contenuti in una cartella
				runAll = UserInput.yesOrNo(MSG_RUN_ALL);
				
				if(choice == DIST) {
					useComponents = UserInput.yesOrNo(MSG_USE_COMPONENTS);
				}
				
				// Lettura file di input
				if(runAll) {
					path = IOFile.selectDir();
				}
				else {
					if(choice == MONO || (choice == DIST && !useComponents))
						path = IOFile.selectFile(EXTENSION_INPUT);
					else {
						do {
							//selezione cartella contenente i componenti (deve terminare con _dist)
							path = IOFile.selectDir();
						}while (path!=null&&!path.endsWith(TAG_DIST));
					}
				}
				
				 
				
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
						path = inF.getAbsolutePath();
						if(path!=null) {
							File fileToOpen = new File(path);		
							if(!fileToOpen.exists())
								System.out.println(MSG_FILE_NOT_FOUND);
							else {				
								switch(choice) {
									case MONO: // Calcolo monolitico
										if(UserInput.check_extension(inF.getName(), EXTENSION_INPUT)) {
											System.out.println(MSG_INPUT_FILE+inF.getName());
											Instance in = new Instance(path);
											if(in != null) {
												MonolithicHypothesis mh = new MonolithicHypothesis(in.getNumUsefulColumns(), in.getMatrixNumRows());
												MonolithicSolution monoSol = new MonolithicSolution(in);
												Problem mono = new Problem(in, mh, monoSol);
												if(hasTimeLimit)
													mono.setTimeLimit(timeLimit);
												mono.exploreH();
												
												// Scrittura file di output
												outputFilePath = path.substring(0, path.lastIndexOf("."+EXTENSION_INPUT))+"."+EXTENSION_OUTPUT;
												IOFile.writeOutputData(mono.getSol().getStringForFile(),outputFilePath);
											}
										}
										break;
									case DIST:
										String pathDirComponents = "";
										int fileCounter = 0;
										double totalTime = 0;
										if(!useComponents) {
											if(UserInput.check_extension(inF.getName(), EXTENSION_INPUT)) {
												System.out.println(MSG_INPUT_FILE+inF.getName());
												Instance in = new Instance(path);
												if(in != null) {
													// Creazione cartella contenente l'input cumulativo per il calcolo distribuito. 
													String newDirPath = path.substring(0, path.lastIndexOf("."+EXTENSION_INPUT))+TAG_DIST;
													File newDir = new File(newDirPath);
													
													// Se la cartella _dist per quell'istanza esiste gia' rimuovo il contenuto 
													if(newDir.exists()) {						
															IOFile.deleteFileInFolder(newDir);
															//Creazione dei file N_i e dei componenti
															in.createNiFiles(newDirPath, path.substring(path.lastIndexOf("\\")+1, path.lastIndexOf(".")));
													}
													else {
														newDir.mkdir();
														//Creazione dei file N_i e dei componenti
														in.createNiFiles(newDirPath, path.substring(path.lastIndexOf("\\")+1, path.lastIndexOf(".")));
													}
													
													File[] files = newDir.listFiles();
													
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
															
															//-------------------------------------
															// Scrittura file di output
															outputFilePath = f.getPath().substring(0, f.getPath().lastIndexOf("."+EXTENSION_INPUT))+"."+EXTENSION_OUTPUT;
															IOFile.writeOutputData(p_i.getSol().getStringForFile(),outputFilePath);
															//-------------------------------------
															
															
														}
													}
													pathDirComponents = newDirPath;
												}
											}
										}
										else {
											//caso in cui uso i componenti già presenti
											if(fileToOpen.isDirectory()&&fileToOpen.getName().endsWith(TAG_DIST))
												pathDirComponents = fileToOpen.getAbsolutePath();
										}
										
										if(pathDirComponents!="") {
											File dirComponents = new File(pathDirComponents);
											File[] components = dirComponents.listFiles();
											ArrayList<Component> componentsList = new ArrayList<>();
											ArrayList<ArrayList<BitSet>> hsList = new ArrayList<>();
											BitSet usefulColumns = new BitSet();
											
											int countMHS = 0, w = 0, inputFileCols = 0;
											fileCounter = 0;
											HashMap<Integer, Integer> mhsPerComponents = new HashMap<>();
											Component c;
											ArrayList<BitSet> hsList_iShrink;
											BitSet hsShrink;
											for(File file_c:components) {
												if(UserInput.check_extension(file_c.getName(), EXTENSION_OUTPUT)) {
													c = new Component(file_c.getAbsolutePath());
													usefulColumns.or(c.getUsefulColum());
													componentsList.add(c);
													mhsPerComponents.put(fileCounter, c.getN_MHS());
													fileCounter++;
													countMHS+=c.getN_MHS();
												}
											}
											
											
											
											
											for(int i=0;i<componentsList.size(); i++) {
												hsList_iShrink = new ArrayList<>();
												for(int j=0;j<componentsList.get(i).getN_MHS();j++) {
													w = 0;
													hsShrink = new BitSet();
													inputFileCols = componentsList.get(i).getInputFileCols();
													for(int k=0; k < inputFileCols; k++) {
														if(usefulColumns.get(k)) {
															if(componentsList.get(i).getMHS(j).get(k))
																hsShrink.set(w);
															w++;
														}
													}
													hsList_iShrink.add(hsShrink);
												}
												hsList.add(hsList_iShrink);
											}
											
											Instance inDist = new Instance(usefulColumns,inputFileCols,countMHS);
											DistributedSolution distSol = new DistributedSolution(inDist);
											
											distSol.setnFiles(fileCounter);
											distSol.setnGlobalMHS(countMHS);
											distSol.setMHSPerComponents(mhsPerComponents);
											
											System.out.println(MSG_START_FINAL_PHASE_DIST);
											
											DistributedHypothesis dh = new DistributedHypothesis(usefulColumns.cardinality(), fileCounter, hsList);
											Problem dist = new Problem(inDist, dh, distSol);
											
											if(hasTimeLimit)
												dist.setTimeLimit(timeLimit);
											dist.exploreH();

											// Scrittura file di output
											outputFilePath = pathDirComponents+"."+EXTENSION_OUTPUT;
											IOFile.writeOutputData(dist.getSol().getStringForFile(),outputFilePath);
											
											//Durata totale (calcolo Ci e composizione)
											totalTime+=dist.getSol().getTime();
											System.out.println("\n Tempo totale: "+totalTime);
										}
										break;
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
					fileDir = new File(path);
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
				String testDirName = "test_"+nRows+"x"+nCols+"p"+probability1+"_n"+nFile+"_"+System.currentTimeMillis();
				File testDir = new File(testDirName);
				testDir.mkdir();
				for(int i=0; i<nFile; i++) {
					t = new Test(nRows, nCols, probability1);
					IOFile.writeOutputData(t.getStringForFile(),testDirName+"\\"+i+"."+EXTENSION_INPUT);
				}
				System.out.println(MSG_FILES_CREATED+testDirName);
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
				if(sCurrentLine.contains(MSG_EXECUTION_COMPLETE)) {
					complete = true;
					break;
				}
				if(sCurrentLine.contains(MSG__EXECUTION_INTERRUPTED)) {
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
