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
import zyzx.linke.base.GlobalParams;
import zyzx.linke.global.BundleFlag;
import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.DefindResponseJson;
import zyzx.linke.model.bean.SwapSkillVo;
import zyzx.linke.utils.AppUtil;
import zyzx.linke.utils.GlideCircleTransform;
import zyzx.linke.utils.StringUtil;
import zyzx.linke.utils.UIUtil;
import zyzx.linke.views.MyRecyclerViewWapper;

/**
 * Created by Austin on 2017-08-27.
 * Desc: 图书交换搜索结果页面
 */

public class SwapSkillSearchResultAct extends BaseActivity {
    private MyRecyclerViewWapper mRecyclerView;
    private SkillAdapter mAdapter;
    private ArrayList<SwapSkillVo> mSwapSkills;
    private int mPageNum;
    private final int SUCCESS = 0x47B2,FAILURE = 0xB52A;
    private boolean isRefreshing = false;

    private String keyWord;
    private TextView tvSearchWant;
    private boolean isNormalSearch = true;//是否是从已经拥有的集合中搜索

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
                        List<SwapSkillVo> swapBookVOs = AppUtil.getSwapSkills(drj.data.getItems());
                        if(isRefreshing){
                            mSwapSkills.clear();
                            mSwapSkills.addAll(swapBookVOs);
                        }else{
                            if(swapBookVOs.isEmpty()){
                                mPageNum--;
                                UIUtil.showToastSafe("没有更多了！");
                            }else{
                                mSwapSkills.addAll(swapBookVOs);
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

        if(mSwapSkills == null){
            mSwapSkills = new ArrayList<>();
        }
        mRecyclerView = (MyRecyclerViewWapper) findViewById(R.id.recyclerView);
        tvSearchWant = (TextView) findViewById(R.id.tv_search_want);
        tvSearchWant.setOnClickListener(this);
        mAdapter = new SkillAdapter(this, mSwapSkills,R.layout.item_skill,R.layout.view_footer,R.id.load_progress,R.id.tv_tip);

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

    }


    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.tv_search_want:
                showDefProgress();
                //从所有用户想要 的书中搜索同样的关键字
                mSwapSkills.clear();
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
     * @param isNormalSearch true:从拥有中搜索；false：从想要交换中搜索
     * @param pageNum
     */
    private void getData(boolean isNormalSearch,int pageNum){
        this.isNormalSearch = isNormalSearch;
        if(isNormalSearch){
            getBookPresenter().searchSwapSkills(keyWord,pageNum, new CallBack() {
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
            getBookPresenter().searchSwapWantSkills(keyWord,pageNum, new CallBack() {
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
        WeakReference<SwapSkillSearchResultAct> mActivity;
        MyHandler(SwapSkillSearchResultAct act){
            this.mActivity = new WeakReference<>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            SwapSkillSearchResultAct act = mActivity==null?null:mActivity.get();
            if(act == null){
                return;
            }
            act.myHandleMessage(msg);
        }
    }

    private class SkillAdapter extends MyCommonAdapter<SwapSkillVo>{

        private SkillAdapter( Context context, List<SwapSkillVo> datas,int itemLayoutResId, int footerLayoutId,int footerProgressResId,int footerTextTipResId) {
            super(context, datas, itemLayoutResId, footerLayoutId,footerProgressResId,footerTextTipResId);
        }

        @Override
        public void convert(MyViewHolder holder, final SwapSkillVo ssVO, int position) {
            if(holder.getHolderType()==MyViewHolder.HOLDER_TYPE_NORMAL){
                holder.setText(R.id.tv_title,ssVO.getSkillTitle());
                holder.setText(R.id.tv_want,StringUtil.isEmpty(ssVO.getSkillWantName())?"现金交换":ssVO.getSkillWantName());
                holder.setText(R.id.tv_have,ssVO.getSkillHaveName());
                if(StringUtil.isEmpty(ssVO.getHeadIcon())){
                    Glide.with(SwapSkillSearchResultAct.this).load(R.mipmap.ease_default_avatar).asBitmap().transform(new GlideCircleTransform(mContext)).into( (ImageView)holder.getView(R.id.iv));
                }else if(ssVO.getHeadIcon().contains("http")){
                    Glide.with(SwapSkillSearchResultAct.this).load(ssVO.getHeadIcon()).placeholder(R.mipmap.ease_default_avatar).transform(new GlideCircleTransform(mContext)).into((ImageView)holder.getView(R.id.iv));
                }else{
                    Glide.with(SwapSkillSearchResultAct.this).load(GlobalParams.BASE_URL+GlobalParams.AvatarDirName+ssVO.getHeadIcon()).placeholder(R.mipmap.ease_default_avatar).transform(new GlideCircleTransform(mContext)).into((ImageView)holder.getView(R.id.iv));
                }
                holder.itemView.setTag(position);

                holder.setOnClickListener(R.id.ll_root, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent in = new Intent(SwapSkillSearchResultAct.this,SkillDeatilAct.class);
                        in.putExtra(BundleFlag.FLAG_SKILL_SWAP,ssVO);
                        SwapSkillSearchResultAct.this.startActivity(in);
                    }
                });
            }
        }
    }
}
