package zyzx.linke.model.bean;

public class Rating {
	private String average;

	private int max;

	private int min;

	private int numRaters;

	public void setAverage(String average) {
		this.average = average;
	}

	public String getAverage() {
		return this.average;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public int getMax() {
		return this.max;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public int getMin() {
		return this.min;
	}

	public void setNumRaters(int numRaters) {
		this.numRaters = numRaters;
	}

	public int getNumRaters() {
		return this.numRaters;
	}

}