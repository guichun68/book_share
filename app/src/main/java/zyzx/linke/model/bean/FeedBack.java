package zyzx.linke.model.bean;

public class FeedBack {
	/**
	 * 手机网络制式
	 * 0 unknown;1 GPRS;2 EDGE;3 UMTS;4 CDMA: Either IS95A or IS95B;
	 * 5 EVDO revision 0;6 EVDO revision A;7 1xRTT;8 HSDPA;9 HSUPA;
	 * 10 HSPA;11 iDen;12 EVDO revision B;13 LTE;14 eHRPD;15 HSPA+ 
	 */
	private String phoneNetworkStandard;
	private String phoneOperatorName;//网络运营商
	private String phoneOsVersion;
	private String phoneModel;//手机型号 如Lenovo A320t
	private String isWifiConnected;
	private String isMobileConnected;
	private String phoneNetWorkStatus;//网络状态：Wifi，4G，手机网络，无网络
	private String phoneBlueToothMac;
	private String phoneNum;//手机号
	
	private String appVersion;
	private String userLoginName;
	private String userRealName;
	
	private String title;
	private String contactWay;
	private String content;
	
	public String getPhoneNetworkStandard() {
		return phoneNetworkStandard;
	}
	public FeedBack setPhoneNetworkStandard(String phoneNetworkStandard) {
		this.phoneNetworkStandard = phoneNetworkStandard;
		return this;
	}
	public String getPhoneOperatorName() {
		return phoneOperatorName;
	}
	public FeedBack setPhoneOperatorName(String phoneOperatorName) {
		this.phoneOperatorName = phoneOperatorName;
		return this;
	}
	public String getPhoneOsVersion() {
		return phoneOsVersion;
	}
	public FeedBack setPhoneOsVersion(String phoneOsVersion) {
		this.phoneOsVersion = phoneOsVersion;
		return this;
	}
	public String getPhoneModel() {
		return phoneModel;
	}
	public FeedBack setPhoneModel(String phoneModel) {
		this.phoneModel = phoneModel;
		return this;
	}
	public String getPhoneNetWorkStatus() {
		return phoneNetWorkStatus;
	}
	public FeedBack setPhoneNetWorkStatus(String phoneNetWorkStatus) {
		this.phoneNetWorkStatus = phoneNetWorkStatus;
		return this;
	}
	public String getPhoneBlueToothMac() {
		return phoneBlueToothMac;
	}
	public FeedBack setPhoneBlueToothMac(String phoneBlueToothMac) {
		this.phoneBlueToothMac = phoneBlueToothMac;
		return this;
	}
	public String getAppVersion() {
		return appVersion;
	}
	public FeedBack setAppVersion(String appVersion) {
		this.appVersion = appVersion;
		return this;
	}
	public String getUserLoginName() {
		return userLoginName;
	}
	public FeedBack setUserLoginName(String userLoginName) {
		this.userLoginName = userLoginName;
		return this;
	}
	public String getUserRealName() {
		return userRealName;
	}
	public FeedBack setUserRealName(String userRealName) {
		this.userRealName = userRealName;
		return this;
	}
	public String getTitle() {
		return title;
	}
	public FeedBack setTitle(String title) {
		this.title = title;
		return this;
	}
	public String getContactWay() {
		return contactWay;
	}
	public FeedBack setContactWay(String contactWay) {
		this.contactWay = contactWay;
		return this;
	}
	public String getContent() {
		return content;
	}
	public FeedBack setContent(String content) {
		this.content = content;
		return this;
	}
	public String getPhoneNum() {
		return phoneNum;
	}
	public FeedBack setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
		return this;
	}
	public String getIsWifiConnected() {
		return isWifiConnected;
	}
	public FeedBack setIsWifiConnected(String isWifiConnected) {
		this.isWifiConnected = isWifiConnected;
		return this;
	}
	public String getIsMobileConnected() {
		return isMobileConnected;
	}
	public FeedBack setIsMobileConnected(String isMobileConnected) {
		this.isMobileConnected = isMobileConnected;
		return this;
	}
		
	
}
