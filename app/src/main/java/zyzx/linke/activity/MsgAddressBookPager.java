package zyzx.linke.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import zyzx.linke.R;
import zyzx.linke.adapter.AllUserBooksListAdapter;
import zyzx.linke.base.BaseMsgPager;

/**
 * Created by austin on 2017/3/22.
 * Desc: 消息中心-->通讯录页面
 */

public class MsgAddressBookPager extends BaseMsgPager implements View.OnClickListener {

    private View mView;
    private ListView mLvFriends;
    private RelativeLayout mRlAddFriend;

    MsgAddressBookPager(Context context) {
        super(context);
    }

    @Override
    protected void initView() {
        mView = View.inflate(mContext, R.layout.msg_address_book,null);
        mRlAddFriend = (RelativeLayout) mView.findViewById(R.id.rl_add_friend);
        mLvFriends = (ListView) mView.findViewById(R.id.lv_friends);

        mRlAddFriend.setOnClickListener(this);

    }

    @Override
    public void initData() {

    }

    @Override
    public View getRootView() {
        return mView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_add_friend:
                //添加好友
                Intent in = new Intent(mContext,FriendSearch.class);
                mContext.startActivity(in);
                break;
        }
    }
}
