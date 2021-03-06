package objects;

import java.util.ArrayList;
import java.util.Collections;

public class Problem {
	private ArrayList<Hypothesis> current;   // Sequenza di ipotesi usata nell'algoritmo di esplorazione di H
	private Solution sol;		             // Oggetto contenente informazioni sulla soluzione per questo problema
	private ArrayList<Hypothesis> next;      // Sequenza di ipotesi usata nell'algoritmo di esplorazione di H
	private Instance in;                     // Oggetto contenente informazioni sulla matrice fornita in input
	private Hypothesis h;                    // L'ipotesi iniziale (pu� essere monolitica o distribuita) da cui iniziare l'esplorazione 
	private boolean hasTimeLimit;            // Flag che ci dice se l'utente ha fissato un limite massimo di tempo per l'esplorazione
	private boolean hasExplorationStopped;   // Flag che ci dice se � stato raggiunto il limite massimo di tempo per l'esplorazione
	private int timeLimit;                   // Limite di tempo (in secondi)
	
	private final static String MSG_MONOLITHIC_START = "Iniziata esplorazione dello spazio delle ipotesi";
	private final static String MSG_EXPLORATION_COMPLETED = "Esplorazione di H completata in %f secondi";
	private final static String MSG_EXPLORATION_INTERRUPTED = "Esplorazione di H interrotta dopo %d secondi";
	private final static int NANO_TO_SEC = 1000000000;
	
	public Problem(Instance in, Hypothesis h, Solution sol) {
		current = new ArrayList<>();
		next = new ArrayList<>();
		this.in = in;
		this.h = h;
		this.sol = sol;
		this.hasTimeLimit = false;
		this.hasExplorationStopped = false;
		this.timeLimit = 0;
	}
	
	public Solution getSol() {
		return sol;
	}
	
	public boolean hasExplorationStopped() {
		return hasExplorationStopped;
	}
	
	public void setExplorationStopped() {
		hasExplorationStopped = true;
	}
	
	public void setTimeLimit(int limit) {
		this.timeLimit = limit;
		this.hasTimeLimit = true;
	}
	
	/**
	 * Modulo per effettuare il calcolo monolitico dei MHS sui dati in ingresso 
	 * Algoritmo principale per l'esplorazione di H
	 * 
	 * @param in : l'oggetto Instance che rappresenta i dati in input
	 */
	public void exploreH() {
		hasExplorationStopped = false;
		System.out.println(MSG_MONOLITHIC_START);

		// Current e Next sono 2 sequenze di ipotesi
		current = new ArrayList<>();		
		next = new ArrayList<>();
		
		long startTime = System.nanoTime();
		h.setField(in); // Invocazione del modulo set_fields ('in' contiene la matrice privata di colonne vuote)
		current.add(h);		
		do {			
			sol.setN_HypothesisPerLevel(current.size());  // All'oggetto soluzione viene aggiunto il numero di ipotesi generate per livello 
			next.clear();
			for(int i=0; i<current.size(); i++) {				
				if(current.get(i).check()) { // Controllo se l'ipotesi i-esima in current e' soluzione
					sol.add(current.get(i));
					current.remove(i);
					i--; //Se viene rimosso un elemento tutti gli altri shiftano a sx quindi si deve decrementare i per non saltarne uno
				}
				else {
					generateChildren(current.get(i)); // Modulo per la generazione dei successori immediati di un'ipotesi
				}
				// Se e' stato fissato un limite di tempo per l'elaborazione, controllo che questo non sia stato superato
				if(hasTimeLimit) {
					if(((double)(System.nanoTime() - startTime)/NANO_TO_SEC) >= timeLimit) {
						setExplorationStopped();
						break;
					}
				}
			}			
			Collections.sort(next, Collections.reverseOrder());
			current = new ArrayList<>(next);
			sol.incrementLevelReached(); // Livello raggiunto nell'esplorazione
		//vecchie condizioni di terminazione del ciclo (senza ottimizzazione sul livello raggiunto)
		//} while(!current.isEmpty() && !hasExplorationStopped);
		//nuove condizioni di terminazione del ciclo
		} while(!current.isEmpty() && !hasExplorationStopped && !sol.isMaxLevelReached());
		long endTime = System.nanoTime();
		double executionTime = ((double)(endTime - startTime))/NANO_TO_SEC;
		sol.setTime(executionTime); 
		if(!hasExplorationStopped) {
			sol.setComplete();
			System.out.println(String.format(MSG_EXPLORATION_COMPLETED, executionTime) );
		}
		else {
			System.out.println(String.format(MSG_EXPLORATION_INTERRUPTED, timeLimit) );
		}
									
	}
	
	
	private void generateChildren(Hypothesis h) {
		Hypothesis h1, h2, pred;	
		int cont = 0;
		int predIndex, lastIndex = 0;
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
						// codice tratto dallo pseudo-codice (non � corretto)
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
						//metodo corretto ma inefficiente
						/*
						if(!current.contains(h2)) {
							cond = false;
							break;
						}
						*/
						
						//metodo corretto e piu' efficiente
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
								//h1.propagate(h2);
								h1.propagate(pred);
							}
						}
						else {
							//h1.propagate(h2);
							h1.propagate(pred);
						}					
					}
				}

				if(cond) {
					if(cont == 0) {
						next.add(h1);
						lastIndex++;
					}
					else {
						if(next.isEmpty()) {
							next.add(h1);
							lastIndex = 0;
						}
						else {
							next.add(lastIndex, h1);
							lastIndex++;
						}
					}
					cont++;
				}
			}
		}	
	}
}
