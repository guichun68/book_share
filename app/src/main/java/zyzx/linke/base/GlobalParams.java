package zyzx.linke.base;


import android.app.Activity;

import zyzx.linke.model.IModel;
import zyzx.linke.model.bean.UserVO;
import zyzx.linke.presentation.IBookPresenter;
import zyzx.linke.presentation.IUserPresenter;
import zyzx.linke.utils.PreferenceManager;

public class GlobalParams {

	public static final Integer AROUND = 5000;//查询地图时默认搜索的半径
    public static String BASE_URL, AvatarDirName,BookCoverDirName;
	protected static IUserPresenter gUserPresenter;
	protected static IModel gModel;
	protected static IBookPresenter gBookPresenter;
	public static boolean gIsPersonCenterScan;//是否是个人中心的扫描行为
	public static String gCurrCity="北京";//当前所在城市，默认北京
	public static boolean isDrawerOpened;

	public static String image_file_location;//图片本地存储地址
	public static boolean isCheckedUpdate;//是否已经自动检查更新了
	public static String BaseDir = "NearBook";//本地默认保存文件夹名
	//环信是否已经断开
    public static boolean isLostEaseConn;


    static{
		BASE_URL = BeanFactoryUtil.properties.getProperty("chanURL");
		AvatarDirName = BeanFactoryUtil.properties.getProperty("AvatarDirName");
		BookCoverDirName = BeanFactoryUtil.properties.getProperty("BookCoverName");
	}


	public static Activity gCurrAct;

	/**
	 * 登录URL
	 */
//	public static String urlLogin = GlobalParams.BASE_URL+"/zyzx/login.action";
	public static String urlLogin = GlobalParams.BASE_URL+"/user/mobileSDK/login.action";
    //第三方账号登录
	public static String urlthirdLogin = GlobalParams.BASE_URL+"/user/third_platform_login.action";
	//分享图书
	public static String urlShareBook = GlobalParams.BASE_URL+"/user/mobileSDK/shareBook.action";
	//得到所有共享的书籍
	public static String urlGetSharedBooks = GlobalParams.BASE_URL+"/user/mobileSDK/getAllsharedBook.action";
	public static String urlGetArea2BookStatusId = GlobalParams.BASE_URL+"/app/mobileSDK/getAreaAndBookStatus.action";
	public static String urlSendBegMsg = GlobalParams.BASE_URL+"/user/mobileSDK/sendMessage.action";
	public static String urlDelFriend = GlobalParams.BASE_URL+"/user/delFriend.action";
	public static String urlGetSubArea = GlobalParams.BASE_URL+"/area/mobileSDK/getArea.action";
	public static String urlGetSkillType = GlobalParams.BASE_URL+"/user/mobileSDK/getSkillType.action";
	public static String urlPulishSkill = GlobalParams.BASE_URL+"/user/mobileSDK/publishSkill.action";
	public static String urlGetSwapSkillDetal = GlobalParams.BASE_URL+"/user/mobileSDK/getSwapSkillDetail.action";
	public static String urlDelSkillSwap = GlobalParams.BASE_URL+"/user/mobileSDK/delSkillSwap.action";
	public static String urlGetBookInfo = GlobalParams.BASE_URL+"/app/mobileSDK/getBookInfo.action";
	public static String urlAddAttention = GlobalParams.BASE_URL+"/user/mobileSDK/addAttention.action";
	public static String urlCheckIfAtentioned = GlobalParams.BASE_URL+"/user/mobileSDK/checkIfAttentioned.action";
	public static String urlCancelAttention = GlobalParams.BASE_URL+"/user/mobileSDK/cancelAttention.action";
	public static String urlReport = GlobalParams.BASE_URL+"/user/mobileSDK/report.action";
	/**
	 * 保存手动录入的书籍信息(包含图片）url
	 */
	public static String urlUploadBook = GlobalParams.BASE_URL+"/user/zyzx/uploadbook.action";
	/**
	 * 上传头像图片url
	 */
	public static String urlUploadHeadIcon = GlobalParams.BASE_URL+"/user/mobileSDK/uploadHeadIcon.action";
	//导入书籍清单
	public static String urlUploadExcel = GlobalParams.BASE_URL+"/user/mobileSDK/uploadExcel.action";
	/**
	 * 关于临客
	 */
	public static String urlAboutus = GlobalParams.BASE_URL+"/pages/aboutlinke.html";
	/**
	 * 得到图书分类，如考研、中考等
	 */
	public static String urlGetBookClassify = GlobalParams.BASE_URL+"/getMySql/getSqlByCodeWithNoLogin.action?page.searchValue.queryId=getBookClassify&page.pageSize=30&page.curPage=1";
	public static String urlGetAreaById = GlobalParams.BASE_URL+"/getMySql/getSqlByCodeWithNoLogin.action?page.searchValue.queryId=getArea&page.searchValue.id=#&page.pageSize=30&page.curPage=1";

	public static String downloadFileName = "";
	//保存用户信息
	public static String urlSaveUserInfo = GlobalParams.BASE_URL+"/user/mobileSDK/saveUserInfo.action";
	//验证短信验证码正确否的url
	public static String urlVerifySMSCode = GlobalParams.BASE_URL+"/zyzx/verifySMSCode.action";
	//重置密码
	public static String urlResetPsw = GlobalParams.BASE_URL+"/user/resetPsw.action";
	//localhost//
	public static String urlExportExcle = GlobalParams.BASE_URL+"/user/mobileSDK/getSummary.action";
	//添加到黑名单
	public static String urlAddBlackList = GlobalParams.BASE_URL+"/user/mobileSDK/addBlackList.action";
	//检查是否在对方的黑名单中
	public static String urlCheckIfIMInBlackList = GlobalParams.BASE_URL+"/user/checkIfinBlackList.action";
	//发起添加好友的请求
	public static String urlAddFriend = GlobalParams.BASE_URL+"/user/addFriend.action";
	/**
	 * 获取所有好友
	 */
	public static String urlGetFriends = GlobalParams.BASE_URL+"/zyzx/getAllFriends.action";
	//checkupdate
	public static String urlCheckUpdate = GlobalParams.BASE_URL+"/app/mobileSDK/checkupdate.action";
	public static String urlModifyPsw = GlobalParams.BASE_URL+"/user/mobileSDK/modifyPsw.action";
	public static String urlFeedBack = GlobalParams.BASE_URL+"/user/mobileSDK/feedback.action";
	/**
	 * 默认头像地址
	 */
	public static String urlDefHeadIcon = GlobalParams.BASE_URL+"/pages/image/head.png";

	public static String urlSearchFriend = GlobalParams.BASE_URL+"/zyzx/searchFriend.action";
	//忘记密码页发送验证码
	public static String urlForgetPSWSms = GlobalParams.BASE_URL+"/user/sendForgetPSWSMS.action";
	public static String urlGetUserInfoByUserId = GlobalParams.BASE_URL+"/getMySql/mobileSDK/getSqlByCodeWithLogin.action?page.searchValue.queryId=getUserInfo&page.searchValue.userid=#&page.pageSize=1&page.curPage=1";
	public static String urlGetUserInfoByUid = GlobalParams.BASE_URL+"/getMySql/mobileSDK/getSqlByCodeWithLogin.action?page.searchValue.queryId=getUserInfoByUid&page.searchValue.uid=#&page.pageSize=1&page.curPage=1";
	public static String urlGetUserInfoByUid2 = GlobalParams.BASE_URL+"/user/mobileSDK/getUserInfo.action";
	public static String urlGetUserInfoByUserId2 = GlobalParams.BASE_URL+"/user/mobileSDK/getUserInfoByUserId.action";
	/**
	 * 用户删除指定书籍
	 */
	public static String urlDeleteUserBooks= GlobalParams.BASE_URL+"/user/mobileSDK/delBook.action";
	/**
	 * 获取用户的所有书籍
	 */
	public static String urlGetUserBooks = GlobalParams.BASE_URL+"/zyzx/getUserBooks.action";
	/**
	 * 取消书籍分享
	 */
	public static String urlCancelShare = GlobalParams.BASE_URL+"/user/mobileSDK/cancelShareBook.action";
	/**
	 * 取消分享并从书架删除
	 */
	public static String urlCancelShareAndDelBook = GlobalParams.BASE_URL+"/zyzx/cancelShareAndDelBook.action";
	/**
	 * 获取我登录的所有书籍(借入的除外)
	 */
	public static String urlGetMyBooks = GlobalParams.BASE_URL+"/getMySql/mobileSDK/getSqlByCodeWithLogin.action?page.searchValue.queryId=getMyOwnBooks&page.searchValue.uid={uid}&page.pageSize={pageSize}&page.curPage={curPage}";
	//获得我分享的所有书籍
	public static String urlGetMyShareBooks = GlobalParams.BASE_URL+"/getMySql/mobileSDK/getSqlByCodeWithLogin.action?page.searchValue.queryId=getMySharedBooks&page.searchValue.uid={uid}&page.pageSize={pageSize}&page.curPage={curPage}";
	/**
	 * 获取用户签名
	 */
	public static String urlSetUserSig = GlobalParams.BASE_URL+"/user/mobileSDK/setUserSig.action";
	public static String urlGetBookBorrowBegs = GlobalParams.BASE_URL+"/user/mobileSDK/getMyBorrowBegs.action";
	public static String urlSetFollowStauts = GlobalParams.BASE_URL+"/user/mobileSDK/setFollowStatus.action";
	public static String urlSwapBook = GlobalParams.BASE_URL+"/user/mobileSDK/swapBook.action";
	public static String urlCancelSwapBook = GlobalParams.BASE_URL+"/user/mobileSDK/cancelSwapBook.action";
	/**
	 *通过给定的bookId集合来批量获取这些书籍的详细信息
	 */
	public static String urlGetBooksByIds = GlobalParams.BASE_URL+"/zyzx/getBooksByIds.action";
	/**
	 * 得到我借入的书籍
	 */
	public static String urlGetMyBorrowedInBooks = GlobalParams.BASE_URL+"/user/mobileSDK/getUserBorrowedBooks.action";

	public static String urlGetSwapBooks = GlobalParams.BASE_URL+"/app/mobileSDK/getSwapBooks.action";
	public static String urlGetSwapBookInfo = GlobalParams.BASE_URL+"/app/mobileSDK/getSwapBookInfo.action";

	public static String urlGetSwapSkills = GlobalParams.BASE_URL+"/app/mobileSDK/getSwapSkills.action";
	public static String urlGetMySwapSkills = GlobalParams.BASE_URL+"/user/mobileSDK/getMySwapSkills.action";
	public static String urlSearchBooks = GlobalParams.BASE_URL+"/user/mobileSDK/searchBook.action";
	public static String urlSearchSwapBooks = GlobalParams.BASE_URL+"/app/mobileSDK/searchSwapBook.action";
	public static String urlSearchSwapSkills = GlobalParams.BASE_URL+"/app/mobileSDK/searchSwapSkill.action";
	public static String urlSearchSwapWantBooks = GlobalParams.BASE_URL+"/app/mobileSDK/searchSwapWantBook.action";
	public static String urlSearchSwapWantSkills = GlobalParams.BASE_URL+"/app/mobileSDK/searchSwapWantSkill.action";
	public static String urlGetAttentions = GlobalParams.BASE_URL+"/user/mobileSDK/getAttentions.action";
	public static String urlGetMyBlackList = GlobalParams.BASE_URL+"/user/mobileSDK/getMyBlackList.action";

	/**
	 * 注册
	 */
	public static String urlRegist = GlobalParams.BASE_URL+"/user/mobileSDK/regUser.action";
	/**
	 * 当用户在地图中分享图书成功后，立即更改表zyzx_user_books表中该书籍的状态为“已添加到地图"
	 */
	public static String urlSetBookStatus = GlobalParams.BASE_URL+"/zyzx/set_zyzx_user_book_status.action";
	/**
	 * 发送SMS登录验证码用
	 */
	public static String urlSmsLogin = GlobalParams.BASE_URL+"/zyzx/sms_login.action";
	public static final String urlISBNAPI = "https://api.douban.com/v2/book/isbn/";
	public static String urlAddBook2MyLib = GlobalParams.BASE_URL+"/user/mobileSDK/addBook2Lib.action";
	public static String urlAddManualBook2Lib = GlobalParams.BASE_URL+"/user/mobileSDK/uploadManualBook.action";
//	public static String urlAddBook2Map = GlobalParams.BASE_URL+"/zyzx/add2Map.action";
    //会话页面点击用户聊天时获得用户信息
    public static String urlGetUserInfoInConversation = GlobalParams.BASE_URL+"/user/getUserInfoInConversation.action";

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
	public static boolean shouldRefreshContactList;//需要更新好友列表么？

	private static UserVO gUserVO;//登录成功后记录的用户
	public static void refreshIP(){
//		urlLogin = GlobalParams.BASE_URL+"/zyzx/login.action";
		urlLogin = GlobalParams.BASE_URL+"/user/mobileSDK/login.action";
        urlthirdLogin = GlobalParams.BASE_URL+"/user/third_platform_login.action";
		urlSmsLogin = GlobalParams.BASE_URL+"/zyzx/sms_login.action";
		urlAboutus = GlobalParams.BASE_URL+"/pages/aboutlinke.html";
		urlRegist = GlobalParams.BASE_URL+"/user/mobileSDK/regUser.action";
		urlAddBook2MyLib = GlobalParams.BASE_URL+"/user/mobileSDK/addBook2Lib.action";
//		urlAddBook2Map = GlobalParams.BASE_URL+"/zyzx/add2Map.action";
		urlGetUserInfoByUserId = GlobalParams.BASE_URL+"/getMySql/mobileSDK/getSqlByCodeWithLogin.action?page.searchValue.queryId=getUserInfo&page.searchValue.userid=#&page.pageSize=1&page.curPage=1";
		urlGetUserInfoByUid = GlobalParams.BASE_URL+"/getMySql/mobileSDK/getSqlByCodeWithLogin.action?page.searchValue.queryId=getUserInfoByUid&page.searchValue.uid=#&page.pageSize=1&page.curPage=1";
		urlGetUserInfoByUserId2 = GlobalParams.BASE_URL+"/user/mobileSDK/getUserInfoByUserId.action";
		urlGetUserInfoByUid2 = GlobalParams.BASE_URL+"/user/mobileSDK/getUserInfo.action";
		urlSetBookStatus = GlobalParams.BASE_URL+"/zyzx/set_zyzx_user_book_status.action";
		urlGetUserBooks = GlobalParams.BASE_URL+"/zyzx/getUserBooks.action";
		urlGetBooksByIds = GlobalParams.BASE_URL+"/zyzx/getBooksByIds.action";
		urlUploadBook = GlobalParams.BASE_URL+"/zyzx/uploadbook.action";
		urlUploadHeadIcon = GlobalParams.BASE_URL+"/user/mobileSDK/uploadHeadIcon.action";
		urlSetUserSig = GlobalParams.BASE_URL+"/user/mobileSDK/setUserSig.action";
		urlGetBookBorrowBegs = GlobalParams.BASE_URL+"/user/mobileSDK/getMyBorrowBegs.action";
		urlSetFollowStauts = GlobalParams.BASE_URL+"/user/mobileSDK/setFollowStatus.action";
		urlSwapBook = GlobalParams.BASE_URL+"/user/mobileSDK/swapBook.action";
		urlCancelSwapBook = GlobalParams.BASE_URL+"/user/mobileSDK/cancelSwapBook.action";
		urlGetMyBooks = GlobalParams.BASE_URL+"/getMySql/mobileSDK/getSqlByCodeWithLogin.action?page.searchValue.queryId=getMyOwnBooks&page.searchValue.uid={uid}&page.pageSize={pageSize}&page.curPage={curPage}";
		urlGetMyShareBooks = GlobalParams.BASE_URL+"/getMySql/mobileSDK/getSqlByCodeWithLogin.action?page.searchValue.queryId=getMySharedBooks&page.searchValue.uid={uid}&page.pageSize={pageSize}&page.curPage={curPage}";
		urlDeleteUserBooks= GlobalParams.BASE_URL+"/user/mobileSDK/delBook.action";
		urlShareBook = GlobalParams.BASE_URL+"/user/mobileSDK/shareBook.action";
		urlGetSkillType = GlobalParams.BASE_URL+"/user/mobileSDK/getSkillType.action";
		urlPulishSkill = GlobalParams.BASE_URL+"/user/mobileSDK/publishSkill.action";
		urlGetSwapSkillDetal = GlobalParams.BASE_URL+"/user/mobileSDK/getSwapSkillDetail.action";
        urlDelSkillSwap = GlobalParams.BASE_URL+"/user/mobileSDK/delSkillSwap.action";
		urlGetBookInfo = GlobalParams.BASE_URL+"/app/mobileSDK/getBookInfo.action";
		urlAddAttention = GlobalParams.BASE_URL+"/user/mobileSDK/addAttention.action";
		urlCheckIfAtentioned = GlobalParams.BASE_URL+"/user/mobileSDK/checkIfAttentioned.action";
		urlCancelAttention = GlobalParams.BASE_URL+"/user/mobileSDK/cancelAttention.action";
		urlReport = GlobalParams.BASE_URL+"/user/mobileSDK/report.action";
		urlGetSharedBooks = GlobalParams.BASE_URL+"/user/mobileSDK/getAllsharedBook.action";
		urlGetArea2BookStatusId = GlobalParams.BASE_URL+"/app/mobileSDK/getAreaAndBookStatus.action";
		urlSendBegMsg = GlobalParams.BASE_URL+"/user/mobileSDK/sendMessage.action";
		urlCancelShare = GlobalParams.BASE_URL+"/user/mobileSDK/cancelShareBook.action";
		urlCancelShareAndDelBook = GlobalParams.BASE_URL+"/zyzx/cancelShareAndDelBook.action";
		urlGetMyBorrowedInBooks = GlobalParams.BASE_URL+"/zyzx/getMyBorrowedInBooks.action";
		urlGetSwapBooks = GlobalParams.BASE_URL+"/app/mobileSDK/getSwapBooks.action";
		urlGetSwapBookInfo = GlobalParams.BASE_URL+"/app/mobileSDK/getSwapBookInfo.action";
        urlGetMyBorrowedInBooks = GlobalParams.BASE_URL+"/user/mobileSDK/getUserBorrowedBooks.action";
		urlGetSwapSkills = GlobalParams.BASE_URL+"/app/mobileSDK/getSwapSkills.action";
		urlSearchFriend = GlobalParams.BASE_URL+"/zyzx/searchFriend.action";
		urlGetMySwapSkills = GlobalParams.BASE_URL+"/app/mobileSDK/getMySwapSkills.action";
		urlSearchBooks = GlobalParams.BASE_URL+"/user/mobileSDK/searchBook.action";
		urlSearchSwapBooks = GlobalParams.BASE_URL+"/app/mobileSDK/searchSwapBook.action";
		urlSearchSwapSkills = GlobalParams.BASE_URL+"/app/mobileSDK/searchSwapSkill.action";
		urlSearchSwapWantBooks = GlobalParams.BASE_URL+"/app/mobileSDK/searchSwapWantBook.action";
		urlSearchSwapWantSkills = GlobalParams.BASE_URL+"/app/mobileSDK/searchSwapWantSkill.action";
		urlGetMyBlackList = GlobalParams.BASE_URL+"/user/mobileSDK/getMyBlackList.action";
		urlGetAttentions = GlobalParams.BASE_URL+"/user/mobileSDK/getAttentions.action";
		urlDefHeadIcon = GlobalParams.BASE_URL+"/pages/image/head.png";
		urlGetFriends = GlobalParams.BASE_URL+"/zyzx/getAllFriends.action";
		urlCheckUpdate = GlobalParams.BASE_URL+"/app/mobileSDK/checkupdate.action";
		urlDelFriend = GlobalParams.BASE_URL+"/user/delFriend.action";
		urlAddBlackList = GlobalParams.BASE_URL+"/user/mobileSDK/addBlackList.action";
		urlCheckIfIMInBlackList = GlobalParams.BASE_URL+"/user/checkIfinBlackList.action";
		urlAddFriend = GlobalParams.BASE_URL+"/user/addFriend.action";
        urlGetUserInfoInConversation = GlobalParams.BASE_URL+"/user/getUserInfoInConversation.action";
		urlForgetPSWSms = GlobalParams.BASE_URL+"/user/sendForgetPSWSMS.action";
		urlVerifySMSCode = GlobalParams.BASE_URL+"/zyzx/verifySMSCode.action";
		urlResetPsw = GlobalParams.BASE_URL+"/user/resetPsw.action";
		urlModifyPsw = GlobalParams.BASE_URL+"/user/mobileSDK/modifyPsw.action";
		urlFeedBack = GlobalParams.BASE_URL+"/user/mobileSDK/feedback.action";
		urlExportExcle = GlobalParams.BASE_URL+"/user/mobileSDK/getSummary.action";
		urlUploadExcel = GlobalParams.BASE_URL+"/user/mobileSDK/uploadExcel.action";
		urlGetSubArea = GlobalParams.BASE_URL+"/area/mobileSDK/getArea.action";
		urlSaveUserInfo = GlobalParams.BASE_URL+"/user/mobileSDK/saveUserInfo.action";
		urlGetBookClassify = GlobalParams.BASE_URL+"/getMySql/getSqlByCodeWithNoLogin.action?page.searchValue.queryId=getBookClassify&page.pageSize=30&page.curPage=1";
		urlGetAreaById = GlobalParams.BASE_URL+"/getMySql/getSqlByCodeWithNoLogin.action?page.searchValue.queryId=getArea&page.searchValue.id=#&page.pageSize=30&page.curPage=1";
		urlAddManualBook2Lib = GlobalParams.BASE_URL+"/user/mobileSDK/uploadManualBook.action";
	}

	public static UserVO getLastLoginUser(){
		if(gUserVO!=null){
			return gUserVO;
		}
		gUserVO = PreferenceManager.getInstance().getLastLoginUser();
		return gUserVO;
	}
	public static void setCurrUserHeadAvatar(String headAvatar){
		getLastLoginUser().setHeadIcon(headAvatar);
		PreferenceManager.getInstance().setCurrentUserAvatar(headAvatar);
		EaseUIHelper.getInstance().getUserProfileManager().getCurrentUser().setAvatar(headAvatar);
	}

	public static void saveUser(UserVO userVO){
		if(userVO!=null) {
			PreferenceManager.getInstance().saveLastLoginUser(userVO);
			gUserVO = userVO;
		}else{
			PreferenceManager.getInstance().saveLastLoginUser(null);
			gUserVO = null;
		}
	}

	static IBookPresenter getBookPresenter(){
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
	static IModel getgModel(){
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

	static IUserPresenter getUserPresenter(){
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
