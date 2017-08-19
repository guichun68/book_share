package zyzx.linke.activity;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zhy.adapter.recyclerview.wrapper.HeaderAndFooterWrapper;
import com.zhy.adapter.recyclerview.wrapper.LoadMoreWrapper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import zyzx.linke.R;
import zyzx.linke.base.BaseSwapPager;
import zyzx.linke.base.GlobalParams;
import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.DefindResponseJson;
import zyzx.linke.model.bean.SwapSkillVo;
import zyzx.linke.utils.AppUtil;
import zyzx.linke.utils.StringUtil;
import zyzx.linke.utils.UIUtil;
import zyzx.linke.views.AdvanceDecoration;

/**
 * Created by austin on 2017/8/12.
 * Desc: 技能交换选项卡页
 */

public class SkillSwapPageOld extends BaseSwapPager {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView mRecyclerView;
    private SkillAdapter mAdapter;
    private ArrayList<SwapSkillVo> mSwapSkillVos;
    private int mPageNum;
    private final int SUCCESS = 0x47B,FAILURE = 0xB52;
    private boolean isRefreshing;
    private int lastVisibleItemPosition;

    private MyHandler handler = new MyHandler(this);

    private void myHandleMessage(Message msg){
        switch (msg.what){
            case SUCCESS:
                String resp = (String) msg.obj;
                DefindResponseJson drj = new DefindResponseJson(resp);
                if(drj.errorCode == DefindResponseJson.NO_DATA){
                    UIUtil.showToastSafe("未能获取数据");
                    dismisLoading();
                    return;
                }
                switch (drj.errorCode){
                    case 2:
                        ArrayList<SwapSkillVo> swapSkillVOs = AppUtil.getSwapSkills(drj.data.getItems());
                        if(isRefreshing){
                            mSwapSkillVos.clear();
                            mSwapSkillVos.addAll(swapSkillVOs);
                        }else{
                            if(swapSkillVOs.isEmpty()){
                                mPageNum--;
                                UIUtil.showToastSafe("没有更多了！");
                            }else{
                                mSwapSkillVos.addAll(swapSkillVOs);
                            }
                        }
                        dismissProgress();
                        mSwipeRefreshLayout.setRefreshing(false);
                        mLoadMoreWrapper.notifyDataSetChanged();
                        break;
                    case 3:
                        UIUtil.showTestLog("没有更多了");
                        mLoadMoreWrapper.notifyDataSetChanged();
                        dismisLoading();
                        break;
                    default:
                        UIUtil.showToastSafe("未能获取数据");
                        dismisLoading();
                        mLoadMoreWrapper.notifyDataSetChanged();
                        return;
                }
                break;
            case FAILURE:
                UIUtil.showToastSafe("未能获取数据");
                dismisLoading();
                break;
        }
    }

    public SkillSwapPageOld(Context context, int layoutResId) {
        super(context, layoutResId);
    }
    private HeaderAndFooterWrapper mHeaderAndFooterWrapper;
    private LoadMoreWrapper mLoadMoreWrapper;
    @Override
    public void initView() {
        if(mSwapSkillVos == null){
            mSwapSkillVos = new ArrayList<>();
        }
        mSwipeRefreshLayout = (SwipeRefreshLayout) getRootView().findViewById(R.id.swipeRefreshLayout);
        mRecyclerView = (RecyclerView) getRootView().findViewById(R.id.recyclerView);

        mSwipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.white);
        //设置刷新时动画的颜色，可以设置4个
        mSwipeRefreshLayout.setColorSchemeResources(R.color.title,
                android.R.color.holo_red_light,android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        /*mSwipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                        .getDisplayMetrics()));*/
        linearLayoutManager =new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        //添加分隔线
        mRecyclerView.addItemDecoration(new AdvanceDecoration(context, OrientationHelper.VERTICAL));
        mAdapter = new SkillAdapter(context,R.layout.item_skill, mSwapSkillVos);
        /*mHeaderAndFooterWrapper = new HeaderAndFooterWrapper(mAdapter);
        mHeaderAndFooterWrapper.addFootView(View.inflate(context,R.layout.view_footer,null));*/
        mLoadMoreWrapper = new LoadMoreWrapper(mAdapter);
        mLoadMoreWrapper.setLoadMoreView(R.layout.view_footer);
        mLoadMoreWrapper.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener()
        {
            @Override
            public void onLoadMoreRequested() {
                UIUtil.showTestLog("TTG","LoadingMore");
                isRefreshing = false;
                getData(++mPageNum);
            }
        });

        mRecyclerView.setAdapter(mLoadMoreWrapper);
//        mHeaderAndFooterWrapper.notifyDataSetChanged();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefreshing = true;
                mPageNum = 1;
                getData(mPageNum);
            }
        });
        /*mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState ==RecyclerView.SCROLL_STATE_IDLE && lastVisibleItemPosition + 1 ==mAdapter.getItemCount()) {

                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView,dx, dy);
                lastVisibleItemPosition =linearLayoutManager.findLastVisibleItemPosition();
            }
        });*/
    }

    private void getData(int pageNum){
        getBookPresenter().getSwapSkills(pageNum, new CallBack() {
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

    class SkillAdapter extends CommonAdapter<SwapSkillVo> {

        private static final int TYPE_ITEM =0;  //普通Item View
        private static final int TYPE_FOOTER = 1;  //底部FootView

        public SkillAdapter(Context context, int layoutId, List<SwapSkillVo> datas) {
            super(context, layoutId, datas);
        }

        @Override
        protected void convert(ViewHolder holder, SwapSkillVo ssVO, int position) {
            if(StringUtil.isEmpty(ssVO.getHeadIcon())){
                Glide.with(context).load(R.mipmap.ease_default_avatar).asBitmap().into((ImageView) holder.getView(R.id.iv));
            }else if(ssVO.getHeadIcon().contains("http")){
                Glide.with(context).load(ssVO.getHeadIcon()).placeholder(R.mipmap.ease_default_avatar).into((ImageView) holder.getView(R.id.iv));
            }else{
                Glide.with(context).load(GlobalParams.BASE_URL+GlobalParams.AvatarDirName+ssVO.getHeadIcon()).placeholder(R.mipmap.ease_default_avatar).into((ImageView) holder.getView(R.id.iv));
            }
            ((TextView)holder.getView(R.id.tv_title)).setText(ssVO.getSkillTitle());
            ((TextView)holder.getView(R.id.tv_have)).setText(ssVO.getSkillHaveName());
            ((TextView)holder.getView(R.id.tv_want)).setText(ssVO.getSkillWantName());
        }
    }

    private static class MyHandler extends Handler{
        WeakReference<SkillSwapPageOld> mActivity;
        MyHandler(SkillSwapPageOld act){
            this.mActivity = new WeakReference<SkillSwapPageOld>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            SkillSwapPageOld act = mActivity==null?null:mActivity.get();
            if(act == null){
                return;
            }
            act.myHandleMessage(msg);
        }
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
        isRefreshing = false;
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
