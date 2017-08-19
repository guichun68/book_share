package zyzx.linke.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

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
import zyzx.linke.views.MyRecyclerViewWapper;

/**
 * Created by austin on 2017/8/12.
 * Desc: 技能交换选项卡页
 */

public class SkillSwapPageOld2 extends BaseSwapPager {
    private final String TAG = SkillSwapPageOld2.class.getSimpleName();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayoutManager linearLayoutManager;
    private MyRecyclerViewWapper mRecyclerView;
    private SkillAdapter mAdapter;
    private ArrayList<SwapSkillVo> mSwapSkillVos;
    private int mPageNum;
    private final int SUCCESS = 0x47B,FAILURE = 0xB52;
    private boolean isRefreshing = false;
    private boolean canLoadingMore = false;
    private int lastVisibleItemPosition;

    private MyHandler handler = new MyHandler(this);

    private void myHandleMessage(Message msg){
        switch (msg.what){
            case SUCCESS:
                String resp = (String) msg.obj;
                DefindResponseJson drj = new DefindResponseJson(resp);
                if(drj.errorCode == DefindResponseJson.NO_DATA){
                    UIUtil.showToastSafe("未能获取数据");
                    dismissLoading();
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
                        mAdapter.changeMoreStatus(SkillAdapter.LOADING_END);
                        break;
                    case 3:
                        UIUtil.showToastSafe("没有更多了");
                        mAdapter.changeMoreStatus(SkillAdapter.NO_MORE_DATE);
                        dismissLoading();
                        break;
                    default:
                        UIUtil.showToastSafe("未能获取数据");
                        dismissLoading();
                        return;
                }
                break;
            case FAILURE:
                UIUtil.showToastSafe("未能获取数据");
                dismissLoading();
                break;
        }
    }
    private final int SLOP = 5;
    public SkillSwapPageOld2(Context context, int layoutResId) {
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
        /*mSwipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                        .getDisplayMetrics()));*/
        linearLayoutManager =new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        //添加分隔线
//        mRecyclerView.addItemDecoration(new AdvanceDecoration(context, OrientationHelper.VERTICAL));
        mAdapter = new SkillAdapter(mSwapSkillVos);

        mRecyclerView.setAdapter(mAdapter);
//        mHeaderAndFooterWrapper.notifyDataSetChanged();

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
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.i(TAG, "onScrollStateChanged----newState: " + newState);
                //RecyclerView.SCROLL_STATE_DRAGGING==1
                if (newState ==RecyclerView.SCROLL_STATE_IDLE && lastVisibleItemPosition + 1 ==mAdapter.getItemCount()) {
                    if(mRecyclerView.getSlidStatus()==MyRecyclerViewWapper.SLIDE_UP
                            && ((SkillAdapter)mRecyclerView.getAdapter()).load_more_status!= SkillAdapter.NO_MORE_DATE){
                        UIUtil.showTestLog("TTG","LoadingMore");
                        isRefreshing = false;
                        getData(++mPageNum);
                        mAdapter.changeMoreStatus(SkillAdapter.PULLUP_LOAD_MORE);
                    }
                    mRecyclerView.setSlidStatus(MyRecyclerViewWapper.SLIDE_IDLE);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView,dx, dy);
                lastVisibleItemPosition =linearLayoutManager.findLastCompletelyVisibleItemPosition();

                if(dy<0){//向下滑动
                    if(Math.abs(dy)>=SLOP){
                        isRefreshing = true;
                        UIUtil.showTestLog("isRefreshing1",isRefreshing+"");
                    }
                }else{
                    if(dy!=0){
                        //向上滑动
                        isRefreshing = false;
                        UIUtil.showTestLog("isRefreshing2",isRefreshing+"");
                    }
                }
                Log.i(TAG, "-----------onScrolled-----------");
                Log.i(TAG, "dx: " + dx);
                Log.i(TAG, "dy: " + dy);
                Log.i(TAG, "CHECK_SCROLL_UP: " + recyclerView.canScrollVertically(-5));
                Log.i(TAG, "CHECK_SCROLL_DOWN: " + recyclerView.canScrollVertically(5));

            }
        });


        getData(mPageNum=1);
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

    private class SkillAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        //上拉加载更多
        public static final int  PULLUP_LOAD_MORE=0;
        //正在加载中
        public static final int  LOADING_MORE=1;
        //没有加载的时候
        public static final int LOADING_END = 2;
        //没有更多内容了
        public static final int NO_MORE_DATE = 3;

        //上拉加载更多状态-默认为0
        public int load_more_status=0;

        private static final int TYPE_ITEM =0;  //普通Item View
        private static final int TYPE_FOOTER = 1;  //底部FootView
        private List<SwapSkillVo> mDatas;
        private LayoutInflater mInflater;

        SkillAdapter(List<SwapSkillVo> datas){
            this.mDatas = datas;
            mInflater = ((Activity)context).getLayoutInflater();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //进行判断显示类型，来创建返回不同的View
            if(viewType==TYPE_ITEM){
//                View view = View.inflate(context,R.layout.item_skill,parent);
                View view=mInflater.inflate(R.layout.item_skill,parent,false);
                //这边可以做一些属性设置，甚至事件监听绑定
                //view.setBackgroundColor(Color.RED);
                ItemViewHolder itemViewHolder=new ItemViewHolder(view);
                return itemViewHolder;
            }else if(viewType==TYPE_FOOTER){
//                View foot_view=View.inflate(context,R.layout.view_footer,parent);
                View foot_view=mInflater.inflate(R.layout.view_footer,parent,false);
                //这边可以做一些属性设置，甚至事件监听绑定
                //view.setBackgroundColor(Color.RED);
                FootViewHolder footViewHolder=new FootViewHolder(foot_view);
                return footViewHolder;
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if(holder instanceof ItemViewHolder) {
                SwapSkillVo ssVO = mDatas.get(position);
                ((ItemViewHolder)holder).tvTitle.setText(ssVO.getSkillTitle());
                ((ItemViewHolder)holder).tvWant.setText(ssVO.getSkillWantName());
                ((ItemViewHolder)holder).tvHave.setText(ssVO.getSkillHaveName());
                if(StringUtil.isEmpty(ssVO.getHeadIcon())){
                    Glide.with(context).load(R.mipmap.ease_default_avatar).asBitmap().into( ((ItemViewHolder)holder).iv);
                }else if(ssVO.getHeadIcon().contains("http")){
                    Glide.with(context).load(ssVO.getHeadIcon()).placeholder(R.mipmap.ease_default_avatar).into(((ItemViewHolder)holder).iv);
                }else{
                    Glide.with(context).load(GlobalParams.BASE_URL+GlobalParams.AvatarDirName+ssVO.getHeadIcon()).placeholder(R.mipmap.ease_default_avatar).into(((ItemViewHolder)holder).iv);
                }
                holder.itemView.setTag(position);
            }else if(holder instanceof FootViewHolder){
                FootViewHolder footViewHolder=(FootViewHolder)holder;
                switch (load_more_status){
                    case PULLUP_LOAD_MORE:
                        footViewHolder.rlRoot.setVisibility(View.VISIBLE);
                        footViewHolder.rlRoot.setBackgroundColor(ContextCompat.getColor(context,R.color.transparent));
                        footViewHolder.progressBar.setVisibility(View.VISIBLE);
                        footViewHolder.tvTip.setText("加载更多内容");
                        break;
                    case LOADING_MORE:
                        footViewHolder.rlRoot.setVisibility(View.VISIBLE);
                        footViewHolder.progressBar.setVisibility(View.VISIBLE);
                        footViewHolder.tvTip.setText("正在加载...");
                        break;
                    case LOADING_END:
                        footViewHolder.rlRoot.setVisibility(View.GONE);
                        footViewHolder.rlRoot.setBackgroundColor(ContextCompat.getColor(context,R.color.transparent));
                        break;
                    case NO_MORE_DATE:
                        footViewHolder.rlRoot.setVisibility(View.VISIBLE);
                        footViewHolder.progressBar.setVisibility(View.GONE);
                        footViewHolder.rlRoot.setBackgroundColor(ContextCompat.getColor(context,R.color.white));
                        footViewHolder.tvTip.setText("无更多内容");
                        break;

                }
            }
        }

        /**
         * //上拉加载更多
         * STATUS_PULLUP_LOAD_MORE=0;
         * //正在加载中
         * STATUS_LOADING_MORE=1;
         * //加载完成已经没有更多数据了
         * NO_MORE_DATA=2;
         * @param status
         */
        public void changeMoreStatus(int status){
            load_more_status=status;
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return mDatas.size()+1;
        }

        @Override
        public int getItemViewType(int position) {
            // 最后一个item设置为footerView
            if (position + 1 == getItemCount()) {
                return TYPE_FOOTER;
            } else {
                return TYPE_ITEM;
            }
        }
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView iv;
        public TextView tvTitle,tvHave,tvWant;
        public ItemViewHolder(View view){
            super(view);
            iv = (ImageView) view.findViewById(R.id.iv);
            tvTitle = (TextView)view.findViewById(R.id.tv_title);
            tvHave = (TextView)view.findViewById(R.id.tv_have);
            tvWant = (TextView)view.findViewById(R.id.tv_want);
        }
    }
    /**
     * 底部FootView布局
     */
    public static class FootViewHolder extends  RecyclerView.ViewHolder{
        private RelativeLayout rlRoot;
        private ProgressBar progressBar;
        private TextView tvTip;
        public FootViewHolder(View view) {
            super(view);
            rlRoot = (RelativeLayout) view.findViewById(R.id.rl_root);
            tvTip = (TextView) view.findViewById(R.id.tv_tip);
            progressBar =(ProgressBar)view.findViewById(R.id.load_progress);
        }
    }

    private static class MyHandler extends Handler{
        WeakReference<SkillSwapPageOld2> mActivity;
        MyHandler(SkillSwapPageOld2 act){
            this.mActivity = new WeakReference<SkillSwapPageOld2>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            SkillSwapPageOld2 act = mActivity==null?null:mActivity.get();
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
