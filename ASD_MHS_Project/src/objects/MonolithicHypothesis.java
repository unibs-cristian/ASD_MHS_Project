package objects;

import java.util.BitSet;

public class MonolithicHypothesis extends Hypothesis {
	private int nSets;
	/**
	 * Nel calcolo monolitico vector ha lunghezza |N| e rappresenta la j-esima colonna della matrice, se h
	 * e' un singoletto che contiene solo l'elemento j-esimo del dominio. Altrimenti il vector � il risultato
	 * del bitwise or delle colonne della matrice relative a tutti gli elementi contenuti in h. 
	 */
	private BitSet vector;
	
	public MonolithicHypothesis(int dimension, int nInsiemi) {
		super(dimension);
		this.nSets = nInsiemi;
		vector = new BitSet(nInsiemi);
	}
	
	public MonolithicHypothesis(int dimension, BitSet bin, int nInsiemi) {
		super(dimension,bin);
		this.nSets = nInsiemi;
		vector = new BitSet(nInsiemi);
	}
	
	public BitSet getVector() {
		return vector;
	}

	@Override
	public void setField(Instance instance) {
		if(bin.nextSetBit(0) == -1) // Controllo se l'ipotesi e' vuota
			vector.set(0, nSets, false);
		else {
			if(bin.cardinality() == 1) { // Controllo se e' un'ipotesi di livello 1
				vector = instance.getMatrixColumn(bin.length()-1);
			}
			else {				
				for(int i = bin.nextSetBit(0); i<bin.length(); i++)
					if(bin.get(i))
						vector.or(instance.getMatrixColumn(i));
			}
		}
	}

	@Override
	public boolean check() {
		if(vector.cardinality() == nSets)
			return true;
		else
			return false;
	}

	@Override
	public void propagate(Hypothesis h) {
		//empty
	}
	
	@Override
	public Hypothesis clone() {
		return new MonolithicHypothesis(dimension, (BitSet)bin.clone(), nSets);
	}
}
