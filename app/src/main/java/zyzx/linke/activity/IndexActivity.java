package zyzx.linke.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.cloud.CloudItem;
import com.amap.api.services.cloud.CloudItemDetail;
import com.amap.api.services.cloud.CloudResult;
import com.amap.api.services.cloud.CloudSearch;
import com.amap.api.services.cloud.CloudSearch.OnCloudSearchListener;
import com.amap.api.services.cloud.CloudSearch.SearchBound;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import zyzx.linke.R;
import zyzx.linke.adapter.DistrictListAdapter;
import zyzx.linke.constant.BundleFlag;
import zyzx.linke.constant.BundleResult;
import zyzx.linke.constant.Const;
import zyzx.linke.adapter.CloudItemListAdapter;
import zyzx.linke.model.bean.City;
import zyzx.linke.utils.CityUtil;
import zyzx.linke.utils.CustomProgressDialog;
import zyzx.linke.utils.UIUtil;
import zyzx.linke.utils.Utils;
import zyzx.linke.views.CityChoosePopupWindow;

/**
 * 云图Item list展现，进入APP先提示定位。如果定位成功，按照当前位置10km周边搜索；如果定位失败，按照默认城市北京搜索
 * 点击城市名称可选择该城市某个区或者切换城市进行本地搜索。
 *
 * @author ligen
 */
public class IndexActivity extends Activity implements OnClickListener,
        PullToRefreshBase.OnRefreshListener<ListView>, PullToRefreshBase.OnLastItemVisibleListener,
        OnCloudSearchListener, OnScrollListener, AMapLocationListener {

    private static final String WHOLE_CITY = "全城";
    private final int CITY_CHOOSE_REQUEST_CODE = 10;
    private final int POI_CHOOSE_REQUEST_CODE = 20;

    private LinearLayout mBtnAreaChoose;
    private ImageView mBtnMap;
    private ArrayList<City> mCityList;
    private ArrayList<String> mCityLetterList;
    private HashMap<String, Integer> mCityMap;
    private TextView mCurrentCityDistrictTextview;
    private String mCurrentDistrict;
    private AMapLocationClientOption mAMapLocationClientOption;
    private AMapLocationClient mAMapLocationClient = null;

    // 快捷搜索词
    private String[] mLetterStrs = {"常", "A", "B", "C", "D", "E", "F", "G",
            "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
            "U", "V", "W", "X", "Y", "Z"};

    private PullToRefreshListView mPullRefreshListView;
    private CloudItemListAdapter mAdapter;
    private CloudSearch mCloudSearch;
    private CloudSearch.Query mQuery;
    private LatLonPoint mCenterPoint = new LatLonPoint(39.911823, 116.394829);
    private String mKeywords = "";
    private ArrayList<CloudItem> mCoudItemList = new ArrayList<CloudItem>();
    private Dialog mProgressDialog = null;
    private Context mApplicationContext;
    private CityChoosePopupWindow mPopupWindow;
    private ImageView mUpDownArrow;
    private String mCurrentCity;
    private int mCurrentPageNum = 0;
    private LinearLayout mLLYNoData;
    private final static int LOCAL_SEARCH_TYPE = 1;
    private final static int ARROUND_SEARCH_TYPE = 2;
    private int mCurrentSearchType = ARROUND_SEARCH_TYPE;

    private OnClickListener mPopupWindowClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.change_city_linearlayout:
                    gotoCityListActivity();
                    break;

                default:
                    break;
            }

        }
    };

    private OnItemClickListener mGridViewItemListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                long arg3) {

            mChosenDistrictIndex = arg2;
            mCurrentDistrict = mDistrictsOfCurrentCity[arg2];
            mCurrentCityDistrictTextview.setText(getCurrentCity()
                    + mCurrentDistrict);
            mCoudItemList.clear();
            mAdapter.notifyDataSetChanged();
            searchByLocal(0);
            mPopupWindow.dismiss();
        }
    };
    private String[] mDistrictsOfCurrentCity;
    private int mChosenDistrictIndex = -1;
    private int mFirstVisibleItem;
    private int mVisibleItemCount;
    private RelativeLayout mSearchBarLinearLayout;
    private EditText minputEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        mApplicationContext = this.getApplicationContext();

        // 注册云图搜索监听
        mCloudSearch = new CloudSearch(this);
        mCloudSearch.setOnCloudSearchListener(this);
        showProgressDialog(Const.LODING_LOCATION);

        // 注册地理位置回调监听
        mAMapLocationClientOption = new AMapLocationClientOption();
        mAMapLocationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);

        mAMapLocationClient = new AMapLocationClient(this.getApplicationContext());
        mAMapLocationClient.setLocationOption(mAMapLocationClientOption);
        mAMapLocationClient.setLocationListener(this);
        mAMapLocationClient.startLocation();

        setUpInteractiveControls();

        mCurrentCity = getResources().getString(R.string.default_city);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        stopLocation();// 停止定位
    }

    private long exitTime = 0;
    private HashMap<String, String[]> mDistrictsOfcityMap = new HashMap<String, String[]>();
    ;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(
                        this,
                        getResources().getString(R.string.press_more_then_exit),
                        Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
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

    /**
     * 进行默认的搜索 类型为根据城市行政区的搜索 默认的城市可以自己配置
     *
     * @param pagenum
     */
    private void searchDefault(int pagenum) {
        mCurrentCity = getResources().getString(R.string.default_city);
        searchByLocal(pagenum);
    }

    /**
     * 根据经纬度进行周边搜索
     *
     * @param pagenum
     */
    private void searchByArround(int pagenum) {
        mCurrentSearchType = ARROUND_SEARCH_TYPE;
        if (mCoudItemList == null || mCoudItemList.size() == 0) {
            mCurrentPageNum = 0;
        } else {
            mCurrentPageNum = pagenum;
        }
        showProgressDialog(Const.LODING_GET_DATA);
        SearchBound bound = new SearchBound(new LatLonPoint(
                mCenterPoint.getLatitude(), mCenterPoint.getLongitude()),
                Const.SEARCH_AROUND);
        try {

            mQuery = new CloudSearch.Query(Const.mTableID, mKeywords, bound);
            mQuery.setPageSize(10);
            mQuery.setPageNum(pagenum);
            mCloudSearch.searchCloudAsyn(mQuery);
        } catch (AMapException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据选择的城市和行政区进行搜索
     *
     * @param pagenum
     */
    private void searchByLocal(int pagenum) {
        mCurrentSearchType = LOCAL_SEARCH_TYPE;
        if (mCoudItemList == null || mCoudItemList.size() == 0) {
            mCurrentPageNum = 0;
        } else {
            mCurrentPageNum = pagenum;
        }
        showProgressDialog(Const.LODING_GET_DATA);
        String localName = "";
        if (mCurrentDistrict != null && !mCurrentDistrict.equals("")
                && !mCurrentDistrict.equals(WHOLE_CITY)) {
            localName = getCurrentCity() + mCurrentDistrict;
        } else {
            localName = getCurrentCity();
        }
        SearchBound bound = new SearchBound(localName);
        try {
            mQuery = new CloudSearch.Query(Const.mTableID, mKeywords, bound);
            mQuery.setPageSize(10);
            mQuery.setPageNum(mCurrentPageNum);
            mCloudSearch.searchCloudAsyn(mQuery);
        } catch (AMapException e) {
            e.printStackTrace();
        }
    }

    private void setUpInteractiveControls() {

        mLLYNoData = (LinearLayout) findViewById(R.id.lly_noData);
        minputEditText = (EditText) findViewById(R.id.input_edittext);
        mSearchBarLinearLayout = (RelativeLayout) findViewById(R.id.search_bar_layout);
        mCurrentCityDistrictTextview = (TextView) findViewById(R.id.current_city_district_textview);
        mUpDownArrow = (ImageView) findViewById(R.id.up_down_arrow);

        mBtnAreaChoose = (LinearLayout) findViewById(R.id.btn_area_choose);
        mBtnAreaChoose.setOnClickListener(this);

        mBtnMap = (ImageView) findViewById(R.id.btn_map);
        mBtnMap.setOnClickListener(this);

        // Set a listener to be invoked when the list should be refreshed.
        mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
        mPullRefreshListView.setOnRefreshListener(this);
        mPullRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        // Add an end-of-list listener
        mPullRefreshListView.setOnLastItemVisibleListener(this);
        ListView actualListView = mPullRefreshListView.getRefreshableView();
        // Need to use the Actual ListView when registering for Context Menu
        registerForContextMenu(actualListView);
        mAdapter = new CloudItemListAdapter(mApplicationContext, mCoudItemList);
        // You can also just use setListAdapter(mAdapter) or
        // mPullRefreshListView.setAdapter(mAdapter)
        actualListView.setAdapter(mAdapter);
        mPullRefreshListView.setOnScrollListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_area_choose:
                showAreaPopupWindow();
                break;

            case R.id.btn_map:
                gotoMapActivity();
                break;

            case R.id.input_edittext:
                gotoKeywordInputActivity();
                break;

            default:
                break;
        }

    }

    private void gotoKeywordInputActivity() {

        Intent intent = new Intent(this, KeywordListActivity.class);
        startActivityForResult(intent, POI_CHOOSE_REQUEST_CODE);
    }

    private void gotoMapActivity() {

        ArrayList<CloudItem> currentVisibleItems = new ArrayList<CloudItem>();

        for (int i = mFirstVisibleItem; i < mCoudItemList.size()
                && i < mFirstVisibleItem + mVisibleItemCount; i++) {
            currentVisibleItems.add(mCoudItemList.get(i));
        }
        Intent intent = new Intent(this, MapActivity.class);
        intent.putParcelableArrayListExtra(BundleFlag.CLOUD_ITEM_LIST,
                currentVisibleItems);
        startActivity(intent);
    }

    private void showAreaPopupWindow() {

        mPopupWindow = new CityChoosePopupWindow(this,
                mPopupWindowClickListener);

        mPopupWindow.showAsDropDown(mSearchBarLinearLayout);
        mUpDownArrow.setBackgroundResource(R.mipmap.arrow_down_white);

        mPopupWindow.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                mUpDownArrow.setBackgroundResource(R.mipmap.arrow_up_white);
            }
        });

        if (mCityLetterList == null || mCityList == null || mCityMap == null)
            createCityListForCityChoose();

        updatePopupWindowData();

    }

    private void updatePopupWindowData() {

        mDistrictsOfCurrentCity = getDistrictsBasedonCityName(getCurrentCity());

        mPopupWindow.getDistrictGridView().setAdapter(
                new DistrictListAdapter(this, mDistrictsOfCurrentCity,
                        mChosenDistrictIndex));

        mPopupWindow.getDistrictGridView().setOnItemClickListener(
                mGridViewItemListener);

        mPopupWindow.getCurrentCityTextView().setText(getCurrentCity());

        mCurrentCityDistrictTextview.setText(getCurrentCity());
    }

    private void createCityListForCityChoose() {
        try {

            String content = Utils.getAssetsFie(this, "city.json");
            dealWithJson(content);
        } catch (IOException e) {
            Log.e("aaa", "city init failed", e);
        }
    }

    private void dealWithJson(String content) {

        try {
            JSONObject json = new JSONObject(content);
            String status = json.getString("status");
            if ("200".equals(status)) {
                JSONObject result = json.getJSONObject("result");
                int cityVersion = result.optInt("version");
                JSONObject data = result.getJSONObject("city");
                HashMap<String, Integer> tempCityHashMap = new HashMap<String, Integer>();
                ArrayList<String> temp_city_letter_list = new ArrayList<String>();
                ArrayList<City> tempCityList = new ArrayList<City>();
                for (int m = 0; m < mLetterStrs.length; m++) {
                    String key = mLetterStrs[m];
                    JSONArray array = data.optJSONArray(key);
                    if (array == null) {
                        continue;
                    }
                    temp_city_letter_list.add(key);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject json_city = array.getJSONObject(i);
                        City model_item = new City();
                        model_item.name = json_city.optString("name");
                        model_item.code = json_city.optString("cityCode");
                        if (i == 0) {
                            model_item.letter = key;
                            tempCityHashMap.put(key, tempCityList.size());
                        }
                        tempCityList.add(model_item);
                    }
                }

                if (mCityList == null || mCityList.size() <= 0) {
                    mCityList = tempCityList;
                    mCityMap = tempCityHashMap;
                    mCityLetterList = temp_city_letter_list;
                }
            } else {
                if (mCityList == null || mCityList.size() <= 0) {
                    showErrorDialog(getString(R.string.city_get_error));
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void showErrorDialog(String string) {

        Dialog dialog = new Dialog(this);

        dialog.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                // TODO Auto-generated method stub

            }
        });

        dialog.show();

    }

    private void gotoCityListActivity() {

        Intent intent = new Intent(this, CityListActivity.class);
        intent.putExtra(BundleFlag.CITY_LIST, mCityList);
        intent.putStringArrayListExtra(BundleFlag.CITY_LETTERS, mCityLetterList);
        intent.putExtra(BundleFlag.CITY_MAP, mCityMap);
        startActivityForResult(intent, CITY_CHOOSE_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (CITY_CHOOSE_REQUEST_CODE == requestCode
                && resultCode == BundleResult.SUCCESS) {
            City city = (City) data.getSerializableExtra(BundleFlag.CITY_MODEL);
            if (city != null) {
                setCity(city);
            }
            mCoudItemList.clear();
            mAdapter.notifyDataSetChanged();
            mCurrentDistrict = "";
            searchByLocal(0);
            return;
        }

        if (requestCode == POI_CHOOSE_REQUEST_CODE
                && resultCode == BundleResult.SUCCESS) {
            String selectedItem = (String) data
                    .getSerializableExtra(BundleFlag.POI_ITEM);
            mKeywords = selectedItem;
            mCoudItemList.clear();
            mAdapter.notifyDataSetChanged();
            searchByLocal(0);
            minputEditText.setText(selectedItem);
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setCity(City city) {
        mChosenDistrictIndex = -1;
        setCurrentCity(city.name.toString());
        updatePopupWindowData();
    }

    /**
     * 根据城市名字，得到该城市下对应的所有行政区的字符串数组
     *
     * @param city
     * @return
     */
    private String[] getDistrictsBasedonCityName(String city) {

        if (mDistrictsOfcityMap.containsKey(city)) {
            return mDistrictsOfcityMap.get(city);
        }

        CityUtil cityUtil = new CityUtil(this, city);
        List<String> lists = cityUtil.getItsDistricts();
        String[] districtsOfThisCity = lists.toArray(new String[lists.size()]);
        mDistrictsOfcityMap.put(city, districtsOfThisCity);

        return districtsOfThisCity;
    }

    private void showProgressDialog(String message) {
        mProgressDialog = CustomProgressDialog.getToastDialog(this,message);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
    }

    private void dissmissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onLastItemVisible() {
        // TODO Auto-generated method stub
    }

    @Override
    public void onCloudItemDetailSearched(CloudItemDetail item, int errorCode) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onCloudSearched(CloudResult result, int errorCode) {
        // TODO Auto-generated method stub
        mPullRefreshListView.onRefreshComplete();
        mPullRefreshListView.clearAnimation();
        dissmissProgressDialog();
        if (errorCode == Const.NO_ERROR && result != null) {
            ArrayList<CloudItem> cloudResult = result.getClouds();
            if (cloudResult != null && cloudResult.size() > 0) {
                mCoudItemList.addAll(cloudResult);
                mAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(mApplicationContext,
                        R.string.error_no_more_item, Toast.LENGTH_SHORT).show();
            }

        } else if (errorCode == Const.ERROR_CODE_SOCKE_TIME_OUT) {
            UIUtil.showToastSafe(this.getApplicationContext(),R.string.error_socket_timeout);

        } else if (errorCode == Const.ERROR_CODE_UNKNOW_HOST) {
            UIUtil.showToastSafe(this.getApplicationContext(),R.string.error_network);
        } else if (errorCode == Const.ERROR_CODE_FAILURE_AUTH) {
            UIUtil.showToastSafe(this.getApplicationContext(),R.string.error_key);
        } else if (errorCode == Const.ERROR_CODE_SCODE) {
            UIUtil.showToastSafe(this.getApplicationContext(),R.string.error_scode);
        } else if (errorCode == Const.ERROR_CODE_TABLEID) {
            UIUtil.showToastSafe(this.getApplicationContext(),R.string.error_table_id);
        } else {
            UIUtil.showToastSafe(this.getApplicationContext(),UIUtil.getString(R.string.error_other)+errorCode);
        }
        if (mCoudItemList == null || mCoudItemList.size() == 0) {
            // mPullRefreshListView.setMode(Mode.DISABLED);
            mLLYNoData.setVisibility(View.VISIBLE);
        } else {
            mLLYNoData.setVisibility(View.GONE);
            // mPullRefreshListView.setMode(Mode.PULL_FROM_END);
        }
    }

    public String getCurrentCity() {
        return mCurrentCity;
    }

    private void setCurrentCity(String mCurrentCity) {
        if (mCurrentCity == null)
            mCurrentCity = "未能定位当前城市";
        mCurrentCity = mCurrentCity.replace("市", "");
        mCurrentCityDistrictTextview.setText(mCurrentCity);
        this.mCurrentCity = mCurrentCity;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {

        mFirstVisibleItem = firstVisibleItem;
        mVisibleItemCount = visibleItemCount;

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // TODO Auto-generated method stub

    }

    /**
     * 地理位置变化回调
     */
    @Override
    public void onLocationChanged(AMapLocation location) {
        // TODO Auto-generated method stub
        dissmissProgressDialog();
        stopLocation();
        if (location == null) {
            // 如果没有地理位置数据返回，则进行默认的搜索
            searchDefault(0);
            return;
        }

        if (location.getErrorCode()!= AMapLocation.LOCATION_SUCCESS) {
            UIUtil.showToastSafe(this.getApplicationContext(),R.string.locate_fail);
            searchDefault(0);
            return;
        }else{
            Double geoLat = location.getLatitude();
            Double geoLng = location.getLongitude();
            mCenterPoint = new LatLonPoint(geoLat, geoLng);
            // 并且设置当前的城市
            setCurrentCity(location.getCity());
            mCurrentSearchType = ARROUND_SEARCH_TYPE;
            searchByArround(0);
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        // TODO Auto-generated method stub
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
        if (mCurrentSearchType == ARROUND_SEARCH_TYPE) {
            searchByArround(mCurrentPageNum);
        } else {
            searchByLocal(mCurrentPageNum);
        }
    }
}
