package objects;

import java.util.BitSet;

public class DistributedHypothesis extends Hypothesis{
	private int nComponenti;
	private BitSet vector;
	
	public DistributedHypothesis(int dimension, int nComponenti) {
		super(dimension);
		this.nComponenti = nComponenti;
		vector = new BitSet(nComponenti);
	}
	
	public BitSet getVector() {
		return vector;
	}
	
	public void setVector(int index) {
		vector.set(index);
	}

	@Override
	public Hypothesis clone() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setField(Instance instance) {
		if(getBin().cardinality() == 0) // Controllo se l'ipotesi e' vuota
			vector.set(0, nComponenti, false); // TODO e' utile?
		else {
			/*
			for(int i=0; i<nComponenti; i++)
				//TODO if h is a MHS in Ci
				if(true)
					vector.set(i);
				else
					vector.set(i,false);
			 */
		}
		
	}

	@Override
	public boolean check() {
		boolean cond = true;
		int i = 0;
		while(i < nComponenti && cond) {
			if(!vector.get(i))
				cond = false;
			i++;
		}
		return cond;
		/*TODO potenziale implementazione equivalente (più efficiente?)
		if(vector.cardinality() == nComponenti)
			return true;
		else 
			return false;
		*/
	}

	@Override
	public void propagate(Hypothesis h) {
		vector.or(((DistributedHypothesis) h).getVector());
	}

	@Override
	public Hypothesis generateLeftMostPredecessor(Instance instance) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Hypothesis generateRightMostPredecessor(Instance instance) {
		// TODO Auto-generated method stub
		return null;
	}

}
