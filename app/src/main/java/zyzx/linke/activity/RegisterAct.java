package zyzx.linke.activity;

import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;
import android.view.WindowManager;
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

    private AppCompatEditText aetLoginName, aetPhone, aetPsw, aetRePsw;
    private Button btnRegister;

    @Override
    protected int getLayoutId() {
        return R.layout.act_regist;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
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
        switch (view.getId()) {
            case R.id.btn_regist:
                if (!checkInput()) {
                    return;
                }
                showProgress("请稍后……");
                getUserPresenter().regist(aetLoginName.getText().toString().trim(), aetPsw.getText().toString().trim(), aetPhone.getText().toString().trim(), new CallBack() {
                    @Override
                    public void onSuccess(final Object obj, int... code) {
                        RegisterAct.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dismissProgress();
                                UIUtil.showToastSafe(R.string.regist_succ);
                                setResult(300);
                                finish();
                            }
                        });

                    }

                    @Override
                    public void onFailure(final Object obj, final int... code) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String errMsg = (String)obj;
                                if(code.length>0){
                                    /*switch (code[0]) {
                                        case 2:
                                            aetPhone.setError(errMsg);
                                            Snackbar.make(aetPhone, errMsg, Snackbar.LENGTH_SHORT).show();
                                            break;
                                        case 1:
                                            aetLoginName.setError(errMsg);
                                            Snackbar.make(aetLoginName, errMsg, Snackbar.LENGTH_SHORT).show();
                                            break;
                                        default:
                                            Snackbar.make(aetLoginName,errMsg+code[0]+"",Snackbar.LENGTH_SHORT).show();
                                            break;
                                    }*/
                                    UIUtil.showToastSafe(errMsg);
                                }
                                else{
                                  UIUtil.showToastSafe(errMsg);
                                }
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
        if (StringUtil.isEmpty(aetLoginName.getText().toString())) {
//            Snackbar.make(aetLoginName, "用户名不能为空", Snackbar.LENGTH_SHORT).show();
            UIUtil.showToastSafe("用户名不能为空");
            aetLoginName.setError("用户名不能为空");
            return false;
        }
        if(StringUtil.checkIfHasSpecialChar(aetLoginName.getText().toString())){
//            Snackbar.make(aetLoginName, "用户名不合法", Snackbar.LENGTH_SHORT).show();
            UIUtil.showToastSafe("用户名不能有特殊字符");
            aetLoginName.setError("用户名不能有特殊字符");
            return false;
        }

        if (StringUtil.isEmpty(aetPhone.getText().toString())) {
//            Snackbar.make(aetPhone, "手机号不能为空", Snackbar.LENGTH_SHORT).show();
            UIUtil.showToastSafe("手机号不能为空");
            aetPhone.setError("手机号不能为空");
            return false;
        }
        if (StringUtil.isEmpty(aetPsw.getText().toString())) {
//            Snackbar.make(aetPsw, "密码不能为空", Snackbar.LENGTH_SHORT).show();
            UIUtil.showToastSafe("密码不能为空");
            aetPsw.setError("密码不能为空");
            return false;
        }
        if (StringUtil.isEmpty(aetRePsw.getText().toString())) {
//            Snackbar.make(aetRePsw, "请再次输入密码", Snackbar.LENGTH_SHORT).show();
            UIUtil.showToastSafe("请再次输入密码");
            aetRePsw.setError("请再次输入密码");
            return false;
        }
        if (aetPsw.getText().toString().contains(" ")) {
//            Snackbar.make(aetPsw, "密码不能包含空字符", Snackbar.LENGTH_SHORT).show();
            UIUtil.showToastSafe("密码不能包含空字符");
            aetPsw.setError("密码不能包含空字符");
            return false;
        }
        if (aetPsw.getText().toString().length() < 6) {
//            Snackbar.make(aetPsw, "密码至少6位", Snackbar.LENGTH_SHORT).show();
            UIUtil.showToastSafe("密码至少6位");
            aetPsw.setError("密码至少6位");
            return false;
        }
        //判断手机号是否合法
        if (!CheckPhone.isPhone(aetPhone.getText().toString())) {
//            Snackbar.make(aetPhone, "手机号不合法", Snackbar.LENGTH_SHORT).show();
            UIUtil.showToastSafe("手机号不合法");
            aetPhone.setError("手机号不合法");
            return false;
        }
        //两次密码是否一致
        if (!aetPsw.getText().toString().equals(aetRePsw.getText().toString())) {
//            Snackbar.make(aetRePsw, "两次密码不一致", Snackbar.LENGTH_SHORT).show();
            UIUtil.showToastSafe("两次密码不一致");
            aetRePsw.setError("两次密码不一致");
            return false;
        }
        return true;
    }

}
