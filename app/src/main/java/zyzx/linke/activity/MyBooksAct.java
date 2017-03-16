package zyzx.linke.activity;

import android.os.Bundle;
import android.widget.AbsListView;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;

import zyzx.linke.R;
import zyzx.linke.adapter.AllMyBookAdapter;
import zyzx.linke.constant.GlobalParams;
import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.BookDetail2;
import zyzx.linke.model.bean.MyBookDetailVO;
import zyzx.linke.utils.UIUtil;

/**
 * Created by austin on 2017/3/16.
 * Desc: 我的所有书籍列表页
 */

public class MyBooksAct extends BaseActivity implements PullToRefreshBase.OnRefreshListener, AbsListView.OnScrollListener {
    private PullToRefreshListView mPullRefreshListView;
    private AllMyBookAdapter myBookAdapter;
    private ArrayList<MyBookDetailVO> mBooks;
    private int pageNum;

    @Override
    protected int getLayoutId() {
        return R.layout.act_all_my_book;
    }

    @Override
    protected void initView(Bundle saveInstanceState) {
        mTitleText.setText("我登记的所有图书");
        mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
        mPullRefreshListView.setOnRefreshListener(this);
        mPullRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);//上拉加载更多
        ListView actualListView = mPullRefreshListView.getRefreshableView();
        // Need to use the Actual ListView when registering for Context Menu
        registerForContextMenu(actualListView);
        mBooks = new ArrayList<>();
        myBookAdapter = new AllMyBookAdapter(mContext,mBooks);
        actualListView.setAdapter(myBookAdapter);
        mPullRefreshListView.setOnScrollListener(this);
    }

    @Override
    protected void initData() {
        mBooks.clear();
        getBooks(GlobalParams.gUser.getUserid(),0);
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
        pageNum++;
        getBooks(GlobalParams.gUser.getUserid(),pageNum);
    }
    /**
     * 获取我的所有书籍（不包含借入的书籍）
     * @param userid
     * @param pageNum
     */
    private void getBooks(Integer userid, int pageNum) {
        GlobalParams.getBookPresenter().getMyBooks(userid, pageNum, new CallBack() {
            @Override
            public void onSuccess(final Object obj) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mPullRefreshListView.onRefreshComplete();
                        mPullRefreshListView.clearAnimation();
                        ArrayList<MyBookDetailVO> books = (ArrayList<MyBookDetailVO>) obj;
                        if(books==null || books.isEmpty()){
                            UIUtil.showToastSafe("没有更多书籍了!");
                        }else{
                            mBooks.addAll(books);
                            myBookAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }

            @Override
            public void onFailure(Object obj) {
                mPullRefreshListView.onRefreshComplete();
                mPullRefreshListView.clearAnimation();
                UIUtil.showToastSafe("未能获取书籍信息!");
            }
        });
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }
}
