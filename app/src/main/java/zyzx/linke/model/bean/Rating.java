package zyzx.linke.model.bean;

import java.io.Serializable;

public class Rating implements Serializable{
	private Double average;

	private Double max;

	private Double min;

	private int numRaters;


	public Double getAverage() {
		return average;
	}

	public void setAverage(Double average) {
		this.average = average;
	}


	public Double getMax() {
		return max;
	}

	public void setMax(Double max) {
		this.max = max;
	}

	public Double getMin() {
		return min;
	}

	public void setMin(Double min) {
		this.min = min;
	}

	public void setNumRaters(int numRaters) {
		this.numRaters = numRaters;
	}

	public int getNumRaters() {
		return this.numRaters;
	}

}