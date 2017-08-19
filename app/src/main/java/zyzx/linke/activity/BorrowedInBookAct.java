package zyzx.linke.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;

import zyzx.linke.R;
import zyzx.linke.adapter.BorrowedInAdapter;
import zyzx.linke.base.BaseActivity;
import zyzx.linke.global.BundleFlag;
import zyzx.linke.base.GlobalParams;
import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.MyBookDetailVO;
import zyzx.linke.utils.UIUtil;

/**
 * Created by austin on 2017/3/20.
 * Desc： 借入的书籍列表
 */

public class BorrowedInBookAct extends BaseActivity implements PullToRefreshBase.OnRefreshListener{

    private PullToRefreshListView mPullRefreshListView;
    private ArrayList<MyBookDetailVO> mBooks;
    private BorrowedInAdapter mBorrowedInAdapter;
    private boolean isLoadingMore;//是否是加载更多的动作
    private int mPageNum;

    @Override
    protected int getLayoutId() {
        return R.layout.act_borrowed_book;
    }

    @Override
    protected void initView(Bundle saveInstanceState) {
        mTitleText.setText("我借入的书");
        mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
        mPullRefreshListView.setOnRefreshListener(this);
        mPullRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);//上拉加载更多
        ListView actualListView = mPullRefreshListView.getRefreshableView();
        // Need to use the Actual ListView when registering for Context Menu
        registerForContextMenu(actualListView);
        actualListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    UIUtil.showTestToast(mContext,"长按事件");
                    return false;
            }
        });

        actualListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //进入图书详情页
                MyBookDetailVO myBookDetailVO = (MyBookDetailVO) parent.getItemAtPosition(position);
                Intent intent = new Intent(mContext, CommonBookDetailAct.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("book", myBookDetailVO);
                intent.putExtra(BundleFlag.SHOWADDRESS, false);
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });

        mBooks = new ArrayList<>();
        mBorrowedInAdapter = new BorrowedInAdapter(mContext, mBooks);
        actualListView.setAdapter(mBorrowedInAdapter);
    }

    @Override
    protected void initData() {
        mBooks.clear();
        showDefProgress();
        getBooks(GlobalParams.getLastLoginUser().getUserid(), 0);
    }

    /**
     * 获取所有我借入的书籍
     *
     * @param userid userId
     * @param pageNum pageNo
     */
    private void getBooks(Integer userid, int pageNum) {
        getBookPresenter().getMyBorrowedInBooks(userid, pageNum, new CallBack() {
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
                            if(isLoadingMore){
                                mPageNum--;
                                if(mPageNum<0)mPageNum=0;
                                isLoadingMore=false;
                            }
                        } else {
                            mBooks.addAll(books);
                            mBorrowedInAdapter.notifyDataSetChanged();
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
                        mPullRefreshListView.onRefreshComplete();
                        mPullRefreshListView.clearAnimation();
                        if(isLoadingMore){
                            mPageNum--;
                            if(mPageNum<0)mPageNum=0;
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
        getBooks(GlobalParams.getLastLoginUser().getUserid(), mPageNum);
    }
}
