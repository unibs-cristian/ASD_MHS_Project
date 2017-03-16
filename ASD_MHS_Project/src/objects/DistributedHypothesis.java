package objects;

import java.util.BitSet;
import java.util.ArrayList;

public class DistributedHypothesis extends Hypothesis{
	private int nComponenti;
	private BitSet vector;
	private ArrayList<ArrayList<BitSet>> componentsList;
	
	public DistributedHypothesis(int dimension, int nComponenti, ArrayList<ArrayList<BitSet>> componentsList) {
		super(dimension);
		this.nComponenti = nComponenti;
		this.componentsList = componentsList;
		vector = new BitSet(nComponenti);
	}
	
	public DistributedHypothesis(int dimension, BitSet bin, int nComponenti, ArrayList<ArrayList<BitSet>> componentsList) {
		super(dimension,bin);
		this.nComponenti = nComponenti;
		this.componentsList = componentsList;
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
		return new DistributedHypothesis(getDimension(), (BitSet)getBin().clone(), nComponenti, componentsList);
	}

	@Override
	public void setField(Instance instance) {
		if(getBin().cardinality() == 0) // Controllo se l'ipotesi e' vuota
			vector.set(0, nComponenti, false); // TODO e' utile?
		else {
			for(int i=0; i<nComponenti; i++)
				//if h is a MHS in Ci
				//TODO è ottimizzabile?
				//if(componentsList.get(i).contains(getBin()))
				if(isMHSinCi(i))
					vector.set(i);
				else
					vector.set(i,false);
		}
		
	}
	
	//Metodo per ottimizzare la ricerca del bin in un Ci sfruttandone l'ordinamento
	private boolean isMHSinCi(int i) {
		BitSet b;
		for(int j=0; j<componentsList.get(i).size(); j++) {
			if(componentsList.get(i).get(j).equals(getBin()))
				return true;
			
			b = (BitSet)getBin().clone();
			b.xor(componentsList.get(i).get(j));
			//getBin() > componentsList.get(i).get(j)
			if(!componentsList.get(i).get(j).get(b.nextSetBit(0)))
				return false;
		}
		return false;
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
