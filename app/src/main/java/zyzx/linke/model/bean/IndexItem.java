package zyzx.linke.model.bean;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by austin on 2017/3/6.
 * Desc: 首页（indexActivity）每个条目的data
 */

public class IndexItem implements Parcelable{

    private BookDetail2 bookDetail;
    private Integer uid;
    private String mTitle;//CloudItem的title（兴趣点名称）
    private String address;//CloudItem的中文名称地址（snippt）
    private double lat;
    private double longi;
    private float distance;

    public IndexItem() {
    }

    public IndexItem(BookDetail2 bookDetail, Integer uid, String mTitle, String address, double lat, double longi) {
        this.bookDetail = bookDetail;
        this.uid = uid;
        this.mTitle = mTitle;
        this.address = address;
        this.lat = lat;
        this.longi = longi;
    }

    protected IndexItem(Parcel in) {
        mTitle = in.readString();
        address = in.readString();
        lat = in.readDouble();
        longi = in.readDouble();
        distance = in.readFloat();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(address);
        dest.writeDouble(lat);
        dest.writeDouble(longi);
        dest.writeFloat(distance);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<IndexItem> CREATOR = new Creator<IndexItem>() {
        @Override
        public IndexItem createFromParcel(Parcel in) {
            return new IndexItem(in);
        }

        @Override
        public IndexItem[] newArray(int size) {
            return new IndexItem[size];
        }
    };

    public BookDetail2 getBookDetail() {
        return bookDetail;
    }

    public void setBookDetail(BookDetail2 bookDetail) {
        this.bookDetail = bookDetail;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLongi() {
        return longi;
    }

    public void setLongi(double longi) {
        this.longi = longi;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }
}
