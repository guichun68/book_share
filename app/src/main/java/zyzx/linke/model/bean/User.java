package zyzx.linke.model.bean;

import java.io.Serializable;

public class User implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6208060136313344736L;
	private Integer userid;
	private String login_name;
	private String mobile_phone;
	private String address;
	private String password;
	private Integer age;
	/**
	 * true 男 false 女
	 */
	private Boolean gender;
	private String hobby;
	private String email;
	private String real_name;
	private String city;
	
	private String lastLoginTime;
	private String signature;
	private String bak3;
	private String bak4;
	private String bak5;
	private String bak6;
	
	
	public User() {
		super();
		// TODO Auto-generated constructor stub
	}




	public User(String login_name, String mobile_phone) {
		super();
		this.login_name = login_name;
		this.mobile_phone = mobile_phone;
	}


	public String getLogin_name() {
		return login_name;
	}


	public void setLogin_name(String login_name) {
		this.login_name = login_name;
	}


	public String getMobile_phone() {
		return mobile_phone;
	}


	public void setMobile_phone(String mobile_phone) {
		this.mobile_phone = mobile_phone;
	}


	public String getAddress() {
		return address;
	}


	public void setAddress(String address) {
		this.address = address;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public Integer getAge() {
		return age;
	}


	public void setAge(Integer age) {
		this.age = age;
	}


	public Boolean getGender() {
		return gender;
	}


	public void setGender(Boolean gender) {
		this.gender = gender;
	}


	public String getHobby() {
		return hobby;
	}


	public void setHobby(String hobby) {
		this.hobby = hobby;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getReal_name() {
		return real_name;
	}


	public void setReal_name(String real_name) {
		this.real_name = real_name;
	}


	public String getCity() {
		return city;
	}


	public void setCity(String city) {
		this.city = city;
	}




	public Integer getUserid() {
		return userid;
	}




	public void setUserid(Integer userid) {
		this.userid = userid;
	}




	public String getLastLoginTime() {
		return lastLoginTime;
	}




	public void setLastLoginTime(String lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}




	public static long getSerialversionuid() {
		return serialVersionUID;
	}


	public String getSignature() {
		return signature;
	}




	public void setSignature(String signature) {
		this.signature = signature;
	}




	public String getBak3() {
		return bak3;
	}


	public void setBak3(String bak3) {
		this.bak3 = bak3;
	}


	public String getBak4() {
		return bak4;
	}


	public void setBak4(String bak4) {
		this.bak4 = bak4;
	}


	public String getBak5() {
		return bak5;
	}


	public void setBak5(String bak5) {
		this.bak5 = bak5;
	}


	public String getBak6() {
		return bak6;
	}


	public void setBak6(String bak6) {
		this.bak6 = bak6;
	}




	@Override
	public String toString() {
		return "User [login_name=" + login_name + ", mobile_phone=" + mobile_phone + ", address=" + address
				+ ", password=" + password + ", age=" + age + ", gender=" + gender + ", hobby=" + hobby + ", email="
				+ email + ", real_name=" + real_name + ", city=" + city + ", lastLoginTime=" + lastLoginTime + "]";
	}


	
	
	
	
}
