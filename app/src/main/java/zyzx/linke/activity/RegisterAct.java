package zyzx.linke.activity;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;
import android.widget.Button;

import zyzx.linke.R;
import zyzx.linke.base.BaseActivity;
import zyzx.linke.model.CallBack;
import zyzx.linke.utils.CheckPhone;
import zyzx.linke.utils.StringUtil;
import zyzx.linke.utils.UIUtil;

/**
 * Created by austin on 2017/2/19.
 * Desc: 注册页面
 */
public class RegisterAct extends BaseActivity {

    private AppCompatEditText aetLoginName,aetPhone,aetPsw,aetRePsw;
    private Button btnRegister;

    @Override
    protected int getLayoutId() {
        return R.layout.act_regist;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mTitleText.setText("用户注册");
        //aetLoginName,aetPhone,aetPsw,aetRePsw;
        aetLoginName = (AppCompatEditText) findViewById(R.id.aet_login_name);
        aetPhone = (AppCompatEditText) findViewById(R.id.aet_phone);
        aetPsw = (AppCompatEditText) findViewById(R.id.aet_psw);
        aetRePsw = (AppCompatEditText) findViewById(R.id.aet_re_psw);

        btnRegister = (Button) findViewById(R.id.btn_regist);
        btnRegister.setOnClickListener(this);

    }

    @Override
    protected void initData() {
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.btn_regist:
                if(!checkInput()){
                    return;
                }
                showProgress("请稍后……");
                getUserPresenter().regist(aetLoginName.getText().toString().trim(), aetPsw.getText().toString().trim(), aetPhone.getText().toString().trim(), new CallBack() {
                    @Override
                    public void onSuccess(final Object obj) {
                        RegisterAct.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dismissProgress();
                                Integer code = (Integer) obj;
                                switch (code){
                                    case 200:
                                        UIUtil.showToastSafe(R.string.regist_succ);
                                        setResult(300);
                                        finish();
                                        break;
                                    case 201:
                                        aetPhone.setError(UIUtil.getString(R.string.phoneOccupation));
                                        Snackbar.make(aetPhone,R.string.phoneOccupation,Snackbar.LENGTH_SHORT).show();
                                        break;
                                    case 202:
                                        aetLoginName.setError(UIUtil.getString(R.string.usernameOccupation));
                                        Snackbar.make(aetLoginName,R.string.usernameOccupation,Snackbar.LENGTH_SHORT).show();
                                        break;
                                    case 500:
                                        Snackbar.make(btnRegister,R.string.server_err,Snackbar.LENGTH_SHORT).show();
                                        break;
                                }
                            }
                        });

                    }

                    @Override
                    public void onFailure(Object obj) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                UIUtil.showToastSafe("访问出错，请稍后再试");
                                dismissProgress();
                            }
                        });
                    }
                });
                break;
        }
    }

    private boolean checkInput() {
        //判空
        if(StringUtil.isEmpty(aetLoginName.getText().toString())){
            Snackbar.make(aetLoginName,"用户名不能为空",Snackbar.LENGTH_SHORT).show();
            aetLoginName.setError("用户名不能为空");
            return false;
        }
        if(StringUtil.isEmpty(aetPhone.getText().toString())){
            Snackbar.make(aetPhone,"手机号不能为空",Snackbar.LENGTH_SHORT).show();
            aetPhone.setError("手机号不能为空");
            return false;
        }
        if(StringUtil.isEmpty(aetPsw.getText().toString())){
            Snackbar.make(aetPsw,"密码不能为空",Snackbar.LENGTH_SHORT).show();
            aetPsw.setError("密码不能为空");
            return false;
        }
        if(StringUtil.isEmpty(aetRePsw.getText().toString())){
            Snackbar.make(aetRePsw,"请再次输入密码",Snackbar.LENGTH_SHORT).show();
            aetRePsw.setError("请再次输入密码");
            return false;
        }
        if(aetPsw.getText().toString().contains(" ")){
            Snackbar.make(aetRePsw,"密码不能包含空字符",Snackbar.LENGTH_SHORT).show();
            aetRePsw.setError("密码不能包含空字符");
            return false;
        }
        if(aetPsw.getText().toString().length()<6){
            Snackbar.make(aetRePsw,"密码至少6位",Snackbar.LENGTH_SHORT).show();
            aetRePsw.setError("密码至少6位");
            return false;
        }
        //判断手机号是否合法
        if(!CheckPhone.isPhone(aetPhone.getText().toString())) {
            Snackbar.make(aetPhone,"手机号不合法",Snackbar.LENGTH_SHORT).show();
            aetPhone.setError("手机号不合法");
            return false;
        }
        //两次密码是否一致
        if(!aetPsw.getText().toString().equals(aetRePsw.getText().toString())){
            Snackbar.make(aetRePsw,"两次密码不一致",Snackbar.LENGTH_SHORT).show();
            aetRePsw.setError("两次密码不一致");
            return false;
        }
        return true;
    }
}
