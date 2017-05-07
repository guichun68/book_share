package zyzx.linke.base;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.IOException;

import zyzx.linke.R;
import zyzx.linke.model.bean.UIProgressResponseListener;
import zyzx.linke.utils.AppUtil;
import zyzx.linke.utils.DownloadUtil;
import zyzx.linke.utils.FileUtil;
import zyzx.linke.utils.ProgressHelper;
import zyzx.linke.utils.StringUtil;
import zyzx.linke.utils.UIUtil;

public class ForceUpdateActivity extends Activity {
	NotificationCompat.Builder mBuilder;
	/** Notification管理 */
	public NotificationManager mNotificationManager;
	private String desc,url;
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
//			setBehindContentView(R.layout.frame_menu);
			desc = getIntent().getStringExtra("desc");
			url = getIntent().getStringExtra("url");
			mBuilder = new NotificationCompat.Builder(this);
			initNotify();
			showConfirmUpdateDialog();
		}
		/** 初始化通知栏 */
		private void initNotify() {
			mBuilder = new NotificationCompat.Builder(this);
			mBuilder.setWhen(System.currentTimeMillis())// 通知产生的时间，会在通知信息里显示
					.setContentIntent(getDefalutIntent(0))
					// .setNumber(number)//显示数量
					.setPriority(0)// 设置该通知优先级
					// .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
					.setOngoing(false)// ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
				 	//.setDefaults(Notification.DEFAULT_VIBRATE)// 向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合：
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
		
		private ProgressBar mProgressBar1;
		private Button mBtn_ok,mBtn_cancel;
		private TextView tv_desc;
		LinearLayout ll_btn;
		/**
		 * 显示，提示用户升级对话框
		 */
		protected void showConfirmUpdateDialog() {
//			AlertDialog.Builder adb = new AlertDialog.Builder(BaseActivity.getForegroundActivity());
			AlertDialog.Builder adb = new AlertDialog.Builder(this);
			adb.setCancelable(false);
			View view = View.inflate(this, R.layout.dialog_forceupdate, null);
			ll_btn = (LinearLayout) view.findViewById(R.id.ll_btn);
			mBtn_ok = (Button) view.findViewById(R.id.btn_ok);
			mBtn_cancel = (Button) view.findViewById(R.id.btn_cancel);
			mProgressBar1 = (ProgressBar) view.findViewById(R.id.progressBar1);
			tv_desc = (TextView) view.findViewById(R.id.tv_desc);
			
			final AlertDialog dialog = adb.create();
			dialog.setView(view, 0,0,0,0);
			tv_desc.setText(desc);
			mBtn_ok.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					ll_btn.setVisibility(View.GONE);
					mProgressBar1.setVisibility(View.VISIBLE);

					new Thread(new Runnable() {
						@Override
						public void run() {
							downloadFile();
						}
					}).start();
				}
			});
			mBtn_cancel.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					dialog.cancel();
					((BaseApplication)BaseApplication.getApplication()).exitApp(ForceUpdateActivity.this);
				}
			});
			dialog.show();
		
			int height = AppUtil.getScreenHeight(ForceUpdateActivity.this);
			int width = AppUtil.getScreenWidth(ForceUpdateActivity.this);
			WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
			params.width = width-50;
			params.height = height/2 ;
			dialog.getWindow().setAttributes(params);
		}

		/**
		 * 下载新版app
		 */
		protected void downloadFile() {
			GlobalParams.downloadFileName = StringUtil.getExtraName(url);
			DownloadUtil.get().download(url, GlobalParams.BaseDir, null,new DownloadUtil.OnDownloadListener() {
				@Override
				public void onDownloadSuccess() {


					mBuilder.setContentText("下载完成");
					mNotificationManager.notify(0x00000fff, mBuilder.build());
					Intent intent = new Intent();
					intent.setAction("android.intent.action.VIEW");
					intent.addCategory("android.intent.category.DEFAULT");
					File downloadFile = new File(Environment.getExternalStorageDirectory(), GlobalParams.BaseDir+"/"+GlobalParams.downloadFileName);
					Toast.makeText(ForceUpdateActivity.this,"已下载到"+downloadFile.getAbsolutePath(),Toast.LENGTH_LONG).show();
					intent.setDataAndType(Uri.fromFile(downloadFile), "application/vnd.android.package-archive");
					startActivityForResult(intent, 0);
				}
				@Override
				public void onDownloading(long total,int progress) {
					mBuilder.setProgress(100, progress, false);
					mNotificationManager.notify(0x00000fff, mBuilder.build());
				}
				@Override
				public void onDownloadFailed() {
					String message = "下载出错了：";
					UIUtil.showToastSafe(message);
					ForceUpdateActivity.this.finish();
				}
			});
		}

	/**
	 * @param saveDir
	 * @return
	 * @throws IOException
	 * 判断下载目录是否存在
	 */
	private String isExistDir(String saveDir) throws IOException {
		// 下载位置
		File downloadFile = new File(Environment.getExternalStorageDirectory(), saveDir);
		if (!downloadFile.mkdirs()) {
			downloadFile.createNewFile();
		}
		String savePath = downloadFile.getAbsolutePath();
		return savePath;
	}
}
