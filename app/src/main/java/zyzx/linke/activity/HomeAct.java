/*
package zyzx.linke.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.cloud.CloudItem;
import com.amap.api.services.cloud.CloudItemDetail;
import com.amap.api.services.cloud.CloudResult;
import com.amap.api.services.cloud.CloudSearch;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import zxing.CaptureActivity;
import zyzx.linke.R;
import zyzx.linke.constant.Const;
import zyzx.linke.activity.amap.GeoFence_Activity;
import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.MarkerStatus;
import zyzx.linke.utils.CustomProgressDialog;
import zyzx.linke.constant.GlobalParams;
import zyzx.linke.utils.UIUtil;


*/
/**
 * 已废弃
 * Created by austin on 2017/2/17.
 * Desc: 登录后的主页面
 *//*


public class HomeAct extends BaseActivity implements AMapLocationListener, AMap.OnMapClickListener, LocationSource, AMap.OnMarkerDragListener, AMap.OnMapLoadedListener, AMap.OnMarkerClickListener, AMap.OnInfoWindowClickListener, AMap.InfoWindowAdapter, CloudSearch.OnCloudSearchListener {
    private Dialog mProgressDialog = null;
    private static final int CAMERA_REQUEST_CODE = 200;
    private MapView mMapView;
    private AMap mAMap;
    private UiSettings mUiSettings;
    private ImageView ivScan;
    private AMapLocationClientOption mLocationOption = null;
    private AMapLocationClient mlocationClient;
    private OnLocationChangedListener mListener;
    private MarkerOptions markerOption = null;
    private Button mBtnNextPage;
    private int mCurrentPageNum = 0;

    // 中心点坐标
    private LatLng centerLatLng = null;
    // 中心点marker
    private Marker centerMarker;
    //位置，默认天安门广场
    private LatLonPoint mCenterPoint = new LatLonPoint(39.906905, 116.397541);
    private BitmapDescriptor icon = BitmapDescriptorFactory
            .fromResource(R.mipmap.book);

    private final static int LOCAL_SEARCH_TYPE = 1;

    //云图搜索引擎
    private CloudSearch mCloudSearch;
    //云图搜索结果
    private ArrayList<CloudItem> mCloudItemList = new ArrayList<CloudItem>();
    private final static int ARROUND_SEARCH_TYPE = 2;
    private int mCurrentSearchType = ARROUND_SEARCH_TYPE;
    private CloudSearch.Query mQuery;
    private String mKeywords = "";
    private MarkerStatus mLastMarkerStatus;
    private CloudItem mCurrentItem = null;
    private TextView mTextViewName;
    private TextView mTextViewAddress;

    @Override
    protected int getLayoutId() {
        return R.layout.act_home;
    }

    @Override
    protected void initView(Bundle saveInstanceState) {
        showProgressDialog(Const.LODING_LOCATION);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();

        mBtnNextPage = (Button) findViewById(R.id.btn_next_page);
        mTextViewName = (TextView) findViewById(R.id.poi_name);
        mTextViewAddress = (TextView) findViewById(R.id.poi_address);
        ivScan = (ImageView) findViewById(R.id.iv_scan);
        ivScan.setOnClickListener(this);
        mTitleText.setText("自由自行");
        mBackBtn.setClickable(true);
        mBackBtn.setOnClickListener(this);
        mRightBtn.setClickable(true);
        mRightBtn.setOnClickListener(this);
        mBtnNextPage.setOnClickListener(this);
        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(saveInstanceState);
        initMap();
    }

    private void initMap() {
        if (mAMap == null) {
            mAMap = mMapView.getMap();
            setUpMap();
        }
    }

    @Override
    protected void initData() {
    }


    */
/**
     * 设置一些amap的属性
     *//*

    private void setUpMap() {
        mAMap.setLocationSource(this);
        setMapUi();
        setMapListener();
//        addMarkersToMap();

    }

    */
/**
     * 往地图上添加marker，为列表页获得的数据
     *//*

    private void addMarkersToMap() {

        int size = mCloudItemList.size();
        for (int i = 0; i < size; i++) {

            // 根据该poi的经纬度进行marker点的添加
            MarkerOptions markerOption = new MarkerOptions();
            markerOption.draggable(true);

            if(icon == null){
                icon = BitmapDescriptorFactory.fromResource(R.mipmap.book);
            }
            markerOption.icon(icon);
            markerOption.position(new LatLng(mCloudItemList.get(i)
                    .getLatLonPoint().getLatitude(), mCloudItemList.get(i)
                    .getLatLonPoint().getLongitude()));
            Marker marker = mAMap.addMarker(markerOption);

            // 每个marker点上带有一个状态类，来说明这个marker是否是被选中的状态
            // 会根据是否被选中来决定一些事件处理
            MarkerStatus markerStatus = new MarkerStatus(i);
            markerStatus.setCloudItem(mCloudItemList.get(i));
            markerStatus.setMarker(marker);
            if (i == 0) {
                markerChosen(markerStatus);
                mLastMarkerStatus = markerStatus;
            }
            setMarkerBasedonStatus(markerStatus);
            marker.setObject(markerStatus);
        }

    }

    */
/**
     * marker被选中之后，需要更改marker的样式，以及在底部bar显示信息
     *
     * @param markerStatus
     *//*

    private void markerChosen(MarkerStatus markerStatus) {
        markerStatus.pressStatusToggle();
        mCurrentItem = (CloudItem) markerStatus.getCloudItem();
        mTextViewName.setText(mCurrentItem.getTitle());
        mTextViewAddress.setText(mCurrentItem.getSnippet());
        setMarkerBasedonStatus(markerStatus);
    }

    */
/**
     * 根据该marker的最新状态决定应该显示什么样的marker
     *
     * @param status
     *//*

    private void setMarkerBasedonStatus(MarkerStatus status) {
        if (status.getIsPressed()) {
            status.getMarker().setIcon(
                    BitmapDescriptorFactory.fromBitmap(BitmapFactory
                            .decodeResource(getResources(),
                                    status.getmResPressed())));
        } else {
            status.getMarker().setIcon(
                    BitmapDescriptorFactory.fromBitmap(BitmapFactory
                            .decodeResource(getResources(),
                                    status.getmResUnPressed())));
        }
    }


    private void setMapListener() {
        mAMap.setOnMapClickListener(this);
//        mAMap.setLocationSource(this);// 设置定位监听
        //注册云图搜索监听
        mCloudSearch.setOnCloudSearchListener(this);
        mAMap.setOnMarkerDragListener(this);// 设置marker可拖拽事件监听器
        mAMap.setOnMapLoadedListener(this);// 设置amap加载成功事件监听器
        mAMap.setOnMarkerClickListener(this);// 设置点击marker事件监听器
        mAMap.setOnInfoWindowClickListener(this);// 设置点击infoWindow事件监听器
        mAMap.setInfoWindowAdapter(this);// 设置自定义InfoWindow样式
    }

    private void setMapUi() {
        // 注册云图搜索
        mCloudSearch = new CloudSearch(this);
        mUiSettings = mAMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setScaleControlsEnabled(true);
        mUiSettings.setRotateGesturesEnabled(false);//设置可以旋转地图
        mUiSettings.setTiltGesturesEnabled(false);
        mUiSettings.setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
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

    private PopupWindow window;
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_img:
                View popView = View.inflate(mContext,R.layout.popwindow,null);
                window = new PopupWindow(popView, UIUtil.dip2px(120),ViewGroup.LayoutParams.WRAP_CONTENT,true);
                window.setOutsideTouchable(true);
                window.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
                window.showAsDropDown(findViewById(R.id.top_title));
                popView.findViewById(R.id.ll_ihave).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gotoActivity(PersonalCenter.class,false);
                    }
                });
                popView.findViewById(R.id.ll_seting).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String,String> param =new HashMap<String, String>();
                        param.put("key","49f0e1fd42a68fcf794c5e135a357f1a");
                        param.put("mTableID","58b687cc305a2a6810d2b236");
                        param.put("data","{\"_location\": \"116.401125,39.922503\",\"_name\": \"故宫角楼故事\",  \"book_image_url\": \"http://www.wbaidu.com\",  \"uid\": \"1016\" }");

                        try {
                            GlobalParams.getgModel().post(GlobalParams.urlAddbook2Gaode, param, new CallBack() {
                                @Override
                                public void onSuccess(Object obj) {
                                    Log.i("zyzx","post success");
                                    Log.e("zyzx",obj.toString());
                                }

                                @Override
                                public void onFailure(Object obj) {
                                    Log.i("zyzx","post failure");
                                    Log.e("zyzx",obj.toString());
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                popView.findViewById(R.id.ll_record).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                break;
            case R.id.iv_scan:
                GlobalParams.gIsPersonCenterScan = false;
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    //申请CAMERA权限
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                            CAMERA_REQUEST_CODE);
                }else{
                    gotoActivity(CaptureActivity.class,false);
                }
                break;
            case R.id.right_img:
                Toast.makeText(HomeAct.this, "点击了搜索", Toast.LENGTH_SHORT).show();
                gotoActivity(GeoFence_Activity.class, false);
                break;
            case R.id.btn_next_page:

                break;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    gotoActivity(CaptureActivity.class, false);
                } else {
                    // Permission Denied
                    Toast.makeText(mContext, "未获取相机权限", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    //确定要退出App?
    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() >= 1) {
            super.onBackPressed();
            return;
        }
        showExitDialog();
//        super.onBackPressed();
    }

    private void showExitDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("确定退出?");
        dialog.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                HomeAct.this.finish();
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

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
        deactivate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        if (null != mlocationClient) {
            mlocationClient.onDestroy();
        }
        System.exit(0);
    }

    private List<Marker> mBookMarkers = new ArrayList<>();
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        CustomProgressDialog.dismissDialog(mProgressDialog);
       if (mListener != null && amapLocation != null) {
            if (amapLocation != null && amapLocation.getErrorCode() == 0) {*/
/*amapLocation.getCity();*//*

                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                GlobalParams.gCurrCity=amapLocation.getCity();
                //云存储查询周边搜索图书
                Double geoLat = amapLocation.getLatitude();
                Double geoLng = amapLocation.getLongitude();
                mCenterPoint = new LatLonPoint(geoLat, geoLng);
                // 并且设置当前的城市
//                setCurrentCity(amapLocation.getCity());
                mCurrentSearchType = ARROUND_SEARCH_TYPE;
                searchByArround(0);
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode() + ": "
                        + amapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
            }
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if(icon == null){
            icon = BitmapDescriptorFactory.fromResource(R.mipmap.book);
        }
        markerOption.icon(icon);
        centerLatLng = latLng;
        addCenterMarker(centerLatLng);
    }

    private void addCenterMarker(LatLng latlng) {
        if (null == centerMarker) {
            centerMarker = mAMap.addMarker(markerOption);
        }
        centerMarker.setPosition(latlng);
//        markerList.add(centerMarker);
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

    */
/**
     * 根据经纬度进行周边搜索
     *
     * @param pagenum
     *//*

    private void searchByArround(int pagenum) {
        mCurrentSearchType = ARROUND_SEARCH_TYPE;
        if (mCloudItemList == null || mCloudItemList.size() == 0) {
            mCurrentPageNum = 0;
        } else {
            mCurrentPageNum = pagenum;
        }

        showProgressDialog(Const.LODING_GET_DATA);
        CloudSearch.SearchBound bound = new CloudSearch.SearchBound(new LatLonPoint(
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

    @Override
    protected void onResume() {
        super.onResume();
        if(window!=null && window.isShowing()){
            window.dismiss();
        }
        mMapView.onResume();
    }


    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }

    @Override
    public void onMapLoaded() {
        mAMap.moveCamera(CameraUpdateFactory.zoomTo(14));
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (mLastMarkerStatus != null) {
            mLastMarkerStatus.pressStatusToggle();
            setMarkerBasedonStatus(mLastMarkerStatus);
        }
        MarkerStatus newMarkerStatus = (MarkerStatus) marker.getObject();
        markerChosen(newMarkerStatus);
        mLastMarkerStatus = newMarkerStatus;
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public void onCloudSearched(CloudResult result, int errorCode) {
        CustomProgressDialog.dismissDialog(mProgressDialog);
        if (errorCode == Const.NO_ERROR && result != null) {
            ArrayList<CloudItem> cloudResult = result.getClouds();
            if (cloudResult != null && cloudResult.size() > 0) {
                mCloudItemList.addAll(cloudResult);
                addMarkersToMap();
            } else {
                UIUtil.showToastSafe(R.string.error_no_more_item);
            }

        } else if (errorCode == Const.ERROR_CODE_SOCKE_TIME_OUT) {
            UIUtil.showToastSafe(R.string.error_socket_timeout);
        } else if (errorCode == Const.ERROR_CODE_UNKNOW_HOST) {
            UIUtil.showToastSafe(R.string.error_network);
        } else if (errorCode == Const.ERROR_CODE_FAILURE_AUTH) {
            UIUtil.showToastSafe(R.string.error_key);
        } else if (errorCode == Const.ERROR_CODE_SCODE) {
            UIUtil.showToastSafe(R.string.error_scode);
        } else if (errorCode == Const.ERROR_CODE_TABLEID) {
            UIUtil.showToastSafe(R.string.error_table_id);
        } else {
            UIUtil.showToastSafe(UIUtil.getString(R.string.error_other)+errorCode);
        }
        if (mCloudItemList == null || mCloudItemList.size() == 0) {
            // mPullRefreshListView.setMode(Mode.DISABLED);
//            mLLYNoData.setVisibility(View.VISIBLE);
        } else {
//            mLLYNoData.setVisibility(View.GONE);
            // mPullRefreshListView.setMode(Mode.PULL_FROM_END);
        }

    }

    @Override
    public void onCloudItemDetailSearched(CloudItemDetail cloudItemDetail, int i) {

    }
    private void showProgressDialog(String message) {
        mProgressDialog = CustomProgressDialog.createLoadingDialog(this, message);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
    }
}
*/
