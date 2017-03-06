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
	
	public MonolithicHypothesis(int dimension, BitSet bin, int nInsiemi) {
		super(dimension,bin);
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
			else {
				/*vector = 
				vector.or(vectorB);*/
			}
		}
	
	}

	@Override
	public boolean check() {
		if(vector.cardinality() == nInsiemi)
			return true;
		else
			return false;
	}

	@Override
	public void propagate(Hypothesis h) {
		// TODO Auto-generated method stub
		//empty
	}
	
	@Override
	public Hypothesis generateLeftMostPredecessor(Instance instance) {
		BitSet leftMostPredecessor = getBin();
		leftMostPredecessor.set(leftMostPredecessor.length(), false);
		MonolithicHypothesis hLeftMostPredecessor = new MonolithicHypothesis(getDimension(),leftMostPredecessor,nInsiemi);
		hLeftMostPredecessor.setField(instance);
		return hLeftMostPredecessor;
	}
	
	@Override
	public Hypothesis generateRightMostPredecessor(Instance instance) {
		BitSet rightMostPredecessor = getBin();
		rightMostPredecessor.set(rightMostPredecessor.nextSetBit(0), false);
		MonolithicHypothesis hRightMostPredecessor = new MonolithicHypothesis(getDimension(),rightMostPredecessor,nInsiemi);
		hRightMostPredecessor.setField(instance);
		return hRightMostPredecessor;
	}
}
