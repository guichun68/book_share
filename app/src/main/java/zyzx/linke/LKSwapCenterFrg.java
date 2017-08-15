package zyzx.linke;


import android.content.res.Resources;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.lang.reflect.Field;
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
public class LKSwapCenterFrg extends BaseFragment implements View.OnClickListener {

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
        mTabLayout.post(new Runnable() {
            @Override
            public void run() {
                setIndicator(mTabLayout,30,30);
            }
        });
//        LinearLayout linearLayout = (LinearLayout) mTabLayout.getChildAt(0);
//        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
//        linearLayout.setDividerPadding(UIUtil.dip2px(25));
//        linearLayout.setDividerDrawable(ContextCompat.getDrawable(getContext(),
//                R.drawable.vertical_divider));
        LinearLayout linearLayout = (LinearLayout) mTabLayout.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        linearLayout.setDividerPadding(UIUtil.dip2px(15));
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

    //修改TabLayout下划线长度
    public void setIndicator (TabLayout tabs,int leftDip,int rightDip) {
        Class<?> tabLayout = tabs.getClass();
        Field tabStrip = null;
        try {
            tabStrip = tabLayout.getDeclaredField("mTabStrip");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        tabStrip.setAccessible(true);
        LinearLayout llTab = null;
        try {
            llTab = (LinearLayout) tabStrip.get(tabs);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        int left = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, leftDip, Resources.getSystem().getDisplayMetrics());
        int right = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, rightDip, Resources.getSystem().getDisplayMetrics());

        for (int i = 0; i < llTab.getChildCount(); i++) {
            View child = llTab.getChildAt(i);
            child.setPadding(0, 0, 0, 0);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
            params.leftMargin = left;
            params.rightMargin = right;
            child.setLayoutParams(params);
            child.invalidate();
        }
    }
}
