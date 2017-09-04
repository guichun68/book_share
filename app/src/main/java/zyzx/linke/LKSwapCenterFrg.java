package zyzx.linke;


import android.content.res.Resources;
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

import zyzx.linke.activity.BookPage;
import zyzx.linke.activity.SkillPage;
import zyzx.linke.adapter.BaseVPAdapter;
import zyzx.linke.base.BaseFragment;
import zyzx.linke.base.BasePager;
import zyzx.linke.utils.UIUtil;

/**
 * 交换中心页面
 */
public class LKSwapCenterFrg extends BaseFragment {

    private ViewPager mViewPager;
    private BaseVPAdapter mViewPagerAdapter;
    private ArrayList<BasePager> mPages = new ArrayList<>();
    private TabLayout mTabLayout;
    private String[] titles = {"书籍交换","技能交换"};
    private BasePager bookExchangePage,skillExchangePage;
//    private ArgbEvaluator evaluator;
//    private LinearLayout llSearch;


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


        LinearLayout linearLayout = (LinearLayout) mTabLayout.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        linearLayout.setDividerPadding(UIUtil.dip2px(15));
        linearLayout.setDividerDrawable(ContextCompat.getDrawable(getContext(),
                R.drawable.vertical_divider));


        mTabLayout.addTab(mTabLayout.newTab().setText(titles[0]));
        mTabLayout.addTab(mTabLayout.newTab().setText(titles[1]));

        mViewPager = (ViewPager)mRootView.findViewById(R.id.vp_viewpager);
        mTabLayout.setupWithViewPager(mViewPager);
        bookExchangePage = new BookPage(getActivity(),R.layout.bookpager);
        skillExchangePage = new SkillPage(getActivity(),R.layout.skillpager);
        mPages.add(bookExchangePage);
        mPages.add(skillExchangePage);
        // 初始化ViewPager的适配器，并设置给它
        mViewPagerAdapter = new BaseVPAdapter(mPages,titles);
        mViewPager.setAdapter(mViewPagerAdapter);

        mBackBtn.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onDestroy() {
        if(bookExchangePage!=null) {
            if (((BookPage) bookExchangePage).getHandler() != null){
                ((BookPage) bookExchangePage).getHandler().removeCallbacksAndMessages(null);
            }
        }
        super.onDestroy();
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
