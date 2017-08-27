package zyzx.linke.base;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.qiangxi.checkupdatelibrary.callback.DownloadCallback;
import com.qiangxi.checkupdatelibrary.http.HttpRequest;
import com.qiangxi.checkupdatelibrary.utils.ApplicationUtil;
import com.qiangxi.checkupdatelibrary.utils.NetWorkUtil;

import java.io.File;
import java.io.IOException;

import zyzx.linke.R;
import zyzx.linke.activity.AppManager;
import zyzx.linke.utils.StringUtil;
import zyzx.linke.utils.UIUtil;

public class ForceUpdateActivity extends BaseActivity {
    private ContentLoadingProgressBar pb;
    private long timeRange = 0;
    private String mDesc,mUrl,mFileName;
    private Button dialogBtn;
    private String filePath;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_force_update;
    }

    @Override
    protected void initView(Bundle saveInstanceState) {
        mDesc = getIntent().getStringExtra("desc");
        mUrl = getIntent().getStringExtra("url");
        mFileName = getIntent().getStringExtra("fileName");

        TextView dialog_txt = (TextView) findViewById(R.id.dialog_txt);
        dialogBtn = (Button) findViewById(R.id.dialog_btn);
        pb = (ContentLoadingProgressBar) findViewById(R.id.pb);

        dialogBtn.setText("下载更新");

        dialog_txt.setText(mDesc);

        dialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //防抖动,两次点击间隔小于500ms都return;
                if (System.currentTimeMillis() - timeRange < 500) {
                    return;
                }
                timeRange = System.currentTimeMillis();
                if (!NetWorkUtil.hasNetConnection(ForceUpdateActivity.this)) {
                    Toast.makeText(ForceUpdateActivity.this, "当前无网络连接", Toast.LENGTH_SHORT).show();
                    return;
                }
                if ("点击安装".equals(dialogBtn.getText().toString().trim())) {

                    try {
                        File file = new File(isExistDir(GlobalParams.BaseDir), mFileName);
                        if (file.exists()) {
                            ApplicationUtil.installApk(ForceUpdateActivity.this, file);
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

    @Override
    protected void onDestroy() {
        UIUtil.showToastSafe("未安装更新，即将退出！");
        super.onDestroy();
        AppManager.getAppManager().finishAllActivity();
        BaseApplication.getInstance().exitApp(this);
    }

    @Override
    public void onBackPressed() {
        UIUtil.showToastSafe("未能更新，即将退出！");
        super.onBackPressed();
        AppManager.getAppManager().finishAllActivity();
        BaseApplication.getInstance().exitApp(this);
    }

    private void download() {
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
                ApplicationUtil.installApk(ForceUpdateActivity.this, file);
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
}
