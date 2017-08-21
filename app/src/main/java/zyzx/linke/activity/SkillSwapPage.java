package zyzx.linke.activity;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import zyzx.linke.R;
import zyzx.linke.adapter.MyCommonAdapter;
import zyzx.linke.adapter.MyViewHolder;
import zyzx.linke.base.BaseSwapPager;
import zyzx.linke.base.GlobalParams;
import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.DefindResponseJson;
import zyzx.linke.model.bean.SwapSkillVo;
import zyzx.linke.utils.AppUtil;
import zyzx.linke.utils.StringUtil;
import zyzx.linke.utils.UIUtil;
import zyzx.linke.views.MyRecyclerViewWapper;

/**
 * Created by austin on 2017/8/12.
 * Desc: 技能交换选项卡页
 */

public class SkillSwapPage extends BaseSwapPager {
    private final String TAG = SkillSwapPage.class.getSimpleName();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayoutManager linearLayoutManager;
    private MyRecyclerViewWapper mRecyclerView;
    private SkillAdapter mAdapter;
    private ArrayList<SwapSkillVo> mSwapSkillVos;
    private int mPageNum;
    private final int SUCCESS = 0x47B,FAILURE = 0xB52;
    private boolean isRefreshing = false;
    private boolean canLoadingMore = false;

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
    private final int SLOP = 5;
    public SkillSwapPage(Context context, int layoutResId) {
        super(context, layoutResId);
    }
    @Override
    public void initView() {
        if(mSwapSkillVos == null){
            mSwapSkillVos = new ArrayList<>();
        }
        mSwipeRefreshLayout = (SwipeRefreshLayout) getRootView().findViewById(R.id.swipeRefreshLayout);
        mRecyclerView = (MyRecyclerViewWapper) getRootView().findViewById(R.id.recyclerView);

        mSwipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.white);
        //设置刷新时动画的颜色，可以设置4个
        mSwipeRefreshLayout.setColorSchemeResources(R.color.title,
                android.R.color.holo_red_light,android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        mAdapter = new SkillAdapter(context,mSwapSkillVos,R.layout.item_skill,R.layout.view_footer,R.id.load_progress,R.id.tv_tip);

        mRecyclerView.setAdapter(mAdapter);

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
            public void onScrollStateChanged(MyRecyclerViewWapper recyclerView, int newState,boolean isLoadMore) {
                if(isLoadMore){
                    isRefreshing = false;
                    getData(++mPageNum);
                    mAdapter.setFooterStatus(SkillAdapter.Status.STATUS_PULLUP_LOAD_MORE);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView,dx, dy);
            }
        });
        getData(mPageNum=1);
    }

    private class SkillAdapter extends MyCommonAdapter<SwapSkillVo>{

        private SkillAdapter( Context context, List<SwapSkillVo> datas,int itemLayoutResId, int footerLayoutId,int footerProgressResId,int footerTextTipResId) {
            super(context, datas, itemLayoutResId, footerLayoutId,footerProgressResId,footerTextTipResId);
        }

        @Override
        public void convert(MyViewHolder holder, SwapSkillVo ssVO,int position) {
            if(holder.getHolderType()==MyViewHolder.HOLDER_TYPE_NORMAL){
                holder.setText(R.id.tv_title,ssVO.getSkillTitle());
                holder.setText(R.id.tv_want,ssVO.getSkillWantName());
                holder.setText(R.id.tv_have,ssVO.getSkillHaveName());
                if(StringUtil.isEmpty(ssVO.getHeadIcon())){
                    Glide.with(context).load(R.mipmap.ease_default_avatar).asBitmap().into( (ImageView)holder.getView(R.id.iv));
                }else if(ssVO.getHeadIcon().contains("http")){
                    Glide.with(context).load(ssVO.getHeadIcon()).placeholder(R.mipmap.ease_default_avatar).into((ImageView)holder.getView(R.id.iv));
                }else{
                    Glide.with(context).load(GlobalParams.BASE_URL+GlobalParams.AvatarDirName+ssVO.getHeadIcon()).placeholder(R.mipmap.ease_default_avatar).into((ImageView)holder.getView(R.id.iv));
                }
                holder.itemView.setTag(position);
            }
        }
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


    private static class MyHandler extends Handler{
        WeakReference<SkillSwapPage> mActivity;
        MyHandler(SkillSwapPage act){
            this.mActivity = new WeakReference<SkillSwapPage>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            SkillSwapPage act = mActivity==null?null:mActivity.get();
            if(act == null){
                return;
            }
            act.myHandleMessage(msg);
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
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
