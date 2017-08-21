package zyzx.linke.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import zyzx.linke.R;
import zyzx.linke.adapter.MyCommonAdapter;
import zyzx.linke.adapter.MyViewHolder;
import zyzx.linke.base.BaseSwapPager;
import zyzx.linke.global.BundleFlag;
import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.ResponseJson;
import zyzx.linke.model.bean.SwapBookVO;
import zyzx.linke.utils.AppUtil;
import zyzx.linke.utils.UIUtil;
import zyzx.linke.views.MyRecyclerViewWapper;

/**
 * Created by austin on 2017/8/12.
 * Desc: 书籍交换选项卡页
 */

public class BookSwapPage extends BaseSwapPager{

    private SwapAdapter mAdapter;
    private final int SUCCFLAG = 0x738A,FAILUREFLAG = 0x891A;
    private List<SwapBookVO> mSwapBookVOs;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private MyRecyclerViewWapper mRecyclerView;
    private int mPageNum = 1;
    private boolean isRefreshing;


    private MyHandler handler = new MyHandler(this);

    private void myHandleMsg(Message msg){

        switch (msg.what){
            case SUCCFLAG:
                String resp = (String) msg.obj;
                ResponseJson drj = new ResponseJson(resp);
                if(drj.errorCode == ResponseJson.NO_DATA){
                    UIUtil.showToastSafe("未能获取数据");
                    dismisLoading();
                    mAdapter.setFooterStatus(MyCommonAdapter.Status.STATUS_LOADING_END);
                    return;
                }
                switch (drj.errorCode){
                    case 2:
                        List<SwapBookVO> swapBookVOs = AppUtil.getSwapBooks(drj.data);
                        if(isRefreshing){
                            mSwapBookVOs.clear();
                            mSwapBookVOs.addAll(swapBookVOs);
                            mAdapter.setFooterStatus(MyCommonAdapter.Status.STATUS_LOADING_END);
                        }else{
                            if(swapBookVOs.isEmpty()){
                                mPageNum--;
                                UIUtil.showToastSafe("没有更多了！");
                                mAdapter.setFooterStatus(MyCommonAdapter.Status.STATUS_NO_MORE_DATE);
                            }else{
                                mSwapBookVOs.addAll(swapBookVOs);
                                mAdapter.setFooterStatus(MyCommonAdapter.Status.STATUS_LOADING_END);
                            }
                        }
                        dismisLoading();
                        mAdapter.notifyDataSetChanged();
                        break;
                    default:
                        UIUtil.showToastSafe("未能获取数据");
                        dismisLoading();
                        mAdapter.setFooterStatus(MyCommonAdapter.Status.STATUS_LOADING_END);
                        return;
                }
                break;
            case FAILUREFLAG:
                UIUtil.showToastSafe("未能获取数据");
                dismisLoading();
                mAdapter.setFooterStatus(MyCommonAdapter.Status.STATUS_LOADING_END);
                break;
        }
    }

    public BookSwapPage(Context context, int layoutResId) {
        super(context, layoutResId);
    }

    @Override
    public void initView() {
        if(mSwapBookVOs == null){
            mSwapBookVOs = new ArrayList<>();
        }
        mSwipeRefreshLayout = (SwipeRefreshLayout) getRootView().findViewById(R.id.swipeRefreshLayout);
        mRecyclerView = (MyRecyclerViewWapper) getRootView().findViewById(R.id.recyclerView);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.title,
                android.R.color.holo_red_light,android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefreshing = true;
                mPageNum = 1;
                getData(mPageNum);
            }
        });
        mRecyclerView.AddMyOnScrollListener(new MyRecyclerViewWapper.MyOnScrollListener() {
            @Override
            public void onScrollStateChanged(MyRecyclerViewWapper recyclerView, int newState,boolean isLoadingMore) {
                if(isLoadingMore){
                    isRefreshing = false;
                    if(mPageNum==1 && mSwapBookVOs.isEmpty()){
                        getData(mPageNum);
                    }else{
                        getData(++mPageNum);
                    }
                    mAdapter.setFooterStatus(MyCommonAdapter.Status.STATUS_PULLUP_LOAD_MORE);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView,dx, dy);
            }
        });

        mAdapter = new SwapAdapter(context,mSwapBookVOs,R.layout.item_swap_book,R.layout.view_footer,R.id.load_progress,R.id.tv_tip);
        mRecyclerView.setAdapter(mAdapter);
        mPageNum = 1;
        showDefProgress();
        getData(1);
    }

    public Handler getHandler(){
        return handler;
    }


    private static class MyHandler extends Handler {
        WeakReference<BookSwapPage> mCurrPage;
        MyHandler(BookSwapPage page){
            this.mCurrPage = new WeakReference<>(page);
        }

        @Override
        public void handleMessage(Message msg) {
            BookSwapPage page = mCurrPage==null?null:mCurrPage.get();
            if(page == null){
                return;
            }
            page.myHandleMsg(msg);
        }
    }

    private class SwapAdapter extends MyCommonAdapter<SwapBookVO> {

        private SwapAdapter(Context context, List<SwapBookVO> datas, int itemLayoutResId,int footItemLayoutResId,int footerProgressResId,int footerTextTipResId) {
            super(context, datas, itemLayoutResId,footItemLayoutResId, footerProgressResId, footerTextTipResId);
        }

        @Override
        public void convert(MyViewHolder holder, final SwapBookVO swapBookVO, int position) {
            Glide.with(context).load(swapBookVO.getBookImageLarge()).into((ImageView)holder.getView(R.id.iv));
            holder.setText(R.id.tv_have_book_name,"《"+swapBookVO.getBookTitle()+"》");
            holder.setText(R.id.tv_have_author,"作者："+swapBookVO.getBookAuthor());
            holder.setText(R.id.tv_want_book_name,"《"+swapBookVO.getSwapBookTitle()+"》");
            holder.setText(R.id.tv_want_author,"作者："+swapBookVO.getSwapBookAuthor());
            holder.setOnClickListener(R.id.ll_root, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent((context),BookSwapAct.class);
                    intent.putExtra(BundleFlag.SWAP_BOOK_VO,swapBookVO);
                    context.startActivity(intent);
                }
            });
        }

        /*@Override
        protected void convert(ViewHolder holder, SwapBookVO swapBookVO, int position) {

        }*/

    }

    private void getData(int pageNum){
        getBookPresenter().getSwapBooks(pageNum, new CallBack() {
            @Override
            public void onSuccess(Object obj, int... code) {
                Message msg = handler.obtainMessage();
                msg.obj = obj;
                msg.what = SUCCFLAG;
                handler.sendMessage(msg);
            }

            @Override
            public void onFailure(Object obj, int... code) {
                handler.sendEmptyMessage(FAILUREFLAG);
            }
        });
    }


    private void dismisLoading(){
        dismissProgress();
        if(isRefreshing){
            mPageNum = 1;
        }else{
            mPageNum--;
            if(mPageNum<1){
                mPageNum = 1;
            }
        }
        mSwipeRefreshLayout.setRefreshing(false);
        isRefreshing = false;
    }
}
