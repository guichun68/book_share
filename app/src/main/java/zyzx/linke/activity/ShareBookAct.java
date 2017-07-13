package zyzx.linke.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.LocationSource;

import java.util.ArrayList;

import zyzx.linke.R;
import zyzx.linke.base.BaseActivity;
import zyzx.linke.global.BundleResult;
import zyzx.linke.model.Area;
import zyzx.linke.utils.UIUtil;

/**
 * Created by austin on 2017/7/13.
 * Desc: 图书分享
 */

public class ShareBookAct extends BaseActivity  {
    private LocationSource.OnLocationChangedListener mLocationChangeListener;
    private String mCurrPro, mCurrCity, mCurrCounty;
    private TextView tvPro,tvCity,tvCounty;
    private Button btnLocation,btnManulSel;
    private Button btnShare,btnCancel;
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption;

    @Override
    protected int getLayoutId() {
        return R.layout.act_share_book;
    }

    @Override
    protected void initView(Bundle saveInstanceState) {
        tvPro = (TextView) findViewById(R.id.tv_pro);
        tvCity = (TextView) findViewById(R.id.tv_city);
        tvCounty = (TextView) findViewById(R.id.tv_county);
        btnShare = (Button) findViewById(R.id.btn_submit);
        btnCancel = (Button) findViewById(R.id.btn_cancel);
        btnLocation = (Button) findViewById(R.id.btn_location);
        btnManulSel = (Button) findViewById(R.id.btn_sel_area);
        btnLocation.setOnClickListener(this);
        btnManulSel.setOnClickListener(this);
        btnShare .setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        initLocation();
        startLocation();
    }

    private void initLocation(){
        //初始化client
        locationClient = new AMapLocationClient(this.getApplicationContext());
        //设置定位参数
        locationClient.setLocationOption(getDefaultOption());
        // 设置定位监听
        locationClient.setLocationListener(locationListener);
    }

    @Override
    protected void initData() {
        mCurrPro = "北京";
        mCurrCity = "海淀区";
        refreshArea();
    }
    /**
     * 默认的定位参数
     * @since 2.8.0
     */
    private AMapLocationClientOption getDefaultOption(){
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(true);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        return mOption;
    }

    /**
     * 定位监听
     */
    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation loc) {
            if (null != loc) {
                //解析定位结果
                String result = UIUtil.getLocationStr(loc);
                UIUtil.showTestLog(result);

                if (loc.getErrorCode() != AMapLocation.LOCATION_SUCCESS) {
                    UIUtil.showToastSafe(R.string.locate_fail);
                    UIUtil.showTestLog(UIUtil.getString(R.string.locate_fail));
                    return;
                } else {
                    mCurrPro = loc.getProvince();
                    mCurrCity = loc.getCity();
                    mCurrCounty = loc.getDistrict();
                    refreshArea();
                    UIUtil.showToastSafe("定位成功:"+mCurrPro+" "+mCurrCity+" "+mCurrCounty);
                }
            } else {
                UIUtil.showTestLog("定位失败，loc is null");
            }
        }
    };

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.btn_location:
                if(locationClient ==null){
                    initLocation();
                }
                //手动定位
                startLocation();
                break;
            case R.id.btn_sel_area:
                startActivityForResult(new Intent(mContext,AreaSelAct.class),777);
                break;
            case R.id.btn_submit:
                UIUtil.showToastSafe("分享图书");
                break;
            case R.id.btn_cancel:
                finish();
                break;
        }
    }

    private void refreshArea() {
        tvPro.setText(mCurrPro);
        tvCity.setText(mCurrCity);
        tvCounty.setText(mCurrCounty);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 777 && resultCode == BundleResult.SUCCESS){
            //选择完地址的回调
            ArrayList<Area> areas = data.getParcelableArrayListExtra("areas");
            UIUtil.showTestLog("AreaSelAct:",areas.get(0).toString());
            UIUtil.showTestLog("AreaSelAct:",areas.get(1).toString());
            UIUtil.showTestLog("AreaSelAct:",areas.get(2)!=null?areas.get(2).toString():"县为空！");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 销毁定位
     */
    private void stopLocation() {
        if (locationClient != null) {
            locationClient.unRegisterLocationListener(locationListener);
            locationClient.onDestroy();
        }
        locationClient = null;
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onPause() {
        super.onPause();
        stopLocation();//停止定位，
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //销毁定位客户端
        if(locationClient !=null){
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
    }

    private void startLocation(){
//        locationClient = new AMapLocationClient(this.getApplicationContext());
//        locationClient.setLocationOption(getDefaultOption());
        // 启动定位
        locationClient.startLocation();
    }

}
