package zyzx.linke.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.amap.api.services.cloud.CloudItem;
import com.bumptech.glide.Glide;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hyphenate.chat.EMClient;

import java.util.ArrayList;
import java.util.List;

import zyzx.linke.R;
import zyzx.linke.adapter.BookAdapter;
import zyzx.linke.base.BaseActivity;
import zyzx.linke.base.GlobalParams;
import zyzx.linke.global.BundleFlag;
import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.BookDetail2;
import zyzx.linke.model.bean.UserVO;
import zyzx.linke.utils.StringUtil;
import zyzx.linke.utils.UIUtil;
import zyzx.linke.views.CircleImageView;

/**
 * Created by austin on 2017/3/4.
 * Desc: 好友主页（显示好友信息并展示其在地图中分享的书籍,即其在zyzx_user_books表中book_status=2的所有书籍）
 */

public class FriendHomePageAct extends BaseActivity implements PullToRefreshBase.OnRefreshListener<ListView> {

    private CircleImageView ivHeadIcon;
    private TextView tvLoginname;
    private TextView tvLocation;
    private TextView tvSignature;
    private boolean headerClickable;

    private PullToRefreshListView mPullRefreshListView;

    private BookAdapter mAdapter;
    private CloudItem mCloudItem;
    private int pageNum = 0;
    private String mAddress;//中文地址描述
    private UserVO mUserVO;
    private ArrayList<BookDetail2> mBooks = new ArrayList<>();
    private boolean showAddress;//是否显示地理位置信息
    private RelativeLayout rlAddress;

    @Override
    protected int getLayoutId() {
        return R.layout.act_friend_page;
    }

    @Override
    protected void initView(Bundle saveInstanceState) {
        mTitleText.setText("好友主页");
        ivHeadIcon = (CircleImageView) findViewById(R.id.iv_icon);
        tvLoginname = (TextView) findViewById(R.id.tv_loginname);
        tvSignature = (TextView) findViewById(R.id.tv_signature);
        tvLocation = (TextView) findViewById(R.id.detail_locaiotn_des);
        mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
        rlAddress = (RelativeLayout) findViewById(R.id.rl_location);
        rlAddress.setOnClickListener(this);

        if(showAddress){
            rlAddress.setVisibility(View.VISIBLE);
        }else{
            rlAddress.setVisibility(View.GONE);
        }
        mPullRefreshListView.setOnRefreshListener(this);
        mPullRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        // Add an end-of-list listener
        ListView actualListView = mPullRefreshListView.getRefreshableView();
        // Need to use the Actual ListView when registering for Context Menu
        registerForContextMenu(actualListView);
        mAdapter = new BookAdapter(this,mBooks);
        // You can also just use setListAdapter(mAdapter) or
        // mPullRefreshListView.setAdapter(mAdapter)
        actualListView.setAdapter(mAdapter);
//        mPullRefreshListView.setOnScrollListener(this);
    }

    @Override
    protected void initData() {
        getIntentData();
        if(headerClickable){
            ivHeadIcon.setOnClickListener(this);
        }
        if(mCloudItem!=null){
            tvLocation.setText(mAddress);
        }
        getUserPresenter().getUserInfo(mCloudItem.getCustomfield().get("uid"),new CallBack(){
            @Override
            public void onSuccess(Object obj) {
                String userJson = (String) obj;
                mUserVO = JSON.parseObject(userJson,UserVO.class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvLoginname.setText(mUserVO !=null? mUserVO.getLogin_name():"用户不存在");
                        if(mUserVO !=null){
                            if(!StringUtil.isEmpty(mUserVO.getSignature())){
                                tvSignature.setText(mUserVO.getSignature());
                            }else{
                                tvSignature.setText(UIUtil.getString(R.string.nowordsig));
                            }
                        }
                        if(!StringUtil.isEmpty(mUserVO.getHead_icon())){
                            Glide.with(mContext).load(mUserVO.getHead_icon()).into(ivHeadIcon);
                        }else{
                            Glide.with(mContext).load(R.mipmap.person).asBitmap().into(ivHeadIcon) ;
                        }
                    }
                });
            }

            @Override
            public void onFailure(Object obj) {
                UIUtil.showToastSafe("未能获取用户信息");
            }
        });
        getBooks(mCloudItem.getCustomfield().get("uid"),0);
    }


    private void getIntentData() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        mCloudItem = intent.getParcelableExtra(BundleFlag.CLOUD_ITEM);
        showAddress = intent.getBooleanExtra(BundleFlag.SHOWADDRESS,true);
        headerClickable = intent.getBooleanExtra(BundleFlag.HEADCLICKABLE,true);
        if(mCloudItem!=null){
            mAddress = mCloudItem.getSnippet();
            if(StringUtil.isEmpty(mAddress)){
                mAddress = intent.getStringExtra(BundleFlag.ADDRESS);
            }
        }
    }


    public void getBooks(String uid,int pageNum){
        getBookPresenter().getUserBooks(uid, pageNum, new CallBack() {
            @Override
            public void onSuccess(final Object obj) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mPullRefreshListView.onRefreshComplete();
                        mPullRefreshListView.clearAnimation();
                        if(obj !=null){
                            if(((List)obj).size()==0){
                                UIUtil.showToastSafe("没有更多书籍了！");
                            }else{
                                mBooks.addAll((List<BookDetail2>) obj);
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });
            }

            @Override
            public void onFailure(Object obj) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mPullRefreshListView.onRefreshComplete();
                        mPullRefreshListView.clearAnimation();
                        UIUtil.showToastSafe("未能获取书籍信息");
                    }
                });

            }
        });
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.rl_location://导航用户所处位置
                Intent intent = new Intent(this, RouteMapActivity.class);
                intent.putExtra(BundleFlag.CLOUD_ITEM, mCloudItem);
                this.startActivity(intent);
            break;
            case R.id.iv_icon:
                if(mUserVO !=null&& mUserVO.getUserid()!=null){
                    if(String.valueOf(mUserVO.getUserid()).equals(EMClient.getInstance().getCurrentUser())){
                        UIUtil.showToastSafe(R.string.neednot_chat_myself);
                        return;
                    }
                    showProgress("正在添加好友…");
                    //检查该用户是否已被对方加入黑名单
                    getUserPresenter().addFriend(mUserVO.getUserid(),new CallBack(){

                        @Override
                        public void onSuccess(Object obj) {
                            dismissProgress();
                            String json = (String) obj;
                            if(StringUtil.isEmpty(json)){
                                UIUtil.showToastSafe("请求出错,请稍后再试！");
                                return;
                            }
                            JSONObject jsonObj = JSON.parseObject(json);
                            int code = jsonObj.getInteger("code");
                            if(code==500){
                                //在对方的黑名单中
                                GlobalParams.shouldRefreshContactList = true;
                                UIUtil.showToastSafe(R.string.error_chat);
                                return;
                            }else if(code ==200){
                                //添加成功
                                UIUtil.showToastSafe("已添加好友"+ mUserVO.getLogin_name());
                                GlobalParams.shouldRefreshContactList = true;
                                Intent in = new Intent(FriendHomePageAct.this,ChatActivity.class);
                                in.putExtra(BundleFlag.UID,String.valueOf(mUserVO.getUserid()));
                                in.putExtra(BundleFlag.LOGIN_NAME, mUserVO.getLogin_name());
                                startActivity(in);
                            }else{
                                UIUtil.showToastSafe("添加好友失败！");
                            }
                        }

                        @Override
                        public void onFailure(Object obj) {
                            dismissProgress();
                            UIUtil.showToastSafe("请求出错,请稍后再试！");
                        }
                    });


                }else{
                    UIUtil.showToastSafe("未能获取用户信息");
                }
                break;
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        ILoadingLayout endLabels = mPullRefreshListView.getLoadingLayoutProxy(
                false, true);
        endLabels.setPullLabel(getResources().getString(R.string.pull_label));
        endLabels.setRefreshingLabel(getResources().getString(
                R.string.refresh_label));
        endLabels.setReleaseLabel(getResources().getString(
                R.string.release_label));
        endLabels.setLoadingDrawable(getResources().getDrawable(
                R.mipmap.publicloading));
        pageNum++;
        getBooks(mCloudItem.getCustomfield().get("uid"),pageNum);
    }
}
