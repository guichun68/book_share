package zyzx.linke.base;

import android.app.Dialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import zyzx.linke.R;
import zyzx.linke.activity.BaseParentActivity;
import zyzx.linke.presentation.IBookPresenter;
import zyzx.linke.presentation.IUserPresenter;
import zyzx.linke.utils.CustomProgressDialog;


public abstract class BaseActivity extends BaseParentActivity implements View.OnClickListener{

    protected TextView mTitleText;
    protected ImageView mBackBtn;
    protected ImageView mRightBtn;

    @Override
    protected void initTitleBar() {
//        Log.e("initTitleBar:",this.toString());
        mTitleText = (TextView) findViewById(R.id.title_text);
        mBackBtn = (ImageView) findViewById(R.id.back_img);
        mRightBtn = (ImageView) findViewById(R.id.right_img);
        try{
            mBackBtn.setOnClickListener(this);
        }catch (NullPointerException e){
            e.printStackTrace();
            throw new RuntimeException("check if you forget to include title layout in your layout xml file.");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_img:
                onBackPressed();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private IBookPresenter mBookPresenter;
    private IUserPresenter mUserPresenter;

    protected IBookPresenter getBookPresenter(){
        if(mBookPresenter==null){
            mBookPresenter=(GlobalParams.getBookPresenter());
        }
        return mBookPresenter;
    }

    protected IUserPresenter getUserPresenter(){
        if(mUserPresenter==null){
            mUserPresenter=GlobalParams.getUserPresenter();
        }
        return mUserPresenter;
    }

    private Dialog mProgressDialog;
    private Dialog mProgressDialogWithTip;

    /**
     * 显示默认的不确定进度条
     */
    protected void showDefProgress(){
        if(mProgressDialog == null){
            mProgressDialog = CustomProgressDialog.getNewProgressBar(mContext,"数据加载中……");
        }
        mProgressDialog.show();
    }
    /**
     * 显示默认的不确定进度条
     */
    protected void showProgress(String tip){
        if(mProgressDialogWithTip == null){
//            mProgressDialogWithTip = CustomProgressDialog.getNewProgressBar(mContext,tip);
            mProgressDialogWithTip = CustomProgressDialog.getProgressUtil().getRequestDialog(this,tip);
        }
        mProgressDialogWithTip.show();
    }

    protected void dismissProgress(){
        CustomProgressDialog.dismissDialog(mProgressDialog);
        CustomProgressDialog.dismissDialog(mProgressDialogWithTip);
        CustomProgressDialog.dismissDialog(mToastDialog);
    }

    private Dialog mToastDialog;
    protected void showToastDialog(String msg){
        mToastDialog = CustomProgressDialog.getToastDialog(this,msg);
        mToastDialog.setCancelable(true);
        mToastDialog.show();
    }

}

