package zyzx.linke.model.bean;

import java.util.List;

/**
 * Created by austin on 2017/3/5.
 * Desc: 作为请求的参数之用，用于批量请求指定bookId的书籍详情信息时标识每个用户的请求信息
 */

public class RequestParamGetBookInfos {
    private List<Integer> bookIds;
    private int uid;
    private String mTitle;//CloudItem的title（兴趣点名称）
    private String address;//CloudItem的中文名称地址（snippt）
    private double lat;
    private double longi;
    private float distance;//CloudItem中距离字段

    public RequestParamGetBookInfos() {
    }

    public RequestParamGetBookInfos(List<Integer> bookIds, int uid, String mTitle, String address, double lat, double longi,float distance) {
        this.bookIds = bookIds;
        this.uid = uid;
        this.mTitle = mTitle;
        this.address = address;
        this.lat = lat;
        this.longi = longi;
        this.distance = distance;
    }

    public List<Integer> getBookIds() {
        return bookIds;
    }

    public void setBookIds(List<Integer> bookIds) {
        this.bookIds = bookIds;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
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
