package zyzx.linke.model.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class Tags implements Parcelable{
	private int count;

	private String name;

	private String title;

	public Tags(){}

	protected Tags(Parcel in) {
		count = in.readInt();
		name = in.readString();
		title = in.readString();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(count);
		dest.writeString(name);
		dest.writeString(title);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Creator<Tags> CREATOR = new Creator<Tags>() {
		@Override
		public Tags createFromParcel(Parcel in) {
			return new Tags(in);
		}

		@Override
		public Tags[] newArray(int size) {
			return new Tags[size];
		}
	};

	public void setCount(int count) {
		this.count = count;
	}

	public int getCount() {
		return this.count;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return this.title;
	}

}