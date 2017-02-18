package zyzx.linke.activity;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;

import zyzx.linke.R;

/**
 * Created by austin on 2017/2/17.
 * Desc: 登录后的主页面
 */

public class HomeAct extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.act_home;
    }

    @Override
    protected void initView() {
        mBackBtn.setVisibility(View.INVISIBLE);
        mTitleText.setText("自由自行");
    }

    @Override
    protected void initData() {

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
