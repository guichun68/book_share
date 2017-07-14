package zyzx.linke.model.bean;

import android.os.Parcel;
import android.os.Parcelable;


public class Images implements Parcelable{
	private String large;

	private String medium;

	private String small;

	public Images(){}

	protected Images(Parcel in) {
		large = in.readString();
		medium = in.readString();
		small = in.readString();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(large);
		dest.writeString(medium);
		dest.writeString(small);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Creator<Images> CREATOR = new Creator<Images>() {
		@Override
		public Images createFromParcel(Parcel in) {
			return new Images(in);
		}

		@Override
		public Images[] newArray(int size) {
			return new Images[size];
		}
	};

	public void setLarge(String large) {
		this.large = large;
	}

	public String getLarge() {
		return this.large;
	}

	public void setMedium(String medium) {
		this.medium = medium;
	}

	public String getMedium() {
		return this.medium;
	}

	public void setSmall(String small) {
		this.small = small;
	}

	public String getSmall() {
		return this.small;
	}

}