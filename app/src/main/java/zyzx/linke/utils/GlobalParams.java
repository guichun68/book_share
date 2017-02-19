package zyzx.linke.utils;


import android.app.Activity;
import android.graphics.Color;

import zyzx.linke.model.IModel;
import zyzx.linke.model.bean.User;
import zyzx.linke.presentation.IUserPresenter;

public class GlobalParams {

	public static String BASE_URL ;
	public static IUserPresenter gUserPresenter;
	public static IModel gModel;

	static{
		BASE_URL = BeanFactoryUtil.properties.getProperty("BaseURL_genymotion");
	}
	public static Activity gCurrAct;
	/**
	 * 登录URL
	 */
	public static String urlLogin = GlobalParams.BASE_URL+"/zyzx/login";
	/**
	 * 关于临客
	 */
	public static String urlAboutus = GlobalParams.BASE_URL+"/pages/aboutlinke.html";
	/**
	 * 注册
	 */
	public static String urlRegist = GlobalParams.BASE_URL+"/zyzx/regist";
	/**
	 * 发送SMS登录验证码用
	 */
	public static String urlSmsLogin = GlobalParams.BASE_URL+"/zyzx/sms_login";
	public static int gVerifyCode ;

	public static User gUser;//登录成功后记录的用户
	public static void refreshIP(){
		urlLogin = GlobalParams.BASE_URL+"/zyzx/login";
		urlSmsLogin = GlobalParams.BASE_URL+"/zyzx/sms_login";
		urlAboutus = GlobalParams.BASE_URL+"/pages/aboutlinke.html";
		urlRegist = GlobalParams.BASE_URL+"/zyzx/regist";
	}

}
