package zyzx.linke.model.bean;

import java.io.Serializable;

public class Images implements Serializable{
	private String large;

	private String medium;

	private String small;

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