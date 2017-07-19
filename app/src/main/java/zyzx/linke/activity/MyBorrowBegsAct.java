package zyzx.linke.activity;

import android.os.Bundle;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

import zyzx.linke.R;
import zyzx.linke.adapter.MyBookBorrowBegAdp;
import zyzx.linke.base.BaseActivity;
import zyzx.linke.base.GlobalParams;
import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.BorrowFlow;
import zyzx.linke.model.bean.BorrowFlowVO;
import zyzx.linke.model.bean.ResponseJson;
import zyzx.linke.utils.AppUtil;
import zyzx.linke.utils.StringUtil;
import zyzx.linke.utils.UIUtil;

/**
 * Created by austin on 2017/7/18.
 */

public class MyBorrowBegsAct extends BaseActivity implements PullToRefreshBase.OnRefreshListener {
    private PullToRefreshListView mPullRefreshListView;
    private int mPageNum = 1;
    private ArrayList<BorrowFlowVO> mBorrows = new ArrayList<>();
    private boolean isLoadingMore;//是否是加载更多的动作
    private MyBookBorrowBegAdp mBorrowAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.act_my_borrow_begs;
    }

    @Override
    protected void initView(Bundle saveInstanceState) {
        mTitleText.setText("求借求赠送记录");
        mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
        mPullRefreshListView.setOnRefreshListener(this);
        mPullRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);//上拉加载更多
        ListView actualListView = mPullRefreshListView.getRefreshableView();
        // Need to use the Actual ListView when registering for Context Menu
        registerForContextMenu(actualListView);
        mBorrowAdapter = new MyBookBorrowBegAdp(mBorrows);
        mPullRefreshListView.setAdapter(mBorrowAdapter);
    }

    @Override
    protected void initData() {
        showDefProgress();
        getBorrowBegs(1);
    }

    /**
     * 获取我的所有借书请求
     */
    private void getBorrowBegs(int pageNo) {
        getUserPresenter().getAllBorrowBegs(GlobalParams.getLastLoginUser().getUserid(),pageNo,new CallBack() {
            @Override
            public void onSuccess(final Object obj, int... code) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissProgress();
                        mPullRefreshListView.onRefreshComplete();
                        mPullRefreshListView.clearAnimation();
                        String json = (String)obj;
                        if(StringUtil.isEmpty(json)){
                            UIUtil.showToastSafe("获取失败");
                            return;
                        }
                        ResponseJson rj = new ResponseJson(json);
                        if(rj.errorCode==null || rj.errorCode==0){
                            UIUtil.showToastSafe("获取失败");
                            return;
                        }
                        if(rj.errorCode == 1){
                            List<BorrowFlowVO> bos = AppUtil.getBorrowBegs(rj.data);
                            if(bos == null || bos.isEmpty()){
                                UIUtil.showToastSafe("没有更多记录了");
                                if(isLoadingMore){
                                    mPageNum --;
                                    if(mPageNum<0)mPageNum = 0;
                                    isLoadingMore = false;
                                }
                            }else{
                                if(isLoadingMore){
                                    mBorrows.addAll(bos);
                                }else{
                                    mBorrows.clear();
                                    mBorrows.addAll(bos);
                                }
                                mBorrowAdapter.notifyDataSetChanged();
                            }
                        }else{
                            UIUtil.showToastSafe(rj.errorMsg);
                        }
                    }
                });
            }

            @Override
            public void onFailure(Object obj, int... code) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissProgress();
                        mPullRefreshListView.onRefreshComplete();
                        mPullRefreshListView.clearAnimation();
                        if (isLoadingMore) {
                            mPageNum--;
                            if (mPageNum < 0) mPageNum = 0;
                            isLoadingMore = false;
                        }
                        UIUtil.showToastSafe("未能获取书籍信息!");
                    }
                });
            }
        });
    }


    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        ILoadingLayout endLabels = mPullRefreshListView.getLoadingLayoutProxy(
                false, true);
        endLabels.setPullLabel(getResources().getString(R.string.pull_label));
        endLabels.setRefreshingLabel(getResources().getString(
                R.string.refresh_label));
        endLabels.setReleaseLabel(getResources().getString(
                R.string.release_label));
        endLabels.setLoadingDrawable(getResources().getDrawable(
                R.mipmap.publicloading));
        mPageNum++;
        isLoadingMore = true;
        getBorrowBegs( mPageNum);
    }
}
