package objects;

import java.util.BitSet;

public abstract class Hypothesis {
	private int dimension;
	private BitSet bin;
	
	public Hypothesis(int dimension){
		bin = new BitSet(dimension);
		this.dimension = dimension;
	}
	
	public abstract void setField();
}
