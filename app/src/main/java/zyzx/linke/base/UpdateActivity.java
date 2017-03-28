package zyzx.linke.base;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.view.Window;

import java.io.File;

import zyzx.linke.R;
import zyzx.linke.utils.DownloadUtil;
import zyzx.linke.utils.StringUtil;
import zyzx.linke.utils.UIUtil;

public class UpdateActivity extends Activity {
	
	NotificationCompat.Builder mBuilder;
	/** Notification管理 */
	public NotificationManager mNotificationManager;
	private String desc,url;
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			desc = getIntent().getStringExtra("desc");
			url = getIntent().getStringExtra("url");
			mBuilder = new NotificationCompat.Builder(this);
//			url = "http://m.apk.67mo.com/apk/999129_21769077_1443483983292.apk";
			initNotify();
			showConfirmUpdateDialog();
		}
		/** 初始化通知栏 */
		private void initNotify() {
//			mBuilder = new NotificationCompat.Builder(this);
			mBuilder.setWhen(System.currentTimeMillis())// 通知产生的时间，会在通知信息里显示
					.setContentIntent(getDefalutIntent(0))
					// .setNumber(number)//显示数量
					.setPriority(0)// 设置该通知优先级
					// .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
					.setOngoing(false)// ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
				 	.setDefaults(Notification.DEFAULT_LIGHTS)// 向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合：
					// Notification.DEFAULT_ALL Notification.DEFAULT_SOUND 添加声音 //
					// requires VIBRATE permission
					.setSmallIcon(R.mipmap.ic_launcher);
			
			mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		}
		/**
		 * @获取默认的pendingIntent,为了防止2.3及以下版本报错
		 * @flags属性:  
		 * 在顶部常驻:Notification.FLAG_ONGOING_EVENT  
		 * 点击去除： Notification.FLAG_AUTO_CANCEL 
		 */
		public PendingIntent getDefalutIntent(int flags){
			PendingIntent pendingIntent= PendingIntent.getActivity(this, 1, new Intent(), flags);
			return pendingIntent;
		}
		/**
		 * 显示，提示用户升级对话框
		 */
		protected void showConfirmUpdateDialog() {
			//对话框，他是activity的一部分。

			android.support.v7.app.AlertDialog.Builder adb = new android.support.v7.app.AlertDialog.Builder(this);
			adb.setTitle("升级提醒");
			adb.setMessage(desc);
//			adb.setCancelable(false);
			adb.setOnCancelListener(new DialogInterface.OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					UpdateActivity.this.finish();
				}
			});
			
			adb.setPositiveButton("立刻升级", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							downloadFile();
						}
					}).start();
					UpdateActivity.this.finish();
				}
			});

			adb.setNegativeButton("暂不升级", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//跳转至主页面
					UpdateActivity.this.finish();
				}
			});
			
			adb.show();
		}
		/**
		 * 下载新版app
		 * 
		 */
		protected void downloadFile() {
			final String fileName = StringUtil.getExtraName(url);
			DownloadUtil.get().download(url, "lk", new DownloadUtil.OnDownloadListener() {
				@Override
				public void onDownloadSuccess() {
					UIUtil.showToastSafe(UpdateActivity.this, "下载完成");

					mBuilder.setContentText("下载完成");
					mNotificationManager.notify(0x00000fff, mBuilder.build());
					Intent intent = new Intent();
					intent.setAction("android.intent.action.VIEW");
					intent.addCategory("android.intent.category.DEFAULT");
					File downloadFile = new File(Environment.getExternalStorageDirectory(), "lk/"+fileName);
					intent.setDataAndType(Uri.fromFile(downloadFile), "application/vnd.android.package-archive");
					startActivityForResult(intent, 0);
				}
				@Override
				public void onDownloading(long total,int progress) {
					mBuilder.setProgress((int) total, progress, false);
					mNotificationManager.notify(0x00000fff, mBuilder.build());
				}
				@Override
				public void onDownloadFailed() {
					String message = "下载出错了：";
					UIUtil.showToastSafe(message);
					UpdateActivity.this.finish();
				}
			});
		}
}
