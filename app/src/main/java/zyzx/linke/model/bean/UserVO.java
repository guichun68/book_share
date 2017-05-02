package zyzx.linke.model.bean;

import java.io.Serializable;

public class UserVO implements Serializable{
	
	private String genderName;
	
	private String cityName;
	
	private String diplomaName;//学历



	private Integer userid;
	private String login_name;
	private String mobile_phone;
	private String address;
	private String password;
	private Integer age;
	private Integer gender;
	private String hobby;
	private String email;
	private String real_name;
	private Integer cityId;

	private String lastLoginTime;
	private String signature;
	private String head_icon;//头像地址
	private String bak4;//勿删，目前已用作返回给会话页面的用户信息时的code使用
	private String birthday;
	private String school;
	private int errorCode;//只在Server中用于Dao层判断查询成功否
	private String department;//院系
	private Integer diplomaId;//学历
	private String soliloquy;//内心独白
	private Integer creditScore;//信用积分

	public UserVO() {
	}

	public UserVO(Integer userId, String loginName, String headIcon){
		this.userid = userId;
		this.login_name = loginName;
		this.head_icon = headIcon;
	}


	public String getGenderName() {
		return genderName;
	}

	public void setGenderName(String genderName) {
		this.genderName = genderName;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getDiplomaName() {
		return diplomaName;
	}

	public void setDiplomaName(String diplomaName) {
		this.diplomaName = diplomaName;
	}


	public Integer getUserid() {
		return userid;
	}

	public void setUserid(Integer userid) {
		this.userid = userid;
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

	public Integer getGender() {
		return gender;
	}

	public void setGender(Integer gender) {
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

	public Integer getCityId() {
		return cityId;
	}

	public void setCityId(Integer cityId) {
		this.cityId = cityId;
	}

	public String getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(String lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getHead_icon() {
		return head_icon;
	}

	public void setHead_icon(String head_icon) {
		this.head_icon = head_icon;
	}

	public String getBak4() {
		return bak4;
	}

	public void setBak4(String bak4) {
		this.bak4 = bak4;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public Integer getDiplomaId() {
		return diplomaId;
	}

	public void setDiplomaId(Integer diplomaId) {
		this.diplomaId = diplomaId;
	}

	public String getSoliloquy() {
		return soliloquy;
	}

	public void setSoliloquy(String soliloquy) {
		this.soliloquy = soliloquy;
	}

	public Integer getCreditScore() {
		return creditScore;
	}

	public void setCreditScore(Integer creditScore) {
		this.creditScore = creditScore;
	}
}
