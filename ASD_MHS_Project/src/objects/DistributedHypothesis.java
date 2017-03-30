package objects;

import java.util.BitSet;
import java.util.ArrayList;

public class DistributedHypothesis extends Hypothesis{
	// Contiene il numero dei componenti
	private int nComponents;      							
	// BitSet con 'nComponents' elementi, ciascuno dei quali 1 uno se l'ipotesi è HS per quel componenente
	private BitSet vector;                             
	// Struttura dati contenente gli insiemi di MHS relativi a ciascuna partizione
	private ArrayList<ArrayList<BitSet>> componentsList;
	
	public DistributedHypothesis(int dimension, int nComponenti, ArrayList<ArrayList<BitSet>> componentsList) {
		super(dimension);
		this.nComponents = nComponenti;
		this.componentsList = componentsList;
		vector = new BitSet(nComponenti);
	}
	
	public DistributedHypothesis(int dimension, BitSet bin, int nComponenti, ArrayList<ArrayList<BitSet>> componentsList) {
		super(dimension,bin);
		this.nComponents = nComponenti;
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
		return new DistributedHypothesis(dimension, (BitSet)bin.clone(), nComponents, componentsList);
	}

	@Override
	public void setField(Instance instance) {
		if(bin.nextSetBit(0) == -1) // Controllo se l'ipotesi e' vuota
			vector.set(0, nComponents, false);
		else {
			for(int i=0; i<nComponents; i++)
				//if h is a MHS in Ci
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
			if(componentsList.get(i).get(j).equals(bin))
				return true;
			
			b = (BitSet)bin.clone();
			b.xor(componentsList.get(i).get(j));
			//getBin() > componentsList.get(i).get(j)
			//TODO controllare questa ottimizzazione 
			/*if(!componentsList.get(i).get(j).get(b.nextSetBit(0)))
				return false;*/
		}
		return false;
	}

	@Override
	public boolean check() {
		boolean cond = true;
		int i = 0;
		while(i < nComponents && cond) {
			if(!vector.get(i))
				cond = false;
			i++;
		}
		return cond;
	}

	@Override
	public void propagate(Hypothesis h) {
		vector.or(((DistributedHypothesis) h).getVector());
	}

}
