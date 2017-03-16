package zyzx.linke.constant;


import android.app.Activity;

import zyzx.linke.model.IModel;
import zyzx.linke.model.bean.User;
import zyzx.linke.presentation.IBookPresenter;
import zyzx.linke.presentation.IUserPresenter;

public class GlobalParams {

	public static final Integer AROUND = 5000;//查询地图时默认搜索的半径
	public static String BASE_URL ;
	protected static IUserPresenter gUserPresenter;
	protected static IModel gModel;
	protected static IBookPresenter gBookPresenter;
	public static boolean gIsPersonCenterScan;//是否是个人中心的扫描行为
	public static String gCurrCity="北京";//当前所在城市，默认北京
	public static boolean isDrawerOpened;

	public static String image_file_location;//图片本地存储地址
	static{
		BASE_URL = BeanFactoryUtil.properties.getProperty("chanURL");
	}


	public static Activity gCurrAct;

	/**
	 * 登录URL
	 */
	public static String urlLogin = GlobalParams.BASE_URL+"/zyzx/login.action";
	/**
	 * 保存手动录入的书籍信息(包含图片）url
	 */
	public static String urlUploadBook = GlobalParams.BASE_URL+"/zyzx/uploadbook.action";
	/**
	 * 上传头像图片url
	 */
	public static String urlUploadHeadIcon = GlobalParams.BASE_URL+"/zyzx/uploadHeadIcon.action";
	/**
	 * 关于临客
	 */
	public static String urlAboutus = GlobalParams.BASE_URL+"/pages/aboutlinke.html";
	/**
	 * 通过用户id获取其下所有书籍和其用户基本信息
	 */
	public static String urlGetUserInfo= GlobalParams.BASE_URL+"/zyzx/getUserinfo.action";
	/**
	 * 获取用户的所有书籍
	 */
	public static String urlGetUserBooks = GlobalParams.BASE_URL+"/zyzx/getUserBooks.action";
	/**
	 * 获取我登录的所有书籍(借入的除外)
	 */
	public static String urlGetMyBooks = GlobalParams.BASE_URL+"/zyzx/getMyBooks.action";
	/**
	 * 获取用户签名
	 */
	public static String urlSetUserSig = GlobalParams.BASE_URL+"/zyzx/setUserSig.action";
	/**
	 *通过给定的bookId集合来批量获取这些书籍的详细信息
	 */
	public static String urlGetBooksByIds = GlobalParams.BASE_URL+"/zyzx/getBooksByIds.action";
	/**
	 * 注册
	 */
	public static String urlRegist = GlobalParams.BASE_URL+"/zyzx/regist.action";
	/**
	 * 当用户在地图中分享图书成功后，立即更改表zyzx_user_books表中该书籍的状态为“已添加到地图"
	 */
	public static String urlSetBookStatus = GlobalParams.BASE_URL+"/zyzx/set_zyzx_user_book_status.action";
	/**
	 * 发送SMS登录验证码用
	 */
	public static String urlSmsLogin = GlobalParams.BASE_URL+"/zyzx/sms_login.action";
	public static final String urlISBNAPI = "https://api.douban.com/v2/book/isbn/";
	public static String urlAddBook2MyLib = GlobalParams.BASE_URL+"/zyzx/add2MyLib.action";
	public static String urlAddBook2Map = GlobalParams.BASE_URL+"/zyzx/add2Map.action";

	//向高德地图中添加一条记录
	public static String urlAddbook2Gaode = "http://yuntuapi.amap.com/datamanage/data/create";
	/**
	 * 更新数据，单条
	 */
	public static String urlGaodeBookUpdate = "http://yuntuapi.amap.com/datamanage/data/update";
	/**
	 * 在指定tableid的数据表内，搜索指定中心点和半径范围内的数据
	 */
	public static String urlQueryBookFromMapAround = "http://yuntuapi.amap.com/datasearch/around";
	public static int gVerifyCode ;

	public static User gUser;//登录成功后记录的用户
	public static void refreshIP(){
		urlLogin = GlobalParams.BASE_URL+"/zyzx/login.action";
		urlSmsLogin = GlobalParams.BASE_URL+"/zyzx/sms_login.action";
		urlAboutus = GlobalParams.BASE_URL+"/pages/aboutlinke.html";
		urlRegist = GlobalParams.BASE_URL+"/zyzx/regist.action";
		urlAddBook2MyLib = GlobalParams.BASE_URL+"/zyzx/add2MyLib.action";
		urlAddBook2Map = GlobalParams.BASE_URL+"/zyzx/add2Map.action";
		urlGetUserInfo = GlobalParams.BASE_URL+"/zyzx/getUserinfo.action";
		urlSetBookStatus = GlobalParams.BASE_URL+"/zyzx/set_zyzx_user_book_status.action";
		urlGetUserBooks = GlobalParams.BASE_URL+"/zyzx/getUserBooks.action";
		urlGetBooksByIds = GlobalParams.BASE_URL+"/zyzx/getBooksByIds.action";
		urlUploadBook = GlobalParams.BASE_URL+"/zyzx/uploadbook.action";
		urlUploadHeadIcon = GlobalParams.BASE_URL+"/zyzx/uploadHeadIcon.action";
		urlSetUserSig = GlobalParams.BASE_URL+"/zyzx/setUserSig.action";
		urlGetMyBooks = GlobalParams.BASE_URL+"/zyzx/getMyBooks.action";
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
