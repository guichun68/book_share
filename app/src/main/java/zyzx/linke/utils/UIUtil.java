package zyzx.linke.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.amap.api.services.core.AMapException;

import zyzx.linke.base.BaseApplication;


public class UIUtil {
	static  String LINE;
	private static String TAG = "zyzx";
	static final String LINE_CHAR="=";
	static final String BOARD_CHAR="|";
	static final int LENGTH = 80;
	// 当测试阶段时true
	private static final boolean isShow = true;

	public static Context getContext() {
		// TODO 待查BaseApplication的用法
		return BaseApplication.getApplication();
	}

	public static Thread getMainThread() {
		return BaseApplication.getMainThread();
	}

	public static long getMainThreadId() {
		return BaseApplication.getMainThreadId();
	}

	/** dip转换px */
	public static int dip2px(int dip) {
		final float scale = getContext().getResources().getDisplayMetrics().density;
		return (int) (dip * scale + 0.5f);
	}

	/** pxz转换dip */
	public static int px2dip(int px) {
		final float scale = getContext().getResources().getDisplayMetrics().density;
		return (int) (px / scale + 0.5f);
	}

	/** 获取主线程的handler */
	public static Handler getHandler() {
		// 获得主线程的looper
		Looper mainLooper = BaseApplication.getMainThreadLooper();
		// 获取主线程的handler
		Handler handler = new Handler(mainLooper);
		return handler;
	}

	/** 延时在主线程执行runnable */
	public static boolean postDelayed(Runnable runnable, long delayMillis) {
		return getHandler().postDelayed(runnable, delayMillis);
	}

	/** 在主线程执行runnable */
	public static boolean post(Runnable runnable) {
		return getHandler().post(runnable);
	}

	/** 从主线程looper里面移除runnable */
	public static void removeCallbacks(Runnable runnable) {
		getHandler().removeCallbacks(runnable);
	}

	public static View inflate(int resId) {
		return LayoutInflater.from(getContext()).inflate(resId, null);
	}

	/** 获取资源 */
	public static Resources getResources() {
		return getContext().getResources();
	}

	/** 获取文字 */
	public static String getString(int resId) {
		return getResources().getString(resId);
	}

	/** 获取文字数组 */
	public static String[] getStringArray(int resId) {
		return getResources().getStringArray(resId);
	}

	/** 获取dimen */
	public static int getDimens(int resId) {
		return getResources().getDimensionPixelSize(resId);
	}

	/** 获取drawable */
	public static Drawable getDrawable(int resId) {
		return getResources().getDrawable(resId);
	}

	/** 获取颜色 */
	public static int getColor(int resId) {
		return getResources().getColor(resId);
	}

	/** 获取颜色选择器 */
	public static ColorStateList getColorStateList(int resId) {
		return getResources().getColorStateList(resId);
	}

	public static boolean isRunInMainThread() {
		return android.os.Process.myTid() == getMainThreadId();
	}

	public static void runInMainThread(Runnable runnable) {
		if (isRunInMainThread()) {
			runnable.run();
		} else {
			post(runnable);
		}
	}

	/** 对toast的简易封装。线程安全，可以在非UI线程调用。 */
	public static void showToastSafe(Context ctx, final int resId) {
		showToastSafe(ctx,getString(resId));
	}

	/** 对toast的简易封装。线程安全，可以在非UI线程调用。 */
	public static void showToastSafe(final Context ctx, final String str) {
		if (isRunInMainThread()) {
			Toast.makeText(ctx,str, Toast.LENGTH_SHORT).show();
		} else {
			post(new Runnable() {
				@Override
				public void run() {
//					showToast(str);
					Toast.makeText(ctx,str, Toast.LENGTH_SHORT).show();
				}
			});
		}
	}
	private static void showToast(String str) {
		if (GlobalParams.gCurrAct != null) {
			Toast.makeText(GlobalParams.gCurrAct, str, Toast.LENGTH_SHORT).show();
		}
	}
	/** 对toast的简易封装。线程安全，可以在非UI线程调用。 */
	public static void showToastSafe(final String str) {
		if (isRunInMainThread()) {
			showToast(str);
		} else {
			post(new Runnable() {
				@Override
				public void run() {
					showToast(str);
				}
			});
		}
	}
	/** 对toast的简易封装。线程安全，可以在非UI线程调用。 */
	public static void showToastSafe(final int resId) {
		showToastSafe(getString(resId));
	}

	/** 测试吐司 */
	public static void showTestToast(Context ctx, String str) {
		if (isShow) {
			// showToastSafe("test:" + str);
//			showToastSafe(str);
			Toast.makeText(ctx,str, Toast.LENGTH_SHORT).show();
		}
	}

	public static void showTestLog(String TAG, String str) {
		if (isShow) {
			Log.i(TAG, str);
		}
	}



	/**
	 * 给button设置drawableLeft图片
	 * 
	 * @param id
	 * @param btn
	 */
	public static void setDrawableLeft(int id, Button btn) {
		Drawable drawable = getResources().getDrawable(id);
		// / 这一步必须要做,否则不会显示.
		drawable.setBounds(0, 0, drawable.getMinimumWidth(),
				drawable.getMinimumHeight());
		btn.setCompoundDrawables(drawable, null, null, null);
	}
	/**
	 * 采用反射获取状态栏的高度
	 * @param context
	 * @return
	 */
	public static int getStatusBarHeight(Context context) {
		// 反射手机运行的类：android.R.dimen.status_bar_height.
		int statusHeight = -1;
		try {
			Class<?> clazz = Class.forName("com.android.internal.R$dimen");
			Object object = clazz.newInstance();
			String heightStr = clazz.getField("status_bar_height").get(object).toString();
			int height = Integer.parseInt(heightStr);
			//dp--->px
			statusHeight = context.getResources().getDimensionPixelSize(height);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return statusHeight;
	}

	public static void showMapError(Context context, int rCode){

		try {
			switch (rCode) {
				//服务错误码
				case 1001:
					throw new AMapException(AMapException.AMAP_SIGNATURE_ERROR);
				case 1002:
					throw new AMapException(AMapException.AMAP_INVALID_USER_KEY);
				case 1003:
					throw new AMapException(AMapException.AMAP_SERVICE_NOT_AVAILBALE);
				case 1004:
					throw new AMapException(AMapException.AMAP_DAILY_QUERY_OVER_LIMIT);
				case 1005:
					throw new AMapException(AMapException.AMAP_ACCESS_TOO_FREQUENT);
				case 1006:
					throw new AMapException(AMapException.AMAP_INVALID_USER_IP);
				case 1007:
					throw new AMapException(AMapException.AMAP_INVALID_USER_DOMAIN);
				case 1008:
					throw new AMapException(AMapException.AMAP_INVALID_USER_SCODE);
				case 1009:
					throw new AMapException(AMapException.AMAP_USERKEY_PLAT_NOMATCH);
				case 1010:
					throw new AMapException(AMapException.AMAP_IP_QUERY_OVER_LIMIT);
				case 1011:
					throw new AMapException(AMapException.AMAP_NOT_SUPPORT_HTTPS);
				case 1012:
					throw new AMapException(AMapException.AMAP_INSUFFICIENT_PRIVILEGES);
				case 1013:
					throw new AMapException(AMapException.AMAP_USER_KEY_RECYCLED);
				case 1100:
					throw new AMapException(AMapException.AMAP_ENGINE_RESPONSE_ERROR);
				case 1101:
					throw new AMapException(AMapException.AMAP_ENGINE_RESPONSE_DATA_ERROR);
				case 1102:
					throw new AMapException(AMapException.AMAP_ENGINE_CONNECT_TIMEOUT);
				case 1103:
					throw new AMapException(AMapException.AMAP_ENGINE_RETURN_TIMEOUT);
				case 1200:
					throw new AMapException(AMapException.AMAP_SERVICE_INVALID_PARAMS);
				case 1201:
					throw new AMapException(AMapException.AMAP_SERVICE_MISSING_REQUIRED_PARAMS);
				case 1202:
					throw new AMapException(AMapException.AMAP_SERVICE_ILLEGAL_REQUEST);
				case 1203:
					throw new AMapException(AMapException.AMAP_SERVICE_UNKNOWN_ERROR);
					//sdk返回错误
				case 1800:
					throw new AMapException(AMapException.AMAP_CLIENT_ERRORCODE_MISSSING);
				case 1801:
					throw new AMapException(AMapException.AMAP_CLIENT_ERROR_PROTOCOL);
				case 1802:
					throw new AMapException(AMapException.AMAP_CLIENT_SOCKET_TIMEOUT_EXCEPTION);
				case 1803:
					throw new AMapException(AMapException.AMAP_CLIENT_URL_EXCEPTION);
				case 1804:
					throw new AMapException(AMapException.AMAP_CLIENT_UNKNOWHOST_EXCEPTION);
				case 1806:
					throw new AMapException(AMapException.AMAP_CLIENT_NETWORK_EXCEPTION);
				case 1900:
					throw new AMapException(AMapException.AMAP_CLIENT_UNKNOWN_ERROR);
				case 1901:
					throw new AMapException(AMapException.AMAP_CLIENT_INVALID_PARAMETER);
				case 1902:
					throw new AMapException(AMapException.AMAP_CLIENT_IO_EXCEPTION);
				case 1903:
					throw new AMapException(AMapException.AMAP_CLIENT_NULLPOINT_EXCEPTION);
					//云图和附近错误码
				case 2000:
					throw new AMapException(AMapException.AMAP_SERVICE_TABLEID_NOT_EXIST);
				case 2001:
					throw new AMapException(AMapException.AMAP_ID_NOT_EXIST);
				case 2002:
					throw new AMapException(AMapException.AMAP_SERVICE_MAINTENANCE);
				case 2003:
					throw new AMapException(AMapException.AMAP_ENGINE_TABLEID_NOT_EXIST);
				case 2100:
					throw new AMapException(AMapException.AMAP_NEARBY_INVALID_USERID);
				case 2101:
					throw new AMapException(AMapException.AMAP_NEARBY_KEY_NOT_BIND);
				case 2200:
					throw new AMapException(AMapException.AMAP_CLIENT_UPLOADAUTO_STARTED_ERROR);
				case 2201:
					throw new AMapException(AMapException.AMAP_CLIENT_USERID_ILLEGAL);
				case 2202:
					throw new AMapException(AMapException.AMAP_CLIENT_NEARBY_NULL_RESULT);
				case 2203:
					throw new AMapException(AMapException.AMAP_CLIENT_UPLOAD_TOO_FREQUENT);
				case 2204:
					throw new AMapException(AMapException.AMAP_CLIENT_UPLOAD_LOCATION_ERROR);
					//路径规划
				case 3000:
					throw new AMapException(AMapException.AMAP_ROUTE_OUT_OF_SERVICE);
				case 3001:
					throw new AMapException(AMapException.AMAP_ROUTE_NO_ROADS_NEARBY);
				case 3002:
					throw new AMapException(AMapException.AMAP_ROUTE_FAIL);
				case 3003:
					throw new AMapException(AMapException.AMAP_OVER_DIRECTION_RANGE);
					//短传分享
				case 4000:
					throw new AMapException(AMapException.AMAP_SHARE_LICENSE_IS_EXPIRED);
				case 4001:
					throw new AMapException(AMapException.AMAP_SHARE_FAILURE);
				default:
					Toast.makeText(context,"查询失败："+rCode , Toast.LENGTH_LONG).show();
					logError("查询失败", rCode);
					break;
			}
		} catch (Exception e) {
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
			logError(e.getMessage(), rCode);
		}
	}

	private static void logError(String info, int errorCode) {
		print(LINE);//start
		print("                                   错误信息                                     ");
		print(LINE);//title
		print(info);
		print("错误码: " + errorCode);
		print("                                                                               ");
		print("如果需要更多信息，请根据错误码到以下地址进行查询");
		print("  http://lbs.amap.com/api/android-sdk/guide/map-tools/error-code/");
		print("如若仍无法解决问题，请将全部log信息提交到工单系统，多谢合作");
		print(LINE);//end
	}

	private static void print(String s) {
		showTestLog(TAG,s);
	}

	static{
		StringBuilder sb = new StringBuilder();
		for(int i = 0;i<LENGTH;i++){
			sb .append(LINE_CHAR);
		}
		LINE = sb.toString();
	}

}
