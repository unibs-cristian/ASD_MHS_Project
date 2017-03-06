package mainProgram;
import java.util.ArrayList;
import java.util.BitSet;
import java.io.File;

import objects.Hypothesis;
import objects.Instance;
import objects.MonolithicHypothesis;
import ioUtils.*;

/**
 * Classe contenente il metodo Main e i metodi corrispondenti ai vari moduli che costituiscono l'elaborato
 *
 */
public class Main {
	
	private final static String EXTENSION = "matrix";
	
	private static ArrayList<Hypothesis> current;
	private static ArrayList<Hypothesis> solutions;		
	private static ArrayList<Hypothesis> next; 
	
	/**
	 * Main program
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// Lettura file di input
		Instance in = readInputData();
		
		MonolithicHypothesis mh = new MonolithicHypothesis(in.getNumUsefulColumns(), in.getMatrixNumRows());
		// Modulo per il calcolo monolitico dei MHS
		calcoloMHS(in, mh);
		for(int i=0; i<solutions.size(); i++) 
			System.out.println(solutions.get(i));
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
		} while(!UserInput.check_extension(f.getName(), EXTENSION));	
		Reader r = new Reader(f.getPath());
		return r.read();
	}
	
	/**
	 * Modulo per effettuare il calcolo monolitico dei MHS sui dati in ingresso 
	 * 
	 * @param in : l'oggetto Instance che rappresenta i dati in input
	 */
	private static void calcoloMHS(Instance in, Hypothesis h) {
		current = new ArrayList<>();
		solutions = new ArrayList<>();		
		next = new ArrayList<>();
		
		h.setField(in);
		current.add(h);		
		do {
			next.clear();
			for(int i=0; i<current.size(); i++) {
				if(current.get(i).check()) {
					solutions.add(current.get(i));
					current.remove(i);
				}
				else {
					generateChildren(current.get(i), in);
				}
			}
			current = next;
		} while(!current.isEmpty());
	}
	
	private static void generateChildren(Hypothesis h, Instance in) {
		Hypothesis h1 = h;
		Hypothesis h2 = h;
		Hypothesis pred = h; 
		Hypothesis last = h;
		int cont = 0;
		boolean cond = false;
		BitSet fin;
		
		if(h.isEmpty()) {
			h1 = h;
			for(int i=0; i<h.getDimension(); i++) {				
				h1.setBin(i);
				h1.setField(in);
				h1.propagate(h);
				next.add(h1);
			}
			return;
		}
		
		pred = h;
		do {
			pred = prev(h);
		} while(!(pred == null || h.getHammingDistance(pred) == 2));
				
		if(pred!=null)
			cont = 0;
		
		
		
		for(int i=h.getBin().nextSetBit(0)-1; i>=0; i--) {
			if(pred!=null) {
				h1 = h;
				h1.setBin(i);
				h1.setField(in);
				h1.propagate(h);
				
				cond = true;
				for(int j=h.getBin().nextSetBit(0); i<=h.getBin().length(); i++) {
					h2 = h1;
					if(h2.getBin().get(j)!=false) {
						h2.set(j,false);
						if(pred!=h2) {
							cond = false;
							fin = h2.getBin();
							fin.set(h.getBin().length(), false);
							while(pred!=null && !isGreater(pred.getBin(), fin)) {
								do {
									pred = prev(h);
								} while(!(pred == null || h.getHammingDistance(pred) == 2));
							}
							break;							
						}
						else {
							h1.propagate(h2);
							do {
								pred = prev(h);
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
					last = h1;
				}
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
		b2.xor(b1);
		if(b2.cardinality() == 0 || b1.get(b2.nextSetBit(0)) == false)
			return false;
		return true;
	}

}
