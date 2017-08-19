package zyzx.linke.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import zyzx.linke.base.BaseSwapPager;

/**
 * Created by Austin on 2015/6/29.
 * Desc:交换中心VP之Adapter
 */
public class ExchangeCenterVPAdapter extends PagerAdapter {

  ArrayList<BaseSwapPager> pagers;
  String []titles;

  public ExchangeCenterVPAdapter(ArrayList<BaseSwapPager> pagers, String[] titles){
    this.pagers = pagers;
    this.titles = titles;
  }

  // TabLayout关联viewpager后，其标题会自动从此方法获取
  @Override
  public CharSequence getPageTitle(int position) {
    return this.titles[position];
  }


  @Override
  public int getCount() {
    return pagers.size();
  }

  @Override
  public boolean isViewFromObject(View view, Object object) {
    return view == object;
  }

  @Override
  public Object instantiateItem(ViewGroup container, int position) {
    View rootView = pagers.get(position).getRootView();
    container.addView(rootView);
    return rootView;
  }

  @Override
  public void destroyItem(ViewGroup container, int position, Object object) {
    container.removeView(pagers.get(position).getRootView());
  }
}
