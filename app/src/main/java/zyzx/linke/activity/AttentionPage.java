package zyzx.linke.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import zyzx.linke.R;
import zyzx.linke.adapter.MyCommonAdapter;
import zyzx.linke.adapter.MyViewHolder;
import zyzx.linke.base.BasePager;
import zyzx.linke.base.GlobalParams;
import zyzx.linke.global.BundleFlag;
import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.DefindResponseJson;
import zyzx.linke.model.bean.ResponseJson;
import zyzx.linke.model.bean.UserVO;
import zyzx.linke.utils.GlideCircleTransform;
import zyzx.linke.utils.StringUtil;
import zyzx.linke.utils.UIUtil;
import zyzx.linke.views.MyRecyclerViewWapper;

/**
 * Created by austin on 2017/8/12.
 * Desc: 我的关注 View （PersonalCenterAct中viewPager的第二个页面）
 */

public class AttentionPage extends BasePager {
    private AttentionAdapter mAdapter;
    private ArrayList<Attention> mAttentions;
    private int mPageNum;
    private final int SUCCESS = 0x47B3,FAILURE = 0xB522;
    private boolean isRefreshing = true;

    public AttentionPage(Context context, int layoutResId) {
        super(context, layoutResId);
    }

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
                        List<JSONObject> datas = drj.data.getItems();
                        ArrayList<Attention> attentions = new ArrayList<>();
                        for(int i=0;i<datas.size();i++){
                            Attention a = new Attention();
                            a.setUserName(datas.get(i).getString("login_name"));
                            a.setHeadIcon(datas.get(i).getString("head_icon"));
                            a.setUid(datas.get(i).getString("relUid"));
                            a.setUserId(datas.get(i).getString("relUserId"));
                            attentions.add(a);
                        }
                        if(isRefreshing){
                            mAttentions.clear();
                            mAttentions.addAll(attentions);
                            mAdapter.setFooterStatus(MyCommonAdapter.Status.STATUS_LOADING_END);
                        }else{
                            if(attentions.isEmpty()){
                                mPageNum--;
                                UIUtil.showToastSafe("没有更多了！");
                                mAdapter.setFooterStatus(MyCommonAdapter.Status.STATUS_NO_MORE_DATE);
                            }else{
                                mAttentions.addAll(attentions);
                                mAdapter.setFooterStatus(MyCommonAdapter.Status.STATUS_LOADING_END);
                            }
                        }
                        dismissProgress();
                        break;
                    case 3:
                        if(!isRefreshing) {
                            UIUtil.showToastSafe("没有更多了");
                        }
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
    public void initView() {
        ((Activity)context).getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if(mAttentions == null){
            mAttentions = new ArrayList<>();
        }
        MyRecyclerViewWapper mRecyclerView;
        mRecyclerView = (MyRecyclerViewWapper) getRootView().findViewById(R.id.recyclerView);
        mAdapter = new AttentionAdapter(context, mAttentions,R.layout.item_user,R.layout.view_footer,R.id.load_progress,R.id.tv_tip);
        mAdapter.setNoMoreDataColorRes(android.R.color.transparent);

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

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView,dx, dy);
            }
        });
        getData(mPageNum=1);
    }

    //刷新
    public void refresh() {
        isRefreshing = true;
        mAttentions.clear();
        getData(mPageNum=1);
    }

    private class AttentionAdapter extends MyCommonAdapter<Attention>{

        private AttentionAdapter( Context context, List<Attention> datas,int itemLayoutResId, int footerLayoutId,int footerProgressResId,int footerTextTipResId) {
            super(context, datas, itemLayoutResId, footerLayoutId,footerProgressResId,footerTextTipResId);
        }

        @Override
        public void convert(MyViewHolder holder, final Attention attention, int position) {
            if(holder.getHolderType()==MyViewHolder.HOLDER_TYPE_NORMAL){
                holder.setText(R.id.tv_user_name,attention.getUserName());
                if(StringUtil.isEmpty(attention.getHeadIcon())){
                    Glide.with(context).load(R.mipmap.person).asBitmap().dontAnimate().transform(new GlideCircleTransform(mContext)).into( (ImageView)holder.getView(R.id.iv));
                }else if(attention.getHeadIcon().contains("http")){
                    Glide.with(context).load(attention.getHeadIcon()).placeholder(R.mipmap.person).dontAnimate().transform(new GlideCircleTransform(mContext)).into((ImageView)holder.getView(R.id.iv));
                }else{
                    Glide.with(context).load(GlobalParams.BASE_URL+GlobalParams.AvatarDirName+attention.getHeadIcon()).placeholder(R.mipmap.person).dontAnimate().transform(new GlideCircleTransform(mContext)).into((ImageView)holder.getView(R.id.iv));
                }
                holder.itemView.setTag(position);

                holder.setOnClickListener(R.id.ll_root, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getUserInfo(attention.getUid());
                    }
                });
            }
        }
    }

    private void getData(int pageNum){
        isRefreshing = true;
        getBookPresenter().getAttentions(pageNum, new CallBack() {
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

    public Handler getHandler(){
        return handler;
    }


    private static class MyHandler extends Handler{
        WeakReference<AttentionPage> mActivity;
        MyHandler(AttentionPage act){
            this.mActivity = new WeakReference<>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            AttentionPage act = mActivity==null?null:mActivity.get();
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

    private class Attention{
        String uid;
        String userId;
        String userName;
        String headIcon;

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        private String getUserName() {
            return userName;
        }

        private void setUserName(String userName) {
            this.userName = userName;
        }

        public String getHeadIcon() {
            return headIcon;
        }

        public void setHeadIcon(String headIcon) {
            this.headIcon = headIcon;
        }
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
                JSONArray ja = rj.data;
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
                Intent in = new Intent(context,FriendHomePageAct.class);
                in.putExtra(BundleFlag.FLAG_USER,mFriend);
                in.putExtra(BundleFlag.IS_IN_FRIENDS_BLACLIST,isInRelsBlackList);
                ((PersonalCenterAct)context).startActivityForResult(in,BundleFlag.FLAG_FRIEND_HOME);
            }

            @Override
            public void onFailure(Object obj, int... code) {
                dismissProgress();
                UIUtil.showToastSafe("用户信息获取失败！");
            }
        });
    }
}
