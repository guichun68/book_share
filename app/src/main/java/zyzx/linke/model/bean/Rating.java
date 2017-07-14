package zyzx.linke.model.bean;

import android.os.Parcel;
import android.os.Parcelable;


public class Rating implements Parcelable{
	private Double average;

	private Double max;

	private Double min;

	private int numRaters;

	public Rating(){}

	protected Rating(Parcel in) {
		numRaters = in.readInt();
		average = in.readDouble();
		max = in.readDouble();
		min = in.readDouble();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(numRaters);
		dest.writeDouble(average!=null?average:0);
		dest.writeDouble(max!=null?max:0);
		dest.writeDouble(min!=null?min:0);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Creator<Rating> CREATOR = new Creator<Rating>() {
		@Override
		public Rating createFromParcel(Parcel in) {
			return new Rating(in);
		}

		@Override
		public Rating[] newArray(int size) {
			return new Rating[size];
		}
	};

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