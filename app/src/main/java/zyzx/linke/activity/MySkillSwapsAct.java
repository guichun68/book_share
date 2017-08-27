package zyzx.linke.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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
import zyzx.linke.model.bean.ResponseJson;
import zyzx.linke.model.bean.SwapSkillVo;
import zyzx.linke.utils.AppUtil;
import zyzx.linke.utils.CustomProgressDialog;
import zyzx.linke.utils.GlideCircleTransform;
import zyzx.linke.utils.StringUtil;
import zyzx.linke.utils.UIUtil;
import zyzx.linke.views.MyRecyclerViewWapper;

/**
 * Created by Austin on 2017-08-25.
 * Desc: 我发布的所有技能交换页
 */

public class MySkillSwapsAct extends BaseActivity {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private MyRecyclerViewWapper mRecyclerView;
    private MySkillsAdapter mAdapter;


    private Dialog mPromptDialog;
    private PopupWindow pop;
    private int mWindowWidth;
    private int mPageNum = 1;
    private boolean isRefreshing;
    private ArrayList<SwapSkillVo> mSwapSkillVos;
    private final int SUCCESS = 0x47B,FAILURE = 0xB52,SUCCESS_DELET = 0x40407,FAILURE_DELETE= 0x445B;


    private MyHandler handler = new MyHandler(this);

    private void myHandleMessage(Message msg){
        dismissProgress();
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
            case SUCCESS_DELET:
                if (pop != null && pop.isShowing()) {
                    pop.dismiss();
                }
                UIUtil.showToastSafe("删除成功");
                mSwapSkillVos.remove(msg.obj);
                mAdapter.notifyItemChanged(msg.arg1);
//                mAdapter.notifyDataSetChanged();
                break;
            case FAILURE_DELETE:
                UIUtil.showToastSafe("未能成功删除");
                break;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_my_swap_skills;
    }

    @Override
    protected void initView(Bundle saveInstanceState) {
        mTitleText.setText("我的技能交换");

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mWindowWidth = size.x;

        if(mSwapSkillVos == null){
            mSwapSkillVos = new ArrayList<>();
        }
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mRecyclerView = (MyRecyclerViewWapper) findViewById(R.id.recyclerView);

        mSwipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.white);
        //设置刷新时动画的颜色，可以设置4个
        mSwipeRefreshLayout.setColorSchemeResources(R.color.title,
                android.R.color.holo_red_light,android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        mAdapter = new MySkillsAdapter(this,mSwapSkillVos,R.layout.item_skill,R.layout.view_footer,R.id.load_progress,R.id.tv_tip);

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
                    mAdapter.setFooterStatus(MyCommonAdapter.Status.STATUS_PULLUP_LOAD_MORE);
                }
            }
        });
        getData(mPageNum=1);
    }

    private void getData(int pageNum){
        getBookPresenter().getMySwapSkills(pageNum, new CallBack() {
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

    }

    private class MySkillsAdapter extends MyCommonAdapter<SwapSkillVo> {

        public MySkillsAdapter(Context context, List<SwapSkillVo> datas, int itemLayoutResId, int footerLayoutId, int footerProgressResId, int footerTextTipResId) {
            super(context, datas, itemLayoutResId, footerLayoutId, footerProgressResId, footerTextTipResId);
        }

        @Override
        public void convert(MyViewHolder holder, final SwapSkillVo ssVO, final int position) {
            super.convert(holder, ssVO, position);
            if(holder.getHolderType()==MyViewHolder.HOLDER_TYPE_NORMAL){
                holder.setText(R.id.tv_title,ssVO.getSkillTitle());
                holder.setText(R.id.tv_want,ssVO.getSkillWantName());
                holder.setText(R.id.tv_have,ssVO.getSkillHaveName());
                if(StringUtil.isEmpty(ssVO.getHeadIcon())){
                    Glide.with(MySkillSwapsAct.this).load(R.mipmap.ease_default_avatar).asBitmap().transform(new GlideCircleTransform(mContext)).into( (ImageView)holder.getView(R.id.iv));
                }else if(ssVO.getHeadIcon().contains("http")){
                    Glide.with(MySkillSwapsAct.this).load(ssVO.getHeadIcon()).placeholder(R.mipmap.ease_default_avatar).transform(new GlideCircleTransform(mContext)).into((ImageView)holder.getView(R.id.iv));
                }else{
                    Glide.with(MySkillSwapsAct.this).load(GlobalParams.BASE_URL+GlobalParams.AvatarDirName+ssVO.getHeadIcon()).placeholder(R.mipmap.ease_default_avatar).transform(new GlideCircleTransform(mContext)).into((ImageView)holder.getView(R.id.iv));
                }
                holder.itemView.setTag(position);

                holder.setOnClickListener(R.id.ll_root, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent in = new Intent(MySkillSwapsAct.this,SkillDeatilAct.class);
                        in.putExtra(BundleFlag.FLAG_SKILL_SWAP,ssVO);
                        MySkillSwapsAct.this.startActivity(in);
                    }
                });
                holder.setOnLongClickListener(R.id.ll_root, new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        View popView = View.inflate(mContext, R.layout.pop_modify_book, null);
                        setPopwinViewControls(popView, ssVO, position);
                        //测量布局的大小
                        popView.measure(0, 0);
                        int popWidth = popView.getMeasuredWidth();
                        int popHeight = popView.getMeasuredHeight();
                        pop = new PopupWindow(popView);
                        // 加上这个popupwindow中的ListView才可以接收点击事件
                        pop.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
                        pop.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);

                        pop.setOutsideTouchable(true);
                        pop.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        pop.setContentView(popView);
                        int[] location = new int[2];
                        v.getLocationInWindow(location);
                        if(popHeight<v.getMeasuredHeight()){
                            pop.showAtLocation(v, Gravity.TOP + Gravity.START, mWindowWidth / 2 - popWidth / 2, location[1] + UIUtil.dip2px(10));
                        }else{
                            pop.showAtLocation(v, Gravity.TOP + Gravity.START, mWindowWidth / 2 - popWidth / 2, location[1] -((popHeight-v.getMeasuredHeight())/2));
                        }
                        AlphaAnimation aa = new AlphaAnimation(0.2f, 1.0f);
                        aa.setDuration(100);
                        ScaleAnimation sa = new ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0.5f);
                        sa.setDuration(100);

                        AnimationSet set = new AnimationSet(false);
                        set.addAnimation(aa);
                        set.addAnimation(sa);
                        popView.startAnimation(set);
                        return true;
                    }
                });
            }
        }
    }

        /**
         * 初始化并设置popupWin中的控件
         *
         * @param popView      popWindow View
         * @param swapSkillVo book detail bean
         */
        private void setPopwinViewControls(final View popView, final SwapSkillVo swapSkillVo, final int position) {

            final TextView item1 = (TextView) popView.findViewById(R.id.tv_item1);//删除
            final TextView item2 = (TextView) popView.findViewById(R.id.tv_item2);//分享
            final TextView item3 = (TextView) popView.findViewById(R.id.tv_item3);//交换
            popView.findViewById(R.id.line1).setVisibility(View.GONE);
            popView.findViewById(R.id.line2).setVisibility(View.GONE);
            item1.setText("删除此交换记录");
            item2.setVisibility(View.GONE);
            item3.setVisibility(View.GONE);
            item1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pop.dismiss();
                        mPromptDialog = CustomProgressDialog.getPromptDialog2Btn(MySkillSwapsAct.this, "确定要删除\"" + swapSkillVo.getSkillTitle() + "\"这条记录么?", "确定", "保留",
                                new PopItemClickListener(swapSkillVo, position, PopItemClickListener.DELETE), null);
                        mPromptDialog.show();
                    }
                });
        }

    private class PopItemClickListener implements View.OnClickListener {
        private int operId;
        private SwapSkillVo swapSkillVo;
        private static final int DELETE = 0;//从书架删除（无论之前什么状态）
        private static final int CANCEL_SHARE = 1;//取消分享
        private static final int CANCEL_SWAP_BOOK = 2;//取消图书交换
        private int position;//在listView中的位置索引

        /**
         * @param bookDetailVO 操作的书籍
         * @param operId       操作id
         */
        PopItemClickListener(SwapSkillVo bookDetailVO, int position, Integer operId) {
            this.operId = operId;
            this.swapSkillVo = bookDetailVO;
            this.position = position;
        }

        @Override
        public void onClick(final View v) {
            CustomProgressDialog.dismissDialog(mPromptDialog);
            switch (operId) {
                case DELETE://删除
                    showDefProgress();
                    getUserPresenter().deleteSwapSkill(swapSkillVo.getSwapSkillId(), new CallBack() {

                        @Override
                        public void onSuccess(final Object obj, int... code) {
                            ResponseJson rj = new ResponseJson((String)obj);
                            Message msg = handler.obtainMessage();
                            if (rj.errorCode != ResponseJson.NO_DATA) {
                                switch (rj.errorCode) {
                                    case 2:
                                        msg.what = SUCCESS_DELET;
                                        msg.obj = swapSkillVo;
                                        msg.arg1 = position;
                                        break;
                                    default:
                                        msg.what = FAILURE_DELETE;
                                        msg.obj = obj;
                                        break;
                                }
                            }else {
                                msg.what = FAILURE_DELETE;
                                msg.obj = obj;
                            }
                            handler.sendMessage(msg);
                        }

                        @Override
                        public void onFailure(Object obj, int... code) {
                            Message msg = handler.obtainMessage();
                            msg.what = FAILURE_DELETE;
                            msg.obj = obj;
                            handler.sendMessage(msg);
                        }
                    });
            }
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


    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }


    private static class MyHandler extends Handler {
        WeakReference<MySkillSwapsAct> mActivity;
        MyHandler(MySkillSwapsAct act){
            this.mActivity = new WeakReference<>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            MySkillSwapsAct act = mActivity==null?null:mActivity.get();
            if(act == null){
                return;
            }
            act.myHandleMessage(msg);
        }
    }
}







