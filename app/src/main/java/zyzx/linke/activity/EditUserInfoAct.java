package zyzx.linke.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import zyzx.linke.R;
import zyzx.linke.adapter.AreaAdapter;
import zyzx.linke.base.BaseActivity;
import zyzx.linke.base.GlobalParams;
import zyzx.linke.db.UserDao;
import zyzx.linke.global.Const;
import zyzx.linke.model.Area;
import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.UserVO;
import zyzx.linke.utils.CustomProgressDialog;
import zyzx.linke.utils.StringUtil;
import zyzx.linke.utils.UIUtil;

/**
 * Created by austin on 2017/5/2.
 * Desc: 资料修改页面
 */

public class EditUserInfoAct extends BaseActivity {

    private Dialog progress;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    private UserVO mUser;
    private TextView tvPro, tvCity, tvCounty, tvBirthday, tvSave, tvModifyDate;
    private EditText etSchool, etDepartment, etSoliloquy;
    private Spinner spProvince, spCity, spCounty, spGender, spDiploma;

    //数据源
    ArrayList<Area> provinces, cities = new ArrayList<>(), counties = new ArrayList<>();

    private AreaAdapter proAdapter, cityAdapter, countyAdapter;//省市县adapter
    private int defaultProIndex;//默认选中的省的角标
    private final int HOLD_FLAG_CITY = 34, HOLD_FLAG_COUNTY = 69;
    private final int GET_CITIES = 0x78EF, GET_COUNTIES = 0x687E, INIT_CITY = 0x97EA, INIT_COUNTY = 0x57BE, GET_AREA_ERROR = 0x404;
    private boolean isFirstComeIn = true;//是否第一次进入页面


    private int initDept;//初始化spinner的层级数（省1，地级市2，县3）
    private Area savePro, saveCity, saveCounty;//最终要保存的省市县
    private UserVO rawUV;//保存原始的用户资料以便最后保存时对比

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            CustomProgressDialog.dismissDialog(progress);
            switch (msg.what) {
                case GET_CITIES:
                    Object obj = msg.obj;
                    String json = "";
                    if (obj != null) json = (String) obj;
                    List<Area> areas = JSON.parseArray(json, Area.class);
                    cities.clear();
                    if (areas != null && !areas.isEmpty()) {
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
                    String gson = "";
                    if (msg.obj != null) gson = (String) msg.obj;
                    List<Area> areas1 = JSON.parseArray(gson, Area.class);

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
                    if (msg.obj != null) {
                        json2 = (String) msg.obj;
                    }
                    List<Area> areas2 = JSON.parseArray(json2, Area.class);
                    cities.clear();
                    if (areas2 != null && !areas2.isEmpty()) {
                        cities.addAll(areas2);
                        for (int i = 0; i < cities.size(); i++) {
                            if (cities.get(i).getName().equals(mUser.getCityName())) {
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
                    if (msg.obj != null) {
                        json3 = (String) msg.obj;
                    }
                    List<Area> areas3 = JSON.parseArray(json3, Area.class);
                    counties.clear();
                    if (areas3 != null) {
                        counties.addAll(areas3);
                        for (int i = 0; i < counties.size(); i++) {
                            if (counties.get(i).getName().equals(mUser.getCountyName())) {
                                spCounty.setSelection(counties.indexOf(counties.get(i)));
                                break;
                            }
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
        rawUV = GlobalParams.getLastLoginUser();
        return R.layout.act_edit_info;
    }

    @Override
    protected void initView(Bundle saveInstanceState) {
        progress = CustomProgressDialog.getNewProgressBarNoTip(mContext);
        mUser = GlobalParams.getLastLoginUser();
        mTitleText.setText("基本资料编辑");
        tvSave = (TextView) findViewById(R.id.tv_add_mylib);
        tvSave.setText("保存");

        tvPro = (TextView) findViewById(R.id.tv_pro);
        tvCity = (TextView) findViewById(R.id.tv_city);
        tvCounty = (TextView) findViewById(R.id.tv_county);
        tvBirthday = (TextView) findViewById(R.id.tv_birthday);
        etSchool = (EditText) findViewById(R.id.et_school);
        etDepartment = (EditText) findViewById(R.id.et_department);
        etSoliloquy = (EditText) findViewById(R.id.et_soliloquy);
        spGender = (Spinner) findViewById(R.id.sp_gender);
        spDiploma = (Spinner) findViewById(R.id.sp_diploma);
        tvModifyDate = (TextView) findViewById(R.id.tv_modify_date);

        spProvince = (Spinner) findViewById(R.id.sp_province);
        spCity = (Spinner) findViewById(R.id.sp_city);
        spCounty = (Spinner) findViewById(R.id.sp_county);

        tvSave.setOnClickListener(this);
        tvModifyDate.setOnClickListener(this);

        tvPro.setText(mUser.getProvinceName());
        tvCity.setText(mUser.getCityName());
        tvCounty.setText(mUser.getCountyName());

        tvBirthday.setText(mUser.getBirthday() == null ? "未填写" : sdf.format(mUser.getBirthday()));
        etSchool.setText(StringUtil.isEmpty(mUser.getSchool()) ? "未填写" : mUser.getSchool());
        if (!StringUtil.isEmpty(mUser.getDepartment())) {
            etDepartment.setText(mUser.getDepartment());
        }
        etSoliloquy.setText(StringUtil.isEmpty(mUser.getSoliloquy()) ? "" : mUser.getSoliloquy());
        if (mUser.getGender() != null) {
            switch (mUser.getGender()) {
                case 1://男
                case 2://女
                case 3://保密
                    spGender.setSelection(mUser.getGender());
                    break;
                case 0:
                default:
                    spGender.setSelection(0);
            }
        }
        if (mUser.getDiplomaId() != null) {
            switch (mUser.getDiplomaId()) {
                case 0://请选择
                case 1://小学
                case 2://初中
                case 3://高中
                case 4://专科
                case 5://本科
                case 6://硕士
                case 7://博士
                    spDiploma.setSelection(mUser.getDiplomaId());
                    break;
                default:
                    spGender.setSelection(0);
            }
        }
    }

    @Override
    protected void initData() {
        UserDao dao = UserDao.getInstance(mContext);
        provinces = dao.queryAllPro();

        proAdapter = new AreaAdapter(provinces);
        cityAdapter = new AreaAdapter(cities);
        countyAdapter = new AreaAdapter(counties);

        spProvince.setAdapter(proAdapter);
        spCity.setAdapter(cityAdapter);
        spCounty.setAdapter(countyAdapter);

        //初始化省份spinner默认选中用户之前填写的地区---start-----
        Area defPro = null;

        if (!StringUtil.isEmpty(mUser.getProvinceName())) {
            //获取该省份在provinces集合中的角标
            defPro = dao.queryProByName(mUser.getProvinceName());
            defaultProIndex = provinces.indexOf(defPro);
            defaultProIndex = defaultProIndex == -1 ? 0 : defaultProIndex;
            initDept = 1;
        }
        if (!StringUtil.isEmpty(mUser.getCountyName())) {
            initDept = 3;
        } else if (!StringUtil.isEmpty(mUser.getCityName())) {
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

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.tv_add_mylib://保存
                final UserVO uv = new UserVO();
                if (saveCounty != null) {
                    uv.setCityId(saveCounty.getId());
                } else if (saveCity != null) {
                    uv.setCityId(saveCity.getId());
                } else if (savePro != null) {
                    uv.setCityId(savePro.getId());
                }
                uv.setGender(spGender.getSelectedItemPosition());
                uv.setSchool(etSchool.getText().toString());
                uv.setDepartment(etDepartment.getText().toString());
                uv.setProvinceName(savePro.getName());
                uv.setCityName(saveCity != null ? saveCity.getName() : null);
                uv.setCountyName(saveCounty != null ? saveCounty.getName() : null);
                uv.setGenderName(spGender.getSelectedItem().toString());
                uv.setDiplomaId(spDiploma.getSelectedItemPosition());
                uv.setDiplomaName(spDiploma.getSelectedItem().toString());
                uv.setSoliloquy(etSoliloquy.getText().toString());
                uv.setBirthday(sdf.parse(StringUtil.isEmpty(tvBirthday.getText().toString()) ? "" : tvBirthday.getText().toString(), new ParsePosition(0)));
                UIUtil.showTestLog(Const.TAG, uv.toString());
                final UserVO usvo = isDifference(uv, rawUV);
                if (usvo == null) {//null说明一致，未做任何修改
                    UIUtil.showToastSafe("已保存");
                    setResult(RESULT_OK);
                    finish();
                    return;
                }
                saveUser(usvo);
                break;
            case R.id.tv_modify_date:
                String dateStr = tvBirthday.getText().toString();
                Date currDate = new Date();
                if (!StringUtil.isEmpty(dateStr)) {
                    currDate = sdf.parse(dateStr, new ParsePosition(0));
                }
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(currDate == null ? new Date() : currDate);

                final DatePickerDialog dateDialog = new DatePickerDialog(mContext, null,
                        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));

                dateDialog.setButton(DialogInterface.BUTTON_POSITIVE, "完成", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //通过mDialog.getDatePicker()获得dialog上的DatePicker组件，然后可以获取日期信息
                        DatePicker datePicker = dateDialog.getDatePicker();
                        int year = datePicker.getYear();
                        int month = datePicker.getMonth();
                        int day = datePicker.getDayOfMonth();
                        tvBirthday.setText(year + "-" + (month + 1) + "-" + day);
                    }
                });
                //取消按钮，如果不需要直接不设置即可
                dateDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.out.println("BUTTON_NEGATIVE~~");
                    }
                });
                dateDialog.show();
                break;
        }
    }

    private void saveUser(final UserVO usvo) {
        showProgress("请稍后");
        getUserPresenter().saveUserInfo(usvo, new CallBack() {
            @Override
            public void onSuccess(Object obj, int... code) {
                dismissProgress();
                String json = (String) obj;
                if (StringUtil.isEmpty(json)) {
                    UIUtil.showToastSafe("保存失败，请检查网络");
                    return;
                }
                JSONObject jsonObject = JSON.parseObject(json);
                Integer code2 = jsonObject.getInteger("code");
                if (code2 != null) {
                    switch (code2) {
                        case 200:
                            UIUtil.showToastSafe("保存成功");
                            GlobalParams.saveUser(usvo);
                            setResult(RESULT_OK); //intent为A传来的带有Bundle的intent，当然也可以自己定义新的Bundle
                            finish();//此处一定要调用finish()方法
                            break;
                        case 500:
                            UIUtil.showToastSafe("保存失败,code=500");
                            break;
                    }
                } else {
                    UIUtil.showToastSafe("保存失败code=null");
                }
            }

            @Override
            public void onFailure(Object obj, int... code) {
                dismissProgress();
                UIUtil.showToastSafe(obj != null ? (String) obj : "访问出错，请稍后再试");
            }
        });
    }

    /**
     * 比较两个用户资料（仅限本页面中的资料）是否一致
     *
     * @param uv
     * @param rawUV
     * @return 如果不一致，则返回一个包含最新最全数据的UserVO对象；如一致则返回null
     */
    private UserVO isDifference(UserVO uv, UserVO rawUV) {

        if (uv.getCityId().intValue() == rawUV.getCityId().intValue()) {
            if (uv.getGender().intValue() == rawUV.getGender().intValue()) {
                if (uv.getSchool().equals(rawUV.getSchool())) {
                    if (uv.getDepartment().equals(rawUV.getDepartment())) {
                        if (uv.getDiplomaId().intValue() == rawUV.getDiplomaId().intValue()) {
                            if (uv.getSoliloquy().equals(rawUV.getSoliloquy())) {
                                if (uv.getBirthday().equals(rawUV.getBirthday())) {
                                    return null;
                                }
                            }
                        }
                    }
                }
            }
        }
        UserVO usv = (UserVO) rawUV.clone();
        if (saveCounty != null) {
            usv.setCityId(saveCounty.getId());
        } else if (saveCity != null) {
            usv.setCityId(saveCity.getId());
        } else if (savePro != null) {
            usv.setCityId(savePro.getId());
        }
        usv.setGender(spGender.getSelectedItemPosition());
        usv.setSchool(etSchool.getText().toString());
        usv.setDepartment(etDepartment.getText().toString());
        usv.setProvinceName(savePro.getName());
        usv.setCityName(saveCity != null ? saveCity.getName() : null);
        usv.setCountyName(saveCounty != null ? saveCounty.getName() : null);
        usv.setGenderName(spGender.getSelectedItem().toString());
        usv.setDiplomaId(spDiploma.getSelectedItemPosition());
        usv.setDiplomaName(spDiploma.getSelectedItem().toString());
        usv.setSoliloquy(etSoliloquy.getText().toString());
        usv.setBirthday(sdf.parse(StringUtil.isEmpty(tvBirthday.getText().toString()) ? "" : tvBirthday.getText().toString(), new ParsePosition(0)));
        return usv;
    }

    Dialog prompt = null;

    @Override
    public void onBackPressed() {
        final UserVO uv = new UserVO();
        if (saveCounty != null) {
            uv.setCityId(saveCounty.getId());
        } else if (saveCity != null) {
            uv.setCityId(saveCity.getId());
        } else if (savePro != null) {
            uv.setCityId(savePro.getId());
        }
        uv.setGender(spGender.getSelectedItemPosition());
        uv.setSchool(etSchool.getText().toString());
        uv.setDepartment(etDepartment.getText().toString());
        uv.setProvinceName(savePro.getName());
        uv.setCityName(saveCity != null ? saveCity.getName() : null);
        uv.setCountyName(saveCounty != null ? saveCounty.getName() : null);
        uv.setGenderName(spGender.getSelectedItem().toString());
        uv.setDiplomaId(spDiploma.getSelectedItemPosition());
        uv.setDiplomaName(spDiploma.getSelectedItem().toString());
        uv.setSoliloquy(etSoliloquy.getText().toString());
        uv.setBirthday(sdf.parse(StringUtil.isEmpty(tvBirthday.getText().toString()) ? "" : tvBirthday.getText().toString(), new ParsePosition(0)));
        UIUtil.showTestLog(Const.TAG, uv.toString());
        final UserVO usvo = isDifference(uv, rawUV);
        if (usvo == null) {//null说明一致，未做任何修改
            setResult(RESULT_OK);
            finish();
        } else {//做了修改，提示是否保存
            prompt = CustomProgressDialog.getPromptDialog2Btn(mContext, "是否保存更改", "保存", "放弃", new PromptClickListener(1, usvo), new PromptClickListener(0, null));
            prompt.show();
        }

    }

    private class PromptClickListener implements View.OnClickListener {
        private int isConfirm;//确认按钮=1，放弃按钮=0
        private UserVO uuoo;

        PromptClickListener(int isConfirm, UserVO uuoo) {
            this.isConfirm = isConfirm;
            this.uuoo = uuoo;
        }

        @Override
        public void onClick(View v) {
            switch (isConfirm) {
                case 0:
                    CustomProgressDialog.dismissDialog(prompt);
                    setResult(RESULT_OK);
                    finish();
                    break;
                case 1:
                    saveUser(uuoo);
                    break;
            }
        }
    }

    private class AreaCallBack implements CallBack {

        @Override
        public void onSuccess(Object obj, int... code) {
            Message msg = mHandler.obtainMessage();
            String jsonTemp = (String) obj;
            JSONObject jsonObj = JSON.parseObject(jsonTemp);
            Integer hold = jsonObj.getInteger("hold");
            switch (hold) {
                case HOLD_FLAG_CITY:
                    msg.what = isFirstComeIn ? INIT_CITY : GET_CITIES;
                    if (initDept == 1) isFirstComeIn = false;
                    msg.obj = jsonObj.getString("areas");
                    mHandler.sendMessage(msg);
                    break;
                case HOLD_FLAG_COUNTY:
                    msg.what = isFirstComeIn ? INIT_COUNTY : GET_COUNTIES;
                    if (initDept == 2) isFirstComeIn = false;
                    msg.obj = jsonObj.getString("areas");
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
