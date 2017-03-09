package objects;

import java.util.BitSet;

public abstract class Hypothesis implements Cloneable, Comparable<Hypothesis>{
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
		BitSet b = (BitSet)getBin().clone();
		b.xor(h1.getBin());
		return b.cardinality();
	}
	
	public String toString() {
		return bin.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Hypothesis other = (Hypothesis) obj;
		if (bin == null) {
			if (other.bin != null)
				return false;
		} else if (!bin.equals(other.bin))
			return false;
		if (dimension != other.dimension)
			return false;
		return true;
	}
	
	public int compareTo(Hypothesis h) {
		BitSet b = (BitSet)getBin().clone();
		b.xor(h.getBin());
		if(b.cardinality() == 0)
			return 0;
		else if (getBin().get(b.nextSetBit(0)) == false)
			return -1;//minore
		else
			return 1;//maggiore

	}
	
	public abstract Hypothesis clone();
	public abstract void setField(Instance instance);
	public abstract boolean check();
	public abstract void propagate(Hypothesis h);
	public abstract Hypothesis generateLeftMostPredecessor(Instance instance);
	public abstract Hypothesis generateRightMostPredecessor(Instance instance);

}
