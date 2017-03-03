package objects;

import java.util.BitSet;

public abstract class Hypothesis {
	private BitSet bin;
	private int dimension;
	
	public Hypothesis(int dimension){
		bin = new BitSet(dimension);
		this.dimension = dimension;
	}
}
