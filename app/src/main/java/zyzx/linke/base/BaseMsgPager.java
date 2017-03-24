package zyzx.linke.base;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import zyzx.linke.utils.CustomProgressDialog;

/**
 * Created by austin on 2017/3/22.
 * Desc: 消息页面基类
 */

public abstract class BaseMsgPager {
    protected Context mContext;
    public BaseMsgPager(Context context){
        this.mContext = context;
        initView();
        initData();
    }
    private Dialog mProgressDialogWithTip;
    /**
     * 显示默认的不确定进度条
     */
    protected void showProgress(String tip){
        if(mProgressDialogWithTip == null){
            mProgressDialogWithTip = CustomProgressDialog.getProgressUtil().getRequestDialog(mContext,tip);
        }
        mProgressDialogWithTip.show();
    }
    protected void dismissProgress(){
        CustomProgressDialog.dismissDialog(mProgressDialogWithTip);
    }

    protected abstract void initView();

    public abstract void initData();
    public abstract View getRootView();
}
