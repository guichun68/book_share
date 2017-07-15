package zyzx.linke.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import zyzx.linke.R;
import zyzx.linke.adapter.AreaAdapter;
import zyzx.linke.base.BaseActivity;
import zyzx.linke.db.UserDao;
import zyzx.linke.global.BundleResult;
import zyzx.linke.model.Area;
import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.ResponseJson;
import zyzx.linke.utils.CustomProgressDialog;
import zyzx.linke.utils.StringUtil;
import zyzx.linke.utils.UIUtil;

/**
 * Created by austin on 2017/6/28.
 * Desc: 城市选择
 */

public class AreaSelAct extends BaseActivity {
    private String prePro,preCity,preCounty;//之前选择的省市县
    private final int GET_CITIES = 0x78EF, GET_COUNTIES = 0x687E, INIT_CITY = 0x97EA, INIT_COUNTY = 0x57BE, GET_AREA_ERROR = 0x404;
    private Dialog progress;
    private final int HOLD_FLAG_CITY = 34, HOLD_FLAG_COUNTY = 69;
    private TextView tvPro, tvCity, tvCounty;
    private Spinner spProvince, spCity, spCounty;
    private boolean isFirstComeIn = true;//是否第一次进入页面
    private int defaultProIndex;//默认选中的省的角标

    //数据源
    ArrayList<Area> provinces = new ArrayList<>(), cities = new ArrayList<>(), counties = new ArrayList<>();
    private AreaAdapter proAdapter, cityAdapter, countyAdapter;//省市县adapter
    private int initDept;//初始化spinner的层级数（省1，地级市2，县3）
    private Area savePro, saveCity, saveCounty;//最终要保存的省市县

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            CustomProgressDialog.dismissDialog(progress);
            switch (msg.what) {
                case GET_CITIES:
                    List<Area> areas = new ArrayList<>();
                    if(msg.obj!=null){
                        List<JSONObject> areasJSON = (List<JSONObject>) msg.obj;
                        for (JSONObject jobj:areasJSON) {
                            Area a = new Area();
                            a.setName(jobj.getString("name"));
                            a.setAreacode(jobj.getString("areaCode"));
                            a.setDepth(jobj.getInteger("depth"));
                            a.setId(jobj.getInteger("id"));
                            a.setParentid(jobj.getInteger("parentId"));
                            a.setZipcode(jobj.getString("zipCode"));
                            areas.add(a);
                        }
                    }
                    cities.clear();
                    if (!areas.isEmpty()) {
                        cities.addAll(areas);
                        spCity.setSelection(0);
                    }
                    cityAdapter.notifyDataSetChanged();
                    Area tempCity = cities.isEmpty() ? null : cities.get(0);
                    if (tempCity == null) {
                        saveCity = null;
                        tvCity.setText("");
                    } else {
                        saveCity = tempCity;
                        tvCity.setText(saveCity.getName());
                        // 获取city下面的所有县
                        progress.show();
                        getUserPresenter().getSubArea(saveCity.getId(), HOLD_FLAG_COUNTY, new AreaCallBack());
                    }
                    break;
                case GET_COUNTIES:
                    List<Area> areas1 = new ArrayList<>();
                    if(msg.obj != null){
                        List<JSONObject> areasJSON = (List<JSONObject>) msg.obj;
                        for (JSONObject jobj:areasJSON) {
                            Area a = new Area();
                            a.setName(jobj.getString("name"));
                            a.setAreacode(jobj.getString("areaCode"));
                            a.setDepth(jobj.getInteger("depth"));
                            a.setId(jobj.getInteger("id"));
                            a.setParentid(jobj.getInteger("parentId"));
                            a.setZipcode(jobj.getString("zipCode"));
                            areas1.add(a);
                        }
                    }

                    counties.clear();
                    if (areas1 != null && !areas1.isEmpty()) {
                        counties.addAll(areas1);
                        spCounty.setSelection(0);
                    }
                    countyAdapter.notifyDataSetChanged();
                    Area tempCounty = counties.isEmpty() ? null : counties.get(0);

                    if (tempCounty == null) {
                        saveCounty = null;
                        tvCounty.setText("");
                    } else {
                        saveCounty = tempCounty;
                        tvCounty.setText(saveCounty.getName());
                    }
                    break;
                case INIT_CITY://初始化页面时不修改 顶部地区显示,显示用户未修改之前的选择地区
                    //显示所在的地级市
                    String json2 = "";
                    List<Area> areas2 =new ArrayList<>();
                    if (msg.obj != null) {
                        List<JSONObject> areasJSON = (List<JSONObject>) msg.obj;
                        for (JSONObject jobj:areasJSON) {
                            Area a = new Area();
                            a.setName(jobj.getString("name"));
                            a.setAreacode(jobj.getString("areaCode"));
                            a.setDepth(jobj.getInteger("depth"));
                            a.setId(jobj.getInteger("id"));
                            a.setParentid(jobj.getInteger("parentId"));
                            a.setZipcode(jobj.getString("zipCode"));
                            areas2.add(a);
                        }
                    }
                    cities.clear();
                    if (!areas2.isEmpty()) {
                        cities.addAll(areas2);
                        for (int i = 0; i < cities.size(); i++) {
                            if (cities.get(i).getName().equals(preCity)) {
                                spCity.setSelection(cities.indexOf(cities.get(i)));
                                break;
                            }
                        }
                    }
                    cityAdapter.notifyDataSetChanged();
                    break;
                case INIT_COUNTY:
                    //显示所在的县
                    String json3 = "";
                    List<Area> areas3 = new ArrayList<>();
                    if (msg.obj != null) {
                        List<JSONObject> areasJSON = (List<JSONObject>) msg.obj;
                        for (JSONObject jobj:areasJSON) {
                            Area a = new Area();
                            a.setName(jobj.getString("name"));
                            a.setAreacode(jobj.getString("areaCode"));
                            a.setDepth(jobj.getInteger("depth"));
                            a.setId(jobj.getInteger("id"));
                            a.setParentid(jobj.getInteger("parentId"));
                            a.setZipcode(jobj.getString("zipCode"));
                            areas3.add(a);
                        }
                    }
                    counties.clear();
                    counties.addAll(areas3);
                    for (int i = 0; i < counties.size(); i++) {
                        if (counties.get(i).getName().equals(preCounty)) {
                            spCounty.setSelection(counties.indexOf(counties.get(i)));
                            break;
                        }
                    }
                    countyAdapter.notifyDataSetChanged();
                    isFirstComeIn = false;
                    break;
                case GET_AREA_ERROR:
                    if (msg.obj != null) {
                        UIUtil.showToastSafe((String) msg.obj);
                    }
                    break;
            }
        }
    };



    @Override
    protected int getLayoutId() {
        return R.layout.act_area_sel;
    }

    @Override
    protected void initView(Bundle saveInstanceState) {
        progress = CustomProgressDialog.getNewProgressBarNoTip(mContext);
        tvPro = (TextView) findViewById(R.id.tv_pro);
        tvCity = (TextView) findViewById(R.id.tv_city);
        tvCounty = (TextView) findViewById(R.id.tv_county);
        spProvince = (Spinner) findViewById(R.id.sp_province);
        spCity = (Spinner) findViewById(R.id.sp_city);
        spCounty = (Spinner) findViewById(R.id.sp_county);
        findViewById(R.id.btn_ok).setOnClickListener(this);
        findViewById(R.id.btn_cancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.btn_ok:
                Intent data = new Intent();
                ArrayList<Area> areas = new ArrayList<>();
                areas.add(savePro);
                areas.add(saveCity);
                areas.add(saveCounty);
                data.putParcelableArrayListExtra("areas",areas);
                setResult(BundleResult.SUCCESS,data);
                finish();
                break;
            case R.id.btn_cancel:
                this.finish();
                break;
        }
    }

    @Override
    protected void initData() {
        prePro=getIntent().getStringExtra("pro");
        preCity = getIntent().getStringExtra("city");
        preCounty = getIntent().getStringExtra("county");
        UserDao dao = UserDao.getInstance(mContext);
        provinces = dao.queryAllPro();

        proAdapter = new AreaAdapter(provinces,mContext);
        cityAdapter = new AreaAdapter(cities,mContext);
        countyAdapter = new AreaAdapter(counties,mContext);

        spProvince.setAdapter(proAdapter);
        spCity.setAdapter(cityAdapter);
        spCounty.setAdapter(countyAdapter);


        //初始化省份spinner默认选中用户之前填写的地区---start-----
        Area defPro = null;
        if (!StringUtil.isEmpty(prePro)) {
            //获取该省份在provinces集合中的角标
            defPro = dao.queryProByName(prePro);
            defaultProIndex = provinces.indexOf(defPro);
            defaultProIndex = defaultProIndex == -1 ? 0 : defaultProIndex;
            initDept = 1;
        }
        if (!StringUtil.isEmpty(preCounty)) {
            initDept = 3;
        } else if (!StringUtil.isEmpty(preCity)) {
            initDept = 2;
        }

        // -------end--------------
        if (defPro == null) {
            defPro = provinces.get(0);
        }
        spProvince.setSelection(defaultProIndex);

        spProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                savePro = (Area) parent.getSelectedItem();
                tvPro.setText(savePro.getName());
                progress.show();
                getUserPresenter().getSubArea(savePro.getId(), HOLD_FLAG_CITY, new AreaCallBack());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                saveCity = (Area) parent.getSelectedItem();
                tvCity.setText(saveCity.getName());
                progress.show();
                getUserPresenter().getSubArea(saveCity.getId(), HOLD_FLAG_COUNTY, new AreaCallBack());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spCounty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                saveCounty = (Area) parent.getSelectedItem();
                tvCounty.setText(saveCounty.getName());
                if (initDept == 3) isFirstComeIn = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private class AreaCallBack implements CallBack {

        @Override
        public void onSuccess(Object obj, int... code) {
            Message msg = mHandler.obtainMessage();
            String jsonTemp = (String) obj;
            ResponseJson rj = new ResponseJson(jsonTemp);
            if(rj.errorCode!=0 || rj.data==null){
                msg.what = GET_AREA_ERROR;
                msg.obj = "访问出错 code="+rj.errorCode;
                mHandler.sendMessage(msg);
                return;
            }
            Map map = (Map) rj.data.get(0);
            Integer hold = Integer.parseInt((String)map.get("hold"));
            List<JSONObject> areas = (List<JSONObject>) map.get("areas");
            switch (hold) {
                case HOLD_FLAG_CITY:
                    msg.what = isFirstComeIn ? INIT_CITY : GET_CITIES;
                    if (initDept == 1) isFirstComeIn = false;
                    msg.obj = areas;
                    mHandler.sendMessage(msg);
                    break;
                case HOLD_FLAG_COUNTY:
                    msg.what = isFirstComeIn ? INIT_COUNTY : GET_COUNTIES;
                    if (initDept == 2) isFirstComeIn = false;
                    msg.obj = areas;
                    mHandler.sendMessage(msg);
                    break;
            }
        }

        @Override
        public void onFailure(Object obj, int... code) {
            Message msg = mHandler.obtainMessage();
            msg.what = GET_AREA_ERROR;
            msg.obj = obj;
            mHandler.sendMessage(msg);
        }
    }
}
