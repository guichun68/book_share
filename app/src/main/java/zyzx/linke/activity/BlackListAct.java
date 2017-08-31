package zyzx.linke.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
import zyzx.linke.model.bean.UserSimple;
import zyzx.linke.model.bean.UserVO;
import zyzx.linke.utils.AppUtil;
import zyzx.linke.utils.GlideCircleTransform;
import zyzx.linke.utils.StringUtil;
import zyzx.linke.utils.UIUtil;
import zyzx.linke.views.MyRecyclerViewWapper;

/**
 * Created by Austin on 2017-08-31.
 * Desc: 我的黑名单列表页
 */


public class BlackListAct extends BaseActivity {

    private MyRecyclerViewWapper mRecyclerView;
    private BlackListAdapter mAdapter;
    private ArrayList<UserSimple> mBlacks;
    private int mPageNum;
    private final int SUCCESS = 0x47B,FAILURE = 0xB52;
    private boolean isRefreshing = true;
    private PopupWindow pop;


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
                        ArrayList<UserSimple> tempBlackList = AppUtil.getBlackLists(drj.data.getItems());
                        if(isRefreshing){
                            mBlacks.clear();
                            mBlacks.addAll(tempBlackList);
                            mAdapter.setFooterStatus(MyCommonAdapter.Status.STATUS_LOADING_END);
                        }else{
                            if(tempBlackList.isEmpty()){
                                mPageNum--;
                                UIUtil.showToastSafe("没有更多了！");
                                mAdapter.setFooterStatus(MyCommonAdapter.Status.STATUS_NO_MORE_DATE);
                            }else{
                                mBlacks.addAll(tempBlackList);
                                mAdapter.setFooterStatus(MyCommonAdapter.Status.STATUS_LOADING_END);
                            }
                        }
                        dismissProgress();

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

    @Override
    protected int getLayoutId() {
        return R.layout.act_black_list;
    }

    @Override
    protected void initView(Bundle saveInstanceState) {
        mTitleText.setText("我的黑名单");
        if(mBlacks == null){
            mBlacks = new ArrayList<>();
        }

        mRecyclerView = (MyRecyclerViewWapper) findViewById(R.id.recyclerView);
        mAdapter = new BlackListAdapter(this, mBlacks,R.layout.item_user,R.layout.view_footer,R.id.load_progress,R.id.tv_tip);

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
        });
        getData(mPageNum=1);
    }

    @Override
    protected void initData() {

    }

    private class BlackListAdapter extends MyCommonAdapter<UserSimple>{

        private BlackListAdapter( Context context, List<UserSimple> datas,int itemLayoutResId, int footerLayoutId,int footerProgressResId,int footerTextTipResId) {
            super(context, datas, itemLayoutResId, footerLayoutId,footerProgressResId,footerTextTipResId);
        }

        @Override
        public void convert(MyViewHolder holder, final UserSimple userSimple, int position) {
            if(holder.getHolderType()==MyViewHolder.HOLDER_TYPE_NORMAL){
                holder.setText(R.id.tv_user_name,userSimple.getUserName());
                if(StringUtil.isEmpty(userSimple.getHeadIcon())){
                    Glide.with(mContext).load(R.mipmap.person).asBitmap().dontAnimate().transform(new GlideCircleTransform(mContext)).into( (ImageView)holder.getView(R.id.iv));
                }else if(userSimple.getHeadIcon().contains("http")){
                    Glide.with(mContext).load(userSimple.getHeadIcon()).placeholder(R.mipmap.person).dontAnimate().transform(new GlideCircleTransform(mContext)).into((ImageView)holder.getView(R.id.iv));
                }else{
                    Glide.with(mContext).load(GlobalParams.BASE_URL+GlobalParams.AvatarDirName+userSimple.getHeadIcon()).placeholder(R.mipmap.person).dontAnimate().transform(new GlideCircleTransform(mContext)).into((ImageView)holder.getView(R.id.iv));
                }
                holder.itemView.setTag(position);

                holder.setOnClickListener(R.id.ll_root, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getUserInfo(userSimple.getUid());
                    }
                });
                holder.setOnLongClickListener(R.id.ll_root, new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        View popView = View.inflate(mContext, R.layout.pop_modify_book, null);

                        /*setPopwinViewControls(popView, bookDetailVO, position);
                        //测量布局的大小
                        popView.measure(0, 0);view.getMeasuredHeight();
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
                        view.getLocationInWindow(location);
                        if(popHeight<view.getMeasuredHeight()){
                            pop.showAtLocation(view, Gravity.TOP + Gravity.START, mWindowWidth / 2 - popWidth / 2, location[1] + UIUtil.dip2px(10));
                        }else{
                            pop.showAtLocation(view, Gravity.TOP + Gravity.START, mWindowWidth / 2 - popWidth / 2, location[1] -((popHeight-view.getMeasuredHeight())/2));
                        }
                        AlphaAnimation aa = new AlphaAnimation(0.2f, 1.0f);
                        aa.setDuration(100);
                        ScaleAnimation sa = new ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0.5f);
                        sa.setDuration(100);

                        AnimationSet set = new AnimationSet(false);
                        set.addAnimation(aa);
                        set.addAnimation(sa);
                        popView.startAnimation(set);*/
                        return true;
                    }
                });
            }
        }
    }

    private void getData(int pageNum){
        getBookPresenter().getMyBlackList(pageNum, new CallBack() {
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


    private static class MyHandler extends Handler {
        WeakReference<BlackListAct> mActivity;
        MyHandler(BlackListAct act){
            this.mActivity = new WeakReference<>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            BlackListAct act = mActivity==null?null:mActivity.get();
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
    }


    private UserVO mFriend = new UserVO();
    //获得用户名并跳转到用户详情页
    private void getUserInfo(String uid){
        showDefProgress();
        getUserPresenter().getUserInfoByUid2(uid, new CallBack() {
            @Override
            public void onSuccess(Object obj, int... code) {
                dismissProgress();
                ResponseJson rj = new ResponseJson((String) obj);
                if(ResponseJson.NO_DATA == rj.errorCode || rj.errorCode!=2){
                    UIUtil.showToastSafe("用户信息获取失败！");
                    return;
                }
                JSONArray ja =  rj.data;
                JSONObject jo = (JSONObject) ja.get(0);
                boolean isInRelsBlackList = ((JSONObject)ja.get(1)).getBoolean("isInRelsBlackList");
                mFriend.setUserid(jo.getInteger("userid"));
                mFriend.setUid(jo.getString("id"));
                mFriend.setLoginName(jo.getString("login_name"));
                mFriend.setMobilePhone(jo.getString("mobile_phone"));
                mFriend.setAddress(jo.getString("address"));
                mFriend.setPassword(jo.getString("password"));
                mFriend.setProvinceName(jo.getString("pro"));
                mFriend.setCityName(jo.getString("city"));
                mFriend.setCountyName(jo.getString("county"));
                String genderStr = jo.getString("gender");
                Integer gender = Integer.parseInt(genderStr==null?"0":genderStr);
                mFriend.setGender(gender);
                mFriend.setHobby(jo.getString("hobby"));
                mFriend.setEmail(jo.getString("email"));
                mFriend.setRealName(jo.getString("real_name"));
                mFriend.setCityId(jo.getInteger("city_id"));
                mFriend.setLastLoginTime(jo.getString("last_login_time"));

                mFriend.setSignature(jo.getString("signature"));
                String headTemp = jo.getString("head_icon");
                mFriend.setHeadIcon(StringUtil.isEmpty(headTemp)?null:GlobalParams.BASE_URL+GlobalParams.AvatarDirName+headTemp);
                mFriend.setBak4(jo.getString("bak4"));
                mFriend.setBirthday(jo.getDate("birthday"));
                mFriend.setSchool(jo.getString("school"));
                mFriend.setDepartment(jo.getString("department"));
                mFriend.setDiplomaId(jo.getInteger("diploma_id"));
                mFriend.setSoliloquy(jo.getString("soliloquy"));
                mFriend.setCreditScore(jo.getInteger("credit_score"));
                mFriend.setFromSystem(jo.getInteger("from_system"));
                Intent in = new Intent(mContext,FriendHomePageAct.class);
                in.putExtra(BundleFlag.FLAG_USER,mFriend);
                in.putExtra(BundleFlag.IS_IN_FRIENDS_BLACLIST,isInRelsBlackList);
                BlackListAct.this.startActivity(in);
            }

            @Override
            public void onFailure(Object obj, int... code) {
                dismissProgress();
                UIUtil.showToastSafe("未能获取用户信息！");
            }
        });
    }

}
