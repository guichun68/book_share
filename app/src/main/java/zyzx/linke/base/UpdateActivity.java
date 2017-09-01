package zyzx.linke.base;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.io.IOException;

import zyzx.linke.R;
import zyzx.linke.activity.AppManager;
import zyzx.linke.checkupdate.bean.CheckUpdateInfo;
import zyzx.linke.checkupdate.callback.DownloadCallback;
import zyzx.linke.checkupdate.http.HttpRequest;
import zyzx.linke.checkupdate.utils.ApplicationUtil;
import zyzx.linke.checkupdate.utils.NetWorkUtil;
import zyzx.linke.utils.CustomProgressDialog;
import zyzx.linke.utils.StringUtil;
import zyzx.linke.utils.UIUtil;

public class UpdateActivity extends BaseActivity {
	
	NotificationCompat.Builder mBuilder;
	private String mDesc,mUrl,mFileName;
	private CheckUpdateInfo mCheckUpdateInfo;
	private long timeRange = 0;
	private Button dialogBtn;
	private ContentLoadingProgressBar pb;
	private String filePath;
	private TextView tvTitle;


	@Override
	protected int getLayoutId() {
		return R.layout.dialog_force_update;
	}
	
	@Override
	protected void initView(Bundle saveInstanceState) {
		mDesc = getIntent().getStringExtra("desc");
		mUrl = getIntent().getStringExtra("url");
		mFileName = getIntent().getStringExtra("fileName");

		if(StringUtil.isEmpty(mFileName))mFileName= StringUtil.getExtraName(mUrl);
		TextView dialog_txt = (TextView) findViewById(R.id.dialog_txt);
		dialogBtn = (Button) findViewById(R.id.dialog_btn);
		tvTitle = (TextView) findViewById(R.id.tv_title);
		pb = (ContentLoadingProgressBar) findViewById(R.id.pb);
		tvTitle.setText("有更新");
		dialogBtn.setText("下载更新");

		dialog_txt.setText(mDesc.replace("#","\n"));
//		dialog_txt.setText(mDesc);

		dialogBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//防抖动,两次点击间隔小于500ms都return;
				if (System.currentTimeMillis() - timeRange < 500) {
					return;
				}
				timeRange = System.currentTimeMillis();
				if (!NetWorkUtil.hasNetConnection(UpdateActivity.this)) {
					Toast.makeText(UpdateActivity.this, "当前无网络连接", Toast.LENGTH_SHORT).show();
					return;
				}
				if ("点击安装".equals(dialogBtn.getText().toString().trim())) {

					try {
						File file = new File(isExistDir(GlobalParams.BaseDir), mFileName);
						if (file.exists()) {
							ApplicationUtil.installApk(UpdateActivity.this, file);
						} else {
							download();
						}

					} catch (IOException e) {
						e.printStackTrace();
					}

					return;
				}
				download();
			}
		});
	}

	@Override
	protected void initData() {

	}


	/**
	 * @param saveDir
	 * @return
	 * @throws IOException 判断下载目录是否存在
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

	private void download() {
		HttpRequest.setShouldCancel(false);
		pb.setVisibility(View.VISIBLE);
		if(StringUtil.isEmpty(filePath)){
			try {
				filePath = isExistDir(GlobalParams.BaseDir);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		HttpRequest.download(mUrl, filePath, mFileName, new DownloadCallback() {
			@Override
			public void onDownloadSuccess(File file) {
				dialogBtn.setEnabled(true);
				dialogBtn.setText("点击安装");
				ApplicationUtil.installApk(UpdateActivity.this, file);
			}

			@Override
			public void onProgress(long currentProgress, long totalProgress) {
				dialogBtn.setEnabled(false);
				dialogBtn.setText("正在下载");
				pb.setMax((int) (totalProgress));
				pb.setProgress((int) (currentProgress));
			}

			@Override
			public void onDownloadFailure(String failureMessage) {
				dialogBtn.setEnabled(true);
				dialogBtn.setText("重新下载");
			}
		});
	}

	Dialog exitDialg;
	@Override
	public void onBackPressed() {
		if(exitDialg != null && exitDialg.isShowing()){
			exitDialg.dismiss();
			return;
		}
		if(exitDialg==null){
			exitDialg = CustomProgressDialog.getPromptDialog2Btn(this, "此次为重要更新，需要更新后才能继续使用，确定退出么？","确定退出","取消",new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					UIUtil.showToastSafe("未安装更新，即将退出！");
					HttpRequest.setShouldCancel(true);
					AppManager.getAppManager().finishAllActivity();
					BaseApplication.getInstance().exitApp(UpdateActivity.this);
				}
			},null);
		}
		exitDialg.show();
	}


	@Override
	protected void onDestroy() {
		HttpRequest.setShouldCancel(true);
		UIUtil.showToastSafe("已取消下载并退出");
		AppManager.getAppManager().finishAllActivity();
		super.onDestroy();
	}
}
