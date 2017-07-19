package zyzx.linke.activity;

import android.os.Bundle;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import zyzx.linke.R;
import zyzx.linke.base.BaseActivity;
import zyzx.linke.base.GlobalParams;
import zyzx.linke.model.CallBack;

/**
 * Created by austin on 2017/7/18.
 */

public class MyBorrowBegsAct extends BaseActivity implements PullToRefreshBase.OnRefreshListener {
    private PullToRefreshListView mPullRefreshListView;
    private int mPageNum = 1;
    private boolean isLoadingMore;//是否是加载更多的动作

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
    }

    @Override
    protected void initData() {

    }

    /**
     * 获取我的所有借书请求
     * @param uid     user's uuid
     * @param pageNum pageNo
     */
    private void getBooks(String uid, int pageNum) {
        getBookPresenter().getAllBorrowBegs(uid, pageNum, new CallBack() {
            @Override
            public void onSuccess(final Object obj, int... code) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissProgress();
                        mPullRefreshListView.onRefreshComplete();
                        mPullRefreshListView.clearAnimation();
                        ArrayList<MyBookDetailVO> books = (ArrayList<MyBookDetailVO>) obj;
                        if (books == null || books.isEmpty()) {
                            UIUtil.showToastSafe("没有更多书籍了!");
                            if (isLoadingMore) {
                                mPageNum--;
                                if (mPageNum < 0) mPageNum = 0;
                                isLoadingMore = false;
                            }
                        } else {
                            mBooks.addAll(books);
                            myBookAdapter.notifyDataSetChanged();
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
        getBooks(GlobalParams.getLastLoginUser().getUid(), mPageNum);
    }
}
