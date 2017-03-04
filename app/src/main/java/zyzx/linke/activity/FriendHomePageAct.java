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
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;

import zyzx.linke.R;
import zyzx.linke.adapter.BookAdapter;
import zyzx.linke.adapter.CloudItemListAdapter;
import zyzx.linke.constant.BundleFlag;
import zyzx.linke.constant.GlobalParams;
import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.BookDetail;
import zyzx.linke.model.bean.User;
import zyzx.linke.utils.StringUtil;
import zyzx.linke.utils.UIUtil;

/**
 * Created by austin on 2017/3/4.
 * Desc: 好友主页
 */

public class FriendHomePageAct extends BaseActivity implements PullToRefreshBase.OnLastItemVisibleListener, AbsListView.OnScrollListener {

    private RelativeLayout rlLocation;
    private ImageView ivHeadIcon;
    private TextView tvLoginname;
    private TextView tvLocation;
    private TextView tvSignature;

    private PullToRefreshListView mPullRefreshListView;
    private int mFirstVisibleItem;
    private int mVisibleItemCount;

    private BookAdapter mAdapter;
    private CloudItem mCloudItem;
    private String coverUrl;
    private String nickName,signature;
    private int pageNum;
    private User mUser;
    private ArrayList<BookDetail> mBooks = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.act_friend_page;
    }

    @Override
    protected void initView(Bundle saveInstanceState) {
        mTitleText.setText("好友主页");
        rlLocation = (RelativeLayout) findViewById(R.id.rl_location);
        ivHeadIcon = (ImageView) findViewById(R.id.iv_icon);
        tvLoginname = (TextView) findViewById(R.id.tv_loginname);
        tvSignature = (TextView) findViewById(R.id.tv_signature);
        tvLocation = (TextView) findViewById(R.id.detail_locaiotn_des);
        rlLocation.setOnClickListener(this);
        mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);

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
    }

    @Override
    protected void initData() {
        getIntentData();
        if(mCloudItem!=null){
            coverUrl  = mCloudItem.getCustomfield().get("book_image_url");
            if(!StringUtil.isEmpty(coverUrl)){
                Glide.with(mContext).load(coverUrl).into(ivHeadIcon);
            }
            tvLocation.setText(mCloudItem.getSnippet());
        }
        GlobalParams.getUserPresenter().getUserInfo(mCloudItem.getCustomfield().get("uid"),new CallBack(){
            @Override
            public void onSuccess(Object obj) {
                String userJson = (String) obj;
                mUser = JSON.parseObject(userJson,User.class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvLoginname.setText(mUser.getLogin_name());
                        tvSignature.setText(mUser.getSignature());
                    }
                });
            }

            @Override
            public void onFailure(Object obj) {
                UIUtil.showToastSafe("未能获取用户信息");
            }
        });

        GlobalParams.getBookPresenter().getUserBooks(mCloudItem.getCustomfield().get("uid"), 0, new CallBack() {
            @Override
            public void onSuccess(Object obj) {

            }

            @Override
            public void onFailure(Object obj) {

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
}
