package zyzx.linke.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.amap.api.services.cloud.CloudItem;
import com.bumptech.glide.Glide;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

import zyzx.linke.R;
import zyzx.linke.adapter.BookAdapter;
import zyzx.linke.constant.BundleFlag;
import zyzx.linke.constant.GlobalParams;
import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.BookDetail2;
import zyzx.linke.model.bean.User;
import zyzx.linke.utils.StringUtil;
import zyzx.linke.utils.UIUtil;
import zyzx.linke.views.CircleImageView;

/**
 * Created by austin on 2017/3/4.
 * Desc: 好友主页
 */

public class FriendHomePageAct extends BaseActivity implements PullToRefreshBase.OnRefreshListener<ListView>,PullToRefreshBase.OnLastItemVisibleListener, AbsListView.OnScrollListener {

    private RelativeLayout rlLocation;
    private CircleImageView ivHeadIcon;
    private TextView tvLoginname;
    private TextView tvLocation;
    private TextView tvSignature;

    private PullToRefreshListView mPullRefreshListView;
    private int mFirstVisibleItem;
    private int mVisibleItemCount;

    private BookAdapter mAdapter;
    private CloudItem mCloudItem;
    private String headIconUrl;
    private String nickName,signature;
    private int pageNum = 0;
    private String mAddress;//中文地址描述
    private User mUser;
    private ArrayList<BookDetail2> mBooks = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.act_friend_page;
    }

    @Override
    protected void initView(Bundle saveInstanceState) {
        mTitleText.setText("好友主页");
        rlLocation = (RelativeLayout) findViewById(R.id.rl_location);
        ivHeadIcon = (CircleImageView) findViewById(R.id.iv_icon);
        tvLoginname = (TextView) findViewById(R.id.tv_loginname);
        tvSignature = (TextView) findViewById(R.id.tv_signature);
        tvLocation = (TextView) findViewById(R.id.detail_locaiotn_des);
        rlLocation.setOnClickListener(this);
        mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
        mPullRefreshListView.setOnRefreshListener(this);
        mPullRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        // Add an end-of-list listener
        mPullRefreshListView.setOnLastItemVisibleListener(this);
        ListView actualListView = mPullRefreshListView.getRefreshableView();
        // Need to use the Actual ListView when registering for Context Menu
        registerForContextMenu(actualListView);
        mAdapter = new BookAdapter(this,mBooks);
        // You can also just use setListAdapter(mAdapter) or
        // mPullRefreshListView.setAdapter(mAdapter)
        actualListView.setAdapter(mAdapter);
        mPullRefreshListView.setOnScrollListener(this);
    }

    private void getIntentData() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        mCloudItem = intent.getParcelableExtra(BundleFlag.CLOUD_ITEM);
        mAddress = mCloudItem.getSnippet();
        if(StringUtil.isEmpty(mAddress)){
            mAddress = intent.getStringExtra(BundleFlag.ADDRESS);
        }
    }

    @Override
    protected void initData() {
        getIntentData();
        if(mCloudItem!=null){
            tvLocation.setText(mAddress);
        }
        GlobalParams.getUserPresenter().getUserInfo(mCloudItem.getCustomfield().get("uid"),new CallBack(){
            @Override
            public void onSuccess(Object obj) {
                String userJson = (String) obj;
                mUser = JSON.parseObject(userJson,User.class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvLoginname.setText(mUser!=null?mUser.getLogin_name():"用户不存在");
                        if(mUser!=null){
                            if(!StringUtil.isEmpty(mUser.getSignature())){
                                tvSignature.setText(mUser.getSignature());
                            }else{
                                tvSignature.setText(UIUtil.getString(R.string.nowordsig));
                            }
                        }
                        if(!StringUtil.isEmpty(mUser.getHead_icon())){
                            Glide.with(mContext).load(mUser.getHead_icon()).into(ivHeadIcon);
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

    public void getBooks(String uid,int pageNum){
        GlobalParams.getBookPresenter().getUserBooks(uid, pageNum, new CallBack() {
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
                mPullRefreshListView.onRefreshComplete();
                mPullRefreshListView.clearAnimation();
                UIUtil.showToastSafe("未能获取书籍信息");
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

        }
    }

    @Override
    public void onLastItemVisible() {

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        mFirstVisibleItem = firstVisibleItem;
        mVisibleItemCount = visibleItemCount;
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
