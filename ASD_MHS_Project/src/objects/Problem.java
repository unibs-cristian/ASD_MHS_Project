package objects;

import java.util.ArrayList;
import java.util.Collections;

public class Problem {
	private ArrayList<Hypothesis> current;
	private Solution sol;		
	private ArrayList<Hypothesis> next;
	private Instance in;
	private Hypothesis h;
	private boolean hasTimeLimit;
	private int timeLimit;
	
	private final static String MSG_MONOLITHIC_START = "Iniziata esplorazione dello spazio delle ipotesi";
	private final static int NANO_TO_SEC = 1000000000;
	
	public Problem(Instance in, Hypothesis h, Solution sol) {
		current = new ArrayList<>();
		next = new ArrayList<>();
		this.in = in;
		this.h = h;
		this.sol = sol;
		this.hasTimeLimit = false;
		this.timeLimit = 0;
	}
	
	public Solution getSol() {
		return sol;
	}
	
	public void setTimeLimit(int limit) {
		this.timeLimit = limit;
		this.hasTimeLimit = true;
	}
	
	/**
	 * Modulo per effettuare il calcolo monolitico dei MHS sui dati in ingresso 
	 * 
	 * @param in : l'oggetto Instance che rappresenta i dati in input
	 */
	public void exploreH() {
		boolean timeLimitReached = false;
		System.out.println(MSG_MONOLITHIC_START);

		current = new ArrayList<>();		
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
					generateChildren(current.get(i));
				}
				// Se e' stato fissato un limite di tempo per l'elaborazione, controllo che questo non sia stato superato
				if(hasTimeLimit) {
					if(((double)(System.nanoTime() - startTime)/NANO_TO_SEC) >= timeLimit) {
						timeLimitReached = true;
						break;
					}
				}
			}			
			Collections.sort(next, Collections.reverseOrder());
			current = new ArrayList<>(next);
//			System.out.print(next.size()+" * ");
//			System.out.println(next);
			sol.incrementLevelReached();
		} while(!current.isEmpty() && !timeLimitReached);		
		long endTime = System.nanoTime();
		double executionTime = ((double)(endTime - startTime))/NANO_TO_SEC;
		sol.setTime(executionTime);
		System.out.println("Monolithic Execution time: " + executionTime);
		if(!timeLimitReached)
			sol.setComplete();						
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
