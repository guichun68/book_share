package zyzx.linke.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import zyzx.linke.R;
import zyzx.linke.adapter.MyCommonAdapter;
import zyzx.linke.adapter.MyViewHolder;
import zyzx.linke.base.BaseActivity;
import zyzx.linke.global.BundleFlag;
import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.DefindResponseJson;
import zyzx.linke.model.bean.SwapBookVO;
import zyzx.linke.utils.AppUtil;
import zyzx.linke.utils.StringUtil;
import zyzx.linke.utils.UIUtil;
import zyzx.linke.views.MyRecyclerViewWapper;

/**
 * Created by Austin on 2017-08-27.
 * Desc: 图书交换搜索结果页面
 */

public class SwapBookSearchResultAct extends BaseActivity {
    private MyRecyclerViewWapper mRecyclerView;
    private SwapAdapter mAdapter;
    private ArrayList<SwapBookVO> mBooks;
    private int mPageNum;
    private final int SUCCESS = 0x47B2,FAILURE = 0xB52A;
    private boolean isRefreshing = false;
    private TextView tvSearchWant;

    private String keyWord;
    private boolean isNormalSearch = true;//是否是从拥有的书籍中搜索

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
                        List<SwapBookVO> swapBookVOs = AppUtil.getSwapBooksSearch(drj.data.getItems());
                        if(isRefreshing){
                            mBooks.clear();
                            mBooks.addAll(swapBookVOs);
                        }else{
                            if(swapBookVOs.isEmpty()){
                                mPageNum--;
                                UIUtil.showToastSafe("没有更多了！");
                            }else{
                                mBooks.addAll(swapBookVOs);
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
        mTitleText.setText("交换书籍搜索结果");

        if(mBooks == null){
            mBooks = new ArrayList<>();
        }

        tvSearchWant = (TextView) findViewById(R.id.tv_search_want);
        mRecyclerView = (MyRecyclerViewWapper) findViewById(R.id.recyclerView);

        mAdapter = new SwapAdapter(this,mBooks,R.layout.item_swap_book,R.layout.view_footer,R.id.load_progress,R.id.tv_tip);

        mRecyclerView.setAdapter(mAdapter);


        mRecyclerView.AddMyOnScrollListener(new MyRecyclerViewWapper.MyOnScrollListener() {
            @Override
            public void onScrollStateChanged(MyRecyclerViewWapper recyclerView, int newState,boolean isLoadMore) {
                if(isLoadMore){
                    isRefreshing = false;
                    getData(isNormalSearch,++mPageNum);
                    mAdapter.setFooterStatus(MyCommonAdapter.Status.STATUS_PULLUP_LOAD_MORE);
                }
            }

        });
        tvSearchWant.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.tv_search_want:
                showDefProgress();
                //从所有用户想要 的书中搜索同样的关键字
                mBooks.clear();
                if(tvSearchWant.getText().toString().equals("从拥有中搜索")){
                    tvSearchWant.setText("从想要交换中搜索");
                    getData(true,mPageNum=1);
                }else{
                    tvSearchWant.setText("从拥有中搜索");
                    getData(false,mPageNum=1);
                }
                break;
        }
    }

    /**
     *
     * @param normalSearch 是否从正面搜索，即true表示从用户已经拥有的书中搜索，false则从用户想要交换的书中搜索
     * @param pageNum
     */
    private void getData(boolean normalSearch,int pageNum){
        this.isNormalSearch = normalSearch;
        if(normalSearch){
            getBookPresenter().searchSwapBooks(keyWord,pageNum, new CallBack() {
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
        }else{
            getBookPresenter().searchSwapWantBooks(keyWord,pageNum, new CallBack() {
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

    }

    @Override
    protected void initData() {
        keyWord = getIntent().getStringExtra(BundleFlag.KEY_WORD);
        if(StringUtil.isEmpty(keyWord)){
            UIUtil.showToastSafe("未能解析关键字");
            return;
        }
        getData(true,mPageNum=1);
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    private static class MyHandler extends Handler {
        WeakReference<SwapBookSearchResultAct> mActivity;
        MyHandler(SwapBookSearchResultAct act){
            this.mActivity = new WeakReference<>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            SwapBookSearchResultAct act = mActivity==null?null:mActivity.get();
            if(act == null){
                return;
            }
            act.myHandleMessage(msg);
        }
    }

    private class SwapAdapter extends MyCommonAdapter<SwapBookVO> {

        private SwapAdapter(Context context, List<SwapBookVO> datas, int itemLayoutResId, int footItemLayoutResId, int footerProgressResId, int footerTextTipResId) {
            super(context, datas, itemLayoutResId,footItemLayoutResId, footerProgressResId, footerTextTipResId);
        }

        @Override
        public void convert(MyViewHolder holder, final SwapBookVO swapBookVO, int position) {
            Glide.with(SwapBookSearchResultAct.this).load(swapBookVO.getBookImageLarge()).placeholder(R.mipmap.defaultcover).into((ImageView)holder.getView(R.id.iv));
            holder.setText(R.id.tv_have_book_name,"《"+swapBookVO.getBookTitle()+"》");
            holder.setText(R.id.tv_have_author,"作者："+ (StringUtil.isEmpty(swapBookVO.getBookAuthor())?"暂无":swapBookVO.getBookAuthor()));
            holder.setText(R.id.tv_want_book_name,"《"+swapBookVO.getSwapBookTitle()+"》");
            holder.setText(R.id.tv_want_author,"作者："+(StringUtil.isEmpty(swapBookVO.getSwapBookAuthor())?"暂无":swapBookVO.getSwapBookAuthor()));
            holder.setOnClickListener(R.id.ll_root, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent((SwapBookSearchResultAct.this),BookSwapAct.class);
                    intent.putExtra(BundleFlag.SWAP_BOOK_VO,swapBookVO);
                    SwapBookSearchResultAct.this.startActivity(intent);
                }
            });
        }
    }
}
