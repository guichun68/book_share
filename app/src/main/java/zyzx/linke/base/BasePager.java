package zyzx.linke.base;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import zyzx.linke.presentation.IBookPresenter;
import zyzx.linke.presentation.IUserPresenter;
import zyzx.linke.utils.CustomProgressDialog;

/**
 * Created by austin on 2017/8/12.
 * Desc: 交换市场选项卡页 抽取类
 */

public abstract class BasePager {
    protected Context context;
    protected View rootView;
    private IUserPresenter mUserPresenter;
    private IBookPresenter mBookPresenter;
    private Dialog mProgressDialog;

    public BasePager(Context context, int layoutResId){
        this.context = context;
        rootView = View.inflate(this.context,layoutResId,null);
        initView();
    }

    public View getRootView(){
        return rootView;
    }

    public abstract void initView();

    protected IUserPresenter getUserPresenter(){
        if(mUserPresenter==null){
            mUserPresenter=GlobalParams.getUserPresenter();
        }
        return mUserPresenter;
    }
    protected IBookPresenter getBookPresenter(){
        if(mBookPresenter==null){
            mBookPresenter=GlobalParams.getBookPresenter();
        }
        return mBookPresenter;
    }

    /**
     * 显示默认的不确定进度条
     */
    protected void showDefProgress(){
        if(mProgressDialog == null){
            mProgressDialog = CustomProgressDialog.getNewProgressBar(context,"数据加载中……");
        }
        mProgressDialog.show();
    }
    protected void dismissProgress(){
        CustomProgressDialog.dismissDialog(mProgressDialog);
    }
}
