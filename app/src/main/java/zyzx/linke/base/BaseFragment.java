package zyzx.linke.base;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import zyzx.linke.R;
import zyzx.linke.presentation.IBookPresenter;
import zyzx.linke.presentation.IUserPresenter;
import zyzx.linke.utils.CustomProgressDialog;

/**
 * Created by austin on 2017/3/25.
 * Desc:
 */

public abstract class BaseFragment extends Fragment{
    private Dialog mProgressDialog,mProDialogWithTip,mToastDialog;

    protected Context mContext;
    protected TextView mTitleText,mTvLeftTip;
    protected ImageView mBackBtn;
    protected ImageView mRightBtn;
    protected View mRootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(mContext==null){
            mContext = getContext();
        }
        if(mRootView==null){
            mRootView=getView(inflater,container);
            initTitleBar();
            initView();
        }
        //缓存的rootView需要判断是否已经被加过parent， 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }
        return mRootView;
    }

    protected abstract View getView(LayoutInflater inflater, ViewGroup container);

    public abstract void initView() ;

    protected void initTitleBar() {
//        Log.e("initTitleBar:",this.toString());
        mTitleText = (TextView) mRootView.findViewById(R.id.title_text);
        mTvLeftTip = (TextView) mRootView.findViewById(R.id.tvLeftTip);
        mBackBtn = (ImageView)mRootView. findViewById(R.id.back_img);
        mRightBtn = (ImageView)mRootView. findViewById(R.id.right_img);
       /* try{
//            mBackBtn.setOnClickListener(this);
        }catch (NullPointerException e){
            e.printStackTrace();
            throw new RuntimeException("check if you forget to include title layout in your layout xml file.");
        }*/
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
        if(mBookPresenter==null){
            mUserPresenter=(GlobalParams.getUserPresenter());
        }
        return mUserPresenter;
    }

    /**
     * 显示默认的不确定进度条
     */
    protected void showDefProgress(){
        if(mProgressDialog == null){
            mProgressDialog = CustomProgressDialog.getNewProgressBar(getContext(),"数据加载中……");
        }
        mProgressDialog.show();
    }
    /**
     * 显示默认的不确定进度条
     */
    protected void showProgress(String tip){
        if(mProDialogWithTip == null){
//            mProDialogWithTip = CustomProgressDialog.getNewProgressBar(mContext,tip);
            mProDialogWithTip = CustomProgressDialog.getProgressUtil().getRequestDialog(getContext(),null);
        }
        mProDialogWithTip.show();
    }

    protected void dismissProgress(){
        CustomProgressDialog.dismissDialog(mProgressDialog);
        CustomProgressDialog.dismissDialog(mProDialogWithTip);
        CustomProgressDialog.dismissDialog(mToastDialog);
    }

    protected void showToastDialog(String msg){
        mToastDialog = CustomProgressDialog.getToastDialog(getContext(),msg);
        mToastDialog.setCancelable(true);
        mToastDialog.show();
    }

    public void gotoActivity(Class<?> clz) {
        gotoActivity(clz, null);
    }

    public  void gotoActivity(Class<?> clz,Bundle ex) {
        Intent intent=new Intent(getContext(), clz);
        if(ex!=null)
            intent.putExtras(ex);
        startActivity(intent);
    }


}