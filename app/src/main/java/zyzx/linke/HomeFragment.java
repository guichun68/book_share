package zyzx.linke;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import zyzx.linke.activity.AreaSelAct;
import zyzx.linke.adapter.AllUserBooksListAdapter;
import zyzx.linke.base.BaseFragment;
import zyzx.linke.global.BundleFlag;
import zyzx.linke.global.BundleResult;
import zyzx.linke.global.Const;
import zyzx.linke.model.Area;
import zyzx.linke.model.bean.IndexItem;
import zyzx.linke.utils.CityUtil;
import zyzx.linke.utils.UIUtil;

/**
 * 主页界面
 */
public class HomeFragment extends BaseFragment implements PullToRefreshBase.OnRefreshListener2<ListView>, View.OnClickListener, AMapLocationListener {
    private final String TAG = HomeFragment.class.getSimpleName();
    private final int CITY_CHOOSE_REQUEST_CODE = 10;
    private final int POI_CHOOSE_REQUEST_CODE = 20;
    private String mKeywords = "";

    private AMapLocationClient mAMapLocationClient = null;

    private Toolbar mToolbar;
    private TextView tvInput;

    private AllUserBooksListAdapter mAdapter;

    private String mCurrPro, mCurrCity, mCurrCounty;
    private int mCurrentPageNum = 0;
    private ArrayList<IndexItem> mListViewItems = new ArrayList<>();



    public String getCurrentCity() {
        return mCurrCity;
    }

    @Override
    protected View getView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.frag_home, container, false);
    }

    @Override
    public void initView() {
        tvInput = (TextView) mRootView.findViewById(R.id.tv_input);
        mToolbar = (Toolbar) mRootView.findViewById(R.id.id_toolbar);
        // 设置显示Toolbar
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        showToastDialog(Const.LODING_LOCATION);

        // 注册地理位置回调监听
        AMapLocationClientOption locationOption = new AMapLocationClientOption();
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        locationOption.setOnceLocation(true);//单次定位

        mAMapLocationClient = new AMapLocationClient(getContext());
        mAMapLocationClient.setLocationOption(locationOption);
        mAMapLocationClient.setLocationListener(this);
        mAMapLocationClient.startLocation();
        setUpInteractiveControls();
        mCurrCity = getResources().getString(R.string.default_city);
    }

    private void setUpInteractiveControls() {

        mRootView.findViewById(R.id.ll_search).setOnClickListener(this);
        mRootView.findViewById(R.id.btn_area_choose).setOnClickListener(this);

        mRootView.findViewById(R.id.btn_map).setOnClickListener(this);

        // Set a listener to be invoked when the list should be refreshed.
        PullToRefreshListView mPullRefreshListView = (PullToRefreshListView) mRootView.findViewById(R.id.pull_refresh_list);
        mPullRefreshListView.setOnRefreshListener(this);
        mPullRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        ILoadingLayout endLabels = mPullRefreshListView.getLoadingLayoutProxy(false, true);
        endLabels.setPullLabel(getResources().getString(R.string.pull_label));
        endLabels.setRefreshingLabel(getResources().getString(
                R.string.refresh_label));
        endLabels.setReleaseLabel(getResources().getString(
                R.string.release_label));
        endLabels.setLoadingDrawable(getResources().getDrawable(
                R.mipmap.publicloading));
        // Add an end-of-list listener
//        mPullRefreshListView.setOnLastItemVisibleListener(this);
        ListView actualListView = mPullRefreshListView.getRefreshableView();
        // Need to use the Actual ListView when registering for Context Menu
        registerForContextMenu(actualListView);
        mAdapter = new AllUserBooksListAdapter(getContext(), mListViewItems);
        // You can also just use setListAdapter(mAdapter) or
        // mPullRefreshListView.setAdapter(mAdapter)
        actualListView.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_area_choose:
                showAreaPopupWindow();
                break;
        }
    }

    private void showAreaPopupWindow() {
        Intent areaIntent = new Intent(mContext,AreaSelAct.class);
        areaIntent.putExtra("pro",mCurrPro);
        areaIntent.putExtra("city",mCurrCity);
        areaIntent.putExtra("county",mCurrCounty);
        startActivityForResult(areaIntent,CITY_CHOOSE_REQUEST_CODE);
    }

    private void showErrorDialog(String string) {

        Dialog dialog = new Dialog(getContext());

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                // TODO Auto-generated method stub
            }
        });
        dialog.show();
    }

    /**
     * 根据选择的城市和行政区进行搜索
     *
     * @param pagenum pageNO
     */
    private void searchByLocal(int pagenum) {
        Log.e(TAG, "在城市(" + mCurrCity + "->"+ mCurrCounty +") 查找第" + pagenum + "页的所有书籍信息--待完善");

    }


    private HashMap<String, String[]> mDistrictsOfcityMap = new HashMap<>();

    /**
     * @param city city
     * @return 根据城市名字，得到该城市下对应的所有行政区的字符串数组
     */
    private String[] getDistrictsBasedonCityName(String city) {
        if (mDistrictsOfcityMap.containsKey(city)) {
            return mDistrictsOfcityMap.get(city);
        }

        CityUtil cityUtil = new CityUtil(getContext(), city);
        List<String> lists = cityUtil.getItsDistricts();
        String[] districtsOfThisCity = lists.toArray(new String[lists.size()]);
        mDistrictsOfcityMap.put(city, districtsOfThisCity);

        return districtsOfThisCity;
    }



    /**
     * 方法必须重写
     */
    @Override
    public void onPause() {
        super.onPause();
        stopLocation();//停止定位，
    }
    /**
     * 销毁定位
     */
    private void stopLocation() {
        if (mAMapLocationClient != null) {
            mAMapLocationClient.unRegisterLocationListener(this);
            mAMapLocationClient.onDestroy();
        }
        mAMapLocationClient = null;
    }

    private void setCurrentCity(String mCurrentCity) {
        if (mCurrentCity == null)
            mCurrentCity = "北京";//"未能定位当前城市",默认北京
        this.mCurrCity = mCurrentCity;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (CITY_CHOOSE_REQUEST_CODE == requestCode
                && resultCode == BundleResult.SUCCESS) {
            ArrayList<Area> areas = data.getParcelableArrayListExtra("areas");
            UIUtil.showTestLog("AreaSelAct:",areas.get(0).toString());
            UIUtil.showTestLog("AreaSelAct:",areas.get(1).toString());
            UIUtil.showTestLog("AreaSelAct:",areas.get(2)!=null?areas.get(2).toString():"县为空！");
            mListViewItems.clear();
            mAdapter.notifyDataSetChanged();
            searchByLocal(0);
            return;
        }

        if (requestCode == POI_CHOOSE_REQUEST_CODE
                && resultCode == BundleResult.SUCCESS) {
            String selectedItem = (String) data
                    .getSerializableExtra(BundleFlag.POI_ITEM);
            mKeywords = selectedItem;
            mListViewItems.clear();
            mAdapter.notifyDataSetChanged();
            searchByLocal(0);
            tvInput.setText(selectedItem);
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        //下拉刷新
        mCurrentPageNum = 0;
        searchByLocal(mCurrentPageNum);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        mCurrentPageNum++;
        searchByLocal(mCurrentPageNum);
    }

    @Override
    public void onLocationChanged(AMapLocation location) {
        dismissProgress();
        stopLocation();
        if (location == null) {
            // 如果没有地理位置数据返回，则进行默认的搜索
            searchDefault(0);
            return;
        }
        if (location.getErrorCode() != AMapLocation.LOCATION_SUCCESS) {
            UIUtil.showToastSafe(R.string.locate_fail);
            searchDefault(0);
            return;
        } else {
            mCurrPro = location.getProvince();
            mCurrCity = location.getCity();
            mCurrCounty = location.getDistrict();
            // 并且设置当前的城市
            setCurrentCity(location.getCity());
            searchByLocal(0);
        }
    }

    /**
     * 进行默认的搜索 类型为根据城市行政区的搜索 默认的城市可以自己配置
     *
     * @param pagenum 页码
     */
    private void searchDefault(int pagenum) {
        mCurrCity = getResources().getString(R.string.default_city);
        searchByLocal(pagenum);
    }

}