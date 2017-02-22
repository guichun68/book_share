package zyzx.linke.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.PopupWindowCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;

import java.util.zip.Inflater;

import zxing.CaptureActivity;
import zyzx.linke.R;
import zyzx.linke.activity.amap.GeoFence_Activity;

/**
 * Created by austin on 2017/2/17.
 * Desc: 登录后的主页面
 */

public class HomeAct extends BaseActivity implements AMapLocationListener, AMap.OnMapClickListener, LocationSource {
    private static final int CAMERA_REQUEST_CODE = 200;
    private MapView mMapView;
    private AMap mAMap;
    private ImageView ivScan;
    private AMapLocationClientOption mLocationOption = null;
    private AMapLocationClient mlocationClient;
    private OnLocationChangedListener mListener;
    private BitmapDescriptor icon = BitmapDescriptorFactory
            .fromResource(R.mipmap.book);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMapView.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.act_home;
    }

    @Override
    protected void initView() {
        ivScan = (ImageView) findViewById(R.id.iv_scan);
        mMapView = (MapView) findViewById(R.id.map);
        ivScan.setOnClickListener(this);
        mTitleText.setText("自由自行");
//        mBackBtn.setImageResource(R.mipmap.me);
        mBackBtn.setClickable(true);
        mBackBtn.setOnClickListener(this);
        mRightBtn.setClickable(true);
        mRightBtn.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        setUpMap();
    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {

        if (mAMap == null) {
            mAMap = mMapView.getMap();
            //设置可以旋转地图
            mAMap.getUiSettings().setRotateGesturesEnabled(true);
            mAMap.moveCamera(CameraUpdateFactory.zoomBy(14));
        }
        markerOption = new MarkerOptions().draggable(true);
        mAMap.setOnMapClickListener(this);
        mAMap.setLocationSource(this);// 设置定位监听

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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_img:
                PopupWindow popupWindow = new PopupWindow(mContext);
                popupWindow.setContentView(View.inflate(mContext,R.layout.act_about_us,null));

                break;
            case R.id.iv_scan:
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

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
       if (mListener != null && amapLocation != null) {
            if (amapLocation != null && amapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode() + ": "
                        + amapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
            }
        }
    }

    private MarkerOptions markerOption = null;
    // 中心点坐标
    private LatLng centerLatLng = null;
    // 中心点marker
    private Marker centerMarker;
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

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }


}
