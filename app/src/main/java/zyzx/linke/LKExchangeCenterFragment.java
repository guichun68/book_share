package zyzx.linke;


import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

import zyzx.linke.activity.BookExchangePage;
import zyzx.linke.activity.SkillExchangePage;
import zyzx.linke.adapter.ExchangeCenterVPAdapter;
import zyzx.linke.base.BaseFragment;
import zyzx.linke.utils.AppUtil;
import zyzx.linke.utils.StringUtil;
import zyzx.linke.utils.UIUtil;
import zyzx.linke.views.BaseExchangePager;

/**
 * 交换中心页面
 */
public class LKExchangeCenterFragment extends BaseFragment implements View.OnClickListener {

    private ViewPager mViewPager;
    private ExchangeCenterVPAdapter mViewPagerAdapter;
    private ArrayList<BaseExchangePager> mPages = new ArrayList<>();
    private TabLayout mTabLayout;
    private String[] titles = {"书籍交换","技能交换"};

    @Override
    protected View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.frag_exchange_market, container, false);
    }

    @Override
    public void initView() {
        mTitleText.setText(R.string.tab_exchange_market);
        mTabLayout = (TabLayout) mRootView.findViewById(R.id.tabLayout);
        LinearLayout linearLayout = (LinearLayout) mTabLayout.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        linearLayout.setDividerPadding(UIUtil.dip2px(25));
        linearLayout.setDividerDrawable(ContextCompat.getDrawable(getContext(),
                R.drawable.vertical_divider));

        mTabLayout.addTab(mTabLayout.newTab().setText("书籍交换"));
        mTabLayout.addTab(mTabLayout.newTab().setText("技能交换"));

        mViewPager = (ViewPager)mRootView.findViewById(R.id.vp_viewpager);
        mTabLayout.setupWithViewPager(mViewPager);
        BaseExchangePager bookExchangePage = new BookExchangePage(getActivity(),R.layout.bookpager);
        BaseExchangePager skillExchangePage = new SkillExchangePage(getActivity(),R.layout.skillpager);
        mPages.add(bookExchangePage);
        mPages.add(skillExchangePage);
        // 初始化ViewPager的适配器，并设置给它
        mViewPagerAdapter = new ExchangeCenterVPAdapter(mPages,titles);
        mViewPager.setAdapter(mViewPagerAdapter);
        mBackBtn.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){

        }
    }
}
