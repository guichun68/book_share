package zyzx.linke.model.bean;

import android.os.Parcel;
import android.os.Parcelable;


public class Series implements Parcelable{
	//id:4542
	//title : "计算机科学丛书"
	private String id;

	private String title;

	public Series(){}

	protected Series(Parcel in) {
		id = in.readString();
		title = in.readString();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(title);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Creator<Series> CREATOR = new Creator<Series>() {
		@Override
		public Series createFromParcel(Parcel in) {
			return new Series(in);
		}

		@Override
		public Series[] newArray(int size) {
			return new Series[size];
		}
	};

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return this.id;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return this.title;
	}

}