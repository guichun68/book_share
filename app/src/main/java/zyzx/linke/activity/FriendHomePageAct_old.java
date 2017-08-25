/*
package zyzx.linke.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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

*/
/**
 * Created by austin on 2017/3/4.
 * Desc: 好友主页（显示好友信息并展示其在地图中分享的书籍,即其在zyzx_user_books表中book_status=2的所有书籍）
 *//*


public class FriendHomePageAct_old extends BaseActivity implements PullToRefreshBase.OnRefreshListener<ListView> {

    private CircleImageView ivHeadIcon;
    private TextView tvLoginname;
    private TextView tvLocation;
    private TextView tvSignature;
    private boolean headerClickable;

    private PullToRefreshListView mPullRefreshListView;

    private BookAdapter mAdapter;
    private int pageNum = 0;
    private String mAddress;//中文地址描述
    private UserVO mUserVO;
    private ArrayList<BookDetail2> mBooks = new ArrayList<>();

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
        mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);

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

    }


    private void getIntentData() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }

    }


    public void getBooks(String uid,int pageNum){
        getBookPresenter().getUserBooks(uid, pageNum, new CallBack() {
            @Override
            public void onSuccess(final Object obj, int... code) {
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
            public void onFailure(Object obj, int... code) {
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
                        public void onSuccess(Object obj, int... code) {
                            dismissProgress();
                            String json = (String) obj;
                            if(StringUtil.isEmpty(json)){
                                UIUtil.showToastSafe("请求出错,请稍后再试！");
                                return;
                            }
                            JSONObject jsonObj = JSON.parseObject(json);
                            if(code[0]==500){
                                //在对方的黑名单中
                                GlobalParams.shouldRefreshContactList = true;
                                UIUtil.showToastSafe(R.string.error_chat);
                                return;
                            }else if(code[0] ==200){
                                //添加成功
                                UIUtil.showToastSafe("已添加好友"+ mUserVO.getLoginName());
                                GlobalParams.shouldRefreshContactList = true;
                                Intent in = new Intent(FriendHomePageAct_old.this,ChatActivity.class);
                                in.putExtra(BundleFlag.UID,String.valueOf(mUserVO.getUserid()));
                                in.putExtra(BundleFlag.LOGIN_NAME, mUserVO.getLoginName());
                                startActivity(in);
                            }else{
                                UIUtil.showToastSafe("添加好友失败！");
                            }
                        }

                        @Override
                        public void onFailure(Object obj, int... code) {
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
//        getBooks(mCloudItem.getCustomfield().get("uid"),pageNum);
    }
}
*/
