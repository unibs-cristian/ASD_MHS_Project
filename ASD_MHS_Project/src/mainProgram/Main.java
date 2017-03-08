package mainProgram;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
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
public class Main {
	
	private final static String EXTENSION_INPUT = "matrix";
	private final static String EXTENSION_OUTPUT = "mhs";
	private final static String MSG_EXECUTION_TIME_1 = "Si desidera fissare una durata massima per l'elaborazione?";
	private final static String MSG_EXECUTION_TIME_2 = "Inserire la durata in secondi";
	private final static String MSG_MONOLITHIC_START = "Iniziato calcolo monolitico dei MHS";
	private final static String MSG_MONOLITHIC_INTERRUPT = "Per interrompere l'elaborazione premere Q";
	private final static String MSG_TIMEOUT_EXCEPTION = "Limite di tempo raggiunto, esecuzione interrotta";
	private final static int NANO_TO_SEC = 1000000000;
	
	private static ArrayList<Hypothesis> current;
	private static Solution sol;		
	private static ArrayList<Hypothesis> next;
	private static String inputFilePath;
	private static boolean hasTimeLimit = false;
	private static int timeLimit; 
	
	/**
	 * Main program
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// Lettura file di input
		Instance in = readInputData();
		
		// Viene chiesto all'utente di fissare l'eventuale durata massima dell'elaborazione
		hasTimeLimit = UserInput.yesOrNo(MSG_EXECUTION_TIME_1);
		if(hasTimeLimit)
			timeLimit = UserInput.leggiInt(MSG_EXECUTION_TIME_2);
		
		MonolithicHypothesis mh = new MonolithicHypothesis(in.getNumUsefulColumns(), in.getMatrixNumRows());
		startMonolithicMHS(in, mh);
					
		// Modulo per il calcolo monolitico dei MHS
		System.out.print(sol.getStringForFile());
		
		// Scrittura file di output
		String outputFilePath = inputFilePath.substring(0, inputFilePath.lastIndexOf("."+EXTENSION_INPUT))+"."+EXTENSION_OUTPUT;
		writeOutputData(sol.getStringForFile(),outputFilePath);
		
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
	private static void startMonolithicMHS(Instance in, MonolithicHypothesis mh) {
		
		if(hasTimeLimit) {
			final Runnable processing = new Runnable() {
				
				@Override
				public void run() {
					exploreH(in, mh);
					
				}
			};
			
			final ExecutorService executor = Executors.newSingleThreadExecutor();
			final Future<?> future = executor.submit(processing);
			executor.shutdown(); 
	
			try { 
				future.get(timeLimit, TimeUnit.SECONDS); 
			}
			catch (InterruptedException ie) { 
				ie.printStackTrace();
			}
			catch (ExecutionException ee) { 
				ee.printStackTrace();
			}
			catch (TimeoutException te) { 
				System.out.println(MSG_TIMEOUT_EXCEPTION);
				// Scrittura file di output
				String outputFilePath = inputFilePath.substring(0, inputFilePath.lastIndexOf("."+EXTENSION_INPUT))+"."+EXTENSION_OUTPUT;
				writeOutputData(sol.getStringForFile(),outputFilePath);			
			}
			if (!executor.isTerminated())
			    executor.shutdownNow(); // If you want to stop the code that hasn't finished.
		}
		else
			exploreH(in, mh);
	}
	
	/**
	 * Modulo per effettuare il calcolo monolitico dei MHS sui dati in ingresso 
	 * 
	 * @param in : l'oggetto Instance che rappresenta i dati in input
	 */
	private static void exploreH(Instance in, Hypothesis h) {
		
		System.out.println(MSG_MONOLITHIC_START);
		if(!hasTimeLimit)
			System.out.println(MSG_MONOLITHIC_INTERRUPT);
		
		current = new ArrayList<>();
		sol = new Solution(in);		
		next = new ArrayList<>();
		
		long startTime = System.nanoTime();
		h.setField(in);
		current.add(h);		
		do {
			next.clear();
			for(int i=0; i<current.size(); i++) {
				//System.out.println(current.get(i).getBin());
				if(current.get(i).check()) {
					sol.add(current.get(i));
					current.remove(i);
					i--;//Se viene rimosso un elemento tutti gli altri shiftano a sx quindi devo decrementare i per non saltarne uno
				}
				else {
					generateChildren(current.get(i), in);
				}
			}
			//TODO attenzione il contenuto di next deve essere prima ordinato
			current = new ArrayList<>(next);
			System.out.println(next);
			sol.incrementLevelReached();
		} while(!current.isEmpty());
		long endTime = System.nanoTime();
		double executionTime = ((double)(endTime - startTime))/NANO_TO_SEC;
		System.out.println("Monolithic Execution time: " + executionTime);
		sol.setComplete();
	}
	
	private static void generateChildren(Hypothesis h, Instance in) {
		Hypothesis h1 = h.clone();
		Hypothesis h2 = h.clone();
		Hypothesis pred = h.clone(); 
		Hypothesis last = h.clone();
		int cont = 0;
		boolean cond = false;
		BitSet fin;
		
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
		do {
			pred = prev(pred);
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
						if(!h2.equals(pred)) {
							cond = false;
							fin = (BitSet)h2.getBin().clone();
							fin.set(h.getBin().length()-1, false);
							//pred <= fin
							while(pred!=null && !isGreater(pred.getBin(), fin)) {
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
				System.out.println(h);
				System.out.println(cont);
				System.out.println(next);
				last = h1.clone();
				cont++;
			}
		}
			
	}
	
	private static Hypothesis prev(Hypothesis h) {
		if(current.indexOf(h) - 1 < 0)
			return null;
		else 
			return current.get(current.indexOf(h) - 1);
	}
	
	private static boolean isGreater(BitSet b1, BitSet b2) {
		BitSet b = (BitSet)b2.clone();
		b.xor(b1);
		if(b.cardinality() == 0 || b1.get(b.nextSetBit(0)) == false)
			return false;
		return true;
	}
	
	

}
