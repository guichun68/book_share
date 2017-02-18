package zyzx.linke.utils;


import android.app.Activity;

import zyzx.linke.model.bean.User;

public class GlobalParams {
	public static String BASE_URL ;

	static{
		BASE_URL = BeanFactoryUtil.properties.getProperty("BaseURL_genymotion");
	}
	public static Activity gCurrAct;
	/**
	 * 登录URL
	 */
	public static String urlLogin = GlobalParams.BASE_URL+"/zyzx/login";
	/**
	 * 发送SMS登录验证码用
	 */
	public static String urlSmsLogin = GlobalParams.BASE_URL+"/zyzx/sms_login";
	public static int gVerifyCode ;

	public static User gUser;//登录成功后记录的用户
	public static void refreshIP(){
		urlLogin = GlobalParams.BASE_URL+"/zyzx/login";
		urlSmsLogin = GlobalParams.BASE_URL+"/zyzx/sms_login";
	}

}
