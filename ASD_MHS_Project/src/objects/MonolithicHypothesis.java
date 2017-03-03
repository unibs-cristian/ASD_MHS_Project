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
	public void setField() {
		// TODO Auto-generated method stub
		
	}

}
