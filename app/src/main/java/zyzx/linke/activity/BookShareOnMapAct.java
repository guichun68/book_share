package zyzx.linke.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;

import java.util.ArrayList;
import java.util.List;

import zyzx.linke.R;
import zyzx.linke.constant.BundleFlag;
import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.AMapCreateItemResultVO;
import zyzx.linke.model.bean.BookDetail2;
import zyzx.linke.overlay.PoiOverlay;
import zyzx.linke.utils.AMapUtil;
import zyzx.linke.utils.CustomProgressDialog;
import zyzx.linke.constant.GlobalParams;
import zyzx.linke.utils.StringUtil;
import zyzx.linke.utils.UIUtil;

/**
 * Created by austin on 2017/2/27.
 * Desc: 地图选点分享图书
 */

public class BookShareOnMapAct extends BaseActivity implements Inputtips.InputtipsListener,AMapLocationListener, AMap.OnMapClickListener, LocationSource,PoiSearch.OnPoiSearchListener, GeocodeSearch.OnGeocodeSearchListener, AMap.OnMarkerClickListener {
    ArrayList<Tip> suggest = new ArrayList<>();
    private String keyWord;
    private MapView mMapView;
    private AMap mAMap;
    private MyTextWatcher myTextWatcher;
    private Button btnSearch,btnCurrPosition,btnOK,btnNextPage;

    private BitmapDescriptor icon = BitmapDescriptorFactory
            .fromResource(R.mipmap.book);
    private AppCompatAutoCompleteTextView actv;
    private MyItemAdapter adapter;
    private AMapLocationClientOption mLocationOption = null;
    private AMapLocationClient mlocationClient;
    private LocationSource.OnLocationChangedListener mListener;

    private MarkerOptions markerOption = null;
    // 中心点坐标
    private LatLng centerLatLng = null;
    // 中心点marker
    private Marker centerMarker;
    private Dialog mProgressDialog;//搜索时的不确定进度条

    private int currentPage = 0;// 当前页面，从0开始计数
    private PoiSearch.Query query;// Poi查询条件类
    private PoiSearch poiSearch;// POI搜索
    private PoiResult poiResult; // poi返回的结果
    private BookDetail2 mBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMapView.onCreate(savedInstanceState);
//        Bundle extras = getIntent().getExtras();
//        mBook = extras.getParcelable("book");
        mBook = (BookDetail2) getIntent().getSerializableExtra(BundleFlag.BOOK);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
        deactivate();
    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {

        if (mAMap == null) {
            mAMap = mMapView.getMap();
            //设置可以旋转地图
            mAMap.getUiSettings().setRotateGesturesEnabled(true);
        }
        markerOption = new MarkerOptions().draggable(true);
        mAMap.setOnMapClickListener(this);
        mAMap.setLocationSource(this);// 设置定位监听
        mAMap.setOnMarkerClickListener(this);// 设置点击marker事件监听器

        mAMap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
            @Override
            public void onMapLoaded() {
                mAMap.moveCamera(CameraUpdateFactory.zoomTo(14));
            }
        });

        mAMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        // 自定义系统定位蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        // 自定义定位蓝点图标
        myLocationStyle.myLocationIcon(
                BitmapDescriptorFactory.fromResource(R.drawable.gps_point));
        // 自定义精度范围的圆形边框颜色
        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));
        // 自定义精度范围的圆形边框宽度
        myLocationStyle.strokeWidth(0);
        // 设置圆形的填充颜色 为半透明蓝色（浅蓝）
        myLocationStyle.radiusFillColor(Color.argb(102, 102, 204, 255));
        // 将自定义的 myLocationStyle 对象添加到地图上
        mAMap.setMyLocationStyle(myLocationStyle);
        mAMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        mAMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
    }


    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            // 设置定位监听
            mlocationClient.setLocationListener(this);
            // 设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            // 只是为了获取当前位置，所以设置为单次定位
            mLocationOption.setOnceLocation(true);
            // 设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            mlocationClient.startLocation();
        }
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_share_book_map;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        btnSearch = (Button) findViewById(R.id.btn_search);
        btnCurrPosition = (Button) findViewById(R.id.btn_curr_position);
        btnOK = (Button) findViewById(R.id.btn_ok);
        btnNextPage = (Button) findViewById(R.id.btn_next_page);
        btnSearch.setOnClickListener(this);
        btnCurrPosition.setOnClickListener(this);
        btnOK.setOnClickListener(this);
        btnNextPage.setOnClickListener(this);

        mProgressDialog = CustomProgressDialog.getNewProgressBar(this);
        mTitleText.setText("在地图中分享");
        actv = (AppCompatAutoCompleteTextView)findViewById(R.id.auto_tv);

        mMapView = (MapView) findViewById(R.id.mv);
        adapter = new MyItemAdapter(new ArrayList<Tip>());
        actv.setAdapter(adapter);
        myTextWatcher = new MyTextWatcher();
        actv.addTextChangedListener(myTextWatcher);

        actv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Tip tip = (Tip)parent.getItemAtPosition(position);
                keyWord = tip.getName();
                actv.setText(keyWord);

                if(tip.getPoint()==null){
                    Log.e("zyzx","NullPointException");
                }else{
                    Log.i("zyzx","not null");
                    mClickPoint = tip.getPoint();
                    LatLng latLng = new LatLng(mClickPoint.getLatitude(),mClickPoint.getLongitude());
                    CameraUpdate update = CameraUpdateFactory.newCameraPosition(new CameraPosition(
                            latLng, 17, 0, 0));
                    mAMap.animateCamera(update, 1000, null);
//                    onMapClick(latLng);
                    mAMap.clear();// 清理之前的图标

                    PoiItem item = new PoiItem(null,mClickPoint,keyWord,null);
                    if(poiItems!=null){
                        poiItems.clear();
                    }else{
                        poiItems = new ArrayList<PoiItem>();
                    }
                    poiItems.add(item);
                    PoiOverlay poiOverlay = new PoiOverlay(mAMap, poiItems);
                    poiOverlay.removeFromMap();
                    poiOverlay.addToMap();
                }

            }
        });

        setUpMap();
    }
    List<PoiItem> poiItems;
    //搜索POI（兴趣点）的回调
    @Override
    public void onPoiSearched(PoiResult result, int rCode) {
        CustomProgressDialog.dismissDialog(mProgressDialog);// 隐藏对话框
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getQuery() != null) {// 搜索poi的结果
                if (result.getQuery().equals(query)) {// 是否是同一条
                    poiResult = result;
                    // 取得搜索到的poiitems有多少页
                    poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                    List<SuggestionCity> suggestionCities = poiResult
                            .getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息

                    if (poiItems != null && poiItems.size() > 0) {
                        mAMap.clear();// 清理之前的图标
                        PoiOverlay poiOverlay = new PoiOverlay(mAMap, poiItems);
                        poiOverlay.removeFromMap();
                        poiOverlay.addToMap();
                        poiOverlay.zoomToSpan();
                    } else if (suggestionCities != null
                            && suggestionCities.size() > 0) {
                        showSuggestCity(suggestionCities);
                    } else {
                        UIUtil.showToastSafe(R.string.no_result);
                    }
                }
            } else {
                UIUtil.showToastSafe(R.string.no_result);
            }
        } else {
            UIUtil.showMapError(this,rCode);
        }
    }

    /**
     * poi没有搜索到数据，返回一些推荐城市的信息
     */
    private void showSuggestCity(List<SuggestionCity> cities) {
        String infomation = "推荐城市\n";
        for (int i = 0; i < cities.size(); i++) {
            infomation += "城市名称:" + cities.get(i).getCityName() + "城市区号:"
                    + cities.get(i).getCityCode() + "城市编码:"
                    + cities.get(i).getAdCode() + "\n";
        }
        UIUtil.showToastSafe(infomation);
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }
    String addressName="";
    //逆地理编码回调
    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        CustomProgressDialog.dismissDialog(mProgressDialog);
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {
                if(StringUtil.isEmpty(result.getRegeocodeAddress().getFormatAddress())){
                    addressName = "未识别区域点";
                }else{
                    addressName=result.getRegeocodeAddress().getFormatAddress()+"附近";
                }
                mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        AMapUtil.convertToLatLng(mClickPoint), mAMap.getCameraPosition().zoom));
//                regeoMarker.setPosition(AMapUtil.convertToLatLng(latLonPoint));
                UIUtil.showToastSafe(addressName);
                actv.setText(addressName);
            } else {
                UIUtil.showToastSafe(R.string.no_result);
            }
        } else {
            UIUtil.showMapError(this, rCode);
        }


        mAMap.clear();// 清理之前的图标
        if(poiItems==null){
            poiItems = new ArrayList<>();
        }else{
            if(poiItem !=null){
                poiItems.remove(poiItem);
            }
        }
        //TODO PoiItem：构造方法第二个参数：店名；第三个参数：地址
        poiItem= new PoiItem("1",mClickPoint,addressName,null);

        poiItems.add(poiItem);
        PoiOverlay poiOverlay = new PoiOverlay(mAMap, poiItems);
        poiOverlay.removeFromMap();
        poiOverlay.addToMap();


    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {}

    @Override
    public boolean onMarkerClick(Marker marker) {
//        addressName
        mClickPoint = new LatLonPoint(marker.getPosition().latitude,marker.getPosition().longitude);
        addressName = marker.getTitle();
        actv.setText(addressName);
        return false;
    }

    class MyTextWatcher implements TextWatcher{
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            if (s.toString().length() < 2) {
                return;
            }
            keyWord = s.toString();

            //第二个参数传入null或者“”代表在全国进行检索，否则按照传入的city进行检索
            InputtipsQuery inputquery = new InputtipsQuery(keyWord, GlobalParams.gCurrCity);
            inputquery.setCityLimit(true);//限制在当前城市
            Inputtips inputTips = new Inputtips(BookShareOnMapAct.this, inputquery);
            inputTips.setInputtipsListener(BookShareOnMapAct.this);
            inputTips.requestInputtipsAsyn();
        }
    }

    @Override
    protected void initData() {
        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);
    }

    @Override
    public void onGetInputtips(List<Tip> list, int i) {
        suggest.clear();
        suggest.addAll(list);
        adapter.setList(suggest);
        adapter.notifyDataSetChanged();
    }
    private String mCurrAddrStr=null;//当前地址（定位获得）
    private Double mCurrLati,mCurrLongti;//当前位置信息(通过定位获得)
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener != null && aMapLocation != null) {
            if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(aMapLocation);// 显示系统小蓝点
                GlobalParams.gCurrCity=aMapLocation.getCity();
                mCurrAddrStr = aMapLocation.getAddress();
                mCurrLati = aMapLocation.getLatitude();
                mCurrLongti = aMapLocation.getLongitude();
            } else {
                String errText = "定位失败," + aMapLocation.getErrorCode() + ": "
                        + aMapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
            }
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.btn_search:
                if(StringUtil.isEmpty(actv.getText().toString())){
                    UIUtil.showToastSafe("请输入关键字");
                    return;
                }else{
                    doSearchQuery();
                }
                break;
            case R.id.btn_curr_position:
                if(mCurrLati==null || mCurrLongti==null){
                    UIUtil.showToastSafe("未能获取当前位置信息");
                    return;
                }
                mClickPoint = new LatLonPoint(mCurrLati,mCurrLongti);
                actv.setText(mCurrAddrStr);
                break;
            case R.id.btn_ok:
                if(mClickPoint==null){
                    UIUtil.showToastSafe("请选择具体的点");
                    return;
                }
                if(mProgressDialog==null){
                    mProgressDialog= CustomProgressDialog.getNewProgressBar(mContext);
                }
                mProgressDialog.show();
//                Log.e("zyzx",GlobalParams.gUser.getUserid()+"");
                    GlobalParams.getBookPresenter().addBook2Map(mBook, GlobalParams.gUser.getUserid(),false, mClickPoint.getLatitude(), mClickPoint.getLongitude(), new CallBack() {
                        @Override
                        public void onSuccess(Object obj) {
                            CustomProgressDialog.dismissDialog(mProgressDialog);
                            String json = (String)obj;
                            if(StringUtil.isEmpty(json)){
                                UIUtil.showToastSafe("分享失败");
                                return;
                            }
                            AMapCreateItemResultVO aCreateResultVo = JSON.parseObject(json, AMapCreateItemResultVO.class);
                            Intent intent = new Intent();
                            intent.putExtra("map_id",aCreateResultVo.get_id());
                            setResult(1001,intent);
                            finish();
                            UIUtil.showToastSafe("分享成功");
                        }

                        @Override
                        public void onFailure(Object obj) {
                            CustomProgressDialog.dismissDialog(mProgressDialog);
                            if(obj!=null)
                            UIUtil.showToastSafe(obj.toString());
                        }
                    });
                break;
            case R.id.btn_next_page:
                nextButton();
                break;
        }
    }

    /**
     * 发现已分享同名的书籍了，是否继续分享 dialog
     */
    private void showSameBookShareDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("您已经在该点分享过此书了，无须重复分享!");
        dialog.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
               /*    if(mClickPoint==null){
                    UIUtil.showToastSafe("请返回地图选择具体的点");
                    return;
                }
             GlobalParams.getBookPresenter().addBook2Map(bookId, GlobalParams.gUser.getUserid(),true, mClickPoint.getLatitude(), mClickPoint.getLongitude(), new CallBack() {
                    @Override
                    public void onSuccess(Object obj) {
                        String resJson = (String) obj;
                        JSONObject jsonObject = JSON.parseObject(resJson);
                        int code = jsonObject.getInteger("code");
                        if (code==200){
                            UIUtil.showToastSafe("添加分享成功");
                            finish();
                        }else{
                            UIUtil.showToastSafe("添加分享失败");
                        }
                    }

                    @Override
                    public void onFailure(Object obj) {
                        UIUtil.showToastSafe(obj.toString());
                    }
                });*/
            }
        });
        dialog.setPositiveButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * 点击下一页按钮
     */
    public void nextButton() {
        if (query != null && poiSearch != null && poiResult != null) {
            if (poiResult.getPageCount() - 1 > currentPage) {
                currentPage++;
                query.setPageNum(currentPage);// 设置查后一页
                poiSearch.searchPOIAsyn();
            } else {
                UIUtil.showToastSafe(R.string.no_next_page);
            }
        }
    }


    /**
     * 开始进行poi搜索
     */
    protected void doSearchQuery() {
        mProgressDialog.show();// 显示进度框
        currentPage = 0;
        query = new PoiSearch.Query(keyWord, "", GlobalParams.gCurrCity);// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(10);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页

        poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();
    }
    PoiItem poiItem;
    private GeocodeSearch geocoderSearch;
    private LatLonPoint mClickPoint;

    @Override
    public void onMapClick(LatLng latLng) {
        if(mProgressDialog==null){
            mProgressDialog = CustomProgressDialog.getNewProgressBar(this);
        }
        mProgressDialog.show();
        //通过逆地理编码来获得用户选择点的中文地址信息
        mClickPoint = new LatLonPoint(latLng.latitude,latLng.longitude);
        RegeocodeQuery query = new RegeocodeQuery(mClickPoint, 200,
                GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        geocoderSearch.getFromLocationAsyn(query);// 设置异步逆地理编码请求
    }

    private void addCenterMarker(LatLng latlng) {
        /*if (null == centerMarker) {
            centerMarker = mAMap.addMarker(markerOption);
        }
        centerMarker.setPosition(latlng);*/
    }

    class MyItemAdapter extends BaseAdapter implements Filterable {
        // 自定义的过滤器
        private SearchFilter _filter;
        private ArrayList<Tip> list;

        MyItemAdapter(ArrayList<Tip> list) {
            this.list = list;
        }

        private void setList(ArrayList<Tip> list) {
            if (this.list != null) {
                this.list.clear();
                this.list.addAll(list);
            } else {
                this.list = list;
            }
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Tip getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MyViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(BookShareOnMapAct.this, R.layout.item_map_tip, null);
                holder = new MyViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (MyViewHolder) convertView.getTag();
            }
            holder.tvAddress.setText(getItem(position).getName());
            if(StringUtil.isEmpty(getItem(position).getDistrict())){
                holder.tvDistrict.setText(null);
            }else{
                holder.tvDistrict.setText(getItem(position).getDistrict());
            }
            return convertView;
        }

        @Override
        public Filter getFilter() {
            if (_filter == null) {
                _filter = new SearchFilter();
            }
            return _filter;
        }

        // 内部类：数据过滤器
        class SearchFilter extends Filter {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                // 定义过滤规则
                FilterResults filterResults = new FilterResults();

                // 如果搜索框内容为空，就恢复原始数据
                if (TextUtils.isEmpty(constraint)) {
                    synchronized (BookShareOnMapAct.this) {
                        filterResults.values = "";
                        filterResults.count = 0;
                    }
                } else {
                    filterResults.values = constraint;
                    filterResults.count = list.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {
                if (results.count > 0) {
                    notifyDataSetChanged();// 通知数据发生了改变
                } else {
                    notifyDataSetInvalidated();// 通知数据失效
                }
            }
        }

        class MyViewHolder {
            private final TextView tvAddress;
            private final TextView tvDistrict;

            MyViewHolder(View root) {
                this.tvAddress = (TextView) root.findViewById(R.id.tv_address);
                this.tvDistrict = (TextView) root.findViewById(R.id.tv_district);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        if (null != mlocationClient) {
            mlocationClient.onDestroy();
        }
    }
}
