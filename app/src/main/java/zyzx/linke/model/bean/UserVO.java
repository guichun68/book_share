package zyzx.linke.model.bean;

import java.io.Serializable;
import java.util.Date;

public class UserVO implements Serializable, Cloneable{
	
	private String genderName;
	private String url;
	private String provinceName;
	private String cityName;
	private String countyName;
	private String diplomaName;//学历

	private String uid;//uuid
	private Integer userid;//环信用
	private String loginName;
	private String mobilePhone;
	private String address;
	private String password;
	private Integer gender;
	private String hobby;
	private String email;
	private String realName;
	private Integer cityId;

	private String lastLoginTime;
	private String signature;
	private String headIcon;//头像地址
	private String bak4;//勿删，目前已用作返回给会话页面的用户信息时的code使用
	private Date birthday;
	private String school;
	private int errorCode;//只在Server中用于Dao层判断查询成功否
	private String department;//院系
	private Integer diplomaId;//学历
	private String soliloquy;//内心独白
	private Integer creditScore;//信用积分
	private Integer fromSystem;//来自哪个系统，1App，2微信，3微博，4qq

	public UserVO() {
	}

	public UserVO(Integer userId, String loginName, String headIcon){
		this.userid = userId;
		this.loginName = loginName;
		this.headIcon = headIcon;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
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

	public Integer getCityId() {
		return cityId;
	}

	public void setCityId(Integer cityId) {
		this.cityId = cityId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
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

	public Integer getFromSystem() {
		return fromSystem;
	}

	public void setFromSystem(Integer fromSystem) {
		this.fromSystem = fromSystem;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getHeadIcon() {
		return headIcon;
	}

	public void setHeadIcon(String headIcon) {
		this.headIcon = headIcon;
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
				", loginName='" + loginName + '\'' +
				", mobilePhone='" + mobilePhone + '\'' +
				", address='" + address + '\'' +
				", password='" + password + '\'' +
				", gender=" + gender +
				", hobby='" + hobby + '\'' +
				", email='" + email + '\'' +
				", realName='" + realName + '\'' +
				", cityId=" + cityId +
				", lastLoginTime='" + lastLoginTime + '\'' +
				", signature='" + signature + '\'' +
				", headIcon='" + headIcon + '\'' +
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
