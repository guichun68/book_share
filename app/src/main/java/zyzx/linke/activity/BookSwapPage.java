package zyzx.linke.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import zyzx.linke.R;
import zyzx.linke.base.BaseSwapPager;
import zyzx.linke.global.BundleFlag;
import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.ResponseJson;
import zyzx.linke.model.bean.SwapBookVO;
import zyzx.linke.utils.AppUtil;
import zyzx.linke.utils.UIUtil;

import static zyzx.linke.utils.UIUtil.getResources;

/**
 * Created by austin on 2017/8/12.
 * Desc: 书籍交换选项卡页
 */

public class BookSwapPage extends BaseSwapPager implements PullToRefreshBase.OnRefreshListener2<ListView>, AdapterView.OnItemClickListener {


    private SwapAdapter mAdapter;
    private final int SUCCFLAG = 0x738A,FAILUREFLAG = 0x891A;
    private List<SwapBookVO> mSwapBookVOs;
    private PullToRefreshListView mPullRefreshListView;
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
                    return;
                }
                switch (drj.errorCode){
                    case 2:
                        List<SwapBookVO> swapBookVOs = AppUtil.getSwapBooks(drj.data);
                        if(isRefreshing){
                            mSwapBookVOs.clear();
                            mSwapBookVOs.addAll(swapBookVOs);
                        }else{
                            if(swapBookVOs.isEmpty()){
                                mPageNum--;
                                UIUtil.showToastSafe("没有更多了！");
                            }else{
                                mSwapBookVOs.addAll(swapBookVOs);
                            }
                        }
                        dismisLoading();
                        mAdapter.notifyDataSetChanged();
                        break;
                    default:
                        UIUtil.showToastSafe("未能获取数据");
                        dismisLoading();
                        return;
                }
                break;
            case FAILUREFLAG:
                UIUtil.showToastSafe("未能获取数据");
                dismisLoading();
                break;
        }
    }

    public BookSwapPage(Context context, int layoutResId) {
        super(context, layoutResId);
    }

    @Override
    public void initView() {
        ListView listView;
        mPullRefreshListView = (PullToRefreshListView) getRootView().findViewById(R.id.pull_refresh_list);
        mPullRefreshListView.setOnRefreshListener(this);
        mPullRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        if(mSwapBookVOs == null){
            mSwapBookVOs = new ArrayList<>();
        }
        mAdapter = new SwapAdapter(context,R.layout.item_swap_book,mSwapBookVOs);
        ILoadingLayout endLabels = mPullRefreshListView.getLoadingLayoutProxy(false, true);
        endLabels.setPullLabel(getResources().getString(R.string.pull_label));
        endLabels.setRefreshingLabel(getResources().getString(
                R.string.refresh_label));
        endLabels.setReleaseLabel(getResources().getString(
                R.string.release_label));
        endLabels.setLoadingDrawable(getResources().getDrawable(
                R.mipmap.publicloading));
        listView = mPullRefreshListView.getRefreshableView();
        listView.setAdapter(mAdapter);
        mPullRefreshListView.setOnItemClickListener(this);
        mPageNum = 1;
        showDefProgress();
        getData(1);
    }

    public Handler getHandler(){
        return handler;
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
            isRefreshing = true;
            mPageNum = 1;
            getData(mPageNum);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        isRefreshing = false;
        if(mPageNum==1 && mSwapBookVOs.isEmpty()){
            getData(mPageNum);
        }else{
            getData(++mPageNum);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SwapBookVO sbv = (SwapBookVO)parent.getItemAtPosition(position);
        Intent intent = new Intent((context),BookSwapAct.class);
        intent.putExtra(BundleFlag.SWAP_BOOK_VO,sbv);
        context.startActivity(intent);
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

    private class SwapAdapter extends CommonAdapter<SwapBookVO> {

        private SwapAdapter(Context context, int layoutId, List<SwapBookVO> datas) {
            super(context, layoutId, datas);
        }

        @Override
        protected void convert(ViewHolder holder, SwapBookVO swapBookVO, int position) {
            Glide.with(context).load(swapBookVO.getBookImageLarge()).into((ImageView)holder.getView(R.id.iv));
            holder.setText(R.id.tv_have_book_name,"《"+swapBookVO.getBookTitle()+"》");
            holder.setText(R.id.tv_have_author,"作者："+swapBookVO.getBookAuthor());
            holder.setText(R.id.tv_want_book_name,"《"+swapBookVO.getSwapBookTitle()+"》");
            holder.setText(R.id.tv_want_author,"作者："+swapBookVO.getSwapBookAuthor());
        }

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
        mPullRefreshListView.onRefreshComplete();
        isRefreshing = false;
    }
}
