package zyzx.linke.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import zyzx.linke.base.GlobalParams;
import zyzx.linke.model.bean.BookDetail2;
import zyzx.linke.model.bean.BorrowFlow;
import zyzx.linke.model.bean.BorrowFlowVO;
import zyzx.linke.model.bean.BorrowedInVO;
import zyzx.linke.model.bean.EnumConst;
import zyzx.linke.model.bean.Images;
import zyzx.linke.model.bean.MyBookDetailVO;
import zyzx.linke.model.bean.Rating;
import zyzx.linke.model.bean.SwapBookVO;
import zyzx.linke.model.bean.SwapSkillVo;
import zyzx.linke.model.bean.TelephonyManagerInfo;

public class AppUtil {
//	public static DbUtils db = DbUtils.create(GlobalParams.MAIN,
//			FileUtils.getDir(GlobalParams.MAIN, ""), "hifm.db");
	public static Date today;
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",
			Locale.getDefault());
	private static final String TAG = "AppUtil";

	private static final String APK_PATH = "/sdcard/zyzx.apk";

	public static void installApp(Context context, String path) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(path)),
				"application/vnd.android.package-archive");

		context.startActivity(intent);
	}

	/**
	 * 获取应用程序的版本信息
	 * 
	 * @param context
	 *            上下文
	 * @param packname
	 *            包名
	 * @return 版本号
	 */
	public static int getAppVersionCode(Context context, String packname) {
		PackageManager pm = context.getPackageManager();
		try {
			PackageInfo info = pm.getPackageInfo(packname, 0);
			return info.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return 1;
		}
	}

	/**
	 * 获取应用程序的版本信息
	 * 
	 * @param context
	 *            上下文
	 * @return 版本号
	 */
	public static String getAppVersionName(Context context) {
		PackageManager pm = context.getPackageManager();
		try {
			ApplicationInfo info = pm.getApplicationInfo("zyzx.linke",
					PackageManager.GET_META_DATA);
			int id = info.uid;
		} catch (NameNotFoundException e1) {
			e1.printStackTrace();
		}
		// 获取当前应用程序的VersionName
		String packname = context.getPackageName();
		try {
			PackageInfo info = pm.getPackageInfo(packname, 0);
			return info.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 查看是否有新版本
	 * 
	 * @param versionCode
	 * @param versionName
	 * @return true:需要更新false：不需要更新
	 */
	private static boolean checkWeatherNeedUpdate(String versionCode,
			String versionName) {
		String[] split1 = versionCode.split("\\.");
		String[] split2 = versionName.split("\\.");
		if (split1.length == 3 && split2.length == 3) {
			for (int i = 0; i < 3; i++) {
				int remote = Integer.valueOf(split1[i]);
				int local = Integer.valueOf(split2[i]);
				if (remote > local) {
					return true;
				} else if (remote == local) {
					continue;
				} else {
					return false;
				}
			}
			return false;
		} else {
			return false;
		}
	}

/*	private static void downloadApp(final Context context, String path) {
		HttpUtils http = new HttpUtils();
		HttpHandler handler = http.download(path, APK_PATH, true, // 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
				false, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
				new RequestCallBack<File>() {

					private ProgressDialog dialog;

					@Override
					public void onStart() {
						// System.out.println("conn...");
						showProgressDialog();
					}

					*//**
					 * 显示升级提醒对话框
					 *//*
					private void showProgressDialog() {
						dialog = new ProgressDialog(context);
						dialog.setCancelable(false);
						dialog.setTitle("更新中...");
						// pDialog.setMessage("请稍后。。");
						// 设置进度条对话框//样式（水平，旋转）
						dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

						// 进度最大值
						// dialog.setMax(MAX_PROGRESS);

						// 显示
						dialog.show();

						// 必须设置到show之后
						// progress = (progress > 0) ? progress : 0;
						// dialog.setProgress(progress);
					}

					@Override
					public void onLoading(long total, long current,
							boolean isUploading) {
						System.out.println(current + "/" + total);

						dialog.setMax((int) total);
						dialog.setProgress((int) current);
					}

					@Override
					public void onSuccess(ResponseInfo<File> responseInfo) {
						dialog.dismiss();

						// System.out.println("downloaded:"
						// + responseInfo.result.getPath());

						// 调用系统的程序安装器来安装apk
						installApp(context, responseInfo.result.getPath());
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						dialog.dismiss();

						System.out.println(msg);

						if (msg != null
								&& msg.contains("downloaded completely")) {
							// 调用系统的程序安装器来安装apk
							installApp(context, APK_PATH);
							return;
						}

						UIUtil.showTestToast("下载失败" + msg);

					}
				});

	}*/

	/**
	 * 判断应用是否在运行
	 * 
	 * @param pkgName
	 * @return
	 */
	private static boolean isRunning(String pkgName) {
		// 获取一个ActivityManager 对象
		ActivityManager activityManager = (ActivityManager) UIUtil.getContext()
				.getSystemService(Context.ACTIVITY_SERVICE);
		// 获取系统中所有正在运行的进程
		List<RunningAppProcessInfo> appProcessInfos = activityManager
				.getRunningAppProcesses();
		// 对系统中所有正在运行的进程进行迭代，如果发现进程名，则return true
		for (RunningAppProcessInfo appProcessInfo : appProcessInfos) {
			String processName = appProcessInfo.processName;
			if (processName.equals(pkgName)) {
				return true;
			}
		}

		return false;
	}


	/**
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isBackground(Context context) {

		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> appProcesses = activityManager
				.getRunningAppProcesses();
		for (RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.processName.equals(context.getPackageName())) {
				if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
					// System.out.println("Background App:");
					return true;
				} else {
					// System.out.println("Foreground App:");
					return false;
				}
			}
		}
		return false;
	}



	// To check if service is enabled
	private static boolean isAccessibilitySettingsOn(Context mContext) {
		int accessibilityEnabled = 0;
		final String service = "cn.hifm/cn.hifm.service.MyAccessibilityService";
		boolean accessibilityFound = false;
		try {
			accessibilityEnabled = Settings.Secure.getInt(mContext
					.getApplicationContext().getContentResolver(),
					Settings.Secure.ACCESSIBILITY_ENABLED);
			Log.v(TAG, "accessibilityEnabled = " + accessibilityEnabled);
		} catch (SettingNotFoundException e) {
			Log.e(TAG,
					"Error finding setting, default accessibility to not found: "
							+ e.getMessage());
		}

		TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(
				':');

		if (accessibilityEnabled == 1) {
			Log.v(TAG, "***ACCESSIBILIY IS ENABLED*** -----------------");
			String settingValue = Settings.Secure.getString(mContext
					.getApplicationContext().getContentResolver(),
					Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
			if (settingValue != null) {
				TextUtils.SimpleStringSplitter splitter = mStringColonSplitter;
				splitter.setString(settingValue);
				while (splitter.hasNext()) {
					String accessabilityService = splitter.next();

					Log.v(TAG, "-------------- > accessabilityService :: "
							+ accessabilityService);
					if (accessabilityService.equalsIgnoreCase(service)) {
						Log.v(TAG,
								"We've found the correct setting - accessibility is switched on!");
						return true;
					}
				}
			}

		} else {
			Log.v(TAG, "***ACCESSIBILIY IS DISABLED***");
		}

		return accessibilityFound;
	}

	/**
	 * 得到当前APP的uid
	 * 
	 * @return
	 */
	public static Integer getCurrAppUid() {
		PackageManager pm = GlobalParams.gCurrAct.getPackageManager();
		try {
			ApplicationInfo info = pm.getApplicationInfo(
					GlobalParams.gCurrAct.getPackageName(),
					PackageManager.GET_META_DATA);
			return info.uid;
		} catch (NameNotFoundException e1) {
			e1.printStackTrace();
			return null;
		}
	}



	/**
	 * 一次WiFi从可用到不可用时间内WiFi流量
	 */
	public static long mWiFiStep = 0L;
	public static long mWiFiTotal;
	/**
	 * 进入WiFi代码块时记录当前的流量和(所有网络连接状态,只第一次进入时记录之)
	 */
	public static long mFirstInTotalStart = 0L;
	public static long mFirstInMobileStart = 0L;
	/**
	 * 出WiFi代码块时记录此时HiFM产生的总流量
	 */
	public static long mFirstInTotalEnd = 0L;
	public static long mFirstInMobileEnd = 0L;

	public static boolean isFirstInWiFi = true;



	/**
	 * 得到APP自安装之时至当前时刻产生的总流量
	 * 
	 * @return
	 */
	public static long getAllTraffic() {
		Integer appUid = getCurrAppUid();
		if (appUid != null) {
			long totalRxBytesW = TrafficStats.getUidRxBytes(appUid);
			// 获取发送的字节总数，包含Mobile和WiFi等.
			long totalTxBytesW = TrafficStats.getUidTxBytes(appUid);
			return totalRxBytesW + totalTxBytesW;
		}
		return 0L;
	}


	/*
	 * ----------------得到WiFi流量增量----------------------
	 */
	public static boolean isFirstIn = true;

	

	public static Long getWiFiTotalToday() {
		return null;
	}

	/*
	 * check if the App is installed
	 */
	public static boolean isAppInstalled(Context context, String packagename) {
		PackageInfo packageInfo;
		try {
			packageInfo = context.getPackageManager().getPackageInfo(
					packagename, 0);
		} catch (NameNotFoundException e) {
			packageInfo = null;
			// e.printStackTrace();
		}
		if (packageInfo == null) {
			// System.out.println("没有安装");
			return false;
		} else {
			// System.out.println("已经安装");
			return true;
		}
	}

	/**
	 * 得到最清晰的图片的url
	 * @return
	 */
	public static String getMostDistinctPicUrl(BookDetail2 book) {
		String url = null;
		if(!StringUtil.isEmpty(book.getImages().getLarge())){
			url = book.getImages().getLarge();
		}else
		if(!StringUtil.isEmpty(book.getImage())){
			url = book.getImage();
		}else
		if(!StringUtil.isEmpty(book.getImages().getMedium())){
			url =  book.getImages().getMedium();
		}else
		if(!StringUtil.isEmpty(book.getImage_medium())){
			url = book.getImage_medium();
		}else
		if(!StringUtil.isEmpty(book.getImages().getSmall())){
			url = book.getImages().getSmall();
		}
		if(url != null){
			if(url.contains("http") || url.contains("HTTP")){

			}else{
				url = GlobalParams.BASE_URL+GlobalParams.BookCoverDirName+url;
			}
		}
		return url;
	}
	public static int getScreenWidth(Context ctx) {
		WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
		int screenWidth = wm.getDefaultDisplay().getWidth();
		return screenWidth;
	}

	public static int getScreenHeight(Context ctx) {
		WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
		int screenHeight = wm.getDefaultDisplay().getHeight();
		return screenHeight;
	}

	/**
	 * 获取手机信息实体
	 *
	 * @param context
	 * @return
	 */
	public static TelephonyManagerInfo getTelephonyInfo(Context context) {
		TelephonyManagerInfo info = new TelephonyManagerInfo();
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		info.CallState = tm.getCallState();
		info.CellLocation = tm.getCellLocation() != null ? tm.getCellLocation().toString() : "";
		info.DeviceId = tm.getDeviceId();
		info.DeviceSoftwareVersion = tm.getDeviceSoftwareVersion();
		info.hasIccCard = tm.hasIccCard();
		info.isNetworkRoaming = tm.isNetworkRoaming();
		info.Line1Number = tm.getLine1Number();
		info.NetworkCountryIso = tm.getNetworkCountryIso();
		info.NetworkOperator = tm.getNetworkOperator();
		info.NetworkOperatorName = tm.getNetworkOperatorName();
		info.NetworkType = tm.getNetworkType();
		info.PhoneType = tm.getPhoneType();
		info.SimCountryIso = tm.getSimCountryIso();
		info.SimOperator = tm.getSimOperator();
		info.SimOperatorName = tm.getSimOperatorName();
		info.SimSerialNumber = tm.getSimSerialNumber();
		info.SimState = tm.getSimState();
		info.SubscriberId = tm.getSubscriberId();
		info.VoiceMailAlphaTag = tm.getVoiceMailAlphaTag();
		info.VoiceMailNumber = tm.getVoiceMailNumber();
		return info;
	}

	// 手机系统版本
	public static String getOsDisplay() {
		return Build.DISPLAY;
	}

	/**
	 * 获取手机型号
	 *
	 * @return
	 */
	public static String getPhoneType() {
		return Build.MODEL;
	}

    public static ArrayList<MyBookDetailVO> getBookDetailVOs(List<JSONObject> items) {
		ArrayList<MyBookDetailVO> mBvos = new ArrayList<>();
		for (JSONObject jo:items) {
			MyBookDetailVO mBV = new MyBookDetailVO();
			BookDetail2 mV = new BookDetail2();
			mV.setPubdateDateType(jo.getDate("CREATE_DATE"));
			mV.setAlt(jo.getString("alt"));
			mV.setTitle(jo.getString("alt_title"));
			String authors = jo.getString("a_name");
			String[] auArr = {};
			if(authors!=null)
			{
				auArr= authors.split(";");
			}
			List<String> au = new ArrayList<>();
			for(int i=0;i<auArr.length;i++){
				au.add(auArr[i]);
			}
			mV.setAuthor(au);
			mV.setAuthor_intro(jo.getString("author_intro"));
			mBV.setBookStatusId(jo.getString("book_status_id"));
			mV.setCatalog(jo.getString("catalog"));
			mV.setBinding(jo.getString("bindName"));
			mV.setId(jo.getString("id"));
			Images img = new Images();
			img.setLarge(jo.getString("image_large"));
			img.setMedium(jo.getString("image_medium"));
			img.setSmall(jo.getString("image_small"));
			mV.setImages(img);
			mV.setIsbn10(jo.getString("isbn10"));
			mV.setIsbn13(jo.getString("isbn13"));
			mV.setFromDouban(jo.getBoolean("isfrom_douban"));
			mV.setOrigin_title(jo.getString("origin_title"));
			mV.setPages(String.valueOf(jo.getInteger("pages")));
			mV.setPrice(jo.getString("price"));
			mV.setPubdateDateType(jo.getDate("pubdate"));
			if(mV.getPubdateDateType()!=null)
			mV.setPubdate(new SimpleDateFormat("yyyy-MM-dd").format(mV.getPubdateDateType()));
			mV.setPublisher(jo.getString("publisher"));
			Rating rate = new Rating();
			rate.setAverage(jo.getDouble("rating_average"));
			rate.setMax(jo.getDouble("rating_max"));
			rate.setMin(jo.getDouble("rating_min"));
			if(jo.getInteger("rating_numRaters")!=null){
				rate.setNumRaters(jo.getInteger("rating_numRaters"));
			}
			mV.setRating(rate);
			mV.setSubtitle(jo.getString("subtitle"));
			mV.setSummary(jo.getString("summary"));
			mV.setBookClassify(jo.getString("book_classify"));
			mV.setTitle(jo.getString("title"));

			List<String> tran = new ArrayList<>();
			tran.add(jo.getString("translator"));
			mV.setTranslator(tran);
			mBV.setUserBookId(jo.getString("user_book_id"));
			mBV.setBook(mV);
			mBV.setShareType(jo.getInteger("share_type"));
			mBV.setShareMsg(jo.getString("share_msg"));
			mBV.setShareAreaId(jo.getInteger("share_area_id"));
			mBV.setRelatedUserId(jo.getString("related_user_id"));
			mBV.setUid(jo.getString("user_id"));
			mBV.setSwapId(jo.getString("swap_book_id"));
			mBvos.add(mBV);
		}
		return mBvos;
    }

    public static ArrayList<BorrowedInVO> getBorrowedBooks(List<JSONObject> items) {
		ArrayList<BorrowedInVO> mBvos = new ArrayList<>();
		for (JSONObject jo:items) {
			BorrowedInVO biv = new BorrowedInVO();

			String imageUrlL = jo.getString("image_large");
			String imageUrlS = jo.getString("image_small");
			if(StringUtil.isEmpty(imageUrlL)){
				if(!StringUtil.isEmpty(imageUrlS)){
					biv.setBookImage(imageUrlS);
				}
			}else{
				biv.setBookImage(imageUrlL);
			}
			String borrowFlowId = jo.getString("borrowFlowId");
			biv.setBorrowFlowId(borrowFlowId);
			String flowId = jo.getString("flow_id");
			biv.setFlowId(flowId);
			String uid = jo.getString("uid");
			biv.setUid(uid);
			String relUid = jo.getString("rel_uid");
			biv.setRelUid(relUid);
			String title = jo.getString("title");
			biv.setBookTitle(title);
			String bookId = jo.getString("id");
			biv.setBookId(bookId);
			String loginName = jo.getString("login_name");
			biv.setOwnerName(loginName);

			String author = jo.getString("a_author");
			biv.setBookAuthor(author);
			mBvos.add(biv);
		}
		return mBvos;
    }

    public static ArrayList<BorrowFlowVO> getBorrowBegs(JSONArray items){
		ArrayList<BorrowFlowVO> result = new ArrayList<>();
		for (int i = 0;i<items.size();i++){
			BorrowFlowVO bfVO = new BorrowFlowVO();
			JSONObject jo = (JSONObject) items.get(i);
			BorrowFlow bf = new BorrowFlow();
			bf.setMsg(jo.getString("msg"));
			bf.setBid(jo.getString("bid"));
			bf.setCreateDate(jo.getDate("CREATE_DATE"));
			bf.setFlowId(jo.getString("flow_id"));
			bf.setRelUid(jo.getInteger("rel_uid"));
			bf.setStatus(jo.getString("status"));
			bf.setId(jo.getString("id"));
			bf.setUid(jo.getInteger("uid"));

			bfVO.setRelUserLoginName(jo.getString("login_name"));
			bfVO.setBookName(jo.getString("title"));
			bfVO.setRelUid(jo.getString("relId"));
			bfVO.setBorrowFlow(bf);
			result.add(bfVO);
		}
		return result;
	}

	public static ArrayList<SwapBookVO> getSwapBooks(JSONArray items) {
		ArrayList<SwapBookVO> result = new ArrayList<>();
		for (int i = 0; i < items.size(); i++) {
			SwapBookVO sbVO = new SwapBookVO();
			JSONObject jo = (JSONObject) items.get(i);
			sbVO.setUserBookId(jo.getString("userBookId"));
			sbVO.setUserId(jo.getString("userId"));
			sbVO.setBookTitle(jo.getString("bookTitle"));
			sbVO.setBookImageLarge(jo.getString("bookImageLarge"));
			sbVO.setSwapId(jo.getString("swapId"));
			sbVO.setSwapBookTitle(jo.getString("swapBookTitle"));
			sbVO.setSwapBookAuthor(jo.getString("swapBookAuthor"));
			sbVO.setSwapMsg(jo.getString("swapMsg"));
			sbVO.setBookAuthor(jo.getString("bookAuthor"));
			result.add(sbVO);
		}
		return result;
	}

	public static ArrayList<SwapBookVO> getSwapBooksSearch(List<JSONObject> items) {
		ArrayList<SwapBookVO> result = new ArrayList<>();
		for (int i = 0; i < items.size(); i++) {
			SwapBookVO sbVO = new SwapBookVO();
			JSONObject jo = (JSONObject) items.get(i);
			sbVO.setUserBookId(jo.getString("id"));
			sbVO.setUserId(jo.getString("user_id"));
			sbVO.setBookTitle(jo.getString("title"));
			sbVO.setBookImageLarge(jo.getString("image_large"));
			sbVO.setSwapId(jo.getString("sb_id"));
			sbVO.setSwapBookTitle(jo.getString("swap_book_title"));
			sbVO.setSwapBookAuthor(jo.getString("swap_book_author"));
			sbVO.setSwapMsg(jo.getString("swap_msg"));
			sbVO.setBookAuthor(jo.getString("a_name"));
			result.add(sbVO);
		}
		return result;
	}

	public static ArrayList<EnumConst> getSwapSkillTyps(JSONArray items) {
		ArrayList<EnumConst> result = new ArrayList<>();
		for (int i = 0; i < items.size(); i++) {
			EnumConst sbVO = new EnumConst();
			JSONObject jo = (JSONObject) items.get(i);
			sbVO.setId(jo.getString("ID"));
			sbVO.setName(jo.getString("NAME"));
			sbVO.setCode(jo.getString("CODE"));
			sbVO.setNameSpace(jo.getString("NAMESPACE"));
			result.add(sbVO);
		}
		return result;
	}

	public static ArrayList<SwapSkillVo> getSwapSkills(List<JSONObject> items) {
		ArrayList<SwapSkillVo> result = new ArrayList<>();
		for (int i = 0; i < items.size(); i++) {
			SwapSkillVo sbVO = new SwapSkillVo();
			JSONObject jo = items.get(i);
			sbVO.setHeadIcon(jo.getString("headIcon"));
			sbVO.setSwapSkillId(jo.getString("swapSkillId"));
			sbVO.setSkillTitle(jo.getString("skillTitle"));
			sbVO.setUid(jo.getString("uid"));
			sbVO.setSkillHaveName(jo.getString("skillHaveName"));
			sbVO.setSkillWantName(jo.getString("skillWantName"));
			sbVO.setSkillType(jo.getString("haveType"));
			sbVO.setSwapSkillType(jo.getString("wantType"));
			result.add(sbVO);
		}
		return result;
	}


	public static String getDiplomaName(Integer diplomaId) {
		if(diplomaId!=null){
			switch (diplomaId){
				case 0:return null;
				case 1:return ("小学");
				case 2:return ("初中");
				case 3:return ("高中");
				case 4:return ("专科");
				case 5:return ("本科");
				case 6:return ("硕士研究生");
				case 7:return ("博士研究生");
			}
		}
		return null;
	}

	public static String getShareDes(int shareType){
		String result = "";
		switch (shareType){
			case 1:
				result=("赠送");
				break;
			case 2:
				result=("借阅");
				break;
			case 3:
				result=("赠送或借阅");
				break;
			default:
				result=("借阅");
		}
		return result;
	}
}
