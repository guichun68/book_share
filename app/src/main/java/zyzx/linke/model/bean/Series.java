package zyzx.linke.model.bean;

import java.io.Serializable;

public class Series implements Serializable{
	//id:4542
	//title : "计算机科学丛书"
	private String id;

	private String title;

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