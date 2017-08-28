package zyzx.linke.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.OrientationHelper;
import android.view.View;

import com.hyphenate.chat.EMClient;

import java.util.ArrayList;

import zyzx.linke.R;
import zyzx.linke.adapter.BorrowedInAdapter;
import zyzx.linke.adapter.MyCommonAdapter;
import zyzx.linke.base.BaseActivity;
import zyzx.linke.base.GlobalParams;
import zyzx.linke.global.BundleFlag;
import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.BorrowedInVO;
import zyzx.linke.utils.UIUtil;
import zyzx.linke.views.AdvanceDecoration;
import zyzx.linke.views.MyRecyclerViewWapper;

/**
 * Created by austin on 2017/3/20.
 * Desc： 我的借入的书籍列表
 */

public class BorrowedInBookAct extends BaseActivity{

    private MyRecyclerViewWapper mMyRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ArrayList<BorrowedInVO> mBooks;
    private BorrowedInAdapter mBorrowedInAdapter;
    private boolean isLoadingMore;//是否是加载更多的动作
    private int mPageNum = 1;

    @Override
    protected int getLayoutId() {
        return R.layout.act_borrowed_book;
    }

    @Override
    protected void initView(Bundle saveInstanceState) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.title,
                android.R.color.holo_red_light,android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mBooks.clear();
                //下拉刷新
                isLoadingMore = false;
                mPageNum = 1;
                getBooks(Integer.parseInt(EMClient.getInstance().getCurrentUser()), mPageNum);
            }
        });
        mBooks = new ArrayList<>();
        mMyRecyclerView = (MyRecyclerViewWapper) findViewById(R.id.recyclerView);
//        mMyRecyclerView.addItemDecoration(new AdvanceDecoration(this, OrientationHelper.HORIZONTAL));
        mBorrowedInAdapter = new BorrowedInAdapter(this, mBooks,R.layout.item_borrowed_books,R.layout.view_footer,R.id.load_progress,R.id.tv_tip);
        mMyRecyclerView.setAdapter(mBorrowedInAdapter);
        mMyRecyclerView.AddMyOnScrollListener(new MyRecyclerViewWapper.MyOnScrollListener() {
            @Override
            public void onScrollStateChanged(MyRecyclerViewWapper recyclerView, int newState,boolean isLoadMore) {
                if(isLoadMore){
                    mPageNum++;
                    isLoadingMore = true;
                    getBooks(Integer.parseInt(EMClient.getInstance().getCurrentUser()), mPageNum);
                    mBorrowedInAdapter.setFooterStatus(MyCommonAdapter.Status.STATUS_PULLUP_LOAD_MORE);
                }
            }
        });
        mTitleText.setText("我借入的书");
        mBorrowedInAdapter.setOnClickListener(new BorrowedInAdapter.OnClickListener() {
            @Override
            public boolean onLongItemClickListener(View view, BorrowedInVO bookDetailVO, int position) {
                UIUtil.showTestToast(mContext,"长按事件");
                return false;
            }

            @Override
            public void onItemClickListener(View view, BorrowedInVO bookDetailVO, int position) {
                //进入图书详情页
                Intent intent = new Intent(mContext, BorrowBookDetail.class);
                intent.putExtra(BundleFlag.BOOK_BORROW,bookDetailVO);
                BorrowedInBookAct.this.startActivity(intent);
            }
        });

    }

    @Override
    protected void initData() {
        mBooks.clear();
        showDefProgress();
        mPageNum = 1;
        getBooks(Integer.parseInt(EMClient.getInstance().getCurrentUser()), mPageNum);
    }

    /**
     * 获取所有我借入的书籍
     *
     * @param userId userId
     * @param pageNum pageNo
     */
    private void getBooks(Integer userId, int pageNum) {
        getBookPresenter().getMyBorrowedInBooks(userId, pageNum, new CallBack() {
            @Override
            public void onSuccess(final Object obj, int... code) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissProgress();
                        mSwipeRefreshLayout.setRefreshing(false);
                        ArrayList<BorrowedInVO> books = (ArrayList<BorrowedInVO>) obj;
                        if (books == null || books.isEmpty()) {
                            UIUtil.showToastSafe("没有更多书籍了!");
                            mBorrowedInAdapter.setFooterStatus(MyCommonAdapter.Status.STATUS_NO_MORE_DATE);
                            if(isLoadingMore){
                                mPageNum--;
                                if(mPageNum<=0)mPageNum=1;
                                isLoadingMore=false;
                            }
                        } else {
                            mBooks.addAll(books);
                            mBorrowedInAdapter.setFooterStatus(MyCommonAdapter.Status.STATUS_LOADING_END);
                        }
                    }
                });
            }

            @Override
            public void onFailure(Object obj, int... code) {
                dismissProgress();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                        if(isLoadingMore){
                            mPageNum--;
                            if(mPageNum<=0)mPageNum=1;
                            isLoadingMore = false;
                        }
                        UIUtil.showToastSafe("未能获取书籍信息!");
                    }
                });

            }
        });
    }




}
