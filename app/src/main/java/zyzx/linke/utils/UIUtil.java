package zyzx.linke.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import zyzx.linke.base.BaseApplication;
import zyzx.linke.base.GlobalParams;


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
	public static void showTestLog(String str) {
		showTestLog(TAG,str);
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

	public static void print(String s) {
		showTestLog(TAG,s);
	}

	static{
		StringBuilder sb = new StringBuilder();
		for(int i = 0;i<LENGTH;i++){
			sb .append(LINE_CHAR);
		}
		LINE = sb.toString();
	}
	public static String sHA1(Context context) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), PackageManager.GET_SIGNATURES);
			byte[] cert = info.signatures[0].toByteArray();
			MessageDigest md = MessageDigest.getInstance("SHA1");
			byte[] publicKey = md.digest(cert);
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < publicKey.length; i++) {
				String appendString = Integer.toHexString(0xFF & publicKey[i])
						.toUpperCase(Locale.US);
				if (appendString.length() == 1)
					hexString.append("0");
				hexString.append(appendString);
				hexString.append(":");
			}
			String result = hexString.toString();
			return result.substring(0, result.length()-1);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
}
