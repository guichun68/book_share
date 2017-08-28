package zyzx.linke.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import zyzx.linke.R;
import zyzx.linke.adapter.BookVOAdapter;
import zyzx.linke.adapter.MyCommonAdapter;
import zyzx.linke.base.BaseActivity;
import zyzx.linke.global.BundleFlag;
import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.DefindResponseJson;
import zyzx.linke.model.bean.MyBookDetailVO;
import zyzx.linke.utils.AppUtil;
import zyzx.linke.utils.StringUtil;
import zyzx.linke.utils.UIUtil;
import zyzx.linke.views.MyRecyclerViewWapper;

/**
 * Created by Austin on 2017-08-27.
 * Desc: 分享中心搜索结果页面
 */

public class BookSearchResultAct extends BaseActivity {
    private MyRecyclerViewWapper mRecyclerView;
    private BookVOAdapter mAdapter;
    private ArrayList<MyBookDetailVO> mBooks;
    private int mPageNum;
    private final int SUCCESS = 0x47B2,FAILURE = 0xB52A;
    private boolean isRefreshing = false;
    private String from;
    private TextView tvSearchWant;
    private String keyWord;

    private MyHandler handler = new MyHandler(this);

    private void myHandleMessage(Message msg){
        switch (msg.what){
            case SUCCESS:
                String resp = (String) msg.obj;
                DefindResponseJson drj = new DefindResponseJson(resp);
                if(drj.errorCode == DefindResponseJson.NO_DATA){
                    UIUtil.showToastSafe("未能获取数据");
                    dismissLoading();
                    mAdapter.setFooterStatus(MyCommonAdapter.Status.STATUS_LOADING_END);
                    return;
                }
                switch (drj.errorCode){
                    case 2:
                        List list = drj.data.getItems();
                        ArrayList<MyBookDetailVO> swapSkillVOs = AppUtil.getBookDetailVOs(list);
                        if(isRefreshing){
                            mBooks.clear();
                            mBooks.addAll(swapSkillVOs);
                        }else{
                            if(swapSkillVOs.isEmpty()){
                                mPageNum--;
                                UIUtil.showToastSafe("没有更多了！");
                            }else{
                                mBooks.addAll(swapSkillVOs);
                            }
                        }
                        dismissProgress();
                        mAdapter.setFooterStatus(MyCommonAdapter.Status.STATUS_LOADING_END);
                        break;
                    case 3:
                        UIUtil.showToastSafe("没有更多了");
                        mAdapter.setFooterStatus(MyCommonAdapter.Status.STATUS_NO_MORE_DATE);
                        dismissLoading();
                        break;
                    default:
                        UIUtil.showToastSafe("未能获取数据");
                        dismissLoading();
                        mAdapter.setFooterStatus(MyCommonAdapter.Status.STATUS_LOADING_END);
                        return;
                }
                break;
            case FAILURE:
                UIUtil.showToastSafe("未能获取数据");
                dismissLoading();
                mAdapter.setFooterStatus(MyCommonAdapter.Status.STATUS_LOADING_END);
                break;
        }
    }


    private void dismissLoading(){
        dismissProgress();
        if(isRefreshing){
            mPageNum = 1;
        }else{
            mPageNum--;
            if(mPageNum<1){
                mPageNum = 1;
            }
        }
        isRefreshing = false;
    }
    
    @Override
    protected int getLayoutId() {
        return R.layout.act_search_result;
    }

    @Override
    protected void initView(Bundle saveInstanceState) {
        mTitleText.setText("分享书籍搜索结果");

        if(mBooks == null){
            mBooks = new ArrayList<>();
        }
        mRecyclerView = (MyRecyclerViewWapper) findViewById(R.id.recyclerView);

        mAdapter = new BookVOAdapter(this,mBooks,R.layout.item_book,R.layout.view_footer,R.id.load_progress,R.id.tv_tip);

        mRecyclerView.setAdapter(mAdapter);


        mRecyclerView.AddMyOnScrollListener(new MyRecyclerViewWapper.MyOnScrollListener() {
            @Override
            public void onScrollStateChanged(MyRecyclerViewWapper recyclerView, int newState,boolean isLoadMore) {
                if(isLoadMore){
                    isRefreshing = false;
                    getData(++mPageNum);
                    mAdapter.setFooterStatus(MyCommonAdapter.Status.STATUS_PULLUP_LOAD_MORE);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView,dx, dy);
            }
        });

        tvSearchWant = (TextView) findViewById(R.id.tv_search_want);
    }



    private void getData(int pageNum){

        getBookPresenter().searchBooks(keyWord,pageNum, new CallBack() {
            @Override
            public void onSuccess(Object obj, int... code) {
                Message msg = handler.obtainMessage();
                msg.obj = obj;
                msg.what = SUCCESS;
                handler.sendMessage(msg);
            }

            @Override
            public void onFailure(Object obj, int... code) {
                handler.sendEmptyMessage(FAILURE);
            }
        });
    }

    @Override
    protected void initData() {
        keyWord = getIntent().getStringExtra(BundleFlag.KEY_WORD);
        from = getIntent().getStringExtra(BundleFlag.FROM);
        if(StringUtil.isEmpty(keyWord)){
            UIUtil.showToastSafe("未能解析关键字");
            return;
        }
        if(!StringUtil.isEmpty(from)&& from.equals(BundleFlag.Share_Center)){
            tvSearchWant.setVisibility(View.GONE);
        }
        getData(mPageNum=1);
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    private static class MyHandler extends Handler {
        WeakReference<BookSearchResultAct> mActivity;
        MyHandler(BookSearchResultAct act){
            this.mActivity = new WeakReference<>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            BookSearchResultAct act = mActivity==null?null:mActivity.get();
            if(act == null){
                return;
            }
            act.myHandleMessage(msg);
        }
    }   
}
