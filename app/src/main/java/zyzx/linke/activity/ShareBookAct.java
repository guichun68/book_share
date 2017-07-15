package zyzx.linke.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import java.util.ArrayList;

import zyzx.linke.R;
import zyzx.linke.base.BaseActivity;
import zyzx.linke.global.BundleResult;
import zyzx.linke.model.Area;
import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.BookDetail2;
import zyzx.linke.model.bean.ResponseJson;
import zyzx.linke.model.bean.UserBooks;
import zyzx.linke.utils.UIUtil;

/**
 * Created by austin on 2017/7/13.
 * Desc: 图书分享
 */

public class ShareBookAct extends BaseActivity {
    private String mCurrPro, mCurrCity, mCurrCounty;
    private TextView tvPro, tvCity, tvCounty;
    private TextView btnLocation, btnManulSel;
    private EditText etMsg;
    private CheckBox cbShareTypeBorrow, cbShareTypeGive;
    private Button btnShare, btnCancel;
    private AMapLocationClient locationClient = null;
    private BookDetail2 mBook;
    private int bookIndex;//暂存当前书籍在我的所有书籍列表的索引，以便分享成功后根据其notify listView
    private String userBookId;//user_books表主键
    private int mShareType=2;//二进制含义：00 不出借，不赠送(不允许)；
    //                                     01不出借，可赠送;
    //                                     10可出借，不赠送；
    //                                     11可出借，可赠送

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
        btnLocation = (TextView) findViewById(R.id.btn_location);
        btnManulSel = (TextView) findViewById(R.id.btn_sel_area);
        cbShareTypeBorrow = (CheckBox) findViewById(R.id.cb_borrow);
        cbShareTypeGive = (CheckBox) findViewById(R.id.cb_give);
        etMsg = (EditText) findViewById(R.id.et_msg);
        btnLocation.setOnClickListener(this);
        btnManulSel.setOnClickListener(this);
        btnShare.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        cbShareTypeBorrow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mShareType = mShareType | 2;
                }else{
                    mShareType = mShareType & 1;
                }
            }
        });
        cbShareTypeGive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mShareType = mShareType | 1;
                }else{
                    mShareType = mShareType & 2;
                }
            }
        });


        initLocation();
        startLocation();
    }

    private void initLocation() {
        //初始化client
        locationClient = new AMapLocationClient(this.getApplicationContext());
        //设置定位参数
        locationClient.setLocationOption(getDefaultOption());
        // 设置定位监听
        locationClient.setLocationListener(locationListener);
    }

    @Override
    protected void initData() {
        mBook = getIntent().getParcelableExtra("book");
        bookIndex = getIntent().getIntExtra("bookIndex",-1);
        userBookId = getIntent().getStringExtra("userBookId");
        mCurrPro = "北京市";
        mCurrCity = "北京市";
        mCurrCounty = "海淀区";
        refreshArea();
    }

    /**
     * 默认的定位参数
     *
     * @since 2.8.0
     */
    private AMapLocationClientOption getDefaultOption() {
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
            dismissProgress();
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
//                    UIUtil.showToastSafe("定位成功:"+mCurrPro+" "+mCurrCity+" "+mCurrCounty);
                }
            } else {
                UIUtil.showTestLog("定位失败，loc is null");
            }
        }
    };

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.btn_location:
                if (locationClient == null) {
                    initLocation();
                }
                showDefProgress();
                //手动定位
                startLocation();
                break;
            case R.id.btn_sel_area:
                startActivityForResult(new Intent(mContext, AreaSelAct.class), 777);
                break;
            case R.id.btn_submit:
                if (!cbShareTypeBorrow.isChecked() && !cbShareTypeGive.isChecked()) {
                    UIUtil.showToastSafe("请至少选择一个分享方式");
                    return;
                }
                //分享
                JSONObject jobj = new JSONObject();
//                String bookStr = JSONObject.toJSONString(mBook);
                jobj.put("bookId", mBook.getId());
                jobj.put("pro", mCurrPro);
                jobj.put("city", mCurrCity);
                jobj.put("county", mCurrCounty);
                jobj.put("shareType",mShareType);
                jobj.put("msg",etMsg.getText().toString());
                jobj.put("userBookId",userBookId);
                showDefProgress();
                getUserPresenter().shareBook(jobj.toJSONString(),new CallBack(){

                    @Override
                    public void onSuccess(Object obj, int... code) {
                        dismissProgress();
                        if(obj!=null){
                            String json = (String) obj;
                            ResponseJson rj = new ResponseJson(json);
                            if(rj.errorCode==1){
                                if(rj.data==null || rj.data.isEmpty()){
                                    UIUtil.showToastSafe("分享出错");
                                    return;
                                }
                                UserBooks ub = JSON.parseObject(JSONObject.parseObject((String)((JSONObject)rj.data.get(0)).get("userBook")).get("ub").toString(),UserBooks.class);
                                UIUtil.showToastSafe("分享成功");
                                Intent intent = new Intent();
                                intent.putExtra("bookIndex",bookIndex);
                                intent.putExtra("userBook",ub);
                                setResult(888,intent);
                                finish();
                            }else{
                                UIUtil.showToastSafe(rj.errorMsg);
                            }
                        }else{
                            UIUtil.showToastSafe("未能分享成功");
                        }
                    }

                    @Override
                    public void onFailure(Object obj, int... code) {
                        dismissProgress();
                        if(obj instanceof String){
                            UIUtil.showToastSafe((String) obj);
                        }
                    }
                });
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
        if (requestCode == 777 && resultCode == BundleResult.SUCCESS) {
            //选择完地址的回调
            ArrayList<Area> areas = data.getParcelableArrayListExtra("areas");
            mCurrPro = areas.get(0).getName();
            if (areas.get(2) == null) {
                mCurrCity = mCurrPro;
                mCurrCounty = areas.get(1).getName();
            } else {
                mCurrCity = areas.get(1).getName();
                mCurrCounty = areas.get(2).getName();
            }
            refreshArea();
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
        if (locationClient != null) {
            locationClient.onDestroy();
            locationClient = null;
//            locationOption = null;
        }
    }

    private void startLocation() {
//        locationClient = new AMapLocationClient(this.getApplicationContext());
//        locationClient.setLocationOption(getDefaultOption());
        // 启动定位
        locationClient.startLocation();
    }

}
