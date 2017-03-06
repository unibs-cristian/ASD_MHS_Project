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
	
	public boolean isEmpty() {
		return bin.cardinality() == 0;
	}
	
	public void setBin(int index) {
		bin.set(index);
	}
	
	public void setBin(BitSet bs) {
		bin = bs;
	}
	
	public void set(int index, boolean value) {
		bin.set(index, value);
	}
	
	public int getHammingDistance(Hypothesis h1) {
		BitSet b = getBin();
		b.xor(h1.getBin());
		return b.cardinality();
	}
	
	public String toString() {
		return bin.toString();
	}
	
	public abstract void setField(Instance instance);
	public abstract boolean check();
	public abstract void propagate(Hypothesis h);
	public abstract Hypothesis generateLeftMostPredecessor(Instance instance);
	public abstract Hypothesis generateRightMostPredecessor(Instance instance);
}
