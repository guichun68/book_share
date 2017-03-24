package zyzx.linke.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.amap.api.services.cloud.CloudItem;
import com.amap.api.services.core.LatLonPoint;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import zyzx.linke.R;
import zyzx.linke.adapter.AllUserBooksListAdapter;
import zyzx.linke.adapter.FriendListAdapter;
import zyzx.linke.base.BaseActivity;
import zyzx.linke.global.BundleFlag;
import zyzx.linke.global.Const;
import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.User;
import zyzx.linke.utils.StringUtil;
import zyzx.linke.utils.UIUtil;

/**
 * Created by austin on 2017/3/24.
 */

public class FriendSearch extends BaseActivity implements PullToRefreshBase.OnRefreshListener<ListView>{

    private int mCurrentPageNum;
    private FriendListAdapter mAdapter;
    private ArrayList<User> mUsers = new ArrayList<>();
    private String mKeyWord=null;
    //view
    private PullToRefreshListView mPullRefreshListView;
    private EditText mEtKey;
    private Button mBtnSearch;


    @Override
    protected int getLayoutId() {
        return R.layout.act_search_friend;
    }

    @Override
    protected void initView(Bundle saveInstanceState) {
        //title
        mTitleText.setText("书友搜索");
        findViewById(R.id.tv_add_mylib).setVisibility(View.GONE);

        mEtKey = (EditText) findViewById(R.id.input_edittext);
        mBtnSearch = (Button) findViewById(R.id.btn_search);
        mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
        // Set a listener to be invoked when the list should be refreshed.
        mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);

        mBtnSearch.setOnClickListener(this);
        mPullRefreshListView.setOnRefreshListener(this);
        mPullRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
    }

    @Override
    protected void initData() {
        mAdapter = new FriendListAdapter( mUsers);
        ListView actualListView = mPullRefreshListView.getRefreshableView();
        // Need to use the Actual ListView when registering for Context Menu
        registerForContextMenu(actualListView);
        actualListView.setAdapter(mAdapter);
      /*  actualListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //进入好友详情页

            }
        });*/
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.btn_search://搜索好友
                if(StringUtil.isEmpty(mEtKey.getText().toString())){
                    UIUtil.showToastSafe("请输入书友名称");
                    return;
                }
                mKeyWord = mEtKey.getText().toString();
                searchFriend(mKeyWord,0,false);
                break;
        }
    }

    /**
     * 搜索好友
     * @param keyWord 搜索关键词
     * @param pageNum 页数
     */
    private void searchFriend(String keyWord, final int pageNum, final boolean isLoadingMore) {
        getUserPresenter().searchFriend(keyWord, pageNum, new CallBack() {
            @Override
            public void onSuccess(final Object obj) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mPullRefreshListView.onRefreshComplete();
                        mPullRefreshListView.clearAnimation();
                        UIUtil.print("查找成功");
                        String json = (String) obj;
                        List<User> users = JSON.parseArray(json,User.class);
                        if(users==null || users.isEmpty()){
                            if(isLoadingMore){
                                mCurrentPageNum--;
                            }
                            UIUtil.showToastSafe("没有更多数据了");
                            if(!isLoadingMore){
                                mUsers.clear();
                                mAdapter.notifyDataSetChanged();
                            }
                        }else{
                            if(isLoadingMore){
                                //有值
                                mUsers.addAll(users);
                            }else{
                                mUsers.clear();
                                mUsers.addAll(users);
                            }
                        }

                        mAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onFailure(Object obj) {
                mPullRefreshListView.onRefreshComplete();
                mPullRefreshListView.clearAnimation();
                if(isLoadingMore){
                    mCurrentPageNum--;
                }
                UIUtil.showToastSafe("查询失败");
            }
        });
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        if(mKeyWord==null){
            return;
        }
        ILoadingLayout endLabels = mPullRefreshListView.getLoadingLayoutProxy(
                false, true);
        endLabels.setPullLabel(getResources().getString(R.string.pull_label));
        endLabels.setRefreshingLabel(getResources().getString(
                R.string.refresh_label));
        endLabels.setReleaseLabel(getResources().getString(
                R.string.release_label));
        endLabels.setLoadingDrawable(getResources().getDrawable(
                R.mipmap.publicloading));
        mCurrentPageNum++;
        searchFriend(mKeyWord,mCurrentPageNum,true);
    }


}
