package zyzx.linke.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import zyzx.linke.R;
import zyzx.linke.adapter.MyBookBorrowBegAdp;
import zyzx.linke.adapter.MyCommonAdapter;
import zyzx.linke.base.BaseActivity;
import zyzx.linke.base.GlobalParams;
import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.BorrowFlowVO;
import zyzx.linke.model.bean.ResponseJson;
import zyzx.linke.utils.AppUtil;
import zyzx.linke.utils.UIUtil;
import zyzx.linke.views.MyRecyclerViewWapper;

/**
 * Created by austin on 2017/7/18.
 * Desc: 我的借阅请求列表 界面
 */

public class MyBorrowBegsAct extends BaseActivity {
    private SwipeRefreshLayout mSwipeRefreshLayout;
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
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        MyRecyclerViewWapper myRecyclerView;
        mTitleText.setText("求借求赠送记录");
        myRecyclerView = (MyRecyclerViewWapper) findViewById(R.id.recyclerView);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.white);
        //设置刷新时动画的颜色，可以设置4个
        mSwipeRefreshLayout.setColorSchemeResources(R.color.title,
                android.R.color.holo_red_light,android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        mBorrowAdapter = new MyBookBorrowBegAdp(this,mBorrows,R.layout.item_begs,R.layout.view_footer,R.id.load_progress,R.id.tv_tip);
        myRecyclerView.setAdapter(mBorrowAdapter);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                showDefProgress();
                mPageNum = 1;
                getBorrowBegs(mPageNum);
                isLoadingMore = false;
            }
        });
        myRecyclerView.AddMyOnScrollListener(new MyRecyclerViewWapper.MyOnScrollListener() {
            @Override
            public void onScrollStateChanged(MyRecyclerViewWapper recyclerView, int newState,boolean isLoadMore) {
                if(isLoadMore){
                    mPageNum++;
                    isLoadingMore = true;
                    getBorrowBegs( mPageNum);
                    mBorrowAdapter.setFooterStatus(MyCommonAdapter.Status.STATUS_PULLUP_LOAD_MORE);
                }
            }
        });
    }

    @Override
    protected void initData() {
        showDefProgress();
        mPageNum = 1;
        getBorrowBegs(mPageNum);
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
                        mSwipeRefreshLayout.setRefreshing(false);
                        String json = (String)obj;
                        ResponseJson rj = new ResponseJson(json);
                        if(rj.errorCode == ResponseJson.NO_DATA){
                            UIUtil.showToastSafe("获取失败");
                            mBorrowAdapter.setFooterStatus(MyCommonAdapter.Status.STATUS_LOADING_END);
                            return;
                        }
                        if(rj.errorCode == 1){
                            List<BorrowFlowVO> bos = AppUtil.getBorrowBegs(rj.data);
                            if(bos == null || bos.isEmpty()){
                                UIUtil.showToastSafe("没有更多记录了");
                                mBorrowAdapter.setFooterStatus(MyCommonAdapter.Status.STATUS_NO_MORE_DATE);
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
                                mBorrowAdapter.setFooterStatus(MyCommonAdapter.Status.STATUS_LOADING_END);
                            }
                        }else{
                            UIUtil.showToastSafe(rj.errorMsg);
                            mBorrowAdapter.setFooterStatus(MyCommonAdapter.Status.STATUS_LOADING_END);
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
                        mSwipeRefreshLayout.setRefreshing(false);
                        if (isLoadingMore) {
                            mPageNum--;
                            if (mPageNum < 0) mPageNum = 0;
                            isLoadingMore = false;
                        }
                        mBorrowAdapter.setFooterStatus(MyCommonAdapter.Status.STATUS_LOADING_END);
                        UIUtil.showToastSafe("未能获取书籍信息!");
                    }
                });
            }
        });
    }

}
