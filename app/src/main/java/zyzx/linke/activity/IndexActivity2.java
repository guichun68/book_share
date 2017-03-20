package zyzx.linke.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
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
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
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
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import zyzx.linke.R;
import zyzx.linke.adapter.AllUserBooksListAdapter;
import zyzx.linke.adapter.DistrictListAdapter;
import zyzx.linke.global.BaseActivity;
import zyzx.linke.global.BundleFlag;
import zyzx.linke.global.BundleResult;
import zyzx.linke.global.Const;
import zyzx.linke.global.GlobalParams;
import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.BookDetail2;
import zyzx.linke.model.bean.City;
import zyzx.linke.model.bean.IndexItem;
import zyzx.linke.model.bean.RequestParamGetBookInfos;
import zyzx.linke.model.bean.ResponseBooks;
import zyzx.linke.utils.CityUtil;
import zyzx.linke.utils.SharedPreferencesUtils;
import zyzx.linke.utils.UIUtil;
import zyzx.linke.utils.Utils;
import zyzx.linke.views.CityChoosePopupWindow;
import zyzx.linke.views.MyActionBarDrawerToggle;

/**
 * 云图Item list展现，进入APP先提示定位。如果定位成功，按照当前位置10km周边搜索；如果定位失败，按照默认城市北京搜索
 * 点击城市名称可选择该城市某个区或者切换城市进行本地搜索。
 *
 * @author ligen
 */
public class IndexActivity2 extends BaseActivity implements OnClickListener,
        PullToRefreshBase.OnRefreshListener<ListView>, PullToRefreshBase.OnLastItemVisibleListener,
        OnCloudSearchListener, OnScrollListener, AMapLocationListener {

    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    //侧边栏标题
    private List<TextView> mSidebarMenus = new ArrayList<>();
    private MyActionBarDrawerToggle mActionBarDrawerToggle;

    private static final String WHOLE_CITY = "全城";
    private final int CITY_CHOOSE_REQUEST_CODE = 10;
    private final int POI_CHOOSE_REQUEST_CODE = 20;

    private ImageView mBtnMap;
    private ArrayList<City> mCityList;
    private ArrayList<String> mCityLetterList;
    private HashMap<String, Integer> mCityMap;
    private TextView mCurrentCityDistrictTextview;
    private String mCurrentDistrict;
    private AMapLocationClientOption mAMapLocationClientOption;
    private AMapLocationClient mAMapLocationClient = null;
    private PullToRefreshListView mPullRefreshListView;

    // 快捷搜索词
    private String[] mLetterStrs = {"常", "A", "B", "C", "D", "E", "F", "G",
            "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
            "U", "V", "W", "X", "Y", "Z"};


    private AllUserBooksListAdapter mAdapter;
    private CloudSearch mCloudSearch;
    private CloudSearch.Query mQuery;
    private LatLonPoint mCenterPoint = new LatLonPoint(39.911823, 116.394829);
    private String mKeywords = "";
    //,跳转到地图页面时携带的兴趣点集合--地图中的点（基本上等同于用户的集合，但不是图书的集合）
    private ArrayList<CloudItem> mCoudItemList = new ArrayList<>();
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
            mListViewItems.clear();
            mAdapter.notifyDataSetChanged();
            searchByLocal(0);
            mPopupWindow.dismiss();
        }
    };
    private String[] mDistrictsOfCurrentCity;
    private int mChosenDistrictIndex = -1;
    private int mFirstVisibleItem;
    private int mVisibleItemCount;
    private EditText minputEditText;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_index2;
    }

    @Override
    protected void initView(Bundle saveInstanceState) {
        GlobalParams.isDrawerOpened = false;
        mApplicationContext = this.getApplicationContext();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.id_drawerlayout);
        TextView item1 = (TextView) findViewById(R.id.tv_ihave);
        TextView item3 = (TextView) findViewById(R.id.tv_setting);
        TextView item4 = (TextView) findViewById(R.id.tv_check_update);
        TextView item5 = (TextView) findViewById(R.id.tv_about);
        TextView item6 = (TextView) findViewById(R.id.tv_log_out);
        item1.setOnClickListener(this);
        item3.setOnClickListener(this);
        item4.setOnClickListener(this);
        item5.setOnClickListener(this);
        item6.setOnClickListener(this);
        mSidebarMenus.add(item1);
        mSidebarMenus.add(item3);
        mSidebarMenus.add(item4);
        mSidebarMenus.add(item5);
        mToolbar = (Toolbar) findViewById(R.id.id_toolbar);
        // 设置显示Toolbar
        setSupportActionBar(mToolbar);
        // 设置Drawerlayout开关指示器，即Toolbar最左边的那个icon
        mActionBarDrawerToggle =
                new MyActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open, R.string.close);

        mActionBarDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);



        // 注册云图搜索监听
        mCloudSearch = new CloudSearch(this);
        mCloudSearch.setOnCloudSearchListener(this);
        showToastDialog(Const.LODING_LOCATION);

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

    @Override
    protected void initData() {
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

            if(GlobalParams.isDrawerOpened){
                mActionBarDrawerToggle.toggle();
                return true;
            }

            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(
                        this,
                        getResources().getString(R.string.press_more_then_exit),
                        Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                MobclickAgent.onKillProcess(mContext);
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
        showToastDialog(Const.LODING_GET_DATA);
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
     * @param pagenum pageNO
     */
    private void searchByLocal(int pagenum) {
        mCurrentSearchType = LOCAL_SEARCH_TYPE;
        if (mCoudItemList == null || mCoudItemList.size() == 0) {
            mCurrentPageNum = 0;
        } else {
            mCurrentPageNum = pagenum;
        }
        showToastDialog(Const.LODING_GET_DATA);
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
        mCurrentCityDistrictTextview = (TextView) findViewById(R.id.current_city_district_textview);
        mUpDownArrow = (ImageView) findViewById(R.id.up_down_arrow);

        findViewById(R.id.btn_area_choose).setOnClickListener(this);

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
        mAdapter = new AllUserBooksListAdapter(mApplicationContext, mListViewItems);
        // You can also just use setListAdapter(mAdapter) or
        // mPullRefreshListView.setAdapter(mAdapter)
        actualListView.setAdapter(mAdapter);
        mPullRefreshListView.setOnScrollListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_area_choose:
                if(GlobalParams.isDrawerOpened){
                    mActionBarDrawerToggle.toggle();
                }
                showAreaPopupWindow();
                break;

            case R.id.btn_map:
                gotoMapActivity();
                break;

            case R.id.input_edittext:
                gotoKeywordInputActivity();
                break;
            case R.id.tv_ihave:
                gotoActivity(PersonalCenter.class,false);
                mActionBarDrawerToggle.toggle();
                break;
            case R.id.tv_setting:
                break;
            case R.id.tv_check_update:
                break;
            case R.id.tv_about:
                gotoActivity(AboutUsAct.class,false);
                mActionBarDrawerToggle.toggle();
                break;
            case R.id.tv_log_out:
                SharedPreferencesUtils.putBoolean(SharedPreferencesUtils.AUTO_LOGIN,false);
                gotoActivity(LoginAct.class,true);
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
        Intent intent = new Intent(this, MapActivity.class);
        intent.putParcelableArrayListExtra(BundleFlag.CLOUD_ITEM_LIST,mCoudItemList);
        startActivity(intent);
    }

    private void showAreaPopupWindow() {

        mPopupWindow = new CityChoosePopupWindow(this,
                mPopupWindowClickListener);

        mPopupWindow.showAsDropDown(mToolbar);
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
                HashMap<String, Integer> tempCityHashMap = new HashMap<>();
                ArrayList<String> temp_city_letter_list = new ArrayList<>();
                ArrayList<City> tempCityList = new ArrayList<>();
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
            mListViewItems.clear();
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
            mListViewItems.clear();
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
     * @param city city
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
        mPullRefreshListView.onRefreshComplete();
        mPullRefreshListView.clearAnimation();

        if (errorCode == Const.NO_ERROR && result != null) {
            ArrayList<CloudItem> cloudResult = result.getClouds();
            if (cloudResult != null && cloudResult.size() > 0) {
                mCoudItemList.addAll(cloudResult);
               parseData(cloudResult);
            } else {
                dismissProgress();
                Toast.makeText(mApplicationContext,
                        R.string.error_no_more_item, Toast.LENGTH_SHORT).show();
            }
        } else if (errorCode == Const.ERROR_CODE_SOCKE_TIME_OUT) {
            dismissProgress();
            UIUtil.showToastSafe(this.getApplicationContext(),R.string.error_socket_timeout);

        } else if (errorCode == Const.ERROR_CODE_UNKNOW_HOST) {
            dismissProgress();
            UIUtil.showToastSafe(this.getApplicationContext(),R.string.error_network);
        } else if (errorCode == Const.ERROR_CODE_FAILURE_AUTH) {
            dismissProgress();
            UIUtil.showToastSafe(this.getApplicationContext(),R.string.error_key);
        } else if (errorCode == Const.ERROR_CODE_SCODE) {
            dismissProgress();
            UIUtil.showToastSafe(this.getApplicationContext(),R.string.error_scode);
        } else if (errorCode == Const.ERROR_CODE_TABLEID) {
            dismissProgress();
            UIUtil.showToastSafe(this.getApplicationContext(),R.string.error_table_id);
        } else {
            dismissProgress();
            UIUtil.showToastSafe(this.getApplicationContext(),UIUtil.getString(R.string.error_other)+errorCode);
        }

    }

    private ArrayList<IndexItem> mListViewItems = new ArrayList<>();
    private boolean isRefresh;//是否是下拉刷新，默认false
    /**
     * 解析数据（将每个坐标点数据中包含的书籍全部解析出来）
     * @param cloudResult
     */
    private void parseData(ArrayList<CloudItem> cloudResult) {

        List<RequestParamGetBookInfos> params = new ArrayList<>();
        for(int i=0;i<cloudResult.size();i++){
            //遍历地图每个点
            CloudItem cloudItem = cloudResult.get(i);
            String bookIds = cloudItem.getCustomfield().get("bookIds");
            Integer uid = Integer.parseInt(cloudItem.getCustomfield().get("uid"));
            List<String> ids = Arrays.asList( bookIds.split("#"));
            RequestParamGetBookInfos param = new RequestParamGetBookInfos(ids,uid,cloudItem.getTitle(),cloudItem.getSnippet(),cloudItem.getLatLonPoint().getLatitude(),cloudItem.getLatLonPoint().getLongitude(),cloudItem.getDistance());
            params.add(param);
        }

        getBookPresenter().getBookInfosByBookIds(params, new CallBack() {
            @Override
            public void onSuccess(Object obj) {
                final List<ResponseBooks> responseBookses = JSON.parseArray((String) obj, ResponseBooks.class);
                if(responseBookses!=null && responseBookses.size()>0){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dismissProgress();
                            if(isRefresh){
                                mListViewItems.clear();
                            }

                            for(int i=0;i<responseBookses.size();i++){
                                List<BookDetail2> bookDetails = responseBookses.get(i).getBookDetails();
                                for(int j=0;j<bookDetails.size();j++){
                                    IndexItem item = new IndexItem();
                                    item.setAddress(responseBookses.get(i).getAddress());
                                    item.setLat(responseBookses.get(i).getLat());
                                    item.setLongi(responseBookses.get(i).getLongi());
                                    item.setmTitle(responseBookses.get(i).getmTitle());
                                    item.setUid(responseBookses.get(i).getUid());
                                    item.setDistance(responseBookses.get(i).getDistance());
                                    item.setBookDetail(bookDetails.get(j));
                                    mListViewItems.add(item);
                                }
                            }
                            if (mListViewItems == null || mListViewItems.size() == 0) {
                                dismissProgress();
                                // mPullRefreshListView.setMode(Mode.DISABLED);
                                mLLYNoData.setVisibility(View.VISIBLE);
                            } else {
                                dismissProgress();
                                mLLYNoData.setVisibility(View.GONE);
                                // mPullRefreshListView.setMode(Mode.PULL_FROM_END);
                            }
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                }

            }

            @Override
            public void onFailure(Object obj) {

            }
        });
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
        dismissProgress();
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
