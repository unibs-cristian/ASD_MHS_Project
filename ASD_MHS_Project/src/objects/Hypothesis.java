package objects;

import java.util.BitSet;

public abstract class Hypothesis {
	private int dimension;
	private BitSet bin;
	
	public Hypothesis(int dimension){
		bin = new BitSet(dimension);
		this.dimension = dimension;
	}
	
	public Hypothesis(int dimension, BitSet bin){
		this.bin = bin;
		this.dimension = dimension;
	}
	
	public BitSet getBin() {
		return bin;
	}
	
	public int getDimension() {
		return dimension;
	}
	
	public abstract void setField(Instance instance);
	public abstract boolean check();
	public abstract void propagate(Hypothesis h);
	public abstract Hypothesis generateLeftMostPredecessor(Instance instance);
	public abstract Hypothesis generateRightMostPredecessor(Instance instance);
}
