package zyzx.linke.utils;


import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.Nullable;

import zyzx.linke.model.IModel;
import zyzx.linke.model.bean.User;
import zyzx.linke.presentation.IBookPresenter;
import zyzx.linke.presentation.IUserPresenter;

public class GlobalParams {

	public static String BASE_URL ;
	protected static IUserPresenter gUserPresenter;
	protected static IModel gModel;
	protected static IBookPresenter gBookPresenter;
	public static boolean gIsPersonCenterScan;//是否是个人中心的扫描行为

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
	public static final String urlISBNAPI = "https://api.douban.com/v2/book/isbn/";
	public static String urlAddBook2MyLib = GlobalParams.BASE_URL+"/zyzx/add2MyLib";
	public static int gVerifyCode ;

	public static User gUser;//登录成功后记录的用户
	public static void refreshIP(){
		urlLogin = GlobalParams.BASE_URL+"/zyzx/login";
		urlSmsLogin = GlobalParams.BASE_URL+"/zyzx/sms_login";
		urlAboutus = GlobalParams.BASE_URL+"/pages/aboutlinke.html";
		urlRegist = GlobalParams.BASE_URL+"/zyzx/regist";
		urlAddBook2MyLib = GlobalParams.BASE_URL+"/zyzx/add2MyLib";
	}

	public static IBookPresenter getBookPresenter(){
		if(GlobalParams.gBookPresenter!=null){
			return  gBookPresenter;
		}
		try {
			GlobalParams.gBookPresenter = BeanFactoryUtil.getImpl(IBookPresenter.class);
			return gBookPresenter;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public static IModel getgModel(){
		if(GlobalParams.gModel!=null){
			return  gModel;
		}
		try {
			GlobalParams.gModel = BeanFactoryUtil.getImpl(IModel.class);
			return gModel;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static IUserPresenter getUserPresenter(){
		if(GlobalParams.gUserPresenter!=null){
			return  gUserPresenter;
		}
		try {
			gUserPresenter = BeanFactoryUtil.getImpl(IUserPresenter.class);
			return gUserPresenter;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
