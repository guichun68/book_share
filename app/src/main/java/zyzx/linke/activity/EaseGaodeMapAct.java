package zyzx.linke.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;

import zyzx.linke.R;
import zyzx.linke.base.BaseActivity;
/**
 * Created by austin on 2017/4/4.
 * Desc: 发送当前位置--高德定位
 */
public class EaseGaodeMapAct extends BaseActivity implements LocationSource, AMapLocationListener, AMap.OnMapLoadedListener {
    private String address;
    private Double latitude,longitude;
    private MapView mMapView;
    private AMap mAMap;
    private boolean isNeedLoc;//是否需要定位(如果是从消息页中点击地图消息进来，则仅展示，不需要定位)
    private Button btnSend;
    /**
     * 定位用
     */
    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;

    @Override
    protected int getLayoutId() {
        return R.layout.act_gaodemap;
    }

    private void getIntentData() {
        Bundle bundle = getIntent().getExtras();
        if(bundle==null)return;
        address = bundle.getString("address");
        latitude = bundle.getDouble("latitude");
        longitude = bundle.getDouble("longitude");
    }

    @Override
    protected void initView(Bundle saveInstanceState) {
        getIntentData();
        setUpInteractiveControls();
        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(saveInstanceState);
        if(mAMap==null){
            mAMap= mMapView.getMap();
            setUpMap();
        }
     }

    /**
     * 设置所有可点击的控件
     */
    private void setUpInteractiveControls() {
        isNeedLoc=longitude==null||latitude==null;
        btnSend = (Button) findViewById(R.id.btn_location_send);
        btnSend.setOnClickListener(this);
        if(isNeedLoc)btnSend.setVisibility(View.VISIBLE);else btnSend.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void initData() {
        LatLng latLng = new LatLng(latitude,longitude);
        Marker marker = mAMap.addMarker(new MarkerOptions().position(latLng).title("位置").snippet(address));
    }

    /**
     * 地图用3D地图显示，可缩放和拖动；
     */
    private void setMapUi() {
        UiSettings mUiSettings=mAMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setScaleControlsEnabled(true);

        mUiSettings.setRotateGesturesEnabled(false);
        mUiSettings.setTiltGesturesEnabled(false);
        if(!isNeedLoc){
            mUiSettings.setMyLocationButtonEnabled(false);// 设置不显示定位按钮
        }else{
            mUiSettings.setMyLocationButtonEnabled(true);
        }
        // 自定义系统定位蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        // 自定义定位蓝点图标
        myLocationStyle.myLocationIcon(
                BitmapDescriptorFactory.defaultMarker());
        // 自定义精度范围的圆形边框颜色
        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));
        myLocationStyle.strokeColor(Color.TRANSPARENT);
        // 自定义精度范围的圆形边框宽度
        myLocationStyle.strokeWidth(0);
        // 设置圆形的填充颜色 为半透明蓝色（浅蓝）
        myLocationStyle.radiusFillColor(Color.TRANSPARENT);
        // 将自定义的 myLocationStyle 对象添加到地图上
        mAMap.setMyLocationStyle(myLocationStyle);
        mAMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        mAMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    private void setUpMap() {
        mAMap.setLocationSource(this);
        setMapUi();
        setMapListener();
    }

    /**
     * 为地图增加一些事件监听
     */
    private void setMapListener() {
        mAMap.setOnMapLoadedListener(this);// 设置amap加载成功事件监听器
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
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
        dismissProgress();
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if(!isNeedLoc){
            return;
        }
        dismissProgress();
        if (mListener != null && amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {/*amapLocation.getCity();*/
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                latitude = amapLocation.getLatitude();
                longitude = amapLocation.getLongitude();
                address = amapLocation.getAddress();
            } else{
                String errText = "定位失败," + amapLocation.getErrorCode() + ": "
                        + amapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
                Toast.makeText(this,"定位失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.btn_location_send:
                if(longitude.intValue()==0|| latitude.intValue()==0){
                    Toast.makeText(this,"未能获取位置,请重新定位",Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = this.getIntent();
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                intent.putExtra("address", address);
                this.setResult(RESULT_OK, intent);
                finish();
                overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                break;
        }
    }

    public void back(View v){
        finish();
    }

    @Override
    public void onMapLoaded() {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude));
        mAMap.moveCamera(cameraUpdate);
        mAMap.moveCamera(CameraUpdateFactory.zoomTo(15));
    }
}
