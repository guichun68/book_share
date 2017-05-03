package zyzx.linke.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import zyzx.linke.R;
import zyzx.linke.adapter.ProvinceAdapter;
import zyzx.linke.base.BaseActivity;
import zyzx.linke.base.GlobalParams;
import zyzx.linke.db.UserDao;
import zyzx.linke.global.Const;
import zyzx.linke.model.Area;
import zyzx.linke.model.CallBack;
import zyzx.linke.model.bean.UserVO;
import zyzx.linke.utils.StringUtil;
import zyzx.linke.utils.UIUtil;

/**
 * Created by austin on 2017/5/2.
 */

public class EditUserInfoAct extends BaseActivity {

    private UserVO mUser;
    private TextView tvCity,tvBirthday,tvSchool,tvSave;
    private EditText etDepartment,etSoliloquy;
    private Spinner spProvince,spCity,spCounty,spGender,spDiploma;
    private UserDao mDao;
    private ProvinceAdapter proAdapter;//省份adapter
    ArrayList<Area> provinces;//
    private int defaultProIndex;//默认选中的省份角标

    @Override
    protected int getLayoutId() {
        return R.layout.act_edit_info;
    }

    @Override
    protected void initView(Bundle saveInstanceState) {
        mUser = GlobalParams.getLastLoginUser();
        mTitleText.setText("基本资料编辑");
        tvSave = (TextView)findViewById(R.id.tv_add_mylib);
        tvSave.setText("保存");
        tvCity = (TextView) findViewById(R.id.tv_city);
        tvBirthday = (TextView) findViewById(R.id.tv_birthday);
        tvSchool = (TextView) findViewById(R.id.tv_birthday);
        etDepartment = (EditText) findViewById(R.id.et_department);
        etSoliloquy = (EditText) findViewById(R.id.et_soliloquy);
        spGender = (Spinner) findViewById(R.id.sp_gender);
        spDiploma = (Spinner) findViewById(R.id.sp_diploma);

        spProvince = (Spinner) findViewById(R.id.sp_province);
        spCity = (Spinner) findViewById(R.id.sp_city);
        spCounty = (Spinner) findViewById(R.id.sp_county);

        tvSave.setOnClickListener(this);

        tvCity.setText(StringUtil.isEmpty(mUser.getCityName()) ? "未填写" : mUser.getCityName());
        tvBirthday.setText(StringUtil.isEmpty(mUser.getBirthday()) ? "未填写" : mUser.getBirthday());
        tvSchool.setText(StringUtil.isEmpty(mUser.getSchool()) ? "未填写" : mUser.getSchool());
        if (!StringUtil.isEmpty(mUser.getDepartment())) {
            etDepartment.setText(mUser.getDepartment());
        }
        etSoliloquy.setText(StringUtil.isEmpty(mUser.getSoliloquy()) ? "" : mUser.getSoliloquy());
        if (mUser.getGender() != null){
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
        if(mUser.getDiplomaId()!=null){
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
        mDao = UserDao.getInstance(mContext);
        provinces = mDao.queryAllPro();
        proAdapter = new ProvinceAdapter(provinces);
        spProvince.setAdapter(proAdapter);

        //初始化省份spinner默认选中用户之前填写的地区---start-----
        Area defaultProvince = null;
        if(!StringUtil.isEmpty(mUser.getCityName())){
            String[] split = mUser.getCityName().split("-");
            if(split.length>0){
                //获取用户所在城市所属省份的名称
                String pro = split[0];
                //获取该省份在provinces集合中的角标
                defaultProvince = mDao.queryProByName(pro);
                defaultProIndex = provinces.indexOf(defaultProvince);
                defaultProIndex = defaultProIndex==-1?0:defaultProIndex;
            }
        }
        // -------end--------------
        if(defaultProvince==null){
            defaultProvince = provinces.get(0);
        }
        spProvince.setSelection(defaultProIndex);
        //访问Server获取该省下面所有的市区
        getUserPresenter().getProCity(defaultProvince.getId(), new CallBack() {
            @Override
            public void onSuccess(Object obj) {

            }

            @Override
            public void onFailure(Object obj) {
                if(obj!=null) {UIUtil.showToastSafe((String)obj);}
            }
        });


    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.tv_add_mylib://保存
                UIUtil.showToastSafe("save");
                UserDao dao = UserDao.getInstance(mContext);
                Area area = dao.queryProByid(26);
                System.out.println(area);
                break;
        }
    }
}
