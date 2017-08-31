package zyzx.linke.activity;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import zyzx.linke.R;
import zyzx.linke.adapter.MyCommonAdapter;
import zyzx.linke.adapter.MyViewHolder;
import zyzx.linke.base.BasePager;
import zyzx.linke.global.BundleFlag;
import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.ResponseJson;
import zyzx.linke.model.bean.SwapBookVO;
import zyzx.linke.utils.AppUtil;
import zyzx.linke.utils.StringUtil;
import zyzx.linke.utils.UIUtil;
import zyzx.linke.views.MyRecyclerViewWapper;

/**
 * Created by austin on 2017/8/12.
 * Desc: 书籍交换选项卡页
 */

public class BookPage extends BasePager {

    private AppCompatEditText etSearch;
    private SwapAdapter mAdapter;
    private final int SUCCFLAG = 0x738A,FAILUREFLAG = 0x891A;
    private List<SwapBookVO> mSwapBookVOs;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private MyRecyclerViewWapper mRecyclerView;
    private int mPageNum = 1;
    private boolean isRefreshing;
    private LinearLayout llSearch;
    private int searchBarHeight;
    int disy;//一次滑动的距离

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

    public BookPage(Context context, int layoutResId) {
        super(context, layoutResId);
    }

    @Override
    public void initView() {
        if(mSwapBookVOs == null){
            mSwapBookVOs = new ArrayList<>();
        }
        llSearch = (LinearLayout) getRootView().findViewById(R.id.ll_search);
        searchBarHeight = llSearch.getHeight();
        mSwipeRefreshLayout = (SwipeRefreshLayout) getRootView().findViewById(R.id.swipeRefreshLayout);
        etSearch = (AppCompatEditText) getRootView().findViewById(R.id.et_search);
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
                //得到第一个item
                /*int firstVisibleItem = ((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                //当前可见item的第一个是否是列表的第一个，如果是第一个应该显示
                if(firstVisibleItem == 0){
                    if(!isSearchBarShow){
                        //如果此时没有显示，则显示
                        isSearchBarShow = true;
                        showSearchBar();
                    }
                }else{//不是第一个
                    if(disy>100 && isSearchBarShow){//滑动距离大于100且toolbar显示中，继续向下滚，隐藏
                        isSearchBarShow = false;
//                    ((IMainView)getActivity()).hideToolBar();
                        hideSearchBar();
                        disy = 0;
                    }
                    if(disy<-100 && !isSearchBarShow){//向上滑动且距离大于100且toolbar隐藏中，则显示
                        isSearchBarShow = true;
//                    ((IMainView)getActivity()).showToolBar();
                        showSearchBar();
                        disy = 0;
                    }
                }
                if((isSearchBarShow && dy>0)||(!isSearchBarShow && dy <0)){//增加滑动的距离，只有再出发两种状态的时候才进行叠加
                    disy += dy;
                }*/
            }
        });

        mAdapter = new SwapAdapter(context,mSwapBookVOs,R.layout.item_swap_book,R.layout.view_footer,R.id.load_progress,R.id.tv_tip);
        mRecyclerView.setAdapter(mAdapter);
        mPageNum = 1;
        showDefProgress();
        getData(1);

        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId== EditorInfo.IME_ACTION_SEND ||(event!=null&&event.getKeyCode()== KeyEvent.KEYCODE_ENTER)) {
                    switch (event.getAction()){
                        case KeyEvent.ACTION_UP:
                            if(StringUtil.isEmpty(v.getText().toString())){
                                UIUtil.showToastSafe("请输入搜索关键字");
                                return true;
                            }
                            Intent i = new Intent(context,SwapBookSearchResultAct.class);
                            i.putExtra(BundleFlag.KEY_WORD,v.getText().toString());
                            context.startActivity(i);
                            return true;
                        default:
                            return true;
                    }
                }
                return false;
            }
        });
    }

    public Handler getHandler(){
        return handler;
    }


    private static class MyHandler extends Handler {
        WeakReference<BookPage> mCurrPage;
        MyHandler(BookPage page){
            this.mCurrPage = new WeakReference<>(page);
        }

        @Override
        public void handleMessage(Message msg) {
            BookPage page = mCurrPage==null?null:mCurrPage.get();
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
            Glide.with(context).load(swapBookVO.getBookImageLarge()).placeholder(R.mipmap.defaultcover).into((ImageView)holder.getView(R.id.iv));
            holder.setText(R.id.tv_have_book_name,"《"+swapBookVO.getBookTitle()+"》");
            holder.setText(R.id.tv_have_author,"作者："+ (StringUtil.isEmpty(swapBookVO.getBookAuthor())?"暂无":swapBookVO.getBookAuthor()));
            holder.setText(R.id.tv_want_book_name,"《"+swapBookVO.getSwapBookTitle()+"》");
            holder.setText(R.id.tv_want_author,"作者："+(StringUtil.isEmpty(swapBookVO.getSwapBookAuthor())?"暂无":swapBookVO.getSwapBookAuthor()));
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

    private void hideSearchBar(){
        ObjectAnimator oa = ObjectAnimator.ofFloat(llSearch,View.TRANSLATION_Y,0,-searchBarHeight);
        oa.setDuration(500);
        oa.start();
    }

    private void showSearchBar(){
        ObjectAnimator oa = ObjectAnimator.ofFloat(llSearch,View.TRANSLATION_Y,-searchBarHeight,0);
        oa.setDuration(500);
        oa.start();
    }
}
