package zyzx.linke.model.bean;

import java.io.Serializable;
import java.util.Date;

public class UserVO implements Serializable, Cloneable{
	
	private String genderName;

	private String provinceName;
	private String cityName;
	private String countyName;
	private String diplomaName;//学历

	private Integer userid;
	private String login_name;
	private String mobile_phone;
	private String address;
	private String password;
	private Integer gender;
	private String hobby;
	private String email;
	private String real_name;
	private Integer cityId;

	private String lastLoginTime;
	private String signature;
	private String head_icon;//头像地址
	private String bak4;//勿删，目前已用作返回给会话页面的用户信息时的code使用
	private Date birthday;
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

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getCountyName() {
		return countyName;
	}

	public void setCountyName(String countyName) {
		this.countyName = countyName;
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

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
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

	@Override
	public String toString() {
		return "UserVO{" +
				"genderName='" + genderName + '\'' +
				", provinceName='" + provinceName + '\'' +
				", cityName='" + cityName + '\'' +
				", countyName='" + countyName + '\'' +
				", diplomaName='" + diplomaName + '\'' +
				", userid=" + userid +
				", login_name='" + login_name + '\'' +
				", mobile_phone='" + mobile_phone + '\'' +
				", address='" + address + '\'' +
				", password='" + password + '\'' +
				", gender=" + gender +
				", hobby='" + hobby + '\'' +
				", email='" + email + '\'' +
				", real_name='" + real_name + '\'' +
				", cityId=" + cityId +
				", lastLoginTime='" + lastLoginTime + '\'' +
				", signature='" + signature + '\'' +
				", head_icon='" + head_icon + '\'' +
				", bak4='" + bak4 + '\'' +
				", birthday='" + birthday + '\'' +
				", school='" + school + '\'' +
				", errorCode=" + errorCode +
				", department='" + department + '\'' +
				", diplomaId=" + diplomaId +
				", soliloquy='" + soliloquy + '\'' +
				", creditScore=" + creditScore +
				'}';
	}

	@Override
	public Object clone()  {
		UserVO uv = null;
		try{
			uv = (UserVO) super.clone();
		}catch(CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return uv;
	}
}
