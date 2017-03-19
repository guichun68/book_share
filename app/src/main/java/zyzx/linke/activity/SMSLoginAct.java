package zyzx.linke.activity;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;
import android.widget.Button;

import zyzx.linke.R;
import zyzx.linke.global.BaseActivity;
import zyzx.linke.global.GlobalParams;
import zyzx.linke.model.CallBack;
import zyzx.linke.utils.CheckPhone;
import zyzx.linke.utils.StringUtil;
import zyzx.linke.utils.TimeUtils;
import zyzx.linke.utils.UIUtil;

/**
 * Created by austin on 2017/2/18.
 * Desc: 短信登录页面
 */
public class SMSLoginAct extends BaseActivity {

    private AppCompatEditText aetPhone,aetVerifyCode;
    private Button btnSendVerifyCode,btnLogin;

    @Override
    protected int getLayoutId() {
        return R.layout.act_sms_login;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mTitleText.setText("手机号登录");
        aetPhone = (AppCompatEditText) findViewById(R.id.aet_phone);
        aetVerifyCode= (AppCompatEditText) findViewById(R.id.aet_verifycode);
        btnSendVerifyCode = (Button) findViewById(R.id.btn_sendverifycode);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnSendVerifyCode.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
    }

    @Override
    protected void initData() {
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.btn_sendverifycode:
                if(!checkPhoneInput()){
                    return;
                }
                final TimeUtils tu = new TimeUtils(btnSendVerifyCode,"发送验证码");
                getUserPresenter().sendLoginSMSVerifyCode(aetPhone.getText().toString().trim(), new CallBack() {
                    @Override
                    public void onSuccess(Object obj) {
                        Integer code = (Integer) obj;
                        switch (code){
                            case 200://发送成功
                                tu.runTimer();
                                break;
                            case 500://code:500 手机号未注册 ;
                                aetPhone.setError("该手机号未注册");
                                Snackbar.make(btnSendVerifyCode,"该手机号未注册",Snackbar.LENGTH_SHORT).show();
                                break;
                            case 600://code 600 该手机号下发现多个注册用户
                                Snackbar.make(btnSendVerifyCode,"该手机号下注册有多个用户,请使用用户名登录",Snackbar.LENGTH_LONG).show();
                                break;
                            case 700://code 700 短信下发失败（查看后台日志或联系秒嘀）
                                Snackbar.make(btnSendVerifyCode,"未能成功发送短信,请稍后再试",Snackbar.LENGTH_SHORT).show();
                                break;
                        }
                    }

                    @Override
                    public void onFailure(Object obj) {
                        UIUtil.showTestLog("zyzx","sms verifyCode send failure.");
                    }
                });
                break;
            case R.id.btn_login:
                if(!checkVerifyInput()){
                    return;
                }
                setResult(200);
                this.finish();
                break;
        }
    }

    /**
     * 登录按钮判断
     * @return
     */
    private boolean checkVerifyInput() {
        String verifyStr = aetVerifyCode.getText().toString();
        if(StringUtil.isEmpty(verifyStr)){
            Snackbar.make(aetVerifyCode,"验证码不能为空",Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if(verifyStr.length()!=6){
            Snackbar.make(aetVerifyCode,"验证码输入有误",Snackbar.LENGTH_SHORT).show();
            return false;
        }
        int input;
        try{
            input = Integer.parseInt(verifyStr);
        }catch (NumberFormatException e){
            Snackbar.make(aetVerifyCode,"验证码输入有误",Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if(input != GlobalParams.gVerifyCode){
            Snackbar.make(aetVerifyCode,"验证码输入有误",Snackbar.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * 对用户输入的手机号进行校验
     * @return
     */
    private boolean checkPhoneInput() {
        //判空
        if(StringUtil.isEmpty(aetPhone.getText().toString())){
            aetPhone.setError("手机号不能为空");
            Snackbar.make(btnSendVerifyCode,"手机号不能为空",Snackbar.LENGTH_SHORT).show();
            return false;
        }
        //正则校验手机
        if(!CheckPhone.isPhone(aetPhone.getText().toString())){
            aetPhone.setError("手机号不合法");
            Snackbar.make(btnSendVerifyCode,"手机号不合法",Snackbar.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        this.setResult(300);
        super.onDestroy();
    }
}
