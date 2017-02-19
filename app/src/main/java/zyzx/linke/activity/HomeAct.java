package zyzx.linke.activity;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import zyzx.linke.R;

/**
 * Created by austin on 2017/2/17.
 * Desc: 登录后的主页面
 */

public class HomeAct extends BaseActivity {

    private ImageView ivScan;
    @Override
    protected int getLayoutId() {
        return R.layout.act_home;
    }

    @Override
    protected void initView() {
        ivScan = (ImageView) findViewById(R.id.iv_scan);
        ivScan.setOnClickListener(this);
        mTitleText.setText("自由自行");
        mBackBtn.setImageResource(R.mipmap.me2);
        mBackBtn.setClickable(true);
        mBackBtn.setOnClickListener(this);
        mRightBtn.setClickable(true);
        mRightBtn.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back_img:
                Toast.makeText(HomeAct.this,"点击了我",Toast.LENGTH_SHORT).show();
                break;
            case R.id.iv_scan:
                Toast.makeText(HomeAct.this,"点击了扫描",Toast.LENGTH_SHORT).show();
                break;
            case R.id.right_img:
                Toast.makeText(HomeAct.this,"点击了搜索",Toast.LENGTH_SHORT).show();
                gotoActivity(GeoFence_Activity.class,false);
                break;
        }
    }

    //确定要退出App?
    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount()>=1){
            super.onBackPressed();
            return;
        }
        showExitDialog();
//        super.onBackPressed();
    }
    private void showExitDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("确定退出?");
        dialog.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                HomeAct.this.finish();
            }
        });
        dialog.setPositiveButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.exit(0);
    }
}
