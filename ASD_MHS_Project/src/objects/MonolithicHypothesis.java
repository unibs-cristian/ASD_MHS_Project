package objects;

import java.util.BitSet;

public class MonolithicHypothesis extends Hypothesis{
	private int nInsiemi;
	private BitSet vector;
	
	public MonolithicHypothesis(int dimension, int nInsiemi) {
		super(dimension);
		this.nInsiemi = nInsiemi;
		vector = new BitSet(nInsiemi);
	}

	@Override
	public void setField(Instance instance) {
		if(getBin().cardinality() == 0) // Controllo se l'ipotesi e' vuota
			vector.set(0, nInsiemi, false); // TODO e' utile?
		else {
			if(getBin().cardinality() == 1) {// Controllo se e' un'ipotesi di livello 1
				vector = instance.getMatrixColumn(getBin().length());
			}
		}
	
	}

}
