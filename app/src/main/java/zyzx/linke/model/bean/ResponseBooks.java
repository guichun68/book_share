package zyzx.linke.model.bean;

import java.util.List;

/**
 * 响应给APP的书籍数据（在书籍详情的基础上添加了uid）
 * @author Austin
 *
 */
public class ResponseBooks {
	
	private List<BookDetail2> bookDetails;
	private Integer uid;
	private String mTitle;//CloudItem的title（兴趣点名称）
	private String address;//CloudItem的中文名称地址（snippt）
	private double lat;
	private double longi;
	private float distance;
	
	
	public List<BookDetail2> getBookDetails() {
		return bookDetails;
	}
	public void setBookDetails(List<BookDetail2> bookDetail) {
		this.bookDetails = bookDetail;
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
