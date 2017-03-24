package zyzx.linke.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.List;

import zyzx.linke.R;
import zyzx.linke.adapter.MsgPagerAdapter;
import zyzx.linke.base.BaseActivity;
import zyzx.linke.base.BaseMsgPager;

/**
 * Created by austin on 2017/3/22.
 * Desc: 消息中心
 */

public class MsgCenterAct extends BaseActivity{

    protected static final String TAG = MsgCenterAct.class.getSimpleName();
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private List<BaseMsgPager> pagers = new ArrayList<>();
    private MsgPagerAdapter mMsgViewPagerAdapter;


    @Override
    protected int getLayoutId() {
        return R.layout.em_activity_main;
    }

    @Override
    protected void initView(Bundle saveInstanceState) {
        mTitleText.setText("消息中心");

        mTabLayout = (TabLayout) findViewById(R.id.tl_tablayout);
        mViewPager = (ViewPager) findViewById(R.id.vp_tabvp);
        initMsgViews();
    }

    @Override
    protected void initData() {
        String[] title = {"消息","通讯录"};
        mMsgViewPagerAdapter = new MsgPagerAdapter(pagers,title);
        mViewPager.setAdapter(mMsgViewPagerAdapter);

        // 将TabLayout和ViewPager进行关联，让两者联动起来
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabsFromPagerAdapter(mMsgViewPagerAdapter);
    }

    public void initMsgViews() {
        pagers.clear();
        MsgSessionPager sessionPager= new MsgSessionPager(mContext);
        MsgAddressBookPager addressBookPager=new MsgAddressBookPager(mContext);
        pagers.add(sessionPager);
        pagers.add(addressBookPager);
    }

    public List<BaseMsgPager> getPagers() {
        if(pagers != null && pagers.size()>0)
            return pagers;
        initMsgViews();
        return pagers;
    }
}
